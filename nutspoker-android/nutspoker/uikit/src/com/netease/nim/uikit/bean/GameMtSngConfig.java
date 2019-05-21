package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 */
public class GameMtSngConfig implements Serializable {
    public int matchChips;
    public int matchCheckinFee;
    public int matchPlayer;
    public int matchDuration;
    public int totalPlayer;//比赛总人数
    public String horde_id;
    public String horde_name;
    public int club_channel;//在俱乐部内部创建牌局时候的人员的身份，0表示普通成员，1表示管理员或者部长
}
