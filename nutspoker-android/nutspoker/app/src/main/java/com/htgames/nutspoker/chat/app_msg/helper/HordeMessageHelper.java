package com.htgames.nutspoker.chat.app_msg.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.msg.model.HordeMessageType;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.table.HordeTable;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper.MESSAGE_STATUS_UNREAD;

/**
 * Created by 周智慧 on 17/3/23.
 */

public class HordeMessageHelper extends BaseHelp {
    public static String TAG = HordeMessageHelper.class.getSimpleName();

    public static String getVerifyNotificationDealResult(SystemMessage message) {
        String handledNickname = "";
        if (message.horde_status == AppMessageStatus.passed.getValue()) {
            return handledNickname + DemoCache.getContext().getString(R.string.message_status_passed);
        } else if (message.horde_status == AppMessageStatus.declined.getValue()) {
            return handledNickname + DemoCache.getContext().getString(R.string.message_status_declined);
        } else if (message.horde_status == AppMessageStatus.ignored.getValue()) {
            return DemoCache.getContext().getString(R.string.message_status_ignored);
        } else if (message.horde_status == AppMessageStatus.expired.getValue()) {
            return DemoCache.getContext().getString(R.string.message_status_expired);
        } else {
            return DemoCache.getContext().getString(R.string.message_status_untreated);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////json解析相关操作///////////////////////////////////////////////////////////////////////////////////////////////
    public static HordeEntity getHordeEntity(JSONObject infoJson) {
        HordeEntity hordeEntity = new HordeEntity();
        JSONObject hordeJson = infoJson.optJSONObject("horde");
        if (hordeJson == null) {
            return hordeEntity;
        }
        hordeEntity.horde_id = hordeJson.optString("horde_id");
        hordeEntity.name = hordeJson.optString("name");
        return hordeEntity;
    }

    public static TeamEntity getTeamEntity(JSONObject infoJson) {
        TeamEntity teamEntity = new TeamEntity();
        JSONObject teamJsonobject = infoJson.optJSONObject("team");
        if (teamJsonobject == null) {
            return teamEntity;
        }
        teamEntity.name = teamJsonobject.optString("name");
        teamEntity.avatar = teamJsonobject.optString("avatar");
        teamEntity.id = teamJsonobject.optString("tid");
        return teamEntity;
    }

    public static SystemMessage parseSystemMessage(String data, int custom_type) {
        SystemMessage systemMessage = new SystemMessage();
        LogUtil.i(TAG, "data:" + data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String fromId = jsonObject.optString(AppMessageConstants.KEY_FROM_ID);
            String targetId = jsonObject.optString(AppMessageConstants.KEY_TARGET_ID);
            long time = jsonObject.optLong(AppMessageConstants.KEY_TIME);
            int type = jsonObject.optInt(AppMessageConstants.KEY_TYPE);
            int status = jsonObject.optInt(AppMessageConstants.KEY_STATUS);
            JSONObject attachJsonObject = jsonObject.optJSONObject(AppMessageConstants.KEY_INFO);//"info"
            HordeEntity hordeEntity = getHordeEntity(attachJsonObject);
            TeamEntity teamEntity = getTeamEntity(attachJsonObject);
            NimUserInfoCache.getInstance().getUserDisplayName(systemMessage.fromAccount);//请求一次云信刷新缓存
            systemMessage.fromAccount = (fromId);
            systemMessage.targetId = (targetId);
            systemMessage.time = (time);
            systemMessage.content = (jsonObject.optString(AppMessageConstants.KEY_CONTENT));
            systemMessage.horde_status = status;
            systemMessage.attach = attachJsonObject.toString();
            systemMessage.custom_outer_type = custom_type;
            systemMessage.custom_inner_type = type;
            systemMessage.tid = teamEntity == null ? "" : teamEntity.id;//俱乐部id
            systemMessage.tname = teamEntity == null ? "" : teamEntity.name;//俱乐部name
            systemMessage.tavatar = teamEntity == null ? "" : teamEntity.avatar;//俱乐部头像
            systemMessage.horde_id = hordeEntity == null ? "" : hordeEntity.horde_id;//部落id
            systemMessage.horde_name = hordeEntity == null ? "" : hordeEntity.name;//部落name
            systemMessage.key = "tid:" + systemMessage.tid + " horde_id:" + systemMessage.horde_id + " custom_inner_type:" + systemMessage.custom_inner_type;
            systemMessage.unread = (true);
            //如果状态为同意或者拒绝（房主），设置为已读
//            if (systemMessage.getStatus() == AppMessageStatus.passed || systemMessage.getStatus() == AppMessageStatus.declined) {
//                systemMessage.setUnread(false);
//            } else {
//                systemMessage.setUnread(true);
//            }//这个消息为啥要强制置为已读？   注释掉by  周智慧20161125
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return systemMessage;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////数据库读写相关操作///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 将部落相关message存入表中
     * @param context
     * @param systemMessage
     */
    public static void addHordeMsgToDB(Context context, SystemMessage systemMessage) {
        synchronized(BaseHelp.sHelp) {
            if (TextUtils.isEmpty(DemoCache.getAccount())) {
                //1.登录才入库 2.不是目前支持的格式(未知类型)
                return;
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, HordeTable.TABLE_HORDE_MSG);
            ContentValues values = new ContentValues();
            values.put(HordeTable.COLUMN_TARGET_ID, systemMessage.targetId);
            values.put(HordeTable.COLUMN_FROMID, systemMessage.fromAccount);
            values.put(HordeTable.COLUMN_OUTER_TYPE, systemMessage.custom_outer_type);
            values.put(HordeTable.COLUMN_INNER_TYPE, systemMessage.custom_inner_type);
            values.put(HordeTable.COLUMN_TIME, systemMessage.time);
            values.put(HordeTable.COLUMN_STATUS, systemMessage.horde_status);
            values.put(HordeTable.COLUMN_CONTENT, systemMessage.content);
            values.put(HordeTable.COLUMN_ATTACH, systemMessage.attach);
            values.put(HordeTable.COLUMN_UNREAD, systemMessage.unread);
            values.put(HordeTable.COLUMN_KEY, systemMessage.key);
            values.put(HordeTable.COLUMN_TID, systemMessage.tid);
            values.put(HordeTable.COLUMN_TNAME, systemMessage.tname);
            values.put(HordeTable.COLUMN_TAVATAR, systemMessage.tavatar);
            values.put(HordeTable.COLUMN_HORDE_ID, systemMessage.horde_id);
            values.put(HordeTable.COLUMN_HORDE_NAME, systemMessage.horde_name);
            String selection = HordeTable.COLUMN_KEY + "= ?";
            String[] selectionArgs = new String[]{systemMessage.key};
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
            if (cursor == null || cursor.getCount() == 0) {
                mDBUtil.insertData(values);
            } else {
                mDBUtil.updateData(values, selection, selectionArgs);
            }
            if (cursor != null) {
                cursor.close();
            }
            mDBUtil.close();
        }
    }

    public final static int SEARCH_TYPE_ALL = 0;//所有部落消息
    public final static int SEARCH_TYPE_HORDE_APPLY = 1;//申请加入部落消息
    public final static int SEARCH_TYPE_UNREAD = 2;//未读
    public final static int SEARCH_TYPE_INIT = 3;//未处理
    //按照类型查询部落信息
    public static ArrayList<SystemMessage> queryHordeMessageByType(Context context , int systemMessageType) {
        synchronized(BaseHelp.sHelp) {
            String orderBy =  HordeTable.COLUMN_TIME + " DESC";//"_id DESC";
            String selection = null;
            String[] selectionArgs = null;
            if(systemMessageType == SEARCH_TYPE_ALL) {
                //do nothing
            } else if (systemMessageType == SEARCH_TYPE_HORDE_APPLY) {
                selection = HordeTable.COLUMN_INNER_TYPE + "= ?";
                selectionArgs = new String[]{HordeMessageType.HORDE_MESSAGE_TYPE_APPLY + ""};
            } else if (systemMessageType == SEARCH_TYPE_UNREAD) {
                selection = HordeTable.COLUMN_UNREAD + "= ?";
                selectionArgs = new String[]{"1"};
            } else if (systemMessageType == SEARCH_TYPE_INIT) {
                selection = HordeTable.COLUMN_STATUS + "= ?";
                selectionArgs = new String[]{"0"};
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, HordeTable.TABLE_HORDE_MSG);
            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            if(cursor == null){
                return null;
            }
            ArrayList<SystemMessage> systemMessagesList = new ArrayList<SystemMessage>();
            while(cursor.moveToNext()) {
                SystemMessage systemMessage = new SystemMessage();
                systemMessage.fromAccount = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_FROMID));
                systemMessage.targetId = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_TARGET_ID));
                systemMessage.time = cursor.getLong(cursor.getColumnIndex(HordeTable.COLUMN_TIME));
                systemMessage.content = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_CONTENT));
                systemMessage.horde_status = cursor.getInt(cursor.getColumnIndex(HordeTable.COLUMN_STATUS));
                systemMessage.attach = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_ATTACH));
                systemMessage.custom_outer_type = cursor.getInt(cursor.getColumnIndex(HordeTable.COLUMN_OUTER_TYPE));
                systemMessage.custom_inner_type = cursor.getInt(cursor.getColumnIndex(HordeTable.COLUMN_INNER_TYPE));
                systemMessage.tid = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_TID));//俱乐部id
                systemMessage.tname = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_TNAME));//俱乐部name
                systemMessage.tavatar = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_TAVATAR));//俱乐部头像
                systemMessage.horde_id = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_HORDE_ID));//部落id
                systemMessage.horde_name = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_HORDE_NAME));//部落name
                systemMessage.key = cursor.getString(cursor.getColumnIndex(HordeTable.COLUMN_KEY));
                systemMessage.unread = cursor.getInt(cursor.getColumnIndex(HordeTable.COLUMN_UNREAD)) == MESSAGE_STATUS_UNREAD;
                systemMessagesList.add(systemMessage);
            }
            cursor.close();
            mDBUtil.close();
            return systemMessagesList;
        }
    }

    /**
     * 消息全部置为已读
     * @param context
     * @param systemMessageType
     */
    public static void resetHordeMessageUnreadCountByType(Context context , int systemMessageType) {
        synchronized(BaseHelp.sHelp) {
            String selection = null;
            String[] selectionArgs = null;
            if(systemMessageType == SEARCH_TYPE_ALL) {
                //do nothing
            } else if(systemMessageType == SEARCH_TYPE_HORDE_APPLY) {
                selection = HordeTable.COLUMN_INNER_TYPE + "= ?";
                selectionArgs = new String[]{HordeMessageType.HORDE_MESSAGE_TYPE_APPLY + ""};
            }
            DBUtil mDBUtil = DBUtil.getInstance(context, HordeTable.TABLE_HORDE_MSG);
            ContentValues contentValues = new ContentValues();
            contentValues.put(HordeTable.COLUMN_UNREAD, false);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }

    /**
     * 删除某条部落消息
     * @param context
     * @param systemMessage
     */
    public static void deleteSystemMessage(Context context , SystemMessage systemMessage) {
        synchronized(BaseHelp.sHelp) {
            String selection = HordeTable.COLUMN_KEY + "= ?";
            String[] selectionArgs = new String[]{ systemMessage.key };
            DBUtil mDBUtil = DBUtil.getInstance(context, HordeTable.TABLE_HORDE_MSG);
            mDBUtil.deleteData(selection , selectionArgs);
            mDBUtil.close();
        }
    }

    public static void setSystemMessageStatus(Context context, String key, int status) {
        synchronized(BaseHelp.sHelp) {
            String selection = HordeTable.COLUMN_KEY + "= ?";
            String[] selectionArgs = new String[]{ key };
            ContentValues contentValues = new ContentValues();
            contentValues.put(HordeTable.COLUMN_STATUS, status);
            DBUtil mDBUtil = DBUtil.getInstance(context, HordeTable.TABLE_HORDE_MSG);
            mDBUtil.updateData(contentValues, selection, selectionArgs);
            mDBUtil.close();
        }
    }
}
