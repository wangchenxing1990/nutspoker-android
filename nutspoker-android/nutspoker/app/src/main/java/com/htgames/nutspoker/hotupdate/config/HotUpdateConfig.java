package com.htgames.nutspoker.hotupdate.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * 热更新配置
 */
public class HotUpdateConfig {
    public static final String KEY_GAME_VERSION = "com.htgames.nutspoker.gameVersion";//游戏Version
//    //app初始化游戏版本号
//    public static String APP_GAME_VERSION = "1.0.1";

    /**
     * 获取配置文件中的游戏版本号
     */
    public static String getGameVersion(Context context) {
        String gameVersion = "";
        if (TextUtils.isEmpty(gameVersion)) {
            ApplicationInfo appInfo;
            try {
                appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                gameVersion = appInfo.metaData.getString(KEY_GAME_VERSION);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (TextUtils.isEmpty(gameVersion)) {
            LogUtil.i("HotUpdate", "配置文件中NGamesId未配置");
        }
        return gameVersion;
    }
}
