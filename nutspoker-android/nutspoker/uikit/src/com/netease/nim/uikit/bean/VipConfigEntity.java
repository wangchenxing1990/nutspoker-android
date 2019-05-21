package com.netease.nim.uikit.bean;

/**
 * VIP对应的描述实体类
 */
public class VipConfigEntity {
    public String desc;
    public String whiteContent;
    public String balckContent;


    public VipConfigEntity() {

    }

    public VipConfigEntity(String desc, String whiteContent, String balckContent) {
        this.desc = desc;
        this.whiteContent = whiteContent;
        this.balckContent = balckContent;
    }
}
