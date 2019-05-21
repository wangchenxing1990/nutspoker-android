package com.htgames.nutspoker.ui.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * 输入过滤器，没有空格和回车
 */
public class NoSpaceAndEnterInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source.equals(" ") || source.toString().matches("\\n")) { // for backspace
            return "";
        }
//            source.equals(" ") ||
//            if (source.toString().matches("[a-zA-Z ]+")) {
//                return source;
//            }
        return source;
    }
}