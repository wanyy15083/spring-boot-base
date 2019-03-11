package com.base.listener;

import com.base.model.QueueMessage;
import com.base.consumer.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

/**
 */
public class MessageConsumer implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private TestService testService;

    public void setTestService(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void onMessage(Message message) {
        try {
            Thread.sleep(500);
            logger.info("receive MQ message = {}", message);
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);
//            testService.sendRabbitMessage(msg.getPayload());
            logger.info("receive MQ status message: {}", msg.getPayload());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }
}
