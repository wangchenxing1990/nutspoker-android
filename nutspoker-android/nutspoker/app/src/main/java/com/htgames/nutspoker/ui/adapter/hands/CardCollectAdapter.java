package com.htgames.nutspoker.ui.adapter.hands;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.PaipuMoreView;
import com.htgames.nutspoker.view.hands.HandCardView;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.bean.NetCardCollectBaseEy;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import static com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsHistory;

/**
 * Created by glp on 2016/8/7.
 */
public class CardCollectAdapter extends BaseAdapter {
    int VIEW_TYPE_TEXAS_OMAHA = 0;//德州和奥马哈的layout
    int VIEW_TYPE_PINEAPPLE = 1;//大菠萝的layout
    PaipuMoreView mHandCardMoreView;
    boolean mIsShowMore = true;
    Context mContext;
    List<NetCardCollectBaseEy> mNetCardCollectEys;
    View mParentLayout;
    GradientDrawable bg_fantasy;

    public CardCollectAdapter(Context context, View view, List<NetCardCollectBaseEy> list, PaipuMoreView.OnMoreListener onMoreListener , PaipuMoreView.OnCallOverback onCallOverback) {
        mHandCardMoreView = new PaipuMoreView(context , CircleConstant.TYPE_PAIPU);
        mHandCardMoreView.setOnMoreListener(onMoreListener);
        mHandCardMoreView.setOnCallOverback(onCallOverback);
        mContext = context;
        mNetCardCollectEys = list;
        mParentLayout = view;
        bg_fantasy = new GradientDrawable();
        bg_fantasy.setCornerRadius(ScreenUtil.dp2px(context, 5));
        bg_fantasy.setColor(context.getResources().getColor(R.color.match_checkin_normal));
    }

    public void setIsShowMore(boolean isShowMore) {
        this.mIsShowMore = isShowMore;
    }

    @Override
    public int getCount() {
        return mNetCardCollectEys == null ? 0 : mNetCardCollectEys.size();
    }

    @Override
    public Object getItem(int position) {
        return mNetCardCollectEys == null ? null : mNetCardCollectEys.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        NetCardCollectBaseEy data = (NetCardCollectBaseEy) getItem(position);
        if (data.play_mode == GameConstants.PLAY_MODE_TEXAS_HOLDEM || data.play_mode == GameConstants.PLAY_MODE_OMAHA) {
            return VIEW_TYPE_TEXAS_OMAHA;
        } else if (data.play_mode == GameConstants.PLAY_MODE_PINEAPPLE) {
            return VIEW_TYPE_PINEAPPLE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        int view_type = getItemViewType(position);
        if (view_type == VIEW_TYPE_TEXAS_OMAHA) {
            view = getTexasOmahaView(position, convertView, parent);
        } else if (view_type == VIEW_TYPE_PINEAPPLE) {
            view = getPineappleView(position, convertView, parent);
        }
        return view;
    }

    private View getTexasOmahaView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_paipu_collect_item, null, false);
            holder.tv_game_create_time = (TextView) convertView.findViewById(R.id.tv_game_create_time);
            holder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
            holder.tv_game_blind = (TextView) convertView.findViewById(R.id.tv_game_blind);
            holder.tv_game_earnings = (TextView) convertView.findViewById(R.id.tv_game_earnings);
            holder.tv_paipu_cardtype = (TextView) convertView.findViewById(R.id.tv_paipu_cardtype);
            holder.mCardTypeView = (HandCardView) convertView.findViewById(R.id.mCardTypeView);
            holder.mHandCardView = (HandCardView) convertView.findViewById(R.id.mHandCardView);
            holder.paipu_sparkbutton = (SparkButton) convertView.findViewById(R.id.paipu_sparkbutton);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        NetCardCollectBaseEy data = (NetCardCollectBaseEy) getItem(position);
        holder.tv_game_create_time.setText(DateTools.getDailyTime_ymd(data.collect_time));
//        WealthHelper.SetMoneyText(holder.tv_game_earnings, data.win_chips, mContext);
        int winValue = data.win_chips;
        holder.tv_game_earnings.setText(winValue > 0 ? "+" + WealthHelper.getWealthShow(winValue) : "" + WealthHelper.getWealthShow(winValue));
        holder.tv_game_earnings.setTextColor(ContextCompat.getColor(mContext, winValue >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
        holder.paipu_sparkbutton.setVisibility(mIsShowMore ? View.VISIBLE : View.GONE);
        holder.paipu_sparkbutton.setOnClickListener(new OnMoreClick(position));
        holder.paipu_sparkbutton.setChecked(true);
        NetCardCollectBaseEy newData = (NetCardCollectBaseEy)data;
        if(newData.pool_cards == null){
            holder.mCardTypeView.setHandCard(null);
        } else {
            List<Integer> tmpList = new ArrayList<>();
            List<List<Integer>> ddList = newData.pool_cards;
            for(List<Integer> list : ddList){
                tmpList.addAll(list);
            }
            holder.mCardTypeView.setHandCard(tmpList);
        }
//        }
        holder.mHandCardView.setHandCard(data.hand_cards);
        //牌型
        holder.tv_paipu_cardtype.setText(PaipuConstants.getCardCategoriesDesc(mContext, data.card_type));
        holder.tv_paipu_cardtype.setTextColor(PaipuConstants.getCardTypeTextColor(mContext, data.card_type));
        holder.tv_game_name.setText(data.name);
        if (data.game_mode == GameConstants.GAME_MODE_NORMAL) {
            //普通模式
            holder.tv_game_blind.setText(GameConstants.getGameBlindsShow(data.sblinds));
        } else if (data.game_mode == GameConstants.GAME_MODE_SNG) {
            //SNG模式
            holder.tv_game_blind.setText("" + data.match_chips);
        } else if (data.game_mode == GameConstants.GAME_MODE_MTT) {
            //MTT模式
            holder.tv_game_blind.setText("" + data.match_chips);
        } else if (data.game_mode == GameConstants.GAME_MODE_MT_SNG) {
            //MT-SNG模式
            holder.tv_game_blind.setText("" + data.match_chips);
        }
        return convertView;
    }

    private View getPineappleView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_paipu_record_item_new, parent, false);
            holder.tv_game_create_time = (TextView) convertView.findViewById(R.id.tv_game_date);
            holder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
            holder.tv_game_earnings = (TextView) convertView.findViewById(R.id.tv_game_earnings);
            holder.tv_handtype = (TextView) convertView.findViewById(R.id.tv_handtype);
            holder.paipu_sparkbutton = (SparkButton) convertView.findViewById(R.id.paipu_sparkbutton);
            holder.iv_pineapple_icon = (ImageView) convertView.findViewById(R.id.iv_pineapple_icon);
            holder.tv_fantasy = (TextView) convertView.findViewById(R.id.tv_fantasy);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        setPineappleView(position, holder);
        return convertView;
    }

    private void setPineappleView(int position, ViewHolder holder) {
        NetCardCollectBaseEy data = (NetCardCollectBaseEy) getItem(position);
        holder.tv_game_create_time.setText(DateTools.getDailyTime_ymd(data.collect_time));
        //010204  从左到右依次是 头道、中道、尾道
        int card_type_tail = data.card_type % 100;
        int card_type_middle = (data.card_type - card_type_tail) / 100 % 100;
        int card_type_head = (data.card_type - card_type_tail - card_type_middle * 100) / 10000 % 100;
        String head = PaipuConstants.getCardCategoriesDesc(mContext , card_type_head);
        String middle = PaipuConstants.getCardCategoriesDesc(mContext , card_type_middle);
        String tail = PaipuConstants.getCardCategoriesDesc(mContext , card_type_tail);
        if (data.card_type == 0) {//爆牌
            holder.tv_handtype.setText("爆牌");
            holder.tv_handtype.setTextColor(mContext.getResources().getColor(R.color.shop_text_no_select_color));
        } else {
            holder.tv_handtype.setText(head + " / " + middle + " / " + tail);
            holder.tv_handtype.setTextColor(mContext.getResources().getColor(R.color.login_solid_color));
        }
        holder.iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[data.play_type]);
        holder.tv_fantasy.setBackgroundDrawable(bg_fantasy);
        holder.tv_fantasy.setVisibility(data.fantasy ? View.VISIBLE : View.GONE);
        holder.paipu_sparkbutton.setVisibility(mIsShowMore ? View.VISIBLE : View.GONE);
        holder.paipu_sparkbutton.setOnClickListener(new OnMoreClick(position));
        holder.paipu_sparkbutton.setChecked(true);
        int winValue = data.win_chips;
        holder.tv_game_earnings.setText(winValue > 0 ? "+" + WealthHelper.getWealthShow(winValue) : "" + WealthHelper.getWealthShow(winValue));
        holder.tv_game_earnings.setTextColor(ContextCompat.getColor(mContext, winValue >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));
        holder.tv_game_name.setText(data.name);
    }

    //更多点击按钮
    class OnMoreClick implements View.OnClickListener {
        int position;
        public OnMoreClick(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            AnimUtil.jump(v);//点击跳动一下
            NetCardCollectBaseEy data = (NetCardCollectBaseEy) getItem(position);
            if (mHandCardMoreView.mOnMoreListener != null) {
                mHandCardMoreView.mOnMoreListener.onCollect(position);
            }
        }
    }

    static class ViewHolder {

        TextView tv_game_create_time;
        TextView tv_game_earnings;
        TextView tv_game_name;
        //TextView tv_game_member;
        TextView tv_game_blind;
        TextView tv_paipu_cardtype;
        HandCardView mCardTypeView;
        HandCardView mHandCardView;
        SparkButton paipu_sparkbutton;
        //下面的是大菠萝独有的ui
        ImageView iv_pineapple_icon;
        TextView tv_fantasy;
        TextView tv_handtype;
    }
}
