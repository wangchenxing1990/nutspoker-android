package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * 联系人表（用于储存加入德州圈的手机联系人）
 */
public class ContactTable implements BaseColumns {
    public static String TABLE_CONTACT = "contact";//联系人
    //
    public static String COLUMN_CONTACT_UID = "uid";
    public static String COLUMN_CONTACT_PHONE = "phone";
    public static String COLUMN_CONTACT_OS = "os";
    public static final String[] COLUMNS = {
            COLUMN_CONTACT_UID,
            COLUMN_CONTACT_PHONE,
            COLUMN_CONTACT_OS
    };
}
