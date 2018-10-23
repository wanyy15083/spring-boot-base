package com.base.sdk.util;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

public class UniqueKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getName());
        sb.append(method.getName());
        for (Object param : params) {
            sb.append(param.toString());
        }
        return sb.toString();
    }
}
