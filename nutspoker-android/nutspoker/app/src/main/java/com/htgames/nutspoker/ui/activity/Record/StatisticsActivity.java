package com.htgames.nutspoker.ui.activity.Record;

import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.DataStatisticsEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.HorizontalProgressBar;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;

import java.lang.reflect.Type;

import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 统计
 */
public class StatisticsActivity extends BaseActivity{
    private final static String TAG = "StatisticsActivity";

    TextView tv_statistics_paiju_count;//总牌局
    TextView tv_statistics_handcard_count;//手牌数
    TextView tv_statistics_winrate_all;//
    TextView tv_statistics_winrate_showdown;//
    TextView tv_statistics_winrate_look_card;//
    TextView tv_statistics_winrate_follow;//
    TextView tv_statistics_gain_all;//
    TextView tv_statistics_gain_hundred_hand;//
    TextView tv_statistics_gain_hundred_hand_bigblind;//
    TextView tv_statistics_gain_allin;//
    HorizontalProgressBar proBar_winrate_all;
    HorizontalProgressBar proBar_winrate_showdown;
    HorizontalProgressBar proBar_winrate_look_card;
    HorizontalProgressBar proBar_winrate_follow;

    LinearLayout ll_winrate;//胜率分布
    LinearLayout ll_daily_gain;//盈利情况
    TextView tv_analysis_table_title;

    //全部，七天，月
    static boolean[] sBooleenRequest = {false,false,false};
    //选择全部
    @OnCheckedChanged(R.id.radio_head_all) void checkAll(boolean checked){
        if(checked){
            if(mType != 0){
                mType = 0;
                onChangeType(mType);
            }
        }
    }

    //选择最近七天
    @OnCheckedChanged(R.id.radio_head_week) void checkWeek(boolean checked){
        if(checked){
            if(mType != 1){
                mType = 1;
                onChangeType(mType);
            }
        }
    }

    //选择一月
    @OnCheckedChanged(R.id.radio_head_month) void checkMonth(boolean checked){
        if(checked){
            if(mType != 2){
                mType = 2;
                onChangeType(mType);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mUnbinder = ButterKnife.bind(this);

        //var
        mRecordAction = new RecordAction(this,null);

        //view
        setHeadTitle(R.string.record_statistics);
        initView();

        //data
        checkAll(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRecordAction.onDestroy();
    }

    void getLocalData(int type){
        sDataStatisticsEntities[type] = GameDataDBHelper.getDataStatistics(this,type);
    }
    boolean getNetData(final int type){
        //今天请求过了，就不在请求了
        if(sBooleenRequest[type])
            return false;

        mRecordAction.getRecordDataFind(type, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0)
                {
                    DataStatisticsEntity data = null;
                    try{
                        Type type = new TypeToken<CommonBeanT<DataStatisticsEntity>>(){}.getType();
                        CommonBeanT<DataStatisticsEntity> cbt = GsonUtils.getGson().fromJson(result,type);
                        data = cbt.data;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(data != null){
                        sDataStatisticsEntities[type] = data;
                        setData(type);
                    }
                    sBooleenRequest[type] = true;
                }

            }

            @Override
            public void onFailed() {

            }
        });

        return  true;
    }

    private void setData(int type) {
        DataStatisticsEntity dataStatisticsEntity = sDataStatisticsEntities[type];
        if(dataStatisticsEntity != null){
            tv_statistics_paiju_count.setText(String.valueOf(dataStatisticsEntity.games));//牌局数量
            tv_statistics_handcard_count.setText(String.valueOf(dataStatisticsEntity.hands));//手牌数量
            tv_statistics_winrate_all.setText(dataStatisticsEntity.hands_count_won + "%");//总胜率
            tv_statistics_winrate_showdown.setText(dataStatisticsEntity.wsd + "%");//摊牌胜率
            tv_statistics_winrate_look_card.setText(dataStatisticsEntity.wwsf + "%");//看翻牌胜率
            tv_statistics_winrate_follow.setText(dataStatisticsEntity.wsd_after_river + "%");//河牌圈跟注胜率
            //
            int allWin = dataStatisticsEntity.my_c_won;//总盈利
            int hundredHandsWin = dataStatisticsEntity.hundred_hands_win;//平均百手盈利
            int hundredBigWin = dataStatisticsEntity.big_blind_won_cnt;//平均百手赢得大盲
            int allInWin = dataStatisticsEntity.allin_chips_avg;//平均all in 筹码
            //
            WealthHelper.SetMoneyText(tv_statistics_gain_all,allWin,this);
            if(dataStatisticsEntity.hands >= GameConstants.HUNDRED_HANDS_COUNT){
                WealthHelper.SetMoneyText(tv_statistics_gain_hundred_hand,hundredHandsWin,this);
            } else{
                tv_statistics_gain_hundred_hand.setText(R.string.hundred_hand_not_reached);
                tv_statistics_gain_hundred_hand.setTextColor(getResources().getColor(R.color.statistics_table_title_color));
            }
            if(dataStatisticsEntity.hands >= GameConstants.HUNDRED_HANDS_COUNT){
                WealthHelper.SetMoneyText(tv_statistics_gain_hundred_hand_bigblind,hundredBigWin,this);
            } else{
                tv_statistics_gain_hundred_hand_bigblind.setText(R.string.hundred_hand_not_reached);
                tv_statistics_gain_hundred_hand_bigblind.setTextColor(getResources().getColor(R.color.statistics_table_title_color));
            }
            WealthHelper.SetMoneyText(tv_statistics_gain_allin,allInWin,this);
            setStatisticsProgressData(dataStatisticsEntity);
        }
    }

    private void setStatisticsProgressData(DataStatisticsEntity data) {

        proBar_winrate_all.setInterpolator(new DecelerateInterpolator());
        proBar_winrate_showdown.setInterpolator(new DecelerateInterpolator());
        proBar_winrate_look_card.setInterpolator(new DecelerateInterpolator());
        proBar_winrate_follow.setInterpolator(new DecelerateInterpolator());

        proBar_winrate_all.setProgressWithAnimation(data.hands_count_won);
        proBar_winrate_showdown.setProgressWithAnimation(data.wsd);
        proBar_winrate_look_card.setProgressWithAnimation(data.wwsf);
        proBar_winrate_follow.setProgressWithAnimation(data.wsd_after_river);

    }

    private void initView() {
        tv_statistics_paiju_count = (TextView)findViewById(R.id.tv_statistics_paiju_count);
        tv_statistics_handcard_count = (TextView)findViewById(R.id.tv_statistics_handcard_count);
        tv_statistics_winrate_all = (TextView)findViewById(R.id.tv_statistics_winrate_all);
        tv_statistics_winrate_showdown = (TextView)findViewById(R.id.tv_statistics_winrate_showdown);
        tv_statistics_winrate_look_card = (TextView)findViewById(R.id.tv_statistics_winrate_look_card);
        tv_statistics_winrate_follow = (TextView)findViewById(R.id.tv_statistics_winrate_follow);
        tv_statistics_gain_all = (TextView)findViewById(R.id.tv_statistics_gain_all);
        tv_statistics_gain_hundred_hand = (TextView)findViewById(R.id.tv_statistics_gain_hundred_hand);
        tv_statistics_gain_hundred_hand_bigblind = (TextView)findViewById(R.id.tv_statistics_gain_hundred_hand_bigblind);
        tv_statistics_gain_allin = (TextView)findViewById(R.id.tv_statistics_gain_allin);
        proBar_winrate_all = (HorizontalProgressBar)findViewById(R.id.proBar_winrate_all);
        proBar_winrate_showdown = (HorizontalProgressBar)findViewById(R.id.proBar_winrate_showdown);
        proBar_winrate_look_card = (HorizontalProgressBar)findViewById(R.id.proBar_winrate_look_card);
        proBar_winrate_follow = (HorizontalProgressBar)findViewById(R.id.proBar_winrate_follow);
        ll_winrate = (LinearLayout)findViewById(R.id.ll_winrate);
        ll_daily_gain = (LinearLayout)findViewById(R.id.ll_daily_gain);
        tv_analysis_table_title = (TextView)findViewById(R.id.tv_analysis_table_title);
    }

    class DataSubscrib extends Subscriber<Integer>{

        @Override
        public void onCompleted() {
            DialogMaker.dismissProgressDialog();
        }

        @Override
        public void onError(Throwable e) {
            DialogMaker.dismissProgressDialog();
        }

        @Override
        public void onNext(Integer integer) {
            setData(integer);
        }
    }

    void onChangeType(int type){
        if(sDataStatisticsEntities[type] == null){
            Observable.just(type)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            DialogMaker.showProgressDialog(StatisticsActivity.this,"",false);
                        }
                    })
                    //.subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .map(new Func1<Integer, Integer>() {
                        @Override
                        public Integer call(Integer integer) {
                            if(!getNetData(integer))
                                getLocalData(integer);
                            return integer;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DataSubscrib());
        } else {
            setData(type);
        }
    }

    /************************************************************************************/
    /**
     * 0 全部，1近一周，2近一月
     */
    int mType = -1;
    RecordAction mRecordAction;
    static DataStatisticsEntity[] sDataStatisticsEntities = {null,null,null};
}
