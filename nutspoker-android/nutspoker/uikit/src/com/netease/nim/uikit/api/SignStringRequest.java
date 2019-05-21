package com.netease.nim.uikit.api;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.bean.AppVersionEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密Volley请求
 */
public class SignStringRequest extends StringRequest {
    private final static String TAG = "SignStringRequest";
    public final static String HEADER_KEY_SIGN = "sn";
    public final static String HEADER_KEY_RAND = "ds";
    public final static String HEADER_KEY_TIME = "tm";
    public final static String HEADER_KEY_BODY = "by";
    public final static String HEADER_KEY_APPVER = "ver";
    public final static String HEADER_KEY_AREA = "area";
    public static final int DEFAULT_TIMEOUT_MS = 8000;

    public SignStringRequest(int method, final String url, final Response.Listener<String> listener, final Response.ErrorListener errorListener) {
        super(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (listener != null) {
                    listener.onResponse(response);
                }
                LogUtil.i(TAG, "SignStringRequest请求 onResponse : " + url);
                LogUtil.i(TAG, "SignStringRequest请求 onResponse : " + response);
                if (!StringUtil.isSpace(url) && url.contains(HostManager.mainHost)) {
                    HostManager.resetMainHostWeight();//主host调用成功一次后将其权重置为0
                }
                //下面的是  强制更新app的code
                if (CacheConstant.isMonopolyDialogShow) {
                    return;
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_MONOPOLY_UPDATE && !url.contains(ApiConstants.URL_APP_UPGRADE) /*url.contains("user/amount")*/) {
                        CacheConstant.isMonopolyDialogShow = true;
                        final AppVersionEntity appVersionEntity = new AppVersionEntity();
                        appVersionEntity.isMandatory = true;
                        appVersionEntity.content = "版本过低";
                        appVersionEntity.downloadUrl = "https://api.everpoker.win/index/download";
                        String canleStr = "";
                        if (appVersionEntity.isMandatory) {
                            //需要强制更新
                            canleStr = CacheConstant.GetString(R.string.exit);
                        } else {
                            canleStr = CacheConstant.GetString(R.string.update_not);
                        }
                        final EasyAlertDialog mVersionDialog = EasyAlertDialogHelper.createOkCancelDiolag(CacheConstant.mTopActivity,
                                null, CacheConstant.GetString(R.string.app_update_title), CacheConstant.GetString(R.string.update), canleStr, false, new EasyAlertDialogHelper.OnDialogActionListener() {
                                    @Override
                                    public void doCancelAction() {
                                        CacheConstant.isMonopolyDialogShow = false;
                                        if (appVersionEntity.isMandatory) {//强更 但是点击"取消"
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(0);
                                        } else {
                                        }
                                    }

                                    @Override
                                    public void doOkAction() {
                                        CacheConstant.isMonopolyDialogShow = false;
                                        Object[] params = new Object[2];
                                        params[0] = appVersionEntity;
                                        params[1] = false;//不检查是否存在文件直接重新下载覆盖
                                        try {
                                            Class checkVersionClass = Class.forName("com.htgames.nutspoker.ui.action.CheckVersionAction");
                                            Constructor constructor = checkVersionClass.getDeclaredConstructor(Activity.class, View.class);
                                            constructor.setAccessible(true);
                                            Object instance = constructor.newInstance(CacheConstant.mTopActivity, null);
                                            java.lang.reflect.Method dealDownloadNewApp = checkVersionClass.getDeclaredMethod("dealDownloadNewApp", AppVersionEntity.class, boolean.class);
                                            dealDownloadNewApp.setAccessible(true);
                                            dealDownloadNewApp.invoke(instance, params);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (!appVersionEntity.isMandatory) {
                                        } else {
//                                            android.os.Process.killProcess(android.os.Process.myPid());
//                                            System.exit(0);
                                            CacheConstant.mTopActivity.finish();
                                            CacheConstant.mTopActivity = null;
                                        }
                                    }
                                });
                        String updateContent = appVersionEntity.content;
                        if (!TextUtils.isEmpty(updateContent)) {
                            String goodUpdateContent = updateContent.replace(" ", "").replace("\t", "");//删除特殊字符
                            mVersionDialog.setMessage2(goodUpdateContent);
                            mVersionDialog.setMessage2GravityLeft();
                        }
                        mVersionDialog.setCancelable(false);
                        mVersionDialog.setCanceledOnTouchOutside(false);
                        mVersionDialog.show();
                        Window windowTest = mVersionDialog.getWindow();
                        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                        windowTest.setGravity(Gravity.CENTER);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
                LogUtil.i(TAG, "SignStringRequest请求 onErrorResponse : " + (error == null ? "error=null" : error.toString()) + "\nurl: " + url);
                HostManager.addHostWeight(url);//增加失败权重
            }
        });
        init();
        initVolleyInterceptor(method, url);
    }

    public SignStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
        init();
    }

    public void init() {
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        LogUtil.i(TAG, "DEFAULT_TIMEOUT_MS :" + DEFAULT_TIMEOUT_MS);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return getCommAuth(getParams());
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return super.getParams();
    }

    public static Map<String, String> getCommAuth(Map<String, String> params){
        String time = String.valueOf(System.currentTimeMillis() / 1000L);
        String randomNum = NetWork.getRandom();
        String paramsMD5 = "";
        if (params != null) {
            paramsMD5 = NetWork.getParamsMD5(params);
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(SignStringRequest.HEADER_KEY_SIGN, NetWork.getSign(randomNum, time));
        paramsMap.put(SignStringRequest.HEADER_KEY_RAND, randomNum);
        paramsMap.put(SignStringRequest.HEADER_KEY_TIME, time);
        paramsMap.put(SignStringRequest.HEADER_KEY_BODY, paramsMD5);
        paramsMap.put(SignStringRequest.HEADER_KEY_APPVER, BaseTools.getAppVersionName(DemoCache.getContext()));
        if(ApiConfig.AppVersion.isTaiwanVersion) {
            paramsMap.put(SignStringRequest.HEADER_KEY_AREA, ApiConfig.AppVersion.AREA_TW);
        }
        LogUtil.i(TAG, paramsMap.toString());
        return paramsMap;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        doVolleyInterceptor(response);
        return super.parseNetworkResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        doVolleyInterceptor(error);
        super.deliverError(error);
    }

    private Object volleyInterceptor;
    private void initVolleyInterceptor(int method, final String url) {
        if (!CacheConstant.debugBuildType) {
            return;
        }
        try {
            Object[] params = new Object[2];
            params[0] = method;
            params[1] = url;
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.network_monitor.VolleyInterceptor");//完整类名
            Constructor<?> constructor = debugClazz.getConstructor(Context.class);
            volleyInterceptor = constructor.newInstance(CacheConstant.sAppContext);//获得实例
            java.lang.reflect.Method getAuthor = debugClazz.getDeclaredMethod("onRequest", int.class, String.class);//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(volleyInterceptor, params);//使用方法
        } catch (Exception e) {
            LogUtil.i(TAG, e == null ? "e=null" : e.toString());
        }
    }

    private void doVolleyInterceptor(NetworkResponse response) {
        if (!CacheConstant.debugBuildType || volleyInterceptor == null) {
            return;
        }
        try {
            Object[] params = new Object[1];
            params[0] = response;
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.network_monitor.VolleyInterceptor");//完整类名
            java.lang.reflect.Method getAuthor = debugClazz.getDeclaredMethod("parseNetworkResponse", NetworkResponse.class);//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(volleyInterceptor, params);//使用方法
        } catch (Exception e) {
            LogUtil.i(TAG, e == null ? "e=null" : e.toString());
        }
    }

    private void doVolleyInterceptor(VolleyError error) {
        if (!CacheConstant.debugBuildType || volleyInterceptor == null) {
            return;
        }
        try {
            Object[] params = new Object[1];
            params[0] = error;
            Class<?> debugClazz = Class.forName("com.htgames.nutspoker.debug.network_monitor.VolleyInterceptor");//完整类名
            java.lang.reflect.Method getAuthor = debugClazz.getDeclaredMethod("deliverError", VolleyError.class);//获得私有方法
            getAuthor.setAccessible(true);//调用方法前，设置访问标志
            getAuthor.invoke(volleyInterceptor, params);//使用方法
        } catch (Exception e) {
            LogUtil.i(TAG, e == null ? "e=null" : e.toString());
        }
    }
}
