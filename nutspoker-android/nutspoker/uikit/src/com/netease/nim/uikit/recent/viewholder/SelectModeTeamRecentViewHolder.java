package com.netease.nim.uikit.recent.viewholder;

public class SelectModeTeamRecentViewHolder extends TeamRecentViewHolder {
	@Override
	public void refresh(Object item) {
		setSelectMode(true);
		super.refresh(item);
	}
}
