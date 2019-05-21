package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * 用户基类
 */
public class UserEntity implements Serializable {
    public String account;
    public String name;
    public String avatar;
    public String uuid;
    public String nickname;
    public String channel;
    public int channelType;

    public UserEntity() {
    }

    public UserEntity(String account, String name, String avatar) {
        this.account = account;
        this.name = name;
        this.avatar = avatar;
    }
}
