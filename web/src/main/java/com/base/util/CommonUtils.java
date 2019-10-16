package com.base.util;

import java.util.UUID;

/**
 * Created by songyigui on 19-10-12.
 */
public class CommonUtils {
    public static int getCurrentTimeStamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
