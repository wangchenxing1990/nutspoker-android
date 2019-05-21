package com.htgames.nutspoker.ui.adapter.team;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.interfaces.IClick;

import java.util.List;

/**
 * Created by 周智慧 on 17/3/21.
 */

public class HordeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity mActivity;
    private List mList;
    public IClick listener;
    public HordeAdapter(Activity activity, List list, IClick listener) {
        mActivity = activity;
        mList = list;
        this.listener = listener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThisVH(LayoutInflater.from(mActivity).inflate(R.layout.list_horde, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThisVH && position < mList.size() && mList.get(position) instanceof HordeEntity) {
            ((ThisVH) holder).bind(position, (HordeEntity) mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }



    public class ThisVH extends RecyclerView.ViewHolder {
        TextView horde_name;
        TextView horde_vid;
        TextView tv_horde_playing_game;
        public ThisVH(View itemView) {
            super(itemView);
            horde_name = (TextView) itemView.findViewById(R.id.horde_name);
            horde_vid = (TextView) itemView.findViewById(R.id.horde_vid);
            tv_horde_playing_game = (TextView) itemView.findViewById(R.id.tv_horde_playing_game);
        }
        public void bind(final int position, HordeEntity hordeEntity) {
            if (hordeEntity == null) {
                return;
            }
            horde_name.setText(hordeEntity.name);
            horde_vid.setText("ID：" + hordeEntity.horde_vid);
            tv_horde_playing_game.setText(hordeEntity.playing_count + mActivity.getResources().getString(R.string.match_game_ing));
            itemView.setBackgroundResource(hordeEntity.is_my == 0 ? R.mipmap.bg_clan_others : R.mipmap.bg_clan_me);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(position);
                    }
                }
            });
        }
    }
}
