package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.netease.nim.uikit.NimUIKit;

/**
 * SharedPreferences集成工具类
 */
public class UserPreferences {
    public static UserPreferences mPreferences;
    public SharedPreferences sp_userinfo;
    public final static String USERINFO = "userinfo";
    //USERINFO里面键
    private static final String KEY_USER_PHONE = "phone";
    private static final String KEY_USER_COUNTRY_CODE = "country_code";
    private static final String KEY_USER_TOKEN = "token";
    private static final String KEY_USER_ID = "uid";
    //余额
    private static final String KEY_USER_COINS = "coins";
    private static final String KEY_USER_DIAMOND = "diamond";
    //手牌收藏数量
    private static final String KEY_COLLECT_HAND_NUM = "collect_hand_num";
    //战鱼号
    static final String KEY_ZHANGYU_ID = "uuid";
    static final String KEY_USER_LEVEL = "level";//100表示能够组局钻石赛
    //第几次修改昵称
    static final String KEY_NICKNAME_TIMES = "nickname_times";

    private UserPreferences(Context context) {
        sp_userinfo = context.getSharedPreferences(USERINFO, Context.MODE_PRIVATE);
    }

    public static UserPreferences getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new UserPreferences(context);
        }
        return mPreferences;
    }

    /** 设置用户手机号 */
    public void setUserPhone(String phnoe) {
        Editor edit = sp_userinfo.edit();
        edit.putString(KEY_USER_PHONE, phnoe);
        edit.apply();
    }

    /** 获取用户帐号 */
    public String getUserPhone() {
        return sp_userinfo.getString(KEY_USER_PHONE , "");
    }

    public void setLevel(int level) {
        Editor edit = sp_userinfo.edit();
        edit.putInt(KEY_USER_LEVEL, level);
        edit.apply();
    }

    public int getLevel() {
        return sp_userinfo.getInt(KEY_USER_LEVEL , 0);
    }

    /** 获取用户手机号国际区号 */
    public String getUserCountryCode() {
//        String countryCode = CountryCodeHelper.getCurrentLocalCountryCode();
        return sp_userinfo.getString(KEY_USER_COUNTRY_CODE, "86");
    }

    /** 设置用户手机号国际区号 */
    public void setUserCountryCode(String countryCode) {
        Editor edit = sp_userinfo.edit();
        edit.putString(KEY_USER_COUNTRY_CODE, countryCode);
        edit.apply();
    }

    /** 设置用户TOKEN */
    public void setUserToken(String token) {
        Editor edit = sp_userinfo.edit();
        edit.putString(KEY_USER_TOKEN, token);
        edit.apply();
    }

    /** 获取用户TOKEN */
    public String getUserToken() {
        return sp_userinfo.getString(KEY_USER_TOKEN , "");
    }

    /** 设置用户UID */
    public void setUserId(String account) {
        Editor edit = sp_userinfo.edit();
        edit.putString(KEY_USER_ID, account);
        edit.apply();
    }

    /** 获取用户帐号 */
    public String getUserId() {
        return sp_userinfo.getString(KEY_USER_ID, "");
    }

    /** 设置用户金币余额 */
    public void setCoins(int coins) {
        Editor edit = sp_userinfo.edit();
        edit.putInt(KEY_USER_COINS, coins);
        edit.apply();
    }

    /** 获取用户昵称 */
    public int getCoins() {
        return sp_userinfo.getInt(KEY_USER_COINS , 0);
    }

    /** 设置用户宝石 */
    public void setDiamond(int diamond) {
        Editor edit = sp_userinfo.edit();
        edit.putInt(KEY_USER_DIAMOND, diamond);
        edit.apply();
    }

    /** 获取用户宝石 */
    public int getDiamond() {
        return sp_userinfo.getInt(KEY_USER_DIAMOND , 0);
    }

    //新增用户战鱼号
    public void setZYId(String zyid){
        Editor edit = sp_userinfo.edit();
        edit.putString(KEY_ZHANGYU_ID, zyid);
        edit.apply();
    }

    public String getZYId(){
        return sp_userinfo.getString(KEY_ZHANGYU_ID,"0");
    }
    /** 设置牌谱收藏数量 */
    public void setCollectHandNum(int diamond) {
        Editor edit = sp_userinfo.edit();
        edit.putInt(KEY_COLLECT_HAND_NUM, diamond);
        edit.apply();
    }

    /**
     * 获取牌谱收藏数量
     */
    public int getCollectHandNum() {
        return sp_userinfo.getInt(KEY_COLLECT_HAND_NUM, 0);
    }

    /**
     * 设置第几次修改昵称
     * @param nickname_times
     */
    public void setNicknameTimes(int nickname_times) {
        Editor edit = sp_userinfo.edit();
        edit.putInt(KEY_NICKNAME_TIMES, nickname_times);
        edit.apply();
    }

    /**
     * 获取nicknametimes
     * @return
     */
    public int getNicknameTimes() {
        return sp_userinfo.getInt(KEY_NICKNAME_TIMES , 0);
    }

    //下面的代码是从一个被删除的UserPreferences.java类拷贝过来的
    private final static String KEY_EARPHONE_MODE="KEY_EARPHONE_MODE";

    public static void setEarPhoneModeEnable(boolean on) {
        saveBoolean(KEY_EARPHONE_MODE, on);
    }

    public static boolean isEarPhoneModeEnable() {
        return getBoolean(KEY_EARPHONE_MODE, true);
    }

    private static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    private static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    static SharedPreferences getSharedPreferences() {
        return NimUIKit.getContext().getSharedPreferences("UIKit." + NimUIKit.getAccount(), Context.MODE_PRIVATE);
    }
}
