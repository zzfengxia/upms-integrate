package com.zz.mq.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-09-23 16:39
 * @desc 异步线程池、调度线程池、自定义线程池等配置
 * ************************************
 */
@Configuration
@EnableAsync       // 开启异步任务
@EnableScheduling  // 开启任务调度（定时任务）
@Import(MdcTaskDecorator.class)
public class ExecutorConfig {
    /**
     * Non web服务需要提前初始化ThreadPoolTaskExecutor到容器，否则会被nacos放入容器中的`ThreadPoolExecutor`抢先注入
     * Web项目会由{@link org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter}中注入的，
     * 而非web项目会因为条件不满足（spring.main.web-application-type: NONE）不会调用
     */
    @Order(0)
    @Bean(name = { "applicationTaskExecutor", AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME })
    @ConditionalOnMissingBean(Executor.class)
    public ThreadPoolTaskExecutor applicationTaskExecutor(TaskExecutorBuilder builder) {
        return builder.build();
    }
}
