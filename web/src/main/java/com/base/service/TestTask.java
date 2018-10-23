package com.base.service;

import com.base.model.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 */
@Component
public class TestTask {
    private static final Logger logger = LoggerFactory.getLogger(TestTask.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.message}")
    private String messageRoutingKey;

    @Value("${rabbit.queue.sendmessage}")
    private String sendMessageQueue;


    @Autowired
    private TestService        testService;

    private final AtomicInteger count = new AtomicInteger(1);

//    @Scheduled(cron = "0/3 * * * * *")
//    public void sendMessageSchedule() {
//        for (int i = 0; i < 3; i++) {
//            int result = count.getAndIncrement();
//            logger.info("sendMessageSchedule result = {}", result);
//            QueueMessage queueMessage = new QueueMessage("test msg: " + result);
//            Message msg = queueMessage.toAmqpMessage();
//            logger.info("send MQ message = {}", msg);
//            rabbitTemplate.send(testExchange, messageRoutingKey, msg);
//        }
//    }


    @RabbitListener(queues = "test.sendmessage")
    public void handleSendMessage(Message message) {
        try {
            logger.info("receive MQ message = {}", message);
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);

            logger.info("receive MQ status message: {}", msg.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }


}
