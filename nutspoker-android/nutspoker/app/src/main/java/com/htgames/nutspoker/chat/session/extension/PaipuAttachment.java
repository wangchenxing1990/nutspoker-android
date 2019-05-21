package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;


import java.util.ArrayList;
import java.util.List;

/**
 * 牌谱附件
 */
public class PaipuAttachment extends CustomAttachment{
    private final static String TAG = "PaipuAttachment";
    String gid;
    String tid;
    String name;
    String owner;
    int sblinds;
    int durations;
    int public_mode;
    int ante_mode;
    int ante;
    int tilt_mode;
    int type;
    int status;
    long create_time;
    int win_chips;
    int card_type;
    ArrayList<Integer> handCards;
    ArrayList<Integer> poolCards;
    //新增加
    String sheetUid;//谁的手牌
    int handsCnt;//第几手牌
    ArrayList<Integer> cardTypeCards;
    String fileName;//文件名称
    String fileNetPath;//文件网络路径
    //
    int gameMode = 0;
    int play_mode = 0;
    int matchChips = 0;///SNG牌局带入记分牌
    int matchPlayer = 0;///
    int matchDuration = 0;//SNG牌局涨盲时间
    int matchCheckinFee = 0;//
//    int totalPlayer = 0;//比赛总人数
    //
    String data = "";

    //新增hid
    String hid = "";

    public PaipuAttachment(String data) {
        super(CustomAttachmentType.Paipu);
        this.data = data;
        parseData(JSON.parseObject(data));
    }

    // 解析开始牌局类型具体数据
    @Override
    protected void parseData(JSONObject data) {
        LogUtil.i(TAG, data.toString());
        gid = data.getString(GameConstants.KEY_GAME_GID);
        tid = data.getString(GameConstants.KEY_GAME_TEADID);
        name = data.getString(GameConstants.KEY_GAME_NAME);
        owner = data.getString(GameConstants.KEY_GAME_CREATOR_ID);
        sblinds = data.getIntValue(GameConstants.KEY_SMALL_BLINDS);
        durations = data.getIntValue(GameConstants.KEY_DURATIONS);
        public_mode = data.getIntValue(GameConstants.KEY_GAME_MODE_PUBLIC);
        ante_mode = data.getIntValue(GameConstants.KEY_GAME_MODE_ANTE);
        ante = data.getIntValue(GameConstants.KEY_GAME_ANTE);
        tilt_mode = data.getIntValue(GameConstants.KEY_GAME_MODE_TILT);
        type = data.getIntValue(GameConstants.KEY_GAME_TYPE);
        status = data.getIntValue(GameConstants.KEY_GAME_STATUS);
        create_time = data.getLongValue(GameConstants.KEY_GAME_CREATE_TIME);
        //新加字段
        sheetUid = data.getString(PaipuConstants.KEY_SHEET_UID);
        handsCnt = data.getIntValue(PaipuConstants.KEY_HANDS_CNT);
        fileName = data.getString(PaipuConstants.KEY_FILE_NAME);
        fileNetPath = data.getString(PaipuConstants.KEY_FILE_NET_PATH);
        //
        //新增hid
        hid = data.getString(PaipuConstants.KEY_HID);
        handCards = new ArrayList<Integer>();
        try {
            if(data.containsKey("hand_cards")){
                JSONArray handCardsArray = data.getJSONArray("hand_cards");
                if(handCardsArray != null && handCardsArray.size() != 0){
                    for(int i = 0; i < handCardsArray.size() ; i++){
                        handCards.add(handCardsArray.getIntValue(i));
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        poolCards = new ArrayList<Integer>();
        try {
            if (data.containsKey("pool_cards")) {
                JSONArray poolCardsArray = data.getJSONArray("pool_cards");
                if (poolCardsArray != null && poolCardsArray.size() != 0) {
                    for (int i = 0; i < poolCardsArray.size(); i++) {
                        poolCards.add(poolCardsArray.getIntValue(i));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cardTypeCards = new ArrayList<Integer>();
        try {
            if (data.containsKey(PaipuConstants.KEY_CARDTYPE_CARDS)) {
                JSONArray cardTypeCardsArray = data.getJSONArray(PaipuConstants.KEY_CARDTYPE_CARDS);
                if (cardTypeCardsArray != null && cardTypeCardsArray.size() != 0) {
                    for (int i = 0; i < cardTypeCardsArray.size(); i++) {
                        cardTypeCards.add(cardTypeCardsArray.getIntValue(i));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //
        win_chips = data.getIntValue("win_chips");
        card_type = data.getIntValue("card_type");
        //
        gameMode = data.getIntValue(GameConstants.KEY_GAME_MODE);
        play_mode = data.getIntValue(GameConstants.KEY_PLAY_MODE);
        matchChips = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHIPS);
        matchPlayer = data.getIntValue(GameConstants.KEY_GAME_MATCH_PLAER);
        matchDuration = data.getIntValue(GameConstants.KEY_GAME_MATCH_DURATION);
        matchCheckinFee = data.getIntValue(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE);
//        totalPlayer = data.getIntValue(GameConstants.KEY_GAME_TOTAL_PLAYER);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put("gid", gid);
        data.put("tid", tid);
        data.put("name", name);
        data.put("owner", owner);
        data.put("sblinds", sblinds);
        data.put("durations", durations);
        data.put("public_mode", public_mode);
        data.put("ante_mode", ante_mode);
        data.put(GameConstants.KEY_GAME_ANTE, ante);
        data.put("tilt_mode", tilt_mode);
        data.put("type", type);
        data.put("status", status);
        data.put("create_time", create_time);
        data.put("win_chips", win_chips);
        data.put("card_type", card_type);
        //新加字段
        data.put(PaipuConstants.KEY_SHEET_UID, sheetUid);
        data.put(PaipuConstants.KEY_HANDS_CNT, handsCnt);
        data.put(PaipuConstants.KEY_FILE_NAME, fileName);
        data.put(PaipuConstants.KEY_FILE_NET_PATH, fileNetPath);
        //
        data.put(GameConstants.KEY_GAME_MODE, gameMode);
        data.put(GameConstants.KEY_PLAY_MODE, play_mode);
        data.put(GameConstants.KEY_GAME_MATCH_CHIPS, matchChips);
        data.put(GameConstants.KEY_GAME_MATCH_PLAER, matchPlayer);
        data.put(GameConstants.KEY_GAME_MATCH_DURATION, matchDuration);
        data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, matchCheckinFee);
        //
        //新增hid
        data.put(PaipuConstants.KEY_HID,hid);

        JSONArray handCardArray = new JSONArray();
        if(handCards != null && handCards.size() != 0){
            for(int cards : handCards){
                handCardArray.add(cards);
            }
        }
        data.put("hand_cards" , handCardArray);
        //
        JSONArray poolCardArray = new JSONArray();
        if(poolCards != null && poolCards.size() != 0){
            for(int cards : poolCards){
                poolCardArray.add(cards);
            }
        }
        data.put("pool_cards" , poolCardArray);
        //
        JSONArray cardTypeCardArray = new JSONArray();
        if(cardTypeCards != null && cardTypeCards.size() != 0){
            for(int cards : cardTypeCards){
                cardTypeCardArray.add(cards);
            }
        }
        data.put(PaipuConstants.KEY_CARDTYPE_CARDS , cardTypeCardArray);
        return data;
    }

    public static JSONObject packToJsonData(PaipuEntity paipuEntity) {
        JSONObject data = new JSONObject();
        GameEntity gameInfo = paipuEntity.gameEntity;
        data.put("gid", paipuEntity.gameEntity.gid);
        data.put("tid", paipuEntity.gameEntity.tid);
        data.put("name", paipuEntity.gameEntity.name);
        data.put("owner", paipuEntity.gameEntity.creatorInfo.account);
        data.put("public_mode", paipuEntity.gameEntity.publicMode);
        data.put("type", paipuEntity.gameEntity.type);
        data.put("status", paipuEntity.gameEntity.status);
        data.put("create_time", paipuEntity.gameEntity.createTime);
        data.put("win_chips", paipuEntity.winChip);
        data.put("card_type", paipuEntity.cardType);
        //
        int gameMode = paipuEntity.gameEntity.gameMode;
        data.put("game_mode" , gameMode);
        if (gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
            //普通模式
            GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
            data.put(GameConstants.KEY_SMALL_BLINDS, gameConfig.blindType);
            data.put(GameConstants.KEY_DURATIONS, gameConfig.timeType);
            data.put(GameConstants.KEY_GAME_MODE_ANTE, gameConfig.anteMode);
            data.put(GameConstants.KEY_GAME_ANTE, gameConfig.ante);
            data.put(GameConstants.KEY_GAME_MODE_TILT, gameConfig.tiltMode);
        } else if (gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
            //SNG模式
            GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameConfig.getChips());
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameConfig.getPlayer());
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameConfig.getDuration());
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameConfig.getCheckInFee());
        } else if(gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
            GameMttConfig gameMttConfig = (GameMttConfig) gameInfo.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameMttConfig.matchChips);
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameMttConfig.matchPlayer);
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameMttConfig.matchDuration);
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameMttConfig.matchCheckinFee);
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameInfo.gameConfig;
            data.put(GameConstants.KEY_GAME_MATCH_CHIPS, gameMtSngConfig.matchChips);
            data.put(GameConstants.KEY_GAME_MATCH_PLAER, gameMtSngConfig.matchPlayer);
            data.put(GameConstants.KEY_GAME_MATCH_DURATION, gameMtSngConfig.matchDuration);
            data.put(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, gameMtSngConfig.matchCheckinFee);
            data.put(GameConstants.KEY_GAME_TOTAL_PLAYER, gameMtSngConfig.totalPlayer);
        }
        //新加字段
        data.put(PaipuConstants.KEY_SHEET_UID, paipuEntity.sheetUid);
        data.put(PaipuConstants.KEY_HANDS_CNT, paipuEntity.handsCnt);
        data.put(PaipuConstants.KEY_FILE_NAME, paipuEntity.fileName);
        data.put(PaipuConstants.KEY_FILE_NET_PATH, paipuEntity.fileNetPath);
        //新增字段
        data.put(PaipuConstants.KEY_HID,paipuEntity.handsId);

        JSONArray handCardArray = new JSONArray();
        List<Integer> handCards = paipuEntity.handCards;
        if(handCards != null && handCards.size() != 0){
            for(int cards : handCards){
                handCardArray.add(cards);
            }
        }
        data.put("hand_cards" , handCardArray);
        //
        List<Integer> poolCards = paipuEntity.poolCards;
        JSONArray poolCardArray = new JSONArray();
        if(poolCards != null && poolCards.size() != 0){
            for(int cards : poolCards){
                poolCardArray.add(cards);
            }
        }
        data.put("pool_cards" , poolCardArray);
        //
        List<Integer> cardTypeCards = paipuEntity.cardTypeCards;
        JSONArray cardTypeCardArray = new JSONArray();
        if(cardTypeCards != null && cardTypeCards.size() != 0){
            for(int cards : cardTypeCards){
                cardTypeCardArray.add(cards);
            }
        }
        data.put(PaipuConstants.KEY_CARDTYPE_CARDS , cardTypeCardArray);
        return data;
    }

    /**
     * 获取值
     * @return
     */
    public PaipuEntity getValue() {
        PaipuEntity paipuEntity = new PaipuEntity();
        GameEntity gameEntity = new GameEntity();
        //牌局信息
        gameEntity.gid = (gid);
        gameEntity.tid = (tid);
        gameEntity.name = (name);
        UserEntity creatorInfo = new UserEntity();
        creatorInfo.account = (owner);
        gameEntity.creatorInfo = (creatorInfo);
        gameEntity.publicMode = (public_mode);
        gameEntity.type = (type);
        gameEntity.status = (status);
        gameEntity.createTime = (create_time);
        //
        gameEntity.gameMode = (gameMode);
        gameEntity.play_mode = (play_mode);
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            GameNormalConfig gameNormalConfig = new GameNormalConfig();
            gameNormalConfig.blindType = (sblinds);
            gameNormalConfig.timeType = (durations);
            gameNormalConfig.anteMode = (ante_mode);
            gameNormalConfig.ante = (ante);
            gameNormalConfig.tiltMode = (tilt_mode);
            gameEntity.gameConfig = (gameNormalConfig);
        } else if(gameMode == GameConstants.GAME_MODE_SNG) {
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
        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
            GameMtSngConfig gameConfig = new GameMtSngConfig();
            gameConfig.matchChips = (matchChips);
            gameConfig.matchPlayer = (matchPlayer);
            gameConfig.matchDuration = (matchDuration);
            gameConfig.matchCheckinFee = (matchCheckinFee);
            gameConfig.totalPlayer = (matchPlayer);
            gameEntity.gameConfig = (gameConfig);
        }
        paipuEntity.gameEntity = (gameEntity);
        //
        paipuEntity.handCards = (handCards);
        paipuEntity.poolCards = (poolCards);
        //新加字段
        paipuEntity.sheetUid = (sheetUid);
        paipuEntity.handsCnt = (handsCnt);
        paipuEntity.fileNetPath = (fileNetPath);
        paipuEntity.fileName = (fileName);
        paipuEntity.cardTypeCards = (cardTypeCards);
        //
        paipuEntity.winChip = (win_chips);
        paipuEntity.cardType = (card_type);
        paipuEntity.jsonDataStr = (data);
        paipuEntity.handsId = (hid);
        return paipuEntity;
    }
}
