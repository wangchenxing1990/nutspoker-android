package com.htgames.nutspoker.circle.bean;

import com.netease.nim.uikit.bean.UserEntity;

import java.io.Serializable;

/**
 * Created by 20150726 on 2016/1/19.
 */
public class CommentItem implements Serializable {

    private String cid;
    private UserEntity user;
    private UserEntity toReplyUser;
    private String content;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public UserEntity getToReplyUser() {
        return toReplyUser;
    }

    public void setToReplyUser(UserEntity toReplyUser) {
        this.toReplyUser = toReplyUser;
    }

}
