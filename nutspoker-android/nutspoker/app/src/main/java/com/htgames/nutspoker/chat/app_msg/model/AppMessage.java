package com.htgames.nutspoker.chat.app_msg.model;

import java.io.Serializable;

public class AppMessage implements Serializable {
    public long messageId;
    public AppMessageType type;
    public String fromId;//如果targetId不为空，fromId是处理者的id；如果targetId为空，fromId是报名玩家的id-----应该是这样
    public String targetId;//报名玩家id，当且仅当是管理员或者房主处理报名的消息targetId才不为空，否则一直都是空-----应该是这样  targetId为空表示是报名消息，否则是处理报名消息
    public String checkinPlayerId;//报名玩家id   是fromId或者targetId   和都有可能     以前数据库(版本5)区分一条消息使用的是fromId，新的数据库版本(6之后)用checkinPlayerId（具体使用方式查看AppMsgDBHelper.java）
    public long time;
    public AppMessageStatus status;
    public String content;
    public String attach;
    public Object attachObject;
    public boolean unread;
    public String key;
    public String sortKey;
    public int online;//当且仅当游戏内结束的消息这个字段是1，是为了不通知用户，因为在游戏内已经通知过了。其余情况都是0



    //下面的字段是特殊用途，不入库
    public int gidGroupIndex = 0;//同一个gid组内的当前的index
//    public int gidGroupNum = 0;//同一个gid组内数目
}