package com.base.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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


    public static String format(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String format(int timeStamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timeStamp*1000L));
    }

    public static String format(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime);
    }

    public static LocalDateTime parse(String timeStr) {
        return LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static int parseTimeStamp(String timeStr) {
        return getTimeStamp(LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public static LocalDateTime getLocalDateTime(int timeStamp) {
        Instant instant = Instant.ofEpochSecond(timeStamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static int getTimeStamp(LocalDateTime dateTime) {
        return (int) dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static int getTimeStamp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return (int) localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    public static LocalDateTime getDatStartTime(LocalDateTime dateTime) {
        return dateTime.withHour(0).withMinute(0).withSecond(0);
    }

    public static LocalDateTime getDatEndTime(LocalDateTime dateStartTime) {
        return dateStartTime.withHour(23).withMinute(59).withSecond(59);
    }

    public static boolean isNotBlank(String str) {
        return (!isBlank(str));
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
