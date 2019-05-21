package com.htgames.nutspoker.game.match.item;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2016/6/21.
 */
public class PlayerSection<T> {
    public int type;
    public ArrayList<T> list;

    public PlayerSection(int type, ArrayList<T> list) {
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