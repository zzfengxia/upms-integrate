package com.zz.mq.service.producer;

import com.zz.mq.config.DeadLetterRabbitMqConfig;
import com.zz.mq.config.DelayRabbitMqConfig;
import com.zz.mq.config.ReliableDeliveryConfig;
import com.zz.mq.service.MqMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-01-16 16:43
 * ************************************
 */
@Component
@Slf4j
public class MessageProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MqMsgService msgService;
    
    @PostConstruct
    private void init() {
        // 开启事务；不能与消息确认模式同时开启
        //rabbitTemplate.setChannelTransacted(true);
    }

    /**
     * 消息生产者
     * RabbitTemplate中一些发送消息的方法
     * send
     * convertAndSend
     * convertSendAndReceive
     * sendAndReceive
     * 带convert的方法会把消息内容转换Message对象(可通过setMessageConverter自定义转换消息操作)
     * 带Receive的方法会尝试获取消息应答返回，并等待超时限制的一段时间，超时限制时间可通过replyTimeout设置
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        long start = System.currentTimeMillis();
        Object resp = rabbitTemplate.convertSendAndReceive(DeadLetterRabbitMqConfig.BUSINESS_EXCHANGE_NAME, "", msg);
        long end = System.currentTimeMillis();

        log.info("消息投递耗时：{}ms, resp:{}", (end -start), Objects.toString(resp));
    }

    public void sendDelayMsg(String msg) {
        boolean flag = msg.length() % 10 == 0;
        if(flag) {
            rabbitTemplate.convertAndSend(DelayRabbitMqConfig.DELAY_EXCHANGE_NAME, DelayRabbitMqConfig.DELAY_QUEUEA_ROUTING_KEY, msg);
        }else {
            rabbitTemplate.convertAndSend(DelayRabbitMqConfig.DELAY_EXCHANGE_NAME, DelayRabbitMqConfig.DELAY_QUEUEB_ROUTING_KEY, msg);
        }
    }

    /**
     * 消息Properties中设置延时时间，可以为队列中不同消息设置不同的延时时间
     * 缺点: 后进队列但是延时时间比前面短的消息必须等待前面的消息成为死信后才能成为死信
     *
     * @param msg
     * @param delayTime
     */
    public void sendDelayMsg(String msg, String delayTime) {
        rabbitTemplate.convertAndSend(DelayRabbitMqConfig.DELAY_EXCHANGE_NAME, DelayRabbitMqConfig.DELAY_QUEUEC_ROUTING_KEY, msg, a ->{
            // 设置消息的延时时间 ms
            a.getMessageProperties().setExpiration(delayTime);
            
            // 设置消息持久化
            a.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return a;
        });
    }

    /**
     * 使用延时插件实现延时队列，需要安装延时队列插件 rabbitmq_delayed_message_exchange
     *
     * @param msg
     * @param delayTime
     */
    public void sendDelayMsgPlugin(String msg, Integer delayTime) {
        rabbitTemplate.convertAndSend(DelayRabbitMqConfig.DELAYED_EXCHANGE_NAME, DelayRabbitMqConfig.DELAYED_ROUTING_KEY, msg, a ->{
            // 使用延时插件的方式设置延时时间
            a.getMessageProperties().setDelay(delayTime);
            return a;
        });
    }

    /**
     * 开启事务，该方法发生异常时，保证消息不会被投递
     * 事务提交的方式会多跟服务器交互几次，性能较低
     *
     * @param msg
     */
    @Transactional
    public void sendMsgTX(String msg) {
        rabbitTemplate.convertAndSend(ReliableDeliveryConfig.BUSINESS_EXCHANGE_NAME, "", msg);
        log.info("tx msg:{}", msg);
        if (msg != null && msg.contains("exception"))
            throw new RuntimeException("surprise!");
        log.info("tx消息已发送 {}" ,msg);
    }
}
