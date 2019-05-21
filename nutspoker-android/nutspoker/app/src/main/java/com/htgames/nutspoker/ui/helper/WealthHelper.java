package com.htgames.nutspoker.ui.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;

/**
 * 余额
 */
public class WealthHelper {

    public static void SetMoneyText(TextView tv, int value, Context context) {
        tv.setText(value >= 0 ? " +" + WealthHelper.getWealthShow(value) : " " + WealthHelper.getWealthShow(value));
        tv.setTextColor(ContextCompat.getColor(context, value >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
    }
    public static void SetGameTimesText(TextView tv, int value,Context context,boolean isReward) {
//        if(value > 0){
        if (isReward)
            tv.setText(context.getString(R.string.game_in_reward_circle, value));
        else
            tv.setText(context.getString(R.string.game_in_final_circle, value));
        tv.setVisibility(View.VISIBLE);
//        } else {
//            tv.setVisibility(View.GONE);
//        }
    }
    /**
     * 获取财富展示（大于等于6位显示为万）
     * @param wealth
     * @return
     */
    public static String getWealthShow(int wealth){
        StringBuffer wealthBuffer = new StringBuffer();
        if(wealth < 0){
            wealthBuffer.append("-");
        }
        wealth = Math.abs(wealth);
        if(String.valueOf(wealth).length() >= 6){
            wealthBuffer.append(wealth / 10000);
            int decimal = (wealth % 10000) / 1000;
            if(decimal != 0){
                wealthBuffer.append(".").append(decimal);
            }
            wealthBuffer.append(DemoCache.getContext().getString(R.string.ten_thousand));
        } else{
            wealthBuffer.append(wealth);
        }
        return wealthBuffer.toString();
    }
}
