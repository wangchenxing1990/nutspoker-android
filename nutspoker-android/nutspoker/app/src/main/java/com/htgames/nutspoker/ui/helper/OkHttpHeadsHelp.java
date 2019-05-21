package com.htgames.nutspoker.ui.helper;

import java.util.Map;
import java.util.Set;

import okhttp3.Headers;

/**
 * Created by glp on 2016/8/18.
 */

public class OkHttpHeadsHelp {

    public static Headers getHeads(Map<String, String> map) {
        if (map == null)
            return null;

        Headers.Builder builder = new Headers.Builder();
        Set<String> stringSet = map.keySet();
        for (String string : stringSet) {
            builder.add(string, map.get(string));
        }

        return builder.build();
    }

}
