package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.AppMsgTable;

/**
 * Created by 周智慧 on 17/1/4.
 */

public interface SQLV6 {//app消息数据库新加COLUMN_CHECKIN_PLAYER_ID字段
    String ALTER_TABLE_APPMSGTABLE_TYPE = "ALTER TABLE " + AppMsgTable.TABLE_APP_MSG
            + " ADD COLUMN " + AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + " Varchar(32)";
}
