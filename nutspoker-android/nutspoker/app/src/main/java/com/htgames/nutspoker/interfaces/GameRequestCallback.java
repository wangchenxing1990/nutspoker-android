package com.htgames.nutspoker.interfaces;

import org.json.JSONObject;

/**
 * 游戏的接口监听回调
 */
public interface GameRequestCallback {
    void onSuccess(JSONObject response);

    void onFailed(int code, JSONObject response);
}
