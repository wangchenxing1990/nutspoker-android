package com.netease.nim.uikit.chesscircle.entity;

import java.io.Serializable;

/**
 * Created by 周智慧 on 17/3/21.
 */

public class HordeEntity implements Serializable {
    public String horde_id;
    public String horde_vid;
    public String name;
    public long ctime;
    public String tid;//创建者的俱乐部id
    public String owner;//创建者的uid
    public int is_my;//是否是我的俱乐部创建的
    public int playing_count;//进行中的牌局数目
    public int is_control;//开局限制
    public int is_modify;//部落是否修改过名称；部落只能修改一次昵称，1代表修改过

    public int money;//创建牌局如果共享到这个部落的时候需要话费的钻石
    public int member;
    public int is_score;//是否开启 上分控制
    public int memberLimit;
}
