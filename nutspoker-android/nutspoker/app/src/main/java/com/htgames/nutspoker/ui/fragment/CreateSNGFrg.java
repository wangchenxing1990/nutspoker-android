package com.htgames.nutspoker.ui.fragment;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.data.GameName;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.CreateGameConfigPref;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.game.mtt.view.MttBlindStructureDialog;
import com.htgames.nutspoker.game.view.PlayerSelectPopDialog;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.view.MatchCreateRulesPopView;
import com.htgames.nutspoker.view.NodeSeekBar;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.widget.EasySwitchBtn;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * SNG模式
 */
public class CreateSNGFrg extends BaseGameCreateFragment implements View.OnClickListener, GameCreateActivity.IPlayModeChange {
    public String teamId;
    public HordeEntity mHordeEntity;
    public HordeEntity mSelectedHordeEntity;
    ArrayList<HordeEntity> costList;
    //    部落牌局设置   最多四个部落
    SwitchButton switch_horde;
    View horde_content;
    //one
    View horde_one_container;
    EasySwitchBtn iv_horde_one;
    TextView tv_horde_one;
    //two
    View horde_two_container;
    EasySwitchBtn iv_horde_two;
    TextView tv_horde_two;
    //three
    View horde_three_container;
    EasySwitchBtn iv_horde_three;
    TextView tv_horde_three;
    //four
    View horde_four_container;
    EasySwitchBtn iv_horde_four;
    TextView tv_horde_four;
    //创建部落牌局  消耗钻石相关
    TextView horde_consume_num;
    TextView horde_remain_num;
    View view;
    Button btn_game_start;
    TextView tv_game_chip;//带入筹码
    TextView tv_game_checkin_fee;
    TextView tv_match_blinds_time_num;
    TextView tv_game_chip_desc;
    TextView tv_mtt_relation;
    LinearLayout ll_game_checkin_fee_rule;
    NodeSeekBar mFeeSeekBar;
    NodeSeekBar mChipsSeekBar;
    NodeSeekBar mTimeSeekBar;
    SwitchButton ck_game_control_into;
    NodeSeekBar mDeskNumSeekBarNew;
    private int gameChipsConfigType = 2;//带入筹码
    private int gamePlayer = 0;//牌局人数
    boolean isControl = true;
    PlayerSelectPopDialog mPlayerSelectPopDialog;
    MttBlindStructureDialog mMttBlindStructureDialog;
    private boolean isEditPlayer = false;//是否是通过键盘编辑
    private GameSngConfigEntity mGameConfig;
    SwitchButton sng_game_ip_switch;
    SwitchButton sng_game_gps_switch;

    public static CreateSNGFrg newInstance() {
        CreateSNGFrg mInstance = new CreateSNGFrg();
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getCreateGameConfig();
        super.onCreate(savedInstanceState);
        teamId = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).teamId : "";
        mHordeEntity = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).mHordeEntity : null;
        costList = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).costList : null;
        setFragmentName(CreateSNGFrg.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_game_create_sng, container, false);
        initView();
        initSureHordeView();
        initSelectHordeView();//初始化horde相关的配置
        initSeekBar();
        setGameConfig();//恢复并显示场次创建牌局的设置
        return view;
    }

    View previousHorde;

    @Override
    public void playModeChange(int play_mode) {
        if (play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            return;//大菠萝不处理
        }
    }

    public class OnHordeClick implements View.OnClickListener {
        int hordeIndex;
        public OnHordeClick(int hordeIndex) {
            this.hordeIndex = hordeIndex;
        }
        @Override
        public void onClick(View v) {
            boolean isClickOne = v == horde_one_container || v == iv_horde_one;
            boolean isClickTwo = v == horde_two_container || v == iv_horde_two;
            boolean isClickTree = v == horde_three_container || v == iv_horde_three;
            boolean isClickFour = v == horde_four_container || v == iv_horde_four;
            if (costList != null && costList.size() == 1) {
                iv_horde_one.setChecked(!iv_horde_one.isChecked);
            } else {
                iv_horde_one.setChecked((previousHorde == horde_one_container || previousHorde == iv_horde_one) && isClickOne ? !iv_horde_one.isChecked : isClickOne);
            }
            iv_horde_two.setChecked((previousHorde == horde_two_container || previousHorde == iv_horde_two) && isClickTwo ? !iv_horde_two.isChecked : isClickTwo);
            iv_horde_three.setChecked((previousHorde == horde_three_container || previousHorde == iv_horde_three) && isClickTree ? !iv_horde_three.isChecked : isClickTree);
            iv_horde_four.setChecked((previousHorde == horde_four_container || previousHorde == iv_horde_four) && isClickFour ? !iv_horde_four.isChecked : isClickFour);
            previousHorde = v;
            if (costList == null || costList.size() <= hordeIndex || horde_consume_num == null) {
                return;
            }
            mSelectedHordeEntity = costList.get(hordeIndex);
            int hordeSelectNum = 0 + (iv_horde_one.isChecked ? 1 : 0) + (iv_horde_two.isChecked ? 1 : 0) + (iv_horde_three.isChecked ? 1 : 0) + (iv_horde_four.isChecked ? 1 : 0);
            int hordeDiamondCOnsume = GameConfigData.create_sng_fee[mFeeSeekBar.currentPosition < GameConfigData.create_sng_fee.length ? mFeeSeekBar.currentPosition : GameConfigData.create_sng_fee.length - 1];
            horde_consume_num.setText(hordeSelectNum <= 0 ? "0" : (hordeDiamondCOnsume + ""));
            if (hordeSelectNum <= 0) {
                mSelectedHordeEntity = null;
            }
        }
    }
    TextView sure_horde_consume_num;
    TextView sure_horde_remain_num;
    private void initSureHordeView() {
        //从部落中创建牌局时要显示消耗钻石的view，但是选择共享部落的view隐藏
        if (mHordeEntity != null) {
            ((ViewStub) view.findViewById(R.id.view_stub)).inflate();
            int hordeDiamondCOnsume = GameConfigData.create_sng_fee[gameChipsConfigType < GameConfigData.create_sng_fee.length ? gameChipsConfigType : GameConfigData.create_sng_fee.length - 1];
            sure_horde_consume_num = (TextView) view.findViewById(R.id.sure_horde_consume_num);
            sure_horde_consume_num.setText(hordeDiamondCOnsume + "");
            sure_horde_remain_num = (TextView) view.findViewById(R.id.sure_horde_remain_num);
            sure_horde_remain_num.setText(diamonds + "");
        }
    }
    private void initSelectHordeView() {
        if (StringUtil.isSpace(teamId) || mHordeEntity != null || costList == null || costList.size() <= 0) {
            return;
        }
        mSelectedHordeEntity = costList.get(0);
        ((ViewStub) view.findViewById(R.id.view_stub_select_horde)).inflate();
        horde_content = view.findViewById(R.id.horde_content);
        //one
        horde_one_container = view.findViewById(R.id.horde_one_container);
        horde_one_container.setOnClickListener(new OnHordeClick(0));
        horde_one_container.setVisibility(costList == null || costList.size() < 1 ? View.GONE : View.VISIBLE);
        iv_horde_one = (EasySwitchBtn) view.findViewById(R.id.iv_horde_one);
        iv_horde_one.setOnClickListener(new OnHordeClick(0));
        iv_horde_one.setChecked(true);
        previousHorde = iv_horde_one;
        tv_horde_one = (TextView) view.findViewById(R.id.tv_horde_one);
        tv_horde_one.setText(costList == null || costList.size() < 1 ? "" : costList.get(0).name);
        //two
        horde_two_container = view.findViewById(R.id.horde_two_container);
        horde_two_container.setOnClickListener(new OnHordeClick(1));
        horde_two_container.setVisibility(costList == null || costList.size() < 2 ? View.GONE : View.VISIBLE);
        iv_horde_two = (EasySwitchBtn) view.findViewById(R.id.iv_horde_two);
        iv_horde_two.setOnClickListener(new OnHordeClick(1));
        tv_horde_two = (TextView) view.findViewById(R.id.tv_horde_two);
        tv_horde_two.setText(costList == null || costList.size() < 2 ? "" : costList.get(1).name);
        //three
        horde_three_container = view.findViewById(R.id.horde_three_container);
        horde_three_container.setOnClickListener(new OnHordeClick(2));
        horde_three_container.setVisibility(costList == null || costList.size() < 3 ? View.GONE : View.VISIBLE);
        iv_horde_three = (EasySwitchBtn) view.findViewById(R.id.iv_horde_three);
        iv_horde_three.setOnClickListener(new OnHordeClick(2));
        tv_horde_three = (TextView) view.findViewById(R.id.tv_horde_three);
        tv_horde_three.setText(costList == null || costList.size() < 3 ? "" : costList.get(2).name);
        //four
        horde_four_container = view.findViewById(R.id.horde_four_container);
        horde_four_container.setOnClickListener(new OnHordeClick(3));
        horde_four_container.setVisibility(costList == null || costList.size() < 4 ? View.GONE : View.VISIBLE);
        iv_horde_four = (EasySwitchBtn) view.findViewById(R.id.iv_horde_four);
        iv_horde_four.setOnClickListener(new OnHordeClick(3));
        tv_horde_four = (TextView) view.findViewById(R.id.tv_horde_four);
        tv_horde_four.setText(costList == null || costList.size() < 4 ? "" : costList.get(3).name);
        //创建部落牌局  消耗钻石相关
        horde_consume_num = (TextView) view.findViewById(R.id.horde_consume_num);
        horde_consume_num.setText(GameConfigData.create_sng_fee[gameChipsConfigType < GameConfigData.create_sng_fee.length ? gameChipsConfigType : GameConfigData.create_sng_fee.length - 1] + "");
        horde_remain_num = (TextView) view.findViewById(R.id.horde_remain_num);
        horde_remain_num.setText(diamonds + "");
        switch_horde = (SwitchButton) view.findViewById(R.id.switch_horde);
        switch_horde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasChecked = switch_horde.isChecked();
                horde_content.setVisibility(hasChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void initView() {
        initGamename();
        view.findViewById(R.id.iv_explain_checkin_fee).setVisibility(View.GONE);
        sng_game_ip_switch = (SwitchButton) view.findViewById(R.id.sng_game_ip_switch);
        sng_game_gps_switch = (SwitchButton) view.findViewById(R.id.sng_game_gps_switch);
        btn_game_start = (Button) view.findViewById(R.id.btn_game_start);
        tv_match_blinds_time_num = (TextView) view.findViewById(R.id.tv_match_blinds_time_num);
        ck_game_control_into = (SwitchButton) view.findViewById(R.id.ck_game_control_into);
        mDeskNumSeekBarNew = (NodeSeekBar) view.findViewById(R.id.mDeskNumSeekBarNew);
        mDeskNumSeekBarNew.setData(new int[]{2, 3, 4, 5, 6, 7, 8, 9}, true, false, R.mipmap.game_chip_thumb, "");
        tv_game_chip = (TextView) view.findViewById(R.id.tv_game_chip);
        tv_mtt_relation = (TextView) view.findViewById(R.id.tv_mtt_relation);
        tv_game_chip_desc = (TextView) view.findViewById(R.id.tv_game_chip_desc);
        tv_game_checkin_fee = (TextView) view.findViewById(R.id.tv_game_checkin_fee);
        mFeeSeekBar = (NodeSeekBar) view.findViewById(R.id.mFeeSeekBar);
        mTimeSeekBar = (NodeSeekBar) view.findViewById(R.id.mTimeSeekBar);
        mChipsSeekBar = (NodeSeekBar) view.findViewById(R.id.mChipsSeekBar);
        ll_game_checkin_fee_rule = (LinearLayout) view.findViewById(R.id.ll_game_checkin_fee_rule);
        ll_game_checkin_fee_rule.setOnClickListener(this);
        btn_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (gamePlayer < 2 || gamePlayer > 1000) {
//                    showPlayerConfimDialog();
//                    return;
//                }
//                int checkinFee = GameConstants.sngCheckInFeeNum[gameChipsConfigType];
//                int serviceFee = checkinFee / 10;
//                if (coins >= (checkinFee + serviceFee)) {
                createGameByGroup();
//                } else {
//                    showTopUpDialog();
//                }
            }
        });
        tv_mtt_relation.setOnClickListener(this);
        ck_game_control_into.setChecked(isControl);
    }

    private void initSeekBar() {
        mFeeSeekBar.setData(GameConstants.sngCheckInFeeNum, false, false, R.mipmap.game_chip_thumb, "");
        mFeeSeekBar.setProgress(2);//setProgress(0)初始化设置0位置不会触发setOnNodeChangeListener事件
        mFeeSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                setCheckinFee(progress);
                //共享部落的钻石消耗
                int hordeDiamondCOnsume = GameConfigData.create_sng_fee[progress < GameConfigData.create_sng_fee.length ? progress : GameConfigData.create_sng_fee.length - 1];
                if (sure_horde_consume_num != null) {
                    sure_horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
                if (horde_consume_num != null) {
                    horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
            }
        });
        setCheckinFee(mFeeSeekBar.currentPosition);
        mTimeSeekBar.setData(GameConstants.sngTimeMinutes, false, true, R.mipmap.game_chip_thumb, getString(R.string.minutes));
        mChipsSeekBar.setData(GameConstants.sngChipsNum, false, false, R.mipmap.game_chip_thumb, "");
        mChipsSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                gameChipsConfigType = progress;
                updataGameConfigUI();
            }
        });
        mChipsSeekBar.setProgress(gameChipsConfigType);
        mTimeSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                int duration = GameConstants.sngTimeMinutes[progress];
                tv_match_blinds_time_num.setText(duration + "分钟");
            }
        });
        tv_match_blinds_time_num.setText((GameConstants.sngTimeMinutes[0]) + "分钟");
    }

    private void setCheckinFee(int progress) {
        int checkinFee = GameConstants.sngCheckInFeeNum[progress];
        int serviceFee = (int) (checkinFee / GameConfigData.SNGServiceRate);
        String checkinFeeStr = GameConstants.getGameChipsShow(checkinFee);
        String serviceFeeStr = GameConstants.getGameChipsShow(serviceFee);
        tv_game_checkin_fee.setText(checkinFeeStr + "+" + serviceFeeStr);
    }

    public void updataGameConfigUI() {
        int sngChips = GameConstants.sngChipsNum[gameChipsConfigType];
        tv_game_chip_desc.setText(getString(R.string.game_chips_bbs, sngChips / 20));
        tv_game_chip.setText("" + sngChips);
//        if (coins >= (serviceFee + checkinFee)) {
//        } else {
//        }
//        mTimeSeekBar.setProgress(GameConstants.sngRecommendBlindTimeGear[gameChipsConfigType]); 时间和记分牌关联去掉
    }

    private void createGameByGroup() {
        String gameName = edt_game_name.getText().toString();
        if (StringUtil.isSpace(gameName)) {
            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_notnull, Toast.LENGTH_SHORT).show();
            return;
        }
        mGameName = new GameName(gameName);
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            HordeEntity consumeEntity = mHordeEntity == null ? (switch_horde != null && switch_horde.isChecked() ? mSelectedHordeEntity : null) : mHordeEntity;//这两个hordeentity不能同时存在的，至少一个为null，mHordeEntity表示从部落大厅传过来的
            boolean hasSufficientDiamond = switch_horde != null && switch_horde.isChecked() ? (consumeEntity == null ? true : diamonds >= consumeEntity.money) : true;
            int time = GameConstants.sngTimeMinutes[mTimeSeekBar.getCurrentTimeProgress()] * 60;
            if (gamePlayer <= 9) {
                //单桌SNG   多桌sng牌局幹掉了，没这种牌局了
                GameSngConfigEntity gameSngConfigEntity = new GameSngConfigEntity();
                gameSngConfigEntity.chips = (GameConstants.sngChipsNum[gameChipsConfigType]);
                gameSngConfigEntity.player = (mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition));
                gameSngConfigEntity.checkInFee = (mFeeSeekBar.getDataItem(mFeeSeekBar.currentPosition));
                gameSngConfigEntity.start_sblinds = 10;
                gameSngConfigEntity.sblinds_mode = 1;
                gameSngConfigEntity.duration = (time);
                //下面的值不传给服务端，只是保存带本地
                gameSngConfigEntity.isControlBuy = ck_game_control_into.isChecked();
                gameSngConfigEntity.checkInFee_index = mFeeSeekBar.currentPosition;
                gameSngConfigEntity.chips_index = gameChipsConfigType;
                gameSngConfigEntity.duration_index = mTimeSeekBar.getCurrentTimeProgress();
                gameSngConfigEntity.player_index = mDeskNumSeekBarNew.currentPosition;
                gameSngConfigEntity.check_ip = sng_game_ip_switch.isChecked() ? 1 : 0;
                gameSngConfigEntity.check_gps = sng_game_gps_switch.isChecked() ? 1 : 0;
                ((GameCreateActivity) getActivity()).createGameByGroupNew(gameSngConfigEntity, GameConstants.GAME_MODE_SNG, ck_game_control_into.isChecked(), mGameName, consumeEntity, hasSufficientDiamond);
                saveCreateGameConfig(gameSngConfigEntity);
            } else {
                //多桌SNG   多桌sng牌局幹掉了，没这种牌局了
                GameMtSngConfig gameMtSngConfig = new GameMtSngConfig();
                gameMtSngConfig.matchChips = (GameConstants.sngChipsNum[gameChipsConfigType]);
                gameMtSngConfig.matchDuration = (time);
                gameMtSngConfig.matchCheckinFee = (mFeeSeekBar.getDataItem(mFeeSeekBar.currentPosition));
                gameMtSngConfig.matchPlayer = (mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition));
                gameMtSngConfig.totalPlayer = (gamePlayer);
                ((GameCreateActivity) getActivity()).createGameMatch(gameMtSngConfig, GameConstants.GAME_MODE_MT_SNG, ck_game_control_into.isChecked(), mGameName, consumeEntity, hasSufficientDiamond);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //可能购买了金币，回来要恢复
        updataGameConfigUI();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).playModeListeners.add(this);
        }
    }

    EasyAlertDialog choiceFailureDialog;

    public void showPlayerConfimDialog() {
        String message = getString(R.string.game_create_mt_sng_player_prompt);
        String btnStr = getString(R.string.setting);
        choiceFailureDialog = EasyAlertDialogHelper.createOneButtonDiolag(getActivity(), "", message, btnStr, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceFailureDialog.dismiss();
                if (isEditPlayer) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            edt_sng_more_player.requestFocus();
                            showKeyboard(true);
                        }
                    }, 300L);
                } else {
                    showPlayerSelectPop();
                }
            }
        });
        if (!getActivity().isFinishing()) {
            choiceFailureDialog.show();
        }
    }

    public void showPlayerSelectPop() {
        if (mPlayerSelectPopDialog == null) {
            mPlayerSelectPopDialog = new PlayerSelectPopDialog(getActivity());
            mPlayerSelectPopDialog.setOnSelectListener(new PlayerSelectPopDialog.OnSelectListener() {
                @Override
                public void onSelect(int player) {
                    isEditPlayer = false;
//                    edt_sng_more_player.setText(String.valueOf(player));
                }
            });
        }
        mPlayerSelectPopDialog.show();
    }

    MatchCreateRulesPopView matchCreateRulesPopView;

    public void showMatchRulsPop(View view, int contentId) {
        if (matchCreateRulesPopView == null) {
            matchCreateRulesPopView = new MatchCreateRulesPopView(getContext() , MatchCreateRulesPopView.TYPE_RIGHT);
        }
        matchCreateRulesPopView.show(view, contentId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mtt_relation:
//                showBlindStructureDialog();以前是显示一个盲注结构表的dialog，现在是打开一个新的页面
                if (getActivity() instanceof GameCreateActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(GameConstants.KEY_PLAY_MODE, ((GameCreateActivity) getActivity()).mPlayMode);
                    Nav.from(getActivity()).withExtras(bundle).toUri(UrlConstants.SNG_BLIND_STRUCTURE);
                }
                break;
            case R.id.ll_game_checkin_fee_rule:
//                showMatchRulsPop(v, R.string.game_match_checkin_fee_rule_desc);新需求砍掉了这个功能
                break;
        }
    }

    EditText edt_game_name;
    GameName mGameName;
    private void initGamename() {
        edt_game_name = (EditText) view.findViewById(R.id.edt_game_name);
        edt_game_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(GameConstants.MAX_GAMENAME_LENGTH), new NameRuleFilter()});
        String prefix = GamePreferences.getInstance(ChessApp.sAppContext).getSngGamePrefix();
        int ownSngGameCount = GamePreferences.getInstance(ChessApp.sAppContext).getSngGameCount();
        mGameName = new GameName(prefix + ownSngGameCount);
        edt_game_name.setText(mGameName.getName());
        edt_game_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //隐藏键盘
                    if (getActivity() instanceof GameCreateActivity) {
                        ((GameCreateActivity) getActivity()).showKeyboard(false);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void getCreateGameConfig() {
        String historyListStr = CreateGameConfigPref.getInstance(getActivity()).getSngConfig();
        LogUtil.i("getCreateGameConfig: " + historyListStr);
        Type type = new TypeToken<GameSngConfigEntity>() {}.getType();
        if (!StringUtil.isSpace(historyListStr)) {
            mGameConfig = GsonUtils.getGson().fromJson(historyListStr, type);
        }
    }

    private void saveCreateGameConfig(GameSngConfigEntity gameSngConfigEntity) {//保存创建牌局的设置
        String historyMgrListStr = GsonUtils.getGson().toJson(gameSngConfigEntity);
        LogUtil.i("saveCreateGameConfig: " + historyMgrListStr);
        CreateGameConfigPref.getInstance(getActivity()).setSngConfig(historyMgrListStr);//把管理员历史记录写入到sharedpreference
    }

    private void setGameConfig() {
        if (mGameConfig == null) {
            return;
        }
        mFeeSeekBar.setProgress(mGameConfig.checkInFee_index >= mFeeSeekBar.datas.length ? mFeeSeekBar.datas.length - 1 : mGameConfig.checkInFee_index);
        mChipsSeekBar.setProgress(mGameConfig.chips_index >= mChipsSeekBar.datas.length ? mChipsSeekBar.datas.length - 1 : mGameConfig.chips_index);
        if (mGameConfig.duration_index > 0) {//初始档位值设置为0会出现ui问题
            mTimeSeekBar.setProgress(mGameConfig.duration_index >= mTimeSeekBar.datas.length ? mTimeSeekBar.datas.length - 1 : mGameConfig.duration_index);
        }
        if (mGameConfig.player_index > 0) {//初始档位值设置为0会出现ui问题
            mDeskNumSeekBarNew.setProgress(mGameConfig.player_index >= mDeskNumSeekBarNew.datas.length ? mDeskNumSeekBarNew.datas.length - 1 : mGameConfig.player_index);
        }
        ck_game_control_into.setChecked(mGameConfig.isControlBuy);
        sng_game_ip_switch.setChecked(mGameConfig.check_ip == 1);
        sng_game_gps_switch.setChecked(mGameConfig.check_gps == 1);
    }

    @Override
    protected void afterGetAmount() {
        super.afterGetAmount();
        if (sure_horde_remain_num != null) {
            sure_horde_remain_num.setText(diamonds + "");
        }
        if (horde_remain_num != null) {
            horde_remain_num.setText(diamonds + "");
        }
    }
}
