package com.htgames.nutspoker.chat.app_msg.attach;

import java.io.Serializable;

/**
 * 游戏状态变化通知
 */
public class GameOverNotify implements Serializable {
    //
    public String gameId;
    public String gameName;
    public String gameCode;
    public String teamId;
    public String creatorId;
    public int gameType;
    public int gameMode;
    public int gameStatus;
    public int userWinChips;//用户盈利
    public int userReward;//用户比赛奖金
    public int userRank;//用户比赛排名
    public int gameEndType;//游戏结束类型
    public int gameStartType;//游戏开始类型
    public int userBalance;//
    public String channel;
}
