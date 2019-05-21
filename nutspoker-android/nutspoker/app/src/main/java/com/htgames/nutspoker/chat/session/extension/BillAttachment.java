package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.ArrayList;

/**
 * 战绩附件
 */
public class BillAttachment extends CustomAttachment{
    private final static String TAG = "BillAttachment";
    String gid;
    String tid;
    String name;
    String owner;
    String owner_avater;
    int blinds;
    int duration;
    int public_mode;
    int ante_mode;
    int ante;
    int tilt_mode;
    int type;
    int status;
    long create_time;
    long end_time;
    int max_pot;
    int all_buys;
    int bouts;
    int win_chips;
    //
    int gameMode;
    int play_mode;
    int matchChips = 0;///SNG牌局带入记分牌
    int matchPlayer = 0;///SNG牌局人数
    int matchDuration = 0;//SNG牌局涨盲时间
    int matchCheckinFee = 0;//
    int all_reward;//总奖池
    int total_time;//比赛时间
    //MTT
    int totalPlayer;//比赛总人数
    int endSblindsIndex;//到达的盲注级别
    //
    String myUid;//牌局信息发送人的UID
    GameMemberEntity myMemberInfo;
    //
    String mvp;
    String fish;
    String richest;
    String gameMembersListStr;
    ArrayList<GameMemberEntity> gameMemberList;
    String data = "";

    public BillAttachment(String data) {
        super(CustomAttachmentType.Bill);
        this.data = data;
        LogUtil.i(TAG , "new :" + data);
        parseData(JSON.parseObject(data));
    }

    // 解析开始牌局类型具体数据
    @Override
    protected void parseData(JSONObject data) {
        gid = data.getString("gid");
        tid = data.getString("tid");
        name = data.getString("name");
        owner = data.getString("owner");
        owner_avater = data.getString("owner_avater");
        blinds = data.getIntValue(GameConstants.KEY_SMALL_BLINDS);
        duration = data.getIntValue(GameConstants.KEY_DURATIONS);
        public_mode = data.getIntValue("public_mode");
        ante_mode = data.getIntValue("ante_mode");
        ante = data.getIntValue(GameConstants.KEY_GAME_ANTE);
        tilt_mode = data.getIntValue("tilt_mode");
        type = data.getIntValue("type");
        status = data.getIntValue("status");
        create_time = data.getLongValue("create_time");
        end_time = data.getLongValue("end_time");
        if (end_time == 0) {
            end_time = create_time;
        }
        //数据清单
        max_pot = data.getIntValue("max_pot");
        all_buys = data.getIntValue("all_buys");
        bouts = data.getIntValue("bouts");
        win_chips = data.getIntValue("win_chips");
        //MVP，大鱼，土豪
        mvp = data.getString("mvp");
        fish = data.getString("fish");
        richest = data.getString("richest");
        //
        gameMode = data.getIntValue(GameConstants.KEY_GAME_MODE);
        play_mode = data.getIntValue(GameConstants.KEY_PLAY_MODE);
        matchChips = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHIPS);
        matchPlayer = data.getIntValue(GameConstants.KEY_GAME_MATCH_PLAER);
        matchDuration = data.getIntValue(GameConstants.KEY_GAME_MATCH_DURATION);
        matchCheckinFee = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE);
        all_reward = data.getIntValue(GameConstants.KEY_GAME_ALL_REWARD);
        total_time = data.getIntValue(GameConstants.KEY_GAME_TOTAL_TIME);
        //
        totalPlayer = data.getIntValue(GameConstants.KEY_GAME_TOTAL_PLAYER);
        endSblindsIndex = data.getIntValue(GameConstants.KEY_GAME_SBLINDS_INDEX);
        //
        myUid = data.getString(GameConstants.KEY_GAME_RECORD_MY_UID);
        //
        gameMemberList = new ArrayList<GameMemberEntity>();
        try {
            if (data.containsKey("gameMembers")) {
                gameMembersListStr = data.getJSONArray("gameMembers").toJSONString();
                JSONArray membersArray = data.getJSONArray("gameMembers");
//            JSONArray membersArray = JSONArray.parseArray(gameMembersListStr);
                int memberSize = membersArray.size();
                LogUtil.i(TAG, "memberSize :" + memberSize);
                for (int j = 0; j < memberSize; j++) {
                    JSONObject memberJson = membersArray.getJSONObject(j);
                    GameMemberEntity gameMemberEntity = new GameMemberEntity();
                    UserEntity userEntity = new UserEntity();
                    String uid = memberJson.getString(GameConstants.KEY_UID);
                    userEntity.account = (uid);
                    userEntity.name = (memberJson.getString(GameConstants.KEY_NICKNAME));
                    userEntity.avatar = (memberJson.getString(GameConstants.KEY_AVATER));
                    gameMemberEntity.userInfo = (userEntity);
                    gameMemberEntity.totalBuy = (memberJson.getIntValue("total_buy"));
                    gameMemberEntity.winChip = (memberJson.getIntValue("win_chips"));
                    gameMemberEntity.premium = (memberJson.getIntValue("premium"));
                    gameMemberEntity.insurance = (memberJson.getIntValue("insurance"));
                    gameMemberEntity.joinCnt = (memberJson.getIntValue("join_cnt"));
                    gameMemberEntity.enterPotCnt = (memberJson.getIntValue("enter_pot_cnt"));
                    gameMemberEntity.winCnt = (memberJson.getIntValue("win_cnt"));
                    //SNG
                    gameMemberEntity.reward = (memberJson.getIntValue("reward"));
                    gameMemberEntity.ranking = (memberJson.getIntValue("ranking"));
                    //
                    if (uid.equals(myUid)) {
                        //获取发送者的相关信息
                        myMemberInfo = gameMemberEntity;
                    }
                    gameMemberList.add(gameMemberEntity);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        LogUtil.i(TAG , data.toJSONString());
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("gid", gid);
        data.put("tid", tid);
        data.put("name", name);
        data.put("owner", owner);
        data.put("owner_avater", owner_avater);
        data.put(GameConstants.KEY_SMALL_BLINDS, blinds);
        data.put(GameConstants.KEY_DURATIONS, duration);
        data.put("public_mode", public_mode);
        data.put("ante_mode", ante_mode);
        data.put(GameConstants.KEY_GAME_ANTE, ante);
        data.put("tilt_mode", tilt_mode);
        data.put("type", type);
        data.put("status", status);
        data.put("create_time", create_time);
        data.put("end_time", end_time);
        data.put("max_pot", max_pot);
        data.put("all_buys", all_buys);
        data.put("bouts", bouts);
        data.put("win_chips", win_chips);
        data.put("mvp", mvp);
        data.put("fish", fish);
        data.put("richest", richest);
        //
        data.put(GameConstants.KEY_PLAY_MODE, play_mode);
        data.put(GameConstants.KEY_GAME_MODE, gameMode);
        data.put(GameConstants.KEY_GAME_MATCH_CHIPS, matchChips);
        data.put(GameConstants.KEY_GAME_MATCH_PLAER, matchPlayer);
        data.put(GameConstants.KEY_GAME_MATCH_DURATION, matchDuration);
        data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, matchCheckinFee);
        //
        data.put(GameConstants.KEY_GAME_ALL_REWARD, all_reward);
        data.put(GameConstants.KEY_GAME_TOTAL_TIME, total_time);
        //
        data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, totalPlayer);
        data.put(GameConstants.KEY_GAME_SBLINDS_INDEX, endSblindsIndex);
        //
        data.put(GameConstants.KEY_GAME_RECORD_MY_UID, myUid);
        //
        JSONArray array = JSON.parseArray(gameMembersListStr);
        data.put("gameMembers" , array);
        return data;
    }

    public static String toPackData(GameBillEntity gameBillEntity) {
        JSONObject data = new JSONObject();
        GameEntity gameEntity = gameBillEntity.gameInfo;
        data.put("gid", gameEntity.gid);
        data.put("tid", gameEntity.tid);
        data.put("name", gameEntity.name);
        data.put("owner", gameEntity.creatorInfo.account);
        data.put("owner_avater", gameEntity.creatorInfo.avatar);
        //
        data.put("public_mode", gameEntity.publicMode);
        data.put("type", gameEntity.type);
        data.put("status", gameEntity.status);
        data.put("create_time", gameEntity.createTime);
        data.put("max_pot", gameBillEntity.maxPot);
        data.put("all_buys", gameBillEntity.allBuys);
        data.put("bouts", gameBillEntity.bouts);
        data.put("win_chips", gameBillEntity.winChip);
        data.put("mvp", gameBillEntity.mvp);
        data.put("fish", gameBillEntity.fish);
        data.put("richest", gameBillEntity.richest);
        //
        data.put(GameConstants.KEY_GAME_MODE, gameEntity.gameMode);
        data.put(GameConstants.KEY_PLAY_MODE, gameEntity.play_mode);
        data.put(GameConstants.KEY_GAME_ALL_REWARD, gameBillEntity.allReward);
        data.put(GameConstants.KEY_GAME_TOTAL_TIME, gameBillEntity.totalTime);
        //
        data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, gameBillEntity.totalPlayer);
        data.put(GameConstants.KEY_GAME_SBLINDS_INDEX, gameBillEntity.endSblindsIndex);
        //
        if(gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL && gameEntity.gameConfig instanceof GameNormalConfig) {
            GameNormalConfig gameNormalConfig = (GameNormalConfig)gameEntity.gameConfig;
            data.put(GameConstants.KEY_SMALL_BLINDS, gameNormalConfig.blindType);
            data.put(GameConstants.KEY_DURATIONS, gameNormalConfig.timeType);
            data.put("ante_mode", gameNormalConfig.anteMode);
            data.put(GameConstants.KEY_GAME_ANTE, gameNormalConfig.ante);
            data.put("tilt_mode", gameNormalConfig.tiltMode);
            //
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, 0);
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, 0);
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, 0);
        } else if(gameEntity.gameMode == GameConstants.GAME_MODE_SNG && gameEntity.gameConfig instanceof GameSngConfigEntity) {
            GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity)gameEntity.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameSngConfigEntity.getChips());
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameSngConfigEntity.getPlayer());
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameSngConfigEntity.getDuration());
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameSngConfigEntity.getCheckInFee());
            //
            data.put(GameConstants.KEY_SMALL_BLINDS, 0);
            data.put(GameConstants.KEY_DURATIONS, 0);
            data.put("ante_mode", 0);
            data.put("tilt_mode", 0);
            data.put(GameConstants.KEY_GAME_ANTE, 0);
        } else if(gameEntity.gameMode == GameConstants.GAME_MODE_MTT && gameEntity.gameConfig instanceof GameMttConfig) {
            GameMttConfig gameMttConfig = (GameMttConfig) gameEntity.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameMttConfig.matchChips);
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameMttConfig.matchPlayer);
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameMttConfig.matchDuration);
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameMttConfig.matchCheckinFee);
            //
            data.put(GameConstants.KEY_SMALL_BLINDS, 0);
            data.put(GameConstants.KEY_DURATIONS, 0);
            data.put("ante_mode", 0);
            data.put("tilt_mode", 0);
            data.put(GameConstants.KEY_GAME_ANTE, 0);
        } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MT_SNG && gameEntity.gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameEntity.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameMtSngConfig.matchChips);
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameMtSngConfig.matchPlayer);
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameMtSngConfig.matchDuration);
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameMtSngConfig.matchCheckinFee);
            data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, gameMtSngConfig.totalPlayer);
            //
            data.put(GameConstants.KEY_SMALL_BLINDS, 0);
            data.put(GameConstants.KEY_DURATIONS, 0);
            data.put("ante_mode", 0);
            data.put("tilt_mode", 0);
            data.put(GameConstants.KEY_GAME_ANTE, 0);
        }
        //
        data.put(GameConstants.KEY_GAME_RECORD_MY_UID, gameBillEntity.myUid);
        //
        JSONArray array = JSON.parseArray(gameBillEntity.jsonMemberStr);
        data.put("gameMembers", array);
        return data.toJSONString();
    }

    /**
     * 获取值
     * @return
     */
    public GameBillEntity getValue() {
        GameBillEntity gameBillEntity = new GameBillEntity();
        GameEntity gameEntity = new GameEntity();
        //牌局信息
        gameEntity.gid = (gid);
        gameEntity.tid = (tid);
        gameEntity.name = (name);
        //
        UserEntity creatorInfo = new UserEntity();
        creatorInfo.account = (owner);
        creatorInfo.avatar = (owner_avater);
        gameEntity.creatorInfo = (creatorInfo);
        //
        gameEntity.gameMode = (gameMode);
        gameEntity.play_mode = (play_mode);
        gameEntity.publicMode = (public_mode);
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            GameNormalConfig gameNormalConfig = new GameNormalConfig();
            gameNormalConfig.blindType = (blinds);
            gameNormalConfig.timeType = (duration);
            gameNormalConfig.anteMode = (ante_mode);
            gameNormalConfig.ante = (ante);
            gameNormalConfig.tiltMode = (tilt_mode);
            gameEntity.gameConfig = (gameNormalConfig);
        } else if (gameMode == GameConstants.GAME_MODE_SNG) {
            GameSngConfigEntity gameConfig = new GameSngConfigEntity();
            gameConfig.setChips(matchChips);
            gameConfig.setPlayer(matchPlayer);
            gameConfig.setDuration(matchDuration);
            gameConfig.setCheckInFee(matchCheckinFee);
            gameEntity.gameConfig = (gameConfig);
        } else if (gameMode == GameConstants.GAME_MODE_MTT && play_mode != GameConstants.PLAY_MODE_PINEAPPLE) {
            GameMttConfig gameConfig = new GameMttConfig();
            gameConfig.matchChips = (matchChips);
            gameConfig.matchPlayer = (matchPlayer);
            gameConfig.matchDuration = (matchDuration);
            gameConfig.matchCheckinFee = (matchCheckinFee);
            gameEntity.gameConfig = (gameConfig);
        } else if (gameMode == GameConstants.GAME_MODE_MTT && play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            PineappleConfigMtt gameConfig = new PineappleConfigMtt(play_mode);
            gameConfig.matchChips = (matchChips);
            gameConfig.matchDuration = (matchDuration);
            gameConfig.matchCheckinFee = (matchCheckinFee);
            gameEntity.gameConfig = (gameConfig);
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
            GameMtSngConfig gameConfig = new GameMtSngConfig();
            gameConfig.matchChips = (matchChips);
            gameConfig.matchPlayer = (matchPlayer);
            gameConfig.matchDuration = (matchDuration);
            gameConfig.matchCheckinFee = (matchCheckinFee);
            gameConfig.totalPlayer = (totalPlayer);
            gameEntity.gameConfig = (gameConfig);
        }
        gameEntity.type = (type);
        gameEntity.status = (status);
        gameEntity.createTime = (create_time);
        gameEntity.endTime = (end_time);
        gameBillEntity.gameInfo = (gameEntity);
        //数据清单
        gameBillEntity.maxPot = (max_pot);
        gameBillEntity.allBuys = (all_buys);
        gameBillEntity.bouts = (bouts);
        gameBillEntity.winChip = (win_chips);
        //MVP，大鱼，土豪
        gameBillEntity.mvp = (mvp);
        gameBillEntity.fish = (fish);
        gameBillEntity.richest = (richest);
        //
        gameBillEntity.gameMemberList = (gameMemberList);
        //SNG
        gameBillEntity.totalTime = (total_time);
        gameBillEntity.allReward = (all_reward);
        //MTT
        gameBillEntity.totalPlayer = (totalPlayer);
        gameBillEntity.endSblindsIndex = (endSblindsIndex);
        //
        gameBillEntity.myUid = (myUid);
        gameBillEntity.myMemberInfo = (myMemberInfo);
//        gameBillEntity.setJsonStr(gameMembersListStr);
        gameBillEntity.jsonStr = (packData().toJSONString());
        return gameBillEntity;
    }
}
