package com.htgames.nutspoker.ui.action.club;

import android.content.Context;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.net.HttpManager;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 推荐俱乐部请求
 */
public class RecommendClubRequest {
    private final static String TAG = "RecommendClubRequest";

    /**
     * 获取已收藏的牌局数量
     *
     * @param context
     * @param mRequestCallback
     */
    public static void getRecommendClub(final Context context, final RequestCallback mRequestCallback) {
        if (!NetworkUtil.isNetAvailable(context)) {
            if (mRequestCallback != null) {
                mRequestCallback.onFailed();
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        String requestHandCollectListUrl = getHost() + ApiConstants.URL_TEAM_RECOMMEND + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHandCollectListUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHandCollectListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
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
        signRequest.setTag(requestHandCollectListUrl);
        HttpManager.getInstance(ChessApp.sAppContext).add(signRequest);
    }
}
