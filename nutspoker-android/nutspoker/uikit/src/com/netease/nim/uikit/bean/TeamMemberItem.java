package com.netease.nim.uikit.bean;

/**
 * 群成员
 */
public class TeamMemberItem {
    /**
     * 每个Item的类型：讨论组成员，添加成员，移除成员
     */
    public static enum TeamMemberItemTag {
        NORMAL,
        ADD,
        DELETE,
        ALL,
        FINISH
    }

    public final static String OWNER = "owner";//拥有者
    public final static String ADMIN = "admin";//管理员

    public TeamMemberItemTag tag;
    public String tid;
    public String account;
    public String desc;

    public TeamMemberItem(TeamMemberItemTag tag, String tid, String account, String desc) {
        this.tag = tag;
        this.tid = tid;
        this.account = account;
        this.desc = desc;
    }
}