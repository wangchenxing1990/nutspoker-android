package com.htgames.nutspoker.api;

/**
 * 云片相关服务端CODE
 */
public class YunpianCode {
//    {
//        "code": 9999,
//            "message": "netease server error",
//            "data": {
//        "http_status_code": 400,
//                "code": 22,
//                "msg": "验证码类短信1小时内同一手机号发送次数不能超过3次",
//                "detail": "验证码类短信1小时内同一手机号发送次数不能超过3次"
//    }
//    }
    public final static String KEY_CODE = "code";

    public final static int CODE_PHONE_INVALID = 2;//手机号格式不正确
    public final static int CODE_AUTHCODE_DAY_LIMIT = 17;//24小时内同一手机号发送次数超过限制
    public final static int CODE_AUTHCODE_HOUR_LIMIT = 22;//1小时内同一手机号发送次数超过限制
    public final static int CODE_AUTHCODE_HOUR_TOO_QUICK = 33;//频率过快
}
