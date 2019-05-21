package com.htgames.nutspoker.ui.action.log;

import android.content.Context;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.net.HttpManager;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.common.util.NetworkUtil;

import java.util.UUID;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 *
 */
public class LogHttpRequest {
    private final static String TAG = "LogHttpRequest";
    static String requestLogUrl;

    public static void logToServer(final Context context , final String logContent) {
        if (!NetworkUtil.isNetAvailable(context)) {
            return;
        }
        requestLogUrl = getHost() + ApiConstants.URL_LOG;
        LogUtil.i(TAG, requestLogUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestLogUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(TAG, response);
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
            public byte[] getBody() throws AuthFailureError {
                String content = LogConstants.getLogJsonStr(ChessApp.sAppContext, logContent);
                LogUtil.i("log", content);
                return content.getBytes();
            }

            @Override
            public String getBodyContentType() {
//                return "multipart/mixed; charset=" + getParamsEncoding();
                return "multipart/mixed; charset=" + ";boundary=" + UUID.randomUUID().toString();
//                return super.getBodyContentType();
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                return super.getRetryPolicy();
            }
        };
        signRequest.setTag(requestLogUrl);
        HttpManager.getInstance(ChessApp.sAppContext).add(signRequest);
    }
}
