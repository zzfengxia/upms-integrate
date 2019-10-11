package com.zz.upms.base.annotation;

import java.lang.annotation.*;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-07-16 10:27
 * @desc EnableExecTimeLog
 * ************************************
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableExecTimeLog {
    /**
     * 参数索引号(从1开始)，作为打印信息的关键字
     *
     * @return
     */
    int argIndex() default -1;
}
