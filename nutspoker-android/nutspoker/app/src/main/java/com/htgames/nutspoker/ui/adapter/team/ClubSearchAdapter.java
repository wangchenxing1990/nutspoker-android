package com.htgames.nutspoker.ui.adapter.team;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.region.db.RegionDBTools;
import com.htgames.nutspoker.ui.adapter.ListBaseAdapter;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/12/9.
 */
public class ClubSearchAdapter extends ListBaseAdapter<TeamEntity> {
    private final static String TAG = "ClubAdapter";
    int showCountLimit = 3;
    private Activity activity;
    boolean isShowLimit = false;//是否有只显示3个限制

    public ClubSearchAdapter(Activity activity, ArrayList<TeamEntity> list) {
        super(activity.getApplicationContext(), list);
        this.activity = activity;
    }

    public ClubSearchAdapter(Activity activity, ArrayList<TeamEntity> list , boolean isShowLimit) {
        super(activity.getApplicationContext(), list);
        this.activity = activity;
        this.isShowLimit = isShowLimit;
    }

    @Override
    public int getCount() {
        if(super.getCount() > 3 && isShowLimit){
            return showCountLimit;
        }
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_club_item, null);
            holder.iv_club_head = (HeadImageView) view.findViewById(R.id.iv_club_head);
            holder.tv_club_info_name = (TextView) view.findViewById(R.id.tv_club_info_name);
            holder.tv_club_info_member_count = (TextView) view.findViewById(R.id.tv_club_info_member_count);
            holder.tv_club_info_area = (TextView) view.findViewById(R.id.tv_club_info_area);
            holder.tv_club_info_introduce = (TextView) view.findViewById(R.id.tv_club_info_introduce);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        TeamEntity team = list.get(position);
//        holder.tv_club_info_name.setText(team.getName());
        //区域
        String extServer = team.extension;
        holder.iv_club_head.loadClubAvatarByUrl(team.id , ClubConstant.getClubExtAvatar(extServer), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        int areaId = ClubConstant.getClubExtAreaId(extServer);
        if (areaId != 0) {
            String area = RegionDBTools.getShowRegionContent(Integer.valueOf(areaId), " ");
            holder.tv_club_info_area.setText(area);
        }
//        //俱乐部虚拟ID
//        String virtualId = "0";
//        try{
//            virtualId = (new JSONObject(extServer)).optString("vid","0");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        holder.tv_club_info_area.setText("ID: " + ClubConstant.getClubVirtualId(team.id, extServer));//改成俱乐部虚拟ID

        setKeywordText(holder.tv_club_info_name, team.name);
        holder.tv_club_info_member_count.setText(team.memberCount + "/" + ClubConstant.getClubMemberLimit(team));
//        if(!TextUtils.isEmpty(team.getIntroduce()) &&!"null".equals(team.getIntroduce())){
//            holder.tv_club_info_introduce.setText(team.getIntroduce() + "俱乐部ID：" + team.getId());
//        } else{
//            holder.tv_club_info_introduce.setText("俱乐部ID：" + team.getId());
//        }

        return view;
    }

    protected final class ViewHolder {
        public HeadImageView iv_club_head;
        public TextView tv_club_info_name;
        public TextView tv_club_info_member_count;
        public TextView tv_club_info_area;
        public TextView tv_club_info_introduce;
    }
}
