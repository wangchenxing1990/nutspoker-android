package com.htgames.nutspoker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.interfaces.OnPasswordChangedListener;
import com.netease.nim.uikit.customview.CustomPasswordTransformationMethod;
import com.netease.nim.uikit.interfaces.PasswordType;
import com.netease.nim.uikit.interfaces.PasswordView;
import com.htgames.nutspoker.view.gridpasswordview.imebugfixer.ImeDelBugFixedEditText;

/**
 * Created by 周智慧 on 16/11/17.
 * 校验验证码页面的输入验证码的viewgroup
 */

public class AuthcodeView extends FrameLayout implements View.OnFocusChangeListener, PasswordView {
    LinearLayout authcodeEditTextLL;//包含四个验证码的viewgroup
    private int passwordType;
    private ImeDelBugFixedEditText inputView;
    private String[] passwordArr;
    private TextView[] viewArr;
    private static final int DEFAULT_PASSWORDLENGTH = 4;//默认验证码长度
    int passwordLength = DEFAULT_PASSWORDLENGTH;//验证码长度,//默认验证码长度4
    TextView descriptionTV;//请输入验证码textview
    private PasswordTransformationMethod transformationMethod;
    private String passwordTransformation;
    private static final String DEFAULT_TRANSFORMATION = "●";

    public AuthcodeView(Context context) {
        this(context, null);
    }

    public AuthcodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AuthcodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.gridPasswordView, defStyleAttr, 0);
        passwordType = ta.getInt(R.styleable.gridPasswordView_passwordType, 0);
        String pdTransformation = ta.getString(R.styleable.gridPasswordView_passwordTransformation);
        passwordTransformation = TextUtils.isEmpty(pdTransformation) ? DEFAULT_TRANSFORMATION : pdTransformation;
        ta.recycle();
    }

    private void init() {
        passwordArr = new String[passwordLength];
        viewArr = new TextView[passwordLength];
        transformationMethod = new CustomPasswordTransformationMethod(passwordTransformation);
        LayoutParams lpDescription = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpDescription.gravity = Gravity.CENTER;
        descriptionTV = new TextView(getContext());
        descriptionTV.setGravity(Gravity.CENTER);
        descriptionTV.setText(getResources().getString(R.string.authcode_please_enter));
        descriptionTV.setTextColor(getResources().getColor(R.color.login_grey_color));
        addView(descriptionTV, lpDescription);
        inflaterViews();
    }

    private void inflaterViews() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LayoutParams lpEditTextLL = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) getResources().getDimension(R.dimen.auth_code_single_code_width));
        lpEditTextLL.gravity = Gravity.CENTER;
        authcodeEditTextLL = new LinearLayout(getContext());
        authcodeEditTextLL.setOrientation(LinearLayout.HORIZONTAL);
        addView(authcodeEditTextLL, lpEditTextLL);

        LinearLayout.LayoutParams singleEditLP = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        singleEditLP.weight = 1;
        singleEditLP.gravity = Gravity.CENTER_VERTICAL;
        RelativeLayout inputViewRL = (RelativeLayout) inflater.inflate(R.layout.authcode_inputview, null);
        authcodeEditTextLL.addView(inputViewRL, singleEditLP);
        inputView = (ImeDelBugFixedEditText) findViewById(R.id.auth_code_input_view);
        inputView.setMaxEms(passwordLength);
        inputView.addTextChangedListener(textWatcher);
        inputView.setDelKeyEventListener(onDelKeyEventListener);
        viewArr[0] = inputView;
        setCustomAttr(inputView);
        for (int index = 1; index < passwordLength; index++) {
            RelativeLayout textViewRL = (RelativeLayout) inflater.inflate(R.layout.authcode_textview, null);
            authcodeEditTextLL.addView(textViewRL, singleEditLP);
            viewArr[index] = (TextView) textViewRL.findViewById(R.id.auth_code_tv);
            setCustomAttr(viewArr[index]);
        }

        setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            inputView.setFocusable(true);
            inputView.setFocusableInTouchMode(true);
            inputView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(inputView, InputMethodManager.SHOW_IMPLICIT);
        }
    };

    private void setCustomAttr(TextView view) {
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        switch (passwordType) {
            case 1:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case 2:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;
            case 3:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
        }
        view.setInputType(inputType);
        view.setTransformationMethod(transformationMethod);
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
            inputView.removeTextChangedListener(textWatcher);
            setPassword(getPassWord());
            inputView.addTextChangedListener(textWatcher);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
    }

    private OnPasswordChangedListener mPasswordChangedListener;
    private void notifyTextChanged() {
        if (mPasswordChangedListener == null)
            return;
        String currentPsw = getPassWord();
        mPasswordChangedListener.onChanged(currentPsw);
        if (currentPsw.length() == passwordLength)
            mPasswordChangedListener.onMaxLength(currentPsw);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            descriptionTV.setVisibility(s == null || s.length() <= 0 ? View.VISIBLE : View.INVISIBLE);
            if (s == null) {
                return;
            }
            String newStr = s.toString();
            if (newStr.length() == 1) {
                passwordArr[0] = newStr;
                notifyTextChanged();
            } else if (newStr.length() == 2) {
                String newNum = newStr.substring(1);
                for (int i = 0; i < passwordArr.length; i++) {
                    if (passwordArr[i] == null) {
                        passwordArr[i] = newNum;
                        viewArr[i].setText(newNum);
                        notifyTextChanged();
                        break;
                    }
                }
                inputView.removeTextChangedListener(this);
                inputView.setText(passwordArr[0]);
                if (inputView.getText().length() >= 1){
                    inputView.setSelection(1);
                }
                inputView.addTextChangedListener(this);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private ImeDelBugFixedEditText.OnDelKeyEventListener onDelKeyEventListener = new ImeDelBugFixedEditText.OnDelKeyEventListener() {
        @Override
        public void onDeleteClick() {
            for (int i = passwordArr.length - 1; i >= 0; i--) {
                if (passwordArr[i] != null) {
                    passwordArr[i] = null;
                    viewArr[i].setText(null);
                    notifyTextChanged();
                    break;
                } else {
                    viewArr[i].setText(null);
                }
            }
        }
    };

    /**
     * Return the text the PasswordView is displaying.
     */
    @Override
    public String getPassWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < passwordArr.length; i++) {
            if (passwordArr[i] != null)
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
        if (TextUtils.isEmpty(password))
            return;
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
        this.mPasswordChangedListener = listener;
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
        for (TextView textView : viewArr)
            textView.setInputType(inputType);
        setPasswordVisibility(visible);
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
        init();
    }
}

