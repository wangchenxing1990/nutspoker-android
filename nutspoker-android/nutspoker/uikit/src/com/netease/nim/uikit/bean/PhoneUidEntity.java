package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 */
public class PhoneUidEntity implements Serializable {
    public String phone;
    public String uid;
    public int os;

    public PhoneUidEntity() {

    }

    public PhoneUidEntity(String phone, String uid, int os) {
        this.phone = phone;
        this.uid = uid;
        this.os = os;
    }
}
