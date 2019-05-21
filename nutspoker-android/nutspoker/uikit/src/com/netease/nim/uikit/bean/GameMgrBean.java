package com.netease.nim.uikit.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 周智慧 on 17/1/3.
 */

public class GameMgrBean<T> implements Serializable {
    public String uid;//用户id------uid
    public String uuid;//用户站鱼id----uuid
    public String nickname;
    public String channel; //管理员channel id 17/1/23 add by db
    public int channelType;
    public String tid;//俱乐部id
    public String vid;//俱乐部虚拟id
    public ArrayList<T> mgrList;
    public int players;//这个渠道下的报名人数
    public int block;//这个渠道禁止报名，0允许  1禁止
}