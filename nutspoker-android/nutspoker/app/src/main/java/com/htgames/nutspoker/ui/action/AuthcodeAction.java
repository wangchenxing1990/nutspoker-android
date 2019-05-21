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
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.api.YunpianCode;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import static com.netease.nim.uikit.api.HostManager.getHost;
/**
 * 验证码功能
 */
public class AuthcodeAction extends BaseAction {
    private final static String TAG = "AuthcodeAction";
    String requestAuthcodeUrl;

    public AuthcodeAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 获取验证码
     * @param phone 用户帐号(手机号)
     * @param from 来源
     * @param showDialog 是否显示Dialog
     * @param validatecode  验证码
     * @param countryCode  手机号区域码
     */
    public void getAuthCode(final String phone, final int from, boolean showDialog ,
                            final String validatecode , final String countryCode,
                            final boolean isVoiceMode, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        if (showDialog) {
            DialogMaker.showProgressDialog(mActivity, getString(R.string.authcode_get_ing), false).setCanceledOnTouchOutside(false);
        }
        final HashMap<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("username", phone);
        paramsMap.put("os", ApiConstants.OS_ANDROID);
        if (from == LoginActivity.FROM_REGISTER) {
            paramsMap.put("action", String.valueOf(ApiConstants.AUTHCODE_TYPE_REGISTER));
        } else {
            paramsMap.put("action", String.valueOf(ApiConstants.AUTHCODE_TYPE_FORGETPASSWORD));
        }
        if(isVoiceMode){
            paramsMap.put("type", "1");
        }
        paramsMap.put("ccode", countryCode);
        int method = Request.Method.GET;
        if(!TextUtils.isEmpty(validatecode)){
            paramsMap.put("validatecode", validatecode);
            method = Request.Method.POST;
            requestAuthcodeUrl = getHost() + ApiConstants.URL_VALIDATE;
        } else{
            requestAuthcodeUrl = getHost() + ApiConstants.URL_VALIDATE + NetWork.getRequestParams(paramsMap);
        }
        LogUtil.i(TAG, requestAuthcodeUrl);
        SignStringRequest signRequest = new SignStringRequest(method, requestAuthcodeUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DialogMaker.dismissProgressDialog();
                        LogUtil.i(TAG, response);
                        try {
                            JSONObject json = new JSONObject(response);
                            int code = json.getInt("code");
                            JSONObject dataJson = json.optJSONObject("data");
                            if (code == 0) {
//                        if(TextUtils.isEmpty(validatecode)){
//                            Toast.makeText(ChessApp.sAppContext, R.string.authcode_get_success, Toast.LENGTH_SHORT).show();
//                        }
                                if (requestCallback != null) {
                                    requestCallback.onResult(code, response, null);
                                }
                            } else if (code == ApiCode.CODE_ACCOUNT_NONE_EXISTENT) {
                                if (TextUtils.isEmpty(validatecode)) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.forget_phone_unregistered, Toast.LENGTH_SHORT).show();
                                }
                                if (requestCallback != null) {
                                    requestCallback.onFailed();
                                }
                            } else if (code == ApiCode.CODE_ACCOUNT_EXIST) {
                                if (TextUtils.isEmpty(validatecode)) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.register_phone_registered, Toast.LENGTH_SHORT).show();
                                }
                                if (requestCallback != null) {
                                    requestCallback.onFailed();
                                }
                            } else if (code == ApiCode.CODE_AUTHCODE_ERROR) {
                                if (TextUtils.isEmpty(validatecode)) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.authcode_get_failed, Toast.LENGTH_SHORT).show();
                                    if (requestCallback != null) {
                                        requestCallback.onFailed();
                                    }
                                } else {
                                    if (requestCallback != null) {
                                        requestCallback.onResult(code, response, null);
                                    }
                                }
                            } else if (code == ApiCode.CODE_ILLEGAL_REQUEST) {
                                if (requestCallback != null) {
                                    requestCallback.onResult(code, response, null);
                                }
                            } else if (code == ApiCode.CODE_NETEASE_ERROR) {//获取验证码频率过快的code也是这个值9999
                                if (requestCallback != null) {
                                    requestCallback.onFailed();
                                }
                                LogUtil.i(TAG, "云片错误");
                                int yunpianCode = dataJson.optInt(YunpianCode.KEY_CODE);
                                if (yunpianCode == YunpianCode.CODE_PHONE_INVALID) {
                                    LogUtil.i(TAG, "手机号码无效");
                                    Toast.makeText(ChessApp.sAppContext, getString(R.string.login_phone_invalid), Toast.LENGTH_SHORT).show();
                                } else if (yunpianCode == YunpianCode.CODE_AUTHCODE_HOUR_LIMIT) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.authcode_voice_hour_limit, Toast.LENGTH_SHORT).show();
                                } else if (yunpianCode == YunpianCode.CODE_AUTHCODE_DAY_LIMIT) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.authcode_voice_day_limit, Toast.LENGTH_SHORT).show();
                                } else if (yunpianCode == YunpianCode.CODE_AUTHCODE_HOUR_TOO_QUICK) {
//                                    Toast.makeText(ChessApp.sAppContext, "验证码获取频率过快，请稍后再试", Toast.LENGTH_SHORT).show();如果频率过快，也跳到验证页面，用上次的验证码验证
                                    if (requestCallback != null) {
                                        requestCallback.onResult(yunpianCode, response, null);//传入yunpianCode而不是code
                                    }
                                } else {
                                    Toast.makeText(ChessApp.sAppContext, R.string.authcode_get_failed, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (TextUtils.isEmpty(validatecode)) {
                                    String message = ApiResultHelper.getShowMessage(json);
                                    if (TextUtils.isEmpty(message)) {
                                        message = ChessApp.sAppContext.getString(R.string.authcode_get_failed);
                                    }
                                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                                }
                                if (requestCallback != null) {
                                    requestCallback.onFailed();
                                }
                            }
                        } catch (Exception e) {
                            if (TextUtils.isEmpty(validatecode)) {
                            } else {
                            }
                            if (requestCallback != null) {
                                requestCallback.onFailed();
                            }
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.authcode_get_failed, Toast.LENGTH_SHORT).show();
                        if (TextUtils.isEmpty(validatecode)) {
                        } else {
                        }
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                        if (!TextUtils.isEmpty(error.getMessage())) {
                            LogUtil.i(TAG, error.getMessage());
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestAuthcodeUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestAuthcodeUrl);
    }
}
