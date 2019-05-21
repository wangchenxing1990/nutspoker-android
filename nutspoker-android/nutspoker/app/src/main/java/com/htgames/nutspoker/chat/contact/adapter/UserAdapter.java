package com.htgames.nutspoker.chat.contact.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.PhoneContactTools;
import com.htgames.nutspoker.chat.contact.bean.PhoneContactEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;

/**
 */
public class UserAdapter extends ListBaseAdapter<NimUserInfo> {
    private Activity mActivity;
    int showCountLimit = 3;
    boolean isShowLimit = false;//是否有只显示3个限制
    boolean isShowStatus = true;//是否显示状态和按钮

    public UserAdapter(Activity context, ArrayList<NimUserInfo> list) {
        super(context, list);
        mActivity = context;
    }

    public UserAdapter(Activity context, ArrayList<NimUserInfo> list , boolean isShowLimit) {
        super(context, list);
        mActivity = context;
        this.isShowLimit = isShowLimit;
    }

    public UserAdapter(Context context, ArrayList<NimUserInfo> list , boolean isShowLimit , boolean isShowStatus) {
        super(context, list);
        this.isShowLimit = isShowLimit;
        this.isShowStatus = isShowStatus;
    }

    @Override
    public int getCount() {
        if(super.getCount() > 3 && isShowLimit){
            return showCountLimit;
        }
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_phonecontact_item, null);
            holder.ll_contact = (LinearLayout) view.findViewById(R.id.ll_contact);
            holder.contacts_item_head = (HeadImageView) view.findViewById(R.id.contacts_item_head);
            holder.btn_contact_action = (TextView) view.findViewById(R.id.btn_contact_action);
            holder.tv_contact_name = (TextView) view.findViewById(R.id.contacts_item_name);
            holder.tv_contact_desc = (TextView) view.findViewById(R.id.contacts_item_desc);
            holder.tv_club_myself = (TextView) view.findViewById(R.id.tv_club_myself);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        NimUserInfo userInfo = list.get(position);
        if(!TextUtils.isEmpty(userInfo.getName())){
//            holder.tv_contact_name.setText(userInfo.getName());
            setKeywordText(holder.tv_contact_name , userInfo.getName());
        } else{
            holder.tv_contact_name.setText(R.string.user);
        }
        holder.contacts_item_head.loadBuddyAvatar(userInfo.getAccount());

        if(isShowStatus){
            PhoneContactEntity phoneContactEntity = PhoneContactTools.getPhoneContact(context , userInfo.getAccount());
            if(phoneContactEntity != null){
                holder.tv_contact_desc.setVisibility(View.VISIBLE);
                holder.tv_contact_desc.setText(String.format(context.getString(R.string.contact_from_phone_search), phoneContactEntity.getName()));
//            setKeywordText(holder.tv_contact_desc , String.format(context.getString(R.string.contact_from_phone_search), phoneContactEntity.getName()));
            }else{
//            if(!TextUtils.isEmpty(userInfo.getSignature())){
//                holder.tv_contact_desc.setVisibility(View.VISIBLE);
//                holder.tv_contact_desc.setText(userInfo.getSignature());
//            } else{
//                holder.tv_contact_desc.setVisibility(View.GONE);
//            }
                holder.tv_contact_desc.setVisibility(View.GONE);
            }
            OnPhoneContactClick mOnPhoneContactClick = new OnPhoneContactClick(position);
            holder.ll_contact.setOnClickListener(mOnPhoneContactClick);
//            //判断是不是好友
//            if(NIMClient.getService(FriendService.class).isMyFriend(userInfo.getAccount())){
//                holder.btn_contact_action.setVisibility(View.VISIBLE);
//                holder.btn_contact_action.setOnClickListener(null);
//                holder.btn_contact_action.setBackgroundResource(android.R.color.transparent);
//                holder.btn_contact_action.setText(R.string.added);
//            } else{
//                holder.btn_contact_action.setVisibility(View.VISIBLE);
//                holder.btn_contact_action.setBackgroundResource(R.drawable.btn_friend_add);
//                holder.btn_contact_action.setText(context.getString(R.string.friend_action_add));
//                holder.btn_contact_action.setOnClickListener(mOnPhoneContactClick);
//            }
            //判断是不是好友改为判断是不是已经发送过俱乐部加入邀请
            holder.btn_contact_action.setVisibility(View.VISIBLE);
        }else{
            holder.btn_contact_action.setVisibility(View.GONE);
            holder.tv_contact_desc.setVisibility(View.GONE);
        }
        if (DemoCache.getAccount().equals(userInfo.getAccount())) {
            holder.btn_contact_action.setVisibility(View.GONE);
            holder.tv_club_myself.setVisibility(View.VISIBLE);
        } else {
            holder.btn_contact_action.setVisibility(View.VISIBLE);
            holder.tv_club_myself.setVisibility(View.GONE);
        }
        return view;
    }

    private class OnPhoneContactClick implements View.OnClickListener{
        private int position;

        public OnPhoneContactClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            NimUserInfo userinfo = (NimUserInfo) getItem(position);
            if (v.getId() == R.id.ll_contact) {
            } else if (v.getId() == R.id.btn_contact_action) {
//                if(context instanceof Activity){
//                    AddVerifyActivity.start((Activity) context, userinfo.getAccount(), AddVerifyActivity.TYPE_VERIFY_FRIEND);
//                }添加好友改为邀请加入俱乐部
//                if (mActivity instanceof SearchActivity) {
//                    ArrayList<String> inviteAccounts = new ArrayList<String>();
//                    inviteAccounts.add(userinfo.getAccount());
//                    ((SearchActivity) mActivity).inviteMembers(inviteAccounts);
//                }
            }
        }
    }

    protected final class ViewHolder {
        private TextView tv_club_myself;
        private LinearLayout ll_contact;
        private HeadImageView contacts_item_head;
        public TextView btn_contact_action;
        public TextView tv_contact_name;
        public TextView tv_contact_desc;
    }
}
