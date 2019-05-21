package com.htgames.nutspoker.game.config;

/**
 * Created by 20150726 on 2016/5/25.
 */
public class GameConfigConstants {
    /**  记分牌 */
    public static final String KEY_CHIPS = "chips";
    public static final String KEY_CHIPS_INDEX = "chips_index";//最小带入小档位个数   最大带入小档位个数   总带入小档位个数（默认10 11 21）
    /**  时间 */
    public static final String KEY_DURATION = "duration";
    /**  小盲 */
    public static final String KEY_SBLINDS = "sblinds";
    /**  ante */
    public static final String KEY_ANTE_INDEX = "ante_index";
    public static final String KEY_ANTE = "ante";//前注二维数组
    /** 盲注结构 */
    public static final String KEY_SBLINDS_STRUCT = "sblinds_struct";
    /** SNG 带入记分牌 */
    public static final String KEY_SNG_CHIPS = "sng_chips";
    /* sng普通的盲注结构表   3个数组  快速也是3个数组总共6个      mtt也是总共6个数组     sng_ante_multiple   sng_sblinds_multiple  sng_sblins*/
    public static final String KEY_SNG_ANTE_MULTIPLE = "sng_ante_multiple";
    public static final String KEY_SNG_SBLINDS_MULTIPLE = "sng_sblinds_multiple";
    public static final String KEY_SNG_SBLINS = "sng_sblinds";
    /* mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins  mtt的六个数组 */
    public static final String KEY_MTT_ANTE_MULTIPLE = "mtt_ante_multiple";
    public static final String KEY_MTT_SBLINDS_MULTIPLE = "mtt_sblinds_multiple";
    public static final String KEY_MTT_SBLINS = "mtt_sblinds";
    public static final String KEY_MTT_ANTE_MULTIPLE_QUICK = "mtt_ante_multiple_quick";
    public static final String KEY_MTT_SBLINDS_MULTIPLE_QUICK = "mtt_sblinds_multiple_quick";
    public static final String KEY_MTT_SBLINS_QUICK = "mtt_sblinds_quick";
    /* mtt参赛人数上限 */
    public static final String KEY_MTT_CHECKIN_LIMIT = "match_max_buy_cnt";
    /** SNG 涨盲时间 */
    public static final String KEY_SNG_DURATION = "sng_duration";
    /** SNG 报名费 */
    public static final String KEY_SNG_CHECKIN_FEE = "sng_checkin_fee";
    /** MTT盲注结构表 */
    public static final String KEY_MTT_SBLINDS_STRUCT = "mtt_sblinds_struct";
    /** MTT盲注ante结构表 */
    public static final String KEY_MTT_SBLINDS_ANTE_STRUCT = "mtt_sblinds_ante";
    /** MT-SNG盲注结构表 */
    public static final String KEY_MTSNG_SBLINDS_STRUCT = "mtsng_sblinds_struct";
    /** MT-SNG盲注ante结构表 */
    public static final String KEY_MTSNG_SBLINDS_ANTE_STRUCT = "mtsng_sblinds_ante";
    /** MTT初始记分牌 */
    public static final String KEY_MTT_CHIPS = "mtt_chips_num";
    /** MTT报名费 */
    public static final String KEY_MTT_CHECKIN_FEE = "mtt_checkin_fee_num";
    /** MTT终止参赛盲注等级 */
    public static final String KEY_MTT_BLIND_LEVEL = "mtt_blind_level_ints";
    public static final String KO_REWARD_RATE = "ko_reward_rate";//猎人赛赏金比例
    public static final String KO_HEAD_RATE = "ko_head_rate";//超级猎人赛比例
}
