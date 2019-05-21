package com.netease.nim.uikit.api;


import com.netease.nim.uikit.common.util.NetworkUtil;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by glp on 2016/8/18.
 *
 * 认证中间件
 */

public class AuthInterceptor implements Interceptor {

    //认证Map
    Map<String, String> mMap;

    public AuthInterceptor(Map<String, String> map) {
        mMap = map;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();

        if (mMap != null && !mMap.isEmpty()) {
            Set<String> setStr = mMap.keySet();
            for (String name : setStr) {
                builder.addHeader(name, mMap.get(name));
            }
        }

        HttpUrl httpUrl = request.url();

        //如果没有网络，并且是Get方法，那么可以去读取文件缓存，其他不行
        if (!NetworkUtil.isNetAvailable(null) && request.method().equalsIgnoreCase("GET")) {
            builder.cacheControl(CacheControl.FORCE_CACHE);
        }

        Response response = chain.proceed(builder.build());
        return response;
    }
}
