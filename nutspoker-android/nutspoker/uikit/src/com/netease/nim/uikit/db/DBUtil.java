package com.netease.nim.uikit.db;

/*
* https://notes.devlabs.bg/realm-objectbox-or-room-which-one-is-for-you-3a552234fd6e 介绍了checkbox、realm和room三者的相关指标的比较
* https://www.kotlindevelopment.com/anko-sqlite-database/也对一些数据库进行了对比
* */
import android.database.Cursor;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
    private static DBUtil mInstance;
    private static DBHelper mDBHelp;
    private SQLiteDatabase mSQLiteDatabase;
    private static String table;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private DBUtil(Context context) {
//		mDBHelp = new DBHelper(context);
        mDBHelp = DBHelper.getInstance(context);
        mSQLiteDatabase = mDBHelp.getWritableDatabase();
//		table = tb;
    }

    /**
     * 初始化数据库操作DBUtil类
     */
    public synchronized static DBUtil getInstance(Context context, String tb) {
        if (mInstance == null) {
            mInstance = new DBUtil(context);
        }
        table = tb;
        return mInstance;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        openWritableDatabase();
        return mSQLiteDatabase;
    }

    /**
     * 打开写入数据库
     */
    public void openWritableDatabase() {
        if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase = mDBHelp.getWritableDatabase();
        }
    }

    /**
     * 打开读取数据库
     */
    public void openReadableDatabase() {
        if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase = mDBHelp.getReadableDatabase();
        }
    }

    /**
     * 关闭数据库
     */
    public void close() {
//		if(mDBHelp != null){
//			mDBHelp.close();
//			mDBHelp = null;
//		}
//		if(mSQLiteDatabase != null){
//			mSQLiteDatabase.close();
//			mSQLiteDatabase = null;
//		}
//		mInstance = null;
    }

    /**
     * 关闭数据库所有相关的，清空方内存回收
     */
    public static void closeDBUtil() {
        if (mInstance != null) {
            if (mDBHelp != null) {
                mDBHelp.closeDBHelper();
                mDBHelp = null;
            }
            mInstance = null;
        }
    }

    /**
     * 添加数据
     */
    public void insertData(ContentValues values) {
        openWritableDatabase();
        mSQLiteDatabase.insert(table, null, values);
    }

    /**
     * 更新数据
     *
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public int updateData(ContentValues values, String whereClause, String[] whereArgs) {
        openWritableDatabase();
        return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
    }

    /**
     * 删除数据
     *
     * @param whereClause
     * @param whereArgs
     */
    public synchronized void deleteData(String whereClause, String[] whereArgs) {
        openWritableDatabase();
        mSQLiteDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * 查询数据
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public synchronized Cursor selectData(String[] columns, String selection,
                                          String[] selectionArgs, String groupBy, String having,
                                          String orderBy) {
        openReadableDatabase();
        Cursor cursor = mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        return cursor;
    }
}