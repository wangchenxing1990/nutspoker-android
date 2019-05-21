package com.htgames.nutspoker.chat.notification.helper;

import android.text.TextUtils;
import com.netease.nim.uikit.bean.GameEntity;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定义消息帮助类
 */
public class CustomNotificationHelper {
    public static String TAG = "CustomNotificationHelper";

    /**
     * 判断通知类型是否是游戏
     * @param content
     * @return
     */
//    public static boolean isGameNotification(String content){
//        try {
//            JSONObject jsonObject = new JSONObject(content);
//            int type = jsonObject.optInt(CustomNotificationConstants.KEY_NOTIFICATION_TYPE);
//            if(type == CustomNotificationConstants.NOTIFICATION_TYPE_GAME){
//                Log.d(TAG , "收到游戏通知。");
//                return true;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 获取游戏状态
     * @param content
     * @return
     */
    public static GameEntity getGameChangedInfo(String content) {
        GameEntity gameEntity = null;
        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONObject data = jsonObject.getJSONObject("data");
            String gid = data.optString(GameConstants.KEY_GAME_GID);
            String gameName = data.optString(GameConstants.KEY_GAME_NAME);
            String tid = data.optString(GameConstants.KEY_GAME_TEADID);
            int status = data.optInt(GameConstants.KEY_GAME_STATUS);
            LogUtil.i(TAG, "gid:" + gid + ";status :" + status);
            gameEntity = new GameEntity();
            gameEntity.gid = (gid);
            gameEntity.name = (gameName);
            gameEntity.tid = (tid);
            gameEntity.status = (status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameEntity;
    }

    /**
     * 获取自定义通知类型
     *
     * @param content
     * @return
     */
    public static int getNotificationType(String content) {
        int type = CustomNotificationConstants.NOTIFICATION_NORMAL;
        if (!TextUtils.isEmpty(content)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                type = jsonObject.optInt(CustomNotificationConstants.KEY_NOTIFICATION_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    public static String getNotificationData(String content) {
        String data = "";
        if (!TextUtils.isEmpty(content)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                data = jsonObject.optString(CustomNotificationConstants.KEY_NOTIFICATION_DATA);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 发送圈子创建成功的自定义通知
     * @param teamId
     */
//    public static void sendGroupCreateNotification(String teamId) {
//        //创建圈子成功，发送一个圈子通知给圈子里面的成员
//        CustomNotification notification = new CustomNotification();
//        notification.setSessionId(teamId);
//        notification.setSessionType(SessionTypeEnum.Team);
//        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
//        json.put(CustomNotificationConstants.KEY_NOTIFICATION_TYPE, CustomNotificationConstants.NOTIFICATION_TYPE_GROUP_INVITE);
//        notification.setContent(json.toString());
//        // 发送自定义通知
//        NIMClient.getService(MsgService.class).sendCustomNotification(notification);
//    }

    /**
     * 发送Tip通知
     * 只有接收方当前在线才会收到
     *
     * @param teamId
     * @param tipContent
     */
    public static void sendGameTipNotification(String teamId, String tipContent) {
        // 构造自定义通知，指定接收者
        CustomNotification notification = new CustomNotification();
        notification.setSessionId(teamId);
        notification.setSessionType(SessionTypeEnum.Team);
        // 设置该消息需要保证送达
//        notification.setSendToOnlineUserOnly(true);
        // 构建通知的具体内容。为了可扩展性，这里采用 json 格式，以 "id" 作为类型区分。这里以类型 “1” 作为“正在输入”的状态通知。
        com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
        json.put(CustomNotificationConstants.KEY_NOTIFICATION_TYPE, CustomNotificationConstants.NOTIFICATION_TYPE_TIP);
        json.put(CustomNotificationConstants.KEY_NOTIFICATION_DATA, tipContent);
        notification.setContent(json.toJSONString());
        notification.setSendToOnlineUserOnly(false);//设置该消息是否只发送给当前在线的用户。只支持P2P
        // 发送自定义通知
        NIMClient.getService(MsgService.class).sendCustomNotification(notification).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

            @Override
            public void onFailed(int i) {
            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }
}
