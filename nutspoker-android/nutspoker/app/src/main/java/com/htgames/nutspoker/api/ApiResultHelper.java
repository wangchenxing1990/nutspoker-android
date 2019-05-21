package com.htgames.nutspoker.api;

import android.text.TextUtils;

import com.netease.nim.uikit.api.ApiCode;

import org.json.JSONObject;

/**
 */
public class ApiResultHelper {
    /**
     * 显示接口消息
     * @param json
     */
    public static String getShowMessage(JSONObject json) {
        String showMessage = "";
        if (json != null) {
            int code = json.optInt("code");
            JSONObject dataJson = json.optJSONObject("data");
            if (dataJson != null) {
                String message = dataJson.optString("message");
                if (code == ApiCode.CODE_MESSAGE_SHOW && !TextUtils.isEmpty(message)) {
                    showMessage = message;
                }
            }
        }
        return showMessage;
    }

    /**
     * 比赛模式控制买入操作是否已经失效
     * @param code
     */
    public static boolean isMatchBuychipsInvalid(int code) {
        if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CUTOFF
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_NOTEXIST
                || code == ApiCode.CODE_CHANNEL_BLOCK_CHECKIN
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_BLINDLEVEL_REACH
                || code == ApiCode.CODE_GAME_NONE_EXISTENT
                || code == ApiCode.CODE_MATCH_CHECKIN_ALREADY
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_PLAYER_FULL
                || code == ApiCode.CODE_USER_IS_NOT_OWNER
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_FINAL
                || code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_HANDLED_BY_OTHER_MGR
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_ALREADY_DEALED
                || code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_COIN_NOT_SUFFICIENT
                || code == ApiCode.CODE_BALANCE_INSUFFICIENT
//                || code == ApiCode.CODE_MATCH_CHECKIN_START_MOMENT
                || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_SCORE_INSUFFICIENT) {
            return true;
        }
        return false;
    }
}
