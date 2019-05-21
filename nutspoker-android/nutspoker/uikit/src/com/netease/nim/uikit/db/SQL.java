package com.netease.nim.uikit.db;

import com.netease.nim.uikit.db.table.AppMsgTable;
import com.netease.nim.uikit.db.table.CardGamesTable;
import com.netease.nim.uikit.db.table.ContactTable;
import com.netease.nim.uikit.db.table.DataAnalysisTable;
import com.netease.nim.uikit.db.table.DataStatisticsTable;
import com.netease.nim.uikit.db.table.GameAudioTable;
import com.netease.nim.uikit.db.table.GameRecordTable;
import com.netease.nim.uikit.db.table.HandsCollectTable;
import com.netease.nim.uikit.db.table.HordeTable;
import com.netease.nim.uikit.db.table.NetCardCollectTable;
import com.netease.nim.uikit.db.table.NetCardRecordTable;
import com.netease.nim.uikit.db.table.RecordEntityTable;
import com.netease.nim.uikit.db.table.SystemMsgTable;

/**
 * Created by 20150726 on 2016/1/25.
 */
public class SQL {
    /**
     * 创建联系人表
     */
    public static final String CREATE_TABLE_CONTACT = "CREATE TABLE IF NOT EXISTS "
            + ContactTable.TABLE_CONTACT
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ContactTable.COLUMN_CONTACT_UID + " TEXT , "
            + ContactTable.COLUMN_CONTACT_OS + " INTEGER , "
            + ContactTable.COLUMN_CONTACT_PHONE + " LONG )";

    /**
     * 创建数据分析表
     */
    public static final String CREATE_TABLE_DATA_ANALYSIS = "CREATE TABLE IF NOT EXISTS "
            + DataAnalysisTable.TABLE_DATA_ANALYSIS
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataAnalysisTable.COLUMN_DATA_WSD + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_WTSD + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_WWSF + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_AF + " FLOAT , "
            + DataAnalysisTable.COLUMN_DATA_AFQ + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_VPIP + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_PFR + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_PFR_VPIP + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_THREE_BET + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_ATT + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_FOLD_STL + " INTEGER , "
            + DataAnalysisTable.COLUMN_DATA_SB + " FLOAT , "
            + DataAnalysisTable.COLUMN_DATA_BB + " FLOAT )";

    /**
     * 创建数据统计表
     */
    public static final String CREATE_TABLE_DATA_STATISTICS = "CREATE TABLE IF NOT EXISTS "
            + DataStatisticsTable.TABLE_DATA_STATISTICS
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DataStatisticsTable.COLUMN_DATA_GAMES + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_HANDS + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_HANDS_COUNT_WON + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_WSD + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_WWSF + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_WSD_AFTER + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_MY_C_WON + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_HUNDRED_HANDS_WIN + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_BIG_BLIND_WON + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_VPIP + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_WAPTMP + " INTEGER , "
            + DataStatisticsTable.COLUMN_DATA_ALLIN_CHIPS + " INTEGER ,"
            + DataStatisticsTable.COLUMN_DATA_TYPE + " INTEGER"
            +")";

    /**
     * 创建系统消息表
     */
    public static final String CREATE_TABLE_SYSTEM_MESSAGE = "CREATE TABLE IF NOT EXISTS "
            + SystemMsgTable.TABLE_SYSTEM_MSG
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SystemMsgTable.COLUMN_TARGET_ID + " Varchar(32) , "
            + SystemMsgTable.COLUMN_MESSAGEID + " INTEGER , "
            + SystemMsgTable.COLUMN_FROMID + " Varchar(32) , "
            + SystemMsgTable.COLUMN_TYPE + " INTEGER , "
            + SystemMsgTable.COLUMN_TIME + " LONG , "
            + SystemMsgTable.COLUMN_STATUS + " INTEGER , "
            + SystemMsgTable.COLUMN_CONTENT + " TEXT , "
            + SystemMsgTable.COLUMN_ATTACH + " TEXT , "
            + SystemMsgTable.COLUMN_UNREAD + " INTEGER )";

    /**
     * 创建APP系统消息表
     */
    public static final String CREATE_TABLE_APP_MESSAGE = "CREATE TABLE IF NOT EXISTS "
            + AppMsgTable.TABLE_APP_MSG
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AppMsgTable.COLUMN_TARGET_ID + " Varchar(32) , "
            + AppMsgTable.COLUMN_MESSAGEID + " INTEGER , "
            + AppMsgTable.COLUMN_FROMID + " Varchar(32) , "
            + AppMsgTable.COLUMN_CHECKIN_PLAYER_ID + " Varchar(32) , "
            + AppMsgTable.COLUMN_TYPE + " INTEGER , "
            + AppMsgTable.COLUMN_TIME + " LONG , "
            + AppMsgTable.COLUMN_STATUS + " INTEGER , "
            + AppMsgTable.COLUMN_CONTENT + " TEXT , "
            + AppMsgTable.COLUMN_ATTACH + " TEXT , "
            + AppMsgTable.COLUMN_KEY + " TEXT , "
            + AppMsgTable.COLUMN_SORT_KEY + " LONG , "//v3:排序字段
            + AppMsgTable.COLUMN_UNREAD + " INTEGER )";

    /**
     * 创建牌局信息表
     */
    public static final String CREATE_TABLE_GAME_RECORD = "CREATE TABLE IF NOT EXISTS "
            + GameRecordTable.TABLE_GAME_RECORD
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GameRecordTable.COLUMN_GAME_NAME + " TEXT , "
            + GameRecordTable.COLUMN_GAME_TEADID + " TEXT , "
            + GameRecordTable.COLUMN_GAME_GID + " TEXT , "
            + GameRecordTable.COLUMN_GAME_CODE + " TEXT , "
            + GameRecordTable.COLUMN_GAME_TYPE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_STATUS + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_SMALL_BLINDS + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_DURATIONS + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MODE_PUBLIC + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MODE_ANTE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_ANTE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MODE_TILT + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_CREATE_TIME + " LONG , "
            + GameRecordTable.COLUMN_GAME_CREATOR_ID + " TEXT , "
            + GameRecordTable.COLUMN_GAME_MAX_POT + " TEXT , "
            + GameRecordTable.COLUMN_GAME_ALL_BUYS + " TEXT , "
            + GameRecordTable.COLUMN_GAME_BOUNTS + " TEXT , "
            + GameRecordTable.COLUMN_GAME_WIN_CHIPS + " TEXT , "
            + GameRecordTable.COLUMN_GAME_MVP + " TEXT , "
            + GameRecordTable.COLUMN_GAME_FISH + " TEXT , "
            + GameRecordTable.COLUMN_GAME_RICHEST + " TEXT , "
            + GameRecordTable.COLUMN_IS_PARTICIPATION + " BOOLEAN , "
            //添加了新字段
            + GameRecordTable.COLUMN_GAME_END_TIME + " LONG , "
            + GameRecordTable.COLUMN_GAME_MODE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MY_UID + " TEXT , "
            + GameRecordTable.COLUMN_GAME_MATCH_PLAYER + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MATCH_DURATION + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MATCH_CHIPS + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_MATCH_CHECKIN_FEE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_ALL_REWARD + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_TOTAL_TIME + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_PLAY_MODE + " INTEGER , "
            + GameRecordTable.COLUMN_MATCH_TYPE + " INTEGER , "
            //MT
            + GameRecordTable.COLUMN_GAME_TOTAL_PLAYER + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_SBLINDS_INDEX + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_KO_MODE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_KO_REWARD_RATE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_KO_HEAD_RATE + " INTEGER , "
            + GameRecordTable.COLUMN_GAME_CONFIG + " TEXT , "
            + GameRecordTable.COLUMN_EXTEND_ONE + " TEXT , "
            + GameRecordTable.COLUMN_EXTEND_TWO + " TEXT , "
            + GameRecordTable.COLUMN_GAME_MEMBERS + " TEXT )";

    /**
     * 创建牌谱收藏表
     */
    public static final String CREATE_TABLE_HANDS_COLLECT = "CREATE TABLE IF NOT EXISTS "
            + HandsCollectTable.TABLE_HANDS_COLLECT
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HandsCollectTable.COLUMN_GAME_GID + " TEXT , "
            + HandsCollectTable.COLUMN_HANDS_CNT + " INTEGER , "
            + HandsCollectTable.COLUMN_HANDS_ID + " TEXT , "
            + HandsCollectTable.COLUMN_FILE_NAME + " TEXT , "
            + HandsCollectTable.COLUMN_FILE_NET_PATH + " TEXT , "
            + HandsCollectTable.COLUMN_GAME_CREATE_TIME + " LONG , "
            + HandsCollectTable.COLUMN_HAND_COLLECT_TIME + " LONG , "
            + HandsCollectTable.COLUMN_DATA + " TEXT )";

    /**
     * 创建新的战绩记录
     */

    /**
     * 创建新版战绩首页数据
     */
    public static final String CREATE_TABLE_RECORD_INDEX = "CREATE TABLE IF NOT EXISTS "
            + RecordEntityTable.TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RecordEntityTable.vpip + " INT,"
            + RecordEntityTable.waptmp + " INT,"
            + RecordEntityTable.hands + " INT,"
            + RecordEntityTable.my_c_won + " INT,"
            + RecordEntityTable.my_c_won_sng_mtt + " INT,"
            + RecordEntityTable.my_c_won_sng + " INT,"
            + RecordEntityTable.my_c_won_mtt + " INT,"
            + RecordEntityTable.games + " INT,"
            + RecordEntityTable.games_sng_mtt + " INT,"
            + RecordEntityTable.games_sng + " INT,"
            + RecordEntityTable.games_mtt + " INT,"
            + RecordEntityTable.hands_won_cnt + " INT,"
            + RecordEntityTable.hands_count_won + " INT,"
            //保险数据
            + RecordEntityTable.trigger_count + " INT,"
            + RecordEntityTable.hit_count + " INT,"
            + RecordEntityTable.buy_count + " INT,"
            + RecordEntityTable.buy_sum + " INT,"
            + RecordEntityTable.pay_sum + " INT,"
            + RecordEntityTable.my_games + " INT,"
            + RecordEntityTable.my_buy_sum + " INT,"
            + RecordEntityTable.my_pay_sum + " INT,"
            //比赛数据
            + RecordEntityTable.match_fee_mtt + " INT,"
            + RecordEntityTable.match_fee_sng + " INT,"
            + RecordEntityTable.match_fee_sng_mtt + " INT,"
            + RecordEntityTable.reward_mtt + " INT,"
            + RecordEntityTable.reward_sng + " INT,"
            + RecordEntityTable.reward_sng_mtt + " INT,"
            + RecordEntityTable.in_reward_mtt + " INT,"
            + RecordEntityTable.in_reward_sng + " INT,"
            + RecordEntityTable.in_reward_sng_mtt + " INT,"
            + RecordEntityTable.in_finals_mtt + " INT,"
            + RecordEntityTable.in_finals_sng + " INT,"
            + RecordEntityTable.in_finals_sng_mtt + " INT"
            + ")";

    /**
     * 创建新版牌谱记录表
     */
    public static final String CREATE_TABLE_NETCARDRECORD = "CREATE TABLE IF NOT EXISTS "
            + NetCardRecordTable.TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NetCardRecordTable.pool_cards + " TEXT , "//json
            + NetCardRecordTable.code + " TEXT , "
            + NetCardRecordTable.win_chips + " INT , "
            + NetCardRecordTable.card_type + " INT , "
            + NetCardRecordTable.time + " BIGINT , "
            + NetCardRecordTable.gid + " TEXT , "
            + NetCardRecordTable.cardtype_cards + " TEXT , "//json
            + NetCardRecordTable.hand_cards + " TEXT,"//json
            + NetCardRecordTable.hands_cnt + " INT,"
            + NetCardRecordTable.id + " TEXT,"
            + NetCardRecordTable.is_collect + " INT"
            + ")";

    /**
     * 创建牌局信息表
     */
    public static final String CREATE_TABLE_CARDGAMEINFO = "CREATE TABLE IF NOT EXISTS "
            + CardGamesTable.TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CardGamesTable.gid + " TEXT , "
            + CardGamesTable.tid + " TEXT , "
            + CardGamesTable.name + " TEXT , "
            + CardGamesTable.code + " TEXT , "
            + CardGamesTable.blinds + " INT , "
            + CardGamesTable.sblinds + " INT , "
            + CardGamesTable.duration + " INT , "
            + CardGamesTable.owner + " TEXT , "
            + CardGamesTable.type + " INT ,"
            + CardGamesTable.public_mode + " INT , "
            + CardGamesTable.ante_mode + " INT , "
            + CardGamesTable.tilt_mode + " INT , "
            + CardGamesTable.status + " INT , "
            + CardGamesTable.create_time + " BIGINT , "
            + CardGamesTable.total_player + " INT , "
            + CardGamesTable.server + " TEXT , "
            + CardGamesTable.game_mode + " INT ,"
            + CardGamesTable.match_chips + " INT , "
            + CardGamesTable.match_player + " INT , "
            + CardGamesTable.match_duration + " INT , "
            + CardGamesTable.end_time + " BIGINT , "
            + CardGamesTable.match_checkin_fee + " INT , "
            + CardGamesTable.room_id + " TEXT"
            +")";

    /**
     * 创建新版收藏列表
     */
    public static final String CREATE_TABLE_CARDCOLLECT = "CREATE TABLE IF NOT EXISTS "
            + NetCardCollectTable.TABLE_NAME
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NetCardCollectTable.id + " TEXT , "
            + NetCardCollectTable.hid + " TEXT , "
            + NetCardCollectTable.uid + " TEXT ,"
            + NetCardCollectTable.collect_time + " BIGINT,"
            + NetCardCollectTable.file_path + " TEXT,"
            + NetCardCollectTable.file_name + " TEXT,"
            + NetCardCollectTable.pool_cards + " TEXT,"//json
            + NetCardCollectTable.code + " TEXT,"
            + NetCardCollectTable.win_chips + " INT,"
            + NetCardCollectTable.card_type + " INT,"
            + NetCardCollectTable.time + " BIGINT,"
            + NetCardCollectTable.gid + " TEXT , "
            + NetCardCollectTable.cardtype_cards + " TEXT,"//json
            + NetCardCollectTable.hand_cards + " TEXT,"//json
            + NetCardCollectTable.hands_cnt + " INT,"
            + NetCardCollectTable.count + " INT,"
            + NetCardCollectTable.tid + " TEXT , "
            + NetCardCollectTable.owner + " TEXT , "
            + NetCardCollectTable.name + " TEXT , "
            + NetCardCollectTable.blinds + " INT , "
            + NetCardCollectTable.sblinds + " INT , "
            + NetCardCollectTable.duration + " INT , "
            + NetCardCollectTable.durations + " INT , "
            + NetCardCollectTable.tilt_mode + " INT , "
            + NetCardCollectTable.type + " INT ,"
            + NetCardCollectTable.status + " INT , "
            + NetCardCollectTable.public_mode + " INT , "
            + NetCardCollectTable.ante_mode + " INT , "
            + NetCardCollectTable.ante + " INT,"
            + NetCardCollectTable.create_time + " BIGINT , "
            + NetCardCollectTable.bouts + " INT,"
            + NetCardCollectTable.game_mode + " INT ,"
            + NetCardCollectTable.match_chips + " INT , "
            + NetCardCollectTable.match_player + " INT , "
            + NetCardCollectTable.match_duration + " INT , "
            + NetCardCollectTable.match_checkin_fee + " INT"
            +")";

    /**
     * 创建游戏语音缓存表
     */
    public static final String CREATE_TABLE_GAMEAUDIO = "CREATE TABLE IF NOT EXISTS "
            + GameAudioTable.TABLE_GAME_AUDIO
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + GameAudioTable.COLUMN_AUDIO_PATH + " TEXT , "
            + GameAudioTable.COLUMN_AUDIO_TIME + " LONG )";

    /**
     * 创建部落相关的表
     */
    public static final String CREATE_TABLE_HORDE = "CREATE TABLE IF NOT EXISTS "
            + HordeTable.TABLE_HORDE_MSG
            + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HordeTable.COLUMN_TARGET_ID + " Varchar(32) , "
            + HordeTable.COLUMN_FROMID + " Varchar(32) , "
            + HordeTable.COLUMN_OUTER_TYPE + " INTEGER , "
            + HordeTable.COLUMN_INNER_TYPE + " INTEGER , "
            + HordeTable.COLUMN_TIME + " LONG , "
            + HordeTable.COLUMN_STATUS + " INTEGER , "
            + HordeTable.COLUMN_CONTENT + " TEXT , "
            + HordeTable.COLUMN_ATTACH + " TEXT , "
            + HordeTable.COLUMN_KEY + " TEXT, "
            + HordeTable.COLUMN_TID + " TEXT, "
            + HordeTable.COLUMN_TNAME + " TEXT, "
            + HordeTable.COLUMN_TAVATAR + " TEXT, "
            + HordeTable.COLUMN_HORDE_ID + " TEXT, "
            + HordeTable.COLUMN_HORDE_NAME + " TEXT, "
            + HordeTable.COLUMN_UNREAD + " INTEGER )";
}
