package com.zz.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 * 延时队列：用来存放需要在指定时间被处理的元素的队列
 * 应用场景
 * 1. 订单在十分钟之内未支付则自动取消。
 * 2. 新创建的店铺，如果在十天内都没有上传过商品，则自动发送消息提醒。
 * 3. 账单在一周内未支付，则自动结算。
 * 4. 用户注册成功后，如果三天内没有登陆则进行短信提醒。
 * 5. 用户发起退款，如果三天内没有得到处理则通知相关运营人员。
 * 6. 预定会议后，需要在预定的时间点前十分钟通知各个与会人员参加会议。
 * 等等...
 * 相对定时任务，使用消息队列实现的方式会更有时效性，且在数据量大的场景可以减少数据轮询，减轻数据库压力，而且更加优雅
 *
 * @author Francis.zz
 * @date 2020-01-17 10:05
 * ************************************
 */
@Configuration
public class DelayRabbitMqConfig {
    public static final String DELAY_EXCHANGE_NAME = "delay.queue.demo.business.exchange";
    public static final String DELAY_QUEUEA_NAME = "delay.queue.demo.business.queuea";
    public static final String DELAY_QUEUEB_NAME = "delay.queue.demo.business.queueb";
    public static final String DELAY_QUEUEC_NAME = "delay.queue.demo.business.queuec";
    public static final String DELAY_QUEUEA_ROUTING_KEY = "delay.queue.demo.business.queuea.routingkey";
    public static final String DELAY_QUEUEB_ROUTING_KEY = "delay.queue.demo.business.queueb.routingkey";
    public static final String DELAY_QUEUEC_ROUTING_KEY = "delay.queue.demo.business.queuec.routingkey";
    public static final String DEAD_LETTER_EXCHANGE = "delay.queue.demo.deadletter.exchange";
    public static final String DEAD_LETTER_QUEUEA_ROUTING_KEY = "delay.queue.demo.deadletter.delay_10s.routingkey";
    public static final String DEAD_LETTER_QUEUEB_ROUTING_KEY = "delay.queue.demo.deadletter.delay_60s.routingkey";
    public static final String DEAD_LETTER_QUEUEC_ROUTING_KEY = "delay.queue.demo.deadletter.delay.routingkey";
    public static final String DEAD_LETTER_QUEUEA_NAME = "delay.queue.demo.deadletter.queuea";
    public static final String DEAD_LETTER_QUEUEB_NAME = "delay.queue.demo.deadletter.queueb";
    public static final String DEAD_LETTER_QUEUEC_NAME = "delay.queue.demo.deadletter.queuec";

    public static final String DELAYED_QUEUE_NAME = "delay.queue.demo.delay.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delay.queue.demo.delay.exchange";
    public static final String DELAYED_ROUTING_KEY = "delay.queue.demo.delay.routingkey";


    // 声明延时Exchange
    @Bean
    public DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    // 声明死信Exchange
    @Bean
    public DirectExchange deadLetterExchangeForDelay() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     * 使用延时队列插件 <url>https://www.rabbitmq.com/community-plugins.html </url>下载rabbitmq_delayed_message_exchange插件
     * @see {@link org.springframework.amqp.rabbit.core.RabbitAdmin#initialize} 会创建所有通过spring bean创建出来的交换机和队列
     * @return
     */
    @Bean
    public DirectExchange customExchange() {
        /*Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);*/
        
        // 直接使用 delayed 声明延迟交换机 RabbitAdmin.declareExchanges会自动创建
        return ExchangeBuilder.directExchange(DELAYED_EXCHANGE_NAME).durable(true).delayed().build();
    }

    // 声明延时队列A并绑定到对应的死信交换机
    @Bean
    public Queue delayQueueA() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEA_ROUTING_KEY);
        // x-message-ttl  声明队列的TTL，投递到该队列的所有消息的存活时间不超过设置的值，过期后消息会被丢弃或投递到死信交换机
        args.put("x-message-ttl", 6000);
        return QueueBuilder.durable(DELAY_QUEUEA_NAME).withArguments(args).build();
    }

    // 声明延时队列B并绑定到对应的死信交换机
    @Bean
    public Queue delayQueueB() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEB_ROUTING_KEY);
        // x-message-ttl  声明队列的TTL
        args.put("x-message-ttl", 10000);
        return QueueBuilder.durable(DELAY_QUEUEB_NAME).withArguments(args).build();
    }

    /**
     * 声明队列C，不设置TTL。在消息的property中设置ttl
     *
     * @return
     */
    @Bean
    public Queue delayQueueC() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEA_ROUTING_KEY);
        return QueueBuilder.durable(DELAY_QUEUEC_NAME).withArguments(args).build();
    }

    @Bean
    public Queue immediateQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    // 声明死信队列A
    @Bean
    public Queue deadLetterQueueAForDelay() {
        return new Queue(DEAD_LETTER_QUEUEA_NAME);
    }

    // 声明死信队列B
    @Bean
    public Queue deadLetterQueueBForDelay() {
        return new Queue(DEAD_LETTER_QUEUEB_NAME);
    }

    // 声明死信队列C
    @Bean
    public Queue deadLetterQueueCForDelay() {
        return new Queue(DEAD_LETTER_QUEUEC_NAME);
    }

    // 声明延时队列A绑定关系
    @Bean
    public Binding delayBindingA(Queue delayQueueA, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueueA).to(delayExchange).with(DELAY_QUEUEA_ROUTING_KEY);
    }

    // 声明业务队列B绑定关系
    @Bean
    public Binding delayBindingB(Queue delayQueueB, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueueB).to(delayExchange).with(DELAY_QUEUEB_ROUTING_KEY);
    }

    // 声明业务队列C绑定关系
    @Bean
    public Binding delayBindingC(Queue delayQueueC, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayQueueC).to(delayExchange).with(DELAY_QUEUEC_ROUTING_KEY);
    }

    // 声明死信队列A绑定关系
    @Bean
    public Binding deadLetterBindingAForDelay(Queue deadLetterQueueAForDelay, DirectExchange deadLetterExchangeForDelay) {
        return BindingBuilder.bind(deadLetterQueueAForDelay).to(deadLetterExchangeForDelay).with(DEAD_LETTER_QUEUEA_ROUTING_KEY);
    }

    // 声明死信队列B绑定关系
    @Bean
    public Binding deadLetterBindingBForDelay(Queue deadLetterQueueBForDelay, DirectExchange deadLetterExchangeForDelay) {
        return BindingBuilder.bind(deadLetterQueueBForDelay).to(deadLetterExchangeForDelay).with(DEAD_LETTER_QUEUEB_ROUTING_KEY);
    }

    // 声明死信队列C绑定关系
    @Bean
    public Binding deadLetterBindingCForDelay(Queue deadLetterQueueCForDelay, DirectExchange deadLetterExchangeForDelay) {
        return BindingBuilder.bind(deadLetterQueueCForDelay).to(deadLetterExchangeForDelay).with(DEAD_LETTER_QUEUEC_ROUTING_KEY);
    }

    @Bean
    public Binding bindingNotify(Queue immediateQueue, DirectExchange customExchange) {
        return BindingBuilder.bind(immediateQueue).to(customExchange).with(DELAYED_ROUTING_KEY);
    }
}
