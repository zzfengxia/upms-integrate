package com.zz.mq.common;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-31 11:15
 * ************************************
 */
public class MqConstants {
    public static String redisKey(String key, String... params) {
        return String.format(key, params);
    }
    
    public static final String MQ_CONSUMER_LOCK = "mq:consumer:lock:%s";
}
