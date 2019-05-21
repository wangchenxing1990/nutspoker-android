package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
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
import com.htgames.nutspoker.interfaces.GameRequestCallback;
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
 */
public class GameMatchAction extends BaseAction {
    private final static String TAG = GameMatchAction.class.getSimpleName();
    public final static int ACTION_AGREE = 1;//同意
    public final static int ACTION_REJECT = 2;//拒绝
    public final static int TYPE_PAUSE = 1;//暂停
    public final static int TYPE_START = 0;//恢复
    //    String requestMatchStatusUrl;
    String requestCheckInUrl;
    String requestCancelCheckInUrl;
    String requestDismissUrl;
    String requestJoinMttWaitUrl;
    String requestControlCheckInUrl;
    String requestUpdateControlUrl;
    String requestPauseUrl;

    public GameMatchAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 登记参赛
     *
     * @param code
     * @param requestCallback
     */
    public void checkInMatch(final String code, final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestCheckInUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_CHECKIN;
        LogUtil.i(TAG, requestCheckInUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCheckInUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
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
                Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", code);
                return paramsMap;
            }
        };
        signRequest.setTag(requestCheckInUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 取消报名
     *
     * @param code
     * @param requestCallback
     */
    public void cancelCheckInMatch(final String code, final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestCancelCheckInUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_CANCEL_CHECKIN;
        LogUtil.i(TAG, requestCancelCheckInUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCancelCheckInUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_cancel_success, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_cancel_failure, Toast.LENGTH_SHORT).show();
                    if (requestCallback != null) {
                        requestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_cancel_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", code);
                return paramsMap;
            }
        };
        signRequest.setTag(requestCancelCheckInUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    public void dismissMatch(final String code, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestDismissUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_DISMISS;
        LogUtil.i(TAG, requestDismissUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestDismissUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_match_dismiss_success, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_GAME_IS_BEGIN) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_match_dismiss_failure_isstart, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.game_match_dismiss_failure);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_dismiss_failure, Toast.LENGTH_SHORT).show();
                    if (requestCallback != null) {
                        requestCallback.onResult(ApiCode.CODE_JSON_ERROR, response, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                Toast.makeText(ChessApp.sAppContext, R.string.game_match_dismiss_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", code);
                return paramsMap;
            }
        };
        signRequest.setTag(requestDismissUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 群主允许通过
     *
     * @param appMessage
     * @param action          : 1是同意 。2 是拒绝
     * @param requestCallback
     */
    public void controlCheckIn(final AppMessage appMessage, final int action, final boolean isShowDialog ,final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        if (appMessage.type != AppMessageType.MatchBuyChips || !(appMessage.attachObject instanceof MatchBuyChipsNotify)) {
            return;
        }
        final MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) appMessage.attachObject;
        if(isShowDialog) {
            DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        }
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", buyChipsNotify.gameCode);
        paramsMap.put("player_id", appMessage.fromId);
        paramsMap.put("action", "" + action);
        requestControlCheckInUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_PASS_CHECKIN + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestControlCheckInUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestControlCheckInUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if (isShowDialog) {
                            if (code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_COIN_NOT_SUFFICIENT || code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                                showInsufficientBalanceDialog();
                            }
                            String failMsg = ApiCode.SwitchCode(code, response);
                            Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_JSON_ERROR, null);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (isShowDialog) {
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestControlCheckInUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 进入等待中的牌局
     *
     * @param gameCode 游戏CODE
     */
    public void doJoinWaitMttGame(final String gameCode) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext) || StringUtil.isSpace(gameCode)) {
            return;
        }
        requestJoinMttWaitUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_WAIT;
        LogUtil.i(TAG, requestJoinMttWaitUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestJoinMttWaitUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                    } else {

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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestJoinMttWaitUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 暂停牌局
     * type  1->暂停 0->恢复
     * @param gameCode 游戏CODE
     */
    public void pauseMttGame(final String gameCode , final int type , final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        requestPauseUrl = getHost() + ApiConstants.URL_GAME_MATCH_PAUSE;
        LogUtil.i(TAG, requestPauseUrl);
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestPauseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if(code == ApiCode.CODE_MATCH_PAUSE_PLAYER_NOTEXIST){
                        } else if(code == ApiCode.CODE_MATCH_PAUSE_NOT_OWNER){

                        } else if(code == ApiCode.CODE_MATCH_PAUSE_AREADY){

                        } else if(code == ApiCode.CODE_MATCH_PAUSE_GAME_NOTEXIST) {
                            Toast.makeText(ChessApp.sAppContext, R.string.match_manage_pause_game_finish, Toast.LENGTH_SHORT).show();
                        } else if(code == ApiCode.CODE_MATCH_PAUSE_NOT){

                        } else {
                            Toast.makeText(ChessApp.sAppContext, R.string.match_pause_failure, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ChessApp.sAppContext, R.string.match_pause_failure, Toast.LENGTH_SHORT).show();
                    if (requestCallback != null) {
                        requestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.match_pause_failure, Toast.LENGTH_SHORT).show();
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("code", gameCode);
                paramsMap.put("type", String.valueOf(type));
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestPauseUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 牌局控制带入
     * @param gameCode 游戏CODE
     * @param is_control 是否打开  1：打开
     */
    public void updateControl(final String gameCode, final boolean is_control ,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            if (requestCallback != null) {
                requestCallback.onFailed();
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestUpdateControlUrl = getHost() + ApiConstants.URL_GAME_MATCH_UPDATE_CONTROL;
        LogUtil.i(TAG, requestUpdateControlUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUpdateControlUrl, new Response.Listener<String>() {
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
                    } else if (code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_NOTCHANGE) {
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else if (code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_FAILURE) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_match_update_control_failure, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_match_update_control_failure, Toast.LENGTH_SHORT).show();
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
                if (error.getMessage() != null) {
                    LogUtil.i(TAG, error.getMessage());
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                if (is_control) {
                    paramsMap.put("is_control", "1");
                } else {
                    paramsMap.put("is_control", "0");
                }
                paramsMap.put("code", gameCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUpdateControlUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 复活重购
     * @param code
     * @param requestCallback
     */
    private String revivalCheckInUrl;
    public void revivalInMatch(final String code, final String uid, final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", code);
        paramsMap.put("uid", uid);
        revivalCheckInUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_REVIVAL;
        LogUtil.i(TAG, revivalCheckInUrl + NetWork.getRequestParams(paramsMap));
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, revivalCheckInUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = "复活失败";
                        }
                        if (!ChessApp.isGameIng) {
                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure, Toast.LENGTH_SHORT).show();
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(-1, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(revivalCheckInUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 同意&拒绝  复活重购
     * @param code
     * @param requestCallback
     */
    private String controlRevivalInUrl;
    public void controlReVivalIn(final AppMessage appMessage, final int action, final boolean isShowDialog ,final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        final MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) appMessage.attachObject;
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("code", buyChipsNotify.gameCode);
        paramsMap.put("channel", buyChipsNotify.channel);
        paramsMap.put("player_id", appMessage.fromId);
        paramsMap.put("action", "" + action);
        controlRevivalInUrl = getHost() + ApiConstants.URL_GAME_MATCH_MTT_CONTROL_REVIVAL;
        LogUtil.i(TAG, controlRevivalInUrl + NetWork.getRequestParams(paramsMap));
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, controlRevivalInUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onSuccess(json);
                        }
                    } else {
                        if (isShowDialog) {
                            if (code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_COIN_NOT_SUFFICIENT || code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                                showInsufficientBalanceDialog();
                            }
                            String failMsg = ApiCode.SwitchCode(code, response);
                            Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (requestCallback != null) {
                        requestCallback.onFailed(ApiCode.CODE_JSON_ERROR, null);
                    }
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_JSON_ERROR, null);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (isShowDialog) {
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
                DialogMaker.dismissProgressDialog();
                if (requestCallback != null) {
                    requestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(controlRevivalInUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    private void showInsufficientBalanceDialog() {
        if (mActivity == null) {
            return;
        }
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOneButtonDiolag(mActivity, "", "该用户余额不足", "确定", false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        if (!mActivity.isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        cancelAll(requestMatchStatusUrl);
        cancelAll(requestCheckInUrl);
        cancelAll(requestDismissUrl);
        cancelAll(requestCancelCheckInUrl);
        cancelAll(requestJoinMttWaitUrl);
        cancelAll(requestControlCheckInUrl);
        cancelAll(requestUpdateControlUrl);
        cancelAll(requestPauseUrl);
        cancelAll(revivalCheckInUrl);
        cancelAll(controlRevivalInUrl);
    }
}
