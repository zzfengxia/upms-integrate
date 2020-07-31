package com.zz.mq.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ************************************
 * create by Intellij IDEA
 * 需要创建的队列枚举，会在系统初始化时统一创建该枚举类中的所有队列，并绑定交换机
 * 而且会创建一份由枚举对象名和queueName为键值对的Bean对象，供消费者监听队列时使用
 * eg:
 * <code>
 *
 * </code>
 *
 * @author Francis.zz
 * @date 2020-03-11 10:25
 * ************************************
 */
public enum QueueEnum {
    PERSIST_DB_QUEUE("auto.demo.db.queue", "demo.db.routingKey", ExchangeEnum.PERSIST_EXCHANGE),
    DELAY_QUEUE("auto.demo.delay.queue", "demo.delay.routingKey", ExchangeEnum.DELAY_EXCHANGE),
    BACK_QUEUE("auto.backup.queue", ExchangeEnum.BACK_EXCHANGE);
    
    /**
     * 队列名
     */
    @Getter
    private String queueName;
    /**
     * 投递的路由key，不需要可以不填
     */
    @Getter
    private String routingKey;
    @Getter
    private boolean durable = true;
    /**
     * 是否是排他队列
     * 为 true 则设置队列为排他的。 如果一个队列被声明为排
     * 他队列， 该队列仅对首次声明它的连接可见， 并在连接断开时自动删除。 这里需要注意
     * 三点： 排他队列是基于连接（ Connection) 可见的， 同一个连接的不同信道（ Channel )
     * 是可以同时访问同一连接创建的排他队列； “ 首次” 是指如果一个连接己经声明了一个
     * 排他队列， 其他连接是不允许建立同名的排他队列的， 这个与普通队列不同； 即使该队
     * 列是持久化的， 一旦连接关闭或者客户端退出， 该排他队列都会被自动删除， 这种队列
     * 适用于一个客户端同时发送和读取消息的应用场景
     */
    @Getter
    private boolean exclusive = false;
    @Getter
    private boolean autoDelete = false;
    /**
     * 队列参数
     */
    @Getter
    private Map<String, Object> arguments;
    /**
     * 绑定的交换机
     */
    @Getter
    private ExchangeEnum exchange;
    
    QueueEnum(String queueName, String routingKey, boolean durable, boolean exclusive, boolean autoDelete, ExchangeEnum exchange, @Nullable String argsStr) {
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.exchange = exchange;
        if(StringUtils.isNotEmpty(argsStr)) {
            arguments = JSON.parseObject(argsStr, new TypeReference<Map<String, Object>>(){});
        }
    }
    
    QueueEnum(String queueName, String routingKey, ExchangeEnum exchange) {
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.exchange = exchange;
    }
    
    QueueEnum(String queueName, ExchangeEnum exchange) {
        this.queueName = queueName;
        this.exchange = exchange;
    }
    
    QueueEnum(String queueName, String routingKey, ExchangeEnum exchange, @Nullable String argsStr) {
        this.queueName = queueName;
        this.routingKey = routingKey;
        this.exchange = exchange;
        if(StringUtils.isNotEmpty(argsStr)) {
            arguments = JSON.parseObject(argsStr, new TypeReference<Map<String, Object>>(){});
        }
    }
    
    public static Map<String, String> queuesMap() {
        return Arrays.stream(QueueEnum.values()).collect(Collectors.toMap(Enum::toString, QueueEnum::getQueueName));
    }
}
