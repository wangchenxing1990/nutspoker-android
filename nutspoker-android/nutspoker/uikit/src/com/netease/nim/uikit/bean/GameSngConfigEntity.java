package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 * SNG游戏配置
 */
public class GameSngConfigEntity implements Serializable {
    public int chips;
    public int player;
    public int duration;
    public int checkInFee;
    public boolean isControlBuy;
    public int start_sblinds;//起始盲注默认10
    public int sblinds_mode;//盲注结构表类型  1普通  2快速
    public int check_ip = 0;///牌局是否是ip限制
    public int check_gps = 0;///牌局是否是gps限制
    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCheckInFee() {
        return checkInFee;
    }

    public void setCheckInFee(int checkInFee) {
        this.checkInFee = checkInFee;
    }
    public String horde_id;
    public String horde_name;
    public int club_channel;//在俱乐部内部创建牌局时候的人员的身份，0表示普通成员，1表示管理员或者部长


    //下面的值不传给服务端，只是保存带本地
    public int checkInFee_index;
    public int chips_index;
    public int duration_index;
    public int player_index;
}
