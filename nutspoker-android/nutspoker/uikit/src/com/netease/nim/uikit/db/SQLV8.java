package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.HordeTable;

/**
 * Created by 周智慧 on 17/3/23.
 */

public interface SQLV8 {
    /**
     * 创建部落相关的表
     */
    public static final String CREATE_TABLE_HORDE = "CREATE TABLE IF NOT EXISTS "
            + HordeTable.TABLE_HORDE_MSG
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HordeTable.COLUMN_TARGET_ID + " Varchar(32) , "
            + HordeTable.COLUMN_FROMID + " Varchar(32) , "
            + HordeTable.COLUMN_OUTER_TYPE + " INTEGER , "
            + HordeTable.COLUMN_INNER_TYPE + " INTEGER , "
            + HordeTable.COLUMN_TIME + " LONG , "
            + HordeTable.COLUMN_STATUS + " INTEGER , "
            + HordeTable.COLUMN_CONTENT + " TEXT , "
            + HordeTable.COLUMN_ATTACH + " TEXT , "
            + HordeTable.COLUMN_KEY + " TEXT, "
            + HordeTable.COLUMN_TID + " TEXT, "
            + HordeTable.COLUMN_TNAME + " TEXT, "
            + HordeTable.COLUMN_TAVATAR + " TEXT, "
            + HordeTable.COLUMN_HORDE_ID + " TEXT, "
            + HordeTable.COLUMN_HORDE_NAME + " TEXT, "
            + HordeTable.COLUMN_UNREAD + " INTEGER )";
}
