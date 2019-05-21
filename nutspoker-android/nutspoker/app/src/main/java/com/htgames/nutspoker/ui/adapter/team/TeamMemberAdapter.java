package com.htgames.nutspoker.ui.adapter.team;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.bean.TeamMemberItem;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;

import java.util.ArrayList;

/**
 * 群成员适配器
 */
public class TeamMemberAdapter extends ListBaseAdapter<TeamMemberItem>{
    public TeamTypeEnum teamType;
    float showAlpha = 0.4f;
    float normalAlpha = 1f;

    public TeamMemberAdapter(Context context, ArrayList<TeamMemberItem> list , TeamTypeEnum type ,RemoveMemberCallback removeMemberCallback, AddMemberCallback addMemberCallback , TeamMemberHolderEventListener mTeamMemberHolderEventListener) {
        super(context, list);
        this.removeMemberCallback = removeMemberCallback;
        this.addMemberCallback = addMemberCallback;
        this.mTeamMemberHolderEventListener = mTeamMemberHolderEventListener;
        this.teamType = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_team_member_item, null);
            holder.frameLayoutHead = (RelativeLayout) view.findViewById(R.id.frameLayoutHead);
            holder.headImageView = (HeadImageView) view.findViewById(R.id.imageViewHeader);
            holder.nameTextView = (TextView) view.findViewById(R.id.textViewName);
            holder.ownerImageView = (ImageView) view.findViewById(R.id.imageViewOwner);
            holder.adminImageView = (ImageView) view.findViewById(R.id.imageViewAdmin);
            holder.deleteImageView = (ImageView) view.findViewById(R.id.imageViewDeleteTag);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TeamMemberItem  memberItem = (TeamMemberItem) getItem(position);
        holder.headImageView.resetImageView();
        ViewGroup.LayoutParams layoutParams = holder.frameLayoutHead.getLayoutParams();
        if(teamType == TeamTypeEnum.Normal){
            layoutParams.width = BaseTools.dip2px(context , 50);
            layoutParams.height = BaseTools.dip2px(context , 50);
        }else{
            layoutParams.width = BaseTools.dip2px(context , 50);
            layoutParams.height = BaseTools.dip2px(context , 50);
        }
        holder.frameLayoutHead.setLayoutParams(layoutParams);
        //
        holder.ownerImageView.setVisibility(View.GONE);
        holder.adminImageView.setVisibility(View.GONE);
        holder.deleteImageView.setVisibility(View.GONE);
        holder.headImageView.setAlpha(normalAlpha);
        if (getMode() == TeamMemberAdapter.Mode.NORMAL) {
            view.setVisibility(View.VISIBLE);
            if (memberItem.tag == TeamMemberItem.TeamMemberItemTag.ADD) {
                // add team member
                holder.headImageView.setBackgroundResource(R.drawable.team_member_add_selector);
                holder.nameTextView.setText(R.string.add);
//                holder.nameTextView.setText(context.getString(R.string.add));
                holder.headImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getAddMemberCallback().onAddMember();
                    }
                });
            } else if (memberItem.tag == TeamMemberItem.TeamMemberItemTag.DELETE) {
                // delete team member
                holder.headImageView.setBackgroundResource(R.drawable.team_member_delete_selector);
                holder.nameTextView.setText(R.string.delete);
//                holder.nameTextView.setText(context.getString(R.string.remove));
                holder.headImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setMode(TeamMemberAdapter.Mode.DELETE);
                        notifyDataSetChanged();
                    }
                });
            } else {
                // show team member
                refreshTeamMember(holder, memberItem, false);
            }
        } else if (getMode() == TeamMemberAdapter.Mode.DELETE) {
            if (memberItem.tag == TeamMemberItem.TeamMemberItemTag.NORMAL) {
                refreshTeamMember(holder, memberItem, true);
            } else {
//                view.setVisibility(View.GONE);
                //
                if (memberItem.tag == TeamMemberItem.TeamMemberItemTag.ADD) {
                    holder.headImageView.setAlpha(showAlpha);
                    holder.nameTextView.setText(R.string.add);
                    holder.headImageView.setBackgroundResource(R.drawable.team_member_add_selector);
                    holder. headImageView.setOnClickListener(null);
                } else if (memberItem.tag == TeamMemberItem.TeamMemberItemTag.DELETE) {
                    holder.headImageView.setAlpha(normalAlpha);
                    holder.nameTextView.setText(R.string.finish);
                    holder.headImageView.setBackgroundResource(R.drawable.team_member_finish_selector);
                    holder.headImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setMode(Mode.NORMAL);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        }
        if(teamType == TeamTypeEnum.Normal){
            holder.nameTextView.setVisibility(View.VISIBLE);
        } else{
            holder.nameTextView.setVisibility(View.GONE);
        }
        return view;
    }

    private void refreshTeamMember(ViewHolder holder , final TeamMemberItem item, final boolean deleteMode) {
        holder.nameTextView.setText(TeamDataCache.getInstance().getTeamMemberDisplayName(item.tid, item.account));
        if(teamType == TeamTypeEnum.Normal){
            holder.headImageView.loadBuddyAvatar(item.account);
        }else{
            holder.headImageView.loadBuddyAvatar(item.account);
        }
        holder.headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteMode && !isSelf(item.account)) {
                    getRemoveMemberCallback().onRemoveMember(item.account);
                }else{
                    if(mTeamMemberHolderEventListener != null){
                        mTeamMemberHolderEventListener.onHeadImageViewClick(item.account);
                    }
                }
            }
        });

        if (item.desc != null) {
            if (item.desc.equals(TeamMemberItem.OWNER)) {
                holder.ownerImageView.setVisibility(View.VISIBLE);
            } else if (item.desc.equals(TeamMemberItem.ADMIN)) {
                holder.adminImageView.setVisibility(View.VISIBLE);
            }
        }

        final String account = item.account;
        if (deleteMode && !isSelf(account)) {
            holder.deleteImageView.setVisibility(View.VISIBLE);
            holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRemoveMemberCallback().onRemoveMember(account);
                }
            });
        } else {
            holder.deleteImageView.setVisibility(View.GONE);
        }
    }

    private boolean isSelf(String account) {
        return account.equals(DemoCache.getAccount());
    }

    protected final class ViewHolder {
        public RelativeLayout frameLayoutHead;
        public HeadImageView headImageView;
        public TextView nameTextView;
        public ImageView ownerImageView;
        public ImageView adminImageView;
        public ImageView deleteImageView;
    }

    private Mode mode = Mode.NORMAL;

    private RemoveMemberCallback removeMemberCallback;

    private AddMemberCallback addMemberCallback;

    TeamMemberHolderEventListener mTeamMemberHolderEventListener;

    /**
     * 当前GridView显示模式：显示讨论组成员，正在移除讨论组成员
     */
    public static enum Mode {
        NORMAL,
        DELETE
    }

    /**
     * 群成员移除回调函数
     */
    public static interface RemoveMemberCallback {
        public void onRemoveMember(String account);
    }

    public static interface AddMemberCallback {
        public void onAddMember();
    }

    public static interface TeamMemberHolderEventListener{
        public void onHeadImageViewClick(String account);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean switchMode() {
        if (getMode() == Mode.DELETE) {
            setMode(Mode.NORMAL);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public RemoveMemberCallback getRemoveMemberCallback() {
        return removeMemberCallback;
    }

    public AddMemberCallback getAddMemberCallback() {
        return addMemberCallback;
    }

}
