package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.bean.CardTypeEy;
import com.netease.nim.uikit.bean.NetCardCollectBaseEy;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.HandsCollectTable;
import com.netease.nim.uikit.db.table.NetCardCollectTable;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.ui.activity.Hands.tool.HandJsonTools;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手牌收藏
 */
public class HandsCollectDBHelper extends BaseHelp {

    //收藏牌谱
    public static void addCollectHands(Context context, PaipuEntity paipuEntity) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            Cursor cursor = null;
            try {
                String selection = HandsCollectTable.COLUMN_GAME_GID + "= ? and " + HandsCollectTable.COLUMN_HANDS_CNT + "= ? ";
                String[] selectionArgs = new String[]{paipuEntity.gameEntity.gid, String.valueOf(paipuEntity.handsCnt)};
                cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
                ContentValues values = new ContentValues();
                values.put(HandsCollectTable.COLUMN_GAME_GID, paipuEntity.gameEntity.gid);
                values.put(HandsCollectTable.COLUMN_GAME_CREATE_TIME, paipuEntity.gameEntity.createTime);
                values.put(HandsCollectTable.COLUMN_FILE_NAME, paipuEntity.fileName);
                values.put(HandsCollectTable.COLUMN_FILE_NET_PATH, paipuEntity.fileNetPath);
                values.put(HandsCollectTable.COLUMN_HANDS_CNT, paipuEntity.handsCnt);
                values.put(HandsCollectTable.COLUMN_HANDS_ID, paipuEntity.handsId);
                values.put(HandsCollectTable.COLUMN_DATA, paipuEntity.jsonDataStr);
                values.put(HandsCollectTable.COLUMN_HAND_COLLECT_TIME, paipuEntity.collectTime);
                if (cursor != null) {
                    if (cursor.getCount() == 0) {
                        mDBUtil.insertData(values);
                    } else {
                        mDBUtil.updateData(values, selection, selectionArgs);
                    }
                }
            } catch (Exception ex) {

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            mDBUtil.close();
        }

    }

    //收藏牌谱
    public static void addCollectHands(Context context, ArrayList<PaipuEntity> paipuList, boolean isDelete) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            if (isDelete) {
                mDBUtil.deleteData(null, null);
            }
            Cursor cursor = null;
            try {
                for (PaipuEntity paipuEntity : paipuList) {
//                String selection = HandsCollectTable.COLUMN_GAME_GID + "= ? and " + HandsCollectTable.COLUMN_HANDS_CNT + "= ? ";
//                String[] selectionArgs = new String[]{paipuEntity.gameEntity.getGid(), String.valueOf(paipuEntity.getHandsCnt())};
                    String selection = HandsCollectTable.COLUMN_HANDS_ID + "= ?";
                    String[] selectionArgs = new String[]{paipuEntity.handsId};
                    cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, null);
                    ContentValues values = new ContentValues();
                    values.put(HandsCollectTable.COLUMN_GAME_GID, paipuEntity.gameEntity.gid);
                    values.put(HandsCollectTable.COLUMN_GAME_CREATE_TIME, paipuEntity.gameEntity.createTime);
                    values.put(HandsCollectTable.COLUMN_HAND_COLLECT_TIME, paipuEntity.collectTime);
                    values.put(HandsCollectTable.COLUMN_FILE_NAME, paipuEntity.fileName);
                    values.put(HandsCollectTable.COLUMN_FILE_NET_PATH, paipuEntity.fileNetPath);
                    values.put(HandsCollectTable.COLUMN_HANDS_CNT, paipuEntity.handsCnt);
                    values.put(HandsCollectTable.COLUMN_HANDS_ID, paipuEntity.handsId);
                    values.put(HandsCollectTable.COLUMN_DATA, paipuEntity.jsonDataStr);
//                Log.d("HandsCollect" ,  paipuEntity.getHandsId());
                    if (cursor != null) {
                        if (cursor.getCount() == 0) {
                            mDBUtil.insertData(values);
                        } else {
                            mDBUtil.updateData(values, selection, selectionArgs);
                        }
                    }
                }
            } catch (Exception ex) {

            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            mDBUtil.close();
        }

    }

    /**
     * 取消收藏
     *
     * @param context
     * @param hid
     */
    public static void cancelCollectHands(Context context, String hid) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            String selection = HandsCollectTable.COLUMN_HANDS_ID + "= ?";
            String[] selectionArgs = new String[]{hid};
            mDBUtil.deleteData(selection, selectionArgs);
        }

    }

    /**
     * 删除所有记录
     *
     * @param context
     */
    public static void clearCollectHands(Context context) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            mDBUtil.deleteData(null, null);
        }

    }

    /**
     * 获取收藏的HID对应fileName
     *
     * @param context
     * @return
     */
    public static Map<String, String> getCollectHidMap(Context context) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            String[] columns = new String[]{HandsCollectTable.COLUMN_HANDS_ID, HandsCollectTable.COLUMN_FILE_NAME};
            Cursor cursor = mDBUtil.selectData(columns, null, null, null, null, null);
            Map<String, String> collectMap = new HashMap<>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String handsId = cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_HANDS_ID));
                    String fileName = cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_FILE_NAME));
                    collectMap.put(fileName, handsId);
                }
                cursor.close();
            }
            mDBUtil.close();
            return collectMap;
        }

    }

    /**
     * 获取收藏的手牌列表
     *
     * @param context
     * @return
     */
    @Deprecated
    public static ArrayList<PaipuEntity> getHandsCollectList(Context context) {
        synchronized (BaseHelp.sHelp) {
            DBUtil mDBUtil = DBUtil.getInstance(context, HandsCollectTable.TABLE_HANDS_COLLECT);
            String sortOrder = HandsCollectTable.COLUMN_HAND_COLLECT_TIME + " DESC";
            Cursor cursor = mDBUtil.selectData(null, null, null, null, null, sortOrder);
            ArrayList<PaipuEntity> paipuList = new ArrayList<PaipuEntity>();
            if (cursor != null) {
                PaipuEntity paipuEntity = null;
                while (cursor.moveToNext()) {
                    paipuEntity = getHandsItem(cursor);
                    paipuEntity.isCollect = (true);
                    paipuList.add(paipuEntity);
                }
                cursor.close();
            }
            mDBUtil.close();
            return paipuList;
        }

    }

    /**
     * 获取数据库中单条GameRecord
     *
     * @param cursor
     * @return
     */
    private static PaipuEntity getHandsItem(Cursor cursor) {
        synchronized (BaseHelp.sHelp) {
            String content = cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_DATA));
            PaipuEntity paipuEntity = null;
            try {
                paipuEntity = HandJsonTools.getPaipuEntity(content);
                if (paipuEntity != null) {
                    paipuEntity.fileName = (cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_FILE_NAME)));
                    paipuEntity.fileNetPath = (cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_FILE_NET_PATH)));
                    paipuEntity.collectTime = (cursor.getLong(cursor.getColumnIndex(HandsCollectTable.COLUMN_HAND_COLLECT_TIME)));
                    paipuEntity.handsId = (cursor.getString(cursor.getColumnIndex(HandsCollectTable.COLUMN_HANDS_ID)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return paipuEntity;
        }
    }


    //删掉指定的牌谱
    public static void DeleteHandsCollectList(Context context,String hid){
        if(TextUtils.isEmpty(hid))
            return;

        synchronized (sHelp){
            DBUtil dbUtil = DBUtil.getInstance(context,NetCardCollectTable.TABLE_NAME);

            String whereClause = NetCardCollectTable.hid + " = ?";
            String[] whereArgs = {hid};
            dbUtil.deleteData(whereClause,whereArgs);
        }
    }

    public static void SetHandsCollectListEx(Context context, List<NetCardCollectBaseEy> list) {
        if (list == null || list.isEmpty())
            return;

        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardCollectTable.TABLE_NAME);
            for (NetCardCollectBaseEy data : list) {
                String selection = NetCardCollectTable.id + " =? ";
                String[] selectionArgs = {data.id};
                Cursor cursor = dbUtil.selectData(null, selection, selectionArgs, null, null, null);

                //如果没有，则增加一条数据
                if (!cursor.moveToFirst()) {
                    ContentValues values = new ContentValues();
                    values.put(NetCardCollectTable.id, data.id);
                    values.put(NetCardCollectTable.hid, data.hid);
                    values.put(NetCardCollectTable.uid, data.uid);
                    values.put(NetCardCollectTable.collect_time, data.collect_time);
                    values.put(NetCardCollectTable.file_path,data.file_path);
                    values.put(NetCardCollectTable.file_name,data.file_name);

                    values.put(NetCardCollectTable.code, data.code);
                    values.put(NetCardCollectTable.win_chips, data.win_chips);
                    values.put(NetCardCollectTable.card_type, data.card_type);
                    values.put(NetCardCollectTable.time, data.time);
                    values.put(NetCardCollectTable.gid, data.gid);


//                    if(DataManager.IsOldPaipu(data.hid)){
//                        NetCardCollectOldEy oldData = (NetCardCollectOldEy)data;
//                        //to Json
//                        values.put(NetCardCollectTable.pool_cards, GsonUtils.getGson().toJson(oldData.pool_cards));
//                        //to Json
//                        values.put(NetCardCollectTable.cardtype_cards, GsonUtils.getGson().toJson(oldData.cardtype_cards));
//                    } else {
                    NetCardCollectBaseEy newData = (NetCardCollectBaseEy)data;
                        //to Json
                        values.put(NetCardCollectTable.pool_cards, GsonUtils.getGson().toJson(newData.pool_cards));
                        //to Json
                        values.put(NetCardCollectTable.cardtype_cards, GsonUtils.getGson().toJson(newData.cardtype_cards));
//                    }

                    //to Json
                    values.put(NetCardCollectTable.hand_cards, GsonUtils.getGson().toJson(data.hand_cards));

                    values.put(NetCardCollectTable.hands_cnt, data.hands_cnt);
                    values.put(NetCardCollectTable.count, data.count);
                    values.put(NetCardCollectTable.tid, data.tid);
                    values.put(NetCardCollectTable.owner, data.owner);
                    values.put(NetCardCollectTable.name, data.name);
                    values.put(NetCardCollectTable.blinds, data.blinds);
                    values.put(NetCardCollectTable.sblinds, data.sblinds);
                    values.put(NetCardCollectTable.duration, data.duration);
                    values.put(NetCardCollectTable.durations, data.durations);
                    values.put(NetCardCollectTable.tilt_mode, data.tilt_mode);
                    values.put(NetCardCollectTable.type, data.type);
                    values.put(NetCardCollectTable.status, data.status);
                    values.put(NetCardCollectTable.public_mode, data.public_mode);
                    values.put(NetCardCollectTable.ante_mode, data.ante_mode);
                    values.put(NetCardCollectTable.ante, data.ante);
                    values.put(NetCardCollectTable.create_time, data.create_time);
                    values.put(NetCardCollectTable.bouts, data.bouts);
                    values.put(NetCardCollectTable.game_mode, data.game_mode);
                    values.put(NetCardCollectTable.match_chips, data.match_chips);
                    values.put(NetCardCollectTable.match_player, data.match_player);
                    values.put(NetCardCollectTable.match_duration, data.match_duration);
                    values.put(NetCardCollectTable.match_checkin_fee, data.match_checkin_fee);

                    dbUtil.insertData(values);
                }

            }

        }
    }

    /**
     * 获取第一页最多20条数据
     * @param context
     * @return
     */
    public static List<NetCardCollectBaseEy> GetHandsCollectListEx(Context context) {
        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardCollectTable.TABLE_NAME);

            //根据VIP等级来获取

            String orderBy = NetCardCollectTable.collect_time + " DESC ";
            Cursor cursor = dbUtil.selectData(null, null, null, null, null, orderBy);

            List<NetCardCollectBaseEy> list = new ArrayList<>();
            int i = 0;
            for (cursor.moveToFirst(); i < PAGE_COOUNT && !cursor.isAfterLast(); cursor.moveToNext(),i++) {
                NetCardCollectBaseEy data = ConvertNetCardCollectEy(cursor);
                list.add(data);
            }
            return list;
        }
    }

    /**
     * 根据指定的收藏时间，获取后面最多20条数据
     * @param context
     * @param collectTime
     * @return
     */
    public static List<NetCardCollectBaseEy> GetHandsCollectListEx(Context context, long collectTime) {
        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, NetCardCollectTable.TABLE_NAME);

            String section = NetCardCollectTable.collect_time + " < ? ";
            String[] sectionArg = {String.valueOf(collectTime)};
            String orderBy = NetCardCollectTable.collect_time + " DESC ";
            Cursor cursor = dbUtil.selectData(null, section, sectionArg, null, null, orderBy);

            List<NetCardCollectBaseEy> list = new ArrayList<>();
            int i = 0;
            for (cursor.moveToFirst(); i < PAGE_COOUNT && !cursor.isAfterLast(); cursor.moveToNext(),i++) {
                NetCardCollectBaseEy data = ConvertNetCardCollectEy(cursor);
                list.add(data);
            }
            return list;
        }
    }

    static NetCardCollectBaseEy ConvertNetCardCollectEy(Cursor cursor){

        NetCardCollectBaseEy data;

        String id = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.id));
        String hid = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.hid));


        if(/*DataManager.IsOldPaipu(hid)*/false) {
//            data = new NetCardCollectOldEy();
//
//            //Json转换
//            Type type = new TypeToken<List<Integer>>(){}.getType();
//            ((NetCardCollectOldEy) data).pool_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardCollectTable.pool_cards)), type);
//
//            //Json转换
//            type = new TypeToken<List<Integer>>(){}.getType();
//            ((NetCardCollectOldEy) data).cardtype_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardCollectTable.cardtype_cards)), type);
        } else{
            data = new NetCardCollectBaseEy();

            //Json转换
            Type type = new TypeToken<List<List<Integer>>>(){}.getType();
            ((NetCardCollectBaseEy) data).pool_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardCollectTable.pool_cards)), type);

            //Json转换
            type = new TypeToken<HashMap<String, CardTypeEy>>(){}.getType();
            ((NetCardCollectBaseEy) data).cardtype_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardCollectTable.cardtype_cards)), type);
        }

        data.id = id;
        data.hid = hid;

        data.uid = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.uid));
        data.collect_time = cursor.getLong(cursor.getColumnIndex(NetCardCollectTable.collect_time));

        data.file_name = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.file_name));
        data.file_path = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.file_path));

        data.win_chips = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.win_chips));
        data.card_type = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.card_type));
        data.time = cursor.getLong(cursor.getColumnIndex(NetCardCollectTable.time));
        data.gid = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.gid));

        //Json转换
        Type type = new TypeToken<List<Integer>>(){}.getType();
        data.hand_cards = GsonUtils.getGson().fromJson(cursor.getString(cursor.getColumnIndex(NetCardCollectTable.hand_cards)),type);

        data.hands_cnt = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.hands_cnt));
        data.count = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.count));
        data.owner = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.owner));
        data.name = cursor.getString(cursor.getColumnIndex(NetCardCollectTable.name));
        data.blinds = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.blinds));
        data.sblinds = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.sblinds));
        data.duration = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.duration));
        data.durations = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.durations));
        data.tilt_mode = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.tilt_mode));
        data.type = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.type));
        data.status = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.status));
        data.public_mode = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.public_mode));
        data.ante_mode = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.ante_mode));
        data.ante = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.ante));
        data.create_time = cursor.getLong(cursor.getColumnIndex(NetCardCollectTable.create_time));
        data.bouts = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.bouts));
        data.game_mode = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.game_mode));
        data.match_chips = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.match_chips));
        data.match_player = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.match_player));
        data.match_duration = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.match_duration));
        data.match_checkin_fee = cursor.getInt(cursor.getColumnIndex(NetCardCollectTable.match_checkin_fee));

        return data;
    }
}
