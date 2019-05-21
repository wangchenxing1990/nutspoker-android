package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * Created by glp on 2016/8/8.
 */

//牌局信息
public class CardGamesTable implements BaseColumns {
    public static final String TABLE_NAME="CardGamesTable";

    //Game ID
    public static final String gid = "gid";
    //Team ID
    public static final String tid = "tid";
    //牌局名称
    public static final String name = "name";
    //牌局Code
    public static final String code = "code";
    //盲注
    public static final String blinds = "blinds";
    //小盲注
    public static final String sblinds = "sblinds";
    //历时
    public static final String duration = "duration";
    //创建者账号
    public static final String owner = "owner";
    //俱乐部，个人
    public static final String type = "type";
    //牌局模式
    public static final String public_mode = "public_mode";

    public static final String ante_mode = "ante_mode";
    public static final String tilt_mode = "tilt_mode";
    //0 准备中 1进行中 2已结束
    public static final String status = "status";
    //创建时间戳
    public static final String create_time = "create_time";
    public static final String total_player = "total_player";
    public static final String server = "server";
    //比赛模式
    public static final String game_mode = "game_mode";
    //比赛盈利
    public static final String match_chips = "match_chips";
    //比赛玩家数量
    public static final String match_player = "match_player";
    //比赛历时
    public static final String match_duration = "match_duration";
    //结束时间戳
    public static final String end_time = "end_time";
    //比赛费用
    public static final String match_checkin_fee = "match_checkin_fee";
    //聊天室ID
    public static final String room_id = "room_id";
}
