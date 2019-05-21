package com.netease.nim.uikit.chesscircle;

import android.text.TextUtils;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 荷官（客服）相关
 */
public class DealerConstant {
    public final static String dealerXiaominUid = "8";//客服小明的UID
    public final static String dealer123456Uid = "10";//客服小云的UID   账号密码都是htgames1234（正式服）
    public final static int DEALER_UID_INTERVAL = 100;

    /**
     * 是否是客服
     * @param sessionId
     * @return
     */
    public static boolean isDealer(String sessionId) {
        boolean isNumber = isNumeric(sessionId);//是否是数字
        if(isNumber && !TextUtils.isEmpty(sessionId) && Long.parseLong(sessionId) <= DEALER_UID_INTERVAL) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        if (StringUtil.isSpace(str)) {
            return false;
        }
        if (str.trim().equals("-")) {
            return false;
        }
        String temp = str.startsWith("-") ? str.substring(1) : str;
        for (int i = temp.length(); --i >= 0; ) {
            if (!Character.isDigit(temp.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDealerByPhone(String phone) {
        if (!StringUtil.isSpace(phone) && phone.equals("123456")) {
            return true;
        }
        return false;
    }

    /**
     * 获取客服名称
     * @param sessionId
     * @return
     */
    public static String getDealerNickname(String sessionId) {
        if(NimUserInfoCache.getInstance().hasUser(sessionId)) {
            return NimUserInfoCache.getInstance().getUserDisplayName(sessionId);
        } else{
            return NimUIKit.getContext().getString(R.string.dealer);
        }
    }
}
