package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;

/**
 * 系统提示View
 */
public class SystemTipView extends FrameLayout {
    TextView tv_system_tip;

    public SystemTipView(Context context) {
        super(context);
        init(context);
    }

    public SystemTipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SystemTipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_system_tip, this, true);
        tv_system_tip = (TextView) findViewById(R.id.tv_system_tip);
    }

    public void setTip(String tip) {
        tv_system_tip.setText(tip);
    }

    public void setTip(int tipResId) {
        tv_system_tip.setText(tipResId);
    }
}
