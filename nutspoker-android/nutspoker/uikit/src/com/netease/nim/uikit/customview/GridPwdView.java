package com.netease.nim.uikit.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.OnPasswordChangedListener;
import com.netease.nim.uikit.interfaces.PasswordType;
import com.netease.nim.uikit.interfaces.PasswordView;

/**
 * Created by 周智慧 on 16/12/29.
 */

public class GridPwdView extends LinearLayout implements PasswordView, NumKeyboard.INumKeyboardClick {
    private final static String TAG = "GridPwdView";
    private static final int DEFAULT_PASSWORDLENGTH = 6;
    private static final int DEFAULT_TEXTSIZE = 25;
    private static final String DEFAULT_TRANSFORMATION = "●";
    private static final int DEFAULT_LINECOLOR = 0xaa888888;
    private static final int DEFAULT_GRIDCOLOR = 0xffffffff;
    private ColorStateList textColor;
    private int textSize = DEFAULT_TEXTSIZE;
    private int lineWidth;
    private int lineColor;
    private int gridColor;
    private Drawable outerLineDrawable;
    private int passwordLength;
    //单字符
    private String passwordTransformation;
    private String[] passwordArr;
    private TextView[] viewArr;

    private OnPasswordChangedListener listener;

    private PasswordTransformationMethod transformationMethod;
    public GridPwdView(Context context) {
        this(context, null);
    }

    public GridPwdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridPwdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GridPwdView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.gridPasswordView, defStyleAttr, 0);
        textColor = ta.getColorStateList(R.styleable.gridPasswordView_textColor);
        if (textColor == null) {
            textColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        }
        int textSize = ta.getDimensionPixelSize(R.styleable.gridPasswordView_textSize, -1);
        if (textSize != -1) {
            this.textSize = ScreenUtil.px2sp(context, textSize);
        }
        lineWidth = (int) ta.getDimension(R.styleable.gridPasswordView_lineWidth, ScreenUtil.dp2px(getContext(), 1));
        lineColor = ta.getColor(R.styleable.gridPasswordView_lineColor, DEFAULT_LINECOLOR);
        gridColor = ta.getColor(R.styleable.gridPasswordView_gridColor, DEFAULT_GRIDCOLOR);
//        outerLineDrawable = generateBackgroundDrawable();
        passwordLength = ta.getInt(R.styleable.gridPasswordView_passwordLength, DEFAULT_PASSWORDLENGTH);
        passwordTransformation = ta.getString(R.styleable.gridPasswordView_passwordTransformation);
        if (TextUtils.isEmpty(passwordTransformation)) {
            passwordTransformation = DEFAULT_TRANSFORMATION;
        }
        ta.recycle();
        passwordArr = new String[passwordLength];
        viewArr = new TextView[passwordLength];
    }

    private GradientDrawable generateBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(gridColor);
        drawable.setCornerRadius(ScreenUtil.dp2px(getContext(), 3));
        drawable.setStroke(lineWidth, lineColor);
        return drawable;
    }

    private void initViews(Context context) {
        setShowDividers(SHOW_DIVIDER_NONE);
        setOrientation(HORIZONTAL);
        transformationMethod = new CustomPasswordTransformationMethod(passwordTransformation);
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        int margins = ScreenUtil.dp2px(context, 3);//两个框框之间的间隔是6dp
        int marginToPhoneEdge = ScreenUtil.dp2px(context, 20);//框框距离手机边缘距离20dp
        int itemWidth = (int) ((ScreenUtil.screenMin - marginToPhoneEdge * 2f - margins * (passwordLength - 1)) / passwordLength);//ScreenUtil.dp2px(context, 48);
        int index = 0;
        while (index < passwordLength) {
            TextView textView = new TextView(context);
            textView.setBackgroundDrawable(context.getResources().getDrawable(R.mipmap.frame_invite));//textView.setBackgroundDrawable(outerLineDrawable);
            textView.setTextColor(textColor);
            textView.setTextSize(textSize);
            textView.setGravity(Gravity.CENTER);
            textView.setTransformationMethod(transformationMethod);
            LayoutParams textViewParams = new LayoutParams(itemWidth, itemWidth);
            textViewParams.setMargins(margins, 0, margins, 0);
            addView(textView, textViewParams);
            viewArr[index] = textView;
            index++;
        }
        setGravity(Gravity.CENTER_HORIZONTAL);
    }

    private void notifyTextChanged() {
        if (listener == null) {
            return;
        }
        String currentPsw = getPassWord();
        listener.onChanged(currentPsw);
        if (currentPsw.length() == passwordLength) {
            listener.onMaxLength(currentPsw);
        }

    }

    @Override
    public String getPassWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passwordArr.length; i++) {
            if (!TextUtils.isEmpty(passwordArr[i]))
                sb.append(passwordArr[i]);
        }
        return sb.toString();
    }

    @Override
    public void clearPassword() {
        for (int i = 0; i < passwordArr.length; i++) {
            passwordArr[i] = null;
            viewArr[i].setText(null);
        }
    }

    @Override
    public void setPassword(String password) {
        clearPassword();
        if (TextUtils.isEmpty(password)) {
            return;
        }
        char[] pswArr = password.toCharArray();
        for (int i = 0; i < pswArr.length; i++) {
            if (i < passwordArr.length) {
                passwordArr[i] = pswArr[i] + "";
                viewArr[i].setText(passwordArr[i]);
            }
        }
    }

    @Override
    public void setPasswordVisibility(boolean visible) {
        for (TextView textView : viewArr) {
            textView.setTransformationMethod(visible ? null : transformationMethod);
            if (textView instanceof EditText) {
                EditText et = (EditText) textView;
                et.setSelection(et.getText().length());
            }
        }
    }

    @Override
    public void togglePasswordVisibility() {
        boolean currentVisible = getPassWordVisibility();
        setPasswordVisibility(!currentVisible);
    }

    private boolean getPassWordVisibility() {
        return viewArr[0].getTransformationMethod() == null;
    }

    @Override
    public void setOnPasswordChangedListener(OnPasswordChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void setPasswordType(PasswordType passwordType) {
        boolean visible = getPassWordVisibility();
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        switch (passwordType) {
            case TEXT:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case TEXTVISIBLE:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;
            case TEXTWEB:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
        }
        for (TextView textView : viewArr) {
            textView.setInputType(inputType);
        }
        setPasswordVisibility(visible);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putStringArray("passwordArr", passwordArr);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            passwordArr = bundle.getStringArray("passwordArr");
            state = bundle.getParcelable("instanceState");
            setPassword(getPassWord());
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void numClick(int num) {
        for (int i = 0; i < passwordLength; i++) {
            if (TextUtils.isEmpty(passwordArr[i])) {
                passwordArr[i] = "" + num;
                viewArr[i].setText("" + num);
                break;
            }
        }
        notifyTextChanged();
    }

    @Override
    public void deleteClick() {
        for (int i = passwordLength - 1; i >= 0; i--) {
            if (!TextUtils.isEmpty(passwordArr[i])) {
                passwordArr[i] = null;
                viewArr[i].setText(null);
                break;
            }
        }
        notifyTextChanged();
    }
}
