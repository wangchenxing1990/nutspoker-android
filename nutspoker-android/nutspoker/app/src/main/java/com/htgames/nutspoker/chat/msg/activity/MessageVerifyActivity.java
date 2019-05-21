package com.htgames.nutspoker.chat.msg.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.MessageDataCache;
import com.htgames.nutspoker.chat.contact.activity.AddFriendActivity;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.helper.MessageTipHelper;
import com.htgames.nutspoker.chat.msg.adapter.SystemMessageAdapter;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.action.FriendRelationshipAction;
import com.htgames.nutspoker.ui.activity.Club.ClubApplyDetail;
import com.htgames.nutspoker.ui.activity.Club.ClubRejectActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.interfs.RejectInterface;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.view.ResultDataView;
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.listview.ListViewUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.AddFriendNotify;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageStatus;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 消息处理
 */
public class MessageVerifyActivity extends BaseActivity implements SystemMessageAdapter.SystemMessageListener{

    private final static String TAG = "MessageVerifyActivity";

    public static final String Extra_Pass = "com.htgames.chesscircle.chat.msg.activity.MessageVerifyActivity.Extra_Pass";

    public int type_message = SystemMessageHelper.TYPE_MESSAGE_FRIEND;
    private ListView lv_message;
    SystemMessageAdapter mSystemMessageAdapter;
    ArrayList<SystemMessage> messagesList;
    String teamId = "";
    FriendRelationshipAction mFriendRelationshipAction;
    ResultDataView mResultDataView;
    private UserInfoObservable.UserInfoObserver userInfoObserver;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_verify);
        initView();
        mResultDataView.showLoading();
        type_message = getIntent().getIntExtra(Extras.EXTRA_MESSAGE_TYPE, SystemMessageHelper.TYPE_MESSAGE_FRIEND);
        if(type_message == SystemMessageHelper.TYPE_MESSAGE_FRIEND){
            setHeadTitle(R.string.contancts_column_newfriends);
            setHeadRightButton(R.string.add, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MessageVerifyActivity.this, AddFriendActivity.class));
                }
            });
        }else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY){
            teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
            setHeadTitle(R.string.verify_message);
        }else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE){
            setHeadTitle(R.string.verify_invite);
        }else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_MSG){
            setHeadTitle(R.string.message);
        }
        messagesList = new ArrayList<>();
        messagesList.addAll(SystemMessageHelper.querySystemMessageByType(getApplicationContext() , type_message ,teamId));
        // 注册观察者
        registerSystemMessageObservers(true);
        registerUserInfoChangedObserver(true);
        registerMesaageDealReceiver(true);
        initMessageList();
        mFriendRelationshipAction = new FriendRelationshipAction(this , null);
    }

    private void registerSystemMessageObservers(boolean register) {
        if (register) {
            MessageDataCache.getInstance().registerMessageDataChangedObserverObserver(messageDataChangedObserver);
        } else {
            MessageDataCache.getInstance().unregisterTeamMemberDataChangedObserver(messageDataChangedObserver);
        }
    }

    private MessageDataCache.MessageDataChangedObserver messageDataChangedObserver = new MessageDataCache.MessageDataChangedObserver() {
        @Override
        public void onMessageUpdate(com.netease.nimlib.sdk.msg.model.SystemMessage message) {
//            MessageDataCache.getInstance().setMessageReadStatus(message);
            dealNetMessage(SystemMessageHelper.getSystemMessage(message));
        }
    };

    public void dealNetMessage(SystemMessage updateMessage) {
        if (updateMessage == null) {
            return;
        }
        boolean isUpdate = false;
        lock.writeLock().lock();
        {
            //设置为已读
            if(type_message == SystemMessageHelper.TYPE_MESSAGE_FRIEND && updateMessage.getType() == SystemMessageType.AddFriend){
                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
            } else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY &&
                    (updateMessage.getType() == SystemMessageType.TeamInvite || updateMessage.getType() == SystemMessageType.DeclineTeamInvite)){
                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
            } else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE &&
                    (updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply)){
                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
            }
            else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_MSG &&
                    (updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply))
            {
                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
            }
            for (SystemMessage message : messagesList) {
                if (type_message == SystemMessageHelper.TYPE_MESSAGE_FRIEND && updateMessage.getType() == SystemMessageType.AddFriend) {
                    if (message.getFromAccount().equals(updateMessage.getFromAccount())) {
                        messagesList.remove(message);
                        break;
                    }
                } else if (type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY &&
                        (updateMessage.getType() == SystemMessageType.TeamInvite || updateMessage.getType() == SystemMessageType.DeclineTeamInvite)) {
                    if (message.getTargetId().equals(updateMessage.getTargetId())) {
                        messagesList.remove(message);
                        break;
                    }
                } else if (type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE &&
                        (updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply)) {
                    if (message.getTargetId().equals(updateMessage.getTargetId()) && message.getFromAccount().equals(updateMessage.getFromAccount())) {
                        messagesList.remove(message);
                        break;
                    }
                }
            }
            messagesList.add(0, updateMessage);
            isUpdate = true;
        }
        //
        lock.writeLock().unlock();
        if (isUpdate) {
            refresh();
        }
    }

    private void initMessageList() {
        mSystemMessageAdapter = new SystemMessageAdapter(getApplicationContext(), messagesList);
        mSystemMessageAdapter.setSystemMessageListener(this);
        lv_message.setAdapter(mSystemMessageAdapter);
        refresh();
        //设置为已读
        SystemMessageHelper.resetSystemMessageUnreadCountByType(getApplicationContext() , type_message);
//        for(SystemMessage message : messagesList) {
//            if (message.isUnread()) {
                //如果消息未处理，设置为拓展字段，标识已读
//                MessageDataCache.getInstance().setMessageReadStatus(message);
//            }
//        }
    }

    private void initView() {
        lv_message = (ListView)findViewById(R.id.lv_message);
        mResultDataView = (ResultDataView)findViewById(R.id.mResultDataView);
    }

    public void ackAddFriendRequest(String account , boolean isAccept){
        NIMClient.getService(FriendService.class).ackAddFriendRequest(account, isAccept); // 通过对方的好友请求
    }

    /**
     * 广播接受是否系统消息回调
     * @param message
     * @param pass
     */
    private void onSystemNotificationDeal(final SystemMessage message, final boolean pass) {
        RequestCallback callback = new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                onProcessSuccess(pass, message);
            }

            @Override
            public void onFailed(int code) {
                onProcessFailed(code, message);
            }

            @Override
            public void onException(Throwable exception) {

            }
        };
        if (message.getType() == SystemMessageType.TeamInvite) {
            //验证入群邀请
            if (pass) {
                // 接受邀请
//                NIMClient.getService(TeamService.class).acceptInvite(message.getTargetId(), message.getFromAccount()).setCallback(callback);
                agreeTeamInvite(message , callback);
            } else {
                // 拒绝邀请,可带上拒绝理由
                //接受邀请后，用户真正入群。如果拒绝邀请，邀请该用户的管理员会收到一条系统消息，类型为 SystemMessageType#DeclineTeamInvite。
                NIMClient.getService(TeamService.class).declineInvite(message.getTargetId(), message.getFromAccount(), "").setCallback(callback);
            }
        } else if (message.getType() == SystemMessageType.ApplyJoinTeam) {
            //验证入群申请,用户发出申请后，所有管理员都会收到一条系统消息，类型为 SystemMessageType#TeamApply。管理员可选择同意或拒绝：
            if (pass) {
                // 同意申请
                NIMClient.getService(TeamService.class).passApply(message.getTargetId(), message.getFromAccount()).setCallback(callback);
            } else {
                // 拒绝申请，可填写理由
                //任意一管理员操作后，其他管理员再操作都会失败。
                // 如果同意入群申请，群内所有成员(包括申请者)都会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
                // 如果拒绝申请，申请者会收到一条系统消息，类型为 SystemMessageType#RejectTeamApply。

                ClubRejectActivity.StartActivityFor(this,message);
                //NIMClient.getService(TeamService.class).rejectApply(message.getTargetId(), message.getFromAccount(), "").setCallback(callback);
            }
        } else if (message.getType() == SystemMessageType.AddFriend) {
            if(pass){
                //同意添加好友
                agreeAddFriend(message);
            } else{
                NIMClient.getService(FriendService.class).ackAddFriendRequest(message.getFromAccount(), pass).setCallback(callback);
            }
        }
    }

    /**
     * 同意俱乐部邀请
     * @param message
     */
    public void agreeTeamInvite(final SystemMessage message , final RequestCallback callback){
        if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            onMessageInit(message);
            return;
        }
        DialogMaker.showProgressDialog(this, null, false).setCanceledOnTouchOutside(false);
        TeamDataCache.getInstance().searchTeamById(message.getTargetId(), new SimpleCallback<Team>() {
            @Override
            public void onResult(boolean success, Team team) {
                DialogMaker.dismissProgressDialog();
                LogUtil.i(TAG, "success :" + success);
                if (success) {
                    if (ClubConstant.isClubMemberIsFull(team)) {
                        Toast.makeText(getApplicationContext(), R.string.club_member_count_limit, Toast.LENGTH_SHORT).show();
                        onMessageInit(message);
                    } else {
                        NIMClient.getService(TeamService.class).acceptInvite(message.getTargetId(), message.getFromAccount()).setCallback(callback);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.taem_invite_agree_failure, Toast.LENGTH_SHORT).show();
                    onMessageInit(message);
                }
            }
        });
    }

    /**
     * 同意添加好友
     * @param message
     */
    public void agreeAddFriend(final SystemMessage message){
        mFriendRelationshipAction.addFriend(message.getFromAccount(), new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    onProcessSuccess(true, message);
                } else {
                    SystemMessageStatus status = SystemMessageStatus.expired;
                    SystemMessageHelper.setSystemMessageStatus(getApplicationContext() , message.getMessageId(), status);
//                    NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(), status);
                    message.setStatus(status);
                    refreshViewHolder(message);
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void onAgree(SystemMessage message) {
        onSystemNotificationDeal(message, true);
    }

    RejectInterface mRejectListener;
    @Override
    public void onReject(SystemMessage message, RejectInterface listener) {
        mRejectListener = listener;
        onSystemNotificationDeal(message, false);
    }

    @Override
    public void onClick(SystemMessage message) {
        if(message.getType() == SystemMessageType.AddFriend || message.getType() == SystemMessageType.ApplyJoinTeam)
        {
            int from = UserProfileActivity.FROM_OTHER_LIST;
            if(message.getType() == SystemMessageType.AddFriend){
                //如果是好友邀请，并且通过好友邀请，点击查看好友信息
                if (message.getAttachObject() != null) {
                    AddFriendNotify attachData = (AddFriendNotify) message.getAttachObject();
                    if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_DIRECT ||
                            attachData.getEvent() == AddFriendNotify.Event.RECV_AGREE_ADD_FRIEND ||
                            attachData.getEvent() == AddFriendNotify.Event.RECV_REJECT_ADD_FRIEND) {
                        // 对方直接加你为好友，对方通过你的好友请求，对方拒绝你的好友请求
                    } else if (attachData.getEvent() == AddFriendNotify.Event.RECV_ADD_FRIEND_VERIFY_REQUEST && message.getStatus() == SystemMessageStatus.init) {
                        // 好友验证请求,并且状态是未处理的
                        from = UserProfileActivity.FROM_MESSAGE_ADDFRIEND;
                    }
                }

            } else if(message.getType() == SystemMessageType.ApplyJoinTeam  && message.getStatus() == SystemMessageStatus.init){
                //俱乐部申请
                //from = UserProfileActivity.FROM_MESSAGE_CLUBAPPLY;

                //去新的
                //UserProfileActivity.start(this , message.getFromAccount() , from , message);
                ClubApplyDetail.StartActivity(this,message);
                return ;
            }

            UserProfileActivity.start(this , message.getFromAccount() , from , message);
        }
        else if(message.getType() == SystemMessageType.RejectTeamApply)//拒绝申请
        {
            ClubApplyDetail.StartActivity(this,message);
        }
        else
        {
            //俱乐部
            String clubId = message.getTargetId();
            int from = ClubInfoActivity.FROM_TYPE_LIST;
            if (message.getType() == SystemMessageType.TeamInvite && message.getStatus() == SystemMessageStatus.init){
                //俱乐部邀请
                from = ClubInfoActivity.FROM_TYPE_MESSAGE_INVITE;
            }
//            ClubInfoActivity.start(this , clubId , from ,message);
        }
    }

    @Override
    public void onLongPressed(SystemMessage message) {
        showLongClickMenu(message);
    }

    private void onProcessSuccess(final boolean pass, SystemMessage message) {
        SystemMessageStatus status = pass ? SystemMessageStatus.passed : SystemMessageStatus.declined;
        SystemMessageHelper.setSystemMessageStatus(getApplicationContext(), message.getMessageId(), status);
//        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(), status);
        message.setStatus(status);
        refreshViewHolder(message);
//        MessageDataCache.getInstance().updateSystemMessageStatus(message, status);
        onSuccessMessage(pass, message);
    }

    /**
     * 处理结果的相关消息提醒功能
     * @param pass
     * @param message
     */
    public void onSuccessMessage(final boolean pass, final SystemMessage message){
        if(pass && message.getType() == SystemMessageType.AddFriend) {
            // 通过好友请求 发送消息：申请者收到
            final IMMessage successMessage = MessageBuilder.createTextMessage(
                    message.getFromAccount(), // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                    SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                    getString(R.string.agree_addfriend_invite_message) // 文本内容
            );
            // 发送消息,自己本地删除
            NIMClient.getService(MsgService.class).sendMessage(successMessage  , true).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(successMessage);
                }

                @Override
                public void onFailed(int i) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(successMessage);
                }

                @Override
                public void onException(Throwable throwable) {
                    NIMClient.getService(MsgService.class).deleteChattingHistory(successMessage);
                }
            });
            // 添加好友成功 保存消息：同意者收到
            MessageTipHelper.saveAddFriendTipMessage(message.getFromAccount(), message.getFromAccount(), SessionTypeEnum.P2P);
        }
    }

    private void onProcessFailed(final int code, SystemMessage message) {
        LogUtil.i(TAG ,  "failed, error code=" + code);
        if(message.getType() == SystemMessageType.TeamInvite){
            Toast.makeText(getApplicationContext() , R.string.taem_invite_agree_failure , Toast.LENGTH_SHORT).show();
        } else if(message.getType() == SystemMessageType.ApplyJoinTeam){
            Toast.makeText(getApplicationContext() , R.string.taem_apply_agree_failure , Toast.LENGTH_SHORT).show();
        } else if(message.getType() == SystemMessageType.AddFriend){
            Toast.makeText(getApplicationContext() , R.string.addfriend_agree_failure , Toast.LENGTH_SHORT).show();
        }
        SystemMessageStatus status = SystemMessageStatus.expired;
//        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(), status);
        SystemMessageHelper.setSystemMessageStatus(getApplicationContext(), message.getMessageId(), status);
        message.setStatus(status);
        refreshViewHolder(message);
    }

    /**
     * 将消息初始化，用于网络错误，恢复初始设置
     * @param message
     */
    public void onMessageInit(SystemMessage message) {
        SystemMessageStatus status = SystemMessageStatus.init;
        SystemMessageHelper.setSystemMessageStatus(getApplicationContext(), message.getMessageId(), status);
//        NIMClient.getService(SystemMessageService.class).setSystemMessageStatus(message.getMessageId(), status);
        message.setStatus(status);
        refreshViewHolder(message);
    }

    public void refreshViewHolder(final SystemMessage message){
        final long messageId = message.getMessageId();
        int index = -1;
        if(messagesList == null){
            return;
        }
        for (int i = 0; i < messagesList.size(); i++) {
            SystemMessage item = messagesList.get(i);
            if (messageId == item.getMessageId()) {
                item.setStatus(message.getStatus());//改变状态
                index = i;
                break;
            }
        }
        if (index < 0) {
            return;
        }
        final int m = index;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (m < 0) {
                    return;
                }
                Object tag = ListViewUtil.getViewHolderByIndex(lv_message, m);
                if (tag instanceof SystemMessageAdapter.ViewHolder) {
                    mSystemMessageAdapter.refreshDirectly(tag, message);
                }
            }
        });
    }

//    private void registerSystemObserver(boolean register) {
//        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(new Observer<SystemMessage>() {
//            @Override
//            public void onEvent(SystemMessage message) {
//                // 收到被删除好友的通知，不要提醒
////                if (message.getType() == SystemMessageType.DeleteFriend) {
////                    message.setUnread(false);
////                    return;
////                }
//
//                if (!messagesList.contains(message)) {
//                    messagesList.add(0, message);
//                }
////                if (!ContactDataCache.getInstance().hasUser(message.getFromAccount())) {
////                    List<String> accounts = new ArrayList<>();
////                    accounts.add(message.getFromAccount());
////                    requestUnknowUser(accounts);
////                }
//                refresh();
//            }
//        }, register);
//    }

    private void refresh() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (messagesList == null || messagesList.size() == 0) {
                    lv_message.setVisibility(View.GONE);
                    showNullView();
                } else {
                    lv_message.setVisibility(View.VISIBLE);
                    mResultDataView.successShow();
                }
                if (mSystemMessageAdapter != null) {
                    mSystemMessageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void showNullView(){
        if(type_message == SystemMessageHelper.TYPE_MESSAGE_FRIEND){
            mResultDataView.nullDataShow(R.string.friend_new_null, R.mipmap.img_friend_null);
        }else if(type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY
                || type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE
                || type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_MSG){
            mResultDataView.nullDataShow(R.string.message_null, R.mipmap.img_message_null);
        }
    }

    private void requestUnknowUser(List<String> accounts) {
//        ContactDataCache.getInstance().getUsersFromRemote(accounts, new IContactHttpCallback<List<User>>() {
//            @Override
//            public void onSuccess(List<User> users) {
//                if (users != null && !users.isEmpty()) {
//                    refresh();
//                }
//            }
//
//            @Override
//            public void onFailed(int code, String errorMsg) {
//
//            }
//        });
    }

    /**
     * 删除所有的消息记录
     */
    private void deleteAllMessages() {
//        MessageDataCache.getInstance().clearSystemMessages();
        SystemMessageHelper.clearSystemMessages(getApplicationContext());
        messagesList.clear();
        refresh();
        Toast.makeText(getApplicationContext(), R.string.clear_all_success, Toast.LENGTH_SHORT).show();
    }

    private void showLongClickMenu(final SystemMessage message) {
        CustomAlertDialog alertDialog = new CustomAlertDialog(this);
        alertDialog.setTitle(R.string.delete_tip);
        String title = getString(R.string.delete_system_message);
        alertDialog.addItem(title, new CustomAlertDialog.onSeparateItemClickListener() {
            @Override
            public void onClick() {
                deleteSystemMessage(message);
            }
        });
        alertDialog.show();
    }

    /** 删除系统消息*/
    public void deleteSystemMessage(final SystemMessage message) {
//        MessageDataCache.getInstance().deleteSystemMessage(message);
        SystemMessageHelper.deleteSystemMessage(getApplicationContext() , message);
        messagesList.remove(message);
        refresh();
        Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if(messagesList != null){
            messagesList.clear();
            messagesList = null;
        }
        if(mFriendRelationshipAction != null){
            mFriendRelationshipAction.onDestroy();
            mFriendRelationshipAction = null;
        }
        registerUserInfoChangedObserver(false);
        registerSystemMessageObservers(false);
        registerMesaageDealReceiver(false);
        super.onDestroy();
    }

    /**
     * 注册用户资料更新监听
     * @param register
     */
    private void registerUserInfoChangedObserver(boolean register) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                    @Override
                    public void onUserInfoChanged(List<String> accounts) {
                        if(mSystemMessageAdapter != null){
                            mSystemMessageAdapter.notifyDataSetChanged();
                        }
                    }
                };
            }
            UserInfoHelper.registerObserver(userInfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    MesaageDealReceiver mesaageDealReceiver;
    IntentFilter filter;
    public final static String ACTION_MESSAGE_DEAL = DemoCache.getContext().getPackageName() + ".action.MesaageDealReceiver";

    /**
     * 动态注册消息处理广播监听
     * @param register
     */
    public void registerMesaageDealReceiver(boolean register){
        if(filter == null){
           filter = new IntentFilter(ACTION_MESSAGE_DEAL);
        }
        if(mesaageDealReceiver == null){
            mesaageDealReceiver = new MesaageDealReceiver();
        }
        if(register){
            registerReceiver(mesaageDealReceiver , filter);
        }else{
            unregisterReceiver(mesaageDealReceiver);
        }
    }

    /**
     * 消息处理广播监听（用于其他界面操作返回后，状态变化）
     */
    public class MesaageDealReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                SystemMessage systemMessage = (SystemMessage)intent.getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);
                boolean pass = intent.getBooleanExtra(Extra_Pass,true);
                if(systemMessage != null){
                    onProcessSuccess(pass , systemMessage);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ClubRejectActivity.RequestCode_Reject)
        {
            boolean needRequest = resultCode == RESULT_OK;
            mRejectListener.onChangeStatus(needRequest);
            if(needRequest)
            {
                final SystemMessage msg = (SystemMessage)data.getSerializableExtra(ClubRejectActivity.ResultData_Msg);
                String reason = data.getStringExtra(ClubRejectActivity.ResultData_Reason);

                if(msg != null) {
                    NIMClient.getService(TeamService.class).rejectApply(msg.getTargetId(), msg.getFromAccount(), reason).setCallback(new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void param) {
                            onProcessSuccess(false, msg);
                        }

                        @Override
                        public void onFailed(int code) {
                            onProcessFailed(code, msg);
                        }

                        @Override
                        public void onException(Throwable exception) {
                            onProcessFailed(-1, msg);
                        }
                    });
                }
            }
        }
    }
}
