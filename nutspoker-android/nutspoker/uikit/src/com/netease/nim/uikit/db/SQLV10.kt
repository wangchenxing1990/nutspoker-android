package com.netease.nim.uikit.db

import com.netease.nim.uikit.db.table.GameRecordTable

/**
 * Created by 周智慧 on 2017/7/26.
 */
object SQLV10 {
    var ALTER_TABLE_GAME_RECORD_MATCH_TYPE: String = "ALTER TABLE ${GameRecordTable.TABLE_GAME_RECORD} ADD COLUMN ${GameRecordTable.COLUMN_MATCH_TYPE} INTEGER DEFAULT 0"
}