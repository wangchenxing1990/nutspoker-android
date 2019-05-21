package com.htgames.nutspoker.chat.session.viewholder;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.CustomAttachmentType;
import com.htgames.nutspoker.circle.view.CirclePaijuView;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * 自定义消息：牌局
 */
public class MsgViewHolderBill extends MsgViewHolderBase {

    @Override
    protected int getContentResId() {
        return R.layout.message_bill_item;
    }

    @Override
    protected void inflateContentView() {
    }

    @Override
    protected void bindContentView() {
        layoutDirection();
        CirclePaijuView mCirclePaijuView = findView(R.id.mCirclePaijuView);
        TextView tv_message_type = findView(R.id.tv_message_type);
        BillAttachment attachment = (BillAttachment) message.getAttachment();
//        final BillEntity billEntity = attachment.getValue();
        final GameBillEntity billEntity = attachment.getValue();
//        tv_game_blind.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        tv_game_time.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        tv_game_creator.setTextColor(isReceivedMessage() ? Color.BLACK : Color.WHITE);
//        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), tv_game_name, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
//        tv_game_name.setMovementMethod(LinkMovementMethod.getInstance());
//        //
//        tv_game_name.setText(billEntity.getName());
//        tv_game_blind.setText(GameConstants.blinds[billEntity.getBlindType()]);
////        tv_game_time.setText(billEntity.getDate());

        if(attachment.getType() == CustomAttachmentType.Bill){
            tv_message_type.setText(R.string.chatroom_msg_custom_type_paiju);
        }
        //
        mCirclePaijuView.setData(billEntity);
        mCirclePaijuView.setBackground(android.R.color.transparent);
        mCirclePaijuView.setOnLongClickListener(longClickListener);
        mCirclePaijuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof Activity){
                    RecordDetailsActivity.start((Activity)context , billEntity ,RecordDetailsActivity.FROM_CHAT);
                }
            }
        });
    }

    private void layoutDirection() {
        RelativeLayout rl_message_game = findViewById(R.id.rl_message_game);
        TextView tv_message_type = findView(R.id.tv_message_type);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)tv_message_type.getLayoutParams();
        if (isReceivedMessage()) {
            rl_message_game.setBackgroundResource(R.drawable.chatroom_friend_item_black_bg);
            params.gravity = Gravity.RIGHT;
//            rl_message_game.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(0), ScreenUtil.dip2px(0));
//            rl_message_game.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
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
