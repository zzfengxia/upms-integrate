package com.zz.mq.common;

import lombok.Getter;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-03-10 15:40
 * ************************************
 */
public enum  MessageStatEnum {
    /**
     * 待确认
     */
    WAIT_CONFIRM(0),
    /**
     * 投递成功
     */
    DELIVER_SUCCESS(1),
    /**
     * 投递失败
     */
    DELIVER_FAIL(2),
    /**
     * 已消费
     */
    CONSUMED_SUCCESS(3);
    
    @Getter
    private int status;
    
    MessageStatEnum(int status) {
        this.status = status;
    }
}
