package com.zz.mq.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-01-18 15:41
 * ************************************
 */
@Component
@Slf4j
public class DelayMessageProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    private void init() {
        // 不能与事务模式同时开启
        //rabbitTemplate.setConfirmCallback(this);
        // 设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者。
        //当把 mandotory 参数设置为 true 时，如果交换机无法将消息进行路由时，会将该消息返回给生产者，而如果该参数设置为false，如果发现消息无法进行路由，则直接丢弃
        // mandatory 参数仅仅是在当消息无法被路由的时候，让生产者可以感知到这一点，只要开启了生产者确认机制，
        // 无论是否设置了 mandatory 参数，都会在交换机接收到消息时进行消息确认回调，而且通常消息的退回回调会在消息的确认回调之前
        //rabbitTemplate.setMandatory(true);
        //rabbitTemplate.setReturnCallback(this);
    }

    public void sendCustomMsg(String exchange, String routingKey, String msg) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        log.info("消息id:{}, msg:{}", correlationData.getId(), msg);

        rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
    }

    /**
     * 消息确认模式
     * 消息投递成功后的回调，但是如果消息不能正确路由而导致的消息丢失，在这里是无法感知的，也会显示消息确认成功
     * 在仅开启了生产者确认机制的情况下(Mandatory为false)，交换机接收到消息后，会直接给消息生产者发送确认消息，
     * 如果发现该消息不可路由，那么消息会被直接丢弃，此时，生产者是不知道消息被丢弃这个事件的
     *
     * @param correlationData
     * @param b
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (b) {
            log.info("消息确认成功, id:{}", id);
        } else {
            log.error("消息未成功投递, id:{}, cause:{}", id, s);
        }
    }

    /**
     * 未成功路由的消息会回调该方法，但优先级没有备份交换机高，如果配置了备份交换机会优先把消息投递到备份交换机，而不是回调该方法
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("消息被服务器退回。msg:{}, replyCode:{}. replyText:{}, exchange:{}, routingKey :{}",
                new String(message.getBody()), replyCode, replyText, exchange, routingKey);
    }
}
