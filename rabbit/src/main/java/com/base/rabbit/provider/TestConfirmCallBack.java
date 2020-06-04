package com.base.rabbit.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

//@Component
public class TestConfirmCallBack implements RabbitTemplate.ConfirmCallback {
    private static final Logger logger = LoggerFactory.getLogger(TestConfirmCallBack.class);

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//        logger.info("message confirm data: {}, ack: {}, cause: {}", correlationData, ack, cause);
        assert correlationData != null;
        String eventId = correlationData.getId();
        if (ack) {
            logger.info("Publish confirmed event[{}].", eventId);
        } else {
            logger.warn("Publish event[{}] is nacked while publish:{}.", eventId, cause);
        }
    }
}
