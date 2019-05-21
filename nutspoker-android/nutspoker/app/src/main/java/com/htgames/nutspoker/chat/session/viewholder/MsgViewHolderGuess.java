package com.htgames.nutspoker.chat.session.viewholder;

import com.htgames.nutspoker.chat.session.extension.GuessAttachment;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderText;

/**
 */
public class MsgViewHolderGuess extends MsgViewHolderText {

    @Override
    protected String getDisplayText() {
        GuessAttachment attachment = (GuessAttachment) message.getAttachment();
        return attachment.getValue().getDesc() + "!";
    }
}
