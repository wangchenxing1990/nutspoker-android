package com.htgames.nutspoker.chat;

import com.htgames.nutspoker.chat.msg.helper.SystemMessageHelper;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.SystemMessageObserver;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.msg.model.SystemMessage;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 系统消息监听&缓存
 */
public class MessageDataCache {
    private final static String TAG = "MessageDataCache";
    private static MessageDataCache instance;
    private List<MessageDataChangedObserver> messageObservers = new ArrayList<>();
//    private final static int MAX_RESULTS_COUNT = 88888;
//    /** 所有 */
//    public final static int TYPE_MESSAGE_ALL = 0;
//    /** 好友邀请 */
//    public final static int TYPE_MESSAGE_FRIEND = 1;
//    /** 入群邀请*/
//    public final static int TYPE_MESSAGE_TEAM_INVITE = 2;
//    /** 申请入群*/
//    public final static int TYPE_MESSAGE_TEAM_APPLY = 3;
//    //好友邀请缓存列表
//    private ArrayList<SystemMessage> friendMessageList = new ArrayList<SystemMessage>();
//    //俱乐部邀请缓存列表
//    private ArrayList<SystemMessage> clubInviteMessageList = new ArrayList<SystemMessage>();
//    //俱乐部申请缓存列表
//    private ArrayList<SystemMessage> clubApplyMessageList = new ArrayList<SystemMessage>();
//
    private ReadWriteLock lock = new ReentrantReadWriteLock();
//
    public static synchronized MessageDataCache getInstance(){
        if (instance == null) {
            instance = new MessageDataCache();
        }
        return instance;
    }

    public void registerObservers(boolean register) {
        NIMClient.getService(SystemMessageObserver.class).observeReceiveSystemMsg(messageObserver, register);
    }
    //消息接受观察者通知
    private Observer<SystemMessage> messageObserver = new Observer<SystemMessage>() {
        @Override
        public void onEvent(SystemMessage message) {
            //Log.d(TAG , "有新消息");

            //这里让管理者忽略掉 入群申请
            if(message.getType() == SystemMessageType.ApplyJoinTeam){
                String teamId = message.getTargetId();
                Team team = TeamDataCache.getInstance().getTeamById(teamId);
                if(!team.getCreator().equals(DemoCache.getAccount()))//如果我不是会长，忽略这条消息
                    return;
            }

            SystemMessageHelper.saveSystemtMessage(DemoCache.getContext() , SystemMessageHelper.getSystemMessage(message));
            notifyTeamMemberDataUpdate(message);
        }
    };
    /**
     * 观察者
     */
    public interface MessageDataChangedObserver {
        void onMessageUpdate(SystemMessage message);
    }
//
    private void notifyTeamMemberDataUpdate(SystemMessage message) {
        for (MessageDataChangedObserver o : messageObservers) {
            o.onMessageUpdate(message);
        }
    }

    public void registerMessageDataChangedObserverObserver(MessageDataChangedObserver o) {
        if (messageObservers.contains(o)) {
            return;
        }
        messageObservers.add(o);
    }

    public void unregisterTeamMemberDataChangedObserver(MessageDataChangedObserver o) {
        messageObservers.remove(o);
    }
}
