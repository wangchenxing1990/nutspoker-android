package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.netease.nim.uikit.bean.PhoneUidEntity;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.ContactTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手机联系人德州圈用户数据库Helper
 */
public class ContactDBHelper extends BaseHelp {
    /**
     * 添加/更新 联系人数据
     * @param context
     * @param phoneUidList
     */
    public static void updateContactList(Context context, List<PhoneUidEntity> phoneUidList) {
        synchronized(BaseHelp.sHelp) {
            Cursor cursor = null;
            String selection = "";
            String[] selectionArgs = null;
            DBUtil mDBUtil = DBUtil.getInstance(context, ContactTable.TABLE_CONTACT);
            for (PhoneUidEntity phoneUidEntity : phoneUidList) {
                ContentValues values = new ContentValues();
                values.put(ContactTable.COLUMN_CONTACT_UID, phoneUidEntity.uid);
                values.put(ContactTable.COLUMN_CONTACT_PHONE, phoneUidEntity.phone);
                values.put(ContactTable.COLUMN_CONTACT_OS, phoneUidEntity.os);
                //
                selection = ContactTable.COLUMN_CONTACT_PHONE + "= ? and " + ContactTable.COLUMN_CONTACT_OS + " = ?";
                selectionArgs = new String[]{phoneUidEntity.phone, String.valueOf(phoneUidEntity.os)};
                cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    mDBUtil.insertData(values);
                } else {
                    mDBUtil.updateData(values, selection, selectionArgs);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
            mDBUtil.close();
        }

    }

    /**
     * 获取联系人列表（德州圈的注册用户）
     *
     * @param context
     * @return
     */
//    public static ArrayList<PhoneUidEntity> getContactList(Context context) {
//        DBUtil mDBUtil = DBUtil.getInstance(context, ContactTable.TABLE_CONTACT);
//        Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
//        ArrayList<PhoneUidEntity> contactList = new ArrayList<PhoneUidEntity>();
//        if (cursor != null) {
//            PhoneUidEntity phoneUidEntity = null;
//            while (cursor.moveToNext()) {
//                String phone = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_PHONE));
//                String uid = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_UID));
//                phoneUidEntity = new PhoneUidEntity(phone, uid);
//                contactList.add(phoneUidEntity);
//            }
//            cursor.close();
//        }
//        mDBUtil.close();
//        return contactList;
//    }

    /**
     * 获取联系人列表（德州圈的注册用户）
     *
     * @param context
     * @return
     */
    public static Map<String, ArrayList<String>> getContactMap(Context context) {
        synchronized(BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, ContactTable.TABLE_CONTACT);
            Cursor cursor = mDBUtil.selectData(null, null, null, null, null, null);
            Map<String, ArrayList<String>> contactMap = new HashMap<String, ArrayList<String>>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String phone = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_PHONE));
                    String uid = cursor.getString(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_UID));
                    int os = cursor.getInt(cursor.getColumnIndex(ContactTable.COLUMN_CONTACT_OS));
                    if (!contactMap.containsKey(phone)) {
                        ArrayList<String> uids = new ArrayList<String>();
                        uids.add(uid);
                        contactMap.put(phone, uids);
                    } else {
                        ArrayList<String> uids = contactMap.get(phone);
                        uids.add(uid);
                        contactMap.put(phone, uids);
                    }
                }
                cursor.close();
            }
            mDBUtil.close();
            return contactMap;
        }

    }
}
