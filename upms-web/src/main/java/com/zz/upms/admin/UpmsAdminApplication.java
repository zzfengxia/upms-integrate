package com.zz.upms.admin;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.zz.upms.base.dao"})
@ComponentScan(basePackages = {"com.zz.upms"})
@NacosPropertySource(dataId = "test", autoRefreshed = true)
public class UpmsAdminApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(UpmsAdminApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UpmsAdminApplication.class);
    }
}
