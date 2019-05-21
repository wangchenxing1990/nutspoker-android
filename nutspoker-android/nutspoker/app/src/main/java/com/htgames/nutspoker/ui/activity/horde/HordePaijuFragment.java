package com.htgames.nutspoker.ui.activity.horde;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.adapter.PaijuListAdapter;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;
import com.netease.nim.uikit.anim.BizierEvaluator2;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 17/3/21.
 */

public class HordePaijuFragment extends BaseFragment {
    HordeEntity mHordeEntity;
    SwipeRefreshLayout refresh_layout;
    private TextView limitCreateTV;
    private MeRecyclerView mRecyclerView;
    private PaijuListAdapter mAdapter;
    String teamId;
    ArrayList<GameEntity> previosudData = new ArrayList<GameEntity>();//这个要判断判断动画类型，是缩小还是放大
    ArrayList<GameEntity> gamePlayingList = new ArrayList<GameEntity>();
    public static HordePaijuFragment newIntance() {
        HordePaijuFragment mInstance = new HordePaijuFragment();
        Bundle bundle = new Bundle();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestTimeLimit.lastGetGameListInHorde = 0;
    }

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof HordeHallAC) {
            mHordeEntity = ((HordeHallAC) getActivity()).mHordeEntity;
            teamId = ((HordeHallAC) getActivity()).teamId;
        }
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameCreateActivity.startFromHorde(getActivity(), teamId, GameConstants.GAME_TYPE_CLUB, mHordeEntity, true);// TODO: 17/3/21 最后一个参数暂时写死为true
            }
        };
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_paiju, null);
        view.findViewById(R.id.container_filter_view).setVisibility(View.GONE);
        refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refresh_layout.setColorSchemeResources(R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color, R.color.login_solid_color);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestTimeLimit.lastGetGameListInHorde = 0;
                getGamePlayingList();
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
                int[] startXY = new int[2];
                anim_container.getLocationInWindow(startXY);
                p0.set((int) anim_container.getX(), (int) anim_container.getY());
                p1.set((int) (ScreenUtil.screenWidth - smallWidth), p0.y + ScreenUtil.dp2px(getActivity(), 20));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    anim_container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    anim_container.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        tv_create_club_game = (TextView) anim_container.findViewById(R.id.tv_create_club_game);
        mRecyclerView = (MeRecyclerView) view.findViewById(R.id.mRecyclerView);
        mAdapter = new PaijuListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        setLayoutAnimation();

        bigWidth = ScreenUtil.dp2px(getActivity(), 105);
        bigHeight = ScreenUtil.dp2px(getActivity(), 105);
        smallWidth = ScreenUtil.dp2px(getActivity(), 65);
        smallHeight = ScreenUtil.dp2px(getActivity(), 65);
        p0.set((int) ((ScreenUtil.screenWidth - bigWidth) / 2), (int) ((ScreenUtil.screenHeight - bigHeight) / 2));
        p1.set((int) (ScreenUtil.screenWidth - bigWidth), p0.y + ScreenUtil.dp2px(getActivity(), 20));
        p2.set(ScreenUtil.screenWidth - ScreenUtil.dp2px(getActivity(), 110), ScreenUtil.screenHeight - ScreenUtil.dp2px(getActivity(), 320));
        updataGameCreateBtnUI();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.omahaAnimationDone = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getGamePlayingList();
    }

    public void removePaijuNotExisted(String code) {
        if (StringUtil.isSpace(code)) {
            return;
        }
        for (int i = 0; i < gamePlayingList.size(); i++) {
            GameEntity gameEntity = gamePlayingList.get(i);
            if (code.equals(gameEntity.code)) {
                gamePlayingList.remove(gameEntity);
                updataGameCreateBtnUI();
                if (mAdapter != null) {
                    mAdapter.notifyItemRemoved(i);
                }
                break;
            }
        }
    }

    public void getGamePlayingList() {
        long currentTime = DemoCache.getCurrentServerSecondTime();
        if ((currentTime - RequestTimeLimit.lastGetGameListInHorde) < RequestTimeLimit.GET_GAME_LIST_IN_HORDE_LIMIT) {
            LogUtil.i(HordeHallAC.TAG, "获取数据时间未到");
            if (refresh_layout != null) {
                refresh_layout.setRefreshing(false);
            }
            return;
        }
        if (getActivity() instanceof HordeHallAC) {
            mHordeEntity = ((HordeHallAC) getActivity()).mHordeEntity;
            teamId = ((HordeHallAC) getActivity()).teamId;
            ((HordeHallAC) getActivity()).mHordeAction.hordePlaying(teamId, mHordeEntity.horde_id, new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (refresh_layout != null) {
                        refresh_layout.setRefreshing(false);
                    }
                    RequestTimeLimit.lastGetGameListInHorde = DemoCache.getCurrentServerSecondTime();
                    gamePlayingList = GameJsonTools.getGamePlayingList(teamId, response);
                    updataGameCreateBtnUI();
                    if (mAdapter != null) {
                        mAdapter.setData(gamePlayingList);
                    }
                }
                @Override
                public void onFailed(int code, JSONObject response) {
                    if (refresh_layout != null) {
                        refresh_layout.setRefreshing(false);
                    }
                }
            });
        }
    }

    private void updataGameCreateBtnUI() {
        if (mRecyclerView == null || mHordeEntity == null) {
            return;
        }
        anim_container.setVisibility(mHordeEntity.is_control == 1 && mHordeEntity.is_my == 0 ? View.GONE : View.VISIBLE);
        if (gamePlayingList == null || gamePlayingList.size() == 0) {
            limitCreateTV.setVisibility(mHordeEntity.is_control == 1 && mHordeEntity.is_my == 0 ? View.VISIBLE : View.GONE);
            mRecyclerView.setAlpha(0);
        } else {
            limitCreateTV.setVisibility(View.GONE);
            mRecyclerView.setAlpha(1);
        }
        //下面判断动画类型并执行动画
        if (previosudData.size() <= 0 && gamePlayingList.size() > 0) {
            doSmallAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
        } else if (previosudData.size() > 0 && gamePlayingList.size() <= 0) {
            doBigAnimation();//动画执行过程中会调用adapter的notifidatasetchanged
        }
        previosudData.clear();
        previosudData.addAll(gamePlayingList);
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

    View anim_container;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof HordeHallAC) {
            mHordeEntity = ((HordeHallAC) getActivity()).mHordeEntity;
        }
    }
}
