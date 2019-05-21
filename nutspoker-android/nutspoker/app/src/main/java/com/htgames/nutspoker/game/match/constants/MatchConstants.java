package com.htgames.nutspoker.game.match.constants;

/**
 */
public class MatchConstants {
    //message消息字段
    public static final String KEY_MATCH_TYPE = "type";//比赛消息类型
    public static final String KEY_MATCH_ROOM_ID = "room_id";//房间ID
    public static final String KEY_MATCH_FROM_ID = "from_id";//来源用户ID
    public static final String KEY_MATCH_TIME = "time";//消息发送时间
    public static final String KEY_MATCH_CONTENT = "content";//消息内容
    //状态
    public static final String KEY_INFO_CURRENT_SBLINDS_LEVEL = "curr_sblinds_index";//当前盲注结构等级
    public static final String KEY_INFO_CURRENT_PLAYER = "curr_left_players";//当前比赛人数
    public static final String KEY_INFO_ALL_REWARD = "all_reward";//当前总奖池
    public static final String KEY_INFO_MAX_CHIPS = "max_chips";//当前最大记分牌
    public static final String KEY_INFO_MIN_CHIPS = "min_chips";//当前最小记分牌
    public static final String KEY_INFO_AVG_CHIPS = "avg_chips";//当前平均记分牌
    public static final String KEY_INFO_MY_CHECKIN_STATUS = "checkin";//我的报名情况
    public static final String KEY_INFO_CHECKIN_PLAYER = "checkin_player";//报名人数
    public static final String KEY_INFO_BUY_TYPE = "buy_type";//买入状态（1.重购中  2.淘汰重构（复活）中 3.主动增购中 4.淘汰增购中 ）
    public static final String KEY_INFO_MATCH_STATUS = "match_status";//比赛状态
    public static final String KEY_INFO_GAME_STATUS = "game_status";//游戏状态
    public static final String KEY_INFO_SCORE = "score";//当前分数，为0既为淘汰
    public static final String KEY_INFO_IS_CONTROL = "is_control";//控制带入
    public static final String KEY_INFO_BEGIN_TIME = "begin_time";//比赛开始时间
    public static final String KEY_INFO_MATCH_IN_REST = "match_in_rest";//比赛是否在休息，1休息 0 正常
    public static final String KEY_INFO_MATCH_PAUSE_TIME = "match_pause_time";//比赛暂停结束时间
    public static final String KEY_INFO_MATCH_MANUL_BEGIN_GAME_SUCCESS = "start_time";//手动开赛成功
    public static final String KEY_INFO_MATCH_CHECKIN_TIME = "checkin_time";//checkin_time  mtt大厅点击"报名"成功后的时间戳
    //成员
    public static final String KEY_PLAYER_UID = "uid";//
    public static final String KEY_PLAYER_UUID = "uuid";//
    public static final String KEY_PLAYER_NICKNAME = "nickname";//当前排名
    public static final String KEY_PLAYER_CHIPS = "chips";//当前记分牌
    public static final String KEY_PLAYER_TABLE_NO = "index";//所在桌子列表
    public static final String KEY_PLAYER_RANK = "ranking";
    public static final String KEY_REBUY_CNT = "rebuy_cnt";//重购次数
    public static final String KEY_ADDON_CNT = "addon_cnt";//增购次数
    public static final String KO_HEAD_CNT = "khc";//mtt大厅玩家列表的人头数
    public static final String KO_HEAD_REWARD = "khr";//mtt大厅玩家列表的人头奖金
    public static final String KO_HEAD_WORTH = "kw";//mtt大厅玩家列表的人头奖金
    public static final String KEY_OPT_USER = "opt_user";//这个玩家是谁批准报名的，私人或者俱乐部
    public static final String KEY_OPT_USER_REAL = "opt_user_real";//这个玩家是谁批准报名的，私人或者俱乐部
    public static final String KEY_OPT_USER_TYPE = "opt_user_type";////这个玩家是谁批准报名的，0私人1俱乐部
    //牌桌
    public static final String KEY_TABLE_INDEX = "index";//牌桌号
    public static final String KEY_TABLE_COUNT = "count";//牌桌人数
    public static final String KEY_TABLE_MAX_CHIPS = "max_chips";//牌桌最大记分牌
    public static final String KEY_TABLE_MIN_CHIPS = "min_chips";//牌桌最小记分牌
//    //奖励(列表)
//    public static final String KEY_REWARD_RANK = "rank";//奖励排名
//    public static final String KEY_REWARD_PERCENT = "percent";//奖励比例
//    public static final String KEY_REWARD_REWARD = "reward";//奖励
//    //奖池
//    public static final String KEY_ALL_REWARD = "all_reward";//总奖池
//    public static final String KEY_REWARD_PLAYER = "reward_player";//奖励圈人数
    //type : 1.牌局状态 2.玩家 3.牌桌   4.奖励
//    public static final int TYPE_MATCH_INFO = 1;
//    public static final int TYPE_MATCH_PLAYER = 2;
//    public static final int TYPE_MATCH_TABLE = 3;
//    public static final int TYPE_MATCH_REWARD = 4;

    public static final int MATCH_IN_REST_BEFORE = 2;//暂停倒计时真正开始之前（需要这手牌结束才能真正倒计时）
    public static final int MATCH_IN_REST = 1;//比赛休息中,  暂停倒计时真正开始
    public static final int MATCH_IN_REST_NOT = 0;//
}
