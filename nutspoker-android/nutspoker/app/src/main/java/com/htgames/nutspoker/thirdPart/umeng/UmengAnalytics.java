package com.htgames.nutspoker.thirdPart.umeng;

import android.content.Context;
import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 友盟统计工具类
 */
public class UmengAnalytics {
    public final static String TAG = "UmengAnalytics";

    /**
     * 设置友盟的APPKEY，如不想在manifest里配置友盟的appkey，可在Activity中配置：只需在程序启动时的Activity中调用此接口：
     * @param appkey
     */
//    public static void setAppkey(Context context, String appkey) {
//        AnalyticsConfig.setAppkey(context, appkey);
//    }

    /**
     * 设置推广渠道名称，如不想在manifest里配置友盟的channel，可在Activity中配置：只需在程序启动时的Activity中调用此接口
     * 渠道命名规范：http://dev.umeng.com/analytics/android-doc/integration#2_1_2
     *
     * @param channel
     */
//    public static void setChannel(String channel) {
//        AnalyticsConfig.setChannel(channel);
//    }

    public static void setDebugMode(boolean enable) {
        MobclickAgent.setDebugMode(enable);
    }

    /**
     * 当应用在后台运行超过30秒（默认）再回到前端，将被认为是两个独立的session(启动)，例如用户回到home，或进入其他程序，经过一段时间后再返回之前的应用。可通过接口
     *
     * @param interval
     */
    public static void setSessionContinueMillis(long interval) {
        MobclickAgent.setSessionContinueMillis(interval);
    }

    /**
     * session的统计:onResume
     * 用来统计应用时长的
     */
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    /**
     * session的统计:onPause
     * 用来统计应用时长的
     */
    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    /**
     * 用来统计页面跳转的:在仅有Activity的应用中，SDK 自动帮助开发者调用了 2  中的方法，并把Activity 类名作为页面名称统计。
     * 但是在包含fragment的程序中我们希望统计更详细的页面，所以需要自己调用方法做更详细的统计。
     */
    public static void onPageStart(String page) {
        MobclickAgent.onPageStart(page);
    }

    public static void onPageEnd(String page) {
        MobclickAgent.onPageEnd(page);
    }

    /**
     * 在包含fragment的程序中我们希望统计更详细的页面，所以需要自己调用方法做更详细的统计。
     * 首先，需要在程序入口处，调用 MobclickAgent.openActivityDurationTrack(false)  禁止默认的页面统计方式，这样将不会再自动统计Activity。
     *
     * @param open
     */
    public static void openActivityDurationTrack(boolean open) {
        MobclickAgent.openActivityDurationTrack(open);
    }

    /**
     * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用MobclickAgent.onKillProcess(Context context)方法，用来保存统计数据。
     *
     * @param context
     */
    public static void setSessionContinueMillis(Context context) {
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 友盟在统计用户时以设备为标准,如果需要统计应用自身的账号使用以下方法
     *
     * @param id 用户账号ID，长度小于64字节
     */
    public static void onProfileSignIn(String id) {
        MobclickAgent.onProfileSignIn(id);
    }

    /**
     * 友盟在统计用户时以设备为标准,如果需要统计应用自身的账号使用以下方法
     *
     * @param provider Provider：账号来源。如果用户通过第三方账号登陆，可以调用此接口进行统计。支持自定义，不能以下划线"_"开头，使用大写字母和数字标识，长度小于32 字节; 如果是上市公司，建议使用股票代码。
     * @param id       用户账号ID，长度小于64字节
     */
    public static void onProfileSignIn(String provider, String id) {
        MobclickAgent.onProfileSignIn(provider, id);
    }

    public static void onProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    /**
     * 解决android6.0设备信息采集的问题,android6.0中采集mac方式变更，新增接口
     * 该接口默认参数是true，即采集mac地址，但如果开发者需要在googleplay发布，考虑到审核风险，可以调用该接口，参数设置为false就不会采集mac地址。
     *
     * @param enable
     */
    public static void setCheckDevice(boolean enable) {
        MobclickAgent.setCheckDevice(enable);
    }

    /**
     * 如果开发者调用Process.kill或者System.exit之类的方法杀死进程，请务必在此之前调用,用来保存统计数据
     *
     * @param context
     */
    public static void onKillProcess(Context context) {
        MobclickAgent.onKillProcess(context);
    }

    /**
     * 设置是否对日志信息进行加密, 默认false(不加密).
     * 如果enable为true，SDK会对日志进行加密。加密模式可以有效防止网络攻击，提高数据安全性。
     * 如果enable为false，SDK将按照非加密的方式来传输日志。
     * 如果您没有设置加密模式，SDK的加密模式为false（不加密）
     */
//    public static void enableEncrypt(boolean enable) {
//        AnalyticsConfig.enableEncrypt(enable);
//    }

    /**
     * 获取测试设备的方法
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            LogUtil.i(TAG, json.toString());
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
