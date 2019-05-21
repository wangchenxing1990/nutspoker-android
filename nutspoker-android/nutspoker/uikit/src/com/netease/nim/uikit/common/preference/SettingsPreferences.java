package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 设置配置
 */
public class SettingsPreferences {
    public static SettingsPreferences mPreferences;
    public final static String SETTINGS = "settings";
    public SharedPreferences sp_setting;
    //SETTINGS里面键
    public final static String KEY_ISFIRST = "isfirst";
    public final static String KEY_ISPUSH = "ispush";
    public final static String KEY_GAMESOUND = "gamesound";//游戏音效
    public final static String KEY_MESSAGE_NOTICE = "message_notice";//游戏通知
    public final static String KEY_MESSAGE_SOUND = "message_sound";//消息声音
    public final static String KEY_MESSAGE_SHAKE = "message_shake";//消息震动
    public final static String KEY_ISAUTOMUCK = "isautomuck"; //游戏是否自动弃牌
    public final static String KEY_CARD_CATE = "gameCardCate"; //游戏牌面样子
    //注册流程
    public final static String KEY_AUTHCODE_TIME = "authcode_time";//验证码发送时间
    //下载APP
    public final static String KEY_DOWNLOAD_ID = "download_id";//
    public final static String KEY_CHECK_VERSION_LAST_TIME = "check_version_time";//最后一次检测版本更新时间（一天检测一次）
    //是否是测试服
    public final static String KEY_APP_IS_TEST_VERSION = "key_app_is_test_version";

    private SettingsPreferences(Context context) {
        sp_setting = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
    }

    public static SettingsPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new SettingsPreferences(context);
        }
        return mPreferences;
    }

    public boolean getIsPush() {
        return sp_setting.getBoolean(KEY_ISPUSH, true);
    }

    public void setIsPush(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_ISPUSH, is);
        edit.apply();
    }

    public boolean getIsFirst() {
        return sp_setting.getBoolean(KEY_ISFIRST, true);
    }

    public void setIsFirst(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_ISFIRST, is);
        edit.apply();
    }

    public boolean isGameSound() {
        return sp_setting.getBoolean(KEY_GAMESOUND, true);
    }

    public void setGameSound(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_GAMESOUND, is);
        edit.apply();
    }

    public int isGameAutoMuck() {
        return sp_setting.getInt(KEY_ISAUTOMUCK, 0);
    }

    public void setGameAutoMuck(int is) {
        SharedPreferences.Editor editor = sp_setting.edit();
        editor.putInt(KEY_ISAUTOMUCK, is);
        editor.apply();
    }

    public void setGameCardCate(int cate) {
        SharedPreferences.Editor editor = sp_setting.edit();
        editor.putInt(KEY_CARD_CATE, cate);
        editor.apply();
    }

    public int getGameCardCate() {
        return sp_setting.getInt(KEY_CARD_CATE, 1);
    }

    public boolean isMessageNotice() {
        return sp_setting.getBoolean(KEY_MESSAGE_NOTICE, true);
    }

    public void setMessageNotice(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_MESSAGE_NOTICE, is);
        edit.apply();
    }

    public boolean isMessageSound() {
        return sp_setting.getBoolean(KEY_MESSAGE_SOUND, true);
    }

    public void setMessageSound(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_MESSAGE_SOUND, is);
        edit.apply();
    }

    public boolean isMessageShake() {
        return sp_setting.getBoolean(KEY_MESSAGE_SHAKE, true);
    }

    public void setMessageShake(boolean is) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putBoolean(KEY_MESSAGE_SHAKE, is);
        edit.apply();
    }

    public long getAuthcodeTime() {
        return sp_setting.getLong(KEY_AUTHCODE_TIME, 0);
    }

    public void setAuthcodeTime(long authcodeTime) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putLong(KEY_AUTHCODE_TIME, authcodeTime);
        edit.apply();
    }

    public String getDownloadId(){
        return sp_setting.getString(KEY_DOWNLOAD_ID , "");
    }

    public void setDownloadId(String downloadId) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putString(KEY_DOWNLOAD_ID, downloadId);
        edit.apply();
    }

    public String getCheckVersionLastTime(){
        return sp_setting.getString(KEY_CHECK_VERSION_LAST_TIME , "");
    }

    public void setCheckVersionLastTime(String lastTime) {
        SharedPreferences.Editor edit = sp_setting.edit();
        edit.putString(KEY_CHECK_VERSION_LAST_TIME, lastTime);
        edit.apply();
    }

    public final static String KEY_POKER_CLANS_PROTOCOL = "poker_clans_protocol";//扑克部落游戏许可及服务协议
    public void setAgreePokerClansProtocol(boolean hasAgree) {
        SharedPreferences.Editor editor = sp_setting.edit();
        editor.putBoolean(KEY_POKER_CLANS_PROTOCOL, hasAgree);
        editor.apply();
    }
    public boolean hasAgreePokerClansProtocol() {
        return sp_setting.getBoolean(KEY_POKER_CLANS_PROTOCOL, false);
    }
}
