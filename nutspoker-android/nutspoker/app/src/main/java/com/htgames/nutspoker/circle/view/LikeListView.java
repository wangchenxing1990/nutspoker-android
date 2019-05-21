package com.htgames.nutspoker.circle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.htgames.nutspoker.circle.adapter.LikeListAdapter;
import com.htgames.nutspoker.circle.spannable.ISpanClick;

/**
 * 赞过的列表
 */
public class LikeListView extends TextView {
    public LikeListView(Context context) {
        super(context);
    }

    public LikeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LikeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ISpanClick mSpanClickListener;

    public void setSpanClickListener(ISpanClick listener) {
        mSpanClickListener = listener;
    }

    public ISpanClick getSpanClickListener() {
        return mSpanClickListener;
    }


    public void setAdapter(LikeListAdapter adapter) {
        adapter.bindListView(this);
    }
}
