package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.htgames.nutspoker.interfaces.OnAvaterClickListener;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsInfoActivity;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;

/**
 */
public class RecordDetailsInfoAdapter extends ListBaseAdapter<GameMemberEntity> {
    OnAvaterClickListener mOnAvaterClickListener;
    int type = RecordDetailsInfoActivity.TYPE_BILL;

    public RecordDetailsInfoAdapter(Context context, ArrayList<GameMemberEntity> list , int type) {
        super(context, list);
        this.type = type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_record_details_info_item, null);
            holder.iv_game_userhead = (HeadImageView) view.findViewById(R.id.iv_game_userhead);
            holder.tv_game_member_nickname = (TextView) view.findViewById(R.id.tv_game_member_nickname);
            holder.tv_game_buys = (TextView) view.findViewById(R.id.tv_game_buys);
            holder.tv_game_earnings = (TextView) view.findViewById(R.id.tv_game_earnings);
            holder.tv_game_insurance = (TextView) view.findViewById(R.id.tv_game_insurance);
            holder.tv_game_insurance_gain = (TextView) view.findViewById(R.id.tv_game_insurance_gain);
            holder.tv_game_gain_all = (TextView) view.findViewById(R.id.tv_game_gain_all);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final GameMemberEntity mGameMemberEntity = (GameMemberEntity) getItem(position);
        final UserEntity userInfo = mGameMemberEntity.userInfo;
        holder.tv_game_member_nickname.setText(UserInfoShowHelper.getUserNickname(userInfo));
        holder.iv_game_userhead.loadBuddyAvatarByUrl(userInfo.account, userInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        //
        int totalBuy = mGameMemberEntity.totalBuy;
        int winChip = mGameMemberEntity.winChip;//盈利
        int insuranceBuy = mGameMemberEntity.premium;
        int insuranceGain = mGameMemberEntity.insurance - insuranceBuy;//保险盈利：保险赚的 - 投保
        int allGain = insuranceGain + winChip;//总盈利：盈利 + 保险盈利
        holder.tv_game_buys.setText(String.valueOf(totalBuy));
        holder.tv_game_insurance.setText(String.valueOf(insuranceBuy));
        RecordHelper.setRecordGainView(holder.tv_game_earnings, winChip);
        RecordHelper.setRecordGainView(holder.tv_game_insurance_gain, insuranceGain);
        RecordHelper.setRecordGainView(holder.tv_game_gain_all, allGain);

//        if (isInsurance) {
//            holder.tv_game_buys.setText(String.valueOf(mGameMemberEntity.getPremium()));
//            holder.iv_insurance_tag.setVisibility(View.GONE);
//            holder.ll_record_item.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        } else {
//            holder.tv_game_buys.setText(String.valueOf(mGameMemberEntity.getTotalBuy()));
//            if (mGameMemberEntity.getPremium() > 0) {
//                holder.iv_insurance_tag.setVisibility(View.VISIBLE);
//            } else {
//                holder.iv_insurance_tag.setVisibility(View.GONE);
//            }
//        }
        holder.iv_game_userhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAvaterClickListener != null) {
                    mOnAvaterClickListener.onAvaterClick(userInfo.account);
                }
            }
        });

        if(type == RecordDetailsInfoActivity.TYPE_BILL_LIST) {
//            holder.tv_game_buys.setVisibility(View.GONE);
//            holder.tv_game_insurance.setVisibility(View.GONE);
        }else {
            holder.tv_game_buys.setVisibility(View.VISIBLE);
            holder.tv_game_insurance.setVisibility(View.VISIBLE);
        }
//        if (DemoCache.getAccount().equals(userInfo.getAccount())) {
//            holder.tv_game_member_nickname.setTextColor(context.getResources().getColor(R.color.record_me_text_color));
//            holder.tv_game_earnings.setTextColor(context.getResources().getColor(R.color.record_me_text_color));
//            holder.tv_game_buys.setTextColor(context.getResources().getColor(R.color.record_me_text_color));
//        } else {
//            holder.tv_game_member_nickname.setTextColor(context.getResources().getColor(android.R.color.white));
//            holder.tv_game_buys.setTextColor(context.getResources().getColor(android.R.color.white));
//        }
        return view;
    }

    public void setOnAvaterClickListener(OnAvaterClickListener mOnAvaterClickListener) {
        this.mOnAvaterClickListener = mOnAvaterClickListener;
    }

    protected final class ViewHolder {
        public HeadImageView iv_game_userhead;
        public TextView tv_game_member_nickname;
        public TextView tv_game_buys;
        public TextView tv_game_earnings;
        public TextView tv_game_insurance;
        public TextView tv_game_insurance_gain;
        public TextView tv_game_gain_all;
    }
}
