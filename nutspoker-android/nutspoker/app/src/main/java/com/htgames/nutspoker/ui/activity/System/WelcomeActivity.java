package com.htgames.nutspoker.ui.activity.System;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.github.jorgecastillo.FillableLoader;
import com.github.jorgecastillo.State;
import com.github.jorgecastillo.listener.OnStateChangeListener;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.push.GeTuiTools;
import com.netease.nim.uikit.anim.Paths;
import com.netease.nim.uikit.common.util.log.LogUtil;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.AppVersionEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.CheckVersionListener;
import com.htgames.nutspoker.ui.action.CheckVersionAction;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.util.SysInfoUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

public class WelcomeActivity extends BaseActivity implements OnStateChangeListener {
    private final static String TAG = "WelcomeActivity";
    private boolean isFrist = false;
    CheckVersionAction mCheckVersionAction;
    boolean fillableAnimFinish = false;
    boolean isAnimationFinish = false;
    boolean isCheckVersionFinish = false;
    int pendingIntentAction = 0;
    public static String shareGameCode = "";//点击分享链接打开app中包含的游戏code
    public static final String KEY_GAME_CODE_FROM_SHARE = "key_game_code_from_share";//点击分享链接打开app中包含的游戏code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GeTuiTools.init(ChessApp.sAppContext);//初始化个推数据  chessapp里面也调用过，都保留，不要删
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Uri data = getIntent().getData();
        if (data != null) {
            shareGameCode = data.getQueryParameter("code");
        }
        setSwipeBackEnable(false);
        initAction();
        circular_reveal_view_id = findViewById(R.id.circular_reveal_view_id);
        startCircularRevealAnimation();
        fillableLoader = (FillableLoader) findViewById(R.id.fillableLoader);
        fillableLoader.setSvgPath(Paths.logoPath);
        fillableLoader.setOnStateChangeListener(this);
        fillableLoader.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fillableLoader != null) {
                    fillableLoader.start();
                }
            }
        }, 100);
        pendingIntentAction = getIntent().getIntExtra(Extras.EXTRA_PENDINGINTENT_ACIONT, 0);
    }

    FillableLoader fillableLoader;
    View circular_reveal_view_id;
    Animator animator;
    private void startCircularRevealAnimation() {
        if(circular_reveal_view_id == null) {
            return;
        }
        circular_reveal_view_id.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                boolean isAttachedToWindow = ViewCompat.isAttachedToWindow(circular_reveal_view_id);
                LogUtil.i(TAG, "isAttachedToWindow: " + isAttachedToWindow);
                if (!isAttachedToWindow) {
                    return;
                }
                circular_reveal_view_id.removeOnLayoutChangeListener(this);
                circular_reveal_view_id.post(new Runnable() {
                    @Override
                    public void run() {
                        //因为CircularReveal动画是api21之后才有的,所以加个判断语句,兼容低版本用渐变
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (circular_reveal_view_id == null) {
                                return;
                            }
                            int cicular_R = (int) (ScreenUtil.screenHeight > ScreenUtil.screenWidth ? ScreenUtil.screenHeight * 0.7 : ScreenUtil.screenWidth * 0.7);
                            int offsetY = ScreenUtil.dp2px(WelcomeActivity.this, 80);
                            int startY = ScreenUtil.dp2px(WelcomeActivity.this, 190);
                            animator = ViewAnimationUtils.createCircularReveal(circular_reveal_view_id, ScreenUtil.screenWidth / 2, startY/*ScreenUtil.screenHeight / 2 - offsetY*/, 0, cicular_R);
                            animator.setDuration(750);
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());
                            animator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    if (circular_reveal_view_id != null) {
                                        circular_reveal_view_id.setVisibility(View.VISIBLE);
                                    }
                                }
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isAnimationFinish = true;
                                    redirectTo();//高级动画结束
                                    LogUtil.i("启动速度", "高级动画结束");
                                }
                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }
                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            });
                            animator.start();
                        } else {
                            if (circular_reveal_view_id == null) {
                                return;
                            }
                            long time = 500;
                            circular_reveal_view_id.setAlpha(0);
                            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                            alphaAnimation.setDuration(time);//持续时间
                            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if (circular_reveal_view_id != null) {
                                        circular_reveal_view_id.setAlpha(1);
                                    }
                                    isAnimationFinish = true;
                                    redirectTo();//alpha动画结束
                                    LogUtil.i("启动速度", "alpha动画结束");
                                }
                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            circular_reveal_view_id.startAnimation(alphaAnimation);
                        }
                    }
                });
            }
        });
    }

    public void initAction() {
        mCheckVersionAction = new CheckVersionAction(this, null);
        mCheckVersionAction.setCheckVersionListener(new CheckVersionListener() {
            @Override
            public void onRedirect(boolean toActivity) {
                if (toActivity) {
                    isCheckVersionFinish = true;
                    redirectTo();//检测更新逻辑结束
                    LogUtil.i("启动速度", "检测更新逻辑结束");
                }
                LogUtil.i(TAG, "onRedirect:" + System.currentTimeMillis() / 1000);
            }

            @Override
            public void onCheckSuccess(AppVersionEntity appVersionEntity) {
                LogUtil.i(TAG, "onCheckSuccess:" + System.currentTimeMillis() / 1000);
            }

            @Override
            public void onCheckError() {
//                showVersionErrorDialog();
                isCheckVersionFinish = true;
                redirectTo();//检测出错
                LogUtil.i("启动速度", "检测出错");
                LogUtil.i(TAG, "onCheckError:" + System.currentTimeMillis() / 1000);
            }

            @Override
            public void onCheckNotNew() {
                LogUtil.i(TAG, "onCheckNotNew:" + System.currentTimeMillis() / 1000);
            }
        });
        mCheckVersionAction.getVersionInfo(true, false, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        onIntent();
    }

    /**
     * 已经登陆过，自动登陆
     *
     * @return
     */
    private boolean canAutoLogin() {
        String account = UserPreferences.getInstance(getApplicationContext()).getUserId();
        String token = UserPreferences.getInstance(getApplicationContext()).getUserToken();
        String uuid = UserPreferences.getInstance(getApplicationContext()).getZYId();
        LogUtil.i(TAG, "get local sdk token =" + token);
        boolean isCan = !TextUtils.isEmpty(account) && !TextUtils.isEmpty(token);
        if(isCan && uuid.equals("0")) {
            isCan = false;
            Toast.makeText(this,R.string.please_relogin,Toast.LENGTH_LONG).show();
        }
        return isCan;
    }

    private void parseNotifyIntent(Intent intent) {
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() > 1) {
//            showMainActivity(null);
        } else {
//            showMainActivity(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
        }
        startMainPageActivity();
    }

    public void onIntent() {
        if (TextUtils.isEmpty(DemoCache.getAccount())) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        } else {
            // 已经登录过了，处理过来的请求
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                    parseNotifyIntent(intent);
                    return;
                }
//                else if (intent.hasExtra(Extras.EXTRA_JUMP_P2P) || intent.hasExtra(AVChatActivity.INTENT_ACTION_AVCHAT)) {
//                    parseNormalIntent(intent);
//                }
            }
            if (!isFrist && intent == null) {
                finish();
            } else {
                startMainPageActivity();
            }
        }
    }

    private void startMainPageActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_GAME_CODE_FROM_SHARE, shareGameCode);//把分享的code传给首页，直接进入游戏
        intent.putExtra(Extras.EXTRA_PENDINGINTENT_ACIONT, pendingIntentAction);//把分享的code传给首页，直接进入游戏
        startActivity(intent);
        //延迟200毫秒结束
        finish();
    }

    public synchronized void redirectTo() {
        if (fillableAnimFinish && isAnimationFinish && isCheckVersionFinish && !this.isFinishing()) {
            if (canAutoLogin()) {
                //自动登录
                onIntent();
            } else {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                //延迟200毫秒结束
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFrist = false;
//        sHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isAnimationFinish = true;
//                redirectTo();
//            }
//        }, 1000);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        if (animator != null) {
            if (animator.isRunning()) {
                animator.cancel();
                animator.removeAllListeners();
            }
            animator = null;
        }
        if (circular_reveal_view_id != null) {
            circular_reveal_view_id.clearAnimation();
            circular_reveal_view_id = null;
        }
        if (mCheckVersionAction != null) {
            mCheckVersionAction.onDestroy();
            mCheckVersionAction = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    public void onStateChange(int state) {
        LogUtil.i("fillableAnimFinish", "fillableAnimFinish state: " + state);
        if (state == State.FINISHED) {
            fillableAnimFinish = true;
            redirectTo();//fillable动画结束
            LogUtil.i("启动速度", "fillable动画结束");
        }
    }
}
