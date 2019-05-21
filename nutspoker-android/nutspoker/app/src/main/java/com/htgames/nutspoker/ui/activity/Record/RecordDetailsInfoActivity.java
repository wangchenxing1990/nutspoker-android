package com.htgames.nutspoker.ui.activity.Record;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import com.netease.nim.uikit.api.ApiConfig;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.adapter.RecordDetailsInfoAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.htgames.nutspoker.view.RecordCalculatePopView;
import com.htgames.nutspoker.view.ScrollListenerHorizontalScrollView;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.view.widget.CustomListView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.constant.Extras;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 */
public class RecordDetailsInfoActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "RecordDetailsInfo";
    public final static int TYPE_BILL = 1;
    public final static int TYPE_BILL_LIST = 2;
    CustomListView lv_record;
    TextView tv_game_name;
    TextView tv_game_date;
    TextView tv_game_members;
    TextView tv_game_blinds;
    TextView tv_game_duration;
    GameBillEntity gameBillEntity;
    ArrayList<GameMemberEntity> gameMemberList;
    RecordDetailsInfoAdapter mRecordDetailsInfoAdapter;
    TextView btn_back;
    TextView btn_share;
    RelativeLayout rl_record_details_info;
    LinearLayout ll_record_details_column;
    LinearLayout ll_details_bg;
    LinearLayout ll_record_total;
    //
    TextView tv_record_all_buy;
    TextView tv_record_all_insurance;
    TextView tv_record_all_insurance_gain;
    //
    ScrollView scrollView;
    ImageView iv_details_logo;
    //
    TextView tv_game_multiple_game_count;
    TextView tv_game_multiple_name;
    //
    ShareView mShareView;
    Bitmap shareBitmap;
    ArrayList<GameBillEntity> gameBillList;
    int type = TYPE_BILL;
    private HashMap<String, GameMemberEntity> gameMemberMap;
    private HashMap<String, Integer[]> userGainMap;//用户盈利列表（包含保险）
    private HashMap<String, Integer[]> userInsuranceGainMap;
    LinearLayout ll_record_details_single;
    LinearLayout ll_record_details_multiple;
    StringBuffer multipleNameBuffer = new StringBuffer();
    int multipleTotalCount = 0;
    //
    TextView tv_column_all_buy_title;
    TextView tv_column_gain_title;
    TextView tv_column_insurance_title;
    TextView tv_column_insurance_gain_title;
    LinearLayout ll_record_scroll;
    LinearLayout ll_scroll_gain;
    private int uniformNum = 3 + 13 + 2;
    private int windowWidth = 0;
    private int itemWidth = 0;
    private int itemMaginTop = 10;
    private int itemHeight = 0;
    TextView[][] gainTextViews;
    TextView[][] insuranceTextViews;
    TextView[] allGainTextViews;
    LinearLayout ll_scroll_userinfo;
    LinearLayout ll_scroll_all_gian;
    LinearLayout ll_column_normal_center;
    LinearLayout ll_scroll_column;//拖动栏目
    ScrollListenerHorizontalScrollView columnHorizontalScrollView;
    ScrollListenerHorizontalScrollView gainHorizontalScrollView;
    private View view_center_bg;
    private View view_right_bg;
    private TextView tv_column_all_gain_title;
    LinearLayout ll_column_first;
    TextView tv_column_first;
    int scrollGainWidth = 0;
    RelativeLayout rl_column_all_first;
    float calculate = 1f;

    public static void start(Activity activity, GameBillEntity gameBillEntity) {
        Intent intent = new Intent(activity, RecordDetailsInfoActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, TYPE_BILL);
        intent.putExtra(Extras.EXTRA_BILL, gameBillEntity);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, ArrayList<GameBillEntity> gameBillList) {
        Intent intent = new Intent(activity, RecordDetailsInfoActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, TYPE_BILL_LIST);
        intent.putExtra(Extras.EXTRA_BILL_LIST, gameBillList);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_details_info);
        setSwipeBackEnable(false);
        windowWidth = BaseTools.getWindowWidth(getApplicationContext());
        itemHeight = BaseTools.dip2px(getApplicationContext(), 50);
        itemWidth = windowWidth / uniformNum;
        type = getIntent().getIntExtra(Extras.EXTRA_TYPE, TYPE_BILL);
        initView();
        if (type == TYPE_BILL) {
            ll_record_details_single.setVisibility(View.VISIBLE);
            ll_record_details_multiple.setVisibility(View.GONE);
            gameBillEntity = (GameBillEntity) getIntent().getSerializableExtra(Extras.EXTRA_BILL);
            updateInfo();
        } else if (type == TYPE_BILL_LIST) {
            ll_record_details_single.setVisibility(View.GONE);
            ll_record_details_multiple.setVisibility(View.VISIBLE);
            DialogMaker.showProgressDialog(RecordDetailsInfoActivity.this, "");
            ThreadUtil.Execute(new Runnable() {
                @Override
                public void run() {
                    dealSettlement();
                }
            });
        }
    }

    public void dealSettlement() {
        gameBillList = (ArrayList<GameBillEntity>) getIntent().getSerializableExtra(Extras.EXTRA_BILL_LIST);
        if (gameBillList != null && gameBillList.size() != 0) {
            multipleTotalCount = gameBillList.size();
            gameMemberMap = new HashMap<String, GameMemberEntity>();
            userGainMap = new HashMap<String, Integer[]>();
            userInsuranceGainMap = new HashMap<String, Integer[]>();
            ArrayList<GameMemberEntity> currentGameMemberList;
            for (int i = 0; i < multipleTotalCount; i++) {
                GameBillEntity gameBillEntity = gameBillList.get(i);
                multipleNameBuffer.append(gameBillEntity.gameInfo.name);
                if (i < (multipleTotalCount - 1)) {
                    multipleNameBuffer.append("/");
                }
                currentGameMemberList = (ArrayList<GameMemberEntity>) gameBillEntity.gameMemberList.clone();
                if (currentGameMemberList != null && currentGameMemberList.size() != 0) {
                    for (GameMemberEntity member : currentGameMemberList) {
                        String uid = member.userInfo.account;
                        //保险(购买了的人才赋值)
                        if (!userInsuranceGainMap.containsKey(uid)) {
                            userInsuranceGainMap.put(uid, new Integer[multipleTotalCount]);
                        }
                        int insuranceGain = 0;
                        if (member.premium != 0) {
                            insuranceGain = member.insurance - member.premium;
                            userInsuranceGainMap.get(uid)[i] = insuranceGain;
                        }
                        //盈利
                        if (!userGainMap.containsKey(uid)) {
                            userGainMap.put(uid, new Integer[multipleTotalCount]);
                        }
                        userGainMap.get(uid)[i] = member.winChip + insuranceGain;
                        if (gameMemberMap.containsKey(uid)) {
                            GameMemberEntity oldMember = gameMemberMap.get(uid);
                            member.winChip = (member.winChip + oldMember.winChip);
                            member.reward = (member.reward + oldMember.reward);
                            member.insurance = (member.insurance + oldMember.insurance);
                            member.premium = (member.premium + oldMember.premium);
                            member.totalBuy = (member.totalBuy + oldMember.totalBuy);
                            member.winCnt = (member.winCnt + oldMember.winCnt);
                            member.enterPotCnt = (member.enterPotCnt + oldMember.enterPotCnt);
                            member.joinCnt = (member.joinCnt + oldMember.joinCnt);
                        }
                        gameMemberMap.put(uid, member);
                    }
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    createDetailsInfoView();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogMaker.dismissProgressDialog();
                }
            });
        }
    }

    public void createDetailsInfoView() {
        Set<String> mapSet = gameMemberMap.keySet();
        if (mapSet != null) {
            gameMemberList = new ArrayList<GameMemberEntity>();
            for (String uid : mapSet) {
                gameMemberList.add(gameMemberMap.get(uid));
            }
//                setMemberList();

//                for (GameMemberEntity gameMemberEntity : gameMemberList) {
            int memberSize = gameMemberList.size();
            int gainItemWidth = itemWidth * 13 / 6;
            scrollGainWidth = LinearLayout.LayoutParams.MATCH_PARENT;
            if (multipleTotalCount > 6) {
                scrollGainWidth = multipleTotalCount * gainItemWidth;
            }
            gainTextViews = new TextView[memberSize][multipleTotalCount];
            insuranceTextViews = new TextView[memberSize][multipleTotalCount];
            allGainTextViews = new TextView[memberSize];
            //画栏目
            for (int i = 0; i < multipleTotalCount; i++) {
                TextView tv_record_item_column = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_record_column_tv_item, null);
                tv_record_item_column.setText(getString(R.string.record_details_info_game_count, i + 1));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(gainItemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                tv_record_item_column.setLayoutParams(lp);
                ll_scroll_column.addView(tv_record_item_column);
            }
//                ll_scroll_gain.addView(columnLayout, new LinearLayout.LayoutParams(gainWidth, itemHeight));
            //
            for (int i = 0; i < memberSize; i++) {
                //成员
                GameMemberEntity mGameMemberEntity = gameMemberList.get(i);
                UserEntity userInfo = mGameMemberEntity.userInfo;
                String uid = userInfo.account;
                Integer[] currentGains = userGainMap.get(uid);
                Integer[] currentInsuranceGains = userInsuranceGainMap.get(uid);
                LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(scrollGainWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i != 0) {
                    linearParams.setMargins(0, itemMaginTop, 0, 0);
                }
                linearLayout.setLayoutParams(linearParams);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setBackgroundResource(R.color.record_detail_info_item_bg);
                for (int j = 0; j < multipleTotalCount; j++) {
                    //游戏场次
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_record_gain_item, null);
                    TextView tv_gain = (TextView) view.findViewById(R.id.tv_record_item_gain);
                    TextView tv_insurance_gain = (TextView) view.findViewById(R.id.tv_record_item_insurance);
                    if (currentGains[j] != null) {
                        int gain = currentGains[j];
                        tv_gain.setVisibility(View.VISIBLE);
                        RecordHelper.setRecordGainView(tv_gain, gain);
                    } else {
                        tv_gain.setVisibility(View.INVISIBLE);
                    }
                    if (currentInsuranceGains[j] != null) {
                        int insuranceGain = currentInsuranceGains[j];
                        tv_insurance_gain.setVisibility(View.VISIBLE);
                        RecordHelper.setRecordGainView(tv_insurance_gain, insuranceGain);
                    } else {
                        tv_insurance_gain.setVisibility(View.INVISIBLE);
                    }
                    gainTextViews[i][j] = tv_gain;
                    insuranceTextViews[i][j] = tv_insurance_gain;
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(gainItemWidth, itemHeight);
                    view.setLayoutParams(lp);
                    linearLayout.addView(view);
                }
//                    ll_scroll_gain.addView(linearLayout, new LinearLayout.LayoutParams(gainWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                ll_scroll_gain.addView(linearLayout);
                //头像
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_record_userinfo_item, null);
                HeadImageView iv_game_userhead = (HeadImageView) view.findViewById(R.id.iv_game_userhead);
                TextView tv_game_member_nickname = (TextView) view.findViewById(R.id.tv_game_member_nickname);
                tv_game_member_nickname.setText(UserInfoShowHelper.getUserNickname(userInfo));
                iv_game_userhead.loadBuddyAvatarByUrl(userInfo.account, userInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
                LinearLayout.LayoutParams userInfoParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
                if (i != 0) {
                    userInfoParams.setMargins(0, itemMaginTop, 0, 0);
                }
                view.setLayoutParams(userInfoParams);
                ll_scroll_userinfo.addView(view);
                //总盈利
                TextView tv_record_item_all_gain = (TextView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_record_all_gain_tv_item, null);
                LinearLayout.LayoutParams allGainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, itemHeight);
                int allGain = RecordHelper.getAllGain(mGameMemberEntity);
                RecordHelper.setRecordGainView(tv_record_item_all_gain, allGain);
                allGainTextViews[i] = tv_record_item_all_gain;
                if (i != 0) {
                    allGainParams.setMargins(0, itemMaginTop, 0, 0);
                }
                tv_record_item_all_gain.setLayoutParams(allGainParams);
                ll_scroll_all_gian.addView(tv_record_item_all_gain);
            }
        }
        tv_game_multiple_game_count.setText(getString(R.string.total_game_record_num, multipleTotalCount));
        tv_game_multiple_name.setText(multipleNameBuffer.toString());
        DialogMaker.dismissProgressDialog();
    }

    private void updateGain(float calculate) {
        this.calculate = calculate;
        int size = (gameMemberList == null ? 0 : gameMemberList.size());
        for (int i = 0; i < size; i++) {
            GameMemberEntity member = gameMemberList.get(i);
            String uid = member.userInfo.account;
            Integer[] gians = userGainMap.get(uid);
            Integer[] insuranceGains = userInsuranceGainMap.get(uid);
            int allGain = 0;
            for (int j = 0; j < multipleTotalCount; j++) {
                if (gians[j] != null) {
                    int gain = gians[j];
                    if (gain > 0) {
                        gain = (int) (gain * calculate);
                    }
                    allGain = allGain + gain;
                    RecordHelper.setRecordGainView(gainTextViews[i][j], gain);
                    gainTextViews[i][j].setVisibility(View.VISIBLE);
                } else {
                    gainTextViews[i][j].setVisibility(View.INVISIBLE);
                }
                if (insuranceGains[j] != null) {
                    int insurance = insuranceGains[j];
//                    if (insurance > 0) {
//                        insurance = (int) (insurance * calculate);
//                    }
                    RecordHelper.setRecordGainView(insuranceTextViews[i][j], insurance);
                    insuranceTextViews[i][j].setVisibility(View.VISIBLE);
                } else {
                    insuranceTextViews[i][j].setVisibility(View.INVISIBLE);
                }
            }
            RecordHelper.setRecordGainView(allGainTextViews[i], allGain);
        }
    }

    LinearLayout simpleShareView;
    /**
     * 创建普通的牌局信息分享样式
     */
    public void createSimpleShareView() {
        int size = (gameMemberList == null ? 0 : gameMemberList.size());
        for (int i = 0; i < size; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            GameMemberEntity member = gameMemberList.get(i);
            String uid = member.userInfo.account;
            Integer[] gians = userGainMap.get(uid);
            int allGain = 0;
            stringBuilder.append(member.userInfo.name);//
            for (int j = 0; j < multipleTotalCount; j++) {
                if (gians[j] != null) {
                    int gain = gians[j];
                    if (gain > 0) {
                        gain = (int) (gain * calculate);
                    }
                    allGain = allGain + gain;
                    if (gain != 0) {
                        if (gain > 0 && j != 0) {
                            stringBuilder.append("+");
                        }
                        stringBuilder.append(gain);
                    }
                } else {
                }
            }
            stringBuilder.append("=").append(allGain);
            LogUtil.d(TAG, stringBuilder.toString());
            TextView shareItemView = new TextView(getApplicationContext());
            shareItemView.setText(stringBuilder.toString());
            simpleShareView.addView(shareItemView);
        }
    }

//    public void dealSettlement() {
//        if (gameBillList != null && gameBillList.size() != 0) {
//            multipleTotalCount = gameBillList.size();
//            gameMemberMap = new HashMap<String, GameMemberEntity>();
//            ArrayList<GameMemberEntity> currentGameMemberList;
//            for (int i = 0; i < multipleTotalCount; i++) {
//                GameBillEntity gameBillEntity = gameBillList.get(i);
//                multipleNameBuffer.append(gameBillEntity.getGameInfo().getName());
//                if (i < (multipleTotalCount - 1)) {
//                    multipleNameBuffer.append("/");
//                }
//                currentGameMemberList = (ArrayList<GameMemberEntity>) gameBillEntity.getGameMemberList().clone();
//                if (currentGameMemberList != null && currentGameMemberList.size() != 0) {
//                    for (GameMemberEntity member : currentGameMemberList) {
//                        String uid = member.getUserInfo().getAccount();
//                        if (gameMemberMap.containsKey(uid)) {
//                            GameMemberEntity oldMember = gameMemberMap.get(uid);
//                            member.setWinChip(member.getWinChip() + oldMember.getWinChip());
//                            member.setReward(member.reward + oldMember.reward);
//                            member.setInsurance(member.insurance + oldMember.insurance);
//                            member.setPremium(member.premium + oldMember.premium);
//                            member.setTotalBuy(member.getTotalBuy() + oldMember.getTotalBuy());
//                            member.setWinCnt(member.winCnt + oldMember.winCnt);
//                            member.setEnterPotCnt(member.enterPotCnt + oldMember.enterPotCnt);
//                            member.setJoinCnt(member.joinCnt + oldMember.joinCnt);
//                        }
//                        gameMemberMap.put(uid, member);
//                    }
//                }
//            }
//            Set<String> mapSet = gameMemberMap.keySet();
//            if (mapSet != null) {
//                gameMemberList = new ArrayList<GameMemberEntity>();
//                for (String uid : mapSet) {
//                    gameMemberList.add(gameMemberMap.get(uid));
//                }
//                setMemberList();
//            }
//            tv_game_multiple_game_count.setText(getString(R.string.total_game_record_num, multipleTotalCount));
//            tv_game_multiple_name.setText(multipleNameBuffer.toString());
//        }
//    }

    private void updateInfo() {
        if (gameBillEntity != null) {
            GameEntity gameInfo = gameBillEntity.gameInfo;
            if (gameInfo != null) {
                tv_game_name.setText(gameInfo.name);
                tv_game_date.setText(DateTools.getOtherStrTime_ymd_hm(String.valueOf(gameInfo.createTime)));
                if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
                    //普通模式
                    GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
                    tv_game_blinds.setText(GameConstants.getGameBlindsShow(gameConfig.blindType));
                    tv_game_duration.setText(GameConstants.getGameDurationShow(gameConfig.timeType));
                } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
                    //SNG模式
                    GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
                }
            }
            gameMemberList = gameBillEntity.gameMemberList;
            setMemberList();
        }
    }

    public void setMemberList() {
        if (gameMemberList != null && gameMemberList.size() != 0) {
            tv_game_members.setText(String.valueOf(gameMemberList.size()));
            mRecordDetailsInfoAdapter = new RecordDetailsInfoAdapter(getApplicationContext(), gameMemberList, type);
            lv_record.setAdapter(mRecordDetailsInfoAdapter);
            //总计
            int allBuy = 0;
            int allInsurance = 0;
            int allInsuranceGain = 0;
            for (GameMemberEntity gameMemberEntity : gameMemberList) {
                allBuy = allBuy + gameMemberEntity.totalBuy;
                allInsurance = allInsurance + gameMemberEntity.premium;
                int insuranceGain = gameMemberEntity.insurance - gameMemberEntity.premium;//保险盈利：保险赚的 - 投保
                allInsuranceGain = allInsuranceGain + insuranceGain;
            }
            tv_record_all_buy.setText(String.valueOf(allBuy));
            tv_record_all_insurance.setText(String.valueOf(allInsurance));
            tv_record_all_insurance_gain.setText(String.valueOf(allInsuranceGain));
        }
    }

    private void initView() {
        //
        simpleShareView = (LinearLayout) findViewById(R.id.simpleShareView);
        //
        tv_column_first = (TextView) findViewById(R.id.tv_column_first);
        tv_column_all_gain_title = (TextView) findViewById(R.id.tv_column_all_gain_title);
        ll_column_first = (LinearLayout) findViewById(R.id.ll_column_first);
        rl_column_all_first = (RelativeLayout) findViewById(R.id.rl_column_all_first);
        ll_column_first.setOnClickListener(this);
        ll_scroll_userinfo = (LinearLayout) findViewById(R.id.ll_scroll_userinfo);
        ll_scroll_all_gian = (LinearLayout) findViewById(R.id.ll_scroll_all_gian);
        ll_column_normal_center = (LinearLayout) findViewById(R.id.ll_column_normal_center);
        ll_scroll_column = (LinearLayout) findViewById(R.id.ll_scroll_column);
        columnHorizontalScrollView = (ScrollListenerHorizontalScrollView) findViewById(R.id.columnHorizontalScrollView);
        gainHorizontalScrollView = (ScrollListenerHorizontalScrollView) findViewById(R.id.gainHorizontalScrollView);
        ll_record_scroll = (LinearLayout) findViewById(R.id.ll_record_scroll);
        ll_scroll_gain = (LinearLayout) findViewById(R.id.ll_scroll_gain);
        view_center_bg = (View) findViewById(R.id.view_center_bg);
        view_right_bg = (View) findViewById(R.id.view_right_bg);
        //
        lv_record = (CustomListView) findViewById(R.id.lv_record);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        ll_details_bg = (LinearLayout) findViewById(R.id.ll_details_bg);
        iv_details_logo = (ImageView) findViewById(R.id.iv_details_logo);
        ll_record_total = (LinearLayout) findViewById(R.id.ll_record_total);
        //
        rl_record_details_info = (RelativeLayout) findViewById(R.id.rl_record_details_info);
        ll_record_details_column = (LinearLayout) findViewById(R.id.ll_record_details_column);
        tv_game_name = (TextView) findViewById(R.id.tv_game_name);
        tv_game_date = (TextView) findViewById(R.id.tv_game_date);
        tv_game_members = (TextView) findViewById(R.id.tv_game_members);
        tv_game_blinds = (TextView) findViewById(R.id.tv_game_blinds);
        tv_game_duration = (TextView) findViewById(R.id.tv_game_duration);
        //
        tv_record_all_buy = (TextView) findViewById(R.id.tv_record_all_buy);
        tv_record_all_insurance = (TextView) findViewById(R.id.tv_record_all_insurance);
        tv_record_all_insurance_gain = (TextView) findViewById(R.id.tv_record_all_insurance_gain);
        //
        ll_record_details_single = (LinearLayout) findViewById(R.id.ll_record_details_single);
        ll_record_details_multiple = (LinearLayout) findViewById(R.id.ll_record_details_multiple);
        //
        tv_game_multiple_game_count = (TextView) findViewById(R.id.tv_game_multiple_game_count);
        tv_game_multiple_name = (TextView) findViewById(R.id.tv_game_multiple_name);
        //
        tv_column_all_buy_title = (TextView) findViewById(R.id.tv_column_all_buy_title);
        tv_column_gain_title = (TextView) findViewById(R.id.tv_column_gain_title);
        tv_column_insurance_title = (TextView) findViewById(R.id.tv_column_insurance_title);
        tv_column_insurance_gain_title = (TextView) findViewById(R.id.tv_column_insurance_gain_title);
        //
        btn_back = (TextView) findViewById(R.id.btn_back);
        btn_share = (TextView) findViewById(R.id.btn_share);
        btn_back.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        if (type == TYPE_BILL_LIST) {
//            tv_column_all_buy_title.setVisibility(View.GONE);
//            tv_column_insurance_title.setVisibility(View.GONE);
            //结算
            lv_record.setVisibility(View.GONE);
            ll_column_first.setVisibility(View.VISIBLE);
            ll_record_scroll.setVisibility(View.VISIBLE);
            ll_column_normal_center.setVisibility(View.GONE);
            columnHorizontalScrollView.setVisibility(View.VISIBLE);
            ll_record_total.setVisibility(View.GONE);
            view_center_bg.setVisibility(View.GONE);
            view_right_bg.setVisibility(View.GONE);
        } else {
            ll_column_first.setVisibility(View.GONE);
            ll_record_scroll.setVisibility(View.GONE);
            lv_record.setVisibility(View.VISIBLE);
            ll_column_normal_center.setVisibility(View.VISIBLE);
            columnHorizontalScrollView.setVisibility(View.GONE);
            ll_record_total.setVisibility(View.VISIBLE);
        }
        //台湾版本检测
        if(ApiConfig.AppVersion.isTaiwanVersion) {
            btn_share.setVisibility(View.GONE);
        }else {
            btn_share.setVisibility(View.VISIBLE);
        }
        //
        initHorizontalScrollView();
    }

    private void initHorizontalScrollView() {
        columnHorizontalScrollView.setScrollView(gainHorizontalScrollView);
        gainHorizontalScrollView.setScrollView(columnHorizontalScrollView);
    }

    public void showShareView() {
        if (mShareView == null) {
            mShareView = new ShareView(this, ShareView.TYPE_SHARE_RECORD);
        }
        if (shareBitmap == null) {
            DialogMaker.showProgressDialog(this, getString(R.string.game_record_dealing));
            ThreadUtil.Execute(new Runnable() {
                @Override
                public void run() {
                    boolean isDealFailure = false;
                    try {
                        if (type == TYPE_BILL) {
                            shareBitmap = getShareBillView(rl_record_details_info, ll_record_details_column, lv_record, ll_record_total, ll_details_bg);
//                            shareBitmap = convertViewToBitmapBg(simpleShareView);
                        } else if (type == TYPE_BILL_LIST) {
//                            shareBitmap = getShareCalculateView(rl_record_details_info, ll_scroll_column,
//                                    ll_scroll_userinfo, ll_scroll_gain, ll_scroll_all_gian, ll_details_bg);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    createSimpleShareView();
                                    simpleShareView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            LogUtil.i(TAG, "onGlobalLayout");
                                            shareBitmap = convertViewToBitmapBg(simpleShareView);
                                            LogUtil.i(TAG, "shareBitmap :" + shareBitmap);
                                            mShareView.setShareGameBillBitmap(shareBitmap);
                                        }
                                    });
                                }
                            });
                        }
                        //savePic(shareBitmap, CacheConstant.getAppDownloadPath() + "pic.jpeg");
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        isDealFailure = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final boolean isOOM = isDealFailure;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogMaker.dismissProgressDialog();
                            if (isOOM) {
                                Toast.makeText(getApplicationContext(), R.string.game_record_share_is_big, Toast.LENGTH_SHORT).show();
                            } else {
                                mShareView.show();
                            }
                        }
                    });
                }
            });
        } else {
            mShareView.show();
        }
//        if(mSharePopDialog == null) {
//            mSharePopDialog = new SharePopDialog(this, SharePopDialog.TYPE_SHARE_RECORD);
//            if (shareBitmap == null) {
//                if (type == TYPE_BILL) {
//                    shareBitmap = getShareBillView(rl_record_details_info, ll_record_details_column, lv_record, ll_record_total, ll_details_bg);
//                } else if (type == TYPE_BILL_LIST) {
//                    shareBitmap = getShareCalculateView(rl_record_details_info, ll_scroll_column,
//                            ll_scroll_userinfo, ll_scroll_gain, ll_scroll_all_gian, ll_details_bg);
//                }
////                savePic(shareBitmap, CacheConstant.getAppDownloadPath() + "pic.jpeg");
//            }
//            mSharePopDialog.setShareBitmap(shareBitmap);
//        }
//        mSharePopDialog.show();
    }

    public Bitmap convertViewToBitmap(View contentView, boolean isAlpha) {
        Bitmap bitmap = null;
        if (isAlpha) {
            bitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(contentView.getWidth(), contentView.getHeight(), Bitmap.Config.RGB_565);
        }
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        contentView.draw(canvas);
        return bitmap;
    }

    public Bitmap convertViewToBitmapBg(View bgView) {
        Bitmap bitmap = Bitmap.createBitmap(bgView.getWidth(), bgView.getHeight(), Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        bgView.draw(canvas);
        return bitmap;
    }

    public Bitmap getShareBillView(View infoView, View columnView, View contentView, View bottomView, View bgView) {
        int width = contentView.getWidth();
        int height = infoView.getHeight() + columnView.getHeight() + contentView.getHeight() + bottomView.getHeight();
        int windowHeight = BaseTools.getWindowHeigh(getApplicationContext());
        if (height < windowHeight) {
            height = windowHeight;
        }
        Bitmap allBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(allBitmap);
        int bgSize = height / bgView.getHeight() + 1;
        for (int i = 0; i < bgSize; i++) {
            canvas.drawBitmap(convertViewToBitmapBg(bgView), 0, infoView.getHeight() + i * bgView.getHeight(), null);
        }
        //把view中的内容绘制在画布上
        if(infoView.getWidth() != 0 && infoView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(infoView, true), new Matrix(), null);
        }
        if(columnView.getWidth() != 0 && columnView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(columnView, true), 0, infoView.getHeight(), null);
        }
        if(contentView.getWidth() != 0 && contentView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(contentView, true), 0, infoView.getHeight() + columnView.getHeight(), null);
        }
        if(bottomView.getWidth() != 0 && bottomView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(bottomView, false), 0, height - bottomView.getHeight(), null);
        }
        //画水印
        canvas.drawBitmap(convertViewToBitmap(iv_details_logo, true), width - iv_details_logo.getWidth() - 10, 10, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return allBitmap;
    }

    public Bitmap getShareCalculateView(View infoView, View columnView,
                                        View leftView, View contentView, View rightView, View bgView) {
        int cotentViewWidth = contentView.getWidth();
        if (scrollGainWidth != LinearLayout.LayoutParams.MATCH_PARENT) {
            cotentViewWidth = scrollGainWidth;
        }
        int width = leftView.getWidth() + cotentViewWidth + rightView.getWidth();
        int height = infoView.getHeight() + columnView.getHeight() + contentView.getHeight();
        int windowHeight = BaseTools.getWindowHeigh(getApplicationContext());
        if (height < windowHeight) {
            height = windowHeight;
        }
        Bitmap allBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(allBitmap);
        int bgSize = height / bgView.getHeight() + 1;
        int bgWidthSize = width / bgView.getWidth() + 1;
        //Log.d(TAG, "bgSize :" + bgSize + "; bgWidthSize :" + bgWidthSize);
        //
//        Drawable bg1Drawable = getResources().getDrawable(R.mipmap.main_bg);
//        Bitmap bg1Bitmap = ((BitmapDrawable) bg1Drawable).getBitmap();
//        Drawable bg2Drawable = getResources().getDrawable(R.mipmap.record_details_info_left_bg);
//        Bitmap bg2Bitmap = ((BitmapDrawable) bg2Drawable).getBitmap();
        //
//        Matrix matrix1 = new Matrix();
//        matrix1.postScale(width / (float) bg1Bitmap.getWidth() + 0.5f, height / (float) bg1Bitmap.getHeight() + 0.5f);
//        Matrix matrix2 = new Matrix();
//        matrix2.postScale(width / (float) bg2Bitmap.getWidth() + 0.5f, height / (float) bg2Bitmap.getHeight() + 0.5f);
        //
//        Bitmap newBg1Bitmap = Bitmap.createBitmap(bg1Bitmap, 0, 0, bg1Bitmap.getWidth(), bg1Bitmap.getHeight(), matrix1, true);
//        Bitmap newBg2Bitmap = Bitmap.createBitmap(bg2Bitmap, 0, 0, bg2Bitmap.getWidth(), bg2Bitmap.getHeight(), matrix2, true);
//        canvas.drawBitmap(newBg1Bitmap, 0, 0, null);
//        canvas.drawBitmap(newBg2Bitmap, 0, 0, null);
        for (int i = 0; i < bgSize; i++) {
//            canvas.drawBitmap(convertViewToBitmapBg(bgView), 0, infoView.getHeight() + i * bgView.getHeight(), null);
//            for (int j = 0; j < bgWidthSize; j++) {
//                canvas.drawBitmap(convertViewToBitmapBg(bgView), j * bgView.getWidth(), infoView.getHeight() + i * bgView.getHeight(), null);
//            }
        }
        //把view中的内容绘制在画布上
        if(infoView.getWidth() != 0 && infoView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(infoView, false), new Matrix(), null);
        }
        if(columnView.getWidth() != 0 && columnView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(columnView, false), leftView.getWidth(), infoView.getHeight(), null);
        }
        if(leftView.getWidth() != 0 && leftView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(leftView, false), 0, infoView.getHeight() + columnView.getHeight(), null);
        }
        if(contentView.getWidth() != 0 && contentView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(contentView, false), leftView.getWidth(), infoView.getHeight() + columnView.getHeight(), null);
        }
        if(rightView.getWidth() != 0 && rightView.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(rightView, false), leftView.getWidth() + cotentViewWidth,
                    infoView.getHeight() + columnView.getHeight(), null);
        }
        if(rl_column_all_first.getWidth() != 0 && rl_column_all_first.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(rl_column_all_first, false), 0, infoView.getHeight(), null);
        }
        if(tv_column_all_gain_title.getWidth() != 0 && tv_column_all_gain_title.getHeight() != 0) {
            canvas.drawBitmap(convertViewToBitmap(tv_column_all_gain_title, false), leftView.getWidth() + cotentViewWidth,
                    infoView.getHeight(), null);
        }
        //画水印
        canvas.drawBitmap(convertViewToBitmap(iv_details_logo, false), width - iv_details_logo.getWidth() - 10, 10, null);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return allBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_share:
                showShareView();
                break;
            case R.id.ll_column_first:
                showCalculatePop(v);
                break;
        }
    }

    RecordCalculatePopView mRecordCalculatePopView;

    private void showCalculatePop(View view) {
        if (mRecordCalculatePopView == null) {
            mRecordCalculatePopView = new RecordCalculatePopView(getApplicationContext());
            mRecordCalculatePopView.setOnChoiceCalculateListener(new RecordCalculatePopView.OnChoiceCalculateListener() {
                @Override
                public void onChoice(float calculate) {
                    String showColumn = (int) (calculate * 100) + "%";
                    updateGain(calculate);
                    tv_column_first.setText(showColumn);
                }
            });
            mRecordCalculatePopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ll_column_first.setBackgroundResource(R.mipmap.btn_record_calculate_bg);
                }
            });
        }
        ll_column_first.setBackgroundResource(R.mipmap.btn_record_calculate_show_bg);
        mRecordCalculatePopView.showAsDropDown(view);
    }

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareBitmap != null) {
            shareBitmap.recycle();
            shareBitmap = null;
        }
    }
}
