package com.htgames.nutspoker.ui.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 没有表情的过滤
 */
public class NoEmojiInputFilter implements InputFilter {

    private final String reg="[^a-zA-Z0-9\u4E00-\u9FA5_]";
//    private final String reg ="^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Pattern pattern = Pattern.compile(reg);
        if(source.equals(" ")){
            return source;
        }
        //正则匹配是否是表情符号
        Matcher matcher = pattern.matcher(source.toString());
        if (!matcher.matches()) {
            //不包含表情
            return source;
        }
        return "";
    }
}
