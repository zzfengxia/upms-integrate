package com.zz.upms.base.service.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Francis.zz on 2018/3/14.
 */
@Component
public class RedisHelper {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static String redisCacheKey(Object id, Class clz) {
        return String.format("cache:%s:%s", clz.getSimpleName(), id);
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

    public void put(String key, String value) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        operations.set(key,value);
    }

    public String get(String key) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        return operations.get(key);
    }

    public void expire(String key, Long time, TimeUnit timeUnit) {
        redisTemplate.expire(key, time, timeUnit);
    }
}
