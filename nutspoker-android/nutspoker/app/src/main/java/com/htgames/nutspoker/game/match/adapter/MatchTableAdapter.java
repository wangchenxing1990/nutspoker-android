package com.htgames.nutspoker.game.match.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.bean.MatchTableEntity;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;

import java.util.ArrayList;

/**
 */
public class MatchTableAdapter extends ListBaseAdapter<MatchTableEntity> {

    public MatchTableAdapter(Context context, ArrayList<MatchTableEntity> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_match_gametable_item, null);
            holder.tv_gametable_name = (TextView) view.findViewById(R.id.tv_gametable_name);
            holder.tv_gametable_member_count = (TextView) view.findViewById(R.id.tv_gametable_member_count);
            holder.tv_gametable_blinds = (TextView) view.findViewById(R.id.tv_gametable_blinds);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MatchTableEntity gameTableEntity = list.get(position);
        holder.tv_gametable_name.setText(getString(R.string.game_table_no, gameTableEntity.index));
        holder.tv_gametable_member_count.setText(String.valueOf(gameTableEntity.memberCount));
        holder.tv_gametable_blinds.setText(gameTableEntity.maxChips + "/" + gameTableEntity.minChips);
        return view;
    }

    protected final class ViewHolder {
        public TextView tv_gametable_name;
        public TextView tv_gametable_member_count;
        public TextView tv_gametable_blinds;
    }
}