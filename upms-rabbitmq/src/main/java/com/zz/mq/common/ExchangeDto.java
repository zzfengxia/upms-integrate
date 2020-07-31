package com.zz.mq.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 * 用于动态创建交换机
 * @author Francis.zz
 * @date 2020-07-27 10:40
 * ************************************
 */
@Getter
@Setter
public class ExchangeDto {
    private String exchangeName;
    private String exchangeType;
    /**
     * 是否持久化
     */
    private boolean durable = true;
    /**
     * 是否自动删除，断开连接数自动删除交换机
     */
    private boolean autoDelete = false;
    /**
     * 是否开启延迟功能
     */
    private boolean delayed = false;
    /**
     * 交换机参数
     */
    private Map<String, Object> args;
}
