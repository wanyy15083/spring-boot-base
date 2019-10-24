package com.base.sdk.util;

import com.base.Application;
import com.base.util.CommonUtils;
import com.base.util.DistributedLocker;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by songyigui on 19-10-23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DistributedlockTest {
    private static final String ZK_ADDR      = "127.0.0.1:2181";
    private static final String DIS_LOCK_KEY = "dis_lock_key";

    @Autowired
    private DistributedLocker distributedLocker;


    @Test
    public void testDisLock() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                String value = null;
                try {
                    value = distributedLocker.lock(DIS_LOCK_KEY, 5);
                    Thread currentThread = Thread.currentThread();
                    if (CommonUtils.isNotBlank(value)) {
                        System.out.println("线程" + currentThread.getName() + "获取锁成功");
                    } else {
                        System.out.println("线程" + currentThread.getName() + "获取锁失败");
                    }
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    distributedLocker.unlockLua(DIS_LOCK_KEY, value);
                }

            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @Test
    public void testDisLockV1() {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1; i++) {
            executor.submit(() -> {
                String value = null;
                try {
                    value = distributedLocker.lockV1(DIS_LOCK_KEY, 5);
                    Thread currentThread = Thread.currentThread();
                    if (CommonUtils.isNotBlank(value)) {
                        System.out.println("线程" + currentThread.getName() + "获取锁成功");
                    } else {
                        System.out.println("线程" + currentThread.getName() + "获取锁失败");
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    distributedLocker.unlockV1(DIS_LOCK_KEY, value);
                }

            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }

    @Test
    public void testMutesLock() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ZK_ADDR, retryPolicy);
        client.start();
        final InterProcessMutex mutex = new InterProcessMutex(client, "/curator/lock");
//        final InterProcessReadWriteLock readWriteLock = new InterProcessReadWriteLock(client, "/curator/readwrite");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                boolean flag = false;
                try {
                    flag = mutex.acquire(5, TimeUnit.SECONDS);
                    Thread currentThread = Thread.currentThread();
                    if (flag) {
                        System.out.println("线程" + currentThread.getName() + "获取锁成功");
                    } else {
                        System.out.println("线程" + currentThread.getName() + "获取锁失败");
                    }
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (flag) {
                        try {
                            mutex.release();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}
