package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * Created by 周智慧 on 17/3/23.
 */

public class HordeTable implements BaseColumns {
    public static final String TABLE_HORDE_MSG = "horde_msg";
    public static final String COLUMN_TARGET_ID = "targetid";
    public static final String COLUMN_FROMID = "fromid";
    public static final String COLUMN_OUTER_TYPE = "outer_type";
    public static final String COLUMN_INNER_TYPE = "inner_type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ATTACH = "attach";
    public static final String COLUMN_UNREAD = "unread";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_TID = "tid";//俱乐部id
    public static final String COLUMN_TNAME = "tname";//俱乐部name
    public static final String COLUMN_TAVATAR = "tavatar";//俱乐部头像
    public static final String COLUMN_HORDE_ID = "horde_id";//部落id
    public static final String COLUMN_HORDE_NAME = "horde_name";//部落name

    public static final String[] COLUMNS = {
            COLUMN_TARGET_ID,
            COLUMN_FROMID,
            COLUMN_OUTER_TYPE,
            COLUMN_INNER_TYPE,
            COLUMN_TIME,
            COLUMN_STATUS,
            COLUMN_CONTENT,
            COLUMN_ATTACH,
            COLUMN_UNREAD,
            COLUMN_KEY,
            COLUMN_TID,
            COLUMN_TNAME,
            COLUMN_TAVATAR,
            COLUMN_HORDE_ID,
            COLUMN_HORDE_NAME
    };
}
