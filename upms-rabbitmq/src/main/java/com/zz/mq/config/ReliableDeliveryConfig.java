package com.zz.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ************************************
 * create by Intellij IDEA
 * RabbitMQ的消息可靠投递
 * RabbitMQ中相关的解决方案：
 * 1. 使用事务机制来让生产者感知消息被成功投递到服务器。
 * 2. 通过生产者确认机制实现。
 * 在RabbitMQ中，所有确保消息可靠投递的机制都会对性能产生一定影响，如使用不当，可能会对吞吐量造成重大影响，只有通过执行性能基准测试，才能在确定性能与可靠投递之间的平衡。
 *
 * 在使用可靠投递前，需要先思考以下问题：
 *
 * 1. 消息发布时，保证消息进入队列的重要性有多高？
 * 2. 如果消息无法进行路由，是否应该将该消息返回给发布者？
 * 3. 如果消息无法被路由，是否应该将其发送到其他地方稍后再重新进行路由？
 * 4. 如果RabbitMQ服务器崩溃了，是否可以接受消息丢失？
 * 5. RabbitMQ在处理新消息时是否应该确认它已经为发布者执行了所有请求的路由和持久化？
 * 6. 消息发布者是否可以批量投递消息？
 * 7. 在可靠投递上是否有可以接受的平衡性？是否可以接受一部分的不可靠性来提升性能？
 * 8. 只考虑平衡性不考虑性能是不行的，至于这个平衡的度具体如何把握，就要具体情况具体分析了，
 *    比如像订单数据这样敏感的信息，对可靠性的要求自然要比一般的业务消息对可靠性的要求高的多
 * @author Francis.zz
 * @date 2020-01-17 15:59
 * ************************************
 */
@Configuration
public class ReliableDeliveryConfig {
    public static final String BUSINESS_EXCHANGE_NAME = "rabbitmq.reliable.demo.simple.business.exchange";
    public static final String BACKUP_EXCHANGE_NAME = "rabbitmq.reliable.demo.simple.backup.exchange";
    public static final String BUSINESS_QUEUEA_NAME = "rabbitmq.reliable.demo.simple.business.queue";
    public static final String BACKUP_QUEUEA_NAME = "rabbitmq.reliable.demo.simple.backup.queue";
    public static final String BUSINESS_ROUTINGKEY_NAME = "rabbitmq.reliable.demo.simple.business.routingkey";

    // 声明业务Exchange
    @Bean
    public DirectExchange deliveryExchange() {
        return (DirectExchange) ExchangeBuilder.directExchange(BUSINESS_EXCHANGE_NAME).durable(true)
                // 声明备份交换机
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME).build();
    }

    /**
     * 备份交换机
     *
     * @return
     */
    @Bean
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    // 声明业务队列
    @Bean
    public Queue deliveryQueue() {
        return QueueBuilder.durable(BUSINESS_QUEUEA_NAME).build();
    }

    // 声明备份队列
    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUEA_NAME).build();
    }

    // 声明业务队列绑定关系
    @Bean
    public Binding deliveryBinding() {
        return BindingBuilder.bind(deliveryQueue()).to(deliveryExchange()).with(BUSINESS_ROUTINGKEY_NAME);
    }

    // 声明备份队列绑定关系
    @Bean
    public Binding backupBinding() {
        return BindingBuilder.bind(backupQueue()).to(backupExchange());
    }
}
