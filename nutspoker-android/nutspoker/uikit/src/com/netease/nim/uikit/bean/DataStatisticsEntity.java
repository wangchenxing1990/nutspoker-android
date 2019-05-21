package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * 数据统计
 */
public class DataStatisticsEntity implements Serializable {
    public int games; //牌局总数
    public int hands; //手牌数
    public int hands_count_won; //总胜率
    public int wsd;//摊牌胜率
    public int wwsf;//看翻牌胜率
    public int wsd_after_river; //河牌圈跟注胜率
    public int my_c_won; //总盈利
    public int hundred_hands_win; //平均百手盈利
    public int big_blind_won_cnt; //平均百手赢得大盲
    public int allin_chips_avg; //平均all in筹码

    /**
     * 0 全部，1近一周，2近一月
     */
    public int type;

//    public int vpip; //入池率
//    public int waptmp; //其中胜率
//    public ArrayList<WinChipEntity> winChipList;
}
