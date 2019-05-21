package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.MatchEntity;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.CircleMatchView;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordMatchActivity extends BaseActivity {

    public static final String ExtraData_Sng = "sng";
    public static final String ExtraData_MtSng = "mtsng";
    public static final String ExtraData_Mtt = "mtt";

    @BindView(R.id.tv_sng_money) TextView mUiSngMoney;
    @BindView(R.id.tv_mtsng_money) TextView mUiMtSngMoney;
    @BindView(R.id.tv_mtt_money) TextView mUiMttMoney;

    @BindView(R.id.tv_sng_join_fee) TextView mUiSngJoinFee;
    @BindView(R.id.tv_mtsng_join_fee) TextView mUiMtSngJoinFee;
    @BindView(R.id.tv_mtt_join_fee) TextView mUiMttJoinFee;

    @BindView(R.id.tv_sng_reward) TextView mUiSngReward;
    @BindView(R.id.tv_mtsng_reward) TextView mUiMtSngReward;
    @BindView(R.id.tv_mtt_reward) TextView mUiMttReward;

    @BindView(R.id.tv_sng_game_times) TextView mUiSngGameTimes;
    @BindView(R.id.tv_mtsng_game_times) TextView mUiMtSngGameTimes;
    @BindView(R.id.tv_mtt_game_times) TextView mUiMttGameTimes;

    @BindView(R.id.tv_sng_game_in_rewardlist) TextView mUiSngInRewardlist;
    @BindView(R.id.tv_mtsng_game_in_rewardlist) TextView mUiMtSngInRewardlist;
    @BindView(R.id.tv_mtt_game_in_rewardlist) TextView mUiMttInRewardlist;

    @BindView(R.id.tv_sng_game_in_finaltable) TextView mUiSngInFinals;
    @BindView(R.id.tv_mtsng_game_in_finaltable) TextView mUiMtSngInFinals;
    @BindView(R.id.tv_mtt_game_in_finaltable) TextView mUiMttInFinals;

    @BindView(R.id.rl_sng_data) View mUiSngData;
    @BindView(R.id.rl_mtsng_data) View mUiMtSngData;
    @BindView(R.id.rl_mtt_data) View mUiMttData;

    @BindView(R.id.tv_sng_nodata) View mUiSngNodata;
    @BindView(R.id.tv_mtsng_nodata) View mUiMtSngNodata;
    @BindView(R.id.tv_mtt_nodata) View mUiMttNodata;

    @BindView(R.id.match_sng_circle) CircleMatchView mCircleSng;
    @BindView(R.id.match_mtsng_circle) CircleMatchView mCircleMtSng;
    @BindView(R.id.match_mtt_circle) CircleMatchView mCircleMtt;

    public static void StartActivity(Activity activity,String back,int sng,int mtsng,int mtt){
        Intent intent = new Intent(activity,RecordMatchActivity.class);
        intent.putExtra(Extra_Back,back);
        intent.putExtra(ExtraData_Sng,sng);
        intent.putExtra(ExtraData_MtSng,mtsng);
        intent.putExtra(ExtraData_Mtt,mtt);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_match);
        mUnbinder = ButterKnife.bind(this);

        //parse data
        mSngMoney = getIntent().getIntExtra(ExtraData_Sng,0);
        mMtSngMoney = getIntent().getIntExtra(ExtraData_MtSng,0);
        mMttMoney = getIntent().getIntExtra(ExtraData_Mtt,0);

        //init data
        mRecordAction = new RecordAction(this,null);

        getNetData();

        //init view;
        setHeadTitle(R.string.match_record);
        updateUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRecordAction.onDestroy();
    }

    void getNetData(){
        //从本地获取一下
        if(mMatchEntity == null)
            mMatchEntity = GameDataDBHelper.getMatchData(this);

        mRecordAction.getRecordMatchData(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0){
                    MatchEntity me = null;
                    try{
                        Type type = new TypeToken<CommonBeanT<MatchEntity>>(){}.getType();
                        CommonBeanT<MatchEntity> cbt = GsonUtils.getGson().fromJson(result,type);
                        me = cbt.data;
                        GameDataDBHelper.setMatchData(RecordMatchActivity.this,me);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(me != null){
                        mMatchEntity = me;
                        updateUi();
                    }
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    void updateUi(){
        WealthHelper.SetMoneyText(mUiSngMoney,mSngMoney,this);
        WealthHelper.SetMoneyText(mUiMtSngMoney,mMtSngMoney,this);
        WealthHelper.SetMoneyText(mUiMttMoney,mMttMoney,this);

        if(mMatchEntity.games_sng > 0){
            mUiSngJoinFee.setText(""+mMatchEntity.match_fee_sng);
            mUiSngReward.setText(""+mMatchEntity.reward_sng);
            mUiSngGameTimes.setText(getString(R.string.game_match_times_format,mMatchEntity.games_sng));
            WealthHelper.SetGameTimesText(mUiSngInFinals,mMatchEntity.in_finals_sng,this,false);
            WealthHelper.SetGameTimesText(mUiSngInRewardlist,mMatchEntity.in_reward_sng,this,true);
            mCircleSng.setMiddleMax(mMatchEntity.games_sng);
            mCircleSng.setMiddleCur(mMatchEntity.in_reward_sng);

            mUiSngData.setVisibility(View.VISIBLE);
            mUiSngNodata.setVisibility(View.GONE);
        } else{
            mUiSngData.setVisibility(View.GONE);
            mUiSngNodata.setVisibility(View.VISIBLE);
        }

        if(mMatchEntity.games_sng_mtt > 0){
            mUiMtSngJoinFee.setText(""+mMatchEntity.match_fee_sng_mtt);
            mUiMtSngReward.setText(""+mMatchEntity.reward_sng_mtt);
            mUiMtSngGameTimes.setText(getString(R.string.game_match_times_format,mMatchEntity.games_sng_mtt));
            WealthHelper.SetGameTimesText(mUiMtSngInFinals,mMatchEntity.in_finals_sng_mtt,this,false);
            WealthHelper.SetGameTimesText(mUiMtSngInRewardlist,mMatchEntity.in_reward_sng_mtt,this,true);
            mCircleMtSng.setMiddleMax(mMatchEntity.games_sng_mtt);
            mCircleMtSng.setOutMax(mMatchEntity.games_sng_mtt);
            mCircleMtSng.setMiddleCur(mMatchEntity.in_reward_sng_mtt);
            mCircleMtSng.setOutCur(mMatchEntity.in_finals_sng_mtt);

            mUiMtSngData.setVisibility(View.VISIBLE);
            mUiMtSngNodata.setVisibility(View.GONE);
        } else {
            mUiMtSngData.setVisibility(View.GONE);
            mUiMtSngNodata.setVisibility(View.VISIBLE);
        }

        if(mMatchEntity.games_mtt > 0){
            mUiMttJoinFee.setText(""+mMatchEntity.match_fee_mtt);
            mUiMttReward.setText(""+mMatchEntity.reward_mtt);
            mUiMttGameTimes.setText(getString(R.string.game_match_times_format,mMatchEntity.games_mtt));
            WealthHelper.SetGameTimesText(mUiMttInFinals,mMatchEntity.in_finals_mtt,this,false);
            WealthHelper.SetGameTimesText(mUiMttInRewardlist,mMatchEntity.in_reward_mtt,this,true);
            mCircleMtt.setMiddleMax(mMatchEntity.games_mtt);
            mCircleMtt.setOutMax(mMatchEntity.games_mtt);
            mCircleMtt.setMiddleCur(mMatchEntity.in_reward_mtt);
            mCircleMtt.setOutCur(mMatchEntity.in_finals_mtt);

            mUiMttData.setVisibility(View.VISIBLE);
            mUiMttNodata.setVisibility(View.GONE);
        } else {
            mUiMttData.setVisibility(View.GONE);
            mUiMttNodata.setVisibility(View.VISIBLE);
        }
    }

    int mSngMoney;
    int mMtSngMoney;
    int mMttMoney;
    RecordAction mRecordAction;
    MatchEntity mMatchEntity;
}
