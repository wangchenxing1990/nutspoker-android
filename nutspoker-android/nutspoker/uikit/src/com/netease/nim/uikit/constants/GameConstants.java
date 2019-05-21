package com.netease.nim.uikit.constants;

import android.text.TextUtils;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 */
public class GameConstants {
    public final static int MAX_SQL_PAGE_SIZE = 30;//sql数据库分页查询的每页数目
    public final static int MAX_GAMENAME_LENGTH = 20;//最大字符长度：牌局名称
    public final static int PLAY_MODE_TEXAS_HOLDEM = 0;//德州扑克
    public final static int PLAY_MODE_OMAHA = 1;//Omaha奥马哈
    public final static int PLAY_MODE_PINEAPPLE = 2;//大菠萝
    //    public final static  String[] blinds = new String[]{"1/2" ,"2/4" , "5/10" , "10/20" , "25/50" , "50/100" , "100/200"};
//    public final static  String[] chips = new String[]{"200" , "400" , "1000" , "2000" , "5000" , "10K" , "20K"};
//    public final static  String[] servicefeesShow = new String[]{"20" , "40" , "100" , "200" , "350" , "700","1,400"};
//    public final static  int[] servicefees = new int[]{20 , 40 , 100 , 200 , 350 , 700 ,1400};

    //SNG
    public final static  int[] sngPlayerInts = new int[]{2 ,3 , 4 , 5 , 6 , 7 , 8 , 9};
    //SNG推荐档位（对应位置）
    public final static  int[] sngRecommendBlindTimeGear = new int[]{0 , 0 , 0 , 0 , 1 , 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};//本来只有一个3
    public static int[] sngChipsNum = new int[]{600 , 1000 , 2000 , 3000 , 4000, 8000};
    public static int[] sngCheckInFeeNum = new int[]{60 , 100 , 200 ,300 , 400 , 800};
    public static int[] sngTimeMinutes = new int[]{3, 5, 7, 10, 15, 20};
    //SNG多桌模式人数
    public static int[] sngMatchPlayers = new int[]{2, 3 ,6 ,9 ,12, 16, 18, 27, 32, 45, 64, 90, 180, 240, 360, 990, 1000};
    //MTT档位
    public static  float[] mttChipsNum = new float[]{1000 , 1500, 2000, 3000, 4000, 8000};
    public static int[] mttCheckInFeeNum = new int[]{100, 150, 200, 300, 400, 800};
    public static int[] mttBlindLevelInts = new int[]{6, 8, 10 , 12 , 15};//盲注级别

//    public static int[] sblindsStructs = new int[]{10, 15, 20, 30 , 40};
    public final static String KEY_UID = "uid";//用户ID
    public final static String KEY_AVATER = "avatar";//用户头像
    public final static String KEY_NICKNAME = "nickname";//用户昵称

    public final static String KEY_GAME_SERVER = "server";//牌局服务器
    public final static String KEY_ROOM_ID = "room_id";//牌局大厅ID

    public final static String KEY_GAME_GID = "gid";//牌局ID
    public final static String KEY_GAME_INFO = "game_info";//牌局ID
    public final static String KEY_GAME_NAME = "name";//牌局名称
    public final static String KEY_GAME_TEADID = "tid";//牌局群组ID
    public final static String KEY_GAME_TEADVID = "vid";//牌局群组虚拟ID
    public final static String KEY_GAME_STATUS = "status";//牌局状态
    public final static String KEY_GAME_CODE = "code";//牌局CODE
//    public final static String KEY_BLINDS_TYPE = "blinds";//牌局筹码类型,小盲
//    public final static String KEY_DURATION_TYPE = "duration";//牌局时间类型
    public final static String KEY_SMALL_BLINDS = "sblinds";//牌局盲注:小盲
    public final static String KEY_DURATIONS = "durations";//牌局时间（单位：s）
    public final static String KEY_GAME_TYPE = "type";//牌局群组类型（0：俱乐部 ， 1：游戏聊天室）
    //
    public final static String KEY_GAME_ANTE = "ante";//牌局：ante数值
    public final static String KEY_GAME_MODE_ANTE = "ante_mode";//牌局模式：ante
    public final static String KEY_GAME_MODE_TILT = "tilt_mode";//牌局模式：保险
    public final static String KEY_GAME_MODE_PUBLIC = "public_mode";//牌局模式：公开
    //
//    public final static String KEY_GAME_CREATOR = "owner";//牌局创建者帐号
    public final static String KEY_GAME_CREATOR_ID = "owner";//牌局创建者ID
    public final static String KEY_GAME_CREATOR_NICKNAME = "owner_nickname";//牌局创建者ID
    //
    public final static String KEY_GAME_MONEY = "money";//牌局时间
    public final static String KEY_GAME_CREATE_TIME = "create_time";//牌局创建时间
    public final static String KEY_GAME_END_TIME = "end_time";//牌局结束时间
    public final static String KEY_GAME_CURRENT_SERVERTIME = "current_time";//牌局创建时间
    public final static String KEY_GAME_CURRENT_ENTRYTIME = "entry_date";//牌局进入时间
    public final static String KEY_GAME_GAMER_COUNT = "gamer_count";//牌局中的人数
    public final static String KEY_GAME_DATE = "date";//牌局日期
    public final static String KEY_GAME_ISINVITED = "invited";//邀请
    public final static String KEY_GAME_IS_CONTROL = "is_control";//
    public final static String KEY_GAME_MIN_BUY_CHIPS = "min_buy_chips";
    public final static String KEY_GAME_MAX_BUY_CHIPS = "max_buy_chips";
    public final static String KEY_GAME_TOTAL_BUY_CHIPS = "total_buy_chips";
    public final static String KEY_GAME_CHECK_IP = "check_ip";
    public final static String KEY_GAME_CHECK_GPS = "check_gps";
    public final static String KEY_GAME_KO_MODE = "ko_mode";
    public final static String KEY_GAME_KO_REWARD_RATE = "ko_reward_rate";
    public final static String KEY_GAME_KO_HEAD_RATE = "ko_head_rate";

    public final static String KEY_GAME_IS_CHECKIN = "is_checkin";//是否已经报名
    //
    public final static String KEY_GAME_CEHCKIN_PLAYER_COUNT = "checkin_player";//牌局中的报名人数
    //
    public final static String KEY_GAME_MODE = "game_mode";//牌局模式
    public final static String KEY_PLAY_MODE = "play_mode";//牌局模式
    //
    public final static String KEY_GAME_MATCH_CHIPS = "match_chips";//记分牌
    public final static String KEY_GAME_MATCH_PLAER = "match_player";//单桌人数
    public final static String KEY_GAME_MATCH_DURATION = "match_duration";//涨盲时间
    public final static String KEY_GAME_MATCH_CHECKIN_FEE = "match_checkin_fee";//参赛费用
    //
    public final static String KEY_GAME_TOTAL_PLAYER = "total_player";//总人数
    public final static String KEY_GAME_SBLINDS_INDEX = "sblinds_index";//比赛结束时候的盲注等级

    public final static String KEY_GAME_MATCH_BLANDS_LEVEL = "match_level";//当前盲注结构等级

    public static final String KEY_GAME_MATCH_ADDON = "match_addon";//比赛增购
    public static final String KEY_GAME_MATCH_REBUY = "match_rebuy";//比赛重购
    public static final String KEY_GAME_MATCH_BEGIN_TIME = "begin_time";//比赛开始时间
    public static final String KEY_GAME_MATCH_MODE_REST = "rest_mode";//比赛中场休息模式
    //
    public final static String KEY_GAME_ALL_REWARD = "all_reward";//总奖池
    public final static String KEY_GAME_TOTAL_TIME = "total_time";//总时间
    public final static String KEY_GAME_RECORD_MY_UID = "my_uid";//牌局信息的数据拥有者
    public final static String KEY_GAME_IS_ADMIN = "is_admin";//牌局信息的数据拥有者
    public final static String KEY_GAME_IS_CLUB_CHANNEL = "is_club_channel";//牌局信息的数据拥有者
    public static final int REQUEST_CODE_INVITE = 1;
    //是否是保险模式
    //是否是ANTE模式
    public final static int GAME_TYPE_CLUB = 0;//牌局类别：俱乐部，圈子
    public final static int GAME_TYPE_GAME = 1;//牌局类别：私人

    public final static int GAME_MODE_NORMAL = 0;//牌局模式：普通局
    public final static int GAME_MODE_SNG = 1;//牌局模式：SNG局
    public final static int GAME_MODE_MT_SNG = 2;//牌局模式：SNG多桌模式
    public final static int GAME_MODE_MTT = 3;//牌局模式：MTT局

    public final static int GAME_MODE_INSURANCE_NOT = 0;//保险模式：否
    public final static int GAME_MODE_INSURANCE_POOL = 1;//保险模式：分池购买
    public final static int GAME_MODE_INSURANCE_QUICK = 2;//保险模式：快速购买
    public final static int GAME_MODE_PUBLIC = 1;//公开模式：是

    public final static int ANTE_TYPE_0 = 0;//ANTE模式：0
    public final static int ANTE_TYPE_1 = 1;//ANTE模式：1倍
    public final static int ANTE_TYPE_2 = 2;//ANTE模式：2倍
    public final static int ANTE_TYPE_3 = 3;//ANTE模式：3倍

    public final static int CONTROL_BUY_MODE = 1;//控制买入模式

    public final static int GAME_ISINVITED = 1;//是被邀请的牌局
    public final static int GAME_RECENT_TAG_STICKY = 1; // 最近牌局置顶tag
    public final static int GAME_RECENT_TAG_NORMAL = 0; // 最近牌局平时tag

    public final static int GAME_MT_ADDON_MODE_NOT = 0; //增购模式：否
    public final static int GAME_MT_ADDON_MODE = 1; //增购模式：是
    public final static int GAME_MT_REBUY_MODE_NOT = 0; //重购模式：否
    public final static int GAME_MT_REBUY_MODE = 1; //重购模式：是
    public final static int GAME_MT_REST_MODE_NOT = 0; //中场休息：否
    public final static int GAME_MT_REST_MODE = 1; //中场休息：是

    public final static int HUNDRED_HANDS_COUNT = 100;

    /**
     * 是否是游戏群
     * @param teamId
     * @return
     */
    public static boolean isGmaeClub(String teamId) {
        Team team = TeamDataCache.getInstance().getTeamById(teamId);
        if (team != null && team.getType() == TeamTypeEnum.Advanced && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                int type = new JSONObject(team.getExtServer()).optInt(GameConstants.KEY_GAME_TYPE);
                if (type == GameConstants.GAME_TYPE_GAME) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否是游戏群
     * @return
     */
    public static boolean isGmaeClub(Team team) {
        if (team != null && team.getType() == TeamTypeEnum.Advanced && !TextUtils.isEmpty(team.getExtServer())) {
            try {
                int type = new JSONObject(team.getExtServer()).optInt(GameConstants.KEY_GAME_TYPE);
                if (type == GameConstants.GAME_TYPE_GAME) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 是否是游戏群
     * @return
     */
    public static boolean isGmaeClub(TeamEntity team) {
        if (team != null && !TextUtils.isEmpty(team.extension)) {
            try {
                int type = new JSONObject(team.extension).optInt(GameConstants.KEY_GAME_TYPE);
                if (type == GameConstants.GAME_TYPE_GAME) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取剩余时间显示格式
     * @param remain
     * @param isEn 是否是英文
     * @return
     */
    public static String getShowRemainTime(int remain , boolean isEn){
        String show = remain == 0 ? ("1" + (isEn ? "m" : "分钟")) : "";
        int hour = remain / 60;
        int minutes = remain % 60;
        if (hour != 0) {
            if (isEn) {
                show = hour + "h";
            } else {
                show = hour + "小时";
            }
        }
        if (minutes != 0) {
            if (isEn) {
                show = show + minutes + "m";
            } else {
                show = show + minutes + "分钟";
            }
        }
        return show;
    }

    /**
     * 获取剩余时间显示格式
     * @param remain
     * @return
     */
    public static String getShowHistoryRemainTime(int remain){
        String show = "";
        int hour = remain / 60;
        int minutes = remain % 60;
        if(hour != 0) {
            show = hour + DemoCache.getContext().getString(R.string.hour);
        }
        if (minutes != 0) {
            show = show + minutes + DemoCache.getContext().getString(R.string.minutes);
        }
        return show;
    }

    /**
     * 获取剩余时间显示格式
     * @param remain
     * @return
     */
    public static String getShowRecentGameRemainTime(int remain) {
        String show = "";
        int hour = remain / 60;
        int minutes = remain % 60;
        if (hour != 0) {
            float minitesInHour = minutes / 60.0f;
            if (minutes == 0 || minitesInHour == 0.0f) {
                show = hour + DemoCache.getContext().getString(R.string.hour);
            } else {
                show = (hour + Float.valueOf((int)(minitesInHour * 10) /10.0f)) + DemoCache.getContext().getString(R.string.hour);
            }
        } else {
            if (minutes != 0) {
                show = minutes + DemoCache.getContext().getString(R.string.minutes);
            }
        }
        return show;
    }

    /**
     * 获取创建牌局限制时候的提示
     * @param gameType
     * @param vipLevel
     * @return
     */
    public static int getCreateGameLimitShowRes(int gameType , int vipLevel, boolean isClubManager) {
        int stringId = R.string.game_create_private_limit_normal;
        if (vipLevel == VipConstants.VIP_LEVEL_BALCK) {
            if(gameType == GAME_TYPE_GAME){
                stringId = R.string.game_create_private_limit_blackvip;
            }else{
                stringId = R.string.game_create_team_limit_blackvip;
            }
        } else if (vipLevel == VipConstants.VIP_LEVEL_WHITE) {
            if(gameType == GAME_TYPE_GAME){
                stringId = R.string.game_create_private_limit_whitevip;
            }else{
                stringId = R.string.game_create_team_limit_whitevip;
            }
        } else {
            if(gameType == GAME_TYPE_GAME){
                stringId = R.string.game_create_private_limit_normal;
            }else{
                if (isClubManager) {
                    stringId = R.string.game_create_team_limit_normal_club_manager;
                } else {
                    stringId = R.string.game_create_team_limit_normal;
                }
            }
        }
        return stringId;
    }

    /**
     * 获取默认的时间显示
     * @param duration
     * @return
     */
    public static String getGameDurationShow(int duration) {
        int min = duration / 60;
        if (min < 60) {
            return min + DemoCache.getContext().getString(R.string.minutes);
        } else {
            DecimalFormat df = new DecimalFormat("#.#");
            return df.format(min / 60f) + DemoCache.getContext().getString(R.string.hour);
        }
    }

    /**
     * 获取默认的时间显示 00:00:33格式
     * @param duration
     * @return
     */
    public static String getGameSngDurationShow(int duration) {
        StringBuffer sngDurationStr = new StringBuffer();
        int second = duration % 60;
        int min = duration / 60 % 60;
        int hour = duration / 60 / 60 % 60;
        if (hour < 10) {
            sngDurationStr.append("0");
        }
        sngDurationStr.append(hour);
        sngDurationStr.append(":");
        if (min < 10) {
            sngDurationStr.append("0");
        }
        sngDurationStr.append(min);
        sngDurationStr.append(":");
        if (second < 10) {
            sngDurationStr.append("0");
        }
        sngDurationStr.append(second);
        return sngDurationStr.toString();
    }

    /**
     * 获取默认的时间显示 33分钟  1小时 格式
     * @param duration
     * @return
     */
    public static String getGameSngAndMttDurationShow(int duration) {
        StringBuffer sngDurationStr = new StringBuffer();
        int second = duration % 60;
        int min = duration / 60 % 60;
        int hour = duration / 60 / 60 % 60;
        return hour + DemoCache.getContext().getString(R.string.hour) + min + DemoCache.getContext().getString(R.string.minutes);
    }

    /**
     * 获取默认的时间显示
     * @return
     */
    public static String getGameSngDurationMinutesShow(int second) {
        int min = second / 60;
        return min + DemoCache.getContext().getString(R.string.minutes);
    }

    /**
     * 获取默认盲注显示
     * @param smallBlinds
     * @return
     */
    public static String getGameBlindsShow(int smallBlinds) {
        String smallStr = getGameChipsShow(smallBlinds);
        String bigStr = getGameChipsShow(smallBlinds * 2);
        return smallStr + "/" + bigStr;
    }

    /**
     * 获取ante
     * @return
     */
    public static int getGameAnte(GameNormalConfig gameNormalConfig) {
        int anteShow = 0;
        if (gameNormalConfig == null) {
            return anteShow;
        }
        if (gameNormalConfig.ante != 0) {
            anteShow = gameNormalConfig.ante;
            return anteShow;
        }
        if (gameNormalConfig.anteMode != GameConstants.ANTE_TYPE_0) {
            if (gameNormalConfig.ante != 0) {
                anteShow = gameNormalConfig.ante;
            } else {
                anteShow = gameNormalConfig.anteMode * gameNormalConfig.blindType;
            }
        }
        return anteShow;
    }

    /**
     * 获取默认记分牌显示
     * @param chips
     * @return
     */
    public static String getGameChipsShow(int chips) {
        String chipsStr = "" + chips;
        if (chips >= 10000) {
            chipsStr = (chips / 1000) + "K";
        }
        return chipsStr;
    }

    public final static int PINEAPPLE_MODE_NORMAL = 0;
    public final static int PINEAPPLE_MODE_BLOOD = 1;
    public final static int PINEAPPLE_MODE_BLOOD_IN_OUT = 2;
    public final static int PINEAPPLE_MODE_YORIKO = 3;
    //用户行为
    // 0 wait 1 surround 2 ready 3 sit 4 leave
    public static final int ACTIVITY_NO = 0;
    public static final int ACTIVITY_SURROUND = 1;
    public static final int ACTIVITY_READY = 2;
    public static final int ACTIVITY_SIT = 3;
    public static final int ACTIVITY_LEAVE = 4;
    //发牌顺序
    public final static String KEY_DEAL_ORDER = "deal_order";//大菠萝普通局的发牌顺序
    public static final int DEAL_ORDER_MEANWHILE = 0;//同时发牌
    public static final int DEAL_ORDER_SEQUENCE = 1;//顺序发牌
    //match_type比赛类型  0常规赛   2金币赛    1钻石赛
    public static final int MATCH_TYPE_NORMAL = 0;//
    public static final int MATCH_TYPE_DIAMOND = 1;//
    public static final int MATCH_TYPE_GOLD = 2;//
    //大菠萝mtt的底注表类型  0普通表   1快速表
    public static final int PINEAPPLE_MTT_ANTE_TABLE_NORMAL = 1;//
    public static final int PINEAPPLE_MTT_ANTE_TABLE_QUICK = 2;//
    //比赛报名等待审批时间300秒=5分钟(原来是180秒=3分钟)
    public static final int GAME_CHECKIN_INTERVAL = 301000;//
}
