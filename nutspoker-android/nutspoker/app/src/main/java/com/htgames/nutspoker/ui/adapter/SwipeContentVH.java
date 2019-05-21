package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.interfaces.IClick;

/**
 * Created by 周智慧 on 17/1/20.
 */

public class SwipeContentVH extends RecyclerView.ViewHolder {
    Activity mActivity;
    //头像区域
    public View head_layout;
    public ImageView btn_member_delete;
    public HeadImageView contacts_item_head;
    public ImageView iv_owner;
    //操作区域
    public View ll_contact_action;
    public TextView btn_contact_action_reject;
    public TextView btn_contact_action_agree;
    public TextView tv_club_myself;
    //用户名称区域
    public TextView contacts_item_name;//用户nickname
    public TextView contacts_item_desc;//站鱼id
    public TextView user_type;//用户类型：创建者、管理员
    //箭头
    public ImageView swipe_content_arrow;
    public SwipeContentVH(View itemView, Activity activity) {
        super(itemView);
        mActivity = activity;
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
        swipe_content_arrow = (ImageView) itemView.findViewById(R.id.swipe_content_arrow);
    }

    public void bind(Object bean, final int position) {

    }

    public IClick listener;
}
