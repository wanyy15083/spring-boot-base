package com.base.sdk.limit;

import com.base.Application;
import com.base.util.CommonUtils;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by songyigui on 2018/5/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedisRateLimiterTest {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Long> redisScript;

    @Test
    public void limitTimeWindow() throws Exception {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        for (int i = 0; i < 200; i++) {
            int currentSec = CommonUtils.getCurrentTimeStamp();
            String key = "limit_" + currentSec;
            Long increment = valueOperations.increment(key, 1);
            redisTemplate.expire(key, 2, TimeUnit.SECONDS);
            System.out.println(increment);
            if (increment >= 100) {
                break;
            }
            Thread.sleep(5);
        }
    }

    @Test
    public void limitTokenBucketLua() throws Exception {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<String, Object> map = new HashMap<>();
        map.put("curr_permits", "0");
        map.put("max_permits", "100000000");
        map.put("rate", "100000");
        hashOperations.putAll("token_limit_test", map);
        int runs = 7;
        int it = 3000;
        for (int i = 0; i < runs; i++) {
            long start = System.currentTimeMillis();
            int tNum = 5;
            AtomicLong count = new AtomicLong(0);
            ExecutorService executor = Executors.newFixedThreadPool(tNum);
            final CountDownLatch latch = new CountDownLatch(tNum);
            for (int j = 0; j < tNum; j++) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int k = 0; k < it; k++) {
                            int currentSec = CommonUtils.getCurrentTimeStamp();
                            Long acquire = redisTemplate.execute(redisScript, ImmutableList.of("token_limit_test"), "acquire", "1", currentSec + "", "");
                            long re = count.incrementAndGet();
                        }
                        latch.countDown();
                    }
                });
            }
            latch.await();
            long opsSec = (count.get() * 1000) / (System.currentTimeMillis() - start);
            System.out.format("Run %d, Lua=%,d ops/sec%n", i, opsSec);
        }

    }

    @Test
    public void limitIncrLua() throws Exception {
        for (int i = 0; i < 200; i++) {
            Long acquire = redisTemplate.execute(redisScript, ImmutableList.of("acquireIncr"), "acquireIncr", "", "", "3");
            System.out.println(acquire);
            Thread.sleep(new Random().nextInt(200));
        }

    }
}
