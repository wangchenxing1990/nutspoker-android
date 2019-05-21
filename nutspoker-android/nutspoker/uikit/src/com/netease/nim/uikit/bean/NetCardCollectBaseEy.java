package com.netease.nim.uikit.bean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by glp on 2016/8/11.
 */

public class NetCardCollectBaseEy {
    //兼容ID，兼容新旧版本的收藏手牌
    public String id;
    //手牌ID，
    public String hid;
    //手牌收藏者用户ID
    public String uid;
    //手牌收藏时间
    public long collect_time;

    public String file_path;//"2016/08/TexasSheet-1470383006-232319-10089-111",
    public String file_name;//"TexasSheet-1470383006-232319-10089-111"

    //public String _id;
    //牌池
//    public List<Integer> pool_cards;
    //牌局Code
    public String code;
    //盈利
    public int win_chips;
    //牌类型
    public int card_type;
    //手牌创建时间
    public long time;
    //牌局ID
    public String gid;
    //卡牌最优牌型
//    public List<Integer> cardtype_cards;
    //手牌
    public List<Integer> hand_cards;
    //牌局的第几手
    public int hands_cnt;
    //收藏牌谱总数
    public int count;
    //Team ID
    public String tid;
    //牌局拥有者
    public String owner;
    //名字
    public String name;
    //盲注类型
    public int blinds;
    //小盲
    public int sblinds;
    //历时
    public int duration;
    //历时
    public int durations;
    //保险
    public int tilt_mode;
    //牌局类型
    public int type;
    //牌局状态
    public int status;
    //是否公开
    public int public_mode;
    //前注模式 以前版本使用，现在为了兼容
    public int ante_mode;
    //前注
    public int ante;
    //创建时间
    public long create_time;
    //
    public int bouts;
    //游戏模式 普通 sng mt-sng mtt
    public int game_mode;
    //比赛筹码
    public int match_chips;
    //比赛人数
    public int match_player;
    //比赛时间
    public int match_duration;
    //比赛报名费
    public int match_checkin_fee;
    //牌池
    public List<List<Integer>> pool_cards;
    //卡牌最优牌型
    public HashMap<String, CardTypeEy> cardtype_cards;
    public int play_mode;//
    public int play_type;//
    public boolean fantasy;
}
