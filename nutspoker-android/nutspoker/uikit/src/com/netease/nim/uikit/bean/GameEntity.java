package com.netease.nim.uikit.bean;

import java.io.Serializable;

/**
 */
public class GameEntity implements Serializable {

    /**
     * 每个Item的类型：添加类型
     */
    public enum DeskItemTag {
        NORMAL,
        ADD
    }

    public String channel;//这个字段不需要从json里面解析，是另外获取的
    public String joinCode;//这个字段不需要从json里面解析，是intent里面传的，主要负责显示

    public String name;//牌局名称
    public String code;//牌局CODE
    public String tid;//牌局群组ID
    public String vid;//牌局群组虚拟ID
    public String gid;//牌局ID
    public int status;//牌局状态     我建的一个mtt 已开赛返回的status是0
    public int activity;//是否参加过牌局，参加过的话三个牌局列表的地方显示"游戏中"  0 wait 1 surround 2 ready 3 sit 4 leave
    public UserEntity creatorInfo;//牌局创建者, 这个字段黄老师没有返回，用uid这个字段表示牌局的创建者
    public String uid;
    public int publicMode;//牌局模式：公开
    public int type;//牌局类型
    public long currentServerTime;//牌局服务器当前时间
    public long createTime;//牌局创建时间
    public long entryTime;//牌局进入时间
    public long endTime;//牌局结束时间
    public int maxCount;//牌局最大人数
    public int gamerCount;//牌局当前人数
    public int checkinPlayerCount;//牌局当前人数
    public int isInvited;//牌局是被邀请的
    public DeskItemTag deskItemTag;
    public int tag;//是否置顶
    public int unReadMsgCount;//牌局未读消息数量
    public String serverIp;//游戏服务器IP
    public int is_admin;//玩家类型 普通成员   管理员（包括房主）（自由局区分直接进游戏还是进等待界面）
    //
    public int gameMode;//游戏模式
    public int match_type;//金币赛0 钻石赛1  金币赛2
    public int play_mode;//0--德州扑克；1--奥马哈
    public boolean isCheckin;//是否已经报名
    public Object gameConfig;
    public String room_id;
    public int club_channel;//在俱乐部内部创建牌局时候的人员的身份，0表示普通成员，1表示管理员或者部长
    public String horde_id;
    public String horde_name;
    public long start_time;//只有普通局才用这个字段，sng和mtt不用这个字段，用来判断游戏内是否点击开始，当且仅当游戏内点击开始了这个字段才有值(freeroomac里面点击开始这个值仍然是0)
    public String json_str;//这个只是服务端的云信扩展字段，把云信扩展字段原封不动地传给游戏，只在这一种情况下用到
}
