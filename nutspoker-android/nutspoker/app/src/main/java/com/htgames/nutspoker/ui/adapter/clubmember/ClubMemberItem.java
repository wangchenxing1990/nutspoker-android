package com.htgames.nutspoker.ui.adapter.clubmember;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.helpers.AnimatorHelper;
import eu.davidea.flexibleadapter.items.AbstractItem;
import eu.davidea.flexibleadapter.items.IFilterable;
import eu.davidea.flexibleadapter.items.ISectionable;
import eu.davidea.flexibleadapter.utils.DrawableUtils;
import eu.davidea.flexibleadapter.utils.FlexibleUtils;
import eu.davidea.viewholders.FlexibleViewHolder;

/**
 * Created by 周智慧 on 2017/5/26.
 */

public class ClubMemberItem extends AbstractItem<ClubMemberItem.SimpleViewHolder>
        implements ISectionable<ClubMemberItem.SimpleViewHolder, ClubMemberHeader>, IFilterable, Serializable {
    /* The header of this item */
    public static int TYPE_MEMBER_AC = 0;//ClubMemberACNew.java页面
    public static int TYPE_SET_MANAGER_AC = 1;//ManagerSetActivity.java页面
    public int adapterType = TYPE_MEMBER_AC;
    @Nullable ClubMemberHeader header;
    public TeamMember mTeamMember;
    public String pinyin;
    public String sortKey;
    public String nickName;

    public ClubMemberItem(TeamMember tm) {
        super(tm == null ? "oewhgohroauwg" : tm.getAccount());
        mTeamMember = tm;
        nickName = NimUserInfoCache.getInstance().getUserDisplayName(mTeamMember == null ? "oewhgohroauwg" : mTeamMember.getAccount());//"我的"
        sortKey = (PinYin.getLeadingLo(nickName.trim())).toLowerCase();//"wd"
        pinyin = (PinYin.getPinYin(nickName.trim())).toLowerCase();//"wode"
        setDraggable(true);
        setSwipeable(true);
    }

    public ClubMemberItem(TeamMember tm, ClubMemberHeader header) {
        this(tm);
        this.header = header;
    }

    @Override
    public ClubMemberHeader getHeader() {
        return header;
    }

    @Override
    public void setHeader(ClubMemberHeader header) {
        this.header = header;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_club_member;
    }

    @Override
    public SimpleViewHolder createViewHolder(View view, FlexibleAdapter adapter) {
        SimpleViewHolder holder = new SimpleViewHolder(view, adapter);
        return holder;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void bindViewHolder(final FlexibleAdapter adapter, final SimpleViewHolder holder, final int position, List payloads) {
        if (mHidden || mTeamMember == null) {
            RecyclerView.LayoutParams rootLayoutParams = new RecyclerView.LayoutParams(1, 1);
            holder.itemView.setLayoutParams(rootLayoutParams);
            return;
        }
        if (mTeamMember == null) {
            return;
        }
        Context context = holder.itemView.getContext();
        RecyclerView.LayoutParams rootLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.club_mmeber_item_height));
        holder.itemView.setLayoutParams(rootLayoutParams);
        holder.listener = adapter instanceof ClubMemberAdapNew ? ((ClubMemberAdapNew) adapter).listener : null;
        holder.tv_arrow.setVisibility(adapter instanceof ClubMemberAdapNew && ((ClubMemberAdapNew) adapter).showArrow ? View.VISIBLE : View.GONE);
        // Background, when bound the first time
        if (payloads.size() == 0) {
            Drawable drawable = DrawableUtils.getSelectableBackgroundCompat(
                    Color.WHITE, Color.parseColor("#dddddd"), //Same color of divider
                    DrawableUtils.getColorControlHighlight(context));
            DrawableUtils.setBackgroundCompat(holder.itemView, drawable);
        }
        holder.contacts_item_head.loadBuddyAvatar(mTeamMember.getAccount());
        holder.contacts_item_name.setText(NimUserInfoCache.getInstance().getUserDisplayName(mTeamMember.getAccount()));
        holder.tv_club_myself.setVisibility(mTeamMember.getAccount().equals(DemoCache.getAccount()) ? View.VISIBLE : View.GONE);
        holder.iv_owner.setVisibility(View.GONE);
        holder.user_type.setVisibility(View.GONE);
        if(mTeamMember.getType() == TeamMemberType.Owner) {
            holder.iv_owner.setVisibility(View.VISIBLE);
            holder.iv_owner.setImageResource(R.mipmap.icon_team_owner);
            holder.user_type.setVisibility(View.VISIBLE);
            holder.user_type.setText(R.string.text_club_owner);
            holder.user_type.setBackgroundResource(R.drawable.text_bg_club_owner);
        } else if(mTeamMember.getType() == TeamMemberType.Manager) {
            holder.iv_owner.setVisibility(View.VISIBLE);
            holder.iv_owner.setImageResource(R.mipmap.club_manage_tag);
            holder.user_type.setVisibility(View.VISIBLE);
            holder.user_type.setBackgroundResource(R.drawable.text_bg_club_mgr);
            holder.user_type.setText(R.string.text_mgrman);
        }
        holder.itemView.findViewById(R.id.item_club_member_content).setOnClickListener(new View.OnClickListener() {//点击
            @Override
            public void onClick(View v) {
                if (holder.listener != null) {
                    holder.listener.onClick(position);
                }
            }
        });
        holder.itemView.findViewById(R.id.item_club_member_content).setOnLongClickListener(new View.OnLongClickListener() {//长按
            @Override
            public boolean onLongClick(View v) {
                if (holder.listener != null) {
                    holder.listener.onLongClick(position);
                }
                return true;
            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.listener != null) {
                    holder.listener.onDelete(position);
                }
            }
        });
        holder.btn_member_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.listener != null) {
                    holder.listener.onDelete(position);
                }
            }
        });
        if (adapterType == TYPE_SET_MANAGER_AC) {
            holder.iv_owner.setVisibility(View.GONE);
            holder.user_type.setVisibility(View.GONE);
        }
        // In case of searchText matches with Title or with a field this will be highlighted
        if (adapter.hasSearchText()) {
            FlexibleUtils.colorAccent = ChessApp.sAppContext.getResources().getColor(R.color.login_solid_color);
            FlexibleUtils.highlightText(holder.contacts_item_name, holder.contacts_item_name.getText().toString(), adapter.getSearchText());
        }
    }

    @Override
    public boolean filter(String constraint) {
        if (mTeamMember == null) {
            return false;
        }
        return getTitle() != null && getTitle().toLowerCase().trim().contains(constraint) ||
                nickName != null && nickName.toLowerCase().trim().contains(constraint);
    }

    static final class SimpleViewHolder extends FlexibleViewHolder {
        Context mContext;
        public IClick listener;
        public SwipeItemLayout mRoot;
        public TextView mDelete;
        ImageView iv_owner;
        HeadImageView contacts_item_head;
        //ImageView iv_owner;
        TextView tv_club_myself;
        TextView contacts_item_name;
        TextView tv_contact_desc;
        TextView user_type;
        View tv_arrow;
        ImageView btn_member_delete;

        public boolean swiped = false;

        SimpleViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            this.mContext = view.getContext();
            mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_club_member_root);
            mDelete = (TextView) itemView.findViewById(R.id.scrollable_view_remove_item);
            iv_owner = (ImageView) itemView.findViewById(R.id.iv_owner);
            user_type = (TextView) itemView.findViewById(R.id.user_type);
            contacts_item_head = (HeadImageView) itemView.findViewById(R.id.contacts_item_head);
            tv_club_myself = (TextView) itemView.findViewById(R.id.tv_club_myself);
            contacts_item_name = (TextView) itemView.findViewById(R.id.contacts_item_name);
            btn_member_delete = (ImageView) itemView.findViewById(R.id.btn_member_delete);
            tv_arrow = itemView.findViewById(R.id.tv_arrow);
//        itemView.findViewById(R.id.contacts_item_desc).setVisibility(View.GONE);
        }

        @Override
        protected void setDragHandleView(@NonNull View view) {
            if (mAdapter.isHandleDragEnabled()) {
                view.setVisibility(View.VISIBLE);
                super.setDragHandleView(view);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "Click on " + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            super.onClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(mContext, "LongClick on " + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
            return super.onLongClick(view);
        }

        @Override
        public void toggleActivation() {
            super.toggleActivation();
            // Here we use a custom Animation inside the ItemView
        }

        @Override
        public float getActivationElevation() {
            return ScreenUtil.dp2px(itemView.getContext(), 4f);
        }

        @Override
        protected boolean shouldActivateViewWhileSwiping() {
            return false;//default=false
        }

        @Override
        protected boolean shouldAddSelectionInActionMode() {
            return false;//default=false
        }

        @Override
        public void scrollAnimators(@NonNull List<Animator> animators, int position, boolean isForward) {
            if (mAdapter.getRecyclerView().getLayoutManager() instanceof GridLayoutManager ||
                    mAdapter.getRecyclerView().getLayoutManager() instanceof StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f);
            } else {
                //Linear layout
                if (isForward)
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.getRecyclerView());
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView());
            }
        }

        @Override
        public void onItemReleased(int position) {
            swiped = (mActionState == ItemTouchHelper.ACTION_STATE_SWIPE);
            super.onItemReleased(position);
        }
    }

    @Override
    public String toString() {
        return "ClubMemberItem[" + super.toString() + "]";
    }

    @Override
    public boolean equals(Object inObject) {
        if (inObject instanceof ClubMemberItem) {
            ClubMemberItem inItem = (ClubMemberItem) inObject;
            return this.getId().equals(inItem.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static Comparator<ClubMemberItem> comparator = new Comparator<ClubMemberItem>() {//AbstractFlexibleItem  ClubMemberItem
        @Override
        public int compare(ClubMemberItem o1, ClubMemberItem o2) {
            return o1.pinyin.compareTo(o2.pinyin);
//            return getAlpha(o1.pinyin).compareTo(o2.pinyin);
        }
    };

    /**
     * 获取首字母
     *
     * @param str
     * @return
     */
    public static String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式匹配
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 将小写字母转换为大写
        } else {
            return "#";
        }
    }
}
