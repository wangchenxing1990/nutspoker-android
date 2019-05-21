package com.htgames.nutspoker.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 17/3/29.   https://issuetracker.google.com/issues/37007621
 */

public class ClubListView extends FrameLayout {
    Drawable bottomDrawable;
    Point p0 = new Point();
    Point p1 = new Point();
    Point p2 = new Point();
    Point p3 = new Point();
    Point p4 = new Point();
    Point p5 = new Point();
    int clubListHeight;
    public ClubListView(@NonNull Context context) {
        this(context, null);
    }

    public ClubListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClubListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClubListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ClubListView);
        clubListHeight = ta.getDimensionPixelOffset(R.styleable.ClubListView_ClubListView_clubListHeight, ScreenUtil.dp2px(getContext(), 110));
        bottomDrawable = getContext().getResources().getDrawable(R.drawable.bg_club_game_bottom);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getContext().getResources().getColor(R.color.bg_club_bessel));
        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBessel(canvas);//绘制贝塞尔曲线
//        drawTriangle(canvas);//绘制直角三角形
    }

    Paint mPaint;
    Path mPath;
    int stepControl = 2;
    int stepCenter = 2;
    private boolean isInc;// 判断控制点是该右移还是左移
    private boolean hasInit;
    private void drawBessel(Canvas canvas) {
        if (!hasInit) {
            hasInit = true;
            p0.set(0, clubListHeight - ScreenUtil.dp2px(getContext(), 30));//最左边
            p2.set(getWidth() / 2, clubListHeight / 2);//中间
            p4.set(getWidth(), ScreenUtil.dp2px(getContext(), 30));//最右边

            p1.set((p0.x + p2.x) / 2, (p0.y + p2.y) / 2 - ScreenUtil.dp2px(getContext(), 20));
            p3.set((p2.x + p4.x) / 2, (p2.y + p4.y) / 2 + ScreenUtil.dp2px(getContext(), 20));
            p5.set(getWidth(), p0.y);
        }

        mPath.moveTo(p0.x, p0.y);
        mPath.quadTo(p1.x, p1.y, p2.x, p2.y);
        mPath.quadTo(p3.x, p3.y, p4.x, p4.y);
        mPath.lineTo(p5.x, p5.y);
        mPath.lineTo(p0.x, p0.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);



        //产生波浪左右涌动的感觉
        if (p1.x >= getWidth() / 2 - 1) {//控制点坐标大于等于终点坐标改标识
            isInc = false;
        } else if (p1.x <= 0 + 1) {//控制点坐标小于等于起点坐标改标识
            isInc = true;
        }
        int commandXAhead = isInc ? p1.x + stepControl : p1.x - stepControl;
        int commandXMiddle = isInc ? p2.x + stepCenter : p2.x - stepCenter;
        int commandXBehind = isInc ? p3.x + stepControl : p3.x - stepControl;
        p1.set(commandXAhead, p1.y);
        p2.set(commandXMiddle, p2.y);
        p3.set(commandXBehind, p3.y);
        int p0X = p1.x - getWidth() / 4 >= 0 ? 0 : p1.x - getWidth() / 4;
        p0.set(p0X, p0.y);
        int p4X = p3.x + getWidth() / 4 <= getWidth() ? getWidth() : p3.x + getWidth() / 4;
        p4.set(p4X, p4.y);
        //水位不断加高  当距离控件顶端还有1/8的高度时，不再上升
//        if (commandY >= 1 / 8f * viewHeight) {
//            commandY -= 2;
//            waterHeight -= 2;
//        }
        //路径重置
        mPath.reset();
        // 重绘
        invalidate();
    }

    private void drawTriangle(Canvas canvas) {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getContext().getResources().getColor(R.color.bg_club_game_bottom));
        p0.set(0, clubListHeight - ScreenUtil.dp2px(getContext(), 30));//最左边
        p4.set(getWidth(), ScreenUtil.dp2px(getContext(), 30));//最右边
        p5.set(getWidth(), p0.y);

        Path mPath = new Path();
        mPath.moveTo(p0.x, p0.y);
        mPath.moveTo(p4.x, p4.y);
        mPath.moveTo(p5.x, p5.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }
}
