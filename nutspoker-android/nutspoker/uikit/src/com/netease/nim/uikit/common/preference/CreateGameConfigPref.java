package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.netease.nim.uikit.common.DemoCache;

/**
 * Created by 周智慧 on 17/2/20.
 */

public class CreateGameConfigPref {
    public static CreateGameConfigPref mPreferences;
    public SharedPreferences sharedPreferences;
    public final static String NormalConfigPref = "normalconfigpref";
    private static final String KEY_NORMAL_CONFIG_PREF = "key_normal_config_pref";
    private static final String KEY_SNG_CONFIG_PREF = "key_sng_config_pref";
    private static final String KEY_MTT_CONFIG_PREF = "key_mtt_config_pref";
    private static final String KEY_PINEAPPLE_CONFIG_PREF = "key_pineapple_config_pref";
    private static final String KEY_PINEAPPLE_CONFIG_MTT_PREF = "key_pineapple_config_mtt_pref";
    private static final String KEY_PLAY_MODE = "key_play_mode";//游戏模式，0="德州扑克"或者1="奥马哈"
    private CreateGameConfigPref(Context context) {
        sharedPreferences = context.getSharedPreferences(DemoCache.getAccount() + "" + NormalConfigPref, Context.MODE_PRIVATE);
    }

    public static CreateGameConfigPref getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new CreateGameConfigPref(context);
        }
        return mPreferences;
    }
    ////////////////////////////////////////////////////////////////////普通局////////////////////////////////////////////////////////////////////
    public String getNormalConfig() {
        return sharedPreferences.getString(KEY_NORMAL_CONFIG_PREF, "");
    }

    public void setNormalConfig(String listString) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_NORMAL_CONFIG_PREF, listString);
        edit.apply();
    }

    ////////////////////////////////////////////////////////////////////sng////////////////////////////////////////////////////////////////////
    public String getSngConfig() {
        return sharedPreferences.getString(KEY_SNG_CONFIG_PREF, "");
    }

    public void setSngConfig(String listString) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_SNG_CONFIG_PREF, listString);
        edit.apply();
    }

    ////////////////////////////////////////////////////////////////////mtt////////////////////////////////////////////////////////////////////
    public String getMttConfig() {
        return sharedPreferences.getString(KEY_MTT_CONFIG_PREF, "");
    }

    public void setMttConfig(String listString) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_MTT_CONFIG_PREF, listString);
        edit.apply();
    }

    ////////////////////////////////////////////////////////////////////大菠萝////////////////////////////////////////////////////////////////////
    public String getPineappleConfig() {
        return sharedPreferences.getString(KEY_PINEAPPLE_CONFIG_PREF, "");
    }

    public void setPineappleConfig(String listString) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_PINEAPPLE_CONFIG_PREF, listString);
        edit.apply();
    }

    public String getPineappleConfigMtt() {
        return sharedPreferences.getString(KEY_PINEAPPLE_CONFIG_MTT_PREF, "");
    }

    public void setPineappleConfigMtt(String listString) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_PINEAPPLE_CONFIG_MTT_PREF, listString);
        edit.apply();
    }

    public int getPlayMode() {
        return sharedPreferences.getInt(KEY_PLAY_MODE, 0);
    }

    public void setPlayMode(int playMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_PLAY_MODE, playMode);
        editor.apply();
    }

    public static void reset() {
        mPreferences = null;
    }
}
