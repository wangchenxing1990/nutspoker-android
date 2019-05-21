package com.netease.nim.uikit.common.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.netease.nim.uikit.common.DemoCache;

/**
 * Created by 周智慧 on 17/1/17.
 * 游戏历史管理员设置
 */

public class GameMgrPref {
    public static GameMgrPref mPreferences;
    public SharedPreferences sp_gameMgrHistory;
    public final static String GameMgrPreference = "gamemgrpreference";
    private static final String GAME_MGR_HISTORY_KEY_PERSONAL = "game_mgr_history_key_personal";
    private static final String GAME_MGR_HISTORY_KEY_CLUB = "game_mgr_history_key_club";
    private GameMgrPref(Context context) {
        sp_gameMgrHistory = context.getSharedPreferences(DemoCache.getAccount() + "" + GameMgrPreference, Context.MODE_PRIVATE);
    }

    public static GameMgrPref getInstance(Context context) {
        if (mPreferences == null) {
            mPreferences = new GameMgrPref(context);
        }
        return mPreferences;
    }

    public String getMgrListStringPersonal() {
        return sp_gameMgrHistory.getString(GAME_MGR_HISTORY_KEY_PERSONAL, "");
    }

    public void setMgrListStringPersonal(String listString) {
        SharedPreferences.Editor edit = sp_gameMgrHistory.edit();
        edit.putString(GAME_MGR_HISTORY_KEY_PERSONAL, listString);
        edit.apply();
    }

    public String getMgrListStringClub() {
        return sp_gameMgrHistory.getString(GAME_MGR_HISTORY_KEY_CLUB, "");
    }

    public void setMgrListStringClub(String listString) {
        SharedPreferences.Editor edit = sp_gameMgrHistory.edit();
        edit.putString(GAME_MGR_HISTORY_KEY_CLUB, listString);
        edit.apply();
    }

    public static void reset() {
        mPreferences = null;
    }
}
