package com.htgames.nutspoker.config;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * 创建游戏的配置表
 */
public class GameConfigData {
    public static int TEAM_NUM;//俱乐部管理员上限
    //ante四个index的前乘
    public static int[] anteMode = new int[]{GameConstants.ANTE_TYPE_0, GameConstants.ANTE_TYPE_1, GameConstants.ANTE_TYPE_2, GameConstants.ANTE_TYPE_3};
    /** 时间的分钟形式 */
    public static int[] blindInts = new int[]{1, 2, 5, 10, 20, 25, 50, 100, 500};
    public static int[] chipsNum = new int[]{200, 400, 1000, 2000, 5000, 10000, 20000, 100000};
    public static int minChipsIndexNum = 10;
    public static int maxChipsIndexNum = 11;
    public static int totalChipsIndexNum = 21;
    public static int[][] antes = new int[100][];
    public static int[] anteInts = new int[]{1, 1, 2, 5, 10, 15, 25};
    public static int[] durationMinutes = new int[]{30, 60, 90, 120, 180, 240, 360};
    /*sng_ante_multiple   sng_sblinds_multiple  sng_sblins   sng盲注结构表  1普通  2快速*/
    public static int[] sng_sblins = new int[200];//起始值 倍数
    public static float[] sng_sblinds_multiple = new float[200];
    public static float[] sng_ante_multiple = new float[200];
    /*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通  2快速*/
    public static int[] mtt_sblins = new int[]{30, 40, 50, 60, 70, 80, 90, 100};//起始值 倍数    //单词少个d, 没事
    public static float[] mtt_sblinds_multiple = new float[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 16, 20, 24, 32, 40, 60, 80, 120, 160, 200, 240, 320, 400, 600, 800, 1200, 1600, 2000, 2400, 3200, 4000, 6000, 8000, 12000, 16000, 20000, 24000, 32000, 40000};
    public static float[] mtt_ante_multiple = new float[]{0, 0, 0, 0, 1, 1, 2, 2, 3, 4, 5, 6, 8, 10, 12, 20, 24, 40, 52, 64, 80, 100, 120, 200, 260, 400, 520, 600, 800, 1040, 1200, 2000, 2400, 3600, 4800, 6000, 7200, 9600, 12000};
    //mtt快速表=2
    public static int[] mtt_sblins_quick = new int[]{10, 20, 25, 30, 35, 40, 45, 50, 55};//起始值 倍数  //单词少个d, 没事
    public static float[] mtt_sblinds_multiple_quick = new float[]{1, 2, 4, 8, 12, 16, 20, 32, 40, 60, 80, 120, 160, 200, 320, 400, 600, 800, 1200, 1600, 2000, 3200, 4000, 6000, 8000, 12000, 16000, 20000, 32000, 40000};
    public static float[] mtt_ante_multiple_quick = new float[]{0, 0, 0, 0, 0, 0, 4, 8, 12, 20, 20, 40, 60, 60, 80, 120, 200, 200, 400, 600, 600, 800, 1200, 2000, 2000, 4000, 6000, 6000, 8000, 12000};//0, 0, 0, 0, 0, 0, 4, 8, 12, 20, 20, 40, 60, 60, 80, 120, 200, 200, 400, 600, 600, 800, 1200, 2000, 2000, 4000, 6000, 6000, 8000, 12000
    //"奥马哈"盲注结构表配置---------------------------------------------"奥马哈"盲注结构表配置---------------------------------------------
    /*sng_ante_multiple   sng_sblinds_multiple  sng_sblins   sng盲注结构表  1普通  2快速*/
    public static int[] omaha_sng_sblins = new int[200];//起始值 倍数
    public static float[] omaha_sng_sblinds_multiple = new float[200];
    public static float[] omaha_sng_ante_multiple = new float[200];
    /*mtt_ante_multiple   mtt_sblinds_multiple  mtt_sblins   mtt盲注结构表  1普通  2快速*/
    public static int[] omaha_mtt_sblins = new int[]{30, 40, 50, 60, 70, 80, 90, 100};//起始值 倍数    //单词少个d, 没事
    public static float[] omaha_mtt_sblinds_multiple = new float[]{1, 2, 3, 4, 5, 6, 7, 8, 10, 12, 16, 20, 24, 32, 40, 60, 80, 120, 160, 200, 240, 320, 400, 600, 800, 1200, 1600, 2000, 2400, 3200, 4000, 6000, 8000, 12000, 16000, 20000, 24000, 32000, 40000};
    public static float[] omaha_mtt_ante_multiple = new float[]{0, 0, 0, 0, 1, 1, 2, 2, 3, 4, 5, 6, 8, 10, 12, 20, 24, 40, 52, 64, 80, 100, 120, 200, 260, 400, 520, 600, 800, 1040, 1200, 2000, 2400, 3600, 4800, 6000, 7200, 9600, 12000};
    //mtt快速表=2
    public static int[] omaha_mtt_sblins_quick = new int[]{10, 20, 25, 30, 35, 40, 45, 50, 55};//起始值 倍数  //单词少个d, 没事
    public static float[] omaha_mtt_sblinds_multiple_quick = new float[]{1, 2, 4, 8, 12, 16, 20, 32, 40, 60, 80, 120, 160, 200, 320, 400, 600, 800, 1200, 1600, 2000, 3200, 4000, 6000, 8000, 12000, 16000, 20000, 32000, 40000};
    public static float[] omaha_mtt_ante_multiple_quick = new float[]{};//0, 0, 0, 0, 0, 0, 4, 8, 12, 20, 20, 40, 60, 60, 80, 120, 200, 200, 400, 600, 600, 800, 1200, 2000, 2000, 4000, 6000, 6000, 8000, 12000
    //"奥马哈"盲注结构表配置---------------------------------------------"奥马哈"盲注结构表配置---------------------------------------------
    //mtt参赛人数上限
    public static int[] mtt_checkin_limit = new int[]{7,10,20,30,40,50,100,200,300,400,500,1000,2000,3000};
    //mtt重构次数  以前版本就是个boolean开关表示是否允许重构
    public static int[] mtt_rebuy_count = new int[]{0, 1, 2, 3, 4, 5};
    //赏金比例
    public static int[] ko_reward_rate = new int[]{1, 2, 5, 8, 10, 15, 18, 20, 25, 28, 30, 32, 35, 38, 40, 42, 45, 50, 55, 60};
    //人头分成
    public static int[] ko_head_rate = new int[]{1, 2, 5, 8, 10, 15, 18, 20, 25, 28, 30, 32, 35, 38, 40, 42, 45, 50, 55, 60};
    //mtt桌子数
    public static int[] mtt_desk_num = new int[]{6, 9};
    public static int[] mtt_desk_num_omaha = new int[]{6, 7, 8, 9};





    //台湾版本
    public static int[] blindInts_tw = new int[]{1, 2, 5, 10, 25, 50, 100, 200, 500};
    public static int[] chipsNum_tw = new int[]{200, 400, 1000, 2000, 5000, 10000, 20000, 40000, 100000};
    public static int[] anteInts_tw = new int[]{1, 1, 2, 5, 10, 15, 25, 50, 100};

    //获取普通模式的小盲
    public static int[] getNormalSBlinds() {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            return blindInts_tw;
        } else {
            return blindInts;
        }
    }

    //获取普通模式的带入记分牌
    public static int[] getNormalChips() {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            return chipsNum_tw;
        } else {
            return chipsNum;
        }
    }

    //获取普通模式的ANTE
    public static int[] getNormalAnte() {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            return anteInts_tw;
        } else {
            return anteInts;
        }
    }

    //获取普通模式的时间
    public static int[] getNormalDuration() {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            return durationMinutes;
        } else {
            return durationMinutes;
        }
    }

    //设置普通模式的小盲
    public static void setNormalSBlinds(int[] blinds) {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            blindInts_tw = blinds;
        } else {
            blindInts = blinds;
        }
    }

    //设置普通模式的带入记分牌
    public static void setNormalChips(int[] chips) {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            chipsNum_tw = chips;
        } else {
            chipsNum = chips;
        }
    }

    //设置普通模式的ANTE
    public static void setNormalAnte(int[] antes) {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            anteInts_tw = antes;
        } else {
            anteInts = antes;
        }
    }

    //设置普通模式的时间
    public static void setNormalDuration(int[] durations) {
        if (ApiConfig.AppVersion.isTaiwanVersion) {
            durationMinutes = durations;
        } else {
            durationMinutes = durations;
        }
    }

    //大菠萝相关配置------------------------------------------------------------------------------------------------------------------------------------------------------
    public static int[] pineappleIconIdsHistory = new int[]{R.mipmap.p_tags_normal, R.mipmap.p_tags_blood, R.mipmap.p_tags_blood_in_out, R.mipmap.p_tags_yoriko, R.mipmap.p_tags_yoriko, R.mipmap.p_tags_yoriko, R.mipmap.p_tags_yoriko};
    public static int[] pineappleIconIdsReady = new int[]{R.mipmap.p_tags_ready_1, R.mipmap.p_tags_ready_2, R.mipmap.p_tags_ready_3, R.mipmap.p_tags_ready_4, R.mipmap.p_tags_ready_4, R.mipmap.p_tags_ready_4, R.mipmap.p_tags_ready_4};
    public static int[] pineappleModeStrIds = new int[]{R.string.pineapple_mode_normal, R.string.pineapple_mode_blood, R.string.pineapple_mode_blood_in_out, R.string.pineapple_mode_yoriko, R.string.pineapple_mode_normal, R.string.pineapple_mode_normal};
    public static int[] pineappleModeStrIdsTeam = new int[]{R.string.pineapple_mode_normal, R.string.pineapple_mode_blood, R.string.pineapple_mode_blood_in_out, R.string.pineapple_mode_yoriko_team, R.string.pineapple_mode_normal, R.string.pineapple_mode_normal};
    public static int[] pineapple_antes = new int[]{};//2, 5, 10, 15, 20, 30, 50, 100
    public static int[][] pineapple_chips = new int[][]{};/*({{200, 400, 600, 800, 1000, 1500, 2000},
            {500, 1000, 1500, 2000, 3000, 4000, 5000},
            {1000, 2000, 3000, 4000, 6000, 8000, 10000},
            {1500, 3000, 4500, 6000, 9000, 12000, 15000},
            {2000, 4000, 6000, 8000, 10000, 15000, 20000},
            {3000, 6000, 9000, 12000, 18000, 24000, 30000},
            {5000, 10000, 15000, 20000, 30000, 40000, 50000},
            {10000, 20000, 30000, 40000, 60000, 80000, 100000}};//底注的每一档对应一个数组*/
    public static int[] pineapple_chips_limit_multiple = new int[]{};//数组内容是倍数, 倍数乘以底注就是最低带入局分0, 5, 10, 20, 30, 40, 50
    public static int[] pineapple_durations = new int[]{};//大菠萝每局持续时间30, 60, 90, 120, 180, 240, 360  / 60
    //大菠萝mtt配置
    public static int[] pineapple_mtt_fees = new int[]{};//常规赛报名费
    public static int[] pineapple_mtt_chips = new int[]{};//初始记分牌
    public static int[] pineapple_mtt_ante_time = new int[]{6, 7, 8, 9, 10, 12, 15, 20};//升底时间 服务端返回的单位是秒s
    public static int[] pineapple_mtt_checkin_limit = new int[]{6, 7, 8, 9, 10, 12, 15, 20};//大菠萝买入次数上限
    public static int[] pineapple_mtt_blind_level = new int[]{6, 8, 10 , 12 , 15};//终止报名级别
    public static int[] pineapple_mtt_ante = new int[]{6, 8, 10 , 12 , 15};//mtt大菠萝普通底注表
    public static int[] pineapple_mtt_ante_quick = new int[]{6, 8, 10 , 12 , 15};//mtt大菠萝快速底注表
    public static int[] pineapple_ko_reward_rate = new int[]{1, 2, 5, 8, 10, 15, 18, 20, 25, 28, 30, 32, 35, 38, 40, 42, 45, 50, 55, 60};//赏金比例
    public static int[] pineapple_ko_head_rate = new int[]{1, 2, 5, 8, 10, 15, 18, 20, 25, 28, 30, 32, 35, 38, 40, 42, 45, 50, 55, 60};//人头分成 累积赏金比例
    public static int[] pineapple_mtt_rebuy_count = new int[]{0, 1, 2 , 3 , 4, 5};//重构次数  这个服务端不返回  本地写死
    //钻石赛 的消耗 配置    这两个是统一配置的， 是共用的， type = 0
    public static int[] mtt_checkin_gold = new int[]{2, 3, 5, 7};//报名费 110, 220, 330, 440, 550, 660, 770, 880, 990, 1100
    public static int[] mtt_checkin_diamond = new int[]{};//报名费 110, 220, 330, 440, 550, 660, 770, 880, 990, 1100
    //共享部落的钻石消耗数组
    public static int[] create_game_fee = new int[]{};
    public static int[] create_sng_fee = new int[]{};
    public static int[] create_mtt_fee = new int[]{};
    public static int[] pineapple_fee = new int[]{};
    //服务费比例
    public static float normalServiceRate = 10;//默认百分之10
    public static float SNGServiceRate = 10;//默认百分之10
    public static float MTTServiceRate = 10;//默认百分之10
}
