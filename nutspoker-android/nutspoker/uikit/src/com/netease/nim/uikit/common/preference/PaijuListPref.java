package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;

/**
 * Created by 周智慧 on 2017/9/25.
 */

public class PaijuListPref {
    public static final String TAG = "PaijuListPrefTag";
    public static PaijuListPref mPaijuListPref;
    private SharedPreferences sp;

    private PaijuListPref() {
        sp = CacheConstant.sAppContext.getSharedPreferences(DemoCache.getAccount() + TAG, Context.MODE_PRIVATE);
    }

    public static PaijuListPref getInstance() {
        if (mPaijuListPref == null) {
            mPaijuListPref = new PaijuListPref();
        }
        return mPaijuListPref;
    }

    public static void reset() {
        mPaijuListPref = null;
    }

    ///***************************************************************主页历史列表***************************************************************///
    public static boolean firstLaunchMainList = true;//只有第一次启动时使用本地保存的json，其余网络请求
    public static final String KEY_MAIN_LIST = "KEY_MAIN_LIST";
    public void setMainList(String mainListJsonStr) {
        Editor editor = sp.edit();
        editor.putString(KEY_MAIN_LIST, mainListJsonStr);
        editor.apply();
    }

    public String getMainList() {
        return sp.getString(KEY_MAIN_LIST, "");
    }

    ///***************************************************************俱乐部牌局列表***************************************************************///
    public static boolean firstLaunchTeamPaiju = true;//只有第一次启动时使用本地保存的json，其余网络请求
    public static final String KEY_TEAM_PAIJU_LIST = "KEY_TEAM_PAIJU_LIST";
    public void setTeamPaijuList(String tid, String mainListJsonStr) {
        Editor editor = sp.edit();
        editor.putString(KEY_TEAM_PAIJU_LIST + "_" + tid, mainListJsonStr);
        editor.apply();
    }

    public String getTeamPaijuList(String tid) {
        return sp.getString(KEY_TEAM_PAIJU_LIST + "_" + tid, "");
    }
}
