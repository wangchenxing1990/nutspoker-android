package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.MemberIntegral;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.adapter.team.MemberIntegralAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 成员积分列表
 */

public class MemberIntegralList extends BaseActivity {

    @BindView(R.id.recyler) RecyclerView mRecyclerView;

    public static void StartActivity(Context context,String teamId){
        Intent intent = new Intent(context,MemberIntegralList.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID,teamId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_integral_list);
        mUnbinder = ButterKnife.bind(this);

        mTeamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);

        mListMember = new ArrayList<>();
        for(TeamMember tm : ClubInfoActivity.teamMembers){
            MemberIntegral mi = new MemberIntegral();
            mi.account = tm.getAccount();
            mListMember.add(mi);
        }

        //请求服务器积分数据

        //本地数据
        mAdapter = new MemberIntegralAdapter(mListMember,mTeamId);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        setHeadTitle(R.string.club_integral_member);
    }

    //data
    List<MemberIntegral> mListMember;
    MemberIntegralAdapter mAdapter;
    String mTeamId;
}
