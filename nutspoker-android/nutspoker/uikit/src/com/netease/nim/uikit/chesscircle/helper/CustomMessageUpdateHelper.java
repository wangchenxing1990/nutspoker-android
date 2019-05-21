package com.netease.nim.uikit.chesscircle.helper;

import android.content.Context;
import android.content.Intent;

import com.netease.nim.uikit.chesscircle.recevice.CustomMessageUpdateReceiver;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.msg.model.IMMessage;

public class CustomMessageUpdateHelper {

    //收到新的系统消息，发送通知
    public static void updateIMMessageStatus(Context context, IMMessage imMessage) {
        Intent intent = new Intent();
        intent.setAction(CustomMessageUpdateReceiver.ACTION_CUSTOMMESSAGE_UPDATE);
        intent.putExtra(Extras.EXTRA_MESSAGE, imMessage);
        context.sendBroadcast(intent);
    }

}
