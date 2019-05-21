package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * Created by glp on 2016/8/8.
 */
//网络牌谱记录数据
public class NetCardRecordTable implements BaseColumns {
    public static final String TABLE_NAME = "NetCardRecordTable";

    //公共牌区 空;3;3+1;3+1+1;3+2;5
    public static final String pool_cards = "pool_cards";
    //牌局Code
    public static final String code = "code";
    //盈利
    public static final String win_chips = "win_chips";
    //牌类型
    public static final String card_type = "card_type";
    //时间戳
    public static final String time = "time"; // --create_time
    //牌局ID
    public static final String gid = "gid";
    //我的最佳牌型
    public static final String cardtype_cards = "cardtype_cards";
    //我的底牌
    public static final String hand_cards = "hand_cards";
    public static final String hands_cnt = "hands_cnt";//第几手牌
    public static final String id = "id";//牌谱记录ID
    public static final String is_collect = "is_collect";//0不收藏，1收藏
}
