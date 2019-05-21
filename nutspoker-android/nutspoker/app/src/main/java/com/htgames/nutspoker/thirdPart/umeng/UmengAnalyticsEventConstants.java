package com.htgames.nutspoker.thirdPart.umeng;

/**
 * 事件
 */
public class UmengAnalyticsEventConstants {

    public final static String ID_GAME_CREATE = "1001";//创建普通牌局（1组局,2俱乐部,3圈子）
    public final static String ID_GAME_JOIN = "1002";//加入普通牌局（1码,2历,3俱,4圈）
    public final static String ID_GAME_INVITE = "1003";//邀请好友加入普通牌局
    public final static String ID_GAME_CREATE_SNG = "1101";//创建SNG牌局（1组局,2俱乐部,3圈子）
    public final static String ID_GAME_JOIN_SNG = "1102";//加入SNG牌局（1码,2历,3俱,4圈）
    public final static String ID_GAME_INVITE_SNG = "1103";//邀请好友加入SNG牌局
    public final static String ID_SHARE_COUNT = "2001";//分享次数
    public final static String ID_COMMENT_COUNT = "2002";//评论次数
    public final static String ID_LIKE_COUNT = "2003";//点赞次数
    public final static String ID_CLUB_COUNT = "3001";//俱乐部(创建数量)
    public final static String ID_GROUP_COUNT = "3002";//圈子(创建数量)
    public final static String ID_SHOP_TOPUP = "4001";//充值次数
    public final static String ID_SHOP_BUYGOODS = "4002";//购买德州币商品

    public final static String TYPE_GAME_PRIVATE = "1";
    public final static String TYPE_GAME_CLUB = "2";
    public final static String TYPE_GAME_GROUP = "3";

    public final static String WAY_GAME_JOIN = "way_game_join";
    public final static String WAY_GAME_JOIN_BY_CODE = "1";
    public final static String WAY_GAME_JOIN_BY_RECENT = "2";
    public final static String WAY_GAME_JOIN_BY_CLUB = "3";
    public final static String WAY_GAME_JOIN_BY_GROUP = "4";
    public final static String WAY_GAME_JOIN_BY_INVITE = "5";
    public final static String WAY_GAME_JOIN_BY_APP_MESSAGE= "6";//通过系统消息进游戏
    public final static String WAY_GAME_JOIN_BY_ENTER_ROOM_FAILED= "7";//自由局进入聊天室失败 会调用game/getGame接口

    public final static String KEY_GAME_TYPE = "type";//
    public final static String KEY_GAME_BLIND = "blind";//
    public final static String KEY_GAME_CHIPS = "chips";//
    public final static String KEY_GAME_DURATION = "duration";//

    public final static String KEY_GAME_JOIN_WAY = "joinWay";//

    public final static String KEY_SHOP_TOPUP_ID = "topupId";//
//    public final static String KEY_SHOP_TOPUP_PRICE = "topupPrice";//
    public final static String KEY_SHOP_GOODS_ID = "goodsId";//商品
}
