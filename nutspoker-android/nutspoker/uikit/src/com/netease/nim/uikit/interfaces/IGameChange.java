package com.netease.nim.uikit.interfaces;

import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

/**
 * Created by 周智慧 on 17/3/17.
 */

public interface IGameChange {
    void onGameCreate(List<IMMessage> messages);
}
