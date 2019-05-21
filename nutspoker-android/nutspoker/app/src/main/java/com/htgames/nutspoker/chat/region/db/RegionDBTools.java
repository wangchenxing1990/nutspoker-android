package com.htgames.nutspoker.chat.region.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.htgames.nutspoker.chat.region.RegionConstants;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.tool.AssetsDatabaseManager;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.ArrayList;

/**
 * 区域数据库工具类
 */
public class RegionDBTools {
    private final static String TAG = "RegionDBTools";

    public static ArrayList<RegionEntity> getRegionList(String selection , String[] selectionArgs) {
        ArrayList<RegionEntity> regionList = new ArrayList<RegionEntity>();
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase mSQLiteDatabase = mg.getDatabase(RegionConstants.DB_REGION);
        // 对数据库进行操作
        Cursor cursor = mSQLiteDatabase.query(RegionConstants.TABLE_REGION, null, selection, selectionArgs, null, null, null);
        try {
            while (cursor.moveToNext()) {
                RegionEntity regionEntity = new RegionEntity();
                int type = cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_TYPE));
                regionEntity.id = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_ID)));
                regionEntity.name = (cursor.getString(cursor.getColumnIndex(RegionConstants.COLUMN_NAME)));
                regionEntity.pid = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_PID)));
                regionEntity.type = (type);
                regionList.add(regionEntity);
            }
        }catch (Exception ex){
        }finally {
            if(cursor != null){
                cursor.close();
            }
            //        mSQLiteDatabase.close();
        }
        return regionList;
    }

    /**
     * 获取中国的所有省份列表
     * @return
     */
    public static ArrayList<RegionEntity> getChinaProvinceList() {
        ArrayList<RegionEntity> regionList = new ArrayList<RegionEntity>();
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase mSQLiteDatabase = mg.getDatabase(RegionConstants.DB_REGION);
        String selection = RegionConstants.COLUMN_TYPE + "= ? and " + RegionConstants.COLUMN_PID + "= ?";
        String[] selectionArgs = new String[]{String.valueOf(RegionConstants.TYPE_PROVINCE) , RegionConstants.COUNTY_CHINA_ID};
        // 对数据库进行操作
        Cursor cursor = mSQLiteDatabase.query(RegionConstants.TABLE_REGION, null, selection, selectionArgs, null, null, null);
        try {
            while (cursor.moveToNext()) {
                RegionEntity regionEntity = new RegionEntity();
                int type = cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_TYPE));
                regionEntity.id = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_ID)));
                regionEntity.name = (cursor.getString(cursor.getColumnIndex(RegionConstants.COLUMN_NAME)));
                regionEntity.pid = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_PID)));
                regionEntity.type = (type);
                regionList.add(regionEntity);
            }
        }catch (Exception ex){
        }finally {
            if(cursor != null){
                cursor.close();
            }
            //        mSQLiteDatabase.close();
        }
        return regionList;
    }

    /**
     * 获取中国的地
     * @return
     */
    public static RegionEntity getRegionChina() {
        String selection = RegionConstants.COLUMN_TYPE + "= ? and " + RegionConstants.COLUMN_ID + "= ?";
        String[] selectionArgs = new String[]{String.valueOf(RegionConstants.TYPE_COUNTY) , RegionConstants.COUNTY_CHINA_ID};
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase mSQLiteDatabase = mg.getDatabase(RegionConstants.DB_REGION);
        if (mSQLiteDatabase == null) {
            return null;
        }
        // 对数据库进行操作
        Cursor cursor = mSQLiteDatabase.query(RegionConstants.TABLE_REGION, null, selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            while (cursor.moveToNext()) {
                RegionEntity regionEntity = new RegionEntity();
                int type = cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_TYPE));
                regionEntity.id = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_ID)));
                regionEntity.name = (cursor.getString(cursor.getColumnIndex(RegionConstants.COLUMN_NAME)));
                regionEntity.pid = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_PID)));
                regionEntity.type = (type);
                return regionEntity;
            }
        }catch (Exception ex){

        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 模糊搜索出城市
     * @return
     */
    public static RegionEntity getRegionByKey(String cityName) {
        cityName = RegionConstants.getCityNameNoSuffix(cityName);//过滤掉后缀
        String selection = RegionConstants.COLUMN_TYPE + "= ? and " + RegionConstants.COLUMN_NAME + " like ?";
        int regionType = RegionConstants.TYPE_CITY;
        if (RegionConstants.isProvinceCity(cityName)) {
            //如果是省级市，用省
            regionType = RegionConstants.TYPE_PROVINCE;
        }
        String[] selectionArgs = new String[]{String.valueOf(regionType), "%" + cityName + "%"};
        // 获取管理对象，因为数据库需要通过管理对象才能够获取
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        SQLiteDatabase mSQLiteDatabase = mg.getDatabase(RegionConstants.DB_REGION);
        // 对数据库进行操作
        Cursor cursor = mSQLiteDatabase.query(RegionConstants.TABLE_REGION, null, selection, selectionArgs, null, null, null);
        try {
            while (cursor.moveToNext()) {
                RegionEntity regionEntity = new RegionEntity();
                int type = cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_TYPE));
                regionEntity.id = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_ID)));
                regionEntity.name = (cursor.getString(cursor.getColumnIndex(RegionConstants.COLUMN_NAME)));
                regionEntity.pid = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_PID)));
                regionEntity.type = (type);
                return regionEntity;
            }
        } catch (Exception ex) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 获取展示的城市
     * @param regionId 区域ID
     * @param separator 分隔符
     * @return
     */
    public static String getShowRegionContent(int regionId , String separator) {
//        StringBuffer stringBuffer = new StringBuffer();
        String regionShow = "";
        //获取数据库中指定的属性，之后根据每一层的type去获取
        //一级
        RegionEntity regionEntity1 = RegionDBTools.getRegionById(regionId);
        if (regionEntity1 == null) {
            return "";
        }
        regionShow = regionEntity1.name;
        if(regionEntity1.type == RegionConstants.TYPE_CITY) {
            //城市，获取省
            RegionEntity regionEntity2 = RegionDBTools.getRegionById(regionEntity1.pid);
            if (regionEntity2 == null) {
                return "";
            }
            regionShow = regionEntity2.name + separator + regionShow;
            if(regionEntity2.type == RegionConstants.TYPE_PROVINCE){
                //城市，获取国家
                RegionEntity regionEntity3 = RegionDBTools.getRegionById(regionEntity2.pid);
                if (regionEntity3 == null) {
                    return "";
                }
                regionShow = regionEntity3.name + separator + regionShow;
            }
        } else if(regionEntity1.type == RegionConstants.TYPE_PROVINCE){
            //可能是省级城市
            RegionEntity regionEntity3 = RegionDBTools.getRegionById(regionEntity1.pid);
            if(regionEntity3 != null){
                regionShow = regionEntity3.name + separator + regionShow;
            }
        } else if(regionEntity1.type == RegionConstants.TYPE_COUNTY){

        }
        return regionShow;
    }

    public static RegionEntity getRegionById(int regionId){
        RegionEntity regionEntity = null;
        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
        // 通过管理对象获取数据库
        String selection = RegionConstants.COLUMN_ID + "= ?";
        SQLiteDatabase mSQLiteDatabase = mg.getDatabase(RegionConstants.DB_REGION);
        String[] selectionArgs = new String[]{String.valueOf(regionId)};
        if (mSQLiteDatabase == null) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = mSQLiteDatabase.query(RegionConstants.TABLE_REGION, null, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    regionEntity = new RegionEntity();
                    int type = cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_TYPE));
                    regionEntity.id = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_ID)));
                    regionEntity.name = (cursor.getString(cursor.getColumnIndex(RegionConstants.COLUMN_NAME)));
                    regionEntity.pid = (cursor.getInt(cursor.getColumnIndex(RegionConstants.COLUMN_PID)));
                    regionEntity.type = (type);
                    LogUtil.i(TAG , "nameId:" + regionEntity.name);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return regionEntity;
    }
}
