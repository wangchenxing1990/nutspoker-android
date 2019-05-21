package com.htgames.nutspoker.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.htgames.nutspoker.tool.DateCalendarTools;
import com.netease.nim.uikit.common.util.log.LogUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 *
 */
public class ShareCodeView extends LinearLayout {
    View view;
    //
    RelativeLayout rl_share_code;
    ImageView iv_code_game_mode_logo;
    //
    ImageView iv_code_first;
    ImageView iv_code_sencond;
    ImageView iv_code_third;
    ImageView iv_code_fourth;
    ImageView iv_code_fifth;
    ImageView iv_code_sixth;
    HeadImageView iv_game_creator_head;
    TextView tv_game_name;
    //
    TextView tv_game_checkin_fee;
    TextView tv_game_chips;
    TextView tv_game_blinds;
    TextView tv_game_match_player;
    TextView tv_game_time;
    TextView tv_game_ante;
    TextView tv_game_other_info;

    TextView tv_mtt_time;
    public int shareGameMode = GameConstants.GAME_MODE_NORMAL;

    public ShareCodeView(Context context) {
        super(context);
        init(context);
    }

    public ShareCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShareCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.view_share_code, null);
        addView(view);
    }

    private void initView() {
        tv_mtt_time = (TextView)view.findViewById(R.id.tv_mtt_time);

        iv_code_first = (ImageView) view.findViewById(R.id.iv_code_first);
        iv_code_sencond = (ImageView) view.findViewById(R.id.iv_code_sencond);
        iv_code_third = (ImageView) view.findViewById(R.id.iv_code_third);
        iv_code_fourth = (ImageView) view.findViewById(R.id.iv_code_fourth);
        iv_code_fifth = (ImageView) view.findViewById(R.id.iv_code_fifth);
        iv_code_sixth = (ImageView) view.findViewById(R.id.iv_code_sixth);
        //
        rl_share_code = (RelativeLayout) view.findViewById(R.id.rl_share_code);
        iv_code_game_mode_logo = (ImageView) view.findViewById(R.id.iv_code_game_mode_logo);
        //
        iv_game_creator_head = (HeadImageView) view.findViewById(R.id.iv_game_creator_head);
        tv_game_name = (TextView) view.findViewById(R.id.tv_game_name);
        //
        tv_game_other_info = (TextView) view.findViewById(R.id.tv_game_other_info);
        tv_game_chips = (TextView) view.findViewById(R.id.tv_game_chips);
        tv_game_blinds = (TextView) view.findViewById(R.id.tv_game_blinds);
        tv_game_checkin_fee = (TextView) view.findViewById(R.id.tv_game_checkin_fee);
        tv_game_match_player = (TextView) view.findViewById(R.id.tv_game_match_player);
        tv_game_time = (TextView) view.findViewById(R.id.tv_game_time);
        tv_game_ante = (TextView) view.findViewById(R.id.tv_game_ante);
        if (shareGameMode == GameConstants.GAME_MODE_NORMAL) {
            rl_share_code.setBackgroundResource(R.drawable.share_code_normal_bg);
            iv_code_game_mode_logo.setImageResource(R.mipmap.message_system);
        } else if (shareGameMode == GameConstants.GAME_MODE_SNG) {
            rl_share_code.setBackgroundResource(R.drawable.share_code_sng_bg);
            iv_code_game_mode_logo.setImageResource(R.mipmap.icon_control_sng);
        } else if (shareGameMode == GameConstants.GAME_MODE_MTT) {
            rl_share_code.setBackgroundResource(R.drawable.share_code_mtt_bg);
            iv_code_game_mode_logo.setImageResource(R.mipmap.icon_control_mtt);
        } else if (shareGameMode == GameConstants.GAME_MODE_MT_SNG) {
            rl_share_code.setBackgroundResource(R.drawable.share_code_sng_bg);
            iv_code_game_mode_logo.setImageResource(R.mipmap.icon_control_mtsng);
        }
    }

    public void setGameInfo(String creatorId, String gameName, String code, int mode, Object gameConfig) {
        shareGameMode = mode;
        initView();
        if (!TextUtils.isEmpty(code) && code.length() == 6) {
            LogUtil.i("ShareView", code);
            //
//            iv_code_sixth.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(5, 6))));
//            iv_code_fifth.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(4, 5))));
//            iv_code_fourth.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(3, 4))));
//            iv_code_third.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(2, 3))));
//            iv_code_sencond.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(1, 2))));
//            iv_code_first.setImageResource(getCodeItemSrc(Integer.valueOf(code.substring(0, 1))));
        }
        iv_game_creator_head.loadBuddyAvatar(creatorId);
        tv_game_name.setText(gameName);

        tv_mtt_time.setVisibility(View.GONE);
        //普通
        if (shareGameMode == GameConstants.GAME_MODE_NORMAL && gameConfig instanceof GameNormalConfig) {
            GameNormalConfig gameNormalConfig = (GameNormalConfig) gameConfig;
            tv_game_checkin_fee.setVisibility(GONE);
            tv_game_match_player.setVisibility(GONE);
            tv_game_chips.setVisibility(GONE);
            tv_game_blinds.setVisibility(VISIBLE);
            tv_game_ante.setVisibility(View.VISIBLE);

            int blinds = gameNormalConfig.blindType;
            tv_game_chips.setText(getContext().getString(R.string.game_share_code_normal_blind, GameConstants.getGameBlindsShow(blinds)));
            tv_game_blinds.setText(getContext().getString(R.string.game_share_code_normal_blind, GameConstants.getGameBlindsShow(blinds)));
            tv_game_time.setText(getContext().getString(R.string.game_share_code_normal_time, GameConstants.getGameDurationShow(gameNormalConfig.timeType)));
            //tv_game_ante.setText(getContext().getString(R.string.game_share_code_normal_ante, GameConstants.getGameAnte(gameNormalConfig)));
            tv_game_ante.setText(getContext().getString(R.string.game_share_code_normal_seat_count , String.valueOf(gameNormalConfig.matchPlayer)));
            if (gameNormalConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT) {
                tv_game_other_info.setVisibility(GONE);
            } else {
                tv_game_other_info.setVisibility(VISIBLE);
                tv_game_other_info.setText(getContext().getString(R.string.game_mode_insurance));
            }
        } else if (shareGameMode == GameConstants.GAME_MODE_SNG && gameConfig instanceof GameSngConfigEntity) {
            GameSngConfigEntity gameSngConfigEntity = (GameSngConfigEntity) gameConfig;
            tv_game_chips.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_match_player.setVisibility(GONE);
            tv_game_other_info.setVisibility(VISIBLE);
            tv_game_blinds.setVisibility(GONE);
            tv_game_ante.setVisibility(GONE);
            tv_game_checkin_fee.setText(getContext().getString(R.string.game_share_code_match_checkin_fee, String.valueOf(gameSngConfigEntity.getCheckInFee())));//
            tv_game_chips.setText(getContext().getString(R.string.game_share_code_match_chip, String.valueOf(gameSngConfigEntity.getChips())));//
            tv_game_time.setText(getContext().getString(R.string.game_share_code_match_blinds_time, GameConstants.getGameSngDurationMinutesShow(gameSngConfigEntity.getDuration())));//
            tv_game_other_info.setText(getContext().getString(R.string.game_share_code_match_player, String.valueOf(gameSngConfigEntity.getPlayer())));
        } else if (shareGameMode == GameConstants.GAME_MODE_MTT && gameConfig instanceof GameMttConfig) {
            GameMttConfig gameMttConfig = (GameMttConfig) gameConfig;
            tv_game_chips.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_match_player.setVisibility(GONE);
            tv_game_other_info.setVisibility(VISIBLE);
            tv_game_blinds.setVisibility(GONE);
            tv_game_ante.setVisibility(VISIBLE);
            tv_mtt_time.setVisibility(View.VISIBLE);
            int chips = gameMttConfig.matchChips;
            String chipsStr = chips + "(" + chips / 20 + "BBs)";
            tv_game_checkin_fee.setText(getContext().getString(R.string.game_share_code_match_checkin_fee, String.valueOf(gameMttConfig.matchCheckinFee)));//
            tv_game_chips.setText(getContext().getString(R.string.game_share_code_match_chip, chipsStr));//

            tv_game_time.setText(getContext().getString(R.string.game_share_code_match_player, String.valueOf(gameMttConfig.matchPlayer)));
            tv_game_ante.setText(getContext().getString(R.string.game_share_code_match_blinds_time, GameConstants.getGameSngDurationMinutesShow(gameMttConfig.matchDuration)));//

            tv_game_other_info.setText(getContext().getString(R.string.tv_begin_time));
            tv_mtt_time.setText(DateCalendarTools.formatTimestampToStr(gameMttConfig.beginTime * 1000,getContext().getString(R.string.tv_time_format)));

        } else if (shareGameMode == GameConstants.GAME_MODE_MT_SNG && gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMttConfig = (GameMtSngConfig) gameConfig;
            tv_game_chips.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_match_player.setVisibility(VISIBLE);
            tv_game_other_info.setVisibility(VISIBLE);
            tv_game_blinds.setVisibility(GONE);
            tv_game_ante.setVisibility(GONE);
            int chips = gameMttConfig.matchChips;
            String chipsStr = chips + "(" + chips / 20 + "BBs)";
            tv_game_checkin_fee.setText(getContext().getString(R.string.game_share_code_match_checkin_fee, String.valueOf(gameMttConfig.matchCheckinFee)));//
            tv_game_chips.setText(getContext().getString(R.string.game_share_code_match_chip, chipsStr));//
            tv_game_time.setText(getContext().getString(R.string.game_share_code_match_blinds_time, GameConstants.getGameSngDurationMinutesShow(gameMttConfig.matchDuration)));//
            tv_game_match_player.setText(getContext().getString(R.string.game_share_code_match_player, String.valueOf(gameMttConfig.matchPlayer)));
            tv_game_other_info.setText(getContext().getString(R.string.game_share_code_total_player, String.valueOf(gameMttConfig.totalPlayer)));
        }
    }

    public int getCodeItemSrc(int i) {
        LogUtil.i("ShareView", "i:" + i);
        switch (i) {
//            case 0:
//                return R.mipmap.icon_code_0;
//            case 1:
//                return R.mipmap.icon_code_1;
//            case 2:
//                return R.mipmap.icon_code_2;
//            case 3:
//                return R.mipmap.icon_code_3;
//            case 4:
//                return R.mipmap.icon_code_4;
//            case 5:
//                return R.mipmap.icon_code_5;
//            case 6:
//                return R.mipmap.icon_code_6;
//            case 7:
//                return R.mipmap.icon_code_7;
//            case 8:
//                return R.mipmap.icon_code_8;
//            case 9:
//                return R.mipmap.icon_code_9;
//            default:
//                return R.mipmap.icon_code_0;
        }
        return 100;
    }
}
