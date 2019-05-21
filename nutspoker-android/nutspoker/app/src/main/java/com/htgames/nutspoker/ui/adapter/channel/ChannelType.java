package com.htgames.nutspoker.ui.adapter.channel;

/**
 * Created by 周智慧 on 17/3/7.
 */

public interface ChannelType {
    int club = 0;//俱乐部渠道  俱乐部管理员和会长创建的游戏算作是俱乐部渠道
    int personal = 1;//管理员渠道()   俱乐部外面创建的游戏
    int creator = 2;//房主渠道(局头-游戏创建者)  如果是俱乐部的普通成员创建的游戏，那么渠道类型算作是房主渠道而不是俱乐部渠道
}
