package com.htgames.nutspoker.hotupdate.interfaces;

import com.htgames.nutspoker.hotupdate.model.UpdateInfoEntity;

/**
 * 更新接口
 */
public interface UpdateRequestCallback {
    public void onSuccess(UpdateInfoEntity updateInfoEntity);
    public void onFailure();
}
