package com.htgames.nutspoker.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.circle.view.CirclePaijuView;
import com.htgames.nutspoker.circle.view.CirclePaipuView;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.ui.activity.Hands.PaipuCollectActivity;
import com.htgames.nutspoker.ui.activity.Hands.PaipuRecordActivity;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.util.ToolUtil;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

/**
 * 分享到会话
 */
public class ShareToSessionDialog extends Dialog implements View.OnClickListener {
    private final static String TAG = "ShareToSessionDialog";
    private Activity activity;
    View view;
    EditText edt_share_message;
    Button btn_dialog_share_cancel;
    Button btn_dialog_share_send;
    CirclePaijuView mCirclePaijuView;
    CirclePaipuView mCirclePaipuView;
    int shareType = CircleConstant.TYPE_PAIJU;
    Object shareContant;
    String toSessionId;
    ArrayList<String> toSessionIdList;
    SessionTypeEnum sessionTypeEnum = SessionTypeEnum.P2P;
    int shareFrom;

    public ShareToSessionDialog(Activity activity, int shareType , Object shareContant , int shareFrom) {
        super(activity, R.style.dialog_custom_prompt);
        this.activity = activity;
        this.shareType = shareType;
        this.shareContant = shareContant;
        this.shareFrom = shareFrom;
        init(activity);
    }

    public void init(Context context) {
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        view = LayoutInflater.from(context).inflate(R.layout.dialog_share_to_session, null);
        initView();
        setContentView(view);
    }

    private void initView() {
        mCirclePaijuView = (CirclePaijuView) view.findViewById(R.id.mCirclePaijuView);
        mCirclePaipuView = (CirclePaipuView) view.findViewById(R.id.mCirclePaipuView);
        edt_share_message = (EditText) view.findViewById(R.id.edt_share_message);
        btn_dialog_share_cancel = (Button) view.findViewById(R.id.btn_dialog_share_cancel);
        btn_dialog_share_send = (Button) view.findViewById(R.id.btn_dialog_share_send);
        btn_dialog_share_cancel.setOnClickListener(this);
        btn_dialog_share_send.setOnClickListener(this);
        //分享牌局信息
        if (shareType == CircleConstant.TYPE_PAIJU && shareContant != null && shareContant instanceof GameBillEntity) {
            mCirclePaijuView.setVisibility(View.VISIBLE);
            mCirclePaijuView.setData((GameBillEntity) shareContant);
            mCirclePaijuView.setBackground(R.drawable.dialog_share_to_session_content_bg);
        }
        //分享牌谱记录
        else if (shareType == CircleConstant.TYPE_PAIPU && shareContant != null && shareContant instanceof PaipuEntity) {
            mCirclePaipuView.setVisibility(View.VISIBLE);
            mCirclePaipuView.setData((PaipuEntity) shareContant);
            mCirclePaipuView.setBackground(R.drawable.dialog_share_to_session_content_bg);
        }
    }

    public void show(String toSessionId , SessionTypeEnum sessionTypeEnum) {
        super.show();
        this.toSessionId = toSessionId;
        this.sessionTypeEnum = sessionTypeEnum;
    }

    public void show(ArrayList<String> toSessionIdList , SessionTypeEnum sessionTypeEnum) {
        super.show();
        this.toSessionIdList = toSessionIdList;
        this.sessionTypeEnum = sessionTypeEnum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dialog_share_cancel:
                dismiss();
                break;
            case R.id.btn_dialog_share_send:
                if(toSessionIdList != null && toSessionIdList.size() != 0){
                    for(String sessionId : toSessionIdList){
                        sendMessage(sessionId);
                    }
                } else if(!TextUtils.isEmpty(toSessionId)){
                    sendMessage(toSessionId);
                }
                Toast.makeText(getContext(), R.string.circle_share_success, android.widget.Toast.LENGTH_SHORT).show();
                dismiss();

                //还需要关闭键盘
                ToolUtil.closeKeyBoard(activity);
                sendMessageFinish();

                break;
        }
    }

    public void sendMessage(String sessionId){
        if(shareType == CircleConstant.TYPE_PAIJU && shareContant instanceof GameBillEntity){
            BillAttachment attachment = new BillAttachment(((GameBillEntity) shareContant).jsonStr);
            IMMessage gameRecordMessage = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, getContext().getString(R.string.msg_custom_type_paiju_info_desc), attachment);
            NIMClient.getService(MsgService.class).sendMessage(gameRecordMessage , false);
        } else if(shareType == CircleConstant.TYPE_PAIPU && shareContant instanceof PaipuEntity){
            PaipuAttachment attachment = new PaipuAttachment( ((PaipuEntity) shareContant).jsonDataStr);
            IMMessage gameRecordMessage = MessageBuilder.createCustomMessage(sessionId, sessionTypeEnum, getContext().getString(R.string.msg_custom_type_paipu_info_desc), attachment);
            NIMClient.getService(MsgService.class).sendMessage(gameRecordMessage , false);
        }
        String text = edt_share_message.getText().toString();
        if(!TextUtils.isEmpty(text)){
            IMMessage textMessage = MessageBuilder.createTextMessage(sessionId , sessionTypeEnum, text);
            NIMClient.getService(MsgService.class).sendMessage(textMessage , false);
        }
    }

    public void sendMessageFinish() {
        if (shareFrom == ContactSelectActivity.FROM_PAIJU_DETAILS) {
            Intent intent = new Intent(activity, RecordDetailsActivity.class);
            activity.startActivity(intent);
        } else if (shareFrom == ContactSelectActivity.FROM_PAIPU_RECORD) {
            Intent intent = new Intent(activity, PaipuRecordActivity.class);
            activity.startActivity(intent);
        } else if (shareFrom == ContactSelectActivity.FROM_PAIPU_COLLECT) {
            Intent intent = new Intent(activity, PaipuCollectActivity.class);
            activity.startActivity(intent);
        }
    }
}
