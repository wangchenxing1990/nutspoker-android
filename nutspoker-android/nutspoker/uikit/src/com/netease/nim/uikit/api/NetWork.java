package com.netease.nim.uikit.api;

import android.content.Context;

import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.constants.CountryCodeHelper;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.MD5;
import com.netease.nim.uikit.common.util.SHA1;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by zjy on 2015/4/27.
 */
public class NetWork {

    public static HashMap<String, String> getRequestCommonParams() {
        return getRequestCommonParams(true);
    }

    public static HashMap<String, String> getRequestCommonParams(boolean hasUid) {
        return getRequestCommonParams(CacheConstant.sAppContext, hasUid);
    }

    /**
     * 获取公共参数
     *
     * @return
     */
    public static HashMap<String, String> getRequestCommonParams(Context mContext) {
        return getRequestCommonParams(mContext, true);
    }

    public static HashMap<String, String> getRequestCommonParams(Context mContext, boolean hasUid) {
        return getRequestCommonParams(mContext, true, true);
    }

    public static HashMap<String, String> getRequestCommonParams(Context mContext, boolean hasUid, boolean hasCCode) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("os", ApiConstants.OS_ANDROID);//1代表android
        if (hasUid) {
            paramsMap.put("uid", UserPreferences.getInstance(mContext).getUserId());
        }
        String countryCode = CountryCodeHelper.getCurrentLocalCountryCode();
        if (hasCCode) {
            paramsMap.put("ccode", countryCode);
        }
        return paramsMap;
    }

    /**
     * 传输数据的接口拼接的数据字段
     */
    public static String getRequestParams(Map<String, String> dataMap) {
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        int paramsSize = dataMap.size();
        String[] keysArray = new String[paramsSize];
        dataMap.keySet().toArray(keysArray);
        Arrays.sort(keysArray);
        StringBuffer paramsBuf = new StringBuffer();
        StringBuffer valuesBuf = new StringBuffer();
        String paramsKey;
        String paramsValue;
        paramsBuf.append("?");
        for (int i = 0; i < paramsSize; i++) {
            paramsKey = keysArray[i];
            paramsValue = dataMap.get(paramsKey);
            if (paramsValue == null) {
                paramsValue = "";
            }
            if (i == paramsSize - 1) {
                paramsBuf.append(paramsKey).append("=").append(URLEncoder.encode(paramsValue));
            } else {
                paramsBuf.append(paramsKey).append("=").append(URLEncoder.encode(paramsValue)).append("&");
            }
            valuesBuf.append(paramsValue);
        }
        String params = paramsBuf.toString();
        return params;
    }

    /**
     * 获取加密字段
     *
     * @param rand 随机数
     * @param time 时间戳
     * @return
     */
    public static String getSign(String rand, String time) {
        String[] arrays = new String[]{rand, time, ApiConstants.HTTP_HEADER_TOKEN};
        Arrays.sort(arrays);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < arrays.length; i++) {
            buffer.append(arrays[i]);
        }
        String sign = SHA1.getSha1(buffer.toString());
        //Log.d("sha1" , buffer.toString());
        //Log.d("sha1", "rand :" + rand + ";time :" + time + ";sign :" + sign);
        return sign;
    }

    /**
     * 获取排序Params MD5加密，用于请求头加密
     *
     * @param map
     * @return
     */
    public static String getParamsMD5(Map<String, String> map) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ApiConstants.HTTP_HEADER_TOKEN);
        if (map != null) {
            map = sortMapByKey(map);
            Iterator iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                String value = map.get(key);
                stringBuffer.append(key).append(value);
                //Log.d("getParamsMD5", "key :" + key + "; value:" + map.get(key));
            }
        }
        String paramsStr = stringBuffer.toString();
        LogUtil.i("getParamsMD5", paramsStr);
        LogUtil.i("getParamsMD5", MD5.toMD5(paramsStr));
        return MD5.toMD5(paramsStr);
    }

    /**
     * 获取随机数
     *
     * @return
     */
    public static String getRandom() {
        int max = 9999;
        int min = 1000;
        Random random = new Random();
        int randomNum = random.nextInt(max - min) + min;
        return String.valueOf(randomNum);
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    public static class MapKeyComparator implements Comparator<String> {
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }
}
