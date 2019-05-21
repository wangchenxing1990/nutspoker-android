package com.htgames.nutspoker.interfaces;

/**
 * Created by glp on 2016/8/10.
 */

public interface DownloadCallback {
    //通用请求接口
    void onDownload(String s);

    void onFailed();
}
