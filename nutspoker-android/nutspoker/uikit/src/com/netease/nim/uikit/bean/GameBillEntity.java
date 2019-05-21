package com.netease.nim.uikit.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 牌局详情列表
 */
public class GameBillEntity implements Serializable {
    public GameEntity gameInfo;//牌局ID
    public int maxPot;//最大底池
    public int allBuys;//总买入
    public int bouts;//总手数
    public int winChip;//获取的筹码
    public String mvp;//MVP
    public String fish;//大鱼
    public String richest;//土豪
    public ArrayList<GameMemberEntity> gameMemberList;
    public String jsonStr;
    public String jsonMemberStr;
    //
    public int allReward;//总奖池
    public int totalTime;//比赛时长
    public String myUid;//牌局信息的拥有者
    public GameMemberEntity myMemberInfo;//我的相关数据
    //MTT添加的
    public int endSblindsIndex;//结束时的盲注级别
    public int totalPlayer;//总的参赛人数
}
