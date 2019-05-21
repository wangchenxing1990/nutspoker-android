package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.adapter.team.MemDetailAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 会长查看成员积分明细
 */

public class MemberDetailActivity extends BaseActivity {

    @BindView(R.id.iv_userhead)
    HeadImageView mUiHeadImageView;
    @BindView(R.id.tv_name)
    TextView mUiName;
    @BindView(R.id.tv_zyh)
    TextView mUiZyh;
    @BindView(R.id.tv_integral)
    TextView mUiIntegral;
    @BindView(R.id.tv_dispatch)
    TextView mUiDispatch;
    @BindView(R.id.tv_contirbution)
    TextView mUiContribution;
    @BindView(R.id.tv_gain)
    TextView mUiGain;
    @BindView(R.id.recycler_view)
    RecyclerView mUiRecyclerView;

    public static void StartActivity(Context context,String account,String tid){
        Intent intent = new Intent(context,MemberDetailActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT,account);
        intent.putExtra(Extras.EXTRA_TEAM_ID,tid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        mUnbinder = ButterKnife.bind(this);

        mTid = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        mAccount = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);

        //请求数据

        mAdapter = new MemDetailAdapter(null);
        mUiRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUiRecyclerView.setAdapter(mAdapter);


        mUiHeadImageView.loadBuddyAvatar(mAccount);
        mUiName.setText(NimUserInfoCache.getInstance().getUserDisplayName(mAccount));
        mUiZyh.setText("缺失战鱼号");
        WealthHelper.SetMoneyText(mUiDispatch,0,this);
        WealthHelper.SetMoneyText(mUiContribution,0,this);
        WealthHelper.SetMoneyText(mUiGain,0,this);
        setHeadTitle(R.string.member_detail);
        setHeadRightButton(R.string.text_dispatch, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClubIntegralOperation.StartActivity(MemberDetailActivity.this,mAccount
                        ,ClubIntegralOperation.Type_Dispatch,null);
            }
        });
    }

    String mTid;
    String mAccount;
    MemDetailAdapter mAdapter;
}
