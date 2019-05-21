package com.htgames.nutspoker.ui.activity.horde;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.widget.ClearableEditTextWithIcon;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 17/3/20.
 */

public class HordeJoinAC extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.edit_search_club) ClearableEditTextWithIcon edit_search_club;
    @BindView(R.id.join_club_cancel_tv) TextView join_club_cancel_tv;
    String teamId;
    public static void start(Activity activity, String tid) {
        Intent intent = new Intent(activity, HordeJoinAC.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID, tid);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_join);
        mUnbinder = ButterKnife.bind(this);
        setHeadTitle(R.string.text_horde_join);
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edit_search_club.setFocusable(true);
                edit_search_club.setFocusableInTouchMode(true);
                edit_search_club.requestFocus();
                showKeyboard(true);
            }
        }, 200L);
        join_club_cancel_tv.setOnClickListener(this);
        edit_search_club.setText("");
        edit_search_club.setIconResource(R.mipmap.icon_search);
        edit_search_club.setHint(R.string.horde_search_hint);
        edit_search_club.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchKey();
                    return true;
                }
                return false;
            }
        });
    }

    private void searchKey() {
        String key = edit_search_club.getText().toString();
        key = key.trim();
        if (StringUtil.isSpace(key)) {
            Toast.makeText(getApplicationContext(), R.string.not_allow_empty, android.widget.Toast.LENGTH_SHORT).show();
        } else {
            HordeDetailAC.startBySearch(this, key, teamId);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.join_club_cancel_tv) {
            finish();
        }
    }
}
