package com.zz.mq.service.consumer;

import com.rabbitmq.client.Channel;
import com.zz.mq.config.ReliableDeliveryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ************************************
 * create by Intellij IDEA
 * RabbitMQ消息可靠投递的消费者
 *
 * @author Francis.zz
 * @date 2020-01-17 16:07
 * ************************************
 */
@Component
@Slf4j
public class ReliableListener {
    @RabbitListener(queues = ReliableDeliveryConfig.BUSINESS_QUEUEA_NAME)
    public void receiveMsg(Channel channel, Message message) throws IOException {
        String msg = new String(message.getBody());
        log.info("[可靠投递]收到业务消息：{}", msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听备份队列信息，未成功路由的消息会被投递到备份交换机
     *
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = ReliableDeliveryConfig.BACKUP_QUEUEA_NAME)
    public void receiveBackupMsg(Channel channel, Message message) throws IOException {
        String msg = new String(message.getBody());
        log.info("[可靠投递-备份队列]收到消息：{}", msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
