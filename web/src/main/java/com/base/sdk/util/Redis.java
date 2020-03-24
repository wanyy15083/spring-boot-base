package com.base.sdk.util;

import org.springframework.beans.factory.annotation.*;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

@Component
public class Redis {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String tryLock(String key, int timeout) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String value = opsForValue.get(key);
        if (value != null) {
            return null;
        }
        String uuid = UUID.randomUUID().toString();
        Boolean res = opsForValue.setIfAbsent(key, uuid, timeout, TimeUnit.SECONDS);
        if (res) {
            return uuid;
        } else {
            return null;
        }
    }

    public void unLock(String key, String uuid) {
        ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();
        String value = opsForValue.get(key);
        if (value == null || !value.equals(uuid)) {
            return;
        }
        redisTemplate.delete(key);
    }

}
