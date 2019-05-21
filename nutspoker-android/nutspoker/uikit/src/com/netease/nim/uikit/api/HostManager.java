package com.netease.nim.uikit.api;

import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 周智慧 on 17/4/25.
 */

public class HostManager {
    public static String mainHost = "https://api1.everpoker.win/";//主host，一般都用这个
    private static HashMap<String, Integer> HOSTS_TEST = new HashMap<String, Integer>();//string表示域名，integer表示失败的权重，权重越大，说明失败的次数越高
    private static HashMap<String, Integer> HOSTS_RELEASE = new HashMap<String, Integer>();
    public static long lastMainHostErrorTime = 0;

    public static void init() {
        mainHost = ApiConfig.isTestVersion ? "http://192.168.2.72/" : "https://47.96.146.236/";
        HOSTS_TEST.put("http://192.168.2.72/", 0);
        HOSTS_TEST.put("http://192.168.2.72/", 1);
        HOSTS_TEST.put("http://192.168.2.72/", 1);
        HOSTS_TEST.put("http://192.168.2.72/", 1);
        HOSTS_TEST.put("http://192.168.2.72/", 1);
        HOSTS_RELEASE.put("http://192.168.2.72/", 0);
        HOSTS_RELEASE.put("http://192.168.2.72/", 1);
        HOSTS_RELEASE.put("http://192.168.2.72/", 1);
        HOSTS_RELEASE.put("http://192.168.2.72/", 1);
        HOSTS_RELEASE.put("http://192.168.2.72/", 1);
    }
    public static String getHost() {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        if (currentTime - lastMainHostErrorTime >= 10 * 60) {//距离上次主host请求失败超过10分钟，那么还要使用主host
            HOSTS_TEST.put("http://192.168.2.72/", 0);
            HOSTS_TEST.put("http://192.168.2.72/", 1);
            HOSTS_TEST.put("http://192.168.2.72/", 1);
            HOSTS_TEST.put("http://192.168.2.72/", 1);
            HOSTS_TEST.put("http://192.168.2.72/", 1);
            HOSTS_RELEASE.put("http://192.168.2.72/", 0);
            HOSTS_RELEASE.put("http://192.168.2.72/", 1);
            HOSTS_RELEASE.put("http://192.168.2.72/", 1);
            HOSTS_RELEASE.put("http://192.168.2.72/", 1);
            HOSTS_RELEASE.put("http://192.168.2.72/", 1);
            lastMainHostErrorTime = Long.MAX_VALUE;
            return mainHost;
        }
        if (HOSTS_TEST.containsKey(mainHost) && HOSTS_TEST.get(mainHost) <= 1) {
            return mainHost;
        }
        if (HOSTS_RELEASE.containsKey(mainHost) && HOSTS_RELEASE.get(mainHost) <= 1) {
            return mainHost;
        }
        //下面的算法找出失败权重最小的api
        String minApi = mainHost;
        int weight = 100;
        if (ApiConfig.isTestVersion) {
            for (Map.Entry<String, Integer> entry : HOSTS_TEST.entrySet()) {
                if (entry.getValue() < weight) {
                    weight = entry.getValue();
                    minApi = entry.getKey();
                }
            }
        } else {
            for (Map.Entry<String, Integer> entry : HOSTS_RELEASE.entrySet()) {
                if (entry.getValue() < weight) {
                    weight = entry.getValue();
                    minApi = entry.getKey();
                }
            }
        }
        return minApi;
    }

    public static void addHostWeight(String url) {
        if (StringUtil.isSpace(url)) {
            return;
        }
        if (url.contains(mainHost)) {
            lastMainHostErrorTime = DemoCache.getCurrentServerSecondTime();
        }
        String toAddApi = "";//找出失败的api
        int weight = -1;
        if (ApiConfig.isTestVersion) {
            for (Map.Entry<String, Integer> entry : HOSTS_TEST.entrySet()) {
                if (url.contains(entry.getKey())) {
                    toAddApi = entry.getKey();
                    weight = entry.getValue() + 1;
                    break;
                }
            }
            if (HOSTS_TEST.containsKey(toAddApi) && weight > -1) {
                HOSTS_TEST.put(toAddApi, weight);
            }
        } else {
            for (Map.Entry<String, Integer> entry : HOSTS_RELEASE.entrySet()) {
                if (url.contains(entry.getKey())) {
                    toAddApi = entry.getKey();
                    weight = entry.getValue() + 1;
                    break;
                }
            }
            if (HOSTS_RELEASE.containsKey(toAddApi) && weight > -1) {
                HOSTS_RELEASE.put(toAddApi, weight);
            }
        }
    }

    public static void resetMainHostWeight() {
        HOSTS_TEST.put("http://192.168.2.72/", 0);
        HOSTS_RELEASE.put("http://192.168.2.72/", 0);
    }
}
