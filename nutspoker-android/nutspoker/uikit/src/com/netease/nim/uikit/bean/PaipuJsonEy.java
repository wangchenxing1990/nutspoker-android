package com.netease.nim.uikit.bean;

import java.util.List;

/**
 * Created by glp on 2016/8/16.
 */

public class PaipuJsonEy {
    public List<Integer> hand_cards;
    public int tableIndex;
    public int card_type;
    public String gid;
    public String record_version;
    public int match_checkin_fee;
    public String uid;
    public String tid;
    public int ante_mode;
    public int match_duration;
    public int match_player;
    public String owner;
    public int game_mode;
    public List<Integer> cardtype_cards;
    public long create_time;
    public int titl_mode;
    public int status;
    public String code;
    public int sblinds;
    public int public_mode;
    public List<Integer> pool_cards;
    public int durations;
    public int hands_cnt;
    public int ante;
    public int type;
    public int win_chips;
    public String name;
    public int match_chips;

    public String file_name;//文件名称
    public String file_path;//文件网络路径

    //如果没有这个字段，说明是 1.2.3以下版本分享，需要去阿里服务器下载
    //新增牌谱ID
    public String hid;
}
