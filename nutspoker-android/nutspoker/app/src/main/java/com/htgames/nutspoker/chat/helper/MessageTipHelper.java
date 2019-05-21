package com.htgames.nutspoker.chat.helper;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.helper.MessageConfigHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by 20150726 on 2015/11/5.
 */
public class MessageTipHelper {
    /**
     * 保存添加好友成功提示消息
     * @param sessionId
     * @param fromAccount
     * @param sessionType
     */
    public static void saveAddFriendTipMessage(String sessionId ,String fromAccount , SessionTypeEnum sessionType){
        String tipContent =  String.format(DemoCache.getContext().getString(R.string.addfriend_success_tip), NimUserInfoCache.getInstance().getUserDisplayName(fromAccount));
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType, content);
        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType);
        message.setContent(tipContent);
        message.setStatus(MsgStatusEnum.success);//设置为已读
        message.setConfig(MessageConfigHelper.getTipMessageConfig(message.getConfig()));
        NIMClient.getService(MsgService.class).saveMessageToLocal(message, true);
    }

    /**
     * 保存俱乐部成功创建提示消息 - 使得该群能立即出现在最近联系人列表（会话列表）中，满足部分开发者需求
     * @param sessionId
     * @param sessionType
     */
    public static void saveCreateClubTipMessage(String sessionId , String teamName, SessionTypeEnum sessionType){
        String tipContent =  String.format(DemoCache.getContext().getString(R.string.createclub_success_tip), teamName);
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType, content);
        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType);
//        message.setRemoteExtension(content);
        message.setContent(tipContent);
        message.setStatus(MsgStatusEnum.success);//设置为已读
        message.setConfig(MessageConfigHelper.getTipMessageConfig(message.getConfig()));
        NIMClient.getService(MsgService.class).saveMessageToLocal(message, true);
    }

    /**
     * 保存游戏提示消息
     * @param sessionId
     * @param fromAccount
     * @param sessionType
     * @param tipContent
     */
    public static void saveGameTipMessage(String sessionId ,String fromAccount , SessionTypeEnum sessionType , String tipContent){
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType, content);
        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType);
        message.setContent(tipContent);
        message.setFromAccount(fromAccount);
        message.setStatus(MsgStatusEnum.success);//设置为已读
//        CustomMessageConfig config = message.getConfig();
//        if(config == null){
//            config = new CustomMessageConfig();
//        }
//        config.enableUnreadCount = false;
        message.setConfig(MessageConfigHelper.getTipMessageConfig(message.getConfig()));
        NIMClient.getService(MsgService.class).saveMessageToLocal(message, true);
    }

    /**
     * 发送群的提示消息：通过消息
     * @param team
     * @param tipContent
     * @param isDelete 发送完毕是否删除
     */
    public static void sendGameTipMessage(Team team , String tipContent , final boolean isDelete){
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        final IMMessage message = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team, content);
        final IMMessage message = MessageBuilder.createTipMessage(team.getId(), SessionTypeEnum.Team);
        message.setContent(tipContent);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }

            @Override
            public void onFailed(int i) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }
        });
    }

    public static void sendGameTipMessage(String sessionId , SessionTypeEnum sessionType , String tipContent , final boolean isDelete) {
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionTypeEnum, content);
        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType);
        message.setContent(tipContent);
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }

            @Override
            public void onFailed(int i) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }

            @Override
            public void onException(Throwable throwable) {
                if (isDelete) {
                    // 删除单条消息
                    NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                }
            }
        });
    }

    /**
     * 发送群的提示消息:成功创建牌局
     * @param teamId
     */
    public static void sendGameTipMessage(String teamId) {
        String tipContent = DemoCache.getContext().getString(R.string.game_create_tip);
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        IMMessage message = MessageBuilder.createTipMessage(teamId, SessionTypeEnum.Team, content);
        final IMMessage message = MessageBuilder.createTipMessage(teamId, SessionTypeEnum.Team);
        message.setContent(tipContent);
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }

    /**
     * 创建群成功，发送系统消息提醒
     * @param teamId
     */
    public void sendSuccessIMMessage(String teamId) {
        IMMessage msg = MessageBuilder.createEmptyMessage(teamId, SessionTypeEnum.Team, System.currentTimeMillis());
        msg.setFromAccount(DemoCache.getAccount());
        msg.setContent(DemoCache.getContext().getString(R.string.game_create_tip));
        msg.setDirect(MsgDirectionEnum.Out);
        msg.setStatus(MsgStatusEnum.success);
        NIMClient.getService(MsgService.class).saveMessageToLocal(msg, true);
    }

    /**
     * 创建一条提示消息（不计入总数）
     * @param sessionId
     * @param sessionType
     * @param tipContent
     * @return
     */
    public static IMMessage createTipMessage(String sessionId , SessionTypeEnum sessionType , String tipContent) {
//        Map<String, Object> content = new HashMap<>(1);
//        content.put("content", tipContent);
//        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
//        IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionTypeEnum, content);
        final IMMessage message = MessageBuilder.createTipMessage(sessionId, sessionType);
        message.setContent(tipContent);
        //设置CustomMessageConfig 不计入未读数量
        message.setConfig(MessageConfigHelper.getTipMessageConfig(message.getConfig()));
        return message;
    }
}
