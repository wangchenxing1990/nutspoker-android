package com.htgames.nutspoker.chat.helper;

import android.text.TextUtils;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.chesscircle.helper.MessageFilter;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.chesscircle.view.AudioConstant;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
//import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.model.Team;

public class MessageHelper {
    private final static String TAG ="MessageHelper";

    public static String getName(String uid, SessionTypeEnum sessionType) {
        if (sessionType == SessionTypeEnum.P2P) {
            return NimUserInfoCache.getInstance().getUserDisplayName(uid);
        } else if (sessionType == SessionTypeEnum.Team) {
            return TeamDataCache.getInstance().getTeamName(uid);
        }
        return uid;
    }

    public static String getVerifyNotificationText(SystemMessage message) {
        StringBuilder sb = new StringBuilder();
        String fromAccountNickname = NimUserInfoCache.getInstance().getUserDisplayNameYou(message.getFromAccount());
//        String fromAccount = message.getFromAccount();
        String teamName = DemoCache.getContext().getString(R.string.club);
        if (message.getType() != SystemMessageType.AddFriend) {
            //群消息
            Team team = TeamDataCache.getInstance().getTeamById(message.getTargetId());
            if (team == null && message.getAttachObject() instanceof TeamEntity) {
                TeamEntity teamEntity = (TeamEntity) message.getAttachObject();
                teamName = teamEntity == null ? message.getTargetId() : teamEntity.name;
            } else {
                teamName = team == null ? message.getTargetId() : team.getName();
            }
        }
        if (message.getType() == SystemMessageType.TeamInvite) {
            sb.append(DemoCache.getContext().getString(R.string.message_team_invite)).append(" ").append(teamName);
        } else if (message.getType() == SystemMessageType.DeclineTeamInvite) {
            sb.append(fromAccountNickname).append(DemoCache.getContext().getString(R.string.message_team_invite_decline, teamName));
        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
            sb.append(DemoCache.getContext().getString(R.string.message_team_apply_join)).append(" ").append(teamName);
        } else if (message.getType() == SystemMessageType.RejectTeamApply) {
            sb.append(fromAccountNickname).append(DemoCache.getContext().getString(R.string.message_team_apply_reject, teamName));
        } else if (message.getType() == SystemMessageType.AddFriend) {
            AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
            if (attachData != null) {
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                    sb.append(DemoCache.getContext().getString(R.string.message_add_friend_direct));
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                    sb.append(DemoCache.getContext().getString(R.string.message_add_friend_agree));
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    sb.append(DemoCache.getContext().getString(R.string.message_add_friend_reject));
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    sb.append(DemoCache.getContext().getString(R.string.message_add_friend_verify_request, TextUtils.isEmpty(message.getContent()) ? "" : "：" + message.getContent()));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 是否验证消息需要处理（需要有同意拒绝的操作栏）
     */
    public static boolean isVerifyMessageNeedDeal(SystemMessage message) {
        if (message.getType() == SystemMessageType.AddFriend) {
            if (message.getAttachObject() != null) {
                AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND ||
                        attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                    return false; // 对方直接加你为好友，对方通过你的好友请求，对方拒绝你的好友请求
                } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                    return true; // 好友验证请求
                }
            }
            return false;
        } else if (message.getType() == SystemMessageType.TeamInvite || message.getType() == SystemMessageType.ApplyJoinTeam) {
            return true;
        } else {
            return false;
        }
    }

    public static String getVerifyNotificationDealResult(SystemMessage message) {
        if (message.getStatus() == SystemMessageStatus.passed) {
            return DemoCache.getContext().getString(R.string.message_status_passed);
        } else if (message.getStatus() == SystemMessageStatus.declined) {
            return DemoCache.getContext().getString(R.string.message_status_declined);
        } else if (message.getStatus() == SystemMessageStatus.ignored) {
            return DemoCache.getContext().getString(R.string.message_status_ignored);
        } else if (message.getStatus() == SystemMessageStatus.expired) {
            return DemoCache.getContext().getString(R.string.message_status_expired);
        } else {
            return DemoCache.getContext().getString(R.string.message_status_untreated);
        }
    }

    /**
     * 判断消息是否记入未读
     * @return
     */
    public static boolean isRecordUnRead(IMMessage imMessage) {
        if (imMessage.getStatus() == MsgStatusEnum.read) {
            return false;
        }
        if (imMessage.getSessionType() == SessionTypeEnum.Team) {
            if (GameConstants.isGmaeClub(imMessage.getSessionId()) || MessageFilter.isClubChangeNotShow(imMessage)) {
                LogUtil.i(TAG, "牌局未读消息 , 俱乐部变更消息");
                //是牌局未读消息，不计入总数
                return false;
            }
        }
        if (RecentContactHelp.isIMMessageMute(imMessage)) {
            LogUtil.i(TAG, "免打扰联系人");
            //免打扰的联系人，不计入总数
            return false;
        }
        if (imMessage.getMsgType() == MsgTypeEnum.notification || imMessage.getMsgType() == MsgTypeEnum.tip) {
            LogUtil.i(TAG, "系统通知类消息");
            //系统通知类消息，提示类消息，不计入总数
            return false;
        }
        if (imMessage.getStatus() == MsgStatusEnum.read) {
            //已读，不计入总数
            LogUtil.i(TAG, "已读消息");
            return false;
        }
        if (imMessage.getDirect() == MsgDirectionEnum.In) {
            //是否是牌局内语音信息，不计入总数
//            if(AudioConstant.isGameAudioMessage(imMessage) && imMessage.getAttachStatus() == AttachStatusEnum.transferred){
            if (AudioConstant.isGameAudioMessage(imMessage)) {
                LogUtil.i(TAG, "牌局内语音信息");
                return false;
            }
        }
        return true;
    }
}
