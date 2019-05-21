package com.htgames.nutspoker.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.List;

/**
 * 进程工具类
 */
public class AppProcessUtil {
    private static final String TAG  = "AppProcessUtil";

    /**
     * 判断当前APP是否在后台
     * 需要权限:android.permission.GET_TASKS
     * @param context
     * @return
     */
    public boolean isApplicationBroughtToBackground(Context context) {
        if (context == null) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            LogUtil.i(TAG, "topActivity:" + topActivity.flattenToString());
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                LogUtil.i(TAG, "isApplicationBroughtToBackground: true");
                return true;
            }
        }
        LogUtil.i(TAG, "isApplicationBroughtToBackground: false");
        return false;
    }
}
