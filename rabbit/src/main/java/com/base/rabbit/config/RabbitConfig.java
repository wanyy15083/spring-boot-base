package com.base.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Configuration
public class RabbitConfig {


    @Value("${rabbit.exchange.ex_test}")
    private String testExchange;

    @Value("${rabbit.routing.key.test}")
    private String testRoutingKey;

    @Value("${rabbit.queue.test}")
    private String testQueue;

    @Value("${rabbit.routing.key.test_delay}")
    private String testDelayRoutingKey;

    @Value("${rabbit.queue.test_delay}")
    private String testDelayQueue;

    @Bean
    public Queue testQueue() {
        return new Queue(testQueue, true, false, false);
    }

    @Bean
    public DirectExchange exTestExchange() {
        return new DirectExchange(testExchange, true, false);
    }

    @Bean
    public Binding testBinding(Queue testQueue, DirectExchange exTestExchange) {
        return BindingBuilder.bind(testQueue).to(exTestExchange).with(testRoutingKey);
    }

    @Bean
    public Queue testDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", testExchange);
        arguments.put("x-dead-letter-routing-key", testRoutingKey);
        return new Queue(testDelayQueue, true, false, false, arguments);
    }

    @Bean
    public Binding testDelayQueueBinding(Queue testDelayQueue, DirectExchange exTestExchange) {
        return BindingBuilder.bind(testDelayQueue).to(exTestExchange).with(testDelayRoutingKey);
    }


}
