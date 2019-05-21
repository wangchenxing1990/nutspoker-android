package com.htgames.nutspoker.chat.app_msg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.netease.nim.uikit.session.constant.Extras;

/**
 * APP消息
 */
public class AppMessageReceiver extends BroadcastReceiver {
    public static final String ACTION_APP_MESSAGE = DemoCache.getContext().getPackageName() + ".action.appmessage";

    @Override
    public void onReceive(Context context, Intent intent) {
        //App消息
        if (intent != null) {
            AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
            if (appMessage != null) {
                if (appMessage.unread) {
                    ReminderManager.getInstance().updateAppMsgUnreadNum(1, true, appMessage);
                }
            }
        }
    }
}
