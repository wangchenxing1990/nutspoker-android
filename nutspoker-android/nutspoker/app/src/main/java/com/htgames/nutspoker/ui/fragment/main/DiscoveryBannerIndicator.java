package com.htgames.nutspoker.ui.fragment.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 17/3/17.
 */

public class DiscoveryBannerIndicator extends View {
    private int mNumOfCircles = 0; // 静态圆点个数
    private int mScreenWidth;
    private float mHeight = ScreenUtil.dip2px(4); // 圆点半径
    private float mWidth = ScreenUtil.dip2px(12); // 圆点半径
    private float mRadius = ScreenUtil.dip2px(3); // 圆点半径
    private float mDistance = ScreenUtil.dip2px(12); // 静态圆点之间距离(边切线之间,不是圆心之间)
    private float[] mXPositions; // 静态圆点圆心X坐标
    private float[] mXCirclePositions; // 静态圆点圆心X坐标
    private float mYPositions = 0; // 静态圆点圆心Y坐标

    private float mLeftOrRightSpace; // 第一个静态圆点左边切线距离屏幕左端的长度
    private float mAllDotsDomainLength; // 所有静态圆点所占长度

    // 静态和动态圆点对应的画笔
    private Paint mPaintStroke;
    private int mCurrentIndex;

    public DiscoveryBannerIndicator(Context context) {
        this(context, (AttributeSet) null);
    }

    public DiscoveryBannerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initLengthRelatedVariables();
    }

    private void initPaint() {
        mPaintStroke = new Paint();
        mPaintStroke.setColor(Color.parseColor("#ffffff")); //纯白
        mPaintStroke.setAntiAlias(true);
    }

    private void initLengthRelatedVariables() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public void setNumOfCircles(int numOfCircles) {
        if (numOfCircles <= 1) {
            this.setVisibility(View.INVISIBLE);
            return;
        }
        this.setVisibility(View.VISIBLE);
        mNumOfCircles = numOfCircles;
        mAllDotsDomainLength = mRadius * 2 * mNumOfCircles + mDistance * (mNumOfCircles - 1);
        mLeftOrRightSpace = (mScreenWidth - mAllDotsDomainLength) / 2.0f;
        mXPositions = new float[mNumOfCircles];
        mXCirclePositions = new float[mNumOfCircles];
        for (int i = 0; i < mNumOfCircles; i++) {
            mXPositions[i] = mLeftOrRightSpace + (mDistance + mWidth) * i;
            mXCirclePositions[i] = mLeftOrRightSpace + (mDistance + mRadius * 2) * i;//
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStrokeCircle(canvas);
    }

    private void drawStrokeCircle(Canvas canvas) {
        if (mNumOfCircles <= 1) {
            return;
        }
        mYPositions = getHeight() / 2;
        for (int i = 0; i < mNumOfCircles; i++) {
            if (mCurrentIndex == i) {
                mPaintStroke.setAlpha(255);
            } else {
                mPaintStroke.setAlpha(175);
            }
//            canvas.drawRect(mXPositions[i], mYPositions, mXPositions[i] + mWidth, mYPositions + mHeight, mPaintStroke);
            canvas.drawCircle(mXCirclePositions[i], mYPositions, mRadius, mPaintStroke);
        }
    }

    public void onPageScrolled(int canSeePageIndex, float v, int i2) {
    }

    public void onPageSelected(int currentIndex) {
        mCurrentIndex = currentIndex;
        invalidate();
    }

    public void onPageScrollStateChanged(int i) {
    }
}
