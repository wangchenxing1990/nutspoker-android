package com.htgames.nutspoker.ui.activity.Login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.PrepareDataHelper;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.MD5;
import com.htgames.nutspoker.ui.action.LoginAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import static com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon.TYPE_RIGHT_EYE;

/**
 */
public class SetPasswordActivity extends BaseActivity {
    String phone = "15158113991"; // UI测试，请忽略赋值
    String countryCode;
    private int from = LoginActivity.FROM_REGISTER;
    String regionId;
    String nickname;
    String validatecode;
    LoginAction mLoginAction;
    //相关view
    ClearableEditTextWithIcon setPasswordET;
    ClearableEditTextWithIcon setNickET;
    Button nextBtn;
    ClearableEditTextWithIcon edtInvitationCode;
    private AbortableFuture<LoginInfo> loginRequest;
    TextWatcher setPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            if (from == LoginActivity.FROM_REGISTER) {
                nextBtn.setEnabled((setPasswordET.getText().length() > 0 && setNickET.getText().length() > 0) ? true : false);
            } else if (from == LoginActivity.FROM_RESET) {
                nextBtn.setEnabled(setPasswordET.getText().length() > 0 ? true : false);
            }
        }
    };
    public static void start(Activity activity, int from, String phone, String regionId, String validatecode , String countryCode) {
        Intent intent = new Intent(activity, SetPasswordActivity.class);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_PHONE, phone);
        intent.putExtra(Extras.EXTRA_REGION, regionId);
        intent.putExtra(Extras.EXTRA_AUTHCODE, validatecode);
        intent.putExtra(Extras.EXTRA_COUNTRY_CODE, countryCode);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        mLoginAction = new LoginAction(this , null);
        initData();
        initHead();
        initView();
    }

    private void initData() {
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, LoginActivity.FROM_REGISTER);
        phone = getIntent().getStringExtra(Extras.EXTRA_PHONE);
        regionId = getIntent().getStringExtra(Extras.EXTRA_REGION);
        validatecode = getIntent().getStringExtra(Extras.EXTRA_AUTHCODE);
        countryCode = getIntent().getStringExtra(Extras.EXTRA_COUNTRY_CODE);
    }

    private void initHead() {
        TextView headTitle = (TextView) findViewById(R.id.tv_head_title);
        headTitle.setTextColor(Color.WHITE);
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        if (from == LoginActivity.FROM_REGISTER) {
            headTitle.setText(R.string.register);
        } else if (from == LoginActivity.FROM_RESET) {
            headTitle.setText(R.string.reset_password_head);
        } else if(from == LoginActivity.FROM_CHANGE_PASSWORD)
            headTitle.setText(R.string.change_pwd);
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText("< " + getResources().getString(R.string.back));
        btn_head_back.setCompoundDrawables(null, null, null, null);
        btn_head_back.setTextColor(Color.WHITE);
    }

    private void initView() {
        edtInvitationCode = (ClearableEditTextWithIcon) findViewById(R.id.edt_invitation_code);
        String language = BaseTools.getLanguage().toLowerCase();
        if(language.contains("tw") || language.contains("hk")) {
            //如果是台湾,香港
            edtInvitationCode.setVisibility(View.VISIBLE);
            findViewById(R.id.set_password_long_divider_three).setVisibility(View.VISIBLE);
        }
        setPasswordET = (ClearableEditTextWithIcon) findViewById(R.id.set_password_et);
        setPasswordET.addTextChangedListener(setPasswordTextWatcher);
        setPasswordET.setRightBtnType(TYPE_RIGHT_EYE);
        setNickET = (ClearableEditTextWithIcon) findViewById(R.id.set_nick_et);
        setNickET.addTextChangedListener(setPasswordTextWatcher);
        nextBtn = (Button) findViewById(R.id.btn_set_password_next_step);
        nextBtn.setEnabled(false);
        nextBtn.setText(from == LoginActivity.FROM_REGISTER ? R.string.register_finish : R.string.finish);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = setPasswordET.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), R.string.set_password_not_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.set_password_is_short, Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() > 32) {
                    Toast.makeText(getApplicationContext(), R.string.set_password_is_long, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (from == LoginActivity.FROM_REGISTER) {
                    nickname = setNickET.getText().toString().trim();
                    if (TextUtils.isEmpty(nickname)) {
                        Toast.makeText(getApplicationContext(), R.string.nickname_empty, Toast.LENGTH_SHORT).show();
                    } else {
//                        loginFailed();
                        //注册
                        register(phone, MD5.toMD5(password));
                    }
                } else if (from == LoginActivity.FROM_RESET) {
                    resetPassword(phone, MD5.toMD5(password));
                }
            }
        });
        if (from == LoginActivity.FROM_REGISTER) {
            //上面的代码默认已经设置好
        } else if (from == LoginActivity.FROM_RESET) {
            setPasswordET.setHint(R.string.set_password_head);
            setNickET.setVisibility(View.GONE);
            findViewById(R.id.set_password_long_divider_two).setVisibility(View.GONE);
            edtInvitationCode.setVisibility(View.GONE);
            findViewById(R.id.set_password_long_divider_three).setVisibility(View.GONE);
        }
    }

    private void register(final String phone, final String password) {
        String invitationCode = edtInvitationCode.getText().toString();
        mLoginAction.register(phone, password, nickname, regionId, validatecode, countryCode,
                invitationCode, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        LogUtil.i("zzh", "设置密码页面注册成功.register onResult()" + "  code:" + code + "   result:" + result + "   Throwable var3:" + (var3 == null ? "var3=null" : var3.toString()));
                        if (code == 0) {
                            try {
                                JSONObject json = new JSONObject(result);
                                JSONObject dataJsonObj = json.getJSONObject("data");
                                String token = dataJsonObj.getString("token");
                                String uid = dataJsonObj.getString("uid");
                                String uuid = dataJsonObj.optString("uuid");//虚拟ID，战鱼号
                                int coins = dataJsonObj.optInt("coins");
                                int diamond = dataJsonObj.optInt("diamond");
                                UserPreferences.getInstance(getApplicationContext()).setCoins(coins);
                                UserPreferences.getInstance(getApplicationContext()).setDiamond(diamond);
                                UserPreferences.getInstance(getApplicationContext()).setZYId(uuid);
                                Toast.makeText(getApplicationContext(), getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                registerSuccess(uid, token);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == ApiCode.CODE_NICKNAME_EXISTED || code == ApiCode.CODE_UPDATE_HORDE_NAME_EXISTED || code == ApiCode.CODE_MODIFY_CLUB_NAME_ALREADY_EXISTED) {
                            showNicknameExistedDialog();//昵称已经被占用
                        } else {
                            LogUtil.i("zzh", "设置密码页面注册失败.register onResult() code:" + code);
                            Toast.makeText(getApplicationContext(), getString(R.string.register_failed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailed() {
                        LogUtil.i("zzh", "设置密码页面注册失败.register onFailed()");
                    }
                });
    }

    EasyAlertDialog nicknameExistedDialog;
    private void showNicknameExistedDialog() {
        if (nicknameExistedDialog == null) {
            nicknameExistedDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    getResources().getString(R.string.nickname_existed), getString(R.string.ok), true,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            nicknameExistedDialog.show();
        }
    }

    public void registerSuccess(String uid , String token) {
        loginByNetease(uid , token);
    }

    public void loginByNetease(final String uid , final String token) {
        if (!NetworkUtil.isNetAvailable(this)) {
            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            loginFailed();
            return;
        }
        com.netease.nim.uikit.common.ui.dialog.DialogMaker.showProgressDialog(this, null, getString(R.string.login_ing), false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (loginRequest != null) {
                    loginRequest.abort();
                    onLoginDone();
                }
            }
        }).setCanceledOnTouchOutside(false);
        LoginInfo info = new LoginInfo(uid, token); // config...
        com.netease.nimlib.sdk.RequestCallback<LoginInfo> callback =
                new com.netease.nimlib.sdk.RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        LogUtil.i("zzh", "设置密码页面注册完登录onSuccess LoginInfo loginInfo = " + (loginInfo == null ? "null" : loginInfo.toString()));
                        onLoginDone();
                        DemoCache.setAccount(uid);
                        saveLoginInfo(phone, token, uid);
                        //个推，友盟绑定用户UID
                        UmengAnalytics.onProfileSignIn(uid);
                        GeTuiHelper.bindAlias(getApplicationContext() , uid);
                        // 初始化消息提醒
//                        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
                        // 初始化免打扰
//                        NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
                        // 准备数据
                        PrepareDataHelper.prepare();
                        //跳转会登录界面执行相关逻辑  // TODO: 16/12/22 不返回到登录界面  直接进入主界面
//                        Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra(Extras.EXTRA_FROM, LoginActivity.FROM_REGISTER);
//                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
//                        MainActivity.start(SetPasswordActivity.this, false, true);
                        startMainPage();
                        finish();
                    }
                    @Override
                    public void onFailed(int code) {
                        LogUtil.i("zzh", "设置密码页面注册完登录onFailed code = " + code);
                        onLoginDone();
                        if (code == 302 || code == 404) {
                            Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + code, Toast.LENGTH_SHORT).show();
                        }
                        loginFailed();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        loginFailed();
                    }
                    // overwrite methods
                };
        loginRequest = NIMClient.getService(AuthService.class).login(info);
        loginRequest.setCallback(callback);
    }

    private void loginFailed() {
        //跳转会登录界面执行相关逻辑
        Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void onLoginDone() {
        loginRequest = null;
        com.netease.nim.uikit.common.ui.dialog.DialogMaker.dismissProgressDialog();
        RequestTimeLimit.resetGetTime();
    }

    public void resetPassword(final String phone, final String password) {
        mLoginAction.resetPassword(phone, password, validatecode , countryCode ,new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    resetPasswordSuccess();
                } else {
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void resetPasswordSuccess() {
        if(from == LoginActivity.FROM_CHANGE_PASSWORD)//需要先执行登出操作...
        {
            //清除Token
            UserPreferences.getInstance(getApplicationContext()).setUserToken("");
            //调用云信登出
            NIMClient.getService(AuthService.class).logout();
        }

        Intent intent = new Intent(this, LoginActivity.class);
        //重新启动一个Activity，并且清除之前的Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Extras.EXTRA_FROM, LoginActivity.FROM_RESET);
        startActivity(intent);
        Toast.makeText(this, R.string.set_password_success, Toast.LENGTH_SHORT).show();
    }

    private void startMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.Extra_IsFromLogin, true);
        intent.putExtra(Extras.EXTRA_IS_IOSER, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(intent);
    }

    /**
     * 保存用户数据
     * @param phone
     * @param token
     */
    public void saveLoginInfo(final String phone, final String token, String uid) {
        UserPreferences.getInstance(getApplicationContext()).setUserPhone(phone);
        UserPreferences.getInstance(getApplicationContext()).setUserCountryCode(countryCode);
        UserPreferences.getInstance(getApplicationContext()).setUserToken(token);
        UserPreferences.getInstance(getApplicationContext()).setUserId(uid);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mLoginAction != null){
            mLoginAction.onDestroy();
            mLoginAction = null;
        }
    }
}
