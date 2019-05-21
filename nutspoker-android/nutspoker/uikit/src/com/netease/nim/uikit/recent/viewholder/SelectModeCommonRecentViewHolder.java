package com.netease.nim.uikit.recent.viewholder;

public class SelectModeCommonRecentViewHolder extends CommonRecentViewHolder {
    @Override
    public void refresh(Object item) {
        setSelectMode(true);
        super.refresh(item);
    }
}
