package com.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

/**
 * Created by songyigui on 19-10-18.
 */
@Configuration
public class RedisScriptConfig {

    @Value("${redis.rate.limit.location}")
    private String location;

    @Bean
    public RedisScript<Long> rateLimiterLua() {
        return new DefaultRedisScript(location, Long.class);
    }
}
