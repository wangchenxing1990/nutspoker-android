package com.htgames.nutspoker.game.util;

import android.text.SpannableStringBuilder;

/**
 * Created by Administrator on 2016/5/23.
 */
public class SpannableUtils {

    public static SpannableStringBuilder getSpannableString(String normalText , String dealText) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
//        SpannableString spanText = new SpannableString(dealText);
//        spanText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        stringBuilder.append(normalText);
        stringBuilder.append(dealText);//stringBuilder.append(spanText);
        return stringBuilder;
    }
}
