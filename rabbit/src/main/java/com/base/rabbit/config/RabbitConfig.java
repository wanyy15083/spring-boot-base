package com.base.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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

    @Value("${rabbit.exchange.ex_delay}")
    private String delayExchange;

    @Value("${rabbit.queue.delay}")
    private String delayQueue;

    @Value("${rabbit.routing.key.delay}")
    private String delayRoutingKey;

    @Value("${rabbit.exchange.bench}")
    private String benchExchange;

    @Value("${rabbit.queue.bench}")
    private String benchQueue;

    @Value("${rabbit.routingKey.bench}")
    private String benchRoutingKey;

//    @Bean
//    public RabbitAdmin rabbitAdmin () {
//        return new RabbitAdmin()
//    }

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

    @Bean
    public CustomExchange delayExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(delayExchange, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Queue delayQueue() {
        return new Queue(delayQueue, true, false, false);
    }

    @Bean
    public Binding binding(Queue delayQueue, CustomExchange delayExchange) {
        return BindingBuilder.bind(delayQueue).to(delayExchange).with(delayRoutingKey).noargs();
    }


    @Bean
    public CustomExchange benchExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type", "direct");
        return new CustomExchange(benchExchange, "x-delayed-message", true, false, arguments);
    }

    @Bean
    public Queue benchQueue() {
        return new Queue(benchQueue, true, false, false);
    }

    @Bean
    public Binding benchBinding(Queue benchQueue, CustomExchange benchExchange) {
        return BindingBuilder.bind(benchQueue).to(benchExchange).with(benchRoutingKey).noargs();
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }


}
