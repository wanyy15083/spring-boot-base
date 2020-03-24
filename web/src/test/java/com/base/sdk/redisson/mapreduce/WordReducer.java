package com.base.sdk.redisson.mapreduce;

import org.redisson.api.mapreduce.*;

import java.util.*;

/**
 * Created by bj-s2-w1631 on 18-9-19.
 */
public class WordReducer implements RReducer<String, Integer> {
    @Override
    public Integer reduce(String reducedKey, Iterator<Integer> iter) {
        int sum = 0;
        while (iter.hasNext()) {
            sum += iter.next();
        }
        return sum;
    }
}
