package com.htgames.nutspoker.ui.adapter.channel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameMgrBean;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by 周智慧 on 17/3/7.
 */

public class ChannelItemVH extends RecyclerView.ViewHolder {
    public SwipeItemLayout mRoot;
    public TextView mDelete;
    //头像区域
    ImageView btn_member_delete;
    HeadImageView contacts_item_head;
    //操作区域
    TextView tv_club_myself;
    //用户名称区域
    TextView contacts_item_name;//用户nickname
    TextView contacts_item_desc;//站鱼id
    TextView user_type;//用户类型：创建者、管理员
    //箭头
    ImageView club_head_image_mask;

    public ChannelItemVH(View itemView) {
        super(itemView);
        mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
        btn_member_delete = (ImageView) itemView.findViewById(R.id.btn_member_delete);
        contacts_item_head = (HeadImageView) itemView.findViewById(R.id.contacts_item_head);
        tv_club_myself = (TextView) itemView.findViewById(R.id.tv_club_myself);
        contacts_item_name = (TextView) itemView.findViewById(R.id.contacts_item_name);//用户nickname
        contacts_item_desc = (TextView) itemView.findViewById(R.id.contacts_item_desc);//站鱼id
        user_type = (TextView) itemView.findViewById(R.id.user_type);
        mDelete = (TextView) itemView.findViewById(R.id.scrollable_view_remove_item);
        club_head_image_mask = (ImageView) itemView.findViewById(R.id.club_head_image_mask);
    }

    public void bind(final GameMgrBean bean, final int position, boolean off, boolean canSwipe) {
        if (bean == null) {
            return;
        }
        mDelete.setText(bean.block == 0 ? R.string.disable_checkin : R.string.enable_checkin);
        mDelete.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(bean.block == 0 ? R.color.red_normal : R.color.match_checkin_normal));
        btn_member_delete.setVisibility(bean.block == 0 ? View.GONE : View.VISIBLE);
        itemView.findViewById(R.id.swipe_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
        if (canSwipe) {
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDelete(position);
                    }
                }
            });
            itemView.findViewById(R.id.swipe_content).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        listener.onLongClick(position);
                    }
                    return true;
                }
            });
        }
        if (bean.channelType == ChannelType.personal) {
            club_head_image_mask.setVisibility(View.GONE);
            contacts_item_head.loadBuddyAvatar(bean.uid);
            contacts_item_name.setText(NimUserInfoCache.getInstance().getUserDisplayName(bean.uid));
            contacts_item_desc.setText("ID: " + bean.uuid);
            tv_club_myself.setText("邀请码：" + bean.channel);
        } else if (bean.channelType == ChannelType.club) {
            club_head_image_mask.setVisibility(View.VISIBLE);
            String teamId = bean.tid;
            tv_club_myself.setText("");
            if (StringUtil.isSpace(teamId)) {
                return;
            }
            final Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null) {
                String clubName = team.getName();
                String extServer = team.getExtServer();
                contacts_item_name.setText(clubName);
                contacts_item_desc.setText("ID: " + ClubConstant.getClubVirtualId(team.getId(), extServer));
                contacts_item_head.loadClubAvatarByUrl(team.getId(), ClubConstant.getClubExtAvatar(extServer), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            } else {
                TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success && result != null) {
                            contacts_item_name.setText(result.getName());
                            contacts_item_desc.setText("ID: " + ClubConstant.getClubVirtualId(result.getId(), result.getExtServer()));
                            contacts_item_head.loadClubAvatarByUrl(result.getId(), ClubConstant.getClubExtAvatar(result.getExtServer()), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                        } else {
                        }
                    }
                });
            }
        } else if (bean.channelType == ChannelType.creator) {
            if (bean.channel.length() == 6) {
                tv_club_myself.setText("邀请码：" + bean.channel);
            } else {
                tv_club_myself.setText("");
            }
            club_head_image_mask.setVisibility(View.GONE);
            contacts_item_head.loadBuddyAvatar(bean.uid);
            contacts_item_name.setText(NimUserInfoCache.getInstance().getUserDisplayName(bean.uid));
            contacts_item_desc.setText("ID: " + bean.uuid);
        }
        if (off) {
            tv_club_myself.setText(bean.players + "人");
        }
    }

    public IClick listener;


}
