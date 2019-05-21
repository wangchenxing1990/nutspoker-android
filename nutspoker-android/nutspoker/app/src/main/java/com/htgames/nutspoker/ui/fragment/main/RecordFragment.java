package com.htgames.nutspoker.ui.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.activity.Hands.PaipuRecordActivity;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.htgames.nutspoker.ui.activity.Record.RecordMatchActivity;
import com.htgames.nutspoker.ui.activity.Record.RecordNormalActivity;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.RecordEntity;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 战绩
 */
public class RecordFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = "RecordFragment";
    View view;
    RecordEntity mRecordEntity;
    RecordAction mRecordAction;
    RecordTopView normalView;//普通局
    RecordTopView matchView;//赛事场
    RecordTopAdapter viewPagerAdapter;
    @BindView(R.id.fl_record_page_paiju_info) View fl_record_page_paiju_info;
    @BindView(R.id.fl_record_page_paiju_record) View fl_record_page_paiju_record;
    @BindView(R.id.tv_record_viewpager_tab_match) TextView tv_record_viewpager_tab_match;
    @BindView(R.id.tv_record_viewpager_tab_normal) TextView tv_record_viewpager_tab_normal;
    @BindView(R.id.record_viewpager) ViewPager record_viewpager;

    @OnClick(R.id.tv_record_viewpager_tab_normal) void clickNormalTab() {
        if (record_viewpager != null) {
            record_viewpager.setCurrentItem(0);
        }
    }
    @OnClick(R.id.tv_record_viewpager_tab_match) void clickMatchTab() {
        if (record_viewpager != null) {
            record_viewpager.setCurrentItem(1);
        }
    }
    @OnClick(R.id.fl_record_page_paiju_info) void clickPaijuInfo() {
        RecordListAC.StartActivity(getActivity(), RecordListAC.FROM_NORMAL);
    }
    @OnClick(R.id.fl_record_page_paiju_record) void clickPaijuRecord() {
        getActivity().startActivity(new Intent(getActivity(), PaipuRecordActivity.class));
    }

    public static RecordFragment newInstance() {
        RecordFragment mInstance = new RecordFragment();
        return mInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_record, container, false);
        mFragmentUnbinder =ButterKnife.bind(this,view);
        setHeadTitle(view, R.string.main_tab_record);
        initView();
        initCircular();
        getRecordData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        normalView = (RecordTopView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_record_viewpager, null);
        normalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isTestVersion) {
                    RecordNormalActivity.StartActivity(getActivity(), getString(R.string.main_tab_record), mRecordEntity);
                }
            }
        });
        matchView = (RecordTopView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_record_viewpager, null, false);
        matchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApiConfig.isTestVersion) {
                    RecordMatchActivity.StartActivity(getActivity(), getString(R.string.main_tab_record), mRecordEntity.my_c_won_sng, mRecordEntity.my_c_won_sng_mtt, mRecordEntity.my_c_won_mtt);
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordAction = new RecordAction(getActivity() , null);
        setFragmentName("RecordFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mRecordAction != null){
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateVip(UserConstant.getMyVipLevel());
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        updateVip(UserConstant.getMyVipLevel());
    }

    private void initCircular() {
//        circularProgressbar = (CircularProgressBar) view.findViewById(R.id.circularProgressbar);
//        circularProgressbar.setColor(getResources().getColor(R.color.record_yellow_color));
//        circularProgressbar.setSecondColor(getResources().getColor(R.color.record_orange_color));
//        circularProgressbar.setBackgroundColor(getResources().getColor(R.color.record_gray_color));
//        sencondCircularProgressbar = (CircularProgressBar) view.findViewById(R.id.sencondCircularProgressbar);
//        sencondCircularProgressbar.setColor(getResources().getColor(R.color.record_orange_color));
//        sencondCircularProgressbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void initView() {
//        mHandCardView = (HandCardView) view.findViewById(R.id.mHandCardView);
//        rl_record_paiju = (RelativeLayout) view.findViewById(R.id.rl_record_paiju);
//        rl_record_paiju.setOnClickListener(this);
//        rl_record_handcard = (RelativeLayout) view.findViewById(R.id.rl_record_handcard);
//        rl_record_handcard.setOnClickListener(this);
        //新的记录
//        tv_record_paiju_count = (TextView)view.findViewById(R.id.tv_record_paiju_count);
//        tv_record_ruchi = (TextView)view.findViewById(R.id.tv_record_ruchi);
//        tv_record_winrate_in = (TextView)view.findViewById(R.id.tv_record_winrate_in);
//        tv_record_hands_count = (TextView)view.findViewById(R.id.tv_record_hands_count);
        viewPagerAdapter = new RecordTopAdapter(getActivity());
        record_viewpager.setAdapter(viewPagerAdapter);
        record_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                tv_record_viewpager_tab_normal.setAlpha(position == 0 ? 1 : 0.4f);
                tv_record_viewpager_tab_match.setAlpha(position == 1 ? 1 : 0.4f);
                viewPagerAnim(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
        } else {
            if(mRecordEntity == null) {
                getRecordData();
            }
            viewPagerAnim(record_viewpager == null ? 0 : record_viewpager.getCurrentItem());
        }
        super.onHiddenChanged(hidden);
    }

    public void viewPagerAnim(int position) {
        if (position == 0) {
            AnimUtil.scaleLargeIn(normalView, 300);
            if (normalView != null) {
                AnimUtil.scaleLargeIn(normalView.record_viewpager_money_num, 500);
                if (mRecordEntity != null) {
                    AnimUtil.textNumAnimation(normalView.record_viewpager_money_num, mRecordEntity.my_c_won, 300);
                }
            }
        } else if (position == 1) {
            AnimUtil.scaleLargeIn(matchView, 300);
            if (matchView != null) {
                AnimUtil.scaleLargeIn(matchView.record_viewpager_money_num, 500);
                if (mRecordEntity != null) {
                    AnimUtil.textNumAnimation(matchView.record_viewpager_money_num, mRecordEntity.my_c_won_sng + mRecordEntity.my_c_won_sng_mtt + mRecordEntity.my_c_won_mtt, 300);
                }
            }
        }
    }

    public void getLocalRecordData() {
        mRecordEntity = GameDataDBHelper.getRecordEntity(ChessApp.sAppContext);
        if (mRecordEntity != null) {
            updateRecordUI();
        }
    }

    public void getRecordData() {
        mRecordAction.getRecordIndexData(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                LogUtil.i(TAG , "getRecordData onResult: " + result);
                if(code == 0) {
                    RecordEntity re = null;
                    try {
                        Type type = new TypeToken<CommonBeanT<RecordEntity>>(){}.getType();
                        CommonBeanT<RecordEntity> cbt = GsonUtils.getGson().fromJson(result,type);
                        re = cbt.data;
                        GameDataDBHelper.setRecordEntity(getContext(),re);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(re != null) {
                        mRecordEntity = re;
                    } else {
                        getLocalRecordData();
                    }
                    updateRecordUI();
                }
            }
            @Override
            public void onFailed() {
                getLocalRecordData();
            }
        });
    }

    void updateVip(int vipLevel){
        if(mVipLevel != vipLevel){
            mVipLevel = vipLevel;

            switch(mVipLevel){
                case 0:
//                    mUiCollectTip.setText(getString(R.string.collect_hand_format,7));
                    break;
                case 1:
//                    mUiCollectTip.setText(getString(R.string.collect_hand_format,30));
                    break;
                case 2:
//                    mUiCollectTip.setText(getString(R.string.collect_hand_format,90));
                    break;
            }
        }
    }

    public void updateRecordUI() {
        if(mRecordEntity == null) {
            return;
        }
        viewPagerAdapter.setData(mRecordEntity);
        viewPagerAnim(record_viewpager == null ? 0 : record_viewpager.getCurrentItem());
        if (getActivity() != null) {
            GameDataDBHelper.setRecordEntity(getActivity() , mRecordEntity);//保存进入数据库
        }
//        WealthHelper.SetMoneyText(mTotalNormalMoney, mRecordEntity.my_c_won,getContext());
        int vpip = mRecordEntity.vpip;//入池率
        int waptmp = (int)(mRecordEntity.waptmp * ((float)vpip /100));//其中胜率
//        tv_record_paiju_count.setText(String.valueOf(mRecordEntity.games));
//        tv_record_hands_count.setText(String.valueOf(mRecordEntity.hands));
//        circularProgressbar.setProgressWithAnimation(vpip);
//        sencondCircularProgressbar.setProgressWithAnimation(waptmp);
//        tv_record_ruchi.setText(mRecordEntity.vpip + "%");
//        tv_record_winrate_in.setText(mRecordEntity.waptmp + "%");
        int totalMatch = mRecordEntity.my_c_won_sng+mRecordEntity.my_c_won_sng_mtt+mRecordEntity.my_c_won_mtt;
//        WealthHelper.SetMoneyText(mTotalMatchMoney, totalMatch,getContext());
//        mTatalSngTimes.setText(getString(R.string.game_match_times_format,mRecordEntity.games_sng));
//        mTatalMtSngTimes.setText(getString(R.string.game_match_times_format,mRecordEntity.games_sng_mtt));
//        mTatalMttTimes.setText(getString(R.string.game_match_times_format,mRecordEntity.games_mtt));
        //参与多少牌局
//        mUiGameInfo.setText(getString(R.string.join_games_foramt, mRecordEntity.games + mRecordEntity.games_sng + mRecordEntity.games_sng_mtt + mRecordEntity.games_mtt));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        } else {
        }
    }
    int mVipLevel = -1;

    public class RecordTopAdapter extends PagerAdapter {
        public RecordTopAdapter(Activity activity) {
            LogUtil.i("RecordTopAdapter RecordTopAdapter");
        }
        @Override
        public int getCount() {
            return 2;
        }

        public void setData(RecordEntity data) {
            LogUtil.i("RecordTopAdapter setData");
            if (normalView != null && normalView.hasInit) {
                normalView.bind(data, RecordTopView.TYPE_NORMAL);
            }
            if (matchView != null && matchView.hasInit) {
                matchView.bind(data, RecordTopView.TYPE_MATCH);
            }
            notifyDataSetChanged();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LogUtil.i("RecordTopAdapter instantiateItem");
            if (position == 0) {
                container.addView(normalView);
            } else if (position == 1) {
                container.addView(matchView);
            }
            return position == 0 ? normalView : matchView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public static class RecordTopView extends RelativeLayout {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_MATCH = 1;
        public boolean hasInit = false;
        TextView record_viewpager_money_num;
        //normal
        TextView record_viewpager_paiju_num;
        TextView record_viewpager_hands_num;
        TextView record_viewpager_paiju_num_des;
        TextView record_viewpager_hands_num_des;
        //match
        TextView record_viewpager_sng_num;
        TextView record_viewpager_mtt_num;
        public RecordTopView(Context context) {
            this(context, null);
        }
        public RecordTopView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
        public RecordTopView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }
        public void bind(RecordEntity data, int type) {
            if (data == null) {
                return;
            }
            record_viewpager_paiju_num.setVisibility(type == TYPE_NORMAL ? VISIBLE : GONE);
            record_viewpager_hands_num.setVisibility(type == TYPE_NORMAL ? VISIBLE : GONE);
            record_viewpager_paiju_num_des.setVisibility(type == TYPE_NORMAL ? VISIBLE : GONE);
            record_viewpager_hands_num_des.setVisibility(type == TYPE_NORMAL ? VISIBLE : GONE);
            record_viewpager_sng_num.setVisibility(type == TYPE_MATCH ? VISIBLE : GONE);
            record_viewpager_mtt_num.setVisibility(type == TYPE_MATCH ? VISIBLE : GONE);
            if (type == TYPE_NORMAL) {
                bindNormal(data);
            } else if (type == TYPE_MATCH) {
                bindMatch(data);
            }
        }
        public void bindNormal(RecordEntity data) {
            String moneyStr = data.my_c_won >=0 ? ("+" + WealthHelper.getWealthShow(data.my_c_won)) : ("" + WealthHelper.getWealthShow(data.my_c_won));
            record_viewpager_money_num.setText(moneyStr);
            record_viewpager_money_num.setTextColor(getContext().getResources().getColor(data.my_c_won >=0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
            record_viewpager_paiju_num.setText("" + data.games);
            record_viewpager_hands_num.setText("" + data.hands);
        }
        public void bindMatch(RecordEntity data) {
            int totalMatch = data.my_c_won_sng + data.my_c_won_sng_mtt + data.my_c_won_mtt;
            String moneyStr = totalMatch >=0 ? ("+" + WealthHelper.getWealthShow(totalMatch)) : ("" + WealthHelper.getWealthShow(totalMatch));
            record_viewpager_money_num.setText(moneyStr);
            record_viewpager_money_num.setTextColor(getContext().getResources().getColor(totalMatch >=0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
            record_viewpager_sng_num.setText("" + data.games_sng + "场");
            record_viewpager_mtt_num.setText("" + data.games_mtt + "场");
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            LogUtil.i("RecordTopAdapter onFinishInflate");
            hasInit = true;
            record_viewpager_money_num = (TextView) findViewById(R.id.record_viewpager_money_num);
            record_viewpager_paiju_num = (TextView) findViewById(R.id.record_viewpager_paiju_num);
            record_viewpager_paiju_num_des = (TextView) findViewById(R.id.record_viewpager_paiju_num_des);
            record_viewpager_hands_num_des = (TextView) findViewById(R.id.record_viewpager_hands_num_des);
            record_viewpager_hands_num = (TextView) findViewById(R.id.record_viewpager_hands_num);
            record_viewpager_sng_num = (TextView) findViewById(R.id.record_viewpager_sng_num);
            record_viewpager_mtt_num = (TextView) findViewById(R.id.record_viewpager_mtt_num);
        }
    }
}
