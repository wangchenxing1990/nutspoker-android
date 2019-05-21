package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.text.TextUtils;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.api.NetWork;
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
import com.htgames.nutspoker.interfaces.RequestCallback;
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
 * 登录动作
 */
public class LoginAction extends BaseAction{
    public final static String TAG = "LoginAction";
    public String requestLoginUrl = "";
    public String requestLoginFacebookUrl = "";
    public String requestRegisterUrl = "";
    public String requestResetPwdUrl = "";

    public LoginAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 通过登录自己服务器
     * @param countryCode
     * @param username
     * @param password
     */
    public void doLoginByHost(final String countryCode ,final String username, final String password, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        requestLoginUrl = getHost() + ApiConstants.URL_LOGIN;
        LogUtil.i(TAG, requestLoginUrl);
        DialogMaker.showProgressDialog(mActivity, getString(R.string.login_ing), false).setCanceledOnTouchOutside(false);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLoginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_ACCOUNT_PASSWORD_INCORRECT || code == ApiCode.CODE_ACCOUNT_PASSWORD_IS_SHORT) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.login_failed_error, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else if (code == ApiCode.CODE_ACCOUNT_NONE_EXISTENT) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.login_failed_account_not_exist, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else if (code == ApiCode.CODE_ACCOUNT_CLOSED) {//账号被封
                        showAccountClosedDialog();
                    } else {
                        DialogMaker.dismissProgressDialog();
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.login_failed);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(ChessApp.sAppContext, e == null ? "json解析异常" : e.toString(), Toast.LENGTH_SHORT).show();
                    DialogMaker.dismissProgressDialog();
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.login_failed, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.e(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("os", ApiConstants.OS_ANDROID);
                paramsMap.put("ccode", countryCode);
                paramsMap.put("username", username);
                paramsMap.put("password", password);
                return paramsMap;
            }
        };
        signRequest.setTag(requestLoginUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    EasyAlertDialog dialog;
    private void showAccountClosedDialog() {
        if (dialog == null) {
            dialog = EasyAlertDialogHelper.createOkCancelDiolag(mActivity, "", "您的账号存在违规情况，已作“冻结”账号处理，如有疑问，请与官方微信客服联系：soully0001", false, new EasyAlertDialogHelper.OnDialogActionListener() {
                @Override
                public void doCancelAction() {
                }

                @Override
                public void doOkAction() {
                }
            });
        }
        if (mActivity != null && !mActivity.isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 通过FB登录自己服务器
     * @param fbToken
     * @param fbUid
     */
    public void doLoginFacebookByHost(final String fbToken, final String fbUid, final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        requestLoginFacebookUrl = getHost() + ApiConstants.URL_LOGIN_FACEBOOK;
        LogUtil.i(TAG, requestLoginFacebookUrl);
        DialogMaker.showProgressDialog(mActivity, getString(R.string.login_ing), false).setCanceledOnTouchOutside(false);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLoginFacebookUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_ACCOUNT_PASSWORD_INCORRECT || code == ApiCode.CODE_ACCOUNT_PASSWORD_IS_SHORT) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.login_failed_error, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else if (code == ApiCode.CODE_ACCOUNT_NONE_EXISTENT) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.login_failed_account_not_exist, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    } else {
                        DialogMaker.dismissProgressDialog();
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.login_failed);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    if (requestCallback != null) {
                        requestCallback.onFailed();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.login_failed, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext , false);
                paramsMap.put("account_token", fbToken);
                paramsMap.put("account_id", fbUid);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestLoginFacebookUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 注册
     * @param phone
     * @param password
     * @param nickname
     * @param regionId
     * @param validatecode 验证码
     * @param invitationCode 邀请码
     * @param requestCallback
     */
    public void register(final String phone, final String password , final String nickname,
                         final String regionId , final String validatecode, final String countryCode,
                         final String invitationCode,final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        requestRegisterUrl = getHost() + ApiConstants.URL_SIGN;
        LogUtil.i(TAG, requestRegisterUrl);
        DialogMaker.showProgressDialog(mActivity, getString(R.string.register_ing), false);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestRegisterUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        Toast.makeText(ChessApp.sAppContext, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onResult(code, response, null);
                        }
                    } else if (code == ApiCode.CODE_NICKNAME_FORMAT_WRONG) {
                        Toast.makeText(ChessApp.sAppContext, R.string.nickname_format_wrong, Toast.LENGTH_SHORT).show();
                    } else if (code == ApiCode.CODE_USER_NICKNAME_LONG) {
                        Toast.makeText(ChessApp.sAppContext, R.string.nickname_is_long, Toast.LENGTH_SHORT).show();
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.register_failed);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                        if (requestCallback != null) {
                            requestCallback.onFailed();
                        }
                    }
                } catch (JSONException e) {
                    DialogMaker.dismissProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(ChessApp.sAppContext, getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.register_failed, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if (requestCallback != null) {
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("os", ApiConstants.OS_ANDROID);
                paramsMap.put("username", phone);
                paramsMap.put("password", password);
                paramsMap.put("area_id", regionId);
                paramsMap.put("nickname", nickname);
                paramsMap.put("validatecode", validatecode);
                if (!TextUtils.isEmpty(invitationCode)) {
                    paramsMap.put("invitation_code", invitationCode);
                }
                paramsMap.put("ccode", countryCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestRegisterUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    /**
     * 重置密码
     * @param phone
     * @param password
     * @param validatecode
     * @param countryCode
     * @param requestCallback
     */
    public void resetPassword(final String phone, final String password ,
                              final String validatecode , final String countryCode,
                              final RequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        requestResetPwdUrl = getHost() + ApiConstants.URL_RESETPASSWORD;
        LogUtil.i(TAG, requestResetPwdUrl);
        DialogMaker.showProgressDialog(mActivity, getString(R.string.reset_password_ing), false);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestResetPwdUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, response);
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        Toast.makeText(ChessApp.sAppContext, R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                        if(requestCallback != null){
                            requestCallback.onResult(code , response , null);
                        }
                    } else {
                        String message = ApiResultHelper.getShowMessage(json);
                        if (TextUtils.isEmpty(message)) {
                            message = ChessApp.sAppContext.getString(R.string.reset_password_failed);
                        }
                        Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ChessApp.sAppContext, R.string.reset_password_failed, Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(error.getMessage())) {
                    LogUtil.i(TAG, error.getMessage());
                }
                if(requestCallback != null){
                    requestCallback.onFailed();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("os", ApiConstants.OS_ANDROID);
                paramsMap.put("username", phone);
                paramsMap.put("password", password);
                paramsMap.put("validatecode", validatecode);
                paramsMap.put("ccode", countryCode);
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestResetPwdUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChessApp.sRequestQueue.cancelAll(requestLoginUrl);
        ChessApp.sRequestQueue.cancelAll(requestRegisterUrl);
        ChessApp.sRequestQueue.cancelAll(requestResetPwdUrl);
        ChessApp.sRequestQueue.cancelAll(requestLoginFacebookUrl);
    }
}
