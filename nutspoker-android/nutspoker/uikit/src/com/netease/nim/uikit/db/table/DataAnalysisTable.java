package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * 数据分析
 */
public class DataAnalysisTable implements BaseColumns{
    public static String TABLE_DATA_ANALYSIS = "dataanalysis";//数据分析
    //
    public static String COLUMN_DATA_WSD = "wsd";
    public static String COLUMN_DATA_WTSD = "wtsd";
    public static String COLUMN_DATA_WWSF = "wwsf";
    public static String COLUMN_DATA_AF = "af";
    public static String COLUMN_DATA_AFQ = "afq";
    public static String COLUMN_DATA_VPIP = "vpip";
    public static String COLUMN_DATA_PFR = "pfr";
    public static String COLUMN_DATA_PFR_VPIP = "pfr_vpip";
    public static String COLUMN_DATA_THREE_BET = "three_bet";
    public static String COLUMN_DATA_ATT = "att_to_stl_lp";
    public static String COLUMN_DATA_FOLD_STL = "fold_blind_to_stl";
    public static String COLUMN_DATA_SB = "sb_three_bet_steal_att";
    public static String COLUMN_DATA_BB = "bb_three_bet_steal_att";
}
