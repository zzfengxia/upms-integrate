package com.zz.generator;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zz.generator.common.Constants;
import com.zz.generator.config.DynamicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 17:09
 * @desc AutoGeneratorApplication
 * ************************************
 */
@SpringBootApplication(exclude={DruidDataSourceAutoConfigure.class, DataSourceAutoConfiguration.class})
public class GeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeneratorApplication.class, args);
    }

    @Bean("defaultSource")
    //@ConfigurationProperties(prefix = "spring.datasource")
    DataSource defaultSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    DynamicDataSource primaryDataSource(@Qualifier("defaultSource") DataSource dataSource) {
        Map<Object, Object> map = new HashMap<>();
        map.put(Constants.DEFAULT_SOURCE_KEY, dataSource);

        DynamicDataSource dynamicDataSource = new DynamicDataSource();

        dynamicDataSource.setTargetDataSources(map);
        dynamicDataSource.setDefaultTargetDataSource(dataSource);
        return dynamicDataSource;
    }
}
