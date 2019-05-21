package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 俱乐部相关
 */
public class ClubAction extends BaseAction {
    private final static String TAG = "ClubAction";

    String requestCreateUrl;
    String requestDismissUrl;

    public ClubAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 创建俱乐部
     * @param clubName
     * @param clubAreaId
     * @param clubAvatar
     * @param requestCallback
     */
    public void createTeam(final String clubName,final int clubAreaId , final String clubAvatar, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);

        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tname", clubName);
        //paramsMap.put("area_id", String.valueOf(clubAreaId));
        if (!TextUtils.isEmpty(clubAvatar)) {
            paramsMap.put("avatar", clubAvatar);
        }

        addRequestPost(getHost() + ApiConstants.URL_TEAM, paramsMap, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();

                if (code == ApiCode.CODE_SUCCESS) {
                    if(requestCallback != null){
                        requestCallback.onResult(code , result , null);
                    }
                } else if (code == ApiCode.CODE_CLUB_CREATE_COUNT_IS_LIMIT) {
                    Toast.makeText(ChessApp.sAppContext, R.string.club_create_count_is_limit, Toast.LENGTH_SHORT).show();
                } else {
                    String message = ApiCode.SwitchCode(code, result);
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.club_create_failed);
                    }
                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.club_create_failed, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        });


//        requestCreateUrl = ApiConstants.URL_TEAM;
//        LogUtil.i(TAG, requestCreateUrl);
//        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateUrl, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                DialogMaker.dismissProgressDialog();
//                LogUtil.i(TAG, response);
//                try {
//                    JSONObject json = new JSONObject(response);
//                    int code = json.getInt("code");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (!TextUtils.isEmpty(error.getMessage())) {
//                    LogUtil.i(TAG, error.getMessage());
//                }
//                Toast.makeText(ChessApp.sAppContext, R.string.club_create_failed, Toast.LENGTH_SHORT).show();
//                DialogMaker.dismissProgressDialog();
//                if(requestCallback != null){
//                    requestCallback.onFailed();
//                }
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return paramsMap;
//            }
//        };
//        signRequest.setTag(requestCreateUrl);
//        mRequestQueue.add(signRequest);
    }

    /**
     * 解散俱乐部
     * @param teamId
     * @param requestCallback
     */
    public void dismissTeam(final String teamId , final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", teamId);
        LogUtil.i(TAG, paramsMap.toString());
        DialogMaker.showProgressDialog(mActivity, getString(R.string.dismiss_club_ing), true);
        requestDismissUrl = getHost() + ApiConstants.URL_TEAM + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestDismissUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.DELETE, requestDismissUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        LogUtil.i(TAG, "解散俱乐部成功");
                        Toast.makeText(ChessApp.sAppContext, R.string.dismiss_club_success, Toast.LENGTH_SHORT).show();
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.dismiss_club_failed);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                    }
                    if(requestCallback != null){
                        requestCallback.onResult(code , response , null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.dismiss_club_failed), Toast.LENGTH_SHORT).show();
                if (error.getMessage() != null) {
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
        signRequest.setTag(requestDismissUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /***
     *添加管理员
     * @param teamId
     * @param adminId
     * @param requestCallback
     */
    public void addAdmin(final String teamId ,final String adminId, final RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("tid", teamId);
        paramsMap.put("admin_id",adminId);

        DialogMaker.showProgressDialog(mActivity, "", true);
        addRequestPost(getHost() + ApiConstants.URL_CLUB_ADD_ADMIN, paramsMap, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if (code == ApiCode.CODE_SUCCESS) {
                    Toast.makeText(ChessApp.sAppContext, R.string.add_clubmgr_success, Toast.LENGTH_SHORT).show();
                    if(requestCallback != null){
                        requestCallback.onResult(code , result , null);
                    }
                } else {
                    String message = ApiCode.SwitchCode(code,result);
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.add_clubmgr_fail);
                    }
                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, getString(R.string.add_clubmgr_fail), Toast.LENGTH_SHORT).show();
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        });
    }

    /***
     *删除管理员
     * @param teamId
     * @param adminId
     * @param requestCallback
     */
    public void deletedmin(final String teamId ,final String adminId,final boolean isShow, final RequestCallback requestCallback) {
        if(!checkNetWork())
            return;
        if(isShow)
            DialogMaker.showProgressDialog(mActivity, getString(R.string.empty), true);

        HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", teamId);
        paramsMap.put("admin_id",adminId);

        addRequestPost(getHost() + ApiConstants.URL_CLUB_DELETE_ADMIN, paramsMap, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if (code == 0) {
                    if(isShow)
                        Toast.makeText(ChessApp.sAppContext, R.string.del_clubmgr_success, Toast.LENGTH_SHORT).show();
                    if(requestCallback != null){
                        requestCallback.onResult(code , result , null);
                    }
                } else {
                    String message = ApiCode.SwitchCode(code,result);
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.del_clubmgr_fail);
                    }
                    if(isShow)
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
                if(isShow) {
                    Toast.makeText(ChessApp.sAppContext, getString(R.string.del_clubmgr_fail), Toast.LENGTH_SHORT).show();
                }
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        });
    }

    //检查是否是俱乐部管理员  //  tid uid "  get     0否1yes
    String requestCheckAdmin;
    public void checkAdmin(String tid, String uid, final VolleyCallback callback) {
        if(!checkNetWork()) {
            if (callback != null) {
                callback.onErrorResponse(null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        requestCheckAdmin = getHost() + ApiConstants.URL_TEAM_CHECK_ADMIN + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCheckAdmin);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestCheckAdmin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                if (callback != null) {
                    callback.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (callback != null) {
                    callback.onErrorResponse(null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCheckAdmin);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部基金充值
    String requestFundPurchase = "";
    public void purchaseFundGoods(final String tid, final String goods_id, final String num, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, "充值中...", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("goods_id", goods_id);
        paramsMap.put("num", num);
        requestFundPurchase = getHost() + ApiConstants.URL_TEAM_FUND_PURCHASE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestFundPurchase);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestFundPurchase, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestFundPurchase);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部基金充值的商品列表
    String requestFundGoods = "";
    public void getFundGoods(final String tid, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);//这个参数貌似不需要，各个俱乐部的商品列表都一样
        requestFundGoods = getHost() + ApiConstants.URL_TEAM_FUND_GOODS + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestFundGoods);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestFundGoods, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestFundGoods);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部基金充值记录
    String requestFundLog = "";
    public void fundLog(final String tid, final String id, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        if (StringUtil.isSpaceOrZero(id)) {
            DialogMaker.showProgressDialog(mActivity, "", false);
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("id", id);
        requestFundLog = getHost() + ApiConstants.URL_TEAM_FUND_LOG + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestFundLog);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestFundLog, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestFundLog);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部基金充值记录
    String requestFundGrant = "";
    public void fundGrant(final String tid, final String fund, final String member_uid, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("fund", fund);
        paramsMap.put("member_uid", member_uid);
        requestFundGrant = getHost() + ApiConstants.URL_TEAM_FUND_GRANT + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestFundGrant);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestFundGrant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestFundGrant);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部 信用分操作记录
    String requestCreditLog = "";
    public void creditLog(final String tid, final String uid, final String id, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        if (StringUtil.isSpaceOrZero(id)) {
            DialogMaker.showProgressDialog(mActivity, "", false);
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        if (!StringUtil.isSpaceOrZero(tid)) {
            paramsMap.put("tid", tid);
        }
        if (!StringUtil.isSpaceOrZero(uid)) {
            paramsMap.put("uid", uid);
        }
        paramsMap.put("id", id);
        requestCreditLog = getHost() + ApiConstants.URL_TEAM_CREDIT_LOG + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreditLog);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestCreditLog, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json.getJSONObject("data"));
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreditLog);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部 信用分操作记录
    String requestCreditScoreSet = "";
    public void creditScoreSet(final String tid, final String member_uid, final String score, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        if (!StringUtil.isSpaceOrZero(tid)) {
            paramsMap.put("tid", tid);
        }
        if (!StringUtil.isSpaceOrZero(member_uid)) {
            paramsMap.put("member_uid", member_uid);
        }
        paramsMap.put("score", score);
        requestCreditScoreSet = getHost() + ApiConstants.URL_TEAM_CREDIT_SCORE_SET + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreditScoreSet);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreditScoreSet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreditScoreSet);
        ChessApp.sRequestQueue.add(signRequest);
    }

    //俱乐部 信用分操作记录
    String requestCreditScoreList = "";
    public void creditScoreList(final String tid, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        if (StringUtil.isSpaceOrZero(tid)) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_PARAMS_ERROR, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        requestCreditScoreList = getHost() + ApiConstants.URL_TEAM_CREDIT_SCORE_LIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreditScoreList);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestCreditScoreList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreditScoreList);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestCreditMyScore = "";
    public void creditMyScore(final String tid, final String uid, final GameRequestCallback mGameRequestCallback) {
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        if (StringUtil.isSpaceOrZero(tid)) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_PARAMS_ERROR, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        requestCreditMyScore = getHost() + ApiConstants.URL_TEAM_CREDIT_MY_SCORE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreditMyScore);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestCreditMyScore, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onSuccess(json);
                        }
                    } else {
                        if (mGameRequestCallback != null) {
                            mGameRequestCallback.onFailed(code, json);
                        }
                    }
                } catch (Exception e) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreditMyScore);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //cancelAll(requestCreateUrl);
        cancelAll(requestDismissUrl);
        cancelAll(requestCheckAdmin);
        cancelAll(requestFundGoods);
        cancelAll(requestFundPurchase);
        cancelAll(requestFundLog);
        cancelAll(requestFundGrant);
        cancelAll(requestCreditLog);
        cancelAll(requestCreditScoreSet);
        cancelAll(requestCreditScoreList);
        cancelAll(requestCreditMyScore);
    }
}
