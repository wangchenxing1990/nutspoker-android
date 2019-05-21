package com.netease.nim.uikit.common.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 */
public class VersionTools {
    public static String getAppVersion(Context context) {
        String versionName = "";
        try {
            PackageManager pkgMng = context.getPackageManager();
            PackageInfo pkgInfo = pkgMng.getPackageInfo(context.getPackageName(), 0);
            versionName = pkgInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
