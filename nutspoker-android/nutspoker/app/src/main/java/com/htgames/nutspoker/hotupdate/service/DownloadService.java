package com.htgames.nutspoker.hotupdate.service;

import android.app.IntentService;
import android.content.Intent;

import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.htgames.nutspoker.hotupdate.HotUpdateConstants;
import com.htgames.nutspoker.hotupdate.download.DownloadRunnable;
import com.htgames.nutspoker.hotupdate.download.DownloadThreadPool;
import com.htgames.nutspoker.hotupdate.interfaces.OnDownloadListener;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.model.UpdateInfoEntity;
import com.htgames.nutspoker.hotupdate.receiver.UpdateReceiver;
import com.htgames.nutspoker.hotupdate.tool.FileTools;
import com.htgames.nutspoker.hotupdate.tool.MD5Util;

import java.io.File;

/**
 * 下载服务
 */
public class DownloadService extends IntentService {
    public final static String TAG = HotUpdateAction.TAG;
    DownloadThreadPool pool;
    boolean isDownloading = false;
    int downloadFileCount = 0;//下载的文件总数
    int successFileCount = 0;//下载成功的数量
    int finishFileCount = 0;//下载结束的数量（不论成功失败）
    String newVersion = "";
    UpdateInfoEntity updateInfoEntity;

    public DownloadService() {
        super("DownloadService");
        pool = new DownloadThreadPool();
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setIntentRedelivery(true);
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null && !isDownloading) {
            isDownloading = true;
            downloadFileCount = 0;
            successFileCount = 0;
            finishFileCount = 0;
            updateInfoEntity = (UpdateInfoEntity) intent.getSerializableExtra(HotUpdateConstants.EXTRA_KEY_DATA);
            if (updateInfoEntity != null && updateInfoEntity.diffFileList != null) {
                newVersion = updateInfoEntity.newVersion;
                downloadFileCount = updateInfoEntity.diffFileList.size();
                try {
                    for (UpdateFileEntity fileEntity : updateInfoEntity.diffFileList) {
                        downloadFile(fileEntity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void downloadFile(final UpdateFileEntity fileEntity) {
        File downFile = HotUpdateConstants.getUpdateTemporaryResFile(fileEntity.fileName);
        LogUtil.d(TAG, "文件：" + fileEntity.fileName + (!downFile.exists() ? "不存在" : "存在"));
        LogUtil.d(TAG, fileEntity.fileCode + " :  " + MD5Util.getFileMD5String(downFile));
        if(!downFile.exists() || !fileEntity.fileCode.equals(MD5Util.getFileMD5String(downFile))) {
            //1.文件不存在  2.文件的MD5值和服务端不匹配
             DownloadRunnable downloadRunnable = new DownloadRunnable(updateInfoEntity.newVersion , fileEntity, new OnDownloadListener() {
                @Override
                public void success() {
                    LogUtil.d(TAG, "文件：" + fileEntity.fileName + " 下载成功");
                    downloadSuccess(fileEntity);
                }

                @Override
                public void failure() {
                    LogUtil.d(TAG, "文件：" + fileEntity.fileName + " 下载失败");
                    downloadFailure(fileEntity);
                }
            });
            pool.execute(downloadRunnable);
        } else{
            LogUtil.d(TAG , "文件已经存在：" + fileEntity.fileName);
            downloadSuccess(fileEntity);
        }
    }

    public synchronized void downloadSuccess(final UpdateFileEntity fileEntity){
        successFileCount = successFileCount + 1;
        finishFileCount = finishFileCount + 1;
        sendNewGameMessage(successFileCount , finishFileCount, fileEntity);
        checkDownloadFinish();
    }

    public synchronized void downloadFailure(final UpdateFileEntity fileEntity){
        finishFileCount = finishFileCount + 1;
        sendNewGameMessage(successFileCount , finishFileCount, fileEntity);
        checkDownloadFinish();
    }

    public synchronized void checkDownloadFinish() {
        if (finishFileCount == downloadFileCount) {
            isDownloading = false;
            //完成下载
            if (successFileCount == downloadFileCount) {
                //说明下载成功,copy到游戏文件夹，COPY成功，删除当前目录，保存当前游戏版本
                FileTools.moveDirectory(HotUpdateConstants.getUpdateTemporaryResPath(), HotUpdateConstants.getUpdateResPath());
            } else {
                //说明下载失败

            }
        }
    }

    /**
     * 发送有游戏的消息提醒
     */
    public void sendNewGameMessage(int successFileCount , int finishFileCount, final UpdateFileEntity fileEntity) {
        Intent intent = new Intent(UpdateReceiver.ACTION_UPDATE);
        intent.putExtra("zzh", fileEntity);
        intent.putExtra(UpdateReceiver.EXTRA_FILE_ALL_COUNT, downloadFileCount);
        intent.putExtra(UpdateReceiver.EXTRA_FILE_SUCCESS_COUNT, successFileCount);
        intent.putExtra(UpdateReceiver.EXTRA_FILE_FINISH_COUNT, finishFileCount);
        intent.putExtra(UpdateReceiver.EXTRA_VERSION_NEW, newVersion);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
