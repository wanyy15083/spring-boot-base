package com.base.consumer.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.base.consumer.controller")
public class DubboConsumerDemo {

    public static void main(String[] args) {

        SpringApplication.run(DubboConsumerDemo.class,args);

    }

}