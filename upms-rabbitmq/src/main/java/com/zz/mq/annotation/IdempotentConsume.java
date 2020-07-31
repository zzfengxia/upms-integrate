package com.zz.mq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ************************************
 * create by Intellij IDEA
 * 使用DB的主键确保消息的幂等消费
 *
 * @author Francis.zz
 * @date 2020-03-10 16:20
 * ************************************
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdempotentConsume {
}
