package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 16/12/9.  代码太烂    需要整理
 */

public class JellyView extends FrameLayout {
    private OverScroller mScroller;
    private float lastX;
    private float lastY;
    private float startX;
    private float startY;
    private float screenWidth;
    private float startHideX;
    public JellyView(Context context) {
        this(context, null);
    }

    public JellyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        screenWidth = ScreenUtil.screenMin;
        startHideX = screenWidth / 3.5f;
        mScroller = new OverScroller(context);
    }

    private float moveDistance;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                lastY = event.getRawY();
                LogUtil.i("zzh", "ACTION_DOWN: " + getX());
                break;
            case MotionEvent.ACTION_MOVE:
                float disX = event.getRawX() - lastX;
                float disY = event.getRawY() - lastY;
                if (!(getX() <= 0 && disX <= 0)) {
                    offsetLeftAndRight((int) disX);
                } else {
                    offsetLeftAndRight(0);
                }
//                offsetTopAndBottom((int) disY);
                lastX = event.getRawX();
                lastY = event.getRawY();
                moveDistance += Math.abs(disX);
                LogUtil.i("zzh", "ACTION_MOVE: " + getX());
                break;
            case MotionEvent.ACTION_UP:
//                if (moveDistance <= 0 && mCodeView != null && !mCodeView.isFocusable()) {//判定是单击事件
//                    mCodeView.setFocusable(true);
//                    hide();
//                    break;//直接隐藏，后面的不需要再执行
//                }
                moveDistance = 0;
                if (getX() > startHideX) {
                    hide();
                } else {
                    show();
                }
                LogUtil.i("zzh", "ACTION_UP after invalidate: " + getX());
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        LogUtil.i("zzh", "computeScroll outer*******: " + getX());
        if (mScroller.computeScrollOffset()) {
            LogUtil.i("zzh", "computeScroll true: " + getX());
            if (mScroller.getCurrX() <= 0) {
                setX(0);
            } else {
                setX(mScroller.getCurrX());
            }
            setY(mScroller.getCurrY());
            invalidate();
        } else {
            LogUtil.i("zzh", "computeScroll false: " + getX());
            if (getX() < 0) {
                setX(0);
                invalidate();
            }
            setVisibility(mShow ? VISIBLE : INVISIBLE);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        LogUtil.i("zzh", "   " + w + "   " + h + "   " + oldw + "   " + oldh);
        startX = screenWidth;//getX();
        startY = getContext().getResources().getDimensionPixelOffset(R.dimen.action_bar_height);//getY();
        setX(startX);
        setY(startY);
    }

    public void spingBack() {
        if (mScroller.springBack((int) getX(), (int) getY(), 0, 0, 0, (int) getY() - 100)) {
            LogUtil.d("TAG", "getX()=" + getX() + "__getY()=" + getY());
            invalidate();
        }
    }

    private boolean mShow;
    public void showAndHide(boolean show) {
        mShow = show;
        if (show) {
            //出现动画
            show();
        } else {
            //隐藏动画
            hide();
        }
    }

    public void hide() {
        if (handleBackInterface != null) {
            handleBackInterface.handleBack(false);
        }
        mScroller.startScroll((int) getX(), (int) getY(), -(int) (getX() - startX), 0);
        mShow = false;
        invalidate();
    }

    public void show() {
        if (handleBackInterface != null) {
            handleBackInterface.handleBack(true);
        }
        setVisibility(VISIBLE);
        mScroller.startScroll((int) getX(), (int) getY(), -(int) (getX() - 0), 0);
        mShow = true;
        invalidate();
    }

    public void resetPosition() {
        if (getVisibility() == VISIBLE) {
            setX(0);
            invalidate();
        } else {
            setX(startX);
            invalidate();
        }
    }

    public IHandleBack handleBackInterface;
    public interface IHandleBack {
        void handleBack(boolean isCodeviewShow);
    }
}
