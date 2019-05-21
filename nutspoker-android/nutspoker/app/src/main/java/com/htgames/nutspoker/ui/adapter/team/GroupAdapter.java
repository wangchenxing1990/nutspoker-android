package com.htgames.nutspoker.ui.adapter.team;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.view.GroupHeadView;
import com.netease.nim.uikit.team.helper.TeamHelper;
import com.netease.nimlib.sdk.team.model.Team;
import java.util.ArrayList;

/**
 */
public class GroupAdapter extends ListBaseAdapter<Team>{
    private final static String TAG = "GroupAdapter";

    public GroupAdapter(Context context, ArrayList<Team> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_group_item, null);
            holder.mGroupHeadView = (GroupHeadView) view.findViewById(R.id.mGroupHeadView);
//            holder.iv_group_head = (HeadImageView) view.findViewById(R.id.iv_group_head);
            holder.tv_group_name = (TextView) view.findViewById(R.id.tv_group_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Team team = list.get(position);
        String teamName = team.getName();
        if(TextUtils.isEmpty(teamName) || teamName.equals(ClubConstant.GROUP_IOS_DEFAULT_NAME)){
            //如果为空，名字用各个群成员加起来
            holder.tv_group_name.setText(TeamHelper.getTeamNameByMember(team.getId()));
        }else{
            holder.tv_group_name.setText(team.getName());
        }
        holder.mGroupHeadView.setGroupId(team.getId());
        return view;
    }

    protected final class ViewHolder {
//        public HeadImageView iv_group_head;
        GroupHeadView mGroupHeadView;
        public TextView tv_group_name;
    }
}
