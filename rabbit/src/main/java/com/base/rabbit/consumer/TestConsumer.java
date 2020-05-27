package com.base.rabbit.consumer;

import com.base.rabbit.message.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Created by songyigui on 19-10-28.
 */
//@Component
public class TestConsumer {
    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);


//    @Autowired
//    private RabbitConsumer rabbitConsumer;

//    @Scheduled(cron = "0/10 * * * * *")
//    public void testConsumeDelay() throws InterruptedException {
//        rabbitConsumer.registry();
//        Thread.sleep(Integer.MAX_VALUE);
//
//    }

    @RabbitListener(queues = "${rabbit.queue.test}")
    public void testConsume(Message message) {
//        try {
        QueueMessage msg = QueueMessage.fromAmqpMessage(message);

        logger.info("testConsume message: {}", msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.info("failed to process message {}, but it's consumed yet.", message);
//        }
//        int a = 1 / 0;
    }

    //    @RabbitListener(queues = "test.rabbit.delay1")
    public void testConsume1(Message message) {
        try {
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);

            logger.info("testConsume message: {}", msg.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }

//    @RabbitListener(queues = "${rabbit.queue.delay}")
//    public void testConsume(Message message) {
//        QueueMessage msg = QueueMessage.fromAmqpMessage(message);
//
//        logger.info("testConsume message: {}", msg);
//    }
}
