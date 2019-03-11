/*
 * Copyright 1999-2012 Alibaba Group.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.util;

import com.base.consumer.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TestConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TestConsumer.class);

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/test-consumer.xml");
        context.start();
        final TestService testService = (TestService) context.getBean("testService");
//        List<Person> person = testService.getPerson("8V");
//        logger.info("person: {}, thread: {}", person, Thread.currentThread().getName());

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 100; i++) {
            final int tmp = i;
            executor.execute(new Runnable() {
                @Override
                public void run() {
//                    List<Person> person = testService.getPerson("8V");
                    String result = null;
                    try {
                        Thread.sleep(3000);
                        result = testService.sayHello();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    logger.info("result: {}, thread: {}", result, Thread.currentThread().getName());
                }
            });
        }

        System.in.read();
    }
}
