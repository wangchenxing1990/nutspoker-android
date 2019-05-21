package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * 创建牌局附件
 */
public class GameAttachment extends CustomAttachment {
    private String name;//群组名称
    private String tid;//群组ID
    public String gid;//牌局ID
    private String code;//牌局code,用于验证进入游戏
    private String roomId;//聊天室ID（俱乐部普通牌局和SNG牌局使用聊天室逻辑）
    private String ownerId;//牌局创建者ID
    private int gameType;//牌局类型：1.私人 2.群
    private int gameStatus;//牌局状态
    private int durationType = 0;//牌局时间
    private int blindsType = 0;//牌局时间
    private int publicMode = 0;//公开模式:0：私人模式
    private int anteMode = 0;//ante 0：非
    private int ante = 0;//ante
    private int tiltMode = 0;//保险模式 0：非
    private long createTime;//创建时间
    private int min_chips;
    private int max_chips;
    private int total_chips;
    private int check_ip;
    private int check_gps;
    private int matchChips = 0;///SNG牌局带入记分牌
    private int matchPlayer = 0;///SNG牌局起始盲注      额  貌似是 单桌人数 3人桌  6人桌 9人桌
    private int matchDuration = 0;//SNG牌局涨盲时间
    private int matchCheckinFee = 0;//
    private int totalPlayer = 0;//总人数
    private int gameMode = 0;//0 普通  1sng 2mt-sng 3mtt
    public int type;//标记是否是群内建的 0   1
    public int ko_mode = 0;//  --0不是猎头赛 1普通 2超级
    public int ko_reward_rate = 0;//  奖金分成
    public int ko_head_rate; //人头分成(只有超级猎人赛才会有)
    public int play_mode;
    public int play_type;
    public int match_type;
    //
    private long matchBeginTime = 0;//比赛模式：开始时间
    String data = "";

    public GameAttachment(String data) {
        super(CustomAttachmentType.CreatGame);
        this.data = data;
        parseData(JSON.parseObject(data));
    }

    // 解析开始牌局类型具体数据
    @Override
    protected void parseData(JSONObject data) {
        play_mode = data.getIntValue("play_mode");
        play_type = data.getIntValue("play_type");
        match_type = data.getIntValue("match_type");
        name = data.getString(GameConstants.KEY_GAME_NAME);
        tid = data.getString(GameConstants.KEY_GAME_TEADID);
        roomId = data.getString(GameConstants.KEY_ROOM_ID);
        gid = data.getString(GameConstants.KEY_GAME_GID);
        code = data.getString(GameConstants.KEY_GAME_CODE);
        gameType = data.getIntValue(GameConstants.KEY_GAME_TYPE);
        gameStatus = data.getIntValue(GameConstants.KEY_GAME_STATUS);
        blindsType = data.getIntValue(GameConstants.KEY_SMALL_BLINDS);
        durationType = data.getIntValue(GameConstants.KEY_DURATIONS);
        publicMode = data.getIntValue(GameConstants.KEY_GAME_MODE_PUBLIC);
        anteMode = data.getIntValue(GameConstants.KEY_GAME_MODE_ANTE);
        ante = data.getIntValue(GameConstants.KEY_GAME_ANTE);
        tiltMode = data.getIntValue(GameConstants.KEY_GAME_MODE_TILT);
        createTime = data.getLongValue(GameConstants.KEY_GAME_CREATE_TIME);
        ownerId = data.getString(GameConstants.KEY_GAME_CREATOR_ID);
        min_chips = data.getIntValue(GameConstants.KEY_GAME_MIN_BUY_CHIPS);
        max_chips = data.getIntValue(GameConstants.KEY_GAME_MAX_BUY_CHIPS);
        total_chips = data.getIntValue(GameConstants.KEY_GAME_TOTAL_BUY_CHIPS);
//        check_ip = data.getBoolean(GameConstants.KEY_GAME_CHECK_IP) ? 1 : 0;  android传的是int，ios传的是boolean，暂时先注释掉
//        check_gps = data.getBoolean(GameConstants.KEY_GAME_CHECK_GPS) ? 1 : 0;
        gameMode = data.getIntValue(GameConstants.KEY_GAME_MODE);
        matchChips = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHIPS);
        matchPlayer = data.getIntValue(GameConstants.KEY_GAME_MATCH_PLAER);
        matchDuration = data.getIntValue(GameConstants.KEY_GAME_MATCH_DURATION);
        matchCheckinFee = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE);
        matchBeginTime = data.getLongValue(GameConstants.KEY_GAME_MATCH_BEGIN_TIME);
        totalPlayer = data.getIntValue(GameConstants.KEY_GAME_TOTAL_PLAYER);
        //mtt
        ko_mode = data.getIntValue(GameConstants.KEY_GAME_KO_MODE);//  --0不是猎头赛 1普通 2超级
        ko_reward_rate = data.getIntValue(GameConstants.KEY_GAME_KO_REWARD_RATE);;//  奖金分成
        ko_head_rate = data.getIntValue(GameConstants.KEY_GAME_KO_HEAD_RATE);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(GameConstants.KEY_GAME_NAME, name);
        data.put(GameConstants.KEY_GAME_TEADID, tid);
        data.put(GameConstants.KEY_ROOM_ID, roomId);
        data.put(GameConstants.KEY_GAME_GID, gid);
        data.put(GameConstants.KEY_GAME_CODE, code);
        data.put(GameConstants.KEY_GAME_TYPE, gameType);
        data.put(GameConstants.KEY_GAME_STATUS, gameStatus);
        data.put(GameConstants.KEY_SMALL_BLINDS, blindsType);
        data.put(GameConstants.KEY_DURATIONS, durationType);
        data.put(GameConstants.KEY_GAME_MODE_PUBLIC, publicMode);
        data.put(GameConstants.KEY_GAME_MODE_ANTE, anteMode);
        data.put(GameConstants.KEY_GAME_ANTE, ante);
        data.put(GameConstants.KEY_GAME_MODE_TILT, tiltMode);
        data.put(GameConstants.KEY_GAME_CREATE_TIME, createTime);
        data.put(GameConstants.KEY_GAME_CREATOR_ID, ownerId);
        //
        data.put(GameConstants.KEY_GAME_MODE, gameMode);
        data.put(GameConstants.KEY_GAME_MATCH_CHIPS, matchChips);
        data.put(GameConstants.KEY_GAME_MATCH_PLAER, matchPlayer);
        data.put(GameConstants.KEY_GAME_MATCH_DURATION, matchDuration);
        data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, matchCheckinFee);
        data.put(GameConstants.KEY_GAME_MATCH_BEGIN_TIME, matchBeginTime);
        data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, totalPlayer);
        data.put(GameConstants.KEY_GAME_MIN_BUY_CHIPS, min_chips);
        data.put(GameConstants.KEY_GAME_MAX_BUY_CHIPS, max_chips);
        data.put(GameConstants.KEY_GAME_TOTAL_BUY_CHIPS, total_chips);
        data.put(GameConstants.KEY_GAME_CHECK_IP, check_ip);
        data.put(GameConstants.KEY_GAME_CHECK_GPS, check_gps);
        data.put("play_mode", play_mode);
        data.put("play_type", play_type);
        data.put("match_type", match_type);
        return data;
    }

    /**
     * 获取值
     * @return
     */
    public GameEntity getValue() {
        GameEntity gameInfo = new GameEntity();
        gameInfo.deskItemTag = (GameEntity.DeskItemTag.NORMAL);
        gameInfo.tid = (tid);
        gameInfo.play_mode = (play_mode);
        gameInfo.room_id = (roomId);
        gameInfo.gid = (gid);
        gameInfo.type = (gameType);
        gameInfo.status = (gameStatus);
        gameInfo.name = (name);
        gameInfo.code = (code);
        gameInfo.publicMode = (publicMode);
        gameInfo.createTime = (createTime);
        UserEntity creatorInfo = new UserEntity();
        creatorInfo.account = (ownerId);
        gameInfo.creatorInfo = (creatorInfo);
        gameInfo.match_type = (match_type);
        //
        gameInfo.gameMode = (gameMode);
        if (play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || play_mode == GameConstants.PLAY_MODE_OMAHA) {
            if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                GameNormalConfig gameConfig = new GameNormalConfig();
                gameConfig.blindType = (blindsType);
                gameConfig.timeType = (durationType);
                gameConfig.anteMode = (anteMode);
                gameConfig.ante = (ante);
                gameConfig.tiltMode = (tiltMode);
                gameConfig.matchChips = matchChips;
                gameConfig.min_chips = min_chips;//", "" + gameNormalConfig.min_chips);
                gameConfig.max_chips = max_chips;//", "" + max_buy_chips);
                gameConfig.total_chips = total_chips;//", "" + total_buy_chips);
                gameConfig.check_ip = check_ip;//", "" + gameNormalConfig.check_ip);
                gameConfig.check_gps = check_gps;//", "" + gameNormalConfig.check_gps);
                gameInfo.gameConfig = (gameConfig);
            } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                GameSngConfigEntity gameConfig = new GameSngConfigEntity();
                gameConfig.setChips(matchChips);
                gameConfig.setPlayer(matchPlayer);
                gameConfig.setDuration(matchDuration);
                gameConfig.setCheckInFee(matchCheckinFee);
                gameInfo.gameConfig = (gameConfig);
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                GameMttConfig gameConfig = new GameMttConfig();
                gameConfig.matchChips = (matchChips);
                gameConfig.matchPlayer = (matchPlayer);
                gameConfig.matchDuration = (matchDuration);
                gameConfig.matchCheckinFee = (matchCheckinFee);
                gameConfig.beginTime = (matchBeginTime);
                gameConfig.ko_mode = ko_mode;
                gameConfig.ko_reward_rate = ko_reward_rate;
                gameConfig.ko_head_rate = ko_head_rate;
                gameConfig.match_type = match_type;
                gameInfo.gameConfig = (gameConfig);
            } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                GameMtSngConfig gameConfig = new GameMtSngConfig();
                gameConfig.matchChips = (matchChips);
                gameConfig.matchPlayer = (matchPlayer);
                gameConfig.matchDuration = (matchDuration);
                gameConfig.matchCheckinFee = (matchCheckinFee);
                gameConfig.totalPlayer = (totalPlayer);
                gameInfo.gameConfig = (gameConfig);
            }
        } else if (play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                PineappleConfig pineappleConfig = new PineappleConfig(play_type);
                pineappleConfig.setAnte(ante);
                pineappleConfig.setChips(matchChips);
                pineappleConfig.setDuration(durationType);
                pineappleConfig.setIp_limit(check_ip);
                pineappleConfig.setGps_limit(check_gps);
                pineappleConfig.setMatch_player(matchPlayer);
                gameInfo.gameConfig = pineappleConfig;
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                PineappleConfigMtt gameConfig = new PineappleConfigMtt(play_type);
                gameConfig.matchChips = (matchChips);
                gameConfig.matchDuration = (matchDuration);
                gameConfig.matchCheckinFee = (matchCheckinFee);
                gameConfig.beginTime = (matchBeginTime);
                gameConfig.ko_mode = ko_mode;
                gameConfig.ko_reward_rate = ko_reward_rate;
                gameConfig.ko_head_rate = ko_head_rate;
                gameConfig.match_type = match_type;
                gameInfo.gameConfig = (gameConfig);
            }
        }
        return gameInfo;
    }

    /**
     * 转换成JSON
     * @param gameInfo
     * @return
     */
    public static String toJsonData(GameEntity gameInfo){
        JSONObject data = new JSONObject();
        data.put(GameConstants.KEY_GAME_NAME, gameInfo.name);
        data.put(GameConstants.KEY_GAME_TEADID, gameInfo.tid);
        data.put(GameConstants.KEY_ROOM_ID, gameInfo.room_id);
        data.put(GameConstants.KEY_GAME_GID, gameInfo.gid);
        data.put(GameConstants.KEY_GAME_CODE, gameInfo.code);
        data.put(GameConstants.KEY_GAME_STATUS, gameInfo.status);
        data.put(GameConstants.KEY_GAME_TYPE, gameInfo.type);
        data.put(GameConstants.KEY_GAME_MODE_PUBLIC, gameInfo.publicMode);
        data.put(GameConstants.KEY_GAME_CREATE_TIME, gameInfo.createTime);
        data.put(GameConstants.KEY_GAME_CREATOR_ID, gameInfo.creatorInfo.account);
        data.put("match_type", gameInfo.match_type);
        //
        int gameMode = gameInfo.gameMode;
        data.put(GameConstants.KEY_GAME_MODE, gameMode);
        if (gameInfo.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
                //普通模式
                GameNormalConfig gameConfig = (GameNormalConfig)gameInfo.gameConfig;
                data.put(GameConstants.KEY_SMALL_BLINDS, gameConfig.blindType);
                data.put(GameConstants.KEY_DURATIONS, gameConfig.timeType);
                data.put(GameConstants.KEY_GAME_MODE_ANTE, gameConfig.anteMode);
                data.put(GameConstants.KEY_GAME_ANTE, gameConfig.ante);
                data.put(GameConstants.KEY_GAME_MODE_TILT, gameConfig.tiltMode);
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.matchChips);
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
                //SNG模式
                GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.getChips());
                data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameConfig.getPlayer());
                data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.getDuration());
                data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameConfig.getCheckInFee());
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
                //MTT模式
                GameMttConfig gameConfig = (GameMttConfig) gameInfo.gameConfig;
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.matchChips);
                data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameConfig.matchPlayer);
                data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.matchDuration);
                data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameConfig.matchCheckinFee);
                data.put(GameConstants.KEY_GAME_MATCH_BEGIN_TIME, gameConfig.beginTime);
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
                //MT-SNG模式
                GameMtSngConfig gameConfig = (GameMtSngConfig) gameInfo.gameConfig;
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.matchChips);
                data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameConfig.matchPlayer);
                data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.matchDuration);
                data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameConfig.matchCheckinFee);
                data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, gameConfig.totalPlayer);
            }
        } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                PineappleConfig gameConfig = (PineappleConfig) gameInfo.gameConfig;
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.getChips());
                data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameConfig.getMatch_player());
                data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.getDuration());
            } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                PineappleConfigMtt gameConfig = (PineappleConfigMtt) gameInfo.gameConfig;
                data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.matchChips);
                data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.matchDuration);
                data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameConfig.matchCheckinFee);
                data.put(GameConstants.KEY_GAME_MATCH_BEGIN_TIME, gameConfig.beginTime);
            }
        }
        return data.toJSONString();
    }
}
