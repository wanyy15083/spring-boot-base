package com.base.sdk.redisson.mapreduce;

import org.redisson.api.mapreduce.*;

/**
 * Created by bj-s2-w1631 on 18-9-19.
 */
public class WordMapper implements RMapper<String, String, String, Integer> {
    @Override
    public void map(String key, String value, RCollector<String, Integer> collector) {
        String[] words = value.split("[^a-zA-Z]");
        for (String word : words) {
            collector.emit(word, 1);
        }
    }
}
