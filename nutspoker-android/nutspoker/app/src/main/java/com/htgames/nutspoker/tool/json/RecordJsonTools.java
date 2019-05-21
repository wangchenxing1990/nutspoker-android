package com.htgames.nutspoker.tool.json;

import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * 记录
 */
public class RecordJsonTools {
    /**
     * 获取历史牌局详情记录
     *
     * @param response
     * @return
     */
    public final static ArrayList<GameBillEntity> getRecordGameBillList(JSONObject response) {
        ArrayList<GameBillEntity> gameBillList = null;
        try {
            JSONObject data = response.getJSONObject("data");
            int page_total = data.optInt("page_total");
            JSONArray array = data.getJSONArray("games");
            int size = array.length();
            gameBillList = new ArrayList<GameBillEntity>(size);
            for (int i = 0; i < size; i++) {
                JSONObject gameJson = array.getJSONObject(i);
                GameBillEntity billEntity = getGameBillEntity(gameJson);
                if (billEntity != null) {
                    billEntity.jsonStr = (array.getString(i));
                }
                gameBillList.add(billEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameBillList;
    }

    public final static ArrayList<GameBillEntity> getRecordGameBillListByGids(JSONObject response) {
        ArrayList<GameBillEntity> gameBillList = null;
        try {
            JSONArray array = response.getJSONArray("data");
            int size = array.length();
            gameBillList = new ArrayList<GameBillEntity>(size);
            for (int i = 0; i < size; i++) {
                JSONObject gameJson = array.getJSONObject(i);
                GameBillEntity billEntity = getGameBillEntity(gameJson);
                if (billEntity != null) {
                    billEntity.jsonStr = (array.getString(i));
                }
                gameBillList.add(billEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameBillList;
    }


    /**
     * 解析GameBillEntity
     *
     * @param response
     * @return
     */
    public static GameBillEntity getGameBillEntity(JSONObject response) {
        GameBillEntity gameBillEntity = null;
        try {
            gameBillEntity = new GameBillEntity();
            gameBillEntity.jsonStr = (response.toString());
            GameEntity gameEntity = new GameEntity();
            //牌局信息
            gameEntity.play_mode = response.optInt(GameConstants.KEY_PLAY_MODE);
            gameEntity.gid = (response.optString("gid"));
            gameEntity.tid = (response.optString("tid"));
            gameEntity.name = (response.optString("name"));
            String horde_id = (response.optString("horde_id"));
            String horde_name = (response.optString("horde_name"));
            int club_channel = response.optInt("club_channel");
            gameEntity.horde_id = horde_id;
            gameEntity.horde_name = horde_name;
            gameEntity.club_channel = club_channel;
            //
            UserEntity creatorInfo = new UserEntity();
            creatorInfo.account = (response.optString(GameConstants.KEY_GAME_CREATOR_ID));
            creatorInfo.avatar = (response.optString(GameConstants.KEY_GAME_CREATOR_NICKNAME));
            gameEntity.creatorInfo = (creatorInfo);
            //
//            gameEntity.setBlindType(response.optInt(GameConstants.KEY_SMALL_BLINDS));
//            gameEntity.setTimeType(response.optInt(GameConstants.KEY_DURATIONS));
//            gameEntity.setAnteMode(response.optInt("ante_mode"));
//            gameEntity.setTiltMode(response.optInt("tilt_mode"));
            gameEntity.publicMode = (response.optInt("public_mode"));
            gameEntity.type = (response.optInt("type"));
            gameEntity.status = (response.optInt("status"));
            long createTime = response.optLong(GameConstants.KEY_GAME_CREATE_TIME);
            long endTime = response.optLong(GameConstants.KEY_GAME_END_TIME);
//            Log.d("record", "endTime:" + endTime);
            if (endTime == 0) {
                endTime = createTime;
            }
            gameEntity.createTime = (createTime);
            gameEntity.endTime = (endTime);//结束时间
            //
            int gameMode = response.optInt(GameConstants.KEY_GAME_MODE);
            gameEntity.gameMode = (gameMode);
            gameEntity.match_type = response.optInt("match_type");
            if (gameEntity.play_mode < GameConstants.PLAY_MODE_PINEAPPLE) {
                if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                    GameNormalConfig gameNormalConfig = new GameNormalConfig();
                    gameNormalConfig.blindType = (response.optInt(GameConstants.KEY_SMALL_BLINDS));
                    gameNormalConfig.timeType = (response.optInt(GameConstants.KEY_DURATIONS));
                    gameNormalConfig.anteMode = (response.optInt(GameConstants.KEY_GAME_MODE_ANTE));
                    gameNormalConfig.ante = (response.optInt(GameConstants.KEY_GAME_ANTE));
                    gameNormalConfig.tiltMode = (response.optInt(GameConstants.KEY_GAME_MODE_TILT));
                    gameNormalConfig.min_chips = response.optInt(GameConstants.KEY_GAME_MIN_BUY_CHIPS);
                    gameNormalConfig.max_chips = response.optInt(GameConstants.KEY_GAME_MAX_BUY_CHIPS);
                    gameNormalConfig.total_chips = response.optInt(GameConstants.KEY_GAME_TOTAL_BUY_CHIPS);
                    gameNormalConfig.check_ip = response.optInt(GameConstants.KEY_GAME_CHECK_IP);
                    gameNormalConfig.check_gps = response.optInt(GameConstants.KEY_GAME_CHECK_GPS);
                    gameNormalConfig.horde_id = horde_id;
                    gameNormalConfig.horde_name = horde_name;
                    gameNormalConfig.club_channel = club_channel;
                    gameEntity.gameConfig = (gameNormalConfig);
                } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                    GameSngConfigEntity gameSngConfigEntity = new GameSngConfigEntity();
                    gameSngConfigEntity.setChips(response.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                    gameSngConfigEntity.setPlayer(response.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                    gameSngConfigEntity.setDuration(response.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                    gameSngConfigEntity.setCheckInFee(response.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
                    gameSngConfigEntity.horde_id = horde_id;
                    gameSngConfigEntity.horde_name = horde_name;
                    gameSngConfigEntity.club_channel = club_channel;
                    gameEntity.gameConfig = (gameSngConfigEntity);
                } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                    GameMttConfig gameMttConfig = new GameMttConfig();
                    gameMttConfig.matchChips = (response.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                    gameMttConfig.matchPlayer = (response.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                    gameMttConfig.matchDuration = (response.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                    gameMttConfig.matchCheckinFee = (response.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
                    gameMttConfig.ko_mode = (response.optInt(GameConstants.KEY_GAME_KO_MODE));
                    gameMttConfig.ko_reward_rate = (response.optInt(GameConstants.KEY_GAME_KO_REWARD_RATE));
                    gameMttConfig.ko_head_rate = (response.optInt(GameConstants.KEY_GAME_KO_HEAD_RATE));
                    gameMttConfig.horde_id = horde_id;
                    gameMttConfig.horde_name = horde_name;
                    gameMttConfig.match_type = gameEntity.match_type;
                    gameMttConfig.club_channel = club_channel;
                    gameEntity.gameConfig = (gameMttConfig);
                } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    GameMtSngConfig gameMttConfig = new GameMtSngConfig();
                    gameMttConfig.matchChips = (response.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                    gameMttConfig.matchPlayer = (response.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                    gameMttConfig.matchDuration = (response.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                    gameMttConfig.matchCheckinFee = (response.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
                    gameMttConfig.totalPlayer = (response.optInt(GameConstants.KEY_GAME_TOTAL_PLAYER));
                    gameMttConfig.horde_id = horde_id;
                    gameMttConfig.horde_name = horde_name;
                    gameMttConfig.club_channel = club_channel;
                    gameEntity.gameConfig = (gameMttConfig);
                }
            } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                    PineappleConfig pineappleConfig = new PineappleConfig(response.optInt("play_type"));
                    pineappleConfig.setAnte(response.optInt(GameConstants.KEY_GAME_ANTE));
                    pineappleConfig.setChips(response.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                    pineappleConfig.setDuration(response.optInt(GameConstants.KEY_DURATIONS));
                    pineappleConfig.setIp_limit(response.optInt(GameConstants.KEY_GAME_CHECK_IP));
                    pineappleConfig.setGps_limit(response.optInt(GameConstants.KEY_GAME_CHECK_GPS));
                    pineappleConfig.setMatch_player(response.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                    pineappleConfig.setLimit_chips(response.optInt("limit_chips"));
                    pineappleConfig.setReady_time(response.optInt("ready_time"));
                    pineappleConfig.setDuration_index(response.optInt("duration"));
                    pineappleConfig.setHorde_id(horde_id);
                    pineappleConfig.setHorde_name(horde_name);
                    pineappleConfig.setClub_channel(club_channel);
                    pineappleConfig.setDeal_order(response.optInt(GameConstants.KEY_DEAL_ORDER));
                    gameEntity.gameConfig = pineappleConfig;
                } else if (gameMode == GameConstants.GAME_MODE_MTT) {
                    PineappleConfigMtt pineappleConfig = new PineappleConfigMtt(response.optInt("play_type"));
                    pineappleConfig.matchCheckinFee = (response.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));//报名费
                    pineappleConfig.matchChips = (response.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                    pineappleConfig.matchDuration = (response.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                    pineappleConfig.match_type = (gameEntity.match_type);
                    pineappleConfig.matchLevel = (response.optInt(GameConstants.KEY_GAME_MATCH_BLANDS_LEVEL));
                    pineappleConfig.restMode = (response.optInt(GameConstants.KEY_GAME_MATCH_MODE_REST));
                    pineappleConfig.beginTime = (response.optInt(GameConstants.KEY_GAME_MATCH_BEGIN_TIME));
                    pineappleConfig.match_max_buy_cnt = (response.optInt("match_max_buy_cnt"));
                    pineappleConfig.match_rebuy_cnt = (response.optInt("match_rebuy_cnt"));
                    pineappleConfig.is_auto = (response.optInt("is_auto"));
                    pineappleConfig.ko_mode = (response.optInt(GameConstants.KEY_GAME_KO_MODE));
                    pineappleConfig.ko_reward_rate = (response.optInt(GameConstants.KEY_GAME_KO_REWARD_RATE));
                    pineappleConfig.ko_head_rate = (response.optInt(GameConstants.KEY_GAME_KO_HEAD_RATE));
                    pineappleConfig.horde_id = horde_id;
                    pineappleConfig.horde_name = horde_name;
                    gameEntity.gameConfig = pineappleConfig;
                }
            }
            //
            gameBillEntity.gameInfo = (gameEntity);
            //数据清单
            gameBillEntity.maxPot = (response.optInt("max_pot"));
            gameBillEntity.allBuys = (response.optInt("all_reward"));//gameBillEntity.setAllBuys(response.optInt("all_buys"));和黄老师协商用all_reward
            gameBillEntity.bouts = (response.optInt("bouts"));
            gameBillEntity.winChip = (response.optInt("win_chips"));
            //MVP，大鱼，土豪
            gameBillEntity.mvp = (response.optString("mvp"));
            gameBillEntity.fish = (response.optString("fish"));
            gameBillEntity.richest = (response.optString("richest"));
            //SNG相关统计参数
            gameBillEntity.allReward = (response.optInt("all_reward"));
            gameBillEntity.totalTime = (response.optInt("total_time"));
            //MTT相关参数
            gameBillEntity.endSblindsIndex = (response.optInt("sblinds_index"));
            gameBillEntity.totalPlayer = (response.optInt("total_player"));
            //
            String myUid = response.optString(GameConstants.KEY_GAME_RECORD_MY_UID);
            gameBillEntity.myUid = (myUid);
            //
            if (!response.isNull("gameMembers")) {
                JSONArray membersArray = response.getJSONArray("gameMembers");
                gameBillEntity.jsonMemberStr = (membersArray.toString());//成员
                int memberSize = membersArray.length();
                ArrayList<GameMemberEntity> memberList = new ArrayList<GameMemberEntity>(memberSize);
                for (int j = 0; j < memberSize; j++) {
                    JSONObject memberJson = membersArray.getJSONObject(j);
                    GameMemberEntity gameMemberEntity = getGameMemberEntity(memberJson);
                    //获取我的数据
                    if (gameMemberEntity.userInfo.account.equals(myUid)) {
                        gameBillEntity.myMemberInfo = (gameMemberEntity);
                    }
                    memberList.add(gameMemberEntity);
                }
                gameBillEntity.gameMemberList = (memberList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameBillEntity;
    }

    /**
     * 解析成员列表
     * @param result
     * @return
     */
    public static ArrayList<GameMemberEntity> getGameMemberList(String result) {
        ArrayList<GameMemberEntity> gameMemberList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(result);
            if (!response.isNull("data")) {
                JSONArray membersArray = response.getJSONArray("data");
                if (membersArray != null) {
                    int memberSize = membersArray.length();
                    for (int j = 0; j < memberSize; j++) {
                        JSONObject memberJson = membersArray.getJSONObject(j);
                        GameMemberEntity gameMemberEntity = getGameMemberEntity(memberJson);
                        gameMemberList.add(gameMemberEntity);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameMemberList;
    }

    /**
     * 解析牌局信息中成员信息
     *
     * @return
     */
    public static GameMemberEntity getGameMemberEntity(JSONObject memberJson) {
        GameMemberEntity gameMemberEntity = null;
        try {
            gameMemberEntity = new GameMemberEntity();
            UserEntity userInfo = new UserEntity();
            userInfo.account = (memberJson.optString(GameConstants.KEY_UID));
            userInfo.name = (memberJson.optString(GameConstants.KEY_NICKNAME));
            userInfo.avatar = (memberJson.optString(GameConstants.KEY_AVATER));
            userInfo.uuid = memberJson.optString("uuid");
            if ("0".equals(userInfo.uuid)) {
                userInfo.uuid = "";
            }
            gameMemberEntity.userInfo = (userInfo);
            gameMemberEntity.totalBuy = (memberJson.optInt("total_buy"));
            gameMemberEntity.winChip = (memberJson.optInt("win_chips"));
            gameMemberEntity.premium = (memberJson.optInt("premium"));
            gameMemberEntity.insurance = (memberJson.optInt("insurance"));
            gameMemberEntity.joinCnt = (memberJson.optInt("join_cnt"));
            gameMemberEntity.enterPotCnt = (memberJson.optInt("enter_pot_cnt"));
            gameMemberEntity.winCnt = (memberJson.optInt("win_cnt"));
            //SNG.MTT
            gameMemberEntity.reward = (memberJson.optInt("reward"));
            gameMemberEntity.ranking = (memberJson.optInt("ranking"));
            //MT模式
            gameMemberEntity.rebuyCnt = (memberJson.optInt("rebuy_cnt"));
            gameMemberEntity.addonCnt = (memberJson.optInt("addon_cnt"));
            gameMemberEntity.blindsIndex = (memberJson.optInt("blinds_index"));//用户被淘汰时的盲注级别

            DecimalFormat df = new DecimalFormat("#.##");
            gameMemberEntity.ko_head_cnt = df.format(memberJson.optInt("ko_head_cnt") / 100f);

            gameMemberEntity.ko_head_reward = (memberJson.optInt("ko_head_reward"));//猎人赛人头奖金   MatchConstants.KO_HEAD_REWARD;
            gameMemberEntity.opt_user = memberJson.optString("opt_user");
            gameMemberEntity.opt_user_type = memberJson.optInt("opt_user_type");
            gameMemberEntity.fantasy_cnt = memberJson.optInt("fantasy_cnt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameMemberEntity;
    }
}
