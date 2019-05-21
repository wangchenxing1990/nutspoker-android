package com.htgames.nutspoker.game.match.bean;

import com.google.gson.annotations.SerializedName;
import com.htgames.nutspoker.game.match.constants.MatchConstants;

import java.io.Serializable;

/**
 * 比赛状态
 */
public class MatchStatusEntity implements Serializable {
    @SerializedName(MatchConstants.KEY_INFO_CURRENT_SBLINDS_LEVEL) public int currentLevel;
    @SerializedName(MatchConstants.KEY_INFO_CURRENT_PLAYER) public int leftPlayer;
    @SerializedName(MatchConstants.KEY_INFO_ALL_REWARD) public int allReward;
    @SerializedName(MatchConstants.KEY_INFO_MAX_CHIPS) public int maxChips;
    @SerializedName(MatchConstants.KEY_INFO_MIN_CHIPS) public int minChips;
    @SerializedName(MatchConstants.KEY_INFO_AVG_CHIPS) public int avgChips;
    @SerializedName(MatchConstants.KEY_INFO_MY_CHECKIN_STATUS) public int myCheckInStatus;
    @SerializedName(MatchConstants.KEY_INFO_CHECKIN_PLAYER) public int checkInPlayer;
    @SerializedName(MatchConstants.KEY_INFO_BUY_TYPE) public int buyTypeStatus;
    @SerializedName(MatchConstants.KEY_INFO_MATCH_STATUS) public int matchStatus;
    @SerializedName(MatchConstants.KEY_INFO_GAME_STATUS) public int gameStatus;
    @SerializedName(MatchConstants.KEY_INFO_SCORE) public int score;//分数（为0的时候为被淘汰）
    @SerializedName(MatchConstants.KEY_INFO_IS_CONTROL) public int isControl;
    @SerializedName(MatchConstants.KEY_INFO_BEGIN_TIME) public long beginTime;
    @SerializedName(MatchConstants.KEY_INFO_MATCH_IN_REST) public int matchInRest;
    @SerializedName(MatchConstants.KEY_INFO_MATCH_PAUSE_TIME) public long matchPauseTime;
    @SerializedName(MatchConstants.KEY_REBUY_CNT) public long rebuy_cnt;//自己的重构次数
    @SerializedName(MatchConstants.KEY_INFO_MATCH_MANUL_BEGIN_GAME_SUCCESS) public long manul_begin_game_success;//手动开赛成功
    @SerializedName(MatchConstants.KEY_INFO_MATCH_CHECKIN_TIME) public long checkin_time;// mtt大厅点击"报名"成功后的时间戳
}
