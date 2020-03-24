package com.base.sdk.redisson;

import com.base.*;
import com.base.sdk.redisson.mapreduce.*;
import org.junit.*;
import org.junit.runner.*;
import org.redisson.*;
import org.redisson.api.*;
import org.redisson.api.annotation.*;
import org.redisson.api.mapreduce.*;
import org.redisson.config.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by songyigui on 2017/7/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RedissonTest {
    private static final Logger logger = LoggerFactory.getLogger(RedissonTest.class);

    @Autowired
    private RedissonClient redisson;


    @Test
    public void testMapReduce() {
        RMap<String, String> map = redisson.getMap("wordsMap");
        map.put("line1", "Alice was beginning to get very tired");
        map.put("line2", "of sitting by her sister on the bank and");
        map.put("line3", "of having nothing to do once or twice she");
        map.put("line4", "had peeped into the book her sister was reading");
        map.put("line5", "but it had no pictures or conversations in it");
        map.put("line6", "and what is the use of a book");
        map.put("line7", "thought Alice without pictures or conversation");

        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(redisson.getConfig());
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
        RMapReduce<String, String, String, Integer> mapReduce = map.<String, Integer>mapReduce().mapper(new WordMapper()).reducer(new WordReducer());
        Map<String, Integer> mapToNumber = mapReduce.execute();
        Integer totalWordsAmount = mapReduce.execute(new WordCollator());
        System.out.println(mapToNumber + ":" + totalWordsAmount);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        node.shutdown();
    }

    @Test
    public void testSchedule() throws InterruptedException {
        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(redisson.getConfig());
        nodeConfig.setExecutorServiceWorkers(Collections.singletonMap("myExecutor", 1));
//        nodeConfig.setMapReduceWorkers(3);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
        RScheduledExecutorService executorService = redisson.getExecutorService("myExecutor");
        executorService.schedule(new RunnableTask(), CronSchedule.of("0/5 * * * * ?"));

        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void testExecutor() {
        RExecutorService executorService = redisson.getExecutorService("myExecutor");
        executorService.submit(new RunnableTask());

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLock() throws Exception {
        RLock lock = redisson.getLock("myLock");
        Runnable task = () -> {
            try {
                lock.lock();
                System.out.println("获得锁==========");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("执行完成==========");

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

        };
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.run();
        t2.run();
        t1.join();
        t2.join();

        TimeUnit.SECONDS.sleep(10);
        System.out.println("结束==========");


//        try {
//            lock.lock();
//            System.out.println("获得锁==========");
//            TimeUnit.SECONDS.sleep(1);
//            System.out.println("执行完成==========");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            lock.unlock();
//        }


    }

    public static class CallableTask implements Callable<Long> {

        @RInject
        RedissonClient redisson;

        @Override
        public Long call() throws Exception {
            RMap<String, String> map = redisson.getMap("myMap");
            if (map == null) {
                map.put("test" + 0, "" + 0);
            } else {
                map.put("test" + map.size(), "" + map.size());
            }
            return Long.valueOf(map.get("test" + (map.size() - 1)));
        }
    }

    public static class RunnableTask implements Runnable {
        @RInject
        RedissonClient redisson;

        @Override
        public void run() {
            RMap<String, String> map = redisson.getMap("myMap");
            if (map == null) {
                map.put("test" + 0, "" + 0);
            } else {
                map.put("test" + map.size(), "" + map.size());
            }
            System.out.println(Long.valueOf(map.get("test" + (map.size() - 1))));
        }

    }
}


