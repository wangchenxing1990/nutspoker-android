package com.htgames.nutspoker.chat.notification.constant;

/**
 * 自定义通知参数
 */
public class CustomNotificationConstants {
    public final static String KEY_NOTIFICATION_TYPE = "type";//自定义通知类型，类型
    public final static String KEY_NOTIFICATION_DATA = "data";//自定义通知类型，数据

    public final static int NOTIFICATION_NORMAL = 0;//默认
    public final static int NOTIFICATION_TYPE_GAME = 1;//游戏牌局结束
    public final static int NOTIFICATION_TYPE_TIP = 2;//提示
    public final static int NOTIFICATION_TYPE_APP_MSG = 3;//APP消息
    public final static int NOTIFICATION_TYPE_HORDE = 4;//部落相关消息  比如 申请加入部落 ==
//    public final static int NOTIFICATION_TYPE_GROUP_INVITE = 1001;//圈子邀请
}
