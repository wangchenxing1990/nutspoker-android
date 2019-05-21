package com.htgames.nutspoker.ui.adapter.clubmember;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.htgames.nutspoker.view.TouchableRecyclerView;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.fastscroller.FastScroller;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;


/**
 * Created by 周智慧 on 2017/5/22.
 */

public class ClubMemberAdapNew extends FlexibleAdapter<AbstractFlexibleItem> implements TouchableRecyclerView.IHandleSwipeItemLayoutWithAnim, FastScroller.BubbleTextCreator {
    private Activity mActivity;
    public boolean isShowRemove = false;
    public boolean isMeCreator = false;
    public boolean isSelfAdmin = false;
    public IClick listener;
    public int mMaxNum = 0;
    public boolean showArrow = false;
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();
    public ClubMemberAdapNew(@Nullable List items) {
        super(items);
    }

    public ClubMemberAdapNew(@Nullable List items, @Nullable Object listeners) {
        super(items, listeners);
        if (listeners instanceof Activity) {
            mActivity = (Activity) listeners;
        }
        if (listeners instanceof IClick) {
            listener = (IClick) listeners;
        }
    }

    public ClubMemberAdapNew(@Nullable List items, @Nullable Object listeners, boolean stableIds) {
        super(items, listeners, stableIds);
        if (listeners instanceof Activity) {
            mActivity = (Activity) listeners;
        }
        if (listeners instanceof IClick) {
            listener = (IClick) listeners;
        }
    }

    public void showRemoveMode(boolean show){
        isShowRemove = show;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
        AbstractFlexibleItem item = getItem(position);
        if (!(item instanceof  ClubMemberItem)) {
            return;
        }
        TeamMember teamMember = ((ClubMemberItem) item).mTeamMember;
        if (teamMember == null) {
            return;
        }
        ((ClubMemberItem.SimpleViewHolder) holder).btn_member_delete.setVisibility(isShowRemove ? View.VISIBLE : View.GONE);
        if(isShowRemove && !DemoCache.getAccount().equals(teamMember.getAccount())) {
            ((ClubMemberItem.SimpleViewHolder) holder).btn_member_delete.setVisibility(View.VISIBLE);
        } else {
            ((ClubMemberItem.SimpleViewHolder) holder).btn_member_delete.setVisibility(View.GONE);
        }
        SwipeItemLayout swipeRoot = (SwipeItemLayout) ((ClubMemberItem.SimpleViewHolder) holder).itemView;
        if (teamMember.getAccount().equals(DemoCache.getAccount())) {
            swipeRoot.setSwipeAble(false);
        } else {
            if (isMeCreator/* || isSelfAdmin*/) {//只有群主才能移除成员
                swipeRoot.setSwipeAble(true);
            } else {
                swipeRoot.setSwipeAble(false);
            }
        }
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
    }

    public void notifyItemRemovedCustom(SwipeItemLayout swipeItemLayout) {
        if (swipeItemLayout == null) {
            return;
        }
        swipeItemLayout.closeWithAnim();
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
    public String onCreateBubbleText(int position) {
        return super.onCreateBubbleText(position >= mMaxNum ? (mMaxNum - 1) : position);
    }
}
