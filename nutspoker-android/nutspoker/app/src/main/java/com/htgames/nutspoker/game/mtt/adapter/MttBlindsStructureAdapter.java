package com.htgames.nutspoker.game.mtt.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.bean.BlindStuctureEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 */
public class MttBlindsStructureAdapter extends ListBaseAdapter<BlindStuctureEntity> {
    //是否是比赛大厅
    int matchLevel = 0;
    boolean isAddonMode = false;
    boolean isMatchRoom = false;
    public boolean showBlind = true;

    public MttBlindsStructureAdapter(Context context, ArrayList<BlindStuctureEntity> list, boolean isMatchRoom) {
        super(context, list);
        this.isMatchRoom = isMatchRoom;
    }

    public void setMatchRoomInfo(int matchLevel ,boolean isRebuyMode ,boolean isAddonMode) {
        isMatchRoom = true;
        this.matchLevel = matchLevel;
        this.isAddonMode = isAddonMode;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_mtt_blinds_stucture_item, null);
            holder.tv_blinds_stucture_level = (TextView) view.findViewById(R.id.tv_blinds_stucture_level);
            holder.tv_blinds_stucture_content = (TextView) view.findViewById(R.id.tv_blinds_stucture_content);
            holder.tv_blinds_stucture_ante_content = (TextView) view.findViewById(R.id.tv_blinds_stucture_ante_content);
            holder.iv_mtt_termination_join_tag = (ImageView) view.findViewById(R.id.iv_mtt_termination_join_tag);
            holder.iv_mtt_rebuy_tag = (ImageView) view.findViewById(R.id.iv_mtt_rebuy_tag);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        view.setBackgroundDrawable(view.getContext().getResources().getDrawable(R.drawable.common_column_bg));
        BlindStuctureEntity blindStuctureEntity = (BlindStuctureEntity) getItem(position);
        holder.tv_blinds_stucture_level.setText("" + blindStuctureEntity.getLevel());
        holder.tv_blinds_stucture_content.setText(GameConstants.getGameBlindsShow(blindStuctureEntity.getSblinds()));
        holder.tv_blinds_stucture_content.setVisibility(showBlind ? View.VISIBLE : View.GONE);
        holder.tv_blinds_stucture_ante_content.setText(GameConstants.getGameChipsShow(blindStuctureEntity.getAnte()));
        if (isMatchRoom) {
            if (blindStuctureEntity.getLevel() == matchLevel && isAddonMode) {
            }
            if (blindStuctureEntity.getLevel() < (matchLevel)) {
                view.setBackgroundResource(R.color.list_item_bg_press);
            }
            holder.iv_mtt_termination_join_tag.setVisibility(blindStuctureEntity.getLevel() == matchLevel ? View.VISIBLE : View.GONE);
        } else {
            holder.iv_mtt_termination_join_tag.setVisibility(View.GONE);
            holder.iv_mtt_rebuy_tag.setVisibility(View.GONE);
        }
        return view;
    }

    protected class ViewHolder {
        public TextView tv_blinds_stucture_level;
        public TextView tv_blinds_stucture_content;
        public TextView tv_blinds_stucture_ante_content;
        public ImageView iv_mtt_termination_join_tag;
        public ImageView iv_mtt_rebuy_tag;
    }
}
