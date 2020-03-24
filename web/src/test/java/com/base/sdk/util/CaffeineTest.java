package com.base.sdk.util;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by songyigui on 19-10-25.
 */
public class CaffeineTest {

    @Test
    public void testLoander() {
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.SECONDS)
//                .weakValues()
//                .softValues()
                .build(key -> "default");

        String key = "test";
        String value = cache.get(key);
        System.out.println("get:" + value);
        Map<String, String> map = cache.getAll(Arrays.asList(key));
        System.out.println("getAll:" + map);

    }

    @Test
    public void testAsyncLoander() throws Exception {
        AsyncLoadingCache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .buildAsync(key -> "default");

        String key = "test";
        CompletableFuture<String> value = cache.get(key);
        System.out.println("get:" + value.get());
        CompletableFuture<Map<String, String>> map = cache.getAll(Arrays.asList(key));
        System.out.println("getAll:" + map.get());

        LoadingCache<String, String> loadingCache = cache.synchronous();
        System.out.println("loadingCache:" + loadingCache.get(key));

    }

    @Test
    public void testBuilder() {
        Cache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .removalListener((key, value, cause) -> System.out.println("key:" + key + ",value:" + value + ",cause:" + cause))
                .recordStats()
                .build();
        String key = "test";
        String value = null;
//        value = cache.getIfPresent(key);
//        System.out.println("getIfPresent:"+value);
        value = cache.get(key, s -> "default");
        System.out.println("get:" + value);
        cache.put(key, "abc");
        cache.invalidate(key);
        System.out.println("put:" + cache.get(key, s -> "default"));

        cache.put(key, "abc");
        ConcurrentMap<String, String> map = cache.asMap();
        System.out.println("as map:" + map);
        cache.invalidate(key);
        System.out.println("as map:" + cache.get(key, s -> "default"));
        CacheStats stats = cache.stats();
        System.out.println("stats:" + stats);
    }
}
