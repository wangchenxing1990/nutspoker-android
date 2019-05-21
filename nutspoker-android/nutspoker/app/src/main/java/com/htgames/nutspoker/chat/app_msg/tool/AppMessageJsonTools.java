package com.htgames.nutspoker.chat.app_msg.tool;

import android.text.TextUtils;

import com.htgames.nutspoker.chat.app_msg.attach.AppNotify;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 20150726 on 2016/4/21.
 */
public class AppMessageJsonTools {
    private final static String TAG = "AppMessageJsonTools";

    public static AppMessage parseAppMessage(String data) {
        AppMessage appMessage = null;
        LogUtil.i(TAG, "data:" + data);
        try {
            appMessage = new AppMessage();
            JSONObject jsonObject = new JSONObject(data);
            String fromId = jsonObject.optString(AppMessageConstants.KEY_FROM_ID, "");
            String targetId = jsonObject.optString(AppMessageConstants.KEY_TARGET_ID, "");
            appMessage.checkinPlayerId = fromId;
            appMessage.content = (jsonObject.optString(AppMessageConstants.KEY_CONTENT, ""));
            long time = jsonObject.optLong(AppMessageConstants.KEY_TIME, 0);
            int type = jsonObject.optInt(AppMessageConstants.KEY_TYPE, 0);
            int status = jsonObject.optInt(AppMessageConstants.KEY_STATUS, 0);
            AppMessageType appMessageType = AppMessageType.typeOfValue(type);
            JSONObject attachJsonObject = jsonObject.optJSONObject(AppMessageConstants.KEY_INFO);//"info"
            if (appMessageType == AppMessageType.GameBuyChips) {
                BuyChipsNotify buyChipsNotify = getBuyChipsNotify(attachJsonObject);
                appMessage.attach = (attachJsonObject.toString());
                appMessage.key = (String.valueOf(buyChipsNotify.buyStarttime));
                appMessage.sortKey = (buyChipsNotify.gameId);
                appMessage.checkinPlayerId = buyChipsNotify.userId;//    fromid
                appMessage.attachObject = (buyChipsNotify);
            } else if (appMessageType == AppMessageType.AppNotice) {
                AppNotify appNotify = getAppNotify(attachJsonObject);
                appMessage.attach = (attachJsonObject.toString());
                appMessage.key = (appNotify.id);//    fromid
                appMessage.attachObject = (appNotify);
                appMessage.content = (appNotify.content);
            } else if (appMessageType == AppMessageType.GameOver) {
                GameOverNotify gameOverNotify = getGameOverNotify(attachJsonObject);
                appMessage.attach = (attachJsonObject.toString());
                appMessage.key = (gameOverNotify.gameId);
                appMessage.attachObject = (gameOverNotify);
                appMessage.checkinPlayerId = fromId;//    fromid
            } else if (appMessageType == AppMessageType.MatchBuyChips) {
                MatchBuyChipsNotify buyChipsNotify = getMatchBuyChipsNotify(attachJsonObject);
                appMessage.attach = (attachJsonObject.toString());
//                appMessage.setKey(String.valueOf(buyChipsNotify.getBuyStarttime()));
                String checkinPlayerId = buyChipsNotify.userId;
                if (TextUtils.isEmpty(checkinPlayerId)) {
                    checkinPlayerId = targetId;
                }
                if (TextUtils.isEmpty(checkinPlayerId)) {
                    checkinPlayerId = fromId;
                }
                appMessage.checkinPlayerId = checkinPlayerId;
                appMessage.key = (AppMessageConstants.getMatchBuyChipsDBKey(buyChipsNotify.gameCode, checkinPlayerId));
                appMessage.sortKey = (buyChipsNotify.gameId);
                appMessage.attachObject = (buyChipsNotify);
            } else if (appMessageType == AppMessageType.MatchBuyChipsResult) {
                MatchBuyChipsResultNotify resultNotify = getMatchBuyChipsResultNotify(attachJsonObject);
                appMessage.attach = (attachJsonObject.toString());
                appMessage.key = (resultNotify.gameId);
                appMessage.sortKey = (resultNotify.gameId);
                appMessage.attachObject = (resultNotify);
                appMessage.checkinPlayerId = fromId;
            }
            NimUserInfoCache.getInstance().getUserDisplayName(appMessage.checkinPlayerId);//请求一次云信刷新缓存
            //
            appMessage.fromId = (fromId);
            appMessage.targetId = (targetId);
            appMessage.time = (time);
            appMessage.type = (appMessageType);
            appMessage.status = (AppMessageStatus.statusOfValue(status));
            appMessage.unread = (true);
            int online = attachJsonObject.optInt("online", 0);
            appMessage.online = online;
            if (online == 1) {//表示已经在游戏内读过了，那么未读标记设置为FALSE；
                appMessage.unread = false;
            }
            //如果状态为同意或者拒绝（房主），设置为已读
//            if (appMessage.getStatus() == AppMessageStatus.passed || appMessage.getStatus() == AppMessageStatus.declined) {
//                appMessage.setUnread(false);
//            } else {
//                appMessage.setUnread(true);
//            }//这个消息为啥要强制置为已读？   注释掉by  周智慧20161125
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appMessage;
    }

    /**
     * 打包成系统消息
     * @param appMessage
     * @return
     */
    public static String packageAppMessage(AppMessage appMessage) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(AppMessageConstants.KEY_FROM_ID, appMessage.fromId);
            jsonObject.put(AppMessageConstants.KEY_TARGET_ID, appMessage.targetId);
            jsonObject.put(AppMessageConstants.KEY_TIME, appMessage.time);
            jsonObject.put(AppMessageConstants.KEY_TYPE, appMessage.type.getValue());
            jsonObject.put(AppMessageConstants.KEY_STATUS, appMessage.status.getValue());
            jsonObject.put(AppMessageConstants.KEY_CONTENT, appMessage.content);
            jsonObject.put(AppMessageConstants.KEY_INFO, new JSONObject(appMessage.attach));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static BuyChipsNotify getBuyChipsNotify(JSONObject infoJson) {
        BuyChipsNotify buyChipsNotify = new BuyChipsNotify();
        JSONObject game = infoJson.optJSONObject(AppMessageConstants.KEY_BUY_GAME);
        if (game != null) {
            int gameMode = game.optInt(AppMessageConstants.KEY_BUY_GAME_MODE, 0);
            buyChipsNotify.gameId = (game.optString(AppMessageConstants.KEY_BUY_GAME_ID, ""));
            buyChipsNotify.gameCode = (game.optString(AppMessageConstants.KEY_BUY_GAME_CODE, ""));
            buyChipsNotify.gameName = (game.optString(AppMessageConstants.KEY_BUY_GAME_NAME, ""));
            buyChipsNotify.gameType = (game.optInt(AppMessageConstants.KEY_BUY_GAME_TYPE, 0));
            buyChipsNotify.channel = game.optString("channel", "");
            buyChipsNotify.gameMode = (gameMode);
            buyChipsNotify.gameCreateTime = (game.optLong(AppMessageConstants.KEY_BUY_GAME_CREATE_TIME, 0));
            //普通模式
            buyChipsNotify.gameDurations = (game.optInt(GameConstants.KEY_DURATIONS, 0));
            buyChipsNotify.gameSBlinds = (game.optInt(GameConstants.KEY_SMALL_BLINDS, 0));
            buyChipsNotify.gamePlayerCount = (game.optInt(GameConstants.KEY_GAME_GAMER_COUNT, 0));
            //SNG
            buyChipsNotify.matchChips = (game.optInt(GameConstants.KEY_GAME_MATCH_CHIPS, 0));
            buyChipsNotify.matchDuration = (game.optInt(GameConstants.KEY_GAME_MATCH_DURATION, 0));
            buyChipsNotify.matchPlayer = (game.optInt(GameConstants.KEY_GAME_MATCH_PLAER, 0));
            buyChipsNotify.matchCheckinFee = (game.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, 0));
            buyChipsNotify.totalPlayer = (game.optInt(GameConstants.KEY_GAME_TOTAL_PLAYER, 0));
            buyChipsNotify.checkinPlayer = (game.optInt(GameConstants.KEY_GAME_CEHCKIN_PLAYER_COUNT, 0));
        }
        JSONObject user = infoJson.optJSONObject(AppMessageConstants.KEY_BUY_USER);
        if (user != null) {
            buyChipsNotify.uuid = (user.optString(AppMessageConstants.KEY_BUY_USER_UUID, ""));
            buyChipsNotify.userId = (user.optString(AppMessageConstants.KEY_BUY_USER_ID, ""));
            buyChipsNotify.userNickname = (user.optString(AppMessageConstants.KEY_BUY_USER_NICKNAME, ""));
            buyChipsNotify.userAvatar = (user.optString(AppMessageConstants.KEY_BUY_USER_AVATAR, ""));
        }
        buyChipsNotify.buyStarttime = (infoJson.optLong(AppMessageConstants.KEY_BUY_STARTTIME, 0));
        buyChipsNotify.buyTimeout = (infoJson.optInt(AppMessageConstants.KEY_BUY_TIMEOUT, 0));
        buyChipsNotify.userWinChips = (infoJson.optInt(AppMessageConstants.KEY_BUY_WIN_CHIPS, 0));
        buyChipsNotify.userBuyChips = (infoJson.optInt(AppMessageConstants.KEY_BUY_BUY_CHIPS, 0));
        buyChipsNotify.userTotalBuyChips = (infoJson.optInt(AppMessageConstants.KEY_BUY_TOTAL_BUY_CHIPS, 0));
        buyChipsNotify.status = (infoJson.optInt(AppMessageConstants.KEY_STATUS, 0));
        buyChipsNotify.nodename = (infoJson.optString(AppMessageConstants.KEY_BUY_NODENAME, ""));
        return buyChipsNotify;
    }

    public static MatchBuyChipsNotify getMatchBuyChipsNotify(JSONObject infoJson) {
        MatchBuyChipsNotify matchBuyChipsNotify = new MatchBuyChipsNotify();
        JSONObject game = infoJson.optJSONObject(AppMessageConstants.KEY_BUY_GAME);
        LogUtil.i(TAG, infoJson.toString());
        if (game != null) {
            matchBuyChipsNotify.gameId = (game.optString(AppMessageConstants.KEY_BUY_GAME_ID, ""));
            matchBuyChipsNotify.gameCode = (game.optString(AppMessageConstants.KEY_BUY_GAME_CODE, ""));
            matchBuyChipsNotify.channel = game.optString("channel", "");
            matchBuyChipsNotify.gameName = (game.optString(AppMessageConstants.KEY_BUY_GAME_NAME, ""));
            matchBuyChipsNotify.gameType = (game.optInt(AppMessageConstants.KEY_BUY_GAME_TYPE, 0));
            matchBuyChipsNotify.gameMode = (game.optInt(AppMessageConstants.KEY_BUY_GAME_MODE, 0));
            matchBuyChipsNotify.gameCreateTime = (game.optLong(AppMessageConstants.KEY_BUY_GAME_CREATE_TIME, 0));
            //
            matchBuyChipsNotify.matchChips = (game.optInt(GameConstants.KEY_GAME_MATCH_CHIPS, 0));
            matchBuyChipsNotify.matchDuration = (game.optInt(GameConstants.KEY_GAME_MATCH_DURATION, 0));
            matchBuyChipsNotify.matchPlayer = (game.optInt(GameConstants.KEY_GAME_MATCH_PLAER, 0));
            matchBuyChipsNotify.matchCheckinFee = (game.optInt(GameConstants.KEY_GAME_MATCH_CHECKIN_FEE, 0));
            matchBuyChipsNotify.totalPlayer = (game.optInt(GameConstants.KEY_GAME_TOTAL_PLAYER, 0));
            matchBuyChipsNotify.checkinPlayer = (game.optInt(GameConstants.KEY_GAME_CEHCKIN_PLAYER_COUNT, 0));
        }
        JSONObject user = infoJson.optJSONObject(AppMessageConstants.KEY_BUY_USER);
        if (user != null) {
            matchBuyChipsNotify.uuid = (user.optString(AppMessageConstants.KEY_BUY_USER_UUID, ""));
            matchBuyChipsNotify.userId = (user.optString(AppMessageConstants.KEY_BUY_USER_ID, ""));
            matchBuyChipsNotify.userNickname = (user.optString(AppMessageConstants.KEY_BUY_USER_NICKNAME, ""));
            LogUtil.i(TAG, user.optString(AppMessageConstants.KEY_BUY_USER_NICKNAME, ""));
            matchBuyChipsNotify.userAvatar = (user.optString(AppMessageConstants.KEY_BUY_USER_AVATAR, ""));
            matchBuyChipsNotify.handledNickname = user.optString(AppMessageConstants.KEY_BUY_USER_MANAGER_NICKNAME, "");//处理报名请求后  再群发这个处理消息  会有这个字段manager_nickname表示是谁处理的
        }
        matchBuyChipsNotify.buyStarttime = (infoJson.optLong(AppMessageConstants.KEY_BUY_STARTTIME, 0));
        matchBuyChipsNotify.buyTimeout = (infoJson.optInt(AppMessageConstants.KEY_BUY_TIMEOUT, 0));
        matchBuyChipsNotify.buyType = (infoJson.optInt(AppMessageConstants.KEY_BUY_BUY_TYPE, 0));
        matchBuyChipsNotify.rebuyCnt = (infoJson.optInt(AppMessageConstants.KEY_BUY_REBUY_CNT, 0));
        matchBuyChipsNotify.result_code = (infoJson.optInt(AppMessageConstants.KEY_BUY_RESULT_CODE, 0));
        matchBuyChipsNotify.result_msg = (infoJson.optString(AppMessageConstants.KEY_BUY_RESULT_MSG, ""));
        matchBuyChipsNotify.nodename = (infoJson.optString(AppMessageConstants.KEY_BUY_NODENAME, ""));
        return matchBuyChipsNotify;
    }

    public static MatchBuyChipsResultNotify getMatchBuyChipsResultNotify(JSONObject infoJson) {
        MatchBuyChipsResultNotify resultNotify = new MatchBuyChipsResultNotify();
        LogUtil.i(TAG, infoJson.toString());
        JSONObject game = infoJson.optJSONObject(AppMessageConstants.KEY_BUY_GAME);
        if (game != null) {
            resultNotify.gameId = (game.optString(AppMessageConstants.KEY_BUY_GAME_ID, ""));
            resultNotify.channel = game.optString("channel", "");
            resultNotify.gameCode = (game.optString(AppMessageConstants.KEY_BUY_GAME_CODE, ""));
            resultNotify.gameName = (game.optString(AppMessageConstants.KEY_BUY_GAME_NAME, ""));
            resultNotify.gameType = (game.optInt(AppMessageConstants.KEY_BUY_GAME_TYPE, 0));
            resultNotify.gameMode = (game.optInt(AppMessageConstants.KEY_BUY_GAME_MODE, 0));
        }
        resultNotify.buyType = (infoJson.optInt(AppMessageConstants.KEY_BUY_BUY_TYPE, 0));
        resultNotify.result_code = (infoJson.optInt(AppMessageConstants.KEY_BUY_RESULT_CODE, 0));
        resultNotify.result_msg = (infoJson.optString(AppMessageConstants.KEY_BUY_RESULT_MSG, ""));
        return resultNotify;
    }

    public static AppNotify getAppNotify(JSONObject infoJson) {
        AppNotify appNotify = new AppNotify();
        appNotify.id = (infoJson.optString(AppMessageConstants.KEY_NOTICE_ID, ""));
        appNotify.title = (infoJson.optString(AppMessageConstants.KEY_NOTICE_TITLE, ""));
        appNotify.content = (infoJson.optString(AppMessageConstants.KEY_NOTICE_CONTENT, ""));
        appNotify.showImage = (infoJson.optString(AppMessageConstants.KEY_NOTICE_SHOW_IMAGE, ""));
        appNotify.url = (infoJson.optString(AppMessageConstants.KEY_NOTICE_URL, ""));
        return appNotify;
    }

    public static GameOverNotify getGameOverNotify(JSONObject infoJson) {
        GameOverNotify gameOverNotify = new GameOverNotify();
        gameOverNotify.channel = infoJson.optString("channel", "");
        gameOverNotify.gameId = (infoJson.optString(AppMessageConstants.KEY_GAMEOVER_GID, ""));
        gameOverNotify.gameName = (infoJson.optString(AppMessageConstants.KEY_GAMEOVER_NAME, ""));
        gameOverNotify.gameCode = (infoJson.optString(AppMessageConstants.KEY_GAMEOVER_CODE, ""));
        gameOverNotify.teamId = (infoJson.optString(AppMessageConstants.KEY_GAMEOVER_TEAMID, ""));
        gameOverNotify.gameMode = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_MODE, 0));
        gameOverNotify.gameStatus = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_STATUS, 0));
        gameOverNotify.gameType = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_TYPE, 0));
        gameOverNotify.userWinChips = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_WINCHIPS, 0));
        gameOverNotify.userReward = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_REWARD, 0));
        gameOverNotify.userRank = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_RANKING, 0));
        gameOverNotify.creatorId = (infoJson.optString(AppMessageConstants.KEY_GAMEOVER_CREATOR_ID, ""));
        gameOverNotify.gameEndType = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_END_TYPE, 0));
        gameOverNotify.gameStartType = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_START_TYPE, 0));
        gameOverNotify.userBalance = (infoJson.optInt(AppMessageConstants.KEY_GAMEOVER_BALANCE, 0));
        return gameOverNotify;
    }
}
