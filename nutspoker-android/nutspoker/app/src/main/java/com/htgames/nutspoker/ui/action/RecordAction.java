package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;
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
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 数据动作
 */
public class RecordAction extends BaseAction {
    private final static String TAG = "RecordAction";

    public static final int RecordNormal_TypeDay = 0;
    public static final int RecordNormal_TypeMonth = 1;

    public static final int RecordDF_TypeAll = 0;
    public static final int RecordDF_TypeWeek = 1;
    public static final int RecordDF_TypeMonth = 2;

    public RecordAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 获取战绩
     * @param requestCallback
     */
    public void getRecordData(RequestCallback requestCallback) {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastGetTime = RequestTimeLimit.lastGetStatisticalTime;
        if ((currentTime - lastGetTime) < RequestTimeLimit.GET_STATISTICAL_TIME_LIMIT) {
            LogUtil.i(TAG, "获取数据时间未到");
            return;
        }

        HashMap<String, String> paramsMap = getRequestCommonMap();;
        addRequestGet(getHost() + ApiConstants.URL_GAME_GAMERECORD,paramsMap,requestCallback);
    }


    /**
     * 获取数据分析
     */
    public void getAnalysisData(RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();;
        addRequestGet(getHost() + ApiConstants.URL_GAME_GAMEDATA,paramsMap,requestCallback);
    }

    /**
     * 获取游戏战绩列表接口
     * @param last_gid
     * @param requestCallback
     */
    public void getRecordList(String last_gid , RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();;
        if (!TextUtils.isEmpty(last_gid)) {
            paramsMap.put("last_gid", last_gid);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_MY_RECORDLIST,paramsMap,requestCallback);
    }

    /**
     * 获取战绩的gids列表
     * @param last_gid
     * @param requestCallback
     */
    public void getRecordListGids(String last_gid, String tid, String horde_id, String name, RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if (!TextUtils.isEmpty(last_gid)) {
            paramsMap.put("last_gid", last_gid);
        }
        if (!StringUtil.isSpaceOrZero(tid)) {
            paramsMap.put("tid", tid);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        if (!StringUtil.isSpaceOrZero(name)) {
            paramsMap.put("name", name);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_MY_RECORDLIST_GID, paramsMap,requestCallback);
    }

    public void getRecordListGidsTeam(String last_gid, String tid, String horde_id, String name, RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if (!TextUtils.isEmpty(last_gid)) {
            paramsMap.put("last_gid", last_gid);
        }
        if (!StringUtil.isSpaceOrZero(tid)) {
            paramsMap.put("tid", tid);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        if (!StringUtil.isSpaceOrZero(name)) {
            paramsMap.put("name", name);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_MY_RECORDLIST_GID_TEAM, paramsMap,requestCallback);
    }

    public void getRecordListGidsHorde(String last_gid, String tid, String horde_id, String name, RequestCallback requestCallback) {
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if (!TextUtils.isEmpty(last_gid)) {
            paramsMap.put("last_gid", last_gid);
        }
        if (!StringUtil.isSpaceOrZero(tid)) {
            paramsMap.put("tid", tid);
        }
        if (!StringUtil.isSpaceOrZero(horde_id)) {
            paramsMap.put("horde_id", horde_id);
        }
        if (!StringUtil.isSpaceOrZero(name)) {
            paramsMap.put("name", name);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_MY_RECORDLIST_GID_HORDE, paramsMap,requestCallback);
    }

    public void getRecordListByGid(String gids, RequestCallback requestCallback) {//通过gid查询具体的战绩
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if (!TextUtils.isEmpty(gids)) {
            paramsMap.put("gids", gids);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_MY_RECORDLIST_BY_GID, paramsMap,requestCallback);
    }

    /**
     * 获取游戏战绩列表接口
     * @param last_gid
     * @param requestCallback
     */
    String requestRecordListForTeam;
    public void getRecordListForTeam(String tid, String last_gid, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("last_gid", last_gid);
        requestRecordListForTeam = getHost() + ApiConstants.URL_RECORD_LIST_TEAM + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestRecordListForTeam);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestRecordListForTeam, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response.substring(0, response.length() / 2));
                LogUtil.i(TAG, response.substring(response.length() / 2, response.length()));//data太长，log显示不全，分开显示
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
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestRecordListForTeam);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestRecordListForHorde;
    public void getRecordListForHorde(String tid, String last_gid, String horde_id, final GameRequestCallback callback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onFailed(-1, null);
            }
            return;
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams();
        paramsMap.put("tid", tid);
        paramsMap.put("last_gid", last_gid);
        paramsMap.put("horde_id", horde_id);
        requestRecordListForHorde = getHost() + ApiConstants.URL_RECORD_LIST_HORDE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestRecordListForHorde);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.GET, requestRecordListForHorde, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response.substring(0, response.length() / 2));
                LogUtil.i(TAG, response.substring(response.length() / 2, response.length()));//data太长，log显示不全，分开显示
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
                LogUtil.i(TAG, error == null ? "VolleyError==null" : error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestRecordListForHorde);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 获取牌局成员列表
     * @param gid
     * @param last_ranking
     * @param requestCallback
     */
    public void getRecordMemberList(final String gid , final String last_ranking , final RequestCallback requestCallback) {
        final HashMap<String, String> paramsMap = getRequestCommonMap();;
        paramsMap.put("gid", gid);
        if (!TextUtils.isEmpty(last_ranking)) {
            paramsMap.put("last_ranking", last_ranking);
        }
        addRequestGet(getHost() + ApiConstants.URL_GAME_RECORD_MEMBERS,paramsMap,requestCallback);
    }

    /**
     * 获取游戏战绩详情接口
     * @param gid
     * @param requestCallback
     */
    public void getRecordDetail(final String gid , final RequestCallback requestCallback) {
        DialogMaker.showProgressDialog(mActivity, getString(R.string.get_ing), false).setCanceledOnTouchOutside(false);
        final HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("gid", String.valueOf(gid));
        addRequestGet(getHost() + ApiConstants.URL_GAME,paramsMap,requestCallback);
    }

    /***
     * 战绩首页请求数据
     * @param requestCallback
     */
    public void getRecordIndexData(final RequestCallback requestCallback){
        final HashMap<String, String> paramsMap = getRequestCommonMap();
        addRequestGet(getHost() + ApiConstants.URL_RECORD_INDEX, paramsMap, requestCallback);
    }

    /**
     * 普通战绩详情 get uid
     * @param type 0日 1月
     * @param requestCallback
     */
    public void getRecordNormalData(int type,final RequestCallback requestCallback){
        final HashMap<String, String> paramsMap = getRequestCommonMap();;
        paramsMap.put("type", "" + type);
        addRequestGet(getHost() + ApiConstants.URL_RECORD_NORMAL, paramsMap, requestCallback);
    }

    /**
     * 比赛战绩数据（sng，mt_sng，mtt)
     * @param requestCallback
     */
    public void getRecordMatchData(final RequestCallback requestCallback){
        final HashMap<String, String> paramsMap = getRequestCommonMap();;
        addRequestGet(getHost() + ApiConstants.URL_RECORD_MATCH, paramsMap, requestCallback);
    }

    /**
     * 数据统计
     * @param type 0全部，1近七天，2近一月
     * @param requestCallback
     */
    public void getRecordDataFind(int type,final RequestCallback requestCallback){
        final HashMap<String, String> paramsMap = getRequestCommonMap();;
        paramsMap.put("type", "" + type);
        addRequestGet(getHost() + ApiConstants.URL_NORMAL_DATA_FIND, paramsMap, requestCallback);
    }

    /**
     * 普通战绩详情-> 保险数据
     * @param requestCallback
     */
    public void getRecordInsuranceData(final RequestCallback requestCallback){
        final HashMap<String, String> paramsMap = getRequestCommonMap();;
        addRequestGet(getHost() + ApiConstants.URL_INSURANCE_DATA_FIND, paramsMap, requestCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestRecordListForTeam);
        cancelAll(requestRecordListForHorde);
    }
}
