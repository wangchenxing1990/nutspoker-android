package com.htgames.nutspoker.game.match.bean;

import com.google.gson.annotations.SerializedName;
import com.htgames.nutspoker.game.match.constants.MatchConstants;

import java.io.Serializable;

/**
 */
public class MatchTableEntity implements Serializable {
    @SerializedName(MatchConstants.KEY_TABLE_INDEX) public int index;
    @SerializedName(MatchConstants.KEY_TABLE_COUNT) public int memberCount;
    @SerializedName(MatchConstants.KEY_TABLE_MAX_CHIPS) public int maxChips;
    @SerializedName(MatchConstants.KEY_TABLE_MIN_CHIPS) public int minChips;

    public MatchTableEntity() {
    }

    public MatchTableEntity(int index, int memberCount, int maxChips, int minChips) {
        this.index = index;
        this.memberCount = memberCount;
        this.maxChips = maxChips;
        this.minChips = minChips;
    }
}
