package com.htgames.nutspoker.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.BaseTools;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * 跑马灯效果的TextView
 */
public class MarqueeTextView extends TextView{
    final String TAG = "MarqueeTextView";
    private boolean isStop = false;
    private int textWidth;
    private boolean isMeasure = false;
    OnMarqueeListener mOnMarqueeListener;
    ObjectAnimator xCreateAnimator;

    public MarqueeTextView(Context context) {
        super(context);
        init(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isMeasure){
            //文字宽度只需获取一次即可
            getTextWidth();
            isMeasure = true;
        }
    }

    public void getTextWidth() {
        Paint paint = this.getPaint();
        String str = this.getText().toString();
        textWidth = (int)paint.measureText(str);
    }

    public void startScroll(int repeatCount){
        isStop = false;
        getTextWidth();
        xCreateAnimator = ObjectAnimator.ofFloat(this, "translationX", BaseTools.getWindowWidth(getContext()), - (textWidth + 100));
        xCreateAnimator.setDuration(15000L);
        xCreateAnimator.setRepeatCount(repeatCount);
        xCreateAnimator.setInterpolator(new LinearInterpolator());
        xCreateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mOnMarqueeListener != null){
                    mOnMarqueeListener.onStart();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnMarqueeListener != null) {
                    mOnMarqueeListener.onStop();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        xCreateAnimator.start();
    }

    public void stopScroll(){
//        xCreateAnimator.end();
    }

    //从头开始滚动
    public void stratFor0(int maxNum){
        startScroll(maxNum);
    }

    public void setOnMarqueeListener(OnMarqueeListener onMarqueeListener){
        this.mOnMarqueeListener = onMarqueeListener;
    }

    public interface OnMarqueeListener{
        public void onStart();
        public void onStop();
    }
}
