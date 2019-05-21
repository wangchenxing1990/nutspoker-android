package com.htgames.nutspoker.game.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.common.DemoCache;

/**
 * 报名处理通知
 */
public class CheckInDealReceiver extends BroadcastReceiver {
    public static final String ACTION_CHECKIN = DemoCache.getContext().getPackageName() + ".action.checkindeal";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}