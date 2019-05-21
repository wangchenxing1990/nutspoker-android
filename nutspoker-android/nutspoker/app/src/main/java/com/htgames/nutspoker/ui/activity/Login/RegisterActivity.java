package com.htgames.nutspoker.ui.activity.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.api.YunpianCode;
import com.htgames.nutspoker.chat.region.RegionEntity;
import com.htgames.nutspoker.chat.region.activity.CountrySelectActivity;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.AuthcodeAction;
import com.htgames.nutspoker.ui.activity.System.WebViewActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.CountryEntity;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.SpanUtils;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册
 */
public class RegisterActivity extends BaseActivity {
    private int from = LoginActivity.FROM_REGISTER;
    private final static String TAG = "RegisterActivity";
    public static final int REQUESTCODE_COUNTRY_SELECT = 200;//国家选择
    TextView btn_register_terms;
    RegionEntity regionEntity;
    AuthcodeAction mAuthcodeAction;
    String phone;
    String mCountryCode;
    @BindView(R.id.tv_register_protocol) TextView tv_register_protocol;
    @BindView(R.id.register_phone_code_tv) TextView countryCodeTextView;
    @OnClick(R.id.register_phone_code_tv) void phoneCodeClick() {
        CountrySelectActivity.start(RegisterActivity.this, REQUESTCODE_COUNTRY_SELECT);
    }
    @BindView(R.id.register_username_et) EditText mPhoneNumEditText;
    @BindView(R.id.btn_register_next_step_btn) Button mNextBtn;
    @OnClick(R.id.btn_register_next_step_btn) void nextBtnClick() {
        if (TextUtils.isEmpty(mPhoneNumEditText.getText())) {
            Toast.makeText(getApplicationContext(), R.string.register_phone_not_null, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!BaseTools.isMobileNO(mCountryCode, mPhoneNumEditText.getText().toString())) {
            //手机号码无效
            Toast.makeText(getApplicationContext(), getString(R.string.login_phone_invalid), Toast.LENGTH_SHORT).show();
            return;
        }
        phone = mPhoneNumEditText.getText().toString();
        getAuthcode(phone); //获取验证码
    }

    public static void startByRegister(Activity activity, String edtPhone, String countryCode) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        if(!TextUtils.isEmpty(edtPhone) && BaseTools.isMobileNO(countryCode , edtPhone)) {
            //如果手机号是正确的，自动填充
            intent.putExtra(Extras.EXTRA_PHONE, edtPhone);
        }
        intent.putExtra(Extras.EXTRA_FROM, LoginActivity.FROM_REGISTER);
        intent.putExtra(Extras.EXTRA_COUNTRY_CODE, countryCode);
        activity.startActivity(intent);
    }

    public static void startByReset(Activity activity, String edtPhone, String countryCode) {
        Intent intent = new Intent(activity, RegisterActivity.class);
        if(!TextUtils.isEmpty(edtPhone) && BaseTools.isMobileNO(countryCode , edtPhone)) {
            //如果手机号是正确的，自动填充
            intent.putExtra(Extras.EXTRA_PHONE, edtPhone);
        }
        intent.putExtra(Extras.EXTRA_FROM, LoginActivity.FROM_RESET);
        intent.putExtra(Extras.EXTRA_COUNTRY_CODE, countryCode);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mUnbinder = ButterKnife.bind(this);//绑定对应的View
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, LoginActivity.FROM_REGISTER);
        phone = getIntent().getStringExtra(Extras.EXTRA_PHONE);
        mCountryCode = getIntent().getStringExtra(Extras.EXTRA_COUNTRY_CODE);
        initRegisterProtocol();
        initHead();
        initView();
        initRegion();
        mPhoneNumEditText.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(UserConstant.MAX_PHONE_NUM_LENGTH)});
    }

    private void initRegisterProtocol() {
        tv_register_protocol.setText(new SpanUtils().append("注册即表示同意").setForegroundColor(getResources().getColor(R.color.shop_text_no_select_color))
                .append("《巨潮网络游戏许可及服务协议》").setForegroundColor(getResources().getColor(R.color.login_solid_color))
                .create());
        tv_register_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.start(RegisterActivity.this, WebViewActivity.TYPE_POKERCLANS_PROTOCOL, ApiConstants.URL_PROTOCOL_REGISTER);
            }
        });
    }

    public void initRegion() {
        regionEntity = RegionDBTools.getRegionChina();
        if (regionEntity != null) {
//            btn_region.setText(regionEntity.getName());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String show = intent.getStringExtra(Extras.EXTRA_REGION_SHOW);
            regionEntity = (RegionEntity) intent.getSerializableExtra(Extras.EXTRA_REGION_DATA);
            if (!TextUtils.isEmpty(show)) {
//                btn_region.setText(show);
            }
        }
    }

    private void initHead() {
        TextView headTitle = (TextView) findViewById(R.id.tv_head_title);
        if (from == LoginActivity.FROM_REGISTER) {
            headTitle.setText(R.string.register);
        } else {
            headTitle.setText(R.string.reset_password_head);
        }
        headTitle.setTextColor(Color.WHITE);
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText(getResources().getString(R.string.back));
        btn_head_back.setTextColor(Color.WHITE);
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

    /**
     * 获取验证码
     */
    public void getAuthcode(final String phone) {
        if (mAuthcodeAction == null) {
            mAuthcodeAction = new AuthcodeAction(this, null);
        }
        mAuthcodeAction.getAuthCode(phone, from, true, null, mCountryCode ,false, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {//这个code是从ApiCode里面取，是result最外面的code，{"code":9999,"message":"netease server error","data":{"http_status_code":400,"code":33,"ms
                    Toast.makeText(RegisterActivity.this, R.string.authcode_get_success, Toast.LENGTH_SHORT).show();
                    AuthcodeActivity.start(RegisterActivity.this, from, phone, "", mCountryCode, String.valueOf(regionEntity.id));
                } else if (code == YunpianCode.CODE_AUTHCODE_HOUR_TOO_QUICK) {//这个code和ApiCode的code不一样，是result里面data的code，{"code":9999,"message":"netease server error","data":{"http_status_code":400,"code":33,"ms
                    //表示获取验证码频率过快code是ApiCode.CODE_NETEASE_ERROR=9999，data里面的code是YunpianCode.CODE_AUTHCODE_HOUR_TOO_QUICK=33
                    AuthcodeActivity.start(RegisterActivity.this, from, phone, "", mCountryCode, String.valueOf(regionEntity.id));
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