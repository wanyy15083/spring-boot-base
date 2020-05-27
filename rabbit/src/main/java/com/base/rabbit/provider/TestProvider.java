package com.base.rabbit.provider;

import com.base.rabbit.message.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by songyigui on 19-10-28.
 */
//@Component
public class TestProvider {
    private static final Logger logger = LoggerFactory.getLogger(TestProvider.class);


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

    @Value("${rabbit.exchange.ex_delay}")
    private String delayExchange;

    @Value("${rabbit.queue.delay}")
    private String delayQueue;

    @Value("${rabbit.routing.key.delay}")
    private String delayRoutingKey;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(new TestConfirmCallBack());
        rabbitTemplate.setReturnCallback(new TestReturnCallBack());
    }

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

//    @Scheduled(cron = "0/10 * * * * *")
//    public void testProvideDelay() {
//        for (int i = 0; i < 300; i++) {
//            int result = count.getAndIncrement();
//            QueueMessage queueMessage = new QueueMessage("test delay msg: " + result);
////            Message msg = queueMessage.toAmqpMessageForTTL(30000 * i + "");//30s
//            Message msg = queueMessage.toAmqpMessageForDelay(10000 * new Random().nextInt(10));
////            Message msg = queueMessage.toAmqpMessage();
//            logger.info("testProvideDelay message = {} delay = {}", queueMessage, msg.getMessageProperties().getDelay());
//            CorrelationData data = new CorrelationData(queueMessage.getTaskId());
//
//            rabbitTemplate.send(delayExchange, delayRoutingKey, msg, data);
//        }
//
//        int result = count.getAndIncrement();
//        QueueMessage queueMessage = new QueueMessage("test delay msg: " + result);
////            Message msg = queueMessage.toAmqpMessageForTTL(30000 * i + "");//30s
//        Message msg = queueMessage.toAmqpMessageForDelay(7200000);
////            Message msg = queueMessage.toAmqpMessage();
//        logger.info("testProvideDelay message = {} delay = {}", queueMessage, msg.getMessageProperties().getDelay());
//        CorrelationData data = new CorrelationData(queueMessage.getTaskId());
//
//        rabbitTemplate.send(delayExchange, delayRoutingKey, msg, data);
//
//    }


    @Scheduled(cron = "0/10 * * * * *")
    public void testProvideDelay() {
//        for (int i = 0; i < 300; i++) {
//            int result = count.getAndIncrement();
//            QueueMessage queueMessage = new QueueMessage("test delay msg: " + result);
//            Message msg = queueMessage.toAmqpMessage();
//            logger.info("testProvideDelay message = {} delay = {}", queueMessage, msg.getMessageProperties().getDelay());
//            int delay = 2000 * i;
//            String queueName = "test_delay_" + delay / 1000;
//            String routingKey = "delay_" + delay / 1000;
//            Map<String, Object> args = new HashMap<String, Object>();
////            args.put("x-message-ttl", delay);
//            args.put("x-expires", 180);
//            args.put("x-dead-letter-exchange", testExchange);
//            args.put("x-dead-letter-routing-key", testRoutingKey);
//            Exchange exchange = new DirectExchange(testExchange, true, false);
//            amqpAdmin.declareExchange(exchange);
////            amqpAdmin.deleteQueue(queueName);
//
//            Queue queue = new Queue(queueName, false, false, false, args);
//            amqpAdmin.declareQueue(queue);
//
//            Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
//            amqpAdmin.declareBinding(binding);
//
//            CorrelationData data = new CorrelationData(queueMessage.getTaskId());
//            rabbitTemplate.send(testExchange, routingKey, msg, data);
//        }

        int result = count.getAndIncrement();
        QueueMessage queueMessage = new QueueMessage("test delay msg: " + result);
        Message msg = queueMessage.toAmqpMessage();
        logger.info("testProvideDelay message = {} delay = {}", queueMessage, msg.getMessageProperties().getDelay());
        int delay = 2000;
        String queueName = "test_delay_" + delay / 1000;
        String routingKey = "delay_" + delay / 1000;
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-expires", 10000);

        args.put("x-message-ttl", delay);
        args.put("x-dead-letter-exchange", testExchange);
        args.put("x-dead-letter-routing-key", testRoutingKey);
        Exchange exchange = new DirectExchange(testExchange, true, false);
        amqpAdmin.declareExchange(exchange);

        Queue queue = new Queue(queueName, true, false, false, args);
        amqpAdmin.declareQueue(queue);

        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
        amqpAdmin.declareBinding(binding);

        CorrelationData data = new CorrelationData(queueMessage.getTaskId());
        rabbitTemplate.send(testExchange, routingKey, msg, data);

    }


}
