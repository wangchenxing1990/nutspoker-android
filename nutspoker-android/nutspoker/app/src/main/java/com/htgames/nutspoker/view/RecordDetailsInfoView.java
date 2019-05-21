package com.htgames.nutspoker.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;

import static com.htgames.nutspoker.config.GameConfigData.pineappleModeStrIds;

/**
 */
public class RecordDetailsInfoView extends RelativeLayout {
    View view;
    HeadImageView iv_userhead_creator;
    TextView tv_game_creator_nickname;
    TextView tv_game_date;
    ImageView iv_horde_record;
    ImageView iv_omaha_icon;
    //普通局
    View record_normal_container;
    TextView tv_game_duration_num;
    TextView tv_game_all_buy_num;
    TextView tv_game_hands_and_reward_num;
    TextView ll_game_insurance;
    TextView tv_game_ante;
    //sng
    View record_sng_container;
    TextView tv_sng_durations;
    TextView tv_sng_total_reward;
    //mtt
    View record_mtt_container;
    TextView tv_mtt_durations;
    TextView tv_mtt_total_reward;
    TextView mtt_checkin_num;
    TextView mtt_buy_num;
    TextView record_detail_mtt_hunter_iv;
    View pineapple_mark_mtt_container;
    TextView pineapple_mtt_play_type;
    //pineapple
    View record_pineapple_container;
    View pineapple_mark_container;
    TextView pineapple_duration_num;
    TextView pineapple_hands_num;
    TextView pineapple_all_buy_num;
    TextView pineapple_play_type;
    public RecordDetailsInfoView(Context context) {
        super(context);
        init(context);
    }

    public RecordDetailsInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordDetailsInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_record_details_info_new, null);
        iv_userhead_creator = (HeadImageView) view.findViewById(R.id.iv_userhead_creator);
        tv_game_creator_nickname = (TextView) view.findViewById(R.id.tv_game_creator_nickname);
        tv_game_date = (TextView) view.findViewById(R.id.tv_game_date);
        iv_horde_record = (ImageView) view.findViewById(R.id.iv_horde_record);
        iv_omaha_icon = (ImageView) view.findViewById(R.id.iv_omaha_icon);
        //普通局
        record_normal_container = view.findViewById(R.id.record_normal_container);
        tv_game_duration_num = (TextView) view.findViewById(R.id.tv_game_duration_num);
        tv_game_all_buy_num = (TextView) view.findViewById(R.id.tv_game_all_buy_num);
        tv_game_hands_and_reward_num = (TextView) view.findViewById(R.id.tv_game_hands_and_reward_num);
        ll_game_insurance = (TextView) view.findViewById(R.id.ll_game_insurance);
        tv_game_ante = (TextView) view.findViewById(R.id.tv_game_ante);
        //sng
        record_sng_container = view.findViewById(R.id.record_sng_container);
        tv_sng_durations = (TextView) view.findViewById(R.id.tv_sng_durations);
        tv_sng_total_reward = (TextView) view.findViewById(R.id.tv_sng_total_reward);
        //mtt
        record_mtt_container = view.findViewById(R.id.record_mtt_container);
        tv_mtt_durations = (TextView) view.findViewById(R.id.tv_mtt_durations);
        tv_mtt_total_reward = (TextView) view.findViewById(R.id.tv_mtt_total_reward);
        record_detail_mtt_hunter_iv = (TextView) view.findViewById(R.id.record_detail_mtt_hunter_iv);
        mtt_checkin_num = (TextView) view.findViewById(R.id.mtt_checkin_num);
        mtt_buy_num = (TextView) view.findViewById(R.id.mtt_buy_num);
        pineapple_mark_mtt_container = view.findViewById(R.id.pineapple_mark_mtt_container);
        pineapple_mtt_play_type = (TextView) view.findViewById(R.id.pineapple_mtt_play_type);
        //pineapple
        record_pineapple_container = view.findViewById(R.id.record_pineapple_container);
        pineapple_mark_container = view.findViewById(R.id.pineapple_mark_container);
        pineapple_duration_num = (TextView) view.findViewById(R.id.pineapple_duration_num);
        pineapple_hands_num = (TextView) view.findViewById(R.id.pineapple_hands_num);
        pineapple_all_buy_num = (TextView) view.findViewById(R.id.pineapple_all_buy_num);
        pineapple_play_type = (TextView) view.findViewById(R.id.pineapple_play_type);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setInfo(GameBillEntity bill) {
        GameEntity gameInfo = bill.gameInfo;
        if (gameInfo != null) {
            UserEntity creatorInfo = gameInfo.creatorInfo;
            record_normal_container.setVisibility(gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL ? VISIBLE : GONE);
            record_sng_container.setVisibility(gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameMode == GameConstants.GAME_MODE_SNG ? VISIBLE : GONE);
            record_mtt_container.setVisibility(gameInfo.gameMode == GameConstants.GAME_MODE_MTT ? VISIBLE : GONE);//普通mtt或者大菠萝mtt共用这个container
            //这个大菠萝只是普通大菠萝，不是mtt大菠萝
            record_pineapple_container.setVisibility(gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfig ? VISIBLE : GONE);
            pineapple_mark_mtt_container.setVisibility(gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfigMtt ? VISIBLE : GONE);
            tv_game_date.setText(DateTools.getOtherStrTime_ymd_hm("" + gameInfo.endTime));
            boolean isHordeRecord = !StringUtil.isSpace(gameInfo.horde_name);
            iv_horde_record.setVisibility(isHordeRecord ? VISIBLE : GONE);
            if (isHordeRecord) {
                tv_game_creator_nickname.setText(gameInfo.horde_name);
                iv_userhead_creator.setImageResource(R.mipmap.icon_horde);
            } else {
                tv_game_creator_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(creatorInfo.account)/*UserInfoShowHelper.getUserNickname(creatorInfo)*/);
                iv_userhead_creator.loadBuddyAvatarByUrl(creatorInfo.account, creatorInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
            }
            //游戏类别
            if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfigMtt) {
                PineappleConfigMtt config = (PineappleConfigMtt) gameInfo.gameConfig;
                pineapple_mark_mtt_container.post(new Runnable() {
                    @Override
                    public void run() {
                        int imageWidth = ScreenUtil.dp2px(getContext(), 91);
                        AnimUtil.translateX(pineapple_mark_mtt_container, -imageWidth, (int) ((ScreenUtil.screenWidth - imageWidth) / 2f), 400);
                    }
                });
                pineapple_mtt_play_type.setText(pineappleModeStrIds[config.getPlay_type()]);
                int original_ko_reward_rate = (config.ko_mode != 0) ? config.ko_reward_rate : 0;
                int allReward = (int) (bill.allReward * 100 / (100.0f - original_ko_reward_rate));
                if (gameInfo.match_type == 0) {
                    tv_mtt_total_reward.setText("" + allReward);
                } else if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) {
                    tv_mtt_total_reward.setText("无");//钻石赛显示这个
                }
                record_detail_mtt_hunter_iv.setVisibility(config.ko_mode == 0 ? GONE : VISIBLE);
                tv_mtt_durations.setText("" + GameConstants.getGameSngDurationShow(bill.totalTime));
                mtt_checkin_num.setText("参赛人数：" + bill.totalPlayer);
                mtt_buy_num.setText("参赛人次：" + (allReward / (config.matchCheckinFee)));
            } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfig) {
                pineapple_duration_num.setText("" + GameConstants.getGameDurationShow(((PineappleConfig) gameInfo.gameConfig).getDuration()));
                pineapple_hands_num.setText("" + bill.bouts);
                pineapple_all_buy_num.setText("" + bill.allBuys);
                pineapple_play_type.setText(pineappleModeStrIds[((PineappleConfig) gameInfo.gameConfig).getPlay_type()]);
                pineapple_mark_container.post(new Runnable() {
                    @Override
                    public void run() {
                        int imageWidth = ScreenUtil.dp2px(getContext(), 91);
                        AnimUtil.translateX(pineapple_mark_container, -imageWidth, (int) ((ScreenUtil.screenWidth - imageWidth) / 2f), 400);
                    }
                });
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
                //普通模式
                GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
                tv_game_all_buy_num.setText("" + bill.allBuys);
                tv_game_hands_and_reward_num.setText("" + bill.bouts);
                tv_game_duration_num.setText("" + GameConstants.getGameDurationShow(gameConfig.timeType));
                ll_game_insurance.setVisibility(gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT ? View.GONE : VISIBLE);
                tv_game_ante.setVisibility(GameConstants.getGameAnte(gameConfig) <= 0 ? View.GONE : VISIBLE);
                tv_game_ante.setText("Ante: " + GameConstants.getGameAnte(gameConfig));
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
                //SNG模式
                tv_sng_durations.setText("" + GameConstants.getGameSngDurationShow(bill.totalTime));
                tv_sng_total_reward.setText("" + bill.allReward);
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
                //mtt
                GameMttConfig gameMttConfig = (GameMttConfig) gameInfo.gameConfig;
                int original_ko_reward_rate = (((GameMttConfig) gameInfo.gameConfig).ko_mode != 0) ? ((GameMttConfig) gameInfo.gameConfig).ko_reward_rate : 0;
                int allReward = (int) (bill.allReward * 100 / (100.0f - original_ko_reward_rate));
                if (gameInfo.match_type == 0) {
                    tv_mtt_total_reward.setText("" + allReward);
                } else if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) {
                    tv_mtt_total_reward.setText("无");//钻石赛显示这个
                }
                record_detail_mtt_hunter_iv.setVisibility(gameMttConfig.ko_mode == 0 ? GONE : VISIBLE);
                tv_mtt_durations.setText("" + GameConstants.getGameSngDurationShow(bill.totalTime));
                mtt_checkin_num.setText("参赛人数：" + bill.totalPlayer);
                mtt_buy_num.setText("参赛人次：" + (allReward / (((GameMttConfig) gameInfo.gameConfig).matchCheckinFee)));
            }
        }
        iv_omaha_icon.setVisibility(gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA ? VISIBLE : GONE);
        if (gameInfo.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            iv_omaha_icon.post(new Runnable() {
                @Override
                public void run() {
                    iv_omaha_icon.setVisibility(VISIBLE);
                    int imageWidth = ScreenUtil.dp2px(getContext(), 56);
                    AnimUtil.translateX(iv_omaha_icon, -imageWidth, (int) ((ScreenUtil.screenWidth - imageWidth) / 2f), 400);
                }
            });
        }
    }
}
