package com.netease.nim.uikit.chesscircle;

import java.util.HashMap;
import java.util.Map;

/**
 * 群组进行中的牌局缓存
 */
public class TeamGameStatusCache {
    static Map<String , Integer> teamCountMap = new HashMap<String , Integer>();

    public static void setTeamCountInfo(Map<String , Integer> cache) {
        if (cache == null) {
            teamCountMap.clear();
            return;
        }
        teamCountMap = cache;
    }

    /**
     * 获取群组在游戏中的牌局数量
     * @param teamId
     * @return
     */
    public static int getTeamGameCount(String teamId) {
        if(teamCountMap != null && teamCountMap.containsKey(teamId)){
            return teamCountMap.get(teamId);
        }
        return 0;
    }
}
