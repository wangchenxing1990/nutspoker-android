package com.htgames.nutspoker.ui.activity.Club.credit

import android.animation.Animator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.TextView
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.ui.adapter.BaseAdapter
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.string.StringUtil
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.*
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import eu.davidea.viewholders.FlexibleViewHolder

/**
 * Created by 周智慧 on 17/9/19.
 */
class CreditListItem(var mEntity: CreditListEntity?) : AbstractItem<CreditListItem.ThisVH>(mEntity?.uid ?: "${Math.random()}"), ISectionable<CreditListItem.ThisVH, AbstractHeaderItem<*>>, IFilterable {
    var mHeader: AbstractHeaderItem<*>? = null
    init {
        title = NimUserInfoCache.getInstance().getUserDisplayName(mEntity?.uid)
    }
    override fun getHeader(): AbstractHeaderItem<*>? {
        return mHeader
    }

    override fun setHeader(header: AbstractHeaderItem<*>?) {
        mHeader = header
    }

    override fun filter(constraint: String): Boolean {
        if (StringUtil.isSpaceOrZero(mEntity?.uid)) {
            return false
        }
        return getTitle().toLowerCase().trim { it <= ' ' }.contains(constraint)
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_credit_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): ThisVH {
        return ThisVH(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ThisVH?, position: Int, payloads: MutableList<Any?>?) {
        holder?.bind(mEntity, position)
    }

    class ThisVH(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        var iv_head = itemView.findViewById(R.id.iv_head) as HeadImageView
        var tv_member_name = itemView.findViewById(R.id.tv_member_name) as TextView
        var tv_credit_num = itemView.findViewById(R.id.tv_credit_num) as TextView
        fun bind(info: CreditListEntity?, position: Int) {
            iv_head.loadBuddyAvatar(info?.uid)
            tv_member_name.text = NimUserInfoCache.getInstance().getUserDisplayName(info?.uid)
            tv_credit_num.text = "信用分：${info?.score}"
            itemView.setOnClickListener {
                (mAdapter as? BaseAdapter)?.mCLickListener?.onClick(position)
            }
            if (mAdapter.hasSearchText()) {
                FlexibleUtils.colorAccent = ChessApp.sAppContext.resources.getColor(R.color.login_solid_color)
                FlexibleUtils.highlightText(tv_member_name, tv_member_name.getText().toString(), mAdapter.searchText)
            }
        }

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            if (mAdapter.recyclerView.layoutManager is GridLayoutManager || mAdapter.recyclerView.layoutManager is StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
            } else {
                //Linear layout
                if (isForward)
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }

    }

    override fun equals(inObject: Any?): Boolean {
        if (inObject is CreditListItem) {
            val inItem = inObject as CreditListItem?
            return this.getId() == inItem!!.getId()
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}