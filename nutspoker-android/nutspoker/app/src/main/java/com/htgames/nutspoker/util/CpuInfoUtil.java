package com.htgames.nutspoker.util;

import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 */
public class CpuInfoUtil {
    private final static String TAG = "CpuInfoUtil";
    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

//    public static boolean isArm64() {
//        boolean isArm64 = false;
//        try {
//            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/cpuinfo"));
//            if (localBufferedReader.readLine().contains("aarch64")) {
//                isArm64 = true;
//            }
//            localBufferedReader.close();
//        } catch (IOException e) {
//        }
//        Log.d(TAG, "是否是64位:" + isArm64);
//        return isArm64;
//    }

    /**
     * Check if the CPU architecture is x86
     */
    public static boolean checkIfCPUx86() {
        //1. Check CPU architecture: arm or x86
        if (getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")) {
            //The CPU is x86
            return true;
        } else {
            return false;
        }
    }

    public static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz= Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(clazz, key, ""));
        } catch (Exception e) {
            LogUtil.i("getSystemProperty", "key = " + key + ", error = " + e.getMessage());
        }
        LogUtil.i("getSystemProperty",  key + " = " + value);
        return value;
    }

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     */
    public static boolean isArm64() {
        boolean isArm64 = false;
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo != null && cpuInfo.exists()) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(cpuInfo);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 512);
                String line = bufferedReader.readLine();
                if (line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64")) {
                    isArm64 = true;
                } else {
                }
            } catch (Throwable t) {
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        LogUtil.i(TAG, "是否是64位:" + isArm64);
        return isArm64;
    }
}
