package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * Created by 周智慧 on 17/2/22.
 */

public class UserAmountBean implements Serializable {
    //{"code":0,"message":"ok","data":{"diamond":"2000","coins":"1840","time":1487662096,"uuid":"5653559"}}
    public String diamond;
    public String coins;
    public String time;
    public String uuid;

    @Override
    public String toString() {
        return "{\"diamond\":" + diamond +
                ",\"coins\":" + coins +
                ",\"time\":" + time +
                ",\"uuid\":" + uuid + "}";
    }
}
