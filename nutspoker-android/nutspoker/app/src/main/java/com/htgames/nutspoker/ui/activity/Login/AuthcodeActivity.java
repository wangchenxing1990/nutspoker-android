package com.htgames.nutspoker.ui.activity.Login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.data.common.AuthCodeConstants;
import com.netease.nim.uikit.interfaces.OnPasswordChangedListener;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.AuthcodeAction;
import com.htgames.nutspoker.view.AuthcodeView;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 验证码
 */
public class AuthcodeActivity extends BaseActivity implements View.OnClickListener{
    public static final String TAG = "AuthcodeActivity";
    private int mPasswordLength = 4;//验证码默认长度4
    String phone;
    String countryCode;
    @BindView(R.id.btn_authcode_next_step_btn) Button btn_authcode_next_step_btn;
    @BindView(R.id.already_send_authcode_tv) TextView tv_already_phone;
    @BindView(R.id.authcode_countdowntime_again_send) TextView tv_authcode_time;
    @BindView(R.id.authcode_notget_voice_tv) TextView ll_authcode_voice;//没收到验证码？
    @BindView(R.id.auth_code_container)
    AuthcodeView mAuthcodeView;
    private int from = LoginActivity.FROM_REGISTER;
    private String regionId;
    AuthcodeAction mAuthcodeAction;
    EasyAlertDialog backDialog;
    EasyAlertDialog authcodeDialog;
    EasyAlertDialog voiceDialog;

    public static void start(Activity activity, int from, String phone, String authcode, String countryCode ,String regionId) {
        Intent intent = new Intent(activity, AuthcodeActivity.class);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_PHONE, phone);
        intent.putExtra(Extras.EXTRA_AUTHCODE, authcode);
        intent.putExtra(Extras.EXTRA_COUNTRY_CODE, countryCode);
        intent.putExtra(Extras.EXTRA_REGION, regionId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authcode);
        mUnbinder = ButterKnife.bind(this);//绑定对应的View
        initData();
        initHead();
        initView();
        initAuthcodeView();
        startCountDownTimer();
        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(true);
            }
        }, 300L);
        btn_authcode_next_step_btn.setEnabled(false);
        btn_authcode_next_step_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mValidateCode)) {
                    checkAuthCode(mValidateCode);
                }
            }
        });
    }

    private String mValidateCode;
    private void initAuthcodeView() {
        mAuthcodeView.setPasswordLength(mPasswordLength);
        mAuthcodeView.setPasswordVisibility(true);
        mAuthcodeView.setOnPasswordChangedListener(new OnPasswordChangedListener() {
            @Override
            public void onChanged(String psw) {
                if (psw.length() < mPasswordLength) {
                    btn_authcode_next_step_btn.setEnabled(false);
                } else if (psw.length() == mPasswordLength) {
                    btn_authcode_next_step_btn.setEnabled(true);
                    mValidateCode = psw;
                }
            }
            @Override
            public void onMaxLength(String psw) {
//                checkAuthCode(psw);
                btn_authcode_next_step_btn.setEnabled(true);
                mValidateCode = psw;
            }
        });
    }

    private void initData() {
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, LoginActivity.FROM_REGISTER);
        phone = getIntent().getStringExtra(Extras.EXTRA_PHONE);
        countryCode = getIntent().getStringExtra(Extras.EXTRA_COUNTRY_CODE);
        regionId = getIntent().getStringExtra(Extras.EXTRA_REGION);
    }

    private void initView() {
        tv_already_phone.setText(getResources().getString(R.string.authcode_already_send, "+" +countryCode + " " + phone));
        ll_authcode_voice.setOnClickListener(this);
        tv_authcode_time.setOnClickListener(this);
        tv_authcode_time.setText(String.format(getString(R.string.authcode_time_countdown), 60));
    }

    private void initHead() {
        TextView headTitle = (TextView) findViewById(R.id.tv_head_title);
        headTitle.setTextColor(Color.WHITE);
        if (from == LoginActivity.FROM_REGISTER) {
            headTitle.setText(R.string.register);
        } else {
            headTitle.setText(R.string.reset_password_head);
        }
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText(getResources().getString(R.string.back));
        btn_head_back.setTextColor(Color.WHITE);
    }

    /**
     * 获取验证码
     */
    public void getAuthCode(final boolean isVoiceMode , final boolean showDiloag) {
        if (mAuthcodeAction == null) {
            mAuthcodeAction = new AuthcodeAction(this, null);
        }
        mAuthcodeAction.getAuthCode(phone, from, showDiloag, null, countryCode ,isVoiceMode , new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0) {
                    if (!isVoiceMode) {
                        startCountDownTimer();
                        Toast.makeText(AuthcodeActivity.this, R.string.authcode_get_success, Toast.LENGTH_SHORT).show();
                    } else {
                        showVoiceSendedDialog();
                    }
                }
            }
            @Override
            public void onFailed() {
            }
        });
    }

    /**
     * 验证验证码
     * @param validatecode
     */
    public void checkAuthCode(String validatecode) {
        if (mAuthcodeAction == null) {
            mAuthcodeAction = new AuthcodeAction(this, null);
        }
        mAuthcodeAction.getAuthCode(phone, from, true, validatecode, countryCode , false , new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0){
                    SetPasswordActivity.start(AuthcodeActivity.this, from, phone, regionId, mAuthcodeView.getPassWord() , countryCode);
                } else if(code == ApiCode.CODE_AUTHCODE_ERROR){
                    int errorNum = AuthCodeConstants.getErrorNum(result);
                    int remainNum = AuthCodeConstants.ERROR_COUNT_LIMIT - errorNum;
                    if(remainNum > 0){
                        Toast.makeText(getApplicationContext() , getString(R.string.authcode_error_try , remainNum) ,Toast.LENGTH_SHORT).show();
                    } else{
                        showRegetDialog();
                    }
                }else if(code == ApiCode.CODE_ILLEGAL_REQUEST) {
                    mAuthcodeView.clearPassword();
                    showRegetDialog();
                }else{
                    Toast.makeText(getApplicationContext() , R.string.network_exception_please_try ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), R.string.authcode_not_right, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showRegetDialog() {
        String message = getString(R.string.authcode_error_limit);
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                message, getString(R.string.reget) , getString(R.string.cancel) , true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        startCountDownTimer();
                        getAuthCode(false , true);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    private final static int COUNTDOWN_INTERVAL = 60000;
    private final static int MILLISFUTURE = 1000;

    CountDownTimer mCountDownTimer;

    public void startCountDownTimer() {
        //如果时间大于60秒，可以重新获取
        tv_authcode_time.setEnabled(false);
        tv_authcode_time.setTextColor(getResources().getColor(R.color.record_detail_column_bg));
        if(mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(COUNTDOWN_INTERVAL, MILLISFUTURE) {
                public void onTick(long millisUntilFinished) {
                    tv_authcode_time.setText(String.format(getString(R.string.authcode_time_countdown), millisUntilFinished / 1000));
                }
                public void onFinish() {
                    tv_authcode_time.setText(R.string.authcode_receive_not);
                    tv_authcode_time.setTextColor(getResources().getColor(R.color.login_solid_color));
                    tv_authcode_time.setEnabled(true);
                }
            };
        }
        mCountDownTimer.cancel();
        mCountDownTimer.start();
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }

    public void showAuthCodeDialog(final boolean isVioce) {
        String message = getString(R.string.authcode_reget_tip);
        if (isVioce) {
            message = getString(R.string.authcode_voice_confim);
        }
        authcodeDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        getAuthCode(isVioce , true);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            authcodeDialog.show();
        }
    }

    public void showVoiceSendedDialog() {
        String message = getString(R.string.authcode_voice_tip);
        voiceDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                message, getString(R.string.ok), true,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            voiceDialog.show();
        }
    }

    public void showBackDialog() {
        if (backDialog == null) {
            backDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.authcode_back_tip), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            finish();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            backDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        if (mAuthcodeAction != null) {
            mAuthcodeAction.onDestroy();
            mAuthcodeAction = null;
        }
        authcodeDialog = null;
        backDialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.authcode_countdowntime_again_send:
                showAuthCodeDialog(false);//重新获取验证码, 参数为false表示不用语音验证码
                break;
            case R.id.authcode_notget_voice_tv:
                showAuthCodeDialog(true);//没收到验证码？获取语音验证码
                break;
        }
    }
}
