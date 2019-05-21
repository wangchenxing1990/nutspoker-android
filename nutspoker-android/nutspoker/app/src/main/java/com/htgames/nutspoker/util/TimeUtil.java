package com.htgames.nutspoker.util;

import com.netease.nim.uikit.constants.VipConstants;

import java.util.Calendar;

/**
 * Created by glp on 2016/8/7.
 */
public class TimeUtil {

    static long mTodayTimeStamp = 0;
    static long mBefore7Stamp = 0;
    static long mBefore30Stamp = 0;
    static long mBefore90Stamp = 0;

    static void checkToday(){
        if(mTodayTimeStamp == 0){
            Calendar c = Calendar.getInstance();
            c.set(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DATE),0,0,0);//今天的00:00:00
            mTodayTimeStamp = c.getTimeInMillis()/1000;

            mBefore7Stamp = mTodayTimeStamp - VipConstants.LEVEL_NOT_RECORD_HANDCOUNT*24*60*60;//多送一天
            mBefore30Stamp = mTodayTimeStamp - VipConstants.LEVEL_WHITE_RECORD_HANDCOUNT*24*60*60;
            mBefore90Stamp = mTodayTimeStamp - VipConstants.LEVEL_BLACK_RECORD_HANDCOUNT*24*60*60;
        }
    }

    /**
     * 7天内
     * @param time
     * @return
     */
    public static boolean IsInWeek(long time){
        checkToday();
        return time > mBefore7Stamp;
    }

    /**
     * 30天内
     * @param time
     * @return
     */
    public static boolean IsInThirtyDay(long time){
        checkToday();
        return time > mBefore30Stamp;
    }

    /**
     * 90天内
     * @param time
     * @return
     */
    public static boolean IsInNinetyDay(long time){
        checkToday();
        return time > mBefore90Stamp;
    }

    public static boolean IsInLimitDay(long time,int vip){
        checkToday();
        switch (vip){
            case 2:
                return IsInNinetyDay(time);
            case 1:
                return IsInThirtyDay(time);
            case 0:
            case -1:
            default:
                return IsInWeek(time);
        }
    }
}
