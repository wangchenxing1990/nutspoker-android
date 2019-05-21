package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.customview.SwipeItemLayout;
import com.netease.nim.uikit.interfaces.IClick;

/**
 * Created by 周智慧 on 17/1/15.
 * 通用viewholder，具有左滑删除功能，长按、点击功能、最左边具有删除按钮
 */

public class GameMgrViewHolder extends RecyclerView.ViewHolder {
    Activity mActivity;
    public SwipeItemLayout mRoot;
    public TextView mDelete;
    //头像区域
    View head_layout;
    ImageView btn_member_delete;
    HeadImageView contacts_item_head;
    ImageView iv_owner;
    //操作区域
    View ll_contact_action;
    TextView btn_contact_action_reject;
    TextView btn_contact_action_agree;
    TextView tv_club_myself;
    //用户名称区域
    TextView contacts_item_name;//用户nickname
    TextView contacts_item_desc;//站鱼id
    TextView user_type;//用户类型：创建者、管理员
    //箭头
    ImageView swipe_content_arrow;
    public GameMgrViewHolder(View itemView, Activity activity) {
        super(itemView);
        mActivity = activity;
        mRoot = (SwipeItemLayout) itemView.findViewById(R.id.item_contact_swipe_root);
        head_layout = itemView.findViewById(R.id.head_layout);
        btn_member_delete = (ImageView) itemView.findViewById(R.id.btn_member_delete);
        contacts_item_head = (HeadImageView) itemView.findViewById(R.id.contacts_item_head);
        iv_owner = (ImageView) itemView.findViewById(R.id.iv_owner);
        ll_contact_action = itemView.findViewById(R.id.ll_contact_action);
        btn_contact_action_reject = (TextView) itemView.findViewById(R.id.btn_contact_action_reject);
        btn_contact_action_agree = (TextView) itemView.findViewById(R.id.btn_contact_action_agree);
        tv_club_myself = (TextView) itemView.findViewById(R.id.tv_club_myself);
        contacts_item_name = (TextView) itemView.findViewById(R.id.contacts_item_name);//用户nickname
        contacts_item_desc = (TextView) itemView.findViewById(R.id.contacts_item_desc);//站鱼id
        user_type = (TextView) itemView.findViewById(R.id.user_type);
        mDelete = (TextView) itemView.findViewById(R.id.scrollable_view_remove_item);
        swipe_content_arrow = (ImageView) itemView.findViewById(R.id.swipe_content_arrow);
        //下面的四个组件的显示和数据无关，不需要放到bind()方法里面
        swipe_content_arrow.setVisibility(View.VISIBLE);
    }

    public void bind(UserEntity bean, final int position) {
        if (bean == null) {
            return;
        }
        contacts_item_head.loadBuddyAvatar(bean.account);
        contacts_item_name.setText(NimUserInfoCache.getInstance().getUserDisplayName(bean.account));
        contacts_item_desc.setText("ID: " + bean.uuid);
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
        itemView.findViewById(R.id.swipe_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });
        tv_club_myself.setVisibility(View.VISIBLE);
        //17/1/23 add by db GameMgrBean 增加channel字段
        tv_club_myself.setText("邀请码：" + bean.channel);
    }

    public IClick listener;
}
