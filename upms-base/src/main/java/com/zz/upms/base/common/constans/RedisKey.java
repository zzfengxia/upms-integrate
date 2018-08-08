package com.zz.upms.base.common.constans;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-02 16:37
 * @desc RedisKey
 * ************************************
 */
public class RedisKey {
    public static String redisKey(String key, String... params) {
        return String.format(key, params);
    }
    /**
     * 系统配置KEY
     */
    public static final String KEY_SERVER_CONFIG = "config:server:hash";

    /**
     * 数据字典缓存时间hash key
     */
    public static final String H_CACHE_TIME = "sys:cacheTime";

    public static final String KEY_SYSTEM_PARAMS = "system:params:%s";
}
