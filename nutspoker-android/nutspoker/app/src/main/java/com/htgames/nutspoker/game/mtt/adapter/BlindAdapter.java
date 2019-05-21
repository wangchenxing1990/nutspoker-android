package com.htgames.nutspoker.game.mtt.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.bean.BlindStuctureEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/1/12.
 */

public class BlindAdapter extends RecyclerView.Adapter<BlindAdapter.BlindViewHolder> {
    public boolean showBlind = true;//是否显示中间的blind，mtt大菠萝的ante表格不显示中间的
    Activity mActivity;
    private int itemHeight = 0;
    ArrayList<BlindStuctureEntity> blindStuctureList = new ArrayList<>();
    public BlindAdapter(Activity activity, List data) {
        mActivity = activity;
        if (data != null) {
            blindStuctureList.addAll(data);
        }
        itemHeight = activity.getResources().getDimensionPixelOffset(R.dimen.blinds_item_height);
    }
    
    public void setData(ArrayList<BlindStuctureEntity> datas) {
        blindStuctureList.clear();
        blindStuctureList = datas;
        notifyDataSetChanged();
    }
    @Override
    public BlindAdapter.BlindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View view = inflater.inflate(R.layout.list_mtt_blinds_stucture_item, null);
        int width = parent.getMeasuredWidth();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, itemHeight));
        view.setClickable(true);
        return new BlindViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BlindAdapter.BlindViewHolder holder, int position) {
        if (position >= blindStuctureList.size()) {
            return;
        }
        holder.bind(blindStuctureList.get(position));
    }

    @Override
    public int getItemCount() {
        return blindStuctureList.size();
    }

    protected class BlindViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_blinds_stucture_level;
        public TextView tv_blinds_stucture_content;
        public TextView tv_blinds_stucture_ante_content;
        public ImageView iv_mtt_termination_join_tag;
        public ImageView iv_mtt_rebuy_tag;
        public BlindViewHolder(View view) {
            super(view);
            tv_blinds_stucture_level = (TextView) view.findViewById(R.id.tv_blinds_stucture_level);
            tv_blinds_stucture_content = (TextView) view.findViewById(R.id.tv_blinds_stucture_content);
            tv_blinds_stucture_content.setVisibility(showBlind ? View.VISIBLE : View.GONE);
            tv_blinds_stucture_ante_content = (TextView) view.findViewById(R.id.tv_blinds_stucture_ante_content);
            iv_mtt_termination_join_tag = (ImageView) view.findViewById(R.id.iv_mtt_termination_join_tag);
            iv_mtt_rebuy_tag = (ImageView) view.findViewById(R.id.iv_mtt_rebuy_tag);
        }
        public void bind(BlindStuctureEntity data) {
            tv_blinds_stucture_level.setText("" + data.getLevel());
            tv_blinds_stucture_content.setText(GameConstants.getGameBlindsShow(data.getSblinds()));
            tv_blinds_stucture_ante_content.setText(GameConstants.getGameChipsShow(data.getAnte()));
            iv_mtt_termination_join_tag.setVisibility(View.GONE);
            iv_mtt_rebuy_tag.setVisibility(View.GONE);
        }
    }
}
