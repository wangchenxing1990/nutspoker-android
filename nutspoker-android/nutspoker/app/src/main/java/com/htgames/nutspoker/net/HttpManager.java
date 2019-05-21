package com.htgames.nutspoker.net;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 网络请求
 */
public class HttpManager {
    private static RequestQueue mRequestQueue;

    public static RequestQueue getInstance(Context context) {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mRequestQueue;
    }
}
