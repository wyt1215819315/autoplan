package com.misec.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LocalDateTimeUtils.
 *
 * @author Yellow
 * @since 2021-01-13 17:31
 */
public class LocalDateTimeUtils {

    public final static DateTimeFormatter FORMATTER_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter FORMATTER_DATE_HH_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final static Map<Integer, String> DATE_SUFFIX_MAP = new HashMap<Integer, String>() {
        {
            put(10, " 00:00:00");
            put(16, ":00");
            put(19, "");
        }
    };

    /**
     * 解析日期字符串并转换为Date对象（默认时区为GMT+8/北京时区）.
     *
     * @param dateStr 日期字符串（符合标准日期格式，支持的格式参考
     *                {@link LocalDateTimeUtils#FORMATTER_DATE_TIME}）
     *                {@link LocalDateTimeUtils#FORMATTER_DATE}）
     *                {@link LocalDateTimeUtils#FORMATTER_DATE_HH_MM}）
     * @return Date对象
     */
    public static Date parse(String dateStr) {
        return LocalDateTimeUtils.parse(dateStr, 8);
    }

    /**
     * 解析日期字符串并转换为Date对象.
     *
     * @param dateStr         日期字符串
     * @param zoneOffsetHours 时区（GMT时区）
     * @return Date对象
     */
    private static Date parse(String dateStr, int zoneOffsetHours) {
        int length = dateStr.trim().length();
        // LocalDateTime解析日期字符串，字符串必须符合标准的日期字符串
        String suffix = DATE_SUFFIX_MAP.get(length);
        if (suffix == null) {
            throw new IllegalArgumentException();
        }
        // 格式化日期字符串
        dateStr = dateStr.trim() + suffix;
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, LocalDateTimeUtils.FORMATTER_DATE_TIME);
        return new Date(localDateTime.toInstant(ZoneOffset.ofHours(zoneOffsetHours)).toEpochMilli());
    }

    /**
     * 格式化Date对象为日期字符串，yyyy-MM-dd HH:mm:ss格式（默认时区为GMT+8/北京时区）.
     *
     * @param date Date日期对象
     * @return 格式化日期字符串
     */
    public static String formatDateTime(Date date) {
        return LocalDateTimeUtils.format(date, 8, FORMATTER_DATE_TIME);
    }

    /**
     * 格式化Date对象为日期字符串，yyyy-MM-dd格式（默认时区为GMT+8/北京时区）.
     *
     * @param date Date日期对象
     * @return 格式化日期字符串
     */
    public static String formatDate(Date date) {
        return LocalDateTimeUtils.format(date, 8, FORMATTER_DATE);
    }


    /**
     * 格式化Date对象为日期字符串（时区为GMT+0）.
     *
     * @param date      Date日期对象
     * @param formatter 日期格式化formatter
     * @return 格式化日期字符串
     */
    public static String formatToStandard(Date date, DateTimeFormatter formatter) {
        return LocalDateTimeUtils.format(date, 0, formatter);
    }

    /**
     * 格式化Date对象为日期字符串（默认时区为GMT+8/北京时区）.
     *
     * @param date      Date日期对象
     * @param formatter 日期格式化formatter
     * @return 格式化日期字符串
     */
    public static String format(Date date, DateTimeFormatter formatter) {
        return LocalDateTimeUtils.format(date, 8, formatter);
    }

    /**
     * 格式化Date对象为日期字符串.
     *
     * @param date            Date日期对象
     * @param zoneOffsetHours 时区（GMT时区）
     * @param formatter       日期格式化formatter
     * @return 格式化日期字符串
     */
    private static String format(Date date, int zoneOffsetHours, DateTimeFormatter formatter) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.ofOffset("GMT", ZoneOffset.ofHours(zoneOffsetHours)));
        return localDateTime.format(formatter);
    }

}
