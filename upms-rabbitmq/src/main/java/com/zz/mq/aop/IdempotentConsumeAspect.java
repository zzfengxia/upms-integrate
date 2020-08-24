package com.zz.mq.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zz.mq.annotation.IdempotentConsume;
import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.common.MqConstants;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.MqMsgService;
import com.zz.mq.service.RedisHelper;
import com.zz.mq.service.RedisLock;
import com.zz.mq.service.producer.ReliableDeliveryProducer;
import com.zz.mq.utils.JodaTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-03-10 16:28
 * ************************************
 */
@Aspect
@Component
@Slf4j
@Order(9999)
public class IdempotentConsumeAspect {
    @Autowired
    private MqMsgService msgService;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private ReliableDeliveryProducer reliableDeliveryProducer;
    
    
    
    @Pointcut(value = "@annotation(idempotentConsume) && args(message, channel)", argNames = "idempotentConsume, message, channel")
    public void pointCut(IdempotentConsume idempotentConsume, Message message, Channel channel) {
    
    }
    
    @Around(value = "pointCut(idempotentConsume, message, channel)")
    public void idempotentConsumeDBAround(ProceedingJoinPoint joinPoint, IdempotentConsume idempotentConsume, Message message, Channel channel) {
        // 幂等消费。通过查询消息表状态判断是否消费过。消费成功后更新消息状态。
        // 如果有分库操作，可以创建单独的消费表，通过主键id的唯一性保证幂等消费。
        String msgId = getCorrelationId(message);
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();
        
        if (isConsumed(msgId)) {// 消费幂等性, 防止消息被重复消费
            log.info("重复消费, msgID:{}", msgId);
            try {
                channel.basicAck(tag, false);// 消费确认
            } catch (IOException e) {
                log.error("ack error. msg id:" + msgId);
            }
            return;
        }
        
        String lockKey = MqConstants.redisKey(MqConstants.MQ_CONSUMER_LOCK, msgId);
        RedisLock consumerLock = null;
        try {
            consumerLock = new RedisLock(redisHelper, lockKey);
            // 对消息ID加锁，防止并发消费
            if(!consumerLock.lock()) {
                // 未拿到锁，发送nack并重投
                log.info("并发消费，等待其他事务消费，msg id:" + msgId);
                try {
                    channel.basicNack(tag, false, true);// 消费确认
                } catch (IOException e) {
                    log.error("nack error. msg id:" + msgId);
                }
                return;
            }
            
            joinPoint.proceed(new Object[]{message, channel});// 真正消费的业务逻辑
            log.info(msgId + ":prepare to update msg");
            int res = msgService.updateStatus(msgId, MessageStatEnum.CONSUMED_SUCCESS);
            log.info(msgId + ":success to update msg, res:" + res);
            channel.basicAck(tag, false);// 消费确认
        } catch (Throwable throwable) {
            log.error("getProxy error", throwable);
            try {
                MqMsgLog msgLog = msgService.selectByMsgId(msgId);
                if(null == msgLog || msgLog.getStatus().equals(MessageStatEnum.CONSUMED_SUCCESS.getStatus())) {
                    log.info("消息已被删除或已消费, msgID:" + msgId + ", msg body:" + messageConverter.fromMessage(message));
                    channel.basicAck(tag, false);
                    return;
                }
    
                int retryCount = msgLog.getTryCount() + 1;
                if(retryCount > 3) {
                    log.info("消息投递次数上限, 已投递:" + (retryCount - 1) + "次, msgID:" + msgId);
                    channel.basicAck(tag, false);
                    return;
                }
                // 消息重新发送到队列，并设置延迟，重试次数越多，延迟越长
                double delayMinutes = Math.pow(retryCount, 2);
                msgLog.setTryCount(retryCount);
                msgLog.setNextTryTime(JodaTimeUtil.plusMinutes(new Date(), (int) delayMinutes));
                msgLog.setUpdateTime(new Date());
                // 重置状态
                msgLog.setStatus(MessageStatEnum.DELIVER_SUCCESS.getStatus());
                msgService.updateMsg(msgLog);
                
                // 重新投递
                MessageProperties messageProperties = msgService.setMessageProperties(msgId, properties.getContentType());
                
                JSON.parseObject(msgLog.getMsgHeader(), new TypeReference<Map<String, Object>>(){}).forEach(messageProperties::setHeader);
                messageProperties.setReceivedExchange(msgLog.getExchange());
                messageProperties.setReceivedRoutingKey(msgLog.getRoutingKey());
                messageProperties.setDelay((int) delayMinutes * 60 * 1000);
                
                Message newMessage = messageConverter.toMessage(msgLog.getMsgText(), messageProperties);
                reliableDeliveryProducer.sendMsg(newMessage);
                
                channel.basicAck(tag, false);
            } catch (IOException e) {
                log.error("消费失败且重新投递失败,msgID:{}", msgId);
            }
        } finally {
            // 解锁
            if (consumerLock != null) {
                consumerLock.close();
            }
        }
    }
    
    /**
     * 获取CorrelationId
     *
     * @param message
     * @return
     */
    private String getCorrelationId(Message message) {
        String correlationId = null;
        
        MessageProperties properties = message.getMessageProperties();
        if(properties.getMessageId() != null) {
            return properties.getMessageId();
        }
        Map<String, Object> headers = properties.getHeaders();
        for (Map.Entry entry : headers.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (key.equals("spring_returned_message_correlation")) {
                correlationId = value;
            }
        }
        
        return correlationId;
    }
    
    /**
     * 消息是否已被消费
     *
     * @param correlationId
     * @return
     */
    private boolean isConsumed(String correlationId) {
        MqMsgLog msgLog = msgService.selectByMsgId(correlationId);
        if (null == msgLog || msgLog.getStatus().equals(MessageStatEnum.CONSUMED_SUCCESS.getStatus())) {
            return true;
        }
        
        return false;
    }
}
