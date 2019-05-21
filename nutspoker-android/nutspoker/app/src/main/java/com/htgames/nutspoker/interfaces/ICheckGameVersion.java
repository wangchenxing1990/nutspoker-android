package com.htgames.nutspoker.interfaces;

import com.netease.nim.uikit.bean.GameEntity;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;

/**
 * Created by 周智慧 on 17/3/21.
 */

public interface ICheckGameVersion {
    void checkGameVersionJoin(GameEntity gameEntity);//加入牌局
    void checkGameVersionCreate(CheckHotUpdateCallback callback);//创建牌局
}
