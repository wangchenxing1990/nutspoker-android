package com.htgames.nutspoker.ui.fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.GameName;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.config.GameConfigData;
import com.netease.nim.uikit.common.preference.CreateGameConfigPref;
import com.netease.nim.uikit.common.preference.GamePreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.htgames.nutspoker.game.view.DateSelectPopDialog;
import com.netease.nim.uikit.common.DateTools;
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
 * MTT模式
 */
public class CreateMTTFrg extends BaseGameCreateFragment implements View.OnClickListener, GameCreateActivity.IPlayModeChange {
    public static int mtt_sblinds_mode = 1;//mtt盲注结构表类型 1普通  2快速
    public static int mtt_start_sblinds_index = 0;//mtt起始盲注seekbar拖动条index
    public String teamId;
    public HordeEntity mHordeEntity;
    public HordeEntity mSelectedHordeEntity;
    private int diamond_match = GameConstants.MATCH_TYPE_NORMAL;//是否是钻石赛
    private boolean can_create_diamond_match = true;//是否能创建钻石赛
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
    TextView tv_game_blind;//大小盲
    TextView tv_game_chip;//带入筹码
    TextView tv_game_chip_desc;
    //盲注设置
    boolean isBlindSettingShow = false;
    View game_create_blind_switch;
    TextView blind_switch_tv;
    TextView mtt_start_sblinds_num;
    TextView tv_match_blinds_time_num;
    ImageView game_create_blind_switch_arrow;
    View ll_blind_config_content;
    TextView mtt_select_blind_tv_normal;
    TextView mtt_select_blind_tv_quick;
    NodeSeekBar mtt_start_blind_seekbar;
    NodeSeekBar mFeeSeekBar;
    NodeSeekBar mChipsSeekBar;
    NodeSeekBar mTimeSeekBar;
    NodeSeekBar mBlindsLevelSeekBar;
    NodeSeekBar mtt_rebuy_seekbar;
    //
    SwitchButton ck_game_control_into;
    SwitchButton ck_game_match_rest;
    //
    TextView tv_game_checkin_fee;
    TextView tv_game_create_blind_level_new;
    TextView tv_mtt_relation;
    LinearLayout ll_game_checkin_fee_rule;
    LinearLayout rl_match_start_date_select;
    //高级设置
    boolean isAdvancedConfigShow = false;
    TextView checkin_player_limit_click;
    TextView checkin_player_limit_num;
    NodeSeekBar checkin_player_limit_seekbar;//总买入次数上限，不是报名人数上限
    private int gameDurationConfigType = 2;//初始3分钟
    private int gameTerminationBlinks = 0;//终止盲注等级
    //时间相关
    DateSelectPopDialog mDateSelectPop;
    TextView tv_match_start_date_select;
    TextView tv_auto_start_des;
    View time_set_des_container;
    SwitchButton switch_auto_start;
    long choiceTime = -1;
    ////////////////////////////////////////////猎人赛相关begin////////////////////////////////////////////
    SwitchButton hunter_switch;
    View ll_hunter_config_content;
    EasySwitchBtn iv_hunter_normal;
    EasySwitchBtn iv_hunter_super;
    TextView tv_hunter_normal_des;
    TextView tv_hunter_super_des;
    TextView tv_normal_hunter_ratio;
    TextView tv_super_hunter_ratio;
    NodeSeekBar seekbar_normal_hunter_ratio;
    NodeSeekBar seekbar_super_hunter_ratio;
    ImageView tv_normal_hunter_ratio_click;
    ImageView tv_super_hunter_ratio_click;
    private int mKoMode;
    ////////////////////////////////////////////猎人赛相关end////////////////////////////////////////////
    private GameMttConfig mGameConfig;
    public static CreateMTTFrg newInstance() {
        CreateMTTFrg mInstance = new CreateMTTFrg();
        return mInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        can_create_diamond_match = UserPreferences.getInstance(getActivity()).getLevel() == 100;//只有level==100的时候才能创建钻石赛
        getCreateGameConfig();
        super.onCreate(savedInstanceState);
        teamId = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).teamId : "";
        mHordeEntity = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).mHordeEntity : null;
        costList = getActivity() instanceof GameCreateActivity ? ((GameCreateActivity) getActivity()).costList : null;
        setFragmentName(CreateMTTFrg.class.getSimpleName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (getActivity() instanceof GameCreateActivity) {
            mPlayMode = ((GameCreateActivity) getActivity()).mPlayMode;
        }
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_game_create_mtt, container, false);
        initMttChipsSeekbar();//这个拖动条比较麻烦，单独领出来处理          mtt起始记分牌是以前的数组(GameConstants.mttChipsNum)乘以起始大盲注
        initGamename();
        initView();//托动条联动太多，view的初始化顺序很重要，别乱改
        initMatchType();//设置比赛类型
        initSureHordeView();
        initSelectHordeView();//初始化horde相关的配置
        initHunterRelated();//初始化猎人赛相关
        initSeekBar();
        setGameConfig();
        return view;
    }

    View previousHorde;

    @Override
    public void playModeChange(int play_mode) {
        if (play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            return;//大菠萝不处理
        }
        mPlayMode = play_mode;
        mDeskNumData = mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? GameConfigData.mtt_desk_num : GameConfigData.mtt_desk_num_omaha;
        if (mDeskNumSeekBarNew != null) {
            mDeskNumSeekBarNew.setData(mDeskNumData, true, false, R.mipmap.game_chip_thumb, "");
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
            int hordeDiamondCOnsume = GameConfigData.create_mtt_fee[mFeeSeekBar.currentPosition < GameConfigData.create_mtt_fee.length ? mFeeSeekBar.currentPosition : GameConfigData.create_mtt_fee.length - 1];
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
            int it = mFeeSeekBar == null ? 0 : mFeeSeekBar.currentPosition;
            int hordeDiamondCOnsume = it < GameConfigData.create_mtt_fee.length ? GameConfigData.create_mtt_fee[it] : GameConfigData.create_mtt_fee[GameConfigData.create_mtt_fee.length - 1];
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
        int it = mFeeSeekBar == null ? 0 : mFeeSeekBar.currentPosition;
        int hordeDiamondCOnsume = it < GameConfigData.create_mtt_fee.length ? GameConfigData.create_mtt_fee[it] : GameConfigData.create_mtt_fee[GameConfigData.create_mtt_fee.length - 1];
        horde_consume_num.setText(hordeDiamondCOnsume + "");
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
        //盲注设置
        tv_game_chip_desc = (TextView) view.findViewById(R.id.tv_game_chip_desc);
        tv_game_chip = (TextView) view.findViewById(R.id.tv_game_chip);
        game_create_blind_switch = view.findViewById(R.id.game_create_blind_switch);
        game_create_blind_switch.setOnClickListener(this);
        blind_switch_tv = (TextView) view.findViewById(R.id.blind_switch_tv);
        ll_blind_config_content = view.findViewById(R.id.ll_blind_config_content);
        game_create_blind_switch_arrow = (ImageView) view.findViewById(R.id.game_create_blind_switch_arrow);
        mtt_start_blind_seekbar = (NodeSeekBar) view.findViewById(R.id.mtt_start_blind_seekbar);
        if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
            if (mtt_sblinds_mode == 1) {
                mtt_start_blind_seekbar.setData(GameConfigData.mtt_sblins, false, false, R.mipmap.game_chip_thumb, "");
            } else if (mtt_sblinds_mode == 2) {
                mtt_start_blind_seekbar.setData(GameConfigData.mtt_sblins_quick, false, false, R.mipmap.game_chip_thumb, "");
            }
        } else if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
            if (mtt_sblinds_mode == 1) {
                mtt_start_blind_seekbar.setData(GameConfigData.omaha_mtt_sblins, false, false, R.mipmap.game_chip_thumb, "");
            } else if (mtt_sblinds_mode == 2) {
                mtt_start_blind_seekbar.setData(GameConfigData.omaha_mtt_sblins_quick, false, false, R.mipmap.game_chip_thumb, "");
            }
        }
        mtt_start_blind_seekbar.setProgress(mtt_start_sblinds_index);
        mtt_start_blind_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                mtt_start_sblinds_index = progress;
//                notifyDataSetChanged();
                setStartBlind();
            }
        });
        mtt_start_sblinds_num = (TextView) view.findViewById(R.id.mtt_start_sblinds_num);
        setStartBlind();
        mtt_select_blind_tv_normal = (TextView) view.findViewById(R.id.mtt_select_blind_tv_normal);
        mtt_select_blind_tv_normal.setOnClickListener(this);
        mtt_select_blind_tv_normal.setSelected(mtt_sblinds_mode == 1 ? true : false);
        mtt_select_blind_tv_quick = (TextView) view.findViewById(R.id.mtt_select_blind_tv_quick);
        mtt_select_blind_tv_quick.setOnClickListener(this);
        mtt_select_blind_tv_quick.setSelected(mtt_sblinds_mode == 2 ? true : false);
        //
        view.findViewById(R.id.rebuy_instructions).setOnClickListener(this);
        view.findViewById(R.id.rest_instructions).setOnClickListener(this);
        rl_match_start_date_select = (LinearLayout) view.findViewById(R.id.rl_match_start_date_select);
        btn_game_start = (Button) view.findViewById(R.id.btn_game_start);
        ck_game_control_into = (SwitchButton) view.findViewById(R.id.ck_game_control_into);
        tv_game_blind = (TextView) view.findViewById(R.id.tv_game_blind);
        tv_game_checkin_fee = (TextView) view.findViewById(R.id.tv_game_checkin_fee);
        //
        tv_match_start_date_select = (TextView) view.findViewById(R.id.tv_match_start_date_select);
        tv_auto_start_des = (TextView) view.findViewById(R.id.tv_auto_start_des);
        switch_auto_start = (SwitchButton) view.findViewById(R.id.switch_auto_start);
        switch_auto_start.setChecked(true);
        time_set_des_container = view.findViewById(R.id.time_set_des_container);
        view.findViewById(R.id.tv_mtt_relation).setVisibility(View.GONE);//sng和mtt共用的盲注结构表(TextView)gone掉，mtt有自己的盲注结构表(TextView)
        tv_match_blinds_time_num = (TextView) view.findViewById(R.id.tv_match_blinds_time_num);
        tv_mtt_relation = (TextView) view.findViewById(R.id.mtt_blinds_click_tv);
        tv_mtt_relation.setOnClickListener(this);
        //
        tv_game_create_blind_level_new = (TextView) view.findViewById(R.id.tv_game_create_blind_level_new);
        ll_game_checkin_fee_rule = (LinearLayout) view.findViewById(R.id.ll_game_checkin_fee_rule);
        rl_match_start_date_select.setOnClickListener(this);
        ll_game_checkin_fee_rule.setOnClickListener(this);
        ck_game_control_into.setChecked(true);
        mFeeSeekBar = (NodeSeekBar) view.findViewById(R.id.mFeeSeekBar);
        mTimeSeekBar = (NodeSeekBar) view.findViewById(R.id.mTimeSeekBar);
        mTimeSeekBar.mSeekBar.shouldDrawDot = false;
        mBlindsLevelSeekBar = (NodeSeekBar) view.findViewById(R.id.mBlindsLevelSeekBar);
        mBlindsLevelSeekBar.mSeekBar.shouldDrawDot = false;
        btn_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (coins >= (serviceFee + mttChips)) {
                tryCreateGameByGroup();
//                } else {
//                    showTopUpDialog();
//                }
            }
        });
        //高级设置
        mtt_rebuy_seekbar = (NodeSeekBar) view.findViewById(R.id.mtt_rebuy_seekbar);
        mtt_rebuy_seekbar.setData(GameConfigData.mtt_rebuy_count, true, false, R.mipmap.game_chip_thumb, "");
        ck_game_match_rest = (SwitchButton) view.findViewById(R.id.ck_game_match_rest);
        checkin_player_limit_click = (TextView) view.findViewById(R.id.checkin_player_limit_click);
        checkin_player_limit_click.setOnClickListener(this);
        mDeskNumData = mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ? GameConfigData.mtt_desk_num : GameConfigData.mtt_desk_num_omaha;
        mDeskNumSeekBarNew = (NodeSeekBar) view.findViewById(R.id.mDeskNumSeekBarNew);
        mDeskNumSeekBarNew.setData(mDeskNumData, true, false, R.mipmap.game_chip_thumb, "");
        checkin_player_limit_seekbar = (NodeSeekBar) view.findViewById(R.id.checkin_player_limit_seekbar);
        checkin_player_limit_seekbar.setData(GameConfigData.mtt_checkin_limit, false, false, R.mipmap.game_chip_thumb, "");
        checkin_player_limit_num = (TextView) view.findViewById(R.id.checkin_player_limit_num);
        checkin_player_limit_num.setText("" + checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition));
        checkin_player_limit_seekbar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                checkin_player_limit_num.setText("" + checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition));
                //单桌人数拖动条和总买入次数上限拖动条  ---联动，保证  总买入次数上限 > 单桌人数
                int destinationNum = checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition);
                if (mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition) >= destinationNum) {
                    desknumLinkMaxBuy(checkin_player_limit_seekbar);
                }
            }
        });
        checkin_player_limit_seekbar.setProgress(GameConfigData.mtt_checkin_limit.length <= 11 ? GameConfigData.mtt_checkin_limit.length - 1 : 11);//默认1000
        mDeskNumSeekBarNew.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                //单桌人数拖动条和总买入次数上限拖动条  ---联动，保证  总买入次数上限 > 单桌人数
                int destinationNum = mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition);
                if (checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition) <= destinationNum) {
                    desknumLinkMaxBuy(mDeskNumSeekBarNew);
                }
            }
        });
        mDeskNumSeekBarNew.setProgress(1);//默认单桌9人
        game_create_advanced_switch_arrow = (ImageView) view.findViewById(R.id.game_create_advanced_switch_arrow);
        ll_advanced_config_new = view.findViewById(R.id.ll_advanced_config_new);
        advanced_switch_tv = (TextView) view.findViewById(R.id.advanced_switch_tv);
        game_create_advanced_switch = view.findViewById(R.id.game_create_advanced_switch);
        game_create_advanced_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvancedConfig(!isAdvancedConfigShow);
            }
        });
    }

    TextView match_type_normal;
    TextView match_type_gold;
    TextView match_type_diamond;
    int normalColor;
    int selectColor;
    GradientDrawable normalDrawable;
    Drawable selectDrawable;
    View iv_explain_checkin_fee;
    private void initMatchType() {
        iv_explain_checkin_fee = view.findViewById(R.id.iv_explain_checkin_fee);
        iv_explain_checkin_fee.setOnClickListener(this);
        iv_explain_checkin_fee.setVisibility(diamond_match == GameConstants.MATCH_TYPE_NORMAL ? View.INVISIBLE : View.VISIBLE);
        if (can_create_diamond_match) {
            Resources resources = getActivity().getResources();
            normalColor = resources.getColor(R.color.shop_text_no_select_color);
            selectColor = resources.getColor(R.color.white);
            normalDrawable = new GradientDrawable();
            normalDrawable.setCornerRadius(ScreenUtil.dp2px(getActivity(), 4f));
            normalDrawable.setColor(resources.getColor(R.color.register_page_bg_color));
            normalDrawable.setStroke(ScreenUtil.dp2px(getActivity(), 1f), resources.getColor(R.color.list_item_bg_press));
            selectDrawable = resources.getDrawable(R.drawable.bg_login_btn);
            View.OnClickListener match_type_click = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int viewId = v.getId();
                    if (viewId == R.id.match_type_normal) {
                        diamond_match = GameConstants.MATCH_TYPE_NORMAL;
                    } else if (viewId == R.id.match_type_gold) {
                        diamond_match = GameConstants.MATCH_TYPE_GOLD;
                    } else if (viewId == R.id.match_type_diamond) {
                        diamond_match = GameConstants.MATCH_TYPE_DIAMOND;
                    }
                    setMatchTypeUI();
                }
            };
            ((ViewStub) view.findViewById(R.id.view_stub_match_type)).inflate();
            match_type_normal = (TextView) view.findViewById(R.id.match_type_normal);
            match_type_normal.setOnClickListener(match_type_click);
            match_type_gold = (TextView) view.findViewById(R.id.match_type_gold);
            match_type_gold.setOnClickListener(match_type_click);
            match_type_diamond = (TextView) view.findViewById(R.id.match_type_diamond);
            match_type_diamond.setOnClickListener(match_type_click);
            setMatchTypeUI();
        }
    }

    private void setMatchTypeUI() {
        if (!can_create_diamond_match) {
            return;
        }
        match_type_normal.setBackgroundDrawable(diamond_match == GameConstants.MATCH_TYPE_NORMAL ? selectDrawable : normalDrawable);
        match_type_normal.setTextColor(diamond_match == GameConstants.MATCH_TYPE_NORMAL ? selectColor : normalColor);
        match_type_gold.setBackgroundDrawable(diamond_match == GameConstants.MATCH_TYPE_GOLD ? selectDrawable : normalDrawable);
        match_type_gold.setTextColor(diamond_match == GameConstants.MATCH_TYPE_GOLD ? selectColor : normalColor);
        match_type_diamond.setBackgroundDrawable(diamond_match == GameConstants.MATCH_TYPE_DIAMOND ? selectDrawable : normalDrawable);
        match_type_diamond.setTextColor(diamond_match == GameConstants.MATCH_TYPE_DIAMOND ? selectColor : normalColor);
        iv_explain_checkin_fee.setVisibility(diamond_match == GameConstants.MATCH_TYPE_NORMAL ? View.INVISIBLE : View.VISIBLE);
        if (mFeeSeekBar != null) {
            mFeeSeekBar.onlyUpdateData(diamond_match == GameConstants.MATCH_TYPE_NORMAL ? GameConstants.mttCheckInFeeNum : (diamond_match == GameConstants.MATCH_TYPE_GOLD ? GameConfigData.mtt_checkin_gold : GameConfigData.mtt_checkin_diamond));
            setFee(mFeeSeekBar.currentPosition);
        }
    }

    NodeSeekBar mDeskNumSeekBarNew;
    private int[] mDeskNumData = GameConfigData.mtt_desk_num;
    ImageView game_create_advanced_switch_arrow;
    View ll_advanced_config_new;
    TextView advanced_switch_tv;
    View game_create_advanced_switch;

    int[] ko_reward_rate = GameConfigData.ko_reward_rate;//这个数组过滤掉以5结尾的数字，checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
    private void initHunterRelated() {
        ll_hunter_config_content = view.findViewById(R.id.ll_hunter_config_content);
        iv_hunter_normal = (EasySwitchBtn) view.findViewById(R.id.iv_hunter_normal);
        iv_hunter_normal.setOnClickListener(this);
        iv_hunter_super = (EasySwitchBtn) view.findViewById(R.id.iv_hunter_super);
        iv_hunter_super.setOnClickListener(this);
        tv_hunter_normal_des = (TextView) view.findViewById(R.id.tv_hunter_normal_des);
        tv_hunter_normal_des.setOnClickListener(this);
        tv_hunter_super_des = (TextView) view.findViewById(R.id.tv_hunter_super_des);
        tv_hunter_super_des.setOnClickListener(this);
        tv_normal_hunter_ratio = (TextView) view.findViewById(R.id.tv_normal_hunter_ratio);
        tv_normal_hunter_ratio.setText(GameConfigData.ko_reward_rate[0] + "%");
        tv_normal_hunter_ratio_click = (ImageView) view.findViewById(R.id.tv_normal_hunter_ratio_click);
        tv_normal_hunter_ratio_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//赏金比例说明
                showHunterRatioExplain(v, 1);
            }
        });
        tv_super_hunter_ratio = (TextView) view.findViewById(R.id.tv_super_hunter_ratio);
        tv_super_hunter_ratio.setText(GameConfigData.ko_head_rate[0] + "%");
        tv_super_hunter_ratio_click = (ImageView) view.findViewById(R.id.tv_super_hunter_ratio_click);
        tv_super_hunter_ratio_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//超级猎人赛赏金比例说明
                showHunterRatioExplain(v, 2);
            }
        });
        seekbar_normal_hunter_ratio = (NodeSeekBar) view.findViewById(R.id.seekbar_normal_hunter_ratio);
        seekbar_normal_hunter_ratio.setData(ko_reward_rate, false, false, R.mipmap.game_chip_thumb, "");
        seekbar_normal_hunter_ratio.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                tv_normal_hunter_ratio.setText(ko_reward_rate[progress] + "%");
            }
        });
        seekbar_normal_hunter_ratio.setProgress(3);
        seekbar_super_hunter_ratio = (NodeSeekBar) view.findViewById(R.id.seekbar_super_hunter_ratio);
        seekbar_super_hunter_ratio.setData(GameConfigData.ko_head_rate, false, false, R.mipmap.game_chip_thumb, "");
        seekbar_super_hunter_ratio.setEnabled(mKoMode == 2);
        seekbar_super_hunter_ratio.setThumbId(mKoMode == 2 ? R.mipmap.game_chip_thumb : R.mipmap.game_chip_thumb_unenable);
        seekbar_super_hunter_ratio.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                tv_super_hunter_ratio.setText(GameConfigData.ko_head_rate[progress] + "%");
            }
        });
        seekbar_super_hunter_ratio.setProgress(4);
        hunter_switch = (SwitchButton) view.findViewById(R.id.hunter_switch);
        hunter_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ll_hunter_config_content.setVisibility(hunter_switch.isChecked() ? View.VISIBLE : View.GONE);
                if (!hunter_switch.isChecked()) {
                    mKoMode = 0;//重置复位
                } else {
                    mKoMode = (iv_hunter_normal.isChecked) ? (1) : (iv_hunter_super.isChecked ? 2 : 1);
                }
                LogUtil.i("createfragment setOnCheckedChangeListener: " + hunter_switch.isChecked());
            }
        });
        showSuperHunter(false);
    }
    private void showHunterRatioExplain(View view, int ko_mode) {
        int contentId = (ko_mode == 1) ? (R.string.game_create_mtt_normal_hunter_instructions) : (R.string.game_create_mtt_super_hunter_instructions);
        if (instructionsPopView == null) {
            instructionsPopView = new MatchCreateRulesPopView(getContext(), MatchCreateRulesPopView.TYPE_LEFT);
            instructionsPopView.setBackground(R.drawable.bg_mtt_instructions);
        }
        instructionsPopView.tv_match_rule_content.setText(contentId);
        instructionsPopView.tv_match_rule_content.setTextColor(view.getContext().getResources().getColor(R.color.text_select_color));
        instructionsPopView.showAsDropDown(view, ScreenUtil.dip2px(getActivity(), -42f), ScreenUtil.dip2px(getActivity(), -4f));
    }
    private void changeHunterMode(int ko_mode) {
        mKoMode = ko_mode;
        iv_hunter_normal.setChecked((mKoMode == 1) ? true : false);
        iv_hunter_super.setChecked((mKoMode == 2) ? true : false);
        seekbar_super_hunter_ratio.setEnabled(mKoMode == 2);
        seekbar_super_hunter_ratio.setThumbId(mKoMode == 2 ? R.mipmap.game_chip_thumb : R.mipmap.game_chip_thumb_unenable);
        if (!iv_hunter_normal.isChecked && !iv_hunter_super.isChecked) {//如果两个都没有选中，那么默认选中第一个
            iv_hunter_normal.setChecked(true);
        }
    }
    private void showSuperHunter(boolean show) {//是否显示超级猎人赛
        view.findViewById(R.id.super_hunter_divider).setVisibility(show ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.super_hunter_container).setVisibility(show ? View.VISIBLE : View.GONE);
        seekbar_super_hunter_ratio.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    private void changeHunterRewardRate(boolean showAll) {
        if (seekbar_normal_hunter_ratio == null) {
            return;
        }
        if (showAll) {
            ko_reward_rate = GameConfigData.ko_reward_rate;
            seekbar_normal_hunter_ratio.onlyUpdateData(ko_reward_rate);
        } else {
            filterRewardRate();//checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
            seekbar_normal_hunter_ratio.onlyUpdateData(ko_reward_rate);
        }
        tv_normal_hunter_ratio.setText(ko_reward_rate[seekbar_normal_hunter_ratio.currentPosition] + "%");
    }
    private int[] filterRewardRate() {
        int[] newRewardRateData = new int[GameConfigData.ko_reward_rate.length];
        int j = 0;
        for (int i = 0; i < GameConfigData.ko_reward_rate.length; i++) {
            String ss = GameConfigData.ko_reward_rate[i] + "";
            if (ss.endsWith("5")) {
                continue;
            } else {
                newRewardRateData[j] = GameConfigData.ko_reward_rate[i];
                j++;
            }
        }
        int[] result = new int[j];
        for (int i = 0; i < j; i++) {
            result[i] = newRewardRateData[i];
        }
        ko_reward_rate = result;
        return result;
    }

    private void setFee(int progress) {
        int checkinFee = mFeeSeekBar.getDataItem(progress < mFeeSeekBar.datas.length ? progress : mFeeSeekBar.datas.length - 1);//GameConstants.mttCheckInFeeNum[progress];
        int serviceFee = (int) (checkinFee / GameConfigData.MTTServiceRate);
        String checkinFeeStr = GameConstants.getGameChipsShow(checkinFee);
        String serviceFeeStr = GameConstants.getGameChipsShow(serviceFee);
        if (diamond_match != GameConstants.MATCH_TYPE_NORMAL) {
            tv_game_checkin_fee.setText(checkinFeeStr);
        } else {
            tv_game_checkin_fee.setText(checkinFeeStr + "+" + serviceFeeStr);
        }
        if (checkinFee < 100) {
            changeHunterRewardRate(false);//更改猎人赛赏金比例，不允许出现小数  checkinFee * 赏金比例不允许出现小数，赏金比例过滤掉以5结尾的档位
        } else {
            changeHunterRewardRate(true);//显示全部的默认赏金比例的档位
        }
    }

    private void initSeekBar() {
        int startFeeIndex = 4;//报名费初始为4对应400金币
        mFeeSeekBar.setData(diamond_match != GameConstants.MATCH_TYPE_NORMAL ? GameConfigData.mtt_checkin_diamond : GameConstants.mttCheckInFeeNum, false, false, R.mipmap.game_chip_thumb, "");
        mFeeSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                setFee(progress);
                //共享部落的钻石消耗
                int hordeDiamondCOnsume = GameConfigData.create_mtt_fee[progress < GameConfigData.create_mtt_fee.length ? progress : GameConfigData.create_mtt_fee.length - 1];
                if (sure_horde_consume_num != null) {
                    sure_horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
                if (horde_consume_num != null) {
                    horde_consume_num.setText(hordeDiamondCOnsume + "");
                }
            }
        });
        mFeeSeekBar.setProgress(startFeeIndex);//setProgress(0)初始化设置0位置不会触发setOnNodeChangeListener事件
//        int checkinFee = GameConstants.mttCheckInFeeNum[startFeeIndex];
//        int serviceFee = (int) (checkinFee / GameConfigData.MTTServiceRate);
//        String checkinFeeStr = GameConstants.getGameChipsShow(checkinFee);
//        String serviceFeeStr = GameConstants.getGameChipsShow(serviceFee);
//        tv_game_checkin_fee.setText(checkinFeeStr + "+" + serviceFeeStr);
        mTimeSeekBar.setData(GameConstants.sngTimeMinutes, false, true, R.mipmap.game_chip_thumb, getString(R.string.minutes));
        mBlindsLevelSeekBar.setData(GameConstants.mttBlindLevelInts, false, false, R.mipmap.game_chip_thumb, "");
        mBlindsLevelSeekBar.setProgress(0);
        mBlindsLevelSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                gameTerminationBlinks = progress;
                updateBlindLevel();
            }
        });
        updateBlindLevel();
        mTimeSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                int duration = GameConstants.sngTimeMinutes[progress];
                tv_match_blinds_time_num.setText(duration + "分钟");
            }
        });
        mTimeSeekBar.setProgress(gameDurationConfigType);
    }

    private int[] mttChipsData = new int[GameConstants.mttChipsNum.length];
    //这个拖动条比较麻烦，单独领出来处理                 mtt起始记分牌是以前的数组(GameConstants.mttChipsNum)乘以起始大盲注
    private int gameChipsConfigType = 7;//带入筹码 初始化4000
    private void initMttChipsSeekbar() {
        int smallBlind = getStartSmallBlind();
        int bigBlind = smallBlind * 2;
        for (int i = 0; i < mttChipsData.length; i++) {
            mttChipsData[i] = (int) (GameConstants.mttChipsNum[i] * bigBlind);
        }
        mChipsSeekBar = (NodeSeekBar) view.findViewById(R.id.mChipsSeekBar);
        mChipsSeekBar.setData(mttChipsData, false, false, R.mipmap.game_chip_thumb, "");
        mChipsSeekBar.setOnNodeChangeListener(new NodeSeekBar.OnNodeChangeListener() {
            @Override
            public void onNodeChanged(int progress) {
                gameChipsConfigType = progress;
                updataGameConfigUI();
            }
        });
        mChipsSeekBar.setProgress(gameChipsConfigType);
    }

    private void updateMttChipsSeekbar() {
        int smallBlind = getStartSmallBlind();
        int bigBlind = smallBlind * 2;
        for (int i = 0; i < mttChipsData.length; i++) {
            mttChipsData[i] = (int) (GameConstants.mttChipsNum[i] * bigBlind);
        }
        mChipsSeekBar.onlyUpdateData(mttChipsData);
        int mttChips = mttChipsData[gameChipsConfigType];
        String mttChipsStr = GameConstants.getGameChipsShow(mttChips);
        tv_game_chip.setText(mttChipsStr);
        tv_game_chip_desc.setText(getString(R.string.game_chips_bbs, mttChips / 20));
    }

    private int getStartSmallBlind() {//获得起始盲注（小盲）
        int smallBlind = 0;
        if (mtt_sblinds_mode == 1) {//普通表
            int startIndex = mtt_start_sblinds_index >= GameConfigData.mtt_sblins.length ? GameConfigData.mtt_sblins.length - 1 : mtt_start_sblinds_index;
            smallBlind = (int) (GameConfigData.mtt_sblinds_multiple[0] * GameConfigData.mtt_sblins[startIndex]);
        } else if (mtt_sblinds_mode == 2) {//快速表
            int startIndex = mtt_start_sblinds_index >= GameConfigData.mtt_sblins_quick.length ? GameConfigData.mtt_sblins_quick.length - 1 : mtt_start_sblinds_index;
            smallBlind = (int) (GameConfigData.mtt_sblinds_multiple_quick[0] * GameConfigData.mtt_sblins_quick[startIndex]);
        }
        int bigBlind = smallBlind * 2;
        return smallBlind;
    }

    //设置起始盲注值
    private void setStartBlind() {
        int smallBlind = getStartSmallBlind();
        int bigBlind = smallBlind * 2;
        mtt_start_sblinds_num.setText(smallBlind + "/" + bigBlind);
        updateMttChipsSeekbar();
    }

    public void showDateSelectPop() {
        if (mDateSelectPop == null) {
            mDateSelectPop = new DateSelectPopDialog(getActivity());
            mDateSelectPop.setOnDateSelectListener(new DateSelectPopDialog.OnDateSelectListener() {
                @Override
                public void onDateSelect(long date) {
                    choiceTime = date;
                    tv_match_start_date_select.setText(DateTools.getOtherStrTime_ymd_hm("" + date));
                    tv_auto_start_des.setVisibility(View.GONE);
                    switch_auto_start.setVisibility(View.VISIBLE);
                    time_set_des_container.setVisibility(View.VISIBLE);
                }
            });
            mDateSelectPop.mOnDateClearListener = new DateSelectPopDialog.OnDateClearListener() {
                @Override
                public void onDateClear() {
                    choiceTime = -1;
                    tv_match_start_date_select.setText(R.string.game_create_match_start_date_select);
                    tv_auto_start_des.setVisibility(View.VISIBLE);
                    switch_auto_start.setVisibility(View.GONE);
                    time_set_des_container.setVisibility(View.GONE);
                }
            };
        }
        if (getActivity() instanceof GameCreateActivity && !getActivity().isFinishing() && !((GameCreateActivity) getActivity()).isDestroyedCompatible() && !mDateSelectPop.isShowing()) {
            mDateSelectPop.show();
        }
    }

    public void updateBlindLevel() {
        int blindLevel = GameConstants.mttBlindLevelInts[gameTerminationBlinks];
        tv_game_create_blind_level_new.setText(getString(R.string.game_create_blind_level_new, blindLevel));
    }

    //显示盲注设置
    private void showBlindSetting(boolean show) {
        isBlindSettingShow = show;
        game_create_blind_switch_arrow.setImageResource(isBlindSettingShow ? R.mipmap.arrow_advance_up : R.mipmap.arrow_advance_down);
        ll_blind_config_content.setVisibility(isBlindSettingShow ? View.VISIBLE : View.GONE);
        blind_switch_tv.setText(getResources().getString(isBlindSettingShow ? R.string.circle_content_packup : R.string.mtt_blind_setting_des));
    }

    /**
     * 显示高级设置
     * @param show
     */
    public void showAdvancedConfig(boolean show) {
        isAdvancedConfigShow = show;
        //新版本需求样式新view
        game_create_advanced_switch_arrow.setImageResource(isAdvancedConfigShow ? R.mipmap.arrow_advance_up : R.mipmap.arrow_advance_down);
        ll_advanced_config_new.setVisibility(isAdvancedConfigShow ? View.VISIBLE : View.GONE);
        advanced_switch_tv.setText(getResources().getString(isAdvancedConfigShow ? R.string.circle_content_packup : R.string.game_create_config_advanced));
    }

    public void updataGameConfigUI() {
        int mttChips = mttChipsData[gameChipsConfigType];
        String mttChipsStr = GameConstants.getGameChipsShow(mttChips);
        tv_game_chip = (TextView) view.findViewById(R.id.tv_game_chip);//可能空指针
        tv_game_chip.setText(mttChipsStr);
        tv_game_chip_desc = (TextView) view.findViewById(R.id.tv_game_chip_desc);//可能空指针
        tv_game_chip_desc.setText(getString(R.string.game_chips_bbs, mttChips / 20));
//        mTimeSeekBar.setProgress(GameConstants.sngRecommendBlindTimeGear[gameChipsConfigType]);时间和记分牌关联去掉
    }

    private void checkAutoStartDialog() {////设置手动开赛需要用一个对话框提示
        if (choiceTime > -1 && switch_auto_start.getVisibility() == View.VISIBLE && switch_auto_start.isChecked()) {
            createGameByGroup();
            return;
        }
        EasyAlertDialog autoStartDialog = EasyAlertDialogHelper.createOkCancelDiolag(getActivity(), "", "该比赛需要手动开赛，保留15天，15天内未开赛，该比赛将自动解散",
                "开赛" , getString(R.string.cancel),false,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        createGameByGroup();
                    }
                });
        if (getActivity() != null && !getActivity().isFinishing()) {
            autoStartDialog.show();
        }
    }

    private void createGameByGroup() {
        if (getActivity() != null && getActivity() instanceof GameCreateActivity) {
            int duration = GameConstants.sngTimeMinutes[mTimeSeekBar.getCurrentTimeProgress()] * 60;
            HordeEntity consumeEntity = mHordeEntity == null ? (switch_horde != null && switch_horde.isChecked() ? mSelectedHordeEntity : null) : mHordeEntity;//这两个hordeentity不能同时存在的，至少一个为null，mHordeEntity表示从部落大厅传过来的
            boolean hasSufficientDiamond = switch_horde != null && switch_horde.isChecked() ? (consumeEntity == null ? true : diamonds >= consumeEntity.money) : true;
            GameMttConfig gameMttConfig = new GameMttConfig();
            gameMttConfig.matchCheckinFee = mFeeSeekBar.getDataItem(mFeeSeekBar.currentPosition);
            gameMttConfig.matchChips = (mttChipsData[gameChipsConfigType]);
            gameMttConfig.matchDuration = (duration);
            gameMttConfig.beginTime = (choiceTime);
            if (choiceTime == -1) {
                gameMttConfig.is_auto = 0;
            } else {
                gameMttConfig.is_auto = switch_auto_start.isChecked() ? 1 : 0;
            }
            if (ck_game_match_rest.isChecked()) {
                gameMttConfig.restMode = (GameConstants.GAME_MT_REST_MODE);
            } else {
                gameMttConfig.restMode = (GameConstants.GAME_MT_REST_MODE_NOT);
            }
            gameMttConfig.match_rebuy_cnt = mtt_rebuy_seekbar.getDataItem(mtt_rebuy_seekbar.currentPosition);
            gameMttConfig.matchLevel = GameConstants.mttBlindLevelInts[gameTerminationBlinks];
            gameMttConfig.matchPlayer = (mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition));
            gameMttConfig.match_type = diamond_match;
            gameMttConfig.match_max_buy_cnt = checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition);
            gameMttConfig.sblinds_mode = mtt_sblinds_mode;
            int smallBlind = 0;
            int startIndex = 0;
            if (mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM) {
                if (mtt_sblinds_mode == 1) {//普通表
                    startIndex = CreateMTTFrg.mtt_start_sblinds_index >= GameConfigData.mtt_sblins.length ? GameConfigData.mtt_sblins.length - 1 : CreateMTTFrg.mtt_start_sblinds_index;
                    smallBlind = (int) (GameConfigData.mtt_sblinds_multiple[0] * GameConfigData.mtt_sblins[startIndex]);
                } else if (mtt_sblinds_mode == 2) {//快速表
                    startIndex = CreateMTTFrg.mtt_start_sblinds_index >= GameConfigData.mtt_sblins_quick.length ? GameConfigData.mtt_sblins_quick.length - 1 : CreateMTTFrg.mtt_start_sblinds_index;
                    smallBlind = (int) (GameConfigData.mtt_sblinds_multiple_quick[0] * GameConfigData.mtt_sblins_quick[startIndex]);
                }
            } else if (mPlayMode == GameConstants.PLAY_MODE_OMAHA) {
                if (mtt_sblinds_mode == 1) {//普通表
                    startIndex = CreateMTTFrg.mtt_start_sblinds_index >= GameConfigData.omaha_mtt_sblins.length ? GameConfigData.omaha_mtt_sblins.length - 1 : CreateMTTFrg.mtt_start_sblinds_index;
                    smallBlind = (int) (GameConfigData.omaha_mtt_sblinds_multiple[0] * GameConfigData.omaha_mtt_sblins[startIndex]);
                } else if (mtt_sblinds_mode == 2) {//快速表
                    startIndex = CreateMTTFrg.mtt_start_sblinds_index >= GameConfigData.omaha_mtt_sblins_quick.length ? GameConfigData.omaha_mtt_sblins_quick.length - 1 : CreateMTTFrg.mtt_start_sblinds_index;
                    smallBlind = (int) (GameConfigData.omaha_mtt_sblinds_multiple_quick[0] * GameConfigData.omaha_mtt_sblins_quick[startIndex]);
                }
            }
            gameMttConfig.start_sblinds = smallBlind;
            gameMttConfig.ko_mode = mKoMode;
            gameMttConfig.ko_reward_rate = seekbar_normal_hunter_ratio.getDataItem(seekbar_normal_hunter_ratio.currentPosition);
            if (mKoMode == 2) {//最好做下判断，只有超级猎人赛才传这个比例
                gameMttConfig.ko_head_rate = seekbar_super_hunter_ratio.getDataItem(seekbar_super_hunter_ratio.currentPosition);
            }
            //下面的值不传给服务端，只是保存带本地------------------
            gameMttConfig.matchCheckinFee_index = mFeeSeekBar.currentPosition;
            gameMttConfig.matchChips_index = gameChipsConfigType;
            gameMttConfig.matchDuration_index = mTimeSeekBar.getCurrentTimeProgress();
            gameMttConfig.start_sblinds_index = startIndex;//起始盲注默认10
            gameMttConfig.matchPlayer_index = mDeskNumSeekBarNew.currentPosition;
            gameMttConfig.match_max_buy_cnt_index = checkin_player_limit_seekbar.currentPosition;//mtt总买入上限
            gameMttConfig.matchLevel_index = gameTerminationBlinks;//延时报名（终止报名）盲注级别
            gameMttConfig.match_rebuy_cnt_index = mtt_rebuy_seekbar.currentPosition;//mtt重构次数 代替  rebuyMode字段，之前字段只能取0和1，新版本改版取0-5
            gameMttConfig.ko_reward_rate_index = seekbar_normal_hunter_ratio.currentPosition;//  奖金分成
            gameMttConfig.ko_head_rate_index = seekbar_super_hunter_ratio.currentPosition;
            gameMttConfig.is_control = ck_game_control_into.isChecked() ? 1 : 0;
            ((GameCreateActivity) getActivity()).createGameMatch(gameMttConfig, GameConstants.GAME_MODE_MTT, ck_game_control_into.isChecked(), mGameName, consumeEntity, hasSufficientDiamond);
            saveCreateGameConfig(gameMttConfig);
        }
    }

    private void tryCreateGameByGroup() {
        String gameName = edt_game_name.getText().toString();
        if (StringUtil.isSpace(gameName)) {
            Toast.makeText(ChessApp.sAppContext, R.string.game_create_name_notnull, Toast.LENGTH_SHORT).show();
            return;
        }
        mGameName = new GameName(gameName);
        if (!isGameBeginTimeVaild(choiceTime)) {
            return;
        }
        checkAutoStartDialog();//设置手动开赛需要用一个对话框提示
    }

    EasyAlertDialog choiceFailureDialog;

    public void showJoinGameDialog(boolean isNull) {
        String message = getString(R.string.game_create_mtt_begintime_null);
        String btnStr = getString(R.string.setting);
        if (!isNull) {
            message = getString(R.string.game_create_mtt_begintime_null);
            btnStr = getString(R.string.resetting);
        }
        choiceFailureDialog = EasyAlertDialogHelper.createOneButtonDiolag(getActivity(), "", message, btnStr, false, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceFailureDialog.dismiss();
                showDateSelectPop();
            }
        });
        if (!getActivity().isFinishing()) {
            choiceFailureDialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //可能购买了金币，回来要恢复
        updataGameConfigUI();
        //下面是从"mtt盲注结构表"回来后的操作
        mtt_select_blind_tv_normal.setSelected(mtt_sblinds_mode == 1 ? true : false);
        mtt_select_blind_tv_quick.setSelected(mtt_sblinds_mode == 2 ? true : false);
        mtt_start_blind_seekbar.onlyUpdateData(mtt_sblinds_mode == 1 ? GameConfigData.mtt_sblins : GameConfigData.mtt_sblins_quick);
        mtt_start_blind_seekbar.setProgress(mtt_start_sblinds_index);
        setStartBlind();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof GameCreateActivity) {
            ((GameCreateActivity) getActivity()).playModeListeners.add(this);
        }
    }

    public boolean isGameBeginTimeVaild(long time) {
        if (time == -1) {
//            showJoinGameDialog(true);
            return true;
        }
        long currentTime = DemoCache.getCurrentServerSecondTime();
        if (time < (currentTime + 60 * 0)) {
            Toast.makeText(getContext(), R.string.game_create_mtt_begintime_early, Toast.LENGTH_SHORT).show();
            return false;
        }
//        else if (time > (currentTime + 7 * 60 * 24)) {
//            Toast.makeText(getContext(), R.string.game_create_match_start_date_is_late, Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }

    MatchCreateRulesPopView matchCreateRulesPopView;
    public void showMatchRulsPop(View view, int contentId) {
        if (matchCreateRulesPopView == null) {
            matchCreateRulesPopView = new MatchCreateRulesPopView(getContext(), MatchCreateRulesPopView.TYPE_RIGHT);
        }
        matchCreateRulesPopView.show(view, contentId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_game_checkin_fee_rule:
//                showMatchRulsPop(v, R.string.game_match_checkin_fee_rule_desc);//新需求砍掉了这个功能
                break;
            case R.id.mtt_blinds_click_tv:
//                showBlindStructureDialog();以前弹dialog显示盲注结构表，现在进入新页面
                Bundle bundle = new Bundle();
                bundle.putInt(GameConstants.KEY_PLAY_MODE, mPlayMode);
                Nav.from(getActivity()).withExtras(bundle).toUri(UrlConstants.MTT_BLIND_STRUCTURE);
                break;
            case R.id.rl_match_start_date_select:
                showDateSelectPop();
                break;
            case R.id.rebuy_instructions:
                showInstructions(v, R.string.game_create_mtt_rebuy_instructions);
                break;
            case R.id.rest_instructions:
                showInstructions(v, R.string.game_create_mtt_rest_instructions);
                break;
            case R.id.game_create_blind_switch:
                showBlindSetting(!isBlindSettingShow);
                break;
            case R.id.checkin_player_limit_click://参赛人数上限tips对话框
                showInstructions(v, R.string.game_create_mtt_checkin_limit_instructions);
                break;
            case R.id.mtt_select_blind_tv_normal:
                mtt_select_blind_tv_normal.setSelected(true);
                mtt_select_blind_tv_quick.setSelected(false);
                mtt_sblinds_mode = 1;
                onTableChanged();
                break;
            case R.id.mtt_select_blind_tv_quick:
                mtt_select_blind_tv_normal.setSelected(false);
                mtt_select_blind_tv_quick.setSelected(true);
                mtt_sblinds_mode = 2;
                onTableChanged();
                break;
            case R.id.iv_hunter_normal:
            case R.id.tv_hunter_normal_des:
                changeHunterMode(1);
                showSuperHunter(false);
                break;
            case R.id.iv_hunter_super:
            case R.id.tv_hunter_super_des:
                changeHunterMode(2);
                showSuperHunter(true);
                break;
            case R.id.iv_explain_checkin_fee:
                int explainStrId = diamond_match == GameConstants.MATCH_TYPE_NORMAL ? R.string.data_null : (diamond_match == GameConstants.MATCH_TYPE_DIAMOND ? R.string.checkin_fee_diamond : R.string.checkin_fee_gold);
                showPlayLimitDialog(v, ChessApp.sAppContext.getResources().getString(explainStrId));
                break;
        }
    }

    private void showPlayLimitDialog(View view, String content) {
        if (instructionsPopView == null) {
            instructionsPopView = new MatchCreateRulesPopView(getActivity(), MatchCreateRulesPopView.TYPE_LEFT);
            instructionsPopView.setBackground(R.drawable.bg_mtt_instructions);
        }
        instructionsPopView.tv_match_rule_content.setText(content);
        instructionsPopView.tv_match_rule_content.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.text_select_color));
        if (!instructionsPopView.isShowing()) {
            instructionsPopView.showAsDropDown(view, ScreenUtil.dip2px(getActivity(), -42f), ScreenUtil.dip2px(getActivity(), -4f));
        }
    }

    private void onTableChanged() {
        //先更新拖动条的数据源
        int[] sblinds_array = mPlayMode == GameConstants.PLAY_MODE_TEXAS_HOLDEM ?
                (mtt_sblinds_mode == 1 ? GameConfigData.mtt_sblins : GameConfigData.mtt_sblins_quick) :
                (mtt_sblinds_mode == 1 ? GameConfigData.omaha_mtt_sblins : GameConfigData.omaha_mtt_sblins_quick);
        mtt_start_blind_seekbar.onlyUpdateData(sblinds_array);
        mtt_start_sblinds_index = mtt_start_blind_seekbar.currentPosition;
//        notifyDataSetChanged();
        setStartBlind();
    }

    MatchCreateRulesPopView instructionsPopView;
    private void showInstructions(View view, int contentId) {
        if (instructionsPopView == null) {
            instructionsPopView = new MatchCreateRulesPopView(getContext(), MatchCreateRulesPopView.TYPE_LEFT);
            instructionsPopView.setBackground(R.drawable.bg_mtt_instructions);
        }
        instructionsPopView.showMTTInstructions(view, contentId, R.color.text_select_color);
    }

    //单桌人数拖动条和总买入次数上限拖动条  ---联动，保证  总买入次数上限 > 单桌人数
    //参数view表示 是哪个拖动条拖动的
    private void desknumLinkMaxBuy(View view) {
        if (view == checkin_player_limit_seekbar) {
            int destinationNum = checkin_player_limit_seekbar.getDataItem(checkin_player_limit_seekbar.currentPosition);
            for (int i = mDeskNumSeekBarNew.currentPosition - 1; i >= 0; i--) {
                if (mDeskNumSeekBarNew.getDataItem(i) < destinationNum) {
                    mDeskNumSeekBarNew.setProgress(i);
                    break;
                }
            }
        } else if (view == mDeskNumSeekBarNew) {
            int destinationNum = mDeskNumSeekBarNew.getDataItem(mDeskNumSeekBarNew.currentPosition);
            for (int i = checkin_player_limit_seekbar.currentPosition + 1; i < checkin_player_limit_seekbar.timeSize; i++) {
                if (checkin_player_limit_seekbar.getDataItem(i) > destinationNum) {
                    checkin_player_limit_seekbar.setProgress(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        mtt_sblinds_mode = 1;//mtt盲注结构表类型 1普通  2快速
        mtt_start_sblinds_index = 0;//mtt起始盲注seekbar拖动条index
        super.onDestroy();
    }

    EditText edt_game_name;
    GameName mGameName;
    private void initGamename() {
        edt_game_name = (EditText) view.findViewById(R.id.edt_game_name);
        edt_game_name.setFilters(new InputFilter[]{new NoSpaceAndEnterInputFilter(), new NameLengthFilter(GameConstants.MAX_GAMENAME_LENGTH), new NameRuleFilter()});
        String ownMttGamePrefix = GamePreferences.getInstance(ChessApp.sAppContext).getMttGamePrefix();
        int ownMttGameCount = GamePreferences.getInstance(ChessApp.sAppContext).getMttGameCount();
        mGameName = new GameName(ownMttGamePrefix + ownMttGameCount);
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
        String historyListStr = CreateGameConfigPref.getInstance(getActivity()).getMttConfig();
        LogUtil.i("getCreateGameConfig: " + historyListStr);
        Type type = new TypeToken<GameMttConfig>() {}.getType();
        if (!StringUtil.isSpace(historyListStr)) {
            mGameConfig = GsonUtils.getGson().fromJson(historyListStr, type);
        }
    }

    private void saveCreateGameConfig(GameMttConfig gameNormalConfig) {//保存创建牌局的设置
        String historyMgrListStr = GsonUtils.getGson().toJson(gameNormalConfig);
        LogUtil.i("saveCreateGameConfig: " + historyMgrListStr);
        CreateGameConfigPref.getInstance(getActivity()).setMttConfig(historyMgrListStr);//把管理员历史记录写入到sharedpreference
    }

    public int mPlayMode;//游戏模式，0="德州扑克"或者1="奥马哈"
    private void setGameConfig() {
        if (mGameConfig == null) {
            return;
        }
        mtt_sblinds_mode = mGameConfig.sblinds_mode;
        mKoMode = mGameConfig.ko_mode;
        mFeeSeekBar.setProgress(mGameConfig.matchCheckinFee_index >= mFeeSeekBar.datas.length ? mFeeSeekBar.datas.length - 1 : mGameConfig.matchCheckinFee_index);//防止配置更改数组越界
        changeHunterRewardRate(GameConstants.mttCheckInFeeNum[mFeeSeekBar.currentPosition] >= 100);
        mChipsSeekBar.setProgress(mGameConfig.matchChips_index >= mttChipsData.length ? mttChipsData.length - 1 : mGameConfig.matchChips_index);
        mTimeSeekBar.setProgress(mGameConfig.matchDuration_index >= GameConstants.sngTimeMinutes.length ? GameConstants.sngTimeMinutes.length - 1 : mGameConfig.matchDuration_index);

        int startIndex = mGameConfig.start_sblinds_index;
        if (mtt_sblinds_mode == 1) {//普通表
            startIndex = startIndex >= GameConfigData.mtt_sblins.length ? GameConfigData.mtt_sblins.length - 1 : startIndex;
        } else if (mtt_sblinds_mode == 2) {//快速表
            startIndex = startIndex >= GameConfigData.mtt_sblins_quick.length ? GameConfigData.mtt_sblins_quick.length - 1 : startIndex;
        }
        mtt_start_blind_seekbar.setProgress(startIndex);
        mtt_select_blind_tv_normal.setSelected(mGameConfig.sblinds_mode == 1);
        mtt_select_blind_tv_quick.setSelected(mGameConfig.sblinds_mode == 2);
        mDeskNumSeekBarNew.setProgress(mGameConfig.matchPlayer_index >= mDeskNumData.length ? mDeskNumData.length - 1 : mGameConfig.matchPlayer_index);
        checkin_player_limit_seekbar.setProgress(mGameConfig.match_max_buy_cnt_index >= GameConfigData.mtt_checkin_limit.length ? GameConfigData.mtt_checkin_limit.length - 1 : mGameConfig.match_max_buy_cnt_index);
        mBlindsLevelSeekBar.setProgress(mGameConfig.matchLevel_index >= GameConstants.mttBlindLevelInts.length ? GameConstants.mttBlindLevelInts.length - 1 : mGameConfig.matchLevel_index);
        mtt_rebuy_seekbar.setProgress(mGameConfig.match_rebuy_cnt_index >= GameConfigData.mtt_rebuy_count.length ? GameConfigData.mtt_rebuy_count.length - 1 : mGameConfig.match_rebuy_cnt_index);
        ck_game_control_into.setChecked(mGameConfig.is_control == 1);
        ck_game_match_rest.setChecked(mGameConfig.restMode != 0);
        iv_hunter_normal.setChecked((mKoMode == 1) ? true : false);
        iv_hunter_super.setChecked((mKoMode == 2) ? true : false);
        hunter_switch.setChecked(mGameConfig.ko_mode != 0);
        ll_hunter_config_content.setVisibility(hunter_switch.isChecked() ? View.VISIBLE : View.GONE);
        seekbar_normal_hunter_ratio.setProgress(mGameConfig.ko_reward_rate_index >= ko_reward_rate.length ? ko_reward_rate.length - 1 : mGameConfig.ko_reward_rate_index);
        seekbar_super_hunter_ratio.setProgress(mGameConfig.ko_head_rate_index >= GameConfigData.ko_head_rate.length ? GameConfigData.ko_head_rate.length - 1 : mGameConfig.ko_head_rate_index);
        changeHunterMode(mKoMode);
        showSuperHunter(mKoMode == 2);
        diamond_match = mGameConfig.match_type;
        setMatchTypeUI();
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
