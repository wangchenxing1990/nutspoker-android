package com.htgames.nutspoker.chat.contact.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiConstants;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.contact.bean.PhoneContactEntity;
import com.htgames.nutspoker.chat.contact.model.ContactRelation;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.ui.liv.LetterIndexView;
import com.netease.nim.uikit.common.ui.liv.LivIndex;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.friend.FriendService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * Created by 20150726 on 2015/10/9.
 */
public class PhoneContactAdapter extends ListBaseAdapter<PhoneContactEntity> {
    //    OnContactClickListener mOnContactClickListener;
    protected final HashMap<String, Integer> indexes = new HashMap<>();
    boolean isShowIndex = false;
    boolean isRegister = false;

    public PhoneContactAdapter(Context context, ArrayList<PhoneContactEntity> list , boolean isShowIndex , boolean isRegister) {
        super(context, list);
        this.isShowIndex = isShowIndex;
        if(isShowIndex){
            updateIndexes();
        }
        this.isRegister = isRegister;
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
            holder.tv_contact_abc = (TextView) view.findViewById(R.id.tv_contact_abc);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        PhoneContactEntity phoneContactEntity = (PhoneContactEntity) getItem(position);
        //
        holder.contacts_item_head.setImageResource(R.mipmap.default_male_head);
        if (!TextUtils.isEmpty(phoneContactEntity.getUid())) {
            holder.contacts_item_head.loadBuddyAvatar(phoneContactEntity.getUid());
        }
//        holder.tv_contact_name.setText(phoneContactEntity.getName());
        setKeywordText(holder.tv_contact_name, phoneContactEntity.getName());
        if (!TextUtils.isEmpty(phoneContactEntity.getSignature())) {
            holder.tv_contact_desc.setVisibility(View.VISIBLE);
//            holder.tv_contact_desc.setText(phoneContactEntity.getSignature());
            setKeywordText(holder.tv_contact_desc, phoneContactEntity.getSignature());
        } else {
            holder.tv_contact_desc.setVisibility(View.GONE);
        }
        if (phoneContactEntity.getRelation() == ContactRelation.REGISTERED) {
            if (NIMClient.getService(FriendService.class).isMyFriend(phoneContactEntity.getUid())) {
                //已经是好友
                holder.btn_contact_action.setText(context.getString(R.string.friend_status_already_add));
                holder.btn_contact_action.setBackgroundResource(android.R.color.transparent);
            } else {
                //不是好友
                holder.btn_contact_action.setText(context.getString(R.string.add));
                holder.btn_contact_action.setBackgroundResource(R.drawable.btn_friend_add);
            }
        } else {
            //该用户还为注册该游戏，邀请
//            holder.tv_contact_desc.setVisibility(View.GONE);
            holder.btn_contact_action.setText(context.getString(R.string.friend_action_invite));
            holder.btn_contact_action.setBackgroundResource(R.drawable.btn_friend_invite);
        }
        holder.btn_contact_action.setOnClickListener(new OnPhoneContactClick(position, false));
        holder.ll_contact.setOnClickListener(new OnPhoneContactClick(position, true));

        if(phoneContactEntity.getRelation() == ContactRelation.REGISTERED){
            holder.tv_contact_abc.setVisibility(View.GONE);
        } else {
            if (isShowIndex) {
                // 当前字母
                String currentStr = getAlpha(phoneContactEntity.getSortKey());
                // 前面的字母
                String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getSortKey()) : " ";
                boolean isSameRelation = (position - 1) >= 0 ?
                        (phoneContactEntity.getRelation() == list.get(position - 1).getRelation()) : false;
                if (!isSameRelation || !previewStr.equals(currentStr)) {
                    holder.tv_contact_abc.setText(currentStr);
                    holder.tv_contact_abc.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_contact_abc.setVisibility(View.GONE);
                }
            } else {
                holder.tv_contact_abc.setVisibility(View.GONE);
            }
        }
        return view;
    }

    private class OnPhoneContactClick implements View.OnClickListener {
        private int position;
        private boolean isClickItem;

        public OnPhoneContactClick(int position, boolean isClickItem) {
            this.position = position;
            this.isClickItem = isClickItem;
        }

        @Override
        public void onClick(View v) {
//            if(mOnContactClickListener != null){
//                mOnContactClickListener.onClick(position , isClickItem);
//            }
            PhoneContactEntity phoneContactEntity = (PhoneContactEntity) getItem(position);
            if (phoneContactEntity.getRelation() == ContactRelation.REGISTERED) {
                if(isRegister){
                    //注册界面，不让跳详情界面
                    if (!NIMClient.getService(FriendService.class).isMyFriend(phoneContactEntity.getUid())) {
                        AddVerifyActivity.start((Activity) context, phoneContactEntity.getUid(), AddVerifyActivity.TYPE_VERIFY_FRIEND);
                    }
                    return;
                }
                if (NIMClient.getService(FriendService.class).isMyFriend(phoneContactEntity.getUid())) {
                    //打开用户详情界面
                    UserProfileActivity.start((Activity) context, phoneContactEntity.getUid());
                } else {
                    //打开用户详情界面
                    if (isClickItem) {
                        UserProfileActivity.start((Activity) context, phoneContactEntity.getUid());
                    } else {
                        //打开用户添加界面
                        AddVerifyActivity.start((Activity) context, phoneContactEntity.getUid(), AddVerifyActivity.TYPE_VERIFY_FRIEND);
                    }
                }
            } else {
                String message = context.getString(R.string.invite_message) + " " + getHost() + ApiConstants.URL_SHARE_URL;
                //打开短信邀请
                doSendSMSTo(phoneContactEntity.getPhone(), message);
            }
        }
    }


    /**
     * 调起系统发短信功能
     *
     * @param phoneNumber
     * @param message
     */
    public void doSendSMSTo(String phoneNumber, String message) {
        LogUtil.i("phone", phoneNumber + ":" + PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber));
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) && context instanceof Activity) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            ((Activity) context).startActivity(intent);
        }
    }

    protected final class ViewHolder {
        private LinearLayout ll_contact;
        private HeadImageView contacts_item_head;
        public TextView btn_contact_action;
        public TextView tv_contact_name;
        public TextView tv_contact_desc;
        public TextView tv_contact_abc;
    }

    //    public interface OnContactClickListener{
//        public void onClick(int position , boolean isClickItem);
//    }
//
//    public void setOnContactClickListner(OnContactClickListener listener){
//        mOnContactClickListener = listener;
//    }
    public final LivIndex createLivIndex(ListView lv, LetterIndexView liv, TextView tvHit) {
        return new LivIndex(null, lv, liv, tvHit, getIndexes());
    }

    private Map<String, Integer> getIndexes() {
        return this.indexes;
    }

    public void updateIndexes() {
        // CLEAR
        this.indexes.clear();
//        // SET
//        this.indexes.putAll(indexes);
        for (int i = 0; i < list.size(); i++) {
            // 得到字母
            String name = getAlpha(list.get(i).getSortKey());
            if (!indexes.containsKey(name)) {
                indexes.put(name, i);
            }
        }
    }

    /**
     * 获取首字母
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
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
