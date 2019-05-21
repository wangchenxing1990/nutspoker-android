package com.htgames.nutspoker.ui.activity.horde.util;

import java.io.Serializable;

/**
 * Created by 周智慧 on 17/3/22.
 */

public class UpdateItem implements Serializable {
    public static final int UPDATE_TYPE_NAME = 0;//更新昵称，目前仅支持修改昵称
    public static final int UPDATE_TYPE_IS_CONTROL = 1;//更新开局权限，目前仅支持修改昵称
    public static final int UPDATE_TYPE_CREATE_HORDE = 2;//创建部落
    public static final int UPDATE_TYPE_CANCEL_HORDE = 3;//解散部落
    public static final int UPDATE_TYPE_IS_SCORE = 4;//上分控制开关更新
    public static final int UPDATE_TYPE_SET_SCORE = 5;//上分设置更新
    public int updateType;
    public String name;//部落更新后的name
    public String horde_id;
    public String tid;
    public int is_control;
    public int is_score;
    public int score;

    public UpdateItem(int updateType) {
        this.updateType = updateType;
    }

    public UpdateItem setName(String name) {
        this.name = name;
        return this;
    }

    public UpdateItem setHordeId(String horde_id) {
        this.horde_id = horde_id;
        return this;
    }

    public UpdateItem setIsControl(int is_control) {
        this.is_control = is_control;
        return this;
    }

    public UpdateItem setIsScore(int is_score) {
        this.is_score = is_score;
        return this;
    }

    public UpdateItem setTid(String tid) {
        this.tid = tid;
        return this;
    }

    public UpdateItem setScore(int score) {
        this.score = score;
        return this;
    }
}
