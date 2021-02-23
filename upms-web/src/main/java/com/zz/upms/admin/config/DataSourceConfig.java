package com.zz.upms.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zz.upms.base.common.constans.Constants;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-07 15:42
 * @desc 动态数据源，多数据源配置，必须在 DataSourceAutoConfiguration 之前。必须排除{@link DataSourceAutoConfiguration}，否则会报循环依赖的错误
 * 有两种方式可以排除，注解或者配置文件方式
 * ************************************
 */

// 必须要在DataSourceAutoConfiguration类之前配置DataSource，或者主启动类上注解排序 DataSourceAutoConfiguration
@Configuration(proxyBeanMethods = false) // 也可以使用@import注解启用该配置
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.shardingsphere.datasource.master")
    @ConditionalOnProperty(prefix = "spring.shardingsphere.datasource.master", name = "url")
    public DataSource dataSourceMaster(MyStatLogger statLogger) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        // 配置了timeBetweenLogStatsMillis属性（大于0）之后，就会定时输出统计信息到日志中。每次输出日志会导致清零（reset）连接池相关的计数器。
        dataSource.setTimeBetweenLogStatsMillis(1000 * 60 * 30);
        
        return dataSource;
    }
    
    @Bean
    @ConfigurationProperties("spring.shardingsphere.datasource.slave")
    @ConditionalOnProperty(name = "spring.shardingsphere.datasource.slave.url")
    public DataSource dataSourceSlave(MyStatLogger statLogger) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        // 配置了timeBetweenLogStatsMillis属性（大于0）之后，就会定时输出统计信息到日志中。每次输出日志会导致清零（reset）连接池相关的计数器。
        dataSource.setTimeBetweenLogStatsMillis(1000 * 60 * 30);
        
        return dataSource;
    }
    
    @Bean
    @Primary // 默认注入源 {@link MybatisPlusAutoConfiguration#sqlSessionFactory}
    @ConditionalOnBean(name = "dataSourceMaster")
    public DynamicDataSource dataSource(final DataSource dataSourceMaster, final DataSource dataSourceSlave, final DataSource shardingDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(Constants.MASTE_SOURCE, dataSourceMaster);
        targetDataSources.put(Constants.SLAVE_SOURCE, dataSourceSlave);
        if(shardingDataSource != null) {
            targetDataSources.put(Constants.DB_SOURCE_SHARDING, shardingDataSource);
        }
        
        return new DynamicDataSource(dataSourceMaster, targetDataSources);
    }
}
