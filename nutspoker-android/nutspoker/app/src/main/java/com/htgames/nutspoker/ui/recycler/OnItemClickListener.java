package com.htgames.nutspoker.ui.recycler;

import android.view.View;

/**
 * Created by 周智慧 on 17/1/2.
 */

public interface OnItemClickListener {
    /**
     * item点击回调
     *
     * @param view
     * @param position
     */
    void onItemClick(View view, int position);

    /**
     * 删除按钮回调
     *
     * @param position
     */
    void onDeleteClick(int position);
    void onLongClick(int position);
}
