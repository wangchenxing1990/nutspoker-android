package com.netease.nim.uikit.constants;

/**
 * vip功能配置
 */
public class VipConstants {
    public static int VIP_LEVEL_NOT = 0;
    public static int VIP_LEVEL_WHITE = 1;
    public static int VIP_LEVEL_BALCK = 2;
    //
    public static int LEVEL_NOT_GAMECOUNT = 1;//牌局数量
    public static int LEVEL_NOT_CLUBCOUNT = 3;//1;//俱乐部数量  需求更改改为3 --- 20161121
    public static int LEVEL_NOT_RECORD_HANDCOUNT = 7;//手牌数量：记录7天                    1月
    public static int LEVEL_NOT_COLLECT_HANDCOUNT = 100;//     15;//手牌数量：收藏    需求更改改为100 --- 20161121
    //白金
    public static int LEVEL_WHITE_GAIN = 1000;//赠送金币数量                              5000
    public static int LEVEL_WHITE_GAMECOUNT = 3;//牌局数量
    public static int LEVEL_WHITE_CLUBCOUNT = 3;//俱乐部数量
    public static int LEVEL_WHITE_RECORD_HANDCOUNT = 30;//手牌数量：记录30天
    public static int LEVEL_WHITE_COLLECT_HANDCOUNT = 100;//手牌数量：收藏
    //黑金
    public static int LEVEL_BLACK_GAIN = 12000;//赠送金币数量
    public static int LEVEL_BLACK_GAMECOUNT = 5;
    public static int LEVEL_BLACK_CLUBCOUNT = 5;
    public static int LEVEL_BLACK_RECORD_HANDCOUNT = 90;//手牌数量：记录90天
    public static int LEVEL_BLACK_COLLECT_HANDCOUNT = 999999;//手牌数量：收藏

    public static int getClubLimitByLevel(int level) {
        int limit = LEVEL_NOT_CLUBCOUNT;
        if(level == VIP_LEVEL_WHITE){
            limit = LEVEL_WHITE_CLUBCOUNT;
        }else if(level == VIP_LEVEL_BALCK){
            limit = LEVEL_BLACK_CLUBCOUNT;
        }
        return limit;
    }

    //根据VIP获取手牌收藏上限
    public static int getHandCollectLimitByLevel(int level) {
        int limit = LEVEL_NOT_COLLECT_HANDCOUNT;
        if(level == VIP_LEVEL_WHITE){
            limit = LEVEL_WHITE_COLLECT_HANDCOUNT;
        }else if(level == VIP_LEVEL_BALCK){
            limit = LEVEL_BLACK_COLLECT_HANDCOUNT;
        }
        return limit;
    }
}
