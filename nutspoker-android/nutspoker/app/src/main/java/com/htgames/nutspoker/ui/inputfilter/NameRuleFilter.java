package com.htgames.nutspoker.ui.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称规则
 * 支持1.中英文 2.特殊字符 3.空格
 */
public class NameRuleFilter  implements InputFilter {
    private final static String TAG = "NameRuleFilter";
    private final String reg="^[A-Za-z0-9\\u4e00-\\u9fa5\\x20\\x5f\\x09]*$";
//    private final String reg="^[\\u4E00-\\u9FA5A-Za-z0-9_\\s]+$";
//    private final String reg="^[\\u4e00-\\u9fa5\\x20-\\x7e\\uFF00-\\uFFEF\\u3000-\\u303F]*$";
//    private final String notAllowReg="/\\/|\\~|\\!|\\@|\\#|\\\\$|\\%|\\^|\\&|\\*|\\(|\\)|\\_|\\+|\\{|\\}|\\:|\\<|\\>|\\?|\\[|\\]|\\,|\\.|\\/|\\;|\\'|\\`|\\-|\\=|\\\\\\|\\|/";

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source.toString());
        if (matcher.matches()) {
            return source;
        }
        LogUtil.i(TAG , "不符合规则：" + source);
        return "";
    }
}
