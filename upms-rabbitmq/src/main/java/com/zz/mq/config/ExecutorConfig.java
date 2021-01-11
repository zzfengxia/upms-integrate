package com.zz.mq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
@EnableAsync
public class ExecutorConfig {
    @Bean
    public AsyncTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("async-executor");
        // 装饰异步线程执行代码
        executor.setTaskDecorator(new MdcTaskDecorator());
        executor.setMaxPoolSize(30);
        executor.setCorePoolSize(10);

        return executor;
    }
}
