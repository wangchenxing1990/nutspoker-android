package com.htgames.nutspoker.ui.activity.Login;

import android.os.Bundle;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.AuthcodeAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePwd1Activity extends BaseActivity {

    UserPreferences mUserPreferences;
    AuthcodeAction mAuthcodeAction;
    String mContryCode = "";
    String mPhoneNumber = "";

    @BindView(R.id.tv_phone_number)
    TextView mUiPhone;
    @OnClick(R.id.btn_next) void clickConfirm(){
        getAuthcode(mPhoneNumber);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd1);
        mUnbinder = ButterKnife.bind(this);

        mUserPreferences = UserPreferences.getInstance(getApplicationContext());

        mContryCode = mUserPreferences.getUserCountryCode();
        mPhoneNumber = mUserPreferences.getUserPhone();

        setHeadTitle(R.string.change_pwd);
        mUiPhone.setText(this.getText(R.string.phone)+": "+mPhoneNumber);
    }

    /**
     * 获取验证码
     */
    public void getAuthcode(final String phone) {
        if (mAuthcodeAction == null)
            mAuthcodeAction = new AuthcodeAction(this, null);

        mAuthcodeAction.getAuthCode(phone, LoginActivity.FROM_CHANGE_PASSWORD, true, null, mContryCode, false, new RequestCallback() {

            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    Toast.makeText(ChangePwd1Activity.this, R.string.authcode_get_success, Toast.LENGTH_SHORT).show();
                    AuthcodeActivity.start(ChangePwd1Activity.this, LoginActivity.FROM_CHANGE_PASSWORD , mPhoneNumber, "", mContryCode, "");
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
}
