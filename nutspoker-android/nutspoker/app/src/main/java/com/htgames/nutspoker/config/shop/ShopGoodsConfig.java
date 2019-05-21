package com.htgames.nutspoker.config.shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 游戏商品配置表
 */
public class ShopGoodsConfig {
    private static Map<Integer , Integer> gameGoodsList;
    private static Map<Integer , Integer> gameGoodsNameList;
    private static Map<Integer , Integer> diamondGoodsList;
    private static Set<Integer> vipIdMap;
    public static final int whiteVipId = 3001;
    public static final int blackVipId = 3002;

    public static Set<Integer> getVipIdMap(){
        if(vipIdMap == null){
            vipIdMap = new HashSet<Integer>(2);
            vipIdMap.add(whiteVipId);
            vipIdMap.add(blackVipId);
        }
        return vipIdMap;
    }

    public static Map<Integer , Integer> getGameGoodsList(){
        if(gameGoodsList == null){
            gameGoodsList = new HashMap<Integer , Integer>();
            gameGoodsList.put(1001 , 100);//R.mipmap.icon_game_goods_1001);
            gameGoodsList.put(1002 , 100);//R.mipmap.icon_game_goods_1002);
            gameGoodsList.put(1003 , 100);//R.mipmap.icon_game_goods_1003);
            gameGoodsList.put(1004 , 100);//R.mipmap.icon_game_goods_1004);
            gameGoodsList.put(1005 , 100);//R.mipmap.icon_game_goods_1005);
            gameGoodsList.put(1006 , 100);//R.mipmap.icon_game_goods_1006);
        }
        return gameGoodsList;
    }

    public static Map<Integer , Integer> getGameGoodsNameList(){
        if(gameGoodsNameList == null){
            gameGoodsNameList = new HashMap<Integer , Integer>();
            gameGoodsNameList.put(1001 , 100);//R.mipmap.icon_game_goods_name_1001);
            gameGoodsNameList.put(1002 , 100);//R.mipmap.icon_game_goods_name_1002);
            gameGoodsNameList.put(1003 , 100);//R.mipmap.icon_game_goods_name_1003);
            gameGoodsNameList.put(1004 , 100);//R.mipmap.icon_game_goods_name_1004);
            gameGoodsNameList.put(1005 , 100);//R.mipmap.icon_game_goods_name_1005);
            gameGoodsNameList.put(1006 , 100);//R.mipmap.icon_game_goods_name_1006);
        }
        return gameGoodsNameList;
    }

    public static Map<Integer , Integer> getDiamondGoodsList(){
        if(diamondGoodsList == null){
            diamondGoodsList = new HashMap<Integer , Integer>();
            diamondGoodsList.put(2001 , 100);//R.mipmap.icon_diamond_goods_2001);
            diamondGoodsList.put(2002 , 100);//R.mipmap.icon_diamond_goods_2002);
            diamondGoodsList.put(2003 , 100);//R.mipmap.icon_diamond_goods_2003);
            diamondGoodsList.put(2004 , 100);//R.mipmap.icon_diamond_goods_2004);
            diamondGoodsList.put(2005 , 100);//R.mipmap.icon_diamond_goods_2005);
        }
        return diamondGoodsList;
    }


}
