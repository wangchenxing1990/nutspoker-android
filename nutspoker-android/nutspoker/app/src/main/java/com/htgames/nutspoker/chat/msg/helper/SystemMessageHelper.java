package com.htgames.nutspoker.chat.msg.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.htgames.nutspoker.chat.msg.attach.SystemMessageAttach;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.SystemMsgTable;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;

import java.util.ArrayList;

/**
 * 系统消息
 */
public class SystemMessageHelper {
    private final static String TAG = "SystemMessageHelper";
    /** 所有 */
    public final static int TYPE_MESSAGE_ALL = 0;
    /** 好友邀请 */
    public final static int TYPE_MESSAGE_FRIEND = 1;
    /** 入群邀请*/
    public final static int TYPE_MESSAGE_TEAM_INVITE = 2;
    /** 申请入群*/
    public final static int TYPE_MESSAGE_TEAM_APPLY = 3;
    //自己申请入群消息，邀请入群消息
    public final static int TYPE_MESSAGE_TEAM_MSG = 4;//"俱乐部"主邀请自己进部、"俱乐部"主拒绝自己进部
    public final static int TYPE_MESSAGE_TEAM_ALL = 5;//all包括俱乐部相关的所有四种ApplyJoinTeam, RejectTeamApply, TeamInvite, DeclineTeamInvite
    public final static int TYPE_MESSAGE_TEAM_UNREAD = 6;//未读
    public final static int TYPE_MESSAGE_TEAM_INIT = 7;//未处理
    //消息状态未读
    public final static int MESSAGE_STATUS_UNREAD = 1;
    public final static int MESSAGE_STATUS_READED = 0;

    /**
     * 是否标记为已读
     * @param systemMessage
     * @return
     */
    public static boolean isTagMessageReaded(com.netease.nimlib.sdk.msg.model.SystemMessage systemMessage) {
        if (systemMessage.getAttachObject() instanceof AddFriendNotify) {
            AddFriendNotify attachData = (AddFriendNotify) systemMessage.getAttachObject();
            if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                //通过了你的好友请求，设置为已读
                LogUtil.i(TAG, "通过了你的好友请求");
                return true;
            }
        }
        return false;
    }

    /**
     * 查询系统通知列表
     * @return
     */
    public static ArrayList<SystemMessage> querySystemMessages(){
        return null;
    }

    /**
     * 根据类型获取系统消息列表
     * @param context
     * @param systemMessageType
     * @return
     */
    public static ArrayList<SystemMessage> querySystemMessageByType(Context context , int systemMessageType , String clubId) {
        String orderBy =  SystemMsgTable.COLUMN_TIME + " DESC";//"_id DESC";
        String selection = null;
        String[] selectionArgs = null;
        if(systemMessageType == TYPE_MESSAGE_FRIEND) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.AddFriend.getValue())};
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_INVITE) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()), String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())};
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_APPLY){
            if(TextUtils.isEmpty(clubId)) {
                selection = SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ?";
                selectionArgs = new String[]{String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()), String.valueOf(SystemMessageType.RejectTeamApply.getValue())};
            } else {
                selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? ) and " + SystemMsgTable.COLUMN_TARGET_ID + "= ?";
                selectionArgs = new String[]{String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()),
                        String.valueOf(SystemMessageType.RejectTeamApply.getValue()) , clubId};
            }
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_MSG){
            selection = SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? or "+SystemMsgTable.COLUMN_TYPE+"= ? " ;
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()), String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())
                ,String.valueOf(SystemMessageType.RejectTeamApply.getValue())};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_ALL) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE + "= ? ";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()), String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())
                    ,String.valueOf(SystemMessageType.RejectTeamApply.getValue()), String.valueOf(SystemMessageType.ApplyJoinTeam.getValue())};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_UNREAD) {
            selection = SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{"1"};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_INIT) {
            selection = SystemMsgTable.COLUMN_STATUS + "= ?";
            selectionArgs = new String[]{"0"};
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
        if(cursor == null){
            return null;
        }
        ArrayList<SystemMessage> systemMessagesList = new ArrayList<SystemMessage>();
        while(cursor.moveToNext()){
            SystemMessageType messageType = SystemMessageType.typeOfValue(cursor.getInt(cursor.getColumnIndex(SystemMsgTable.COLUMN_TYPE)));
            String fromAccount = cursor.getString(cursor.getColumnIndex(SystemMsgTable.COLUMN_FROMID));
            String targetid = cursor.getString(cursor.getColumnIndex(SystemMsgTable.COLUMN_TARGET_ID));
            //
            SystemMessage systemMessage = new SystemMessage();
            systemMessage.setMessageId(cursor.getLong(cursor.getColumnIndex(SystemMsgTable.COLUMN_MESSAGEID)));
            systemMessage.setTargetId(targetid);
            systemMessage.setFromAccount(fromAccount);
            systemMessage.setContent(cursor.getString(cursor.getColumnIndex(SystemMsgTable.COLUMN_CONTENT)));
            systemMessage.setType(messageType);
            systemMessage.setStatus(SystemMessageStatus.statusOfValue(cursor.getInt(cursor.getColumnIndex(SystemMsgTable.COLUMN_STATUS))));
            systemMessage.setTime(cursor.getLong(cursor.getColumnIndex(SystemMsgTable.COLUMN_TIME)));
            int unread = cursor.getInt(cursor.getColumnIndex(SystemMsgTable.COLUMN_UNREAD));
            systemMessage.setUnread(unread == MESSAGE_STATUS_UNREAD ? true : false);
            //
            String attach = cursor.getString(cursor.getColumnIndex(SystemMsgTable.COLUMN_ATTACH));
            systemMessage.setAttach(attach);
            //
            if(messageType == SystemMessageType.AddFriend){
                AddFriendNotify attachData = SystemMessageAttach.parseAddFriendNotify(targetid , attach);
                if(attachData != null){
                    systemMessage.setAttachObject(attachData);
                }
            } else {
                TeamEntity attachData = SystemMessageAttach.parseTeamEntity(attach);
                if(attachData != null){
                    systemMessage.setAttachObject(attachData);
                }
            }
            systemMessagesList.add(systemMessage);
        }
        cursor.close();
        mDBUtil.close();
        return systemMessagesList;
    }

    /**
     * 设置单条系统通知为已读
     * @param context
     * @param messageId
     */
    public static void setSystemMessageRead(Context context , String messageId) {
        String selection = SystemMsgTable.COLUMN_MESSAGEID + "= ? and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
        String[] selectionArgs = new String[]{String.valueOf(messageId), String.valueOf(MESSAGE_STATUS_UNREAD)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(SystemMsgTable.COLUMN_UNREAD, false);
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.updateData(contentValues, selection, selectionArgs);
        mDBUtil.close();
    }

    /**
     * 查询指定类型的系统通知未读数总和（同步版本）
     * @param context
     * @param systemMessageType
     */
    public static void resetSystemMessageUnreadCountByType(Context context , int systemMessageType) {
        String selection = null;
        String[] selectionArgs = null;
        if (systemMessageType == TYPE_MESSAGE_FRIEND) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.AddFriend.getValue()), String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_INVITE) {
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()),
                    String.valueOf(SystemMessageType.DeclineTeamInvite.getValue()), String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_APPLY) {
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()),
                    String.valueOf(SystemMessageType.RejectTeamApply.getValue()), String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_ALL) {
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE+"= ? or " + SystemMsgTable.COLUMN_TYPE+"= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()), String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())
                    ,String.valueOf(SystemMessageType.RejectTeamApply.getValue()), String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()), String.valueOf(MESSAGE_STATUS_UNREAD)};
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(SystemMsgTable.COLUMN_UNREAD, false);
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.updateData(contentValues, selection, selectionArgs);
        mDBUtil.close();
    }

    /**
     * 将所有系统通知设为已读，系统通知的未读消息总数将清零。
     * @param context
     */
    public static void resetSystemMessageUnreadCount(Context context) {
        String selection = SystemMsgTable.COLUMN_UNREAD + "= ?";
        String[] selectionArgs = new String[]{String.valueOf(MESSAGE_STATUS_UNREAD)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(SystemMsgTable.COLUMN_UNREAD, false);
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.updateData(contentValues, selection, selectionArgs);
        mDBUtil.close();
    }

    /**
     * 设置系统通知状态
     * @param context
     * @param messageId
     * @param status
     */
    public static void setSystemMessageStatus(Context context , long messageId, SystemMessageStatus status) {
        String selection = SystemMsgTable.COLUMN_MESSAGEID + "= ?";
        String[] selectionArgs = new String[]{String.valueOf(messageId)};
        ContentValues contentValues = new ContentValues();
        contentValues.put(SystemMsgTable.COLUMN_STATUS, status.getValue());
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.updateData(contentValues, selection, selectionArgs);
        mDBUtil.close();
    }

    /**
     * 根据消息类型查询消息未读数量
     * @param context
     * @param systemMessageType
     * @return
     */
    public static int querySystemMessageUnreadCountByType(Context context, int systemMessageType){
        String selection = null;
        String[] selectionArgs = null;
        if(systemMessageType == TYPE_MESSAGE_FRIEND) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.AddFriend.getValue()) , String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_INVITE){//邀请加入+用户拒绝邀请
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()) ,
                    String.valueOf(SystemMessageType.DeclineTeamInvite.getValue()) ,String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_APPLY){//用户申请加入+管理员拒绝申请
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()) ,
                    String.valueOf(SystemMessageType.RejectTeamApply.getValue()) ,String.valueOf(MESSAGE_STATUS_UNREAD)};
        } else if(systemMessageType == TYPE_MESSAGE_TEAM_MSG) {//管理员拒绝申请+邀请加入+拒绝邀请加入
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ? or "+SystemMsgTable.COLUMN_TYPE+"= ? ) and " + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()) ,
                    String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())
                    ,String.valueOf(SystemMessageType.RejectTeamApply.getValue())
                    ,String.valueOf(MESSAGE_STATUS_UNREAD)};

        } else if (systemMessageType == TYPE_MESSAGE_TEAM_ALL) {//所有四种俱乐部相关的消息
            selection = "(" + SystemMsgTable.COLUMN_TYPE + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE  + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE + "= ? or "
                    + SystemMsgTable.COLUMN_TYPE+"= ? ) and "
                    + SystemMsgTable.COLUMN_UNREAD + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()), String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())
                    ,String.valueOf(SystemMessageType.RejectTeamApply.getValue()), String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()), String.valueOf(MESSAGE_STATUS_UNREAD)};
        }

        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        int unreadCount = 0;
        if(cursor != null){
            unreadCount = cursor.getCount();
            cursor.close();
        }
        mDBUtil.close();
        return unreadCount;
    }

    /**
     * 获取所有未读消息数量
     * @param context
     * @return
     */
    public static int queryAllSystemMessageUnreadCount(Context context){
        String selection = SystemMsgTable.COLUMN_UNREAD + "= ?";
        String[] selectionArgs = new String[]{ String.valueOf(MESSAGE_STATUS_UNREAD) };
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        int unreadCount = 0;
        if(cursor != null){
            unreadCount = cursor.getCount();
            cursor.close();
        }
        mDBUtil.close();
        LogUtil.i(TAG, "消息总数 ： " + unreadCount);
        return unreadCount;
    }

    public static boolean isUnreadClubByTeamId(Context context ,String teamId){
        String selection = SystemMsgTable.COLUMN_UNREAD + "= ? and " + SystemMsgTable.COLUMN_TARGET_ID + "= ?";
        String[] selectionArgs = new String[]{ String.valueOf(MESSAGE_STATUS_UNREAD) , teamId};
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        if(cursor == null){
            return false;
        }
        int unreadCount = cursor.getCount();
        cursor.close();
        mDBUtil.close();
        return unreadCount > 0 ? true : false;
    }

    /**
     * 保存系统消息
     * @param context
     * @param systemMessage
     */
    public static void saveSystemtMessage(Context context, SystemMessage systemMessage) {
        LogUtil.i(TAG, "保存系统消息 ：" + systemMessage.getMessageId());
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        String selection = null;
        String[] selectionArgs = null;
        SystemMessageType systemMessageType = systemMessage.getType();
        if(systemMessageType == SystemMessageType.AddFriend) {
            //好友申请,只需要判断fromAccount
            selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_FROMID + " = ?";
            selectionArgs = new String[]{String.valueOf(systemMessageType.getValue()) , systemMessage.getFromAccount()};
        } else if(systemMessageType == SystemMessageType.TeamInvite || systemMessageType == SystemMessageType.DeclineTeamInvite){
            //俱乐部邀请，被俱乐部拒绝申请
            selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_TARGET_ID + "= ?";
            selectionArgs = new String[]{String.valueOf(systemMessageType.getValue()) , systemMessage.getTargetId()};
        } else if(systemMessageType == SystemMessageType.ApplyJoinTeam || systemMessageType == SystemMessageType.RejectTeamApply){
            //俱乐部申请，邀请被拒绝
            selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_TARGET_ID + "= ? and " + SystemMsgTable.COLUMN_FROMID +  "= ?";
            selectionArgs = new String[]{String.valueOf(systemMessageType.getValue()) ,  systemMessage.getTargetId() , systemMessage.getFromAccount()};
        }
        Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
        if(cursor != null){
            if(cursor.getCount() != 0){
                //删除原有的
                mDBUtil.deleteData(selection , selectionArgs);
            }
            cursor.close();
            //添加
            mDBUtil.insertData(getSystemtMessageContentValues(systemMessage));
        }
        mDBUtil.close();
    }

    /**
     * 删除系统消息
     * @param context
     * @param systemMessage
     */
    public static void deleteSystemMessage(Context context , SystemMessage systemMessage){
        String selection = SystemMsgTable.COLUMN_MESSAGEID + "= ?";
        String[] selectionArgs = new String[]{ String.valueOf(systemMessage.getMessageId())};
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.deleteData(selection , selectionArgs);
        mDBUtil.close();
    }

    /**
     * 根据系统消息类型批量删除
     * @param context
     * @param systemMessageType
     */
    public static void clearSystemMessagesByType(Context context , int systemMessageType) {
        String selection = null;
        String[] selectionArgs = null;
        if (systemMessageType == TYPE_MESSAGE_FRIEND) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.AddFriend.getValue())};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_INVITE) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.TeamInvite.getValue()),
                    String.valueOf(SystemMessageType.DeclineTeamInvite.getValue())};
        } else if (systemMessageType == TYPE_MESSAGE_TEAM_APPLY) {
            selection = SystemMsgTable.COLUMN_TYPE + "= ? or " + SystemMsgTable.COLUMN_TYPE + "= ?";
            selectionArgs = new String[]{String.valueOf(SystemMessageType.ApplyJoinTeam.getValue()),
                    String.valueOf(SystemMessageType.RejectTeamApply.getValue())};
        }
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.deleteData(selection, selectionArgs);
        mDBUtil.close();
    }

    //删掉原来申请失败的系统信息
    public static void deleteClubApplyFailedMsg(Context context,String teamId){
        String selection = SystemMsgTable.COLUMN_TYPE + "= ? and " + SystemMsgTable.COLUMN_TARGET_ID+" = ?" ;
        String[] selectionArgs = new String[]{String.valueOf(SystemMessageType.RejectTeamApply.getValue()),teamId};
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.deleteData(selection, selectionArgs);
        mDBUtil.close();
    }

    public static void clearSystemMessages(Context context) {
        DBUtil mDBUtil = DBUtil.getInstance(context, SystemMsgTable.TABLE_SYSTEM_MSG);
        mDBUtil.deleteData(null, null);
        mDBUtil.close();
    }

    public static ContentValues getSystemtMessageContentValues(SystemMessage systemMessage) {
        ContentValues values = new ContentValues();
        values.put(SystemMsgTable.COLUMN_MESSAGEID, systemMessage.getMessageId());
        values.put(SystemMsgTable.COLUMN_TARGET_ID, systemMessage.getTargetId());
        values.put(SystemMsgTable.COLUMN_FROMID, systemMessage.getFromAccount());
        values.put(SystemMsgTable.COLUMN_TYPE, systemMessage.getType().getValue());
        values.put(SystemMsgTable.COLUMN_TIME, systemMessage.getTime());
        values.put(SystemMsgTable.COLUMN_STATUS, systemMessage.getStatus().getValue());
        values.put(SystemMsgTable.COLUMN_CONTENT, systemMessage.getContent());
        values.put(SystemMsgTable.COLUMN_ATTACH, systemMessage.getAttach());
        values.put(SystemMsgTable.COLUMN_UNREAD, systemMessage.isUnread());
        return values;
    }

    public static SystemMessage getSystemMessage(com.netease.nimlib.sdk.msg.model.SystemMessage nimSystemMessage) {
        SystemMessage systemMessage = new SystemMessage();
        systemMessage.setMessageId(nimSystemMessage.getMessageId());
        systemMessage.setTargetId(nimSystemMessage.getTargetId());
        systemMessage.setFromAccount(nimSystemMessage.getFromAccount());
        systemMessage.setContent(nimSystemMessage.getContent());
        systemMessage.setType(nimSystemMessage.getType());
        systemMessage.setAttach(nimSystemMessage.getAttach());
        systemMessage.setAttachObject(nimSystemMessage.getAttachObject());
        systemMessage.setStatus(nimSystemMessage.getStatus());
        long time = (long) (nimSystemMessage.getTime() / 1000);//云信的消息时间戳都是毫秒，但是客户端的时间戳都是秒
        systemMessage.setTime(time);
        if (isTagMessageReaded(nimSystemMessage)){
            //标记为已读
            systemMessage.setUnread(false);
        } else{
            systemMessage.setUnread(nimSystemMessage.isUnread());
        }
        return systemMessage;
    }
}
