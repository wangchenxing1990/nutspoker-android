package com.htgames.nutspoker.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.helper.UserInfoShowHelper;
import com.htgames.nutspoker.ui.helper.RecordHelper;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.GameNormalConfig;
import com.netease.nim.uikit.bean.GameSngConfigEntity;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.SpanUtils;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 20150726 on 2016/3/3.
 */
public class RecordDetailsUserView extends RelativeLayout {
    View view;
    HeadImageView iv_game_current_userhead;
    TextView tv_game_current_nickname;
    TextView tv_game_join_hands_des;
    TextView tv_game_join_hands_num;
    TextView tv_game_ruchi_hands;
    TextView tv_game_win_des;
    TextView tv_game_win_num;
    TextView tv_game_lose;
    TextView tv_game_win_chips;
    TextView tv_game_insurance_chips;
    //    TextView tv_record_details_user_gain_title;
    TextView tv_rank;
    TextView match_user_gain;
    TextView tv_hunter_rank;
    TextView match_user_gain_hunter;
    TextView tv_pineapple_wins;
    View normal_info_container;
    View match_info_container;
    TextView tv_diamond_match_rank;//钻石赛 排名

    LinearLayout ll_game_insurance_chips;

    public RecordDetailsUserView(Context context) {
        super(context);
        init(context);
    }

    public RecordDetailsUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecordDetailsUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_record_details_user, null);
        tv_game_join_hands_des = (TextView) view.findViewById(R.id.tv_game_join_hands_des);
        tv_game_win_des = (TextView) view.findViewById(R.id.tv_game_win_des);
        tv_pineapple_wins = (TextView) view.findViewById(R.id.tv_pineapple_wins);
        iv_game_current_userhead = (HeadImageView) view.findViewById(R.id.iv_game_current_userhead);
        tv_game_current_nickname = (TextView) view.findViewById(R.id.tv_game_current_nickname);
//        tv_game_join_hands = (TextView) view.findViewById(R.id.tv_game_join_hands);
        tv_game_join_hands_num = (TextView) view.findViewById(R.id.tv_game_join_hands_num);
        tv_game_ruchi_hands = (TextView) view.findViewById(R.id.tv_game_ruchi_hands);
//        tv_game_win = (TextView) view.findViewById(R.id.tv_game_win);
        tv_game_win_num = (TextView) view.findViewById(R.id.tv_game_win_num);
        tv_game_lose = (TextView) view.findViewById(R.id.tv_game_lose);
        tv_game_win_chips = (TextView) view.findViewById(R.id.tv_game_win_chips);
        match_user_gain = (TextView) view.findViewById(R.id.match_user_gain);
        tv_hunter_rank = (TextView) view.findViewById(R.id.tv_hunter_rank);
        match_user_gain_hunter = (TextView) view.findViewById(R.id.match_user_gain_hunter);
        tv_game_insurance_chips = (TextView) view.findViewById(R.id.tv_game_insurance_chips);
//        tv_record_details_user_gain_title = (TextView) view.findViewById(R.id.tv_record_details_user_gain_title);
        normal_info_container = view.findViewById(R.id.normal_info_container);
        match_info_container = view.findViewById(R.id.match_info_container);
//        mRankView = (RankView) view.findViewById(mRankView);
        tv_rank = (TextView) view.findViewById(R.id.tv_rank);
        ll_game_insurance_chips = (LinearLayout) view.findViewById(R.id.ll_game_insurance_chips);
        tv_diamond_match_rank = (TextView) view.findViewById(R.id.tv_diamond_match_rank);
        addView(view , new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setUserHandInfo(GameMemberEntity gameMemberEntity , GameEntity gameInfo , int endBlindIndex) {
        if (gameMemberEntity == null || gameInfo == null) {
            return;
        }
        UserEntity userEntity = gameMemberEntity.userInfo;//加载头像  和  昵称
        iv_game_current_userhead.loadBuddyAvatarByUrl(userEntity.account, userEntity.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        tv_game_current_nickname.setText(NimUserInfoCache.getInstance().getUserDisplayName(userEntity.account)/*UserInfoShowHelper.getUserNickname(userEntity)*/);
        if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfigMtt) {
            PineappleConfigMtt config = (PineappleConfigMtt) gameInfo.gameConfig;
            normal_info_container.setVisibility(GONE);
            match_info_container.setVisibility(VISIBLE);
            ll_game_insurance_chips.setVisibility(GONE);
            tv_rank.setVisibility(VISIBLE);
            int rank = gameMemberEntity.ranking;
            tv_rank.setText("" + rank);
            tv_game_join_hands_des.setText("手数/盈利局");
            tv_game_join_hands_num.setText("" + gameMemberEntity.joinCnt + "/" + gameMemberEntity.winCnt);
            tv_game_win_des.setText("范特西");
            tv_game_win_num.setText("" + gameMemberEntity.fantasy_cnt);
            tv_pineapple_wins.setVisibility(VISIBLE);
            tv_hunter_rank.setText(gameMemberEntity.ko_head_cnt + "");int winChips = gameMemberEntity.winChip;
            if(gameInfo.gameMode == GameConstants.GAME_MODE_SNG || gameInfo.gameMode == GameConstants.GAME_MODE_MTT || gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                winChips = gameMemberEntity.reward;
            }
            int totalWin = winChips;// + insuranceWinChips;//这里修改成盈利加保险盈利
            match_user_gain.setText("奖金：" + totalWin);
            if (config.ko_mode == 0) {//不是猎人赛
                match_user_gain.setVisibility(GONE);
                tv_hunter_rank.setVisibility(GONE);
                match_user_gain_hunter.setText("奖金：" + gameMemberEntity.reward);
            } else {//是猎人赛
                match_user_gain.setVisibility(VISIBLE);
                tv_hunter_rank.setVisibility(VISIBLE);
                match_user_gain_hunter.setText("赏金：" + gameMemberEntity.ko_head_reward);
            }
            if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                match_info_container.setVisibility(VISIBLE);//钻石赛只显示一个信息：排名
                tv_diamond_match_rank.setVisibility(GONE);
            } else {
                match_info_container.setVisibility(GONE);//钻石赛只显示一个信息：排名
                tv_diamond_match_rank.setVisibility(VISIBLE);
                tv_diamond_match_rank.setText("排名：" + gameMemberEntity.ranking);
            }
        } else if (gameInfo.play_mode == GameConstants.PLAY_MODE_PINEAPPLE && gameInfo.gameConfig instanceof PineappleConfig) {
            normal_info_container.setVisibility(GONE);
            match_info_container.setVisibility(GONE);
            tv_game_join_hands_des.setText("手数/盈利局");
            tv_game_join_hands_num.setText("" + gameMemberEntity.joinCnt + "/" + gameMemberEntity.winCnt);
            tv_game_win_des.setText("范特西");
            tv_game_win_num.setText("" + gameMemberEntity.fantasy_cnt);
            tv_pineapple_wins.setVisibility(VISIBLE);
            String pineappleWinStr = gameMemberEntity.winChip >= 0 ? " +" + WealthHelper.getWealthShow(gameMemberEntity.winChip) : " " + WealthHelper.getWealthShow(gameMemberEntity.winChip);
            int color = ContextCompat.getColor(ChessApp.sAppContext, gameMemberEntity.winChip >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no);
            tv_pineapple_wins.setText(new SpanUtils().append("盈利: ").append(pineappleWinStr).setForegroundColor(color).create());
        } else if (gameInfo.play_mode < GameConstants.PLAY_MODE_PINEAPPLE) {
            boolean isNormal = gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig;
            normal_info_container.setVisibility(isNormal ? VISIBLE : GONE);
            match_info_container.setVisibility(!isNormal ? VISIBLE : GONE);
            tv_game_join_hands_des.setText("总手数");
            tv_game_win_des.setText("盈利局");
            tv_pineapple_wins.setVisibility(GONE);
            int joinHands = gameMemberEntity.joinCnt;
            int ruchiHands = gameMemberEntity.enterPotCnt;
            int win = gameMemberEntity.winCnt;
            int lose = gameMemberEntity.joinCnt - gameMemberEntity.winCnt;
            int winChips = gameMemberEntity.winChip;
            if(gameInfo.gameMode == GameConstants.GAME_MODE_SNG || gameInfo.gameMode == GameConstants.GAME_MODE_MTT || gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                winChips = gameMemberEntity.reward;
            }
//        tv_game_join_hands.setText(String.format(getContext().getString(R.string.record_details_join_hand) , joinHands));
            tv_game_join_hands_num.setText("" + joinHands);
            tv_game_ruchi_hands.setText(String.format(getContext().getString(R.string.record_details_ruchi_hand) , ruchiHands + ""));
//        tv_game_win.setText(String.format(getContext().getString(R.string.record_details_win) , win));
            tv_game_win_num.setText("" + win);
            tv_game_lose.setText(String.format(getContext().getString(R.string.record_details_lose), lose));

            //保险盈利
            int insuranceWinChips = gameMemberEntity.insurance;// - gameMemberEntity.getPremium();//保险盈利 - 投保金额
//        RecordHelper.setRecordGainView(tv_game_insurance_chips , insuranceWinChips , gameInfo.gameMode);
            WealthHelper.SetMoneyText(tv_game_insurance_chips, insuranceWinChips, ChessApp.sAppContext);
            //盈利
//        RecordHelper.setRecordGainView(tv_game_win_chips , winChips+insuranceWinChips , gameInfo.gameMode);//这里修改成盈利加保险盈利
            int totalWin = winChips;// + insuranceWinChips;//这里修改成盈利加保险盈利
            WealthHelper.SetMoneyText(tv_game_win_chips, totalWin, ChessApp.sAppContext);
            match_user_gain.setText("奖金：" + totalWin);
            if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL && gameInfo.gameConfig instanceof GameNormalConfig) {
                //普通模式
                tv_rank.setVisibility(GONE);
//            tv_record_details_user_gain_title.setText(R.string.record_details_user_win_chips);
                GameNormalConfig gameConfig = (GameNormalConfig) gameInfo.gameConfig;
                if (gameConfig.tiltMode == GameConstants.GAME_MODE_INSURANCE_NOT) {
                    ll_game_insurance_chips.setVisibility(GONE);
                } else {
                    ll_game_insurance_chips.setVisibility(VISIBLE);
                }
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG && gameInfo.gameConfig instanceof GameSngConfigEntity) {
                //SNG模式
//            tv_record_details_user_gain_title.setText(R.string.record_details_user_reward);
                GameSngConfigEntity gameConfig = (GameSngConfigEntity) gameInfo.gameConfig;
                ll_game_insurance_chips.setVisibility(GONE);
//            mRankView.setVisibility(VISIBLE);
                tv_rank.setVisibility(VISIBLE);
                int rank = gameMemberEntity.ranking;
                tv_rank.setText("" + rank);
                match_user_gain.setVisibility(GONE);
                tv_hunter_rank.setVisibility(GONE);
                match_user_gain_hunter.setText("奖金：" + totalWin);
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof GameMttConfig) {
                //MTT模式
                GameMttConfig gameConfig = (GameMttConfig) gameInfo.gameConfig;
                ll_game_insurance_chips.setVisibility(GONE);
                tv_rank.setVisibility(VISIBLE);
                int rank = gameMemberEntity.ranking;
                tv_rank.setText("" + rank);
                tv_hunter_rank.setText(gameMemberEntity.ko_head_cnt + "");
                if (gameConfig.ko_mode == 0) {//不是猎人赛
                    match_user_gain.setVisibility(GONE);
                    tv_hunter_rank.setVisibility(GONE);
                    match_user_gain_hunter.setText("奖金：" + totalWin);
                } else {//是猎人赛
                    match_user_gain.setVisibility(VISIBLE);
                    tv_hunter_rank.setVisibility(VISIBLE);
                    match_user_gain_hunter.setText("赏金：" + gameMemberEntity.ko_head_reward);
                }
                if (gameInfo.match_type == 0) {
                    match_info_container.setVisibility(VISIBLE);//钻石赛只显示一个信息：排名
                    tv_diamond_match_rank.setVisibility(GONE);
                } else if (gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND) {
                    match_info_container.setVisibility(GONE);//钻石赛只显示一个信息：排名
                    tv_diamond_match_rank.setVisibility(VISIBLE);
                    tv_diamond_match_rank.setText("排名：" + gameMemberEntity.ranking);
                }
            }
        }
    }

    public void setUserHandInfoNull() {
//        tv_game_join_hands.setText(String.format(getContext().getString(R.string.record_details_join_hand), 0));
        tv_game_join_hands_num.setText("" + 0);
        tv_game_ruchi_hands.setText(String.format(getContext().getString(R.string.record_details_ruchi_hand), 0 + ""));
//        tv_game_win.setText(String.format(getContext().getString(R.string.record_details_win), 0));
        tv_game_win_num.setText("" + 0);
        tv_game_lose.setText(String.format(getContext().getString(R.string.record_details_lose), 0));
        //盈利
        RecordHelper.setRecordGainView(tv_game_win_chips, 0, GameConstants.GAME_MODE_NORMAL);
        //保险盈利
        RecordHelper.setRecordGainView(tv_game_insurance_chips, 0, GameConstants.GAME_MODE_NORMAL);
    }
}
