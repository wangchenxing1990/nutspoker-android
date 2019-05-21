package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * APP系统消息
 */
public class AppMsgTable implements BaseColumns {
    public static final String TABLE_APP_MSG = "app_msg";
    public static final String COLUMN_TARGET_ID = "targetid";
    public static final String COLUMN_MESSAGEID = "messageid";
    public static final String COLUMN_FROMID = "fromid";
    public static final String COLUMN_CHECKIN_PLAYER_ID = "checkinplayerid";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_ATTACH = "attach";
    public static final String COLUMN_UNREAD = "unread";
    public static final String COLUMN_KEY = "key";//作为唯一字段（1.普通模式买入请求的话，用申请发送时间   2.比赛模式的买入请求用 gid-playerid）
    public static final String COLUMN_SORT_KEY = "sort_key";//用于排序的字段（1.控制带入用gid）

    public static final String[] COLUMNS = {
            COLUMN_TARGET_ID,
            COLUMN_MESSAGEID,
            COLUMN_FROMID,
            COLUMN_CHECKIN_PLAYER_ID,
            COLUMN_TYPE,
            COLUMN_TIME,
            COLUMN_STATUS,
            COLUMN_CONTENT,
            COLUMN_ATTACH,
            COLUMN_UNREAD,
            COLUMN_KEY,
            COLUMN_SORT_KEY
    };
}
