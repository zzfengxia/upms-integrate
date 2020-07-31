package com.zz.mq.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * ************************************
 * create by Intellij IDEA
 * redis配置。自定义序列化方式等。
 * @author Francis.zz
 * @date 2018-06-09 17:44
 * ************************************
 */
@Configuration
public class RedisConfig {
    /**
     * 自定义RedisTemplate配置覆盖默认redis注入
     * 查看默认配置{@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
     *
     * @return
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> cusRedisTemplate = new StringRedisTemplate();
        // 使用FastJson序列化value
        GenericFastJsonRedisSerializer jsonRedisSerializer = new GenericFastJsonRedisSerializer();
        cusRedisTemplate.setKeySerializer(new StringRedisSerializer());
        cusRedisTemplate.setValueSerializer(jsonRedisSerializer);
        cusRedisTemplate.setHashValueSerializer(jsonRedisSerializer);

        cusRedisTemplate.setConnectionFactory(factory);
        // 手动初始化
        cusRedisTemplate.afterPropertiesSet();

        return cusRedisTemplate;
    }
}
