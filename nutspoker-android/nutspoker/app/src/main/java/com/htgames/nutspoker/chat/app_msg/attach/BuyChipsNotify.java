package com.htgames.nutspoker.chat.app_msg.attach;

import java.io.Serializable;

/**
 */
public class BuyChipsNotify implements Serializable {
    public String gameId;
    public String gameName;
    public String gameCode;
    public int gameType;
    public int gameMode;
    public long gameCreateTime;
    public int gameSBlinds;
    public int gameDurations;
    public int gamePlayerCount;
    //
    public int matchChips;
    public int matchCheckinFee;
    public int matchPlayer;
    public int matchDuration;
    public int totalPlayer;//比赛总人数
    public int checkinPlayer;//报名人数
    //
    public String userId;
    public String userNickname;
    public String userAvatar;
    public String uuid;
    public String channel;
    public long buyStarttime;//买入发起时间
    public int buyTimeout;//买入超时
    public int userWinChips;//用户盈利
    public int userBuyChips;//用户买入
    public int userTotalBuyChips;//用户总买入
    public int status;//状态
    public String nodename;//
}
