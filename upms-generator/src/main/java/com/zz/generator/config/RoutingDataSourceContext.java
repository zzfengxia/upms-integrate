package com.zz.generator.config;

import com.zz.generator.common.Constants;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-10 10:52
 * @desc RoutingDataSourceContext
 * ************************************
 */
public class RoutingDataSourceContext implements AutoCloseable {
    // holds data source key in thread local:
    static final ThreadLocal<String> threadLocalDataSourceKey = new ThreadLocal<>();

    public static String getDataSourceRoutingKey() {
        String key = threadLocalDataSourceKey.get();
        return key == null ? Constants.DEFAULT_SOURCE_KEY : key;
    }

    public static void setDataSourceRoutingKey(String key) {
        threadLocalDataSourceKey.set(key);
    }

    @Override
    public void close() throws Exception {
        threadLocalDataSourceKey.remove();
    }
}
