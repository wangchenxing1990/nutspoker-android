package com.htgames.nutspoker.tool;

import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.YearMonthEy;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 20150726 on 2015/11/17.
 */
public class DateCalendarTools {

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     */
    public static long getTodayDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long currentTime =  System.currentTimeMillis();
        Date nowDate = new Date(currentTime);
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTime(nowDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime() / 1000L;
    }

    public static int getThisMonth(){
        SimpleDateFormat formatter = new SimpleDateFormat("M");
        String month = formatter.format(System.currentTimeMillis());
        return Integer.parseInt(month);
    }

    public static int getThisYear(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String year = formatter.format(System.currentTimeMillis());
        return Integer.parseInt(year);
    }

    public static String formatTimestampToStr(long timeStamp, String format) {
        if (!TextUtils.isEmpty(format) && timeStamp >0 ) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINESE);
                synchronized (dateFormat) {
                    // SimpleDateFormat is not thread safe
                    return dateFormat.format(timeStamp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }
    /**
     * 把指定的时间转换成YMD格式
     * @param time
     * @return
     */
    public static long getDateYMD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date(time * 1000L);
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.setTime(nowDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime().getTime() / 1000L;
    }

    /**
     * 得到二个日期间的间隔天数
     * @param startDay 开始时间
     * @param endDay 结束时间（今天）
     * @return
     */
    public static int getTwoDayInterval(long startDay, long endDay) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        int day = 0;
        try {
            day = (int)(endDay * 1000L -  startDay * 1000L) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0;
        }
        return day;
    }

    /**
     * 得到指定月后的日期
     */
    public static String getAfterMonth(int month) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse("2009-11-04");//初始日期
        } catch (Exception e) {
        }
        c.setTime(date);//设置日历时间
        c.add(Calendar.MONTH, month);//在日历的月份上增加6个月
        String strDate = sdf.format(c.getTime());//的到你想要得6个月后的日期
        return strDate;
    }

    /**
     * 得到指定日期的前一天
     */
    public static String getAfterDay(int currentDate) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(String.valueOf(currentDate));//初始日期
        } catch (Exception e) {

        }
        c.setTime(date);//设置日历时间
//        c.add(Calendar.MONTH, month);//在日历的月份上增加6个月
        String strDate = sdf.format(c.getTime());//的到你想要得6个月后的日期
        return strDate;
    }

    /**
     * 获取当前时间的前一天
     * @return
     */
    public static long getBeforeDay(int day){
        long currentTime =  System.currentTimeMillis();
        Date nowDate = new Date(currentTime);
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(nowDate);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, day);  //设置为前一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        dBefore = calendar.getTime();   //得到前一天的时间
        return dBefore.getTime() / 1000;
    }

    public static YearMonthEy getBeforeMonth(int day){

        YearMonthEy yearMonthEy = new YearMonthEy();

        int year = getThisYear();
        int curMonth = getThisMonth();
        int pre = curMonth + day;
        while (pre <= 0) {
            pre += 12;
            year --;
        }

        while( pre >12) {
            pre -= 12;
            year ++;
        }

        yearMonthEy.year = year;
        yearMonthEy.month = pre;

        return yearMonthEy;
    }

    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 两个时间之间的天数
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    //获取当天时间
    public String getNowTime(String dateformat) {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);
        //可以方便地修改日期格式
        String nwoStr = dateFormat.format(now);
        return nwoStr;
    }

    // 获得当前日期与本周日相差的天数
    private int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; //因为按中国礼拜一 作为第一天所以这里减1
        if (dayOfWeek == 1) {
            return 0;
        } else {
            return 1 - dayOfWeek;
        }
    }

    public static Calendar getDayCalendar(long currentTime) {
        Date nowDate = new Date(currentTime * 1000L);
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(nowDate);//把当前时间赋给日历
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public static int getDayOfWeek(int day) {
        int dayRes = R.string.sunday;
        switch (day){
            case Calendar.SUNDAY:
                dayRes = R.string.sunday;
                break;
            case Calendar.MONDAY:
                dayRes = R.string.monday;
                break;
            case Calendar.TUESDAY:
                dayRes = R.string.tuesday;
                break;
            case Calendar.WEDNESDAY:
                dayRes = R.string.wendesday;
                break;
            case Calendar.THURSDAY:
                dayRes = R.string.thursady;
                break;
            case Calendar.FRIDAY:
                dayRes = R.string.friday;
                break;
            case Calendar.SATURDAY:
                dayRes = R.string.saturday;
                break;
        }
        return dayRes;
    }
}
