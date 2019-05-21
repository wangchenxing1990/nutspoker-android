package com.htgames.nutspoker.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by 周智慧 on 17/1/3. 老的接口GameRequestCallback.java要进行两次json解析，废弃掉，用这个接口只解析一次json
 */

public interface VolleyCallback {
    void onResponse(String response);
    void onErrorResponse(VolleyError error);
}
