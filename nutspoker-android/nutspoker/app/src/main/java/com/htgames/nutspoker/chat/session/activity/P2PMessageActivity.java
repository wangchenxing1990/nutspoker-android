package com.htgames.nutspoker.chat.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.fragment.MessageFragment;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.List;


/**
 * 点对点聊天界面
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class P2PMessageActivity extends BaseMessageActivity {
    private RelativeLayout invalidTeamTipView;
    TextView tv_invalid_tip;
    TextView btn_invalid_action;
    ImageView iv_head_mute;
    boolean isDealer = false;//是否是客服

    public static void StartActivity(Context context,int type,PaipuEntity paipu){
        Intent intent = new Intent(context, P2PMessageActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", paipu);
        context.startActivity(intent);
    }

    public static void start(Context context, String contactId, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, P2PMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String contactId, int newMessageNum , SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, contactId);
        intent.putExtra(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, P2PMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        isDealer = DealerConstant.isDealer(sessionId);
        initViews();
        initInvalidTipView();
        // 单聊特例话数据，包括个人信息，
        requestBuddyInfo(false);
        registerObservers(true);
        registerMsgUnreadInfoObserver(true);
        updateNewChatUI(btn_head_back, ReminderManager.getInstance().getUnreadSessionCount());//获取未读消息数量
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int type = intent.getIntExtra("type", 0);
            if (type == CircleConstant.TYPE_PAIPU) {
                PaipuEntity paipuEntity = (PaipuEntity) intent.getSerializableExtra("data");
                PaipuAttachment attachment = new PaipuAttachment(paipuEntity.jsonDataStr);
                IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.P2P, getString(R.string.msg_custom_type_paipu_info_desc), attachment);
                messageFragment.sendMessage(message);
            }
        }
    }

    public void initInvalidTipView() {
        invalidTeamTipView = (RelativeLayout) findViewById(R.id.rl_invalid_tip);
        tv_invalid_tip = (TextView) findViewById(R.id.tv_invalid_tip);
        btn_invalid_action = (TextView) findViewById(R.id.btn_invalid_action);
        tv_invalid_tip.setText(R.string.session_tip_friend_not);
        btn_invalid_action.setText(R.string.invalid_action_addfriend);
        invalidTeamTipView.setVisibility(View.GONE);
        btn_invalid_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVerifyActivity.start(P2PMessageActivity.this, sessionId, AddVerifyActivity.TYPE_VERIFY_FRIEND);
            }
        });
    }

    public void updataInvalidTipView() {
        if (!isDealer) {
            if (NIMClient.getService(FriendService.class).isMyFriend(sessionId)) {
                //是我的朋友
                invalidTeamTipView.setVisibility(View.GONE);
                setHeadRightButtonVisible();
            } else {
                //不是我的朋友
//                invalidTeamTipView.setVisibility(View.VISIBLE);
                invalidTeamTipView.setVisibility(View.GONE);//之前聊天需要时好友，新版本是需要有一方是俱乐部管理员20161205
                setHeadRightButtonGone();
            }
        } else {
//            setHeadRightButton(R.string.help, new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    WebViewActivity.start(P2PMessageActivity.this ,WebViewActivity.TYPE_HELP , ApiConstants.URL_HELP);
//                }
//            }); // : 17/2/27  客服右上角不跳h5的页面了
        }
    }

    /**
     * 注册未读消息数量观察者
     */
    private void registerMsgUnreadInfoObserver(boolean register) {
        if (register) {
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);
        }
    }

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item.getId() == ReminderId.SESSION) {
                updateNewChatUI(btn_head_back, item.getUnread());
            } else if (item.getId() == ReminderId.CONTACT) {
            }
        }
    };

    protected void initViews() {
        if (!isDealer) {
            setHeadRightButton(R.string.details, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageInfoActivity.start(P2PMessageActivity.this, sessionId);
                }
            });
        }
        btn_head_back = (TextView) findViewById(R.id.btn_head_back);
        iv_head_mute = (ImageView) findViewById(R.id.iv_head_mute);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updataInvalidTipView();
        updataUserInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
        registerMsgUnreadInfoObserver(false);
    }

    private void requestBuddyInfo(boolean isInput) {
//        setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        if (isInput) {
            setHeadTitle("对方正在输入..");
        } else {
            setHeadTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        }
    }

    private void registerObservers(boolean register) {
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(commandObserver, register);
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
//            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            updataUserInfo();
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
//            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            updataUserInfo();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
//            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            updataUserInfo();
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
//            setTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
            updataUserInfo();
        }
    };

    public void updataUserInfo() {
        setHeadTitle(UserInfoHelper.getUserTitleName(sessionId, SessionTypeEnum.P2P));
        if (isDealer && !NimUserInfoCache.getInstance().hasUser(sessionId)) {
            //如果是客服，第一次进去需要拿去客服的信息
            setHeadTitle(DealerConstant.getDealerNickname(sessionId));
            NimUserInfoCache.getInstance().getUserInfoFromRemote(sessionId, new RequestCallback<NimUserInfo>() {
                @Override
                public void onSuccess(NimUserInfo userInfo) {
                    if (userInfo != null) {
                        setHeadTitle(userInfo.getName());
                    }
                }

                @Override
                public void onFailed(int i) {

                }

                @Override
                public void onException(Throwable throwable) {

                }
            });
        }
        updataInvalidTipView();
        if (NIMClient.getService(FriendService.class).isNeedMessageNotify(sessionId)) {
            //有消息提醒
            iv_head_mute.setVisibility(View.GONE);
        } else {
            iv_head_mute.setVisibility(View.VISIBLE);
        }
    }

    private UserInfoObservable.UserInfoObserver uinfoObserver;

    private void registerUserInfoObserver() {
        if (uinfoObserver == null) {
            uinfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    if (accounts.contains(sessionId)) {
                        requestBuddyInfo(false);
                    }
                }
            };
        }
        UserInfoHelper.registerObserver(uinfoObserver);
    }

    private void unregisterUserInfoObserver() {
        if (uinfoObserver != null) {
            UserInfoHelper.unregisterObserver(uinfoObserver);
        }
    }

    /**
     * 命令消息接收观察者
     */
    Observer<CustomNotification> commandObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            if (!sessionId.equals(message.getSessionId()) || message.getSessionType() != SessionTypeEnum.P2P) {
                return;
            }
            showCommandMessage(message);
        }
    };

    protected void showCommandMessage(CustomNotification message) {
        String content = message.getContent();
        try {
            JSONObject json = JSON.parseObject(content);
            int id = json.getIntValue("id");
            if (id == 1) {
                // 正在输入
//                requestBuddyInfo(true);
//                requestBuddyInfo(false);
            } else {
//                requestBuddyInfo(false);
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected MessageFragment fragment() {
        Bundle arguments = getIntent().getExtras();
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.P2P);
        arguments.putInt(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_p2p_message;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.i("paipu", "onActivityResult");
        if (requestCode == REQUESTCODE_GAMEBILL && resultCode == Activity.RESULT_OK && data != null) {
            //分享牌局记录
            GameBillEntity gameBillEntity = (GameBillEntity) data.getSerializableExtra("bill");
            BillAttachment attachment = new BillAttachment(gameBillEntity.jsonStr);
//            IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.P2P, gameBillEntity.getJsonStr() , attachment);
            IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.P2P, getString(R.string.msg_custom_type_paiju_info_desc), attachment);
            messageFragment.sendMessage(message);
        } else if (requestCode == REQUESTCODE_PAIPU && resultCode == Activity.RESULT_OK && data != null) {
            //分享牌谱记录
            PaipuEntity paipuEntity = (PaipuEntity) data.getSerializableExtra("data");
            PaipuAttachment attachment = new PaipuAttachment(paipuEntity.jsonDataStr);
            IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.P2P, getString(R.string.msg_custom_type_paipu_info_desc), attachment);
            messageFragment.sendMessage(message);
        }
    }
}
