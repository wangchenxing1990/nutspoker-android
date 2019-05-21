package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.DataStatisticsTable;

/**
 * Created by glp on 2016/7/29.
 */

public interface SQLV4 {

    //数据统计
    String ALTER_TABLE_DATASTATISTICS_TYPE = "ALTER TABLE " + DataStatisticsTable.TABLE_DATA_STATISTICS
            + " ADD COLUMN " + DataStatisticsTable.COLUMN_DATA_TYPE + " INTEGER DEFAULT 0";
}
