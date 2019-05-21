package com.htgames.nutspoker.game.match.bean;

import com.google.gson.annotations.SerializedName;
import com.htgames.nutspoker.game.match.constants.MatchConstants;

/**
 * 比赛通知消息
 */
public class MatchPushMsgEntity {
    @SerializedName(MatchConstants.KEY_INFO_CHECKIN_PLAYER)
    private int checkInPlayer;

    public int getCheckInPlayer() {
        return checkInPlayer;
    }

    public void setCheckInPlayer(int checkInPlayer) {
        this.checkInPlayer = checkInPlayer;
    }
}
