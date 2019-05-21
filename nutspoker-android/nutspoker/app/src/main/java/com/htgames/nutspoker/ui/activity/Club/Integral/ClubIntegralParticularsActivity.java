package com.htgames.nutspoker.ui.activity.Club.Integral;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.adapter.team.IntegralDetailAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 俱乐部/个人-积分明细
 */
public class ClubIntegralParticularsActivity extends BaseActivity implements View.OnClickListener {

    public static final int Type_Member = 0;
    public static final int Type_Club = 1;

    PopupWindow clubPopupWindow = null;
    //全部记录
    LinearLayout ll_pop_integral_record_all;
    //分配
    LinearLayout ll_pop_integral_record_distribution_creator;
    //我的捐赠
    LinearLayout ll_pop_integral_record_donation_my;
    //盈利
    LinearLayout ll_pop_integral_record_profit;

    @BindView(R.id.ll_club_integral_record_info)
    LinearLayout ll_club_integral_record_info;
    @BindView(R.id.tv_club_integral_record_distribution_creator)
    TextView tv_club_integral_record_distribution_creator;
    @BindView(R.id.tv_club_integral_record_donation_my)
    TextView tv_club_integral_record_donation_my;
    @BindView(R.id.tv_club_integral_record_profit)
    TextView tv_club_integral_record_profit;
    @BindView(R.id.tv_head_right)
    TextView tv_head_right;
    @BindView(R.id.recycler_view)
    RecyclerView mUiRecycler;
    //data
    int mType;
    //会员ID或者俱乐部ID
    String mId;
    IntegralDetailAdapter mAdapter;

    public static void start(Activity activity,int type,String id) {
        Intent intent = new Intent(activity, ClubIntegralParticularsActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE,type);
        intent.putExtra(Extras.EXTRA_ACCOUNT,id);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_integral_particulars);
        mType = getIntent().getIntExtra(Extras.EXTRA_TYPE, Type_Member);
        mId = getIntent().getStringExtra(Extras.EXTRA_TYPE);
        initView();
        if (mType == Type_Member) {
            ll_club_integral_record_info.setVisibility(View.VISIBLE);
        } else {
            ll_club_integral_record_info.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this);
        mType = getIntent().getIntExtra(Extras.EXTRA_TYPE, Type_Member);
        mId = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        mAdapter = new IntegralDetailAdapter(getApplicationContext(), null);
        mUiRecycler.setLayoutManager(new LinearLayoutManager(this));
        mUiRecycler.setAdapter(mAdapter);
        initHead();
    }

    public void initHead() {
        setHeadTitle(R.string.club_integral_particulars);
        setHeadRightButton(R.string.club_integral_record_all, this);
        Drawable drawable = getResources().getDrawable(R.mipmap.icon_systemtip_mark);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tv_head_right.setCompoundDrawables(null, null, drawable, null);
    }

    private void showClubPopWindow() {
        if (clubPopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_integral_particulars, null);
            clubPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            clubPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            clubPopupWindow.setOutsideTouchable(true);
            clubPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            clubPopupWindow.setWidth(BaseTools.dip2px(getApplicationContext(), 160));
            clubPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            ll_pop_integral_record_all = (LinearLayout) popView.findViewById(R.id.ll_pop_integral_record_all);
            ll_pop_integral_record_distribution_creator = (LinearLayout) popView.findViewById(R.id.ll_pop_integral_record_distribution_creator);
            ll_pop_integral_record_donation_my = (LinearLayout) popView.findViewById(R.id.ll_pop_integral_record_donation_my);
            ll_pop_integral_record_profit = (LinearLayout) popView.findViewById(R.id.ll_pop_integral_record_profit);
            ll_pop_integral_record_all.setOnClickListener(this);
            ll_pop_integral_record_distribution_creator.setOnClickListener(this);
            ll_pop_integral_record_donation_my.setOnClickListener(this);
            ll_pop_integral_record_profit.setOnClickListener(this);
        }
        clubPopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), 0, -BaseTools.dip2px(getApplicationContext(), 9));
    }

    public void dissmissClubPopWindow() {
        if (clubPopupWindow != null && clubPopupWindow.isShowing()) {
            clubPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
                if (clubPopupWindow != null && clubPopupWindow.isShowing()) {
                    dissmissClubPopWindow();
                } else {
                    showClubPopWindow();
                }
                break;
            case R.id.ll_pop_integral_record_all:
                break;
            case R.id.ll_pop_integral_record_distribution_creator:
                break;
            case R.id.ll_pop_integral_record_donation_my:
                break;
            case R.id.ll_pop_integral_record_profit:
                break;
        }
    }
}
