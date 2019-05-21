package com.netease.nim.uikit.common.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.netease.nim.uikit.common.DemoCache;

import java.util.Locale;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.CountryCodeConstants;

/**
 * 基础工具类
 */
public class BaseTools {

    /**
     * 获取当前版本
     */
    public static String getAppVersionName(Context context) {
        String verionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            verionName = pi.versionName;
            if (verionName == null || verionName.length() <= 0) {
                return "";
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.i("version", "current version:" + verionName);
        return verionName;
    }

    /**
     * 获取当前屏幕分辨率 - 宽度
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        return mScreenWidth;
    }

    /**
     * 获取当前屏幕分辨率 - 高度
     * @param context
     * @return
     */
    public static int getWindowHeigh(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        return mScreenHeigh;
    }

    /**
     * dip转为px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转为dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue
     * @param fontScale（DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(float pxValue, float fontScale) {
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 判断用户名是否符合规则
     * @param username
     * @return
     */
    public static boolean checkUserName(String username) {
        return username.matches("[0-9A-Za-z_]*");
    }

    /**
     * 显示内存
     * @param context
     */
    public static void displayMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo info = new MemoryInfo();
        activityManager.getMemoryInfo(info);
        LogUtil.i("memory", "系统剩余内存:" + (info.availMem >> 10) + "k");
        LogUtil.i("memory", "系统是否处于低内存运行：" + info.lowMemory);
        LogUtil.i("memory", "当系统剩余内存低于" + info.threshold + "时就看成低内存运行");
    }

    /**
     * 验证手机格式,根据区号
     */
    public static boolean isMobileNO(String countryCode, String mobiles) {
        if (CountryCodeConstants.CODE_CHINA.equals(countryCode) && !isMobileNO(mobiles)) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        虚拟网：17
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static boolean isNO(String mobiles) {
        String telRegex = "^[0-9]*$";//
        if (TextUtils.isEmpty(mobiles))
            return false;
        else
            return mobiles.matches(telRegex);
    }

    public static String getLanguage() {
        Locale locale = DemoCache.getContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String languageCountry = language + "-" + country;
        LogUtil.i("getLanguage", languageCountry);
        return languageCountry;
    }

//    /**
//     * 判断是否是俱乐部ID：全是数字
//     */
//    public static boolean isClubId(String mobiles) {
//        String telRegex = "[0-9]*";
//        return mobiles.matches(telRegex);
//    }
}
