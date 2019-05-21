package com.htgames.nutspoker.ui.adapter.team

import android.app.Activity
import android.support.v7.widget.RecyclerView
import com.htgames.nutspoker.view.TouchableRecyclerView
import com.netease.nim.uikit.customview.SwipeItemLayout
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import java.util.*

/**
 * Created by 周智慧 on 2017/6/19.
 */
class HordeViewAdap : FlexibleAdapter<AbstractFlexibleItem<*>>, FastScroller.BubbleTextCreator, TouchableRecyclerView.IHandleSwipeItemLayoutWithAnim {
    private val mOpenedSil = ArrayList<SwipeItemLayout>()
    override fun openSwipeItemLayoutWithAnim(swipeRoot: SwipeItemLayout?) {
        if (swipeRoot != null) {
            mOpenedSil.add(swipeRoot)
            swipeRoot.open()
        }
    }

    override fun closeOpenedSwipeItemLayoutWithAnim() {
        for (sil in mOpenedSil) {
            sil.closeWithAnim()
        }
        mOpenedSil.clear()
    }

    private lateinit var mActivity: Activity
    lateinit var mListener: IClick
    constructor(items: List<AbstractFlexibleItem<*>>, listeners: Any, stableIds: Boolean) : super(items, listeners, stableIds) {
        if (listeners is Activity) {
            mActivity = listeners
        }
        if (listeners is IClick) {
            mListener = listeners
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<*>?) {
        super.onBindViewHolder(holder, position, payloads)
        val swipeRoot: SwipeItemLayout? = (holder as? HordeViewItem.BaseViewHolder)?.item_contact_swipe_root
        swipeRoot?.setDelegate(object : SwipeItemLayout.SwipeItemLayoutDelegate {
            override fun onSwipeItemLayoutOpened(swipeItemLayout: SwipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim()
                mOpenedSil.add(swipeItemLayout)
            }

            override fun onSwipeItemLayoutClosed(swipeItemLayout: SwipeItemLayout) {
                //                    swipeItemLayout.clearAnimation();
                mOpenedSil.remove(swipeItemLayout)
            }

            override fun onSwipeItemLayoutStartOpen(swipeItemLayout: SwipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim()
            }
        })
    }
}