package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.GameRecordTable;

/**
 * V2版本数据库
 */
public class SQLV2 {
    /**
     * 添加了游戏记录新字段（SNG相关）
     */
//    public static final String ALTER_TABLE_GAME_RECORD = "ALTER TABLE "
//            + GameRecordTable.TABLE_GAME_RECORD
//            + " ADD COLUMN"
//            + GameRecordTable.COLUMN_GAME_MODE + " INTEGER DEFAULT 0,"
//            + GameRecordTable.COLUMN_GAME_MY_UID + " TEXT,"
//            + GameRecordTable.COLUMN_GAME_SNG_SBLINDS + " INTEGER DEFAULT 0,"
//            + GameRecordTable.COLUMN_GAME_SNG_SBLINDS_TIME + " INTEGER DEFAULT 0,"
//            + GameRecordTable.COLUMN_GAME_SNG_CHIPS + " INTEGER DEFAULT 0,"
//            + GameRecordTable.COLUMN_GAME_ALL_REWARD + " INTEGER DEFAULT 0,"
//            + GameRecordTable.COLUMN_GAME_TOTAL_TIME + " INTEGER DEFAULT 0";

    public static final String ALTER_TABLE_GAME_RECORD_GAME_MODE = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MODE + " INTEGER DEFAULT 0";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MYUID = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MY_UID + " TEXT";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_PLAYER = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MATCH_PLAYER + " INTEGER DEFAULT 0";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_DURATION = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MATCH_DURATION + " INTEGER DEFAULT 0";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_CHIPS = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MATCH_CHIPS + " INTEGER DEFAULT 0";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_ALLREWARD = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_ALL_REWARD + " INTEGER DEFAULT 0";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_TOTALTIME = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_TOTAL_TIME + " INTEGER DEFAULT 0";
    //
    public static final String ALTER_TABLE_GAME_RECORD_GAME_MATCH_CHECKIN_FEE = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE + " INTEGER DEFAULT 0";
    //结束时间
    public static final String ALTER_TABLE_GAME_RECORD_GAME_END_TIME = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_END_TIME + " LONG DEFAULT 0";
    //ante
    public static final String ALTER_TABLE_GAME_RECORD_GAME_ANTE = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_ANTE + " INTEGER DEFAULT 0";
}
