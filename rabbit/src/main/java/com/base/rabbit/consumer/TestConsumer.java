package com.base.rabbit.consumer;

import com.base.rabbit.message.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by songyigui on 19-10-28.
 */
@Component
public class TestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);


    @RabbitListener(queues = "test.rabbit")
    public void testConsume(Message message) {
        try {
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);

            logger.info("testConsume message: {}", msg.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }
}
