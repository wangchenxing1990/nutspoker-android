package com.netease.nim.uikit.chesscircle.helper;

import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * 最近联系人帮助类
 */
public class RecentContactHelp {

    /**
     * 获取对应联系人是否消息免打扰
     * @param recent
     * @return
     */
    public static boolean isRecentMute(RecentContact recent) {
        if (recent.getSessionType() == SessionTypeEnum.Team) {
            Team team = TeamDataCache.getInstance().getTeamById(recent.getContactId());
            if (team != null && team.mute()) {
                //群免消息打扰
                return true;
            }
        } else if (recent.getSessionType() == SessionTypeEnum.P2P) {
            if (!NIMClient.getService(FriendService.class).isNeedMessageNotify(recent.getContactId())) {
                //true：需要消息提醒 ， false：静音
                //群免消息打扰
                return true;
            }
        }
        return false;
    }

    /**
     * 获取俱乐部team是否消息免打扰
     * @param recent
     * @return
     */
    public static boolean isTeamMsgMute(String teamId) {
        Team team = TeamDataCache.getInstance().getTeamById(teamId);
        if (team != null && team.mute()) {
            //群免消息打扰
            return true;
        }
        return false;
    }

    /**
     * 获取对应消息的联系人是否消息免打扰
     * @param imMessage
     * @return
     */
    public static boolean isIMMessageMute(IMMessage imMessage) {
        if (imMessage.getSessionType() == SessionTypeEnum.Team) {
            Team team = TeamDataCache.getInstance().getTeamById(imMessage.getSessionId());
            if (team != null && team.mute()) {
                //群免消息打扰
                return true;
            }
        } else if (imMessage.getSessionType() == SessionTypeEnum.P2P) {
            if (!NIMClient.getService(FriendService.class).isNeedMessageNotify(imMessage.getSessionId())) {
                //true：需要消息提醒 ， false：静音
                //群免消息打扰
                return true;
            }
        }
        return false;
    }

    /**
     * 删除最近联系人
     * @param sessionId 会话ID
     * @param sessionTypeEnum 会话类型
     * @param isDeleteHistory 是否删除历史记录
     */
    public static void deleteRecentContact(String sessionId , SessionTypeEnum sessionTypeEnum, boolean isDeleteHistory) {
        if (isDeleteHistory) {
            NIMClient.getService(MsgService.class).clearChattingHistory(sessionId, sessionTypeEnum);//清除历史记录
        }
        NIMClient.getService(MsgService.class).deleteRecentContact2(sessionId, sessionTypeEnum);
    }
}
