package com.htgames.nutspoker.chat.chatroom.helper;

import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;

import java.util.List;

/**
 * Created by huangjun on 2016/1/13.
 */
public class ChatRoomNotificationHelper {
    public static String getNotificationText(ChatRoomNotificationAttachment attachment) {
        if (attachment == null) {
            return "";
        }

        String targets = getTargetNicks(attachment);
        String text;
        if(TextUtils.isEmpty(targets)) {
            targets = "";
        }
        switch (attachment.getType()) {
            case ChatRoomMemberIn:
//                text = buildText("欢迎", targets, "进入直播间");
                text = buildText(getString(R.string.chatroom_member_in, targets));
                break;
            case ChatRoomMemberExit:
//                text = buildText(targets, "离开了直播间");
                text = getString(R.string.chatroom_member_exit, targets);
                break;
            case ChatRoomMemberBlackAdd:
//                text = buildText(targets, "被管理员拉入黑名单");
                text = getString(R.string.chatroom_member_black_add, targets);
                break;
            case ChatRoomMemberBlackRemove:
//                text = buildText(targets, "被管理员解除拉黑");
                text = getString(R.string.chatroom_member_black_remove, targets);
                break;
            case ChatRoomMemberMuteAdd:
//                text = buildText(targets, "被管理员禁言");
                text = getString(R.string.chatroom_member_mute_add, targets);
                break;
            case ChatRoomMemberMuteRemove:
//                text = buildText(targets, "被管理员解除禁言");
                text = getString(R.string.chatroom_member_mute_remove, targets);
                break;
            case ChatRoomManagerAdd:
//                text = buildText(targets, "被任命管理员身份");
                text = getString(R.string.chatroom_member_manager_add, targets);
                break;
            case ChatRoomManagerRemove:
//                text = buildText(targets, "被解除管理员身份");
                text = getString(R.string.chatroom_member_manager_remove, targets);
                break;
            case ChatRoomCommonAdd:
//                text = buildText(targets, "被设为普通成员");
                text = getString(R.string.chatroom_member_common_add, targets);
                break;
            case ChatRoomCommonRemove:
//                text = buildText(targets, "被取消普通成员");
                text = getString(R.string.chatroom_member_common_remove, targets);
                break;
            case ChatRoomClose:
//                text = buildText("直播间被关闭");
                text = getString(R.string.chatroom_close);
                break;
            case ChatRoomInfoUpdated:
//                text = buildText("直播间信息已更新");
                text = getString(R.string.chatroom_info_update);
                break;
            case ChatRoomMemberKicked:
//                text = buildText(targets, "被踢出直播间");
                text = getString(R.string.chatroom_member_kicked , targets);
                break;
            default:
                text = attachment.toString();
                break;
        }

        return text;
    }

    private static String getTargetNicks(final ChatRoomNotificationAttachment attachment) {
        StringBuilder sb = new StringBuilder();
        List<String> accounts = attachment.getTargets();
        List<String> targets = attachment.getTargetNicks();
        if (attachment.getTargetNicks() != null) {
            for (int i = 0; i < targets.size(); i++) {
                sb.append(DemoCache.getAccount().equals(accounts.get(i)) ? "你" : targets.get(i));
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    private static String buildText(String pre, String targets, String operate) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(pre)) {
            sb.append(pre);
        }

        if (!TextUtils.isEmpty(targets)) {
            sb.append(targets);
        }

        if (!TextUtils.isEmpty(operate)) {
            sb.append(operate);
        }

        return sb.toString();
    }

    private static String buildText(String targets, String operate) {
        return buildText(null, targets, operate);
    }

    private static String buildText(String operate) {
        return buildText(null, operate);
    }

    private static String getString(@StringRes int resId, Object... formatArgs) {
        return NimUIKit.getContext().getString(resId, formatArgs);
    }
}
