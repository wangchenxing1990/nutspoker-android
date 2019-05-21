package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 会长积分管理界面
 */
public class ClubIntegralActivity extends BaseActivity{

    //点击积分明细
    @OnClick(R.id.rl_club_integral_particulars)
    void clickParticulars() {
        ClubIntegralParticularsActivity.start(this,ClubIntegralParticularsActivity.Type_Club,mTeam.getId());
    }

    //点击成员积分
    @OnClick(R.id.rl_club_integral_member)
    void clickMember(){
        MemberIntegralList.StartActivity(this,mTeam.getId());
    }

    //点击分配积分
    @OnClick(R.id.rl_club_integral_distribution)
    void clickDistribution(){
        ClubIntegralDistributionActivity.StartActivity(this);
    }

    //点击牌局信息
    @OnClick(R.id.rl_club_record_list)
    void clickList(){
        //现在默认导入到以前的牌局信息，需要加 type
        RecordListAC.StartActivity(this, RecordListAC.FROM_NORMAL);
    }

    public static void start(Activity activity, Team team) {
        Intent intent = new Intent(activity, ClubIntegralActivity.class);
        intent.putExtra(Extras.EXTRA_TEAM_DATA,team);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_integral);
        mUnbinder = ButterKnife.bind(this);

        mTeam = (Team) getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);

        setHeadTitle(R.string.manager_club_integral);
    }

    Team mTeam;
}
