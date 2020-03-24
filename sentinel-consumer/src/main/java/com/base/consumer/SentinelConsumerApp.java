package com.base.consumer;

import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

/**
 * Created by bj-s2-w1631 on 18-11-15.
 */
@SpringBootApplication
@DubboComponentScan("com.base.consumer")
public class SentinelConsumerApp {

    private static final String RES_KEY = "com.base.api.FooService:sayHello(java.lang.String)";

    public static void main(String[] args) {

        SpringApplicationBuilder consumerBuilder = new SpringApplicationBuilder();
        ApplicationContext applicationContext = consumerBuilder
                .web(WebApplicationType.NONE).sources(SentinelConsumerApp.class)
                .run(args);

        FooServiceConsumer service = applicationContext.getBean(FooServiceConsumer.class);

        for (int i = 0; i < 100; i++) {
            try {
                String message = service.sayHello("Tom");
                System.out.println((i + 1) + " -> Success: " + message);
            } catch (SentinelRpcException ex) {
                System.out.println("Blocked");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
