package com.base.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @SentinelResource("resource")
    public String hello() {
        return "Hello";
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test1() {
        return "Hello test";
    }

    @RequestMapping(value = "/template", method = RequestMethod.GET)
    public String client() {
        return restTemplate.getForObject("http://www.taobao.com/test", String.class);
    }

}