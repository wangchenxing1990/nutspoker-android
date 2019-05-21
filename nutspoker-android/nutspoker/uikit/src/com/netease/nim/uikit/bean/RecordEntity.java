package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * Created by glp on 2016/7/29.
 */

public class RecordEntity implements Serializable{

    public int vpip;//普通牌局入池率
    public int waptmp;//普通牌局其中胜率
    public int hands;//普通牌局总手数

    public int my_c_won;//普通牌局总盈利
    public int my_c_won_sng_mtt;//比赛模式 mt－sng 的总盈利
    public int my_c_won_sng;//比赛模式 sng 的总盈利
    public int my_c_won_mtt;//比赛模式 mtt 的总盈利

    public int games;//普通牌局 牌局数
    public int games_sng_mtt;//比赛模式 mt－sng 的牌局数
    public int games_sng;//比赛模式 sng 的牌局数
    public int games_mtt;//比赛模式 mtt 的牌局数

    public int hands_won_cnt;//胜利总手数
    public int hands_count_won;//普通牌局总胜率

}
