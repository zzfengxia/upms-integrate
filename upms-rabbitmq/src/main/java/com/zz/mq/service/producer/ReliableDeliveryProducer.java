package com.zz.mq.service.producer;

import com.zz.mq.common.MessageStatEnum;
import com.zz.mq.entity.MqMsgLog;
import com.zz.mq.service.MqMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 * 可靠消息投递，分布式事务最终一致性解决
 *
 * @author Francis.zz
 * @date 2020-07-21 14:51
 * ************************************
 */
@Component
public class ReliableDeliveryProducer {
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private MqMsgService msgService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void persistMsg(MqMsgLog mqMsgLog) {
        mqMsgLog.setStatus(MessageStatEnum.WAIT_CONFIRM.getStatus());
        mqMsgLog.setCreateTime(new Date());
        // 消息标记
        msgService.insert(mqMsgLog);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void doBusinessAndSaveMsg(MqMsgLog message) {
        // 处理业务逻辑，写入DB。比如创建订单保存订单信息。确保与订单消息写入DB在同一事务中
        
        // 保存订单信息
        
        // 写入消息到DB
        persistMsg(message);
    }
    
    public void sendMsg(MqMsgLog mqMsgLog) {
        // 发送消息，开启生产消息确认机制，在确认回调逻辑中更新消息状态为“已发送”
        CorrelationData correlationData = new CorrelationData(mqMsgLog.getMsgId());
        MessageProperties messageProperties = msgService.setMessageProperties(mqMsgLog.getMsgId(), MessageProperties.CONTENT_TYPE_JSON);
        // 直接组装成 Message 对象不会再去调用messageConvert，所以这里需要进行消息转换，才能在消费端使用 json 获取消息
        Message message = new Jackson2JsonMessageConverter().toMessage(mqMsgLog.getMsgText(), messageProperties);
        
        rabbitTemplate.convertAndSend(mqMsgLog.getExchange(), mqMsgLog.getRoutingKey(), message, correlationData);
        log.info("demo db msg producer exec...");
    }
    
    public void sendMsg(MqMsgLog mqMsgLog, String routingKey) {
        // 发送消息，开启生产消息确认机制，在确认回调逻辑中更新消息状态为“已发送”
        CorrelationData correlationData = new CorrelationData(mqMsgLog.getMsgId());
        MessageProperties messageProperties = msgService.setMessageProperties(mqMsgLog.getMsgId(), MessageProperties.CONTENT_TYPE_JSON);
    
        Message message = new Jackson2JsonMessageConverter().toMessage(mqMsgLog.getMsgText(), messageProperties);
        rabbitTemplate.convertAndSend(mqMsgLog.getExchange(), routingKey, message, correlationData);
    }
    
    public void sendMsg(MqMsgLog mqMsgLog, int delayTime) {
        // 发送消息，开启生产消息确认机制，在确认回调逻辑中更新消息状态为“已发送”
        // 发送消息会调用`convertMessageIfNecessary`方法转换消息，如果这里传的 Message 对象就不会再转换也就是配置的 `Jackson2JsonMessageConverter` Bean不会被使用
        // 获取是其他类型的消息，则会使用配置的转换器转换，并且会new MessageProperties
        CorrelationData correlationData = new CorrelationData(mqMsgLog.getMsgId());
        rabbitTemplate.convertAndSend(mqMsgLog.getExchange(), mqMsgLog.getRoutingKey(), mqMsgLog.getMsgText(), message -> {
            message.getMessageProperties().setDelay(delayTime);
            message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
            message.getMessageProperties().setMessageId(mqMsgLog.getMsgId());
            return message;
        }, correlationData);
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public void doBusinessAndSaveMsg(Message message) {
        // 处理业务逻辑，写入DB。比如创建订单保存订单信息。确保与订单消息写入DB在同一事务中
        
        // 保存订单信息
        
        // 写入消息到DB
        msgService.saveMessage(message);
    }
    
    public void sendMsg(Message message) {
        // 发送消息，开启生产消息确认机制，在确认回调逻辑中更新消息状态为“已发送”
        CorrelationData correlationData = new CorrelationData(message.getMessageProperties().getMessageId());
        
        rabbitTemplate.convertAndSend(message.getMessageProperties().getReceivedExchange(),
                message.getMessageProperties().getReceivedRoutingKey(), message, correlationData);
        log.info("sendMsg producer exec...");
    }
}
