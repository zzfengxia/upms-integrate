package com.zz.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ************************************
 * create by Intellij IDEA
 * 消息可靠投递、幂等消费完整配置
 *
 * @author Francis.zz
 * @date 2020-03-10 11:04
 * ************************************
 */
@Configuration
public class RabbitMqConfig {
	@Bean
	public RabbitTemplate setTemplate(ConnectionFactory connectionFactory) {
	    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 不能与事务模式同时开启
        rabbitTemplate.setConfirmCallback(callBackForDB());
        // 设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者。
        //当把 mandotory 参数设置为 true 时，如果交换机无法将消息进行路由时，会将该消息返回给生产者，而如果该参数设置为false，如果发现消息无法进行路由，则直接丢弃
        // mandatory 参数仅仅是在当消息无法被路由的时候，让生产者可以感知到这一点，只要开启了生产者确认机制，
        // 无论是否设置了 mandatory 参数，都会在交换机接收到消息时进行消息确认回调，而且通常消息的退回回调会在消息的确认回调之前
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(callBackForDB());
	    
	    return rabbitTemplate;
	}
    
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public MessageCallBackForDB callBackForDB() {
        return new MessageCallBackForDB();
    }
    
    @Bean("dbExchange")
    public DirectExchange dbExchange() {
        return new DirectExchange(QueueEnum.PERSIST_DB_QUEUE.getExchange());
    }
    
    @Bean("dbQueue")
    public Queue dbQueue() {
        return QueueBuilder.durable(QueueEnum.PERSIST_DB_QUEUE.getQueue()).build();
    }
    
    @Bean("dbBind")
    public Binding dbBuind() {
	    return BindingBuilder.bind(dbQueue()).to(dbExchange()).with(QueueEnum.PERSIST_DB_QUEUE.getRoutingKey());
    }
}
