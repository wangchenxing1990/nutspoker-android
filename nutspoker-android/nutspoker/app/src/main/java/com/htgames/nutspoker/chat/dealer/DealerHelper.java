package com.htgames.nutspoker.chat.dealer;

import android.app.Activity;

import com.htgames.nutspoker.chat.session.SessionHelper;
import com.netease.nim.uikit.chesscircle.DealerConstant;

/**
 * Created by 20150726 on 2016/2/25.
 */
public class DealerHelper {
    /**
     * 打开客服聊天
     *
     * @param activity
     */
    public static void startDealerChatting(Activity activity) {
        SessionHelper.startP2PSession(activity, DealerConstant.dealerXiaominUid);
    }

    public static void startDealerChatting(Activity activity, String dealerUid) {
        SessionHelper.startP2PSession(activity, dealerUid);
    }
}
