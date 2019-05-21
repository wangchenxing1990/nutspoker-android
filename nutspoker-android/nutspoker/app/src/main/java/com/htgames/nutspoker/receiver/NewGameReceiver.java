package com.htgames.nutspoker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.common.DemoCache;

/**
 * 游戏相关广播接收器
 */
public class NewGameReceiver extends BroadcastReceiver {
    public static final String ACTION_NEWGAME = DemoCache.getContext().getPackageName() + ".action.newgame";
    public static final int ACTION_NEWGAME_TYPE = 0;//创建新游戏发送广播
    public static final int ACTION_CANCELGAME_TYPE = 1;//解散游戏发送广播
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
