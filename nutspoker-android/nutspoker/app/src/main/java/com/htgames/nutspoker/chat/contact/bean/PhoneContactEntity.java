package com.htgames.nutspoker.chat.contact.bean;

import com.htgames.nutspoker.chat.contact.model.ContactRelation;

import java.io.Serializable;

/**
 * Created by 20150726 on 2015/10/9.
 */
public class PhoneContactEntity implements Serializable {
    String name;
    String signature;
    String phone;
    String uid;
    ContactRelation relation;
    String pinyin;
    private String sortKey;
    int userOSType;//android还是IOS

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ContactRelation getRelation() {
        return relation;
    }

    public void setRelation(ContactRelation relation) {
        this.relation = relation;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUserOSType() {
        return userOSType;
    }

    public void setUserOSType(int userOSType) {
        this.userOSType = userOSType;
    }
}
