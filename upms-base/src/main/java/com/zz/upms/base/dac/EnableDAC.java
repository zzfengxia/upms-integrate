package com.zz.upms.base.dac;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * 启用数据访问控制的注解
 * @author Francis.zz
 * @date 2020-07-10 17:39
 * ************************************
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableDAC {
    /**
     * 指定从响应对象中获取数据访问权限控制结果集的方法。该方法获取到的结果对象必须实现{@link DacField} 接口。
     * 直接实现了{@link DacField} 接口的对象无需指定get方法，直接过滤该对象中非当前登录用户所属的数据。
     */
    String getMethod() default "";
    
    /**
     * 过滤后的数据重新赋值到响应对象的方法，参数必须实现{@link DacField}
     * 必须与 getMethod 同时使用，否则不会生效
     */
    String setMethod() default "";
    
    Class<?> setMethodParams() default Object.class;
}
