package com.htgames.nutspoker.game.model;

/**
 * 0.报名
 * 1.主动重购
 * 2.淘汰重构（复活）
 * 3.主动增购
 * 4.淘汰增购
 */
public class GameMatchBuyType {
    public static int TYPE_BUY_CHECKIN = 0;
    public static int TYPE_BUY_REBUY = 1;//主动重购
    public static int TYPE_BUY_REBUY_WEEDOUT = 2;//淘汰重构（复活）
    public static int TYPE_BUY_ADDON = 3;//主动增购
    public static int TYPE_BUY_ADDON_WEEDOUT = 4;//淘汰增购

    public static int TYPE_BUY_REBUY_REVIVAL = 5;//复活重构
}
