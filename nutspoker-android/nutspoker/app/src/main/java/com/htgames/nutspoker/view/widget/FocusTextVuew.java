package com.htgames.nutspoker.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 始终获取焦点的TextView，用于跑马灯
 */
public class FocusTextVuew extends TextView {
    public FocusTextVuew(Context context) {
        super(context);
    }

    public FocusTextVuew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextVuew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
//        return super.isFocused();
        return true;
    }
}


