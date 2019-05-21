package com.htgames.nutspoker.ui.recycler;

import android.app.Activity;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 16/11/23.
 */

public class MeRecycAdapter extends RecyclerView.Adapter<MeVH> {
    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int ITEM_TYPE_EMPTY_DIVIDER = 1;
    private ArrayList<MeItemData> mData = new ArrayList<>();
    private final LayoutInflater mLayoutInflater;
    private final Activity mContext;
    private MeVH.IShareInMe mShareInterface;
    public void setShareInterface(MeVH.IShareInMe shareInterface) {
        mShareInterface = shareInterface;
    }
    public MeRecycAdapter(Activity context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }
    @Override
    public MeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE_NORMAL) {
            view = mLayoutInflater.inflate(R.layout.me_recycler_item, parent, false);
        } else if (viewType == ITEM_TYPE_EMPTY_DIVIDER) {
            view = new Space(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(mContext, 10));
            view.setLayoutParams(params);//15dp的间隔
            android.support.v4.view.ViewCompat.setAlpha(view, 0);
        }
        MeVH viewHolder = new MeVH<MeItemData>(mContext, view);
        viewHolder.setShareInterface(mShareInterface);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MeVH holder, int position) {
        MeItemData itemData = getItem(position);
        holder.bind(position, itemData);
    }

    public MeItemData getItem(int position) {
        return (mData != null && position < mData.size()) ? mData.get(position) : null;
    }

    public void setData(List list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) == null ? ITEM_TYPE_EMPTY_DIVIDER : getItem(position).type;
    }

    public static class MeItemData implements Serializable {
        public MeItemData(int nameId, int drawableId, String url, int type) {
            this.nameId = nameId;
            this.drawableId = drawableId;
            this.url = url;
            this.type = type;
        }
        public int nameId;
        public int drawableId;
        public String url;//跳转链接
        public int type;//item类型
    }
}
