package com.htgames.nutspoker.chat.session.extension;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * 实现自定义消息的附件解析器
 */
public class CustomAttachParser implements MsgAttachmentParser {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DATA = "data";

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = JSON.parseObject(json);
            int type = object.getInteger(KEY_TYPE);
            JSONObject data = object.getJSONObject(KEY_DATA);
            switch (type) {
                case CustomAttachmentType.Guess:
                    attachment = new GuessAttachment();
                    break;
                case CustomAttachmentType.CreatGame:
                    attachment = new GameAttachment(data.toJSONString());
                    break;
//                case CustomAttachmentType.Tip:
//                    attachment = new TipAttachment();
//                    break;
                case CustomAttachmentType.Paipu:
                    attachment = new PaipuAttachment(data.toJSONString());
                    break;
                case CustomAttachmentType.Bill:
                    attachment = new BillAttachment(data.toJSONString());
                    break;
//                case CustomAttachmentType.SnapChat:
//                    SnapChatAttachment snapchat = new SnapChatAttachment(data);
//                    return snapchat;
//                case CustomAttachmentType.Sticker:
//                    attachment = new StickerAttachment();
//                    break;
//                case CustomAttachmentType.RTS:
//                    attachment = new RTSAttachment();
//                    break;
//                default:
//                    attachment = new DefaultCustomAttachment();
//                    break;
            }
            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {

        }

        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        object.put(KEY_TYPE, type);
        if (data != null) {
            object.put(KEY_DATA, data);
        }
        return object.toJSONString();
    }
}
