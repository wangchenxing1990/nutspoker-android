package com.netease.nim.uikit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 16/12/16.
 */

public class AnimUtil {
    public final static int ANIMATION_START = 0x01;
    public final static int ANIMATION_STOP = 0x02;
    public final static int ANIMATION_CANCEL = 0x03;

    public final static int TIME_ANIMATION = 500;// 动画时间
    public final static int TIME_DELAY500 = 500;// 动画延时
    public final static int TIME_ANIMATION300 = 300;// 动画时间

    /**
     * 放大动画显示
     *
     * @param view
     * @param duration
     */
    public static void scaleLargeIn(View view, int duration) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0F, 1F);
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0F, 1F);

        animatorScaleX.setInterpolator(new OvershootInterpolator());
        animatorScaleX.setDuration(duration);
        animatorScaleY.setInterpolator(new OvershootInterpolator());
        animatorScaleY.setDuration(duration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorScaleX).with(animatorScaleY);

        animatorSet.start();
    }

    /**
     * TextView数字动画
     * @param textView
     * @param value
     * @param duration
     */
    public static void textNumAnimation(final TextView textView, final int value, int duration) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, value);
        animator.setDuration(duration);
        final String finalPreFix = value >= 0 ? "+" : "";
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(finalPreFix + (int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 缩小动画显示
     *
     * @param view
     * @param duration
     */
    public static void scaleSmall(final View view, int duration) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1F, 0F);
        ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1F, 0F);
        animatorScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorScaleX.setDuration(duration);
        animatorScaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorScaleY.setDuration(duration);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorScaleX).with(animatorScaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    /**
     * 淡入
     *
     * @param view
     * @param time
     */
    public static void fadeIn(final View view, int time) {
        if (view == null) {
            return;
        }
        ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(time);
        fadeInAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeInAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        fadeInAnimator.start();
    }

    /**
     * 淡出
     *
     * @param view
     * @param time
     */
    public static void fadeOut(final View view, int time) {
        if (view == null) {
            return;
        }
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(time);
        fadeOutAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOutAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        fadeOutAnimator.start();
    }

    /**
     * 点击反馈，跳动一下
     */
    public static void jump(final View view, float offset, final OnAnimationCompletedListener listener) {
        if (view == null) {
            return;
        }
        ObjectAnimator animatorScaleLargeY = ObjectAnimator.ofFloat(view, "scaleY", 1F, offset);
        ObjectAnimator animatorScaleLargeX = ObjectAnimator.ofFloat(view, "scaleX", 1F, offset);
        ObjectAnimator animatorScaleSmallY = ObjectAnimator.ofFloat(view, "scaleY", offset, 1F);
        ObjectAnimator animatorScaleSmallX = ObjectAnimator.ofFloat(view, "scaleX", offset, 1F);
        animatorScaleLargeX.setInterpolator(new OvershootInterpolator());
        animatorScaleLargeX.setDuration(TIME_ANIMATION300);
        animatorScaleLargeY.setInterpolator(new OvershootInterpolator());
        animatorScaleLargeY.setDuration(TIME_ANIMATION300);

        AnimatorSet animatorLargeSet = new AnimatorSet();
        animatorLargeSet.play(animatorScaleLargeX).with(animatorScaleLargeY);

        AnimatorSet animatorSmallSet = new AnimatorSet();
        animatorLargeSet.play(animatorScaleSmallX).with(animatorScaleSmallY);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(animatorLargeSet).after(animatorSmallSet);

        if (listener != null) {
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    listener.onCompleted();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    listener.onCompleted();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        animatorSet.start();
    }

    /**
     * 默认的跳动 幅度1.1f
     *
     * @param view
     */
    public static void jump(final View view) {
        jump(view, 1.1f, null);
    }

    /**
     * 从下面冒出来
     */
    public static void slideInFromBottom(final View view, int duration) {
        if (view == null) {
            return;
        }
        ObjectAnimator slideInAnimation = ObjectAnimator.ofFloat(view, "translationY", view.getHeight(), 0f);
        slideInAnimation.setDuration(duration);
        slideInAnimation.setInterpolator(new OvershootInterpolator());
        slideInAnimation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        slideInAnimation.start();
    }

    /**
     * 从右侧移出
     */
    public static void slideOutToRight(final View view, int duration) {
        if (view == null) {
            return;
        }
        ObjectAnimator slideOutAnimation = ObjectAnimator.ofFloat(view, "translationX", 0f, view.getWidth());
        slideOutAnimation.setDuration(duration);
        slideOutAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        slideOutAnimation.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        slideOutAnimation.start();
    }

    /**
     * 抖动效果
     * @param context
     * @param view
     */
    public static void shake(Context context, View view) {
        if(context == null || view  == null){
            return;
        }
        Animation shakeInAnimation = AnimationUtils.loadAnimation(context, R.anim.shake);
        view.startAnimation(shakeInAnimation);
    }


    /**
     * 设置 StateListAnimator的点击反馈
     * @param view
     */
    public static void setClickStateFeedback(View view){
        if(view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            StateListAnimator stateListAnimator = new StateListAnimator();
            AnimatorSet normalAnimator = new AnimatorSet();
            AnimatorSet pressedAnimator = new AnimatorSet();

            normalAnimator.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1.0f).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1.0f).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "translationZ", 3, 0).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "elevation", 3, 0).setDuration(TIME_ANIMATION300)

            );

            pressedAnimator.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "translationZ", 0, 3).setDuration(TIME_ANIMATION300),
                    ObjectAnimator.ofFloat(view, "elevation", 0, 3).setDuration(TIME_ANIMATION300)
            );

            stateListAnimator.addState(new int[] { -android.R.attr.state_pressed }, normalAnimator);//正常状态
            stateListAnimator.addState(new int[] { android.R.attr.state_pressed }, pressedAnimator);//按住状态

            view.setStateListAnimator(stateListAnimator);
        }
    }


    /**
     * @author Devis跑
     * @des 动画监听接口
     */
    public interface MyAnimationListener {
        void onAnimatation(int index);
    }

    /**
     * 监听动画完成或者取消事件
     */
    public interface OnAnimationCompletedListener {
        void onCompleted();
    }

    public static void translateY(View view, int fromY, int toY, int duration) {
        ObjectAnimator translateYAnim = ObjectAnimator.ofFloat(view, "translationY", fromY, toY);
        translateYAnim.setDuration(duration);
        translateYAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        translateYAnim.start();
    }

    public static void translateX(View view, int fromX, int toX, int duration) {
        ObjectAnimator translateXAnim = ObjectAnimator.ofFloat(view, "translationX", fromX, toX);
        translateXAnim.setDuration(duration);
        translateXAnim.setInterpolator(new LinearInterpolator());
        translateXAnim.start();
    }

    public static void startRotate(View v, int fromDegree, int toDegree, int duration, int repeatMode) {
        if (v == null) {
            return;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", fromDegree, toDegree);
        animator.setDuration(duration);
        animator.setRepeatMode(repeatMode);
        animator.start();
    }

    public static void closeCircularRevealAnimation(final View circular_reveal_view_id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (circular_reveal_view_id.getVisibility() != View.VISIBLE) {
                circular_reveal_view_id.setVisibility(View.VISIBLE);
            }
            //因为CircularReveal动画是api21之后才有的,所以加个判断语句,免得崩溃
            int cicular_R = (int) (ScreenUtil.screenHeight > ScreenUtil.screenWidth ? ScreenUtil.screenHeight * 1.5 : ScreenUtil.screenWidth * 1.5);
            Animator animator = ViewAnimationUtils.createCircularReveal(circular_reveal_view_id, ScreenUtil.screenWidth / 2, ScreenUtil.dip2px(circular_reveal_view_id.getContext(), 30), cicular_R, 0);
            animator.setDuration(600);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (circular_reveal_view_id != null) {
                        circular_reveal_view_id.setVisibility(View.GONE);
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
}
