package com.base.sdk.util;

import com.base.Application;
import com.base.dao.PersonDao;
import com.base.model.database.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by songyigui on 2017/7/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTest {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private ThreadPoolTaskExecutor zzExecutor;
    @Autowired
    private ThreadPoolTaskExecutor zzExecutor1;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
//    @Autowired
//    private ThreadPoolTaskExecutor threadPoolTaskExecutorTest;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Test
    public void testEx() {
//        zzExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread().getName() + "===123");
//            }
//        });
//        zzExecutor1.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread().getName() + "===123");
//            }
//        });
        zzExecutor1.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "===123");
            }
        });
//        threadPoolTaskExecutorTest.execute(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println(Thread.currentThread().getName() + "===123");
//            }
//        });
    }

    @Test
    public void testDao() {
        redisTemplate.opsForValue().set("key1", "test");
        System.out.println(redisTemplate.opsForValue().get("key1"));
        Person p = new Person();
        p.setFname("8V");
        System.out.println(personDao.getPersons(p));
        System.out.println(personDao.getPersons(p));
        System.out.println(personDao.getPersons(p));
    }


    @Test
    public void testPool() throws Exception {
//        runTasks(3);
//        runTasks(5);
//        runTasks(6);
//        runTasks(10);
//        runTasks(15);
//        runTasks(20);
        runTasks(50);
//
//        threadPoolTaskExecutor.setCorePoolSize(5);
//        threadPoolTaskExecutor.setMaxPoolSize(5);
//        threadPoolTaskExecutor.setQueueCapacity(0);
//        runTasks(3);
//        runTasks(5);
//        runTasks(6);
//        runTasks(10);
//        runTasks(15);
//
//        threadPoolTaskExecutor.setCorePoolSize(5);
//        threadPoolTaskExecutor.setMaxPoolSize(5);
//        threadPoolTaskExecutor.setQueueCapacity(5);
//        runTasks(3);
//        runTasks(5);
//        runTasks(6);
//        runTasks(10);
//        runTasks(15);
//        runTasks(20);


    }

    private void runTasks(int num) throws Exception {
//        threadPoolTaskExecutor.initialize();
        CountDownLatch latch = new CountDownLatch(num);
//        for (int i = 0; i < num; i++) {
//            int ii = i;
//            threadPoolTaskExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                    }
//                    System.out.println(Thread.currentThread().getName() + "task=" + ii);
//                    latch.countDown();
//                }
//            });
//            ThreadPoolExecutor executor = threadPoolTaskExecutor.getThreadPoolExecutor();
//            System.out.println(i + "===线程池信息：" + PoolInfo.getInfo(executor));
//        }
        latch.await();
    }


    public static class PoolInfo {

        // 当前线程池中的工作线程数
        private int     poolSize;
        // 队列中的任务
        private int     queueSize;
        // 线程正在执行的任务
        private int     activeCount;
        // 已经执行完成的任务
        private long    completedTaskCount;
        // 是否允许coreThread超时
        private boolean allowsCoreThreadTimeOut;
        // 核心线程数
        private int     corePoolSize;
        // 最大线程数
        private int     maximumPoolSize;
        // 线程池中曾经最大的线程数量
        private int     largestPoolSize;
        // 线程超时时间
        private long    keepAliveTime;
        // 全部的任务数，队列任务+正在执行+已经执行完成
        private long    taskCount;

        public static PoolInfo getInfo(ThreadPoolExecutor executor) {
            PoolInfo poolInfo = new PoolInfo();
            poolInfo.setPoolSize(executor.getPoolSize());
            poolInfo.setQueueSize(executor.getQueue().size());
            poolInfo.setActiveCount(executor.getActiveCount());
            poolInfo.setCompletedTaskCount(executor.getCompletedTaskCount());
            poolInfo.setAllowsCoreThreadTimeOut(executor.allowsCoreThreadTimeOut());
            poolInfo.setCorePoolSize(executor.getCorePoolSize());
            poolInfo.setMaximumPoolSize(executor.getMaximumPoolSize());
            poolInfo.setLargestPoolSize(executor.getLargestPoolSize());
            poolInfo.setKeepAliveTime(executor.getKeepAliveTime(TimeUnit.SECONDS));
            poolInfo.setTaskCount(executor.getTaskCount());
            return poolInfo;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }

        public int getQueueSize() {
            return queueSize;
        }

        public void setQueueSize(int queueSize) {
            this.queueSize = queueSize;
        }

        public int getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(int activeCount) {
            this.activeCount = activeCount;
        }

        public long getCompletedTaskCount() {
            return completedTaskCount;
        }

        public void setCompletedTaskCount(long completedTaskCount) {
            this.completedTaskCount = completedTaskCount;
        }

        public boolean isAllowsCoreThreadTimeOut() {
            return allowsCoreThreadTimeOut;
        }

        public void setAllowsCoreThreadTimeOut(boolean allowsCoreThreadTimeOut) {
            this.allowsCoreThreadTimeOut = allowsCoreThreadTimeOut;
        }

        public int getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaximumPoolSize() {
            return maximumPoolSize;
        }

        public void setMaximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
        }

        public int getLargestPoolSize() {
            return largestPoolSize;
        }

        public void setLargestPoolSize(int largestPoolSize) {
            this.largestPoolSize = largestPoolSize;
        }

        public long getKeepAliveTime() {
            return keepAliveTime;
        }

        public void setKeepAliveTime(long keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
        }

        public long getTaskCount() {
            return taskCount;
        }

        public void setTaskCount(long taskCount) {
            this.taskCount = taskCount;
        }

        @Override
        public String toString() {
            return "PoolInfo{" +
                    "poolSize=" + poolSize +
                    ", queueSize=" + queueSize +
                    ", activeCount=" + activeCount +
                    ", completedTaskCount=" + completedTaskCount +
                    ", allowsCoreThreadTimeOut=" + allowsCoreThreadTimeOut +
                    ", corePoolSize=" + corePoolSize +
                    ", maximumPoolSize=" + maximumPoolSize +
                    ", largestPoolSize=" + largestPoolSize +
                    ", keepAliveTime=" + keepAliveTime +
                    ", taskCount=" + taskCount +
                    '}';
        }
    }

}


