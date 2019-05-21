package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人-积分管理
 */
public class MyIntegralActivity extends BaseActivity {

    @BindView(R.id.tv_integral) TextView mUiIntegral;
    @OnClick(R.id.rl_integral_detail) void clickDetail(){
        //积分明细
        ClubIntegralParticularsActivity.start(this,ClubIntegralParticularsActivity.Type_Member
                , UserPreferences.getInstance(this).getUserId());
    }
    @OnClick(R.id.rl_integral_contribution) void clickContribution(){
        //捐赠积分
        if(mTeam != null)
            ClubIntegralOperation.StartActivity(this,mTeam.getId(),ClubIntegralOperation.Type_Contribution,mTeam);
    }
    @OnClick(R.id.rl_integral_gameinfo) void clickGameInfo(){
        //牌局信息
        //现在默认导入到以前的牌局信息，需要加 type
        RecordListAC.StartActivity(this, RecordListAC.FROM_NORMAL);
    }

    public static void StartActivity(Context context,Team team){
        Intent intent = new Intent(context,MyIntegralActivity.class);
        intent.putExtra(Extras.EXTRA_TEAM_DATA,team);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_integral);
        mUnbinder = ButterKnife.bind(this);

        mTeam = (Team)getIntent().getSerializableExtra(Extras.EXTRA_TEAM_DATA);

        setHeadTitle(R.string.integral_my);
    }

    Team mTeam;
    public static int mMyIntegral;
}
