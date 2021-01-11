package com.zz.mq.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-09-23 16:54
 * @desc MDC异步线程复制装饰器
 * ************************************
 */
public class MdcTaskDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(final Runnable runnable) {
        final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();

        return new Runnable() {
            @Override
            public void run() {
                // 复制MDC数据到子线程
                if(copyOfContextMap != null) {
                    MDC.setContextMap(copyOfContextMap);
                }
                runnable.run();
            }
        };
    }
}
