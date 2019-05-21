package com.htgames.nutspoker.hotupdate.tool;

import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;

/**
 * Created by 周智慧 on 17/4/14.
 */

public class HotUpdateItem {
    public int downloadFileCount;
    public int finishFileCount;
    public int successFileCount;

    public String newVersion;
    public UpdateFileEntity updateFileEntity;

    public static final int UPDATE_TYPE_ING = 0;//更新中
    public static final int UPDATE_TYPE_STUCK = 1;//热更新失败
    public int updateType;

    public HotUpdateItem(int updateType) {
        this.updateType = updateType;
    }

    public HotUpdateItem setDownloadFileCount(int downloadFileCount) {
        this.downloadFileCount = downloadFileCount;
        return this;
    }

    public HotUpdateItem setFinishFileCount(int finishFileCount) {
        this.finishFileCount = finishFileCount;
        return this;
    }

    public HotUpdateItem setSuccessFileCount(int successFileCount) {
        this.successFileCount = successFileCount;
        return this;
    }

    public HotUpdateItem setNewVersion(String newVersion) {
        this.newVersion = newVersion;
        return this;
    }

    public HotUpdateItem setUpdateFileEntity(UpdateFileEntity updateFileEntity) {
        this.updateFileEntity = updateFileEntity;
        return this;
    }
}
