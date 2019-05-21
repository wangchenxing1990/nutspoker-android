package com.htgames.nutspoker.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.GameName;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.config.GameConfigData;
import com.netease.nim.uikit.common.preference.CreateGameConfigPref;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.game.helper.GameConfigHelper;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter;
import com.htgames.nutspoker.ui.inputfilter.NameRuleFilter;
import com.htgames.nutspoker.ui.inputfilter.NoSpaceAndEnterInputFilter;
import com.htgames.nutspoker.view.NodeSeekBar;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.common.ui.widget.EasySwitchBtn;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * 创建牌局（普通模式）
 */
public class CreateNormalFrg extends BaseGameCreateFragment implements View.OnClickListener, GameCreateActivity.IPlayModeChange {
    private final static String TAG = "GameCreateNormal";
    private int[] minChipsData = new int[GameConfigData.minChipsIndexNum];
    private int[] maxChipsData = new int[GameConfigData.maxChipsIndexNum];
    private int[] totalChipsData = new int[GameConfigData.totalChipsIndexNum];
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
    //<!-- ************************************************************************************************ 帶入设置start ************************************************************************************************ -->
    boolean isCheckinSettingShow = false;
    View game_create_checkin_setting_switch;
    ImageView game_create_checkin_setting_switch_arrow;
    View checkin_setting_content_container;
    TextView checkin_setting_switch_tv;
    TextView min_chip_num;
    TextView max_chip_num;
    TextView total_chip_num;
    TextView create_normal_ante_num;
    NodeSeekBar min_chip_seekbar;
    NodeSeekBar max_chip_seekbar;
    NodeSeekBar total_chip_seekbar;
    NodeSeekBar create_normal_ante_seekbar;
    //<!-- ************************************************************************************************ 帶入设置end ************************************************************************************************ -->
    TextView tv_game_blind;//大小盲
    TextView tv_game_chip;//带入筹码
    NodeSeekBar mChipsSeekBar;
    NodeSeekBar mTimeSeekBar;
    private int gameBlinksConfigType = 0;
    Button btn_game_start;
    private GameNormalConfig mGameConfig;//上次创建牌局的设置
    View insurance_container;
    //高级设置
    int anteMode = GameConstants.ANTE_TYPE_0;//ANTE模式
    boolean isAdvancedConfigShow = false;
    //需求改版新样式的view  20161207 周智慧
    View advanced_setting_root;
    View game_create_advanced_switch;
    TextView advanced_switch_tv;
    ImageView game_create_advanced_switch_arrow;
    View ll_advanced_config_new;
    SwitchButton switch_game_control_into;
    SwitchButton switch_game_mode_insurance;
    SwitchButton normal_game_ip_switch;
    SwitchButton normal_game_gps_switch;
    SwitchButton normal_game_straddle_switch;
    NodeSeekBar mDeskNumSeekBarNew;
    //奥马哈的座位数文案显示和德州的文案显示不一样
    TextView tv_game_normal_table_count;

    public static CreateNormalFrg newInstance() {
        CreateNormalFrg mInstance = new CreateNormalFrg();
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getCreateGameConfig();
        super.onCreate(savedInstanceState);
        teamId = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).teamId : "";
        mHordeEntity = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).mHordeEntity : null;
        costList = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).costList : null;
        updateChipsData();
        setFragmentName(CreateNormalFrg.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getActivity() instanceof GameCreateActivity) {
            mPlayMode = ((GameCreateActivity) getActivity()).mPlayMode;
        }
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_game_create_normal, container, false);
        initView();
        initSureHordeView();
        initSelectHordeView();//初始化horde相关的配置
        initSeekBar();
        setGameConfig();//恢复并显示场次创建牌局的设置
        return view;
    }

    View previousHorde;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).playModeListeners.add(this);
        }
    }

    @Override
    public void playModeChange(int play_mode) {
        if (play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            return;//大菠萝不处理
        }
        //奥马哈现金局组局，7-9人时，不显示保险选项
        mPlayMode = play_mode;
        if (insurance_container != null && mDeskNumSeekBarNew != null) {
            if (mPlayMode == GameConstants.PLAY_MODE_OMAHA && mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition) >= 7) {
                insurance_container.setVisibility(View.GONE);
            } else {
                insurance_container.setVisibility(View.VISIBLE);
            }
        }
        tv_game_normal_table_count.setText(mPlayMode == GameConstants.PLAY_MODE_OMAHA ? "座位数(仅限2-6人可开启保险)" : "座位数");
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
            int hordeDiamondCOnsume = GameConfigData.create_game_fee[gameBlinksConfigType < GameConfigData.create_game_fee.length ? gameBlinksConfigType : GameConfigData.create_game_fee.length - 1];
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
            int hordeDiamondCOnsume = GameConfigData.create_game_fee[gameBlinksConfigType < GameConfigData.create_game_fee.length ? gameBlinksConfigType : GameConfigData.create_game_fee.length - 1];
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
        horde_consume_num.setText(GameConfigData.create_game_fee[gameBlinksConfigType < GameConfigData.create_game_fee.length ? gameBlinksConfigType : GameConfigData.create_game_fee.length - 1] + "");
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

    public void updateAnteUI() {
//        int ante = GameConstants.anteInts[gameBlinksConfigType] * anteMode;
//        int ante = GameConfigData.getNormalAnte()[gameBlinksConfigType] * anteMode;
    }

    private void initView() {
        initGamename();
        tv_game_normal_table_count = (TextView) view.findViewById(R.id.tv_game_normal_table_count);
        tv_game_normal_table_count.setText(mPlayMode == GameConstants.PLAY_MODE_OMAHA ? "座位数(仅限2-6人可开启保险)" : "座位数");
        insurance_container = view.findViewById(R.id.insurance_container);
        //带入设置start
        checkin_setting_content_container = view.findViewById(R.id.checkin_setting_content_container);
        checkin_setting_switch_tv = (TextView) view.findViewById(R.id.checkin_setting_switch_tv);
        game_create_checkin_setting_switch = view.findViewById(R.id.game_create_checkin_setting_switch);
        game_create_checkin_setting_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckinSetting(!isCheckinSettingShow);
            }
        });
        game_create_checkin_setting_switch_arrow = (ImageView) view.findViewById(R.id.game_create_checkin_setting_switch_arrow);
        min_chip_num = (TextView) view.findViewById(R.id.min_chip_num);
        min_chip_num.setText("" + GameConstants.getGameChipsShow(minChipsData[1]));
        min_chip_seekbar = (NodeSeekBar) view.findViewById(R.id.min_chip_seekbar);
        min_chip_seekbar.setData(minChipsData, false, false, R.mipmap.game_chip_thumb, "");
        min_chip_seekbar.setProgress(1);
        min_chip_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                int minChips = min_chip_seekbar.getDataItem(progress);
                min_chip_num.setText("" + GameConstants.getGameChipsShow(min_chip_seekbar.getDataItem(progress)));
                tv_game_chip.setText(GameConstants.getGameChipsShow(minChips));
                //再更新最大带入界面：满足最大带入>最小带入就行
                if (max_chip_seekbar.getDataItem(max_chip_seekbar.currentPosition) < minChips) {
                    max_chip_seekbar.setProgress(findMaxChipsIndexByNum(minChips));
                }
            }
        });
        max_chip_num = (TextView) view.findViewById(R.id.max_chip_num);
        max_chip_num.setText("" + GameConstants.getGameChipsShow(maxChipsData[4]));
        max_chip_seekbar = (NodeSeekBar) view.findViewById(R.id.max_chip_seekbar);
        max_chip_seekbar.setData(maxChipsData, false, false, R.mipmap.game_chip_thumb, "");
        max_chip_seekbar.setProgress(4);
        max_chip_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                max_chip_num.setText("" + (progress == maxChipsData.length - 1 ? "无限" : GameConstants.getGameChipsShow(max_chip_seekbar.getDataItem(progress))));
                //再更新最大带入界面：满足最大带入>最小带入就行
                if (max_chip_seekbar.getDataItem(progress) < min_chip_seekbar.getDataItem(min_chip_seekbar.currentPosition)) {
                    min_chip_seekbar.setProgress(findSmallMinChipsIndexByNum(max_chip_seekbar.getDataItem(progress)));
                }
            }
        });
        total_chip_num = (TextView) view.findViewById(R.id.total_chip_num);
        total_chip_num.setText("无限");
        total_chip_seekbar = (NodeSeekBar) view.findViewById(R.id.total_chip_seekbar);
        total_chip_seekbar.setData(totalChipsData, false, false, R.mipmap.game_chip_thumb, "");
        total_chip_seekbar.setProgress(GameConfigData.totalChipsIndexNum - 1);
        total_chip_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                total_chip_num.setText("" + (progress == totalChipsData.length - 1 ? "无限" : GameConstants.getGameChipsShow(total_chip_seekbar.getDataItem(progress))));
            }
        });
        create_normal_ante_num = (TextView) view.findViewById(R.id.create_normal_ante_num);
        create_normal_ante_num.setText("" + GameConfigData.antes[gameBlinksConfigType][0]);
        create_normal_ante_seekbar = (NodeSeekBar) view.findViewById(R.id.create_normal_ante_seekbar);
        create_normal_ante_seekbar.setData(GameConfigData.antes[gameBlinksConfigType], false, false, R.mipmap.game_chip_thumb, "");
        create_normal_ante_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                anteMode = progress;
                create_normal_ante_num.setText("" + create_normal_ante_seekbar.getDataItem(progress));
            }
        });
        //带入设置end
        btn_game_start = (Button) view.findViewById(R.id.btn_game_start);
        tv_game_blind = (TextView) view.findViewById(R.id.tv_game_blind);
        tv_game_chip = (TextView) view.findViewById(R.id.tv_game_chip);
        mChipsSeekBar = (NodeSeekBar) view.findViewById(R.id.mChipsSeekBar);
        mTimeSeekBar = (NodeSeekBar) view.findViewById(R.id.mTimeSeekBar);
        btn_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int chips = GameConstants.chipsNum[gameBlinksConfigType];
//                int serviceFee = GameConfigHelper.getServiceFee(chips);
//                if (coins >= (chips + serviceFee)) {
                createGameByGroup();
//                } else {
//                    showTopUpDialog();
//                }
            }
        });
        //高级设置 //新版本改动样式新view
        normal_game_straddle_switch = (SwitchButton) view.findViewById(R.id.normal_game_straddle_switch);
        advanced_setting_root = view.findViewById(R.id.advanced_setting_root);
        game_create_advanced_switch_arrow = (ImageView) view.findViewById(R.id.game_create_advanced_switch_arrow);
        ll_advanced_config_new = view.findViewById(R.id.ll_advanced_config_new);
        switch_game_control_into = (SwitchButton) view.findViewById(R.id.switch_game_control_into);
        switch_game_mode_insurance = (SwitchButton) view.findViewById(R.id.switch_game_mode_insurance);
        mDeskNumSeekBarNew = (NodeSeekBar) view.findViewById(R.id.mDeskNumSeekBarNew);
        mDeskNumSeekBarNew.setData(new int[]{2, 3, 4, 5, 6, 7, 8, 9}, true, false, R.mipmap.game_chip_thumb, "");
        mDeskNumSeekBarNew.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                playModeChange(mPlayMode);
            }
        });
        mDeskNumSeekBarNew.setProgress(7);//默认单桌9人
        advanced_switch_tv = (TextView) view.findViewById(R.id.advanced_switch_tv);
        game_create_advanced_switch = view.findViewById(R.id.game_create_advanced_switch);
        game_create_advanced_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvancedConfig(!isAdvancedConfigShow);
            }
        });
        normal_game_ip_switch = (SwitchButton) view.findViewById(R.id.normal_game_ip_switch);
        normal_game_gps_switch = (SwitchButton) view.findViewById(R.id.normal_game_gps_switch);
    }

    private void createGameByGroup() {
        String gameName = edt_game_name.getText().toString();
        if (StringUtil.isSpace(gameName)) {
            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_notnull, Toast.LENGTH_SHORT).show();
            return;
        }
        mGameName = new GameName(gameName);
        HordeEntity consumeEntity = mHordeEntity == null ? (switch_horde != null && switch_horde.isChecked() ? mSelectedHordeEntity : null) : mHordeEntity;//这两个hordeentity不能同时存在的，至少一个为null，mHordeEntity表示从部落大厅传过来的
        boolean hasSufficientDiamond = switch_horde != null && switch_horde.isChecked() ? (consumeEntity == null ? true : diamonds >= consumeEntity.money) : true;
        int insurance = insurance_container.getVisibility() == View.VISIBLE && switch_game_mode_insurance.isChecked() ? GameConstants.GAME_MODE_INSURANCE_POOL : GameConstants.GAME_MODE_INSURANCE_NOT;
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            GameNormalConfig gameNormalConfig = new GameNormalConfig();
            gameNormalConfig.blindType = (gameBlinksConfigType);//游戏创建的时候是type(档位)，   服务端返回sblinds用的也是这个字段，但是不是档位了，是具体的值（蛋疼）
            gameNormalConfig.timeType = (mTimeSeekBar.getCurrentTimeProgress());//创建游戏传的是档位，服务端返回json解析是数值秒，（蛋疼）
            gameNormalConfig.anteMode = (anteMode);
            gameNormalConfig.ante = (create_normal_ante_seekbar.getDataItem(create_normal_ante_seekbar.currentPosition));
            gameNormalConfig.tiltMode = (insurance);
            gameNormalConfig.matchPlayer = (mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition));
            gameNormalConfig.min_chips = min_chip_seekbar.getDataItem(min_chip_seekbar.currentPosition);
            gameNormalConfig.max_chips = max_chip_seekbar.getDataItem(max_chip_seekbar.currentPosition);
            gameNormalConfig.total_chips = total_chip_seekbar.getDataItem(total_chip_seekbar.currentPosition);
            gameNormalConfig.check_ip = normal_game_ip_switch.isChecked() ? 1 : 0;
            gameNormalConfig.check_gps = normal_game_gps_switch.isChecked() ? 1 : 0;
            gameNormalConfig.check_straddle = normal_game_straddle_switch.isChecked() ? 1 : 0;
            //下面的值不传给服务端，只是保存带本地
            int sblinds =  GameConfigData.getNormalSBlinds()[gameBlinksConfigType];
            gameNormalConfig.sblinds = sblinds;
            gameNormalConfig.is_control = switch_game_control_into.isChecked() ? 1 : 0;
            gameNormalConfig.chip_seekbar_index = mChipsSeekBar.currentPosition;
            gameNormalConfig.min_chips_index = min_chip_seekbar.currentPosition;
            gameNormalConfig.max_chips_index = max_chip_seekbar.currentPosition;
            gameNormalConfig.total_chips_index = total_chip_seekbar.currentPosition;
            gameNormalConfig.ante_index = create_normal_ante_seekbar.currentPosition;
            gameNormalConfig.matchPlayer_index = mDeskNumSeekBarNew.currentPosition;
            gameNormalConfig.timeType_index = gameNormalConfig.timeType;
            ((GameCreateActivity) getActivity()).createGameByGroupNew(gameNormalConfig, GameConstants.GAME_MODE_NORMAL, switch_game_control_into.isChecked(), mGameName, consumeEntity, hasSufficientDiamond);
            saveCreateGameConfig(gameNormalConfig);
        }
    }

    private void initSeekBar() {
        mTimeSeekBar.setData(GameConfigData.getNormalDuration(), true, true, R.mipmap.game_time_thumb, "");
        mTimeSeekBar.setProgress(0);
        mChipsSeekBar.mSeekBar.shouldDrawDot = false;
        mChipsSeekBar.setData(GameConfigData.getNormalSBlinds(), false, false, R.mipmap.game_chip_thumb, "");
        mChipsSeekBar.setProgress(0);
        mChipsSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                gameBlinksConfigType = progress;
                updataGameConfigUI();
                //共享部落的钻石消耗
                int hordeDiamondCOnsume = GameConfigData.create_game_fee[gameBlinksConfigType < GameConfigData.create_game_fee.length ? gameBlinksConfigType : GameConfigData.create_game_fee.length - 1];
                if (sure_horde_consume_num != null) {
                    sure_horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
                if (horde_consume_num != null) {
                    horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
            }
        });
    }

    public void updataGameConfigUI() {
        updateChipsData();
        int sblinds = GameConfigData.getNormalSBlinds()[gameBlinksConfigType];
        int minChips = GameConfigData.getNormalChips()[gameBlinksConfigType];
        int serviceFee = GameConfigHelper.getServiceFee(minChips);
        tv_game_blind.setText(GameConstants.getGameBlindsShow(sblinds));
        tv_game_chip.setText(GameConstants.getGameChipsShow(minChips));
        updateAnteUI();
//        if (coins >= (serviceFee + chips)) {
//            tv_game_chip.setTextColor(getResources().getColor(android.R.color.white));
//            tv_game_blind.setTextColor(getResources().getColor(android.R.color.white));
//            tv_game_coins.setTextColor(getResources().getColor(R.color.game_create_text_color));
//            tv_game_servicefee.setTextColor(getResources().getColor(R.color.game_create_text_color));
//        } else {
//            tv_game_chip.setTextColor(getResources().getColor(R.color.game_create_text_color));
//            tv_game_blind.setTextColor(getResources().getColor(R.color.game_create_text_color));
//            tv_game_coins.setTextColor(getResources().getColor(R.color.authcode_countdown_color));
//            tv_game_servicefee.setTextColor(getResources().getColor(R.color.authcode_countdown_color));
//        }
//        mAnteSeekBarNew.updateSBlinds(GameConfigData.getNormalAnte()[gameBlinksConfigType]);
//        int anteTypes = GameConfigData.getNormalAnte()[gameBlinksConfigType];
//        int [] newAntes = new int [] {GameConfigData.anteMode[0] * anteTypes, GameConfigData.anteMode[1] * anteTypes, GameConfigData.anteMode[2] * anteTypes, GameConfigData.anteMode[3] * anteTypes};
//        mAnteSeekBarNew.updateAnteData(newAntes);
    }

    /**
     * 显示带入设置
     *
     * @param show
     */
    public void showCheckinSetting(boolean show) {
        isCheckinSettingShow = show;
        //新版本需求样式新view
        game_create_checkin_setting_switch_arrow.setImageResource(isCheckinSettingShow ? R.mipmap.arrow_advance_up : R.mipmap.arrow_advance_down);
        checkin_setting_content_container.setVisibility(isCheckinSettingShow ? View.VISIBLE : View.GONE);
        checkin_setting_switch_tv.setText(getResources().getString(isCheckinSettingShow ? R.string.circle_content_packup : R.string.game_create_checkin_setting));
    }

    /**
     * 显示高级设置
     *
     * @param show
     */
    public void showAdvancedConfig(boolean show) {
        isAdvancedConfigShow = show;
        //新版本需求样式新view
        game_create_advanced_switch_arrow.setImageResource(isAdvancedConfigShow ? R.mipmap.arrow_advance_up : R.mipmap.arrow_advance_down);
        ll_advanced_config_new.setVisibility(isAdvancedConfigShow ? View.VISIBLE : View.GONE);
        advanced_switch_tv.setText(getResources().getString(isAdvancedConfigShow ? R.string.circle_content_packup : R.string.game_create_config_advanced));
    }

    private void updateChipsData() {
        //最大带入默认是最小带入的5倍    总带入默认是无限
        int beginMinChipsIndexNum = GameConfigData.getNormalSBlinds()[gameBlinksConfigType] * 100;
        for (int i = 0; i < GameConfigData.minChipsIndexNum; i++) {
            minChipsData[i] = beginMinChipsIndexNum * (i + 1);
        }
        for (int i = 0; i < GameConfigData.maxChipsIndexNum; i++) {
            maxChipsData[i] = beginMinChipsIndexNum * (i + 1) * 2;
        }
        maxChipsData[GameConfigData.maxChipsIndexNum - 1] = Integer.MAX_VALUE;//是int最大值，传给服务端时改成-1，不能在这里改成-1
        for (int i = 0; i < GameConfigData.totalChipsIndexNum; i++) {
            totalChipsData[i] = beginMinChipsIndexNum * (i + 1) * 2;
        }
        totalChipsData[GameConfigData.totalChipsIndexNum - 1] = Integer.MAX_VALUE;//是int最大值，传给服务端时改成-1，不能在这里改成-1
        if (min_chip_seekbar == null || max_chip_seekbar == null || total_chip_seekbar == null || create_normal_ante_seekbar == null) {
            return;
        }
        //先更新数据
        min_chip_seekbar.onlyUpdateData(minChipsData);
        max_chip_seekbar.onlyUpdateData(maxChipsData);
        total_chip_seekbar.onlyUpdateData(totalChipsData);
        create_normal_ante_seekbar.onlyUpdateData(GameConfigData.antes[gameBlinksConfigType < GameConfigData.antes.length ? gameBlinksConfigType : GameConfigData.antes.length - 1]);
        //更新完数据再刷新界面
        //先更新最小带入界面：满足最小带入==新的 最小带入（最小带入有两个地方拖动设置）
        int minChips = GameConfigData.getNormalChips()[gameBlinksConfigType];
        if (minChips != min_chip_seekbar.getDataItem(min_chip_seekbar.currentPosition)) {
            min_chip_seekbar.setProgress(1);
        }
//        min_chip_seekbar.setProgress(findMinChipsIndexByNum(minChips));这个方法也能把"最小带入"调到正确位置，但是循环遍历浪费时间
        min_chip_num.setText("" + GameConstants.getGameChipsShow(min_chip_seekbar.getDataItem(min_chip_seekbar.currentPosition)));
        max_chip_num.setText("" + (max_chip_seekbar.currentPosition == maxChipsData.length - 1 ? "无限" : GameConstants.getGameChipsShow(max_chip_seekbar.getDataItem(max_chip_seekbar.currentPosition))));
        total_chip_num.setText("" + (total_chip_seekbar.currentPosition == totalChipsData.length - 1 ? "无限" : GameConstants.getGameChipsShow(total_chip_seekbar.getDataItem(total_chip_seekbar.currentPosition))));
        create_normal_ante_num.setText("" + create_normal_ante_seekbar.getDataItem(create_normal_ante_seekbar.currentPosition));
    }

    //根据具体的数值查找"最小带入"数组对应的index
    private int findMinChipsIndexByNum(int num) {
        for (int i = 0; i < minChipsData.length; i++) {
            if (minChipsData[i] == num) {
                return i;
            }
        }
        return 1;
    }

    //根据具体的数值查找"最小带入"数组对应的index
    private int findSmallMinChipsIndexByNum(int num) {
        for (int i = min_chip_seekbar.currentPosition - 1; i >= 0; i--) {
            if (minChipsData[i] <= num) {
                return i;
            }
        }
        return 0;
    }

    //根据具体的数值查找"最大带入"数组对应的index
    private int findMaxChipsIndexByNum(int num) {
        for (int i = max_chip_seekbar.currentPosition + 1; i < maxChipsData.length; i++) {
            if (maxChipsData[i] >= num) {
                return i;
            }
        }
        return maxChipsData.length - 1;
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        updataGameConfigUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    EditText edt_game_name;
    GameName mGameName;
    private void initGamename() {
        edt_game_name = (EditText) view.findViewById(R.id.edt_game_name);
        edt_game_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(GameConstants.MAX_GAMENAME_LENGTH), new NameRuleFilter()});
        int ownGameCount = GamePreferences.getInstance(ChessApp.sAppContext).getOwngameCount();
        String prefix = GamePreferences.getInstance(ChessApp.sAppContext).getOwngamePrefix();
        mGameName = new GameName(prefix + ownGameCount);
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
        String historyListStr = CreateGameConfigPref.getInstance(getActivity()).getNormalConfig();
        LogUtil.i("getCreateGameConfig: " + historyListStr);
        Type type = new TypeToken<GameNormalConfig>() {}.getType();
        if (!StringUtil.isSpace(historyListStr)) {
            mGameConfig = GsonUtils.getGson().fromJson(historyListStr, type);
        }
    }

    private void saveCreateGameConfig(GameNormalConfig gameNormalConfig) {//保存创建牌局的设置
        String historyMgrListStr = GsonUtils.getGson().toJson(gameNormalConfig);
        LogUtil.i("saveCreateGameConfig: " + historyMgrListStr);
        CreateGameConfigPref.getInstance(getActivity()).setNormalConfig(historyMgrListStr);//把管理员历史记录写入到sharedpreference
    }

    public int mPlayMode;//游戏模式，0="德州扑克"或者1="奥马哈"
    private void setGameConfig() {
        insurance_container.setVisibility(mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? View.VISIBLE : View.GONE);
        if (mGameConfig == null) {
            return;
        }
        mChipsSeekBar.setProgress(mGameConfig.chip_seekbar_index >= mChipsSeekBar.datas.length ? mChipsSeekBar.datas.length - 1 : mGameConfig.chip_seekbar_index);
        max_chip_seekbar.setProgress(mGameConfig.max_chips_index >= max_chip_seekbar.datas.length ? max_chip_seekbar.datas.length - 1 : mGameConfig.max_chips_index);
        total_chip_seekbar.setProgress(mGameConfig.total_chips_index >= total_chip_seekbar.datas.length ? total_chip_seekbar.datas.length - 1 : mGameConfig.total_chips_index);
        create_normal_ante_seekbar.setProgress(mGameConfig.ante_index >= create_normal_ante_seekbar.datas.length ? create_normal_ante_seekbar.datas.length - 1 : mGameConfig.ante_index);
        mDeskNumSeekBarNew.setProgress(mGameConfig.matchPlayer_index >= mDeskNumSeekBarNew.datas.length ? mDeskNumSeekBarNew.datas.length - 1 : mGameConfig.matchPlayer_index);
        switch_game_control_into.setChecked(mGameConfig.is_control == 1);
        switch_game_mode_insurance.setChecked(mGameConfig.tiltMode != 0);
        normal_game_ip_switch.setChecked(mGameConfig.check_ip == 1);
        normal_game_gps_switch.setChecked(mGameConfig.check_gps == 1);
        mTimeSeekBar.setProgress(mGameConfig.timeType_index >= mTimeSeekBar.datas.length ? mTimeSeekBar.datas.length - 1 : mGameConfig.timeType_index);
        normal_game_straddle_switch.setChecked(mGameConfig.check_straddle == 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                min_chip_seekbar.setProgress(mGameConfig.min_chips_index >= min_chip_seekbar.datas.length ? min_chip_seekbar.datas.length - 1 : mGameConfig.min_chips_index);//最小带入拖动条和mChipsSeekBar拖动条有联动，让最小带入拖动条延迟设置index
            }
        }, 100);
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
