package com.htgames.nutspoker.game.match.viewholder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.record.RankViewV;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.common.util.string.StringUtil;

/**
 * Created by 周智慧 on 17/3/12.
 */

public class NormalMgrPlayerVH extends RecyclerView.ViewHolder {
    TextView tv_club_myself;
    private HeadImageView contacts_item_head;
    public TextView btn_contact_action_agree;
    public TextView contacts_item_name;
    public TextView user_type;
    public TextView contacts_item_desc;
    public RankViewV mRankView;
    public TextView match_palyer_hunter_head;
    public View mtt_hunter_container;
    public TextView tv_opt_user;
    public TextView match_palyer_hunter_worth_reward;
    public static NormalMgrPlayerVH createViewHolder(Context context) {
        return new NormalMgrPlayerVH(LayoutInflater.from(context).inflate(R.layout.view_swipe_content, null), context);
    }

    public NormalMgrPlayerVH(View itemView, Context context) {
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
        tv_opt_user = (TextView) itemView.findViewById(R.id.tv_opt_user);
        match_palyer_hunter_worth_reward = (TextView) itemView.findViewById(R.id.match_palyer_hunter_worth_reward);
    }

    public void bind(GameEntity gameInfo, final MatchPlayerEntity bean, int gameStatus, boolean isAllPlayer) {
//            tv_club_myself.setVisibility(bean.uid.equals(DemoCache.getAccount()) ? View.VISIBLE : View.GONE);
        contacts_item_head.loadBuddyAvatar(bean.uid);
        if (StringUtil.isSpace(bean.nickname)) {
            bean.nickname = NimUserInfoCache.getInstance().getUserDisplayName(bean.uid);
        }
        contacts_item_name.setText(bean.nickname);
        contacts_item_desc.setText("ID: " + bean.uuid);
        contacts_item_desc.setVisibility(StringUtil.isSpace(bean.uuid) ? View.GONE : View.VISIBLE);
        tv_club_myself.setVisibility(isAllPlayer ? View.GONE : gameStatus == GameStatus.GAME_STATUS_WAIT ? View.GONE : View.VISIBLE);
        tv_club_myself.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        WealthHelper.SetMoneyText(tv_club_myself, bean.reward, itemView.getContext());
        //猎人赛相关
        mtt_hunter_container.setVisibility(isAllPlayer ? View.GONE : gameStatus == GameStatus.GAME_STATUS_WAIT ? View.GONE : View.VISIBLE);
        match_palyer_hunter_head.setVisibility(View.GONE);///普通猎人赛显示
        Drawable leftDrawable = itemView.getContext().getResources().getDrawable(R.mipmap.icon_club_chat_chip);
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        match_palyer_hunter_worth_reward.setVisibility(isAllPlayer ? View.GONE : View.VISIBLE);//超级猎人赛显示
        match_palyer_hunter_worth_reward.setText(bean.total_buy + "");
        match_palyer_hunter_worth_reward.setCompoundDrawables(leftDrawable, null, null, null);
        //报名批准人
        tv_opt_user.setText(bean.opt_user);
        tv_opt_user.setVisibility(StringUtil.isSpace(bean.opt_user) ? View.GONE : View.VISIBLE);
    }
}
