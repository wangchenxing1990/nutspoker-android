package com.htgames.nutspoker.ui.fragment.main;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by fuwu on 16/4/25.
 */
public class BannerViewPager extends ViewPager {
    private static final String TAG = BannerViewPager.class.getSimpleName();
    private GestureDetector mGestureDetector;
    private boolean slidingEnabled = true;
    public DiscoveryBannerIndicator customIndicator;
    private DiscoveryBannerAdapter mAdapter;
    public int currentBannerIndex = 0;
    private Handler autoNextHandler;

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(getContext(), new ScrollDetector());
    }

    public BannerViewPager(Context context) {
        super(context);
    }

    public boolean isSlidingEnabled() {
        return slidingEnabled;
    }

    public void setSlidingEnabled(boolean slidingEnabled) {
        this.slidingEnabled = slidingEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = false;
        //viewpager support包问题，这个先catch
        try {
            result = super.onTouchEvent(ev);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mIsBeingTouched = true;
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mIsBeingTouched = false;
        }
        mGestureDetector.onTouchEvent(ev);
        // 告诉父view，我的单击事件我自行处理，不要阻碍我。
        getParent().requestDisallowInterceptTouchEvent(doMyself);
        return super.dispatchTouchEvent(ev);
    }

    boolean doMyself = true;

    //当前手指是否在该控件上，如果是要暂停自动滚动
    private boolean mIsBeingTouched;

    public boolean isBeingTouched() {
        return mIsBeingTouched;
    }

    class ScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                doMyself = true;
            } else {
                doMyself = false;
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        if (adapter instanceof DiscoveryBannerAdapter) {
            mAdapter = (DiscoveryBannerAdapter) adapter;
        }
        super.setAdapter(adapter);
        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                customIndicator.onPageScrolled(i % mAdapter.mData.size(), v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                if (autoNextHandler != null) {
                    autoNextHandler.removeMessages(0);
                    autoNextHandler.sendEmptyMessageDelayed(0, DiscoveryFragment.INTERVAL);
                }
                currentBannerIndex = i;
                customIndicator.onPageSelected(i % mAdapter.mData.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                customIndicator.onPageScrollStateChanged(i % mAdapter.mData.size());
            }
        });
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public void customIndicator(DiscoveryBannerIndicator customIndicator) {
        this.customIndicator = customIndicator;
    }

    @Override
    public void setCurrentItem(int i) {
        super.setCurrentItem(i);
        currentBannerIndex = i;
        if (this.customIndicator != null) {
            this.customIndicator.onPageSelected(i % (mAdapter == null || mAdapter.mData == null ? 1 : mAdapter.mData.size()));
        }
    }

    public void setCurrentItem(int i, boolean smoothScroll) {
        super.setCurrentItem(i, smoothScroll);
        currentBannerIndex = i;
        if (this.customIndicator != null) {
            this.customIndicator.onPageSelected(i % (mAdapter == null || mAdapter.mData == null ? 1 : mAdapter.mData.size()));
        }
    }

    public void setAutoNextHandler(Handler handler) {
        autoNextHandler = handler;
    }
}
