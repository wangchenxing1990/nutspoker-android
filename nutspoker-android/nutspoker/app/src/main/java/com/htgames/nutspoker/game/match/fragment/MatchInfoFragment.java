package com.htgames.nutspoker.game.match.fragment;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.game.match.activity.MatchRoomActivity;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.game.mtt.view.AnteTableDialog;
import com.htgames.nutspoker.game.mtt.view.MttBlindStructureDialog;
import com.htgames.nutspoker.game.mtt.view.MttRemarkDialog;
import com.htgames.nutspoker.game.mtt.view.MttRewardDialog;
import com.htgames.nutspoker.game.util.SpannableUtils;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.base.BaseFragment;
import com.htgames.nutspoker.view.MatchCreateRulesPopView;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.team.model.Team;

import org.json.JSONObject;

import static com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsReady;

/**
 * 比赛状态页面
 */
public class MatchInfoFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = MatchInfoFragment.class.getSimpleName();
    View view;
    View match_room_top;//顶部的view，大菠萝mtt的时候颜色显示不一样
    View rl_game_share;
    TextView tv_game_table_wait;
//    TextView tv_game_share_code_click;
    TextView tv_game_share_code;
//    ImageView iv_club_match_logo;
    TextView tv_mtt_checkin_count;
    TextView tv_mtt_all_reward;
    ImageView iv_game_mode;
    TextView tv_match_prompt;
    //
    TextView tv_match_start_style;
    TextView tv_match_start_time;
    TextView tv_mtt_creator;
    TextView tv_mtt_max_cnt;
    TextView tv_mtt_instructions;
    TextView tv_mtt_reward;
    TextView tv_mtt_checkin_fee;
    TextView tv_mtt_chips_num;
    TextView tv_mtt_blinds_duration;
    TextView tv_mtt_match_player;
    TextView tv_mtt_match_blind_level;
    TextView tv_mtt_rebuy;
    TextView tv_mtt_addon;
    TextView tv_mtt_termination_join;
    TextView tv_mtt_midfield_rest;
    //
    LinearLayout ll_mtt_checkin_fee;
    LinearLayout ll_mtt_info_status_wait;
    LinearLayout ll_mtt_info_status_ing;
    TextView tv_mtt_checkin_fee_des;
    TextView tv_match_current_level;
    TextView tv_match_next_level;
    TextView tv_match_current_duration;
    TextView tv_match_current_chips;
    ImageView iv_omaha_icon;
    GameEntity gameInfo;
    MatchStatusEntity matchStatusEntity;
    int gameStatus = GameStatus.GAME_STATUS_WAIT;
    MttBlindStructureDialog mMttBlindStructureDialog;
    int blindsLevel = -1;
    boolean isRebuyMode = false;//是否是重购模式
    boolean isAddonMode = false;//是否是增购模式
    boolean isMidRest = false;//是否中场休息
    private ChatRoomMember master;
    ////////////////////////////////////////////猎人赛相关begin////////////////////////////////////////////
    View match_hunter_info_container;
    TextView hunter_reward_rate;
    TextView hunter_head_rate;
    TextView match_info_tv_hunter_marker;
    ////////////////////////////////////////////猎人赛相关end////////////////////////////////////////////
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        if (isFragmentCreated) {
//            ((MatchRoomActivity) getActivity()).getGameMatchStatus();
        }
    }

    public static MatchInfoFragment newInstance(GameEntity gameEntity) {
        MatchInfoFragment mInstance = new MatchInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Extras.EXTRA_DATA, gameEntity);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    public void setGameInfo(GameEntity gameEntity) {
        gameInfo = gameEntity;
        setViewInfo();
        remarkContent(false, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFragmentName("MatchInfoFragment");
        if (getArguments().getSerializable(Extras.EXTRA_DATA) instanceof GameEntity) {
            gameInfo = (GameEntity) getArguments().getSerializable(Extras.EXTRA_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_match_info, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        getChatRoomMaster();
        isFragmentCreated = true;
        setViewInfo();
    }

    private void setViewInfo() {
        if (!isFragmentCreated || gameInfo == null || view == null) {
            return;
        }
        if (getActivity() instanceof MatchRoomActivity && ((MatchRoomActivity) getActivity()).matchStatus != null) {
            this.matchStatusEntity = ((MatchRoomActivity) getActivity()).matchStatus;
            this.gameStatus = this.matchStatusEntity.gameStatus;
        }
        initView();
        updateInfoUI(gameInfo.status, gameInfo.joinCode);
        if (gameInfo.gameConfig instanceof PineappleConfigMtt) {
            changePineappleBg();
        }
    }

    private void initView() {
        match_room_top = view.findViewById(R.id.match_room_top);
        tv_mtt_checkin_fee_des = (TextView) view.findViewById(R.id.tv_mtt_checkin_fee_des);
        tv_mtt_checkin_fee_des.setOnClickListener(this);
        ll_mtt_info_status_wait = (LinearLayout) view.findViewById(R.id.ll_mtt_info_status_wait);
        ll_mtt_info_status_ing = (LinearLayout) view.findViewById(R.id.ll_mtt_info_status_ing);
        ll_mtt_checkin_fee = (LinearLayout) view.findViewById(R.id.ll_mtt_checkin_fee);
        rl_game_share = view.findViewById(R.id.rl_game_share);
        iv_game_mode = (ImageView) view.findViewById(R.id.iv_game_mode);
        tv_mtt_max_cnt = (TextView) view.findViewById(R.id.tv_mtt_max_cnt);
        tv_match_start_style = (TextView) view.findViewById(R.id.tv_match_start_style);
        tv_match_start_time = (TextView) view.findViewById(R.id.tv_match_start_time);
        //
//        iv_club_match_logo = (ImageView) view.findViewById(R.id.iv_club_match_logo);
        tv_game_share_code = (TextView) view.findViewById(R.id.tv_game_share_code);
        tv_game_table_wait = (TextView) view.findViewById(R.id.tv_game_table_wait);
//        tv_game_share_code_click = (TextView) view.findViewById(R.id.tv_game_share_code_click);
        tv_mtt_checkin_count = (TextView) view.findViewById(R.id.tv_mtt_checkin_count);
        tv_mtt_all_reward = (TextView) view.findViewById(R.id.tv_mtt_all_reward);
        tv_mtt_creator = (TextView) view.findViewById(R.id.tv_mtt_creator);
        tv_mtt_instructions = (TextView) view.findViewById(R.id.tv_mtt_instructions);
        tv_mtt_instructions.setOnClickListener(this);
        tv_mtt_instructions.setText(gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig ? R.string.game_create_sng_blinds_relation : R.string.pineapple_mtt_ante_table);
        tv_mtt_reward = (TextView) view.findViewById(R.id.tv_mtt_reward);
        tv_mtt_reward.setOnClickListener(this);
        tv_mtt_checkin_fee = (TextView) view.findViewById(R.id.tv_mtt_checkin_fee);
        tv_mtt_chips_num = (TextView) view.findViewById(R.id.tv_mtt_chips_num);
        tv_mtt_blinds_duration = (TextView) view.findViewById(R.id.tv_mtt_blinds_duration);
        tv_mtt_match_player = (TextView) view.findViewById(R.id.tv_mtt_match_player);
        tv_mtt_match_blind_level = (TextView) view.findViewById(R.id.tv_mtt_match_blind_level);
        tv_mtt_rebuy = (TextView) view.findViewById(R.id.tv_mtt_rebuy);
        tv_mtt_addon = (TextView) view.findViewById(R.id.tv_mtt_addon);
        tv_mtt_termination_join = (TextView) view.findViewById(R.id.tv_mtt_termination_join);
        tv_mtt_midfield_rest = (TextView) view.findViewById(R.id.tv_mtt_midfield_rest);
        tv_match_prompt = (TextView) view.findViewById(R.id.tv_match_prompt);
        //
        tv_match_current_level = (TextView) view.findViewById(R.id.tv_match_current_level);
        tv_match_next_level = (TextView) view.findViewById(R.id.tv_match_next_level);
        tv_match_current_duration = (TextView) view.findViewById(R.id.tv_match_current_duration);
        tv_match_current_chips = (TextView) view.findViewById(R.id.tv_match_current_chips);
        ll_mtt_checkin_fee.setOnClickListener(this);
        if (this.gameStatus == GameStatus.GAME_STATUS_WAIT) {
            tv_mtt_checkin_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_checkin_count, matchStatusEntity == null ? 0 : matchStatusEntity.checkInPlayer));
        } else if (this.gameStatus == GameStatus.GAME_STATUS_START) {
            tv_mtt_checkin_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_player_join_count, matchStatusEntity == null ? 0 : matchStatusEntity.leftPlayer, matchStatusEntity == null ? 0 : matchStatusEntity.checkInPlayer));
        }
        tv_mtt_all_reward.setText(getString(R.string.game_mtt_all_reward, "" + 0));
        hunter_reward_rate = (TextView) view.findViewById(R.id.hunter_reward_rate);
        hunter_head_rate = (TextView) view.findViewById(R.id.hunter_head_rate);
        match_info_tv_hunter_marker = (TextView) view.findViewById(R.id.match_info_tv_hunter_marker);
        iv_omaha_icon = (ImageView) view.findViewById(R.id.iv_omaha_icon);
        setOmahaIcon();//设置奥马哈标记
        setPineapplePlayType();//设置mtt大菠萝play_type
    }

    private void setOmahaIcon() {//设置奥马哈标记
        if (gameInfo != null && view != null) {
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
                iv_omaha_icon.setImageResource(R.mipmap.tag_omaha_2);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_omaha_icon.setVisibility(View.VISIBLE);
                        AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(ChessApp.sAppContext, 62), 0, 400);//图片的宽度是40dp
                    }
                }, 300);
            } else {
                iv_omaha_icon.setVisibility(View.GONE);
            }
        }
    }

    private void setPineapplePlayType() {//设置mtt大菠萝play_type
        if (gameInfo != null && view != null) {
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfigMtt) {
                iv_omaha_icon.setImageResource(pineappleIconIdsReady[((PineappleConfigMtt) gameInfo.gameConfig).getPlay_type()]);
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_omaha_icon.setVisibility(View.VISIBLE);
                        AnimUtil.translateX(iv_omaha_icon, -ScreenUtil.dp2px(ChessApp.sAppContext, 62), 0, 400);//图片的宽度是40dp
                    }
                }, 300);
            } else {
                iv_omaha_icon.setVisibility(View.GONE);
            }
        }
    }

    public void updateCheckinPlayer(MatchStatusEntity statusEntity) {
        if (statusEntity != null) {
            this.matchStatusEntity = statusEntity;
        }
        if (view == null || this.matchStatusEntity == null) {
            return;
        }
        if (this.gameStatus == GameStatus.GAME_STATUS_WAIT) {
            tv_mtt_checkin_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_checkin_count, matchStatusEntity.checkInPlayer));
        } else if (this.gameStatus == GameStatus.GAME_STATUS_START) {
            tv_mtt_checkin_count.setText(ChessApp.sAppContext.getResources().getString(R.string.game_mtt_player_join_count, matchStatusEntity.leftPlayer, matchStatusEntity.checkInPlayer));
        }
    }

    public void updateInfoUI(int gameStatus, String joinCode) {
        if (joinCode == null) {
            joinCode = "";
        }
        this.gameStatus = gameStatus;
        if (gameInfo == null || view == null) {
            return;
        }
        Resources res = ChessApp.sAppContext.getResources();
        boolean isHunder = (gameInfo.gameConfig instanceof GameMttConfig && ((GameMttConfig) gameInfo.gameConfig).ko_mode != 0)
                || (gameInfo.gameConfig instanceof PineappleConfigMtt && ((PineappleConfigMtt) gameInfo.gameConfig).ko_mode != 0);
        match_info_tv_hunter_marker.setVisibility(isHunder ? View.VISIBLE : View.GONE);
        match_info_tv_hunter_marker.setOnClickListener(this);
        view.findViewById(R.id.match_hunter_info_container).setVisibility(isHunder ? View.VISIBLE : View.GONE);
        tv_mtt_creator.setText(res.getString(R.string.game_mtt_creator, gameInfo.creatorInfo.name));
        if (joinCode.length() == 6) {//如果是俱乐部比赛但是code为6位那么还是显示code而不是显示俱乐部信息
            rl_game_share.setOnClickListener(this);
            tv_game_share_code.setText(joinCode/**/);
            tv_game_table_wait.setText("邀请码");
            rl_game_share.findViewById(R.id.tv_game_share_code_click).setVisibility(View.VISIBLE);
//            iv_club_match_logo.setVisibility(View.GONE);
        } else if (joinCode.length() > 6) {//如果是俱乐部比赛但是code为6位那么还是显示code而不是显示俱乐部信息
            tv_game_table_wait.setText("来自俱乐部");
            tv_game_share_code.setTextSize(15);
            String teamId = joinCode.substring(6, joinCode.length());//gameInfo.getTid();
            if (StringUtil.isSpace(teamId)) {
                return;
            }
            final Team team = TeamDataCache.getInstance().getTeamById(teamId);
            String clubName = "";
            if (team != null) {
                clubName = TeamDataCache.getInstance().getTeamById(teamId).getName();
                tv_game_share_code.setText(clubName);
            } else {
                TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success && result != null) {
                            tv_game_share_code.setText(result.getName());
                        } else {
                        }
                    }
                });
            }
//            tv_game_share_code_click.setVisibility(View.GONE);
//            iv_club_match_logo.setVisibility(View.VISIBLE);
        }
        boolean isPineapple = gameInfo.gameConfig instanceof PineappleConfigMtt;
        int chips = 0;
        int checinFee = 0;
        int serviceFee = 0;
        int match_max_buy_cnt = gameInfo.gameConfig instanceof BaseMttConfig ? ((BaseMttConfig) gameInfo.gameConfig).match_max_buy_cnt : 0;
        tv_mtt_max_cnt.setText("总买入次数上限：" + match_max_buy_cnt);
        tv_match_prompt.setText(R.string.game_mtt_prompt);
        tv_match_start_style.setText(R.string.game_match_timestart);
        iv_game_mode.setImageResource(R.mipmap.icon_mtt_blue);
        if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof BaseMttConfig) {
            BaseMttConfig gameMttConfig = (BaseMttConfig) gameInfo.gameConfig;
            tv_match_start_time.setText(DateTools.getMatchBeginDate(gameMttConfig.beginTime));
            chips = gameMttConfig.matchChips;
            checinFee = gameMttConfig.matchCheckinFee;
            serviceFee = (int) (checinFee / GameConfigData.MTTServiceRate);
            isRebuyMode = (gameMttConfig.rebuyMode == GameConstants.GAME_MT_REBUY_MODE ? true : false);
            isAddonMode = (gameMttConfig.addonMode == GameConstants.GAME_MT_ADDON_MODE ? true : false);
            isMidRest = (gameMttConfig.restMode == GameConstants.GAME_MT_REST_MODE ? true : false);
            blindsLevel = gameMttConfig.matchLevel;
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                ll_mtt_info_status_wait.setVisibility(View.VISIBLE);
                ll_mtt_info_status_ing.setVisibility(View.GONE);
                int sblinds = gameMttConfig.matchChips;
                int duration = gameMttConfig.matchDuration;
                tv_mtt_rebuy.setVisibility(View.VISIBLE);
//                tv_mtt_addon.setVisibility(View.VISIBLE);//
                tv_mtt_midfield_rest.setVisibility(View.VISIBLE);
//                tv_mtt_termination_join.setVisibility(View.VISIBLE);"终止参赛"和"延时报名"是同样的意思，重复
                tv_mtt_blinds_duration.setText(SpannableUtils.getSpannableString(res.getString(isPineapple ? R.string.game_mtt_blinds_duration_pineapple : R.string.game_mtt_blinds_duration), gameMttConfig.matchDuration / 60 + res.getString(R.string.minutes)));
                tv_mtt_match_blind_level.setText("终止报名：" + "第" + blindsLevel + "盲注级别");
                tv_mtt_rebuy.setText("重购次数：" + gameMttConfig.match_rebuy_cnt + "次");
                if (isAddonMode) {
                    tv_mtt_addon.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_addon), res.getString(R.string.game_match_addon_desc, blindsLevel)));
                } else {
                    tv_mtt_addon.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_addon), res.getString(R.string.nothing)));
                }
                if (isMidRest) {
                    tv_mtt_midfield_rest.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_midfield_rest), res.getString(R.string.game_match_midfield_rest_desc)));
                } else {
                    tv_mtt_midfield_rest.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_midfield_rest), res.getString(R.string.nothing)));
                }
                tv_mtt_termination_join.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_termination_join), res.getString(R.string.game_match_termination_join_desc, blindsLevel)));
            } else if (gameStatus == GameStatus.GAME_STATUS_START) {
                ll_mtt_info_status_wait.setVisibility(View.GONE);
                ll_mtt_info_status_ing.setVisibility(View.VISIBLE);
                updateMatchIngUI(this.matchStatusEntity);
                tv_match_current_duration.setText(SpannableUtils.getSpannableString(res.getString(isPineapple ? R.string.game_mtt_blinds_duration_pineapple : R.string.game_mtt_blinds_duration), gameMttConfig.matchDuration / 60 + res.getString(R.string.minutes)));
            }
            if (gameMttConfig.ko_mode != 0) {//猎人赛
                int fee_allot_hunter = (int) (checinFee * gameMttConfig.ko_reward_rate / 100.0f);
                int fee_allot_other = checinFee - fee_allot_hunter;
                hunter_reward_rate.setText("报名费：奖池" + fee_allot_other + "+赏金" + fee_allot_hunter + "+记录费" + serviceFee);
                hunter_reward_rate.setVisibility(gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? View.VISIBLE : View.GONE);
                hunter_head_rate.setVisibility(gameMttConfig.ko_mode != 0 ? View.VISIBLE : View.GONE);
                int rise = (int) (fee_allot_hunter * gameMttConfig.ko_head_rate / 100.0f);
                String normalHunterStr = gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? "全员赏金：每淘汰一名玩家获得赏金" + fee_allot_hunter : "全员赏金：每淘汰一名玩家即可获得一定的猎头奖";
                String superHunterStr = gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? "累增赏金：每淘汰一名玩家身价至少上涨" + (rise) : "累增赏金：每淘汰一名玩家身价上涨";
                if (gameMttConfig.ko_mode == 1) {
                    hunter_head_rate.setText(normalHunterStr);
                } else if (gameMttConfig.ko_mode == 2) {
                    hunter_head_rate.setText(superHunterStr);
                }
                if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) {//钻石赛的显示不一样
                    hunter_reward_rate.setText(gameMttConfig.ko_mode == 1 ? "累增赏金：每淘汰一名玩家身价上涨" : "全员赏金：每淘汰一名玩家即可获得一定的猎头奖");
                }
            }
            if (gameInfo.gameConfig instanceof GameMttConfig) {
                GameMttConfig mttConfig = (GameMttConfig) gameInfo.gameConfig;
                int matchPlayer = mttConfig.matchPlayer;
                tv_mtt_match_player.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_mtt_match_player), matchPlayer + res.getString(R.string.person)));
            } else if (gameInfo.gameConfig instanceof PineappleConfigMtt) {
                tv_mtt_match_player.setVisibility(View.GONE);//大菠萝mtt不显示单桌人数
            }
        }
        tv_mtt_checkin_fee.setText(checinFee + (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? ("+" + serviceFee) : ""));
        //金币的bg
        Drawable icon_club_chat_chip = ChessApp.sAppContext.getResources().getDrawable(R.mipmap.icon_club_paiju_checkin_fee);
        icon_club_chat_chip.setBounds(0, 0, icon_club_chat_chip.getIntrinsicWidth(), icon_club_chat_chip.getIntrinsicHeight());
        //钻石的bg
        Drawable bg_diamond = ChessApp.sAppContext.getResources().getDrawable(R.mipmap.icon_mtt_room_diamond);
        bg_diamond.setBounds(0, 0, bg_diamond.getIntrinsicWidth(), bg_diamond.getIntrinsicHeight());
        Drawable bg = gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? null :
                gameInfo.match_type == GameConstants.MATCH_TYPE_GOLD ? icon_club_chat_chip :
                gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND ? bg_diamond : null;
        tv_mtt_checkin_fee.setCompoundDrawables(bg, null, null, null);
        tv_mtt_chips_num.setText(chips + "");
    }

    //比赛进行中的状态更新
    public void updateMatchIngUI(MatchStatusEntity matchStatusEntity) {
        if (matchStatusEntity != null) {
            this.matchStatusEntity = matchStatusEntity;
        }
        if (matchStatusEntity == null || view == null) {
            return;
        }
        this.gameStatus = matchStatusEntity.gameStatus;
        Object object = gameInfo.gameConfig;
        if (object == null) {
            return;
        }
        int blindMode = object instanceof GameMttConfig ? ((GameMttConfig) object).sblinds_mode : 0;
        int start_sblinds = object instanceof GameMttConfig ? ((GameMttConfig) object).start_sblinds : 1;
        int currentLevel = 1;
        if (matchStatusEntity.currentLevel == 0) {
            currentLevel = 1;
        } else {
            if (blindMode == 1) {
                if (matchStatusEntity.currentLevel <= GameConfigData.mtt_sblinds_multiple.length) {
                    currentLevel = matchStatusEntity.currentLevel;//websocket从1开始传的
                } else {
                    currentLevel = GameConfigData.mtt_sblinds_multiple.length;
                }
            } else if (blindMode == 2) {
                if (matchStatusEntity.currentLevel <= GameConfigData.mtt_sblinds_multiple_quick.length) {
                    currentLevel = matchStatusEntity.currentLevel;//websocket从1开始传的
                } else {
                    currentLevel = GameConfigData.mtt_sblinds_multiple_quick.length;
                }
            } else {
                currentLevel = matchStatusEntity.currentLevel;
            }
        }
        int maxChips = matchStatusEntity.maxChips;
        double checkinFee = 50;//单次买入费用
        int matchChips = 100;
        if (object instanceof GameMttConfig) {
            checkinFee = ((GameMttConfig) object).matchCheckinFee;
            matchChips = ((GameMttConfig) object).matchChips;
        }
        Resources res = ChessApp.sAppContext.getResources();
        int aveChips = (int) Math.floor(matchStatusEntity.allReward / checkinFee * matchChips / (matchStatusEntity.leftPlayer == 0 ? 1 : matchStatusEntity.leftPlayer));// matchStatusEntity.avgChips值是错的  7.8取7    平均记分牌：总奖池 / 报名费 * 带入记分牌 / 剩余在玩人数
        int minChips = matchStatusEntity.minChips;
        int currentBlind = 0;
        int currentAnte = 0;
        int nextBlind = 0;
        int nextAnte = 0;
        if (gameInfo.gameConfig instanceof GameMttConfig) {
            if (blindMode == 1) {
                currentBlind = (int) (GameConfigData.mtt_sblinds_multiple[currentLevel - 1] * start_sblinds);
                currentAnte = (int) (GameConfigData.mtt_ante_multiple[currentLevel - 1] * start_sblinds);
                tv_match_next_level.setVisibility((currentLevel <= (GameConfigData.mtt_sblinds_multiple.length - 1)) ? View.VISIBLE : View.GONE);
                if (currentLevel <= (GameConfigData.mtt_sblinds_multiple.length - 1)) {
                    nextBlind = (int) (GameConfigData.mtt_sblinds_multiple[currentLevel] * start_sblinds);
                    nextAnte = (int) (GameConfigData.mtt_ante_multiple[currentLevel] * start_sblinds);
                    String nextBlindShow = res.getString(R.string.game_match_blinds_content, GameConstants.getGameBlindsShow(nextBlind), "" + nextAnte);
                    tv_match_next_level.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_match_next_blinds_title, currentLevel + 1), nextBlindShow));
                }
            } else if (blindMode == 2) {
                currentBlind = (int) (GameConfigData.mtt_sblinds_multiple_quick[currentLevel - 1] * start_sblinds);
                currentAnte = (int) (GameConfigData.mtt_ante_multiple_quick[currentLevel - 1] * start_sblinds);//默认是乘以mtt_sblins[0]，拖动拖动条时乘数会变
                tv_match_next_level.setVisibility((currentLevel <= (GameConfigData.mtt_sblinds_multiple_quick.length - 1)) ? View.VISIBLE : View.GONE);
                if (currentLevel <= (GameConfigData.mtt_sblinds_multiple_quick.length - 1)) {
                    nextBlind = (int) (GameConfigData.mtt_sblinds_multiple_quick[currentLevel] * start_sblinds);
                    nextAnte = (int) (GameConfigData.mtt_ante_multiple_quick[currentLevel] * start_sblinds);
                    String nextBlindShow = res.getString(R.string.game_match_blinds_content, GameConstants.getGameBlindsShow(nextBlind), "" + nextAnte);
                    tv_match_next_level.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_match_next_blinds_title, currentLevel + 1), nextBlindShow));
                }
            }
            //
            String currentBlindShow = res.getString(R.string.game_match_blinds_content, GameConstants.getGameBlindsShow(currentBlind), "" + currentAnte);
            tv_match_current_level.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_match_current_blinds_title, currentLevel), currentBlindShow));
        } else if (gameInfo.gameConfig instanceof PineappleConfigMtt) {
            currentAnte = currentLevel - 1 < GameConfigData.pineapple_mtt_ante.length ? GameConfigData.pineapple_mtt_ante[currentLevel - 1] : 0;
            nextAnte = currentLevel < GameConfigData.pineapple_mtt_ante.length ? GameConfigData.pineapple_mtt_ante[currentLevel] : 0;
            tv_match_current_level.setText("当前底注：" + currentAnte);
            tv_match_next_level.setVisibility((currentLevel <= (GameConfigData.pineapple_mtt_ante.length - 1)) ? View.VISIBLE : View.GONE);
            tv_match_next_level.setText("下一底注：" + nextAnte);
        }
        String currentChipsShow = res.getString(R.string.game_match_chips_content, maxChips, aveChips, minChips);
        tv_match_current_chips.setText(SpannableUtils.getSpannableString(res.getString(R.string.game_match_chips_title), currentChipsShow));
        if (this.gameStatus == GameStatus.GAME_STATUS_WAIT) {
            tv_mtt_checkin_count.setText(res.getString(R.string.game_mtt_checkin_count, matchStatusEntity.checkInPlayer));
        } else if (this.gameStatus == GameStatus.GAME_STATUS_START) {
            tv_mtt_checkin_count.setText(res.getString(R.string.game_mtt_player_join_count, matchStatusEntity.leftPlayer, matchStatusEntity.checkInPlayer));
        }
        tv_mtt_all_reward.setText(res.getString(R.string.game_mtt_all_reward, "" + matchStatusEntity.allReward));
    }

    private void showBlindStructureDialog() {//mtt盲注结构表
        if (gameInfo == null || !(gameInfo.gameConfig instanceof GameMttConfig)) {
            return;
        }
        Object object = gameInfo.gameConfig;
        if (mMttBlindStructureDialog == null) {
            mMttBlindStructureDialog = new MttBlindStructureDialog(getActivity(), gameInfo, true);
            mMttBlindStructureDialog.setData(((GameMttConfig) object).sblinds_mode, ((GameMttConfig) object).start_sblinds);
            mMttBlindStructureDialog.setMttBlindLevel(blindsLevel, isRebuyMode, isAddonMode);
        }
        if (!getActivity().isFinishing()) {
            mMttBlindStructureDialog.show();
        }
    }

    private AnteTableDialog mAnteTableDialog;
    private void showAnteTable() {
        if (mAnteTableDialog == null) {
            mAnteTableDialog = new AnteTableDialog(getActivity(), gameInfo);
            mAnteTableDialog.setData(((PineappleConfigMtt) gameInfo.gameConfig).getAnte_table_type());
            mAnteTableDialog.setMttBlindLevel(blindsLevel, isRebuyMode, isAddonMode);
        }
        if (!getActivity().isFinishing()) {
            mAnteTableDialog.show();
        }
    }

    public String mMatchStateStr = "";//比赛说明  “摸金校尉”
    public String mMatchPicList = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508310453531&di=1d6a5458127a381c930b281650903669&imgtype=0&src=http%3A%2F%2Fimg2.3lian.com%2F2014%2Ff4%2F97%2Fd%2F148.jpg," +
//            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508310453531&di=de43d43eca58a509b7a0f737f9ccf1e4&imgtype=0&src=http%3A%2F%2Fpic37.nipic.com%2F20140122%2F17755754_165857492000_2.jpg," +
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1508912142&di=dedd2746405d88266ad4c3cc3bcbaf60&imgtype=jpg&er=1&src=http%3A%2F%2Fpic8.nipic.com%2F20100809%2F4314454_093616306543_2.jpg," +
            "http://a0.att.hudong.com/30/13/20300542517439140015134902637_s.jpg";//比赛说明 d的图片列表，逗号分割
    private MttRewardDialog mMttRewardDialog;
    private void showRewardStructureDialog() {
        if (gameInfo == null || !(gameInfo.gameConfig instanceof BaseMttConfig)) {
            return;
        }
        if (mMttRewardDialog == null) {
            mMttRewardDialog = new MttRewardDialog(getActivity(), gameInfo);
        }
        mMttRewardDialog.updateData(matchStatusEntity, mMatchStateStr);
        mMttRewardDialog.show();
    }
    private MttRemarkDialog mMttRemarkDialog;
    private void showRemarkDialog() {
        if (gameInfo == null || !(gameInfo.gameConfig instanceof BaseMttConfig)) {
            return;
        }
        if (mMttRemarkDialog == null) {
            mMttRemarkDialog = new MttRemarkDialog(getActivity(), gameInfo, mMatchStateStr, mMatchPicList);
        }
        mMttRemarkDialog.updateDialogMatchState(matchStatusEntity, mMatchStateStr, mMatchPicList);
        mMttRemarkDialog.show();
    }

    public void updateDialogMatchState(String str, String picList) {
        mMatchStateStr = str;
        mMatchPicList = picList;
        if (mMttRemarkDialog != null) {
            mMttRemarkDialog.updateDialogMatchState(matchStatusEntity, mMatchStateStr, mMatchPicList);
        }
    }

//    private void getChatRoomMaster() {
//        ChatRoomInfo roomInfo = ((MatchRoomActivity) getActivity()).getRoomInfo();
//        master = ChatRoomMemberCache.getInstance().getChatRoomMember(roomInfo.getRoomId(), roomInfo.getCreator());
//        if (master != null) {
//            updateCreatorInfo(master);
//        } else {
//            ChatRoomMemberCache.getInstance().fetchMember(roomInfo.getRoomId(), roomInfo.getCreator(),
//                    new SimpleCallback<ChatRoomMember>() {
//                        @Override
//                        public void onResult(boolean success, ChatRoomMember result) {
//                            if (success) {
//                                master = result;
//                                updateCreatorInfo(master);
//                            }
//                        }
//                    });
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i("MatchInfoFragment onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i("MatchInfoFragment  onResume");
    }

    MatchCreateRulesPopView matchCreateRulesPopView;

    public void showMatchRulsPop(View view, int contentId) {
        if (matchCreateRulesPopView == null) {
            matchCreateRulesPopView = new MatchCreateRulesPopView(getContext() , MatchCreateRulesPopView.TYPE_RIGHT);
            matchCreateRulesPopView.setBackground(R.drawable.bg_room_tips);
        }
        int xOffsetDp = -5; int YOffsetDp = 2;
        matchCreateRulesPopView.showMTTInstructions(view, contentId, R.color.text_select_color, xOffsetDp, YOffsetDp);
    }

    private void showMatchHunterRulsPop(View view, int contentId) {
        if (matchCreateRulesPopView == null) {
            matchCreateRulesPopView = new MatchCreateRulesPopView(getContext() , MatchCreateRulesPopView.TYPE_RIGHT);
            matchCreateRulesPopView.setBackground(R.drawable.bg_room_tips);
        }
        matchCreateRulesPopView.showMatchHunterRulsPop(view, contentId, R.color.text_select_color);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_game_share:
                if(!ApiConfig.AppVersion.isTaiwanVersion) {
                    if (getActivity() instanceof MatchRoomActivity) {
                        ((MatchRoomActivity) getActivity()).postShare();
                    }
                }
                break;
            case R.id.tv_mtt_instructions:
                if (gameInfo != null) {
                    if ((gameInfo.gameConfig instanceof GameMttConfig)) {
                        showBlindStructureDialog();//mtt盲注结构表
                    } else if (gameInfo.gameConfig instanceof PineappleConfigMtt) {
                        showAnteTable();
                    }
                }
                break;
            case R.id.tv_mtt_reward:
                if (gameInfo != null) {
                    if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                        showRewardStructureDialog();//普通mtt比赛显示奖励表
                    } else {
                        showRemarkDialog();//钻石赛和金币赛mtt比赛显示公告
                    }
                }
                break;
            case R.id.tv_mtt_checkin_fee_des:
//                int strId = gameInfo != null && gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND ? R.string.game_match_room_checkin_fee_rule_desc_diamond : R.string.game_match_room_checkin_fee_rule_desc;
                int strId = gameInfo == null ? R.string.data_null :
                        gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? R.string.game_match_room_checkin_fee_rule_desc :
                        gameInfo.match_type == GameConstants.MATCH_TYPE_GOLD ? R.string.game_match_room_checkin_fee_rule_desc_gold :
                        gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND ? R.string.game_match_room_checkin_fee_rule_desc_diamond : R.string.data_null;
                showMatchRulsPop(tv_mtt_checkin_fee_des, strId);
                break;
            case R.id.match_info_tv_hunter_marker:
                showMatchHunterRulsPop(match_info_tv_hunter_marker, R.string.game_match_room_hunter_rule_desc);
                break;
        }
    }

    public void changePineappleBg() {
        if (match_room_top != null) {
            match_room_top.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt));
        }
    }

    public void remarkContent(final boolean post, String content) {//获取比赛说明信息
        if (gameInfo != null && gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {//普通mtt没有比赛说明这个东西
            return;
        }
        if (gameInfo != null && getActivity() instanceof MatchRoomActivity &&  ((MatchRoomActivity) getActivity()).mGameAction != null) {
            ((MatchRoomActivity) getActivity()).mGameAction.mttRemark(post, gameInfo.code, content, "", new GameRequestCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response != null && response.optJSONObject("data") != null) {
                        mMatchStateStr = response.optJSONObject("data").optString("content");
                        String picStr = response.optJSONObject("data").optString("pic_list");
                        if (!StringUtil.isSpace(picStr)) {
                            mMatchPicList = response.optJSONObject("data").optString("pic_list");
                        }
                        updateDialogMatchState(mMatchStateStr, mMatchPicList);
                    }
                }
                @Override
                public void onFailed(int code, JSONObject response) {
                    if (post) {
                        Toast.makeText(getActivity(), "修改比赛说明失败：" + code, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
