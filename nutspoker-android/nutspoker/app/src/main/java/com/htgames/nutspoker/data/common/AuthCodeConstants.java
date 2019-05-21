package com.htgames.nutspoker.data.common;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 20150726 on 2016/3/25.
 */
public class AuthCodeConstants {
    public final static String KEY_VALIDATECODE = "validatecode";
    public final static String KEY_ERROR_NUM = "error_num";
    public final static int ERROR_COUNT_LIMIT = 5;//错误次数

    /**
     * 获取手机验证码
     * @param result
     * @return
     */
//    public static String getKeyValidatecode(String result){
//        String validatecode = "";
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            JSONObject dataObject = jsonObject.optJSONObject("data");
//            if(dataObject != null && dataObject.has(KEY_VALIDATECODE)){
//                validatecode = dataObject.optString(KEY_VALIDATECODE);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return validatecode;
//    }

    /**
     * 获取错误次数
     * @param result
     * @return
     */
    public static int getErrorNum(String result){
        int errorNum = 0;
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject dataObject = jsonObject.optJSONObject("data");
            errorNum = dataObject.optInt(KEY_ERROR_NUM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorNum;
    }
}
