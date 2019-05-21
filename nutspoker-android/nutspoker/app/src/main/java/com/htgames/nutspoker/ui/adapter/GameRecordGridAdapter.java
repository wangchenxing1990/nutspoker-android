package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;

/**
 */
public class GameRecordGridAdapter extends ListBaseAdapter<GameEntity> {
    public GameRecordGridAdapter(Context context, ArrayList<GameEntity> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = convertView;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.grid_record_game_item, null);
            holder.imageView = (HeadImageView) view.findViewById(R.id.iv_game_creator_head);
            holder.tv_game_name = (TextView) view.findViewById(R.id.tv_game_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final GameEntity gameEntity = list.get(position);
        holder.tv_game_name.setText(gameEntity.name);
        holder.imageView.loadBuddyAvatar(gameEntity.creatorInfo.account);
        return view;
    }

    class ViewHolder {
        HeadImageView imageView;
        TextView tv_game_name;
    }
}
