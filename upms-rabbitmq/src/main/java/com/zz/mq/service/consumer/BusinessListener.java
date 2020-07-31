package com.zz.mq.service.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.zz.mq.annotation.EnableMDCLog;
import com.zz.mq.annotation.IdempotentConsume;
import com.zz.mq.config.DeadLetterRabbitMqConfig;
import com.zz.mq.config.DelayRabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 * 处理@RabbitListener的方法: {@link org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor#processListener}
 * RabbitListener的queues支持属性占位和表达式，表达式即为spring的SpEL语句
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
    
    /**
     * 幂等消费
     * getMessagingMessageConverter 方法来转换获取到的消息
     * @see {@link org.springframework.amqp.support.converter.Jackson2JsonMessageConverter#fromMessage}
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{queuesMap.PERSIST_DB_QUEUE}")
    @IdempotentConsume
    @EnableMDCLog(argIndex = 0)
    public void demoDbListener(Message message, Channel channel) {
        // 这里会使用配置的 `Jackson2JsonMessageConverter` Bean来转换消息体
        String msg = (String) new Jackson2JsonMessageConverter().fromMessage(message);
        log.info("db msg:" + msg);
        Map<String, String> result = JSON.parseObject(msg, new TypeReference<Map<String, String>>(){});
        log.info("msg from json:" + result.get("msg"));
        log.info("db demo listener exec...");
    }
    
    /**
     * 延迟队列消费
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = "#{queuesMap.DELAY_QUEUE}")
    @IdempotentConsume
    @EnableMDCLog(argIndex = 0)
    public void demoDelayListener(Message message, Channel channel) {
        String msg = (String) new Jackson2JsonMessageConverter().fromMessage(message);
        log.info("msg:" + msg);
        Map<String, String> result = JSON.parseObject(msg, new TypeReference<Map<String, String>>(){});
        log.info("msg from json:" + result.get("msg"));
        log.info("delay demo listener exec...");
        testLog();
        throw new IllegalArgumentException("111");
    }
    
    private void testLog() {
        log.info("testLog...");
    }
}
