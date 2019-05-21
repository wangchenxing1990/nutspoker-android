package com.htgames.nutspoker.ui.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.View;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;

/**
 * Created by 周智慧 on 16/11/23.
 */

public class MeRecyclerView extends RecyclerView {
    private Context mContext;
    public MeRecyclerView(Context context) {
        this(context, null);
    }
    public MeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(context, attrs, defStyle);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyle) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.merecyclerview, defStyle, 0);
        final int DECORATION_SPACE_THIN = (int) ta.getDimension(R.styleable.merecyclerview_dividerHeight, 1);//ScreenUtil.dp2px(getContext(), 1);
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
        setItemAnimator(new DefaultItemAnimator());
        getItemAnimator().setChangeDuration(0);//解决闪屏问题：http://www.jianshu.com/p/654dac931667
        ((SimpleItemAnimator)getItemAnimator()).setSupportsChangeAnimations(false);
        RecycledViewPool pool = getRecycledViewPool();
        if (pool != null) {
            for (int i = 0; i < 40; i++) {
                pool.setMaxRecycledViews(i, 10);
            }
        }
        setHasFixedSize(true);
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
}
