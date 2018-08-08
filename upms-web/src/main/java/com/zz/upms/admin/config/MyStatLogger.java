package com.zz.upms.admin.config;

import com.alibaba.druid.pool.DruidDataSourceStatLogger;
import com.alibaba.druid.pool.DruidDataSourceStatLoggerAdapter;
import org.springframework.stereotype.Component;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-07 15:02
 * @desc 自定义druid监控输出
 * ************************************
 */
@Component
public class MyStatLogger extends DruidDataSourceStatLoggerAdapter implements DruidDataSourceStatLogger {

}
