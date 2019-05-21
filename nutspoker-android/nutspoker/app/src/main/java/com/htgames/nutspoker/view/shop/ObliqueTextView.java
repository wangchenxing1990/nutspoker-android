package com.htgames.nutspoker.view.shop;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 倾斜的字体
 */
public class ObliqueTextView extends TextView {

    public ObliqueTextView(Context context) {
        super(context);
    }

    public ObliqueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //倾斜度45,上下左右居中
//        canvas.rotate(-45, getMeasuredWidth() / 3, getMeasuredHeight() / 3);
        //倾斜度45,上下左右居中
        canvas.rotate(-45, 5, 5);
//        canvas.rotate(-45, getMeasuredWidth() / 4, getMeasuredHeight() / 4);
        super.onDraw(canvas);
    }
}