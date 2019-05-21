package com.netease.nim.uikit.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.netease.nim.uikit.R;

/**
 * Created by 周智慧 on 16/12/29.
 */

public class NumKeyboard extends LinearLayout implements View.OnClickListener {
    public NumKeyboard(Context context) {
        this(context, null);
    }

    public NumKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NumKeyboard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(context).inflate(R.layout.view_num_keyboard, this, true);
        findViewById(R.id.hall_join_key_1).setOnClickListener(this);
        findViewById(R.id.hall_join_key_2).setOnClickListener(this);
        findViewById(R.id.hall_join_key_3).setOnClickListener(this);
        findViewById(R.id.hall_join_key_4).setOnClickListener(this);
        findViewById(R.id.hall_join_key_5).setOnClickListener(this);
        findViewById(R.id.hall_join_key_6).setOnClickListener(this);
        findViewById(R.id.hall_join_key_7).setOnClickListener(this);
        findViewById(R.id.hall_join_key_8).setOnClickListener(this);
        findViewById(R.id.hall_join_key_9).setOnClickListener(this);
        findViewById(R.id.hall_join_key_0).setOnClickListener(this);
        findViewById(R.id.hall_join_key_del).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (numKeyboardClick == null) {
            return;
        }
        int viewId = v.getId();
        if (viewId == R.id.hall_join_key_1) {
            numKeyboardClick.numClick(1);
        } else if (viewId == R.id.hall_join_key_2) {
            numKeyboardClick.numClick(2);
        } else if (viewId == R.id.hall_join_key_3) {
            numKeyboardClick.numClick(3);
        } else if (viewId == R.id.hall_join_key_4) {
            numKeyboardClick.numClick(4);
        } else if (viewId == R.id.hall_join_key_5) {
            numKeyboardClick.numClick(5);
        } else if (viewId == R.id.hall_join_key_6) {
            numKeyboardClick.numClick(6);
        } else if (viewId == R.id.hall_join_key_7) {
            numKeyboardClick.numClick(7);
        } else if (viewId == R.id.hall_join_key_8) {
            numKeyboardClick.numClick(8);
        } else if (viewId == R.id.hall_join_key_9) {
            numKeyboardClick.numClick(9);
        } else if (viewId == R.id.hall_join_key_0) {
            numKeyboardClick.numClick(0);
        } else if (viewId == R.id.hall_join_key_del) {
            numKeyboardClick.deleteClick();
        }
    }

    public void setNumKeyboardClick(INumKeyboardClick clickListener) {
        numKeyboardClick = clickListener;
    }
    private INumKeyboardClick numKeyboardClick;
    public interface INumKeyboardClick {
        void numClick(int num);
        void deleteClick();
    }
}
