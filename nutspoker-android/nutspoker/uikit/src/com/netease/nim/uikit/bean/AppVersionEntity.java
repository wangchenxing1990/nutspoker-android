package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 */
public class AppVersionEntity implements Serializable {
    public String newVersion;
    public boolean isMandatory;//是否需要强制更新
    public boolean isShow;//是否显示更新
    public String downloadUrl;
    public String content;
}
