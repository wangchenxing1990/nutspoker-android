package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.GameRecordTable;

/**
 * Created by 周智慧 on 17/2/17.添加猎人赛相关的字段
 */

public interface SQLV7 {
    public static final String ALTER_TABLE_GAME_RECORD_GAME_KO_MODE = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_KO_MODE + " INTEGER DEFAULT 0";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_KO_REWARD_RATE = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_KO_REWARD_RATE + " INTEGER DEFAULT 0";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_KO_HEAD_RATE = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_KO_HEAD_RATE + " INTEGER DEFAULT 0";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_GAME_CONFIG = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_CONFIG + " TEXT";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_EXTEND_ONE = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_EXTEND_ONE + " TEXT";
    public static final String ALTER_TABLE_GAME_RECORD_GAME_EXTEND_TWO = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_EXTEND_TWO + " TEXT";
}
