package com.htgames.nutspoker.ui.adapter.team;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.IntegralDetail;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;

/**
 * 积分明细Adapter
 */
public class IntegralDetailAdapter extends RecyclerView.Adapter<IntegralDetailAdapter.IntegralViewHolder> {

    List<IntegralDetail> mList;
    Context context;

    public IntegralDetailAdapter(Context context, List<IntegralDetail> list) {
        this.context = context;
        mList = list;
    }

    @Override
    public IntegralViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate()
        return null;
    }

    @Override
    public void onBindViewHolder(IntegralViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class IntegralViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_integral_date_node_day;
        private TextView tv_game_date_node_month;
        private LinearLayout ll_integral_time;
        private TextView tv_integral_time;
        private HeadImageView iv_integral_userhead;
        private TextView tv_integral_content;
        private TextView tv_integral_earnings;

        public IntegralViewHolder createViewHolder(Context context) {
            return new IntegralViewHolder(LayoutInflater.from(context).inflate(R.layout.list_integral_detail_item, null));
        }

        public IntegralViewHolder(View itemView) {
            super(itemView);
            tv_integral_date_node_day = (TextView) itemView.findViewById(R.id.tv_integral_date_node_day);
            tv_game_date_node_month = (TextView) itemView.findViewById(R.id.tv_game_date_node_month);
            ll_integral_time = (LinearLayout) itemView.findViewById(R.id.ll_integral_time);
            tv_integral_time = (TextView) itemView.findViewById(R.id.tv_integral_time);
            iv_integral_userhead = (HeadImageView) itemView.findViewById(R.id.iv_integral_userhead);
            tv_integral_content = (TextView) itemView.findViewById(R.id.tv_integral_content);
            tv_integral_earnings = (TextView) itemView.findViewById(R.id.tv_integral_earnings);
        }

        public void refresh(IntegralDetail integralDetail, int position) {
            iv_integral_userhead.loadBuddyAvatarByUrl(integralDetail.uid, integralDetail.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            tv_integral_time.setText(DateTools.getStrTime_hm(String.valueOf(integralDetail.time)));

            if (position != 0) {
                IntegralDetail lastIntegralDetail = (IntegralDetail) mList.get(position - 1);
                if (lastIntegralDetail != null && DateTools.isTheSameDay(lastIntegralDetail.time, integralDetail.time)) {
                    //判断是否是同一天
                    tv_integral_date_node_day.setVisibility(View.INVISIBLE);
                    tv_game_date_node_month.setVisibility(View.GONE);
                } else {
                    String showDay = DateTools.getRecordTimeNodeDayDisplay(String.valueOf(integralDetail.time));
                    String showMonth = DateTools.getRecordTimeNodeMonth(String.valueOf(integralDetail.time));
                    tv_integral_date_node_day.setVisibility(View.VISIBLE);
                    tv_integral_date_node_day.setText(showDay);
                    tv_game_date_node_month.setText(showMonth);
                    if (showDay.equals(context.getString(R.string.today)) || showDay.equals(context.getString(R.string.yesterday))) {
                        tv_game_date_node_month.setVisibility(View.GONE);
                    } else {
                        tv_game_date_node_month.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                String showDay = DateTools.getRecordTimeNodeDayDisplay(String.valueOf(integralDetail.time));
                String showMonth = DateTools.getRecordTimeNodeMonth(String.valueOf(integralDetail.time));
                tv_integral_date_node_day.setVisibility(View.VISIBLE);
                tv_game_date_node_month.setText(showMonth);
                tv_integral_date_node_day.setText(showDay);
                if (showDay.equals(context.getString(R.string.today)) || showDay.equals(context.getString(R.string.yesterday))) {
                    tv_game_date_node_month.setVisibility(View.GONE);
                } else {
                    tv_game_date_node_month.setVisibility(View.VISIBLE);
                }
            }
            RecordHelper.setRecordGainView(tv_integral_earnings, integralDetail.integral);
        }
    }

}
