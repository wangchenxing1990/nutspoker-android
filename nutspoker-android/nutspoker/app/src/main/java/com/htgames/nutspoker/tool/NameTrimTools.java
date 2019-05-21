package com.htgames.nutspoker.tool;

import android.text.TextUtils;

import com.netease.nim.uikit.common.util.log.LogUtil;

/**
 * 过滤掉空格
 */
public class NameTrimTools {
    private final static String TAG = "NameTrimTools";

    /**
     * 过滤掉空格
     *
     * @param name
     * @return
     */
    public static String getNameTrim(String name) {
        char space = (char) 32;
        return getNameTrim(name, space);
    }

    /**
     * 过滤掉末尾数据
     *
     * @param resource
     * @param ch
     * @return
     */
    public static String getNameTrim(String resource, char ch) {
        if (TextUtils.isEmpty(resource) || TextUtils.isEmpty(resource.trim())) {
            return "";
        }
        char currentChar;
        int size = resource.length();
        int position = size - 1;
        while (position >= 0) {
            currentChar = resource.charAt(position);
            LogUtil.i(TAG, "currentChar :" + currentChar);
            if (currentChar != ch) {
                //如果不为空格，跳出循环
                break;
            }
            position = position - 1;
        }
        String show = resource.substring(0, position + 1);
        LogUtil.i(TAG, show);
        return show;
    }
}
