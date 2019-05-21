package com.htgames.nutspoker.ui.activity.System;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.AppVersionEntity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.login.LogoutHelper;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.db.DBUtil;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.hotupdate.preference.HotUpdatePreferences;
import com.netease.nim.uikit.common.util.VersionTools;
import com.htgames.nutspoker.interfaces.CheckVersionListener;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.action.CheckVersionAction;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.msg.MsgService;

/**
 * Created by 20150726 on 2015/9/25.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    View settings_logout;
    SwitchButton switch_message_sound;//消息声音
    SwitchButton switch_message_shake;//消息震动
    SettingsPreferences mSettingsPreferences;
    RelativeLayout rl_message_sound;
    RelativeLayout rl_message_shake;
    ChessApp mChessApp;
    EasyAlertDialog logoutDialog;
    TextView tv_version_current_app;
    TextView tv_version_current_game;
    CheckVersionAction mCheckVersionAction;
    AppVersionEntity appVersionEntity;
    UserPreferences mUserPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mSettingsPreferences = SettingsPreferences.getInstance(getApplicationContext());
        mUserPreferences = UserPreferences.getInstance(getApplicationContext());
        mChessApp = (ChessApp) getApplication();
        setHeadTitle(R.string.me_column_settings);
        initView();
        switch_message_sound.setCheckedImmediately(mSettingsPreferences.isMessageSound());
        switch_message_shake.setCheckedImmediately(mSettingsPreferences.isMessageShake());
        updataNoticeView();
        initAction();
    }

    private void initAction() {
        mCheckVersionAction = new CheckVersionAction(this, null);
        mCheckVersionAction.setCheckVersionListener(new CheckVersionListener() {
            @Override
            public void onRedirect(boolean toActivity) {
            }
            @Override
            public void onCheckSuccess(AppVersionEntity version) {
                if (version != null) {
                    appVersionEntity = version;
                    String currentVersion = VersionTools.getAppVersion(getApplicationContext());
                    if (!appVersionEntity.isShow
                            || currentVersion.equals(appVersionEntity.newVersion)
                            || !mCheckVersionAction.isHaveNewVersion(currentVersion, appVersionEntity.newVersion)) {
                        //已经是最新版本(1.不显示版本 2.当前版本是最新的)
                    } else {
                        tv_version_current_app.setText(R.string.app_version_have_new);
                    }
                }
            }

            @Override
            public void onCheckError() {

            }

            @Override
            public void onCheckNotNew() {

            }
        });
        mCheckVersionAction.getVersionInfo(false, false, false);
    }

    public void updataNoticeView() {
        if (mSettingsPreferences.isMessageNotice()) {
            rl_message_sound.setVisibility(View.VISIBLE);
            rl_message_shake.setVisibility(View.VISIBLE);
        } else {
            rl_message_sound.setVisibility(View.GONE);
            rl_message_shake.setVisibility(View.GONE);
        }
    }

    private void initView() {
        handleLogoutBg();
        String appVersion = BaseTools.getAppVersionName(getApplicationContext());
        String gameVersion = HotUpdatePreferences.getInstance(getApplicationContext()).getGameVersion();
        switch_message_sound = (SwitchButton) findViewById(R.id.switch_message_sound);
        switch_message_shake = (SwitchButton) findViewById(R.id.switch_message_shake);
        rl_message_sound = (RelativeLayout) findViewById(R.id.rl_message_sound);
        rl_message_shake = (RelativeLayout) findViewById(R.id.rl_message_shake);
        tv_version_current_app = (TextView) findViewById(R.id.setting_current_app_version);
        tv_version_current_app.setText(appVersion);
        tv_version_current_game = (TextView) findViewById(R.id.setting_current_game_version);
        tv_version_current_game.setText(gameVersion);
        switch_message_sound.setOnCheckedChangeListener(this);
        switch_message_shake.setOnCheckedChangeListener(this);
    }

    private void handleLogoutBg() {
        settings_logout = findViewById(R.id.settings_logout);
        settings_logout.setOnClickListener(this);

        int normalColor = getResources().getColor(R.color.white);
        int pressedColor = getResources().getColor(R.color.list_item_bg_press);
        int rippleColor = getResources().getColor(R.color.list_item_bg_ripple);
        int[] colors = new int[] { normalColor, pressedColor, normalColor };
        int[][] states = new int[3][];
        states[0] = new int[] { -android.R.attr.state_pressed };
        states[1] = new int[] { android.R.attr.state_pressed };
        states[2] = new int[] {};

        int[] rippleColors = new int[] { rippleColor, rippleColor, rippleColor };
        int[][] rippleStates = new int[3][];
        rippleStates[0] = new int[] { android.R.attr.state_pressed };
        rippleStates[1] = new int[] { -android.R.attr.state_pressed };
        rippleStates[2] = new int[] {};
        ColorStateList rippleListColor = new ColorStateList(rippleStates, rippleColors);

        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setCornerRadius(ScreenUtil.dp2px(this, 5));
        normalDrawable.setColor(normalColor);

        GradientDrawable pressDrawable = new GradientDrawable();
        pressDrawable.setCornerRadius(ScreenUtil.dp2px(this, 5));
        pressDrawable.setColor(pressedColor);

        GradientDrawable rippleColorDrawable = new GradientDrawable();
        rippleColorDrawable.setCornerRadius(ScreenUtil.dp2px(this, 5));
        rippleColorDrawable.setColor(rippleColor);

        StateListDrawable listDrawable = new StateListDrawable();
        listDrawable.addState(new int[] { -android.R.attr.state_pressed }, normalDrawable);
        listDrawable.addState(new int[] { android.R.attr.state_pressed }, pressDrawable);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable rippleDrawable = new RippleDrawable(rippleListColor, listDrawable, rippleColorDrawable);
            settings_logout.setBackgroundDrawable(rippleDrawable);
        } else {
            settings_logout.setBackgroundDrawable(listDrawable);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_logout:
                showLogoutDialog();
                break;
        }
    }

    // 注销    这个方法不要随便改，彩蛋页通过反射调用了这个方法
    private void onLogout(int reason) {
        UserPreferences.getInstance(ChessApp.sAppContext).setUserToken("");//清除登陆状态    MainActiviy的onLogout(int)方法没有这两行
        NIMClient.getService(AuthService.class).logout();   //MainActiviy的onLogout(int)方法没有这两行
        //下面的和MainActiviy的onLogout(int)方法
        GeTuiHelper.unBindAliasUid(ChessApp.sAppContext);//解绑个推别名
        HandsCollectDBHelper.clearCollectHands(ChessApp.sAppContext);//清空手牌收藏
        UserPreferences.getInstance(ChessApp.sAppContext).setCollectHandNum(0);//清空手牌收藏数量
        UmengAnalytics.onProfileSignOff();
        //清除登录状态&缓存&注销监听
        LogoutHelper.logout();
        DBUtil.closeDBUtil();//关闭数据库，因为数据库以个人UI作为账户
        LogUtil.i("zzh", "setting activity onLogout");
    }

    /**
     * 清楚所有聊天记录
     */
    public void clearAllMsg() {
        NIMClient.getService(MsgService.class).clearMsgDatabase(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == switch_message_sound) {
            mSettingsPreferences.setMessageSound(isChecked);
            mChessApp.updateStatusBarNotificationConfig();
        } else if (buttonView == switch_message_shake) {
            mSettingsPreferences.setMessageShake(isChecked);
            mChessApp.updateStatusBarNotificationConfig();
        }
    }

    public void showLogoutDialog() {
        String message = getString(R.string.logout_tip);
        logoutDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                message, null, null, true, R.layout.dialog_easy_alert_new,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        onLogout(LoginActivity.REASON_NO);
                        // 启动登录
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Extras.EXTRA_REASON, LoginActivity.REASON_NO);
                        startActivity(intent);
                        finish();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            logoutDialog.show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCheckVersionAction != null) {
            mCheckVersionAction.onDestroy();
            mCheckVersionAction = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clickIndex = 0;
    }

    private long lastTime = System.currentTimeMillis();
    private int clickIndex = 0;
    Toast toastObject;
    private int totalClickCnt = 2;
    public void toDevelopAC(View view) {
        if (!CacheConstant.debugBuildType) {
            return;
        }
        long clickTime = System.currentTimeMillis();
        float timeInterval = (clickTime - lastTime) / 1000f;
        lastTime = clickTime;
        if (toastObject != null) {
            toastObject.cancel();
        }
        if (clickIndex == totalClickCnt) {
            toastObject = Toast.makeText(this, "开发者彩蛋开启中...", Toast.LENGTH_SHORT);
            toastObject.show();
            Nav.from(this).toUri(UrlConstants.DEVELOP);
            clickIndex = 100;//  防止一直点击
            return;
        }
        if (clickIndex > 0 && timeInterval >= 2f) {
            clickIndex = 0;
            return;
        } else if (clickIndex < totalClickCnt) {
            toastObject = Toast.makeText(this, "还差" + (totalClickCnt - clickIndex) + "步开启彩蛋...", Toast.LENGTH_SHORT);
            clickIndex++;
            toastObject.show();
            return;
        }
    }
}
