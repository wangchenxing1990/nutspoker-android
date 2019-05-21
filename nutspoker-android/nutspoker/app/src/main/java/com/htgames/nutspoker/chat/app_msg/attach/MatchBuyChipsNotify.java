package com.htgames.nutspoker.chat.app_msg.attach;

import java.io.Serializable;

/**
 * 比赛模式申请买入
 */
public class MatchBuyChipsNotify implements Serializable {
    public String handledNickname;//处理消息的管理员或者房主的昵称     处理报名请求后  再群发这个处理消息  会有这个字段manager_nickname表示是谁处理的
    public String gameId;
    public String gameName;
    public String gameCode;
    public int gameType;
    public int gameMode;
    public long gameCreateTime;
    public String channel;
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
    //
    public long buyStarttime;//买入发起时间
    public int buyTimeout;//买入超时
    public int buyType;//买入类型（1.主动重购  2.淘汰重构（复活） 3.主动增购 4.淘汰增购 ）
    public int rebuyCnt;//重购次数
    public int result_code;//操作结果CODE
    public String result_msg;//操作结果MSG（code为998的时候用）
    public String nodename;//
}
