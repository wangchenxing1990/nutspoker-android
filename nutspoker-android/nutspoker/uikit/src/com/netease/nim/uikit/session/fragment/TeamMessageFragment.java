package com.netease.nim.uikit.session.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;

/**
 * Created by zhoujianghua on 2015/9/10.
 */
public class TeamMessageFragment extends MessageFragment {

    private Team team;

    public static TeamMessageFragment newInstance() {
        TeamMessageFragment mInstance = new TeamMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Extras.EXTRA_SHOW_DIVIDER, true);
        mInstance.setArguments(bundle);
        return mInstance;
    }

    @Override
    public boolean isAllowSendMessage(IMMessage message) {
        if (team == null || !team.isMyTeam()) {
            Toast.makeText(getActivity(), R.string.team_send_message_not_allow, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
