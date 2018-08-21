package com.zz.generator.annotation;

import java.lang.annotation.*;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-10 16:36
 * @desc RoutingWith
 * ************************************
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoutingWith {
    String value();
}
