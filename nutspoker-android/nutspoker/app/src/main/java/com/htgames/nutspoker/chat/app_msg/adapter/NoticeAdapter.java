package com.htgames.nutspoker.chat.app_msg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.htgames.nutspoker.chat.app_msg.interfaces.AppMessageListener;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.viewholder.AppNoticeVH;
import com.htgames.nutspoker.chat.app_msg.viewholder.GameStatusVH;
import com.htgames.nutspoker.chat.app_msg.viewholder.MatchBuyChipsResultHolder;

import java.util.ArrayList;

/**
 * 通知消息
 */
public class NoticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "NoticeAdapter";
    Context context;
    ArrayList<AppMessage> list = new ArrayList<AppMessage>();
    private LayoutInflater layoutInflater;
    private int showInterval = 30 * 60;
    private AppMessageListener listener;

    public NoticeAdapter(Context context, ArrayList<AppMessage> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<AppMessage> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        AppMessage item = getItem(position);
        if (item == null) {
            return super.getItemViewType(position);
        }
        return item.type.getValue();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AppMessageType.AppNotice.getValue()) {
            return AppNoticeVH.createViewHolder(context);
        } else if (viewType == AppMessageType.GameOver.getValue()) {
            return GameStatusVH.createViewHolder(context);
        } else if (viewType == AppMessageType.MatchBuyChipsResult.getValue()) {
            return MatchBuyChipsResultHolder.createViewHolder(context);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        boolean showTopPad = position > 0;
        if (position > 0) {
            AppMessage lastMessage = getItem(position - 1);
            if (lastMessage != null && lastMessage.type == AppMessageType.AppNotice ) {
                showTopPad = false;
            }
        }
        AppMessage message = getItem(position);
        boolean showTime = false;
        boolean showTimeHeader = position == 0;//showTime的父布局
        if (position == 0) {
            //系统时间和发送时间间隔5分钟，显示
            showTime = true;
        } else {
            if ((getItem(position - 1).time - message.time) > showInterval) {
                showTime = true;
            } else {
                showTime = false;
            }
        }
        if (viewHolder instanceof AppNoticeVH) {
            AppNoticeVH holder = (AppNoticeVH) viewHolder;
            holder.refresh(context, message, showTopPad, position);
            holder.setAppMessageListener(listener);
        } else if (viewHolder instanceof GameStatusVH) {
            GameStatusVH holder = (GameStatusVH) viewHolder;
            holder.refresh(context, message, showTime, position);
            holder.setAppMessageListener(listener);
        } else if (viewHolder instanceof MatchBuyChipsResultHolder) {
            MatchBuyChipsResultHolder holder = (MatchBuyChipsResultHolder) viewHolder;
            holder.refresh(context, message, showTime, position);
            holder.setAppMessageListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public AppMessage getItem(int position) {
        if (list != null && list.size() != 0) {
            return list.get(position);
        }
        return null;
    }

    public void setAppMessageListener(AppMessageListener listener) {
        this.listener = listener;
    }
}
