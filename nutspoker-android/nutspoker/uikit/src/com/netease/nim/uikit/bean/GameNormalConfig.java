package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * 游戏普通模式配置
 */
public class GameNormalConfig implements Serializable {
    public int ante;//牌局：ANTE数值
    public int anteMode;//牌局模式：ANTE
    public int tiltMode;//牌局模式：保险
    public int blindType;//游戏创建的时候是type(档位)，牌谱记录显示是小盲   服务端返回sblinds用的也是这个字段，但是不是档位了，是具体的值（蛋疼）
    public int sblinds;//小盲  具体的值，未来解析服务端数据时用这个字段代替blindType
    public int timeType;//创建游戏传的是档位，服务端返回json解析是数值秒，（蛋疼）
    public int matchPlayer;//单桌人数（6/9人）
    public int matchChips = 0;///牌局带入记分牌
    public int min_chips = 0;///牌局最小带入记分牌
    public int max_chips = 0;///牌局最大带入记分牌
    public int total_chips = 0;///牌局总带入记分牌
    public int check_ip = 0;///牌局是否是ip限制
    public int check_gps = 0;///牌局是否是gps限制
    public int check_straddle = 0;///牌局是否是强制straddle


    //下面的变量不需要传给服务端，只是把游戏设置保存到本地
    public int is_control = 0;///牌局是否是控制带入0不是1是控制带入
    public int chip_seekbar_index = 0;///最上部的拖动条的index
    public int min_chips_index = 0;///牌局最小带入记分牌index
    public int max_chips_index = 0;///牌局最大带入记分牌index
    public int total_chips_index = 0;///牌局总带入记分牌index
    public int ante_index;//牌局：ANTE数值index
    public int matchPlayer_index;//单桌人数（6/9人）_index
    public int timeType_index;//时间_index
    public String horde_id;
    public String horde_name;
    public int club_channel;//在俱乐部内部创建牌局时候的人员的身份，0表示普通成员，1表示管理员或者部长
}
