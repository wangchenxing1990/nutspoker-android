package com.htgames.nutspoker.chat.reminder;

import android.util.SparseArray;

import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * TAB红点提醒管理器
 * Created by huangjun on 2015/3/18.
 */
public class ReminderManager {

    // callback
    public static interface UnreadNumChangedCallback {
        public void onUnreadNumChanged(ReminderItem item);
    }

    // singleton
    private static ReminderManager instance;

    public static synchronized ReminderManager getInstance() {
        if (instance == null) {
            instance = new ReminderManager();
        }

        return instance;
    }

    // observers
    private SparseArray<ReminderItem> items = new SparseArray<>();

    private List<UnreadNumChangedCallback> unreadNumChangedCallbacks = new ArrayList<>();

    private ReminderManager() {
        populate(items);
    }

    /**
     * 获取未读消息数量
     * @return
     */
    public int getUnreadSessionCount(){
        return items.get(ReminderId.SESSION).getUnread();
    }

    public int getUnreadSysMsgCount(){
        int unreadNum = 0;
        unreadNum = items.get(ReminderId.CONTACT).getUnread() + items.get(ReminderId.CLUB_APPLY).getUnread() + items.get(ReminderId.CLUB_INVITE).getUnread();
        return unreadNum;
    }

    public final void updateSysMsgUnreadNum(int unreadNum){
        updateUnreadMessageNum(unreadNum, false, ReminderId.SYSTEM_MESSAGE, null);
    }

    public final void updateSysMsgUnreadNum(int reminderId , int unreadNum){
        updateUnreadMessageNum(unreadNum, false, reminderId, null);
    }

    //APP消息
    public final void updateAppMsgUnreadNum(int unreadNum){
        updateUnreadMessageNum(unreadNum, false, ReminderId.APP_MESSAGE, null);
    }

    public final void updateAppMsgUnreadNum(int unreadNum , boolean delta, AppMessage appMessage){
        updateUnreadMessageNum(unreadNum, delta, ReminderId.APP_MESSAGE, appMessage);
    }

    public int getUnreadAppMessageCount(){
        return items.get(ReminderId.APP_MESSAGE).getUnread();
    }

    // interface
    public final void updateSessionUnreadNum(int unreadNum) {
        updateUnreadMessageNum(unreadNum, false, ReminderId.SESSION, null);
    }

    public final void updateSessionDeltaUnreadNum(int delta) {
        updateUnreadMessageNum(delta, true, ReminderId.SESSION, null);
    }

    public final void updateHordeUnreadNum(SystemMessage systemMessage) {
        for (UnreadNumChangedCallback cb : unreadNumChangedCallbacks) {
            ReminderItem item = new ReminderItem(ReminderId.HORDE_MESSAGE);
            item.attachObject = systemMessage;
            cb.onUnreadNumChanged(item);
        }
    }

    public final void updateContactUnreadNum(int unreadNum) {
        updateUnreadMessageNum(unreadNum, false, ReminderId.CONTACT, null);
    }

    public void registerUnreadNumChangedCallback(UnreadNumChangedCallback cb) {
        if (unreadNumChangedCallbacks.contains(cb)) {
            return;
        }

        unreadNumChangedCallbacks.add(cb);
    }

    public void unregisterUnreadNumChangedCallback(UnreadNumChangedCallback cb) {
        if (!unreadNumChangedCallbacks.contains(cb)) {
            return;
        }

        unreadNumChangedCallbacks.remove(cb);
    }

    // inner
    private final void populate(SparseArray<ReminderItem> items) {
        items.put(ReminderId.SESSION, new ReminderItem(ReminderId.SESSION));
        items.put(ReminderId.SYSTEM_MESSAGE, new ReminderItem(ReminderId.SYSTEM_MESSAGE));
        items.put(ReminderId.CONTACT, new ReminderItem(ReminderId.CONTACT));
        items.put(ReminderId.CLUB_APPLY, new ReminderItem(ReminderId.CLUB_APPLY));
        items.put(ReminderId.CLUB_INVITE, new ReminderItem(ReminderId.CLUB_INVITE));
        items.put(ReminderId.APP_MESSAGE, new ReminderItem(ReminderId.APP_MESSAGE));
        items.put(ReminderId.HORDE_MESSAGE, new ReminderItem(ReminderId.HORDE_MESSAGE));
    }

    private final void updateUnreadMessageNum(int unreadNum, boolean delta, int reminderId, AppMessage appMessage) {
        AppMessageType type = appMessage == null ? AppMessageType.AppNotice : appMessage.type;
        ReminderItem item = items.get(reminderId);
        if (item == null) {
            return;
        }

        int num = item.getUnread();

        if (num == 0 && num == unreadNum) {
            //如果之前和新的都为0，不发生变化
            return;
        }

        // 增量
        if (delta) {
            num = num + unreadNum;
            if (num < 0) {
                num = 0;
            }
        } else {
            num = unreadNum;
        }

        item.setUnread(num);
        item.setIndicator(false);
        for (UnreadNumChangedCallback cb : unreadNumChangedCallbacks) {
            cb.onUnreadNumChanged(item);
        }
    }
}
