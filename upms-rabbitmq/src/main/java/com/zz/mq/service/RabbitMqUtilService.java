package com.zz.mq.service;

import com.rabbitmq.client.AMQP;
import com.zz.mq.common.ExchangeDto;
import com.zz.mq.common.ExchangeEnum;
import com.zz.mq.common.QueueDto;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 * 动态创建MQ交换机、队列、以及绑定关系.获取队列消息数量
 * @author Francis.zz
 * @date 2020-07-24 10:28
 * ************************************
 */
@Service
public class RabbitMqUtilService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;
    
    public long getQueueMessageCount(String queueName) {
        List<Long> result = new ArrayList<>(1);
        rabbitTemplate.execute(channel -> {
            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queueName);
            //获取队列中的消息个数
            int queueCount = declareOk.getMessageCount();
            result.add((long) queueCount);
            return null;
        });
        
        return result.get(0);
    }
    
    /**
     * 动态声明交换机
     */
    public void declareExchange(ExchangeDto exchange) {
        amqpAdmin.declareExchange(createExchange(exchange));
    }
    
    /**
     * 动态删除交换机
     */
    public boolean deleteExchange(String exchangeName) {
        return amqpAdmin.deleteExchange(exchangeName);
    }
    
    /**
     * 动态删除队列
     */
    public boolean deleteQueue(String queueName) {
        return amqpAdmin.deleteQueue(queueName);
    }
    
    /**
     * 动态创建队列及其绑定关系
     */
    public void declareQueueAndBinding(QueueDto queueDto) {
        Queue queue = new Queue(queueDto.getQueueName(), queueDto.isDurable(), queueDto.isExclusive(),
                queueDto.isAutoDelete(), queueDto.getArguments());
        amqpAdmin.declareQueue(queue);
    
        ExchangeDto exchangeDto = queueDto.getExchange();
        BindingBuilder.DestinationConfigurer destinationConfigurer = BindingBuilder.bind(queue);
        Binding binding = null;
        switch (exchangeDto.getExchangeType()) {
            case ExchangeTypes.FANOUT:
                binding = destinationConfigurer.to((FanoutExchange) RabbitMqUtilService.createExchange(exchangeDto));
            
                break;
            case ExchangeTypes.TOPIC:
                binding = destinationConfigurer.to((TopicExchange) RabbitMqUtilService.createExchange(exchangeDto))
                        .with(queueDto.getRoutingKey());
            
                break;
            case ExchangeTypes.HEADERS:
                // todo HeaderExchange用的少，这里只是范例，实际使用需要补全Header配置
                binding = destinationConfigurer.to((HeadersExchange) RabbitMqUtilService.createExchange(exchangeDto))
                        .whereAll("").exist();
            
                break;
            case ExchangeTypes.DIRECT:
            default:
                binding = destinationConfigurer.to((DirectExchange) RabbitMqUtilService.createExchange(exchangeDto))
                        .with(queueDto.getRoutingKey());
            
                break;
        }
    
        amqpAdmin.declareBinding(binding);
    }
    
    
    public static <T extends Exchange> T createExchangeWithEnum(ExchangeEnum exchangeEnum) {
        ExchangeBuilder exchangeBuilder = new ExchangeBuilder(exchangeEnum.getExchangeName(), exchangeEnum.getExchangeType());
        
        exchangeBuilder.durable(exchangeEnum.isDurable());
        if(exchangeEnum.getArgs() != null && !exchangeEnum.getArgs().isEmpty()) {
            exchangeBuilder.withArguments(exchangeEnum.getArgs());
        }
        if(exchangeEnum.isAutoDelete()) {
            exchangeBuilder.autoDelete();
        }
        if(exchangeEnum.isDelayed()) {
            exchangeBuilder.delayed();
        }
        
        return (T) exchangeBuilder.build();
    }
    
    public static <T extends Exchange> T createExchange(ExchangeDto exchangeDto) {
        ExchangeBuilder exchangeBuilder = new ExchangeBuilder(exchangeDto.getExchangeName(), exchangeDto.getExchangeType());
        
        exchangeBuilder.durable(exchangeDto.isDurable());
        if(exchangeDto.getArgs() != null && !exchangeDto.getArgs().isEmpty()) {
            exchangeBuilder.withArguments(exchangeDto.getArgs());
        }
        if(exchangeDto.isAutoDelete()) {
            exchangeBuilder.autoDelete();
        }
        if(exchangeDto.isDelayed()) {
            exchangeBuilder.delayed();
        }
        
        return (T) exchangeBuilder.build();
    }
}
