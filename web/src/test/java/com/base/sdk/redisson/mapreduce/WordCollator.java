package com.base.sdk.redisson.mapreduce;

import org.redisson.api.mapreduce.*;

import java.util.*;

/**
 * Created by bj-s2-w1631 on 18-9-19.
 */
public class WordCollator implements RCollator<String, Integer, Integer> {
    @Override
    public Integer collate(Map<String, Integer> resultMap) {
        int result = 0;
        for (Integer count : resultMap.values()) {
            result += count;
        }
        return result;
    }
}
