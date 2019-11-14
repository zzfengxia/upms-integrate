package com.zz.upms.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.zz.upms.base.common.constans.Constants;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
 * @desc 数据源配置
 * ************************************
 */
@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    @ConditionalOnProperty("spring.datasource.druid.url")
    public DataSource dataSourceOne(MyStatLogger statLogger) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        // 自定义输出统计信息
        //dataSource.setStatLogger(statLogger);
        // 配置了timeBetweenLogStatsMillis属性（大于0）之后，就会定时输出统计信息到日志中。每次输出日志会导致清零（reset）连接池相关的计数器。
        dataSource.setTimeBetweenLogStatsMillis(1000 * 60 * 30);

        return dataSource;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.one")
    public DataSource dataSourceMaster(MyStatLogger statLogger) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        // 配置了timeBetweenLogStatsMillis属性（大于0）之后，就会定时输出统计信息到日志中。每次输出日志会导致清零（reset）连接池相关的计数器。
        dataSource.setTimeBetweenLogStatsMillis(1000 * 60 * 30);

        return dataSource;
    }

    @Bean
    @ConfigurationProperties("spring.datasource.druid.two")
    public DataSource dataSourceSlave(MyStatLogger statLogger) {
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        // 配置了timeBetweenLogStatsMillis属性（大于0）之后，就会定时输出统计信息到日志中。每次输出日志会导致清零（reset）连接池相关的计数器。
        dataSource.setTimeBetweenLogStatsMillis(1000 * 60 * 30);

        return dataSource;
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource dataSourceMaster, DataSource dataSourceSlave) {
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put(Constants.MASTE_SOURCE, dataSourceMaster);
        targetDataSources.put(Constants.SLAVE_SOURCE, dataSourceSlave);

        return new DynamicDataSource(dataSourceMaster, targetDataSources);
    }
}
