package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import com.htgames.nutspoker.chat.app_msg.attach.*;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.tool.AppMessageJsonTools;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.AppMsgTable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 */
public class AppMsgDBHelper extends BaseHelp {
    private final static String TAG = "AppMsgDBHelper";
    public final static int TYPE_ALL = 0;
    public final static int TYPE_NOTICE = 1;
    public final static int TYPE_CONTROL_CENTER = 2;

    /**
     * 获取所有未读消息数量
     *
     * @param context
     * @return
     */
    public static AppMessage getLastAppMessage(Context context) {
        synchronized(BaseHelp.sHelp){
            String orderBy = AppMessageConstants.KEY_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, null, null, null, null, orderBy);
            if (cursor == null) {
                return null;
            }
            AppMessage mAppMessage = null;
            if (cursor.moveToNext()) {
                mAppMessage = getAppMessage(cursor);
            }
            cursor.close();
            mDBUtil.close();
            return mAppMessage;
        }
    }

    /**
     * 设置所有消息为已读
     *
     * @param context
     */
    public static void resetAppMessageUnreadCountByType(Context context) {
        synchronized(BaseHelp.sHelp){
            String selection = null;
            String[] selectionArgs = null;
            selection = AppMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_UNREAD, false);
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }

    //重置指定类型消息为已读
    public static void resetAppMessageUnreadCountByType(Context context , int type) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? ) and "
                        + AppMsgTable.COLUMN_UNREAD + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.AppNotice.getValue()),
                        String.valueOf(AppMessageType.GameOver.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChipsResult.getValue()),
                        String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            } else if (type == TYPE_CONTROL_CENTER) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ?  ) and "
                        + AppMsgTable.COLUMN_UNREAD + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.GameBuyChips.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                        String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_UNREAD, false);
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }

    public static void resetMatchBuyChipsUnreadCountByGid(Context context , String gid) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_UNREAD + "= ? and "
                    + AppMsgTable.COLUMN_TYPE + "= ? and "
                    + AppMsgTable.COLUMN_SORT_KEY + "= ?";
            String[] selectionArgs = new String[]{
                    String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD),
                    String.valueOf(AppMessageType.MatchBuyChips),
                    gid};
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_UNREAD, false);
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }

    public static void resetAppMessageUnreadCountByGid(Context context, String gid) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            selection = AppMsgTable.COLUMN_UNREAD + "= ? and " + AppMsgTable.COLUMN_TYPE + "= ? and "
                    + AppMsgTable.COLUMN_SORT_KEY + "= ?";
            selectionArgs = new String[]{
                    String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD),
                    String.valueOf(AppMessageType.MatchBuyChips),
                    gid };
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_UNREAD, false);
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }

    }

    //重置一条消息为已读
    public static void resetOneAppMessageUnreadCount(Context context, AppMessage appMessage) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            selection = AppMsgTable.COLUMN_UNREAD + "= ? and "
                    + AppMsgTable.COLUMN_TYPE + "= ? and "
                    + AppMsgTable.COLUMN_KEY + "= ?";
            selectionArgs = new String[]{
                    String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD),
                    String.valueOf(appMessage.type.getValue()),
                    String.valueOf(appMessage.key)};
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_UNREAD, false);
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }

    }

    /**
     * 设置系统通知状态
     *
     * @param context
     * @param status
     */
    public static void setSystemMessageStatus(Context context, AppMessageType type, String checkinPlayerId, String key, AppMessageStatus status) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ? and " + AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + "= ? and " + AppMsgTable.COLUMN_KEY + "= ?";
            String[] selectionArgs = new String[]{String.valueOf(type.getValue()), checkinPlayerId, key};
            ContentValues contentValues = new ContentValues();
            contentValues.put(AppMsgTable.COLUMN_STATUS, status.getValue());
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }

    /**
     * 获取所有未读消息数量
     *
     * @param context
     * @return
     */
    public static int queryAllAppMessageUnreadCount(Context context) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_UNREAD + "= ?";
            String[] selectionArgs = new String[]{String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            int unreadCount = 0;
            if (cursor != null) {
                unreadCount = cursor.getCount();
                cursor.close();
            }
            mDBUtil.close();
            LogUtil.i(TAG, "消息总数 ： " + unreadCount);
            return unreadCount;
        }
    }

    /**
     * 获取指定类型未读消息数量
     *
     * @param context
     * @return
     */
    public static int queryAppMessageUnreadCountByType(Context context , int type) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? ) and "
                        + AppMsgTable.COLUMN_UNREAD + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.AppNotice.getValue()),
                        String.valueOf(AppMessageType.GameOver.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChipsResult.getValue()),
                        String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            } else if (type == TYPE_CONTROL_CENTER) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ?  ) and "
                        + AppMsgTable.COLUMN_UNREAD + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.GameBuyChips.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                        String.valueOf(AppMessageConstants.MESSAGE_STATUS_UNREAD)};
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            int unreadCount = 0;
            if (cursor != null) {
                unreadCount = cursor.getCount();
                cursor.close();
            }
            mDBUtil.close();
            LogUtil.i(TAG, "type:" + type + ";消息总数 ： " + unreadCount);
            return unreadCount;
        }
    }

    /**
     * 获取指定类型未处理的消息数量（原始的消息，未做任何处理，但是可能已读）
     *
     * @param context
     * @return
     */
    public static int queryAppMessageInitCountByType(Context context , int type) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? ) and "
                        + AppMsgTable.COLUMN_STATUS + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.AppNotice.getValue()),
                        String.valueOf(AppMessageType.GameOver.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChipsResult.getValue()),
                        String.valueOf(AppMessageStatus.init.getValue())};
            } else if (type == TYPE_CONTROL_CENTER) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ?  ) and "
                        + AppMsgTable.COLUMN_STATUS + "= ?";
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.GameBuyChips.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                        String.valueOf(AppMessageStatus.init.getValue())};
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            int unreadCount = 0;
            if (cursor != null) {
                unreadCount = cursor.getCount();
                cursor.close();
            }
            mDBUtil.close();
            LogUtil.i(TAG, "type:" + type + ";消息总数 ： " + unreadCount);
            return unreadCount;
        }
    }

    //判断信息是否已经存在   消息可能反着发，数据库中的消息和刚来的消息是同一个但是数据库中的更新时间戳更大（离线的时候会出现这种情况，必现）
    public static boolean isAppMessageExist(Context context, AppMessage appMessage) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ? and " +
                    AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + "= ? and " + AppMsgTable.COLUMN_KEY + "= ?";
            int messageType = appMessage.type.getValue();
            String[] selectionArgs = new String[]{String.valueOf(messageType), appMessage.checkinPlayerId, appMessage.key};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            boolean isExist = false;
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    isExist = true;
                }
                cursor.close();
            }
            mDBUtil.close();
            if (isExist) {
                LogUtil.i(TAG, "当前信息已经存在");
            }
            return isExist;
        }
    }

    //判断信息是否已经存在   消息可能反着发，数据库中的消息和刚来的消息是同一个但是数据库中的更新时间戳更大（离线的时候会出现这种情况，必现）  ---蛋疼的云信
    public static boolean isNewerAppMessageExist(Context context, AppMessage appMessage) {
        synchronized(BaseHelp.sHelp) {
            String orderBy = AppMsgTable.COLUMN_TIME + " DESC";
            String selection = AppMsgTable.COLUMN_TYPE + "= ? and " +
                    AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + "= ? and " + AppMsgTable.COLUMN_KEY + "= ?";
            int messageType = appMessage.type.getValue();
            String[] selectionArgs = new String[]{String.valueOf(messageType), appMessage.checkinPlayerId, appMessage.key};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            boolean isExist = false;
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                long time = cursor.getLong(cursor.getColumnIndex(AppMsgTable.COLUMN_TIME));
                if (time > appMessage.time) {
                    isExist = true;
                }
                cursor.close();
            }
            mDBUtil.close();
            if (isExist) {
                LogUtil.i(TAG, "当前更新的信息已经存在");
            }
            return isExist;
        }
    }

    /**
     * 删除消息
     *
     * @param context
     * @param appMessage
     */
    public static void deleteAppMessage(Context context, AppMessage appMessage) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ? and " +
                    AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + "= ? and " + AppMsgTable.COLUMN_KEY + "= ?";
            int messageType = appMessage.type.getValue();
            String[] selectionArgs = new String[]{String.valueOf(messageType), appMessage.checkinPlayerId, appMessage.key};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }

    }

    //清除已经操作的带入消息
    public static void clearDealedBuyChips(Context context) {
        synchronized(BaseHelp.sHelp) {
            String selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? ) and "
                    + AppMsgTable.COLUMN_STATUS + "<> ?";
            String[] selectionArgs = new String[]{
                    String.valueOf(AppMessageType.GameBuyChips.getValue()),
                    String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                    String.valueOf(AppMessageStatus.init.getValue())};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }

    }

    //清除所有出系统公告外的消息
    public static void clearAppMessageExceptNotice(Context context) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ?";
            String[] selectionArgs = new String[]{
                    String.valueOf(AppMessageType.GameBuyChips.getValue()), String.valueOf(AppMessageType.GameOver.getValue())};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }

    }

    //根据类型清除消息
    public static void clearAppMessagesByType(Context context, AppMessageType appMessageType) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ?";
            String[] selectionArgs = new String[]{String.valueOf(appMessageType.getValue())};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }
    }

    //清空消息中心的所有消息
    public static void clearNoticeCenterMessages(Context context) {
        synchronized(BaseHelp.sHelp) {
            String selection = AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? or "
                    + AppMsgTable.COLUMN_TYPE + "= ?";
            String[] selectionArgs = new String[]{
                    String.valueOf(AppMessageType.AppNotice.getValue()),
                    String.valueOf(AppMessageType.MatchBuyChipsResult.getValue()),
                    String.valueOf(AppMessageType.GameOver.getValue())};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }

    }

    //清空控制中心的所有消息
    public static void clearControlCenterMessages(Context context, AppMessageStatus appMessageStatus, boolean isThisStatus) {
        int status = appMessageStatus == null ? -1 : appMessageStatus.getValue();
        synchronized(BaseHelp.sHelp) {
            String selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ?) and " + (AppMsgTable.COLUMN_STATUS + (isThisStatus ? "= ? " : "!= ?"));
            String[] selectionArgs = new String[]{"" + AppMessageType.GameBuyChips.getValue(), "" + AppMessageType.MatchBuyChips.getValue(), String.valueOf(status)};
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            mDBUtil.deleteData(selection, selectionArgs);
            mDBUtil.close();
        }
    }

    public static void addAppMessage(Context context, AppMessage appMessage) {
        synchronized(BaseHelp.sHelp) {
            if (TextUtils.isEmpty(DemoCache.getAccount()) || !AppMessageHelper.isKnowAppMessage(appMessage)) {
                //1.登录才入库 2.不是目前支持的格式(未知类型)
                return;
            }
            Cursor cursor = null;
            String selection = "";
            String[] selectionArgs = null;
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            ContentValues values = new ContentValues();
            values.put(AppMsgTable.COLUMN_TARGET_ID, appMessage.targetId);
            values.put(AppMsgTable.COLUMN_MESSAGEID, appMessage.messageId);
            values.put(AppMsgTable.COLUMN_CHECKIN_PLAYER_ID, appMessage.checkinPlayerId);
            values.put(AppMsgTable.COLUMN_FROMID, appMessage.fromId);
            values.put(AppMsgTable.COLUMN_TYPE, appMessage.type.getValue());
            values.put(AppMsgTable.COLUMN_TIME, appMessage.time);
            values.put(AppMsgTable.COLUMN_STATUS, appMessage.status.getValue());
            values.put(AppMsgTable.COLUMN_CONTENT, appMessage.content);
            values.put(AppMsgTable.COLUMN_ATTACH, appMessage.attach);
            values.put(AppMsgTable.COLUMN_KEY, appMessage.key);
            if (DealerConstant.isNumeric(appMessage.sortKey)) {
                values.put(AppMsgTable.COLUMN_SORT_KEY, Long.parseLong(appMessage.sortKey));//V3：排序
            } else {
                values.put(AppMsgTable.COLUMN_SORT_KEY, appMessage.sortKey);//V3：排序
            }
            values.put(AppMsgTable.COLUMN_UNREAD, appMessage.unread);
//        selection = AppMsgTable.COLUMN_TYPE + "= ? and " + AppMsgTable.COLUMN_FROMID + "= ? and "
//                +  AppMsgTable.COLUMN_KEY + " = ?";
//        selectionArgs = new String[]{String.valueOf(appMessage.type.getValue())
//                    , appMessage.getFromId() , appMessage.key};
            selection = AppMsgTable.COLUMN_TYPE + "= ? and "
                    + AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + "= ? and " + AppMsgTable.COLUMN_KEY + " = ?";
            int messageType = appMessage.type.getValue();
            selectionArgs = new String[]{String.valueOf(messageType), appMessage.checkinPlayerId, appMessage.key};
            cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                if (appMessage.type == AppMessageType.GameBuyChips /*&& appMessage.getStatus() == AppMessageStatus.init*/) {
                    //如果不存在，推送过来的买入请求，并且不是初始化的，不入库
                    mDBUtil.insertData(values);
                } else if (appMessage.type == AppMessageType.MatchBuyChips /*&& appMessage.getStatus() == AppMessageStatus.init*/) {
                    //如果不存在，推送过来的买入请求，并且不是初始化的，不入库
                    mDBUtil.insertData(values);
                } else if (appMessage.type == AppMessageType.AppNotice) {
                    mDBUtil.insertData(values);
                } else if (appMessage.type == AppMessageType.GameOver) {
                    mDBUtil.insertData(values);
                } else if (appMessage.type == AppMessageType.MatchBuyChipsResult) {
                    mDBUtil.insertData(values);
                }
            } else {
                mDBUtil.updateData(values, selection, selectionArgs);
            }
            if (cursor != null) {
                cursor.close();
            }
            mDBUtil.close();
        }

    }

    public static ArrayList<AppMessage> queryAppMessage(Context context, int type) {
        synchronized(BaseHelp.sHelp) {
            String orderBy = AppMessageConstants.KEY_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            String selection = "";
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                selection = AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ?";
                selectionArgs = new String[]{String.valueOf(AppMessageType.AppNotice.getValue()),
                        String.valueOf(AppMessageType.GameOver.getValue()), "" + AppMessageType.MatchBuyChipsResult.getValue()};
            } else if (type == TYPE_CONTROL_CENTER) {
                orderBy = AppMsgTable.COLUMN_SORT_KEY + " DESC , " +
                        AppMsgTable.COLUMN_STATUS + " ASC , " +
                        AppMessageConstants.KEY_TIME + " DESC";
                selection = AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ?";
                selectionArgs = new String[]{"" + AppMessageType.GameBuyChips.getValue(),
                        "" + AppMessageType.MatchBuyChips.getValue()};
            }
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            if (cursor == null) {
                return null;
            }
            ArrayList<AppMessage> mAppMessagesList = new ArrayList<AppMessage>();
            int lastIndex = -1;
            int gidIndex = 0;
            while (cursor.moveToNext()) {
                AppMessage nextMsg = getAppMessage(cursor);
                if (lastIndex >= 0 && mAppMessagesList.size() > lastIndex) {
                    AppMessage lastMsg = mAppMessagesList.get(lastIndex);
                    if (lastMsg.sortKey != null && lastMsg.sortKey.equals(nextMsg.sortKey)) {
                        gidIndex++;
                    } else {
                        gidIndex = 0;
                    }
                }
                nextMsg.gidGroupIndex = gidIndex;
                mAppMessagesList.add(nextMsg);
                lastIndex++;
            }
            cursor.close();
            mDBUtil.close();
            return mAppMessagesList;
        }
    }

    public static ArrayList<AppMessage> queryAppMessageWithStatus(Context context, int type, AppMessageStatus appMessageStatus, boolean isThisStatus) {
        int msgStatus = appMessageStatus == null ? -1 : appMessageStatus.getValue();
        synchronized(BaseHelp.sHelp) {
            String orderBy = AppMessageConstants.KEY_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            String selection = "";
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ? ) and "
                        + (isThisStatus ? (AppMsgTable.COLUMN_STATUS + "= ?") : (AppMsgTable.COLUMN_STATUS + "!= ?"));
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.AppNotice.getValue()),
                        String.valueOf(AppMessageType.GameOver.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChipsResult.getValue()),
                        String.valueOf(msgStatus)};
            } else if (type == TYPE_CONTROL_CENTER) {
                orderBy = AppMsgTable.COLUMN_SORT_KEY + " DESC , " +
                        AppMsgTable.COLUMN_STATUS + " ASC , " +
                        AppMessageConstants.KEY_TIME + " DESC";
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or "
                        + AppMsgTable.COLUMN_TYPE + "= ?  ) and "
                        + (isThisStatus ? (AppMsgTable.COLUMN_STATUS + "= ?") : (AppMsgTable.COLUMN_STATUS + "!= ?"));
                selectionArgs = new String[]{
                        String.valueOf(AppMessageType.GameBuyChips.getValue()),
                        String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                        String.valueOf(msgStatus)};
            }
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            if (cursor == null) {
                return null;
            }
            ArrayList<AppMessage> mAppMessagesList = new ArrayList<AppMessage>();
            int lastIndex = -1;
            int gidIndex = 0;
            while (cursor.moveToNext()) {
                AppMessage nextMsg = getAppMessage(cursor);
                if (lastIndex >= 0 && mAppMessagesList.size() > lastIndex) {
                    AppMessage lastMsg = mAppMessagesList.get(lastIndex);
                    if (lastMsg.sortKey != null && lastMsg.sortKey.equals(nextMsg.sortKey)) {
                        gidIndex++;
                    } else {
                        gidIndex = 0;
                    }
                }
                nextMsg.gidGroupIndex = gidIndex;
                mAppMessagesList.add(nextMsg);
                lastIndex++;
            }
            cursor.close();
            mDBUtil.close();
            return mAppMessagesList;
        }
    }

    public static ArrayList<AppMessage> queryAppMessageByPage(Context context, int type, long sortKey) {//分页查询, 系统消息是根据time分页，控制中心是根据gid
        synchronized(BaseHelp.sHelp) {
            String orderBy = AppMessageConstants.KEY_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            //
            String selection = "";
            String[] selectionArgs = null;
            if (type == TYPE_NOTICE) {
                orderBy = AppMessageConstants.KEY_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? ) and " + AppMessageConstants.KEY_TIME + "< ?";
                selectionArgs = new String[]{
                        "" + AppMessageType.AppNotice.getValue(),
                        "" + AppMessageType.GameOver.getValue(),
                        "" + AppMessageType.MatchBuyChipsResult.getValue(),
                        "" + sortKey};
            } else if (type == TYPE_CONTROL_CENTER) {
                orderBy = AppMsgTable.COLUMN_SORT_KEY + " DESC , " +
                        AppMsgTable.COLUMN_STATUS + " ASC , " +
                        AppMessageConstants.KEY_TIME + " DESC";// limit " + 20000;//控制中心最多显示20000条
                selection = AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ?";
                selectionArgs = new String[]{"" + AppMessageType.GameBuyChips.getValue(),
                        "" + AppMessageType.MatchBuyChips.getValue()};
                /*orderBy = AppMsgTable.COLUMN_SORT_KEY + " DESC , " +
                        AppMsgTable.COLUMN_STATUS + " ASC , " +
                        AppMessageConstants.KEY_TIME + " DESC limit " + GameConstants.MAX_SQL_PAGE_SIZE;
                selection = "(" + AppMsgTable.COLUMN_TYPE + "= ? or " + AppMsgTable.COLUMN_TYPE + "= ? ) and " + AppMsgTable.COLUMN_SORT_KEY + "<= ?";
                selectionArgs = new String[]{"" + AppMessageType.GameBuyChips.getValue(),
                        "" + AppMessageType.MatchBuyChips.getValue(),
                        "" + sortKey};*/
            }
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            if (cursor == null) {
                return null;
            }
            ArrayList<AppMessage> mAppMessagesList = new ArrayList<AppMessage>();
            while (cursor.moveToNext()) {
                mAppMessagesList.add(getAppMessage(cursor));
            }
            cursor.close();
            mDBUtil.close();
            return mAppMessagesList;
        }
    }

    public static ArrayList<AppMessage> queryInitMatchBuyChipsByGid(Context context, String gid) {
        synchronized(BaseHelp.sHelp) {
            String orderBy = AppMessageConstants.KEY_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, AppMsgTable.TABLE_APP_MSG);
            //
            LogUtil.i(TAG, "gid:" + gid);
            String selection = AppMsgTable.COLUMN_TYPE + "= ? and "
                    + AppMsgTable.COLUMN_SORT_KEY + "= ? and " + AppMsgTable.COLUMN_STATUS + "= ?";
            String[] selectionArgs = new String[]{
                    String.valueOf(AppMessageType.MatchBuyChips.getValue()),
                    gid,
                    String.valueOf(AppMessageStatus.init.getValue())};
            //
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            if (cursor == null) {
                return null;
            }
            ArrayList<AppMessage> mAppMessagesList = new ArrayList<AppMessage>();
            while (cursor.moveToNext()) {
                mAppMessagesList.add(getAppMessage(cursor));
            }
            cursor.close();
            mDBUtil.close();
            return mAppMessagesList;
        }

    }

    public static AppMessage getAppMessage(Cursor cursor) {
        synchronized(BaseHelp.sHelp) {
            AppMessageType messageType = AppMessageType.typeOfValue(cursor.getInt(cursor.getColumnIndex(AppMsgTable.COLUMN_TYPE)));
            String fromAccount = cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_FROMID));
            String targetid = cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_TARGET_ID));
            String checkinPlayerId = cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_CHECKIN_PLAYER_ID));
            AppMessage mAppMessage = new AppMessage();
            mAppMessage.messageId = (cursor.getLong(cursor.getColumnIndex(AppMsgTable.COLUMN_MESSAGEID)));
            mAppMessage.targetId = (targetid);
            mAppMessage.fromId = (fromAccount);
            mAppMessage.checkinPlayerId = checkinPlayerId;
            mAppMessage.content = (cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_CONTENT)));
            mAppMessage.type = (messageType);
            mAppMessage.key = (cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_KEY)));
            //
            mAppMessage.sortKey = (cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_SORT_KEY)));//V3：排序
//        Log.d(TAG, "" + cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_SORT_KEY)));
            //
            mAppMessage.status = (AppMessageStatus.statusOfValue(cursor.getInt(cursor.getColumnIndex(AppMsgTable.COLUMN_STATUS))));
            mAppMessage.time = (cursor.getLong(cursor.getColumnIndex(AppMsgTable.COLUMN_TIME)));
            int unread = cursor.getInt(cursor.getColumnIndex(AppMsgTable.COLUMN_UNREAD));
            mAppMessage.unread = (unread == AppMessageConstants.MESSAGE_STATUS_UNREAD ? true : false);
            //
            String attach = cursor.getString(cursor.getColumnIndex(AppMsgTable.COLUMN_ATTACH));
            LogUtil.i(TAG, attach);
            mAppMessage.attach = (attach);
            //
            if (messageType == AppMessageType.GameBuyChips) {
                try {
                    JSONObject jsonObject = new JSONObject(attach);
                    BuyChipsNotify attachData = AppMessageJsonTools.getBuyChipsNotify(jsonObject);
                    if (attachData != null) {
                        mAppMessage.checkinPlayerId = fromAccount;
                        mAppMessage.attachObject = (attachData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (messageType == AppMessageType.AppNotice) {
                try {
                    JSONObject jsonObject = new JSONObject(attach);
                    AppNotify attachData = AppMessageJsonTools.getAppNotify(jsonObject);
                    if (attachData != null) {
                        mAppMessage.attachObject = (attachData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (messageType == AppMessageType.GameOver) {
                try {
                    JSONObject jsonObject = new JSONObject(attach);
                    GameOverNotify attachData = AppMessageJsonTools.getGameOverNotify(jsonObject);
                    if (attachData != null) {
                        mAppMessage.attachObject = (attachData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (messageType == AppMessageType.MatchBuyChips) {
                try {
                    JSONObject jsonObject = new JSONObject(attach);
                    MatchBuyChipsNotify attachData = AppMessageJsonTools.getMatchBuyChipsNotify(jsonObject);
                    if (attachData != null) {
                        checkinPlayerId = attachData.userId;
                        if (TextUtils.isEmpty(checkinPlayerId)) {
                            checkinPlayerId = mAppMessage.fromId;
                        }
                        if (TextUtils.isEmpty(checkinPlayerId)) {
                            checkinPlayerId = mAppMessage.targetId;
                        }
                        mAppMessage.checkinPlayerId = checkinPlayerId;
                        mAppMessage.attachObject = (attachData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (messageType == AppMessageType.MatchBuyChipsResult) {
                try {
                    JSONObject jsonObject = new JSONObject(attach);
                    MatchBuyChipsResultNotify attachData = AppMessageJsonTools.getMatchBuyChipsResultNotify(jsonObject);
                    if (attachData != null) {
                        mAppMessage.attachObject = (attachData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return mAppMessage;
        }

    }
}
