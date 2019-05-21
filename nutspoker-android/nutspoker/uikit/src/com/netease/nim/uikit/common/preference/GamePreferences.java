package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;

/**
 * 游戏配置
 */
public class GamePreferences {
    public static GamePreferences mPreferences;
    public SharedPreferences sp_gameconfig;
    public final static String GAMECONFIG = "gameconfig";
    //GAMECONFIG里面键
    private static final String KEY_GAME_CONFIG_DATA = "game_config_data";//游戏相关配置数据
    private static final String KEY_GAME_CONFIG_DATA_OMAHA = "game_config_data_omaha";//游戏相关配置数据----奥马哈的
    private static final String KEY_GAME_CONFIG_PINEAPPLE = "game_config_data_pineapple";//游戏相关配置数据----奥马哈的
    private static final String KEY_GAME_CONFIG_VERSION = "game_config_ver";//游戏相关配置版本
    private static final String KEY_GAME_CONFIG_VERSION_OMAHA = "game_config_ver_omaha";//奥马哈游戏相关配置版本，和"德州扑克"分开，否则json串太长
    private static final String KEY_GAME_CONFIG_VERSION_PINEAPPLE = "game_config_ver_pineapple";//大菠萝游戏相关配置版本，和"德州扑克"分开，否则json串太长
    private static final String KEY_HAS_SHOWED_PLAY_MODE_GUIDANCE = "play_mode_guidance_has_showed";//playmode指导
    //
    private static final String KEY_GAME_LAST_CONFIG_SNG = "last_game_config_sng";//上次游戏SNG模式相关配置

    private GamePreferences(Context context) {
        sp_gameconfig = context.getSharedPreferences(GAMECONFIG + "_" + DemoCache.getAccount(), Context.MODE_PRIVATE);
    }

    public static GamePreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new GamePreferences(context);
        }
        return mPreferences;
    }

    ///***************************************************************普通***************************************************************///
    private static final String KEY_OWNGAME_COUNT = "owngame_count";//后缀牌局数目 比如    "66"
    private static final String KEY_OWNGAME_PREFIX = "owngame_prefix";//前缀    比如  "周智慧的牌局"
    public void setOwngameCount(int count) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_OWNGAME_COUNT, count);
        edit.apply();
    }

    public int getOwngameCount() {
        return sp_gameconfig.getInt(KEY_OWNGAME_COUNT, 1);
    }

    public void setOwngamePrefix(String prefix) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_OWNGAME_PREFIX, prefix);
        edit.apply();
    }

    public String getOwngamePrefix() {
        String defaultStr = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()) + "的牌局";
        return sp_gameconfig.getString(KEY_OWNGAME_PREFIX, defaultStr);
    }

    ///***************************************************************SNG***************************************************************///
    private static final String KEY_OWNGAME_SNG_PREFIX = "owngame_sng_prefix";//我开的SNG牌局数量
    private static final String KEY_OWNGAME_SNG_COUNT = "owngame_sng_count";//我开的SNG牌局数量
    public void setSngGameCount(int count) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_OWNGAME_SNG_COUNT, count);
        edit.apply();
    }

    public int getSngGameCount() {
        return sp_gameconfig.getInt(KEY_OWNGAME_SNG_COUNT, 1);
    }

    public void setSngGamePrefix(String prefix) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_OWNGAME_SNG_PREFIX, prefix);
        edit.apply();
    }

    public String getSngGamePrefix() {
        String defaultStr = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()) + "的SNG";
        return sp_gameconfig.getString(KEY_OWNGAME_SNG_PREFIX, defaultStr);
    }

    ///***************************************************************MTT***************************************************************///
    private static final String KEY_OWNGAME_MTT_PREFIX = "owngame_mtt_prefix";//我开的MTT牌局数量
    private static final String KEY_OWNGAME_MTT_COUNT = "owngame_mtt_count";//我开的MTT牌局数量
    public void setMttGameCount(int count) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_OWNGAME_MTT_COUNT, count);
        edit.apply();
    }

    public int getMttGameCount() {
        return sp_gameconfig.getInt(KEY_OWNGAME_MTT_COUNT, 1);
    }

    public void setMttGamePrefix(String prefix) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_OWNGAME_MTT_PREFIX, prefix);
        edit.apply();
    }

    public String getMttGamePrefix() {
        String defaultStr = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()) + "的MTT";
        return sp_gameconfig.getString(KEY_OWNGAME_MTT_PREFIX, defaultStr);
    }

    ///***************************************************************大菠萝普通***************************************************************///
    private static final String KEY_OWNGAME_PINEAPPLE_PREFIX = "owngame_pineapple_prefix";//我开的MTT牌局数量
    private static final String KEY_OWNGAME_PINEAPPLE_COUNT = "owngame_pineapple_count";//我开的MTT牌局数量
    public int getPineappleGameCount() {
        return sp_gameconfig.getInt(KEY_OWNGAME_PINEAPPLE_COUNT, 1);
    }

    public void setPineappleGameCount(int count) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_OWNGAME_PINEAPPLE_COUNT, count);
        edit.apply();
    }

    public String getPineappleGamePrefix() {
        String defaultStr = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()) + "的大菠萝";
        return sp_gameconfig.getString(KEY_OWNGAME_PINEAPPLE_PREFIX, defaultStr);
    }

    public void setPineappleGamePrefix(String prefix) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_OWNGAME_PINEAPPLE_PREFIX, prefix);
        edit.apply();
    }

    ///***************************************************************大菠萝MTT***************************************************************///
    private static final String KEY_OWNGAME_PINEAPPLE_MTT_PREFIX = "key_owngame_pineapple_mtt_prefix";//我开的MTT牌局数量
    private static final String KEY_OWNGAME_PINEAPPLE_MTT_COUNT = "key_owngame_pineapple_mtt_count";//我开的MTT牌局数量
    public int getPineappleMttGameCount() {
        return sp_gameconfig.getInt(KEY_OWNGAME_PINEAPPLE_MTT_COUNT, 1);
    }

    public void setPineappleMttGameCount(int count) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_OWNGAME_PINEAPPLE_MTT_COUNT, count);
        edit.apply();
    }

    public String getPineappleMttGamePrefix() {
        String defaultStr = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()) + "大菠萝MTT";
        return sp_gameconfig.getString(KEY_OWNGAME_PINEAPPLE_MTT_PREFIX, defaultStr);
    }

    public void setPineappleMttGamePrefix(String prefix) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_OWNGAME_PINEAPPLE_MTT_PREFIX, prefix);
        edit.apply();
    }

    public void setConfigVersion(int ver) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_GAME_CONFIG_VERSION, ver);
        edit.apply();
    }

    public int getConfigVersion() {
        return sp_gameconfig.getInt(KEY_GAME_CONFIG_VERSION, 0);
    }

    public void setConfigVersionOmaha(int ver) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_GAME_CONFIG_VERSION_OMAHA, ver);
        edit.apply();
    }

    public int getConfigVersionOmaha() {
        return sp_gameconfig.getInt(KEY_GAME_CONFIG_VERSION_OMAHA, 0);
    }

    public void setConfigVersionPineapple(int ver) {
        Editor edit = sp_gameconfig.edit();
        edit.putInt(KEY_GAME_CONFIG_VERSION_PINEAPPLE, ver);
        edit.apply();
    }

    public int getConfigVersionPineapple() {
        return sp_gameconfig.getInt(KEY_GAME_CONFIG_VERSION_PINEAPPLE, 0);
    }

    public void setGameConfigData(String data) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_GAME_CONFIG_DATA, data);
        edit.apply();
    }

    public String getGameConfigData() {
        return sp_gameconfig.getString(KEY_GAME_CONFIG_DATA, "");
    }

    public String getGameConfigDataOmaha() {
        return sp_gameconfig.getString(KEY_GAME_CONFIG_DATA_OMAHA, "");
    }

    public void setGameConfigDataOmaha(String data) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_GAME_CONFIG_DATA_OMAHA, data);
        edit.apply();
    }

    public String getGameConfigDataPineapple() {
        return sp_gameconfig.getString(KEY_GAME_CONFIG_PINEAPPLE, "");
    }

    public void setGameConfigDataPineapple(String data) {
        Editor edit = sp_gameconfig.edit();
        edit.putString(KEY_GAME_CONFIG_PINEAPPLE, data);
        edit.apply();
    }
    
    public boolean hasShowPlayModeGuidance() {
        return sp_gameconfig.getBoolean(KEY_HAS_SHOWED_PLAY_MODE_GUIDANCE, false);
    }
    
    public void setHasShowPlayModeGuidance(boolean hasShowed) {
        Editor edit = sp_gameconfig.edit();
        edit.putBoolean(KEY_HAS_SHOWED_PLAY_MODE_GUIDANCE, hasShowed);
        edit.apply();
    }

}
