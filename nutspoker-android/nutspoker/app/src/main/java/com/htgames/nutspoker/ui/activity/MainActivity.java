package com.htgames.nutspoker.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crl.zzh.customrefreshlayout.ControlToast;
import com.github.jorgecastillo.FillableLoader;
import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.MessageDataCache;
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgControlAC;
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgInfoAC;
import com.htgames.nutspoker.chat.app_msg.helper.HordeMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.contact.activity.EditUserInfoActivity;
import com.htgames.nutspoker.chat.contact.activity.EditUserItemActivity;
import com.htgames.nutspoker.chat.helper.MessageHelper;
import com.htgames.nutspoker.chat.helper.MessageTipHelper;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.htgames.nutspoker.chat.notification.CustomNotificationCache;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.htgames.nutspoker.chat.notification.helper.CustomNotificationHelper;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.chat.session.activity.TeamMessageAC;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.htgames.nutspoker.chat.session.extension.GuessAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.data.cache.AudioCacheManager;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.game.helper.GameConfigHelper;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.netease.nim.uikit.PendingIntentConstants;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.push.GeTuiTools;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.AssetsDatabaseManager;
import com.htgames.nutspoker.tool.shop.ShopDataCache;
import com.htgames.nutspoker.ui.action.AmountAction;
import com.htgames.nutspoker.ui.action.CheckVersionAction;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.GameConfigAction;
import com.htgames.nutspoker.ui.action.HandCollectAction;
import com.htgames.nutspoker.ui.action.ShopAction;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.activity.System.WelcomeActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.fragment.main.DiscoveryFragment;
import com.htgames.nutspoker.ui.fragment.main.GameFragmentNew;
import com.htgames.nutspoker.ui.fragment.main.MeFragment;
import com.htgames.nutspoker.ui.fragment.main.RecentContactsFragment;
import com.htgames.nutspoker.ui.fragment.main.RecordFragment;
import com.htgames.nutspoker.util.PrintUtil;
import com.htgames.nutspoker.view.JellyView;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.view.systemannouncement.SystemWindowManager;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.LoginSyncDataStatusObserver;
import com.netease.nim.uikit.anim.Paths;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.NetCardCountEy;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.chesscircle.helper.MessageFilter;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.chesscircle.view.AudioConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.preference.CheckVersionPref;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.dialog.ProtocolDialog;
import com.netease.nim.uikit.common.util.MD5;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.customview.GridPwdView;
import com.netease.nim.uikit.customview.HomeTabItem;
import com.netease.nim.uikit.customview.HomeTabLayout;
import com.netease.nim.uikit.customview.NumKeyboard;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.interfaces.IControlComming;
import com.netease.nim.uikit.interfaces.OnPasswordChangedListener;
import com.netease.nim.uikit.login.LogoutHelper;
import com.netease.nim.uikit.nim.ChatRoomHelper;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.helper.TeamNotificationHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.ClientType;
import com.netease.nimlib.sdk.auth.OnlineClient;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.NotificationAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.MemberChangeAttachment;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_APP_QUIT = "APP_QUIT";
    private static final int SHOW_LAYOUT_DELAY = 200;
    public static final int REQUEST_CODE_SETTINGS = 1;
    public final static int REQUESTCODE_EDIT = 3;
    public final static int REQUESTCODE_APP_MESSAGE = 4;
    public static final int RESULT_CODE_QUIT = 1;//"设置"页面退出登录回调
    public final static int RESULTCODE_EDIT_SUCCESS = 2;
    public static final int RESULT_CODE_APP_MESSAGE_UPDATE = 10;
    public static final String Extra_IsFromLogin = "com.htgames.chesscircle.ui.activity.isfromlogin";
    private HomeTabItem discoveryTab;
    private HomeTabItem clubTab;
    private ImageView homeTab;
    private HomeTabItem recordTab;
    private HomeTabItem meTab;
    DiscoveryFragment mDiscoveryFragment;
    GameFragmentNew mGameFragment;
    RecordFragment mRecordFragment;
    MeFragment mMeFragment;
    public RecentContactsFragment mRecentContactsFragment;
    private Fragment mCurrentFragment;
    //new
    public GameAction mGameAction;
    private AmountAction mAmountAction;
    private GameConfigAction mGameConfigAction;
    private HandCollectAction mHandCollectAction;

    public final static int TYPE_SESSION_P2P = 0;
    public final static int TYPE_SESSION_TEAM = 1;

    final static String FRAGMENT_DISCOVERY = "DiscoveryFragment";
    final static String FRAGMENT_GAME = "GameFragmentNew";
    final static String FRAGMENT_RECORD = "RecordFragment";
    final static String FRAGMENT_ME = "MeFragment";
    final static String FRAGMENT_RECENTCONTACTS = "RecentContactsFragment";
    int unReadMessageCount = 0;
    int unReadAppMessageCount = 0;//未读的APP消息数量
    int unReadDealarMessage = 0;
    ShareView mShareView;
    NetworkConnectRecevice networkConnectRecevice;
    IntentFilter intentFilter;
    HotUpdateAction mHotUpdateAction;
    ShopAction mShopAction;
    int pendingIntentAction = 0;
    boolean isIOSRegister = false;//是否是IOS用户
    boolean syncCompleted = false;//是否数据同步完成
    boolean mIsFromLogin = false;//是否来自登陆界面
    AudioCacheManager mAudioCacheManager;
    public static void start(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, boolean isIOSRegister, boolean isFromLogin) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra(Extra_IsFromLogin, isFromLogin);
        intent.putExtra(Extras.EXTRA_IS_IOSER, isIOSRegister);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void startSession(Activity activity, String sessionId, int sessionType) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Extras.EXTRA_SESSION_ID, sessionId);
        intent.putExtra(Extras.EXTRA_SESSION_TYPE, sessionType);
        activity.startActivity(intent);
    }

//    public void onGameFragmentCreateView() {//异步加载fragment会影响动画的流畅性，丝滑性
//    }
    View frame_content;
    View circular_reveal_view_id;
    Animator animator;
    private void closeCircularRevealAnimation() {
        if (isFinishing() || isDestroyedCompatible()) {
            return;
        }
        circular_reveal_view_id = findViewById(R.id.circular_reveal_view_id);
        if (circular_reveal_view_id == null) {
            return;
        }
        if (mIsFromLogin) {
            circular_reveal_view_id.setVisibility(View.INVISIBLE);
            return;//不执行动画，这个动画在login页面执行过了
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
                        if (circular_reveal_view_id == null) {
                            return;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (circular_reveal_view_id.getVisibility() != View.VISIBLE) {
                                circular_reveal_view_id.setVisibility(View.VISIBLE);
                            }
                            //因为CircularReveal动画是api21之后才有的,所以加个判断语句,免得崩溃
                            int cicular_R = (int) (ScreenUtil.screenHeight > ScreenUtil.screenWidth ? ScreenUtil.screenHeight * 1.5 : ScreenUtil.screenWidth * 1.5);
                            animator = ViewAnimationUtils.createCircularReveal(circular_reveal_view_id, ScreenUtil.screenWidth / 2, ScreenUtil.dip2px(circular_reveal_view_id.getContext(), 30), cicular_R, 0);
                            animator.setDuration(600);
                            animator.setInterpolator(new AccelerateDecelerateInterpolator());
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (circular_reveal_view_id != null && circular_reveal_view_id.getVisibility() == View.VISIBLE) {
                                        circular_reveal_view_id.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            animator.start();
                        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            long time = 800;
                            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                            alphaAnimation.setDuration(time);//持续时间
                            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if (circular_reveal_view_id != null) {
                                        circular_reveal_view_id.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                            if (circular_reveal_view_id != null) {
                                circular_reveal_view_id.startAnimation(alphaAnimation);
                            }
                        }
                    }
                });
            }
        });
    }

    long currentTimeStamp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsFromLogin = getIntent().getBooleanExtra(Extra_IsFromLogin, false);
        currentTimeStamp = System.currentTimeMillis();
        LogUtil.i("zzh", "启动速度 mainactivity oncreate: " + currentTimeStamp);

        setContentView(R.layout.activity_main);
        closeCircularRevealAnimation();
        FillableLoader fillableLoader = (FillableLoader) findViewById(R.id.fillableLoader);
        fillableLoader.setSvgPath(Paths.logoPath);
        fillableLoader.start();
        frame_content = findViewById(R.id.frame_content);
        LogUtil.i("zzh", "启动速度 mainactivity oncreate setContentView:" + (System.currentTimeMillis() - currentTimeStamp));//310-350很耗时
        currentTimeStamp = System.currentTimeMillis();

        initHomeBottomTab();
        ChessApp.showDebugView();
        LogUtil.i("zzh", "启动速度 mainactivity oncreate initHomeBottomTab: " + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();

        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        NIMClient.getService(TeamService.class).queryTeamListByType(TeamTypeEnum.Advanced)
                .setCallback(new com.netease.nimlib.sdk.RequestCallback<List<Team>>() {
                    @Override
                    public void onSuccess(List<Team> teams) {
                        PrintUtil.printTeamsInfo("zzh", " mainactivity初始化所有高级teams(包括游戏群):", teams);
                        ChessApp.allList.clear();
                        ChessApp.teamList.clear();
                        ChessApp.allList.addAll(teams);
                        for(Team team : teams) {
                            if(!GameConstants.isGmaeClub(team)) {
                                ChessApp.teamList.add(team);
                            }
                        }
                    }
                    @Override
                    public void onFailed(int code) {
                    }
                    @Override
                    public void onException(Throwable throwable) {
                    }
                });

        pendingIntentAction = getIntent().getIntExtra(Extras.EXTRA_PENDINGINTENT_ACIONT, 0);
        if (savedInstanceState != null) {
            initSaveFragment();
//            savedInstanceState.putParcelable("android:support:fragments", null);
            //弹出所有Fragment 全部重新加载
//            FragmentManager manager = getSupportFragmentManager();
//            manager.popBackStackImmediate(null, 1);
        }
        RequestTimeLimit.resetGetTime();
        isIOSRegister = getIntent().getBooleanExtra(Extras.EXTRA_IS_IOSER, false);
        setSwipeBackEnable(false);
        // 等待同步数据完成
        syncCompleted = LoginSyncDataStatusObserver.getInstance().observeSyncDataCompletedEvent(new Observer<Void>() {
            @Override
            public void onEvent(Void v) {
                DialogMaker.dismissProgressDialog();
                doDataSyncCompleted();
            }
        });
        LogUtil.i(TAG, "启动速度 sync completed = " + syncCompleted + (System.currentTimeMillis() - currentTimeStamp) + " savedInstanceState != null:" + (savedInstanceState != null));
        currentTimeStamp = System.currentTimeMillis();

        if (!syncCompleted) {
            DialogMaker.showProgressDialog(MainActivity.this, getString(R.string.prepare_data)).setCanceledOnTouchOutside(false);
        } else {
            doDataSyncCompleted();
        }
        initAction();
        mCurrentFragment = null;
        initFragments();
        registerObservers(true);
        LogUtil.i("zzh", "启动速度 mainactivity oncreate initFragments: " + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();

        initGetTuiOrUmeng();
        LogUtil.i("zzh", "启动速度 mainactivity oncreate initGetTuiOrUmeng: " + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();

        getUnreadGameMsgCount();
//        LogHttpRequest.logToServer(getApplicationContext(), "login success");
        if (isIOSRegister) {
            //是IOS注册用户
            showIosRegisterDialog();
        }
        ChatRoomHelper.init();//聊天室初始化
        doIntentAction();//执行意图
        if (!TextUtils.isEmpty(WelcomeActivity.shareGameCode)) {
            android.widget.Toast.makeText(ChessApp.sAppContext, "正在加入来自分享的游戏，请稍等...", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    joinSharedGame(WelcomeActivity.shareGameCode);
                    WelcomeActivity.shareGameCode = "";
                }
            }, 300);
        }
        nimNotifAction(getIntent());
        LogUtil.i("zzh", "启动速度 mainactivity oncreate initJoinCode: " + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();

        PaijuListPref.firstLaunchMainList = true;
    }

    JellyView rl_quick_game_join_jelly;
    GridPwdView join_game_grid_pwd_view;
    NumKeyboard num_keyboard_layout;

    public void showJoinCodeView(final boolean show) {
        if (show) {
            boolean hasAgree = SettingsPreferences.getInstance(this).hasAgreePokerClansProtocol();
            if (!hasAgree) {
                showPokerClansProtocol(MainActivity.this, new View.OnClickListener() {//显示codeview
                    @Override
                    public void onClick(View v) {
                        showOrHideCode(show);
                    }
                }, null);
            } else {
                showOrHideCode(show);
            }
        } else {
            showOrHideCode(show);
        }
        LogUtil.i("zzh", "show: " + show + "     rl_quick_game_join_jelly.getX(): " + rl_quick_game_join_jelly.getX());
    }

    private void showOrHideCode(boolean show) {
        if (mGameFragment != null) {
            mGameFragment.showBackTV(show);
        }
        if (rl_quick_game_join_jelly != null) {
            rl_quick_game_join_jelly.showAndHide(show);
        }
    }

    public void joinGameByCodeResult(boolean success) {
        if (join_game_grid_pwd_view == null) {
            return;
        }
        if (success) {
            join_game_grid_pwd_view.setPassword("");
            showJoinCodeView(false);
        } else {
            join_game_grid_pwd_view.clearPassword();
        }
    }

    public void initGetTuiOrUmeng() {
        //UmengAnalytics.setDebugMode(true);//UMENT DEBUG
//        GeTuiTools.init(ChessApp.sAppContext);//初始化个推数据  chessapp里面也调用过，都保留，不要删
        GeTuiTools.bindAlias(ChessApp.sAppContext, DemoCache.getAccount());
//        GeTuiHelper.setTag(ChessApp.sAppContext);
    }

    private void initHomeBottomTab() {
//        mOnlineStatusView = (OnlineStatusView) findViewById(R.id.mOnlineStatusView);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoveryTab.setEnabled(v == discoveryTab ? false : true);
                clubTab.setEnabled(v == clubTab ? false : true);
                homeTab.setEnabled(v == homeTab ? false : true);
                recordTab.setEnabled(v == recordTab ? false : true);
                meTab.setEnabled(v == meTab ? false : true);
                if (v == discoveryTab) {
                    if (mDiscoveryFragment == null) {
                        mDiscoveryFragment = DiscoveryFragment.newInstance();
                    }
                    changeFragment(mDiscoveryFragment, FRAGMENT_DISCOVERY);
                } else if (v == clubTab) {
                    if (mRecentContactsFragment == null) {
                        initRecentContactsFragment();
                    }
                    changeFragment(mRecentContactsFragment, FRAGMENT_RECENTCONTACTS);
                } else if (v == homeTab) {
                    if (mCurrentFragment != null && mGameFragment != null && mCurrentFragment == mGameFragment) {
//                        mGameFragment.onBackPressed();
                    } else {
                        if (mGameFragment == null) {
                            mGameFragment = GameFragmentNew.newInstance();
                        }
                        changeFragment(mGameFragment, FRAGMENT_GAME);
                    }
                } else if (v == recordTab) {
                    if (mRecordFragment == null) {
                        mRecordFragment = RecordFragment.newInstance();
                    }
                    changeFragment(mRecordFragment, FRAGMENT_RECORD);
                } else if (v == meTab) {
                    if (mMeFragment == null) {
                        mMeFragment = MeFragment.newInstance();
                        mMeFragment.setUnReadDealarMessage(unReadDealarMessage);
                    }
                    changeFragment(mMeFragment, FRAGMENT_ME);
                }
            }
        };
        HomeTabLayout home_tab_layout = (HomeTabLayout) findViewById(R.id.home_tab_layout);
        discoveryTab = (HomeTabItem) home_tab_layout.getChildAt(0);
        discoveryTab.setOnClickListener(clickListener);
        clubTab = (HomeTabItem) home_tab_layout.getChildAt(1);
        clubTab.setOnClickListener(clickListener);
        homeTab = (ImageView) home_tab_layout.findViewById(R.id.item_data);
        homeTab.setImageResource(R.drawable.home_tab_game);
        homeTab.setOnClickListener(clickListener);
        recordTab = (HomeTabItem) home_tab_layout.getChildAt(3);
        recordTab.setOnClickListener(clickListener);
        meTab = (HomeTabItem) home_tab_layout.getChildAt(4);
        meTab.setOnClickListener(clickListener);
        discoveryTab.setResources(R.string.main_tab_discovery, R.drawable.home_tab_discovery);
        clubTab.setResources(R.string.club, R.drawable.home_tab_chat);
        recordTab.setResources(R.string.main_tab_record, R.drawable.home_tab_contacts);
        meTab.setResources(R.string.more, R.drawable.home_tab_me);
    }

    /**
     * 初始化内存中的Fragment，存在的话取出
     */
    public void initSaveFragment() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(FRAGMENT_GAME) != null) {
            mGameFragment = (GameFragmentNew) fm.findFragmentByTag(FRAGMENT_GAME);
        }
        if (fm.findFragmentByTag(FRAGMENT_DISCOVERY) != null) {
            mDiscoveryFragment = (DiscoveryFragment) fm.findFragmentByTag(FRAGMENT_DISCOVERY);
        }
        if (fm.findFragmentByTag(FRAGMENT_RECORD) != null) {
            mRecordFragment = (RecordFragment) fm.findFragmentByTag(FRAGMENT_RECORD);
        }
        if (fm.findFragmentByTag(FRAGMENT_ME) != null) {
            mMeFragment = (MeFragment) fm.findFragmentByTag(FRAGMENT_ME);
        }
        if (fm.findFragmentByTag(FRAGMENT_RECENTCONTACTS) != null) {
            mRecentContactsFragment = (RecentContactsFragment) fm.findFragmentByTag(FRAGMENT_RECENTCONTACTS);
        }
    }

    private void initFragments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mGameFragment == null) {
            mGameFragment = GameFragmentNew.newInstance();
            if (!mGameFragment.isAdded()) {
                ft.add(R.id.frame_content, mGameFragment, FRAGMENT_GAME).commit();
            }
        }
//        ft.commitAllowingStateLoss();
        mCurrentFragment = mGameFragment;
    }

    public void changeFragment(Fragment fragment, String tag) {
        if (fragment == mCurrentFragment) {
            //如果选择的是当前的，不进行任何操作
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().executePendingTransactions();//虽然已经add过了，但是是异步的，因此Fragment.isAdded()=false
        if (mCurrentFragment != null) {
            ft.hide(mCurrentFragment);
        }
        mCurrentFragment = fragment;
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.frame_content, fragment, tag);
        }
//            ft.addToBackStack("");
        ft.commitAllowingStateLoss();
    }

    public void doDataSyncCompleted() {
        requestContactMessageUnreadCount();//获取最近联系人未读消息数量
    }

    public void doIntentAction() {
        //Log.d("pendingIntentAction", "pendingIntentAction :" + pendingIntentAction);
        if (pendingIntentAction != 0) {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (pendingIntentAction == PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL) {
                        AppMsgControlAC.start(MainActivity.this);
                    } else {
                        AppMsgInfoAC.start(MainActivity.this);//以前"系统消息"和"控制中心"是同一个页面，后来分开了    默认进入"系统消息"
                    }
                }
            }, 50);
        }
    }

    public void initAction() {
        mGameAction = new GameAction(this, null);
        mAmountAction = new AmountAction(this, null);
        mHotUpdateAction = new HotUpdateAction(this, null);
        mShopAction = new ShopAction(this, null);//获取商品列表
        mGameConfigAction = new GameConfigAction(this, null);
        //获取收藏牌谱数量
        mHandCollectAction = new HandCollectAction(this, null);
        //录音缓存管理
        mAudioCacheManager = new AudioCacheManager(getApplicationContext());
    }

    private void doThingsAfterInitAction() {//这个方法很耗时，加快主页启动速度
        mAmountAction.setRequestCallback(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (mMeFragment != null) {
                    mMeFragment.updateAmountUI();
                }
            }
            @Override
            public void onFailed() {
            }
        });
        mHotUpdateAction.onCreate();
        mHotUpdateAction.initHotUpdate();
        mShopAction.getShopGoodsList(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    ShopDataCache.getInstance().buildCache(result);
                }
            }

            @Override
            public void onFailed() {

            }
        });
        //获取APP组局配置
        String gameConfigData = GamePreferences.getInstance(getApplicationContext()).getGameConfigData();
        GameConfigHelper.dealGameConfig(gameConfigData);
        //奥马哈
        String gameConfigDataOmaha = GamePreferences.getInstance(getApplicationContext()).getGameConfigDataOmaha();
        GameConfigHelper.dealGameConfigOmaha(gameConfigDataOmaha);
        //大菠萝
        String gameConfigDataPineapple = GamePreferences.getInstance(getApplicationContext()).getGameConfigDataPineapple();
        GameConfigHelper.dealGameConfigPineapple(gameConfigDataPineapple);
        mGameConfigAction.getGameConfig();
        mGameConfigAction.getGameConfigOmaha();
        mGameConfigAction.getGameConfigPineapple();
        mHandCollectAction.getCollectHandNumFromNet(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    try {
                        Type type = new TypeToken<CommonBeanT<NetCardCountEy>>() {
                        }.getType();
                        CommonBeanT<NetCardCountEy> cbt = GsonUtils.getGson().fromJson(result, type);

                        UserPreferences.getInstance(MainActivity.this).setCollectHandNum(cbt.data.count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed() {
            }
        });
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                mAudioCacheManager.clearAudioCache();//清除过期的录音
            }
        });
    }

    public static void showPokerClansProtocol(Activity activity, final View.OnClickListener okListener, final View.OnClickListener cancelListener) {
        final ProtocolDialog agreeProtocolDialog = new ProtocolDialog(activity, R.style.dialog_default_style, true, ApiConstants.URL_PROTOCOL_GAME);
        agreeProtocolDialog.setTitle("免责声明及行为规范");
        agreeProtocolDialog.addPositiveButton(CacheConstant.GetString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeProtocolDialog.dismiss();
                SettingsPreferences.getInstance(ChessApp.sAppContext).setAgreePokerClansProtocol(true);
                if (okListener != null) {
                    okListener.onClick(v);
                }
            }
        });
        agreeProtocolDialog.addNegativeButton(CacheConstant.GetString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeProtocolDialog.dismiss();
                SettingsPreferences.getInstance(ChessApp.sAppContext).setAgreePokerClansProtocol(false);
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
            }
        });
        if (!activity.isFinishing()) {
            agreeProtocolDialog.show();
        }
        Window windowTest = agreeProtocolDialog.getWindow();
        windowTest.getDecorView().setPadding(0, 0, 0, 0);
        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowTest.setAttributes(lp);
    }

    public void tryCreateGame() {
        boolean hasAgree = SettingsPreferences.getInstance(this).hasAgreePokerClansProtocol();
        if (!hasAgree) {
            showPokerClansProtocol(MainActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryCreateGameAfterAgreeProtocol();
                }
            }, null);//创建牌局
        } else {
            tryCreateGameAfterAgreeProtocol();
        }
    }

    private void tryCreateGameAfterAgreeProtocol() {
        final boolean texasDataNull = GameConfigData.mtt_ante_multiple_quick.length <= 0 || GameConfigData.create_game_fee.length <= 0;
        final boolean omahaDataNull = GameConfigData.omaha_mtt_ante_multiple_quick.length <= 0;
        final boolean pineappleDataNull = GameConfigData.pineapple_antes.length <= 0 || GameConfigData.pineapple_mtt_fees.length <= 0;
        if (texasDataNull || omahaDataNull || pineappleDataNull) {
            EasyAlertDialog getConfigDialog = EasyAlertDialogHelper.createOkCancelDiolag(MainActivity.this, "", "未获取到游戏配置文件，需要请求配置数据吗？",
                    getString(R.string.ok) , getString(R.string.cancel), false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }
                        @Override
                        public void doOkAction() {
                            DialogMaker.showProgressDialog(MainActivity.this, "", false);
                            if (texasDataNull) {
                                GamePreferences.getInstance(DemoCache.getContext()).setConfigVersion(0);
                                mGameConfigAction.getGameConfig();
                            }
                            if (omahaDataNull) {
                                GamePreferences.getInstance(DemoCache.getContext()).setConfigVersionOmaha(0);
                                mGameConfigAction.getGameConfigOmaha();
                            }
                            if (pineappleDataNull) {
                                GamePreferences.getInstance(DemoCache.getContext()).setConfigVersionPineapple(0);
                                mGameConfigAction.getGameConfigPineapple();
                            }
                        }
                    });
            if (!isFinishing() && !isDestroyedCompatible()) {
                getConfigDialog.show();
            }
        } else {
            GameCreateActivity.start(MainActivity.this, GameConstants.GAME_TYPE_GAME);
        }
    }

    public void checkGameVersion(CheckHotUpdateCallback callback) {
        mHotUpdateAction.doHotUpdate(true, callback);
    }

    public void getAmount() {
        if (mAmountAction != null) {
            mAmountAction.getAmount(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            updateTeamList(teams);
            if (mRecentContactsFragment != null) {
                mRecentContactsFragment.updateClubUI();
            }
            PrintUtil.printTeamsInfo("zzh", "main activity onUpdateTeams：", teams);
            for (Team team : teams) {
                if (GameConstants.isGmaeClub(team)) {
                    PrintUtil.printTeamInfo("zzh", "main activity onUpdateTeams isGmaeClub：", team);
                    if (mGameFragment != null && mGameFragment == mCurrentFragment) {
                        //删除最近牌局中的结束牌局
                        mGameFragment.notifyNewGame("");
                        break;
                    }
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            PrintUtil.printTeamInfo("zzh", "main activity onRemoveTeam：", team);
            if (GameConstants.isGmaeClub(team)) {
                PrintUtil.printTeamInfo("zzh", "main activity onRemoveTeam isGmaeClub：", team);
                //私人牌局解散，删除联系人列表和历史记录
                RecentContactHelp.deleteRecentContact(team.getId(), SessionTypeEnum.Team, true);
                if (mGameFragment != null && mGameFragment == mCurrentFragment) {
                    //删除最近牌局中的结束牌局
                    mGameFragment.notifyNewGame("");
                }
            }
        }
    };

    public void updateTeamList(List<Team> teams) {
        if (teams == null || teams.isEmpty()) {
            return;
        }
        //老的team  key值集合
        Set<String> oldSet = new HashSet<String>();
        for (Team team : ChessApp.teamList) {
            oldSet.add(team.getId());
        }
        //老的所有群  key值集合
        Set<String> oldAllSet = new HashSet<String>();
        for (Team team : ChessApp.allList) {
            oldAllSet.add(team.getId());
        }
        //新的team  key值集合
        Set<String> newSet = new HashSet<String>();
        for (Team team : teams) {
            newSet.add(team.getId());
        }
        //俱乐部list删除重复的老的
        Iterator<Team> oldIterator = ChessApp.teamList.iterator();
        while (oldIterator.hasNext()) {
            Team team = oldIterator.next();
            if (newSet.contains(team.getId())) {
                oldIterator.remove();
            }
        }
        //所有群list删除重复的老的
        Iterator<Team> oldAllIterator = ChessApp.allList.iterator();
        while (oldAllIterator.hasNext()) {
            Team team = oldAllIterator.next();
            if (newSet.contains(team.getId())) {
                oldAllIterator.remove();
            }
        }
        //删除了一些相同的，然后全部加上，最后再便遍历一遍
        for (Team team : teams) {
            if (team.isMyTeam()) {
                if (!GameConstants.isGmaeClub(team)) {
                    if (team.getCreator().equals(DemoCache.getAccount()) && !oldSet.contains(team.getId())) {
                        RecentContactsFragment.myOwnClubCount++;
                    }
                    ChessApp.teamList.add(team);
                }
                ChessApp.allList.add(team);
            }
        }
        //上面是把teamList和allList刷新了一遍
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i("zzh", "onNewIntent");
        if (intent != null) {
            mIsFromLogin = intent.getBooleanExtra(Extra_IsFromLogin, false);
            if (mIsFromLogin && mGameFragment != mCurrentFragment) {//如果当前页不是游戏Fragment
                if (mGameFragment == null)//
                    mGameFragment = GameFragmentNew.newInstance();
                changeFragment(mGameFragment, FRAGMENT_GAME);
            }

            pendingIntentAction = intent.getIntExtra(Extras.EXTRA_PENDINGINTENT_ACIONT, 0);
            //Log.d("pendingIntentAction", "pendingIntentAction :" + pendingIntentAction);
            if (pendingIntentAction != 0) {
                doIntentAction();
            }
//            final String shareGameCode = intent.getStringExtra(WelcomeActivity.KEY_GAME_CODE_FROM_SHARE);
            if (!TextUtils.isEmpty(WelcomeActivity.shareGameCode)) {
                android.widget.Toast.makeText(ChessApp.sAppContext, "正在加入来自分享的游戏，请稍等...", Toast.LENGTH_SHORT).show();
                joinSharedGame(WelcomeActivity.shareGameCode);
                WelcomeActivity.shareGameCode = "";
            }
        }
        nimNotifAction(intent);
        //
        final String sessionId = intent.getStringExtra(Extras.EXTRA_SESSION_ID);
        final int sessionType = intent.getIntExtra(Extras.EXTRA_SESSION_TYPE, TYPE_SESSION_P2P);
        if (!TextUtils.isEmpty(sessionId)) {
            //聊天
            // 发送后，稍作延时后跳转
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sessionType == TYPE_SESSION_P2P) {
                        discoveryTab.performClick();
                        SessionHelper.startP2PSession(MainActivity.this, sessionId);
                    } else if (sessionType == TYPE_SESSION_TEAM) {
                        clubTab.performClick();
                        TeamMessageAC.start(MainActivity.this, sessionId, SessionHelper.getTeamCustomization(sessionId), TeamMessageAC.PAGE_TYPE_CHAT);
                    }
                }
            }, 50);
        }
    }

    /**
     * 解析云信通知栏的跳转 注意: StatusBarNotificationConfig 中的notificationEntrance 字段指明了点击通知需要跳转到的Activity，Activity启动后可以获取收到的消息：
     复制ArrayList<IMMessage> messages = (ArrayList<IMMessage>)
     getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT); // 可以获取消息的发送者，跳转到指定的单聊、群聊界面
     */
    private void nimNotifAction(Intent intent) {
        if (intent == null) {
            return;
        }
        ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
        if (messages == null || messages.size() <= 0) {
            return;
        }
        final IMMessage lastMessage = messages.get(messages.size() - 1);//最后面的事最新的消息
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lastMessage.getSessionType() == SessionTypeEnum.P2P) {
                    List msgs = new ArrayList();
                    msgs.add(lastMessage);
                    if (MD5.isVerifiedMsg(lastMessage.getUuid(), msgs) || DealerConstant.isDealer(lastMessage.getSessionId())) {
                        discoveryTab.performClick();
                        SessionHelper.startP2PSession(MainActivity.this, lastMessage.getSessionId());
                    }
                } else if (lastMessage.getSessionType() == SessionTypeEnum.Team) {
                    clubTab.performClick();
                    TeamMessageAC.start(MainActivity.this, lastMessage.getSessionId(), SessionHelper.getTeamCustomization(lastMessage.getSessionId()), TeamMessageAC.PAGE_TYPE_CHAT);
                }
            }
        }, 50);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    public void switchChatFragment(String sessionId, boolean isSwitchTab) {
        switchChatFragment(sessionId, null, isSwitchTab);
    }

    public void switchChatFragment(String sessionId, String serverId, boolean isSwitchTab) {
        if (isSwitchTab) {
            //切换到通讯界面
            clubTab.performClick();
        }
        if (TextUtils.isEmpty(serverId)) {
            SessionHelper.startTeamSession(this, sessionId);
        } else {
            SessionHelper.startTeamSession(this, sessionId, serverId);
        }
    }

    /**
     * 用户状态监听
     */
    Observer<StatusCode> onlineStatusObserver = new Observer<StatusCode>() {
        public void onEvent(StatusCode status) {
            LogUtil.i(TAG, "User status changed to: " + status);
            //kickOut
            if (status.wontAutoLogin()) {
                if (status == StatusCode.KICKOUT) {
//                    Toast.makeText(getApplicationContext(), R.string.login_in_otherip, Toast.LENGTH_SHORT).show();
                    onLogout(LoginActivity.REASON_KICKOUT);
                } else if (status == StatusCode.PWD_ERROR) {
                    //密码错误
//                    Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                    onLogout(LoginActivity.REASON_PWD_ERROR);
                } else if (status == StatusCode.FORBIDDEN) {
                    //被禁用
                    onLogout(LoginActivity.REASON_FORBIDDEN);
                }
            } else {
//                mOnlineStatusView.setVisibility(View.GONE);//mOnlineStatusView.updateStatus(status);
                if (status == StatusCode.NET_BROKEN) {
                    //未连接
                } else if (status == StatusCode.UNLOGIN) {
                    //未登录
                } else if (status == StatusCode.LOGINING) {
                } else if (status == StatusCode.LOGINED) {
                }
            }
        }
    };

    /**
     * 当前登录的客户端观察者
     */
    Observer<List<OnlineClient>> clientsObserver = new Observer<List<OnlineClient>>() {
        @Override
        public void onEvent(List<OnlineClient> onlineClients) {
            if (onlineClients == null || onlineClients.size() == 0) {
            } else {
//                TextView status = (TextView) multiportBar.findViewById(R.id.multiport_desc_label);
                OnlineClient client = onlineClients.get(0);
                switch (client.getClientType()) {
                    case ClientType.Windows:
//                        status.setText(getString(R.string.multiport_logging) + getString(R.string.computer_version));
                        break;
                    case ClientType.Web:
//                        status.setText(getString(R.string.multiport_logging) + getString(R.string.web_version));
                        break;
                    default:
//                        multiportBar.setVisibility(View.GONE);
                        break;
                }
            }
        }
    };

    Observer<List<RecentContact>> recentContactObserver = new Observer<List<RecentContact>>() {
                @Override
                public void onEvent(List<RecentContact> recentContacts) {
                    //有消息出现，都会走这个接口
                    dealRecentContact(recentContacts);
                }
            };

    public void getUnreadGameMsgCount() {
        // 查询最近联系人列表数据
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                if (code != ResponseCode.RES_SUCCESS || recents == null) {
                    return;
                }
                dealRecentContact(recents);
            }
        });
    }

    public void dealRecentContact(List<RecentContact> recents) {
        unReadDealarMessage = 0;
        if (recents != null && recents.size() != 0) {
            PrintUtil.printRecentContacts("zzh", "MainActivity recentContactObserver  ", recents);
            for (RecentContact recentContact : recents) {
                if (recentContact.getSessionType() == SessionTypeEnum.P2P) {
                    List uuids = new ArrayList();
                    uuids.add(recentContact.getRecentMessageId());//RecentContact的getRecentMessageId  和 IMessage的uuid一样
                    List messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                    boolean isVerifiedMsg = MD5.isVerifiedMsg(recentContact.getRecentMessageId(), messages);
                    if (isVerifiedMsg || DealerConstant.isDealer(recentContact.getContactId())) {
                        ChessApp.unreadP2PMsg.put(recentContact.getContactId(), recentContact);
                    }
                } else if (recentContact.getSessionType() == SessionTypeEnum.Team/* && !GameConstants.isGmaeClub(recentContact.getContactId())这个条件不需要，计算俱乐部消息红点数目时会过滤掉游戏群*/) {
                    calculateClubUnreadnumAndContent(recentContact);//把所有的群消息和未读数目保存到缓存中(包含俱乐部消息和游戏群消息)
                    if (mRecentContactsFragment != null) {
                        mRecentContactsFragment.chatMsgChanged();
                    }
                    updateNewChatUI(0);//更新"俱乐部"tab红点
                }
                if (recentContact.getSessionType() == SessionTypeEnum.P2P && DealerConstant.isDealer(recentContact.getContactId())) {
                    unReadDealarMessage = unReadDealarMessage + recentContact.getUnreadCount();
                }
            }
        }
        if (mMeFragment != null) {
            mMeFragment.setUnReadDealarMessage(unReadDealarMessage);
        }
        if (mGameFragment != null) {
            mGameFragment.setRecentContact(recents);
        }
        updateP2PMsg();
//        updateNewMeUI(unReadDealarMessage);//客服的聊天信息红点放到"动态"里面
    }

    public void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactObserver, register);
        //监听用户状态发生改变
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(onlineStatusObserver, register);
        NIMClient.getService(AuthServiceObserver.class).observeOtherClients(clientsObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);//开启新消息通知变化
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(messageStatusObserver, register);// 监听消息状态变化
//        NIMClient.getService(SystemMessageObserver.class).observeUnreadCountChange(sysMsgUnreadCountChangedObserver, register);
        if (register) {
            //注册未读消息数量观察者
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);
            //注册/注销系统消息未读数变化、比如 邀请加入俱乐部、拒绝邀请加入俱乐部、申请加入俱乐部、拒绝申请加入俱乐部、已添加你为好友、通过了你的好友请求、拒绝了你的好友请求
            MessageDataCache.getInstance().registerMessageDataChangedObserverObserver(messageDataChangedObserver);
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);
            CustomNotificationCache.getInstance().registerCustomNotificationObserver(mmCustomNotificationObserve);
            IntentFilter filter = new IntentFilter();//动态注册有新消息提醒通知
            filter.addAction(NewGameReceiver.ACTION_NEWGAME);
            //app消息观察者
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);
            MessageDataCache.getInstance().unregisterTeamMemberDataChangedObserver(messageDataChangedObserver);
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
            CustomNotificationCache.getInstance().unregisterCustomNotificationObserver(mmCustomNotificationObserve);
            unregisterReceiver(mAppMessageReceiver);
        }
        registerNetworkConnectReceiver(register);
    }

    NewGameReceiver mNewGameReceiver = new NewGameReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i("新游戏或者游戏解散通知：intent：" + intent);
            if (mGameFragment != null && mGameFragment == mCurrentFragment && intent != null) {
                String gid = intent.getStringExtra(Extras.EXTRA_GAME_ID);
                boolean isGameCancel = intent.getBooleanExtra(Extras.EXTRA_GAME_CANCEL, false);
                if (isGameCancel) {
                    mGameFragment.notifyCancelGame(gid);
                } else {
                    mGameFragment.notifyNewGame(gid);
                }
            }
        }
    };

    private void enableMsgNotification(boolean enable) {
//        boolean msg = (pager.getCurrentItem() != MainTab.RECENT_CONTACTS.tabIndex);
        boolean msg = (mCurrentFragment == mRecentContactsFragment);
        if (enable | msg) {
            /**
             * 设置最近联系人的消息为已读
             * @param account,    聊天对象帐号，或者以下两个值：
             *                    {@link #MSG_CHATTING_ACCOUNT_ALL} 目前没有与任何人对话，但能看到消息提醒（比如在消息列表界面），不需要在状态栏做消息通知
             *                    {@link #MSG_CHATTING_ACCOUNT_NONE} 目前没有与任何人对话，需要状态栏消息通知
             */
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item.getId() == ReminderId.SESSION) {
                updateNewChatUI(item.getUnread());
            } else if (item.getId() == ReminderId.CONTACT) {
            } else if (item.getId() == ReminderId.SYSTEM_MESSAGE) {
            } else if (item.getId() == ReminderId.APP_MESSAGE) {
                updateAppMessageUI(null);
            } else if (item.getId() == ReminderId.HORDE_MESSAGE) {
                updateClubInfo();//HORDE_MESSAGE
            }
        }
    };

    MessageDataCache.MessageDataChangedObserver messageDataChangedObserver = new MessageDataCache.MessageDataChangedObserver() {
        @Override
        public void onMessageUpdate(SystemMessage message) {
            int unreadCount = SystemMessageHelper.queryAllSystemMessageUnreadCount(getApplicationContext());
            ReminderManager.getInstance().updateSysMsgUnreadNum(unreadCount);//更新未读系统消息数量
            LogUtil.i("zzh", "好友消息&俱乐部消息： type" + message.getType() + "  fromAccount: " + message.getFromAccount() + "  content: " + message.getContent() + " targetId:" + message.getTargetId()+"\n"
                     + " attachObject:" + message.getAttachObject() + " getattach:" + message.getAttach());
            //判断类型
            if (message.getType() == SystemMessageType.AddFriend) {
                AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                if (attachData != null) {
                    // 针对不同的事件做处理
                    if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT) {
                        // 已添加你为好友
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND) {
                        // 对方通过了你的好友请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                        // 对方拒绝了你的好友请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST) {
                        // 对方请求添加好友，一般场景会让用户选择同意或拒绝对方的好友请求。通过message.getContent()获取好友验证请求的附言
                    }
                }
            } else if (message.getType() == SystemMessageType.ApplyJoinTeam || message.getType() == SystemMessageType.TeamInvite || message.getType() == SystemMessageType.DeclineTeamInvite || message.getType() == SystemMessageType.RejectTeamApply) {
                //俱乐部    相关的消息
                updateClubInfo();
            }
        }
    };

    /**
     * 查询系统消息未读数
     */
    private void requestSystemMessageUnreadCount() {
//        int unreadSystemMessageCount = MessageDataCache.getInstance().getMessageUnreadCount(MessageDataCache.TYPE_MESSAGE_ALL);
        int unreadSystemMessageCount = SystemMessageHelper.queryAllSystemMessageUnreadCount(getApplicationContext());
        ReminderManager.getInstance().updateSysMsgUnreadNum(unreadSystemMessageCount);//更新未读系统消息数量
        //Log.d(TAG, "unreadSystemMessageCount :" + unreadSystemMessageCount);
    }

    /**
     * 查询最近联系人消息未读数
     */
    private void requestContactMessageUnreadCount() {
        // 查询最近联系人列表数据
        //方法1：
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
            @Override
            public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && recents != null) {
                    int unreadNum = 0;
                    ChessApp.unreadChatNumPerTeam.clear();
                    ChessApp.unreadP2PMsg.clear();
                    PrintUtil.printRecentContacts("zzh", "MainActivity初始化 ", recents);
                    for (RecentContact r : recents) {
                        if (r.getSessionType() == SessionTypeEnum.P2P) {//不是自己发的消息，自己发的消息也能收到
                            List uuids = new ArrayList();
                            uuids.add(r.getRecentMessageId());//RecentContact的getRecentMessageId  和 IMessage的uuid一样
                            List messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                            boolean isVerifiedMsg = MD5.isVerifiedMsg(r.getRecentMessageId(), messages);
                            if (isVerifiedMsg || DealerConstant.isDealer(r.getContactId())) {
                                ChessApp.unreadP2PMsg.put(r.getContactId(), r);
                            }
                        } else if (r.getSessionType() == SessionTypeEnum.Team/* && !GameConstants.isGmaeClub(recentContact.getContactId())这个条件不需要，计算俱乐部消息红点数目时会过滤掉游戏群*/) {
                            calculateClubUnreadnumAndContent(r);//把所有的群消息和未读数目保存到缓存中(包含俱乐部消息和游戏群消息)
                            if (mRecentContactsFragment != null) {
                                mRecentContactsFragment.chatMsgChanged();
                            }
                            updateNewChatUI(0);//更新"俱乐部"tab红点
                        }
                        if (RecentContactHelp.isRecentMute(r)) {
                            //免打扰的联系人，不计入总数
                            continue;
                        }
                        unreadNum += r.getUnreadCount();
                    }
                    updateP2PMsg();
                    ReminderManager.getInstance().updateSessionUnreadNum(unreadNum);
                }
            }
        });
        //方法2：
//        int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();
//        unConstactReadNum = unreadNum;
//        ReminderManager.getInstance().updateSessionUnreadNum(unreadNum);
    }

    //把所有的群消息和未读数目保存到缓存中(包含俱乐部消息和游戏群消息)
    private void calculateClubUnreadnumAndContent(RecentContact r) {
        //未读消息给"俱乐部"页面展示
        //1是群信息；2不是游戏群；3这个群没有禁止接受消息
        boolean isMute = RecentContactHelp.isTeamMsgMute(r.getContactId());
        String fromNickname =  NimUserInfoCache.getInstance().getUserDisplayName(r.getFromAccount());//消息来自谁//r.getFromNick();这里会null pointer crash（比如创建sng后解散)
        String content = r.getContent();//消息内容
        String finalContent = fromNickname + ": " + content;;//消息拼接成的最终显示内容
        if (r.getMsgType() == MsgTypeEnum.notification) {
            finalContent = TeamNotificationHelper.getTeamNotificationText(r.getContactId(), r.getFromAccount(), (NotificationAttachment) r.getAttachment());
        } else if (r.getMsgType() == MsgTypeEnum.custom) {
            content = getMyDigestOfAttachment(r.getAttachment());
            finalContent = fromNickname + ": " + content;
        } else {
            finalContent = fromNickname + ": " + content;
        }
        if (!StringUtil.isSpace(r.getFromAccount()) && !StringUtil.isSpace(content) && !StringUtil.isSpace(finalContent)) {//最终要显示的消息内容为空的话不显示   第一个if条件的notification类型可能存在最终消息为空的情况
            if (r.getMsgType() != MsgTypeEnum.notification && r.getMsgType() != MsgTypeEnum.tip) {//这两类消息不显示"小红点"，但是消息内容显示出来
                ChessApp.unreadChatNumPerTeam.put(r.getContactId(), isMute ? 0 : r.getUnreadCount());//mute的话把未读数目强制置为0
            }
            ChessApp.newestMsgContentPerTeam.put(r.getContactId(), finalContent);
        }
    }

    //监听到有新消息变化，刷新界面，如果最近联系人Fragment还未创建的情况下
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            dealNewIncomingMessage(messages);
        }
    };

    public synchronized void dealNewIncomingMessage(List<IMMessage> messages) {
        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
        //Log.d("msgObserver", "incomingMessageObserver 收到新消息  : " + messages.size());
        int newConstactReadNum = 0;
        PrintUtil.printIMMessages("zzh", " MainActivity incomingMessageObserver: ", messages);
        for (final IMMessage imMessage : messages) {
            if (imMessage.getSessionType() == SessionTypeEnum.Team) {
                if (imMessage.getAttachment() instanceof MemberChangeAttachment && ((MemberChangeAttachment) imMessage.getAttachment()).getType() == NotificationType.PassTeamApply) {
                    SystemMessageHelper.deleteClubApplyFailedMsg(this, imMessage.getSessionId());//再次申请加入俱乐部之后这个消息就从数据库中删除了
                    if (AudioConstant.isGameAudioMessage(imMessage) || MessageFilter.isClubChangeNotShow(imMessage)) {
                        //游戏内语音或者是不显示的系统消息
                        NIMClient.getService(MsgService.class).deleteChattingHistory(imMessage);
                        //Log.d(TAG, "incomingMessageObserver 删除:" + imMessage.getUuid());
                    }
                }
                if (MessageHelper.isRecordUnRead(imMessage)) {
                    newConstactReadNum = newConstactReadNum + 1;
                }
            } else if (imMessage.getSessionType() == SessionTypeEnum.P2P) {
                if (mGameFragment != null && mCurrentFragment == mGameFragment) {
                    if (imMessage.getAttachment() != null && imMessage.getAttachment() instanceof GameAttachment) {
                        GameEntity gameEntity = ((GameAttachment) imMessage.getAttachment()).getValue();
                        if (gameEntity != null) {
                            //收到邀请，通知游戏
                            mGameFragment.notifyNewGame(gameEntity.gid);
                        }
                    }
                }
            }
            if (mRecentContactsFragment == null && newConstactReadNum != 0 && syncCompleted) {
                //如果RecentContactsFragment初始化，消息逻辑交由RecentContactsFragment处理
                ReminderManager.getInstance().updateSessionDeltaUnreadNum(newConstactReadNum);
            }
        }
    }

    private Observer<IMMessage> messageStatusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(final IMMessage msg) {
            PrintUtil.printIMMessage("zzh", "messageStatusObserver: ", msg);
            // 1、根据sessionId判断是否是自己的消息 2、更改内存中消息的状态 3、刷新界面
            //Log.d(TAG, "收到游戏内语音:" + msg.getUuid());
            if (msg.getSessionType() == SessionTypeEnum.Team && msg.getDirect() == MsgDirectionEnum.In &&
                    (msg.getAttachStatus() == AttachStatusEnum.transferred || msg.getAttachStatus() == AttachStatusEnum.fail)) {
                if (AudioConstant.isGameAudioMessage(msg)) {
                    //群牌局语音
                    NIMClient.getService(MsgService.class).deleteChattingHistory(msg);
                    //Log.d(TAG, "删除下载好的游戏内语音:" + msg.getUuid());
                }
            }
        }
    };

    CustomNotificationCache.CustomNotificationObserver mmCustomNotificationObserve = new CustomNotificationCache.CustomNotificationObserver() {
        @Override
        public void onNotificationIncoming(CustomNotification message) {
            PrintUtil.printCustomNotification("zzh", "onNotificationIncoming:  ", message);
            String content = message.getContent();
            if (!TextUtils.isEmpty(content)) {
                //Log.d(TAG, "contet:" + content);
            }
            try {
                JSONObject jsonObject = new JSONObject(content);
                int type = jsonObject.optInt(CustomNotificationConstants.KEY_NOTIFICATION_TYPE);
                String data = jsonObject.optString(CustomNotificationConstants.KEY_NOTIFICATION_DATA);
                if (type == CustomNotificationConstants.NOTIFICATION_TYPE_APP_MSG) {
                    if (sMatchRA != null && !data.isEmpty()) {
                        JSONObject jsonObjectData = new JSONObject(data);
                        if (jsonObjectData != null) {
                            String info = jsonObjectData.optString("info");
                            if (!info.isEmpty()) {
                                JSONObject jsonObjectInfo = new JSONObject(info);
                                if (jsonObjectInfo != null) {
                                    //从云信的推送消息中获取到，游戏暂停后重新开始...
                                    if (jsonObjectInfo.optInt("status", 0) == 106) {
                                        sMatchRA.getGameMatchStatus();
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // 在这里处理自定义通知。
                    if (message.getSessionType() == SessionTypeEnum.Team) {
                        if (CustomNotificationHelper.getNotificationType(content) == CustomNotificationConstants.NOTIFICATION_TYPE_GAME) {
                            GameEntity gameEntity = CustomNotificationHelper.getGameChangedInfo(content);
                            //牌局
//                            if (gameEntity != null) {
//                                if (message.getSessionType() == SessionTypeEnum.Team) {
//                                    mGameAction.updateGameMessageStatus(message.getSessionId(), message.getSessionType(), gameEntity.getGid(), gameEntity.getStatus());
//                                } else if (message.getSessionType() == SessionTypeEnum.P2P) {
//
//                                }
//                            }
                        } else if (CustomNotificationHelper.getNotificationType(content) == CustomNotificationConstants.NOTIFICATION_TYPE_TIP) {
                            String tipContent = CustomNotificationHelper.getNotificationData(content);
                            //判断系统消息来源，是群组并且是TIP消息才入库
                            MessageTipHelper.saveGameTipMessage(message.getSessionId(), message.getFromAccount(), message.getSessionType(), tipContent);//收到后保存在本地
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void updateNewChatUI(int udReadCount) {
        //这个tab红点只显示俱乐部消息，把游戏群的消息过滤掉
        int showUnReadCount = 0;
        for (Map.Entry<String, Integer> entry : ChessApp.unreadChatNumPerTeam.entrySet()) {
            String contactId = entry.getKey();
            if (!GameConstants.isGmaeClub(contactId)) {
                showUnReadCount = showUnReadCount + entry.getValue();
            }
        }
        clubTab.updateUnreadCount(showUnReadCount);
    }

    public void updateNewMeUI(int udReadCount) {
        meTab.updateUnreadCount(udReadCount);
    }

    public void updateNewSysMessageUI(int udReadCount) {
    }

    AppMessageReceiver mAppMessageReceiver = new AppMessageReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent == null || intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE) == null) {
                return;
            }
            AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
            if (appMessage.type == AppMessageType.GameOver) {
                if (mRecentContactsFragment != null) {
                    RequestTimeLimit.lastGetGamePlayingTime = 0;
                    mRecentContactsFragment.getPaijuDataByTeamId();
                }
            }
            updateAppMessageUI(appMessage);
        }
    };

    private void updateAppMessageUI(AppMessage appMessage) {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.updateAppMessageUI(appMessage);
        } else {
            initDiscoveryTabUI();//mDiscoveryFragment还未初始化
        }
    }
    private void updateClubInfo() {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.updateClubInfo(null);
        } else {
            if (hasReadDBOnce) {
                unreadClubInfoNum = HordeMessageHelper.queryHordeMessageByType(this, HordeMessageHelper.SEARCH_TYPE_INIT).size()
                        + SystemMessageHelper.querySystemMessageByType(this , SystemMessageHelper.TYPE_MESSAGE_TEAM_INIT, "").size();
                discoveryTab.updateUnreadCount(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
            } else {
                initDiscoveryTabUI();//mDiscoveryFragment还未初始化
            }
        }
    }
    private void updateP2PMsg() {
        if (mDiscoveryFragment != null) {
            mDiscoveryFragment.updateP2PMsg();
        } else {
            if (hasReadDBOnce) {
                unreadP2PMsg = 0;
                for (Map.Entry<String, RecentContact> entry : ChessApp.unreadP2PMsg.entrySet()) {
                    unreadP2PMsg += entry.getValue().getUnreadCount();
                }
                discoveryTab.updateUnreadCount(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
            } else {
                initDiscoveryTabUI();//mDiscoveryFragment还未初始化
            }
        }
    }
    int unreadNoticeCount = 0;
    int unHandledControlCount = 0;
    int unreadP2PMsg = 0;
    int unreadClubInfoNum = 0;
    boolean hasReadDBOnce = false;//是否已经读取过一次数据库
    private synchronized void initDiscoveryTabUI() {
        hasReadDBOnce = true;
        //appmessage消息数量
        unreadNoticeCount = AppMsgDBHelper.queryAppMessageUnreadCountByType(this, AppMsgDBHelper.TYPE_NOTICE);//系统消息, 未读
        unHandledControlCount = AppMsgDBHelper.queryAppMessageInitCountByType(this, AppMsgDBHelper.TYPE_CONTROL_CENTER);// 原始的消息，未做任何处理，但是可能已读，和"系统消息"不一样
        //俱乐部消息数量
        unreadClubInfoNum = HordeMessageHelper.queryHordeMessageByType(this, HordeMessageHelper.SEARCH_TYPE_INIT).size()
                + SystemMessageHelper.querySystemMessageByType(this , SystemMessageHelper.TYPE_MESSAGE_TEAM_INIT, "").size();
        discoveryTab.updateUnreadCount(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
        //私聊消息数量
        unreadP2PMsg = 0;
        for (Map.Entry<String, RecentContact> entry : ChessApp.unreadP2PMsg.entrySet()) {
            unreadP2PMsg += entry.getValue().getUnreadCount();
        }
        discoveryTab.updateUnreadCount(unreadNoticeCount + unHandledControlCount + unreadP2PMsg + unreadClubInfoNum);
    }

    public synchronized void updateDiscoveryTabUI(int unreadAppMsgNum) {
        discoveryTab.updateUnreadCount(unreadAppMsgNum);
    }

    /**
     * 注册网络状态监听
     *
     * @param register
     */
    public void registerNetworkConnectReceiver(boolean register) {
        if (networkConnectRecevice == null) {
            // 注册广播接受者java代码
            // 创建广播接受者对象
            networkConnectRecevice = new NetworkConnectRecevice();
        }
        // 注册receiver
        if (register) {
            intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkConnectRecevice, intentFilter);
        } else {
            unregisterReceiver(networkConnectRecevice);
        }
    }

    /**
     * 网络链接广播
     */
    private class NetworkConnectRecevice extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mCurrentFragment != null) {
                if (mCurrentFragment instanceof GameFragmentNew) {
                    ((GameFragmentNew) mCurrentFragment).setNetworkStatus();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    public void initRecentContactsFragment() {
        mRecentContactsFragment = RecentContactsFragment.newInstance();
        mRecentContactsFragment.setCallback(new RecentContactsCallback() {
            @Override
            public void onRecentContactsLoaded() {
                // 最近联系人列表加载完毕
            }

            @Override
            public void onUnreadCountChange(int unreadCount) {
                ReminderManager.getInstance().updateSessionUnreadNum(unreadCount);
            }

            @Override
            public void onItemClick(RecentContact recent) {
                // 回调函数，以供打开会话窗口时传入定制化参数，或者做其他动作
                switch (recent.getSessionType()) {
                    case P2P:
                        SessionHelper.startP2PSession(MainActivity.this, recent.getContactId(), recent.getUnreadCount());
                        break;
                    case Team:
                        SessionHelper.startTeamSession(MainActivity.this, recent.getContactId(), recent.getUnreadCount());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public String getDigestOfAttachment(MsgAttachment attachment) {
                return getMyDigestOfAttachment(attachment);
            }

            @Override
            public String getDigestOfTipMsg(RecentContact recent) {
                String msgId = recent.getRecentMessageId();
                List<String> uuids = new ArrayList<>(1);
                uuids.add(msgId);
                List<IMMessage> msgs = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (msgs != null && !msgs.isEmpty()) {
                    IMMessage msg = msgs.get(0);
                    if (!TextUtils.isEmpty(msg.getContent())) {
                        return msg.getContent();
                    }
                }
                return null;
            }
        });
    }

    public String getMyDigestOfAttachment(MsgAttachment attachment) {
        // 设置自定义消息的摘要消息，展示在最近联系人列表的消息缩略栏上
        // 当然，你也可以自定义一些内建消息的缩略语，例如图片，语音，音视频会话等，自定义的缩略语会被优先使用。
        if (attachment instanceof GuessAttachment) {
            GuessAttachment guess = (GuessAttachment) attachment;
            return guess.getValue().getDesc();
        } else if (attachment instanceof GameAttachment) {
            RequestTimeLimit.lastGetGamePlayingTime = 0;
            if (mRecentContactsFragment != null) {
                mRecentContactsFragment.getPaijuDataByTeamId();
            }
            GameAttachment tip = (GameAttachment) attachment;
            return getString(R.string.msg_custom_type_game_create_desc);
        }
//                else if (attachment instanceof TipAttachment) {
//                    TipAttachment tip = (TipAttachment) attachment;
////                    return "[自定义系统消息]";
//                    return tip.getNotificationText();
//                }
        else if (attachment instanceof BillAttachment) {
            BillAttachment billAttachment = (BillAttachment) attachment;
            return getString(R.string.msg_custom_type_paiju_info_desc);
        } else if (attachment instanceof PaipuAttachment) {
            PaipuAttachment paipuAttachment = (PaipuAttachment) attachment;
            return getString(R.string.msg_custom_type_paipu_info_desc);
        }
        return null;
    }

    public void doGameJoin(String joinWay, GameEntity gameEntity, boolean isDelete, GameRequestCallback mGameRequestCallback) {
        int gameMode = gameEntity.gameMode;
        if (gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
            mGameAction.joinGame(joinWay, gameEntity.code, mGameRequestCallback);
        } else if (gameEntity.type == GameConstants.GAME_TYPE_CLUB && (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG)) {
            //mGameAction.doGameJoin(joinWay, gameEntity, false, isDelete, mGameRequestCallback);
            //俱乐部也使用聊天室
            mGameAction.joinGame(joinWay, gameEntity.code, mGameRequestCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == EditUserInfoActivity.PICK_AVATAR_REQUEST) {
                //上传头像 //处理选取图片
                String path = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
                LogUtil.i(TAG, "pick avatar:" + path);
                if (mMeFragment != null) {
                    mMeFragment.updateAvatar(path);
                }
            }
        }
        if (requestCode == REQUESTCODE_EDIT && resultCode == RESULTCODE_EDIT_SUCCESS) {
            if (mMeFragment != null) {
                mMeFragment.getUserInfo(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 注销
    private void onLogout(int reason) {
        PokerActivity.exitGame();
        GeTuiHelper.unBindAliasUid(getApplicationContext());//解绑个推别名
        HandsCollectDBHelper.clearCollectHands(getApplicationContext());//清空手牌收藏
        UserPreferences.getInstance(getApplicationContext()).setCollectHandNum(0);//清空手牌收藏数量
        UmengAnalytics.onProfileSignOff();
        //清除登录状态&缓存&注销监听
        LogoutHelper.logout();
        DBUtil.closeDBUtil();//关闭数据库，因为数据库以个人UI作为账户
        // 启动登录
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Extras.EXTRA_REASON, reason);
        startActivity(intent);
        finish();
    }

    public boolean hasOnceResume = false;

    @Override
    public void onResume() {
        super.onResume();
        ControlToast.Companion.getInstance().setMainActivityDestroyed(false);
        LogUtil.i("zzh", "启动速度 main onResume begin: " +  + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();

        HotUpdateManager.getInstance().clearCallback();
        mChessApp.setGameIng(false);//设置不在游戏里面
        enableMsgNotification(false);
//        requestSystemMessageUnreadCount();//获取未读系统消息数量
        updateNewChatUI(0);//"俱乐部"页面红点消息
//        getUnReadAppMessageCount();
        checkVersion();
        if (!hasOnceResume) {
            hasOnceResume = true;
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    /** MessageQueue.IdleHandler可以用来在线程空闲的时候，指定一个操作；有点类似Handler.postDelayed(Runnable r, long delayMillis),都是在将来的某一个时间
                     执行一个操作。
                     不过，使用IdleHandler的好处在于可以不用指定一个将来时间，只要线程空闲了，就可以执行它指定的操作。
                     比较适合那种需要在将来执行操作，但是又不知道需要指定多少延迟时间的操作。
                        * 返回值boolean 意思是needKeep
                        * true，表示要保留保留， 代表不移除这个idleHandler，可以反复执行
                        * false代表执行完毕之后就移除这个idleHandler, 也就是只执行一次*/
                    LogUtil.i("zzh", "启动速度 mainactivity onresume queueIdle begin:" + (System.currentTimeMillis() - currentTimeStamp));//310-350很耗时
                    currentTimeStamp = System.currentTimeMillis();
                    PermissionUtils.requestMultiPermissions(MainActivity.this, null);//这行代码会导致执行onpause函数，因此onresume会执行2次，用个布尔变量限制下，蛋疼
                    inflateViewStub();
                    initDiscoveryTabUI();
                    getAmount();
                    doThingsAfterInitAction();
                    LogUtil.i("zzh", "启动速度 mainactivity onresume queueIdle end:" + (System.currentTimeMillis() - currentTimeStamp));//310-350很耗时
                    currentTimeStamp = System.currentTimeMillis();
                    return false;
                }
            });

        }
        LogUtil.i("zzh", "启动速度 main onResume end: " +  + (System.currentTimeMillis() - currentTimeStamp));
        currentTimeStamp = System.currentTimeMillis();
    }

    private void inflateViewStub() {
        ((ViewStub) findViewById(R.id.view_stub)).inflate();
        rl_quick_game_join_jelly = (JellyView) findViewById(R.id.rl_quick_game_join_jelly);
        rl_quick_game_join_jelly.handleBackInterface = new JellyView.IHandleBack() {
            @Override
            public void handleBack(boolean isCodeviewShow) {
                if (mGameFragment != null) {
                    mGameFragment.showBackTV(isCodeviewShow);
                }
            }
        };
        join_game_grid_pwd_view = (GridPwdView) findViewById(R.id.join_game_grid_pwd_view);
        join_game_grid_pwd_view.setPasswordVisibility(true);
        int topMargin = (int) (ScreenUtil.screenHeight * 0.12f);
        LinearLayout.LayoutParams pwdLP = (LinearLayout.LayoutParams) join_game_grid_pwd_view.getLayoutParams();
        pwdLP.setMargins(0, topMargin, 0, 0);
        join_game_grid_pwd_view.setOnPasswordChangedListener(new OnPasswordChangedListener() {
            @Override
            public void onChanged(String psw) {
                if (psw.length() >= 6) {
                    if(HotUpdateHelper.isGameUpdateIng()){
                        return;
                    }
                    CheckHotUpdateCallback callback = new CheckHotUpdateCallback() {
                        @Override
                        public void notUpdate() {
                            String gameCode = join_game_grid_pwd_view.getPassWord();
                            if (mGameFragment != null) {
                                mGameFragment.doGameJoinByGameStatus(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE, gameCode);
                            }
                        }
                    };
                    if (HotUpdateHelper.isNeedToCheckVersion()) {
                        checkGameVersion(callback);
                    } else{
                        callback.notUpdate();
                    }
                }
            }
            @Override
            public void onMaxLength(String psw) {
            }
        });
        num_keyboard_layout = (NumKeyboard) findViewById(R.id.num_keyboard_layout);
        num_keyboard_layout.setNumKeyboardClick(join_game_grid_pwd_view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i("zzh", "启动速度 main onPause");
    }

    private void joinSharedGame(final String shareGameCode) {
        if(HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        if (!HotUpdateHelper.isNeedToCheckVersion()) {
            if (mGameFragment != null) {
                mGameFragment.doGameJoinByGameStatus(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE, shareGameCode);
            }
        } else {
            checkGameVersion(new CheckHotUpdateCallback() {
                @Override
                public void notUpdate() {
                    if (mGameFragment != null) {
                        mGameFragment.doGameJoinByGameStatus(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE, shareGameCode);
                    }
                }
            });
        }
    }

    public void getUnReadAppMessageCount() {
        unReadAppMessageCount = AppMsgDBHelper.queryAllAppMessageUnreadCount(getApplicationContext());
        ReminderManager.getInstance().updateAppMsgUnreadNum(unReadAppMessageCount);
    }

    @Override
    protected void onRestart() {
        SystemWindowManager.showSystemAnnouncementView();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        SystemWindowManager.dismissSystemAnnouncementView();
        super.onStop();
        //Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        ControlToast.Companion.getInstance().setMainActivityDestroyed(true);
        if (locationTimer != null) {
            locationTimer.cancel();
            locationTimer = null;
        }
        RequestTimeLimit.lastGetRecentGameTime = 0;
        stopListenLocation("");//onDestroy
        ChessApp.hideDebugView();
        unregisterReceiver(mHomeKeyEventReceiver);
        registerObservers(false);
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        if (mAmountAction != null) {
            mAmountAction.onDestroy();
            mAmountAction = null;
        }
        if (mHotUpdateAction != null) {
            mHotUpdateAction.onDestroy();
            mHotUpdateAction = null;
        }
        if (mShopAction != null) {
            mShopAction.onDestroy();
            mShopAction = null;
        }
        if (mGameConfigAction != null) {
            mGameConfigAction.onDestroy();
            mGameConfigAction = null;
        }
        if (mHandCollectAction != null) {
            mHandCollectAction.onDestroy();
            mHandCollectAction = null;
        }
        mShareView = null;
        AssetsDatabaseManager.closeAllDatabase();//关闭打开的数据库
        SystemWindowManager.removeSystemAnnouncementView(getApplicationContext());
        recentContactObserver = null;
        onlineStatusObserver = null;
        clientsObserver = null;
        incomingMessageObserver = null;
        messageStatusObserver = null;
        unreadNumChangedCallback = null;
        messageDataChangedObserver = null;
        teamDataChangedObserver = null;
        mmCustomNotificationObserve = null;
        networkConnectRecevice = null;
        mAudioCacheManager = null;

        mDiscoveryFragment = null;
        mRecentContactsFragment = null;
        mGameFragment = null;
        mRecordFragment = null;
        mMeFragment = null;
        mCurrentFragment = null;
        networkConnectRecevice = null;
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
        super.onDestroy();
    }

    public void showKeyboardDelayed(final boolean isShow) {
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard(isShow);
            }
        }, SHOW_LAYOUT_DELAY);
    }

    /**
     * 跳转到系统设置界面
     */
    public void intentSystemSettings() {
        try {
            // 跳转到系统的网络设置界面
            Intent intentSettings = null;
            // 先判断当前系统版本
            if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                intentSettings = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            } else {
                intentSettings = new Intent();
                intentSettings.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
            }
            startActivity(intentSettings);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showShareView() {
        if (mShareView == null) {
            mShareView = new ShareView(this);
            mShareView.setOnCallOverback(new ShareView.OnCallOverback() {
                @Override
                public void showShadow(boolean show) {
                    if (show) {
                        findViewById(R.id.view_shadow).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.view_shadow).setVisibility(View.GONE);
                    }
                }
            });
        }
        mShareView.show();
//        SharePopDialog mSharePopDialog = new SharePopDialog(this);
//        mSharePopDialog.show();
    }

    EasyAlertDialog vipDialog;

    public void showVipDialog() {
        if (vipDialog == null) {
            vipDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.record_vip_dialog_tip), getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            vipDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(MainActivity.this, ShopActivity.TYPE_SHOP_VIP);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            vipDialog.show();
        }
    }


    public void showIosRegisterDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this, R.style.dialog_default_style).create();
        View rootView = LayoutInflater.from(this).inflate(R.layout.view_ios_register_dialog, null, false);
        rootView.findViewById(R.id.ios_register_dialog_positive_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
                EditUserItemActivity.start(MainActivity.this, DemoCache.getAccount(), EditUserItemActivity.FROM_PERFECT_INFO, UserConstant.KEY_NICKNAME, nickname);
                dialog.dismiss();
            }
        });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
        Window windowTest = dialog.getWindow();
        android.view.WindowManager.LayoutParams lp = windowTest.getAttributes();
        lp.width = ScreenUtil.getScreenWidth(this);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setContentView(rootView);
//        ViewGroup.LayoutParams params = rootView.getLayoutParams();
//        params.width = (int) ScreenUtil.getDialogWidth();
        dialog.setCancelable(false);
    }

    private long mExitTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (rl_quick_game_join_jelly != null) {//小于等于10表示codeview出现了，其实codeview出现后getx=0
                if (rl_quick_game_join_jelly.getX() < 0) {
                    rl_quick_game_join_jelly.resetPosition();//有时候按返回键的时候X是负值，不知道什么原因导致的
                }
                if (rl_quick_game_join_jelly.getX() <= 10) {
                    showJoinCodeView(false);
                    return true;
                }
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), R.string.exit_again_tips, Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }


    public static MatchRoomActivity sMatchRA;

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    ChessApp.hideDebugView();
                    CheckVersionPref.Companion.getInstance(MainActivity.this).setLastGoBackgroundTime(DemoCache.getCurrentServerSecondTime());
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };

    private void checkVersion() {
        if (ChessApp.isGameIng) {//如果在游戏中，不进行强更
            return;
        }
        long currentTimeSecond = DemoCache.getCurrentServerSecondTime();
        long lastGoBackgroundTime = CheckVersionPref.Companion.getInstance(this).getLastGoBackgroundTime();
        if (currentTimeSecond - lastGoBackgroundTime > CheckVersionPref.Companion.getCHECK_INTERVAL()) {//超过10分钟
            CheckVersionAction checkVersionAction = new CheckVersionAction(this, null);
            checkVersionAction.getVersionInfo(false, false, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_READ_PHONE_STATE) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }

    private int locationFailedTimes = 0;//定位失败的次数，第1次失败的话不把CacheConstant.mLocation置为null，第二次失败的话CacheConstant.mLocation置为null
    private Timer locationTimer;
    private Runnable mLocationRunnable = new Runnable() {
        @Override
        public void run() {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                stopListenLocation("");//没有开启定位
                return;
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    };
    private void getLocation() {
//        long currentTime = DemoCache.getCurrentServerSecondTime();
//        if (currentTime - RequestTimeLimit.lastGetLocationTime < RequestTimeLimit.GET_LOCATION_RECORD) {
//            return;
//        }
        if (locationTimer == null) {
            locationTimer = new Timer();
        }
        locationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(mLocationRunnable);
            }
        }, 0, RequestTimeLimit.GET_LOCATION_RECORD);
    }

    private void stopListenLocation(String location) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            stopListenLocation("");
            CacheConstant.mLocation = location;
            LogUtil.i("location", "onLocationChanged: " + location.toString());
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            LogUtil.i("location", "onStatusChanged status: " + status);
            if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                locationFailedTimes++;
                if (locationFailedTimes >= 2) {
                    CacheConstant.mLocation = null;
                    locationFailedTimes = 0;
                }
            }
        }

        public void onProviderEnabled(String provider) {
            LogUtil.i("location", "onStatusChanged provider: " + provider);
        }

        public void onProviderDisabled(String provider) {
            LogUtil.i("location", "onProviderDisabled provider: " + provider);
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                CacheConstant.mLocation = null;
                stopListenLocation("");//onProviderDisabled
                return;
            }
        }
    };
}
