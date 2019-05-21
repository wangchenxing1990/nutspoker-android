package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.htgames.nutspoker.game.helper.GameConfigHelper;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 游戏配置
 */
public class GameConfigAction extends BaseAction {
    private final static String TAG = "GameConfigAction";
    String requestGameConfigUrl;

    public GameConfigAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * "德州扑克"配置
     */
    public void getGameConfig() {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            DialogMaker.dismissProgressDialog();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("ver" , "" + GamePreferences.getInstance(ChessApp.sAppContext).getConfigVersion());
        requestGameConfigUrl = getHost() + ApiConstants.URL_GAME_CONFIG + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameConfigUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGameConfigUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        String data = json.optString("data");
                        GameConfigHelper.dealGameConfig(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    DialogMaker.dismissProgressDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                DialogMaker.dismissProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameConfigUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取"奥马哈"配置，加上新字段type=1
     */
    public void getGameConfigOmaha() {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            DialogMaker.dismissProgressDialog();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("ver" , "" + GamePreferences.getInstance(ChessApp.sAppContext).getConfigVersionOmaha());
        paramsMap.put("type" , String.valueOf(1));
        requestGameConfigUrl = getHost() + ApiConstants.URL_GAME_CONFIG + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameConfigUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGameConfigUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        String data = json.optString("data");
                        GameConfigHelper.dealGameConfigOmaha(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    DialogMaker.dismissProgressDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                DialogMaker.dismissProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameConfigUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取"大菠萝"配置，加上新字段type=2
     */
    public void getGameConfigPineapple() {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            DialogMaker.dismissProgressDialog();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("ver" , "" + GamePreferences.getInstance(ChessApp.sAppContext).getConfigVersionPineapple());
        paramsMap.put("type" , String.valueOf(2));
        requestGameConfigUrl = getHost() + ApiConstants.URL_GAME_CONFIG + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestGameConfigUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestGameConfigUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        String data = json.optString("data");
                        GameConfigHelper.dealGameConfigPineapple(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    DialogMaker.dismissProgressDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
                DialogMaker.dismissProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestGameConfigUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestGameConfigUrl);
    }
}
