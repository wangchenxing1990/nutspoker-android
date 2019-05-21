package com.netease.nim.uikit.common;

import android.content.Context;
import android.location.Location;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;

/**
 * APP临时缓存
 */
public class DemoCache {
    private final static String TAG = "DemoCache";
    private static Context context;
    private static String account;
    private static StatusBarNotificationConfig notificationConfig;
    private static int checkTimeValue = 0;//校验时间(本地和服务器的误差)
    private static Location location;//当前区域
//    private static int collectCount = 0;//收藏的数量

    public static void clear() {
        account = null;
    }

    public static String getAccount() {
        if (StringUtil.isSpace(account)) {
            account = UserPreferences.getInstance(CacheConstant.sAppContext).getUserId();
        }
        return account;
    }

    public static void setAccount(String account) {
        DemoCache.account = account;
        NimUIKit.setAccount(account);
    }

    public static void setNotificationConfig(StatusBarNotificationConfig notificationConfig) {
        DemoCache.notificationConfig = notificationConfig;
    }

    public static StatusBarNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        DemoCache.context = context.getApplicationContext();
    }

    /**
     * 获取服务端校验时间的误差，比服务的快了多少（本地时间 - 服务器时间）
     * @return
     */
    public static int getCheckTimeValue() {
        return checkTimeValue;
    }

    public static void setCheckTimeValue(long netTime) {
        if (netTime <= 1400000000 || netTime >= 2000000000) {//2014/05/14 ---  2033/05/18
            return;
        }
        int time = (int) (System.currentTimeMillis() / 1000L - netTime);
        LogUtil.i("checktime", "本地时间比服务器快了 :" + time);
        DemoCache.checkTimeValue = time;
    }

    public static long getCurrentServerSecondTime() {
        return System.currentTimeMillis() / 1000L - checkTimeValue;
    }

    public static void setLocation(Location lo) {
        location = lo;
    }

    public static Location getLocation() {
        return location;
    }

//    public static int getCollectCount() {
//        return collectCount;
//    }
//
//    public static void setCollectCount(int count) {
//        Log.d(TAG , "收藏了多少手牌 :" + count);
//        DemoCache.collectCount = count;
//    }
}
