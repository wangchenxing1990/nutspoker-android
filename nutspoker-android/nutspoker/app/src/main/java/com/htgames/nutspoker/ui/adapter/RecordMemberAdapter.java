package com.htgames.nutspoker.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMemberEntity;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.interfaces.OnAvaterClickListener;
import com.htgames.nutspoker.ui.helper.RecordMemberItemType;
import com.htgames.nutspoker.ui.helper.RecordMemberSection;
import com.htgames.nutspoker.ui.helper.RecordMemberViewHolder;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 成员列表适配器
 */
public class RecordMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "RecordMemberAdapter";
    Context context;
    public GameEntity gameInfo;
    private LinkedHashMap<Integer, RecordMemberSection> sections;
    int gameMode = GameConstants.GAME_MODE_NORMAL;
    private int checkPosition = -1;
    public int checkin_player_by_channel = -1;//某个渠道下的报名玩家人数
    public int billTotalPlayer = -1;//==bill.totalPlayer这个牌局的总人数;
    OnAvaterClickListener onAvaterClickListener;
    OnItemClickListener onItemClickListener;
    public int insurancePond;//所有保险玩家的保险盈利的相加总和再取反

    public RecordMemberAdapter(Context context , int gameMode, GameEntity gameInfo) {
        this.context = context;
        this.sections = new LinkedHashMap<>();
        this.gameMode = gameMode;
        this.gameInfo = gameInfo;
    }

    public void addSection(int sectionType, RecordMemberSection section) {
        this.sections.put(sectionType, section);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return RecordMemberViewHolder.createViewHolder(context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        int currentSectionPostion = 0;
        LogUtil.i(TAG, "position :" + position);
        for (Map.Entry<Integer, RecordMemberSection> entry : sections.entrySet()) {
            int itemType = entry.getKey();
            int currentPostion = position - currentSectionPostion;
            RecordMemberSection section = entry.getValue();
            int sectionTotal = section.getSectionItemsTotal();
            //
            boolean isInsurance = false;
            boolean isChecked = false;
            if (itemType == RecordMemberItemType.INSURANCE) {
                isInsurance = true;
            } else {
                if (checkPosition == position) {
                    isChecked = true;
                }
            }
            //
            if (position >= currentSectionPostion && (position < (currentSectionPostion + sectionTotal))) {
                //
                if (viewHolder instanceof RecordMemberViewHolder) {
                    RecordMemberViewHolder holder = (RecordMemberViewHolder) viewHolder;
                    //
                    if (currentPostion == 0) {
                        holder.ll_record_member_column.setVisibility(View.VISIBLE);
                        if (gameMode == GameConstants.GAME_MODE_NORMAL) {
                            //普通模式
                            holder.tv_details_all_buy_title.setVisibility(View.VISIBLE);
                            if (!isInsurance) {
                                holder.tv_game_memeber.setText(context.getString(R.string.record_details_colunm_members, sectionTotal));
                                holder.tv_details_all_buy_title.setText(R.string.record_details_colunm_all_buy);
                                holder.tv_details_all_gain_title.setText(R.string.record_details_colunm_all_winchips);
                                //
                                Drawable drawable = context.getResources().getDrawable(R.mipmap.icon_club_chat_member);//icon_game_record_details_member);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                holder.tv_game_memeber.setCompoundDrawables(drawable, null, null, null);
                            } else {
                                String insurancePondStr = insurancePond >= 0 ? ("+" + insurancePond) : ("" + insurancePond);
                                holder.tv_game_memeber.setVisibility(View.INVISIBLE);//holder.tv_game_memeber.setText(R.string.record_details_colunm_insurance);
//                                holder.tv_details_all_buy_title.setText("保险池：" + insurancePondStr);//holder.tv_details_all_buy_title.setText(R.string.record_details_colunm_insurance_buy);投保不显示了
                                holder.tv_details_all_gain_title.setText("");//holder.tv_details_all_gain_title.setText(R.string.record_details_colunm_insurance_pay);
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                                SpannableString spanText = new SpannableString(insurancePondStr);
                                spanText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, insurancePond >= 0 ? R.color.record_list_earn_yes : R.color.record_list_earn_no)), 0, spanText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                stringBuilder.append("保险池：");
                                stringBuilder.append(spanText);//stringBuilder.append(spanText);
                                holder.tv_details_all_buy_title.setText(stringBuilder);
                                //
                                Drawable drawable = context.getResources().getDrawable(R.mipmap.icon_club_chat_member);//icon_game_record_details_insurance);
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                holder.tv_game_memeber.setCompoundDrawables(drawable, null, null, null);
                            }
                        } else if (gameMode == GameConstants.GAME_MODE_SNG || gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
                            int playerNum = gameMode == GameConstants.GAME_MODE_SNG ? (sectionTotal) : (billTotalPlayer >= 0 ? billTotalPlayer : 0);
                            if (checkin_player_by_channel != -1) {
                                playerNum = checkin_player_by_channel;
                            }
                            holder.tv_game_memeber.setText(context.getString(R.string.record_details_colunm_members, playerNum));
                            holder.tv_details_all_buy_title.setVisibility(View.GONE);
                            holder.tv_details_all_buy_title.setText(R.string.chips_buy);
                            if (gameMode == GameConstants.GAME_MODE_MTT && gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig && ((GameMttConfig) gameInfo.gameConfig).ko_mode != 0) {//猎人赛
                                holder.tv_details_all_buy_title.setVisibility(View.VISIBLE);
                                holder.tv_details_all_buy_title.setText(((GameMttConfig) gameInfo.gameConfig).ko_mode == 1 ? "猎头/赏金" : "赏金");
                            }
                            holder.tv_details_all_gain_title.setText("奖励");
                        }
                    } else {
                        holder.ll_record_member_column.setVisibility(View.GONE);
                    }
                    final GameMemberEntity gameMemberEntity = (GameMemberEntity) section.list.get(currentPostion);
                    holder.iv_game_userhead.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onAvaterClickListener != null) {
                                onAvaterClickListener.onAvaterClick(gameMemberEntity.userInfo.account);
                            }
                        }
                    });
                    if (itemType == RecordMemberItemType.NORMAL) {
                        holder.record_detail_player_info_container.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onItemClickListener != null) {
                                    onItemClickListener.onItemClick(position);
                                }
                            }
                        });
                    }
                    holder.refresh(gameMemberEntity, gameMode, isInsurance, isChecked, currentPostion, gameInfo);
                }
            }
            currentSectionPostion += sectionTotal;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Map.Entry<Integer, RecordMemberSection> entry : sections.entrySet()) {
            RecordMemberSection section = entry.getValue();
            count += section.getSectionItemsTotal();
        }
        return count;
    }

    public void checkPosition(int checkPosition) {
        this.checkPosition = checkPosition;
        notifyDataSetChanged();
    }

    public void setOnAvaterClickListener(OnAvaterClickListener listener) {
        this.onAvaterClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(int position);
    }
}
