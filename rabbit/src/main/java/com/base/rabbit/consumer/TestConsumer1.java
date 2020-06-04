package com.base.rabbit.consumer;

import com.base.rabbit.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Created by songyigui on 19-10-28.
 */
//@Component
public class TestConsumer1 {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer1.class);

    @RabbitListener(queues = {"${rabbit.queue.test_delay}"})
    public void receive(Event event) {
        logger.info("receive message: {}", event);
        int i =1/0;
    }

}
