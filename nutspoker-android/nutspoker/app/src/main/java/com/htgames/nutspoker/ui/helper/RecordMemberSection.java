package com.htgames.nutspoker.ui.helper;

import com.netease.nim.uikit.bean.GameMemberEntity;

import java.util.ArrayList;

/**
 */
public class RecordMemberSection {
    public int type;
    public ArrayList<GameMemberEntity> list;

    public RecordMemberSection(int type, ArrayList<GameMemberEntity> list) {
        this.type = type;
        this.list = list;
    }

    public int getSectionItemsTotal() {
        return list == null ? 0 : list.size();
    }

    public GameMemberEntity getItem(int position) {
        if (list != null && position < list.size()) {
            return list.get(position);
        }
        return null;
    }
}
