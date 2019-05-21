package com.htgames.nutspoker.hotupdate.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.AppVersionEntity;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.htgames.nutspoker.hotupdate.HotUpdateConstants;
import com.htgames.nutspoker.receiver.DownloadReceiver;
import com.htgames.nutspoker.tool.DownloadTools;
import com.htgames.nutspoker.tool.FileUtil;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.File;

/**
 * Created by 周智慧 on 17/3/31.更新app下载的service
 */

public class DownloadAppService extends IntentService {
    public static final String TAG = DownloadService.TAG;
    DownloadReceiver mBroadcastReceiver;
    AppVersionEntity appVersionEntity;
    String downloadPath = CacheConstant.getAppDownloadPath();
    public DownloadAppService() {
        super("DownloadAppService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadAppService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setIntentRedelivery(true);
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        appVersionEntity = (AppVersionEntity) intent.getSerializableExtra(HotUpdateConstants.EXTRA_KEY_DATA);
        String url = appVersionEntity.downloadUrl;
        String newVersion = appVersionEntity.newVersion;
        String app_name = DownloadTools.getAppFileName(newVersion);

        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 9) {
            registerBoradcastReceiver();
        } else {
            downloadForWebView();
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            // 禁止发出通知，既后台下载，如果要使用这一句必须声明一个权限：android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
            // request.setShowRunningNotification(false);
            // 不显示下载界面
            //在通知栏中显示
            request.setVisibleInDownloadsUi(true);
            request.setTitle(getString(R.string.app_name));

            File dowloadFolder = new File(downloadPath);
            if(!dowloadFolder.exists()){
                dowloadFolder.mkdirs();
            }
            if (FileUtil.checkSDCard()) {
                File file = new File(downloadPath + app_name);
                if (file.exists()) {
                    LogUtil.i(TAG, file.getPath() + ":存在");
                    file.delete();
                } else {
                    LogUtil.i(TAG, file.getPath() + ":不存在");
                }
                String downloadDir = CacheConstant.APP_FOLDER_NAME + "/" + CacheConstant.APP_DOWNLOAD_PATH_NAME;
//                request.setDestinationInExternalPublicDir(downloadDir, app_name);//设置下载路径和文件名
                request.setDestinationUri(Uri.parse("file://" + downloadPath + app_name));
                request.setDescription(getString(R.string.version_downloading));
            }
            // request.setDestinationInExternalFilesDir(this, null, "tar.apk");
            long id = downloadManager.enqueue(request);
            //  把id保存好，在接收者里面要用，最好保存在Preferences里面
            SettingsPreferences.getInstance(this).setDownloadId(String.valueOf(id));
        } catch (Exception e) {
            e.printStackTrace();
            downloadForWebView();
        } finally {
//            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    private void downloadForWebView() {
        Uri uri = Uri.parse(appVersionEntity.downloadUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        unregisterDownloadReceiver();
        super.onDestroy();
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DownloadReceiver.ACTION_DOWNLOAD);
        mBroadcastReceiver = new DownloadReceiver();
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 退出的时候要把注册的Receiver广播给关掉
     */
    public void unregisterDownloadReceiver() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }
}
