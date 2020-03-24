package com.base.sdk.util;

import java.util.*;

public class Main {
    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        String input = in.next();





        String input = "1,3,9,8,5,6,1";
        String[] split = input.split(",");
        if (split.length==0) {
            throw new IllegalArgumentException("输入有误!");
        }
        int[] pArr = new int[split.length];
        Integer[] qArr = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            int priority = Integer.parseInt(split[i]);
            pArr[i] = priority;
            qArr[i] = priority;
        }

        Arrays.sort(qArr, Comparator.reverseOrder());

        System.out.println(Arrays.toString(qArr));

        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < qArr.length; j++) {
            for (int k = 0; k < pArr.length; k++) {
                if (qArr[j] == pArr[k]) {
                    sb.append(k).append(",");
                    pArr[k] =0;
                    break;
                }
            }

        }
        System.out.println(sb.substring(0, sb.length()-1).toString());
    }
}
