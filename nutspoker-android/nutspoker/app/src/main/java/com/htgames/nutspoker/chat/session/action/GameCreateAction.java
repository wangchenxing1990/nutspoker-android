package com.htgames.nutspoker.chat.session.action;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.session.activity.TeamMessageAC;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.ui.activity.Game.GameCreateActivity;
import com.netease.nim.uikit.session.actions.BaseAction;

/**
 * 牌局创建动作
 */
public class GameCreateAction extends BaseAction {

    public GameCreateAction() {
        super(R.drawable.btn_match_checkin, R.string.input_panel_game_create);
    }

    @Override
    public void onClick() {
        GameCreateActivity.startActivityForResult(getActivity() , TeamMessageAC.REQUESTCODE_GAMECREATE ,getAccount() ,GameConstants.GAME_TYPE_CLUB);
    }
}