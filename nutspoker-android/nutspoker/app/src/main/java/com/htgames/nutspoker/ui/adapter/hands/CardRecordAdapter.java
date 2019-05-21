package com.htgames.nutspoker.ui.adapter.hands;

import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.CardGamesEy;
import com.netease.nim.uikit.bean.NetCardRecordEy;
import com.htgames.nutspoker.data.DataManager;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.data.common.PaipuConstants;
import com.netease.nim.uikit.common.DateTools;
import com.htgames.nutspoker.ui.activity.Hands.PaipuRecordActivity;
import com.htgames.nutspoker.ui.helper.WealthHelper;
import com.htgames.nutspoker.view.PaipuMoreView;
import com.htgames.nutspoker.view.hands.HandCardView;
import com.netease.nim.uikit.AnimUtil;
import com.netease.nim.uikit.sparkbutton.SparkButton;

import java.util.ArrayList;
import java.util.List;

import static com.htgames.nutspoker.config.GameConfigData.pineappleIconIdsHistory;

/**
 * Created by glp on 2016/8/7.
 */
public class CardRecordAdapter extends BaseAdapter implements DataManager.OnDataFinish{
    int VIEW_TYPE_TEXAS_OMAHA = 0;//德州和奥马哈的layout
    int VIEW_TYPE_PINEAPPLE = 1;//大菠萝的layout
    PaipuMoreView mHandCardMoreView;
    boolean mIsShowMore = true;
    PaipuRecordActivity mActivity;
    List<NetCardRecordEy> mListNetCardRecord;
    View mParentLayout;
    GradientDrawable bg_fantasy;

    public CardRecordAdapter(PaipuRecordActivity activity, View view, List<NetCardRecordEy> list, PaipuMoreView.OnMoreListener onMoreListener,PaipuMoreView.OnCallOverback onCallOverback){
        mActivity = activity;
        mListNetCardRecord = list;

        mHandCardMoreView = new PaipuMoreView(activity , CircleConstant.TYPE_PAIPU);
        mHandCardMoreView.setOnMoreListener(onMoreListener);
        mHandCardMoreView.setOnCallOverback(onCallOverback);
        mParentLayout = view;
        bg_fantasy = new GradientDrawable();
        bg_fantasy.setCornerRadius(ScreenUtil.dp2px(mActivity, 5));
        bg_fantasy.setColor(mActivity.getResources().getColor(R.color.match_checkin_normal));
    }

    public void setData(List<NetCardRecordEy> list) {
        mListNetCardRecord = list;
        notifyDataSetChanged();
    }

    public void setIsShowMore(boolean isShowMore) {
        this.mIsShowMore = isShowMore;
    }

    @Override
    public int getCount() {
        return mListNetCardRecord==null?0:mListNetCardRecord.size();
    }

    @Override
    public Object getItem(int position) {
        return mListNetCardRecord==null ? null : mListNetCardRecord.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        NetCardRecordEy data = (NetCardRecordEy) getItem(position);
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

    private View getTexasOmahaView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_paipu_record_item, parent, false);
            holder.rl_game_info = (RelativeLayout) convertView.findViewById(R.id.rl_game_info);
            holder.ll_record_match = (LinearLayout) convertView.findViewById(R.id.ll_record_match);
            holder.tv_game_match_player = (TextView) convertView.findViewById(R.id.tv_game_match_player);
            holder.tv_game_match_checkin_fee = (TextView) convertView.findViewById(R.id.tv_game_match_checkin_fee);
            //
            holder.tv_game_date = (TextView) convertView.findViewById(R.id.tv_game_date);
//            holder.tv_record_no = (TextView) view.findViewById(R.id.tv_record_no);
//            holder.tv_game_hand_count = (TextView) view.findViewById(R.id.tv_game_hand_count);
//            holder.tv_game_create_time = (TextView) view.findViewById(R.id.tv_game_create_time);
            holder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
            holder.tv_game_blind = (TextView) convertView.findViewById(R.id.tv_game_blind);
            holder.mCardTypeView = (HandCardView) convertView.findViewById(R.id.mCardTypeView);
            holder.tv_game_earnings = (TextView) convertView.findViewById(R.id.tv_game_earnings);
            holder.tv_handtype = (TextView) convertView.findViewById(R.id.tv_handtype);
            holder.paipu_sparkbutton = (SparkButton) convertView.findViewById(R.id.paipu_sparkbutton);
//            holder.view_divider_header = (View) view.findViewById(R.id.view_divider_header);
//            holder.view_divider_footer = (View) view.findViewById(R.id.view_divider_footer);
            holder.view_record_divider = (View) convertView.findViewById(R.id.view_record_divider);
            holder.mHandCardView = (HandCardView) convertView.findViewById(R.id.mHandCardView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        setView(position, holder);
        return convertView;
    }

    private View getPineappleView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.list_paipu_record_item_new, parent, false);
            holder.rl_game_info = (RelativeLayout) convertView.findViewById(R.id.rl_game_info);
            holder.tv_game_date = (TextView) convertView.findViewById(R.id.tv_game_date);
            holder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
            holder.tv_game_earnings = (TextView) convertView.findViewById(R.id.tv_game_earnings);
            holder.tv_handtype = (TextView) convertView.findViewById(R.id.tv_handtype);
            holder.paipu_sparkbutton = (SparkButton) convertView.findViewById(R.id.paipu_sparkbutton);
            holder.view_record_divider = (View) convertView.findViewById(R.id.view_record_divider);
            holder.iv_pineapple_icon = (ImageView) convertView.findViewById(R.id.iv_pineapple_icon);
            holder.tv_fantasy = (TextView) convertView.findViewById(R.id.tv_fantasy);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        setPineappleView(position, holder);
        return convertView;
    }

    private void setView(int position, ViewHolder holder) {
        setTime(position, holder);
        NetCardRecordEy data = (NetCardRecordEy) getItem(position);
        holder.mHandCardView.setHandCard(data.hand_cards);
        //牌型
        holder.tv_handtype.setText(PaipuConstants.getCardCategoriesDesc(mActivity , data.card_type));
        holder.tv_handtype.setTextColor(PaipuConstants.getCardTypeTextColor(mActivity, data.card_type));
//        holder.tv_handtype.setSelected(true);
//        holder.tv_handtype.requestFocus();

        CardGamesEy gameData = DataManager.getInstance().getCardGamesEy(data.gid,mActivity.mHandCollectAction,this);
        if(gameData != null){
            holder.tv_game_name.setText(gameData.name);
            holder.tv_game_date.setText(DateTools.getOtherStrTime_md_hm(gameData.create_time));
            if (gameData.game_mode == GameConstants.GAME_MODE_NORMAL) {
                //普通模式
                holder.ll_record_match.setVisibility(View.GONE);
//                holder.tv_game_blind.setVisibility(View.VISIBLE);功能去掉
//                holder.tv_game_blind.setText(GameConstants.getGameBlindsShow(gameData.sblinds));功能去掉
            } else if (gameData.game_mode == GameConstants.GAME_MODE_SNG) {
//                holder.ll_record_match.setVisibility(View.VISIBLE);功能去掉
                //SNG模式
                holder.tv_game_match_player.setText(""+gameData.match_player);
//                holder.tv_game_blind.setVisibility(View.GONE);功能去掉
//                holder.tv_game_blind.setText(""+gameData.match_chips);功能去掉
                holder.tv_game_match_checkin_fee.setText(String.valueOf(gameData.match_checkin_fee));
            } else if (gameData.game_mode == GameConstants.GAME_MODE_MTT) {
                //MTT模式
//                holder.ll_record_match.setVisibility(View.VISIBLE);功能去掉
//                holder.tv_game_blind.setVisibility(View.GONE);功能去掉
                holder.tv_game_match_player.setText(""+gameData.match_player);
                holder.tv_game_match_checkin_fee.setText(String.valueOf(gameData.match_checkin_fee));
            } else if (gameData.game_mode == GameConstants.GAME_MODE_MT_SNG) {
                //MT-SNG模式
//                holder.ll_record_match.setVisibility(View.VISIBLE);功能去掉
//                holder.tv_game_blind.setVisibility(View.GONE);功能去掉
                holder.tv_game_match_player.setText(""+gameData.match_player);
                holder.tv_game_match_checkin_fee.setText(String.valueOf(gameData.match_checkin_fee));
            }
        }
        if(data.pool_cards == null){
            holder.mCardTypeView.setHandCard(null);
        } else {
            List<Integer> tmpList = new ArrayList<>();
            List<List<Integer>> ddList = data.pool_cards;
            for(List<Integer> list : ddList){
                tmpList.addAll(list);
            }
            holder.mCardTypeView.setHandCard(tmpList);
        }
    }

    private void setPineappleView(int position, ViewHolder holder) {
        setTime(position, holder);
        NetCardRecordEy data = (NetCardRecordEy) getItem(position);
        //010204  从左到右依次是 头道、中道、尾道
        int card_type_tail = data.card_type % 100;
        int card_type_middle = (data.card_type - card_type_tail) / 100 % 100;
        int card_type_head = (data.card_type - card_type_tail - card_type_middle * 100) / 10000 % 100;
        String head = PaipuConstants.getCardCategoriesDesc(mActivity , card_type_head);
        String middle = PaipuConstants.getCardCategoriesDesc(mActivity , card_type_middle);
        String tail = PaipuConstants.getCardCategoriesDesc(mActivity , card_type_tail);
        if (data.card_type == 0) {//爆牌
            holder.tv_handtype.setText("爆牌");
            holder.tv_handtype.setTextColor(mActivity.getResources().getColor(R.color.shop_text_no_select_color));
        } else {
            holder.tv_handtype.setText(head + " / " + middle + " / " + tail);
            holder.tv_handtype.setTextColor(mActivity.getResources().getColor(R.color.login_solid_color));
        }
        holder.iv_pineapple_icon.setImageResource(pineappleIconIdsHistory[data.play_type]);
        holder.tv_fantasy.setBackgroundDrawable(bg_fantasy);
        holder.tv_fantasy.setVisibility(data.fantasy ? View.VISIBLE : View.GONE);
        CardGamesEy gameData = DataManager.getInstance().getCardGamesEy(data.gid,mActivity.mHandCollectAction, this);
        if(gameData != null) {
            holder.tv_game_name.setText(gameData.name);
            holder.tv_game_date.setText(DateTools.getOtherStrTime_md_hm(gameData.create_time));
        }
    }

    private void setTime(int position, ViewHolder holder) {
        NetCardRecordEy data = (NetCardRecordEy) getItem(position);
        NetCardRecordEy dataPre = null;
        NetCardRecordEy dataNext = null;
        if(position > 0)
            dataPre = (NetCardRecordEy) getItem(position-1);
        if(position < getCount()-1){
            dataNext = (NetCardRecordEy) getItem(position+1);
        }
        //如果不为空，并且是同属于一个牌局
        if(dataPre != null && dataPre.gid.equals(data.gid)){
            holder.rl_game_info.setVisibility(View.GONE);
//            holder.tv_game_hand_count.setVisibility(View.GONE);
//            holder.view_divider_header.setVisibility(View.VISIBLE);
        } else {
            holder.rl_game_info.setVisibility(View.VISIBLE);
//            holder.tv_game_hand_count.setVisibility(View.VISIBLE);
//            holder.view_divider_header.setVisibility(View.GONE);
        }

        //holder.tv_game_hand_count.setText(mActivity.getString(R.string.hands_count, data.hands_cnt));
        //holder.tv_record_no.setText(String.valueOf(data.hands_cnt));
        //如果下一手也是同一局
        if(dataNext!=null && dataNext.gid.equals(data.gid)){
            holder.view_record_divider.setVisibility(View.VISIBLE);
//            holder.view_divider_footer.setVisibility(View.VISIBLE);
        } else {
            holder.view_record_divider.setVisibility(View.GONE);
//            holder.view_divider_footer.setVisibility(View.GONE);
        }

//        WealthHelper.SetMoneyText(holder.tv_game_earnings,data.win_chips,mActivity);
        int winValue = data.win_chips;
        holder.tv_game_earnings.setText(winValue > 0 ? "+" + WealthHelper.getWealthShow(winValue) : "" + WealthHelper.getWealthShow(winValue));
        holder.tv_game_earnings.setTextColor(ContextCompat.getColor(mActivity, winValue >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no));

        holder.paipu_sparkbutton.setVisibility(mIsShowMore ? View.VISIBLE : View.GONE);
        holder.paipu_sparkbutton.setOnClickListener(new OnMoreClick(position));
        holder.paipu_sparkbutton.setChecked(data.is_collect == 1 ? true : false);
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

    @Override
    public void onDataFinish(Object data) {
        if(data != null && data instanceof CardGamesEy){
            notifyDataSetChanged();
        }
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
            NetCardRecordEy data = (NetCardRecordEy) getItem(position);
            if (v instanceof SparkButton && data.is_collect != 1) {
                ((SparkButton) v).playAnimation();
            }
//            boolean isOldPaipu = DataManager.IsOldPaipu(data.id);
//            mHandCardMoreView.setPosition(position, isOldPaipu, data.hand_cards, data.is_collect == 1);
//            mHandCardMoreView.showAtLocation(mParentLayout, Gravity.CENTER | Gravity.BOTTOM, 0, 0);
            if (mHandCardMoreView.mOnMoreListener != null) {
                mHandCardMoreView.mOnMoreListener.onCollect(position);
            }
        }
    }

    static class ViewHolder{
        RelativeLayout rl_game_info;
        LinearLayout ll_record_match;
        TextView tv_game_match_player;
        TextView tv_game_match_checkin_fee;
        TextView tv_game_date;
        //
//        TextView tv_game_hand_count;
//        TextView tv_record_no;

//        TextView tv_game_create_time;
        TextView tv_game_earnings;
        TextView tv_game_name;
        TextView tv_game_blind;
        SparkButton paipu_sparkbutton;
//        View view_divider_header;
//        View view_divider_footer;
        HandCardView mHandCardView;
        View view_record_divider;
        HandCardView mCardTypeView;
        //下面的是大菠萝独有的ui
        ImageView iv_pineapple_icon;
        TextView tv_fantasy;
        TextView tv_handtype;
    }
}
