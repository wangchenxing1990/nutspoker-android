package com.netease.nim.uikit.session.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.chesscircle.helper.MessageConfigHelper;
import com.netease.nim.uikit.chesscircle.helper.MessageFilter;
import com.netease.nim.uikit.chesscircle.interfaces.OnClubConfigChangeListener;
import com.netease.nim.uikit.chesscircle.interfaces.OnEditModeListener;
import com.netease.nim.uikit.chesscircle.recevice.CustomMessageUpdateReceiver;
import com.netease.nim.uikit.chesscircle.view.AudioConstant;
import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.util.MD5;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.interfaces.IGameChange;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.actions.ImageAction;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.module.Container;
import com.netease.nim.uikit.session.module.ModuleProxy;
import com.netease.nim.uikit.session.module.input.InputPanel;
import com.netease.nim.uikit.session.module.list.MessageListPanel;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.MessageReceipt;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面基类
 * <p/>
 * Created by huangjun on 2015/2/1.
 */
public class MessageFragment extends TFragment implements ModuleProxy {
    public IGameChange mGameCreateInterface;
    private View rootView;
    private SessionCustomization customization;
    protected static final String TAG = "MessageActivity";
    // 聊天对象
    protected String sessionId; // p2p对方Account或者群id
    protected SessionTypeEnum sessionType;
    // modules
    protected InputPanel inputPanel;
    protected MessageListPanel messageListPanel;
    private boolean isGameTeam = false;
    private int newMessageNum = 0;
    OnClubConfigChangeListener mOnClubConfigChangeListener;
    OnEditModeListener mOnEditModeListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void setOnClubConfigChangeListener(OnClubConfigChangeListener listener) {
        mOnClubConfigChangeListener = listener;
    }

    public void setOnEditModeListener(OnEditModeListener listener) {
        mOnEditModeListener = listener;
    }

    /**
     * 设置为游戏聊天，用于不推送消息
     *
     * @param isGameTeam
     */
    public void setGameTeam(boolean isGameTeam) {
        this.isGameTeam = isGameTeam;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof IGameChange) {
            mGameCreateInterface = (IGameChange) getActivity();
        }
        parseIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActionBarActivity = (TActionBarActivity) getActivity();
        rootView = inflater.inflate(R.layout.nim_message_fragment, container, false);

        rootView.findViewById(R.id.message_activity_list_view_container).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                LogUtil.e("MessageFragment","left="+left
//                        +",top="+top
//                        +",right="+right
//                        +",bottom="+bottom
//                        +",oldLeft="+oldLeft
//                        +",oldTop="+oldTop
//                        +",oldRight="+oldRight
//                        +",oldBottom="+oldBottom);
//
//                LogUtil.e("MessageFragment","OrignalBottom="+mOrignalBottom+",mLastBottom="+mLastBottom);
                if(mOrignalBottom != 0){
                    if (bottom > mLastBottom && bottom >= mOrignalBottom){//底边工具收起
                        if(mActionBarActivity != null)
                            mActionBarActivity.onBottomBoardShow(false);
                    }
                    else if (bottom < mLastBottom && mLastBottom >= mOrignalBottom){//底边工具展开
                        if(mActionBarActivity != null)
                            mActionBarActivity.onBottomBoardShow(true);
                    }
                }

                //第一次初始化Bottom，这里针对聊天界面做的优化，如果有其他界面，不合适
                if(oldBottom == 0 && mOrignalBottom == 0){
                    mOrignalBottom = bottom;
                }

                mLastBottom = bottom;
            }
        });

        return rootView;
    }

    /**
     * ***************************** life cycle *******************************
     */

    @Override
    public void onPause() {
        super.onPause();

        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE,
                SessionTypeEnum.None);
        inputPanel.onPause();
        messageListPanel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        messageListPanel.onResume();
        NIMClient.getService(MsgService.class).setChattingAccount(sessionId, sessionType);
        getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL); // 默认使用听筒播放
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        messageListPanel.onDestroy();
        if (inputPanel != null) {
            inputPanel.onDestroy();
        }
        registerObservers(false);
    }

    public boolean onBackPressed() {
        if (inputPanel != null && inputPanel.collapse(true)) {
            return true;
        }

        if (messageListPanel != null && messageListPanel.onBackPressed()) {
            return true;
        }
        return false;
    }

    public void refreshMessageList() {
        if (messageListPanel != null) {
            messageListPanel.refreshMessageList();
        }
    }

    private void parseIntent() {
        sessionId = getArguments().getString(Extras.EXTRA_ACCOUNT);
        sessionType = (SessionTypeEnum) getArguments().getSerializable(Extras.EXTRA_TYPE);
        //未读消息数量
        newMessageNum = getArguments().getInt(Extras.EXTRA_NEW_MESSAGE_NUM, 0);
        LogUtil.d(TAG, "newMessageNum:" + newMessageNum);

        customization = (SessionCustomization) getArguments().getSerializable(Extras.EXTRA_CUSTOMIZATION);
        Container container = new Container(getActivity(), sessionId, sessionType, this);

        if (messageListPanel == null) {
            messageListPanel = new MessageListPanel(container, rootView, false, false, sessionType, newMessageNum);
        } else {
            messageListPanel.reload(container, null);
        }

        if (inputPanel == null) {
            if (sessionType == SessionTypeEnum.P2P && DealerConstant.isDealer(sessionId)) {
                //客服模式，不显示 1.语音按妞
                inputPanel = new InputPanel(messageListPanel,container, rootView, getDealerActionList(), true, true);
            } else {
                inputPanel = new InputPanel(messageListPanel,container, rootView, getActionList());
            }
            inputPanel.setCustomization(customization);
            inputPanel.setOnEditModeListener(mOnEditModeListener);
        } else {
            inputPanel.reload(container, customization);
        }

        registerObservers(true);

        if (customization != null) {
            messageListPanel.setChattingBackground(customization.backgroundUri, customization.backgroundColor);
        }
    }

    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected boolean isAllowSendMessage(final IMMessage message) {
        if (message.getSessionType() == SessionTypeEnum.P2P) {
            if (NIMClient.getService(FriendService.class).isMyFriend(sessionId) || DealerConstant.isDealer(sessionId)) {
                //是好友或者是客服，可以发送
                return true;
            } else {
//                Toast.makeText(getActivity().getApplicationContext(), R.string.is_not_friend, Toast.LENGTH_SHORT).show();
//                return false;
                //之前聊天需要时好友，新版本是需要有一方是俱乐部管理员20161205 直接return true
                return true;
            }
        }
        return true;
    }

    /**
     * ****************** 观察者 **********************
     */

    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeReceiveMessage(incomingMessageObserver, register);
        service.observeMessageReceipt(messageReceiptObserver, register);
        //添加了自定义消息更新UI
        if (getActivity() != null) {
            if (register) {
                IntentFilter intentFilter = new IntentFilter(CustomMessageUpdateReceiver.ACTION_CUSTOMMESSAGE_UPDATE);
                getActivity().registerReceiver(mCustomMessageUpdateReceiver, intentFilter);
            } else {
                getActivity().unregisterReceiver(mCustomMessageUpdateReceiver);
            }
        }
    }

    /**
     * 自定义消息更新通知
     */
    CustomMessageUpdateReceiver mCustomMessageUpdateReceiver = new CustomMessageUpdateReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent != null) {
                IMMessage imMessage = (IMMessage) intent.getSerializableExtra(Extras.EXTRA_MESSAGE);
                if (imMessage != null && imMessage.getSessionId().equals(sessionId) && messageListPanel != null) {
                    LogUtil.d(TAG, "更新自定义消息状态： " + imMessage.getUuid());
                    LogUtil.i("zzh", "messagefragment mCustomMessageUpdateReceiver: " + imMessage.getContent()/* + " " + imMessage.getFromAccount() + " "  + imMessage.getFromNick() + " " + imMessage.getPushContent() + " " + imMessage.getSessionId() + "\n"
                            + " " + imMessage.getUuid() + " " + imMessage.getAttachment() + " " + imMessage.getAttachStatus() + " " + imMessage.getConfig() + "\n"
                            + " " + imMessage.getDirect() + " " + imMessage.getLocalExtension() + " " + imMessage.getMsgType() + " " + imMessage.getPushPayload() + " " + imMessage.getRemoteExtension() + "\n"
                            + " " + imMessage.getSessionType() + " " + imMessage.getStatus() + " " + imMessage.getTime()
                    */);
                    //当前的会话，改变列表
                    messageListPanel.updateMessageStatus(imMessage);
                }
            }
        }
    };

    public List<IMMessage> getMessages() {
        return messageListPanel == null ? null : messageListPanel.items;
    }

    public void notifydatasetChanged(IMMessage message, int position) {
        if (messageListPanel != null && messageListPanel.adapter != null && getMessages() != null && getMessages().size() > position) {
            messageListPanel.items.add(position, message);
            messageListPanel.items.remove(position + 1);
            messageListPanel.adapter.notifyDataSetChanged();
        }
    }

    /**
     * 消息接收观察者
     */
    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {

            if (messages == null || messages.isEmpty()) {
                return;
            }
            int size = messages.size();
            for (int i = (size - 1); i >= 0; i--) {
                IMMessage imMessage = messages.get(i);
                LogUtil.d(TAG, "收到：incomingMessageObserver，消息   getUuid:" + imMessage.getUuid() + "   getContent" + imMessage.getContent());
                if (messageListPanel.isMyMessage(imMessage) && AudioConstant.isGameAudioMessage(imMessage)) {
                    //1.是我的俱乐部消息 2.游戏内语音,删除不加入列表
                    LogUtil.d(TAG, "收到：游戏内语音，删除:" + imMessage.getUuid());
                    messages.remove(imMessage);
                } else if (messageListPanel.isMyMessage(imMessage) && MessageFilter.isClubChangeNotShow(imMessage)) {
                    //系统消息（不显示的）
                    messages.remove(imMessage);
                    if (mOnClubConfigChangeListener != null) {
                        //通知Activity
                        mOnClubConfigChangeListener.configChanged();
                    }
                }
            }
            if (messages.isEmpty()) {
                return;
            }
            messageListPanel.onIncomingMessage(messages);
            sendMsgReceipt(); // 发送已读回执
            if (mGameCreateInterface != null) {
                mGameCreateInterface.onGameCreate(messages);
            }
        }
    };

    private Observer<List<MessageReceipt>> messageReceiptObserver = new Observer<List<MessageReceipt>>() {
        @Override
        public void onEvent(List<MessageReceipt> messageReceipts) {
            receiveReceipt();
        }
    };

    /**
     * ********************** implements ModuleProxy *********************
     */
    @Override
    public boolean sendMessage(IMMessage message) {
        message.setRemoteExtension(MD5.getP2PExtension(message.getUuid()));
        LogUtil.i("zzh", "发送消息sendMessage" + " getContent:" + message.getContent() +
                "  getFromAccount:" + message.getFromAccount() +
                "  getFromNick:"  + message.getFromNick() +
                "  getPushContent:" + message.getPushContent() +
                "  getSessionId:" + message.getSessionId() + "\n" +
                "  getUuid:" + message.getUuid() +
                "  getAttachment:" + message.getAttachment() +
                "  getAttachStatus:" + message.getAttachStatus() +
                "  getConfig:" + message.getConfig() + "\n" +
                "  getDirect:" + message.getDirect() +
                "  getLocalExtension:" + message.getLocalExtension() +
                "  getMsgType:" + message.getMsgType() +
                "  getPushPayload:" + message.getPushPayload() +
                "  getRemoteExtension:" + message.getRemoteExtension() + "\n" +
                "  getSessionType:" + message.getSessionType() +
                "  getStatus:" + message.getStatus() +
                "  getTime:" + message.getTime()
        );
        if (!isAllowSendMessage(message)) {
            return false;
        }
        // send message to server and save to db
        NIMClient.getService(MsgService.class).sendMessage(message, false);
        if (isGameTeam) {
            message.setConfig(MessageConfigHelper.getGameTeamMessageConfig(message.getConfig()));
        }
        if (messageListPanel != null) {
            messageListPanel.onMsgSend(message);
        }
        return true;
    }

    @Override
    public void onInputPanelExpand() {
        messageListPanel.scrollToBottom();
    }

    @Override
    public void shouldCollapseInputPanel() {
        if (inputPanel != null) {
            inputPanel.collapse(false);
        }
    }

    @Override
    public boolean isLongClickEnabled() {
        return inputPanel == null ? true : !inputPanel.isRecording();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (inputPanel != null) {
            inputPanel.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 操作面板集合
    protected List<BaseAction> getActionList() {
        List<BaseAction> actions = new ArrayList<>();
//        actions.add(new ImageAction());
//        actions.add(new VideoAction());
//        actions.add(new LocationAction());
//        List<BaseAction> actions = null;
        if (customization != null && customization.actions != null) {
            if (actions == null) {
                actions = new ArrayList<>();
            }
            actions.addAll(customization.actions);
        }
        return actions;
    }

    //刷新Action工具
    public void updateActionPanelLayout(List<BaseAction> actions) {
        if (inputPanel != null) {
            LogUtil.d("ActionsPanel", "messageFragment updateActionPanelLayout");
            inputPanel.updateActionPanelLayout(actions);
        }
    }

    /**
     * 发送已读回执
     */
    public void sendMsgReceipt() {
        messageListPanel.sendReceipt();
    }

    /**
     * 收到已读回执
     */
    public void receiveReceipt() {
        messageListPanel.receiveReceipt();
    }

    // 客服：操作面板集合
    protected List<BaseAction> getDealerActionList() {
        List<BaseAction> actions = new ArrayList<>();
        actions.add(new ImageAction());
        return actions;
    }

    //记录原始的Bottom，用于检测底边的工具弹出情况
    TActionBarActivity mActionBarActivity;
    int mOrignalBottom = 0;
    int mLastBottom = 0;
}
