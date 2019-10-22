package com.base.task;

import com.base.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * standalone (distribute lock)
 * Created by songyigui on 19-10-12.
 */
@Component
public class DelayProcessTask {
    private static final Logger logger    = LoggerFactory.getLogger(DelayProcessTask.class);
    public static final String  DELAY_KEY = "delay_order";

    @Autowired
    private StringRedisTemplate redisTemplate;

//    @Scheduled(cron = "0/1 * * * * *")
    public void produceOrder() {
        int currentTime = CommonUtils.getCurrentTimeStamp();
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        for (int i = 0; i < 10000; i++) {
            zSetOperations.add(DELAY_KEY, CommonUtils.generateUUID(), currentTime+10);
        }
        logger.info("订单生产完成================");
    }

//    @Scheduled(cron = "0/1 * * * * *")
    public void condumeOrder() {
        int currentTime = CommonUtils.getCurrentTimeStamp();
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        Set<String> delayOrders = zSetOperations.rangeByScore(DELAY_KEY, 0, currentTime);
        if (delayOrders == null || delayOrders.size()==0) {
            logger.info("暂时没有订单================");
        }

        for (String delayOrder : delayOrders) {
            zSetOperations.remove(DELAY_KEY, delayOrder);
        }
        logger.info("订单处理完成================");
    }
}
