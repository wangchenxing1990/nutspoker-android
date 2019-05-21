package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.NetCardCollectTable;

/**
 * Created by glp on 2016/8/17.
 */

public interface SQLV5 {

    //收藏列表，新增旧版牌谱的文件路径跟文件名
    String ALTER_TABLE_CARDCOLLECT_FILEPATH = "ALTER TABLE " + NetCardCollectTable.TABLE_NAME
            + " ADD COLUMN " + NetCardCollectTable.file_path + " TEXT ";
    String ALTER_TABLE_CARDCOLLECT_FILENAME = "ALTER TABLE " + NetCardCollectTable.TABLE_NAME
            + " ADD COLUMN " + NetCardCollectTable.file_name + " TEXT ";

}
