package com.base.sdk.limit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by songyigui on 2018/5/15.
 */
public class RateLimiterTest {
    private final static long limitSec = 100;

    private final LoadingCache<Long, AtomicLong> counter = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS)
            .build(new CacheLoader<Long, AtomicLong>() {
                @Override
                public AtomicLong load(Long sec) throws Exception {
                    return new AtomicLong(0);
                }
            });

    public boolean limitTimeWindow() throws ExecutionException {
        long currentSec = System.currentTimeMillis() / 1000;
        if (counter.get(currentSec).incrementAndGet() > limitSec) {
            return false;
        }
        return true;
    }

    private RateLimiter rateLimiter = RateLimiter.create(limitSec);

    public boolean limitGuava() {
        rateLimiter.acquire();
        return rateLimiter.tryAcquire();
    }


    /**
     * 令牌桶算法 * 每秒生成 2 個令牌
     */
    private static final RateLimiter limiter = RateLimiter.create(2);

    private void rateLimiter() {// 默认就是 1
        final double acquire = limiter.acquire(1);
        System.out.println("当前时间 - " + LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - 阻塞 - " + acquire + " 通过...");
    }

    @Test
    public void testDemo1() throws InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            service.execute(this::rateLimiter);
        }
        TimeUnit.SECONDS.sleep(5);
    }


    /**
     * 计数器限流算法（允许将任务放入到缓冲队列） * 信号量，用来达到削峰的目的
     */
    private static final Semaphore semaphore = new Semaphore(3);

    private void semaphoreLimiter() {// 队列中允许存活的任务个数不能超过 5 个
        if (semaphore.getQueueLength() > 5) {
            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - 拒絕...");
        } else {
            try {
                semaphore.acquire();
                System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - 通过...");
                //处理核心逻辑
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }
    }

    @Test
    public void testSemaphore() throws InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            service.execute(this::semaphoreLimiter);
        }
        TimeUnit.SECONDS.sleep(5);
    }


    /**
     * 计数器限流算法（比较暴力/超出直接拒绝）
     * <p>
     * Atomic，限制总数
     */
    private static final AtomicInteger atomic = new AtomicInteger(0);

    private void atomicLimiter() {
        // 最大支持 3 個
        if (atomic.get() >= 3) {
            System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - " + "拒絕...");
        } else {
            try {
                atomic.incrementAndGet();
                //处理核心逻辑
                System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName() + " - " + "通过...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                atomic.decrementAndGet();
            }
        }
    }

    @Test
    public void testAtomic() throws InterruptedException {
        final ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            service.execute(this::atomicLimiter);
        }
        TimeUnit.SECONDS.sleep(5);
    }
}

