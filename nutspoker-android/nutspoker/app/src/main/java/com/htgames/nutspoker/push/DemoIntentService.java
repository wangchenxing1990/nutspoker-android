package com.htgames.nutspoker.push;

import android.content.Context;
import android.text.TextUtils;

import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.tool.AppMessageJsonTools;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.notifications.HTNotificationManager;
import com.netease.nim.uikit.PendingIntentConstants;
import com.htgames.nutspoker.util.AppProcessUtil;
import com.htgames.nutspoker.view.systemannouncement.SystemWindowManager;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * Created by 周智慧 on 17/4/18.
 */

public class DemoIntentService extends GTIntentService {
    AppProcessUtil mAppProcessUtil;
    HTNotificationManager mHTNotificationManager;
    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        LogUtil.e(TAG, "onReceiveServicePid -> " + " pid = " + (pid));
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        LogUtil.e(TAG, "onReceiveMessageData -> " + "GTTransmitMessage msg = " + (msg == null ? "msg=null" : msg.toString()));
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        LogUtil.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));
        LogUtil.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg + "\ncid = " + cid);
        AppMessage appMessage = null;
        if (mAppProcessUtil == null) {
            mAppProcessUtil = new AppProcessUtil();
        }
        if (payload == null) {
            LogUtil.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            LogUtil.d(TAG, "receiver payload = " + data);
            // 测试消息为了观察数据变化
//            if (data.equals("收到一条透传测试消息")) {
//                data = data + "-" + cnt;
//                cnt++;
//            }
//            sendMessage(data, 0);
            if (StringUtil.isSpace(data)) {
                return;
            }
            String showMessage = "";//用于通知展示的消息内容
            int pendingAction = 0;
            int type = PushConstants.getPushType(data);
            if (type == PushConstants.TYPE_ANNOUNCEMENT && PushConstants.isAnnouncementShow(data)) {
                AnnouncementEntity announcementEntity = PushConstants.getAnnouncement(data);
                if (announcementEntity != null && !TextUtils.isEmpty(announcementEntity.msg)) {
                    showMessage = announcementEntity.msg;
                    //
                    LogUtil.i(TAG, "接受到紧急系统通知 : " + announcementEntity.msg);
                    if (!TextUtils.isEmpty(showMessage) && !TextUtils.isEmpty(DemoCache.getAccount()) && mAppProcessUtil != null && !mAppProcessUtil.isApplicationBroughtToBackground(context)) {
                        //1。用户已经登录   2.APP在当前界面
                        SystemWindowManager.createSystemAnnouncementView(context, announcementEntity.msg);
                    }
                }
            } else if (type == PushConstants.TYPE_APP_MSG) {
                String jsonData = PushConstants.getPushData(data);
                //App消息(需要登录才入库)
                appMessage = AppMessageJsonTools.parseAppMessage(jsonData);
                //如果状态为同意或者拒绝（房主），设置为已读
                if (appMessage.status == AppMessageStatus.passed || appMessage.status == AppMessageStatus.declined || appMessage.online == 1) {
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
        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        LogUtil.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        LogUtil.e(TAG, "onReceiveOnlineState -> " + "online = " + online);
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        LogUtil.e(TAG, "onReceiveCommandResult -> " + "cmdMessage = " + (cmdMessage == null ? "cmdMessage=null" : (cmdMessage.toString() + "action: " + cmdMessage.getAction())));
        if (cmdMessage == null) {
            return;
        }
        int action = cmdMessage.getAction();
        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();
        String text = "设置标签失败, 未知异常";
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = "设置标签成功";
                break;
            case PushConsts.SETTAG_ERROR_COUNT:
                text = "设置标签失败, tag数量过大, 最大不能超过200个";
                break;
            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = "设置标签失败, 频率过快, 两次间隔应大于1s且一天只能成功调用一次";
                break;
            case PushConsts.SETTAG_ERROR_REPEAT:
                text = "设置标签失败, 标签重复";
                break;
            case PushConsts.SETTAG_ERROR_UNBIND:
                text = "设置标签失败, 服务未初始化成功";
                break;
            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = "设置标签失败, 未知异常";
                break;
            case PushConsts.SETTAG_ERROR_NULL:
                text = "设置标签失败, tag 为空";
                break;
            case PushConsts.SETTAG_NOTONLINE:
                text = "还未登陆成功";
                break;
            case PushConsts.SETTAG_IN_BLACKLIST:
                text = "该应用已经在黑名单中,请联系售后支持!";
                break;
            case PushConsts.SETTAG_NUM_EXCEED:
                text = "已存 tag 超过限制";
                break;
            default:
                break;
        }

        LogUtil.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + text);
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();
        LogUtil.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }
}
