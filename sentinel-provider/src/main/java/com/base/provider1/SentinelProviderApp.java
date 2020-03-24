package com.base.provider1;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by bj-s2-w1631 on 18-11-15.
 */
@SpringBootApplication
@DubboComponentScan(basePackages = "com.base.provider1")
public class SentinelProviderApp {

    public static void main(String[] args) {

        SpringApplicationBuilder providerBuilder = new SpringApplicationBuilder();
        providerBuilder.web(WebApplicationType.NONE)
                .sources(SentinelProviderApp.class).run(args);

        System.out.println("Service provider is ready");
    }


}
