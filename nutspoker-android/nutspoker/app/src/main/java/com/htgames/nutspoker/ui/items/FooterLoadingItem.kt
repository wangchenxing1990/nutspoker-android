package com.htgames.nutspoker.ui.items

import android.graphics.Color
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.R
import eu.davidea.flexibleadapter.items.AbstractItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Created by 周智慧 on 2017/7/20.
 */
class FooterLoadingItem(var mState: Int = STATE_PRE) : AbstractItem<FooterLoadingItem.VH>("") {
    companion object {
        val STATE_PRE = 0
        val STATE_LOADING = 1
        val STATE_FINISH = 2
        val STATE_LOAD_FAIL = 3
    }

    override fun getLayoutRes(): Int {
        return R.layout.layout_footer_loading
    }

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<out IFlexible<*>>?): VH {
        return VH(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: VH?, position: Int, payloads: MutableList<Any?>?) {
        holder?.bind(mState)
    }

    class VH(view: View?, adapter: FlexibleAdapter<*>?) : FlexibleViewHolder(view, adapter) {
        var tv_footer_loading: TextView = itemView.findViewById(R.id.tv_footer_loading) as TextView
        var progress_bar_footer_loading: ProgressBar = itemView.findViewById(R.id.progress_bar_footer_loading) as ProgressBar
        fun bind(state: Int) {
            when (state) {
                STATE_PRE -> {
                    tv_footer_loading.text = "上拉加载更多"
                    progress_bar_footer_loading.visibility = View.INVISIBLE
                }
                STATE_LOADING -> {
                    tv_footer_loading.text = ""
                    progress_bar_footer_loading.visibility = View.VISIBLE
                }
                STATE_FINISH -> {
                    tv_footer_loading.text = "无更多数据了"
                    progress_bar_footer_loading.visibility = View.INVISIBLE
                }
                STATE_LOAD_FAIL -> {
                    tv_footer_loading.text = "加载失败"
                    progress_bar_footer_loading.visibility = View.INVISIBLE
                }
            }
        }

        fun test() {

        }
    }
}
