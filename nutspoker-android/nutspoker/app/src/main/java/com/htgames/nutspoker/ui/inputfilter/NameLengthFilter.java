package com.htgames.nutspoker.ui.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 中/英长度过滤器
 */
public class NameLengthFilter implements InputFilter {
    private final static String TAG = "NameLengthFilter";
    private int MAX_EN;// 最大英文/数字长度 一个汉字算两个字母
    String regEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字

    public NameLengthFilter(int mAX_EN) {
        super();
        MAX_EN = mAX_EN;
    }

    /**
     *
     * @param source //输入的文字
     * @param start //开始位置
     * @param end //结束位置
     * @param dest //当前显示的内容
     * @param dstart //当前开始位置
     * @param dend  //当前结束位置
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
//        int destCount = dest.toString().length() + getChineseCount(dest.toString());
//        int sourceCount = source.toString().length() + getChineseCount(source.toString());
//        Log.d("NameLengthFilter", "长度 ：" + (destCount + sourceCount));
//        if (destCount + sourceCount > MAX_EN) {
//            Log.d("NameLengthFilter", "超过长度 ：" + (destCount + sourceCount));
//            return "";
//        } else {
//            return source;
//        }
        int destCount = dest.length() + getChineseCount(dest.toString());
        //输入中的文字数量
        int sourceCount = source.length() + getChineseCount(source.toString());
        //其中mMax是文本的长度上限，keep是mMax与当前text的长度的差值。
        int keep = MAX_EN - destCount;
        LogUtil.i(TAG ,"start : " + start + ";keep :" + keep + ";destCount:" + destCount + ";sourceCount:" +sourceCount);
        if (keep <= 0) {
            //当keep小于0时，也就是文本已经超过长度上限时，返回""
            return "";
        } else if (keep >= sourceCount) {
            //当文本没超过长度上限时，返回null，保持文本原样不变（keep original）
            return source; // keep original
        } else {
            int length = 0;
            int sourceEnd = 0;//结尾
            //中英大于最大数（最大数量 - 已经输入的字符数（中文当2个））
            for (int i = 0; i < source.length(); i++) {
                if (length > keep) {
                    //如果长度大于可输入长度
                    break;
                }
                length = length + 1;
                if (getChineseCount(String.valueOf(source.charAt(i))) != 0) {
                    if (length > keep) {
                        //如果长度大于可输入长度
                        break;
                    }
                    length = length + 1;
                }
                sourceEnd = sourceEnd + 1;
            }
            if(sourceEnd == 0){
                return "";
            }
            return source.subSequence(0, sourceEnd);
        }
    }

    private int getChineseCount(String str) {
        int count = 0;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }
}