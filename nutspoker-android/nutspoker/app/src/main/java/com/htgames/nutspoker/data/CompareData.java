package com.htgames.nutspoker.data;


import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.constants.GameConstants;

import java.util.Comparator;

/**
 * Created by glp on 2016/8/26.
 */

public class CompareData {

    public static Comparator<GameEntity> sRecentComp = new Comparator<GameEntity>() {
        @Override
        public int compare(GameEntity o1, GameEntity o2) {
            // 先比较置顶tag
            long sticky = (o1.tag & GameConstants.GAME_RECENT_TAG_STICKY) - (o2.tag & GameConstants.GAME_RECENT_TAG_STICKY);//是否置顶
            long invite = o1.isInvited - o2.isInvited;
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else if (invite != 0) {
                return invite > 0 ? -1 : 1;
            } else if(o1.entryTime != o2.entryTime) {
                long time = o1.entryTime - o2.entryTime;
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            } else{
                long time = o1.createTime - o2.createTime;
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };

}
