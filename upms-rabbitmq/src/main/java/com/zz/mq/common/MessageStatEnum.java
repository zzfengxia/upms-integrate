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
    DELIVERING(0), DELIVER_SUCCESS(1), DELIVER_FAIL(2), CONSUMED_SUCCESS(3);
    
    @Getter
    private int status;
    
    MessageStatEnum(int status) {
        this.status = status;
    }
}
