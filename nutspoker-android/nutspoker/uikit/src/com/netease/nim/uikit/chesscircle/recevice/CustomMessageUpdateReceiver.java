package com.netease.nim.uikit.chesscircle.recevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.NimUIKit;

public class CustomMessageUpdateReceiver extends BroadcastReceiver {
    public final static String ACTION_CUSTOMMESSAGE_UPDATE = NimUIKit.getContext().getPackageName() + ".custommessage_update";

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
