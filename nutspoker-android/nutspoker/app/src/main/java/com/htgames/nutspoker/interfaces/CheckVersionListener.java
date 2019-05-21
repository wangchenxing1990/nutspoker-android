package com.htgames.nutspoker.interfaces;

import com.netease.nim.uikit.bean.AppVersionEntity;

/**
 * 版本检测监听接口
 */
public interface CheckVersionListener {
    public void onRedirect(boolean toActivity);

    public void onCheckSuccess(AppVersionEntity appVersionEntity);

    public void onCheckError();

    public void onCheckNotNew();
}
