package com.base.rabbit.config;

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

    @Value("${rabbit.routing.key.test}")
    private String testRoutingKey;

    @Value("${rabbit.queue.test}")
    private String testQueue;

    @Bean
    public Queue testQueue() {
        return new Queue(testQueue, true, false, false);
    }

    @Bean
    public DirectExchange exTestExchange() {
        return new DirectExchange(testExchange, true, false);
    }

    @Bean
    public Binding bindingStatus(Queue testQueue, DirectExchange exTestExchange) {
        return BindingBuilder.bind(testQueue).to(exTestExchange).with(testRoutingKey);
    }


}
