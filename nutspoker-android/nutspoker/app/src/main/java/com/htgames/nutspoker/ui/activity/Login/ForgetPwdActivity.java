package com.htgames.nutspoker.ui.activity.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CountryEntity;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.activity.CountrySelectActivity;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.action.AuthcodeAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class ForgetPwdActivity extends BaseActivity {
    private final static String TAG = "ForgetPwdActivity";
    public static final int REQUESTCODE_COUNTRY_SELECT = 200;//国家选择
    RegionEntity regionEntity;
    AuthcodeAction mAuthcodeAction;
    String phone;
    String mCountryCode;
    @BindView(R.id.register_phone_code_tv)
    TextView countryCodeTextView;
    @OnClick(R.id.register_phone_code_tv) void phoneCodeClick() {
        CountrySelectActivity.start(ForgetPwdActivity.this, REQUESTCODE_COUNTRY_SELECT);
    }
    @BindView(R.id.register_username_et)
    EditText mPhoneNumEditText;
    @BindView(R.id.btn_register_next_step_btn) Button mNextBtn;
    @OnClick(R.id.btn_register_next_step_btn) void nextBtnClick() {
        if (TextUtils.isEmpty(mPhoneNumEditText.getText())) {
            Toast.makeText(getApplicationContext(), R.string.register_phone_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!BaseTools.isMobileNO(mCountryCode, mPhoneNumEditText.getText().toString())) {
            //手机号码无效
            android.widget.Toast.makeText(getApplicationContext(), getString(R.string.login_phone_invalid), android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        phone = mPhoneNumEditText.getText().toString();
        getAuthcode(phone); //获取验证码
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUnbinder = ButterKnife.bind(this);//绑定对应的View
        phone = getIntent().getStringExtra(Extras.EXTRA_PHONE);
        mCountryCode = getIntent().getStringExtra(Extras.EXTRA_COUNTRY_CODE);
        initHead();
        initView();
    }

    private void initView() {
        mNextBtn.setEnabled(TextUtils.isEmpty(phone) ? false : true);
        mPhoneNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mNextBtn.setEnabled(true);
                } else {
                    mNextBtn.setEnabled(false);
                }
            }
        });
        if (!TextUtils.isEmpty(phone)) {
            mPhoneNumEditText.setText(phone);
        }
        if (!TextUtils.isEmpty(mCountryCode)) {
            countryCodeTextView.setText("+" + mCountryCode);
        }
    }

    private void initHead() {
        TextView headTitle = (TextView) findViewById(R.id.tv_head_title);
        headTitle.setText(R.string.reset_password_head);
        headTitle.setTextColor(Color.WHITE);
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText(getResources().getString(R.string.back));
        btn_head_back.setTextColor(Color.WHITE);
    }

    /**
     * 获取验证码
     */
    public void getAuthcode(final String phone) {
        if (mAuthcodeAction == null) {
            mAuthcodeAction = new AuthcodeAction(this, null);
        }
        mAuthcodeAction.getAuthCode(phone, LoginActivity.FROM_RESET, true, null, mCountryCode, false, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    Toast.makeText(ForgetPwdActivity.this, R.string.authcode_get_success, Toast.LENGTH_SHORT).show();
                    AuthcodeActivity.start(ForgetPwdActivity.this, LoginActivity.FROM_RESET, phone, "",mCountryCode ,"");
                }
            }
            @Override
            public void onFailed() {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthcodeAction != null) {
            mAuthcodeAction.onDestroy();
            mAuthcodeAction = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_COUNTRY_SELECT && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra(Extras.EXTRA_DATA);
            mCountryCode = countryEntity == null ? mCountryCode : countryEntity.countryCode;
            countryCodeTextView.setText("+" + (countryEntity == null ? "86" : countryEntity.countryCode));
        }
    }
}
