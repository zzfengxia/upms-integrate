package com.zz.mq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.zz.mq.dao"})
@ComponentScan(basePackages = {"com.zz.mq"})
public class UpmsRabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(UpmsRabbitmqApplication.class, args);
    }

}
