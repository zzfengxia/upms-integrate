package com.zz.upms.base.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Francis.zz on 2018/3/14.
 */
@Component
public class RedisHelper {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static String redisCacheKey(Object id, Class clz) {
        return redisCacheKey(id, clz.getSimpleName());
    }
    public static String redisCacheKey(Object id, String className) {
        return String.format("cache:%s:%s", className, id);
    }

    public String hMGet(String key, String hashKey) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();

        return operations.get(key, hashKey);
    }

    public void hMSet(String key, String hashKey, String hashValue) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();

        operations.put(key, hashKey, hashValue);
    }

    public void hMSet(String key, Map<String, String> hashs) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();

        operations.putAll(key, hashs);
    }

    public void hMDel(String key, Object... hashKeys) {
        HashOperations<String, String, String> operations = redisTemplate.opsForHash();

        operations.delete(key, hashKeys);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    public void put(String key, String value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        operations.set(key,value);
    }
    
    public void put(String key, String value, Long time, TimeUnit timeUnit) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        
        operations.set(key, value, time, timeUnit);
    }

    public String get(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        return operations.get(key);
    }

    public void expire(String key, Long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }

    /**
     * 获取sort set中指定key的所有数据
     *
     * @param key
     * @param reverse 是否倒序
     * @return
     */
    public Set<String> zrangeAll(String key, boolean reverse) {
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        return reverse ? operations.reverseRange(key, 0, -1) : operations.range(key, 0, -1);
    }

    /**
     * 获取sort set中指定key的所有数据
     *
     * @param key
     * @param reverse 是否倒序
     * @return
     */
    public Set<ZSetOperations.TypedTuple<String>> zrangeAllWithScore(String key, boolean reverse) {
        ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
        return reverse ? operations.reverseRangeWithScores(key, 0, -1) : operations.rangeWithScores(key, 0, -1);
    }

    /**
     * 使用该方法实现分布式锁，必须确保加锁与设置有效期操作的原子性
     *
     * @param key
     * @param value
     * @param expire
     * @param timeUnit
     * @return
     */
    public Boolean setNX(String key, String value, long expire, TimeUnit timeUnit) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        return operations.setIfAbsent(key, value, expire, timeUnit);
    }

    public String getSet(String key, String value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        return operations.getAndSet(key, value);
    }
}
