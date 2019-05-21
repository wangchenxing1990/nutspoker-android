package com.htgames.nutspoker.chat.notification;

import android.text.TextUtils;

import com.crl.zzh.customrefreshlayout.ControlToast;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.helper.AppMessageHelper;
import com.htgames.nutspoker.chat.app_msg.helper.GameOverHelper;
import com.htgames.nutspoker.chat.app_msg.helper.HordeMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.tool.AppMessageJsonTools;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.notifications.HTNotificationManager;
import com.netease.nim.uikit.PendingIntentConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class CustomNotificationCache {
    private final static String TAG = "CustomNotificationCache";
    private static CustomNotificationCache instance;
    private List<CustomNotificationObserver> messageObservers = new ArrayList<>();
    HTNotificationManager mHTNotificationManager;

    public static synchronized CustomNotificationCache getInstance() {
        if (instance == null) {
            instance = new CustomNotificationCache();
        }
        LogUtil.i(TAG , "getInstance");
        return instance;
    }

    public void registerObservers(boolean register) {
        //注册自定义通知,如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(mCustomNotificationObserve, register);
    }

    //云信自定义通知接收
    Observer<CustomNotification> mCustomNotificationObserve = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            dealNewCustomNotification(message);
            notifyCustomNotification(message);
        }
    };

    public void dealNewCustomNotification(CustomNotification message) {
        String content = message.getContent();
        if (!TextUtils.isEmpty(content)) {
            LogUtil.i(TAG, "content:" + content);
        }
        try {
            JSONObject jsonObject = new JSONObject(content);
            int type = jsonObject.optInt(CustomNotificationConstants.KEY_NOTIFICATION_TYPE);
            String data = jsonObject.optString(CustomNotificationConstants.KEY_NOTIFICATION_DATA);
            if (type == CustomNotificationConstants.NOTIFICATION_TYPE_APP_MSG) {
                //App消息
                AppMessage appMessage = AppMessageJsonTools.parseAppMessage(data);
                if (!AppMessageHelper.isKnowAppMessage(appMessage)) {
                    //未知消息类型，不做处理
                    return;
                }
//                if (appMessage.getType() == AppMessageType.GameBuyChips && !AppMsgDBHelper.isAppMessageExist(DemoCache.getContext(), appMessage) && appMessage.getStatus() != AppMessageStatus.init) {
//                    //买入请求，且不存在并且不是初始化，不进行相关逻辑
//                    return;
//                }
                ////判断更新的信息是否已经存在   消息可能反着发，数据库中的消息和刚来的消息是同一个但是数据库中的更新时间戳更大（离线的时候会出现这种情况，必现）  ---蛋疼的云信
                if (AppMsgDBHelper.isNewerAppMessageExist(DemoCache.getContext(), appMessage)) {
                    return;
                }
                AppMsgDBHelper.addAppMessage(DemoCache.getContext(), appMessage);
                AppMessageHelper.sendAppMessageIncoming(DemoCache.getContext(), appMessage);
                if (appMessage.online == 1) {//当且仅当游戏内结束的消息这个字段是1，是为了不通知用户，因为在游戏内已经通知过了。其余情况都是0
                    return;
                }
                String showMessage = appMessage.content;
                //通知栏展示(内容有，并且未处理才进行展示)
                if (!TextUtils.isEmpty(showMessage) && appMessage.status == AppMessageStatus.init) {
//                    if (mHTNotificationManager == null) {//jb这个单例会导致通知栏的设置复用，不用单例了
//                        mHTNotificationManager = HTNotificationManager.getInstance(DemoCache.getContext());
//                    }
                    int pendingAction = PendingIntentConstants.ACTION_APP_MESSAGE;
                    if ((appMessage.type == AppMessageType.MatchBuyChips || appMessage.type == AppMessageType.GameBuyChips)) {
                        pendingAction = PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL;//以前"系统消息"和"控制中心"是同一个页面，后来分开了
                        String gameName = "nil";
                        if (appMessage.attachObject instanceof BuyChipsNotify) {
                            gameName = ((BuyChipsNotify) appMessage.attachObject).gameName;
                        } else if (appMessage.attachObject instanceof MatchBuyChipsNotify) {
                            gameName = ((MatchBuyChipsNotify) appMessage.attachObject).gameName;
                        }
                        ControlToast.Companion.getInstance().show("“" + gameName + "”" + "有申请消息");
                    }
                    HTNotificationManager.getInstance(DemoCache.getContext()).showIntentActivityNotify(DemoCache.getContext(), showMessage, pendingAction);
                }
                //
                if (appMessage.type == AppMessageType.GameOver && appMessage.attachObject instanceof GameOverNotify) {
                    GameOverNotify gameOverNotify = (GameOverNotify) appMessage.attachObject;
                    LogUtil.i("GameOverHelper", "showMessage :" + gameOverNotify.teamId + ";" + gameOverNotify.creatorId);
                    GameOverHelper.dealGameOverMessage(gameOverNotify);
                }
            } else if (type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
                SystemMessage systemMessage = HordeMessageHelper.parseSystemMessage(data, type);
                HordeMessageHelper.addHordeMsgToDB(DemoCache.getContext(), systemMessage);
                ReminderManager.getInstance().updateHordeUnreadNum(systemMessage);
            }
        } catch (Exception ex) {

        }
    }

    public interface CustomNotificationObserver {
        void onNotificationIncoming(CustomNotification message);
    }

    private void notifyCustomNotification(CustomNotification message) {
        for (CustomNotificationObserver o : messageObservers) {
            o.onNotificationIncoming(message);
        }
    }

    public void registerCustomNotificationObserver(CustomNotificationObserver o) {
        if (messageObservers.contains(o)) {
            return;
        }
        messageObservers.add(o);
    }

    public void unregisterCustomNotificationObserver(CustomNotificationObserver o) {
        messageObservers.remove(o);
    }
}
