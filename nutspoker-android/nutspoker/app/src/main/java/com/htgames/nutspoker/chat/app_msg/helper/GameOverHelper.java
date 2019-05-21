package com.htgames.nutspoker.chat.app_msg.helper;

import android.text.TextUtils;

import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 20150726 on 2016/5/21.
 */
public class GameOverHelper {
    private final static String TAG ="GameOverHelper";

    /**
     * 处理游戏结束的通知：
     * 功能：如果是俱乐部牌局创建者，需要通过这方式去改变消息状态
     * 条件：1.俱乐部牌局  2.牌局状态结束  3.牌局teamId不为空  4.创建者是当前玩家
     * @param gameOverNotify
     */
    public static void dealGameOverMessage(GameOverNotify gameOverNotify) {
        if (gameOverNotify != null
                && gameOverNotify.gameType == GameConstants.GAME_TYPE_CLUB
                && gameOverNotify.gameStatus == GameStatus.GAME_STATUS_FINISH
                && !TextUtils.isEmpty(gameOverNotify.teamId)
                && gameOverNotify.creatorId.equals(DemoCache.getAccount())) {
//            MessageStatusHelper.updateGameMessageStatus(gameOverNotify.getTeamId(), gameOverNotify.getGameId(), gameOverNotify.getGameStatus());
        }
    }
}
