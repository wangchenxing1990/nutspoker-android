package com.htgames.nutspoker.game.match.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.interfaces.AppMessageListener;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.viewholder.BuyChipsVHNew;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.interfaces.IMsgExpired;
import com.htgames.nutspoker.game.match.item.MatchPlayerItemType;
import com.htgames.nutspoker.game.match.item.PlayerSection;
import com.htgames.nutspoker.game.match.viewholder.MatchMgrPlayerVH;
import com.htgames.nutspoker.game.match.viewholder.NormalMgrPlayerVH;
import com.htgames.nutspoker.game.match.viewholder.PlayerViewHolder;
import com.htgames.nutspoker.game.match.viewholder.SngMgrPlayerVH;
import com.htgames.nutspoker.game.model.CheckInStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.ui.adapter.FooterClkBtnVH;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.interfaces.IClick;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 */
public class MatchPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int rank_type = 1;//1名次  2猎头  3赏金 4身价
    public boolean isAllPlayer = true;
    private final static String TAG = "MatchPlayerAdapter";
    Context context;
    private LayoutInflater layoutInflater;
    public GameEntity gameInfo;
    public boolean hasClick = false;//"更多"按钮
    public IClick.IOnlyClick clickListener;//"更多"按钮    复用   "移除"报名玩家按钮
    //    ControlActionListener mControlActionListener;
    AppMessageListener mAppMessageListener;
    public IMsgExpired iMsgExpired;
    public int gameStatus = GameStatus.GAME_STATUS_WAIT;
    int myCheckInStatus = CheckInStatus.CHECKIN_STATUES_NOT;
    //
    private LinkedHashMap<Integer, PlayerSection> sections;
    OnPlayerClick onPlayerClick;
    public int checkInCount = 0;
    public boolean canHandle = false;

    public MatchPlayerAdapter(Context context , AppMessageListener listener , OnPlayerClick onPlayerClick) {
        this.context = context;
        mAppMessageListener = listener;
        this.onPlayerClick = onPlayerClick;
        layoutInflater = LayoutInflater.from(context);
        sections = new LinkedHashMap<>();
    }

    public void addSection(int sectionType, PlayerSection section) {
        this.sections.put(sectionType, section);
    }

    public void updateStatus(int status, int myCheckInStatus) {
        gameStatus = status;
        this.myCheckInStatus = myCheckInStatus;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int currentPostion = 0;
        for (Map.Entry<Integer, PlayerSection> entry : sections.entrySet()) {
            PlayerSection section = entry.getValue();
            int sectionTotal = section.getSectionItemsTotal();
            if (position >= currentPostion && (position < (currentPostion + sectionTotal))) {
                return entry.getKey();
            }
            currentPostion += sectionTotal;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.i(TAG, "同意或者拒绝 onCreateViewHolder viewType :" + viewType);
        if (viewType == MatchPlayerItemType.ChipBuy) {
            return BuyChipsVHNew.createViewHolder(context);//买入申请
        } else if (viewType == MatchPlayerItemType.Player) {
            return PlayerViewHolder.createViewHolder(context);//玩家
        } else if (viewType == MatchPlayerItemType.More) {
            return FooterClkBtnVH.createViewHolder(context, parent, hasClick);//底部footer
        } else if (viewType == MatchPlayerItemType.MttPlayer) {
            return MatchMgrPlayerVH.createViewHolder(context);//管理页面的玩家
        } else if (viewType == MatchPlayerItemType.NormalMgrPlayer) {
            return NormalMgrPlayerVH.createViewHolder(context);//普通局
        } else if (viewType == MatchPlayerItemType.SngMgrPlayer) {
            return SngMgrPlayerVH.createViewHolder(context);//sng
        } else {
            return PlayerViewHolder.createViewHolder(context);//
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int playerCount = getPlayerCount();
        int currentSectionPostion = 0;
        LogUtil.i(TAG, "同意或者拒绝 onBindViewHolder position :" + position);
        for (Map.Entry<Integer, PlayerSection> entry : sections.entrySet()) {
            final int currentPostion = position - currentSectionPostion;
            PlayerSection section = entry.getValue();
            int sectionTotal = section.getSectionItemsTotal();
            if (position >= currentSectionPostion && (position < (currentSectionPostion + sectionTotal))) {
                //
                if (viewHolder instanceof BuyChipsVHNew) {
                    final BuyChipsVHNew holder = (BuyChipsVHNew) viewHolder;
                    final AppMessage appMessage = (AppMessage) section.list.get(currentPostion);
                    holder.iMsgExpired  = iMsgExpired;
                    holder.showInfoHead(false);
                    holder.refresh(context, appMessage, false);
                    holder.setActionClickListener(new OnAgreeClick(appMessage, position), new OnRejectClick(appMessage, position));
                    if (holder.mCountDownTimers != null) {
                        holder.mCountDownTimers.cancel();
                        holder.mCountDownTimers = null;
                    }
                    int timeout = appMessage.attachObject instanceof BuyChipsNotify ? ((BuyChipsNotify) appMessage.attachObject).buyTimeout :
                            appMessage.attachObject instanceof MatchBuyChipsNotify ? ((MatchBuyChipsNotify) appMessage.attachObject).buyTimeout :
                                    0;//倒计时时间
                    if (appMessage.status == AppMessageStatus.init && BuyChipsVHNew.shouldShowCountDown(appMessage, holder)) {
                        holder.mCountDownTimers = new CountDownTimer(timeout * 1000, 1000) {//倒计时时间尽量写长一些，不同的类型倒计时时间不一样，保证900000是最长的就行
                            @Override
                            public void onTick(long millisUntilFinished) {
                                holder.updateStatus(context, appMessage);
                                LogUtil.i("***************************************************************************************");
                            }
                            @Override
                            public void onFinish() {
                            }
                        };
                        holder.mCountDownTimers.start();
                    }
                    //
//                    holder.mMatchBuyChipsView.showInfoHead(false);
//                    holder.mMatchBuyChipsView.setInfo(appMessage, appMessage.getFromId());
//                    holder.mMatchBuyChipsView.setActionClickListener(new OnActionClick(entry.getKey(), currentPostion, true),
//                            new OnActionClick(entry.getKey(), currentPostion, false));
                } else if (viewHolder instanceof PlayerViewHolder) {
                    PlayerViewHolder holder = (PlayerViewHolder) viewHolder;
                    final MatchPlayerEntity matchPlayerEntity = (MatchPlayerEntity) section.list.get(currentPostion);
                    holder.refresh(gameInfo, matchPlayerEntity, gameStatus, currentPostion, rank_type);
                    holder.rl_match_player_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onPlayerClick != null) {
                                onPlayerClick.onClick(matchPlayerEntity);
                            }
                        }
                    });
                } else if (viewHolder instanceof FooterClkBtnVH) {
                    ((FooterClkBtnVH) viewHolder).clickListener = clickListener;
                    ((FooterClkBtnVH) viewHolder).bind(position, playerCount, checkInCount, gameStatus, myCheckInStatus);
                } else if (viewHolder instanceof MatchMgrPlayerVH) {
                    final MatchPlayerEntity matchPlayerEntity = (MatchPlayerEntity) section.list.get(currentPostion);
                    ((MatchMgrPlayerVH) viewHolder).bind(gameInfo, matchPlayerEntity, gameStatus, canHandle);
                    ((MatchMgrPlayerVH) viewHolder).btn_contact_action_agree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (clickListener != null) {
                                clickListener.onClick(currentPostion);
                            }
                        }
                    });
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onPlayerClick != null) {
                                onPlayerClick.onClick(matchPlayerEntity);
                            }
                        }
                    });
                } else if (viewHolder instanceof NormalMgrPlayerVH) {
                    final MatchPlayerEntity matchPlayerEntity = (MatchPlayerEntity) section.list.get(currentPostion);
                    int status = gameInfo == null ? GameStatus.GAME_STATUS_WAIT : gameInfo.status;
                    ((NormalMgrPlayerVH) viewHolder).bind(gameInfo, matchPlayerEntity, status, isAllPlayer);
                } else if (viewHolder instanceof SngMgrPlayerVH) {
                    final MatchPlayerEntity matchPlayerEntity = (MatchPlayerEntity) section.list.get(currentPostion);
                    int status = gameInfo == null ? GameStatus.GAME_STATUS_WAIT : gameInfo.status;
                    ((SngMgrPlayerVH) viewHolder).bind(gameInfo, matchPlayerEntity, status, isAllPlayer);
                }
            }
            currentSectionPostion += sectionTotal;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Map.Entry<Integer, PlayerSection> entry : sections.entrySet()) {
            PlayerSection section = entry.getValue();
            count += section.getSectionItemsTotal();
        }
        return count;
    }

    public int getPlayerCount() {
        int count = 0;
        for (Map.Entry<Integer, PlayerSection> entry : sections.entrySet()) {
            if (entry.getKey() == MatchPlayerItemType.Player) {
                PlayerSection section = entry.getValue();
                count += section.getSectionItemsTotal();
            }
        }
        return count;
    }

    public class OnAgreeClick implements View.OnClickListener {
        AppMessage appMessage;
        int mPosition;
        public OnAgreeClick(AppMessage appMessage, int position) {
            this.appMessage = appMessage;
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mAppMessageListener != null) {
                mAppMessageListener.onAgree(appMessage, mPosition);
            }
        }
    }

    public class OnRejectClick implements View.OnClickListener {
        AppMessage appMessage;
        int mPosition;
        public OnRejectClick(AppMessage appMessage, int position) {
            this.appMessage = appMessage;
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mAppMessageListener != null) {
                mAppMessageListener.onReject(appMessage, mPosition);
            }
        }
    }

//    private class OnActionClick implements View.OnClickListener {
//        private int position;
//        private boolean isAgree = false;
//        private int type;
//
//        public OnActionClick(int type, int position, boolean isAgree) {
//            this.type = type;
//            this.position = position;
//            this.isAgree = isAgree;
//        }
//
//        @Override
//        public void onClick(View v) {
//            AppMessage member = (AppMessage) sections.get(type).getItem(position);
//            if (mControlActionListener != null) {
//                if (isAgree) {
//                    mControlActionListener.onAgress(member);
//                } else {
//                    mControlActionListener.onReject(member);
//                }
//            }
//        }
//    }

    public interface OnPlayerClick{
        public void onClick(MatchPlayerEntity playerEntity);
    }

}