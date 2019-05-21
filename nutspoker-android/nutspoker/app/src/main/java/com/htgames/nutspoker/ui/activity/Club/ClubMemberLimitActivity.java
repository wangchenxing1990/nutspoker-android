package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.config.ClubConfig;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.htgames.nutspoker.interfaces.RequestCallback;
import com.htgames.nutspoker.tool.shop.ShopDataCache;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.netease.nim.uikit.bean.ClubLimitEntity;
import com.htgames.nutspoker.ui.action.EditClubInfoAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * 群成员上限
 */
public class ClubMemberLimitActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "ClubMemberLimitActivity";
    private boolean isAreadyUpgrade = false;//是否已经升级过
    private int lastMemberLimit = 0;//已购买的成员上限档位
    private int choiceLimit = 0;//成员上限档位
    private int choiceMonth = 0;//时间档位
    LinearLayout ll_club_memberlimit_renewal;//续费
    LinearLayout ll_club_memberlimit_upgrade;//升级布局
    Button btn_limit_count_minus;
    Button btn_limit_count_add;
    Button btn_limit_time_minus;
    Button btn_limit_time_add;
    TextView tv_limit_membercount_show;
    TextView tv_limit_time_show;
    TextView tv_diamond_balance;
    TextView tv_diamong_price;
    Button btn_upgrade;
    //
    RadioGroup rg_memberlimit_time;
    RadioButton rb_memberlimit_time_1;
    RadioButton rb_memberlimit_time_2;
    RadioButton rb_memberlimit_time_3;
    RadioButton rb_memberlimit_time_4;
    //
    ProgressBar proBar_club_limit_upgrade;
    LinearLayout ll_expect_remaining_time;
    TextView tv_club_memberlimit_remaining_time;
    TextView tv_expect_remaining_time;
    //
    ArrayList<ClubLimitEntity> clubLimitConfigList = null;
    int remainingDay = 0;//剩余的天数
    private final static int monthDay = 30;
    EditClubInfoAction mEditClubInfoAction;
    String clubId = "";
    Team clubInfo;
    int needPrice = 0;//需要花费的宝石
    EasyAlertDialog buyResultDialog;
    int canBuyMonth = 12;//可以购买的月份

    public static void start(Activity activity , String clubId){
        Intent intent = new Intent(activity , ClubMemberLimitActivity.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID , clubId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_member_limit);
        setHeadTitle(R.string.club_member_limit_upgrade);
        clubId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        clubInfo = TeamDataCache.getInstance().getTeamById(clubId);
        //
        clubLimitConfigList = (ArrayList<ClubLimitEntity>) ShopDataCache.getInstance().getClubLimitList().clone();
        if(clubLimitConfigList == null && clubLimitConfigList.size() == 0) {
            clubLimitConfigList = ClubConfig.getClubLimitConfig();
        }
        if(clubLimitConfigList == null || clubLimitConfigList.size() == 0){
            finish();
        }
        mEditClubInfoAction = new EditClubInfoAction(this , null);
        initView();
        registerObservers(true);
    }

    private void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataObserver);
        }
    }

    TeamDataCache.TeamDataChangedObserver teamDataObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            for (Team team : teams) {
                if (team.getId().equals(clubId)) {
                    clubInfo = team;
                    //初始化数据
                    choiceLimit = 0;
                    choiceMonth = 0;
                    updateUI();
                    updateLimitUI();
                    updatePrice();
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        updateLimitUI();
        updatePrice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mEditClubInfoAction != null){
            mEditClubInfoAction.onDestroy();
            mEditClubInfoAction = null;
        }
        registerObservers(false);
    }

    public void updateUI(){
        int clubMemberLimitCount = ClubConstant.getClubMemberLimit(clubInfo);
        remainingDay = ClubConstant.getClubMemberLimitRemainDay(clubInfo);
        canBuyMonth = 12 - remainingDay /monthDay;
        LogUtil.i(TAG , "canBuyMonth :" + canBuyMonth);
        int size = clubLimitConfigList == null ? 0 : clubLimitConfigList.size();
        for(int i = (size - 1) ; i >= 0 ; i--){
            if(clubLimitConfigList.get(i).memberCountLimit < clubMemberLimitCount){
                clubLimitConfigList.remove(i);
            }
        }
        tv_limit_membercount_show.setText(String.valueOf(clubLimitConfigList.get(choiceLimit).memberCountLimit));
        if(remainingDay > 0){
            //续费
            isAreadyUpgrade = true;
        } else {
            //购买
            rg_memberlimit_time.check(R.id.rb_memberlimit_time_1);
            isAreadyUpgrade = false;
        }
        if(choiceLimit == 0){
            btn_limit_count_minus.setEnabled(false);
        }
        if(clubLimitConfigList != null && clubLimitConfigList.size() == 1){
            btn_limit_count_add.setEnabled(false);
        }
        if(choiceMonth == 0){
            btn_limit_time_minus.setEnabled(false);
        }
        if(canBuyMonth == 0){
            btn_limit_time_add.setEnabled(false);
        }
    }

    private void initView() {
        ll_club_memberlimit_renewal = (LinearLayout) findViewById(R.id.ll_club_memberlimit_renewal);
        ll_club_memberlimit_upgrade = (LinearLayout) findViewById(R.id.ll_club_memberlimit_upgrade);
        btn_limit_count_minus = (Button) findViewById(R.id.btn_limit_count_minus);
        btn_limit_count_add = (Button) findViewById(R.id.btn_limit_count_add);
        btn_limit_time_minus = (Button) findViewById(R.id.btn_limit_time_minus);
        btn_limit_time_add = (Button) findViewById(R.id.btn_limit_time_add);
        btn_upgrade = (Button) findViewById(R.id.btn_upgrade);
        tv_limit_membercount_show = (TextView) findViewById(R.id.tv_limit_membercount_show);
        tv_limit_time_show = (TextView) findViewById(R.id.tv_limit_time_show);
        tv_diamond_balance = (TextView) findViewById(R.id.tv_diamond_balance);
        tv_diamong_price = (TextView) findViewById(R.id.tv_diamong_price);
        //
        rg_memberlimit_time = (RadioGroup) findViewById(R.id.rg_memberlimit_time);
        rb_memberlimit_time_1 = (RadioButton) findViewById(R.id.rb_memberlimit_time_1);
        rb_memberlimit_time_2 = (RadioButton) findViewById(R.id.rb_memberlimit_time_2);
        rb_memberlimit_time_3 = (RadioButton) findViewById(R.id.rb_memberlimit_time_3);
        rb_memberlimit_time_4 = (RadioButton) findViewById(R.id.rb_memberlimit_time_4);
        //
        ll_expect_remaining_time = (LinearLayout)findViewById(R.id.ll_expect_remaining_time);
        proBar_club_limit_upgrade = (ProgressBar) findViewById(R.id.proBar_club_limit_upgrade);
        tv_club_memberlimit_remaining_time = (TextView) findViewById(R.id.tv_club_memberlimit_remaining_time);
        tv_expect_remaining_time = (TextView) findViewById(R.id.tv_expect_remaining_time);
        //
        btn_limit_count_minus.setEnabled(false);
        btn_limit_time_minus.setEnabled(false);
        btn_limit_count_minus.setOnClickListener(this);
        btn_limit_count_add.setOnClickListener(this);
        btn_limit_time_minus.setOnClickListener(this);
        btn_limit_time_add.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
        //
        rg_memberlimit_time.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            int gear = 0;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_memberlimit_time_1:
                        gear = 0;
                        break;
                    case R.id.rb_memberlimit_time_2:
                        gear = 1;
                        break;
                    case R.id.rb_memberlimit_time_3:
                        gear = 2;
                        break;
                    case R.id.rb_memberlimit_time_4:
                        gear = 3;
                        break;
                }
                choiceMonth = ClubConfig.limitTime[gear];
                updatePrice();
            }
        });
        rb_memberlimit_time_1.setText(ClubConfig.limitTimeShow[0]);
        rb_memberlimit_time_2.setText(ClubConfig.limitTimeShow[1]);
        rb_memberlimit_time_3.setText(ClubConfig.limitTimeShow[2]);
        rb_memberlimit_time_4.setText(ClubConfig.limitTimeShow[3]);
        proBar_club_limit_upgrade.setMax(360);
        updatePrice();
    }

    public void updatePrice() {
        if (!isAreadyUpgrade) {
            //售价 * 月份
            int monthSellPrice = clubLimitConfigList.get(choiceLimit).monthSellPrice;
            int month = choiceMonth;//4档
            needPrice =  monthSellPrice * month;
        } else {
            //{[（月单价1-月单价2）/30] * N} * 月单价1的折扣 + 售价1*补充的月份
            int choiceMonthOriginalPrice = clubLimitConfigList.get(choiceLimit).monthOriginalPrice;
            int choiceMonthSellPrice = clubLimitConfigList.get(choiceLimit).monthSellPrice;
            int lastMonthUnitPrice = clubLimitConfigList.get(lastMemberLimit).monthOriginalPrice;
            float currentMonthDiscount = clubLimitConfigList.get(choiceLimit).discount;
            int fillChoiceDay = remainingDay;//补充的天数(既剩余天数)
            needPrice = (int)((choiceMonthOriginalPrice - lastMonthUnitPrice) / 30f * fillChoiceDay * currentMonthDiscount)
                        +  choiceMonthSellPrice * choiceMonth;
        }
        tv_diamong_price.setText(String.valueOf(needPrice));
        if(needPrice > 0){
            btn_upgrade.setEnabled(true);
        } else{
            btn_upgrade.setEnabled(false);
        }
        if(UserPreferences.getInstance(getApplicationContext()).getDiamond() < needPrice){
            //宝石不够
            tv_diamond_balance.setTextColor(getResources().getColor(R.color.authcode_countdown_color));
        } else{
            tv_diamond_balance.setTextColor(getResources().getColor(R.color.white_main_text_color));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_limit_count_minus:
                changeMemberCount(false);
                break;
            case R.id.btn_limit_count_add:
                changeMemberCount(true);
                break;
            case R.id.btn_limit_time_minus:
                changeLimitTime(false);
                break;
            case R.id.btn_limit_time_add:
                changeLimitTime(true);
                break;
            case R.id.btn_upgrade:
                if(UserPreferences.getInstance(getApplicationContext()).getDiamond() < needPrice) {
                    showTopUpDialog();
                } else {
                    showConfimDialog();
                }
                break;
        }
    }

    public void buyClubMemberLimit() {
        mEditClubInfoAction.upgradeClub(clubId, clubLimitConfigList.get(choiceLimit).id,
                choiceMonth, new RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if(code == 0) {
                            showBuyResultDialog(true);
                            if (!isAreadyUpgrade) {
                                isAreadyUpgrade = true;
                            }
                            updateUI();
                            updateLimitUI();
                        } else if(code == ApiCode.CODE_BALANCE_INSUFFICIENT){
                            showTopUpDialog();
                        } else{
                            showBuyResultDialog(false);
                        }
                    }

                    @Override
                    public void onFailed() {
                        showBuyResultDialog(false);
                    }
                });
    }

    public void updateLimitUI() {
        if (isAreadyUpgrade) {
            //续费
            ll_club_memberlimit_renewal.setVisibility(View.VISIBLE);
            ll_club_memberlimit_upgrade.setVisibility(View.GONE);
            btn_upgrade.setText(R.string.upgrade);
            updateLimitTimeUI();
        } else {
            //升级
            ll_club_memberlimit_renewal.setVisibility(View.GONE);
            ll_club_memberlimit_upgrade.setVisibility(View.VISIBLE);
            tv_limit_membercount_show.setText(String.valueOf(clubLimitConfigList.get(choiceLimit).memberCountLimit));
            btn_upgrade.setText(R.string.buy);
        }
        tv_diamond_balance.setText(String.format(getString(R.string.club_member_limit_diamond_balance), UserPreferences.getInstance(getApplicationContext()).getDiamond()));
    }

    public void changeMemberCount(boolean isAdd) {
        if (isAdd) {
            choiceLimit = choiceLimit + 1;
        } else {
            choiceLimit = choiceLimit - 1;
        }
        tv_limit_membercount_show.setText(String.valueOf(clubLimitConfigList.get(choiceLimit).memberCountLimit));
        if (choiceLimit == 0) {
            btn_limit_count_minus.setEnabled(false);
            btn_limit_count_add.setEnabled(true);
        } else if (choiceLimit == (clubLimitConfigList.size() - 1)) {
            btn_limit_count_minus.setEnabled(true);
            btn_limit_count_add.setEnabled(false);
        } else {
            btn_limit_count_minus.setEnabled(true);
            btn_limit_count_add.setEnabled(true);
        }
        updatePrice();
    }

    public void changeLimitTime(boolean isAdd) {
        if (isAdd) {
            choiceMonth = choiceMonth + 1;
        } else {
            choiceMonth = choiceMonth - 1;
        }
        updateLimitTimeUI();
        updatePrice();
    }

    public void updateLimitTimeUI() {
        if(isAreadyUpgrade){
            tv_limit_time_show.setText(getString(R.string.club_member_limit_month_num , choiceMonth));
            if(choiceMonth == 0){
                tv_expect_remaining_time.setVisibility(View.INVISIBLE);
            } else{
                tv_expect_remaining_time.setVisibility(View.VISIBLE);
                tv_expect_remaining_time.setText(getString(R.string.club_member_limit_time_extended_hint, choiceMonth));
            }
        }
        int expectRemainingDay = remainingDay + choiceMonth * monthDay;
        final int progress = remainingDay;
        final int expectProgress = expectRemainingDay;
        if(progress > 0 && progress < 10){
            proBar_club_limit_upgrade.setProgress(10);
        } else{
            proBar_club_limit_upgrade.setProgress(progress);
        }
        proBar_club_limit_upgrade.setSecondaryProgress(expectProgress);
        if(remainingDay > 0){
            tv_club_memberlimit_remaining_time.setText(getString(R.string.club_limit_remaining, remainingDay));
        }
        //
        ll_expect_remaining_time.post(new Runnable() {
            @Override
            public void run() {
                int progressWidth = ll_expect_remaining_time.getWidth();
                int remaingTimeWidth = tv_expect_remaining_time.getWidth();
                int marginLeft = (int) ((choiceMonth * monthDay + remainingDay)/ 360f * progressWidth - remaingTimeWidth / 2);
                LogUtil.i(TAG, "width : " + progressWidth + ";marginLeft :" + marginLeft + "; expectProgress:" + expectProgress);
                if(marginLeft <= 0){
                    marginLeft = 0;
                } else if (marginLeft > (progressWidth - remaingTimeWidth)) {
                    marginLeft = progressWidth - remaingTimeWidth;
                }
                LinearLayout.LayoutParams lineParams = (LinearLayout.LayoutParams) tv_expect_remaining_time.getLayoutParams();
                lineParams.setMargins(marginLeft , 0, 0, 0);
                tv_expect_remaining_time.setLayoutParams(lineParams);
            }
        });
        //
        if(choiceMonth == 0){
            btn_limit_time_minus.setEnabled(false);
            if(canBuyMonth > 0){
                btn_limit_time_add.setEnabled(true);
            } else{
                btn_limit_time_add.setEnabled(false);
            }
        } else if(choiceMonth < canBuyMonth){
            btn_limit_time_minus.setEnabled(true);
            btn_limit_time_add.setEnabled(true);
        } else{
            btn_limit_time_minus.setEnabled(true);
            btn_limit_time_add.setEnabled(false);
        }
//        if (choiceMonth == 0 && canBuyMonth > 0) {
//            btn_limit_time_minus.setEnabled(false);
//            btn_limit_time_add.setEnabled(true);
//        } else if(choiceMonth < canBuyMonth) {
//            btn_limit_time_minus.setEnabled(true);
//            btn_limit_time_add.setEnabled(true);
//        } else{
//            btn_limit_time_minus.setEnabled(true);
//            btn_limit_time_add.setEnabled(false);
//        }
    }

    public void showConfimDialog() {
        String message = getString(R.string.upgrade_club_member_limit_config);
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        buyClubMemberLimit();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    public void showBuyResultDialog(boolean success) {
        String message = getString(R.string.buy_failure);
        if(success){
            message = getString(R.string.buy_success);
        }
        buyResultDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, null,
                message, getString(R.string.ok), false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyResultDialog.dismiss();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            buyResultDialog.show();
        }
    }

    EasyAlertDialog topUpDialog;

    public void showTopUpDialog() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.diamond_topup_dialog_tip), getString(R.string.buy) ,getString(R.string.cancel) , true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            topUpDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(ClubMemberLimitActivity.this, ShopActivity.TYPE_SHOP_DIAMOND);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }
}
