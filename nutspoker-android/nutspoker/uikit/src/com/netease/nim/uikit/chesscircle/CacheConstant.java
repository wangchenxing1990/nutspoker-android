package com.netease.nim.uikit.chesscircle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;

import com.netease.nim.uikit.common.framework.AppDirUtil;

import java.io.File;

/**
 * 缓存目录
 */
public class CacheConstant {
    public static boolean debugBuildType = false;//是否是debug编译
    public static int sMaxThread;
    public static Context sAppContext;
    public static AppDirUtil sAppDirUtil;
    public static final String APP_FOLDER_NAME = "EverPoker";
    public static final String APP_PICTURE_PATH_NAME = "扑克部落";
    //
    public static final String APP_HAND_COLLECT_PATH_NAME = "handcollect";//牌局收藏文件夹
    public static final String APP_HAND_RECORD_PATH_NAME = "handrecord";//牌局记录文件夹
    public static final String APP_HAND_CACHE = "cache/hand";//手牌缓存目录
    //
    public static final String APP_DOWNLOAD_PATH_NAME = "download";//下载文件夹
    public static final String APP_CACHE_GAME_IMGAE = "cache/image";//下载文件夹
    public static final String APP_NIM = "nim";//
    public static Activity mTopActivity = null;
    public static boolean isMonopolyDialogShow = false;
    public static Location mLocation = null;
    public static void init(Application app, Context context) {
        sAppContext = context;
        sMaxThread = Runtime.getRuntime().availableProcessors() * 2 + 1;
        sAppDirUtil = new AppDirUtil(sAppContext);
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mTopActivity = activity;
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mTopActivity = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    public static String getAppStoragePath() {
//        String externalPath = Environment.getExternalStorageDirectory().getPath();
//        return externalPath + "/" + CacheConstant.APP_FOLDER_NAME;
        String rootPath = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {//sd卡能用
            rootPath = Environment.getExternalStorageDirectory().getPath();
        } else {//sd卡不能用
            if (sAppContext.getFilesDir() != null) {
                rootPath = sAppContext.getFilesDir().getPath();
            }
        }
        return rootPath + File.separator + CacheConstant.APP_FOLDER_NAME;
    }

    public static String getAppDownloadPath(){
        String path = getAppStoragePath() + "/" + APP_DOWNLOAD_PATH_NAME + "/";
        return path;
    }

    public static String getAppHandRecordPath(){
        String path = getAppStoragePath() + "/" + APP_HAND_RECORD_PATH_NAME + "/";
        return path;
    }

    //获取收藏文件夹
//    public static String getAppHandCollectPath(){
//        String path = getAppStoragePath() + "/" + APP_HAND_COLLECT_PATH_NAME + "/";
//        return path;
//    }

    //获取手牌缓存文件夹
//    public static String getHandCachePath(){
//        String path =  getAppStoragePath() + "/" + APP_HAND_CACHE;
//        return path;
//    }

    public static String getGameImageCachePath(){
        String path =  getAppStoragePath() + "/" + APP_CACHE_GAME_IMGAE;//这个不带/，游戏那边自己加
        return path;
    }

    public static String getImageCachePath() {
        String path = getAppStoragePath() + "/" + APP_CACHE_GAME_IMGAE;//这个不带/，
        return path;
    }

    //云信的相关文件夹
    public static String getNimPath() {
        String path = getAppStoragePath() + "/" + APP_NIM;//
        return path;
    }

    public static String GetString(int resId){
        return sAppContext.getResources().getString(resId);
    }
}
