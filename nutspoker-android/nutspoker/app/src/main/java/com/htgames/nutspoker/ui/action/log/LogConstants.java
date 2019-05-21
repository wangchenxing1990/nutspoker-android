package com.htgames.nutspoker.ui.action.log;

import android.content.Context;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.hotupdate.preference.HotUpdatePreferences;
import com.netease.nim.uikit.common.util.BaseTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class LogConstants {
    private final static String KEY_PF = "PF";//平台类型 1:PC 2:iOS 3:Android
    private final static String KEY_OS = "OS";//不同平台不同(android哪个版本)
    private final static String KEY_DEVICE = "DEVICE";//设备型号
    private final static String KEY_PID = "PID";//项目ID  1:新平台 2:德扑圈 3:捕鱼
    private final static String KEY_UID = "UID";//UID:用户ID
    private final static String KEY_RID = "RID";//RID:房间ID，可空
    private final static String KEY_MSG = "MSG";//MSG:具体的日志
    private final static String APPVER = "APPVER";//APP版本号
    private final static String GVER = "GVER";//游戏版本
    private final static String DNAME = "DNAME";//设备名称

    private final static String PF_ANDROID = "3";
    private final static String PID_TEXAS = "2";

    public static String getLogJsonStr(Context context ,String msg) {
        android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int currentapiVersion=android.os.Build.VERSION.SDK_INT;
        String device = android.os.Build.MODEL;//获取手机型号
        String systemVersion = android.os.Build.VERSION.RELEASE;//系统版本号
        String os = android.os.Build.VERSION.RELEASE;
        String deviceName = "";
        LogUtil.i("log" , "device :" + device + " ; systemVersion :" + systemVersion
                + "currentapiVersion :" + currentapiVersion);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_PF, PF_ANDROID);
            jsonObject.put(KEY_OS, os);
            jsonObject.put(KEY_DEVICE, device);
            jsonObject.put(KEY_PID, PID_TEXAS);
            jsonObject.put(KEY_UID, UserPreferences.getInstance(DemoCache.getContext()).getUserId());
            jsonObject.put(KEY_MSG, msg);
            jsonObject.put(APPVER, BaseTools.getAppVersionName(context));
            jsonObject.put(GVER, HotUpdatePreferences.getInstance(context).getGameVersion());
            jsonObject.put(DNAME, deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
