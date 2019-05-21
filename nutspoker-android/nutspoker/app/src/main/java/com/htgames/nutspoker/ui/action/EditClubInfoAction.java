package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
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
import com.netease.nim.uikit.common.preference.UserPreferences;
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
 * 编辑俱乐部信息
 */
public class EditClubInfoAction extends BaseAction {
    private final static String TAG = "EditClubInfoAction";
    String requestUpdateUrl;
    String requestUpgradeUrl;

    public EditClubInfoAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 更新俱乐部信息
     * @param teamId 俱乐部ID
     * @param teamName 俱乐部名称
     * @param teamIntroduce 俱乐部介绍
     * @param areaId 区域ID
     * @param requestCallback
     */
    public void updateClubInfo(final String teamId , final String teamName,
                               final String teamIntroduce ,final String areaId ,
                               final String teamAvatarUrl,final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(R.string.club_edit_ing), true).setCanceledOnTouchOutside(false);
        requestUpdateUrl = getHost() + ApiConstants.URL_TEAM_UPDATE;
        LogUtil.i(TAG, requestUpdateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if(requestCallback != null){
                            requestCallback.onSuccess(json);
                        }
                        if(TextUtils.isEmpty(teamAvatarUrl)){
                            Toast.makeText(ChessApp.sAppContext, R.string.club_edit_success, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ChessApp.sAppContext, R.string.club_edit_heaa_success, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                        if (code == ApiCode.CODE_MESSAGE_SHOW) {
                            String message = ApiResultHelper.getShowMessage(json);
                            if (TextUtils.isEmpty(message)) {
                                message = ChessApp.sAppContext.getString(R.string.club_edit_failed);
                            }
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        } else {
                            if (!TextUtils.isEmpty(teamAvatarUrl)) {
                                Toast.makeText(ChessApp.sAppContext, R.string.club_edit_heaa_failed, Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                } catch (JSONException e) {
                    if (requestCallback != null) {
                        requestCallback.onFailed(-1, null);
                    }
                    if(TextUtils.isEmpty(teamAvatarUrl)){
                        Toast.makeText(ChessApp.sAppContext, R.string.club_edit_failed, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChessApp.sAppContext, R.string.club_edit_heaa_failed, Toast.LENGTH_SHORT).show();
                    }
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(-1, null);
                }
                if(TextUtils.isEmpty(teamAvatarUrl)) {
                    Toast.makeText(ChessApp.sAppContext, R.string.club_edit_failed, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ChessApp.sAppContext, R.string.club_edit_heaa_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("tid", teamId);
                if (!TextUtils.isEmpty(teamName)) {
                    paramsMap.put("tname", teamName);
                }
                if (teamIntroduce != null) {
                    paramsMap.put("intro", teamIntroduce);
                }
                if (!TextUtils.isEmpty(teamAvatarUrl)) {
                    paramsMap.put("avatar", teamAvatarUrl);
                }
                if (!TextUtils.isEmpty(areaId)) {
                    paramsMap.put("area_id", areaId);
                }
                LogUtil.i(TAG , paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 更新俱乐部开关
     * @param teamId 俱乐部ID
     * @param isPrivate 是否是私有的
     * @param isCreatorCreate 是否是只能房主开局
     * @param requestCallback
     */
    public void updateClubConfig(final String teamId ,
                               final boolean isPrivate,
                               final boolean isCreatorCreate,
                               final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            if (requestCallback != null) {
                requestCallback.onFailed();
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(R.string.club_edit_ing), true).setCanceledOnTouchOutside(false);
        requestUpdateUrl = getHost() + ApiConstants.URL_TEAM_UPDATE;
        LogUtil.i(TAG, requestUpdateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateUrl, new Response.Listener<String>() {
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
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
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
                paramsMap.put("tid", teamId);
                if (isPrivate) {
                    paramsMap.put("is_private", "1");
                } else {
                    paramsMap.put("is_private", "0");
                }
                if (isCreatorCreate) {
                    paramsMap.put("is_owner", "1");
                } else {
                    paramsMap.put("is_owner", "0");
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

//    public void doUpdateTeamInfo(String teamName, String teamIntroduce) {
//        if (teamName.equals(team.getName())) {
//            //如果名称没有变过，就不更新
//            teamName = null;
//        }
//        if (teamIntroduce.equals(team.getIntroduce())) {
//            //如果介绍没有变过，就不更新
//            teamIntroduce = null;
//        }
//        DialogMaker.showProgressDialog(this, getString(R.string.club_edit_ing), true).setCanceledOnTouchOutside(false);
//        updateTeamInfoFields(team.getId(), teamName, teamIntroduce, null, new RequestCallback<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                DialogMaker.dismissProgressDialog();
//                Toast.makeText(ChessApp.sAppContext, R.string.club_edit_success, Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_OK, new Intent());
//                finish();
//            }
//
//            @Override
//            public void onFailed(int i) {
//                DialogMaker.dismissProgressDialog();
//                Toast.makeText(ChessApp.sAppContext, R.string.club_edit_failed, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//                DialogMaker.dismissProgressDialog();
//            }
//        });
//    }

    /**
     * 提升群俱乐部成员上限
     * @param teamId 俱乐部ID
     * @param goodsId 商品ID
     * @param months 购买月份
     * @param requestCallback
     */
    public void upgradeClub(final String teamId , final int goodsId,
                            final int months ,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(R.string.club_limit_upgrade_ing), true).setCanceledOnTouchOutside(false);
        requestUpgradeUrl = getHost() + ApiConstants.URL_TEAM_UPGRADE;
        LogUtil.i(TAG, requestUpgradeUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpgradeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        JSONObject dataJSONObject = json.optJSONObject("data");
                        int diamond = dataJSONObject.optInt("diamond");//购买成功，返回宝石
                        UserPreferences.getInstance(ChessApp.sAppContext).setDiamond(diamond);
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
//                        Toast.makeText(ChessApp.sAppContext, R.string.club_limit_upgrade_success, Toast.LENGTH_SHORT).show();
                    } else if(code == ApiCode.CODE_BALANCE_INSUFFICIENT){
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if(code == ApiCode.CODE_SHOP_GOODS_BUY_FAILURE){
//                        Toast.makeText(ChessApp.sAppContext, R.string.club_limit_upgrade_failure, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else {
//                        Toast.makeText(ChessApp.sAppContext, R.string.club_limit_upgrade_failure, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
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
                if(error != null && !TextUtils.isEmpty(error.getMessage())){
                    LogUtil.i(TAG , error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
                DialogMaker.dismissProgressDialog();
//                Toast.makeText(ChessApp.sAppContext, R.string.club_limit_upgrade_failure, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("tid", teamId);
                paramsMap.put("goodsid", String.valueOf(goodsId));
                paramsMap.put("months", String.valueOf(months));
                LogUtil.i(TAG , paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpgradeUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestUpdateUrl);
        cancelAll(requestUpgradeUrl);
    }
}
