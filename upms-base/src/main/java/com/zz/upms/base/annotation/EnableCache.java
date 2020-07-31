package com.zz.upms.base.annotation;

import com.zz.upms.base.common.constans.Constants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ************************************
 * create by Intellij IDEA
 * 实体类开启缓存后，简单的增删改查会自动缓存操作
 *
 * @author Francis.zz
 * @date 2020-07-17 15:33
 * ************************************
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCache {
    /**
     * 缓存TTL时间，ms
     * @return
     */
    long value() default Constants.DEFAULT_CACHE_TIME;
    
    /**
     * 指定 keyGenerator BeanName
     * @return
     */
    String keyGenerator() default "";
    
    /**
     * 指定key值
     * @return
     */
    String key() default "";
}
