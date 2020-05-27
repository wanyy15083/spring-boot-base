package com.base.rabbit.consumer;

import java.io.UnsupportedEncodingException;

public class HelloWorldHandler {

    public void handleMessage(byte[] text) throws UnsupportedEncodingException {
        System.out.println("Received: " + new String(text, "utf-8"));
    }

}