package com.base.consumer1;

import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallbackRegistry;
import com.alibaba.csp.sentinel.concurrent.NamedThreadFactory;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.SentinelRpcException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import com.alibaba.dubbo.rpc.RpcResult;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bj-s2-w1631 on 18-11-15.
 */
@SpringBootApplication
@DubboComponentScan("com.base.consumer1")
public class SentinelConsumerApp {

    private static final String RES_KEY = "org.base.api.FooService:hello(java.lang.String)";

    private static final ExecutorService pool = Executors.newFixedThreadPool(10,
            new NamedThreadFactory("dubbo-consumer-pool"));

    public static void main(String[] args) {

        initFlowRule();

        SpringApplicationBuilder consumerBuilder = new SpringApplicationBuilder();
        ApplicationContext applicationContext = consumerBuilder
                .web(WebApplicationType.NONE).sources(SentinelConsumerApp.class)
                .run(args);

        FooServiceConsumer service = applicationContext.getBean(FooServiceConsumer.class);

        for (int i = 0; i < 10; i++) {
            pool.submit(() -> {
                try {
                    String message = service.sayHello("Tom");
                    System.out.println("Success: " + message);
                } catch (SentinelRpcException ex) {
                    System.out.println("Blocked");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            pool.submit(() -> System.out.println("Another: " + service.doAnother()));
        }

    }


    private static void initFlowRule() {
        FlowRule flowRule = new FlowRule();
        flowRule.setResource(RES_KEY);
        flowRule.setCount(5);
        flowRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        flowRule.setLimitApp("default");
        FlowRuleManager.loadRules(Collections.singletonList(flowRule));
    }

    private static void registerFallback() {
        // Register fallback handler for consumer.
        // If you only want to handle degrading, you need to
        // check the type of BlockException.
        DubboFallbackRegistry.setConsumerFallback((a, b, ex) ->
                new RpcResult("Error: " + ex.getClass().getTypeName()));
    }
}
