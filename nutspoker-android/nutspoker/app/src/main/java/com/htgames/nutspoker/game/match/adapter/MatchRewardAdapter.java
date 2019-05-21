package com.htgames.nutspoker.game.match.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.reward.RewardAllot;
/**
 * 奖励圈
 */
public class MatchRewardAdapter extends BaseAdapter {
    RewardAllot mRewardAllot;
    Context context;
    int playerCount;
    int allReward;

    public MatchRewardAdapter(Context context ,RewardAllot mRewardAllot , int playerCount , int allReward) {
        this.context = context;
        this.mRewardAllot = mRewardAllot;
        this.playerCount = playerCount;
        this.allReward = allReward;
    }

    public void updateData(int playerCount , int allReward) {
        this.playerCount = playerCount;
        this.allReward = allReward;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mRewardAllot.getListSize(playerCount);
    }

    @Override
    public Object getItem(int i) {
        return mRewardAllot.getRewardRank(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.list_mtt_blinds_stucture_item, null);
            view.findViewById(R.id.iv_mtt_termination_join_tag).setVisibility(View.GONE);
            view.findViewById(R.id.iv_mtt_rebuy_tag).setVisibility(View.GONE);
            holder.tv_reward_rank = (TextView) view.findViewById(R.id.tv_blinds_stucture_level);
            holder.tv_reward_proportion = (TextView) view.findViewById(R.id.tv_blinds_stucture_content);
            holder.tv_reward_reward = (TextView) view.findViewById(R.id.tv_blinds_stucture_ante_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        view.setBackgroundDrawable(view.getContext().getResources().getDrawable(R.drawable.common_column_bg));
        holder.tv_reward_rank.setText(mRewardAllot.getRewardRank(position));
        holder.tv_reward_proportion.setText("" + String.format("%.3f", mRewardAllot.getRewardPercent(position, playerCount)) + "%");
        holder.tv_reward_reward.setText(String.valueOf((int)(allReward * mRewardAllot.getRewardPercent(position, playerCount) / 100f)));
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_reward_rank;
        public TextView tv_reward_proportion;
        public TextView tv_reward_reward;
    }
}