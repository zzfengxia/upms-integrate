package com.zz.mq.service;

import java.util.concurrent.TimeUnit;

/**
 * ************************************
 * redis setNX实现的分布式锁，可能受服务器时间不同步，full GC、网络超时等问题造成锁超时误删锁的问题，从而导致多个线程同时持有锁
 * 建议使用redisson实现的分布式锁
 *
 * @author Francis.zz
 * @date   2019-12-03 15:05:22
 * ************************************
 */
public class RedisLock implements AutoCloseable {

    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
    private RedisHelper redisHelper;
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，无限的执行等待
     */
    private int expireMsecs = 60 * 1000;

    /**
     * 锁等待时间，防止线程饥饿
     */
    private int timeoutMsecs = 10 * 1000;

    private volatile boolean locked = false;

    public RedisLock(RedisHelper redisHelper, String lockKey) {
        this.redisHelper = redisHelper;
        this.lockKey = lockKey;
    }

    /**
     * Detailed constructor with default lock expiration of 60000 msecs.
     */
    public RedisLock(RedisHelper redisHelper, String lockKey, int timeoutMsecs) {
        this(redisHelper, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * Detailed constructor.
     */
    public RedisLock(RedisHelper redisHelper, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisHelper, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    /**
     * @return lock key
     */
    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = redisHelper.get(key);
        return obj != null ? obj.toString() : null;
    }

    private boolean setNX(final String key, final String value, long expire, TimeUnit timeUnit) {
        Boolean isSuccess = redisHelper.setNX(key, value, expire, timeUnit);
        return isSuccess != null ? isSuccess : false;
    }

    private String getSet(final String key, final String value) {
        Object obj = redisHelper.getSet(key, value);
        return obj != null ? (String) obj : null;
    }

    /**
     * 获得 lock.
     *
     * @return true if lock is acquired, false acquire timeouted
     */
    public synchronized boolean lock() {
        int timeout = timeoutMsecs;
        while (timeout >= 0) {
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            // 锁到期时间
            String expiresStr = String.valueOf(expires);
            if (this.setNX(lockKey, expiresStr, expireMsecs + 30L, TimeUnit.MILLISECONDS)) {
                // lock acquired
                locked = true;
                return true;
            }

            //redis里的时间
            String currentValueStr = this.get(lockKey);
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                // 可能被别的线程抢先获取到锁
                // lock is expired

                String oldValueStr = this.getSet(lockKey, expiresStr);
                redisHelper.expire(lockKey, expireMsecs + 30L, TimeUnit.MILLISECONDS);
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    // lock acquired
                    locked = true;
                    return true;
                }
            }
            timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;

            try {
                Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
            } catch (InterruptedException e) {
                // no op
            }

        }
        return false;
    }


    /**
     * Acqurired lock release.
     */
    private synchronized void unlock() {
        if (locked) {
            redisHelper.delete(lockKey);
            locked = false;
        }
    }


    @Override
    public void close() {
        unlock();
    }
}
