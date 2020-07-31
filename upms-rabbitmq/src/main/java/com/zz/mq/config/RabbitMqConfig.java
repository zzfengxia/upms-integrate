package com.zz.mq.config;

import com.zz.mq.common.ExchangeEnum;
import com.zz.mq.common.QueueEnum;
import com.zz.mq.service.RabbitMqUtilService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;

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
	    // 开启事务
        //rabbitTemplate.setChannelTransacted(true);
        
        // 不能与事务模式同时开启
        rabbitTemplate.setConfirmCallback(callBackForDB());
        // 设置 mandatory 参数可以在当消息传递过程中不可达目的地时将消息返回给生产者。
        //当把 mandotory 参数设置为 true 时，如果交换机无法将消息进行路由时，会将该消息返回给生产者，而如果该参数设置为false，如果发现消息无法进行路由，则直接丢弃
        // mandatory 参数仅仅是在当消息无法被路由的时候，让生产者可以感知到这一点，只要开启了生产者确认机制，
        // 无论是否设置了 mandatory 参数，都会在交换机接收到消息时进行消息确认回调，而且通常消息的退回回调会在消息的确认回调之前
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(callBackForDB());
        rabbitTemplate.setMessageConverter(converter());
        
        // 在这里设置消息转换类只会对 消息生产者有效(没有封装成Message对象的消息才会使用这里的转换器转换)，消息消费端会使用默认的 `SimpleMessageConverter` 转换器
	    // rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
	    return rabbitTemplate;
	}
    
    /**
     * 通过spring bean方式配置的消息转换器会对消息生产者和消费者都生效
     *
     * @return
     */
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public MessageCallBackForDB callBackForDB() {
        return new MessageCallBackForDB();
    }
    
    /**
     * 配置启用rabbitmq事务，如果同时开启了生产者消息确认模式，则会报错：“cannot switch from tx to confirm mode”
     */
    /*@Bean
    public RabbitTransactionManager rabbitTransactionManager(CachingConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }*/
    
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    /**
     * @see {@link org.springframework.amqp.rabbit.core.RabbitAdmin#initialize} 会创建所有通过spring bean创建出来的交换机和队列
     * 这里不使用spring bean创建，直接通过Enum管理所有交换机和队列，并在此创建到RabbitMq中
     */
    @PostConstruct
    public void registryQueueAndExchange() {
        registryExchange(amqpAdmin);
        registryQueueAndBinding(amqpAdmin);
    }
    
    private void registryExchange(AmqpAdmin amqpAdmin) {
        // 创建 ExchangeEnum中的所有交换机
        Arrays.stream(ExchangeEnum.values()).forEach(exchangeEnum -> {
            amqpAdmin.declareExchange(RabbitMqUtilService.createExchangeWithEnum(exchangeEnum));
        });
    }
    
    private void registryQueueAndBinding(AmqpAdmin amqpAdmin) {
        // 创建队列并绑定交换机
        Arrays.stream(QueueEnum.values()).forEach(queueEnum -> {
            Queue queue = new Queue(queueEnum.getQueueName(), queueEnum.isDurable(), queueEnum.isExclusive(),
                    queueEnum.isAutoDelete(), queueEnum.getArguments());
            amqpAdmin.declareQueue(queue);
    
            ExchangeEnum exchangeEnum = queueEnum.getExchange();
            BindingBuilder.DestinationConfigurer destinationConfigurer = BindingBuilder.bind(queue);
            Binding binding = null;
            switch (exchangeEnum.getExchangeType()) {
                case ExchangeTypes.FANOUT:
                    binding = destinationConfigurer.to((FanoutExchange) RabbitMqUtilService.createExchangeWithEnum(exchangeEnum));
                    
                    break;
                case ExchangeTypes.TOPIC:
                    binding = destinationConfigurer.to((TopicExchange) RabbitMqUtilService.createExchangeWithEnum(exchangeEnum))
                            .with(queueEnum.getRoutingKey());
            
                    break;
                case ExchangeTypes.HEADERS:
                    // todo HeaderExchange用的少，这里只是范例，实际使用需要补全Header配置
                    binding = destinationConfigurer.to((HeadersExchange) RabbitMqUtilService.createExchangeWithEnum(exchangeEnum))
                            .whereAll("").exist();
            
                    break;
                case ExchangeTypes.DIRECT:
                default:
                    binding = destinationConfigurer.to((DirectExchange) RabbitMqUtilService.createExchangeWithEnum(exchangeEnum))
                            .with(queueEnum.getRoutingKey());
            
                    break;
            }
    
            amqpAdmin.declareBinding(binding);
        });
    }
    
    /**
     * 注册枚举队列信息到spring bean
     */
    @Bean
    public Map<String, String> queuesMap() {
        return QueueEnum.queuesMap();
    }
}
