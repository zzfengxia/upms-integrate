package com.zz.mq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages = {"com.zz.mq"})
@MapperScan(basePackages = {"com.zz.mq.dao"})
//@EnableAsync
public class UpmsRabbitmqApplication extends SpringBootServletInitializer {
    
    /**
     * 继承 SpringBootServletInitializer 并实现configure方法来让程序在外部tomcat等容器启动
     * pom文件排序`spring-boot-starter-tomcat`依赖，无法使用jar方法直接启动，需要放置在外部容器中启动
     */
    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(this.getClass());
        return super.configure(builder);
    }*/
    
    /**
     * 确保消息的可靠投递和可靠消费步骤：
     * 1. 写入消息信息到DB，设置为初始待确认状态，并与消息生产服务的本地事务共用事务，保证原子性。投递消息
     * 2. 开启消息生产者确认模式，投递成功后修改消息状态为成功投递。并且可以使用定时扫描待确认状态消息重新投递的方式兜底
     * 3. 消息幂等消费
     * 4. 开启收到消费确认，成功消费的消息状态更改为已完成。消费执行失败后重新投递消息，并设置重试次数
     * 5. 定时扫描重试次数上限或者未消费的消息，并预警，运营后台处理
     */
    public static void main(String[] args) {
        SpringApplication.run(UpmsRabbitmqApplication.class, args);
    }

}
