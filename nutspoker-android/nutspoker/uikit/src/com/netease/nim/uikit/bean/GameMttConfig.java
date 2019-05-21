package com.netease.nim.uikit.bean;

/**
 */
public class GameMttConfig extends BaseMttConfig {
    public int matchPlayer;
    public int start_sblinds;//起始盲注默认10
    public int sblinds_mode;//盲注结构表类型  1普通  2快速

    //下面的值不传给服务端，只是保存带本地
    public int matchCheckinFee_index;
    public int matchChips_index;
    public int matchDuration_index;
    public int start_sblinds_index;//起始盲注默认10
    public int matchPlayer_index;
    public int match_max_buy_cnt_index;//mtt总买入上限
    public int matchLevel_index;//延时报名（终止报名）盲注级别
    public int match_rebuy_cnt_index;//mtt重构次数 代替  rebuyMode字段，之前字段只能取0和1，新版本改版取0-5
    public int ko_reward_rate_index = 0;//  奖金分成
    public int ko_head_rate_index; //人头分成(只有超级猎人赛才会有)
}
