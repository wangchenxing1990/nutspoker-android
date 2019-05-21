package com.htgames.nutspoker.view;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 17/3/30.
 */

public class ChannelTypeView extends FrameLayout {
    int normalImageID;
    int selectImageID;
    int viewWidth;
    int viewHeight;
    int defaultWidth = 60;//默认宽度
    int defaultHeight = 60;//默认高度
    int duration = 250;
    Animator selectAnimator;
    Animator unSelectAnimator;
    ImageView normalImageView;
    ImageView selectImageView;
    public boolean isInAnim = false;//是否在动画中
    public ChannelTypeView(@NonNull Context context) {
        this(context, null);
    }

    public ChannelTypeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChannelTypeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChannelTypeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ChannelTypeView);
        boolean selected = ta.getBoolean(R.styleable.ChannelTypeView_ChannelTypeViewSelected, false);
        normalImageID = ta.getResourceId(R.styleable.ChannelTypeView_ChannelTypeViewNormalImage, R.mipmap.channel_personal_normal);
        selectImageID = ta.getResourceId(R.styleable.ChannelTypeView_ChannelTypeViewSelectImage, R.mipmap.channel_personal_select);
        viewWidth = ta.getDimensionPixelOffset(R.styleable.ChannelTypeView_ChannelTypeViewWidth, ScreenUtil.dp2px(getContext(), defaultWidth));
        viewHeight = ta.getDimensionPixelOffset(R.styleable.ChannelTypeView_ChannelTypeViewHeight, ScreenUtil.dp2px(getContext(), defaultHeight));
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        normalImageView = new ImageView(getContext());
        normalImageView.setVisibility(selected ? INVISIBLE : VISIBLE);
        normalImageView.setImageResource(normalImageID);
        selectImageView = new ImageView(getContext());
        selectImageView.setVisibility(!selected ? INVISIBLE : VISIBLE);
        selectImageView.setImageResource(selectImageID);
        addView(normalImageView, lp);
        addView(selectImageView, lp);
        super.setEnabled(!selected);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            doUnSelectAnim();
        } else {
            doSelectAnim();
        }
    }

    public void setMeSelected(boolean isSelected) {
        super.setEnabled(!isSelected);
        selectImageView.setVisibility(isSelected ? VISIBLE : INVISIBLE);
        normalImageView.setVisibility(isSelected ? INVISIBLE : VISIBLE);
    }

    private void doSelectAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cicular_R = (int) (viewWidth / 2.0f);
            selectAnimator = ViewAnimationUtils.createCircularReveal(selectImageView, cicular_R, cicular_R, 0, cicular_R);
            selectAnimator.setDuration(duration);
            selectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            selectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isInAnim = true;
                    selectImageView.setVisibility(VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    isInAnim = false;
                    normalImageView.setVisibility(INVISIBLE);
                    selectImageView.setVisibility(VISIBLE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                    isInAnim = false;
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                    isInAnim = true;
                }
            });
            selectAnimator.start();
        } else {
            selectImageView.setVisibility(VISIBLE);
            normalImageView.setVisibility(INVISIBLE);
        }
    }

    private void doUnSelectAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int cicular_R = (int) (viewWidth / 2.0f);
            unSelectAnimator = ViewAnimationUtils.createCircularReveal(selectImageView, cicular_R, cicular_R, cicular_R, 0);
            unSelectAnimator.setDuration(duration);
            unSelectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            unSelectAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isInAnim = true;
                    normalImageView.setVisibility(VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    isInAnim = false;
                    normalImageView.setVisibility(VISIBLE);
                    selectImageView.setVisibility(INVISIBLE);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                    isInAnim = false;
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                    isInAnim = true;
                }
            });
            unSelectAnimator.start();
        } else {
            selectImageView.setVisibility(INVISIBLE);
            normalImageView.setVisibility(VISIBLE);
        }
    }

    public void onDestroy() {
        if (selectAnimator != null) {
            if (selectAnimator.isRunning()) {
                selectAnimator.cancel();
                selectAnimator.removeAllListeners();
            }
            selectAnimator.removeAllListeners();
            selectAnimator = null;
        }
        if (unSelectAnimator != null) {
            if (unSelectAnimator.isRunning()) {
                unSelectAnimator.cancel();
                unSelectAnimator.removeAllListeners();
            }
            unSelectAnimator.removeAllListeners();
            unSelectAnimator = null;
        }
        if (normalImageView != null) {
            normalImageView.clearAnimation();
            normalImageView = null;
        }
        if (selectImageView != null) {
            selectImageView.clearAnimation();
            selectImageView = null;
        }
        isInAnim = false;
    }
}
