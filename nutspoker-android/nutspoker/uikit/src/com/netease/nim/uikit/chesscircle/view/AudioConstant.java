package com.netease.nim.uikit.chesscircle.view;
import android.text.TextUtils;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * 音频
 */
public class AudioConstant {
    public final static String KEY_AUDIO_UID = "uid";
    public final static String KEY_AUDIO_CODE = "code";
    public final static String KEY_AUDIO_TABLE_INDEX = "tableIndex";

    /**
     * 构建游戏语音消息拓展参数
     *
     * @param uid
     * @param code 牌局对应的CODE
     * @return
     */
    public final static Map<String, Object> getGameAudioMessageExtension(String uid, String code, int tableIndex) {
        Map<String, Object> extension = new HashMap<String, Object>();
        extension.put(KEY_AUDIO_UID, uid);
        extension.put(KEY_AUDIO_CODE, code);
        extension.put(KEY_AUDIO_TABLE_INDEX, tableIndex);
        return extension;
    }

    public final static Map<String, Object> getMatchRoomAudioMessageExtension(String uid, String code) {
        Map<String, Object> extension = new HashMap<String, Object>();
        extension.put(KEY_AUDIO_UID, uid);
        extension.put(KEY_AUDIO_CODE, code);
        extension.put(KEY_AUDIO_TABLE_INDEX, 0);
        return extension;
    }

    /**
     * 消息是否属于游戏语音，根据指定的牌局
     * @param imMessage
     * @return
     */
    public final static boolean isGameAudioMessage(IMMessage imMessage, String code) {
        if (imMessage != null && imMessage.getSessionType() == SessionTypeEnum.Team || imMessage.getSessionType() == SessionTypeEnum.ChatRoom && imMessage.getMsgType() == MsgTypeEnum.audio) {

            Map<String, Object> audioMessageExtesion = imMessage.getRemoteExtension();
            if (audioMessageExtesion != null
                    && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_CODE)
                    && code.equals(String.valueOf(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_CODE)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否属于比赛牌局游戏语音
     *
     * @param imMessage
     * @param code
     * @param tableIndex 牌桌号
     * @return
     */
    public final static boolean isMatchAudioMessage(ChatRoomMessage imMessage, String code, int tableIndex) {
        if (imMessage == null || imMessage.getSessionType() != SessionTypeEnum.ChatRoom || imMessage.getMsgType() != MsgTypeEnum.audio) {
            return false;
        }
        Map<String, Object> audioMessageExtesion = imMessage.getRemoteExtension();
        try {
            if (audioMessageExtesion != null
                    && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_CODE)
                    && code.equals(String.valueOf(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_CODE)))
                    && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_TABLE_INDEX)
                    && Integer.parseInt(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_TABLE_INDEX).toString()) == tableIndex) {
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 是否属于比赛牌局创建者游戏语音
     * @param imMessage
     * @param code
     * @param tableIndex 牌桌号
     * @return
     */
    public final static boolean isMatchOwnerAudioMessage(ChatRoomMessage imMessage, String code, String ownerId, int tableIndex) {
        if (imMessage == null || imMessage.getSessionType() != SessionTypeEnum.ChatRoom || imMessage.getMsgType() != MsgTypeEnum.audio) {
            return false;
        }
        Map<String, Object> audioMessageExtesion = imMessage.getRemoteExtension();
        try {
            if (audioMessageExtesion != null
                    && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_CODE)
                    && code.equals(String.valueOf(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_CODE)))
                    && tableIndex > 0) {
                    if (audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_TABLE_INDEX)) {
                        if (Integer.parseInt(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_TABLE_INDEX).toString()) == 0) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return true;
                    }
            }
        } catch (Exception e) {

        }
        return false;
    }

//    public final static boolean isMatchAudioMessage(IMMessage imMessage, String code) {
//        if (imMessage == null || imMessage.getSessionType() != SessionTypeEnum.Team || imMessage.getMsgType() != MsgTypeEnum.audio) {
//            return false;
//        }
//        Map<String, Object> audioMessageExtesion = imMessage.getRemoteExtension();
//        if (audioMessageExtesion != null
//                && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_CODE)
//                && code.equals(String.valueOf(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_CODE)))) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 消息是否属于游戏语音
     *
     * @param imMessage
     * @return
     */
    public final static boolean isGameAudioMessage(IMMessage imMessage) {
        if (imMessage == null || imMessage.getSessionType() != SessionTypeEnum.Team || imMessage.getMsgType() != MsgTypeEnum.audio) {
            return false;
        }
        Map<String, Object> audioMessageExtesion = imMessage.getRemoteExtension();
        LogUtil.d("MessageHelper", audioMessageExtesion == null ? "null" : audioMessageExtesion.toString());
        if (audioMessageExtesion != null
                && audioMessageExtesion.containsKey(AudioConstant.KEY_AUDIO_CODE)
                && !TextUtils.isEmpty(String.valueOf(audioMessageExtesion.get(AudioConstant.KEY_AUDIO_CODE)))) {
            return true;
        }
        return false;
    }
}
