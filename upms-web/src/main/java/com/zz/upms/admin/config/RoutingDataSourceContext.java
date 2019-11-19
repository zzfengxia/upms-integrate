package com.zz.upms.admin.config;

import com.zz.upms.base.common.constans.Constants;

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
        return key == null ? Constants.MASTE_SOURCE : key;
    }

    public static void setDataSourceRoutingKey(String key) {
        threadLocalDataSourceKey.set(key);
    }

    public static void clear() {
        threadLocalDataSourceKey.remove();
    }

    @Override
    public void close() throws Exception {
        threadLocalDataSourceKey.remove();
    }
}
