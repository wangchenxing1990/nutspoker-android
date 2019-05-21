package com.htgames.nutspoker.chat.session.action;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.Record.RecordListAC;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.activity.BaseMessageActivity;

/**
 * 菜单中：战绩点击
 */
public class BillAction extends BaseAction {

    public BillAction() {
        super(R.drawable.btn_match_checkin, R.string.input_panel_bill);
    }

    @Override
    public void onClick() {
        RecordListAC.StartActivityFor(getActivity(), RecordListAC.FROM_SEND_BY_SESSION
                , BaseMessageActivity.REQUESTCODE_GAMEBILL);
    }
}