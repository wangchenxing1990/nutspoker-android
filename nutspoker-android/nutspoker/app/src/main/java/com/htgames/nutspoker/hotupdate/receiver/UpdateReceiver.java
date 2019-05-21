package com.htgames.nutspoker.hotupdate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.common.DemoCache;

public class UpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_UPDATE = DemoCache.getContext().getPackageName() + ".action.update";
    public static final String EXTRA_FILE_ALL_COUNT = "allCount";
    public static final String EXTRA_FILE_SUCCESS_COUNT = "successCount";
    public static final String EXTRA_FILE_FINISH_COUNT = "finishCount";
    public static final String EXTRA_VERSION_NEW = "newVersion";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
