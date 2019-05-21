package com.htgames.nutspoker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.customview.SwipeItemLayout;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Created by 周智慧 on 17/1/15.
 * https://android.jlelse.eu/fast-scrolling-with-recyclerview-2b89d4574688 为recyclerview右侧增加快速滑动的组件
 */

public class TouchableRecyclerView extends RecyclerView {
    public static int LAYOUT_MANAGER_LINEAR = 0;
    public static int LAYOUT_MANAGER_GRID = 1;

    private Context mContext;
    int Slop;

    public TouchableRecyclerView(Context context) {
        this(context, null);
    }

    public TouchableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initEvent(context, attrs, defStyle);
    }

    private void initEvent(Context context, AttributeSet attrs, int defStyle) {
        mContext = context;
        Slop = ViewConfiguration.get(mContext).getScaledEdgeSlop();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.merecyclerview, defStyle, 0);
        final int layoutManagerType = ta.getInteger(R.styleable.merecyclerview_layout_manager_type, LAYOUT_MANAGER_LINEAR);//0LinearLayoutManager   1GridLayoutManager
        final int spanCount = (int) ta.getInteger(R.styleable.merecyclerview_grid_span_count, 3);
        final int DECORATION_SPACE_THIN = (int) ta.getDimension(R.styleable.merecyclerview_dividerHeight, 1.5f);//ScreenUtil.dp2px(getContext(), 1);
        setItemAnimator(new DefaultItemAnimator());
        getItemAnimator().setChangeDuration(0);//解决闪屏问题：http://www.jianshu.com/p/654dac931667
        ((SimpleItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
        RecycledViewPool pool = getRecycledViewPool();
        if (pool != null) {
            for (int i = 0; i < 40; i++) {
                pool.setMaxRecycledViews(i, 10);
            }
        }
        //下面设置layoutmanager
        if (layoutManagerType == LAYOUT_MANAGER_LINEAR) {
            final SmoothScrollLinearLayoutManager layoutManager = new SmoothScrollLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            setLayoutManager(layoutManager);
            ItemDecoration decorationThin = new ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                        outRect.set(0, 0, 0, DECORATION_SPACE_THIN);
                    } else {
                        outRect.set(0, 0, DECORATION_SPACE_THIN, 0);
                    }
                }
            };
            addItemDecoration(decorationThin);
        } else if (layoutManagerType == LAYOUT_MANAGER_GRID) {
            setLayoutManager(new GridLayoutManager(getContext(), spanCount, GridLayoutManager.VERTICAL, false));
            ItemDecoration decorationThin = new ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    outRect.set(DECORATION_SPACE_THIN, DECORATION_SPACE_THIN, DECORATION_SPACE_THIN, DECORATION_SPACE_THIN);
                }
            };
            addItemDecoration(decorationThin);
        }
        setHasFixedSize(true);
    }


    /**
     * 判断 当前手势触摸的距离是否为拖动的最小距离
     *
     * @param e
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int dx = 0;
        int dy = 0;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = (int) e.getX();
                dy = (int) e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int tempX = (int) e.getX();
                int tempY = (int) e.getY();
                if (Math.abs(dx - tempX) > Slop && Math.abs(tempY - dy) > Slop) {
                    closeAllOpenedItem();
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    public void closeAllOpenedItem() {
        if (getAdapter() instanceof IHandleSwipeItemLayoutWithAnim)
            ((IHandleSwipeItemLayoutWithAnim) getAdapter()).closeOpenedSwipeItemLayoutWithAnim();
    }

    private int getOrientation(RecyclerView parent) {
        LinearLayoutManager layoutManager;
        try {
            layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        } catch (ClassCastException e) {
            throw new IllegalStateException("DividerDecoration can only be used with a " + "LinearLayoutManager.", e);
        }
        return layoutManager.getOrientation();
    }



    public interface IHandleSwipeItemLayoutWithAnim {
        public void closeOpenedSwipeItemLayoutWithAnim();
        public void openSwipeItemLayoutWithAnim(SwipeItemLayout swipeRoot);
    }
}
