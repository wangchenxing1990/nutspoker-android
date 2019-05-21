package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * Created by glp on 2016/7/29.
 */

public class RecordEntityTable implements BaseColumns {
    public static String TABLE_NAME = "RecordEntityTable";//

    public static String vpip = "vpip";//普通牌局入池率
    public static String waptmp = "waptmp";//普通牌局其中胜率
    public static String hands = "hands";//普通牌局总手数

    public static String my_c_won = "my_c_won";//普通牌局总盈利
    public static String my_c_won_sng_mtt = "my_c_won_sng_mtt";//比赛模式 mt－sng 的总盈利
    public static String my_c_won_sng = "my_c_won_sng";//比赛模式 sng 的总盈利
    public static String my_c_won_mtt = "my_c_won_mtt";//比赛模式 mtt 的总盈利

    public static String games = "games";//普通牌局 牌局数
    public static String games_sng_mtt = "games_sng_mtt";//比赛模式 mt－sng 的牌局数
    public static String games_sng = "games_sng";//比赛模式 sng 的牌局数
    public static String games_mtt = "games_mtt";//比赛模式 mtt 的牌局数

    public static String hands_won_cnt = "hands_won_cnt";//胜利总手数
    public static String hands_count_won = "hands_count_won";//普通牌局总胜率

    //保险数据
    //保险触发次数
    public static String trigger_count = "trigger_count";
    //中牌次数
    public static String hit_count = "hit_count";
    //买入次数
    public static String buy_count = "buy_count";
    //买入总计
    public static String buy_sum = "buy_sum";
    //支付总计
    public static String pay_sum = "pay_sum";
    //我组局的次数
    public static String my_games = "my_games";
    //我组局的买入总计
    public static String my_buy_sum = "my_buy_sum";
    //我组局的支付总计
    public static String my_pay_sum = "my_pay_sum";

    //比赛信息
//    public static String games_sng_mtt;
//    public static String games_sng;
//    public static String games_mtt;

    public static String match_fee_mtt = "match_fee_mtt";
    public static String match_fee_sng = "match_fee_sng";
    public static String match_fee_sng_mtt = "match_fee_sng_mtt";
    public static String reward_mtt = "reward_mtt";
    public static String reward_sng = "reward_sng";
    public static String reward_sng_mtt = "reward_sng_mtt";
    public static String in_reward_mtt = "in_reward_mtt";
    public static String in_reward_sng = "in_reward_sng";
    public static String in_reward_sng_mtt = "in_reward_sng_mtt";
    public static String in_finals_mtt = "in_finals_mtt";
    public static String in_finals_sng = "in_finals_sng";
    public static String in_finals_sng_mtt = "in_finals_sng_mtt";
}
