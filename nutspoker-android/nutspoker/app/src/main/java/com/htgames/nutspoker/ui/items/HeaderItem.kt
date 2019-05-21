package com.htgames.nutspoker.ui.items

import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.htgames.nutspoker.R
import com.netease.nim.uikit.common.util.SpanUtils
import com.netease.nim.uikit.common.util.string.StringUtil
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Created by 周智慧 on 2017/7/24.
 */
class HeaderItem(var id: String, var mLeft: String = "", var mMiddle: String = "", var mRight: String = "", var mLeftDrawable: Drawable? = null, var mType: Int) : AbstractHeaderItem<HeaderItem.BaseViewHolder>(), IFilterable {
    companion object {
        val TYPE_NORMAL = 0
        val TYPE_INSURANCE = 1
    }
    var insurancePond = 0
    override fun filter(constraint: String): Boolean {
        return id.toLowerCase().trim { it <= ' ' }.contains(constraint)
    }
    override fun equals(other: Any?): Boolean {
        if (other is HeaderItem) {
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

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): BaseViewHolder {
        return BaseViewHolder(view, adapter, true)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: BaseViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.bind(mType, mLeft, mMiddle, mRight, mLeftDrawable, insurancePond)
    }

    open class BaseViewHolder(view: View, adapter: FlexibleAdapter<*>, stick: Boolean) : FlexibleViewHolder(view, adapter, stick) {
        val tv_channel_section = itemView.findViewById(R.id.tv_channel_section) as TextView
        val tv_channel_section_middle = itemView.findViewById(R.id.tv_channel_section_middle) as TextView
        val tv_channel_section_right = itemView.findViewById(R.id.tv_channel_section_right) as TextView
        fun bind(mType: Int, left: String, middle: String, right: String, leftDrawable: Drawable?, insurancePond: Int) {
            tv_channel_section.text = left
            tv_channel_section.setCompoundDrawables(leftDrawable, null, null, null)
            tv_channel_section.visibility = if (StringUtil.isSpace(left)) View.INVISIBLE else View.VISIBLE
            tv_channel_section_middle.text = middle
            tv_channel_section_middle.visibility = if (StringUtil.isSpace(middle)) View.INVISIBLE else View.VISIBLE
            tv_channel_section_right.text = right
            if (mType == TYPE_INSURANCE) {
                var color = ContextCompat.getColor(itemView.context, if (insurancePond >= 0) R.color.record_list_earn_yes else R.color.record_list_earn_no)
                tv_channel_section.visibility = View.INVISIBLE
                tv_channel_section_middle.visibility = View.VISIBLE
                tv_channel_section_right.visibility = View.INVISIBLE
                tv_channel_section_middle.text = SpanUtils().append("保险池：")
                        .append("$insurancePond").setForegroundColor(color)
                        .create()
            }
        }
    }
}