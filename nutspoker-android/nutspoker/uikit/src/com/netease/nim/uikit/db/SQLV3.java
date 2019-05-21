package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.AppMsgTable;
import com.netease.nim.uikit.db.table.GameRecordTable;

/**
 * 3版本数据库
 */
public class SQLV3 {
    //系统消息
    public static final String ALTER_TABLE_APP_MEG_SORT_KEY = "ALTER TABLE "
            + AppMsgTable.TABLE_APP_MSG + " ADD COLUMN " + AppMsgTable.COLUMN_SORT_KEY + " TEXT DEFAULT ''";

    //牌局信息
    public static final String ALTER_TABLE_GAME_RECORD_TOTAL_PLAYER = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_TOTAL_PLAYER + " INTEGER DEFAULT 0";
    public static final String ALTER_TABLE_GAME_RECORD_SBLINDS_INDEX = "ALTER TABLE "
            + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_SBLINDS_INDEX + " INTEGER DEFAULT 0";
}
