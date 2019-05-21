package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * Created by 周智慧 on 2017/8/25.
 */

public class BaseMttConfig implements Serializable {
    public int matchCheckinFee;//match_checkin_fee  报名费
    public int matchChips;//match_chips 初始记分牌
    public int matchDuration;// match_duration: Int = 0//大菠萝是 升底时间  mtt的是涨盲时间
    public int match_max_buy_cnt;//mtt总买入上限
    public int matchLevel;//延时报名（终止报名）盲注级别
    public int match_rebuy_cnt;//mtt重构次数 代替  rebuyMode字段，之前字段只能取0和1，新版本改版取0-5
    public int match_type;//0金币赛，1钻石赛  2金币赛
    public int is_auto;//是否自动开赛
    public int ko_mode = 0;//  --0不是猎头赛 1普通 2超级
    public int ko_reward_rate = 0;//  奖金分成
    public int ko_head_rate; //人头分成(只有超级猎人赛才会有)
    public int is_control;
    public long beginTime;//开始时间
    public int restMode;//是否中场休息
    public int rebuyMode;//是否重购
    public int addonMode;//是否增购
    public String horde_id;
    public String horde_name;
    public int club_channel;//在俱乐部内部创建牌局时候的人员的身份，0表示普通成员，1表示管理员或者部长
}
