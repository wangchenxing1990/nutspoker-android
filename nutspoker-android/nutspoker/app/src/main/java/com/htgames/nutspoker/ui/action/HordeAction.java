package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 周智慧 on 17/3/20.
 */

public class HordeAction extends BaseAction {
    public static String TAG = HordeAction.class.getSimpleName();
    public HordeAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 创建部落
     */
    String requestCreateHorde;
    public void createHorde(String tid, String name, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("tid", tid);
        paramsMap.put("name", name);
        requestCreateHorde = getHost() + ApiConstants.URL_HORDE_CREATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCreateHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);//{"code":0,"message":"ok","data":{"horde_id":"4","horde_vid":3939732}}
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
                    Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreateHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestUpdateHorde;
    public void updateHorde(String horde_id, String name, final GameRequestCallback callback) {//更新部落信息
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("name", name);
        requestUpdateHorde = getHost() + ApiConstants.URL_HORDE_UPDATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestUpdateHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                            Toast.makeText(ChessApp.sAppContext, "更新成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_INVALID) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称不合法", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_TOO_LONG) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称长度超出范围", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_CREATOR) {
                            Toast.makeText(ChessApp.sAppContext, "用户不是部落创建者", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称已经存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestSetScore;
    public void setScore(String horde_id, String tid, String score, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("tid", tid);
        paramsMap.put("score", score);
        requestSetScore = getHost() + ApiConstants.URL_HORDE_SET_SCORE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestSetScore);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestSetScore, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                            Toast.makeText(ChessApp.sAppContext, "更新成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_CREATOR) {
                            Toast.makeText(ChessApp.sAppContext, "用户不是部落创建者", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_HORDE_SET_SCORE_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "修改上分失败", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_HORDE_SET_SCORE_FAILED_CONTROL_OFF) {
                            Toast.makeText(ChessApp.sAppContext, "上分开关没有打开", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, "修改上分失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestSetScore);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void updateIsScore(String horde_id, int is_score, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("is_score", is_score + "");
        requestUpdateHorde = getHost() + ApiConstants.URL_HORDE_UPDATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestUpdateHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                            Toast.makeText(ChessApp.sAppContext, "更新成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_CREATOR) {
                            Toast.makeText(ChessApp.sAppContext, "用户不是部落创建者", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void updateHordeCreateGameLimit(String horde_id, int is_control, final GameRequestCallback callback) {//更新部落信息
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("is_control", is_control + "");
        requestUpdateHorde = getHost() + ApiConstants.URL_HORDE_UPDATE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestUpdateHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int resultCode = json.getInt("code");
                    if (resultCode == 0) {
                        if(callback != null) {
                            callback.onSuccess(json);
                            Toast.makeText(ChessApp.sAppContext, "更新成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_INVALID) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称不合法", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_TOO_LONG) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称长度超出范围", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_CREATOR) {
                            Toast.makeText(ChessApp.sAppContext, "用户不是部落创建者", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落名称已经存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_SHORT).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "部落更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.create_team_failed, Toast.LENGTH_SHORT).show();
                if (callback != null) {
                    callback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordeList;
    public void hordeList(String tid, final GameRequestCallback callback) {//某个俱乐部下面的部落列表    get   uid  tid
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        requestHordeList = getHost() + ApiConstants.URL_HORDE_LIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordeList);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHordeList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);//{"code":0,"message":"ok","data":[{"id":"4","horde_vid":"3939732","name":"dgtf","ctime":"1490010504","tid":"25622697","owner":"10033","is_my":1,"horde_id":"4"}]}
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
                        if (resultCode == ApiCode.CODE_HORDE_LIST_NO_AUTHORITY) {
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
        signRequest.setTag(requestHordeList);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordeView;
    public void hordeView(String horde_id, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("horde_id", horde_id);
        requestHordeView = getHost() + ApiConstants.URL_HORDE_VIEW + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordeView);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHordeView, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);/*{"code":0,"message":"ok","data":[{"id":"4","tid":"25622697","tname":"wsx","vid":"4490316","avatar":"","is_owner":1}]}*/
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
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {//部落不存在
                        } else if (resultCode == ApiCode.CODE_HORDE_LIST_NO_AUTHORITY) {//没有管理员权限
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
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestHordeView);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestSearchHorde;
    public void searchHorde(String tid, String horde_vid, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_vid", horde_vid);
        requestSearchHorde = getHost() + ApiConstants.URL_HORDE_SEARCH + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestSearchHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestSearchHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);/*{"code":0,"message":"ok","data":[{"id":"4","tid":"25622697","tname":"wsx","vid":"4490316","avatar":"","is_owner":1}]}*/
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
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {//部落不存在
                        } else if (resultCode == ApiCode.CODE_HORDE_LIST_NO_AUTHORITY) {//没有管理员权限
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
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestSearchHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordeApply;
    public void hordeApply(String horde_id, String tid, String content, final GameRequestCallback callback) {
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("content", content);
        requestHordeApply = getHost() + ApiConstants.URL_HORDE_JOIN + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordeApply);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestHordeApply, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);/*{"code":0,"message":"ok","data":[{"id":"4","tid":"25622697","tname":"wsx","vid":"4490316","avatar":"","is_owner":1}]}*/
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
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {//部落不存在
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_CLUB_ALREADY_IN_HORDE) {//俱乐部已经在部落中
                            Toast.makeText(ChessApp.sAppContext, "俱乐部已经在部落中", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_INVALID) {
                            Toast.makeText(ChessApp.sAppContext, "内容不符合文本", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NAME_TOO_LONG) {
                            Toast.makeText(ChessApp.sAppContext, "输入内容超出长度", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_CLUB_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "俱乐部不存在", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "发送失败", Toast.LENGTH_LONG).show();
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
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, "发送失败", Toast.LENGTH_LONG).show();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestHordeApply);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestPassJoin;
    public void passHordeJoin(String tid, String horde_id, int status, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        paramsMap.put("status", status + "");
        requestPassJoin = getHost() + ApiConstants.URL_HORDE_PASS_JOIN + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestPassJoin);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestPassJoin, new Response.Listener<String>() {
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
                        if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_EXISTED) {//部落不存在
                            Toast.makeText(ChessApp.sAppContext, "部落不存在", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_CLUB_ALREADY_IN_HORDE) {//俱乐部已经在部落中
                            Toast.makeText(ChessApp.sAppContext, "俱乐部已经在部落中", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_UPDATE_HORDE_NOT_CREATOR) {
                            Toast.makeText(ChessApp.sAppContext, "只有俱乐部部长才有权限操作", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_CLUB_NOT_EXISTED) {
                            Toast.makeText(ChessApp.sAppContext, "俱乐部不存在", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_HORDE_PASSJOIN_FAILED) {
                            Toast.makeText(ChessApp.sAppContext, "操作失败", Toast.LENGTH_LONG).show();
                        } else if (resultCode == ApiCode.CODE_HORDE_PASSJOIN_EXPIRED) {
                            Toast.makeText(ChessApp.sAppContext, "消息已失效", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "操作失败", Toast.LENGTH_LONG).show();
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
                Toast.makeText(ChessApp.sAppContext, "操作失败", Toast.LENGTH_LONG).show();
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestPassJoin);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordePlaying;
    public void hordePlaying(String tid, String horde_id, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        requestHordePlaying = getHost() + ApiConstants.URL_HORDE_PLAYING + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordePlaying);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestHordePlaying, new Response.Listener<String>() {
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
        signRequest.setTag(requestHordePlaying);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestCancelHorde;
    public void hordeCancel(String horde_id, String tid, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        requestCancelHorde = getHost() + ApiConstants.URL_HORDE_CANCEL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCancelHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCancelHorde, new Response.Listener<String>() {
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
                        android.widget.Toast.makeText(mActivity, "解散部落成功", Toast.LENGTH_SHORT).show();
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_QUITE_HORDE_HAVING_GAME) {
                            Toast.makeText(mActivity, "还有牌局进行中，不能解散", Toast.LENGTH_SHORT).show();
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
        signRequest.setTag(requestCancelHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordeQuit;//主动退出部落
    public void hordeQuit(String tid, String horde_id, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        requestHordeQuit = getHost() + ApiConstants.URL_HORDE_QUIT + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordeQuit);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestHordeQuit, new Response.Listener<String>() {
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
                        Toast.makeText(mActivity, "退出部落成功", Toast.LENGTH_SHORT).show();
                    } else {
                        if (callback != null) {
                            callback.onFailed(resultCode, json);
                        }
                        if (resultCode == ApiCode.CODE_QUITE_HORDE_HAVING_GAME) {
                            Toast.makeText(mActivity, "还有牌局进行中，不能退出", Toast.LENGTH_SHORT).show();
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
        signRequest.setTag(requestHordeQuit);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestHordeKill;//将俱乐部移出部落
    public void hordeKill(String tid, String horde_id, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("horde_id", horde_id);
        requestHordeKill = getHost() + ApiConstants.URL_HORDE_KILL + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestHordeKill);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestHordeKill, new Response.Listener<String>() {
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
                        android.widget.Toast.makeText(mActivity, "移除俱乐部成功", Toast.LENGTH_SHORT).show();
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
        signRequest.setTag(requestHordeKill);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestCostList;//创建牌局的时候共享部落的时候配置信息
    public void getCostList(String tid, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        requestCostList = getHost() + ApiConstants.URL_COST_LIST + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestCostList);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestCostList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);//{"code":0,"message":"ok","data":[{"id":"4","name":"\u4f60","horde_id":"4","money":20},{"id":"3","name":"\u6211","horde_id":"3","money":30}]}
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
        signRequest.setTag(requestCostList);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestCreateHorde);
        cancelAll(requestUpdateHorde);
        cancelAll(requestHordeList);
        cancelAll(requestHordeView);
        cancelAll(requestSearchHorde);
        cancelAll(requestHordeApply);
        cancelAll(requestPassJoin);
        cancelAll(requestHordePlaying);
        cancelAll(requestCancelHorde);
        cancelAll(requestHordeQuit);
        cancelAll(requestHordeKill);
        cancelAll(requestCostList);
        cancelAll(requestSetScore);
    }
}
