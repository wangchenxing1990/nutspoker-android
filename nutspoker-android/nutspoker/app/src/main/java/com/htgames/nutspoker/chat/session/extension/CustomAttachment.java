package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * 先定义一个自定义消息附件的基类，负责解析你的自定义消息的公用字段，比如类型等等。
 */
public abstract class CustomAttachment implements MsgAttachment {
    // 自定义消息附件的类型，根据该字段区分不同的自定义消息
    protected int type;

    CustomAttachment(int type) {
        this.type = type;
    }

    // 解析公用字段，然后将具体的附件内容分发给具体的子类去解析。
    public void fromJson(JSONObject json) {
//        type = json.getInteger("type");
//        JSONObject data = json.getJSONObject("data");
        if (json != null) {
            parseData(json);
        }
    }

    // 实现 MsgAttachment 的接口，封装公用字段，然后调用子类的封装函数。
    @Override
    public String toJson(boolean send) {
        //封装成为Json
        return CustomAttachParser.packData(type, packData());
    }

    public int getType() {
        return type;
    }

    // // 子类的解析和封装接口。子类仅处理自己的具体数据，避免污染公共字段。
    protected abstract void parseData(JSONObject data);

    // 数据打包
    protected abstract JSONObject packData();
}
