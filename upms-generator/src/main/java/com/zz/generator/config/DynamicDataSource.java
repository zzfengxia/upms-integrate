package com.zz.generator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-09 17:49
 * @desc 动态数据源，实现运行时创建新数据源
 * ************************************
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private Map<Object, Object> backupTargetDataSources = new ConcurrentHashMap<>();

    /**
     * 切换数据源
     *
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceContext.getDataSourceRoutingKey();
    }

    /**
     * 动态注册数据源
     *
     * @param key
     * @param ds
     */
    public void addDataSourceToTargetDataSource(String key, DataSource ds) {
        log.info("registry datasource [{}]", key);
        this.backupTargetDataSources.put(key, ds);
        super.setTargetDataSources(backupTargetDataSources);

        this.afterPropertiesSet();
    }
}
