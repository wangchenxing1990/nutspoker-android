package com.netease.nim.uikit.session.helper;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.helper.MessageFilter;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.MuteMemberAttachment;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;

import java.util.List;
import java.util.Map;

/**
 * 系统消息描述文本构造器。主要是将各个系统消息转换为显示的文本内容。<br>
 * Created by huangjun on 2015/3/11.
 */
public class TeamNotificationHelper {
    private static ThreadLocal<String> teamId = new ThreadLocal<String>();

    public static String getMsgShowText(final IMMessage message) {
        String content = "";
        String messageTip = message.getMsgType().getSendMessageTip();
        if (messageTip.length() > 0) {
            content += "[" + messageTip + "]";
        } else {
            if (message.getSessionType() == SessionTypeEnum.Team && message.getAttachment() != null) {
                content += getTeamNotificationText(message, message.getSessionId());
            } else {
                content += message.getContent();
            }
        }
        return content;
    }

    public static String getTeamNotificationText(IMMessage message, String tid) {
        return getTeamNotificationText(message.getSessionId(), message.getFromAccount(), (NotificationAttachment) message.getAttachment());
    }

    public static String getTeamNotificationText(String tid, String fromAccount, NotificationAttachment attachment) {
        teamId.set(tid);
        String text = buildNotification(tid, fromAccount, attachment);
        teamId.set(null);
        return text;
    }

    public static String groupOrClub = getString(R.string.group);

    public static String buildNotification(String tid, String fromAccount, NotificationAttachment attachment) {
        groupOrClub = getString(R.string.group);
        if(TeamDataCache.getInstance().getTeamById(tid) != null){
            if(TeamDataCache.getInstance().getTeamById(tid).getType() == TeamTypeEnum.Advanced) {
                if(TeamDataCache.getInstance().getTeamById(tid).getVerifyType() ==  VerifyTypeEnum.Free){
                    groupOrClub = getString(R.string.game);
                } else{
                    //俱乐部
                    groupOrClub = getString(R.string.club);
                }
            }
        }
        String text;
        switch (attachment.getType()) {
            case InviteMember:
                text = buildInviteMemberNotification(((MemberChangeAttachment) attachment), fromAccount);
                break;
            case KickMember:
                text = buildKickMemberNotification(((MemberChangeAttachment) attachment));
                break;
            case LeaveTeam:
                text = buildLeaveTeamNotification(fromAccount);
                break;
            case DismissTeam:
                text = buildDismissTeamNotification(fromAccount);
                break;
            case UpdateTeam:
                text = buildUpdateTeamNotification(tid, fromAccount, (UpdateTeamAttachment) attachment);
                break;
            case PassTeamApply:
                if (TeamDataCache.getInstance().getTeamById(tid).getVerifyType() == VerifyTypeEnum.Free) {
                    text = buildManagerJoinGameNotification((MemberChangeAttachment) attachment);
                } else {
                    text = buildManagerPassTeamApplyNotification((MemberChangeAttachment) attachment);
                }
                break;
            case TransferOwner:
                text = buildTransferOwnerNotification(fromAccount, (MemberChangeAttachment) attachment);
                break;
            case AddTeamManager:
                text = buildAddTeamManagerNotification((MemberChangeAttachment) attachment);
                break;
            case RemoveTeamManager:
                text = buildRemoveTeamManagerNotification((MemberChangeAttachment) attachment);
                break;
            case AcceptInvite:
                text = buildAcceptInviteNotification(fromAccount, (MemberChangeAttachment) attachment);
                break;
            case MuteTeamMember:
                text = buildMuteTeamNotification((MuteMemberAttachment) attachment);
                break;
            default:
                text = getTeamMemberDisplayName(fromAccount) + ": unknown message";
                break;
        }

        return text;
    }

    private static String getTeamMemberDisplayName(String account) {
        return TeamDataCache.getInstance().getTeamMemberDisplayNameYou(teamId.get(), account);
    }

    private static String buildMemberListString(List<String> members, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        for (String account : members) {
            if (!TextUtils.isEmpty(fromAccount) && fromAccount.equals(account)) {
                continue;
            }
            sb.append(getTeamMemberDisplayName(account));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    private static String buildInviteMemberNotification(MemberChangeAttachment a, String fromAccount) {
        StringBuilder sb = new StringBuilder();
        String selfName = getTeamMemberDisplayName(fromAccount);
//        sb.append(selfName);
//        sb.append("邀请 ");
//        sb.append(buildMemberListString(a.getTargets(), fromAccount));
//        sb.append(" 加入" + groupOrClub);
        sb.append(getString(R.string.club_invite_member_join , selfName ,buildMemberListString(a.getTargets(), fromAccount) ,groupOrClub));

        return sb.toString();
    }

    private static String buildKickMemberNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
//        sb.append(buildMemberListString(a.getTargets(), null));
//        sb.append(" 已被移出" + groupOrClub);
        sb.append(getString(R.string.club_kick_member_join , buildMemberListString(a.getTargets(), null) , groupOrClub));
        return sb.toString();
    }

    private static String buildLeaveTeamNotification(String fromAccount) {
//        return getTeamMemberDisplayName(fromAccount) + " 离开了" + groupOrClub;
        return getString(R.string.club_leave_team , getTeamMemberDisplayName(fromAccount) ,groupOrClub);
    }

    private static String buildDismissTeamNotification(String fromAccount) {
//        return getTeamMemberDisplayName(fromAccount) + " 解散了群";|
//        return getTeamMemberDisplayName(fromAccount) + " 解散了" + groupOrClub;
        return getString(R.string.club_dismis_team , getTeamMemberDisplayName(fromAccount) ,groupOrClub);
    }

    private static String buildUpdateTeamNotification(String tid, String account, UpdateTeamAttachment a) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TeamFieldEnum, Object> field : a.getUpdatedFields().entrySet()) {
            if (field.getKey() == TeamFieldEnum.Name) {
//                sb.append(groupOrClub + "名称被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_name , groupOrClub , field.getValue()));
            } else if (field.getKey() == TeamFieldEnum.Introduce) {
//                sb.append(groupOrClub + "介绍被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_introduce , groupOrClub , field.getValue()));
            } else if (field.getKey() == TeamFieldEnum.Announcement) {
//                sb.append(TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account) + " 修改了" + groupOrClub + "公告");
                sb.append(getString(R.string.club_update_team_announcement , TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account) , groupOrClub));
            } else if (field.getKey() == TeamFieldEnum.VerifyType) {
                VerifyTypeEnum type = (VerifyTypeEnum) field.getValue();
//                String authen = groupOrClub + "身份验证权限更新为";
                String authen = getString(R.string.club_update_team_verifyType , groupOrClub);
                if (type == VerifyTypeEnum.Free) {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_allow_anyone_join));
                } else if (type == VerifyTypeEnum.Apply) {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_need_authentication));
                } else {
                    sb.append(authen + NimUIKit.getContext().getString(R.string.team_not_allow_anyone_join));
                }
            } else if (field.getKey() == TeamFieldEnum.Extension) {
//                sb.append(groupOrClub + "扩展字段被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_extension , groupOrClub , field.getValue()));
            } else if (field.getKey() == TeamFieldEnum.Ext_Server) {
                String ext = field.getValue().toString();
                if (MessageFilter.isClubChangeNotShow(ext)) {
                    //不显示的内容
                } else if (MessageFilter.isClubAreaChange(ext)) {
                    //区域发生变更
//                    sb.append(TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account) + " 更改了" + groupOrClub + "所在地");
                    sb.append(getString(R.string.club_update_team_area, TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account), groupOrClub));
                } else if (MessageFilter.isClubAvatarChange(ext)) {
                    //头像发生变化
//                    sb.append(TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account) + " 更改了" + groupOrClub + "头像");
                    sb.append(getString(R.string.club_update_team_head, TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account), groupOrClub));
                } else if (MessageFilter.isClubMemberCountLimitChange(ext)) {
                    sb.append(getString(R.string.club_update_team_member_limit, TeamDataCache.getInstance().getTeamMemberDisplayNameYou(tid, account), groupOrClub));
                } else {
//                    sb.append(groupOrClub + "扩展字段(服务器)被更新为 " + field.getValue());
//                    sb.append(groupOrClub + "信息发生变更");
                    sb.append(getString(R.string.club_update_team_ext_changed, groupOrClub));
                }
            } else if (field.getKey() == TeamFieldEnum.ICON) {
                sb.append(getString(R.string.club_update_team_head_netease));
            } else if (field.getKey() == TeamFieldEnum.InviteMode) {
//                sb.append("群邀请他人权限被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_invite_mode) + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamUpdateMode) {
//                sb.append("群资料修改权限被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_update_mode) + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.BeInviteMode) {
//                sb.append("群被邀请人身份验证权限被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_beinvite_mode) + field.getValue());
            } else if (field.getKey() == TeamFieldEnum.TeamExtensionUpdateMode) {
//                sb.append("群扩展字段修改权限被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_extension_update_mode) + field.getValue());
            } else {
//                sb.append(groupOrClub + field.getKey() + "被更新为 " + field.getValue());
                sb.append(getString(R.string.club_update_team_changed , groupOrClub , field.getKey() ,field.getValue()));
            }
            sb.append("\r\n");
        }
        if (sb.length() < 2) {
//            return "未知通知";
            return getString(R.string.club_notification_unknown);
        }
        return sb.delete(sb.length() - 2, sb.length()).toString();
    }

    private static String buildManagerPassTeamApplyNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_manager_pass_team_apply , buildMemberListString(a.getTargets(), null)));
        return sb.toString();
    }

    private static String buildManagerJoinGameNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_manager_join_game , buildMemberListString(a.getTargets(), null)));
        return sb.toString();
    }

    private static String buildTransferOwnerNotification(String from, MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_transfer_owner , getTeamMemberDisplayName(from) ,buildMemberListString(a.getTargets(), null)));

        return sb.toString();
    }

    private static String buildAddTeamManagerNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_add_team_manager , buildMemberListString(a.getTargets(), null)));

        return sb.toString();
    }

    private static String buildRemoveTeamManagerNotification(MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_remove_team_manager , buildMemberListString(a.getTargets(), null)));

        return sb.toString();
    }

    private static String buildAcceptInviteNotification(String from, MemberChangeAttachment a) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.club_accept_invite , getTeamMemberDisplayName(from) , buildMemberListString(a.getTargets(), null)));

        return sb.toString();
    }

    private static String buildMuteTeamNotification(MuteMemberAttachment a) {
        StringBuilder sb = new StringBuilder();

        sb.append(buildMemberListString(a.getTargets(), null));
        sb.append(getString(R.string.club_mute_team_member));
        sb.append(a.isMute() ? getString(R.string.club_mute_add) : getString(R.string.club_mute_remove));
        return sb.toString();
    }

    private static String getString(@StringRes int resId, Object... formatArgs){
        return NimUIKit.getContext().getString(resId , formatArgs);
    }
}
