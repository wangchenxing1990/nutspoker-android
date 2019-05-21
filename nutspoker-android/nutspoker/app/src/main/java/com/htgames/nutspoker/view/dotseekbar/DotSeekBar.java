package com.htgames.nutspoker.view.dotseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.htgames.nutspoker.R;


/**
 * Created by glp on 2016/6/28.
 */

public class DotSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    public boolean draggable = true;//是否禁止拖动
    public boolean shouldDrawDot = true;//是否绘制圆点
    //结点数量
    int mDotCount;
    //进度条左侧绘制
    //进度条左侧绘制
    public Drawable mDrawDotLeft;
    //进度条右侧绘制
    public Drawable mDrawDotRight;
    //步长
    int mStep = 0;
    //结点Y坐标
    int mDotY;
    //结点宽度 px
    int mDotWidth;
    //结点高度 px
    int mDotHeight;
    //是否只能落在结点
    boolean mDotOnly;
    //是否精确一点。 默认不精确，有利于用户点击
    boolean mDotExact;
    int mLastProgress;
    public final static String Tag = "DotSeekBar";
    public DotSeekBar(Context context) {
        super(context);
    }
    public DotSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        onDotAttr(context,attrs,0);
    }
    public DotSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onDotAttr(context,attrs,defStyleAttr);
    }

    void onDotAttr(Context context,AttributeSet attrs,int defStyleAttr) {
        setProgress(0);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DotSeekBar,defStyleAttr, 0);
        try {
            mDotCount = a.getInteger(R.styleable.DotSeekBar_dotCount, 0);
            mDrawDotLeft = a.getDrawable(R.styleable.DotSeekBar_dotLeft);
            mDrawDotRight = a.getDrawable(R.styleable.DotSeekBar_dotRight);
            mDotWidth = a.getDimensionPixelOffset(R.styleable.DotSeekBar_dotWidth, 10);
            mDotHeight = a.getDimensionPixelOffset(R.styleable.DotSeekBar_dotHeight, 10);
            mDotOnly = a.getBoolean(R.styleable.DotSeekBar_dotOnly, true);
            mDotExact = a.getBoolean(R.styleable.DotSeekBar_dotExact, false);
        } finally {
            a.recycle();
        }
        mStep = getMax() / (mDotCount-1);
        mLastProgress = getProgress();
        super.setOnSeekBarChangeListener(this);
    }

    public void setDotCount(int count){
        mDotCount = count;
        mStep = getMax() / (mDotCount - 1 <= 0 ? 1 : mDotCount - 1);
        invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        draggable = enabled;
        super.setEnabled(enabled);
        invalidate();
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
        mStep = getMax() / (mDotCount-1);
        invalidate();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //Log.e(Tag,"onMeasure widthMeasureSpec="+widthMeasureSpec+",heightMeasureSpec="+heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //Log.e(Tag,"onLayout changed="+(changed?"true":"false")+";left="+left+",top="+top+",right="+right+",bottom="+bottom);
        if(changed) {
            mDotY = (bottom + top) / 2 - top;
        }
    }

//    @Override
//    public synchronized void setProgress(int progress) {
//        if(mStep == 0 || !mDotOnly)
//            super.setProgress(progress);
//        else {
//            int diff;
//            if(progress >= getProgress()) {
//                if(!mDotExact && progress-mStep/2 <= getProgress() || mDotExact && progress-mStep < getProgress())
//                    return ;
//                diff = progress - getProgress()+mStep/2;
//            } else {
//                if(!mDotExact && progress+mStep/2 >= getProgress() || mDotExact && progress+mStep > getProgress())
//                    return ;
//                diff = getProgress() - progress+mStep/2;
//                diff = -diff;
//            }
//            diff = diff/mStep*mStep;
//            super.setProgress(getProgress() + diff);
//        }
//    }

    OnSeekBarChangeListener mChildListener;
    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        //super.setOnSeekBarChangeListener(l);
        mChildListener = l;
    }

    /***
     * 重写绘制函数
     * @param canvas
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //描绘结点
        if (mDotCount > 0) {
            int width = getRight() - getLeft() - getPaddingLeft() - getPaddingRight();
            int splitCount = mDotCount - 1;
            int step = width / splitCount;//结点步长
            float curPercent = getProgress() * 1.0f / getMax();//当前进度
            for (int i = 0; i < mDotCount; i++) {
                float temp = (i * 1.0f / splitCount);
                Drawable drawable = curPercent >= temp ? mDrawDotLeft : mDrawDotRight;
                if (shouldDrawDot) {
                    drawDrawable(drawable, canvas, getPaddingLeft() + step * i - mDotWidth / 2, mDotY - mDotHeight / 2);
                }
            }
        }
        super.onDraw(canvas);
    }

    void drawDrawable(Drawable d,Canvas canvas){
        drawDrawable(d,canvas,0,0);
    }

    void drawDrawable(Drawable d,Canvas canvas,int left){
        drawDrawable(d,canvas,left,0);
    }

    void drawDrawable(Drawable d, Canvas canvas, int left, int top) {
        if (d == null || canvas == null)
            return;
        d.setBounds(left, top, left + mDotWidth, top + mDotHeight);
        //d.getPadding();
        //d.setState();
        //d.setLevel();
        d.draw(canvas);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mStep == 0 || !mDotOnly || progress % mStep == 0) {
            if (mChildListener != null) {
                mChildListener.onProgressChanged(seekBar, progress, fromUser);
            }
        } else {
            //mLastProgress = getProgress();
            int diff;
            if (progress >= mLastProgress) {
                if (!mDotExact && progress - mStep / 2 <= mLastProgress || mDotExact && progress - mStep < mLastProgress) {
                    super.setProgress(mLastProgress);
                    return;
                }
                diff = progress - mLastProgress + mStep / 2;
            } else {
                if (!mDotExact && progress + mStep / 2 >= mLastProgress || mDotExact && progress + mStep > mLastProgress) {
                    super.setProgress(mLastProgress);
                    return;
                }
                diff = mLastProgress - progress + mStep / 2;
                diff = -diff;
            }
            diff = diff / mStep * mStep;
            mLastProgress = mLastProgress + diff;
            super.setProgress(mLastProgress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(mChildListener != null){
            mChildListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(mChildListener != null){
            mChildListener.onStopTrackingTouch(seekBar);
        }
    }

    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar 里
     * 你可以看下他们的继承关系
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //原来是要将TouchEvent传递下去的,我们不让它传递下去就行了
        return draggable ? super.onTouchEvent(event) : false;
    }
}
