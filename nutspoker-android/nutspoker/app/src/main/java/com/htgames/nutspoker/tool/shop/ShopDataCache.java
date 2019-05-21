package com.htgames.nutspoker.tool.shop;

import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.bean.ClubLimitEntity;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.bean.GameGoodsEntity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 商店缓存
 */
public class ShopDataCache {
    private final static String TAG = "ShopDataCache";
    static ShopDataCache instance;
    ArrayList<GameGoodsEntity> goodsList = new ArrayList<GameGoodsEntity>();
    ArrayList<DiamondGoodsEntity> diamondGoodsList = new ArrayList<DiamondGoodsEntity>();
    ArrayList<ClubLimitEntity> clubLimitList = new ArrayList<ClubLimitEntity>();
    String cacheJsonData;

    public static synchronized ShopDataCache getInstance() {
        if (instance == null) {
            instance = new ShopDataCache();
            LogUtil.i(TAG , "new");
        }
        return instance;
    }

    public void buildCache(String cacheData) {
        LogUtil.i(TAG, "buildCache");
        cacheJsonData = cacheData;
        goodsList = ShopJsonHelper.getGameGoodsList(cacheData);
        diamondGoodsList = ShopJsonHelper.getDiamondGoodsList(cacheData);
        clubLimitList = ShopJsonHelper.getClubLimitList(cacheData);
        LogUtil.i(TAG, "goodsList :" + (goodsList == null ? 0 : goodsList.size()));
        LogUtil.i(TAG, "diamondGoodsList :" + (diamondGoodsList == null ? 0 : diamondGoodsList.size()));
        LogUtil.i(TAG, "clubLimitList :" + (clubLimitList == null ? 0 : clubLimitList.size()));
    }

    public String getShopData() {
        try {
            if (!TextUtils.isEmpty(cacheJsonData)) {
                JSONObject shopJson = new JSONObject(cacheJsonData);
                int code = shopJson.optInt("code");
                String data = shopJson.optString("data");
                if (code == 0 && !TextUtils.isEmpty(data)) {
                    LogUtil.i(TAG, "data :" + data);
                    return data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public ArrayList<GameGoodsEntity> getGoodsList() {
        LogUtil.i(TAG , "getGoodsList");
        return goodsList;
    }

    public ArrayList<DiamondGoodsEntity> getDiamondGoodsList() {
        LogUtil.i(TAG , "getDiamondGoodsList");
        return diamondGoodsList;
    }

    public ArrayList<ClubLimitEntity> getClubLimitList() {
        LogUtil.i(TAG , "getClubLimitList");
        return clubLimitList;
    }

    public void clear() {
        if (goodsList != null) {
            goodsList.clear();
        }
        if (diamondGoodsList != null) {
            diamondGoodsList.clear();
        }
        if (clubLimitList != null) {
            clubLimitList.clear();
        }
    }
}
