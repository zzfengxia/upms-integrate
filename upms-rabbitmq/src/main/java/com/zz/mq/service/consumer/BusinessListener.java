package com.zz.mq.service.consumer;

import com.rabbitmq.client.Channel;
import com.zz.mq.annotation.IdempotentConsumeDB;
import com.zz.mq.config.DeadLetterRabbitMqConfig;
import com.zz.mq.config.DelayRabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-01-16 16:28
 * ************************************
 */
@Component
@Slf4j
public class BusinessListener {
    @RabbitListener(queues = DeadLetterRabbitMqConfig.BUSINESS_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        log.info("业务队列A收到消息：{}", msg);
        boolean ack = true;
        Exception exception = null;
        try {
            if (msg.contains("deadletter")) {
                throw new RuntimeException("dead letter exception");
            }
        } catch (Exception e) {
            ack = false;
            exception = e;
        }
        if (!ack) {
            log.error("消息消费发生异常，error msg:{}", exception.getMessage(), exception);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        } else {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    @RabbitListener(queues = DeadLetterRabbitMqConfig.BUSINESS_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException {
        log.info("业务队列B收到消息：" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayRabbitMqConfig.DELAYED_QUEUE_NAME)
    public void delayReceive(Message message, Channel channel) throws IOException {
        log.info("插件延时队列收到消息：" + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
    
    @RabbitListener(queues = "demo.db.queue")
    @IdempotentConsumeDB
    public void demoDbListener(Message message, Channel channel) {
        log.info("db demo listener exec...");
    }
}
