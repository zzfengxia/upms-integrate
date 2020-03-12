package com.zz.mq.config;

import lombok.Getter;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-03-11 10:25
 * ************************************
 */
public enum QueueEnum {
    PERSIST_DB_QUEUE("demo.db.exchange", "demo.db.routingKey", "demo.db.queue");
    
    QueueEnum(String exchange, String routingKey, String queue) {
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.queue = queue;
    }
    
    QueueEnum(String exchange, String queue) {
        this.exchange = exchange;
        this.queue = queue;
    }
    
    @Getter
    private String exchange;
    @Getter
    private String routingKey;
    @Getter
    private String queue;
}
