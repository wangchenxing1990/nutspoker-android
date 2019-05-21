package com.netease.nim.uikit.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.db.table.GameRecordTable;

/**
 * 数据库工具类SQLiteOpenHelper
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TAG = "DBHelper";
    public static String DB_NAME = "database.db";
    public static int DB_VERSION = 10;//数据库第一个版本是从5开始的
    private Context context;
    public static DBHelper mInstance;

    private DBHelper(Context context) {
        //1debug-测试服"debug-test"  2debug-正式服"debug-normal"   3release版本""
        super(context, UserPreferences.getInstance(context).getUserId() + (CacheConstant.debugBuildType ? (ApiConfig.isTestVersion ? "debug-test" : "debug-normal") : "") + ".db", null, DB_VERSION);
        this.context = context;
        //Log.d(TAG , "数据库：" + UserPreferences.getInstance(context).getUserId() + ".db");
    }

    /**
     * 单例模式，防止数据库死锁
     * @param context
     * @return
     */
    public synchronized static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            LogUtil.i(TAG, "mInstance == null");
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    /**
     * 关闭DBHelper
     */
    public void closeDBHelper() {
        if (mInstance != null) {
            mInstance.close();
            mInstance = null;
            context = null;
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL.CREATE_TABLE_CONTACT);
        db.execSQL(SQL.CREATE_TABLE_DATA_ANALYSIS);
        db.execSQL(SQL.CREATE_TABLE_DATA_STATISTICS);
        db.execSQL(SQL.CREATE_TABLE_SYSTEM_MESSAGE);
        db.execSQL(SQL.CREATE_TABLE_APP_MESSAGE);
        db.execSQL(SQL.CREATE_TABLE_GAME_RECORD);
        db.execSQL(SQL.CREATE_TABLE_HANDS_COLLECT);
        //新版战绩首页
        db.execSQL(SQL.CREATE_TABLE_RECORD_INDEX);
        //新版牌谱记录
        db.execSQL(SQL.CREATE_TABLE_NETCARDRECORD);
        //新版牌谱的牌局信息
        db.execSQL(SQL.CREATE_TABLE_CARDGAMEINFO);
        //新版牌谱收藏
        db.execSQL(SQL.CREATE_TABLE_CARDCOLLECT);
        //创建游戏语音
        db.execSQL(SQL.CREATE_TABLE_GAMEAUDIO);
        //创建部落表
        db.execSQL(SQL.CREATE_TABLE_HORDE);
    }

    //第一次创建的时候不会执行该方法，只有当有数据库存在并且升级的时候才会执行
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtil.i("onUpgrade", "oldVersion = " + oldVersion + ", newVersion = " + newVersion);
        onCreate(db);//不存在的表会去创建
        if (oldVersion == 1) {
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MODE);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MYUID);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_PLAYER);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_DURATION);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_CHIPS);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_ALLREWARD);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_TOTALTIME);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_END_TIME);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_MATCH_CHECKIN_FEE);
            db.execSQL(SQLV2.ALTER_TABLE_GAME_RECORD_GAME_ANTE);
        }
        if (oldVersion < 3) {
            db.execSQL(SQLV3.ALTER_TABLE_APP_MEG_SORT_KEY);
            db.execSQL(SQLV3.ALTER_TABLE_GAME_RECORD_TOTAL_PLAYER);
            db.execSQL(SQLV3.ALTER_TABLE_GAME_RECORD_SBLINDS_INDEX);
            db.delete(GameRecordTable.TABLE_GAME_RECORD, null, null);//清空牌局信息列表
        }
        if(oldVersion < 4){
            //增加数据统计数据的类型
            db.execSQL(SQLV4.ALTER_TABLE_DATASTATISTICS_TYPE);
        }
        if(oldVersion < 5){
            db.execSQL(SQLV5.ALTER_TABLE_CARDCOLLECT_FILEPATH);
            db.execSQL(SQLV5.ALTER_TABLE_CARDCOLLECT_FILENAME);
        }
        if(oldVersion < 6) {
            db.execSQL(SQLV6.ALTER_TABLE_APPMSGTABLE_TYPE);
        }
        if(oldVersion < 7) {
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_KO_MODE);
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_KO_REWARD_RATE);
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_KO_HEAD_RATE);
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_GAME_CONFIG);
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_EXTEND_ONE);
            db.execSQL(SQLV7.ALTER_TABLE_GAME_RECORD_GAME_EXTEND_TWO);
        }
        if(oldVersion < 8) {
            db.execSQL(SQLV8.CREATE_TABLE_HORDE);
        }
        if (oldVersion < 9) {
            db.execSQL(SQLV9.ALTER_TABLE_GAME_RECORD_GAME_PLAY_MODE);
        }
        if (oldVersion < 10) {
            db.execSQL(SQLV10.INSTANCE.getALTER_TABLE_GAME_RECORD_MATCH_TYPE());
        }
    }
}
