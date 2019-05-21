package com.htgames.nutspoker.ui.activity.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.GameName;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.chat.helper.MessageTipHelper;
import com.htgames.nutspoker.chat.session.activity.BaseMessageActivity;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.game.match.activity.FreeRoomAC;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.match.fragment.CreatePineappleMttFrg;
import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.ICheckGameVersion;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.ui.fragment.CreatePineappleNormalFrg;
import com.htgames.nutspoker.ui.fragment.CreateSNGFrg;
import com.htgames.nutspoker.ui.fragment.CreateMTTFrg;
import com.htgames.nutspoker.ui.fragment.CreateNormalFrg;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.preference.CreateGameConfigPref;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.constants.VipConstants;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 创建牌局
 */
public class GameCreateActivity extends BaseTeamActivity {
    public static int REQUEST_CODE_SELECT_PINEAPPLE_ANTE_TABLE = 28761;//选择 大菠萝mtt的底注结构表
    private final static String TAG = "GameCreateActivity";
    public GameAction mGameAction;
    String serverIp = "";
    String gameClubId = "";
    int coins = 0;
    int ownGameCount = 1;
    int ownSngGameCount = 1;
    int ownMttGameCount = 1;
    public int gameAreType = GameConstants.GAME_TYPE_GAME;
    public String teamId;
    public HordeEntity mHordeEntity;
    public boolean isClubManager = false;//在老的会员等级规则下：   俱乐部里面创建游戏规则---->1普通成员上限3  管理员5          俱乐部外面创建游戏规则：都是3
    int myVipLevel = VipConstants.VIP_LEVEL_NOT;
    CreateNormalFrg mCreateNormalFrg;
    CreateSNGFrg mCreateSNGFrg;
    CreateMTTFrg mCreateMTTFrg;
    private Fragment mCurrentFragment;//这个变量只保存三个fragment，不保存大菠萝fragment，大菠萝fragment用一个布尔值mPineappleFragmentShowing存储
    private Fragment mCurrentPineappleFrg;//这个变量保存2个fragment，表示是大菠萝"普通"fragment和"mtt"fragment
    final static String FRAGMENT_MODE_NORMAL = CreateNormalFrg.class.getSimpleName();
    final static String FRAGMENT_MODE_SNG = CreateSNGFrg.class.getSimpleName();
    final static String FRAGMENT_MODE_MTT = CreateMTTFrg.class.getSimpleName();
    //大菠萝相关
    CreatePineappleNormalFrg mGameCreatePineappleNormalFrg;
    CreatePineappleMttFrg mCreatePineappleMttFrg;
    PagerSlidingTabStrip pineapple_tabs;
    //
    PagerSlidingTabStrip create_game_tabs;
    GamePreferences mGamePreferences;
    public ArrayList<HordeEntity> costList;
    public int mPlayMode;//游戏模式，0="德州扑克"或者1="奥马哈"

    public static void start(final Activity activity, final int gameType) {
        needGameUpdate(activity, new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                Intent intent = new Intent(activity, GameCreateActivity.class);
                intent.putExtra(Extras.EXTRA_GAME_TYPE, gameType);
                activity.startActivity(intent);
            }
        });
    }

    public static void startActivityForResult(final Activity activity, final int requestCode, final String teamId, final int gameType) {
        needGameUpdate(activity, new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                Intent intent = new Intent(activity, GameCreateActivity.class);
                intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
                intent.putExtra(Extras.EXTRA_GAME_TYPE, gameType);
                activity.startActivityForResult(intent, requestCode);
            }
        });
    }

    public static void startFromTeamByResult(final Activity activity, final int requestCode, final String teamId, final int gameType, final boolean isClubManager, final ArrayList<HordeEntity> costList) {
        needGameUpdate(activity, new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                Intent intent = new Intent(activity, GameCreateActivity.class);
                intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
                intent.putExtra(Extras.EXTRA_GAME_TYPE, gameType);
                intent.putExtra(Extras.EXTRA_HORDE_COST_LSIT, costList);
                intent.putExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, isClubManager);
                activity.startActivityForResult(intent, requestCode);
            }
        });
    }

    public static void startFromHorde(final Activity activity, final String teamId, final int gameType, final HordeEntity hordeEntity, final boolean isClubManager) {
        needGameUpdate(activity, new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                Intent intent = new Intent(activity, GameCreateActivity.class);
                intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
                intent.putExtra(Extras.EXTRA_GAME_TYPE, gameType);
                intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity);
                intent.putExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, isClubManager);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_create);
        gameAreType = getIntent().getIntExtra(Extras.EXTRA_GAME_TYPE, GameConstants.GAME_TYPE_GAME);
        if (gameAreType == GameConstants.GAME_TYPE_CLUB) {
            teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        }
        isClubManager = getIntent().getBooleanExtra(Extras.EXTRA_GAME_CREATE_IS_CLUB_MANAGER, false);
        mHordeEntity = (HordeEntity) getIntent().getSerializableExtra(Extras.EXTRA_CUSTOMIZATION);
        costList = (ArrayList<HordeEntity>) getIntent().getSerializableExtra(Extras.EXTRA_HORDE_COST_LSIT);
        mPlayMode = CreateGameConfigPref.getInstance(this).getPlayMode();
        if (mPlayMode < GameConstants.PLAY_MODE_PINEAPPLE) {
            initView();
            initFragment();
        }
//        initSeekBar();
        mGamePreferences = GamePreferences.getInstance(getApplicationContext());
        ownGameCount = mGamePreferences.getOwngameCount();
        ownSngGameCount = mGamePreferences.getSngGameCount();
        ownMttGameCount = mGamePreferences.getMttGameCount();
        mGameAction = new GameAction(this, null);
        getSwipeBackLayout().setEdgeSize(ScreenUtil.dp2px(this, 20));//设置activity左滑滑动触发范围, 默认150(该机器50dp)像素，减小下，不然总是和标准局fragment的时间拖动条冲突
        initHead();
        if (!mGamePreferences.hasShowPlayModeGuidance()) {
            final View layer = LayoutInflater.from(this).inflate(R.layout.layout_game_create_layer, null);
            final View tips_hand = layer.findViewById(R.id.tips_hand);
            float translationY = ScreenUtil.dip2px(this, 5);
            final TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, translationY, -translationY);
            translateAnimation.setDuration(600);
            translateAnimation.setRepeatCount(Animation.INFINITE);
            translateAnimation.setRepeatMode(Animation.REVERSE);
            tips_hand.setAnimation(translateAnimation);
            tips_hand.startAnimation(translateAnimation);
            addContentView(layer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            layer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            layer.findViewById(R.id.iv_layer_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AnimUtil.closeCircularRevealAnimation(layer);
                    mGamePreferences.setHasShowPlayModeGuidance(true);
                }
            });
        }
        if (mPlayMode == GameConstants.PLAY_MODE_PINEAPPLE) {
            initPineappleFragment();
        }
    }

    TextView tv_head_title;
    ImageView iv_head_mute;
    private void initHead() {
        View.OnClickListener playModeClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playModePopupWindow != null && playModePopupWindow.isShowing()) {
                    playModePopupWindow.dismiss();
                    return;
                }
                showPlayModeDialog();
            }
        };
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_head_title.setText(mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? R.string.texas_holdem : (mPlayMode == GameConstants.PLAY_MODE_OMAHA ? R.string.omaha : R.string.pine_apple));
        tv_head_title.setPadding(0, 0, ScreenUtil.dp2px(this, 5), 0);
        tv_head_title.setTextColor(getResources().getColor(R.color.login_solid_color));
        tv_head_title.setOnClickListener(playModeClick);
        iv_head_mute = (ImageView) findViewById(R.id.iv_head_mute);
        iv_head_mute.setVisibility(View.VISIBLE);
        iv_head_mute.setColorFilter(ContextCompat.getColor(this, R.color.login_solid_color));//或者android:tint="@color/login_solid_color"效果一样
        iv_head_mute.setImageResource(R.mipmap.arrow_advance_down);
        iv_head_mute.setOnClickListener(playModeClick);
    }

    PopupWindow playModePopupWindow;
    View texas_holdem_container, omaha_container, pineapple_container;
    ImageView iv_texas_holdem_selected, iv_omaha_selected, iv_pineapple_selected;
    private void showPlayModeDialog() {
        View.OnClickListener changePlayModeClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPlayModeDialog();
                if (v == texas_holdem_container) {
                    if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
                        return;
                    }
                    mPlayMode = GameConstants.PLAY_MODE_TEXAS_HOLDEM;
                    showPineappleFragment(false);
                } else if (v == omaha_container) {
                    if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
                        return;
                    }
                    mPlayMode = GameConstants.PLAY_MODE_OMAHA;
                    showPineappleFragment(false);
                } else if (v == pineapple_container) {
                    if (mPlayMode == GameConstants.PLAY_MODE_PINEAPPLE) {
                        return;
                    }
                    mPlayMode = GameConstants.PLAY_MODE_PINEAPPLE;
                    showPineappleFragment(true);
                }
                tv_head_title.setText(mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? R.string.texas_holdem : (mPlayMode == GameConstants.PLAY_MODE_OMAHA ? R.string.omaha : R.string.pine_apple));
                notifyPlayMode();
            }
        };
        if (playModePopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.layout_play_mode, null);
            playModePopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
//            playModePopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            playModePopupWindow.setOutsideTouchable(false);
//            playModePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            playModePopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            playModePopupWindow.setHeight(ScreenUtil.screenHeight - getResources().getDimensionPixelOffset(R.dimen.action_bar_height) - ScreenUtil.getStatusBarHeight(this));
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            texas_holdem_container = popView.findViewById(R.id.texas_holdem_container);
            texas_holdem_container.setOnClickListener(changePlayModeClick);
            omaha_container = popView.findViewById(R.id.omaha_container);
            omaha_container.setOnClickListener(changePlayModeClick);
            pineapple_container = popView.findViewById(R.id.pineapple_container);
            pineapple_container.setOnClickListener(changePlayModeClick);
            iv_texas_holdem_selected = (ImageView) popView.findViewById(R.id.iv_texas_holdem_selected);
            iv_omaha_selected = (ImageView) popView.findViewById(R.id.iv_omaha_selected);
            iv_pineapple_selected = (ImageView) popView.findViewById(R.id.iv_pineapple_selected);
            popView.findViewById(R.id.set_play_mode_space).setOnClickListener(changePlayModeClick);
            playModePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    AnimUtil.startRotate(iv_head_mute, 180, 0, 300, Animation.RESTART);
                }
            });
        }
        iv_texas_holdem_selected.setVisibility(mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? View.VISIBLE : View.GONE);
        iv_omaha_selected.setVisibility(mPlayMode == GameConstants.PLAY_MODE_OMAHA ? View.VISIBLE : View.GONE);
        iv_pineapple_selected.setVisibility(mPlayMode == GameConstants.PLAY_MODE_PINEAPPLE ? View.VISIBLE : View.GONE);
        AnimUtil.startRotate(iv_head_mute, 0, 180, 300, Animation.RESTART);
        playModePopupWindow.showAsDropDown(tv_head_title);
    }

    private void dismissPlayModeDialog() {
        if (playModePopupWindow != null && playModePopupWindow.isShowing()) {
            playModePopupWindow.dismiss();
        }
    }

    public ArrayList<IPlayModeChange> playModeListeners = new ArrayList<>();
    public interface IPlayModeChange {
        void playModeChange(int play_mode);
    }
    private void notifyPlayMode() {
        for (int i = 0; i < playModeListeners.size(); i++) {
            playModeListeners.get(i).playModeChange(mPlayMode);
        }
    }

    public static void needGameUpdate(Activity activity, CheckHotUpdateCallback callback) {
        if (HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        if (HotUpdateHelper.isNeedToCheckVersion()) {
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).checkGameVersion(callback);
            } else if (activity instanceof BaseMessageActivity) {
                ((BaseMessageActivity) activity).checkGameVersion(callback);
            } else if (activity instanceof ICheckGameVersion) {
                ((ICheckGameVersion) activity).checkGameVersionCreate(callback);
            }
        } else {
            callback.notUpdate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private void initFragment() {
        if (mCreateNormalFrg == null) {
            mCreateNormalFrg = CreateNormalFrg.newInstance();
        }
        mPineappleFragmentShowing = false;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mCurrentFragment = mCreateNormalFrg;
        if (mCreateNormalFrg.isAdded()) {
            ft.show(mCreateNormalFrg);
        } else {
            ft.add(R.id.frame_content, mCreateNormalFrg, mCreateNormalFrg.getTag());
        }
        create_game_tabs.setVisibility(View.VISIBLE);
        if (mCurrentPineappleFrg != null) {
            pineapple_tabs.setVisibility(View.INVISIBLE);
            ft.hide(mCurrentPineappleFrg);
        }
        ft.commitAllowingStateLoss();
    }

    public void showFirstFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mCreateNormalFrg != null && mCreateNormalFrg.isAdded()) {
            ft.hide(mCreateNormalFrg);
        }
        if (mCreateSNGFrg != null && mCreateSNGFrg.isAdded()) {
            ft.hide(mCreateSNGFrg);
        }
        if (mCreateNormalFrg != null && mCreateNormalFrg.isAdded()) {
            ft.show(mCreateNormalFrg);
        }
        ft.commitAllowingStateLoss();
        mCurrentFragment = mCreateNormalFrg;
        hasShowFirstFragment = true;
    }
    public boolean hasShowFirstFragment = false;

    public void changeFragment(Fragment fragment, String tag) {
        if (fragment == mCurrentFragment) {
            //如果选择的是当前的，不进行任何操作
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
//        updateTabUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //可能购买了金币，回来要恢复
        coins = UserPreferences.getInstance(getApplicationContext()).getCoins();
//        updataGameConfigUI();
        myVipLevel = UserConstant.getMyVipLevel();
    }

    private void initView() {
        create_game_tabs = (PagerSlidingTabStrip) findViewById(R.id.create_game_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.create_view_pager);
        MyPagerAdapter adapter = new MyPagerAdapter();
        viewPager.setAdapter(adapter);
        create_game_tabs.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (mCreateNormalFrg == null) {
                        mCreateNormalFrg = CreateNormalFrg.newInstance();
                    }
//                        iv_game_mode_rule.setVisibility(View.GONE);
                    changeFragment(mCreateNormalFrg, FRAGMENT_MODE_NORMAL);
                } else if (position == 1) {
                    if (mCreateSNGFrg == null) {
                        mCreateSNGFrg = CreateSNGFrg.newInstance();
                    }
//                    iv_game_mode_rule.setVisibility(View.VISIBLE);
                    changeFragment(mCreateSNGFrg, FRAGMENT_MODE_SNG);
                } else if (position == 2) {
                    if (mCreateMTTFrg == null) {
                        mCreateMTTFrg = CreateMTTFrg.newInstance();
                    }
//                    iv_game_mode_rule.setVisibility(View.VISIBLE);
                    changeFragment(mCreateMTTFrg, FRAGMENT_MODE_MTT);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public class MyPagerAdapter extends PagerAdapter {
        public String[] TITLES = { "标准局", "SNG", "MTT" };
        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
        @Override
        public int getCount() {
            return TITLES.length;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View unusedView = new View(GameCreateActivity.this);
            container.addView(unusedView);
            return unusedView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }

    EasyAlertDialog hordeControlDialog;
    public void showHordeControlDialog() {
        if (hordeControlDialog == null) {
            hordeControlDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "无法共享至该", "该部落已开启开局限制，只允许创建者开局", getResources().getString(R.string.ok), true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            hordeControlDialog.show();
        }
    }

    EasyAlertDialog topUpDialog;
    public void showTopUpDialog(final int type) {
        String msg = getString(R.string.game_create_topup_dialog_tip);;
        if (type == ShopActivity.TYPE_SHOP_COIN) {
            msg = getString(R.string.game_create_topup_dialog_tip);
        } else if (type == ShopActivity.TYPE_SHOP_DIAMOND) {
            msg = getString(R.string.game_create_topup_dialog_tip_diamond);
        }
        topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                msg, getString(R.string.buy), getString(R.string.cancel), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                        topUpDialog.dismiss();
                    }

                    @Override
                    public void doOkAction() {
                        ShopActivity.start(GameCreateActivity.this, type);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

    public void showCreateLimitDialog(int code) {
        String title = "";//"创建牌局数已满";//= getString(UserConstant.getVipLevelShowRes(myVipLevel));
        String message = "创建牌局数已满";//getString(GameConstants.getCreateGameLimitShowRes(gameAreType, myVipLevel, isClubManager));
        EasyAlertDialogHelper.showOneButtonDiolag(this, title, message, getString(R.string.ok),
                false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
    }

    // : 17/3/2  需求改版 普通局和sng（俱乐部内和俱乐部外）统一都用聊天室(以前的逻辑是俱乐部内用聊天室，俱乐部外新建群)
    public void createGameByGroupNew(final Serializable gameConfig, final int gameMode, final boolean isControlBuy, final GameName mGameName, HordeEntity selectedHordeEntity, boolean hasSufficientDiamond) {
        int is_control = mHordeEntity == null ? (selectedHordeEntity != null ? selectedHordeEntity.is_control : 0) : mHordeEntity.is_control;
        int is_my = mHordeEntity == null ? (selectedHordeEntity != null ? selectedHordeEntity.is_my : 0) : mHordeEntity.is_my;
        if (is_my != 1 && is_control == 1) {//部落禁止建局，那么也禁止共享
            showHordeControlDialog();
            return;
        }
        if (!hasSufficientDiamond) {//创建部落牌局需要花费钻石，钻石不足的提示对话框
            showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        //通过APP服务器创建  进入聊天室之前不请求云信
        mGameAction.doGameFreeCreate(gameAreType, teamId, mGameName.getName(), gameConfig, isControlBuy, mHordeEntity == null ? (selectedHordeEntity == null ? "" : selectedHordeEntity.horde_id) : mHordeEntity.horde_id,
                mPlayMode,
                new VolleyCallback() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response);
                            int code = json.getInt("code");
                            if (code == 0) {
                                CreateGameConfigPref.getInstance(GameCreateActivity.this).setPlayMode(mPlayMode);
                                Toast.makeText(GameCreateActivity.this, "创建成功", Toast.LENGTH_SHORT).show();
                                RequestTimeLimit.lastGetRecentGameTime = (0);
                                RequestTimeLimit.lastGetGameListInHorde = 0;
                                RequestTimeLimit.lastGetAmontTime = 0;
                                JSONObject data = json.getJSONObject("data");
                                serverIp = data.optString(GameConstants.KEY_GAME_SERVER);
                                String roomId = data.optString(GameConstants.KEY_ROOM_ID);
                                String gameCode = data.optString(GameConstants.KEY_GAME_CODE);
                                if (gameMode == GameConstants.GAME_MODE_SNG) {
                                    mGamePreferences.setSngGamePrefix(mGameName.getPrefix());
                                    mGamePreferences.setSngGameCount(mGameName.getGameCount() + 1);
                                } else {
                                    mGamePreferences.setOwngamePrefix(mGameName.getPrefix());
                                    mGamePreferences.setOwngameCount(mGameName.getGameCount() + 1);
                                }
                                if (gameAreType == GameConstants.GAME_TYPE_GAME) {
                                    //统计，私人牌局
                                    UmengAnalyticsEvent.onEventGameCreate(getApplicationContext(), UmengAnalyticsEventConstants.TYPE_GAME_PRIVATE, gameConfig);
                                } else {
                                    RequestTimeLimit.lastGetGamePlayingTime = 0;
                                    if (!TextUtils.isEmpty(teamId) && TeamDataCache.getInstance().getTeamById(teamId) != null) {
                                        Team team = TeamDataCache.getInstance().getTeamById(teamId);
                                        if (team.getType() == TeamTypeEnum.Advanced) {
                                            //统计，俱乐部
                                            UmengAnalyticsEvent.onEventGameCreate(getApplicationContext(), UmengAnalyticsEventConstants.TYPE_GAME_CLUB, gameConfig);
                                        } else {
                                            //统计，圈子
                                            UmengAnalyticsEvent.onEventGameCreate(getApplicationContext(), UmengAnalyticsEventConstants.TYPE_GAME_GROUP, gameConfig);
                                        }
                                    }
                                }
                                finish();
                                FreeRoomAC.startByCreate(GameCreateActivity.this, roomId, teamId, mGameName.getName(), gameCode, serverIp);
                            } else {
                                if (code == ApiCode.CODE_GAME_IS_CREATED) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed_already, Toast.LENGTH_SHORT).show();
                                } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {//余额不足，创建失败，提示购买
                                    showTopUpDialog(ShopActivity.TYPE_SHOP_COIN);
                                } else if (code == ApiCode.CODE_GAME_CREATE_FAILURE) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                                } else if (code == ApiCode.CODE_GAME_NAME_LENGTH_LONG) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_long, Toast.LENGTH_SHORT).show();
                                } else if (code == ApiCode.CODE_GAME_NAME_FORMAT_WRONG) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_invalid, Toast.LENGTH_SHORT).show();
                                } else if (code == ApiCode.CODE_GAME_TEAM_COUNT_IS_LIMIT || code == ApiCode.CODE_GAME_PRIVATE_COUNT_IS_LIMIT) {//用户的俱乐部牌局创建已经达到上限3024      //用户的私人牌局创建已经达到上限3025
                                    showCreateLimitDialog(code);
                                } else if (code == ApiCode.CODE_CLUB_CREATE_BY_OWNER) {
                                    Toast.makeText(ChessApp.sAppContext, R.string.game_create_by_club_creator, Toast.LENGTH_SHORT).show();
                                } else if (code == ApiCode.CODE_UPDATE_NICKNAME_UBSUFFICIENT_DIAMOND) {
                                    showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND);//钻石不足
                                } else {
                                    String message = ApiResultHelper.getShowMessage(json);
                                    if (TextUtils.isEmpty(message)) {
                                        message = ChessApp.sAppContext.getString(R.string.game_create_failed);
                                    }
                                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                        }
                        DialogMaker.dismissProgressDialog();
                    }
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(ChessApp.sAppContext, R.string.game_create_failed, Toast.LENGTH_SHORT).show();
                        LogUtil.i(GameAction.TAG, "创建失败error" + (error == null ? "error==null" : error.toString()));
                    }
                });
    }

    /**
     * 创建牌局聊天室
     */
    public void createGameMatch(final Object gameConfig, final int gameMode, final boolean isControlBuy, final GameName mGameName, HordeEntity selectedHordeEntity, boolean hasSufficientDiamond) {
        int is_control = mHordeEntity == null ? (selectedHordeEntity != null ? selectedHordeEntity.is_control : 0) : mHordeEntity.is_control;
        int is_my = mHordeEntity == null ? (selectedHordeEntity != null ? selectedHordeEntity.is_my : 0) : mHordeEntity.is_my;
        if (is_my != 1 && is_control == 1) {//部落禁止建局，那么也禁止共享
            showHordeControlDialog();
            return;
        }
        if (!hasSufficientDiamond) {//创建部落牌局需要花费钻石，钻石不足的提示对话框
            showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND);
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        //通过APP服务器创建
        mGameAction.doGameMatchCreate(gameAreType, teamId, mGameName.getName(), gameConfig, isControlBuy, mHordeEntity == null ? (selectedHordeEntity == null ? "" : selectedHordeEntity.horde_id) : mHordeEntity.horde_id,
                mPlayMode,
                new GameRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject data) {
                        if (gameMode == GameConstants.GAME_MODE_MTT) {
                            mGamePreferences.setMttGamePrefix(mGameName.getPrefix());
                            mGamePreferences.setMttGameCount(mGameName.getGameCount() + 1);
                        } else if (gameMode == GameConstants.GAME_MODE_MT_SNG) {
                            mGamePreferences.setSngGameCount(ownSngGameCount + 1);
                        }
                        CreateGameConfigPref.getInstance(GameCreateActivity.this).setPlayMode(mPlayMode);
                        onCreateMatchSuccess(data);
                    }

                    @Override
                    public void onFailed(int code, JSONObject response) {
                        DialogMaker.dismissProgressDialog();
                        if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                            showTopUpDialog(ShopActivity.TYPE_SHOP_COIN);
                        } else if (code == ApiCode.CODE_GAME_TEAM_COUNT_IS_LIMIT || code == ApiCode.CODE_GAME_PRIVATE_COUNT_IS_LIMIT) {
                            showCreateLimitDialog(code);
                        } else if (code == ApiCode.CODE_UPDATE_NICKNAME_UBSUFFICIENT_DIAMOND) {
                            showTopUpDialog(ShopActivity.TYPE_SHOP_DIAMOND);//钻石不足
                        }
                    }
                });
    }

    /**
     * 创建成功回调
     */
    private void onCreateSuccess(Team team) {
        if (team == null) {
            return;
        }
        MessageTipHelper.sendGameTipMessage(team.getId());
        TeamDataCache.getInstance().addOrUpdateTeam(team);
        LogUtil.i(GameAction.TAG, "gamecreateactivity TeamDataCache.getInstance().addOrUpdateTeam(team):" + team.getId());//看下这里的log出现几次，有时候点击"开局"却创建多个聊天室
        finish();//以前俱乐部外面的普通局是群，现在改为聊天室了// : 17/3/14
//        if (TextUtils.isEmpty(serverIp)) {
//            SessionHelper.startGameTeamSession(GameCreateActivity.this, team.getId()); // 进入创建的群
//        } else {
//            SessionHelper.startGameTeamSession(GameCreateActivity.this, team.getId(), serverIp); // 进入创建的群
//        }
        //将创建的群组加入最近联系人列表
    }

    public void onCreateMatchSuccess(JSONObject data) {
        if (gameAreType == GameConstants.GAME_TYPE_CLUB) {
            RequestTimeLimit.lastGetGamePlayingTime = 0;
        }
        RequestTimeLimit.lastGetRecentGameTime = (0);
        RequestTimeLimit.lastGetGameListInClub = 0;
        RequestTimeLimit.lastGetGameListInHorde = 0;
        RequestTimeLimit.lastGetAmontTime = 0;
        Toast.makeText(getApplicationContext(), R.string.game_create_success, Toast.LENGTH_SHORT).show();
        String roomId = data.optString(GameConstants.KEY_ROOM_ID);
        String serverIp = data.optString(GameConstants.KEY_GAME_SERVER);
        String websocketUrl = data.optString("ws_url");
        String code = data.optString("code");
        MatchRoomActivity.startWithWebsockUrl(this, null, serverIp, code, null, websocketUrl, roomId);
        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
        intent.putExtra(Extras.EXTRA_DATA, data.toString());
        intent.putExtra(NewGameReceiver.ACTION_NEWGAME, NewGameReceiver.ACTION_NEWGAME_TYPE);
        sendBroadcast(intent);
        finish();
    }

    /**
     * 根据群字段获取
     * @param teamId
     */
    public void getTeamByNet(String teamId) {
//        DialogMaker.showProgressDialog(this, getString(R.string.game_enter_ing), false);
        DialogMaker.updateLoadingMessage("");
        NIMClient.getService(TeamService.class).searchTeam(teamId) .setCallback(new RequestCallback<Team>() {
                    @Override
                    public void onSuccess(Team team) {
                        DialogMaker.dismissProgressDialog();
                        onCreateSuccess(team);
                    }
                    @Override
                    public void onFailed(int i) {
                        DialogMaker.dismissProgressDialog();
                        showJoinGameDialog();
                    }
                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                        showJoinGameDialog();
                    }
                });
    }

    EasyAlertDialog joinGameDialog;
    public void showJoinGameDialog() {
        if (joinGameDialog == null) {
            joinGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", getString(R.string.game_enter_fauiler), getString(R.string.retry), getString(R.string.cancel), false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            joinGameDialog.dismiss();
                            finish();
                        }
                        @Override
                        public void doOkAction() {
                            if (!TextUtils.isEmpty(gameClubId)) {
                                getTeamByNet(gameClubId);
                            } else {
                                finish();
                            }
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            joinGameDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (playModePopupWindow != null && playModePopupWindow.isShowing()) {
            playModePopupWindow.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
    }

    private boolean mPineappleFragmentShowing = false;
    private void initPineappleFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mGameCreatePineappleNormalFrg == null) {
            mGameCreatePineappleNormalFrg = CreatePineappleNormalFrg.Companion.newInstance();
            if (!mGameCreatePineappleNormalFrg.isAdded()) {
                ft.add(R.id.frame_content_pineapple, mGameCreatePineappleNormalFrg, CreatePineappleNormalFrg.Companion.getTAG());
            }
        }
        pineapple_tabs = (PagerSlidingTabStrip) findViewById(R.id.pineapple_tabs);
        pineapple_tabs.setVisibility(View.VISIBLE);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_pineapple);
        MyPagerAdapter adapter = new MyPagerAdapter();
        adapter.TITLES = new String[]{"标准局", "MTT"};
        viewPager.setAdapter(adapter);
        pineapple_tabs.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    if (mGameCreatePineappleNormalFrg == null) {
                        mGameCreatePineappleNormalFrg = CreatePineappleNormalFrg.Companion.newInstance();
                    }
                    changePineappleFrg(mGameCreatePineappleNormalFrg, mGameCreatePineappleNormalFrg.Companion.getTAG());
                } else if (position == 1) {
                    if (mCreatePineappleMttFrg == null) {
                        mCreatePineappleMttFrg = CreatePineappleMttFrg.Companion.newInstance();
                    }
                    changePineappleFrg(mCreatePineappleMttFrg, mCreatePineappleMttFrg.Companion.getTAG());
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (mPlayMode == GameConstants.PLAY_MODE_PINEAPPLE) {
            mPineappleFragmentShowing = true;
            mCurrentPineappleFrg = mGameCreatePineappleNormalFrg;
            ft.show(mCurrentPineappleFrg);
            if (mCurrentFragment != null) {
                ft.hide(mCurrentFragment);
            }
            ft.commitAllowingStateLoss();
        }
    }

    private void showPineappleFragment(boolean show) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (show) {
            if (mGameCreatePineappleNormalFrg == null) {
                initPineappleFragment();
            }
            if (mPineappleFragmentShowing) {
                return;
            }
            mPineappleFragmentShowing = true;
            ft.show(mCurrentPineappleFrg);
            ft.hide(mCurrentFragment);
            create_game_tabs.setVisibility(View.INVISIBLE);
            pineapple_tabs.setVisibility(View.VISIBLE);
        } else {
            if (mCreateNormalFrg == null) {
                initView();
                initFragment();
            }
            if (!mPineappleFragmentShowing) {
                return;
            }
            mPineappleFragmentShowing = false;
            ft.hide(mCurrentPineappleFrg);
            ft.show(mCurrentFragment);
            create_game_tabs.setVisibility(View.VISIBLE);
            pineapple_tabs.setVisibility(View.INVISIBLE);
        }
        ft.commitAllowingStateLoss();
    }

    private void changePineappleFrg(Fragment fragment, String tag) {
        if (fragment == mCurrentPineappleFrg) {
            //如果选择的是当前的，不进行任何操作
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mCurrentPineappleFrg != null) {
            ft.hide(mCurrentPineappleFrg);
        }
        mCurrentPineappleFrg = fragment;
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.frame_content_pineapple, fragment, tag);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_PINEAPPLE_ANTE_TABLE && resultCode == RESULT_OK) {//大菠萝mtt的底注结构表
            if (mCreatePineappleMttFrg != null) {
                mCreatePineappleMttFrg.changeAnteTableType(data.getIntExtra(AnteTableAC.Companion.getKEY_ANTE_TABLE_TYPE(), GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL));
            }
        }
    }
}
