package com.htgames.nutspoker.tool.json;

import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 游戏相关JSON解析类
 */
public class GameJsonTools {

    /**
     * 解析JSON成为GameEntity
     * @param jsonData
     * @return GameEntity
     * @throws JSONException
     */
    public final static GameEntity parseGameData(String jsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        GameEntity gameEntity = new GameEntity();
        gameEntity.play_mode = jsonObject.optInt(GameConstants.KEY_PLAY_MODE);
        gameEntity.horde_id = jsonObject.optString("horde_id", "");
        gameEntity.horde_name = jsonObject.optString("horde_name", "");
        gameEntity.room_id = jsonObject.optString("room_id","");
        gameEntity.uid = jsonObject.optString("owner", "");//gameEntity.uid = jsonObject.optString("uid", "");
        if (StringUtil.isSpace(gameEntity.uid)) {
            gameEntity.uid = jsonObject.optString("uid", "");
        }
        if (jsonObject.has(GameConstants.KEY_GAME_IS_ADMIN)) {
            gameEntity.is_admin = jsonObject.optInt(GameConstants.KEY_GAME_IS_ADMIN);
        }
        gameEntity.deskItemTag = (GameEntity.DeskItemTag.NORMAL);
        gameEntity.tid = (jsonObject.optString(GameConstants.KEY_GAME_TEADID));
        gameEntity.gid = (jsonObject.optString(GameConstants.KEY_GAME_GID));
        gameEntity.name = (jsonObject.optString(GameConstants.KEY_GAME_NAME));
        gameEntity.code = jsonObject.optString(GameConstants.KEY_GAME_CODE);
        gameEntity.type = (jsonObject.optInt(GameConstants.KEY_GAME_TYPE));
        gameEntity.status = (jsonObject.optInt(GameConstants.KEY_GAME_STATUS));
        //
        gameEntity.createTime = (jsonObject.optLong(GameConstants.KEY_GAME_CREATE_TIME));
        gameEntity.publicMode = (jsonObject.optInt(GameConstants.KEY_GAME_MODE_PUBLIC));
        //
        gameEntity.checkinPlayerCount = (jsonObject.optInt(GameConstants.KEY_GAME_CEHCKIN_PLAYER_COUNT));
        gameEntity.currentServerTime = (jsonObject.optLong(GameConstants.KEY_GAME_CURRENT_SERVERTIME));
        gameEntity.gamerCount = (jsonObject.optInt(GameConstants.KEY_GAME_GAMER_COUNT));
        gameEntity.club_channel = jsonObject.optInt("club_channel");
        gameEntity.match_type = jsonObject.optInt("match_type");
        gameEntity.activity = jsonObject.optInt("activity");
        gameEntity.start_time = jsonObject.optLong("start_time");//只有普通局才用这个字段，sng和mtt不用这个字段，用来判断游戏内是否点击开始，当且仅当游戏内点击开始了这个字段才有值
        int isCheckin = jsonObject.optInt(GameConstants.KEY_GAME_IS_CHECKIN);
        if (isCheckin == 1) {
            gameEntity.isCheckin = (true);
        } else {
            gameEntity.isCheckin = (false);
        }
        //
        int gameMode = jsonObject.optInt(GameConstants.KEY_GAME_MODE);
        gameEntity.gameMode = (gameMode);
        if (gameEntity.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || gameEntity.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            if(gameMode == GameConstants.GAME_MODE_NORMAL) {
                GameNormalConfig gameNormalConfig = new GameNormalConfig();
                gameNormalConfig.blindType = (jsonObject.optInt(GameConstants.KEY_SMALL_BLINDS));
                gameNormalConfig.timeType = (jsonObject.optInt(GameConstants.KEY_DURATIONS));
                if (jsonObject.has("remain_time")) {
                    gameNormalConfig.timeType = (jsonObject.optInt("remain_time"));//持续时间 加上了 暂停时间, 有时候不会传这个字段
                }
                gameNormalConfig.anteMode = (jsonObject.optInt(GameConstants.KEY_GAME_MODE_ANTE));
                gameNormalConfig.ante = (jsonObject.optInt(GameConstants.KEY_GAME_ANTE));
                gameNormalConfig.tiltMode = ((int) jsonObject.optLong(GameConstants.KEY_GAME_MODE_TILT));
                gameNormalConfig.matchChips = (int) jsonObject.optLong(GameConstants.KEY_GAME_MATCH_CHIPS);
                gameNormalConfig.matchPlayer = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                gameNormalConfig.min_chips = jsonObject.optInt(GameConstants.KEY_GAME_MIN_BUY_CHIPS);
                gameNormalConfig.max_chips = jsonObject.optInt(GameConstants.KEY_GAME_MAX_BUY_CHIPS);
                gameNormalConfig.total_chips = jsonObject.optInt(GameConstants.KEY_GAME_TOTAL_BUY_CHIPS);
                gameNormalConfig.check_ip = jsonObject.optInt(GameConstants.KEY_GAME_CHECK_IP);
                gameNormalConfig.check_gps = jsonObject.optInt(GameConstants.KEY_GAME_CHECK_GPS);
                gameEntity.gameConfig = gameNormalConfig;
            } else if (gameMode == GameConstants.GAME_MODE_SNG) {
                GameSngConfigEntity gameSngConfigEntity = new GameSngConfigEntity();
                gameSngConfigEntity.chips = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                gameSngConfigEntity.player = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                gameSngConfigEntity.duration = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                gameSngConfigEntity.checkInFee = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
                gameSngConfigEntity.check_ip = jsonObject.optInt(GameConstants.KEY_GAME_CHECK_IP);
                gameSngConfigEntity.check_gps = jsonObject.optInt(GameConstants.KEY_GAME_CHECK_GPS);
                gameEntity.gameConfig = (gameSngConfigEntity);
            } else if(gameMode == GameConstants.GAME_MODE_MTT) {
                GameMttConfig gameMttConfig = new GameMttConfig();
                gameMttConfig.matchChips = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHIPS);
                gameMttConfig.match_type = gameEntity.match_type;
                gameMttConfig.matchPlayer = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_PLAER);
                gameMttConfig.matchDuration = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_DURATION);
                gameMttConfig.matchLevel = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_BLANDS_LEVEL);
                gameMttConfig.rebuyMode = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_REBUY);
                gameMttConfig.addonMode = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_ADDON);
                gameMttConfig.restMode = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_MODE_REST);
                gameMttConfig.beginTime = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_BEGIN_TIME);
                gameMttConfig.matchCheckinFee = jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE);
                gameMttConfig.match_max_buy_cnt = jsonObject.optInt("match_max_buy_cnt");
                gameMttConfig.match_rebuy_cnt = jsonObject.optInt("match_rebuy_cnt");
                gameMttConfig.sblinds_mode = jsonObject.optInt("sblinds_mode");
                gameMttConfig.start_sblinds = jsonObject.optInt("start_sblinds");
                gameMttConfig.is_auto = jsonObject.optInt("is_auto");
                gameMttConfig.ko_mode = jsonObject.optInt(GameConstants.KEY_GAME_KO_MODE);
                gameMttConfig.ko_reward_rate = jsonObject.optInt(GameConstants.KEY_GAME_KO_REWARD_RATE);
                gameMttConfig.ko_head_rate = jsonObject.optInt(GameConstants.KEY_GAME_KO_HEAD_RATE);
                gameEntity.gameConfig = gameMttConfig;
            } else if(gameMode == GameConstants.GAME_MODE_MT_SNG) {
                GameMtSngConfig gameMtSngConfig = new GameMtSngConfig();
                gameMtSngConfig.matchChips = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                gameMtSngConfig.matchCheckinFee = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
                gameMtSngConfig.matchPlayer = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                gameMtSngConfig.matchDuration = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                gameMtSngConfig.totalPlayer = (jsonObject.optInt(GameConstants.KEY_GAME_TOTAL_PLAYER));
                gameEntity.gameConfig = gameMtSngConfig;
            }
        } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            if (gameMode == GameConstants.GAME_MODE_MTT) {
                PineappleConfigMtt pineappleConfig = new PineappleConfigMtt(jsonObject.optInt("play_type"));
                pineappleConfig.matchCheckinFee = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));//报名费
                pineappleConfig.matchChips = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                pineappleConfig.matchDuration = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
                pineappleConfig.match_type = (gameEntity.match_type);
                pineappleConfig.matchLevel = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_BLANDS_LEVEL));
                pineappleConfig.restMode = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_MODE_REST));
                pineappleConfig.beginTime = (jsonObject.optInt(GameConstants.KEY_GAME_MATCH_BEGIN_TIME));
                pineappleConfig.match_max_buy_cnt = (jsonObject.optInt("match_max_buy_cnt"));
                pineappleConfig.match_rebuy_cnt = (jsonObject.optInt("match_rebuy_cnt"));
                pineappleConfig.is_auto = (jsonObject.optInt("is_auto"));
                pineappleConfig.ko_mode = (jsonObject.optInt(GameConstants.KEY_GAME_KO_MODE));
                pineappleConfig.ko_reward_rate = (jsonObject.optInt(GameConstants.KEY_GAME_KO_REWARD_RATE));
                pineappleConfig.ko_head_rate = (jsonObject.optInt(GameConstants.KEY_GAME_KO_HEAD_RATE));
                gameEntity.gameConfig = pineappleConfig;
            } else if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                PineappleConfig pineappleConfig = new PineappleConfig(jsonObject.optInt("play_type"));
                pineappleConfig.setAnte(jsonObject.optInt(GameConstants.KEY_GAME_ANTE));
                pineappleConfig.setChips(jsonObject.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
                pineappleConfig.setDuration(jsonObject.optInt(GameConstants.KEY_DURATIONS));
                if (jsonObject.has("remain_time")) {
                    pineappleConfig.setDuration(jsonObject.optInt("remain_time"));//持续时间 加上了 暂停时间, 有时候不会传这个字段
                }
                pineappleConfig.setIp_limit(jsonObject.optInt(GameConstants.KEY_GAME_CHECK_IP));
                pineappleConfig.setGps_limit(jsonObject.optInt(GameConstants.KEY_GAME_CHECK_GPS));
                pineappleConfig.setMatch_player(jsonObject.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
                pineappleConfig.setLimit_chips(jsonObject.optInt("limit_chips"));
                pineappleConfig.setReady_time(jsonObject.optInt("ready_time"));
                pineappleConfig.setDuration_index(jsonObject.optInt("duration"));
                pineappleConfig.setDeal_order(jsonObject.optInt(GameConstants.KEY_DEAL_ORDER));
                gameEntity.gameConfig = pineappleConfig;
            }
        }
        UserEntity creatorInfo = new UserEntity();
        creatorInfo.account = (gameEntity.uid);
        creatorInfo.avatar = (jsonObject.optString(GameConstants.KEY_AVATER));
        creatorInfo.name = (jsonObject.optString(GameConstants.KEY_NICKNAME));//这个字段可能是空
        if (StringUtil.isSpaceOrZero(creatorInfo.name)) {
            creatorInfo.name = (jsonObject.optString(GameConstants.KEY_GAME_CREATOR_NICKNAME));
        }
        gameEntity.creatorInfo = (creatorInfo);
        gameEntity.serverIp = (jsonObject.optString(GameConstants.KEY_GAME_SERVER));//游戏服务器
        return gameEntity;
    }

    /**
     * 进行中的牌局
     * @param teamId
     * @param response
     * @return
     */
    public final static ArrayList<GameEntity> getGamePlayingList(String teamId, JSONObject response) {
        ArrayList<GameEntity> gameList = new ArrayList<GameEntity>();
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray gameArray = data.getJSONArray("games");
            int size = gameArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject gameJson = gameArray.getJSONObject(i);
                GameEntity gameEntity = parseGameData(gameJson.toString());
                gameEntity.tid = (teamId);
                gameList.add(gameEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameList;
    }

    public final static ArrayList<GameEntity> getGamePlayingList(String teamId, JSONObject response, ArrayList<GameEntity> gameListTexas, ArrayList<GameEntity> gameListOmaha, ArrayList<GameEntity> gameListPineapple,
                                                                 ArrayList<GameEntity> gameListMtt, ArrayList<GameEntity> gameListSng) {
        ArrayList<GameEntity> gameList = new ArrayList<GameEntity>();
        try {
            JSONObject data = response.getJSONObject("data");
            JSONArray gameArray = data.getJSONArray("games");
            int size = gameArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject gameJson = gameArray.getJSONObject(i);
                GameEntity gameEntity = parseGameData(gameJson.toString());
                gameEntity.tid = (teamId);
                gameList.add(gameEntity);
                if (gameEntity.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    if (gameEntity.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
                        gameListTexas.add(gameEntity);
                    } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                        gameListOmaha.add(gameEntity);
                    } else if (gameEntity.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
                        gameListPineapple.add(gameEntity);
                    }
                }
                if (gameEntity.gameMode == GameConstants.GAME_MODE_SNG) {
                    gameListSng.add(gameEntity);
                } else if (gameEntity.gameMode == GameConstants.GAME_MODE_MTT) {
                    gameListMtt.add(gameEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameList;
    }

    /**
     * 最近参与的牌局
     *
     * @param response
     * @return
     */
    public final static ArrayList<GameEntity> getHistoryGameList(JSONObject response) {
        ArrayList<GameEntity> gameList = new ArrayList<GameEntity>();
        try {
            JSONArray gameArray = response.getJSONArray("data");
            int size = gameArray.length();
            for (int i = 0; i < size; i++) {
                JSONObject gameJson = gameArray.getJSONObject(i);
                int gameMode = gameJson.optInt(GameConstants.KEY_GAME_MODE);
                long start_time = gameJson.optLong("start_time");
//                if (gameMode == GameConstants.GAME_MODE_NORMAL && start_time > 0) {
//                    long passTime = /*gameJson.optLong(GameConstants.KEY_GAME_CURRENT_SERVERTIME)*/start_time - gameJson.optLong(GameConstants.KEY_GAME_CREATE_TIME);//过去的时间
//                    int remainingTime = gameJson.optInt(GameConstants.KEY_DURATIONS) / 60 - (int) (passTime / 60);//剩余的时间
//                    if (remainingTime <= 0) {
//                        continue;//已经结束,过滤掉
//                    }
//                }
                GameEntity gameEntity = parseGameData(gameJson.toString());
                gameEntity.entryTime = (gameJson.optLong(GameConstants.KEY_GAME_CURRENT_ENTRYTIME));
                gameEntity.isInvited = (gameJson.optInt(GameConstants.KEY_GAME_ISINVITED));
                gameEntity.unReadMsgCount = (0);
                gameEntity.tag = (GameConstants.GAME_RECENT_TAG_NORMAL);
                gameList.add(gameEntity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gameList;
    }

}
