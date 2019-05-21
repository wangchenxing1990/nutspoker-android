package com.htgames.nutspoker.game.match.interfaces;

import com.htgames.nutspoker.chat.app_msg.model.AppMessage;

/**
 * Created by Administrator on 2016/5/22.
 */
public interface ControlActionListener {
    public void onAgress(AppMessage item);

    public void onReject(AppMessage item);
}
