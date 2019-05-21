package com.htgames.nutspoker.hotupdate.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.hotupdate.config.HotUpdateConfig;
import com.netease.nim.uikit.common.DateTools;

/**
 * 游戏热更新
 */
public class HotUpdatePreferences {
    public static HotUpdatePreferences mPreferences;
    public SharedPreferences sp_gameupdate;
    public final static String GAMEUPDATE = "gameupdate";
    private static final String KEY_GAME_VERSION = "game_version";//上次游戏版本号
    private static final String KEY_APP_VERSION = "app_version";//上次APP版本号
    private static final String KEY_CHECK_VERSION_TIME = "check_version_time";//上次检测热更新时间

    private HotUpdatePreferences(Context context) {
        sp_gameupdate = context.getSharedPreferences(GAMEUPDATE, Context.MODE_PRIVATE);
    }

    public static HotUpdatePreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new HotUpdatePreferences(context);
        }
        return mPreferences;
    }

    public void setGameVersion(String version) {
        Editor edit = sp_gameupdate.edit();
        edit.putString(KEY_GAME_VERSION, version);
        edit.commit();
    }

    public String getGameVersion() {
        return sp_gameupdate.getString(KEY_GAME_VERSION, HotUpdateConfig.getGameVersion(DemoCache.getContext()));
    }

    public void setAppVersion(String version) {
        Editor edit = sp_gameupdate.edit();
        edit.putString(KEY_APP_VERSION, version);
        edit.commit();
    }

    public String getAppVersion() {
        return sp_gameupdate.getString(KEY_APP_VERSION, "");
    }

    //设置检测时间为当前时间
    public void setCheckVersionCurrentTime() {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        Editor edit = sp_gameupdate.edit();
        edit.putLong(KEY_CHECK_VERSION_TIME, currentTime);
        edit.commit();
    }

    public void setCheckVersionTime(long time) {
        Editor edit = sp_gameupdate.edit();
        edit.putLong(KEY_CHECK_VERSION_TIME, time);
        edit.commit();
    }

    public long getCheckVersionTime() {
        return sp_gameupdate.getLong(KEY_CHECK_VERSION_TIME, 0);
    }
}
