package com.base.api.test;

import java.util.*;

public class TestMain {
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 40; i++) {
            map.put(new Random().nextInt(20),i);
        }
    }
}
