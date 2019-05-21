package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 20150726 on 2016/3/7.
 */
public class UserAction extends BaseAction{
    private final static String TAG = "UserAction";
    String requestUrl;
    String requestTeamListhUrl;

    public UserAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 根据手机号获取用户ID列表
     * @param phoneList
     * @param requestCallback
     */
    public void getUids(final ArrayList<String> phoneList ,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity) || phoneList == null || phoneList.size() == 0) {
            return;
        }
        requestUrl = getHost() + ApiConstants.URL_GETUIDS;
        LogUtil.i(TAG, requestUrl);
        StringBuffer paramsBuf = new StringBuffer();
        for(String phone : phoneList){
            paramsBuf.append(phone).append(",");
        }
        if (paramsBuf.length() == 0) {
            return;
        }
        final String usernames = paramsBuf.substring(0, paramsBuf.length() - 1);//去除最后个，
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("usernames", usernames);
                LogUtil.i(TAG , paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取相关的俱乐部列表
     * @param uid
     * @param requestCallback
     */
    public void getTeamListByUid(String uid ,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext , false);
        paramsMap.put("uid", uid);
        requestTeamListhUrl = getHost() + ApiConstants.URL_TEAM_FRIEND + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestTeamListhUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestTeamListhUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                    } else{
                        if(requestCallback != null){
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestTeamListhUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestUrl);
        cancelAll(requestTeamListhUrl);
    }

}
