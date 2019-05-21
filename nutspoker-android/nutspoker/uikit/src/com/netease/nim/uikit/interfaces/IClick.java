package com.netease.nim.uikit.interfaces;

/**
 * Created by 周智慧 on 17/1/20.
 */

public interface IClick {
    void onDelete(int position);
    void onClick(int position);
    void onLongClick(int position);
    public interface IOnlyClick {
        void onClick(int position);
    }
}
