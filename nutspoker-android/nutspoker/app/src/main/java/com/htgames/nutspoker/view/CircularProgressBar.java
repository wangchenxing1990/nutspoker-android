package com.htgames.nutspoker.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.htgames.nutspoker.R;

/**
 * 环形进度
 */
public class CircularProgressBar extends View {
    // Properties
    private float progress = 0;
    private float secondProgress = 0;
    private float strokeWidth = getResources().getDimension(R.dimen.default_stroke_width);
    private float backgroundStrokeWidth = getResources().getDimension(R.dimen.default_background_stroke_width);
    private int color = Color.BLACK;
    private int secondColor = Color.BLACK;
    private int backgroundColor = Color.GRAY;

    // Object used to draw
    private int startAngle = 130;//开始的角度
    private int endAngle = 280;//开始的角度
    private int emptyAngle = 80;//空着的部分的角度
    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;//第一条进度条
    private Paint secondForegroundPaint;//第二条进度条

    //region Constructor & Init Method
    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, 0, 0);
        //Reading values from the XML layout
        try {
            // Value
            progress = typedArray.getFloat(R.styleable.CircularProgressBar_progress, progress);
            // StrokeWidth
            strokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_progressbar_width, strokeWidth);
            backgroundStrokeWidth = typedArray.getDimension(R.styleable.CircularProgressBar_background_progressbar_width, backgroundStrokeWidth);
            // Color
            color = typedArray.getInt(R.styleable.CircularProgressBar_progressbar_color, color);
            backgroundColor = typedArray.getInt(R.styleable.CircularProgressBar_background_progressbar_color, backgroundColor);
        } finally {
            typedArray.recycle();
        }

        // Init Background
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);//设置我们画笔的 笔触风格  1.ROUND 画成圆角
        backgroundPaint.setAntiAlias(true);// 消除锯齿

        // Init Foreground
        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);//置画笔样式 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充 3.Paint.Style.FILL：填充
        foregroundPaint.setStrokeWidth(strokeWidth);//当画笔样式（style）为STROKE或FILL_OR_STROKE时(空心样式时)，设置笔刷的粗细度。
        foregroundPaint.setStrokeCap(Paint.Cap.ROUND);//设置我们画笔的 笔触风格  1.ROUND 画成圆角
        foregroundPaint.setAntiAlias(true);// 消除锯齿

        // Init Foreground
        secondForegroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        secondForegroundPaint.setColor(secondColor);
        secondForegroundPaint.setStyle(Paint.Style.STROKE);//置画笔样式 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充 3.Paint.Style.FILL：填充
        secondForegroundPaint.setStrokeWidth(strokeWidth);//当画笔样式（style）为STROKE或FILL_OR_STROKE时(空心样式时)，设置笔刷的粗细度。
        secondForegroundPaint.setStrokeCap(Paint.Cap.ROUND);//设置我们画笔的 笔触风格  1.ROUND 画成圆角
        secondForegroundPaint.setAntiAlias(true);// 消除锯齿
    }
    //endregion

    //region Draw Method
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawOval(rectF, backgroundPaint);//椭圆形
        canvas.drawArc(rectF, startAngle, endAngle, false, backgroundPaint);//椭圆形
        float angle = (360 - emptyAngle) * progress / 100;//扫描的角度
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);//画弧度
        float secondAngle = (360 - emptyAngle) * secondProgress / 100;//扫描的角度
        canvas.drawArc(rectF, startAngle, secondAngle, false, secondForegroundPaint);//画弧度

        //canvas.drawCircle(rectF.centerX(),rectF.centerY(),rectF.width()/2-mInCircleSpace,mInCirclePaint);
    }
    //endregion

    //region Mesure Method
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量大小
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        float highStroke = (strokeWidth > backgroundStrokeWidth) ? strokeWidth : backgroundStrokeWidth;
        rectF.set(0 + highStroke / 2, 0 + highStroke / 2, min - highStroke / 2, min - highStroke / 2);
    }
    //endregion

    //region Method Get/Set
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = (progress <= 100) ? progress : 100;
        invalidate();
    }

    //region Method Get/Set
    public float getSecondProgress() {
        return secondProgress;
    }

    public void setSencondProgress(float progress) {
        this.secondProgress = (progress <= 100) ? progress : 100;
        invalidate();
    }

    public float getProgressBarWidth() {
        return strokeWidth;
    }

    public void setProgressBarWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public float getBackgroundProgressBarWidth() {
        return backgroundStrokeWidth;
    }

    public void setBackgroundProgressBarWidth(float backgroundStrokeWidth) {
        this.backgroundStrokeWidth = backgroundStrokeWidth;
        backgroundPaint.setStrokeWidth(backgroundStrokeWidth);
        requestLayout();//Because it should recalculate its bounds
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        foregroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public void setSecondColor(int color) {
        this.secondColor = color;
        secondForegroundPaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        invalidate();
        requestLayout();
    }
    //endregion

    //region Other Method

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircularProgressBar#setProgress(float)} directly within this method.
     * @param progress The progress it should animate to it.
     */
    public void setProgressWithAnimation(float progress) {
        setProgressWithAnimation(progress, 1500);
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircularProgressBar#setProgress(float)} directly within this method.
     * @param progress The progress it should animate to it.
     * @param duration The length of the animation, in milliseconds.
     */
    public void setProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }

    /**
     * Set the progress with an animation.
     * Note that the {@link ObjectAnimator} Class automatically set the progress
     * so don't call the {@link CircularProgressBar#setSencondProgress(float)} directly within this method.
     *
     * @param progress The progress it should animate to it.
     * @param duration The length of the animation, in milliseconds.
     */
    public void setSecondProgressWithAnimation(float progress, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float currentValue = (float)animation.getAnimatedValue();
                setSencondProgress(currentValue);
            }
        });
        objectAnimator.start();
    }
    //endregion
}
