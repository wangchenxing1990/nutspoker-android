package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.GameDataEntity;
import com.netease.nim.uikit.bean.InsuranceEntity;
import com.netease.nim.uikit.bean.RecordEntity;
import com.netease.nim.uikit.bean.WinChipEntity;
import com.netease.nim.uikit.bean.YearMonthEy;
import com.htgames.nutspoker.data.common.UserConstant;
import com.netease.nim.uikit.constants.VipConstants;
import com.htgames.nutspoker.db.GameDataDBHelper;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.tool.DateCalendarTools;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.view.statistics.DailyGainView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class RecordNormalActivity extends BaseActivity {

    @BindView(R.id.iv_record_data_analysis_lock) View iv_record_data_analysis_lock;
    @BindView(R.id.iv_record_statistics_lock) View iv_record_statistics_lock;
    @BindView(R.id.tv_record_statistics_lock) View tv_record_statistics_lock;
    @BindView(R.id.tv_record_data_analysis_lock) View tv_record_data_analysis_lock;
    @BindView(R.id.tv_record_statistics) TextView tv_record_statistics;
    @BindView(R.id.tv_record_data_analysis) TextView tv_record_data_analysis;

    @BindView(R.id.tv_insurance_money) TextView mInsuranceMoney;
    @BindView(R.id.tv_insurance_times) TextView mInsuranceTimes;
    @BindView(R.id.tv_total_buy) TextView mTotalBuy;
    @BindView(R.id.tv_total_pay) TextView mTotalPay;
    @BindView(R.id.tv_total_buy_times) TextView mBuyTimes;
    @BindView(R.id.tv_total_lucky) TextView mLuckyTimes;

    @BindView(R.id.tv_insurance_my_money) TextView mMyInsMoney;
    @BindView(R.id.tv_insurance_create_times) TextView mMyCreateTimes;
    @BindView(R.id.tv_total_my_buy) TextView mMyBuy;
    @BindView(R.id.tv_total_my_pay) TextView mMyPay;

    @BindView(R.id.day_data) DailyGainView mStatisticsDay;
    @BindView(R.id.month_data) DailyGainView mStatisticsMonth;
    //选中日
    @OnCheckedChanged(R.id.radioBtn_day) void checkDay(boolean checked){
        if(checked){
            if(mType != RecordAction.RecordNormal_TypeDay) {
                mType = RecordAction.RecordNormal_TypeDay;
                updateListUi(mType);
            }

            if(mWinChipListDay == null)
                getNetData(mType);//获取日数据
        }
    }
    //选中月
    @OnCheckedChanged(R.id.radioBtn_month) void checkMonth(boolean checked){
        if(checked){
            if(mType != RecordAction.RecordNormal_TypeMonth){
                mType = RecordAction.RecordNormal_TypeMonth;

                if(mWinChipListMonth == null){
                    getNetData(mType);//获取月数据
                } else {
                    updateListUi(mType);
                }
            } else {
                if(mWinChipListMonth == null){
                    getNetData(mType);//获取月数据
                }
            }
        }
    }

    @OnClick(R.id.rl_record_statistics) void clickStatistics() {
        if (UserConstant.getMyVipLevel() == VipConstants.VIP_LEVEL_NOT) {
            showVipDialog();
        } else {
            Intent intent = new Intent(this, StatisticsActivity.class);
            intent.putExtra("data", mRecordEntity);
            startActivity(intent);
        }
    }

    @OnClick(R.id.rl_record_data_analysis) void clickAnalysis(){
        if(UserConstant.getMyVipLevel() == VipConstants.VIP_LEVEL_NOT){
            showVipDialog();
        } else{
            Intent analysisIntent = new Intent(this, DataAnalysisActivity.class);
            if(mRecordEntity != null) {
                analysisIntent.putExtra(Extras.EXTRA_HANDCOUNT, mRecordEntity.hands);
            }
            startActivity(analysisIntent);
        }
    }

    public static void StartActivity(Activity activity,String back , RecordEntity mRecordEntity) {
        Intent intent = new Intent(activity, RecordNormalActivity.class);
        intent.putExtra(Extra_Back, back);
        intent.putExtra("data", mRecordEntity);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_normal);
        mUnbinder = ButterKnife.bind(this);
        mRecordEntity = (RecordEntity) getIntent().getSerializableExtra("data");
        //view
        mStatisticsMonth.setType(1);//设置月类型
        setHeadTitle(R.string.normal_record);

        //var
        mRecordAction = new RecordAction(this, null);
        vipLevel = -1;

        //local data
        getLocalRecordData();

        //init view 临时数据填充
        List<WinChipEntity> tempList = new ArrayList<>();
        initWinChip(tempList);
        mStatisticsDay.setDailyGainInfo(tempList,0,null);
        mStatisticsDay.setInit(false);

        List<WinChipEntity> tempList2 = new ArrayList<>();
        initWinChipMonth(tempList2);
        mStatisticsMonth.setDailyGainInfo(tempList2,0,null);
        mStatisticsMonth.setInit(false);

        updateUi();

        //net data
        mType = RecordAction.RecordNormal_TypeDay;
        getNetData(mType);//获取日数据
        getInsuranceData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mRecordAction != null){
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateVipUI();
    }

    public void updateVipUI(){
        if(vipLevel == UserConstant.getMyVipLevel()){
            return;
        }
        vipLevel = UserConstant.getMyVipLevel();
        if(vipLevel == VipConstants.VIP_LEVEL_NOT) {
            iv_record_data_analysis_lock.setVisibility(View.VISIBLE);
            iv_record_statistics_lock.setVisibility(View.VISIBLE);
            tv_record_statistics_lock.setVisibility(View.VISIBLE);
            tv_record_data_analysis_lock.setVisibility(View.VISIBLE);
            tv_record_statistics.setTextColor(getResources().getColor(R.color.gray_auxiliary_text_color));
            tv_record_data_analysis.setTextColor(getResources().getColor(R.color.gray_auxiliary_text_color));
        } else {
            iv_record_data_analysis_lock.setVisibility(View.GONE);
            iv_record_statistics_lock.setVisibility(View.GONE);
            tv_record_statistics_lock.setVisibility(View.GONE);
            tv_record_data_analysis_lock.setVisibility(View.GONE);
            tv_record_statistics.setTextColor(getResources().getColor(R.color.record_column_title_color));
            tv_record_data_analysis.setTextColor(getResources().getColor(R.color.record_column_title_color));
        }
    }

    void updateListUi(int type){
        if(type == RecordAction.RecordNormal_TypeDay) {
            if(mWinChipListDay != null && mWinChipListDay.data != null && mWinChipListDay.data.size() != 0){

                if(!mStatisticsDay.isInit()){
                    dealWinChipDay(mWinChipListDay.data);
                    mStatisticsDay.setDailyGainInfo(mWinChipListDay.data,maxBalance,mWinChipListDay.insure);
                }
            }

            mStatisticsDay.setVisibility(View.VISIBLE);
            mStatisticsMonth.setVisibility(View.GONE);
        }else if(type == RecordAction.RecordNormal_TypeMonth){
            if(mWinChipListMonth != null && mWinChipListMonth.data != null && mWinChipListMonth.data.size() != 0){

                if(!mStatisticsMonth.isInit()) {
                    dealWinChipMonth(mWinChipListMonth.data);
                    mStatisticsMonth.setDailyGainInfo(mWinChipListMonth.data, maxBalance, mWinChipListMonth.insure);
                }
            }

            mStatisticsDay.setVisibility(View.GONE);
            mStatisticsMonth.setVisibility(View.VISIBLE);
        }
    }

    void updateUi(){
        if(mInsuranceData != null){
            WealthHelper.SetMoneyText(mInsuranceMoney,mInsuranceData.buy_sum-mInsuranceData.pay_sum,this);
            WealthHelper.SetMoneyText(mMyInsMoney,mInsuranceData.my_buy_sum-mInsuranceData.my_pay_sum,this);

            mInsuranceTimes.setText(""+mInsuranceData.trigger_count);
            mTotalBuy.setText(""+mInsuranceData.buy_sum);
            mTotalPay.setText(""+mInsuranceData.pay_sum);
            mBuyTimes.setText(""+mInsuranceData.buy_count);
            mLuckyTimes.setText(""+mInsuranceData.hit_count);

            mMyCreateTimes.setText(""+mInsuranceData.my_games);
            mMyBuy.setText(""+mInsuranceData.my_buy_sum);
            mMyPay.setText(""+mInsuranceData.my_pay_sum);
        }
    }

    void getLocalRecordData() {
        //获取保险数据
        mInsuranceData = GameDataDBHelper.getInsurance(this);
    }

    boolean getNetData(final int type){

        if(mBooleenRequest[type])
            return false;

        DialogMaker.showProgressDialog(this,"",false);
        mRecordAction.getRecordNormalData(type, new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if(code == 0){
                    try{
                        Type dataType = new TypeToken<CommonBeanT<GameDataEntity>>(){}.getType();
                        CommonBeanT<GameDataEntity> gd = GsonUtils.getGson().fromJson(result,dataType);

                        if(gd != null){
                            if(type == RecordAction.RecordNormal_TypeDay){
                                mWinChipListDay = gd.data;
                            } else if(type == RecordAction.RecordNormal_TypeMonth){
                                mWinChipListMonth = gd.data;
                            }
                            updateListUi(type);
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    mBooleenRequest[type] = true;
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, R.string.get_failuer, Toast.LENGTH_SHORT).show();
                updateListUi(type);
            }
        });

        return true;
    }

    void getInsuranceData(){
        mRecordAction.getRecordInsuranceData(new RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if(code == 0){
                    InsuranceEntity ie = null;
                    try{
                        Type type = new TypeToken<CommonBeanT<InsuranceEntity>>(){}.getType();
                        CommonBeanT<InsuranceEntity> cbt = GsonUtils.getGson().fromJson(result,type);
                        ie = cbt.data;
                        GameDataDBHelper.setInsurance(RecordNormalActivity.this,ie);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if(ie != null) {//更新数据，如果失败，那么就读取本地数据
                        mInsuranceData = ie;
                        updateUi();
                    }
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    EasyAlertDialog vipDialog;

    public void showVipDialog() {
        if (vipDialog == null) {
            vipDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.record_vip_dialog_tip), getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            vipDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(RecordNormalActivity.this, ShopActivity.TYPE_SHOP_VIP);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            vipDialog.show();
        }
    }

    void initWinChip(List<WinChipEntity> list) {
        List<WinChipEntity> winChipList = list;
        winChipList.clear();
        for (int day = -4; day <= 34; day++) {
            long dayTime = DateCalendarTools.getBeforeDay(-day);
            winChipList.add(0, new WinChipEntity(0, dayTime));
        }
    }

    public void initWinChipMonth(List<WinChipEntity> list) {
        List<WinChipEntity> winChipList = list;
        winChipList.clear();
        for (int day = -4; day < 16; day++) {
            YearMonthEy yearMonthEy = DateCalendarTools.getBeforeMonth(-day);
            winChipList.add(0, new WinChipEntity(0, yearMonthEy.month,yearMonthEy.year));
        }
    }

    public void dealWinChipDay(List<WinChipEntity> list) {
        List<WinChipEntity> winChipList = list;
        Map<Long , Integer> winChipPositionMap = new HashMap<>();
        int size = winChipList.size();
        //将存在的存储为节点。用于查重
        maxBalance = 0;
        for (int i = 0; i < size; i++) {
            WinChipEntity winChipEntity = winChipList.get(i);
            winChipPositionMap.put(winChipEntity._id, winChipEntity.count);
            //求出最大的收入
            if (Math.abs(winChipEntity.count) > maxBalance) {
                maxBalance = Math.abs(winChipEntity.count);
            }
        }
        winChipList.clear();
        long currentData = DateCalendarTools.getTodayDate();
        if (winChipPositionMap.containsKey(currentData)) {
            winChipList.add(0, new WinChipEntity(winChipPositionMap.get(currentData), currentData));
        } else {
            winChipList.add(0, new WinChipEntity(0, currentData));
        }
        //循环处理数据
        for (int day = 1; day <= 34; day++) {
            long dayTime = DateCalendarTools.getBeforeDay(-day);
            if (winChipPositionMap.containsKey(dayTime)) {
                winChipList.add(0, new WinChipEntity(winChipPositionMap.get(dayTime), dayTime));
            } else {
                winChipList.add(0, new WinChipEntity(0, dayTime));
            }
        }
        for (int day = 1; day <= 4; day++) {
            long dayTime = DateCalendarTools.getBeforeDay(day);
            WinChipEntity winChipEntity = new WinChipEntity(0, dayTime);
            winChipList.add(winChipEntity);
        }
    }

    public void dealWinChipMonth(List<WinChipEntity> list){
        List<WinChipEntity> winChipList = list;
        Map<Long , Integer> winChipPositionMap = new HashMap<>();
        int size = winChipList.size();
        //将存在的存储为节点。用于查重
        maxBalance = 0;
        for (int i = 0; i < size; i++) {
            WinChipEntity winChipEntity = winChipList.get(i);
            winChipPositionMap.put(winChipEntity._id, winChipEntity.count);
            //求出最大的收入
            if (Math.abs(winChipEntity.count) > maxBalance) {
                maxBalance = Math.abs(winChipEntity.count);
            }
        }
        winChipList.clear();
        long currentMonth = DateCalendarTools.getThisMonth();
        if (winChipPositionMap.containsKey(currentMonth)) {
            winChipList.add(0, new WinChipEntity(winChipPositionMap.get(currentMonth), currentMonth,DateCalendarTools.getThisYear()));
        } else {
            winChipList.add(0, new WinChipEntity(0, currentMonth,DateCalendarTools.getThisYear()));
        }
        //循环处理数据
        for (int day = 1; day < 16; day++) {
            YearMonthEy yearMonthEy = DateCalendarTools.getBeforeMonth(-day);
            if (winChipPositionMap.containsKey(yearMonthEy.month) && day < 12) {
                //数据在一年以内
                winChipList.add(0, new WinChipEntity(winChipPositionMap.get(yearMonthEy.month), yearMonthEy.month,yearMonthEy.year));
            } else {
                winChipList.add(0, new WinChipEntity(0,yearMonthEy.month,yearMonthEy.year));
            }
        }
        for (int day = 1; day <= 4; day++) {
            YearMonthEy yearMonthEy = DateCalendarTools.getBeforeMonth(day);
            WinChipEntity winChipEntity = new WinChipEntity(0, yearMonthEy.month,yearMonthEy.year);
            winChipList.add(winChipEntity);
        }
    }
    /**
     * ********************* data ****************************************************
     */
    private int maxBalance = 0;

    RecordEntity mRecordEntity;
//    DataStatisticsEntity mDataStatisticsEntity;
    RecordAction mRecordAction;

    //保险数据
    InsuranceEntity mInsuranceData;
    int vipLevel = -1;
    /**
     * 0:日
     * 1:月
     */
    int mType = -1;

    GameDataEntity mWinChipListDay;
    GameDataEntity mWinChipListMonth;

    boolean[] mBooleenRequest = {false,false};
}
