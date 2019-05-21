package com.htgames.nutspoker.config;

import com.netease.nim.uikit.bean.ClubLimitEntity;

import java.util.ArrayList;

/**
 * 俱乐部配置
 */
public class ClubConfig {
    private final static int[] limitId = new int[]{4001 ,4002 ,4003 ,4004 ,10105 ,10106 ,10107};

    private final static int[] limitMemberCount = new int[]{150 ,200 ,250 ,300 ,500 ,800 ,1000};

    /**
     * 单价
     */
    private final static int[] limitPrice = new int[]{600 ,1200 ,1800 ,2400 ,4800 ,8400 ,10600};
    /**
     * 售价
     */
    private final static int[] limitSellingPrice = new int[]{600 ,1176 ,1746 ,2280 ,4320 ,7140 ,8480};

    //折扣
    private final static float[] limitSellingDiscount = new float[]{1 ,0.98f ,0.97f ,0.95f ,0.9f ,0.85f ,0.8f};

    public final static String[] limitTimeShow = new String[]{"30天" ,"90天" ,"180天" ,"360天"};

    public final static int[] limitTime = new int[]{1 ,3 ,6 ,12};

    public static ArrayList<ClubLimitEntity> getClubLimitConfig(){
        ArrayList<ClubLimitEntity> clubConfigList = new ArrayList<ClubLimitEntity>();
        for(int i = 0 ; i < limitId.length ; i++){
            ClubLimitEntity clubLimitEntity = new ClubLimitEntity();
            clubLimitEntity.id = (limitId[i]);
            clubLimitEntity.discount = (limitSellingDiscount[i]);
            clubLimitEntity.memberCountLimit = (limitMemberCount[i]);
            clubLimitEntity.monthOriginalPrice = (limitPrice[i]);
            clubLimitEntity.monthSellPrice = (limitSellingPrice[i]);
            clubConfigList.add(clubLimitEntity);
        }
        return clubConfigList;
    }

}
