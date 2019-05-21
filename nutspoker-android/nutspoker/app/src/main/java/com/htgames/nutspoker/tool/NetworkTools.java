package com.htgames.nutspoker.tool;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;
import com.netease.nim.uikit.common.util.log.LogUtil;
public class NetworkTools {
    public static final String TAG = "NetworkTools";
    /**
     * 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络
     */
    public final static int CMNET = 3;
    public final static int CMWAP = 2;
    public final static int WIFI = 1;
    public final static int NONEWTWORK = -1;

    public static int getAPNType(Context context) {
        int netType = NONEWTWORK;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            LogUtil.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is " + networkInfo.getExtraInfo());
            if (networkInfo.getExtraInfo() != null && networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = CMNET;
            } else {
                netType = CMWAP;
            }
        }
        if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;
        }
        return netType;
    }

    public static String getAPNType_String(int type) {
        String type_status = "网络异常";
        switch (type) {
            case NONEWTWORK:
                type_status = "没有网络 ";
                break;
            case WIFI:
                type_status = "WIFI";
                break;
            case CMWAP:
                type_status = "CMWAP";
                break;
            case CMNET:
                type_status = "CMNET";
                break;
            default:
                break;
        }
        return type_status;
    }



    /**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true 在运行, false 不在运行
     */

    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i=0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        LogUtil.i(TAG,"service is running?==" + isRunning);
        return isRunning;
    }


    /**
     * 判断是否是WIFI上网
     */
    public static boolean isConnectWifi(Context context){
        int type = getAPNType(context);
        if(type == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    public static boolean isNetConnect(Context context){
        if(NONEWTWORK == getAPNType(context)){
            return false;
        }
        return true;
    }
}
