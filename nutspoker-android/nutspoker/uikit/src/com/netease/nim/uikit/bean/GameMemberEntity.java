package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * 牌局玩家信息
 */
public class GameMemberEntity implements Serializable {
    //    public String uid;
//    public String nickname;
//    public String avater;
    public UserEntity userInfo;
    public int totalBuy;//总买入筹码
    public int winChip;//牌局中赢得筹码
    public int premium;//保险买入
    public int insurance;//保险赔付
    public int joinCnt;//参与手数
    public int enterPotCnt;//入池手数
    public int winCnt;//胜利手数，失败手数 ＝ 参与手数 － 胜利手数
    //
    public int reward;//自己的奖金
    public int ranking;//排名
    //MT模式
    public int addonCnt;//重购次数
    public int rebuyCnt;//增购次数
    public int blindsIndex;//用户被淘汰时的盲注级别
    //
    public Integer[] gains; //盈利列表
    public Integer[] insuranceGains; //保险盈利

    public String ko_head_cnt;//mtt大厅玩家列表的人头数   以前是float现在改为String，
    public int ko_head_reward;//mtt大厅玩家列表的人头奖金

    public String opt_user;//这个玩家是谁批准报名的，私人或者俱乐部
    public int opt_user_type;///这个玩家是谁批准报名的，0私人1俱乐部
    //大菠萝
    public int fantasy_cnt;//大菠萝"范特西"手数
}
