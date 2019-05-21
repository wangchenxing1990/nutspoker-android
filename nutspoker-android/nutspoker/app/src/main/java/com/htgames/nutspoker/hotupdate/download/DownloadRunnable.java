package com.htgames.nutspoker.hotupdate.download;

import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.htgames.nutspoker.hotupdate.HotUpdateConstants;
import com.htgames.nutspoker.hotupdate.interfaces.OnDownloadListener;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.tool.MD5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载，下载失败重试
 */
public class DownloadRunnable extends BaseRunable {
    private final String TAG = HotUpdateAction.TAG;
    OnDownloadListener onDownloadListener;
    UpdateFileEntity fileEntity;
    private int MAX_DOWNLOAD_RETRY_COUNT = 3;//最大重试次数
    private int currentRetryCount = 0;//当前尝试次数
    private boolean isDownloadIng = false;
    String newVersion;

    public DownloadRunnable(String newVersion, UpdateFileEntity fileEntity ,OnDownloadListener listener) {
        this.onDownloadListener = listener;
        this.fileEntity = fileEntity;
        this.newVersion = newVersion;
    }

    @Override
    public void run() {
        super.run();
//        if(fileEntity.getFileName().contains("facebook")){
//            //用于测试大文件下载
//            downloadFile( "http://gdown.baidu.com/data/wisegame/f039674abed341c2/Facebook_25862464.apk", HotUpdateConstants.getUpdateTemporaryResFile(fileEntity.getFileName()));
//        } else{
            downloadFile(HotUpdateConstants.getHotUpdateResUrl(newVersion ,fileEntity.fileName) , HotUpdateConstants.getUpdateTemporaryResFile(fileEntity.fileName));
//        }
    }

    public void downloadFile(String downloadUrl, File file) {
        LogUtil.d(TAG, downloadUrl);
        //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
        if(isDownloadIng){
            return;
        }
        isDownloadIng = true;
        if (file.mkdirs() && file.exists()) {
            file.delete();
        }
        FileOutputStream os = null;
        InputStream is = null;
        int downloadLength = 0;
        try {
//            String encodeUrl = URLEncoder.encode(downloadUrl, "UTF-8");
//            String str = Uri.encode(downloadUrl);
            URL url = new URL(downloadUrl); // 构造URL
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // 打开连接
            con.setRequestMethod("GET");
            con.setReadTimeout(10000);
            con.setConnectTimeout(5000);
            int contentLength = con.getContentLength();//获得文件的长度
            LogUtil.d(TAG, downloadUrl + "  path:" + file.getPath() + "长度 :" + contentLength);
            int responseCode = con.getResponseCode();// 拿到服务器返回的响应码
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = con.getInputStream(); // 输入流
                byte[] bs = new byte[1024]; // 1K的数据缓冲
                int len; // 读取到的数据长度
                // 输出的文件流
                os = new FileOutputStream(file);
                // 开始读取
                while ((len = is.read(bs)) != -1) {
                    os.write(bs, 0, len);
                    os.flush();
                    downloadLength = downloadLength + len;
//                    Log.d(TAG, file.getName() + ":" + downloadLength);
                }
            } else {
            }
        } catch (Exception e) {
            LogUtil.d(TAG, e.toString());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                //下载完成，判断code是否一致，一致的话，下载成功
                if(file.exists() && MD5Util.getFileMD5String(file).equals(fileEntity.fileCode)){
                    onDownloadListener.success();
                    isDownloadIng = false;
                } else{
                    LogUtil.d(TAG, "md5  :  " + MD5Util.getFileMD5String(file) + "  ; code  :  " + fileEntity.fileCode);
                    checkDownloadRetry();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断下载失败重试
     */
    public void checkDownloadRetry(){
        if(currentRetryCount == MAX_DOWNLOAD_RETRY_COUNT){
            LogUtil.d(TAG , "已经重试3次，下载失败");
            currentRetryCount = 0;
            isDownloadIng = false;
            onDownloadListener.failure();
        } else{
            isDownloadIng = false;
            LogUtil.d(TAG, "下载失败 ， 重试 :" + currentRetryCount);
            currentRetryCount = currentRetryCount + 1;
            downloadFile(HotUpdateConstants.getHotUpdateResUrl(newVersion ,fileEntity.fileName), HotUpdateConstants.getUpdateTemporaryResFile(fileEntity.fileName));
        }
    }
}
