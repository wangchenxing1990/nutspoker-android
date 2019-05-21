package com.htgames.nutspoker.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.PendingIntentConstants;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.netease.nim.uikit.session.constant.Extras;

/**
 * 通知栏管理类
 */
public class HTNotificationManager {
    /** Notification管理 */
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    Context context;
    /** Notification的ID */
    public int notifyId = 100;
    private int requestCode = 0;

    public static HTNotificationManager getInstance(Context context) {
        HTNotificationManager instance = new HTNotificationManager(context);
        return instance;
    }

    public HTNotificationManager(Context context) {
        this.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//初始化要用到的系统服务
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(context.getString(R.string.app_name))
                .setContentText("内容")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
//                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.icon);
    }

    public void showNotify(String title , String message) {
        mBuilder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
//				.setNumber(number)//显示数量
                .setTicker(message);//通知首次出现在通知栏，带上升动画效果的
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    /** 显示通知栏点击跳转到指定Activity */
    public void showIntentActivityNotify(Context context ,String message , int pendingAction) {
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//        notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setTicker(message);

        //判断是否需要声音提醒
        SettingsPreferences settingsPreferences = SettingsPreferences.getInstance(context);
        boolean isControlMsg = pendingAction == PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL;//控制中心的消息提示音特殊化，其余默认
        //需要注意一点，如果default、sound同时出现，那么sound无效，会使用默认铃声。
        //同样，如果default、vibrate同时出现时，会采用默认形式。   另外还需要注意一点：使用振动器时需要权限，如下：
        if (settingsPreferences.isMessageNotice()) {
            if (settingsPreferences.isMessageShake() && settingsPreferences.isMessageSound()) {
                if (isControlMsg) {
                    mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                    mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification));
                } else {
                    mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
                }
            } else if (settingsPreferences.isMessageShake()) {
                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                mBuilder.setSound(null);//.vibrate = null;
            } else if (settingsPreferences.isMessageSound()) {
                mBuilder.setVibrate(null);
                if (isControlMsg) {
                    mBuilder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification));
                } else {
                    mBuilder.setDefaults(Notification.DEFAULT_SOUND);
                }
            } else {
                mBuilder.setVibrate(null);//.vibrate = null;
                mBuilder.setSound(null);//.vibrate = null;
            }
        }
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(context, LoginActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (!TextUtils.isEmpty(DemoCache.getAccount())) {
            resultIntent = new Intent(context, MainActivity.class);
//            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            resultIntent.putExtra(Extras.EXTRA_PENDINGINTENT_ACIONT, pendingAction);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
        requestCode = requestCode + 1;
    }

    public void clearAppNotify() {
        clearNotify(notifyId);
    }

    /**
     * 清除当前创建的通知栏
     */
    public void clearNotify(int notifyId){
        mNotificationManager.cancel(notifyId);//删除一个特定的通知ID对应的通知
//		mNotification.cancel(getResources().getString(R.string.app_name));
    }

    /**
     * 清除所有通知栏
     * */
    public void clearAllNotify() {
        mNotificationManager.cancelAll();// 删除你发的所有通知
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
