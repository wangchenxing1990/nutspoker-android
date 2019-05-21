package com.htgames.nutspoker.chat.app_msg.bean;

import java.io.Serializable;

/**
 */
public class ControlTipEntity implements Serializable {
    public String gid;
    public int gameMode;
    public int unOperateCount;
    public boolean isChecked;

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public int getUnOperateCount() {
        return unOperateCount;
    }

    public void setUnOperateCount(int unOperateCount) {
        this.unOperateCount = unOperateCount;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
