package com.htgames.nutspoker.ui.activity.System;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact.activity.PhoneContactActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;

/**
 * 好友引导
 */
public class FriendGuideActivity extends BaseActivity implements View.OnClickListener {
    Button btn_friend_add;
    Button btn_friend_not;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_guide);
        setSwipeBackEnable(false);
        initHead();
        initView();
    }

    private void initView() {
        btn_friend_add = (Button)findViewById(R.id.btn_friend_add);
        btn_friend_not = (Button)findViewById(R.id.btn_friend_not);
        btn_friend_add.setOnClickListener(this);
        btn_friend_not.setOnClickListener(this);
    }

    private void initHead() {
        setHeadLeftButtonGone();
        setHeadTitle(R.string.friend_guide_head);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_friend_add:
                PhoneContactActivity.start(this , PhoneContactActivity.FROM_REGISTER);
                finish();
                break;
            case R.id.btn_friend_not:
                MainActivity.start(this);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.start(this);
        super.onBackPressed();
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }
}
