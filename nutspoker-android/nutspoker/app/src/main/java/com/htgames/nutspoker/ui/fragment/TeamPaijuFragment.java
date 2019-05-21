package com.htgames.nutspoker.ui.fragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.session.activity.TeamMessageAC;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.GameConfigAction;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.adapter.PaijuListAdapter;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.htgames.nutspoker.util.GameSortUtil;
import com.netease.nim.uikit.anim.BizierEvaluator2;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.customview.FilterView;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.htgames.nutspoker.chat.session.activity.TeamMessageAC.REQUESTCODE_GAMECREATE;

/**
 * Created by 周智慧 on 16/11/30.
 */

public class TeamPaijuFragment extends Fragment {
    /*R.drawable.btn_chatroom_game_ing         R.drawable.btn_chatroom_create_game      R.drawable.btn_chatroom_game_create
    * */
    View.OnClickListener clickListener;
    public Team team;
    String sessionId;
    private MeRecyclerView mRecyclerView;
    SwipeRefreshLayout refresh_layout;
    private PaijuListAdapter mAdapter;
    private TextView limitCreateTV;
    public static final String TAG = TeamMessageAC.TAG;
    ArrayList<GameEntity> previosudData = new ArrayList<GameEntity>();//这个要判断判断动画类型，是缩小还是放大
    ArrayList<GameEntity> gamePlayingList = new ArrayList<GameEntity>();//0
    ArrayList<GameEntity> gameListTexas = new ArrayList<GameEntity>();//1德州扑克  只包含普通局
    ArrayList<GameEntity> gameListOmaha = new ArrayList<GameEntity>();//2奥马哈  只包含普通局
    ArrayList<GameEntity> gameListPineapple = new ArrayList<GameEntity>();//3大菠萝 只包含普通局
    ArrayList<GameEntity> gameListMtt = new ArrayList<GameEntity>();//4mtt
    ArrayList<GameEntity> gameListSng = new ArrayList<GameEntity>();//5sng
    ArrayList<GameEntity> currentList = new ArrayList<GameEntity>();//当前使用的数据源
    ArrayList<GameEntity> currentListHasDesk = new ArrayList<GameEntity>();//当前使用的数据源且有空位
    public GameAction mGameAction;
    public static TeamPaijuFragment newInstance(String teamId) {
        TeamPaijuFragment mInstance = new TeamPaijuFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras.EXTRA_SHOW_DIVIDER, true);
        bundle.putString(Extras.EXTRA_ACCOUNT, teamId);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            if (mAdapter != null) {
                mAdapter.omahaAnimationDone = true;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.i(TAG, TAG + " onAttach " + getActivity() + " " + isAdded());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = getActivity().getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mGameAction = new GameAction(getActivity(), null);
        RequestTimeLimit.lastGetGameListInClub = 0;
        LogUtil.i(TAG, TAG + " onCreate " + getActivity() + " " + isAdded());
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG, TAG + " onCreateView " + getActivity() + " " + isAdded());
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HordeEntity> costList = ((TeamMessageAC) getActivity()).costList;
                if (StringUtil.isSpace(sessionId)) {
                    Toast.makeText(getActivity(), "获取俱乐部信息失败", Toast.LENGTH_SHORT).show();//可能获取俱乐部信息失败    可能创建牌局返回code3005
                    return;
                }
                if (!isOwnerOrManager) {
                    costList = null;//只有俱乐部的管理员和创建者才有权限共享牌局到部落
                }
                final boolean texasDataNull = GameConfigData.mtt_ante_multiple_quick.length <= 0 || GameConfigData.create_game_fee.length <= 0;
                final boolean omahaDataNull = GameConfigData.omaha_mtt_ante_multiple_quick.length <= 0;
                final boolean pineappleDataNull = GameConfigData.pineapple_antes.length <= 0 || GameConfigData.pineapple_mtt_fees.length <= 0;
                if (texasDataNull || omahaDataNull || pineappleDataNull) {
                    final GameConfigAction mGameConfigAction = new GameConfigAction(getActivity(), null);
                    EasyAlertDialog getConfigDialog = EasyAlertDialogHelper.createOkCancelDiolag(getActivity(), "", "未获取到游戏配置文件，需要请求配置数据吗？",
                            getString(R.string.ok) , getString(R.string.cancel),false,
                            new EasyAlertDialogHelper.OnDialogActionListener() {
                                @Override
                                public void doCancelAction() {
                                }
                                @Override
                                public void doOkAction() {
                                    DialogMaker.showProgressDialog(getActivity(), "", false);
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
                    if (!getActivity().isFinishing()) {
                        getConfigDialog.show();
                    }
                } else {
                    GameCreateActivity.startFromTeamByResult(getActivity(), REQUESTCODE_GAMECREATE, sessionId, GameConstants.GAME_TYPE_CLUB, isOwnerOrManager, costList);
                }
//                if (v == mCenterPaijuTV) {
//                    mCenterPaijuTV.setText("戳到我了");
//                } else {
//                    mCenterPaijuTV.setText("");
//                }
            }
        };
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_paiju, null);
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestTimeLimit.lastGetGameListInHorde = 0;
                getGamePlayingList();//刷新
            }
        });
        limitCreateTV = (TextView) view.findViewById(R.id.tv_paiju_control);
        limitCreateTV.setText("只能部落创建者开局");
        anim_container = view.findViewById(R.id.anim_container);
        anim_container.setOnClickListener(clickListener);
        //You should add the android:layout_width and android:layout_height attributes in the include tag. Otherwise, the margins are not taken into consideration.
        //((RelativeLayout.LayoutParams) anim_container.getLayoutParams()).setMargins(0, -ScreenUtil.dp2px(getActivity(), 60), 0, 0);//xml里面的margintop=60dp没生效
        anim_container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (currentList == null || currentList.size() <= 0) {
                    int[] startXY = new int[2];
                    anim_container.getLocationInWindow(startXY);
                    p0.set((int) anim_container.getX(), (int) anim_container.getY());
                    p1.set((int) (ScreenUtil.screenWidth - smallWidth), p0.y + ScreenUtil.dp2px(getActivity(), 20));
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    anim_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    anim_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        tv_create_club_game = (TextView) anim_container.findViewById(R.id.tv_create_club_game);
        mRecyclerView = (MeRecyclerView) view.findViewById(R.id.mRecyclerView);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.setNestedScrollingEnabled(false);
        }
        mAdapter = new PaijuListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
//        setLayoutAnimation();

        bigWidth = ScreenUtil.dp2px(getActivity(), 105);
        bigHeight = ScreenUtil.dp2px(getActivity(), 105);
        smallWidth = ScreenUtil.dp2px(getActivity(), 65);
        smallHeight = ScreenUtil.dp2px(getActivity(), 65);
        p0.set((int) ((ScreenUtil.screenWidth - bigWidth) / 2), (int) ((ScreenUtil.screenHeight - bigHeight) / 2 - ScreenUtil.dp2px(getActivity(), 50)));
        p1.set((int) (ScreenUtil.screenWidth - bigWidth), p0.y + ScreenUtil.dp2px(getActivity(), 20));
        p2.set(ScreenUtil.screenWidth - ScreenUtil.dp2px(getActivity(), 110), ScreenUtil.screenHeight - ScreenUtil.dp2px(getActivity(), 240));
        updataGameCreateBtnUI(null);
        //三个筛选按钮
        initFilterViews();
        return view;
    }

    private void setLayoutAnimation() {
        int duration = 400;
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new OvershootInterpolator());
        animationSet.setDuration(duration);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(translateAnimation);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animationSet);
        layoutAnimationController.setDelay(0.2f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        mRecyclerView.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i(TAG, TAG + " onActivityCreated " + getActivity() + " " + isAdded());
        getGamePlayingList();//onActivityCreated
    }

    public void removePaijuNotExisted(String code) {
        if (StringUtil.isSpace(code)) {
            return;
        }
        for (int i = 0; i < gamePlayingList.size(); i++) {
            GameEntity gameEntity = gamePlayingList.get(i);
            if (code.equals(gameEntity.code)) {
                gamePlayingList.remove(gameEntity);
                updataGameCreateBtnUI(null);
                if (mAdapter != null) {
                    mAdapter.notifyItemRemoved(i);
                }
                break;
            }
        }
    }

    public void getGamePlayingList() {
        if (PaijuListPref.firstLaunchTeamPaiju) {
            String jsonStr = PaijuListPref.getInstance().getTeamPaijuList(sessionId);
            if (!StringUtil.isSpaceOrZero(jsonStr)) {
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                try {
                    refreshData(new JSONObject(jsonStr));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            PaijuListPref.firstLaunchTeamPaiju = false;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        if ((currentTime - RequestTimeLimit.lastGetGameListInClub) < RequestTimeLimit.GET_GAME_LIST_IN_CLUB_LIMIT) {
            LogUtil.i(GameAction.TAG, "获取俱乐部牌局数据时间未到");
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        if (mGameAction == null) {
            if (getActivity() instanceof Activity) {
                mGameAction = new GameAction(getActivity(), null);
            }
        }
        if (mGameAction == null) {
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        RequestTimeLimit.lastGetGameListInClub = DemoCache.getCurrentServerSecondTime();
        mGameAction.getGamePlayingList(sessionId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                RequestTimeLimit.lastGetGameListInClub = DemoCache.getCurrentServerSecondTime();
                refreshData(response);
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                if (refresh_layout != null) {
                    refresh_layout.setRefreshing(false);
                }
                LogUtil.i(TAG, "code: " + code);
            }
        });
    }

    private void refreshData(JSONObject response) {
        gameListTexas.clear();
        gameListOmaha.clear();
        gameListPineapple.clear();
        gameListMtt.clear();
        gameListSng.clear();
        gamePlayingList = GameJsonTools.getGamePlayingList(sessionId, response, gameListTexas, gameListOmaha, gameListPineapple, gameListMtt, gameListSng);
        if (mAdapter != null && mRecyclerView != null) {
            updateDataByPaijuType();//refreshData
            //mRecyclerView.scheduleLayoutAnimation();
        }
    }

    boolean isOwnerOrManager = false;
    public void updataGameCreateBtnUI(TeamMember tm) {
        if (mRecyclerView == null) {
            return;
        }
        if (team != null) {
            if (!team.isMyTeam()) {
                limitCreateTV.setVisibility(View.VISIBLE);
                limitCreateTV.setText("您已不在这个俱乐部，点击加入新俱乐部");
                limitCreateTV.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                limitCreateTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddVerifyActivity.start(getActivity(), sessionId, AddVerifyActivity.TYPE_VERIFY_CLUB);
                    }
                });
            } else {
                if(tm == null) {
                    tm = NIMClient.getService(TeamService.class).queryTeamMemberBlock(team.getId(), DemoCache.getAccount());
                }
                if(tm == null) {
                    return;
                }
                boolean isCreateLimit = ClubConstant.isClubCreateGameByCreatorLimit(team) ? true : false;//限制开局
                isOwnerOrManager = (tm != null && (tm.getType() == TeamMemberType.Owner || tm.getType() == TeamMemberType.Manager)) ? true : false;
                anim_container.setVisibility(isCreateLimit ? (isOwnerOrManager ? View.VISIBLE : View.GONE) : View.VISIBLE);
                if (filter_view_right != null && filter_view_right.getMMmode() != 0) {
                    if (currentListHasDesk == null || currentListHasDesk.size() == 0) {
                        limitCreateTV.setVisibility(isCreateLimit ? (isOwnerOrManager ? View.GONE : View.VISIBLE) : View.GONE);
                        mRecyclerView.setAlpha(0);
                    } else {
                        limitCreateTV.setVisibility(View.GONE);
                        mRecyclerView.setAlpha(1);
                    }
                } else {
                    if (currentList == null || currentList.size() == 0) {
                        limitCreateTV.setVisibility(isCreateLimit ? (isOwnerOrManager ? View.GONE : View.VISIBLE) : View.GONE);
                        mRecyclerView.setAlpha(0);
                    } else {
                        limitCreateTV.setVisibility(View.GONE);
                        mRecyclerView.setAlpha(1);
                    }
                }
            }
        }
        judgeAnimation();
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(TAG, TAG + " onStart " + getActivity() + " " + isAdded());
    }

    @Override
    public void onResume() {
        super.onResume();
        getGamePlayingList();//onResume
        LogUtil.i(TAG, TAG + " onResume " + getActivity() + " " + isAdded());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.omahaAnimationDone = true;
        }
        LogUtil.i(TAG, TAG + " onPause " + getActivity() + " " + isAdded());
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(TAG, TAG + " onStop " + getActivity() + " " + isAdded());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.i(TAG, TAG + " onDestroyView " + getActivity() + " " + isAdded());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        LogUtil.i(TAG, TAG + " onDestroy " + getActivity() + " " + isAdded());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.i(TAG, TAG + " onDetach " + getActivity() + " " + isAdded());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        LogUtil.i(TAG, TAG + " onHiddenChanged is hidden: " + hidden);
    }

    View anim_container;
    ImageView iv_create_club_game;
    TextView tv_create_club_game;
    Point p0 = new Point();
    Point p1 = new Point();
    Point p2 = new Point();
    float bigWidth;
    float bigHeight;
    float smallWidth;
    float smallHeight;
    int duration = 900;
    private void doSmallAnimation() {
        anim_container.setAlpha(0);//置为透明，解决动画突兀的感觉
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator2(p1), p0, p2);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                anim_container.setX(point.x);
                anim_container.setY(point.y);
            }
        });
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(anim_container, "scaleX", 1F, 0.65F);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(anim_container, "scaleY", 1F, 0.65F);
        animatorScaleX.setInterpolator(new OvershootInterpolator());
        animatorScaleX.setDuration(duration);
        animatorScaleY.setInterpolator(new OvershootInterpolator());
        animatorScaleY.setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorScaleX).with(animatorScaleY).with(valueAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                AnimUtil.shake(GradientAC.this, anim_container);
                tv_create_club_game.setAlpha(0);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animatorScaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float containAlphaProgress = (1 - (float) animation.getAnimatedValue()) / 0.35f;
                anim_container.setAlpha(containAlphaProgress);
                float progress = ((float) animation.getAnimatedValue() - 0.58f) / 0.35f;
                tv_create_club_game.setAlpha(progress);
//                if ((float) animation.getAnimatedValue() <= 0.8f) {
//                    if (mAdapter != null) {
//                        mAdapter.setData(gamePlayingList);
//                    }
//                }
            }
        });
        animatorSet.start();
    }

    private void doBigAnimation() {
//        int[] startXY = new int[2];
//        anim_container.getLocationInWindow(startXY);
//        p0.set(startXY[0], startXY[1]);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BizierEvaluator2(p1), p2, p0);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new OvershootInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                anim_container.setX(point.x);
                anim_container.setY(point.y);
            }
        });
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(anim_container, "scaleX", 0.65F, 1);
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(anim_container, "scaleY", 0.65F, 1);
        animatorScaleX.setInterpolator(new OvershootInterpolator());
        animatorScaleX.setDuration(duration);
        animatorScaleY.setInterpolator(new OvershootInterpolator());
        animatorScaleY.setDuration(duration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorScaleX).with(animatorScaleY).with(valueAnimator);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
//                AnimUtil.shake(GradientAC.this, anim_container);
                tv_create_club_game.setAlpha(1);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        animatorScaleX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = ((float) animation.getAnimatedValue() - 0.65f) / 0.35f;
                tv_create_club_game.setAlpha(progress);
//                if ((float) animation.getAnimatedValue() >= 0.8f) {
//                    if (mAdapter != null) {
//                        mAdapter.setData(gamePlayingList);
//                    }
//                }
            }
        });
        animatorSet.start();
    }

    FilterView filter_view_left;
    FilterView filter_view_middle;
    FilterView filter_view_right;
    private void initFilterViews() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == filter_view_left) {
                    filter_view_left.changeMode(3);
                    filter_view_middle.changeMode(0);
                    if (filter_view_right.getMMmode() != 0) {
                        filter_view_right.changeMode(0);
                        updateDataByPaijuType();//如果上次选中的"有空位", 这次取消"有空位"
                    }
                    tryShowPaijuTypeMenu();
                } else if (v == filter_view_middle) {
                    filter_view_left.changeMode(0);
                    if (filter_view_middle.getMMmode() == 0) {
                        filter_view_middle.changeMode(1);
                        type_sort = 0;//从小到大
                    } else if (filter_view_middle.getMMmode() == 1) {
                        filter_view_middle.changeMode(2);
                        type_sort = 1;//从大到小
                    } else if (filter_view_middle.getMMmode() == 2) {
                        filter_view_middle.changeMode(1);
                        type_sort = 0;//从小到大
                    }
                    filter_view_right.changeMode(0);
                    updateDataBySortType();//手动点击排序
                } else if (v == filter_view_right) {
                    if (filter_view_right.getMMmode() != 0) {
                        return;
                    }
                    filter_view_left.changeMode(0);
                    filter_view_middle.changeMode(0);
                    filter_view_right.changeMode(3);
                    updateDataByPaijuType();//点击有空位
                }
            }
        };
        filter_view_left = (FilterView) view.findViewById(R.id.filter_view_left);
        filter_view_left.setOnClickListener(clickListener);
        filter_view_middle = (FilterView) view.findViewById(R.id.filter_view_middle);
        filter_view_middle.setOnClickListener(clickListener);
        filter_view_middle.setEnabled(type_paiju != 0);//当牌局类型是"所有牌局"时这个view不能点击
        filter_view_right = (FilterView) view.findViewById(R.id.filter_view_right);
        filter_view_right.setOnClickListener(clickListener);
    }

    int type_sort = -1;//默认从小到大排序
    public void updateDataBySortType() {
        if (type_paiju == 0 || type_sort == -1 || filter_view_middle.getMMmode() == 0) {
            return;
        }
        GameSortUtil.INSTANCE.setSort_type(type_sort);
        if (type_paiju == 1 || type_paiju == 2) {
            Collections.sort(currentList, GameSortUtil.INSTANCE.getComparatorBlinds());
        } else if (type_paiju == 3) {
            Collections.sort(currentList, GameSortUtil.INSTANCE.getComparatorPineappleNormal());
        } else if (type_paiju == 4 || type_paiju == 5) {
            Collections.sort(currentList, GameSortUtil.INSTANCE.getComparatorMatch());
        }
        mAdapter.setData(currentList);
    }

    //1普通局人数小于座位数 表示有空位   2sng为开赛表示有空位   3mtt全都有空位即mtt全都显示
    private void updateDataByEmptyDesk() {
        currentListHasDesk.clear();
        for (GameEntity entity : currentList) {
            if (entity.gameConfig instanceof GameNormalConfig) {
                if (entity.gamerCount < ((GameNormalConfig) entity.gameConfig).matchPlayer) {
                    currentListHasDesk.add(entity);
                }
            } else if (entity.gameConfig instanceof GameSngConfigEntity) {
                if (entity.start_time <= 0) {
                    currentListHasDesk.add(entity);
                }
            } else if (entity.gameConfig instanceof BaseMttConfig) {
                currentListHasDesk.add(entity);
            } else if (entity.gameConfig instanceof PineappleConfig) {
                if (entity.gamerCount < ((PineappleConfig) entity.gameConfig).getMatch_player()) {
                    currentListHasDesk.add(entity);
                }
            }
        }
    }

    int type_paiju = 0;//0所有牌局 1德州扑克 2奥马哈  3大菠萝 4mtt 5sng
    PopupWindow typePopupWindow;
    View container_all;
    View container_texas;
    View container_omaha;
    View container_pineapple;
    View container_mtt;
    View container_sng;
    TextView tv_all;
    TextView tv_texas;
    TextView tv_omaha;
    TextView tv_pineapple;
    TextView tv_mtt;
    TextView tv_sng;
    View iv_all;
    View iv_texas;
    View iv_omaha;
    View iv_pineapple;
    View iv_mtt;
    View iv_sng;
    View previous_container = container_all;
    HashMap<View, Integer> typesMap = new HashMap<>();
    private void tryShowPaijuTypeMenu() {
        if (typePopupWindow != null && typePopupWindow.isShowing()) {
            typePopupWindow.dismiss();
            return;
        }
        if (typePopupWindow == null) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (previous_container == v) {
                        typePopupWindow.dismiss();
                        return;
                    }
                    previous_container = v;
                    type_paiju = typesMap.get(v);
                    filter_view_middle.setEnabled(type_paiju != 0);//当牌局类型是"所有牌局"时这个view不能点击
                    typePopupWindow.dismiss();
                    updateDataByPaijuType();//clickListener
                    int[] strIds = new int[]{R.string.paiju_filter_all, R.string.texas_holdem, R.string.omaha, R.string.pine_apple, R.string.game_mtt_tournament, R.string.game_sng_single_desk};
                    filter_view_left.changeContent(strIds[type_paiju]);
                    int selectedColor = getActivity().getResources().getColor(R.color.login_solid_color);
                    int normalColor = getActivity().getResources().getColor(R.color.login_grey_color);
                    tv_all.setTextColor(v == container_all ? selectedColor : normalColor);
                    tv_texas.setTextColor(v == container_texas ? selectedColor : normalColor);
                    tv_omaha.setTextColor(v == container_omaha ? selectedColor : normalColor);
                    tv_pineapple.setTextColor(v == container_pineapple ? selectedColor : normalColor);
                    tv_mtt.setTextColor(v == container_mtt ? selectedColor : normalColor);
                    tv_sng.setTextColor(v == container_sng ? selectedColor : normalColor);
                    iv_all.setVisibility(v == container_all ? View.VISIBLE : View.INVISIBLE);
                    iv_texas.setVisibility(v == container_texas ? View.VISIBLE : View.INVISIBLE);
                    iv_omaha.setVisibility(v == container_omaha ? View.VISIBLE : View.INVISIBLE);
                    iv_pineapple.setVisibility(v == container_pineapple ? View.VISIBLE : View.INVISIBLE);
                    iv_mtt.setVisibility(v == container_mtt ? View.VISIBLE : View.INVISIBLE);
                    iv_sng.setVisibility(v == container_sng ? View.VISIBLE : View.INVISIBLE);
                }
            };
            View popView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_paiju_type, null);
            typePopupWindow = new PopupWindow(popView);
            typePopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            typePopupWindow.setOutsideTouchable(false);
            typePopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            typePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            iv_all = popView.findViewById(R.id.iv_all);
            iv_texas = popView.findViewById(R.id.iv_texas);
            iv_omaha = popView.findViewById(R.id.iv_omaha);
            iv_pineapple = popView.findViewById(R.id.iv_pineapple);
            iv_mtt = popView.findViewById(R.id.iv_mtt);
            iv_sng = popView.findViewById(R.id.iv_sng);
            tv_all = (TextView) popView.findViewById(R.id.tv_all);
            tv_texas = (TextView) popView.findViewById(R.id.tv_texas);
            tv_omaha = (TextView) popView.findViewById(R.id.tv_omaha);
            tv_pineapple = (TextView) popView.findViewById(R.id.tv_pineapple);
            tv_mtt = (TextView) popView.findViewById(R.id.tv_mtt);
            tv_sng = (TextView) popView.findViewById(R.id.tv_sng);
            container_all = popView.findViewById(R.id.container_all);
            container_all.setOnClickListener(clickListener);
            container_texas = popView.findViewById(R.id.container_texas);
            container_texas.setOnClickListener(clickListener);
            container_omaha = popView.findViewById(R.id.container_omaha);
            container_omaha.setOnClickListener(clickListener);
            container_pineapple = popView.findViewById(R.id.container_pineapple);
            container_pineapple.setOnClickListener(clickListener);
            container_mtt = popView.findViewById(R.id.container_mtt);
            container_mtt.setOnClickListener(clickListener);
            container_sng = popView.findViewById(R.id.container_sng);
            container_sng.setOnClickListener(clickListener);
            previous_container = container_all;
            typePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    filter_view_left.resetAnimation();
                }
            });
            typesMap.put(container_all, 0);
            typesMap.put(container_texas, 1);
            typesMap.put(container_omaha, 2);
            typesMap.put(container_pineapple, 3);
            typesMap.put(container_mtt, 4);
            typesMap.put(container_sng, 5);
            filter_view_middle.setEnabled(type_paiju != 0);//当牌局类型是"所有牌局"时这个view不能点击
        }
        filter_view_left.rotateAnimation();
        typePopupWindow.showAsDropDown(filter_view_left);
    }

    private void updateDataByPaijuType() {
        if (type_paiju == 0) {
            currentList = gamePlayingList;
        } else if (type_paiju == 1) {
            currentList = gameListTexas;
        } else if (type_paiju == 2) {
            currentList = gameListOmaha;
        } else if (type_paiju == 3) {
            currentList = gameListPineapple;
        } else if (type_paiju == 4) {
            currentList = gameListMtt;
        } else if (type_paiju == 5) {
            currentList = gameListSng;
        }
        if (type_paiju != 0 && type_sort != -1 && filter_view_middle.getMMmode() != 0) {
            updateDataBySortType();//刷新排序
        } else if (filter_view_right.getMMmode() != 0) {
            updateDataByEmptyDesk();//刷新
            mAdapter.setData(currentListHasDesk);
        } else {
            mAdapter.setData(currentList);
        }
        updataGameCreateBtnUI(null);
    }

    private void judgeAnimation() {
        //下面判断动画类型并执行动画
        if (filter_view_right != null && filter_view_right.getMMmode() != 0) {
            if (previosudData.size() <= 0 && currentListHasDesk.size() > 0) {
                doSmallAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
            } else if (previosudData.size() > 0 && currentListHasDesk.size() <= 0) {
                doBigAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
            }
            previosudData.clear();
            previosudData.addAll(currentListHasDesk);
        } else {
            if (previosudData.size() <= 0 && currentList.size() > 0) {
                doSmallAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
            } else if (previosudData.size() > 0 && currentList.size() <= 0) {
                doBigAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
            }
            previosudData.clear();
            previosudData.addAll(currentList);
        }
    }
}
