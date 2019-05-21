package com.htgames.nutspoker.ui.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * 权限请求
 */
public class PermissionHelper {
    private final static String TAG = "PermissionHelper";
    public final static String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public final static String ACCESS_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final int PERMISSIONS_GRANTED = PackageManager.PERMISSION_GRANTED; // 权限授权
    public static final int PERMISSIONS_DENIED = PackageManager.PERMISSION_DENIED; // 权限拒绝
    private Context context;

    public PermissionHelper(Context context) {
        this.context = context;
    }

    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return hasPermission(permission);
    }

    public boolean hasPermission(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean hasPermission = true;
        int targetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                hasPermission = (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                hasPermission = (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED);
            }
        }
        LogUtil.i(TAG, permission + ":" + hasPermission);
        return hasPermission;
    }

    public void requestPermission(Activity activity ,String permission , int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * 第一次请求权限时，用户拒绝了，下一次：shouldShowRequestPermissionRationale()  返回 true，应该显示一些为什么需要这个权限的说明
       第二次请求权限时，用户拒绝了，并选择了“不在提醒”的选项时：shouldShowRequestPermissionRationale()  返回 false
       设备的策略禁止当前应用获取这个权限的授权：shouldShowRequestPermissionRationale()  返回 false
     * @param activity
     * @param permission
     * @return
     */
    public boolean shouldShowRequestPermissionRationale(Activity activity , String permission) {
        boolean isShouldRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        LogUtil.i(TAG, permission + "  isShouldRequestPermission:" + isShouldRequestPermission);
        return isShouldRequestPermission;
    }

    /**
     * 检查系统版本的，是否需要权限请求
     * @return
     */
    public boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}