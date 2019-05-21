package com.htgames.nutspoker.chat.msg.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.fragment.main.DiscoveryFragment;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.MessageDataCache;
import com.htgames.nutspoker.chat.app_msg.helper.HordeMessageHelper;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.helper.MessageTipHelper;
import com.htgames.nutspoker.chat.msg.adapter.SystemMessageAdapter;
import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.htgames.nutspoker.chat.msg.model.HordeMessageType;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.chat.notification.constant.CustomNotificationConstants;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.activity.Club.ClubApplyDetail;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
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
import com.netease.nim.uikit.nav.UIUrl;
import com.netease.nim.uikit.nav.UrlConstants;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by 周智慧 on 16/12/6.
 * 代码来自MessageVerifyActivity.java, 之前是同一个activity根据消息类型显示不同的页面，新版本是在同一个页面显示所有的俱乐部相关消息
 */
@UIUrl(urls = UrlConstants.CLUB_MESSAGE_VERIFY)
public class MessageVerifyAC extends BaseActivity implements SystemMessageAdapter.SystemMessageListener {
    private final static String TAG = "MessageVerifyAC";
    public static final String Extra_Pass = "com.htgames.chesscircle.chat.msg.activity.MessageVerifyAC.Extra_Pass";
    private ListView lv_message;
    SystemMessageAdapter mSystemMessageAdapter;
    ArrayList<SystemMessage> messagesList = new ArrayList<>();
    ResultDataView mResultDataView;
    HordeAction mHordeAction;
    private UserInfoObservable.UserInfoObserver userInfoObserver;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_verify);
        mHordeAction = new HordeAction(this, null);
        initView();
        mResultDataView.showLoading();
        setHeadTitle(R.string.verify_message);
        messagesList.addAll(HordeMessageHelper.queryHordeMessageByType(getApplicationContext(), HordeMessageHelper.SEARCH_TYPE_ALL));
        messagesList.addAll(SystemMessageHelper.querySystemMessageByType(getApplicationContext() , SystemMessageHelper.TYPE_MESSAGE_TEAM_ALL, ""));
        Collections.sort(messagesList, new Comparator<SystemMessage>() {
            @Override
            public int compare(SystemMessage o1, SystemMessage o2) {
                return o1.time < o2.time ? 1 : (o1.time == o2.time ? 0 : -1);
            }
        });
        // 注册观察者
        registerSystemMessageObservers(true);
        registerUserInfoChangedObserver(true);
        registerMesaageDealReceiver(true);
        registerObservers(true);
        initMessageList();
    }

    public void registerObservers(boolean register) {
        if (register) {
            //注册未读消息数量观察者
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);
        } else {
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);
        }
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
//            if (type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY && (updateMessage.getType() == SystemMessageType.TeamInvite || updateMessage.getType() == SystemMessageType.DeclineTeamInvite)) {
//                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
//            } else if (type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE && (updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply)) {
//                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
//            } else if (type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_MSG && (updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply)) {
//                SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
//            }
            SystemMessageHelper.setSystemMessageRead(getApplicationContext(), String.valueOf(updateMessage.getMessageId()));
            for (SystemMessage message : messagesList) {
                if (/*type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_APPLY && */(updateMessage.getType() == SystemMessageType.TeamInvite || updateMessage.getType() == SystemMessageType.DeclineTeamInvite)) {
                    if (message.getTargetId().equals(updateMessage.getTargetId())) {
                        messagesList.remove(message);
                        break;
                    }
                } else if (/*type_message == SystemMessageHelper.TYPE_MESSAGE_TEAM_INVITE && */(updateMessage.getType() == SystemMessageType.ApplyJoinTeam || updateMessage.getType() == SystemMessageType.RejectTeamApply)) {
                    if (message.getTargetId().equals(updateMessage.targetId) && message.fromAccount.equals(updateMessage.fromAccount)) {
                        messagesList.remove(message);
                        break;
                    }
                }
            }
            messagesList.add(0, updateMessage);
            List<String> nimRefreshList = new ArrayList<>();
            for (int i = 0; i < messagesList.size(); i++) {
                nimRefreshList.add(messagesList.get(i).fromAccount);
            }
            NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
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
        SystemMessageHelper.resetSystemMessageUnreadCountByType(getApplicationContext() , SystemMessageHelper.TYPE_MESSAGE_TEAM_ALL);
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

                ClubRejectActivity.StartActivityFor(this, message);
                //NIMClient.getService(TeamService.class).rejectApply(message.getTargetId(), message.getFromAccount(), "").setCallback(callback);
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

    private int findPositionByHordeMsg(SystemMessage message) {
        int index = -1;
        for (int i = 0; i < messagesList.size(); i++) {
            SystemMessage item = messagesList.get(i);
            if (message.key.equals(item.key)) {
                item.horde_status = (message.horde_status);//改变状态
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onAgree(final SystemMessage message) {
        if (message.custom_outer_type == 0) {//云信消息
            onSystemNotificationDeal(message, true);
        } else if (message.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
            if (message.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------同意   申请加入部落
                mHordeAction.passHordeJoin(message.tid, message.horde_id, 1, new GameRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        message.horde_status = AppMessageStatus.passed.getValue();
                        HordeMessageHelper.setSystemMessageStatus(getApplicationContext(), message.key, message.horde_status);
                        int index = findPositionByHordeMsg(message);
                        if (index >= 0) {
                            final int m = index;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Object tag = ListViewUtil.getViewHolderByIndex(lv_message, m);
                                    if (tag instanceof SystemMessageAdapter.ViewHolder) {
                                        mSystemMessageAdapter.refreshHordeJoin(tag, message);
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailed(int code, JSONObject response) {
                        if (code == -1) {//网络错误的话，消息状态初始化为0
                            message.horde_status = 0;
                        } else if (code == ApiCode.CODE_HORDE_PASSJOIN_EXPIRED) {
                            message.horde_status = AppMessageStatus.expired.getValue();
                            HordeMessageHelper.setSystemMessageStatus(getApplicationContext(), message.key, 1);
                        }
                        int index = findPositionByHordeMsg(message);
                        if (index >= 0) {
                            final int m = index;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Object tag = ListViewUtil.getViewHolderByIndex(lv_message, m);
                                    if (tag instanceof SystemMessageAdapter.ViewHolder) {
                                        mSystemMessageAdapter.refreshHordeJoin(tag, message);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    RejectInterface mRejectListener;
    @Override
    public void onReject(final SystemMessage message, RejectInterface listener) {
        mRejectListener = listener;
        if (message.custom_outer_type == 0) {//云信消息
            onSystemNotificationDeal(message, false);
        } else if (message.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
            if (message.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------拒绝   申请加入部落
                mHordeAction.passHordeJoin(message.tid, message.horde_id, 2, new GameRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        message.horde_status = AppMessageStatus.declined.getValue();
                        HordeMessageHelper.setSystemMessageStatus(getApplicationContext(), message.key, message.horde_status);
                        int index = findPositionByHordeMsg(message);
                        if (index >= 0) {
                            final int m = index;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Object tag = ListViewUtil.getViewHolderByIndex(lv_message, m);
                                    if (tag instanceof SystemMessageAdapter.ViewHolder) {
                                        mSystemMessageAdapter.refreshHordeJoin(tag, message);
                                    }
                                }
                            });
                        }
                    }
                    @Override
                    public void onFailed(int code, JSONObject response) {
                        if (code == -1) {//网络错误的话，消息状态初始化为0
                            message.horde_status = 0;
                        } else if (code == ApiCode.CODE_HORDE_PASSJOIN_EXPIRED) {
                            message.horde_status = AppMessageStatus.expired.getValue();
                            HordeMessageHelper.setSystemMessageStatus(getApplicationContext(), message.key, 1);
                        }
                        int index = findPositionByHordeMsg(message);
                        if (index >= 0) {
                            final int m = index;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Object tag = ListViewUtil.getViewHolderByIndex(lv_message, m);
                                    if (tag instanceof SystemMessageAdapter.ViewHolder) {
                                        mSystemMessageAdapter.refreshHordeJoin(tag, message);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onClick(SystemMessage message) {
        if(message.getType() == SystemMessageType.AddFriend || message.getType() == SystemMessageType.ApplyJoinTeam) {
            int from = UserProfileActivity.FROM_OTHER_LIST;
            if(message.getType() == SystemMessageType.AddFriend) {
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
                ClubApplyDetail.StartActivity(this, message);
                return ;
            }

//            UserProfileActivity.start(this , message.getFromAccount() , from , message);
        } else if (message.getType() == SystemMessageType.RejectTeamApply) {//拒绝申请
            ClubApplyDetail.StartActivity(this, message);
        } else {
            //俱乐部
            String clubId = message.getTargetId();
            int from = ClubInfoActivity.FROM_TYPE_LIST;
            if (message.getType() == SystemMessageType.TeamInvite && message.getStatus() == SystemMessageStatus.init) {
                //俱乐部邀请
                from = ClubInfoActivity.FROM_TYPE_MESSAGE_INVITE;
            }
            ClubInfoActivity.start(this , clubId , from ,message);
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
        mResultDataView.nullDataShow(R.string.message_null, R.mipmap.img_message_null);
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
        if (message.custom_outer_type == 0) {//云信消息
            SystemMessageHelper.deleteSystemMessage(getApplicationContext() , message);
        } else if (message.custom_outer_type == CustomNotificationConstants.NOTIFICATION_TYPE_HORDE) {
            if (message.custom_inner_type == HordeMessageType.HORDE_MESSAGE_TYPE_APPLY) {//-------------------------------------------------------------------------申请加入部落
                HordeMessageHelper.deleteSystemMessage(getApplicationContext(), message);
            }
        }
        messagesList.remove(message);
        refresh();
        Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        //处理部落数据库 全部置为已读
        HordeMessageHelper.resetHordeMessageUnreadCountByType(this, HordeMessageHelper.SEARCH_TYPE_ALL);
        ReminderManager.getInstance().updateHordeUnreadNum(null);
        if(messagesList != null){
            messagesList.clear();
            messagesList = null;
        }
        registerObservers(false);
        registerUserInfoChangedObserver(false);
        registerSystemMessageObservers(false);
        registerMesaageDealReceiver(false);
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        DiscoveryFragment.updateClubNum = true;
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

    MessageVerifyAC.MesaageDealReceiver mesaageDealReceiver;
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
            mesaageDealReceiver = new MessageVerifyAC.MesaageDealReceiver();
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
    public class MesaageDealReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null){
                SystemMessage systemMessage = (SystemMessage)intent.getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);
                boolean pass = intent.getBooleanExtra(Extra_Pass,true);
                if(systemMessage != null) {
                    onProcessSuccess(pass , systemMessage);
                }
            }
        }
    }

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item == null) {
                return;
            }
            if (item.getId() == ReminderId.SESSION) {
            } else if (item.getId() == ReminderId.CONTACT) {
            } else if (item.getId() == ReminderId.APP_MESSAGE) {
            } else if (item.getId() == ReminderId.HORDE_MESSAGE || item.getId() == ReminderId.SYSTEM_MESSAGE) {
                if (item.attachObject instanceof SystemMessage) {
                    for (SystemMessage message : messagesList) {
                        if (((SystemMessage) item.attachObject).key.equals(message.key)) {
                            messagesList.remove(message);
                            break;
                        }
                    }
                    messagesList.add((SystemMessage) item.attachObject);
                    refresh();
                } else {
                    //如果消息为空那么do nothing
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ClubRejectActivity.RequestCode_Reject) {
            boolean needRequest = resultCode == RESULT_OK;
            mRejectListener.onChangeStatus(needRequest);
            if(needRequest) {
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
