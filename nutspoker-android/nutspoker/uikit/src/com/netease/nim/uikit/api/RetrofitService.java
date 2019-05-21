package com.netease.nim.uikit.api;

import com.netease.nim.uikit.bean.CardGamesEy;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.PayAli;
import com.netease.nim.uikit.bean.UserAmountBean;

import org.json.JSONObject;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by glp on 2016/8/18.
 */

public interface RetrofitService {
    interface PayService {
        @FormUrlEncoded
        @POST("xxx")
        Observable<CommonBeanT<PayAli>> getAliPay(@Field("os") String os, @Field("uid") String uid, @Field("product_id") String productId);
    }

    interface GameInfoService {
        @GET("game/view")
        Observable<CommonBeanT<CardGamesEy>> getGameInfo(@Query("os") String os, @Query("gid") String gid);
    }

    interface ClubService {
        @FormUrlEncoded
        @POST("team/leave")
        Observable<CommonBeanT<JSONObject>> exitClub(@Field("os") String os, @Field("uid") String uid, @Field("tid") String tid);
    }

    interface GetAmountService {
        @GET("user/amount")
        Observable<CommonBeanT<UserAmountBean>> getAmount(@Query("os") String os, @Query("uid") String uid);
    }
}
