package com.base.rabbit.provider;

import com.base.rabbit.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by songyigui on 19-10-28.
 */
//@Component
public class TestProvider1 {
    private static final Logger logger = LoggerFactory.getLogger(TestProvider1.class);


    private final AtomicInteger count = new AtomicInteger(1);


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.test}")
    private String testRoutingKey;

    @Value("${rabbit.routing.key.test_delay}")
    private String testDelayRoutingKey;

    @Value("${rabbit.queue.test_delay}")
    private String testDelayQueue;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(new TestConfirmCallBack());
    }

    @Scheduled(cron = "0/3 * * * * *")
    public void send() {
        for (int i = 0; i < 3; i++) {
            Event event = new Event();
            int id = count.getAndIncrement();
            event.setId(id);
            event.setName("event--" + id);
            logger.info("testProvide message = {}", event);
            rabbitTemplate.convertAndSend(testExchange, testDelayRoutingKey, event, new CorrelationData(String.valueOf(event.getId())));
        }
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
