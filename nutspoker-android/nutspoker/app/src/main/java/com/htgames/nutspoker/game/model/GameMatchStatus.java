package com.htgames.nutspoker.game.model;

/**
 * 比赛模式中状态
 */
public class GameMatchStatus {
    public final static int GAME_STATUS_NORMAL = 0;//游戏状态:无
    public final static int GAME_STATUS_WILL_START = 99;//游戏状态:即将开始
    public final static int GAME_STATUS_MID_REST = 100;//游戏状态:中场休息
    public final static int GAME_STATUS_FINAL_REST = 101;//游戏状态:决赛休息
    public final static int GAME_STATUS_REST_FINISH = 102;//游戏状态:休息结束
    public final static int GAME_STATUS_MID_REST_WILL = 103;//游戏状态:将要开始中场休息
    public final static int GAME_STATUS_FINAL = 104;//游戏状态:决赛桌
    public final static int GAME_STATUS_PAUSE = 105;//游戏状态:暂停中
    public final static int GAME_STATUS_RESUME = 106;//游戏状态:暂停恢复
}
