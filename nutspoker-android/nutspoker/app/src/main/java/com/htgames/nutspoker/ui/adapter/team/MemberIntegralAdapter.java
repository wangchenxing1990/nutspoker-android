package com.htgames.nutspoker.ui.adapter.team;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.MemberIntegral;
import com.htgames.nutspoker.ui.activity.Club.Integral.MemberDetailActivity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by glp on 2016/8/19.
 */

public class MemberIntegralAdapter extends RecyclerView.Adapter<MemberIntegralAdapter.ViewHolder> {

    public static final String Tag = "MemberIntegralAdapter";

    List<MemberIntegral> mList;
    String mTeamId;

    public MemberIntegralAdapter(List<MemberIntegral> list,String teamId) {
        mList = list;
        mTeamId = teamId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_member_integral, parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        MemberIntegral data = mList.get(position);

        if (data != null) {
            holder.head.loadBuddyAvatar(data.account);
            holder.name.setText(NimUserInfoCache.getInstance().getUserDisplayName(data.account));
            holder.integral.setText("" + data.integral);

            final String fAccount = data.account;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //去往某个人的详情
                    MemberDetailActivity.StartActivity(holder.itemView.getContext(),fAccount,mTeamId);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * 按照积分排序,并通知更新列表
     *
     * @param list
     */
    public void sortListNotifyChanged(List<MemberIntegral> list) {
        if (list != null) {
            Collections.sort(list, new Comparator<MemberIntegral>() {
                @Override
                public int compare(MemberIntegral lhs, MemberIntegral rhs) {
                    return lhs.integral - rhs.integral;
                }
            });
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        //头像
        @BindView(R.id.iv_userhead) HeadImageView head;
        //名称
        @BindView(R.id.tv_name) TextView name;
        //积分
        @BindView(R.id.tv_integral) TextView integral;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
