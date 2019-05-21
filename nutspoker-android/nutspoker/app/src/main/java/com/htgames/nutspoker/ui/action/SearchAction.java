package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.ui.activity.Club.ClubSearchActivity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 20150726 on 2016/3/23.
 */
public class SearchAction extends BaseAction {
    private final static String TAG = "SearchAction";
    private String requestSearchUserUrl;
    String requestSearchClubUrl;
    String requestSearchUrl;

    public SearchAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 搜索用户
     * @param word
     * @param requestCallback
     */
    public void searchUser(final String word , final RequestCallback requestCallback) {
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("word", word);
        requestSearchUserUrl = getHost() + ApiConstants.URL_USER_FINDFRIEND + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestSearchUserUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestSearchUserUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                    } else {
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
        signRequest.setTag(requestSearchUserUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 搜索俱乐部
     * @param searchType 俱乐部搜索类型
     * @param content 俱乐部搜索内容
     * @param requestCallback
     */
    public void searchTeam(int searchType , String content , final RequestCallback requestCallback) {
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        if (searchType == ClubSearchActivity.SEARCH_TYPE_ARE) {
            paramsMap.put("area_id", content);
        } else if (searchType == ClubSearchActivity.SEARCH_TYPE_TEAM_WORD) {
            paramsMap.put("word", content);
        }
        paramsMap.put("type", String.valueOf(searchType));
        requestSearchClubUrl = getHost() + ApiConstants.URL_TEAM_SEARCH + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestSearchClubUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestSearchClubUrl, new Response.Listener<String>() {
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
                    } else {
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
        signRequest.setTag(requestSearchClubUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 高级搜索
     * @param word 关键字
     * @param requestCallback
     */
    public void searchKey(final String word ,final RequestCallback requestCallback) {
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), true);
//        final HashMap<String, String> paramsMap = new HashMap<String, String>();
//        paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("word", word);
//        paramsMap.put("type", "0"); 传0表示搜索用户，传1表示搜索俱乐部
        requestSearchUrl = getHost() + ApiConstants.URL_SEARCH + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestSearchUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestSearchUrl, new Response.Listener<String>() {
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
                    } else {
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
                DialogMaker.dismissProgressDialog();
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
        signRequest.setTag(requestSearchUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestBanner;
    public void fetchBanner(final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        requestBanner = getHost() + ApiConstants.URL_INDEX_BANNER + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestBanner);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestBanner, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                    }
                } catch (JSONException e) {
                    if (callback != null) {
                        callback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null) {
                    callback.onFailed(-1, null);
                }
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestBanner);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestTeamByVid;
    public void searchTeamByVid(String vid, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("vid", vid);
        requestTeamByVid = getHost() + ApiConstants.URL_TEAM_SEARCH_BY_VID + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestTeamByVid);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestTeamByVid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);//{"code":0,"message":"ok","data":[{"tid":"56561256","vid":"99057"}]}
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                    }
                } catch (JSONException e) {
                    if (callback != null) {
                        callback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
                DialogMaker.dismissProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestTeamByVid);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestSearchUserUrl);
        cancelAll(requestSearchClubUrl);
        cancelAll(requestSearchUrl);
        cancelAll(requestBanner);
        cancelAll(requestTeamByVid);
    }
}
