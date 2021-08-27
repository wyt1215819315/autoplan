package com.oldwu.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.util.Random;

public class NumberUtil {

    private static final String[] NUMBER1 = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] NUMBER2 = {"零", "拾", "佰", "仟", "万", "亿"};

    public static String getNetFileSizeDescription(long size) {
        StringBuilder bytes = new StringBuilder();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * 生成随机字符串
     *
     * @param length
     * @return
     */
    public static String generateRandomStrs(int length) {
        String string = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int i = 0;
        String randomStrs = "";
        while (i < length) {
            Random random = new Random();
            double e = random.nextDouble() * string.length();
            e = Math.floor(e);
            randomStrs = randomStrs + string.toCharArray()[(int) e];
            i++;
        }
        return randomStrs;
    }

    /**
     * 小写转大写
     *
     * @param str
     * @return
     */
    public static String smallXieTOBigXie(String str) {
        return StringUtils.upperCase(str);
    }

    /**
     * 将秒数格式化成人看的文字
     *
     * @param second
     * @return
     */
    public static String autoFormatTime(int second) {
        StringBuilder str = new StringBuilder();
        if (second <= 60) {
            //小于一分钟
            return second + "秒";
        }
        if (second <= 3600) {
            //小于一小时
            int minute = second / 60;
            int s = second - minute * 60;
            str.append(minute).append("分");
            if (s != 0) {
                str.append(s).append("秒");
            }
            return str.toString();
        }
        if (second <= 86400) {
            //小于一天
            int hours = second / 3600;
            int minutes = (second - hours * 3600) / 60;
            int s = second - hours * 3600 - minutes * 60;
            str.append(hours).append("小时");
            if (minutes != 0) {
                str.append(minutes).append("分");
            }
            if (s != 0) {
                str.append(s).append("秒");
            }
            return str.toString();
        }
        if (second > 86400) {
            int days = second / 86400;
            int hours = (second - days * 86400) / 3600;
            int minutes = (second - days * 86400 - hours * 3600) / 60;
            int s = second - days * 86400 - hours * 3600 - minutes * 60;
            str.append(days).append("天");
            if (hours != 0) {
                str.append(hours).append("小时");
            }
            if (minutes != 0) {
                str.append(minutes).append("分");
            }
            if (s != 0) {
                str.append(s).append("秒");
            }
            return str.toString();
        }
        return second + "秒";
    }

    /**
     * @param num
     * @return
     * @Author:lulei
     * @Description:将数字转化为大写
     */
    public static String getNumberStr(int num) {
        if (num < 0) {
            return "";
        }
        if (num == 0) {
            return NUMBER1[0];
        }
        int split = 10000;
        int y = num / (split * split);
        int w = (num / split) % split;
        int g = num % split;
        StringBuffer sb = new StringBuffer();
        //亿
        if (y > 0) {
            sb.append(getNumberStr1000(y));
            sb.append(NUMBER2[5]);
        }
        //万
        if (w > 999) {
            sb.append(getNumberStr1000(w));
            sb.append(NUMBER2[4]);
        } else {
            if (w > 0) {
                if (y != 0) {
                    sb.append(NUMBER2[0]);
                }
                sb.append(getNumberStr1000(w));
                sb.append(NUMBER2[4]);
            }
        }
        //万以下
        if (g > 0) {
            if (w != 0) {
                if (g > 999) {
                    sb.append(getNumberStr1000(g));
                } else {
                    sb.append(NUMBER2[0]);
                    sb.append(getNumberStr1000(g));
                }

            } else {
                if (y != 0) {
                    sb.append(NUMBER2[0]);
                }
                sb.append(getNumberStr1000(g));
            }
        }
        return sb.toString();
    }

    /**
     * @param num
     * @return
     * @Author:lulei
     * @Description:对万以下的数字进行大小写转化
     */
    private static String getNumberStr1000(int num) {
        if (num > 9999 || num < 0) {
            return "";
        }
        int q = num / 1000;
        int b = (num / 100) % 10;
        int s = (num / 10) % 10;
        int g = num % 10;
        StringBuffer sb = new StringBuffer();
        //千
        if (q > 0) {
            sb.append(NUMBER1[q]);
            sb.append(NUMBER2[3]);
        }
        //百
        if (b > 0) {
            sb.append(NUMBER1[b]);
            sb.append(NUMBER2[2]);
        } else {
            if (q != 0) {
                sb.append(NUMBER2[0]);
            }
        }
        //十
        if (s > 0) {
            sb.append(NUMBER1[s]);
            sb.append(NUMBER2[1]);
        } else {
            if (b != 0) {
                sb.append(NUMBER2[0]);
            }
        }
        //个
        if (g > 0) {
            sb.append(NUMBER1[g]);
        }
        if (sb.toString().equals("壹拾")) {
            return "拾";
        }
        return sb.toString();
    }
}
