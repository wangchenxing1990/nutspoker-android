package com.htgames.nutspoker.circle.view;

import android.content.Context;
import android.util.AttributeSet;

import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.htgames.nutspoker.view.record.RankView;
import com.netease.nim.uikit.common.util.log.LogUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

/**
 * Created by 20150726 on 2016/1/21.
 */
public class CirclePaijuView extends LinearLayout {
    private final static String TAG = "CirclePaijuView";
    public Context context;
    View view;
    LinearLayout ll_circle_paiju;
    TextView tv_game_name;
    TextView tv_game_duration;
    TextView tv_game_blind;
    TextView tv_game_member;
    TextView tv_game_checkin_fee;
    TextView tv_game_earnings;
    HeadImageView iv_game_creator_userhead;
    TextView tv_game_ante;
    ImageView iv_game_insurance;
    RankView mRankView;
    //
    RelativeLayout rl_game_mode;
    ImageView iv_game_mode;

    public CirclePaijuView(Context context) {
        super(context);
        init(context);
    }

    public CirclePaijuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CirclePaijuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.layout_circle_paiju_item, null);
        initView();
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setBackground(int resId) {
        ll_circle_paiju.setBackgroundResource(resId);
    }

    private void initView() {
        ll_circle_paiju = (LinearLayout) view.findViewById(R.id.ll_circle_paiju);
        tv_game_name = (TextView) view.findViewById(R.id.tv_game_name);
        tv_game_duration = (TextView) view.findViewById(R.id.tv_game_duration);
        mRankView = (RankView) view.findViewById(R.id.mRankView);
        tv_game_member = (TextView) view.findViewById(R.id.tv_game_member);
        tv_game_checkin_fee = (TextView) view.findViewById(R.id.tv_game_checkin_fee);
        tv_game_blind = (TextView) view.findViewById(R.id.tv_game_blind);
        tv_game_earnings = (TextView) view.findViewById(R.id.tv_game_earnings);
        iv_game_creator_userhead = (HeadImageView) view.findViewById(R.id.iv_game_creator_userhead);
        iv_game_insurance = (ImageView) view.findViewById(R.id.iv_game_insurance);
        tv_game_ante = (TextView) view.findViewById(R.id.tv_game_ante);
        rl_game_mode = (RelativeLayout) view.findViewById(R.id.rl_game_mode);
        iv_game_mode = (ImageView) view.findViewById(R.id.iv_game_mode);
    }

    public void setData(GameBillEntity billEntity) {
        GameEntity gameInfo = billEntity.gameInfo;
        tv_game_name.setText(gameInfo.name);
        UserEntity creatorInfo = gameInfo.creatorInfo;
        iv_game_creator_userhead.loadBuddyAvatarByUrl(creatorInfo.account, creatorInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        //
        GameMemberEntity showMemberInfo = billEntity.myMemberInfo;
        int winChip = billEntity.winChip;
        if (showMemberInfo != null) {
            winChip = RecordHelper.getAllGain(showMemberInfo);
        }
        if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
            //普通模式
            tv_game_duration.setVisibility(VISIBLE);
            tv_game_blind.setVisibility(VISIBLE);
            tv_game_member.setVisibility(GONE);
            tv_game_checkin_fee.setVisibility(GONE);
            rl_game_mode.setVisibility(GONE);
            mRankView.setVisibility(GONE);
            GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
            tv_game_blind.setText(GameConstants.getGameBlindsShow(gameConfig.blindType));
            tv_game_duration.setText(GameConstants.getGameDurationShow(gameConfig.timeType));
            LogUtil.i(TAG, "blind :" + gameConfig.blindType + "; time:" + gameConfig.timeType);
            if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT) {
                iv_game_insurance.setVisibility(GONE);
            } else {
                iv_game_insurance.setVisibility(VISIBLE);
            }
            int ante = GameConstants.getGameAnte(gameConfig);
            if (ante == GameConstants.ANTE_TYPE_0) {
                tv_game_ante.setVisibility(View.GONE);
            } else {
                tv_game_ante.setVisibility(View.VISIBLE);
                tv_game_ante.setText(String.format(context.getString(R.string.game_message_ante), ante));
            }
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
            //SNG模式
            GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
            //
            rl_game_mode.setVisibility(VISIBLE);
            tv_game_member.setVisibility(VISIBLE);
            mRankView.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_duration.setVisibility(GONE);
            tv_game_blind.setVisibility(GONE);
            tv_game_ante.setVisibility(GONE);
            iv_game_insurance.setVisibility(GONE);
            tv_game_member.setText(String.valueOf(gameConfig.getPlayer()));
            tv_game_checkin_fee.setText(String.valueOf(gameConfig.getCheckInFee()));
            //
            if (showMemberInfo != null) {
                winChip = showMemberInfo.reward;
                int rank = showMemberInfo.ranking;
//                SngHelper.setSngRankTagView(tv_record_sng_rank_tag, false, rank);
                mRankView.setRankTagView(rank);
            }
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_sng);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
            GameMttConfig gameMttConfig = (GameMttConfig) gameInfo.gameConfig;
            rl_game_mode.setVisibility(VISIBLE);
            tv_game_member.setVisibility(VISIBLE);
            mRankView.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_duration.setVisibility(GONE);
            tv_game_blind.setVisibility(GONE);
            tv_game_ante.setVisibility(GONE);
            iv_game_insurance.setVisibility(GONE);
            tv_game_member.setText(billEntity.totalPlayer + "");
            tv_game_checkin_fee.setText("" + gameMttConfig.matchCheckinFee);
            //
            if (showMemberInfo != null) {
                winChip = showMemberInfo.reward;
                int rank = showMemberInfo.ranking;
//                tv_record_sng_rank_tag.setBackgroundResource(RecordHelper.getRecordSngRankImage(rank, true));
//                SngHelper.setSngRankTagView(tv_record_sng_rank_tag, false, rank);
                mRankView.setRankTagView(rank);
            }
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtt);
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameInfo.gameConfig;
            rl_game_mode.setVisibility(VISIBLE);
            tv_game_member.setVisibility(VISIBLE);
            mRankView.setVisibility(VISIBLE);
            tv_game_checkin_fee.setVisibility(VISIBLE);
            tv_game_duration.setVisibility(GONE);
            tv_game_blind.setVisibility(GONE);
            tv_game_ante.setVisibility(GONE);
            iv_game_insurance.setVisibility(GONE);
            tv_game_member.setText("" + billEntity.totalPlayer);
            tv_game_checkin_fee.setText(String.valueOf(gameMtSngConfig.matchCheckinFee));
            //
            if (showMemberInfo != null) {
                winChip = showMemberInfo.reward;
                int rank = showMemberInfo.ranking;
//                tv_record_sng_rank_tag.setBackgroundResource(RecordHelper.getRecordSngRankImage(rank, true));
//                SngHelper.setSngRankTagView(tv_record_sng_rank_tag, false, rank);
                mRankView.setRankTagView(rank);
            }
            //
            iv_game_mode.setImageResource(R.mipmap.icon_control_mtsng);
        }
        RecordHelper.setRecordGainView(tv_game_earnings, winChip, gameInfo.gameMode);
    }
}
