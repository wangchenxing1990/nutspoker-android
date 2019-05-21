package com.htgames.nutspoker.game.match.interfaces;

import com.htgames.nutspoker.chat.app_msg.model.AppMessage;

/**
 * Created by 周智慧 on 17/2/10.
 */

public interface IMsgExpired {
    void onExpired(AppMessage appMessage);
}
