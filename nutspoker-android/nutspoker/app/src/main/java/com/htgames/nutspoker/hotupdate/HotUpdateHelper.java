package com.htgames.nutspoker.hotupdate;

import com.netease.nim.uikit.common.util.log.LogUtil;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.hotupdate.preference.HotUpdatePreferences;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.widget.Toast;

/**
 * 游戏热更新
 */
public class HotUpdateHelper {
    private static boolean isGameUpdateIng = false;//游戏是否更新中
    private static int checkVersionInterval = 30 * 60;//检测版本时间间隔

    public static void setGameUpdateIng(boolean ing) {
        isGameUpdateIng = ing;
    }

    /**
     * 游戏是否在更新中
     * @return
     */
    public static boolean isGameUpdateIng() {
        if(isGameUpdateIng){
            Toast.makeText(DemoCache.getContext() , R.string.game_updating_later , Toast.LENGTH_SHORT).show();
        }
        return isGameUpdateIng;
    }

    /**
     * 是否需要检测更新
     * @return
     */
    public static boolean isNeedToCheckVersion(){
        long currentTime = DemoCache.getCurrentServerSecondTime();
        long lastCheckTime = HotUpdatePreferences.getInstance(DemoCache.getContext()).getCheckVersionTime();
        boolean need = (currentTime - lastCheckTime) >= checkVersionInterval;
        LogUtil.i("HotUpdate" , "need:" + need);
        return need;
//        return false;
    }
}
