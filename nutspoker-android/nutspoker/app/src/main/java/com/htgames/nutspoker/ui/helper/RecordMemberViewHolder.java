package com.htgames.nutspoker.ui.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.match.reward.RewardAllot;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.view.record.RankViewV;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * 游戏成员ViewHolder
 */
public class RecordMemberViewHolder extends RecyclerView.ViewHolder {
    Context context;
    public HeadImageView iv_game_userhead;
    public View record_detail_player_info_container;
    public ImageView iv_insurance_tag;
    RankViewV mRankView;
    public TextView tv_record_rank;
    public TextView tv_game_earnings;
    public TextView tv_game_name;
    public TextView tv_game_buys;
    public TextView record_member_hunter_info;
    public View record_member_hunter_info_container;
    public TextView record_member_hunter_reward;
    //
    public LinearLayout ll_record_member_column;
    public TextView tv_game_memeber;
    public TextView tv_details_all_buy_title;
    public TextView tv_details_all_gain_title;
    public TextView tv_uuid;
    public TextView tv_rebuy_cnt;
    public TextView tv_addon_cnt;
    public View ko_head_cnt_container;
    public View ko_reward_container;
    public TextView tv_opt_user;//通过人

    public static RecordMemberViewHolder createViewHolder(Context context) {
        return new RecordMemberViewHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_record_member, null));
    }

    public RecordMemberViewHolder(View itemView) {
        super(itemView);
        record_detail_player_info_container = itemView.findViewById(R.id.record_detail_player_info_container);
        iv_game_userhead = (HeadImageView) itemView.findViewById(R.id.iv_game_userhead);
        iv_insurance_tag = (ImageView) itemView.findViewById(R.id.iv_insurance_tag);
        mRankView = (RankViewV) itemView.findViewById(R.id.mRankView);
        tv_game_earnings = (TextView) itemView.findViewById(R.id.tv_game_earnings);
        tv_game_name = (TextView) itemView.findViewById(R.id.tv_game_name);
        tv_game_buys = (TextView) itemView.findViewById(R.id.tv_game_buys);
        record_member_hunter_info = (TextView) itemView.findViewById(R.id.record_member_hunter_info);
        record_member_hunter_info_container = itemView.findViewById(R.id.record_member_hunter_info_container);
        record_member_hunter_reward = (TextView) itemView.findViewById(R.id.record_member_hunter_reward);
        ll_record_member_column = (LinearLayout) itemView.findViewById(R.id.ll_record_member_column);
        tv_game_memeber = (TextView) itemView.findViewById(R.id.tv_game_memeber);
        tv_details_all_buy_title = (TextView) itemView.findViewById(R.id.tv_details_all_buy_title);
        tv_details_all_gain_title = (TextView) itemView.findViewById(R.id.tv_details_all_gain_title);
        tv_rebuy_cnt = (TextView) itemView.findViewById(R.id.tv_rebuy_cnt);
        tv_addon_cnt = (TextView) itemView.findViewById(R.id.tv_addon_cnt);
        tv_uuid = (TextView) itemView.findViewById(R.id.tv_uuid);
        context = DemoCache.getContext();
        ko_head_cnt_container = itemView.findViewById(R.id.ko_head_cnt_container);
        ko_reward_container = itemView.findViewById(R.id.ko_reward_container);
        tv_opt_user = (TextView) itemView.findViewById(R.id.tv_opt_user);
    }

    public void refresh(GameMemberEntity mGameMemberEntity, int gameMode , boolean isInsurance , boolean isChecked ,int position, GameEntity gameEntity) {
        record_member_hunter_info_container.setVisibility(View.GONE);
        final UserEntity userInfo = mGameMemberEntity.userInfo;
        tv_uuid.setText("ID: " + mGameMemberEntity.userInfo.uuid);
        tv_uuid.setVisibility(StringUtil.isSpace(mGameMemberEntity.userInfo.uuid) || "0".equals(mGameMemberEntity.userInfo.uuid) ? View.GONE : View.VISIBLE);
        tv_game_name.setText(/*UserInfoShowHelper.getUserNickname(userInfo)*/userInfo.name);
        iv_insurance_tag.setVisibility(View.GONE);
        if (isInsurance) {
            tv_game_buys.setText("");//tv_game_buys.setText("" + mGameMemberEntity.getPremium());投保不显示了
            iv_insurance_tag.setVisibility(View.VISIBLE);
            record_detail_player_info_container.setBackgroundResource(R.drawable.common_column_bg);
        } else {
            tv_game_buys.setText("" + mGameMemberEntity.totalBuy);
            if (isChecked) {
                record_detail_player_info_container.setBackgroundColor(context.getResources().getColor(R.color.list_item_bg_press));
            } else {
                record_detail_player_info_container.setBackgroundResource(R.drawable.common_column_bg);
            }
        }
        int winChip = mGameMemberEntity.winChip + mGameMemberEntity.insurance;
        if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
            winChip = mGameMemberEntity.reward;
        } else if (isInsurance) {
            winChip = mGameMemberEntity.insurance; //- mGameMemberEntity.getPremium();投保不显示了
        }
        tv_game_buys.setVisibility(gameMode == GameConstants.GAME_MODE_NORMAL ? View.VISIBLE : View.GONE);
        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
            mRankView.setVisibility(View.GONE);
        } else if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
            mRankView.setVisibility(View.VISIBLE);
            int rank = mGameMemberEntity.ranking;
            mRankView.setRankTagView(rank);
        }
        iv_game_userhead.loadBuddyAvatarByUrl(userInfo.account, userInfo.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE);
        //自己
        tv_game_name.setTextColor(context.getResources().getColor(DemoCache.getAccount().equals(userInfo.account) ? R.color.login_solid_color : R.color.text_select_color));
        if(gameMode == GameConstants.GAME_MODE_MTT) {
            int rebuyCnt = mGameMemberEntity.rebuyCnt;
            int addonCnt = mGameMemberEntity.addonCnt;
            //增购，重购
            if (rebuyCnt > 0) {
                tv_rebuy_cnt.setVisibility(View.VISIBLE);
                tv_rebuy_cnt.setText("R" + rebuyCnt);
            } else {
                tv_rebuy_cnt.setVisibility(View.GONE);
            }
            if (addonCnt > 0) {
                tv_addon_cnt.setVisibility(View.GONE);//tv_addon_cnt.setVisibility(View.VISIBLE);
                tv_addon_cnt.setText("A");
            } else {
                tv_addon_cnt.setVisibility(View.GONE);
            }
            //猎人赛
            if (gameEntity != null && gameEntity.gameConfig instanceof GameMttConfig && ((GameMttConfig) gameEntity.gameConfig).ko_mode != 0) {
                record_member_hunter_info_container.setVisibility(View.VISIBLE);
                record_member_hunter_info.setText(mGameMemberEntity.ko_head_cnt + "/" + mGameMemberEntity.ko_head_reward);
                ko_head_cnt_container.setVisibility(((GameMttConfig) gameEntity.gameConfig).ko_mode == 1 ? View.VISIBLE : View.GONE);
                record_member_hunter_reward.setText("" + mGameMemberEntity.ko_head_reward);
                ko_reward_container.setVisibility(((GameMttConfig) gameEntity.gameConfig).ko_mode == 2 ? View.VISIBLE : View.GONE);
                winChip = winChip + mGameMemberEntity.ko_head_reward;//猎人赛的话需要加上猎头赏金
            }
        } else{
            tv_rebuy_cnt.setVisibility(View.GONE);
            tv_addon_cnt.setVisibility(View.GONE);
        }
        tv_game_earnings.setText("" + (winChip > 0 ? "+" : "") + winChip);
        tv_game_earnings.setTextColor(DemoCache.getContext().getResources().getColor(winChip >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
        tv_opt_user.setText(mGameMemberEntity.opt_user);
        //新需求，1：房主能看到所有的通过人，2：但是管理员只能看到奖励圈之内的通过人并且不能看到uuid（tips: 只有mtt有这个需求）
        if (DemoCache.getAccount().equals(gameEntity.creatorInfo.account)) {
            tv_opt_user.setVisibility(StringUtil.isSpace(mGameMemberEntity.opt_user) ? View.GONE : View.VISIBLE);
        } else if (gameMode == GameConstants.GAME_MODE_MTT) {
            int rewardList = RewardAllot.getListSize(gameEntity.checkinPlayerCount);//奖励圈的人数
            tv_opt_user.setVisibility(StringUtil.isSpace(mGameMemberEntity.opt_user) ? View.GONE : View.VISIBLE);
            if (position >= rewardList) {//mtt牌局对于管理员来说，在奖励圈之外的玩家，看不到通过人
                tv_opt_user.setVisibility(View.GONE);
            }
        }
    }
}