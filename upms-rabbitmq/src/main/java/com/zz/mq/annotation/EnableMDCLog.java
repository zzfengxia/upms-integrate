package com.zz.mq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-29 11:40
 * ************************************
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMDCLog {
    /**
     * Message参数在参数数组中的下标索引，用来获取Message参数，从而获取message id. 如果不设置该索引或者拦截方法中没有Message 参数，则使用随机生成的UUID作为session id
     * 以0开始
     *
     * @see {@link org.springframework.amqp.core.Message}
     */
    int argIndex() default -1;
}
