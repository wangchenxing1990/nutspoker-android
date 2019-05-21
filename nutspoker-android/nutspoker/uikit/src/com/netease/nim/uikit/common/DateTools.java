package com.netease.nim.uikit.common;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.log.LogUtil;
public class DateTools {
    private final static String TAG = "DateTools";

    // 将时间戳转为字符串
    public static String getStrTime_ymd_hm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    public static String getStrTime_ymd_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    public static String getStrTime_ymd(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    public static String getStrTime_y(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;

    }

    public static String getStrTime_md(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    public static String getStrTime_hm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    public static String getStrTime_hms(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;

    }

    public static long getCurrentDate() {
        String re_time = null;
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        d = new Date(currentTime);
        long l = d.getTime();
        String str = String.valueOf(l);
        if (str.length() < 10) {
            re_time = str;
        } else {
            re_time = str.substring(0, 10);
        }
        LogUtil.i(TAG, "current date:" + re_time);
        return Long.valueOf(re_time);
    }

    public static String getOtherStrTime_ymd_hm(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    public static String getOtherStrTime_md_hm(long cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd  HH:mm");
        // 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getSection(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd  EEEE");
//		对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
//		yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param sjc_time    需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                <p/>如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String sjc_time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        String time = getStrTime_ymd_hms(sjc_time);
        pattern = "yyyy-MM-dd HH:mm:ss";
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(
                        thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today))
                        .getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd HH:mm");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
//						display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
                    } else {

                        if (dTime < tMin) {
//                            display = "Just";
							display = DemoCache.getContext().getString(R.string.time_just);
                        } else if (dTime < tHour) {
//                            display = (int) Math.ceil(dTime / tMin) + " minutes ago";
//							display = (int) Math.ceil(dTime / tMin) + "分钟前";
							display = DemoCache.getContext().getString(R.string.time_minutes_ago , (int) Math.ceil(dTime / tMin));
                        } else if (dTime < tDay && tDate.after(yesterday)) {
//                            display = (int) Math.ceil(dTime / tHour) + " hours ago";
//							display = (int) Math.ceil(dTime / tHour) + "小时前";
							display = DemoCache.getContext().getString(R.string.time_hour_ago , (int) Math.ceil(dTime / tHour));
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
//							display = "Yesterday ";
//							display = "Yesterday " + new SimpleDateFormat("HH:mm").format(tDate);
                            display = halfDf.format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    public static String formatTodayDisplayTime(String sjc_time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        String time = getStrTime_ymd_hms(sjc_time);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(
                        thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today))
                        .getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
                    } else {
                        if (dTime < tDay && tDate.after(yesterday)) {
                            display = "Today ";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "Yesterday ";
//							display = "Yesterday " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }


    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getRecordCreateTime(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
//		对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
//		yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getRecordTimeNodeDay(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd" + DemoCache.getContext().getString(R.string.day));
//		对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
//		yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /*
     * 将时间戳转为字符串 ，格式：yyyy.MM.dd  星期几
     */
    public static String getRecordTimeNodeMonth(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("MM" + DemoCache.getContext().getString(R.string.month));
//		对于创建SimpleDateFormat传入的参数：EEEE代表星期，如“星期四”；MMMM代表中文月份，如“十一月”；MM代表月份，如“11”；
//		yyyy代表年份，如“2010”；dd代表天，如“25”
        // 例如：cc_time=1291778220
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /**
     * 判断2天是否是同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isTheSameDay(long date1 , long date2) {
        if (date1 <= 0 || date2 <= 0) {
            return false;
        }
        Date d1 = new Date(date1 * 1000L);
        Date d2 = new Date(date2 * 1000L);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
                && (c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    public static boolean isTheSameMonth(long date1 , long date2) {
        if (date1 <= 0 || date2 <= 0) {
            return false;
        }
        Date d1 = new Date(date1 * 1000L);
        Date d2 = new Date(date2 * 1000L);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
                && (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH));
    }

    public static String getRecordTimeNodeDayDisplay(String sjc_time) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;
        String time = getStrTime_ymd_hms(sjc_time);
        String pattern = "yyyy-MM-dd HH:mm:ss";
        if (time != null) {
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(
                        thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today))
                        .getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    long dTime = today.getTime() - tDate.getTime();
//                    if (tDate.before(thisYear)) {
//                        display = new SimpleDateFormat("yyyy-MM-dd").format(tDate);
//                    } else {
                        if (dTime < tDay && tDate.after(yesterday)) {
                            display = DemoCache.getContext().getString(R.string.today);
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = DemoCache.getContext().getString(R.string.yesterday);
                        } else {
                            SimpleDateFormat halfDf = new SimpleDateFormat("dd" + DemoCache.getContext().getString(R.string.day));
                            display = halfDf.format(tDate);
                        }
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }

    public static long getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static long getMonthDay(int month , int day) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.add(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);// 设置为1号,当前日期既为本月第一天
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static String getDailyTime_ymd(long cc_time) {
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        re_StrTime = sdf.format(cc_time > Integer.MAX_VALUE ? cc_time : cc_time * 1000L);
        return re_StrTime;
    }

    public static String getDailyTime_md(long cc_time){
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;
    }

    public static String getDailyTime_ym(long cc_time){
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;
    }

    public static String getDailyTime_d(long cc_time) {
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("d");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;
    }

    public static String getDailyTime_m(long cc_time) {
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;
    }

    public static String getMatchBeginDate(long cc_time) {
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日" + "\n" + "HH:mm");
        re_StrTime = sdf.format(cc_time * 1000L);
        return re_StrTime;
    }

    public static String getThisYear(long month){
        String re_StrTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年");
        re_StrTime = sdf.format(System.currentTimeMillis());
        return String.format("%s%02d月",re_StrTime,month);
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
}

