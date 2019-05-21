package com.htgames.nutspoker.ui.activity.Record;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.DataAnalysisEntity;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.config.AnalysisConfig;
import com.htgames.nutspoker.view.record.DataAnalysisItemView;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.session.constant.Extras;

/**
 * 数据分析
 */
public class DataAnalysisActivity extends BaseActivity {
    private final static String TAG = "DataAnalysisActivity";
    DataAnalysisItemView mWsdItem;
    DataAnalysisItemView mWtsdItem;
    DataAnalysisItemView mWwsfItem;
    DataAnalysisItemView mAfItem;
    DataAnalysisItemView mAfqItem;
    DataAnalysisItemView mVpipItem;
    DataAnalysisItemView mPfrItem;
    DataAnalysisItemView mPfrVpipItem;
    DataAnalysisItemView m3BetItem;
    DataAnalysisItemView mStlItem;
    DataAnalysisItemView mFoldStlItem;
    DataAnalysisItemView mSb3BetItem;
    DataAnalysisItemView mBb3BetItem;
    RecordAction mRecordAction;
    DataAnalysisEntity mDataAnalysisEntity;
    //
    ScrollView scrollview_data_analysis;
    LinearLayout ll_data_analysis_noshow;
    TextView tv_data_analysis_noshow;
    int handCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_analysis);
        setHeadTitle(R.string.record_data_analysis);
        handCount = getIntent().getIntExtra(Extras.EXTRA_HANDCOUNT, 0);
        initView();
        initNoShow();
    }

    public void getData(){
        mDataAnalysisEntity = GameDataDBHelper.getDataAnalysis(getApplicationContext());
        if(mDataAnalysisEntity != null){
            setData();
        }
        mRecordAction.getAnalysisData(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    mDataAnalysisEntity = JsonResolveUtil.getDataAnalysisEntity(result);
                    setData();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void initNoShow() {
        if(handCount < 100){
            tv_data_analysis_noshow.setText(String.valueOf(100 - handCount));
            scrollview_data_analysis.setVisibility(View.GONE);
            ll_data_analysis_noshow.setVisibility(View.VISIBLE);
        }else{
            scrollview_data_analysis.setVisibility(View.VISIBLE);
            ll_data_analysis_noshow.setVisibility(View.GONE);
            mRecordAction = new RecordAction(this , null);
            getData();
        }
    }

    private void initView() {
        mWsdItem = (DataAnalysisItemView) findViewById(R.id.mWsdItem);
        mWtsdItem = (DataAnalysisItemView) findViewById(R.id.mWtsdItem);
        mWwsfItem = (DataAnalysisItemView) findViewById(R.id.mWwsfItem);
        mAfItem = (DataAnalysisItemView) findViewById(R.id.mAfItem);
        mAfqItem = (DataAnalysisItemView) findViewById(R.id.mAfqItem);
        mVpipItem = (DataAnalysisItemView) findViewById(R.id.mVpipItem);
        mPfrItem = (DataAnalysisItemView) findViewById(R.id.mPfrItem);
        mPfrVpipItem = (DataAnalysisItemView) findViewById(R.id.mPfrVpipItem);
        m3BetItem = (DataAnalysisItemView) findViewById(R.id.m3BetItem);
        mStlItem = (DataAnalysisItemView) findViewById(R.id.mStlItem);
        mFoldStlItem = (DataAnalysisItemView) findViewById(R.id.mFoldStlItem);
        mSb3BetItem = (DataAnalysisItemView) findViewById(R.id.mSb3BetItem);
        mBb3BetItem = (DataAnalysisItemView) findViewById(R.id.mBb3BetItem);
        //
        scrollview_data_analysis = (ScrollView) findViewById(R.id.scrollview_data_analysis);
        ll_data_analysis_noshow = (LinearLayout) findViewById(R.id.ll_data_analysis_noshow);
        tv_data_analysis_noshow = (TextView) findViewById(R.id.tv_data_analysis_noshow);
        //设置栏目
        mWsdItem.setDataColumnType(AnalysisConfig.Column.WSD);
        mWtsdItem.setDataColumnType(AnalysisConfig.Column.WTSD);
        mWwsfItem.setDataColumnType(AnalysisConfig.Column.WWSF);
        mAfItem.setDataColumnType(AnalysisConfig.Column.AF);
        mAfqItem.setDataColumnType(AnalysisConfig.Column.AFQ);
        mVpipItem.setDataColumnType(AnalysisConfig.Column.VPIP);
        mPfrItem.setDataColumnType(AnalysisConfig.Column.PFR);
        mPfrVpipItem.setDataColumnType(AnalysisConfig.Column.PFR_VPIP);
        m3BetItem.setDataColumnType(AnalysisConfig.Column.BET);
        mStlItem.setDataColumnType(AnalysisConfig.Column.STL);
        mFoldStlItem.setDataColumnType(AnalysisConfig.Column.FOLD_STL);
        mSb3BetItem.setDataColumnType(AnalysisConfig.Column.SB_BET);
        mBb3BetItem.setDataColumnType(AnalysisConfig.Column.BB_BET);
    }

    public void setData() {
        if(mDataAnalysisEntity == null){
            return;
        }
        GameDataDBHelper.savaDataAnalysis(getApplicationContext() , mDataAnalysisEntity);
        mWsdItem.setData(AnalysisConfig.Column.WSD, mDataAnalysisEntity.wsd);
        mWtsdItem.setData(AnalysisConfig.Column.WTSD, mDataAnalysisEntity.wtsd);
        mWwsfItem.setData(AnalysisConfig.Column.WWSF, mDataAnalysisEntity.wwsf);
        mAfItem.setData(AnalysisConfig.Column.AF, mDataAnalysisEntity.af);
        mAfqItem.setData(AnalysisConfig.Column.AFQ, mDataAnalysisEntity.afq);
        mVpipItem.setData(AnalysisConfig.Column.VPIP, mDataAnalysisEntity.vpip);
        mPfrItem.setData(AnalysisConfig.Column.PFR, mDataAnalysisEntity.pfr);
        mPfrVpipItem.setData(AnalysisConfig.Column.PFR_VPIP, mDataAnalysisEntity.pfr_vpip);
        m3BetItem.setData(AnalysisConfig.Column.BET, mDataAnalysisEntity.three_bet);
        mStlItem.setData(AnalysisConfig.Column.STL, mDataAnalysisEntity.att_to_stl_lp);
        mFoldStlItem.setData(AnalysisConfig.Column.FOLD_STL, mDataAnalysisEntity.fold_blind_to_stl);
        mSb3BetItem.setData(AnalysisConfig.Column.SB_BET, mDataAnalysisEntity.sb_three_bet_steal_att);
        mBb3BetItem.setData(AnalysisConfig.Column.BB_BET, mDataAnalysisEntity.bb_three_bet_steal_att);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRecordAction != null){
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
    }
}
