package com.htgames.nutspoker.chat.reminder;

import java.io.Serializable;

public class ReminderItem implements Serializable {
    public static final long serialVersionUID = -2101649256143239157L;
    public Object attachObject;
    protected final int id;
    public String teamId;
    public int unread;

    private boolean indicator;

    public ReminderItem(int id) {
        this.id = id;
        this.unread = 0;
    }

    public int getId() {
        return id;
    }

    public int unread() {
        return unread;
    }

    public boolean indicator() {
        return unread <= 0 && indicator;
    }

    public int getUnread() {
        return unread;
    }

    public void setUnread(int unread) {
        this.unread = unread;
    }

    public void setIndicator(boolean indicator) {
        this.indicator = indicator;
    }

    /*package*/ ReminderItem copy() {
        ReminderItem item = new ReminderItem(id);
        copyData(item);
        return item;
    }

    protected void copyData(ReminderItem item) {
        item.unread = this.unread;
        item.indicator = this.indicator;
    }
}
