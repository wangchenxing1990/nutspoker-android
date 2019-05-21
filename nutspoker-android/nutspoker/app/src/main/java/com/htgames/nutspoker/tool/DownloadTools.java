package com.htgames.nutspoker.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.htgames.nutspoker.receiver.DownloadReceiver;
import com.netease.nim.uikit.chesscircle.CacheConstant;

import java.io.File;
import com.netease.nim.uikit.common.util.log.LogUtil;
public class DownloadTools {
    private final static String TAG = "DownloadTools";
    public Activity activity;
    public Context context;
    public String app_url;
    public String app_name;
    DownloadReceiver mBroadcastReceiver;
    /**
     * APK文件名
     */
    public final static String APP_NAME = "Poker";
    public final static String APK = ".apk";
    String downloadPath = "";

    /**
     * 下载工具类
     * @param activity
     * @param app_url  下载URL
     * @param app_name 下载文件名
     */
    public DownloadTools(Activity activity, String app_url, String app_name) {
        LogUtil.i("url", app_url);
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.app_url = app_url;
        this.app_name = app_name;
        this.downloadPath = CacheConstant.getAppDownloadPath();
    }

    public void download() {
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 9) {
            registerBoradcastReceiver();
            down_new(app_url);
            Toast.makeText(context, activity.getString(R.string.version_downloading), Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse(app_url);
            Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(downloadIntent);
        }
    }

    @SuppressLint("NewApi")
    public void down_new(String app_url) {
        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Activity.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(app_url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
        // request.setShowRunningNotification(false);
        // 不显示下载界面
        request.setVisibleInDownloadsUi(true);
        File dowloadFolder = new File(downloadPath);
        if(!dowloadFolder.exists()){
            dowloadFolder.mkdirs();
        }
//        request.setTitle(activity.getString(R.string.version_downloading));
        request.setTitle(activity.getString(R.string.app_name));
        if (FileUtil.checkSDCard()) {
            File file = new File(downloadPath + app_name);
            if (file.exists()) {
                LogUtil.i(TAG, file.getPath() + ":存在");
                file.delete();
            } else {
                LogUtil.i(TAG, file.getPath() + ":不存在");
            }
            String downloadDir = CacheConstant.APP_FOLDER_NAME + "/" + CacheConstant.APP_DOWNLOAD_PATH_NAME;
            request.setDestinationInExternalPublicDir(downloadDir, app_name);//设置下载路径和文件名
            request.setDescription(activity.getString(R.string.version_downloading));
        }
        // request.setDestinationInExternalFilesDir(this, null, "tar.apk");
        long id = downloadManager.enqueue(request);
        // TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
        SettingsPreferences.getInstance(context).setDownloadId(String.valueOf(id));
    }

    /**
     * 获取应用APK的文件名
     * @param newVersion
     * @return
     */
    public static String getAppFileName(String newVersion) {
        String filaName = APP_NAME + "_" + newVersion + APK;
        return filaName;
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadReceiver.ACTION_DOWNLOAD);
        mBroadcastReceiver = new DownloadReceiver();
        // 注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 退出的时候要把注册的Receiver广播给关掉
     */
    public void unregisterDownloadReceiver() {
        if (mBroadcastReceiver != null) {
            activity.unregisterReceiver(mBroadcastReceiver);
        }
    }
}