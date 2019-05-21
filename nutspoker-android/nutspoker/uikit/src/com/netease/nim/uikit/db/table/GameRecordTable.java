package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

import com.netease.nim.uikit.constants.GameConstants;


/**
 * 牌局信息
 */
public class GameRecordTable implements BaseColumns {
    public static String TABLE_GAME_RECORD = "gamerecord";//数据分析
    //
    public static String COLUMN_GAME_NAME = "name";
    public static String COLUMN_GAME_TEADID = "tid";
    public static String COLUMN_GAME_GID = "gid";
    public static String COLUMN_GAME_CODE = "code";
    public static String COLUMN_GAME_TYPE = "type";
    public static String COLUMN_GAME_STATUS = "status";
    public static String COLUMN_GAME_PLAY_MODE = "play_mode";
    //
    public static String COLUMN_GAME_SMALL_BLINDS = "sblinds";
    public static String COLUMN_GAME_DURATIONS = "durations";
    public static String COLUMN_GAME_MODE_PUBLIC = "public_mode";
    public static String COLUMN_GAME_ANTE = "ante";
    public static String COLUMN_GAME_MODE_ANTE = "ante_mode";
    public static String COLUMN_GAME_MODE_TILT = "tilt_mode";
    //
    public static String COLUMN_GAME_CREATE_TIME = "create_time";
    public static String COLUMN_GAME_CREATOR_ID = "owner";
    //
    public static String COLUMN_GAME_END_TIME = "end_time";//结束时间
    //
    public static String COLUMN_GAME_MAX_POT = "max_pot";
    public static String COLUMN_GAME_ALL_BUYS = "all_buys";
    public static String COLUMN_GAME_BOUNTS = "bouts";
    public static String COLUMN_GAME_WIN_CHIPS = "win_chips";
    public static String COLUMN_GAME_MVP = "mvp";
    public static String COLUMN_GAME_FISH = "fish";
    public static String COLUMN_GAME_RICHEST = "richest";
    public static String COLUMN_GAME_MEMBERS = "members";
    //比赛
    public static String COLUMN_GAME_MODE = "game_mode";//牌局模式
    public static String COLUMN_MATCH_TYPE = "match_type";//mtt比赛模式：0金币赛1钻石赛
    public static String COLUMN_GAME_MATCH_CHIPS = "match_chips";//
    public static String COLUMN_GAME_MATCH_PLAYER = "match_player";//
    public static String COLUMN_GAME_MATCH_DURATION = "match_duration";//
    public static String COLUMN_GAME_MATCH_CHECKIN_FEE = "match_checkin_fee";//参赛费用
    public static String COLUMN_GAME_ALL_REWARD = "all_reward";//总奖池
    public static String COLUMN_GAME_TOTAL_TIME = "total_time";//总时间
    public static String COLUMN_GAME_MY_UID = "my_uid";//牌局参与者
    //MTT
    public static String COLUMN_GAME_TOTAL_PLAYER = GameConstants.KEY_GAME_TOTAL_PLAYER;//牌局总人数
    public static String COLUMN_GAME_SBLINDS_INDEX = GameConstants.KEY_GAME_SBLINDS_INDEX;//牌局结束时候盲注界别
    public static String COLUMN_GAME_KO_MODE = GameConstants.KEY_GAME_KO_MODE;//牌局结束时候盲注界别
    public static String COLUMN_GAME_KO_REWARD_RATE = GameConstants.KEY_GAME_KO_REWARD_RATE;//牌局结束时候盲注界别
    public static String COLUMN_GAME_KO_HEAD_RATE = GameConstants.KEY_GAME_KO_HEAD_RATE;//牌局结束时候盲注界别
    //
    public static String COLUMN_IS_PARTICIPATION = "isParticipation";//是否参与

    public static String COLUMN_GAME_CONFIG = "game_config";//是否参与
    public static String COLUMN_EXTEND_ONE = "extend_one";//扩展字段1
    public static String COLUMN_EXTEND_TWO = "extend_two";//扩展字段2
}
