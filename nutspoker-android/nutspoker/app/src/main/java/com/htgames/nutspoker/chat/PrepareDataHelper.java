package com.htgames.nutspoker.chat;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * 登录后准备缓存数据工具类
 */
public class PrepareDataHelper {
    private static final String TAG = PrepareDataHelper.class.getSimpleName();

    public static void prepare() {
        buildDataCache();
        LogUtil.i(TAG, "prepare data completed");
    }

    private static void buildDataCache() {
        // clear
//        MessageDataCache.getInstance().clearCache();
        NimUIKit.clearCache();
        // 构建缓存
        DataCacheManager.buildDataCacheAsync();
//        MessageDataCache.getInstance().buildCache();//系统消息构建缓存
        LogUtil.i(TAG, "build data cache completed");
    }
}
