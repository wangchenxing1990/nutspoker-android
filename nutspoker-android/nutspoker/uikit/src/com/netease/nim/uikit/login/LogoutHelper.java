package com.netease.nim.uikit.login;

import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.CreateGameConfigPref;
import com.netease.nim.uikit.common.preference.GameMgrPref;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.nim.ChatRoomHelper;

/**
 * 注销帮助类
 * Created by huangjun on 2015/10/8.
 */
public class LogoutHelper {
    public static void logout() {
        // 清理缓存&注销监听&清除状态
        removeLoginState();
//        MessageDataCache.getInstance().clearCache();
        ChatRoomHelper.logout();
        NimUIKit.clearCache();
        DemoCache.clear();
        LoginSyncDataStatusObserver.getInstance().reset();
    }

    /**
     * 清除登陆状态的相关数据
     */
    private static void removeLoginState() {
        if (DemoCache.getContext() != null) {
            UserPreferences.getInstance(DemoCache.getContext()).setUserToken("");
            UserPreferences.getInstance(DemoCache.getContext()).setUserId("");
            UserPreferences.getInstance(DemoCache.getContext()).setCoins(0);
            UserPreferences.getInstance(DemoCache.getContext()).setDiamond(0);
//            GamePreferences.getInstance(DemoCache.getContext()).setOwngameCount(1);//把自己创建的牌局计数清空
//            GamePreferences.getInstance(DemoCache.getContext()).setSngGameCount(1);//把自己创建的牌局计数清空
//            GamePreferences.getInstance(DemoCache.getContext()).setMttGameCount(1);//把自己创建的牌局计数清空
//            GamePreferences.getInstance(DemoCache.getContext()).setPineappleGameCount(1);//把自己创建的牌局计数清空
//            GamePreferences.getInstance(DemoCache.getContext()).setPineappleMttGameCount(1);//把自己创建的牌局计数清空
            GameMgrPref.reset();//管理员历史记录置为null，否则这个单例一直存在，登出后需要重新初始化这个单例
            CreateGameConfigPref.reset();//创建游戏的默认设置单例设置为null
            PaijuListPref.reset();
        }
    }
}
