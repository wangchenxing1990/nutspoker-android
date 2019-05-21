package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/1/15.
 */

public class GameMgrAdapter<T> extends RecyclerView.Adapter<GameMgrViewHolder> implements TouchableRecyclerView.IHandleSwipeItemLayoutWithAnim {
    /**
     * 当前处于打开状态的item
     */
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();
    private Activity mContext;
    ArrayList<T> mDatas = new ArrayList<T>();
    public GameMgrAdapter(Activity ct, ArrayList<T> datas) {
        mContext = ct;
        mDatas = datas;
    }

    public void setData(ArrayList<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public GameMgrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_common_adapter, parent, false);
        return new GameMgrViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(GameMgrViewHolder holder, final int position) {
        SwipeItemLayout swipeRoot = holder.mRoot;
        swipeRoot.setSwipeAble(true);
        swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
            @Override
            public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil.add(swipeItemLayout);
            }
            @Override
            public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                mOpenedSil.remove(swipeItemLayout);
            }
            @Override
            public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
            }
        });
        if (mDatas != null && mDatas.size() > position && mDatas.get(position) instanceof UserEntity) {
            UserEntity bean = (UserEntity) mDatas.get(position);
            if (mContext instanceof IClick) {
                holder.listener = (IClick) mContext;
            }
            holder.bind(bean, position);
        }
    }

    @Override
    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (SwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    @Override
    public void openSwipeItemLayoutWithAnim(SwipeItemLayout swipeRoot) {
        if (swipeRoot != null) {
            mOpenedSil.add(swipeRoot);
            swipeRoot.open();
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }
}
