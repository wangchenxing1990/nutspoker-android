package com.htgames.nutspoker.chat.app_msg.interfaces;

import com.htgames.nutspoker.chat.app_msg.model.AppMessage;

/**
 */
public interface AppMessageListener {
    void onAgree(AppMessage message, int position);

    void onReject(AppMessage message, int position);

    void onClick(AppMessage message, int position);

    void onLongPressed(AppMessage message, int position);
}
