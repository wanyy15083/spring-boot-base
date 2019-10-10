package com.base.provider.bootstrap;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DubboProviderDemo {

    public static void main(String[] args) {

        new SpringApplicationBuilder(DubboProviderDemo.class)
                .web(WebApplicationType.NONE)
                .run(args);

    }

}