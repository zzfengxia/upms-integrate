package com.zz.mq.aop;

import com.rabbitmq.client.Channel;
import com.zz.mq.annotation.IdempotentConsumeDB;
import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.MqMsgService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
public class IdempotentConsumeAspect {
    @Autowired
    private MqMsgService msgService;
    
    @Pointcut(value = "@annotation(idempotentConsumeDB) && args(message, channel)", argNames = "idempotentConsumeDB, message, channel")
    public void pointCut(IdempotentConsumeDB idempotentConsumeDB, Message message, Channel channel) {
    
    }
    
    @Around(value = "pointCut(idempotentConsumeDB, message, channel)")
    public void idempotentConsumeDBAround(ProceedingJoinPoint joinPoint, IdempotentConsumeDB idempotentConsumeDB, Message message, Channel channel) {
        // 幂等消费。通过查询消息表状态判断是否消费过。消费成功后更新消息状态。
        // 如果有分库操作，可以创建单独的消费表，通过主键id的唯一性保证幂等消费。
        String msgId = getCorrelationId(message);
        if (isConsumed(msgId)) {// 消费幂等性, 防止消息被重复消费
            log.info("重复消费, msgID:{}", msgId);
            return;
        }
    
        MessageProperties properties = message.getMessageProperties();
        long tag = properties.getDeliveryTag();
    
        try {
            joinPoint.proceed(new Object[]{message, channel});// 真正消费的业务逻辑
            msgService.updateStatus(msgId, MessageStatEnum.CONSUMED_SUCCESS);
            channel.basicAck(tag, false);// 消费确认
        } catch (Throwable throwable) {
            log.error("getProxy error", throwable);
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException e) {
                log.error("消费失败且重新投递失败,msgID:{}", msgId);
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
