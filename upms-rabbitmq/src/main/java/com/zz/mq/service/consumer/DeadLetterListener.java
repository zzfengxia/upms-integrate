package com.zz.mq.service.consumer;

import com.rabbitmq.client.Channel;
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
 * @date 2020-01-16 16:29
 * ************************************
 */
@Component
@Slf4j
public class DeadLetterListener {
    @RabbitListener(queues = DeadLetterRabbitMqConfig.DEAD_LETTER_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        log.info("[死信队列]死信队列A收到消息：" + new String(message.getBody()) + "\n properties:" + message.getMessageProperties().toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DeadLetterRabbitMqConfig.DEAD_LETTER_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException {
        log.info("[死信队列]死信队列B收到消息：" + new String(message.getBody()) + "\n properties:" + message.getMessageProperties().toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayRabbitMqConfig.DEAD_LETTER_QUEUEA_NAME)
    public void delayReceiveA(Message message, Channel channel) throws IOException {
        log.info("[延迟队列]死信队列A收到消息：" + new String(message.getBody()) + "\n properties:" + message.getMessageProperties().toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayRabbitMqConfig.DEAD_LETTER_QUEUEB_NAME)
    public void delayReceiveB(Message message, Channel channel) throws IOException {
        log.info("[延迟队列]死信队列B收到消息：" + new String(message.getBody()) + "\n properties:" + message.getMessageProperties().toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayRabbitMqConfig.DEAD_LETTER_QUEUEC_NAME)
    public void delayReceiveC(Message message, Channel channel) throws IOException {
        log.info("[延迟队列]死信队列C收到消息：" + new String(message.getBody()) + "\n properties:" + message.getMessageProperties().toString());
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
