package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * Created by 20150726 on 2015/11/16.
 */
public class WinChipEntity implements Serializable{
    public int count;
    public long _id;
    public int year;//年的使用

    public WinChipEntity(int count, long date) {
        this.count = count;
        this._id = date;
    }

    public WinChipEntity(int count, long month,int year) {
        this.count = count;
        this._id = month;
        this.year = year;
    }
}
