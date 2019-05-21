package com.netease.nim.uikit.chesscircle.helper;

import android.text.TextUtils;

import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;

import java.util.Map;

/**
 * 消息过滤器
 * 作用：过滤掉 1.拓展字段  2.服务器拓展字段
 */
public class MessageFilter {
    public final static String TAG = "MessageFilter";
    public final static String KEY_AUDIO_UID = "uid";
    public final static String KEY_AUDIO_CODE = "code";

    /**
     * 判断是否是群更新拓展字段
     * @param imMessage
     * @return
     */
    public static boolean isTeamUpdateExtension(IMMessage imMessage) {
        if (imMessage.getMsgType() == MsgTypeEnum.notification) {
            LogUtil.d(TAG, "MsgTypeEnum.notification");
            if(imMessage.getAttachment() instanceof UpdateTeamAttachment){
                UpdateTeamAttachment updateTeamAttachment = (UpdateTeamAttachment) imMessage.getAttachment();
                for (Map.Entry<TeamFieldEnum, Object> field : updateTeamAttachment.getUpdatedFields().entrySet()) {
                    LogUtil.d(TAG, "拓展字段内容变更系统消息");
                    if (field.getKey() == TeamFieldEnum.Extension || field.getKey() == TeamFieldEnum.Ext_Server) {
                        //拓展字段内容变更系统消息
                        LogUtil.d(TAG, "Extension ，Ext_Server 改变 " + field.getKey() + ":" + field.getValue().toString());
                        // 删除单条消息
                        // NIMClient.getService(MsgService.class).deleteChattingHistory(message);
                        if(isClubAreaChange(field.getValue().toString()) || isClubAvatarChange(field.getValue().toString())){
                            //俱乐部
                            return false;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断俱乐部变更消息是否显示
     * @param imMessage
     * @return
     */
    public static boolean isClubChangeNotShow(IMMessage imMessage) {
        if (imMessage.getAttachment() instanceof UpdateTeamAttachment) {
            UpdateTeamAttachment attachment = (UpdateTeamAttachment) imMessage.getAttachment();
            for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                if (field.getKey() == TeamFieldEnum.Ext_Server) {
                    String ext = field.getValue().toString();
                    if (ClubConstant.getClubUpdateType(ext) == ClubConstant.TYPE_CLUB_EDIT_NOT_SHOW) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isClubChangeNotShow(String changeText) {
        if (ClubConstant.getClubUpdateType(changeText) == ClubConstant.TYPE_CLUB_EDIT_NOT_SHOW) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是俱乐部区域变更，是的话就显示
     * @param changeText
     * @return
     */
    public static boolean isClubAreaChange(String changeText){
        if(ClubConstant.getClubUpdateType(changeText) == ClubConstant.TYPE_CLUB_EDIT_AREA){
            return true;
        }
        return false;
    }

    /**
     * 判断是否是俱乐部头像变化，是的话就显示
     * @param changeText
     * @return
     */
    public static boolean isClubAvatarChange(String changeText){
        if(ClubConstant.getClubUpdateType(changeText) == ClubConstant.TYPE_CLUB_EDIT_AVATAR){
            return true;
        }
        return false;
    }

    /**
     * 判断是否是群人数上限提升，是的话就显示
     * @param changeText
     * @return
     */
    public static boolean isClubMemberCountLimitChange(String changeText){
        if(ClubConstant.getClubUpdateType(changeText) == ClubConstant.TYPE_CLUB_TEAM_UPGRADE){
            return true;
        }
        return false;
    }

    /**
     * 消息是否属于游戏语音
     * @param audioMessageExtesion
     * @return
     */
    public final static boolean isGameAudioMessage(Map<String , Object> audioMessageExtesion){
        if(audioMessageExtesion != null
                && audioMessageExtesion.containsKey(KEY_AUDIO_CODE)
                && !TextUtils.isEmpty((String) audioMessageExtesion.get(KEY_AUDIO_CODE))){
            return true;
        }
        return false;
    }
}
