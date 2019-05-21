package com.htgames.nutspoker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.htgames.nutspoker.R;

/**
 * Created by glp on 2016/8/1.
 */

public class CircleMatchView extends View {

    int mInColor;
    int mMiddleColor;
    int mOutColor;
    int mMiddleRingColor;
    int mOutRingColor;
    int mCircleSpace;
    int mCirclePaintWidth;

    float mMiddleMax = 100.0f;
    float mMiddleCur = 0.0f;
    float mOutMax = 100.0f;
    float mOutCur = 0.0f;

    boolean mOutCircleVisible = true;

    Paint mInPaint;
    Paint mMiddlePaint;
    Paint mOutPaint;
    Paint mMiddleRingPaint;
    Paint mOutRingPaint;

    public CircleMatchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        onAttrs(context,attrs);
    }

    void onAttrs(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleMatchView, 0, 0);
        try {
            mInColor = a.getColor(R.styleable.CircleMatchView_in_color, Color.BLACK);
            mMiddleColor = a.getColor(R.styleable.CircleMatchView_middle_color,Color.BLACK);
            mOutColor = a.getColor(R.styleable.CircleMatchView_out_color,Color.BLACK);
            mMiddleRingColor = a.getColor(R.styleable.CircleMatchView_middle_ring_color,Color.GREEN);
            mOutRingColor = a.getColor(R.styleable.CircleMatchView_out_ring_color,Color.YELLOW);
            mCircleSpace = a.getDimensionPixelOffset(R.styleable.CircleMatchView_circle_space, 3);
            mCirclePaintWidth = a.getDimensionPixelOffset(R.styleable.CircleMatchView_circle_paint_width, 3);

            mMiddleMax = a.getFloat(R.styleable.CircleMatchView_middle_max,100.0f);
            mMiddleCur = a.getFloat(R.styleable.CircleMatchView_middle_cur,100.0f);
            mOutMax = a.getFloat(R.styleable.CircleMatchView_out_max,100.0f);
            mOutCur = a.getFloat(R.styleable.CircleMatchView_out_cur,100.0f);

            mOutCircleVisible = a.getBoolean(R.styleable.CircleMatchView_out_circle_visible,true);
        } finally {
            a.recycle();
        }

        mInPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInPaint.setColor(mInColor);
        mInPaint.setStyle(Paint.Style.FILL);
        mInPaint.setAntiAlias(true);// 消除锯齿

        mMiddlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddlePaint.setColor(mMiddleColor);
        mMiddlePaint.setStyle(Paint.Style.FILL);
        mMiddlePaint.setAntiAlias(true);// 消除锯齿

        mOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutPaint.setColor(mOutColor);
        mOutPaint.setStyle(Paint.Style.FILL);
        mOutPaint.setAntiAlias(true);// 消除锯齿

        mMiddleRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleRingPaint.setColor(mMiddleRingColor);
        mMiddleRingPaint.setStrokeWidth(mCirclePaintWidth);
        mMiddleRingPaint.setStyle(Paint.Style.STROKE);
        mMiddleRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mMiddleRingPaint.setAntiAlias(true);// 消除锯齿

        mOutRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutRingPaint.setColor(mOutRingColor);
        mOutRingPaint.setStrokeWidth(mCirclePaintWidth);
        mOutRingPaint.setStyle(Paint.Style.STROKE);
        mOutRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mOutRingPaint.setAntiAlias(true);// 消除锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF outRect = new RectF(getLeft()+mCirclePaintWidth,getTop()+mCirclePaintWidth,getRight()-mCirclePaintWidth,getBottom()-mCirclePaintWidth);

        float centerX = getRight()/2;
        float centerY = getBottom()/2;
        float radius = centerX-mCirclePaintWidth/2;

        float max;
        if(mOutCircleVisible){
            max = mOutMax==0?1:mOutMax;
            canvas.drawCircle(centerX,centerY,radius,mOutPaint);
            canvas.drawArc(outRect,90,mOutCur/max*360,false,mOutRingPaint);
        }

        float radius2 = radius-mCircleSpace;
        if(radius2 > 0){
            max = mMiddleMax==0?1:mMiddleMax;
            RectF middleRect = new RectF(outRect.left+mCircleSpace,outRect.top+mCircleSpace,outRect.right-mCircleSpace,outRect.bottom-mCircleSpace);
            canvas.drawCircle(centerX,centerY,radius2,mMiddlePaint);
            canvas.drawArc(middleRect,90,mMiddleCur/max*360,false,mMiddleRingPaint);
        }

        float radius3 = radius2-mCircleSpace;
        if(radius3 > 0){
            canvas.drawCircle(centerX,centerY,radius3,mInPaint);
        }
    }

    public void setMiddleMax(float middleMax) {
        mMiddleMax = middleMax;
        invalidate();
    }

    public void setMiddleCur(float middleCur) {
        mMiddleCur = middleCur;
        invalidate();
    }

    public void setOutMax(float outMax) {
        mOutMax = outMax;
        invalidate();
    }

    public void setOutCur(float outCur) {
        mOutCur = outCur;
        invalidate();
    }

    public void setOutCircleVisible(boolean outCircleVisible) {
        mOutCircleVisible = outCircleVisible;
        invalidate();
    }
}
