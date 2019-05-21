package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.GameRecordTable;

/**
 * Created by 周智慧 on 2017/5/24.
 */

public interface SQLV9 {
    String ALTER_TABLE_GAME_RECORD_GAME_PLAY_MODE = "ALTER TABLE " + GameRecordTable.TABLE_GAME_RECORD + " ADD COLUMN " + GameRecordTable.COLUMN_GAME_PLAY_MODE + " INTEGER DEFAULT 0";
}
