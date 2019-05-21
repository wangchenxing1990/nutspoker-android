package com.htgames.nutspoker.ui.activity.Hands.tool;

import android.text.TextUtils;

import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 */
public class HandJsonTools {
    /**
     * 解析本地的牌谱
     * @param data
     * @return
     */
    public static PaipuEntity getPaipuEntity(String data) throws JSONException {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        PaipuEntity paipuEntity = null;
        paipuEntity = new PaipuEntity();
        JSONObject paipuJson = new JSONObject(data);
        //
        GameEntity gameEntity = new GameEntity();
        gameEntity.tid = (paipuJson.optString(GameConstants.KEY_GAME_TEADID));
        gameEntity.gid = (paipuJson.optString(GameConstants.KEY_GAME_GID));
        gameEntity.name = (paipuJson.optString(GameConstants.KEY_GAME_NAME));
        gameEntity.status = (paipuJson.optInt(GameConstants.KEY_GAME_STATUS));
        gameEntity.code = (paipuJson.optString(GameConstants.KEY_GAME_CODE));
        gameEntity.type = (paipuJson.optInt(GameConstants.KEY_GAME_TYPE));
        gameEntity.createTime = (paipuJson.optLong(GameConstants.KEY_GAME_CREATE_TIME));
        int blind = paipuJson.optInt(GameConstants.KEY_SMALL_BLINDS);//大小盲这边不是type，为了统一，转为type
        gameEntity.publicMode = (paipuJson.optInt(GameConstants.KEY_GAME_MODE_PUBLIC));
        //
        int gameMode = paipuJson.optInt(GameConstants.KEY_GAME_MODE);
        LogUtil.i("gameMode" , "gameMode : " + gameMode);
        gameEntity.gameMode = (gameMode);
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            GameNormalConfig gameNormalConfig = new GameNormalConfig();
            gameNormalConfig.blindType = (blind);
            gameNormalConfig.timeType = (paipuJson.optInt(GameConstants.KEY_DURATIONS));
            gameNormalConfig.anteMode = (paipuJson.optInt(GameConstants.KEY_GAME_MODE_ANTE));
            gameNormalConfig.ante = (paipuJson.optInt(GameConstants.KEY_GAME_ANTE));
            gameNormalConfig.tiltMode = (paipuJson.optInt(GameConstants.KEY_GAME_MODE_TILT));
            gameEntity.gameConfig = (gameNormalConfig);
        } else if (gameMode == GameConstants.GAME_MODE_SNG) {
            GameSngConfigEntity gameSngConfigEntity = new GameSngConfigEntity();
            gameSngConfigEntity.setChips(paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
            gameSngConfigEntity.setPlayer(paipuJson.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
            gameSngConfigEntity.setDuration(paipuJson.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
            gameSngConfigEntity.setCheckInFee(paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
            gameEntity.gameConfig = (gameSngConfigEntity);
        } else if(gameMode == GameConstants.GAME_MODE_MTT) {
            GameMttConfig gameMttConfig = new GameMttConfig();
            gameMttConfig.matchChips = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
            gameMttConfig.matchCheckinFee = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
            gameMttConfig.beginTime = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_BEGIN_TIME));
            gameMttConfig.matchPlayer = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
            gameMttConfig.matchLevel = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_BLANDS_LEVEL));
            gameMttConfig.matchDuration = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
            gameMttConfig.addonMode = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_ADDON));
            gameMttConfig.rebuyMode = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_REBUY));
            gameMttConfig.restMode = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_MODE_REST));
            gameEntity.gameConfig = (gameMttConfig);
        } else if(gameMode == GameConstants.GAME_MODE_MT_SNG) {
            GameMtSngConfig gameMtSngConfig = new GameMtSngConfig();
            gameMtSngConfig.matchChips = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHIPS));
            gameMtSngConfig.matchCheckinFee = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE));
            gameMtSngConfig.matchPlayer = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_PLAER));
            gameMtSngConfig.matchDuration = (paipuJson.optInt(GameConstants.KEY_GAME_MATCH_DURATION));
            gameMtSngConfig.totalPlayer = (paipuJson.optInt(GameConstants.KEY_GAME_TOTAL_PLAYER));
            gameEntity.gameConfig = (gameMtSngConfig);
        }
        //
        UserEntity creatorInfo = new UserEntity();
        creatorInfo.account = (paipuJson.optString(GameConstants.KEY_GAME_CREATOR_ID));
        gameEntity.creatorInfo = (creatorInfo);
        //
        paipuEntity.gameEntity = (gameEntity);
        //
        paipuEntity.handsCnt = (paipuJson.optInt(PaipuConstants.KEY_HANDS_CNT));
        paipuEntity.sheetUid = (paipuJson.optString(PaipuConstants.KEY_SHEET_UID));
        paipuEntity.winChip = (paipuJson.optInt(PaipuConstants.KEY_WIN_CHIPS));
        paipuEntity.cardType = (paipuJson.optInt(PaipuConstants.KEY_CARD_TYPE));
        //
        JSONArray handCardsArray = paipuJson.optJSONArray(PaipuConstants.KEY_HAND_CARDS);
        if (handCardsArray != null) {
            ArrayList<Integer> handCards = new ArrayList<Integer>();
            for (int i = 0; i < handCardsArray.length(); i++) {
                handCards.add(handCardsArray.optInt(i));
            }
            paipuEntity.handCards = (handCards);
        }
        //
        JSONArray poolCardsArray = paipuJson.optJSONArray(PaipuConstants.KEY_POOL_CARDS);
        if (poolCardsArray != null) {
            ArrayList<Integer> poolCards = new ArrayList<Integer>();
            for (int i = 0; i < poolCardsArray.length(); i++) {
                poolCards.add(poolCardsArray.optInt(i));
            }
            paipuEntity.poolCards = (poolCards);
        }
        JSONArray cardTypeCardsArray = paipuJson.optJSONArray(PaipuConstants.KEY_CARDTYPE_CARDS);
        if (cardTypeCardsArray != null) {
            ArrayList<Integer> cardTypeCards = new ArrayList<Integer>();
            for (int i = 0; i < cardTypeCardsArray.length(); i++) {
                cardTypeCards.add(cardTypeCardsArray.optInt(i));
            }
            paipuEntity.cardTypeCards = (cardTypeCards);
        }
        //
        paipuEntity.node = (paipuJson.optString(GameConstants.KEY_GAME_GID));
        paipuEntity.jsonDataStr = (data);
        return paipuEntity;
    }

    /**
     * 解析收藏牌局列表
     * @param result
     * @return
     */
    public static ArrayList<PaipuEntity> getCollectPaipuList(String result){
        ArrayList<PaipuEntity> paipuList = null;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray urlArray = jsonObject.optJSONArray("data");
            if (urlArray != null) {
                int size = urlArray.length();
                paipuList = new ArrayList<PaipuEntity>(size);
                for (int i = 0; i < size; i++) {
                    JSONObject item = urlArray.getJSONObject(i);
                    PaipuEntity paipuEntity = HandJsonTools.getPaipuEntity(item.toString());
                    String filePath = item.optString(PaipuConstants.KEY_FILE_NET_PATH);
                    String fileName = item.optString(PaipuConstants.KEY_FILE_NAME);
                    paipuEntity.handsId = (item.optString(PaipuConstants.KEY_HID));
                    paipuEntity.collectCount = (item.optInt(PaipuConstants.KEY_COLLECT_COUNT));
                    paipuEntity.fileName = (fileName);
                    paipuEntity.fileNetPath = (filePath);
                    paipuEntity.isCollect = (true);
                    paipuList.add(paipuEntity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paipuList;
    }
}
