package com.zz.upms.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

import javax.sql.DataSource;

/**
 * 自定义动态数据源配置需要排除{@link org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration}，
 * 可以在配置文件排除，或者在这里exclude
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration}
 * {@link org.springframework.context.annotation.ComponentScan} 注解只能扫描spring-boot项目包内的bean并注册到spring容器中，
 * 因此需要 EnableAutoConfiguration 注解来注册项目包外的bean。而spring.factories文件，则是用来记录项目包外需要注册的bean类名
 *
 * {@link com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration#sqlSessionFactory(DataSource)} 这里会先创建DataSource Bean，
 * 如果配置了多数据源，则必须要有一个数据源指定@Primary，比如{@link com.zz.upms.admin.config.DataSourceConfig#dataSource(DataSource, DataSource)}动态数据源，
 * 这里优化先初始化需要被注入的ds1,ds2
 *
 */
//@Import(DataSourceConfig.class)
@MapperScan(basePackages = {"com.zz.upms.base.dao"})
//@NacosPropertySource(dataId = "test", autoRefreshed = true)
@SpringBootApplication(scanBasePackages = {"com.zz.upms"})
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
