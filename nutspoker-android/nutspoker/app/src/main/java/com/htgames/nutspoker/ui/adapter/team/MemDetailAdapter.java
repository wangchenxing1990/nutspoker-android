package com.htgames.nutspoker.ui.adapter.team;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.IntegralDetail;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.helper.WealthHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by glp on 2016/8/24.
 */

public class MemDetailAdapter extends RecyclerView.Adapter<MemDetailAdapter.ViewHolder> {

    List<IntegralDetail> mList;

    public MemDetailAdapter(List<IntegralDetail> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_member_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IntegralDetail data = mList.get(position);
        IntegralDetail dataPre = position > 0 ? mList.get(position - 1) : null;

        if (dataPre == null) {
            String showDay = DateTools.getRecordTimeNodeDayDisplay(String.valueOf(data.time));
            String showMonth = DateTools.getRecordTimeNodeMonth(String.valueOf(data.time));
            holder.day.setVisibility(View.VISIBLE);
            holder.month.setText(showMonth);
            holder.day.setText(showDay);
            if (showDay.equals(ChessApp.sAppContext.getString(R.string.today)) || showDay.equals(ChessApp.sAppContext.getString(R.string.yesterday)))
                holder.month.setVisibility(View.GONE);
            else
                holder.month.setVisibility(View.VISIBLE);
        } else {
            if (DateTools.isTheSameDay(dataPre.time, data.time)) {
                //判断是否是同一天
                holder.day.setVisibility(View.GONE);
                holder.month.setVisibility(View.GONE);
            } else {
                String showDay = DateTools.getRecordTimeNodeDayDisplay(String.valueOf(data.time));
                String showMonth = DateTools.getRecordTimeNodeMonth(String.valueOf(data.time));
                holder.day.setVisibility(View.VISIBLE);
                holder.day.setText(showDay);
                holder.month.setText(showMonth);
                if (showDay.equals(ChessApp.sAppContext.getString(R.string.today)) || showDay.equals(ChessApp.sAppContext.getString(R.string.yesterday)))
                    holder.month.setVisibility(View.GONE);
                else
                    holder.month.setVisibility(View.VISIBLE);
            }
        }

        if (position == (getItemCount() - 1)) {
            holder.timeline.setVisibility(View.GONE);
        } else {
            holder.timeline.setVisibility(View.VISIBLE);
        }

        switch (data.type) {
            case 0:
                holder.option.setText(R.string.type_contribution);
                break;
            case 1:
                holder.option.setText(R.string.type_contribution);
                break;
            case 2:
                holder.option.setText(R.string.type_contribution);
                break;
        }

        holder.hm.setText(DateTools.getStrTime_hm(String.valueOf(data.time)));
        WealthHelper.SetMoneyText(holder.integral, data.integral, ChessApp.sAppContext);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_day)
        TextView day;
        @BindView(R.id.tv_month)
        TextView month;
        @BindView(R.id.view_time_line)
        View timeline;
        @BindView(R.id.tv_time)
        TextView hm;
        @BindView(R.id.tv_optip)
        TextView option;
        @BindView(R.id.tv_integral)
        TextView integral;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
