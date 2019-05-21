package com.htgames.nutspoker.ui.action;

import android.app.Activity;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 好友关系
 */
public class FriendRelationshipAction extends BaseAction{
    public static final String TAG = "FriendRelation";
    String requestAddFriendUrl = "";
    String requestDelFriendUrl = "";

    public FriendRelationshipAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 添加好友
     * @param friendAccid
     * @param requestCallback
     */
    public void addFriend(final String friendAccid, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestAddFriendUrl = getHost() + ApiConstants.URL_FRIEND_ADD;
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestAddFriendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                        Toast.makeText(ChessApp.sAppContext, R.string.add_friend_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChessApp.sAppContext, R.string.add_friend_failure , Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.add_friend_failure , Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("accid", friendAccid);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestAddFriendUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 解散俱乐部：通过APP服务器
     */
    public void deleteFriend(final String friendAccid , final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("faccid", friendAccid);
        LogUtil.i(TAG, paramsMap.toString());
        DialogMaker.showProgressDialog(ChessApp.sAppContext, getString(R.string.remove_friend_ing), false);
        requestDelFriendUrl = getHost() + ApiConstants.URL_FRIEND_DEL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestDelFriendUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.DELETE, requestDelFriendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        android.widget.Toast.makeText(ChessApp.sAppContext, R.string.remove_friend_success, android.widget.Toast.LENGTH_SHORT).show();
                        if(requestCallback != null){
                            requestCallback.onResult(code , response ,null);
                        }
                    } else {
                        Toast.makeText(ChessApp.sAppContext, R.string.remove_friend_failed, android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.remove_friend_failed, android.widget.Toast.LENGTH_SHORT).show();
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestDelFriendUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取好友状态
     * @param requestCallback
     */
    public void getFriendStatusList(final RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        addRequestGet(getHost() + ApiConstants.URL_USER_FRIENDSTATUS,paramsMap,requestCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestAddFriendUrl);
        cancelAll(requestDelFriendUrl);
    }
}
