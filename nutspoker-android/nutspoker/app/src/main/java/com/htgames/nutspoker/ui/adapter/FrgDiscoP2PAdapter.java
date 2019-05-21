package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.view.AppMessageRecentView;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.view.GroupHeadView;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 周智慧 on 16/12/5.
 */

public class FrgDiscoP2PAdapter extends RecyclerView.Adapter<FrgDiscoP2PAdapter.P2PVH> {
    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag
    //不是自己发的消息，自己发的消息也能收到
    private Activity mActivity;
    public FrgDiscoP2PAdapter(Activity activity) {
        mActivity = activity;
    }

    @Override
    public P2PVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.nim_recent_contact_list_item, null);
        return new P2PVH(mActivity, view, this);
    }

    @Override
    public void onBindViewHolder(P2PVH holder, int position) {
        RecentContact recent = getItem(position);
        holder.bind(recent);
    }

    @Override
    public int getItemCount() {
        return ChessApp.unreadP2PMsg.size();
    }

    public RecentContact getItem(int position) {
        int i = 0;
        for (Map.Entry entry : ChessApp.unreadP2PMsg.entrySet()) {
            if (i == position) {
                return (RecentContact) entry.getValue();
            }
            i++;
        }
        return null;
    }



    public static class P2PVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private Activity VHActivity;
        //代码来自AppMessageRecentView.java
        protected FrameLayout portraitPanel;
        protected HeadImageView imgHead;
        protected TextView tvNickname;
        protected TextView tvMessage;
        protected TextView tvUnread;
        protected View unreadIndicator;
        protected TextView tvDatetime;
        // 消息发送错误状态标记，目前没有逻辑处理
        protected ImageView imgMsgStatus;
        //快速游戏的状态
        protected ImageView iv_recent_new_notify;
        protected ImageView imgGameStatus;
        protected ImageView iv_icon_game_quick;
        private RelativeLayout rl_club_head;
        protected HeadImageView iv_club_head;
        TextView tv_game_count;
        ImageView iv_mute;
        //圈子头像
        GroupHeadView mGroupHeadView;
        boolean isSelectMode = false;
        LinearLayout ll_recent_nickname;
        RelativeLayout rl_recent_message;
        RecentContact data;
        FrgDiscoP2PAdapter adapter;
        public P2PVH(Activity activity, View view, FrgDiscoP2PAdapter adapter) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.adapter = adapter;
            VHActivity = activity;
            this.portraitPanel = (FrameLayout) view.findViewById(com.netease.nim.uikit.R.id.portrait_panel);
            this.imgHead = (HeadImageView) view.findViewById(com.netease.nim.uikit.R.id.img_head);
            this.tvNickname = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_nickname);
            this.tvMessage = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_message);
            this.tvUnread = (TextView) view.findViewById(com.netease.nim.uikit.R.id.unread_number_tip);
            this.unreadIndicator = view.findViewById(com.netease.nim.uikit.R.id.new_message_indicator);
            this.tvDatetime = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_date_time);
            this.imgMsgStatus = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.img_msg_status);
            //
            this.rl_club_head = (RelativeLayout) view.findViewById(com.netease.nim.uikit.R.id.rl_club_head);
            this.iv_club_head = (HeadImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_club_head);
            this.iv_recent_new_notify = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_recent_new_notify);//消息免打扰的小红点
            this.imgGameStatus = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_game_status);//
            this.iv_icon_game_quick = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_icon_game_quick);//
            this.tv_game_count = (TextView) view.findViewById(com.netease.nim.uikit.R.id.tv_game_count);//
            this.iv_mute = (ImageView) view.findViewById(com.netease.nim.uikit.R.id.iv_mute);//
            this.mGroupHeadView = (GroupHeadView) view.findViewById(com.netease.nim.uikit.R.id.mGroupHeadView);//
            this.ll_recent_nickname = (LinearLayout) view.findViewById(com.netease.nim.uikit.R.id.ll_recent_nickname);//
            this.rl_recent_message = (RelativeLayout) view.findViewById(com.netease.nim.uikit.R.id.rl_recent_message);//
            //
            this.unreadIndicator.setVisibility(View.GONE);
            this.imgMsgStatus.setVisibility(View.GONE);
            this.rl_club_head.setVisibility(View.GONE);
            this.iv_recent_new_notify.setVisibility(View.GONE);
            this.imgGameStatus.setVisibility(View.GONE);
            this.iv_icon_game_quick.setVisibility(View.GONE);
            this.tv_game_count.setVisibility(View.GONE);
            this.iv_mute.setVisibility(View.GONE);
            this.mGroupHeadView.setVisibility(View.GONE);
        }

        public void bind(final RecentContact recent) {
            if (recent == null) {
                return;
            }
            data = recent;
            this.imgHead.loadBuddyAvatar(recent.getContactId());//设置头像
            this.tvNickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(recent.getContactId()));//recent.getFromNick()   NimUserInfoCache.getInstance().getUserDisplayName(recent.getFromAccount())
            List<String> accounts = new ArrayList<>();
            accounts.add(recent.getContactId());
//            NimUserInfoCache.getInstance().getUserListByNeteaseEx(accounts, 0, new NimUserInfoCache.IFetchCallback() {
//                @Override
//                public void onFetchFinish(@Nullable List<? extends Object> list) {
//                    if (tvNickname != null && recent != null) {
//                        tvNickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(recent.getContactId()));
//                    }
//                }
//            });
            this.tvMessage.setText(recent.getContent());
            this.tvDatetime.setText(DateTools.getDailyTime_ymd(recent.getTime()));
            int unreadNum = recent.getUnreadCount();
            this.tvUnread.setVisibility(unreadNum > 0 ? View.VISIBLE : View.GONE);
            iv_recent_new_notify.setVisibility(unreadNum == 2147483647 ? View.VISIBLE : View.GONE);
            this.tvUnread.setText(AppMessageRecentView.unreadCountShowRule(unreadNum));
            if (unreadNum > 99) {
                tvUnread.setText(com.netease.nim.uikit.R.string.new_message_count_max);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {//根布局
                if (data != null) {
                    SessionHelper.startP2PSession(VHActivity, data.getContactId(), data.getUnreadCount());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == itemView) {//根布局
                showLongClickMenu(data);
            }
            return true;
        }

        private void showLongClickMenu(final RecentContact recent) {
            CustomAlertDialog alertDialog = new CustomAlertDialog(VHActivity);
            alertDialog.setTitle(UserInfoHelper.getUserTitleName(recent.getFromAccount(), recent.getSessionType()));
            String title = VHActivity.getResources().getString(com.netease.nim.uikit.R.string.main_msg_list_delete_chatting);
            alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
                @Override
                public void onClick() {
                    // 删除会话，删除后，消息历史被一起删除
                    NIMClient.getService(MsgService.class).deleteRecentContact(recent);
                    NIMClient.getService(MsgService.class).clearChattingHistory(recent.getContactId(), recent.getSessionType());
                    Iterator iterator = ChessApp.unreadP2PMsg.keySet().iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().equals(recent.getContactId())) {
                            iterator.remove();
                            break;
                        }
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            title = (isTagSet(recent, RECENT_TAG_STICKY) ? VHActivity.getResources().getString(com.netease.nim.uikit.R.string.main_msg_list_clear_sticky_on_top) : VHActivity.getResources().getString(com.netease.nim.uikit.R.string.main_msg_list_sticky_on_top));
//            alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
//                @Override
//                public void onClick() {
//                    if (isTagSet(recent, RECENT_TAG_STICKY)) {
//                        removeTag(recent, RECENT_TAG_STICKY);
//                    } else {
//                        addTag(recent, RECENT_TAG_STICKY);
//                    }
//                    NIMClient.getService(MsgService.class).updateRecent(recent);
//                }
//            });
            alertDialog.show();
        }

        private boolean isTagSet(RecentContact recent, long tag) {
            return (recent.getTag() & tag) == tag;
        }

        private void addTag(RecentContact recent, long tag) {
            tag = recent.getTag() | tag;
            recent.setTag(tag);
        }

        private void removeTag(RecentContact recent, long tag) {
            tag = recent.getTag() & ~tag;
            recent.setTag(tag);
        }
    }
}
