package com.zz.upms.base.service.base;

import com.zz.upms.base.common.exception.BizException;
import com.zz.upms.base.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * redis setNX实现的分布式锁，可能受服务器时间不同步，full GC、网络超时等问题造成锁超时误删锁的问题，从而导致多个线程同时持有锁
 * 建议使用redisson实现的分布式锁
 */
@Slf4j
public class RedisLock implements AutoCloseable {

    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;
    private RedisHelper redisService;
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

    public RedisLock(RedisHelper redisService, String lockKey) {
        this.redisService = redisService;
        this.lockKey = lockKey;
    }

    /**
     * Detailed constructor with default lock expiration of 60000 msecs.
     */
    public RedisLock(RedisHelper redisService, String lockKey, int timeoutMsecs) {
        this(redisService, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * Detailed constructor.
     */
    public RedisLock(RedisHelper redisService, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisService, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    /**
     * @return lock key
     */
    public String getLockKey() {
        return lockKey;
    }

    private String get(final String key) {
        Object obj = redisService.get(key);
        return obj != null ? obj.toString() : null;
    }

    private boolean setNX(final String key, final String value) {
        Boolean isSuccess = redisService.setNX(key, value);
        return isSuccess != null ? isSuccess : false;
    }

    private String getSet(final String key, final String value) {
        Object obj = redisService.getSet(key, value);
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
            if (this.setNX(lockKey, expiresStr)) {
                // lock acquired
                redisService.expire(lockKey, expireMsecs + 30L, TimeUnit.MILLISECONDS);
                locked = true;
                return true;
            }

            //redis里的时间
            String currentValueStr = this.get(lockKey);
            if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                // 可能被别的线程抢先获取到锁
                // lock is expired

                String oldValueStr = this.getSet(lockKey, expiresStr);
                redisService.expire(lockKey, expireMsecs + 30L, TimeUnit.MILLISECONDS);
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
                throw new BizException(ErrorCode.BIZ_ERROR, e);
            }

        }
        return false;
    }


    /**
     * Acqurired lock release.
     */
    private synchronized void unlock() {
        if (locked) {
            log.info("lockKey：{}，serializeKey：{}，value：{}", lockKey, this.get(lockKey));
            redisService.delete(lockKey);
            locked = false;
        }
    }


    @Override
    public void close() {
        unlock();
    }
}
