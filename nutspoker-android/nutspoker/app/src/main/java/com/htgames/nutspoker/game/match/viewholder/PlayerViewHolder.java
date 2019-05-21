package com.htgames.nutspoker.game.match.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.view.record.RankViewV;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;

/**
 */
public class PlayerViewHolder extends RecyclerView.ViewHolder {
    public RelativeLayout rl_match_player_item;
//    public TextView tv_record_sng_rank_tag;
    public TextView tv_nickname;
    public TextView tv_chips;
    public TextView tv_table_no;
    public TextView tv_rebuy_cnt;
    public TextView tv_addon_cnt;
    public TextView match_palyer_hunter_head;
    public View mtt_hunter_container;
    public TextView match_palyer_hunter_worth_reward;
    RankViewV mRankView;
    public TextView mtt_player_contacts_item_desc;//uuid
    public TextView tv_opt_user;//是谁批准报名的
    public Drawable drawable_opt_personal;
    public Drawable drawable_opt_club;
    public static PlayerViewHolder createViewHolder(Context context) {
        return new PlayerViewHolder(LayoutInflater.from(context).inflate(R.layout.list_match_player_item, null), context);
    }

    public PlayerViewHolder(View itemView, Context context) {
        super(itemView);
        rl_match_player_item = (RelativeLayout) itemView.findViewById(R.id.rl_match_player_item);
        mRankView = (RankViewV) itemView.findViewById(R.id.mRankView);
        tv_nickname = (TextView) itemView.findViewById(R.id.tv_nickname);
        tv_chips = (TextView) itemView.findViewById(R.id.tv_chips);
        tv_table_no = (TextView) itemView.findViewById(R.id.tv_table_no);
        tv_rebuy_cnt = (TextView) itemView.findViewById(R.id.tv_rebuy_cnt);
        tv_addon_cnt = (TextView) itemView.findViewById(R.id.tv_addon_cnt);
        mtt_hunter_container = itemView.findViewById(R.id.mtt_hunter_container);
        match_palyer_hunter_head = (TextView) itemView.findViewById(R.id.match_palyer_hunter_head);
        match_palyer_hunter_worth_reward = (TextView) itemView.findViewById(R.id.match_palyer_hunter_worth_reward);
        mtt_player_contacts_item_desc = (TextView) itemView.findViewById(R.id.mtt_player_contacts_item_desc);
        tv_opt_user = (TextView) itemView.findViewById(R.id.tv_opt_user);
        drawable_opt_personal = context.getResources().getDrawable(R.mipmap.approve_icon);
        drawable_opt_personal.setBounds(0, 0, drawable_opt_personal.getIntrinsicWidth(), drawable_opt_personal.getIntrinsicHeight());
        drawable_opt_club = context.getResources().getDrawable(R.mipmap.approve_icon);
        drawable_opt_club.setBounds(0, 0, drawable_opt_club.getIntrinsicWidth(), drawable_opt_club.getIntrinsicHeight());
    }

    public void refresh(GameEntity gameInfo, MatchPlayerEntity matchPlayerEntity, int gameStatus, int position, int rank_type) {
        if (matchPlayerEntity == null) {
            return;
        }
        boolean showMe = position == 0 && gameStatus != GameStatus.GAME_STATUS_WAIT && matchPlayerEntity.uid.equals(DemoCache.getAccount());
        //比赛开始后第一行显示"我的排名"
        String playerName = matchPlayerEntity.nickname;//NimUserInfoCache.getInstance().getUserDisplayName(matchPlayerEntity.uid);//用黄老师返回的昵称，保持和ios一致
        if (StringUtil.isSpace(playerName)) {
            playerName = NimUserInfoCache.getInstance().getUserDisplayName(matchPlayerEntity.uid);
        }
        if (showMe) {
            tv_nickname.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.login_solid_color));
            tv_nickname.setText("我的排名  " + matchPlayerEntity.rank);
            tv_chips.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.login_solid_color));
            mRankView.setVisibility(View.GONE);
        } else {
            tv_nickname.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.text_select_color));
            tv_chips.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.text_select_color));
            tv_nickname.setText(TextUtils.isEmpty(playerName) ? DemoCache.getContext().getResources().getString(R.string.user) : playerName);
        }
        if (matchPlayerEntity.tableNo != 0) {
            tv_table_no.setText(DemoCache.getContext().getString(R.string.game_table_no, "" + matchPlayerEntity.tableNo));
//            tv_table_no.setVisibility(View.VISIBLE);// TODO: 16/12/19 牌桌名字需求暂时不要
        } else {
            tv_table_no.setVisibility(View.GONE);
        }
        mtt_player_contacts_item_desc.setText("ID：" + matchPlayerEntity.uuid);
        mtt_player_contacts_item_desc.setVisibility(StringUtil.isSpace(matchPlayerEntity.uuid) || "0".equals(matchPlayerEntity.uuid) || "false".equals(matchPlayerEntity.uuid) ? View.GONE : View.VISIBLE);
        tv_opt_user.setText(matchPlayerEntity.opt_user);
        tv_opt_user.setVisibility(StringUtil.isSpace(matchPlayerEntity.opt_user) ? View.GONE : View.VISIBLE);
        tv_opt_user.setCompoundDrawables(matchPlayerEntity.opt_user_type == 0 ? drawable_opt_personal : drawable_opt_club, null, null, null);
        boolean isOut = false;
        if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
            mRankView.setVisibility(View.GONE);
            tv_chips.setText("" + matchPlayerEntity.chips);
        } else {
            if (!showMe) {
                if (rank_type == 1) {//只有名次排序显示奖杯图片
                    mRankView.setRankTagView(matchPlayerEntity.rank);
                } else {
                    mRankView.setOnlyNum(matchPlayerEntity.rank);
                }
            }
            if (matchPlayerEntity.chips <= 0) {
                isOut = true;
                tv_chips.setText(R.string.match_player_status_out);
            } else {
                tv_chips.setText("" + matchPlayerEntity.chips);
            }
        }
        if (matchPlayerEntity.uid.equals(DemoCache.getAccount()) && position == 0) {
//            tv_nickname.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_divider_color));
//            tv_chips.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_divider_color));
            tv_table_no.setTextColor(DemoCache.getContext().getResources().getColor(R.color.record_item_divider_color));
        } else {
            if (isOut) {
//                tv_nickname.setTextColor(DemoCache.getContext().getResources().getColor(R.color.match_out_text_color));
//                tv_chips.setTextColor(DemoCache.getContext().getResources().getColor(R.color.match_out_text_color));
                tv_table_no.setTextColor(DemoCache.getContext().getResources().getColor(R.color.match_out_text_color));
            } else {
//                tv_nickname.setTextColor(DemoCache.getContext().getResources().getColor(R.color.white_main_text_color));
//                tv_chips.setTextColor(DemoCache.getContext().getResources().getColor(R.color.white_main_text_color));
                tv_table_no.setTextColor(DemoCache.getContext().getResources().getColor(R.color.white_main_text_color));
            }
        }
        int rebuyCnt = matchPlayerEntity.rebuyCnt;
        int addonCnt = matchPlayerEntity.addonCnt;
        //增购，重购
        if (rebuyCnt > 0) {
            tv_rebuy_cnt.setVisibility(View.VISIBLE);
            tv_rebuy_cnt.setText("R" + rebuyCnt);
        } else {
            tv_rebuy_cnt.setVisibility(View.GONE);
        }
        if (addonCnt > 0) {
            tv_addon_cnt.setVisibility(View.VISIBLE);
            tv_addon_cnt.setText("A");
        } else {
            tv_addon_cnt.setVisibility(View.GONE);
        }
        if (gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig) {//玩家列表是否显示猎人赛标记
            mtt_hunter_container.setVisibility(gameStatus == GameStatus.GAME_STATUS_WAIT ? View.GONE : (((GameMttConfig) gameInfo.gameConfig).ko_mode == 0 ? View.GONE : View.VISIBLE));
            match_palyer_hunter_head.setVisibility(((GameMttConfig) gameInfo.gameConfig).ko_mode == 1 ? View.VISIBLE : View.GONE);//普通猎人赛只显示猎头数
            match_palyer_hunter_worth_reward.setVisibility(((GameMttConfig) gameInfo.gameConfig).ko_mode == 2 ? View.VISIBLE : View.GONE);//超级猎人赛只显示"赏金"
            if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                match_palyer_hunter_head.setText("" + matchPlayerEntity.ko_head_cnt + "/" + matchPlayerEntity.ko_head_reward);//猎头/赏金
                match_palyer_hunter_worth_reward.setText("" + matchPlayerEntity.ko_worth + "/" + matchPlayerEntity.ko_head_reward);//身价/赏金
            } else {
                match_palyer_hunter_head.setText("" + matchPlayerEntity.ko_head_cnt/* + "/" + matchPlayerEntity.ko_head_reward*/);//猎头
                match_palyer_hunter_worth_reward.setText("" + /*matchPlayerEntity.ko_worth + "/" + */matchPlayerEntity.ko_head_reward);//赏金
            }
        }
    }
}