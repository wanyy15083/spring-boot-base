package com.base.rabbit.provider;

import com.base.rabbit.message.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by songyigui on 19-10-28.
 */
@Component
public class TestProvider {
    private static final Logger logger = LoggerFactory.getLogger(Component.class);


    private final AtomicInteger count = new AtomicInteger(1);


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.test}")
    private String testRoutingKey;

    @Value("${rabbit.routing.key.test_delay}")
    private String testDelayRoutingKey;

    //    @Scheduled(cron = "0/3 * * * * *")
    public void testProvide() {
        for (int i = 0; i < 3; i++) {
            int result = count.getAndIncrement();
            QueueMessage queueMessage = new QueueMessage("test msg: " + result);
            Message msg = queueMessage.toAmqpMessage();
            logger.info("testProvide message = {}", queueMessage.toString());
            rabbitTemplate.send(testExchange, testRoutingKey, msg);
        }
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void testProvideDelay() {
        for (int i = 0; i < 3; i++) {
            int result = count.getAndIncrement();
            QueueMessage queueMessage = new QueueMessage("test delay msg: " + result);
            Message msg = queueMessage.toAmqpMessageForTTL(30000 * i + "");
            logger.info("testProvideDelay message = {}", queueMessage.toString());
            rabbitTemplate.send(testExchange, testDelayRoutingKey, msg);
        }

    }


}
