package com.htgames.nutspoker.chat.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.contact_selector.ContactSelectHelper;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.ui.helper.team.TeamCreateHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.helper.MessageListPanelHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

import java.util.ArrayList;

/**
 * 聊天详情
 */
public class MessageInfoActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "MessageInfoActivity";
    RelativeLayout rl_chat_clear_history;
    private String account;
    SwitchButton switch_newmessage_notify;
    private static final int REQUEST_CODE_NORMAL = 1;

    public static void start(Context context, String acctout) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_SESSION_ID, acctout);
        intent.setClass(context, MessageInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
        setHeadTitle(R.string.chat_info_head);
        account = getIntent().getStringExtra(Extras.EXTRA_SESSION_ID);
        initView();
        updateSwitchBtn();
        initSwitchButton();
        if (!NIMClient.getService(FriendService.class).isMyFriend(account)) {
            //不是好友不能操作任何东西
            switch_newmessage_notify.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userName.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
        UmengAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengAnalytics.onPause(this);
    }

    private void updateSwitchBtn() {
        if (DemoCache.getAccount() != null && !DemoCache.getAccount().equals(account)) {
            boolean black = NIMClient.getService(FriendService.class).isInBlackList(account);
            boolean notice = NIMClient.getService(FriendService.class).isNeedMessageNotify(account);
            switch_newmessage_notify.setCheckedImmediately(notice);
        }
    }

    public void initSwitchButton() {
        switch_newmessage_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    switch_newmessage_notify.setChecked(!isChecked);
                    return;
                }
                NIMClient.getService(FriendService.class).setMessageNotify(account, isChecked).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        if (isChecked) {
                            LogUtil.i(TAG, "开启消息提醒成功");
                        } else {
                            LogUtil.i(TAG, "关闭消息提醒成功");
                        }
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 408) {
                            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "on failed:" + code, Toast.LENGTH_SHORT).show();
                        }
                        switch_newmessage_notify.setChecked(!isChecked);
                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        });
    }

    /**
     * 创建群聊
     */
    private void createTeamMsg() {
        ArrayList<String> memberAccounts = new ArrayList<>();
        memberAccounts.add(account);
        ContactSelectActivity.Option option = ContactSelectHelper.getCreateContactSelectOption(memberAccounts, 50);
//        NimUIKit.startContactSelect(this, option, REQUEST_CODE_NORMAL);// 创建群
        ContactSelectHelper.startContactSelect(this, option, REQUEST_CODE_NORMAL);// 创建群
    }

    TextView userName;

    private void initView() {
        rl_chat_clear_history = (RelativeLayout) findViewById(R.id.rl_chat_clear_history);
        switch_newmessage_notify = (SwitchButton) findViewById(R.id.switch_newmessage_notify);
        rl_chat_clear_history.setOnClickListener(this);
        //
        HeadImageView userHead = (HeadImageView) findViewById(R.id.user_layout).findViewById(R.id.imageViewHeader);
        userName = (TextView) findViewById(R.id.user_layout).findViewById(R.id.textViewName);
        userHead.loadBuddyAvatar(account);
        userName.setText(NimUserInfoCache.getInstance().getUserDisplayName(account));
        userHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfile();
            }
        });
        ((TextView) findViewById(R.id.create_team_layout).findViewById(R.id.textViewName)).setText(R.string.create_team_activity);
        ((TextView) findViewById(R.id.create_team_layout).findViewById(R.id.textViewName)).setVisibility(View.INVISIBLE);
        HeadImageView addImage = (HeadImageView) findViewById(R.id.create_team_layout).findViewById(R.id.imageViewHeader);
        addImage.setBackgroundResource(R.drawable.team_member_add_selector);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTeamMsg();
            }
        });
        addImage.setVisibility(View.GONE);
    }

    private void openUserProfile() {
        UserProfileActivity.start(this, account);
    }

    /**
     * 设置消息
     */
    public void muteMessageNotify() {

    }

    /**
     * 清除聊天记录
     */


    /**
     * 删除与某个聊天对象的全部消息记录
     *
     * @param account
     * @param sessionType
     */
    public void clearChattingHistory(String account, SessionTypeEnum sessionType) {
        // 删除与某个聊天对象的全部消息记录
        NIMClient.getService(MsgService.class).clearChattingHistory(account, sessionType);
    }

    /**
     * 清除聊天记录
     *
     * @param sessionId
     */
    public void doClearCache(final String sessionId) {
        EasyAlertDialogHelper.createOkCancelDiolag(this, null, getString(R.string.session_clear_tip), true, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {

            }

            @Override
            public void doOkAction() {
                clearChattingHistory(sessionId, SessionTypeEnum.P2P);
                MessageListPanelHelper.getInstance().notifyClearMessages(sessionId);
                Toast.makeText(getApplicationContext() , R.string.session_clear_success , Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_chat_clear_history:
                doClearCache(account);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_NORMAL) {
                final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
                if (selected != null && !selected.isEmpty()) {
                    TeamCreateHelper.createNormalTeam(MessageInfoActivity.this, selected, true, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            UmengAnalyticsEvent.onEventGroupCount(getApplicationContext());
                            finish();
                        }

                        @Override
                        public void onFailed(int code) {

                        }

                        @Override
                        public void onException(Throwable exception) {

                        }
                    });
                } else {
                    Toast.makeText(DemoCache.getContext(), R.string.group_select_member_must_leastone, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
