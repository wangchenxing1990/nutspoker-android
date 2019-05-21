package com.htgames.nutspoker.ui.adapter

import android.app.Activity
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

/**
 * Created by 周智慧 on 2017/8/30.
 */
class BaseAdapter : FlexibleAdapter<AbstractFlexibleItem<*>>, FastScroller.BubbleTextCreator {
    var mActivity: Activity? = null
    var mCLickListener: IClick? = null
    var mMaxNum = 0
    constructor(items: List<AbstractFlexibleItem<*>>, listeners: Any, stableIds: Boolean) : super(items, listeners, stableIds) {
        if (listeners is Activity) {
            mActivity = listeners
        }
        if (listeners is IClick) {
            mCLickListener = listeners
        }
    }

    override fun onCreateBubbleText(position: Int): String {
        return super.onCreateBubbleText(if (position >= mMaxNum) (mMaxNum - 1) else position)
    }
}