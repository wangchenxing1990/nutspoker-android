package com.htgames.nutspoker.game.match.helper;

import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.game.model.GameStatus;

/**
 */
public class GameMatchHelper {

    /**
     * 是否可以报名MTT比赛(1.比赛还未开始，人未满 2.比赛进行中，盲注等级未达到，人未满 3.还未达到决赛桌)
     * @param matchStatusEntity
     * @param matchBlindLevel
     * @return
     */
    public static boolean canCheckInMtt(MatchStatusEntity matchStatusEntity , int matchBlindLevel, GameEntity gameInfo) {
        if (matchStatusEntity == null) {
            return false;
        }
        int max_buy_cnt = 1000;//总买入上限
        int checkinFee = 50;//单次买入费用
        //总买入上限 * 单次买入费用 == 总奖池(满足这个条件表示不能再报名)
        Object object = gameInfo.gameConfig;
        if (object instanceof BaseMttConfig) {
            max_buy_cnt = ((BaseMttConfig) object).match_max_buy_cnt;
            checkinFee = ((BaseMttConfig) object).matchCheckinFee;
        }
        int allReward = matchStatusEntity.allReward;//总奖池
        int gameStatus = matchStatusEntity.gameStatus;
        int checkinPlayer = matchStatusEntity.checkInPlayer;
        int matchStatus = matchStatusEntity.matchStatus;
        int currentLevel = matchStatusEntity.currentLevel;
//        if (checkinPlayer >= 1000) {//老的逻辑是判断报名人数是否已经达到1000决定是否能继续报名
//            //人满
//            return false;
//        }
        if (allReward / checkinFee >= max_buy_cnt) {
            return false;//不能再报名
        }
        if (gameStatus == GameStatus.GAME_STATUS_START) {
            if (matchStatus == GameMatchStatus.GAME_STATUS_FINAL || currentLevel >= matchBlindLevel) {
                //盲注等级达到或者到达决赛桌
                return false;
            }
        }
        return true;
    }

    /**
     * 是否可以报名MTSNG模式
     * @param matchStatusEntity
     * @param totalPlayer
     * @return
     */
    public static boolean canCheckInMtSng(MatchStatusEntity matchStatusEntity , int totalPlayer) {
        if (matchStatusEntity == null) {
            return false;
        }
        int gameStatus = matchStatusEntity.gameStatus;
        int checkinPlayer = matchStatusEntity.checkInPlayer;
        int matchStatus = matchStatusEntity.matchStatus;
        if (gameStatus != GameStatus.GAME_STATUS_WAIT ||
                checkinPlayer >= totalPlayer) {
            //人满
            return false;
        }
        return true;
    }
}
