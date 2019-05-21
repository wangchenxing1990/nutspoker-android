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
import android.widget.ImageView;
import android.widget.TextView;

import com.github.jorgecastillo.FillableLoader;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.PrepareDataHelper;
import com.htgames.nutspoker.chat.region.activity.CountrySelectActivity;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.ui.action.LoginAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.FriendGuideActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.anim.Paths;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.CountryEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.MD5;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.CountryCodeConstants;
import com.netease.nim.uikit.constants.CountryCodeHelper;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Login
 */
public class LoginActivity extends BaseActivity {
    public static final int FROM_REGISTER = 1;
    public static final int FROM_RESET = 2;
    public static final int FROM_CHANGE_PASSWORD = 3;
    public static final int REASON_NO = 0;
    public static final int REASON_KICKOUT = 1;//原因：帐号在其他地方登录
    public static final int REASON_PWD_ERROR = 2;//原因：密码错误
    public static final int REASON_FORBIDDEN = 3;//原因：被禁用
    public static final int REQUESTCODE_COUNTRY_SELECT = 200;//国家选择
    //    private ClearableEditTextWithIcon edt_login_phone;
    //private ClearableEditTextWithIcon edt_login_password;
    //private Button btn_login;
    //private Button btn_register;
    //private TextView tv_login_forget_password;
    @BindView(R.id.login_username_et) EditText mUserNameET;
    @BindView(R.id.login_password_et) EditText mPasswordET;
    @BindView(R.id.np_login_phone_code_tv ) TextView countryCodeTextView;
    public static String currentCountryCode;
    public static Map<String, String> countryCodeMap = new HashMap<String, String>();
    private AbortableFuture<LoginInfo> loginRequest;
    LoginAction mLoginAction;
    int intentReason = 0;
    TextView tv_login_facebook;
    //
//    CallbackManager callbackManager;
//    LoginManager mLoginManager;
    @BindView(R.id.btn_login) Button btn_login;

    void onTextAfterChanged(){
        if (mUserNameET.getText().toString().length() > 0 && mPasswordET.getText().length() > 0) {
            btn_login.setEnabled(true);
            btn_login.setTextColor(Color.WHITE);
        } else {
            btn_login.setEnabled(false);
            btn_login.setTextColor(Color.parseColor("#9a9a9a"));
        }
    }

    //这个接口只能用于 继承于TextEdit
    @OnTextChanged(value = R.id.login_password_et,callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED) void onPhoneAfterChanged(){
        onTextAfterChanged();
    }

    //点击登陆
    @OnClick(R.id.btn_login) void clickLogin(){
        String phone = mUserNameET.getText().toString().toLowerCase();
        String password = mPasswordET.getText().toString();
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), getString(R.string.login_not_null), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!BaseTools.isMobileNO(currentCountryCode, phone)) {
            //手机号码无效
            Toast.makeText(getApplicationContext(), getString(R.string.login_phone_invalid), Toast.LENGTH_SHORT).show();
            return;
        }
//      login( phone, password);
        doLoginByHost(currentCountryCode, phone, MD5.toMD5(password));
    }
    //点击注册
    @OnClick(R.id.btn_register) void clickRegister(){
        String edtPhone = mUserNameET.getText().toString().toLowerCase();
        RegisterActivity.startByRegister(this, edtPhone, currentCountryCode);
    }
    //点击忘记密码
    @OnClick(R.id.tv_login_forget_password) void clickForgetPassword() {
        String edtForPhone = mUserNameET.getText().toString().toLowerCase();
        RegisterActivity.startByReset(this, edtForPhone, currentCountryCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final FillableLoader fillableLoader = (FillableLoader) findViewById(R.id.fillableLoader);
        fillableLoader.setSvgPath(Paths.logoPath);
        fillableLoader.start();
//        fillableLoader.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (fillableLoader != null) {
//                    fillableLoader.start();
//                }
//            }
//        }, 200);
        mUnbinder = ButterKnife.bind(this);//绑定对应的View
        currentCountryCode = CountryCodeHelper.getCurrentLocalCountryCode();
        countryCodeMap = CountryCodeConstants.getCountryCodeMap(this);
        setSwipeBackEnable(false);
        intentReason = getIntent().getIntExtra(Extras.EXTRA_REASON, REASON_NO);
        initView();
        initNIMClient();
        mLoginAction = new LoginAction(this, null);
        if (intentReason != REASON_NO) {
            showPromptDiolag(intentReason);
        }

        if(ApiConfig.AppVersion.isTaiwanVersion) {
//            FacebookSdk.sdkInitialize(getApplicationContext());
//            initFacebook();
        }
    }

    private void initFacebook() {
//        tv_login_facebook = (TextView) findViewById(R.id.tv_login_facebook);
//        callbackManager = CallbackManager.Factory.create();
//        mLoginManager = LoginManager.getInstance();
//        mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                String fbUserId = loginResult.getAccessToken().getUserId();
//                String fbToken = loginResult.getAccessToken().getToken();
//                doLoginByFacebook(fbToken, fbUserId);
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
//        tv_login_facebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mLoginManager.logOut();
//                mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
//            }
//        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int from = intent.getIntExtra(Extras.EXTRA_FROM, REASON_NO);
            if (from == FROM_REGISTER) {
                Intent intentMain = new Intent(LoginActivity.this, FriendGuideActivity.class);
                startActivity(intentMain);
                finish();
            }
        }
    }

    private void initView() {
        btn_login = (Button) findViewById(R.id.btn_login);
        mUserNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                onTextAfterChanged();
            }
        });
        mUserNameET.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(UserConstant.MAX_PHONE_NUM_LENGTH)});
        btn_login.setEnabled(false);
        countryCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountrySelectActivity.start(LoginActivity.this, REQUESTCODE_COUNTRY_SELECT);
            }
        });
    }

    private void initNIMClient() {
        String lastPhone = UserPreferences.getInstance(getApplicationContext()).getUserPhone();
        String lastCountryCode = UserPreferences.getInstance(getApplicationContext()).getUserCountryCode();
        if (!TextUtils.isEmpty(lastPhone)) {
            mUserNameET.setText(lastPhone);
        }
        if (!TextUtils.isEmpty(lastCountryCode)) {
            countryCodeTextView.setText("手机号 +" + lastCountryCode);
        }
        //监听用户状态发生改变
//        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
//            public void onEvent(StatusCode status) {
//                LogUtil.i(TAG, "User status changed to: " + status);
//            }
//        }, true);
    }

    public void loginByNetease(final String uid, final String token, final boolean isIOSRegister) {
        if (!NetworkUtil.isNetAvailable(LoginActivity.this)) {
            Toast.makeText(this, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            onLoginDone();
            return;
        }

        LoginInfo info = new LoginInfo(uid, token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
//                        Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                        DemoCache.setAccount(uid);
                        saveLoginInfo(uid, token);
                        UmengAnalytics.onProfileSignIn(uid);
                        GeTuiHelper.bindAlias(getApplicationContext(), uid);
                        // 初始化消息提醒
//                        NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
                        // 初始化免打扰
//                        NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());
                        // 准备数据
                        PrepareDataHelper.prepare();
                        onLoginDone();
                        loginIntent(isIOSRegister);
                    }

                    @Override
                    public void onFailed(int code) {
                        LogUtil.e(LoginAction.TAG, "云信登陆失败 code = " + code);
                        onLoginDone();
                        if (code == 302 || code == 404) {
                            Toast.makeText(getApplicationContext(), R.string.login_failed_error, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        LogUtil.e(LoginAction.TAG, "云信登陆异常 msg = " + throwable.getLocalizedMessage());
                        onLoginDone();
                        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                    // overwrite methods
                };
        loginRequest = NIMClient.getService(AuthService.class).login(info);
        loginRequest.setCallback(callback);
    }

    public void loginIntent(boolean isIOSRegister) {
        MainActivity.start(LoginActivity.this, isIOSRegister,true);
        finish();
    }

    /**
     * 通过登录自己服务器
     *
     * @param phone
     * @param password
     */
    public void doLoginByHost(String countryCode, final String phone, final String password) {
        mLoginAction.doLoginByHost(countryCode, phone, password,
                new com.htgames.nutspoker.interfaces.RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            loginByNetSuccess(result);
                        } else
                            LogUtil.e(LoginAction.TAG, "自己服务器登陆结果异常 code = " + code);
                    }

                    @Override
                    public void onFailed() {
                        LogUtil.e(LoginAction.TAG, "自己服务器登陆结果异常");
                    }
                });
    }

    public void loginByNetSuccess(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONObject dataJsonObj = json.getJSONObject("data");
            String uid = dataJsonObj.optString("uid");
            String uuid = dataJsonObj.optString("uuid");//虚拟ID，战鱼号
            String token = dataJsonObj.getString("token");
            int level = dataJsonObj.optInt("level");
            int diamond = dataJsonObj.optInt("diamond");
            int coins = dataJsonObj.optInt("coins");
            int is_regist = dataJsonObj.optInt("is_regist");//是否是IOS用户，迁移到android
            boolean isIOSRegister = (is_regist == ApiConstants.IS_IOS_REGISTER ? true : false);
            LogUtil.i(LoginAction.TAG, "登录APP服务器成功，获取token:" + token + "  是否是IOS用户：" + isIOSRegister);
            //保存数据到本地
            UserPreferences.getInstance(getApplicationContext()).setCoins(coins);
            UserPreferences.getInstance(getApplicationContext()).setDiamond(diamond);
            UserPreferences.getInstance(getApplicationContext()).setZYId(uuid);
            UserPreferences.getInstance(getApplicationContext()).setLevel(level);
            loginByNetease(uid, token, isIOSRegister);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onLoginDone() {
        loginRequest = null;
        DialogMaker.dismissProgressDialog();
        RequestTimeLimit.resetGetTime();
    }

    /**
     * 保存用户数据
     *
     * @param uid
     * @param token
     */
    public void saveLoginInfo(final String uid, final String token) {
        UserPreferences.getInstance(getApplicationContext()).setUserId(uid);
        UserPreferences.getInstance(getApplicationContext()).setUserPhone(mUserNameET.getText().toString());
        UserPreferences.getInstance(getApplicationContext()).setUserToken(token);
        UserPreferences.getInstance(getApplicationContext()).setUserCountryCode(currentCountryCode);
    }

    EasyAlertDialog promptDiolag;

    public void showPromptDiolag(int reason) {
        String message = getString(R.string.login_failed);
        if (reason == REASON_KICKOUT) {
            message = getString(R.string.login_in_otherip);
        } else if(reason == REASON_FORBIDDEN) {
            message = getString(R.string.login_in_forbidden);
        }
        promptDiolag = EasyAlertDialogHelper.createOneButtonDiolag(this, null,
                message, getString(R.string.ok), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
        promptDiolag.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_COUNTRY_SELECT && resultCode == Activity.RESULT_OK && data != null) {
            CountryEntity countryEntity = (CountryEntity) data.getSerializableExtra(Extras.EXTRA_DATA);
            countryCodeTextView.setText("手机号 +" + (countryEntity != null ? countryEntity.countryCode : "86"));
            currentCountryCode = countryEntity == null ? currentCountryCode : countryEntity.countryCode;
        }
//        if (callbackManager != null) {
//            callbackManager.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    protected void onDestroy() {
//        callbackManager = null;
//        mLoginManager = null;
        if (mLoginAction != null) {
            mLoginAction.onDestroy();
            mLoginAction = null;
        }
        super.onDestroy();
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }


    public static void SetPasswordHeadView(BaseActivity activity,int from){
        if (from == LoginActivity.FROM_REGISTER)
            activity.setHeadTitle(R.string.register);
        else if(from == LoginActivity.FROM_RESET)
            activity.setHeadTitle(R.string.reset_password_head);
        else if(from == LoginActivity.FROM_CHANGE_PASSWORD)
            activity.setHeadTitle(R.string.change_pwd);
        else
            activity.setHeadTitle(R.string.password);//默认密码
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChessApp.hideDebugView();
    }
}