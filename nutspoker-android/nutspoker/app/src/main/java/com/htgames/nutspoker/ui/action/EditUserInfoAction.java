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
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.uinfo.constant.GenderEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 更新个人信息动作类
 */
public class EditUserInfoAction extends BaseAction {
    private final static String TAG = "EditUserInfoAction";
    String requestUrl;

    public EditUserInfoAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     * 更新用户数据
     * @param nickname 昵称
     * @param avatar 头像
     * @param gender 性别
     * @param sign 个性签名
     * @param areaId 区域
     * @param requestCallback 回调
     */
    public void updateUserInfo(final String nickname, final String avatar, final GenderEnum gender, final String sign ,final String areaId ,final GameRequestCallback requestCallback) {
        if (!NetworkUtil.isNetAvailable(mActivity)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(mActivity, getString(com.netease.nim.uikit.R.string.empty), false);
        requestUrl = getHost() + ApiConstants.URL_USER_RESETUINFO;
        LogUtil.i(TAG, requestUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
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
//                        if(!TextUtils.isEmpty(nickname)){
//                            UserPreferences.getInstance(ChessApp.sAppContext).setNickName(nickname);//设置昵称，用于判断该用户是否设置过昵称
//                        }
                        if(TextUtils.isEmpty(avatar)){
                            //不是设置头像，提示
                            Toast.makeText(ChessApp.sAppContext, R.string.user_info_update_success, android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if(requestCallback != null){
                            requestCallback.onFailed(code, json);
                        }
                        if(code == ApiCode.CODE_NICKNAME_FORMAT_WRONG){
                            Toast.makeText(ChessApp.sAppContext, R.string.nickname_format_wrong, Toast.LENGTH_SHORT).show();
                        } else if(code == ApiCode.CODE_USER_NICKNAME_LONG) {
                            Toast.makeText(ChessApp.sAppContext, R.string.nickname_is_long, Toast.LENGTH_SHORT).show();
                        } else if (code == ApiCode.CODE_NICKNAME_EXISTED) {//昵称已经被占用
                        } else {
                            if(TextUtils.isEmpty(avatar)){
                                Toast.makeText(ChessApp.sAppContext, R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(requestCallback != null){
                        requestCallback.onFailed(-1, null);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                if(requestCallback != null){
                    requestCallback.onFailed(-1, null);
                }
                if(TextUtils.isEmpty(avatar)){
                    Toast.makeText(ChessApp.sAppContext, R.string.user_info_update_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> paramsMap = new HashMap<String, String>();
//                paramsMap.put("uid", UserPreferences.getInstance(ChessApp.sAppContext).getUserId());
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                if(!StringUtil.isSpace(nickname)) {
                    paramsMap.put("nickname", nickname);
                }
                if (gender == GenderEnum.MALE) {
                    paramsMap.put("gender", String.valueOf(GenderEnum.MALE.getValue()));
                }  else if (gender == GenderEnum.FEMALE) {
                    paramsMap.put("gender", String.valueOf(GenderEnum.FEMALE.getValue()));
                }
                if (!TextUtils.isEmpty(avatar)) {
                    paramsMap.put("avatar", avatar);
                }
                if(sign != null){
                    paramsMap.put("sign", sign);
                }
                if(areaId != null){
                    paramsMap.put("area_id", areaId);
                }
                LogUtil.i(TAG, paramsMap.toString());
                return paramsMap;
            }
        };
        signRequest.setTag(requestUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    String requestUserAlias = "";
    public void clubUserAlias(final String member_uid, final String name, final GameRequestCallback mGameRequestCallback) {//俱乐部管理员给其他成员设置备注
        if (!checkNetWork()) {
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(ApiCode.CODE_NETWORD_ERROR, null);
            }
            return;
        }
        DialogMaker.showProgressDialog(mActivity, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("name", name);
        paramsMap.put("member_uid", member_uid);
        requestUserAlias = getHost() + ApiConstants.URL_TEAM_USER_ALIAS + NetWork.getRequestParams(paramsMap);
        LogUtil.i(TAG, requestUserAlias);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestUserAlias, new Response.Listener<String>() {
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
        signRequest.setTag(requestUserAlias);
        ChessApp.sRequestQueue.add(signRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAll(requestUrl);
        cancelAll(requestUserAlias);
    }
}
