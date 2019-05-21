package com.htgames.nutspoker.push;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.tool.AppMessageJsonTools;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.notifications.HTNotificationManager;
import com.netease.nim.uikit.PendingIntentConstants;
import com.htgames.nutspoker.util.AppProcessUtil;
import com.htgames.nutspoker.view.systemannouncement.SystemWindowManager;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

/**
 * 个推
 */
public class PushReceiver extends BroadcastReceiver {
    private final static String TAG = "PushReceiver";
    HTNotificationManager mHTNotificationManager;
    AppProcessUtil mAppProcessUtil;

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AppMessage appMessage = null;
        LogUtil.i(TAG, "onReceive() action=" + bundle.getInt("action"));
        if (mAppProcessUtil == null) {
            mAppProcessUtil = new AppProcessUtil();
        }
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");
                byte[] payload = bundle.getByteArray("payload");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                LogUtil.i(TAG, "第三方回执接口调用" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    LogUtil.i(TAG, "个推data：" + (data));
                    String showMessage = "";//用于通知展示的消息内容
                    int pendingAction = 0;
                    //
                    int type = PushConstants.getPushType(data);
                    if (type == PushConstants.TYPE_ANNOUNCEMENT && PushConstants.isAnnouncementShow(data)) {
                        AnnouncementEntity announcementEntity = PushConstants.getAnnouncement(data);
                        if (announcementEntity != null && !TextUtils.isEmpty(announcementEntity.msg)) {
                            showMessage = announcementEntity.msg;
                            //
                            LogUtil.i(TAG, "接受到紧急系统通知 : " + announcementEntity.msg);
                            if (!TextUtils.isEmpty(showMessage) && !TextUtils.isEmpty(DemoCache.getAccount())
                                    && mAppProcessUtil != null && !mAppProcessUtil.isApplicationBroughtToBackground(context)) {
                                //1。用户已经登录   2.APP在当前界面
                                SystemWindowManager.createSystemAnnouncementView(context, announcementEntity.msg);
                            }
                        }
                    } else if (type == PushConstants.TYPE_APP_MSG) {
                        String jsonData = PushConstants.getPushData(data);
                        //App消息(需要登录才入库)
                        appMessage = AppMessageJsonTools.parseAppMessage(jsonData);
                        //如果状态为同意或者拒绝（房主），设置为已读
                        if (appMessage.status == AppMessageStatus.passed || appMessage.status == AppMessageStatus.declined) {
                            appMessage.unread = (false);
                        }
                        LogUtil.i(TAG, "appMessage :" + appMessage.type);
                        showMessage = appMessage.content;
                        pendingAction = PendingIntentConstants.ACTION_APP_MESSAGE;
                        if ((appMessage.type == AppMessageType.MatchBuyChips || appMessage.type == AppMessageType.GameBuyChips)) {
                            pendingAction = PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL;//以前"系统消息"和"控制中心"是同一个页面，后来分开了
                        }
                        AppMsgDBHelper.addAppMessage(DemoCache.getContext(), appMessage);
                        AppMessageHelper.sendAppMessageIncoming(context, appMessage);
                    }
                    //通知栏展示
                    if (!TextUtils.isEmpty(showMessage)) {
                        LogUtil.i(TAG, "showMessage :" + showMessage);
                        if (mHTNotificationManager == null) {
                            mHTNotificationManager = HTNotificationManager.getInstance(context);
                        }
                        mHTNotificationManager.showIntentActivityNotify(context, showMessage, pendingAction);
                    }
                    //
                    LogUtil.i(TAG, "receiver payload : " + data);

                    payloadData.append(data);
                    payloadData.append("\n");
                    //
//                    if (MainActivity.tLogView != null) {
//                        GetuiSdkDemoActivity.tLogView.append(data + "\n");
//                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
//                if (GetuiSdkDemoActivity.tView != null) {
//                    GetuiSdkDemoActivity.tView.setText(cid);
//                }
//                Log.d(TAG , "cid :" + cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
}

