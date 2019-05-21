package com.htgames.nutspoker.chat.session.action;

import android.content.Intent;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Hands.PaipuCollectActivity;
import com.htgames.nutspoker.ui.activity.Hands.PaipuRecordActivity;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.activity.BaseMessageActivity;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * 牌谱图标点击动作
 */
public class PaipuAction extends BaseAction {

    public PaipuAction() {
        super(R.drawable.btn_match_checkin, R.string.input_panel_paipu);
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(getActivity(), PaipuCollectActivity.class);
        if(getSessionType() == SessionTypeEnum.P2P){
            intent.putExtra(PaipuRecordActivity.EXTRA_FROM , PaipuRecordActivity.FROM_SESSION_BY_P2P);
        } else if(getSessionType() == SessionTypeEnum.Team) {
            intent.putExtra(PaipuRecordActivity.EXTRA_FROM , PaipuRecordActivity.FROM_SESSION_BY_TEAM);
        }
        getActivity().startActivityForResult(intent, BaseMessageActivity.REQUESTCODE_PAIPU);
    }
}
