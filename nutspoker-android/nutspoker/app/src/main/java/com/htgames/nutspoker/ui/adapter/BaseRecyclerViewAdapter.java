package com.htgames.nutspoker.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView.Adapter基类
 */
public class BaseRecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;//ITEM类型：头部
    private static final int TYPE_FOOTER = 2;//ITEM类型：尾部
    /** 头部界面*/
    private View mHeaderView;
    /** 底部界面*/
    private View mFooterView;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
