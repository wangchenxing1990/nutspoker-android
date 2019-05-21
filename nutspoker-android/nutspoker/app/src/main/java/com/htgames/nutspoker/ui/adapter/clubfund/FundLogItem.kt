package com.htgames.nutspoker.ui.adapter.clubfund

import android.animation.Animator
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.TextView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.ui.adapter.BaseAdapter
import com.netease.nim.uikit.bean.FundLogEntity
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.common.DateTools
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.SpanUtils
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.AbstractHeaderItem
import eu.davidea.flexibleadapter.items.AbstractItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.viewholders.FlexibleViewHolder
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by 周智慧 on 2017/8/30.
 */
class FundLogItem(var mEntity: FundLogEntity?) : AbstractItem<FundLogItem.ThisVH>(mEntity?.id), ISectionable<FundLogItem.ThisVH, AbstractHeaderItem<*>> {
    var mHeader: AbstractHeaderItem<*>? = null
    override fun getHeader(): AbstractHeaderItem<*>? {
        return mHeader
    }

    override fun setHeader(header: AbstractHeaderItem<*>?) {
        mHeader = header
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_fund_log
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ThisVH {
        return ThisVH(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ThisVH?, position: Int, payloads: MutableList<Any?>?) {
        holder?.bind(mEntity, position)
    }

    class ThisVH(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        var tv_time = itemView.findViewById(R.id.tv_time) as TextView
        var iv_head = itemView.findViewById(R.id.iv_head) as HeadImageView
        var tv_log_desc_top = itemView.findViewById(R.id.tv_log_desc_top) as TextView
        var tv_log_desc_below = itemView.findViewById(R.id.tv_log_desc_below) as TextView
        var tv_fund = itemView.findViewById(R.id.tv_fund) as TextView
        fun bind(entity: FundLogEntity?, position: Int) {
            if (entity == null) {
                return
            }
            //设置时间
            var upperStr = DateTools.getWeekOfDate(Date(entity.time * 1000))
            var lowerStr = SimpleDateFormat("MM-dd").format(entity.time * 1000)
            tv_time.text = SpanUtils().appendLine(upperStr).setFontSize(13, true)
                    .append(lowerStr).setFontSize(10, true)
                    .create()
            if (entity.type == ClubConstant.FUND_TYPE_GRANT) {
                iv_head.loadBuddyAvatar(if (entity.type == ClubConstant.FUND_TYPE_PURCHASE) entity.admin_uid else entity.receiver_uid)
            } else {
                iv_head.setImageResource(R.mipmap.fund_icon_purchase)
            }
            tv_log_desc_top.text = if (entity.type == ClubConstant.FUND_TYPE_PURCHASE) "充值基金" else "发放给“${NimUserInfoCache.getInstance().getUserDisplayName(entity.receiver_uid)}”"
            tv_log_desc_below.text = entity.admin_nickname
            tv_fund.text = if (entity.type == ClubConstant.FUND_TYPE_PURCHASE) "+${entity.fund}" else "-${entity.fund}"
            tv_fund.setTextColor(if (entity.type == ClubConstant.FUND_TYPE_PURCHASE) Color.RED else Color.GRAY)
            itemView.setOnClickListener {
                (mAdapter as? BaseAdapter)?.mCLickListener?.onClick(position)
            }
        }

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            if (mAdapter.recyclerView.layoutManager is GridLayoutManager || mAdapter.recyclerView.layoutManager is StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
            } else {
                if (isForward)//Linear layout
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }
    }
}