package com.base.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 */
@Configuration
public class RabbitConfig {


    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.message}")
    private String messageRoutingKey;

    @Value("${rabbit.queue.sendmessage}")
    private String sendMessageQueue;

    @Value("${rabbit.routing.key.status}")
    private String statusRoutingKey;

    @Value("${rabbit.queue.statuschange}")
    private String statusChangeQueue;

    @Value("${rabbit.exchange.ex_test_dead}")
    private String testDeadExchange;

    @Value("${rabbit.routing.key.dead}")
    private String deadRoutingKey;

    @Value("${rabbit.queue.dead}")
    private String deadQueue;

    @Bean
    public Queue statuschangeQueue() {
        return new Queue(statusChangeQueue, true, false, false);
    }

    @Bean
    public Queue sendmessageQueue() {
        return new Queue(sendMessageQueue, true, false, false);
    }

    /**
     * delay
     *
     * @return
     */
//    @Bean
//    public Queue sendmessageQueue() {
//        Map<String, Object> arguments = new HashMap<>();
//        arguments.put("x-message-ttl", 10000L);
//        arguments.put("x-ha-policy", "all");
//        arguments.put("x-dead-letter-exchange", "ex_test_dead");
//        return new Queue("sendmessage", true, false, false, arguments);
//    }
    @Bean
    public Queue deadQueue() {
        return new Queue(deadQueue, true, false, false);
    }

    @Bean
    public DirectExchange exTestExchange() {
        return new DirectExchange(testExchange, true, false);
    }

    @Bean
    public DirectExchange exTestDeadExchange() {
        return new DirectExchange(testDeadExchange, true, false);
    }


    @Bean
    public Binding bindingStatus(Queue statuschangeQueue, DirectExchange exTestExchange) {
        return BindingBuilder.bind(statuschangeQueue).to(exTestExchange).with(statusRoutingKey);
    }

    @Bean
    public Binding bindingMessage(Queue sendmessageQueue, DirectExchange exTestExchange) {
        return BindingBuilder.bind(sendmessageQueue).to(exTestExchange).with(messageRoutingKey);
    }

    @Bean
    public Binding bindingDeadd(Queue deadQueue, DirectExchange exTestDeadExchange) {
        return BindingBuilder.bind(deadQueue).to(exTestDeadExchange).with(deadRoutingKey);
    }


}
