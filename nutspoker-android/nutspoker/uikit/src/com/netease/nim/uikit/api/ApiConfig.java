package com.netease.nim.uikit.api;

import android.content.Context;

import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.netease.nim.uikit.common.util.EncrUtil.decrypt;

/**
 * Created by glp on 2016/7/22.
 * 配置正式环境~
 * 第一步 ，{@link ApiConfig.isTestVersion} 改成false
 * 第二步，去app的build.gradle文件 ，ext.isTestVersion 改成 false
 * 第三步，版本号迭代,修改getTheAppCode，getTheAppVersion，getTheGameVersion
 * 台湾版本配置
 * 检查 {@link ApiConfig.AppVersion.isTaiwanVersion} 改成true
 * 在app的build.gradle 中的 isTaiwanVersion
 */

public class ApiConfig {
    /***
     * APP版本定义
     */
    public static boolean isTestVersion = true;//是否是测试版本
    public interface AppVersion {
        boolean isTaiwanVersion = false;
        String AREA_TW = "886";//区域版本（台湾）
        String AREA_CHNIA = "86";//区域版本（台湾）
    }


    /***
     * WebSocke 请求的域名配置
     */
    public static final String HOST_WEBSOCKET_FORMAL = "ws://ws.everpoker.win:50200/";//正式WB环境：host
    public static final String HOST_WEBSOCKET_TEST = "ws://test.api.everpoker.win:50100/";//测试WB环境：host

    public static String getHostWebsocket() {
        if (isTestVersion)
            return HOST_WEBSOCKET_TEST;
        return HOST_WEBSOCKET_FORMAL;
    }

    /***
     * Lua脚本资源热更新 请求的域名配置
     */
    public static final String FILE_HOST_FORMAL = "http://download.everpoker.htgames.cn/update/";//http://121.43.38.85/";//热更新服务器：host -正式
    public static final String FILE_HOST_TEST = "http://192.168.0.118:9195/";//http://192.168.0.156:8000/";//热更新服务器：host -测试

    public static String getFileHost() {
        if (isTestVersion)
            return FILE_HOST_TEST;
        return FILE_HOST_FORMAL;
    }

    /**
     * aes密钥
     */
    public static final String APP_SECRET_KEY = "a0c4c3h4r8f6ghijklm3o1qysauvwzyz";

    /**
     * 获取牌谱分享地址
     *
     * @param hid
     * @return
     */
    public static String getPaipuShareUrl(String hid) {
        String paipuShareUrl = HostManager.getHost() + "public/video?id=" + reverse(hid);
        LogUtil.i("ShareUrl", paipuShareUrl);
        return paipuShareUrl;
    }

    private static String reverse(String str) {
        StringBuilder sb = new StringBuilder(str);
        str = sb.reverse().toString();
        return str;
    }

    //微信大陆
    public static final String APP_WEIXIN_DL = "wx0c09060fb527ab4c";//换名之前-->"wxd0ee307a98a726cb";//signature: 'e375c09db2b543000e1d22c1ac2d1b1d',
    public static final String SECRET_WEIXIN_DL = "";//换名之前-->"9cd8a6490e70cc37bf9f051004351a82";
    //微信台湾
    public static final String APP_WEIXIN_TW = "wxa71b203c100fe666";
    public static final String SECRET_WEIXIN_TW = "35591cda6182ec88c517f566f453adf9";

    //微博大陆
    public static final String APP_WEIBO_DL = "1235355227";//
    public static final String SECRET_WEIBO_DL = "1296ae7a7e99f23b3531d45514d64935";//
    //微博台湾
    public static final String APP_WEIBO_TW = "1235355227";
    public static final String SECRET_WEIBO_TW = "1296ae7a7e99f23b3531d45514d64935";

    /**************************************************************YX************************************************************************/
    private static final String YXIN_KEY_VALUE_TEST = "";
    private static final String YXIN_KEY_VALUE = "";
    public static String getYxinKeyValue(Context context) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(context.getAssets().open("yummy.txt"), "UTF-8"));
            String e;
            int index = 0;
            while ((e = br.readLine()) != null) {
                index++;
                if (isTestVersion && index == 6) {
                    return decrypt(e);
                }
                if (!isTestVersion && index == 8) {
                    return decrypt(e);
                }
            }
        } catch (Exception var10) {
            throw new RuntimeException(var10);
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException var9) {
            }
        }
        return isTestVersion ? YXIN_KEY_VALUE_TEST : YXIN_KEY_VALUE;
    }
    /**************************************************************youmeng************************************************************************/
    public static String UMENG_APPKEY_VALUE_TEST = "58acfa1982b6352eaf001111";
    public static String UMENG_APPKEY_VALUE = "584f9e81c89576363f000239";
    public static String getUmengAppkeyValue() {
        return isTestVersion ? UMENG_APPKEY_VALUE_TEST : UMENG_APPKEY_VALUE;
    }
}
