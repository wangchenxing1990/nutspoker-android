package com.netease.nim.uikit.chesscircle.helper;

import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;

/**
 * 消息提醒配置类
 */
public class MessageConfigHelper {
    /**
     * TIP提示型消息
     * @param config
     * @return
     */
    public static CustomMessageConfig getTipMessageConfig(CustomMessageConfig config){
        if(config == null){
            config = new CustomMessageConfig();
        }
        config.enableUnreadCount = false;//该消息是否要计入未读数，如果为true，那么对方收到消息后，最近联系人列表中未读数加1。
        config.enablePush = false;//该消息是否要消息提醒
        config.enablePushNick = false;//该消息是否需要推送昵称（针对iOS客户端有效），如果为true，那么对方收到消息后，iOS端将不显示推送昵称。
        config.enableHistory = false;//该消息是否要保存到服务器
        config.enableRoaming = false;//该消息是否需要漫游。
        config.enableSelfSync = false;//多端同时登录时，发送一条自定义消息后，是否要同步到其他同时登录的客户端。
        return config;
    }

    /**
     * 游戏俱乐部消息设置（不推送）
     * @param config
     * @return
     */
    public static CustomMessageConfig getGameTeamMessageConfig(CustomMessageConfig config){
        if(config == null){
            config = new CustomMessageConfig();
        }
        config.enablePush = false;
        config.enableHistory = false;
        config.enableRoaming = false;
        return config;
    }

    /**
     * 游戏内语音消息设置
     * @param config
     * @return
     */
    public static CustomMessageConfig getGameAudioMessageConfig(CustomMessageConfig config){
        if(config == null){
            config = new CustomMessageConfig();
        }
        config.enableUnreadCount = false;//该消息是否要计入未读数，如果为true，那么对方收到消息后，最近联系人列表中未读数加1。
        config.enablePush = false;//该消息是否要消息提醒
        config.enablePushNick = false;//该消息是否需要推送昵称（针对iOS客户端有效），如果为true，那么对方收到消息后，iOS端将不显示推送昵称。
        config.enableHistory = false;//该消息是否要保存到服务器
        config.enableRoaming = false;//该消息是否需要漫游。
        config.enableSelfSync = false;//多端同时登录时，发送一条自定义消息后，是否要同步到其他同时登录的客户端。
        return config;
    }
}
