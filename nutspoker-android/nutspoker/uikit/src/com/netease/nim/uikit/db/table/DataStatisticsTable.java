package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * 数据统计
 */
public class DataStatisticsTable implements BaseColumns{
    public static String TABLE_DATA_STATISTICS = "datastatistics";//
    //
    public static String COLUMN_DATA_GAMES = "games";
    public static String COLUMN_DATA_HANDS = "hands";
    public static String COLUMN_DATA_HANDS_COUNT_WON = "hands_count_won";
    public static String COLUMN_DATA_WSD = "wsd";
    public static String COLUMN_DATA_WWSF = "wwsf";
    public static String COLUMN_DATA_WSD_AFTER = "wsd_after_river";
    public static String COLUMN_DATA_MY_C_WON = "my_c_won";
    public static String COLUMN_DATA_HUNDRED_HANDS_WIN = "hundred_hands_win";
    public static String COLUMN_DATA_BIG_BLIND_WON = "big_blind_won_cnt";
    public static String COLUMN_DATA_VPIP = "vpip";
    public static String COLUMN_DATA_WAPTMP = "waptmp";
    public static String COLUMN_DATA_ALLIN_CHIPS = "allin_chips_avg";

    public static String COLUMN_DATA_TYPE = "type";
}