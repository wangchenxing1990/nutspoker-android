package com.htgames.nutspoker.chat.session.action;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.session.extension.GuessAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * Created by hzxuwen on 2015/6/11.
 */
public class GuessAction extends BaseAction {

    public GuessAction() {
        super(R.drawable.btn_match_checkin, R.string.input_panel_bill);
    }

    @Override
    public void onClick() {
        GuessAttachment attachment = new GuessAttachment();
        IMMessage message = MessageBuilder.createCustomMessage(getAccount(), getSessionType(), attachment.getValue().getDesc(), attachment);
        sendMessage(message);
    }
}
