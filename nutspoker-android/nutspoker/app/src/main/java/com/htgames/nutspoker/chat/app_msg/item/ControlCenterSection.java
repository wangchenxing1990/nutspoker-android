package com.htgames.nutspoker.chat.app_msg.item;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2016/6/24.
 */
public class ControlCenterSection<T> {
    public String type;
    public ArrayList<T> list;

    public ControlCenterSection(String type, ArrayList<T> list) {
        this.type = type;
        this.list = list;
    }

    public int getSectionItemsTotal() {
        return list == null ? 0 : list.size();
    }

    public T getItem(int position) {
        if (list != null && position < list.size()) {
            return list.get(position);
        }
        return null;
    }
}
