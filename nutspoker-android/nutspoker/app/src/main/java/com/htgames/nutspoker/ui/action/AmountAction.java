package com.htgames.nutspoker.ui.action;

import android.app.Activity;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 余额动作
 */
public class AmountAction extends BaseAction {
    private final static String TAG = "AmountAction";
    String requestAmountUrl;

    public AmountAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 获取游戏余额
     */
    public void getAmount(boolean isLimit) {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetAmountTime = RequestTimeLimit.lastGetAmontTime;
        if (isLimit) {
            if ((currentTime - lastGetAmountTime) < RequestTimeLimit.GET_AMONT_TIME_LIMIT) {
                LogUtil.i(TAG, "获取金币时间未到");
                return;
            }
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (mRequestCallback != null) {
                mRequestCallback.onFailed();
            }
            return;
        }
        cancelAll(requestAmountUrl);
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
//        paramsMap.put("os", ApiConstants.OS_ANDROID);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        requestAmountUrl = getHost() + ApiConstants.URL_USER_AMOUNT + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestAmountUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestAmountUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        RequestTimeLimit.lastGetAmontTime = DemoCache.getCurrentServerSecondTime();
                        JSONObject jsonObject = json.getJSONObject("data");
                        int level = jsonObject.optInt("level");
                        int diamond = jsonObject.optInt("diamond");
                        int coins = jsonObject.optInt("coins");
                        long time = jsonObject.optLong("time");//校时
                        int nickname_times = jsonObject.optInt("nickname_times");//第几次修改昵称
                        //保存
                        UserPreferences.getInstance(ChessApp.sAppContext).setCoins(coins);
                        UserPreferences.getInstance(ChessApp.sAppContext).setDiamond(diamond);
                        UserPreferences.getInstance(ChessApp.sAppContext).setNicknameTimes(nickname_times);
                        UserPreferences.getInstance(ChessApp.sAppContext).setLevel(level);
                        //校时
                        DemoCache.setCheckTimeValue(time);
                        //
                        if (mRequestCallback != null) {
                            mRequestCallback.onResult(resultCode, response.toString(), null);
                        }
                    } else {
                        if (mRequestCallback != null) {
                            mRequestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (mRequestCallback != null) {
                        mRequestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (mRequestCallback != null) {
                    mRequestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestAmountUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    RequestCallback mRequestCallback;

    public void setRequestCallback(RequestCallback callback){
        this.mRequestCallback = callback;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestAmountUrl);
        mRequestCallback = null;
    }
}
