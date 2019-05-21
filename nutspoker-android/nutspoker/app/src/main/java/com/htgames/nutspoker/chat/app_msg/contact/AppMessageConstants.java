package com.htgames.nutspoker.chat.app_msg.contact;

import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 20150726 on 2016/4/21.
 */
public class AppMessageConstants {
    public static String KEY_FROM_ID = "fromid";
    public static String KEY_TARGET_ID = "targetid";
    public static String KEY_TYPE = "type";
    public static String KEY_TIME = "time";
    public static String KEY_CONTENT = "content";
    public static String KEY_INFO = "info";
    public static String KEY_STATUS = "status";
    //控制买入
    public static String KEY_BUY_GAME = "game";
    public static String KEY_BUY_GAME_ID = "gid";
    public static String KEY_BUY_GAME_NAME = "name";
    public static String KEY_BUY_GAME_CODE = "code";
    public static String KEY_BUY_GAME_TYPE = "type";
    public static String KEY_BUY_GAME_MODE = "game_mode";
    public static String KEY_BUY_GAME_CREATE_TIME = "create_time";
    //
    public static String KEY_BUY_USER = "user";
    public static String KEY_BUY_USER_ID = "id";
    public static String KEY_BUY_USER_UUID = "uuid";
    public static String KEY_BUY_USER_NICKNAME = "nickname";
    public static String KEY_BUY_USER_MANAGER_NICKNAME = "manager_nickname";
    public static String KEY_BUY_USER_AVATAR = "avatar";
    public static String KEY_BUY_STARTTIME = "starttime";
    public static String KEY_BUY_TIMEOUT = "timeout";
    public static String KEY_BUY_WIN_CHIPS = "win_chips";
    public static String KEY_BUY_BUY_CHIPS = "buy_chips";
    public static String KEY_BUY_TOTAL_BUY_CHIPS = "total_buy";
    public static String KEY_BUY_NODENAME = "nodename";
    //比赛模式控制带入
    public static String KEY_BUY_BUY_TYPE = "buy_type";//买入类型
    public static String KEY_BUY_REBUY_CNT = "rebuy_cnt";//重购次数
    public static String KEY_BUY_RESULT_CODE = "result_code";//返回CODE
    public static String KEY_BUY_RESULT_MSG = "result_msg";//返回消息
    //
    public static String KEY_MATCH_BUY_ACTION_STATUS = "action_status";
    //系统公告
    public static String KEY_NOTICE_ID = "id";
    public static String KEY_NOTICE_TITLE = "title";
    public static String KEY_NOTICE_CONTENT = "content";
    public static String KEY_NOTICE_SHOW_IMAGE = "image";
    public static String KEY_NOTICE_URL = "url";
    //牌局状态变化
    public static String KEY_GAMEOVER_GID = GameConstants.KEY_GAME_GID;
    public static String KEY_GAMEOVER_NAME = GameConstants.KEY_GAME_NAME;
    public static String KEY_GAMEOVER_CODE = GameConstants.KEY_GAME_CODE;
    public static String KEY_GAMEOVER_CREATOR_ID = GameConstants.KEY_GAME_CREATOR_ID;//牌局创建者
    public static String KEY_GAMEOVER_TEAMID = GameConstants.KEY_GAME_TEADID;
    public static String KEY_GAMEOVER_STATUS = GameConstants.KEY_GAME_STATUS;
    public static String KEY_GAMEOVER_TYPE = GameConstants.KEY_GAME_TYPE;
    public static String KEY_GAMEOVER_MODE = GameConstants.KEY_GAME_MODE;
    public static String KEY_GAMEOVER_WINCHIPS = "winchips";//盈利
    public static String KEY_GAMEOVER_REWARD = "reward";//奖金
    public static String KEY_GAMEOVER_RANKING = "ranking";//排名
    public static String KEY_GAMEOVER_BALANCE = "balance";//返还余额（1.普通模式是余额  2.比赛模式是报名费）
    public static String KEY_GAMEOVER_START_TYPE = "start_type";//开始类型（0正常 1即将开始）
    public static String KEY_GAMEOVER_END_TYPE = "end_type";//结束类型（0正常 1房主结束 2超时结束）

    public static int TYPE_GAMEOVER_END_NORMAL = 0;//结束类型（0正常 1房主结束 2超时结束）
    public static int TYPE_GAMEOVER_END_BY_CREATOR = 1;//结束类型（0正常 1房主结束 2超时结束）
    public static int TYPE_GAMEOVER_END_TIMEOUT = 2;//结束类型（0正常 1房主结束 2超时结束）
    public static int TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH = 3;//结束类型（0正常 1房主结束 2超时结束 3人数不齐）

    public static int MESSAGE_STATUS_UNREAD = 1;
    public static int MESSAGE_STATUS_READED = 0;

    /**
     * 获取MT模式买入数据库KEY
     * @param gameCode
     * @param playerId
     * @return
     */
    public static String getMatchBuyChipsDBKey(String gameCode , String playerId) {
        return gameCode + "-" + playerId;
    }
}
