package com.netease.nim.uikit.common.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 17/2/16.
 */

public class EasySwitchBtn extends View implements View.OnClickListener {
    public boolean isChecked = false;
    public int checkedImage;
    public int uncheckedImage;
    public EasySwitchBtn(Context context) {
        this(context, null);
    }

    public EasySwitchBtn(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasySwitchBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EasySwitchBtn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EasySwitchBtn, defStyleAttr, 0);
        isChecked = ta.getBoolean(R.styleable.EasySwitchBtn_EasySwitchBtn_isChecked, false);
        checkedImage = ta.getResourceId(R.styleable.EasySwitchBtn_EasySwitchBtn_checked_image, R.mipmap.icon_hunter_select);//
        uncheckedImage = ta.getResourceId(R.styleable.EasySwitchBtn_EasySwitchBtn_unchecked_image, R.mipmap.icon_hunter_normal);
        setOnClickListener(this);
        setBackgroundResource(isChecked ? checkedImage : uncheckedImage);
    }

    @Override
    public void onClick(View v) {
        isChecked = !isChecked;
        setBackgroundResource(isChecked ? checkedImage : uncheckedImage);
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        setBackgroundResource(isChecked ? checkedImage : uncheckedImage);
    }
}
