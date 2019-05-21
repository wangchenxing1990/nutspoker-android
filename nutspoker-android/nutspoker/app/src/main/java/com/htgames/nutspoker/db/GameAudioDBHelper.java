package com.htgames.nutspoker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.netease.nim.uikit.db.BaseHelp;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.db.table.GameAudioTable;

import java.util.ArrayList;

/**
 * 游戏语音数据库操作
 */
public class GameAudioDBHelper extends BaseHelp {
    private final static String TAG = "GameAudioDBHelper";

    /**
     * 保存游戏语音
     * @param context
     * @param audioPath
     * @param audioTime
     */
    public static void saveGameAudio(Context context , String audioPath , long audioTime) {
        synchronized (BaseHelp.sHelp) {
            try {
                DBUtil mDBUtil = DBUtil.getInstance(context, GameAudioTable.TABLE_GAME_AUDIO);
                ContentValues values = new ContentValues();
                values.put(GameAudioTable.COLUMN_AUDIO_PATH, audioPath);
                values.put(GameAudioTable.COLUMN_AUDIO_TIME, audioTime);
                mDBUtil.insertData(values);
                mDBUtil.close();
            } catch (Exception ex) {
            } finally {
            }
        }
    }

    /**
     * 获取失效的游戏语音列表
     * @param context
     * @return
     */
    public static ArrayList<String> getOverdueAudioList(Context context) {
        synchronized (BaseHelp.sHelp) {
            ArrayList<String> audioList = new ArrayList<>();
//            String selection = GameAudioTable.COLUMN_AUDIO_TIME + "< ?";
//            String[] selectionArgs = new String[]{String.valueOf(endTime)};
            String orderBy = GameAudioTable.COLUMN_AUDIO_TIME + " DESC";
            DBUtil mDBUtil = DBUtil.getInstance(context, GameAudioTable.TABLE_GAME_AUDIO);
//            Cursor cursor = mDBUtil.selectData(null, selection, selectionArgs, null, null, orderBy);
            Cursor cursor = mDBUtil.selectData(null, null, null, null, null, orderBy);
            if (cursor != null && !cursor.isClosed()) {
                while (cursor.moveToNext()) {
                    String audioPath = cursor.getString(cursor.getColumnIndex(GameAudioTable.COLUMN_AUDIO_PATH));
                    audioList.add(audioPath);
                }
                cursor.close();
            }
            mDBUtil.close();
            return audioList;
        }
    }

    /**
     * 删除数据库中的录音文件记录
     * @param context
     * @param path
     */
    public static void deleteGameAudio(Context context,String path) {
        if (TextUtils.isEmpty(path))
            return;

        synchronized (sHelp) {
            DBUtil dbUtil = DBUtil.getInstance(context, GameAudioTable.TABLE_GAME_AUDIO);
            String whereClause = GameAudioTable.COLUMN_AUDIO_PATH + " = ? ";
            String[] whereArgs = {path};
            dbUtil.deleteData(whereClause, whereArgs);
            dbUtil.close();
        }
    }
}
