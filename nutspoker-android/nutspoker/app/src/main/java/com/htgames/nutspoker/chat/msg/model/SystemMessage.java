package com.htgames.nutspoker.chat.msg.model;

import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;

import java.io.Serializable;

/**
 * Created by 20150726 on 2016/3/24.
 */
public class SystemMessage implements Serializable {
    public long messageId;
    public SystemMessageType type;
    public String fromAccount;
    public String targetId;
    public long time;
    public SystemMessageStatus status;
    public String content;
    public String attach;
    public Object attachObject;
    public boolean unread;

    //下面的不是云信的东西，是自己定义的东西  SystemMsgTable表用不到下面三个字段，是给HordeTable用的
    public int custom_outer_type;//自定义消息类型，这个消息不是云信消息，而是自定义的，比如申请加入部落等等，外部的type，比如CustomNotificationConstants.NOTIFICATION_TYPE_HORDE
    public int custom_inner_type;//自定义消息类型，这个消息不是云信消息，而是自定义的，比如申请加入部落等等，内部的type，比如SystemMessageTypeCustom.CUSTOM_TYPE_APPLY_HORDE
    public String key;
    public String tid;//俱乐部id
    public String tname;//俱乐部name
    public String tavatar;//俱乐部头像
    public String horde_id;//部落id
    public String horde_name;//部落name
    public int horde_status;//部落信息状态 0初始化，  1通过， 2拒绝

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public SystemMessageType getType() {
        return type;
    }

    public void setType(SystemMessageType type) {
        this.type = type;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public SystemMessageStatus getStatus() {
        return status;
    }

    public void setStatus(SystemMessageStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Object getAttachObject() {
        return attachObject;
    }

    public void setAttachObject(Object attachObject) {
        this.attachObject = attachObject;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }
}