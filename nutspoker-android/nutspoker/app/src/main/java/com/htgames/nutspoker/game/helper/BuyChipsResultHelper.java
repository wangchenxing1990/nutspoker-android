package com.htgames.nutspoker.game.helper;

import android.content.Context;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.game.model.BuyChipsResultCode;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;

/**
 */
public class BuyChipsResultHelper {

    /**
     * 获取控制带入失败原因
     * @param context
     * @param code
     * @return
     */
    public static String getBuyChipsResult(Context context, int code) {
        String result = "";
        switch (code) {
            case BuyChipsResultCode.MORE_CHIPS:
                return context.getString(R.string.buychips_result_morechips);
            case BuyChipsResultCode.FINALLY:
                return context.getString(R.string.buychips_result_finally);
            case BuyChipsResultCode.NOT_IN_REBUY:
                return context.getString(R.string.buychips_result_not_in_rebuy);
            case BuyChipsResultCode.NOT_IN_ADDON:
                return context.getString(R.string.buychips_result_not_in_addon);
            case BuyChipsResultCode.REJECT:
                return context.getString(R.string.buychips_result_reject);
            case BuyChipsResultCode.TIMEOUT:
                return context.getString(R.string.buychips_result_timeout);
            case BuyChipsResultCode.NETWORK_ERROR:
                return context.getString(R.string.buychips_result_network_error);
        }
        return result;
    }

    /**
     * 显示弹出框
     * @param context
     * @param appMessage
     */
    public static void showBuyChipsResultDialog(Context context, AppMessage appMessage) {
        if (appMessage == null || appMessage.attachObject == null
                || appMessage.type != AppMessageType.MatchBuyChips
                || !(appMessage.attachObject instanceof MatchBuyChipsNotify)) {
            return;
        }
        int resultCode = 0;
        int buyType = 0;
        MatchBuyChipsNotify matchBuyChipsNotify = (MatchBuyChipsNotify) appMessage.attachObject;
        resultCode = matchBuyChipsNotify.result_code;
        buyType = matchBuyChipsNotify.buyType;
        if (resultCode == 0) {
            return;
        }
        String result = getBuyChipsResult(context, resultCode);
        String message = context.getString(R.string.buychips_rebuy_failure_dialog_message, result);
        if (buyType == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT || buyType == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT) {
            message = context.getString(R.string.buychips_addon_failure_dialog_message, result);
        }
        EasyAlertDialogHelper.createOneButtonDiolag(context, "", message, context.getString(R.string.ok), false, null);
    }
}
