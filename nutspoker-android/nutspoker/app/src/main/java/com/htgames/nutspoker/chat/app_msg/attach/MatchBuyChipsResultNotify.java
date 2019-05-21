package com.htgames.nutspoker.chat.app_msg.attach;

import java.io.Serializable;

/**
 * 比赛模式申请买入状态
 */
public class MatchBuyChipsResultNotify implements Serializable {
    //
    public String gameId;
    public String gameName;
    public String gameCode;
    public String channel;
    public int gameType;
    public int gameMode;
    public int buyType;
    public int result_code;//操作结果CODE
    public String result_msg;//操作结果MSG（code为998的时候用）
}
