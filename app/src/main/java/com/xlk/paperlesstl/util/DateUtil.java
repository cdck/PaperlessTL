package com.xlk.paperlesstl.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Created by xlk on 2020/11/28.
 * @desc
 */
public class DateUtil {
    /**
     * 将当前获取的时间戳转换成详细日期时间
     *
     * @return 返回格式 2021年3月23日17时32分11秒
     */
    public static String nowDate() {
        Date tTime = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        return format.format(tTime);
    }

    /**
     * @param time 单位 毫秒
     *             时区设置：SimpleDateFormat对象.setTimeZone(TimeZone.getTimeZone("GTM"));
     */
    public static String[] getGTMDate(long time) {
        Date tTime = new Date(time);

        SimpleDateFormat tim = new SimpleDateFormat("HH:mm");
        tim.setTimeZone(TimeZone.getTimeZone("GTM"));
        String timt = tim.format(tTime);

        //只有一个E 则解析出来是 周几，4个E则是星期几
        SimpleDateFormat day = new SimpleDateFormat("MM月dd日");
        day.setTimeZone(TimeZone.getTimeZone("GTM"));
        String dayt = day.format(tTime);

        //只有一个E 则解析出来是 周几，4个E则是星期几
        SimpleDateFormat week = new SimpleDateFormat("EEEE");
        week.setTimeZone(TimeZone.getTimeZone("GTM"));
        String weekt = week.format(tTime);

        return new String[]{timt, dayt, weekt};
    }

    /**
     * 转成时分秒 00:00:00
     *
     * @param ms 单位：毫秒
     */
    public static String convertTime(long ms) {
        String ret = "";
        Date date = new Date(ms);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GTM"));
        ret = timeFormat.format(date);
        return ret;
    }


    /**
     * 转换成 2020/07/22 09:40
     *
     * @param seconds 单位:秒
     */
    public static String secondFormatDateTime(long seconds) {
        return millisecondFormatDateTime(seconds * 1000);
    }

    /**
     * 转换成 2020/07/22 09:40
     *
     * @param ms 单位:毫秒
     */
    public static String millisecondFormatDateTime(long ms) {
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return format.format(date);
    }

    /**
     * 转换成 2020-07-22 09:40:00
     *
     * @param ms 单位:毫秒
     */
    public static String millisecondFormatDetailedTime(long ms) {
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    /**
     * @param seconds 秒
     * @return
     */
    public static String getHHss(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.format(date);
    }

    /**
     * @param seconds 秒
     */
    public static String countdown(long seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm分ss秒");
        return timeFormat.format(date);
    }
}
