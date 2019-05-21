package com.htgames.nutspoker.net;

/**
 * 请求限制
 */
public class RequestTimeLimit {
    public static int GET_AMONT_TIME_LIMIT = 10;
    public static int GET_STATISTICAL_TIME_LIMIT = 60 * 5;
    public static int GET_GAME_PLAYING_TIME_LIMIT = 60 * 10;//five minutes
    public static int GET_GAME_RECENT_TIME_LIMIT = 60;
    public static long lastGetAmontTime = 0;//最后次获取金币时间
    public static long lastGetStatisticalTime = 0;//最后次获取掌机时间
    public static long lastGetGamePlayingTime = 0;//最后次请求列表  俱乐部 列表上面显示的数目
    public static long lastGetRecentGameTime = 0;//最后次请求最近游戏列表

    public static long lastGetGameListInClub = 0;//请求俱乐部正在进行的游戏详情
    public static int GET_GAME_LIST_IN_CLUB_LIMIT = 10;

    public static long lastGetGameListInHorde = 0;//请求部落正在进行的游戏详情
    public static int GET_GAME_LIST_IN_HORDE_LIMIT = 10;

    public static long lastTeamRecord = 0;//请求俱乐部战绩
    public static int GET_TEAM_RECORD = 10;

    public static long lastHordeRecord = 0;//请求俱乐部战绩
    public static int GET_HORDE_RECORD = 10;

    public static long lastGetLocationTime = 0;//获取位置信息频率
    public static int GET_LOCATION_RECORD = 10 * 60 * 60 * 1000;//10分钟请求一次位置信息
    /**
     * 重置获取上次时间限制
     */
    public static void resetGetTime() {
        lastGetAmontTime = 0;
        lastGetStatisticalTime = 0;
        lastGetGamePlayingTime = 0;
        lastGetRecentGameTime = 0;
        lastGetGameListInClub = 0;
    }
}
