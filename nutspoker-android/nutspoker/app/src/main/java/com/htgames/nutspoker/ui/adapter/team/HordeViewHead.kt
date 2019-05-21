package com.htgames.nutspoker.ui.adapter.team

import android.view.View
import android.widget.TextView
import com.htgames.nutspoker.R
import com.netease.nim.uikit.chesscircle.entity.TeamEntity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Created by 周智慧 on 2017/6/19.
 */
class HordeViewHead(var id: String) : AbstractHeaderItem<HordeViewHead.ThisViewHolder>(), IFilterable {
    override fun filter(constraint: String): Boolean {
        return id.toLowerCase().trim { it <= ' ' }.contains(constraint)
    }

    override fun equals(other: Any?): Boolean {
        if (other is HordeViewHead) {
            return this.id == other.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun getLayoutRes(): Int {
        return R.layout.layout_channel_section
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ThisViewHolder {
        return ThisViewHolder(view, adapter, true)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ThisViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.bind(id)
    }

    class ThisViewHolder(view: View, adapter: FlexibleAdapter<*>, stick: Boolean) : FlexibleViewHolder(view, adapter, stick) {
        val tv_channel_section = itemView.findViewById(R.id.tv_channel_section) as TextView
        val tv_channel_section_right = itemView.findViewById(R.id.tv_channel_section_right) as TextView
        fun bind(id: String) {
            tv_channel_section.text = id
            tv_channel_section_right.visibility = View.GONE
        }
    }
}