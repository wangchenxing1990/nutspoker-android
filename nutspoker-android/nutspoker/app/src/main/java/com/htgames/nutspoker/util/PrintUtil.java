package com.htgames.nutspoker.util;

import com.htgames.nutspoker.ChessApp;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

/**
 * Created by 周智慧 on 17/1/12.
 **************************************************************
 *                                                            *
 *   .=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-.       *
 *    |                     ______                     |      *
 *    |                  .-"      "-.                  |      *
 *    |                 /            \                 |      *
 *    |     _          |              |          _     |      *
 *    |    ( \         |,  .-.  .-.  ,|         / )    |      *
 *    |     > "=._     | )(__/  \__)( |     _.=" <     |      *
 *    |    (_/"=._"=._ |/     /\     \| _.="_.="\_)    |      *
 *    |           "=._"(_     ^^     _)"_.="           |      *
 *    |               "=\__|IIIIII|__/="               |      *
 *    |              _.="| \IIIIII/ |"=._              |      *
 *    |    _     _.="_.="\          /"=._"=._     _    |      *
 *    |   ( \_.="_.="     `--------`     "=._"=._/ )   |      *
 *    |    > _.="                            "=._ <    |      *
 *    |   (_/                                    \_)   |      *
 *    |                                                |      *
 *    '-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-='      *
 *                                                            *
 *                            dead                            *
 **************************************************************
 */
public class PrintUtil {
    /**
     * 打印team信息
     */
    public static void printTeamInfo(String tag, String secondTag, Team team) {
        if (team == null || !CacheConstant.debugBuildType) {
            return;
        }
        LogUtil.i(tag, secondTag  + " id: " + team.getId() +
                "  name:" + team.getName() +
                "  type:" + team.getType() +
                "  getCreator" + team.getCreator() + "\n" +
                "   getExtServer:" + team.getExtServer() +
                "   getExtension:" + team.getExtension() +
                "   getMemberCount:" + team.getMemberCount() +
                "   getMemberLimit:" + team.getMemberLimit() +
                "  是俱乐部吗:" + (GameConstants.isGmaeClub(team.getId()) ? "不是" : "是")
        );
    }

    /**
     * 打印teams信息
     */
    public static void printTeamsInfo(String tag, String secondTag, List<Team> teams) {
        if (teams == null || teams.size() <= 0) {
            return;
        }
        LogUtil.i(tag, "\n" + "--------------" + secondTag + "--------------");
        for (int i = 0; i < teams.size(); i++) {
            printTeamInfo(tag, secondTag + " i:" + i + " ", teams.get(i));
        }
        LogUtil.i(tag, "--------------" + secondTag + "--------------" + "\n");
    }
    /*======================================================   ================================================================*/
    /*======================================================   ================================================================*/
    public static void printIMMessage(String tag, String secondTag, IMMessage message) {
        if (message == null || !CacheConstant.debugBuildType) {
            return;
        }
        LogUtil.i(tag, secondTag + " getContent:" + message.getContent() +
                "  getFromAccount:" + message.getFromAccount() +
                "  getFromNick:"  + message.getFromNick() +
                "  getPushContent:" + message.getPushContent() +
                "  getSessionId:" + message.getSessionId() + "\n" +
                "  getUuid:" + message.getUuid() +
                "  getAttachment:" + message.getAttachment() +
                "  getAttachStatus:" + message.getAttachStatus() +
                "  getConfig:" + message.getConfig() + "\n" +
                "  getDirect:" + message.getDirect() +
                "  getLocalExtension:" + message.getLocalExtension() +
                "  getMsgType:" + message.getMsgType() +
                "  getPushPayload:" + message.getPushPayload() +
                "  getRemoteExtension:" + message.getRemoteExtension() + "\n" +
                "  getSessionType:" + message.getSessionType() +
                "  getStatus:" + message.getStatus() +
                "  getTime:" + message.getTime()
        );
    }
    
    public static void printIMMessages(String tag, String secondTag, List<IMMessage> messages) {
        if (messages == null || messages.size() <= 0) {
            return;
        }
        LogUtil.i(tag, "\n" + "--------------" + secondTag + "--------------");
        for (int i = 0; i < messages.size(); i++) {
            printIMMessage(tag, secondTag + " i:" + i + " ", messages.get(i));
        }
        LogUtil.i(tag, "--------------" + secondTag + "--------------" + "\n");
    }
    /*======================================================   ================================================================*/
    /*======================================================   ================================================================*/
    public static void printCustomNotification(String tag, String secondTag, CustomNotification message) {
        if (message == null || !CacheConstant.debugBuildType) {
            return;
        }
        LogUtil.i(tag, secondTag + message.getContent() +
                "  getFromAccount:" + message.getFromAccount() + " "  +
                "  getSessionId:" + message.getSessionId() + "\n" +
                "  getConfig:" + " " + message.getConfig() +
                "  getPushPayload:" + " " + message.getPushPayload() + "\n" +
                "  getSessionType:" + message.getSessionType() +
                "  getTime:" + message.getTime()
        );
    }
    /*======================================================   ================================================================*/
    /*======================================================   ================================================================*/
    public static void printRecentContact(String tag, String secondTag, RecentContact r) {
        if (r == null || !CacheConstant.debugBuildType) {
            return;
        }
        LogUtil.i(tag, secondTag +
                "  getMsgType:" + r.getMsgType() +
                "  getContent:" + r.getContent() +
                "  getUnreadCount:" + r.getUnreadCount() +
                "   getFromAccount:" + r.getFromAccount() +
                "  getTime：" + r.getTime() + "\n" +
                "  getContactId:" + r.getContactId() +
                "   r.getSessionType():" + r.getSessionType() +
                "  getMsgType:" + r.getMsgType() +
                "  getExtension:" + r.getExtension() +
                //"  getFromNick:" + r.getFromNick() +   这里会null pointer crash（比如创建sng后解散)
                "  云信缓存fromNickname:" + NimUserInfoCache.getInstance().getUserDisplayName(r.getFromAccount()) +
                "  getRecentMessageId:"        + r.getRecentMessageId() +
                "  getAttachment:" + r.getAttachment() + " " +
                "  getTag:" + r.getTag() +
                "  getMsgStatus:" + r.getMsgStatus()
        );
    }
    public static void printRecentContacts(String tag, String secondTag, List<RecentContact> recents) {
        if (recents == null || recents.size() <= 0) {
            return;
        }
        LogUtil.i(tag, "\n" + "--------------" + secondTag + "--------------");
        for (int i = 0; i < recents.size(); i++) {
            printRecentContact(tag, secondTag + " i:" + i, recents.get(i));
        }
        LogUtil.i(tag, "--------------" + secondTag + "--------------" + "\n");
    }
    /*======================================================   ================================================================*/
    /*======================================================   ================================================================*/
    public static void printChatRoomMessage(String tag, String secondTag, ChatRoomMessage r) {
        if (r == null || !CacheConstant.debugBuildType) {
            return;
        }
        LogUtil.i(tag, secondTag +
                "  getContent:" + r.getContent() +
//                "  getAttachment:" + (r.getAttachment() instanceof Object ? r.getAttachment() : "null") +
                "  getFromNick:" + r.getFromNick() +
                "   getFromAccount:" + r.getFromAccount() +
                "  getTime：" + r.getTime() + "\n" +
                "  getMsgType:" + r.getMsgType() +
                "   r.getPushContent():" + r.getPushContent() +
                "  getSessionId:" + r.getSessionId() +
                "  getUuid:" + r.getUuid() +
                //"  getFromNick:" + r.getFromNick() +   这里会null pointer crash（比如创建sng后解散)
                "  云信缓存fromNickname:" + NimUserInfoCache.getInstance().getUserDisplayName(r.getFromAccount()) +
                "  getChatRoomMessageExtension:"        + r.getChatRoomMessageExtension() +
                "  getConfig:" + r.getConfig() + " " +
                "  getDirect:" + r.getDirect() +
                "  getLocalExtension:" + r.getLocalExtension() + "\n" +
                " getPushPayload" + r.getPushPayload() +
                " getRemoteExtension" + r.getRemoteExtension() +
                " getSessionType" + r.getSessionType() +
                " getStatus" + r.getStatus()
        );
    }
    public static void printChatRoomMessages(String tag, String secondTag, List<ChatRoomMessage> messages) {
        if (messages == null || messages.size() <= 0) {
            return;
        }
        LogUtil.i(tag, "\n" + "--------------" + secondTag + "--------------");
        for (int i = 0; i < messages.size(); i++) {
            printChatRoomMessage(tag, secondTag + " i:" + i, messages.get(i));
        }
        LogUtil.i(tag, "--------------" + secondTag + "--------------" + "\n");
    }
    /*======================================================   ================================================================*/
    /*======================================================   ================================================================*/
}
