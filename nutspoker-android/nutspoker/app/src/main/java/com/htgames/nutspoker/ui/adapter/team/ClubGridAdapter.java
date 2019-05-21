package com.htgames.nutspoker.ui.adapter.team;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;

/**
 * 俱乐部Grid列表适配器（好友的俱乐部列表）
 */
public class ClubGridAdapter extends ListBaseAdapter<TeamEntity> {
    private final static String TAG = "ClubAdapter";

    public ClubGridAdapter(Activity activity, ArrayList<TeamEntity> list) {
        super(activity.getApplicationContext(), list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.grid_club_item, null);
            holder.rl_club_head_bg = view.findViewById(R.id.rl_club_head_bg);
            holder.iv_club_head = (HeadImageView) view.findViewById(R.id.iv_club_head);
            holder.tv_club_info_name = (TextView) view.findViewById(R.id.tv_club_info_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TeamEntity team = (TeamEntity) getItem(position);
        holder.iv_club_head.loadClubAvatarByUrl(team.id, ClubConstant.getClubExtAvatar(team.extension), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        if (ClubConstant.isSuperClub(team)) {
            //超级俱乐部
//            holder.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_super_head_bg);
        } else {
//            holder.rl_club_head_bg.setBackgroundResource(R.drawable.default_club_head_bg);
        }
        holder.tv_club_info_name.setText(team.name);
        return view;
    }

    protected final class ViewHolder {
        public View rl_club_head_bg;
        public HeadImageView iv_club_head;
        public TextView tv_club_info_name;
    }
}
