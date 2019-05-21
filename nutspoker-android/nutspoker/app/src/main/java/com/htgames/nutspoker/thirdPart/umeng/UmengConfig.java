package com.htgames.nutspoker.thirdPart.umeng;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.utils.Log;

/**
 * Umeng配置文件
 */
public class UmengConfig {
    public static void init() {
        //http://dev.umeng.com/analytics/android-doc/integration
        /*2.2.5  在代码中配置Appkey和Channel   如果希望在代码中配置Appkey、Channel、Token（Dplus）等信息，请在程序入口处调用如下方法：
        MobclickAgent. startWithConfigure(UMAnalyticsConfig config)
        UMAnalyticsConfig初始化参数类，提供多参数构造方式：
        UMAnalyticsConfig(Context context, String appkey, String channelId)
        UMAnalyticsConfig(Context context, String appkey, String channelId, EScenarioType eType)
        UMAnalyticsConfig(Context context, String appkey, String channelId, EScenarioType eType,Boolean isCrashEnable)
        构造意义：
        String appkey:官方申请的Appkey
        String channel: 渠道号
        EScenarioType eType: 场景模式，包含统计、游戏、统计盒子、游戏盒子
        Boolean isCrashEnable: 可选初始化. 是否开启crash模式*/
        //是否测试
        MobclickAgent.setDebugMode(ApiConfig.isTestVersion);
        String appkey = ApiConfig.getUmengAppkeyValue();////release版本把key字符串置空，放到gradle里面初始化
        String channelId = ChessApp.getAppMetaData("UMENG_CHANNEL");//这个值在androidmanifest里面配置的
        if (!StringUtil.isSpace(appkey)) {
            MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(ChessApp.sAppContext, ApiConfig.getUmengAppkeyValue(), channelId));
        }
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        //MobclickAgent.openActivityDurationTrack(false);
        //UmengAnalytics.getDeviceInfo(getApplicationContext());
        initPlatform();
    }

    public static void initPlatform() {
        //这里还是用release打包好一点，否则不同开发人员编译的demo无法唤醒微信。
        Config.IsToastTip = false;//关闭log和toast
        Log.LOG = false;
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            PlatformConfig.setWeixin(ApiConfig.APP_WEIXIN_TW, ApiConfig.SECRET_WEIXIN_TW);//微信:台湾版本正式服
            PlatformConfig.setSinaWeibo(ApiConfig.APP_WEIBO_TW, ApiConfig.SECRET_WEIBO_TW);//新浪微博:正式服
        } else {
            PlatformConfig.setWeixin(ApiConfig.APP_WEIXIN_DL, ApiConfig.SECRET_WEIXIN_DL);//微信:正式服
            PlatformConfig.setSinaWeibo(ApiConfig.APP_WEIBO_DL, ApiConfig.SECRET_WEIBO_DL);//新浪微博:正式服
        }
    }
}
