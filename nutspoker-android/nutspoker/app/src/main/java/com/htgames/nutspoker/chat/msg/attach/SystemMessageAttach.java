package com.htgames.nutspoker.chat.msg.attach;

import android.text.TextUtils;

import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 系统消息抄送解析类
 */
public class SystemMessageAttach {
    //节点
    public static final String KEY_NODE_ATTACH = "attach";
    public static final String KEY_NODE_NOTIFY_EVENT = "vt";//好友通知
    public static final String KEY_NODE_TEAMINFO = "tinfo";//群信息
    //字段
    public static final String KEY_TEAM_ID = "1";//群ID，可以转为LONG型
    public static final String KEY_TEAM_NAME = "3";//群名称
    public static final String KEY_TEAM_TYPE = "4";//群类型，0代表讨论组，1代表高级群，可转为Integer类型
    public static final String KEY_TEAM_CREATOR_ID = "5";//创建者用户帐号
    public static final String KEY_TEAM_ANNOUNCEMENT = "7";//群公告等各种属性
    public static final String KEY_TEAM_MEMBERCOUNT = "9";//群有效成员数量
    public static final String KEY_TEAM_EXTENSION = "18";//第三方拓展字段
    public static final String KEY_TEAM_EXTSERVER = "19";//第三方服务器拓展字段

    /**
     * 解析系统消息里面的好友通知类型
     * @param targetid
     * @param attach
     * @return
     */
    public static AddFriendNotify parseAddFriendNotify(String targetid ,String attach) {
        AddFriendNotify attachData = null;
        if(TextUtils.isEmpty(attach)){
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(attach);
            if(jsonObject.has(KEY_NODE_NOTIFY_EVENT)){
                int event = jsonObject.optInt(KEY_NODE_NOTIFY_EVENT);
                //加好友 , account:接受者的  ， msg：添加的好友的内容
                attachData = new AddFriendNotify(targetid, AddFriendNotify.Event.eventOfValue((byte) event));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attachData;
    }

    /**
     * 解析系统消息里面的群组资料
     * @param attach
     * @return
     */
    public static TeamEntity parseTeamEntity(String attach){
        TeamEntity teamEntity = null;
        if(TextUtils.isEmpty(attach)){
            return null;
        }
        try {
            JSONObject json = new JSONObject(attach);
            if(json.has(KEY_NODE_TEAMINFO)){
                JSONObject infoObject = json.optJSONObject(KEY_NODE_TEAMINFO);
                teamEntity = new TeamEntity();
                teamEntity.id = (infoObject.optString(KEY_TEAM_ID));
                teamEntity.name = (infoObject.optString(KEY_TEAM_NAME));
                teamEntity.type = (infoObject.optInt(KEY_TEAM_TYPE));
                teamEntity.creator = (infoObject.optString(KEY_TEAM_CREATOR_ID));
                teamEntity.memberCount = (infoObject.optInt(KEY_TEAM_MEMBERCOUNT));
                teamEntity.extension = (infoObject.optString(KEY_TEAM_EXTSERVER));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return teamEntity;
    }
}
