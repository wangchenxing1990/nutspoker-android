package com.htgames.nutspoker.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * 横向ProgressBar
 */
public class HorizontalProgressBar extends ProgressBar {
    private final static String TAG = "HorizontalProgressBar";
    private final static long ANIMATION_DURATION = 1000L;

    public HorizontalProgressBar(Context context) {
        super(context);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircularProgressBar#setSencondProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     * @param duration The length of the animation, in milliseconds.
     */
    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) ((float) animation.getAnimatedValue());
                if (getProgress() != progress) {
                    setProgress(progress);
                }
            }
        });
    }

    public void setProgressWithAnimation(float progress) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(ANIMATION_DURATION);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int)((float)animation.getAnimatedValue());
                if(getProgress() != progress){
                    setProgress(progress);
                }
            }
        });
    }
}
