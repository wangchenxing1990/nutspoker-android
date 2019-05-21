package com.htgames.nutspoker.circle.bean;

import com.netease.nim.uikit.bean.UserEntity;

import java.io.Serializable;

/**
 * Created by 20150726 on 2016/1/19.
 */
public class LikeItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private UserEntity user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
