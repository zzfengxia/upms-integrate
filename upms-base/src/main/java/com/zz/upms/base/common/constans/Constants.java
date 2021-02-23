package com.zz.upms.base.common.constans;

/**
 * --------------------------------
 * create by Intellij IDEA.
 *
 * @author Francis.zz
 * @date 2018-03-14 14:54
 * --------------------------------
 */
public final class Constants {
    /**
     * 默认缓存时间(ms)
     */
    public final static long DEFAULT_CACHE_TIME = 1000 * 60 * 2;

    /**
     * 超级管理员账户ID
     */
    public final static Long SUPER_ADMIN = 1L;

    /**
     * 正常状态
     */
    public final static String STATUS_NORMAL = "1";
    /**
     * 禁用，无效状态
     */
    public final static String STATUS_INVALID = "0";

    /**
     * 主数据源
     */
    public static final String MASTE_SOURCE = "masterDB";
    /**
     * 从数据源
     */
    public static final String SLAVE_SOURCE = "slaveDB";
    /**
     * Sharding JDBC数据源
     */
    public static final String DB_SOURCE_SHARDING = "shardingDB";
}
