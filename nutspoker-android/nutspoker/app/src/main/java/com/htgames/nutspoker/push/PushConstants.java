package com.htgames.nutspoker.push;

import android.text.TextUtils;

import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.netease.nim.uikit.common.DateTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 系统公告
 */
public class PushConstants {
    public final static String TAG = "PushLog";
    //
    private static String EXTRA_TYPE = "type";
    private static String EXTRA_DATA = "data";
    //
    private static String EXTRA_INTERVAL = "interval";
    private static String EXTRA_TIME = "time";
    private static String EXTRA_MSG = "msg";

    public static int TYPE_ANNOUNCEMENT = 1;//系统紧急公告
    public static int TYPE_APP_MSG = 2;//系统消息（和云信统一）

    /**
     * 获取推送类型
     * @param payload
     * @return
     */
    public static int getPushType(String payload) {
        int type = 0;
        if(!TextUtils.isEmpty(payload)){
            try {
                JSONObject jsonObject = new JSONObject(payload);
                if(jsonObject.has(EXTRA_TYPE)){
                    type = jsonObject.optInt(EXTRA_TYPE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    public static String getPushData(String payload) {
        String data = "";
        if (!TextUtils.isEmpty(payload)) {
            try {
                JSONObject jsonObject = new JSONObject(payload);
                if (jsonObject.has(EXTRA_DATA)) {
                    data = jsonObject.optString(EXTRA_DATA);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 获取紧急系统公告
     * @param payload
     * @return
     */
    public static AnnouncementEntity getAnnouncement(String payload) {
        AnnouncementEntity announcementEntity = null;
        if (!TextUtils.isEmpty(payload)) {
            try {
                JSONObject jsonObject = new JSONObject(payload);
                announcementEntity = new AnnouncementEntity();
                announcementEntity.type = (jsonObject.optInt(EXTRA_TYPE));
                announcementEntity.interval = (jsonObject.optInt(EXTRA_INTERVAL));
                announcementEntity.time = (jsonObject.optLong(EXTRA_TIME));
                announcementEntity.msg = (jsonObject.optString(EXTRA_MSG));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return announcementEntity;
    }

    /**
     * 是否显示公告（根据有效时间）
     * @param payload
     * @return
     */
    public static boolean isAnnouncementShow(String payload){
        if (!TextUtils.isEmpty(payload)) {
            try {
                JSONObject jsonObject = new JSONObject(payload);
                int interval = jsonObject.optInt(EXTRA_INTERVAL);
                long serverTime = jsonObject.optLong(EXTRA_TIME);
                long currentTime = DemoCache.getCurrentServerSecondTime();
                if(currentTime < (serverTime + interval)){
                    LogUtil.i(TAG, "消息有效");
                    return true;
                } else {
                    LogUtil.i(TAG, "消息超时无效");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
