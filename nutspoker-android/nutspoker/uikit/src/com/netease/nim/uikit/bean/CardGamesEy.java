package com.netease.nim.uikit.bean;

/**
 * Created by glp on 2016/8/8.
 */
//牌局信息
public class CardGamesEy {
    //Game ID
    public String gid;
    //Team ID
    public String tid;
    //牌局名称-牌谱记录页使用
    public String name;
    //牌局Code
    public String code;
    //——
    public int blinds;//——
    //小盲
    public int sblinds;
    //历时
    public int duration;
    //创建者账号
    public String owner;
    //俱乐部，个人
    public int type;
    //牌局模式
    public int public_mode;
    //牌局模式：ANTE
    public int ante_mode;
    //牌局模式：保险
    public int tilt_mode;
    //0 准备中 1进行中 2已结束
    public int status;
    //创建时间戳-牌谱记录页使用如果没有结束时间，用开始时间
    public long create_time;
    public int total_player;
    public String server;
    //比赛模式  ——
    public int game_mode;//——
    //比赛盈利
    public int match_chips;
    //比赛玩家数量-牌谱记录页使用
    public int match_player;//——
    //比赛历时
    public int match_duration;
    //结束时间戳牌谱记录页使用 默认使用结束时间
    public long end_time;
    //比赛费用-牌谱记录页使用
    public int match_checkin_fee;//——
    //聊天室ID
    public String room_id;
}
