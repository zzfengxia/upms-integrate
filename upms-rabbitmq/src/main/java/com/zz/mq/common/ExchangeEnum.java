package com.zz.mq.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 * 需要创建的交换机枚举，会在系统初始化时统一创建该枚举类中的所有交换机
 *
 * @author Francis.zz
 * @date 2020-07-24 14:46
 * ************************************
 */
public enum ExchangeEnum {
    BACK_EXCHANGE("auto.back.exchange", ExchangeTypes.FANOUT, true, false),
    DELAY_EXCHANGE("auto.delay.exchange", ExchangeTypes.DIRECT, true, false, true, "{\"alternate-exchange\": \"" + ExchangeEnum.BACK_EXCHANGE.exchangeName + "\"}"),
    PERSIST_EXCHANGE("auto.demo.db.exchange", ExchangeTypes.DIRECT, "{\"alternate-exchange\": \"" + ExchangeEnum.BACK_EXCHANGE.exchangeName + "\"}");
    
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
     * 是否使用延迟功能
     */
    private boolean delayed = false;
    /**
     * 交换机参数
     */
    private Map<String, Object> args;
    
    ExchangeEnum(String exchangeName, String exchangeType, boolean durable, boolean autoDelete) {
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.durable = durable;
        this.autoDelete = autoDelete;
    }
    
    ExchangeEnum(String exchangeName, String exchangeType, boolean durable, boolean autoDelete, boolean delayed, @Nullable String argsStr) {
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        this.durable = durable;
        this.autoDelete = autoDelete;
        this.delayed = delayed;
        if(StringUtils.isNotEmpty(argsStr)) {
            args = JSON.parseObject(argsStr, new TypeReference<Map<String, Object>>(){});
        }
    }
    
    ExchangeEnum(String exchangeName, String exchangeType) {
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
    }
    
    ExchangeEnum(String exchangeName, String exchangeType, @Nullable String argsStr) {
        this.exchangeName = exchangeName;
        this.exchangeType = exchangeType;
        if(StringUtils.isNotEmpty(argsStr)) {
            args = JSON.parseObject(argsStr, new TypeReference<Map<String, Object>>(){});
        }
    }
    
    public String getExchangeName() {
        return exchangeName;
    }
    
    public String getExchangeType() {
        return exchangeType;
    }
    
    public boolean isDurable() {
        return durable;
    }
    
    public boolean isAutoDelete() {
        return autoDelete;
    }
    
    public boolean isDelayed() {
        return delayed;
    }
    
    public Map<String, Object> getArgs() {
        return args;
    }
}
