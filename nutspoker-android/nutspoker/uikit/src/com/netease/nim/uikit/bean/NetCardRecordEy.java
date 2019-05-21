package com.netease.nim.uikit.bean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by glp on 2016/8/5.
 */
//网络牌谱记录数据
public class NetCardRecordEy {
    //公共牌区 空;3;3+1;3+1+1;3+2;5
    public List<List<Integer>> pool_cards;
    //牌局Code
    public String code;
    //盈利
    public int win_chips;//——
    //牌类型
    public int card_type;//——
    //时间戳
    public long time; // --create_time
    //牌局ID
    public String gid;
    //我的最佳牌型
    public HashMap<String, CardTypeEy> cardtype_cards;
    //我的底牌
    public List<Integer> hand_cards;
    public int hands_cnt;//第几手牌
    public String id;//牌谱记录ID
    public int is_collect;//0不收藏，1收藏
    public int play_mode;//
    public int play_type;//
    public boolean fantasy;
}
