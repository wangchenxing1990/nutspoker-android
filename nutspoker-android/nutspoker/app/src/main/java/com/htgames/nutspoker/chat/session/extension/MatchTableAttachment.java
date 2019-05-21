package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSONObject;

public class MatchTableAttachment extends CustomAttachment {

    public MatchTableAttachment() {
        super(CustomAttachmentType.MatchTableUpdate);
    }

    @Override
    protected void parseData(JSONObject data) {

    }

    @Override
    protected JSONObject packData() {
        return null;
    }
}
