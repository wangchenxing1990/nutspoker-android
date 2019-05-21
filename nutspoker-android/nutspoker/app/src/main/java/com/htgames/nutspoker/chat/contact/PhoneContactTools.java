package com.htgames.nutspoker.chat.contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.bean.PhoneContactEntity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.contact.core.query.PinYin;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * 通讯录工具类
 */
public class PhoneContactTools {
    private final static String TAG = "PhoneContactTools";

    /**
     * 获取库Phon表字段
     **/
    private static final String[] PHONES_PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**
     * 头像ID
     **/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**
     * 联系人的ID
     **/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

    public static ArrayList<PhoneContactEntity> getPhoneContactList(Context context) {
        ArrayList<PhoneContactEntity> phoneContactList = new ArrayList<PhoneContactEntity>();
        ContentResolver resolver = context.getContentResolver();
        String sortOrder = "sort_key COLLATE LOCALIZED asc";
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, sortOrder);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
//                if (phoneNumber.contains("\\+86")) {
//                    phoneNumber = phoneNumber.replaceAll("\\+86", "");//去除
//                }
//                if (phoneNumber.contains("\\+886")) {
//                    phoneNumber = phoneNumber.replaceAll("\\+886", "");//去除
//                }
                phoneNumber = phoneNumber.replaceAll(" ", "").replaceAll("-", "");//去除-
                //当手机号码为空的或者为空字段 跳过当前循环
//                Log.d(TAG, phoneNumber + " 是不是手机号码:" + BaseTools.isMobileNO(phoneNumber));
                //1.手机号为空 2.手机号码格式不正确 3.手机号码是登录者的
//                if (TextUtils.isEmpty(phoneNumber) || !BaseTools.isMobileNO(phoneNumber) || UserPreferences.getInstance(context).getUserPhone().equals(phoneNumber))
                if (!isShowPhoneNo(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_male_head);
                }
                PhoneContactEntity phoneContactEntity = new PhoneContactEntity();
                phoneContactEntity.setName(contactName);
                phoneContactEntity.setPhone(phoneNumber);
//                phoneContactEntity.setSortKey(getSortKeyString(context, contactid));
                phoneContactEntity.setSortKey(PinYin.getLeadingLo(contactName));
                phoneContactEntity.setPinyin(PinYin.getPinYin(contactName));
//                mContactsPhonto.add(contactPhoto);
                phoneContactList.add(phoneContactEntity);
            }
            phoneCursor.close();
        }
        return phoneContactList;
    }

    private static String getSortKeyString(Context context ,long rawContactId) {
        String Where = ContactsContract.RawContacts.CONTACT_ID + " ="
                + rawContactId;
        String[] projection = {"sort_key"};
        Cursor cur = context.getContentResolver().query(
                ContactsContract.RawContacts.CONTENT_URI, projection, Where,
                null, null);
        int sortIndex = cur.getColumnIndex("sort_key");
        cur.moveToFirst();
        String sortValue = cur.getString(sortIndex);
        cur.close();
        return sortValue;
    }

    /**
     * 根据关键字搜索联系人(模糊搜索手机号)
     * @param context
     * @param word
     * @return
     */
//    public static ArrayList<PhoneContactEntity> getPhoneContactListByWord(Context context , String word) {
//        ArrayList<PhoneContactEntity> phoneContactList = new ArrayList<PhoneContactEntity>();
//        ContentResolver resolver = context.getContentResolver();
//        // 获取手机联系人
//        String selection;
//        String[] selectionArgs;
//        if(BaseTools.isMobileNO(word)){
//            //是手机号
//            selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
//            selectionArgs = new String[]{word};
//            Log.d(TAG , "是手机号：key:" + word);
//        }else{
////            selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "= ? " + "or " + ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
//            selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ?";
//            selectionArgs = new String[]{"%" + word + "%"};
//            Log.d(TAG , "模糊搜索名称：" + word);
//        }
//        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, selection, selectionArgs, null);
//        if (phoneCursor != null) {
//            while (phoneCursor.moveToNext()) {
//                //得到手机号码
//                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
//                //当手机号码为空的或者为空字段 跳过当前循环
//                Log.d(TAG, phoneNumber + " 是不是手机号码:" + BaseTools.isMobileNO(phoneNumber));
//                //1.手机号为空 2.手机号码格式不正确 3.手机号码是登录者的
//                if (TextUtils.isEmpty(phoneNumber) || !BaseTools.isMobileNO(phoneNumber) || DemoCache.getAccount().equals(phoneNumber))
//                    continue;
//                //得到联系人名称
//                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
//                //得到联系人ID
//                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
//                //得到联系人头像ID
//                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
//                //得到联系人头像Bitamp
//                Bitmap contactPhoto = null;
//                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
//                if (photoid > 0) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
//                    contactPhoto = BitmapFactory.decodeStream(input);
//                } else {
//                    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_male_head);
//                }
//                PhoneContactEntity phoneContactEntity = new PhoneContactEntity();
//                phoneContactEntity.setName(contactName);
//                phoneContactEntity.setPhone(phoneNumber);
////                mContactsPhonto.add(contactPhoto);
//                phoneContactList.add(phoneContactEntity);
//            }
//            phoneCursor.close();
//        }
//        return phoneContactList;
//    }

    /**
     * 即使关键字搜索联系人(模糊搜索手机号，联系人)
     * @param context
     * @param word
     * @return
     */
    public static ArrayList<PhoneContactEntity> getPhoneContactListByWord(Context context , String word) {
        ArrayList<PhoneContactEntity> phoneContactList = new ArrayList<PhoneContactEntity>();
        ContentResolver resolver = context.getContentResolver();
        // 获取手机联系人
        String selection;
        String[] selectionArgs;
        selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ?" + " or " + ContactsContract.CommonDataKinds.Phone.NUMBER + " like ?";
        selectionArgs = new String[]{"%" + word + "%" , "%" + word + "%"};
        LogUtil.i(TAG, "模糊搜索名称：" + word);
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, selection, selectionArgs, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                phoneNumber = phoneNumber.replaceAll(" ","");//去除空格
                phoneNumber = phoneNumber.replaceAll("-","");//去除-
                //当手机号码为空的或者为空字段 跳过当前循环
                LogUtil.i(TAG, phoneNumber + " 是不是手机号码:" + BaseTools.isMobileNO(phoneNumber));
                //1.手机号为空 2.手机号码格式不正确 3.手机号码是登录者的
                if (!isShowPhoneNo(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_male_head);
                }
                PhoneContactEntity phoneContactEntity = new PhoneContactEntity();
                phoneContactEntity.setName(contactName);
                phoneContactEntity.setPhone(phoneNumber);
//                mContactsPhonto.add(contactPhoto);
                phoneContactList.add(phoneContactEntity);
            }
            phoneCursor.close();
        }
        return phoneContactList;
    }

    /**
     * 判断该手机号码是否是通讯录
     * @param context
     * @param phone
     * @return
     */
    public static PhoneContactEntity getPhoneContact(Context context , String phone) {
        PhoneContactEntity phoneContactEntity = null;
        ContentResolver resolver = context.getContentResolver();
        // 获取手机联系人
        String selection;
        String[] selectionArgs;
        selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?";
        selectionArgs = new String[]{ phone };
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, selection, selectionArgs, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                LogUtil.i(TAG, phoneNumber + " 是不是手机号码:" + BaseTools.isMobileNO(phoneNumber));
                //1.手机号为空 2.手机号码格式不正确 3.手机号码是登录者的
//                if (TextUtils.isEmpty(phoneNumber)
//                        || !BaseTools.isMobileNO(phoneNumber)
//                        || DemoCache.getAccount().equals(phoneNumber))
                if (!isShowPhoneNo(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                //得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                //得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                //photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.mipmap.default_male_head);
                }
                phoneContactEntity = new PhoneContactEntity();
                phoneContactEntity.setName(contactName);
                phoneContactEntity.setPhone(phoneNumber);
                break;
            }
            phoneCursor.close();
        }
        return phoneContactEntity;
    }

    public static boolean isShowPhoneNo(String phoneNumber) {
        boolean isPhone = !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 6 && !UserPreferences.getInstance(DemoCache.getContext()).getUserPhone().equals(phoneNumber);
        return isPhone;
    }
}
