package com.htgames.nutspoker.ui.adapter.record

import android.app.Activity
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

/**
 * Created by 周智慧 on 2017/7/18.
 */
class RecordListAdap : FlexibleAdapter<AbstractFlexibleItem<*>>, FastScroller.BubbleTextCreator {
    lateinit var mActivity: Activity
    lateinit var mListener: IClick
    var mMaxNum = 0
    constructor(items: List<AbstractFlexibleItem<*>>, listeners: Any, stableIds: Boolean) : super(items, listeners, stableIds) {
        if (listeners is Activity) {
            mActivity = listeners
        }
        if (listeners is IClick) {
            mListener = listeners
        }
    }

    override fun onCreateBubbleText(position: Int): String {
        return super.onCreateBubbleText(if (position >= mMaxNum) (mMaxNum - 1) else position)
    }
}