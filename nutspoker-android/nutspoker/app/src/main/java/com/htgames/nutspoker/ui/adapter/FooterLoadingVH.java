package com.htgames.nutspoker.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.netease.nim.uikit.pulltorefresh.BottomLoadingView;

/**
 * Created by 周智慧 on 17/2/6.
 */

public class FooterLoadingVH extends RecyclerView.ViewHolder {
    BottomLoadingView bottomLoadingView;
    public FooterLoadingVH(View itemView) {
        super(itemView);
        if (itemView instanceof BottomLoadingView) {
            bottomLoadingView = (BottomLoadingView) itemView;
        }
    }

    /**
     * 预加载状态
     */
    public void statePre() {
        if (bottomLoadingView == null) {
            return;
        }
        bottomLoadingView.statePre();
    }

    /**
     * 加载状态
     */
    public void stateLoading() {
        if (bottomLoadingView == null) {
            return;
        }
        bottomLoadingView.stateLoading();
    }

    /**
     * 数据已经加载完状态
     */
    public void stateAllDataLoadDone(boolean isAllLoadFinish) {
        if (bottomLoadingView == null) {
            return;
        }
        bottomLoadingView.stateAllDataLoadDone(isAllLoadFinish);
    }

    /**
     * 隐藏掉
     */
    public void gone() {
        if (bottomLoadingView == null) {
            return;
        }
        bottomLoadingView.gone();
    }
}
