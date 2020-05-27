package com.base.rabbit.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

//@Component
public class TestReturnCallBack implements RabbitTemplate.ReturnCallback {
    private static final Logger logger = LoggerFactory.getLogger(TestReturnCallBack.class);


    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//        logger.info("message returned msg:{}, replyCode:{}, replyText: {}, exchange: {}, routingKey: {}", message.toString(), replyCode, replyText, exchange, routingKey);
    }
}
