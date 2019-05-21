package com.netease.nim.uikit.pulltorefresh;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 16/12/26.
 */

public class BottomLoadingView extends RelativeLayout {
    private ProgressBar mProgressBar;
    private TextView mStateTv;
    public BottomLoadingView(Context context) {
        this(context, null);
    }

    public BottomLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.parseColor("#ededf2"));
        mStateTv = new TextView(context);
        mStateTv.setText("上拉加载更多");
        mStateTv.setId(R.id.refresh_header_view_text_view);
        mStateTv.setTextColor(Color.LTGRAY);
        mStateTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtil.dp2px(context, 11));
        mStateTv.setGravity(Gravity.CENTER);
        mProgressBar = new ProgressBar(context);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.nim_progress_small_white));
        mProgressBar.setVisibility(GONE);
        RelativeLayout.LayoutParams mStateTvLp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ScreenUtil.dp2px(context, 22));
        mStateTvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mStateTvLp.leftMargin = ScreenUtil.dp2px(context, 5);
        RelativeLayout.LayoutParams mProgressImgLp = new RelativeLayout.LayoutParams(ScreenUtil.dp2px(context, 15), ScreenUtil.dp2px(context, 15));
        mProgressImgLp.addRule(RelativeLayout.LEFT_OF, mStateTv.getId());
        mProgressImgLp.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mStateTv, mStateTvLp);
        addView(mProgressBar, mProgressImgLp);
    }

    /**
     * 预加载状态
     */
    public void statePre() {
        setVisibility(VISIBLE);
        mStateTv.setText("上拉加载更多");
        mProgressBar.setVisibility(GONE);
    }

    /**
     * 加载状态
     */
    public void stateLoading() {
        setVisibility(VISIBLE);
        mStateTv.setText("加载中");
        mProgressBar.setVisibility(VISIBLE);
    }

    /**
     * 数据已经加载完状态
     */
    public void stateAllDataLoadDone(boolean isAllLoadFinish) {
        if (isAllLoadFinish) {
            setVisibility(VISIBLE);
            mStateTv.setText("没有更多数据了");
            mProgressBar.setVisibility(GONE);
        } else {
            mStateTv.setText("加载中");
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏掉
     */
    public void gone() {
        setVisibility(GONE);
    }
}
