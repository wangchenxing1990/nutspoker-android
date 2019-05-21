package com.netease.nim.uikit.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.netease.nim.uikit.bean.CommonBean;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by glp on 2016/7/1.
 */

public class HttpApi {

    public static final String Tag = "HttpApi";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //默认10秒超时
    public static final int DEFAULT_TIMEOUT = 5;
    //http 缓存文件最大大小
    static final long DEFAULT_CACHE_FILESIZE = 100 * 1024 * 1024;//100MB

    static Cache sCache;

    /**
     * 替代参考使用 {@link HttpApi.GetNetObservable}
     *
     * @param map
     * @return
     */
    @Deprecated
    public static Retrofit GetRetrofitIns(Map<String, String> map) {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .writeTimeout(DEFAULT_TIMEOUT/2,TimeUnit.SECONDS)
//                .readTimeout(DEFAULT_TIMEOUT/2,TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addNetworkInterceptor(new AuthInterceptor(SignStringRequest.getCommAuth(map)));
        if (CacheConstant.debugBuildType) {
            Class<?> debugClazz = null;//完整类名
            try {
                debugClazz = Class.forName("com.htgames.nutspoker.debug.network_monitor.ChuckInterceptor");
                Constructor<?> constructor = debugClazz.getConstructor(Context.class);
                Interceptor chuckInterceptor = (Interceptor) constructor.newInstance(CacheConstant.sAppContext);//获得实例
                builder.addInterceptor(chuckInterceptor);
            } catch (Exception e) {
                LogUtil.i(Tag, e == null ? "e=null" : e.toString());
            }
        }
        OkHttpClient client = builder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(getHost())
                .addConverterFactory(GsonConverterFactory.create())//转换Json
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//返回被观察者
                .build();
        return retrofit;
    }

    public static Object create(Retrofit retrofit, Class clazz) {
        return retrofit.create(clazz);
    }

    static Cache GetHttpCatch() {
        if (sCache == null)
            sCache = new Cache(CacheConstant.sAppDirUtil.getTempCacheDirFile(), DEFAULT_CACHE_FILESIZE);
        return sCache;
    }

    /**
     * 只关心Http请求结果的Code，不对Data做解析
     *
     * @param builder Builder对象
     * @param action0 请求开始前，可以做一些操作，比如显示加载对话框
     * @return
     */
    public static Observable<Integer> GetNetCodeObservable(@NonNull Builder builder, @Nullable Action0 action0) {
        return GetNetObservable(builder, action0)
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        try {
                            CommonBean cb = GsonUtils.getGson().fromJson(s, CommonBean.class);
                            return cb.code;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                });
    }

    /**
     * HTTP 模板
     *
     * @param builder Builder对象
     * @param action0 请求开始前，可以做一些操作，比如显示加载对话框
     * @return
     */
    public static Observable<String> GetNetObservable(@NonNull Builder builder, @Nullable Action0 action0) {

        if (builder == null)
            return null;
        /**
         * .doOnSubscribe 跟在最后面最近的一个subscribeOn指定的线程执行,不管前面指定多少个subscribeOn，和后面多少个subscribeOn，后面没指定，默认跟just一个线程
         */
        Observable<Builder> observable;
        if (action0 != null) {
            observable = Observable.just(builder)
                    .doOnSubscribe(action0)
                    .subscribeOn(AndroidSchedulers.mainThread());
        } else {
            observable = Observable.just(builder);
        }

        return observable
                .observeOn(Schedulers.io())
                .map(new Func1<Builder, String>() {
                    @Override
                    public String call(Builder builder) {
                        //同步 OKHttp
                        OkHttpClient client = new OkHttpClient.Builder()
                                .cache(GetHttpCatch())
                                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                                //.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                                //.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                                //.authenticator()//服务器返回401,407可以使用这个
                                //Application Interceptor
                                .addInterceptor(new AuthInterceptor(SignStringRequest.getCommAuth(builder.getParams())))
                                //Network Interceptor
                                //.addNetworkInterceptor()
                                .build();

                        String url;
                        if (TextUtils.isEmpty(builder.getBaseUrl()))
                            url = getHost() + builder.getMethodUrl();
                        else
                            url = builder.getBaseUrl() + builder.getMethodUrl();

                        CacheControl.Builder cacheBuilder = new CacheControl.Builder();
                        if(builder.getCacheTime() == -1)
                            cacheBuilder.noStore();//不缓存任何数据
                        else
                            cacheBuilder.maxAge(builder.getCacheTime(), TimeUnit.SECONDS);//缓存数据
                        Request.Builder requestBuilder = new Request.Builder()
                                .cacheControl(cacheBuilder.build());

                        if(builder.getMethod() == 0){
                            url += NetWork.getRequestParams(builder.getParams());
                            requestBuilder.url(url);
                        } else {
                            requestBuilder.url(url);

                            Set<String> stringSet = builder.getParams().keySet();
                            FormBody.Builder fbBuilder = new FormBody.Builder();
                            for (String name : stringSet) {
                                fbBuilder.add(name, builder.getParams().get(name));
                            }

                            switch (builder.getMethod()) {
                                case 1:
                                    requestBuilder.post(fbBuilder.build());
                                    break;
                                case 2:
                                    if(stringSet.isEmpty())
                                        requestBuilder.delete();
                                    else
                                        requestBuilder.delete(fbBuilder.build());
                                    break;
                                case 3:
                                    requestBuilder.put(fbBuilder.build());
                                    break;
                                case 4:
                                    requestBuilder.patch(fbBuilder.build());
                                    break;
                            }
                        }

                        Request request = requestBuilder.build();
                        Response response;
                        String retStr = "";
                        try {
                            response = client.newCall(request).execute();
//                            Headers responseHeaders = response.headers();
//                            for (int i = 0; i < responseHeaders.size(); i++) {
//                                LogUtil.e("HttpApi", responseHeaders.name(i) + ":" + responseHeaders.value(i));
//                            }
                            //无网络，无缓存的时候，修改一下我们的code
                            if(response.code() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT){
                                retStr = "{\"code\":"+ HttpURLConnection.HTTP_GATEWAY_TIMEOUT+",\"message\":\"\",\"data\":\"\"}";
                            } else {
                                retStr = response.body().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return retStr;
                    }
                });
    }

    /**
     * Http请求参数构件类
     */
    public static final class Builder {
        String baseUrl;//基础访问地址，可以为空
        String methodUrl;//方法
        /**
         * 0 : GET
         * 1 : POST
         * 2 : DELETE
         * 3 : PUT
         * 4 : PATCH
         */
        int method = 0;//默认GET
        int cacheTime = -1;//请求缓存时间，单位秒，默认不缓存
        Map<String, String> params = new HashMap<>();//参数

        public Builder() {
            this.baseUrl = null;
        }

        public Builder(@Nullable String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getMethodUrl() {
            return methodUrl;
        }

        public int getMethod() {
            return method;
        }

        public int getCacheTime() {
            return cacheTime;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public Builder methodGet(@NonNull String methodUrl) {
            return methodUrl(0, methodUrl);
        }

        public Builder methodPost(@NonNull String methodUrl) {
            return methodUrl(1, methodUrl);
        }

        public Builder methodUrl(int method, @NonNull String methodUrl) {
            this.method = method;
            this.methodUrl = methodUrl;
            return this;
        }

        public Builder cacheTime(int cacheTime) {
            this.cacheTime = cacheTime;
            return this;
        }

        /**
         * @param params 如果为null，则清空this.params，否则加入params
         * @return
         */
        public Builder mapParams(@Nullable Map<String, String> params) {
            if (params == null)
                this.params.clear();
            else
                this.params.putAll(params);
            return this;
        }

        public Builder param(@NonNull String key, @NonNull String value) {
            this.params.put(key, value);
            return this;
        }
    }
}
