package com.htgames.nutspoker.chat.msg.helper;

import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.chesscircle.helper.CustomMessageUpdateHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

/**
 * 消息状态Helper
 */
public class MessageStatusHelper {
    private final static String TAG = "GameOverHelper";

    public static void updateGameMessageStatus(String teamId, final String gid, final int gameStatus) {
        updateGameMessageStatus(teamId , SessionTypeEnum.Team , gid , gameStatus);
    }

    /**
     * 改变牌局状态（进行中-结束），如果在消息界面，需要更新UI，刷新列表
     *
     * @param sessionId
     * @param sessionTypeEnum
     * @param gid             牌局ID
     * @param gameStatus      牌局状态
     */
    public static void updateGameMessageStatus(String sessionId, SessionTypeEnum sessionTypeEnum, final String gid, final int gameStatus) {
        IMMessage emptyMsg = MessageBuilder.createEmptyMessage(sessionId, sessionTypeEnum, 0);
        NIMClient.getService(MsgService.class).queryMessageListByType(MsgTypeEnum.custom, emptyMsg, 1000).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> imMessages) {
                LogUtil.i(TAG, "牌局消息 : onSuccess :" + imMessages.size());
                for (IMMessage imMessage : imMessages) {
                    if (imMessage.getAttachment() instanceof GameAttachment) {
                        //自定义消息，牌局
                        GameAttachment gameAttachment = (GameAttachment) imMessage.getAttachment();
                        GameEntity gameEntity = gameAttachment.getValue();
                        if (gameEntity.gid.equals(gid)) {
                            gameEntity.status = (gameStatus);
                            gameAttachment = new GameAttachment(gameAttachment.toJsonData(gameEntity));
                            imMessage.setAttachment(gameAttachment);
                            LogUtil.i(TAG, "update:" + gameAttachment.getValue().code + ";变更后的状态为;" + gameAttachment.getValue().status);
                            NIMClient.getService(MsgService.class).updateIMMessageStatus(imMessage);
                            //自定义消息更新，通知UI变化
                            CustomMessageUpdateHelper.updateIMMessageStatus(DemoCache.getContext(), imMessage);
                            break;
                        }
                    }
                    LogUtil.i(TAG, "牌局消息 : " + imMessage.getUuid());
                }
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    /**
     * 修改消息状态
     * @param imMessage
     * @param gameStatus
     * @param needNotifyUI 是否需要通知UI变化
     */
    public static void updateGameMessageStatus(IMMessage imMessage , int gameStatus , boolean needNotifyUI) {
        if (imMessage.getAttachment() instanceof GameAttachment) {
            //自定义消息，牌局
            GameAttachment gameAttachment = (GameAttachment) imMessage.getAttachment();
            GameEntity gameEntity = gameAttachment.getValue();
            gameEntity.status = (gameStatus);
            gameAttachment = new GameAttachment(gameAttachment.toJsonData(gameEntity));
            imMessage.setAttachment(gameAttachment);
            LogUtil.i(TAG, "update:" + gameAttachment.getValue().code + ";变更后的状态为;" + gameAttachment.getValue().status);
            NIMClient.getService(MsgService.class).updateIMMessageStatus(imMessage);
            if(needNotifyUI) {
                //自定义消息更新，通知UI变化
                CustomMessageUpdateHelper.updateIMMessageStatus(DemoCache.getContext(), imMessage);
            }
        }
    }
}
