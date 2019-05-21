package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * 系统消息
 */
public class SystemMsgTable implements BaseColumns {
    public static final String TABLE_SYSTEM_MSG = "system_msg";
    public static final String COLUMN_TARGET_ID = "targetid";
    public static final String COLUMN_MESSAGEID = "messageid";
    public static final String COLUMN_FROMID = "fromid";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ATTACH = "attach";
    public static final String COLUMN_UNREAD = "unread";

    public static final String[] COLUMNS = {
            COLUMN_TARGET_ID,
            COLUMN_MESSAGEID,
            COLUMN_FROMID,
            COLUMN_TYPE,
            COLUMN_TIME,
            COLUMN_STATUS,
            COLUMN_CONTENT,
            COLUMN_ATTACH,
            COLUMN_UNREAD
    };
}
