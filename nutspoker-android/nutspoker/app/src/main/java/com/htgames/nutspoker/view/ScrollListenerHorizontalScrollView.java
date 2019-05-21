package com.htgames.nutspoker.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * 带监听的HorizontalScrollView
 */
public class ScrollListenerHorizontalScrollView extends HorizontalScrollView {
    public ScrollListenerHorizontalScrollView(Context context) {
        super(context);
    }

    public ScrollListenerHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollListenerHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface ScrollViewListener {
        void onScrollTypeChanged(ScrollType scrollType);

        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private Handler mHandler;
    private ScrollViewListener scrollViewListener;

    /**
     * 滚动状态   IDLE 滚动停止  TOUCH_SCROLL 手指拖动滚动         FLING滚动
     */
    public enum ScrollType {
        IDLE, TOUCH_SCROLL, FLING
    }

    /**
     * 记录当前滚动的距离
     */
    private int currentX = -9999999;
    /**
     * 当前滚动状态
     */
    private ScrollType scrollType = ScrollType.IDLE;
    /**
     * 滚动监听间隔
     */
    private int scrollDealy = 50;
    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (getScrollX() == currentX) {
                //滚动停止  取消监听线程
                LogUtil.i("", "停止滚动");
                scrollType = ScrollType.IDLE;
                if (scrollViewListener != null) {
                    scrollViewListener.onScrollTypeChanged(scrollType);
                }
                mHandler.removeCallbacks(this);
                return;
            } else {
                //手指离开屏幕    view还在滚动的时候
                LogUtil.i("", "Fling。。。。。");
                scrollType = ScrollType.FLING;
                if (scrollViewListener != null) {
                    scrollViewListener.onScrollTypeChanged(scrollType);
                }
            }
            currentX = getScrollX();
            mHandler.postDelayed(this, scrollDealy);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                this.scrollType = ScrollType.TOUCH_SCROLL;
                if(scrollViewListener != null) {
                    scrollViewListener.onScrollTypeChanged(scrollType);
                }
                //手指在上面移动的时候   取消滚动监听线程
                if(mHandler != null){
                    mHandler.removeCallbacks(scrollRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                //手指移动的时候
                if (mHandler != null) {
                    mHandler.post(scrollRunnable);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 必须先调用这个方法设置Handler  不然会出错
     */
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 设置滚动监听
     */
    public void setOnScrollStateChangedListener(ScrollViewListener listener) {
        this.scrollViewListener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollViewListener != null){
            scrollViewListener.onScrollChanged(l, t, oldl, oldt);
        }
        if(secondScrollView != null){
            secondScrollView.scrollTo(l , t);
        }
    }

    /**
     * 联动ScrollListenerHorizontalScrollView
     */
    ScrollListenerHorizontalScrollView secondScrollView;

    public void setScrollView(ScrollListenerHorizontalScrollView view){
        this.secondScrollView = view;
    }
}
