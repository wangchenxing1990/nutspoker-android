package com.htgames.nutspoker.chat.app_msg;

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
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
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
 */
public class BuyChipsAction extends BaseAction{
    private final static String TAG = "BuyChipsAction";
    private String requestControlInUrl;

    public BuyChipsAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    public void controlBuyIn(final AppMessage appMessage, final int action , final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestControlInUrl = getHost() + ApiConstants.URL_GAME_CONTROLIN;
        LogUtil.i(TAG, requestControlInUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestControlInUrl, new Response.Listener<String>() {
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
                        if (code == ApiCode.CODE_MATCH_CHECKIN_CONTROL_COIN_NOT_SUFFICIENT || code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                            showInsufficientBalanceDialog();
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed(code, json);
                        }
                        String failMsg = ApiCode.SwitchCode(code, response);
                        Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String failMsg = ApiCode.SwitchCode(ApiCode.CODE_JSON_ERROR, null);
                    Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.e(TAG, error.getMessage());
                }
                DialogMaker.dismissProgressDialog();
                if(requestCallback != null) {
                    requestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
                }
                String failMsg = ApiCode.SwitchCode(ApiCode.CODE_NETWORD_ERROR, null);
                Toast.makeText(ChessApp.sAppContext, failMsg, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("targetid", appMessage.targetId);
                paramsMap.put("fromid", appMessage.fromId);
                paramsMap.put("status", String.valueOf(action));
                if(appMessage.attachObject instanceof BuyChipsNotify) {
                    final BuyChipsNotify buyChipsNotify = (BuyChipsNotify) appMessage.attachObject;
                    paramsMap.put("starttime", String.valueOf(buyChipsNotify.buyStarttime));
                    paramsMap.put("timeout", String.valueOf(buyChipsNotify.buyTimeout));
                    paramsMap.put("code", buyChipsNotify.gameCode);
                    paramsMap.put("nodename", buyChipsNotify.nodename);
                }else if(appMessage.attachObject instanceof MatchBuyChipsNotify) {
                    final MatchBuyChipsNotify buyChipsNotify = (MatchBuyChipsNotify) appMessage.attachObject;
                    paramsMap.put("starttime", String.valueOf(buyChipsNotify.buyStarttime));
                    paramsMap.put("timeout", String.valueOf(buyChipsNotify.buyTimeout));
                    paramsMap.put("code", buyChipsNotify.gameCode);
                    paramsMap.put("nodename", buyChipsNotify.nodename);
                    paramsMap.put("type", "1");//代表重构或者增购
                }
                return paramsMap;
            }
        };
        signRequest.setTag(requestControlInUrl);
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
        cancelAll(requestControlInUrl);
    }
}
