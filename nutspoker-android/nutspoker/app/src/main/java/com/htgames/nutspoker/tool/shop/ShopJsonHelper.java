package com.htgames.nutspoker.tool.shop;

import com.netease.nim.uikit.bean.ClubLimitEntity;
import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.bean.GameGoodsEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  商店JSON解析帮助类
 */
public class ShopJsonHelper {

    /**
     * 获取金币商城列表
     */
    public final static ArrayList<GameGoodsEntity> getGameGoodsList(String result) {
        ArrayList<GameGoodsEntity> goodsList = new ArrayList<GameGoodsEntity>();
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            JSONArray array = data.getJSONArray("goods");
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject itemJson = array.getJSONObject(i);
                GameGoodsEntity gameGoodsEntity = new GameGoodsEntity();
                gameGoodsEntity.id = (itemJson.optInt("id"));
                gameGoodsEntity.name = (itemJson.optString("name"));
                gameGoodsEntity.desc = (itemJson.optString("desc"));
                gameGoodsEntity.chip = (itemJson.optInt("chip"));
                gameGoodsEntity.present = (itemJson.optInt("present"));
                gameGoodsEntity.discount = (itemJson.optDouble("discount"));//浮点
                gameGoodsEntity.diamond = (itemJson.optInt("diamond"));
                goodsList.add(gameGoodsEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    /**
     * 获取钻石商城列表
     */
    public final static ArrayList<DiamondGoodsEntity> getDiamondGoodsList(String result) {
        ArrayList<DiamondGoodsEntity> goodsList = new ArrayList<DiamondGoodsEntity>();
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            JSONArray array = data.getJSONArray("diamond");
            int size = array.length();
            for (int i = 0; i < size; i++) {
                JSONObject itemJson = array.getJSONObject(i);
                DiamondGoodsEntity diamondGoodsEntity = new DiamondGoodsEntity();
                diamondGoodsEntity.id = (itemJson.optInt("id"));
                diamondGoodsEntity.name = (itemJson.optString("name"));
                diamondGoodsEntity.desc = (itemJson.optString("desc"));
                diamondGoodsEntity.diamond = (itemJson.optInt("diamond"));
                diamondGoodsEntity.vip = (itemJson.optInt("vip"));
                diamondGoodsEntity.price = (itemJson.optInt("price"));
                diamondGoodsEntity.skuId = (itemJson.optString("android_id"));
                goodsList.add(diamondGoodsEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return goodsList;
    }

    /**
     * 获取俱乐部成员上限
     * @param result
     * @return
     */
    public final static ArrayList<ClubLimitEntity> getClubLimitList(String result) {
        ArrayList<ClubLimitEntity> limitList = new ArrayList<ClubLimitEntity>();
        try {
            JSONObject response = new JSONObject(result);
            JSONObject data = response.getJSONObject("data");
            if(data.has("teams")) {
                JSONArray array = data.getJSONArray("teams");
                int size = array.length();
                for (int i = 0; i < size; i++) {
                    JSONObject itemJson = array.getJSONObject(i);
                    ClubLimitEntity clubLimitEntity = new ClubLimitEntity();
                    clubLimitEntity.id = (itemJson.optInt("id"));
                    clubLimitEntity.memberCountLimit = (itemJson.optInt("nums"));
                    clubLimitEntity.discount = ((float)itemJson.optDouble("discount"));
                    clubLimitEntity.monthSellPrice = (itemJson.optInt("sell_diamond"));
                    clubLimitEntity.monthOriginalPrice = (itemJson.optInt("original_diamond"));
                    limitList.add(clubLimitEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return limitList;
    }
}
