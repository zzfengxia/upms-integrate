package com.zz.upms.admin;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.zz.upms"})
@MapperScan(basePackages = {"com.zz.upms.base.dao"})
@NacosPropertySource(dataId = "test", autoRefreshed = true)
@EnableCaching
public class UpmsAdminApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(UpmsAdminApplication.class, args);
    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UpmsAdminApplication.class);
    }
}
