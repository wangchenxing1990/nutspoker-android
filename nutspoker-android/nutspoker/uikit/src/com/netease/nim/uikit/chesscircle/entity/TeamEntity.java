package com.netease.nim.uikit.chesscircle.entity;

import java.io.Serializable;

/**
 */
public class TeamEntity implements Serializable {
    public String id;
    public String vid;//俱乐部虚拟id
    public String name;
    public String introduce;
    public String announcement;
    public String creator;
    public int type;
    public int memberLimit;
    public int memberCount;
    public int verifyType;
    public String extension;

    //// 17/3/22新家的字段
    public String avatar;

    //部落相关
    public int is_owner;//这个俱乐部是否是部落的创建者
    public int score;//部落中俱乐部列表的每个俱乐部的上分分数
}
