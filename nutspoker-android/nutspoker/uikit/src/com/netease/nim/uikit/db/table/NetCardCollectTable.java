package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * Created by glp on 2016/8/11.
 */

public class NetCardCollectTable implements BaseColumns {
    public static String TABLE_NAME = "NetCardCollectTable";

    public static String id = "id";
    public static String hid = "hid";
    public static String uid = "uid";
    public static String collect_time = "collect_time";

    public static String file_path = "file_path";//"2016/08/TexasSheet-1470383006-232319-10089-111",
    public static String file_name = "file_name";//"TexasSheet-1470383006-232319-10089-111"

    public static String pool_cards = "pool_cards";
    public static String code = "code";
    public static String win_chips = "win_chips";
    public static String card_type = "card_type";
    public static String time = "time";
    public static String gid = "gid";
    public static String cardtype_cards = "cardtype_cards";
    public static String hand_cards = "hand_cards";
    public static String hands_cnt = "hands_cnt";
    public static String count = "count";
    public static String tid = "tid";
    public static String owner = "owner";
    public static String name = "name";
    public static String blinds = "blinds";
    public static String sblinds = "sblinds";
    public static String duration = "duration";
    public static String durations = "durations";
    public static String tilt_mode = "tilt_mode";
    public static String type = "type";
    public static String status = "status";
    public static String public_mode = "public_mode";
    public static String ante_mode = "ante_mode";
    public static String ante = "ante";
    public static String create_time = "create_time";
    public static String bouts = "bouts";
    public static String game_mode = "game_mode";
    public static String match_chips = "match_chips";
    public static String match_player = "match_player";
    public static String match_duration = "match_duration";
    public static String match_checkin_fee = "match_checkin_fee";
}
