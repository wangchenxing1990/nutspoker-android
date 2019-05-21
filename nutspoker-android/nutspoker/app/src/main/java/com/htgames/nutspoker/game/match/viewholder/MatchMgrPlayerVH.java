package com.htgames.nutspoker.game.match.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.view.record.RankViewV;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;

/**
 * Created by 周智慧 on 17/3/1.
 */

public class MatchMgrPlayerVH extends RecyclerView.ViewHolder {
    TextView tv_club_myself;
    private HeadImageView contacts_item_head;
    public TextView btn_contact_action_agree;
    public TextView contacts_item_name;
    public TextView user_type;
    public TextView contacts_item_desc;
    public RankViewV mRankView;
    public TextView match_palyer_hunter_head;
    public TextView match_palyer_hunter_worth_reward;
    public View mtt_hunter_container;
    public TextView tv_opt_user;
    public TextView tv_opt_user_with_action;
    public static MatchMgrPlayerVH createViewHolder(Context context) {
        return new MatchMgrPlayerVH(LayoutInflater.from(context).inflate(R.layout.view_swipe_content, null), context);
    }

    public MatchMgrPlayerVH(View itemView, Context context) {
        super(itemView);
        itemView.findViewById(R.id.swipe_content_divider).setVisibility(View.VISIBLE);
        tv_club_myself = (TextView) itemView.findViewById(R.id.tv_club_myself);
        contacts_item_head = (HeadImageView) itemView.findViewById(R.id.contacts_item_head);
        btn_contact_action_agree = (TextView) itemView.findViewById(R.id.btn_contact_action_agree);
        contacts_item_name = (TextView) itemView.findViewById(R.id.contacts_item_name);
        user_type = (TextView) itemView.findViewById(R.id.user_type);
        contacts_item_desc = (TextView) itemView.findViewById(R.id.contacts_item_desc);
        mRankView = (RankViewV) itemView.findViewById(R.id.mRankView);
        mtt_hunter_container = itemView.findViewById(R.id.mtt_hunter_container);
        match_palyer_hunter_head = (TextView) itemView.findViewById(R.id.match_palyer_hunter_head);
        match_palyer_hunter_worth_reward = (TextView) itemView.findViewById(R.id.match_palyer_hunter_worth_reward);
        tv_opt_user = (TextView) itemView.findViewById(R.id.tv_opt_user);
        tv_opt_user_with_action = (TextView) itemView.findViewById(R.id.tv_opt_user_with_action);
    }

    public void bind(GameEntity gameInfo, final MatchPlayerEntity bean, int gameStatus, boolean canHandle) {
//            tv_club_myself.setVisibility(bean.uid.equals(DemoCache.getAccount()) ? View.VISIBLE : View.GONE);
        contacts_item_head.loadBuddyAvatar(bean.uid);
        if (StringUtil.isSpace(bean.nickname)) {
            bean.nickname = NimUserInfoCache.getInstance().getUserDisplayName(bean.uid);
        }
        String opt_user = StringUtil.isSpace(bean.opt_user_real) ? bean.opt_user : bean.opt_user_real;//opt_user_real有的话就用，没有的话使用opt_user
        contacts_item_name.setText(bean.nickname);
        user_type.setText("R" + bean.rebuyCnt);
        user_type.setVisibility(bean.rebuyCnt <= 0 ? View.GONE : View.VISIBLE);
        contacts_item_desc.setText("ID: " + bean.uuid);
        contacts_item_desc.setVisibility(StringUtil.isSpace(bean.uuid) ? View.GONE : View.VISIBLE);
        btn_contact_action_agree.setText("移除");
        btn_contact_action_agree.setVisibility(gameStatus == GameStatus.GAME_STATUS_WAIT && canHandle ? View.VISIBLE : View.GONE);//这个页面只负责看信息，没有操作的功能
        tv_opt_user_with_action.setVisibility(gameStatus == GameStatus.GAME_STATUS_WAIT && canHandle && !StringUtil.isSpace(opt_user) ? View.VISIBLE : View.GONE);//这个页面只负责看信息，没有操作的功能
        tv_opt_user_with_action.setText(opt_user);
        mRankView.setRankTagView(bean.rank);
        if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
            mRankView.setVisibility(View.GONE);
        }
        tv_club_myself.setVisibility(gameStatus == GameStatus.GAME_STATUS_START || !canHandle ? View.VISIBLE : View.GONE);
        tv_club_myself.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        if (bean.chips <= 0) {
            tv_club_myself.setText(R.string.match_player_status_out);
        } else {
            tv_club_myself.setText("" + bean.chips);
        }
        //猎人赛相关
        if (gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig) {//玩家列表是否显示猎人赛标记
            mtt_hunter_container.setVisibility(gameStatus == GameStatus.GAME_STATUS_WAIT ? View.GONE : (((GameMttConfig) gameInfo.gameConfig).ko_mode == 0 ? View.GONE : View.VISIBLE));
            match_palyer_hunter_head.setVisibility(((GameMttConfig) gameInfo.gameConfig).ko_mode == 1 ? View.VISIBLE : View.GONE);//普通猎人赛只显示猎头数
            match_palyer_hunter_worth_reward.setVisibility(((GameMttConfig) gameInfo.gameConfig).ko_mode == 2 ? View.VISIBLE : View.GONE);//超级猎人赛只显示"赏金"
            if (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL) {
                match_palyer_hunter_head.setText("" + bean.ko_head_cnt + "/" + bean.ko_head_reward);//猎头/赏金
                match_palyer_hunter_worth_reward.setText("" + bean.ko_worth + "/" + bean.ko_head_reward);//身价/赏金
            } else {
                match_palyer_hunter_head.setText("" + bean.ko_head_cnt/* + "/" + matchPlayerEntity.ko_head_reward*/);//猎头
                match_palyer_hunter_worth_reward.setText("" + /*matchPlayerEntity.ko_worth + "/" + */bean.ko_head_reward);//赏金
            }
        }
        //报名批准人
        tv_opt_user.setText(opt_user);
        if (tv_opt_user_with_action.getVisibility() == View.VISIBLE) {
            tv_opt_user.setVisibility(View.GONE);
        } else {
            tv_opt_user.setVisibility(StringUtil.isSpace(opt_user) ? View.GONE : View.VISIBLE);//通过人为空的话不显示gone掉
        }
    }
}
