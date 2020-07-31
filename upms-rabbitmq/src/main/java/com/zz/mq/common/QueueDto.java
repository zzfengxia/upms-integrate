package com.zz.mq.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 * 用于动态创建队列
 * @author Francis.zz
 * @date 2020-07-27 10:40
 * ************************************
 */
@Getter
@Setter
public class QueueDto {
    /**
     * 队列名
     */
    private String queueName;
    /**
     * 投递的路由key，不需要可以不填
     */
    private String routingKey;
    
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
    private boolean exclusive = false;
    
    private boolean autoDelete = false;
    /**
     * 队列参数
     */
    private Map<String, Object> arguments;
    /**
     * 绑定的交换机
     */
    private ExchangeDto exchange;
}
