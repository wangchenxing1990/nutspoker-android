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
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.data.common.CircleConstant;
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
 * 朋友圈动作
 */
public class CircleAction extends BaseAction {
    private final static String TAG = "CircleAction";
    String requestFindListUrl;
    String requestShareUrl;
    String requestCommentUrl;
    String requestLikeUrl;
    String requestRevokeUrl;

    public CircleAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 获取发现列表
     * @param lsid 最后一条分享的id, lsid为空时，返回最新分享，>0时返回lsid之前的分享
     * @param mRequestCallback
     */
    public void getFindList(String lsid , final RequestCallback mRequestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (mRequestCallback != null) {
                mRequestCallback.onFailed();
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
        if (!TextUtils.isEmpty(lsid)) {
            paramsMap.put("lsid", lsid);
        }
        requestFindListUrl = getHost() + ApiConstants.URL_SHARE_FIND + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestFindListUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestFindListUrl, new Response.Listener<String>() {
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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestFindListUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 分享到德州圈
     * @param content 内容
     * @param type 类型
     * @param gid  牌局ID
     * @param requestCallback
     */
    public void doShareToCircle(final String content, final int type, final String gid, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(R.string.circle_share_ing), false);
        requestShareUrl = getHost() + ApiConstants.URL_SHARE;
        LogUtil.i(TAG, requestShareUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestShareUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                        Toast.makeText(ChessApp.sAppContext, R.string.circle_share_success, android.widget.Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChessApp.sAppContext, R.string.circle_share_failure, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.circle_share_failure, Toast.LENGTH_SHORT).show();
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("content", content);
                paramsMap.put("pid", "0");
                paramsMap.put("type", String.valueOf(type));
                if (type == CircleConstant.TYPE_PAIJU) {
                    paramsMap.put("gid", gid);
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestShareUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 评论德州圈
     * @param content
     * @param pid 分享的id
     * @param reply_to_uid
     * @param requestCallback
     */
    public void doComment(final String content, final String pid , final String reply_to_uid, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestCommentUrl = getHost() + ApiConstants.URL_SHARE_COMMENT;
        LogUtil.i(TAG, requestCommentUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCommentUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                        if(!TextUtils.isEmpty(reply_to_uid)){
                            Toast.makeText(ChessApp.sAppContext, R.string.circle_reply_success, android.widget.Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChessApp.sAppContext, R.string.circle_comment_success, android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(!TextUtils.isEmpty(reply_to_uid)){
                            Toast.makeText(ChessApp.sAppContext, R.string.circle_reply_failure, android.widget.Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChessApp.sAppContext, R.string.circle_comment_failure, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if(!TextUtils.isEmpty(reply_to_uid)){
                    Toast.makeText(ChessApp.sAppContext, R.string.circle_reply_failure, android.widget.Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ChessApp.sAppContext, R.string.circle_comment_failure, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("content", content);
                paramsMap.put("pid", pid);
                if (!TextUtils.isEmpty(reply_to_uid)) {
                    paramsMap.put("reply_to_uid", reply_to_uid);
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestCommentUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 点赞
     * @param sid 分享的ID
     * @param like 1:赞  2.取消赞
     * @param requestCallback
     */
    public void doLike(final String sid , final boolean like , final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
//        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestLikeUrl = getHost() + ApiConstants.URL_SHARE_LIKE;
        LogUtil.i(TAG, requestLikeUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLikeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
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
                DialogMaker.dismissProgressDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("sid", sid);
                if(like){
                    paramsMap.put("like", CircleConstant.LIKE_ACTION_LIKE);
                }else{
                    paramsMap.put("like", CircleConstant.LIKE_ACTION_LIKE_CANCEL);
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestLikeUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 撤回评论
     */
    public void revokeShareOrComment(String sid  ,final RequestCallback mRequestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("sid", sid);
        requestRevokeUrl = getHost() + ApiConstants.URL_SHARE_REVOKE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestRevokeUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.DELETE, requestRevokeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        Toast.makeText(ChessApp.sAppContext, R.string.circle_delete_success, android.widget.Toast.LENGTH_SHORT).show();
                        if (mRequestCallback != null) {
                            mRequestCallback.onResult(resultCode, response.toString(), null);
                        }
                    }else{
                        Toast.makeText(ChessApp.sAppContext, R.string.circle_delete_failure, android.widget.Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChessApp.sAppContext, R.string.circle_delete_failure, android.widget.Toast.LENGTH_SHORT).show();
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
        signRequest.setTag(requestRevokeUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestFindListUrl);
        cancelAll(requestShareUrl);
        cancelAll(requestCommentUrl);
        cancelAll(requestLikeUrl);
        cancelAll(requestRevokeUrl);
    }
}
