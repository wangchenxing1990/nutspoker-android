package com.htgames.nutspoker.ui.adapter.channel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 周智慧 on 17/3/7.
 */

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TouchableRecyclerView.IHandleSwipeItemLayoutWithAnim {
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();
    public LinkedHashMap<ChannelSection, ArrayList<GameMgrBean>> mSectionDataMap = new LinkedHashMap<ChannelSection, ArrayList<GameMgrBean>>();
    public ArrayList<Object> mDataArrayList = new ArrayList<Object>();
    public final Context mContext;
    public static final int VIEW_TYPE_SECTION = 0;
    public static final int VIEW_TYPE_ITEM = 1;
    public boolean mSwipeable = true;
    public GameEntity gameInfo;
    public boolean off;//是channellistoff还是channellist
    public ChannelAdapter(Context context, boolean off) {
        mContext = context;
        this.off = off;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SECTION) {
            return new ChannelSectionVH(LayoutInflater.from(mContext).inflate(R.layout.layout_channel_section, parent, false));
        } else if (viewType == VIEW_TYPE_ITEM) {
            return new ChannelItemVH(LayoutInflater.from(mContext).inflate(R.layout.item_channel_list, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelItemVH && mDataArrayList.size() > position && mDataArrayList.get(position) instanceof GameMgrBean) {
            GameMgrBean bean = (GameMgrBean) mDataArrayList.get(position);
            boolean canSwipe = true;
            if ((!StringUtil.isSpace(bean.tid) && gameInfo.tid.equals(bean.tid)) || (!StringUtil.isSpace(bean.uid) && gameInfo.creatorInfo.account.equals(bean.uid))) {
                canSwipe = false;
            }
            if (!mSwipeable) {
                canSwipe = mSwipeable;
            }
            ((ChannelItemVH) holder).bind(bean, position, off, canSwipe);
            SwipeItemLayout swipeRoot = ((ChannelItemVH) holder).mRoot;
            swipeRoot.setSwipeAble(canSwipe);
            swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                @Override
                public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                    mOpenedSil.add(swipeItemLayout);
                }
                @Override
                public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
//                    swipeItemLayout.clearAnimation();
                    mOpenedSil.remove(swipeItemLayout);
                }
                @Override
                public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                }
            });
            if (mContext instanceof IClick) {
                ((ChannelItemVH) holder).listener = (IClick) mContext;
            }
        } else if (holder instanceof ChannelSectionVH && mDataArrayList.size() > position && mDataArrayList.get(position) instanceof ChannelSection) {
            ((ChannelSectionVH) holder).bind((ChannelSection) mDataArrayList.get(position));
        }
    }

    public void notifyItemRemovedCustom(SwipeItemLayout swipeItemLayout) {
        if (swipeItemLayout == null) {
            return;
        }
        swipeItemLayout.close();
        mOpenedSil.remove(swipeItemLayout);
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
        return mDataArrayList == null ? 0 : mDataArrayList.size();
    }

    public void addSection(ChannelSection channelSection, ArrayList<GameMgrBean> list) {
        mSectionDataMap.put(channelSection, list);
    }

    public void datasetChanged() {
        mDataArrayList.clear();
        for (Map.Entry<ChannelSection, ArrayList<GameMgrBean>> entry : mSectionDataMap.entrySet()) {
            if (entry.getValue().size() > 0) {
                mDataArrayList.add(entry.getKey());
            }
            if (entry.getKey().isExpanded) {
                mDataArrayList.addAll(entry.getValue());
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isSection(position)) {
            return VIEW_TYPE_SECTION;
        } else return VIEW_TYPE_ITEM;
    }

    private boolean isSection(int position) {
        return mDataArrayList.get(position) instanceof ChannelSection;
    }

    public boolean containsSection(Object key) {
        return mSectionDataMap.containsKey(key);
    }

    public Object getItem(int position) {
        if (mDataArrayList.size() > position) {
            return mDataArrayList.get(position);
        }
        return null;
    }
}
