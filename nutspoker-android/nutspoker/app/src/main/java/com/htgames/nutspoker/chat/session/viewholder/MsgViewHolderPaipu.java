package com.htgames.nutspoker.chat.session.viewholder;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.chat.session.activity.BaseMessageActivity;
import com.htgames.nutspoker.chat.session.extension.CustomAttachmentType;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.circle.view.CirclePaipuView;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * 自定义消息：牌谱
 */
public class MsgViewHolderPaipu extends MsgViewHolderBase {

    @Override
    protected int getContentResId() {
        return R.layout.message_paipu_item;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        layoutDirection();
        RelativeLayout rl_message_game = findViewById(R.id.rl_message_game);
        CirclePaipuView mCirclePaipuView = findViewById(R.id.mCirclePaipuView);
        TextView tv_message_type = findView(R.id.tv_message_type);
        PaipuAttachment attachment = (PaipuAttachment) message.getAttachment();
        final PaipuEntity paipuEntity = attachment.getValue();
//        tv_game_blind.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        tv_game_time.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        tv_game_creator.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tv_game_name, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        mCirclePaipuView.setBackground(android.R.color.transparent);
        mCirclePaipuView.setData(paipuEntity);
        //
        rl_message_game.setOnLongClickListener(longClickListener);
        rl_message_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null && context instanceof BaseMessageActivity) {
                    ((BaseMessageActivity) context).openHandPlay(paipuEntity);
                }
            }
        });
        if(attachment.getType() == CustomAttachmentType.Paipu){
            tv_message_type.setText(R.string.chatroom_msg_custom_type_paipu);
        }
    }

    private void layoutDirection() {
        RelativeLayout rl_message_game = findViewById(R.id.rl_message_game);
        TextView tv_message_type = findView(R.id.tv_message_type);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tv_message_type.getLayoutParams();
        if (isReceivedMessage()) {
            rl_message_game.setBackgroundResource(R.drawable.chatroom_friend_item_black_bg);
//            rl_message_game.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(0), ScreenUtil.dip2px(0));
//            rl_message_game.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
            params.gravity = Gravity.RIGHT;
        } else {
            rl_message_game.setBackgroundResource(R.drawable.chatroom_my_item_black_bg);
//            rl_message_game.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(10), ScreenUtil.dip2px(0), ScreenUtil.dip2px(0));
            params.gravity = Gravity.LEFT;
        }
        tv_message_type.setLayoutParams(params);
    }

    @Override
    protected int leftBackground() {
        return 0;
    }

    @Override
    protected int rightBackground() {
        return 0;
    }

    protected String getDisplayText() {
        return message.getContent();
    }
}
