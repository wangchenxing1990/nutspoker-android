package com.htgames.nutspoker.ui.action;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.base.BaseAction;

import java.util.HashMap;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by glp on 2016/8/5.
 */

public class CardRecordListAction extends BaseAction{

    public CardRecordListAction(Activity activity, View baseView) {
        super(activity, baseView);
    }

    /**
     *获取牌谱总数
     * @param callback
     */
    public void getCRListCount(RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("count","1");
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_LIST,paramsMap,callback);
    }

    /**
     *获取牌谱列表   默认一页20手，
     * @param handId 如果为空，那么返回的是第一页数据，否则就是返回这手后面一页的牌谱数据
     * @param callback
     */
    public void getCRList(@Nullable String handId, RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        if(handId != null)
            paramsMap.put("last_hid",handId);
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_LIST,paramsMap,callback);
    }

    /**
     * 下载文件
     * @param handId 指定牌谱ID
     * @param callback
     */
    public void getCRFile(String handId,RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("hid",handId);
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_DOWN,paramsMap,callback);
    }

    /**
     * 收藏牌谱
     * @param handId
     * @param callback
     */
    public void collectHand(String handId,RequestCallback callback){
        HashMap<String, String> paramsMap = getRequestCommonMap();
        paramsMap.put("hid",handId);
        addRequestGet(getHost() + ApiConstants.URL_CARDRECORD_COLLECT,paramsMap,callback);
    }
}
