package com.htgames.nutspoker.chat.app_msg.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.htgames.nutspoker.chat.app_msg.interfaces.AppMessageListener;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;

/**
 */
public class BaseAppViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    private AppMessageListener listener;
    public BaseAppViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public void setAppMessageListener(AppMessageListener listener) {
        this.listener = listener;
    }

    public class OnClick implements View.OnClickListener {
        AppMessage appMessage;
        int mPosition;
        public OnClick(AppMessage appMessage, int position) {
            this.appMessage = appMessage;
            mPosition = position;
        }
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClick(appMessage, mPosition);
            }
        }
    }

    public class OnLongClick implements View.OnLongClickListener {
        AppMessage appMessage;
        int mPosition;
        public OnLongClick(AppMessage appMessage, int position) {
            this.appMessage = appMessage;
            mPosition = position;
        }
        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                listener.onLongPressed(appMessage, mPosition);
            }
            return false;
        }
    }
}
