package com.htgames.nutspoker.ui.adapter.recordmember

import android.app.Activity
import com.netease.nim.uikit.bean.GameEntity
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

/**
 * Created by 周智慧 on 2017/7/24.
 */
class RecordMemberAdap : FlexibleAdapter<AbstractFlexibleItem<*>>, FastScroller.BubbleTextCreator {
    lateinit var mActivity: Activity
    lateinit var mListener: IClick
    var isMeGameMgr: Boolean = true
    var mGameInfo: GameEntity? = null
    var checkPosition: Int = 0
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