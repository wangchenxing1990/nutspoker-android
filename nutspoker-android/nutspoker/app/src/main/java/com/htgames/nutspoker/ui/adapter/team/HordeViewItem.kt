package com.htgames.nutspoker.ui.adapter.team

import android.animation.Animator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.htgames.nutspoker.R
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.chesscircle.entity.TeamEntity
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.customview.SwipeItemLayout
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.*
import eu.davidea.viewholders.FlexibleViewHolder
import java.io.Serializable

/**
 * Created by 周智慧 on 2017/6/19.
 */
class HordeViewItem(var mTeamEntity: TeamEntity) : AbstractItem<HordeViewItem.BaseViewHolder>(mTeamEntity.id), ISectionable<HordeViewItem.BaseViewHolder, HordeViewHead>, IFilterable, Serializable {
    companion object {
        val ITEM_TYPE_DETAIL = 0
        val ITEM_TYPE_SEARCH = 1
        val ITEM_TYPE_SCORE = 2
    }
    var mIsChief: Boolean = false
    var itemType = ITEM_TYPE_DETAIL
    var swipable = false
    override fun getHeader(): HordeViewHead? {
        return mHeader as? HordeViewHead
    }

    override fun setHeader(header: HordeViewHead) {
        mHeader = header
    }

    override fun filter(constraint: String): Boolean {
        return getTitle() != null && getTitle().toLowerCase().trim { it <= ' ' }.contains(constraint) ||
                mTeamEntity.name.toLowerCase().trim { it <= ' ' }.contains(constraint)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_horde_club_view
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>): BaseViewHolder {
        if (itemType == ITEM_TYPE_DETAIL) {
            return DetailViewHolder(view, adapter)
        } else if (itemType == ITEM_TYPE_SEARCH) {
            return SearchViewHolder(view, adapter)
        } else if (itemType == ITEM_TYPE_SCORE) {
            return ScoreViewHolder(view, adapter)
        }
        return DetailViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: BaseViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.bind(mTeamEntity, mIsChief, position, swipable)
    }

    var mHeader: HordeViewHead? = null

    constructor(teamEntity: TeamEntity, isChief: Boolean = false, header: HordeViewHead? = null) : this(teamEntity) {
        mIsChief = isChief
        mHeader = header
    }

    open class BaseViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        var mDelete: TextView = itemView.findViewById(R.id.scrollable_view_remove_item) as TextView
        var item_contact_swipe_root: SwipeItemLayout = itemView.findViewById(R.id.item_contact_swipe_root) as SwipeItemLayout
        var swipe_content: View = itemView.findViewById(R.id.swipe_content)
        var iv_club_head: HeadImageView = itemView.findViewById(R.id.iv_club_head) as HeadImageView
        var tv_club_info_name: TextView = itemView.findViewById(R.id.tv_club_info_name) as TextView
        var tv_club_info_area: TextView = itemView.findViewById(R.id.tv_club_info_area) as TextView
        var mCLick: IClick? = (adapter as? HordeViewAdap)?.mListener ?: null
        var horde_view_arrow = itemView.findViewById(R.id.horde_view_arrow)
        var horde_upper_limit_num: TextView = itemView.findViewById(R.id.horde_upper_limit_num) as TextView
        //
        var iv_member = itemView.findViewById(R.id.iv_member) as ImageView
        var tv_club_info_member_count = itemView.findViewById(R.id.tv_club_info_member_count) as TextView
        open fun bind(teamEntity: TeamEntity, isChief: Boolean, position: Int, swipable: Boolean) {
            item_contact_swipe_root.setSwipeAble(swipable)
            iv_member.visibility = if (isChief) View.GONE else View.VISIBLE
            tv_club_info_area.visibility = if (isChief) View.VISIBLE else View.GONE
            tv_club_info_member_count.visibility = if (isChief) View.GONE else View.VISIBLE
            if (teamEntity == null || position < 0) {
                return
            }
            //  teammember只能请求云信，咱们的服务端没有存这些信息
            val team = TeamDataCache.getInstance().getTeamById(teamEntity.id)
            if (team == null) {
                TeamDataCache.getInstance().fetchTeamById(teamEntity.id) { success, result ->
                    if (success && result != null) {
                        tv_club_info_member_count.text = result.memberCount.toString() + "/" + ClubConstant.getClubMemberLimit(result)
                    } else {
                    }
                }
            } else {
                tv_club_info_member_count.text = team.memberCount.toString() + "/" + ClubConstant.getClubMemberLimit(team)
            }
            horde_upper_limit_num.text = "当前分：${GameConstants.getGameChipsShow(teamEntity.score)}"
            iv_club_head.loadClubAvatarByUrl(teamEntity.id, /*ClubConstant.getClubExtAvatar(extServer)*/teamEntity.avatar, HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
            tv_club_info_name.text = teamEntity.name
            tv_club_info_area.text = "ID: ${teamEntity.vid}"
            swipe_content.setOnClickListener {
                mCLick?.onClick(position)
            }
        }
        override fun scrollAnimators(animators: MutableList<Animator>, position: Int, isForward: Boolean) {
            if (mAdapter.recyclerView.layoutManager is GridLayoutManager || mAdapter.recyclerView.layoutManager is StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.recyclerView, 0.5f)
            } else {
                if (isForward)
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }
    }

    class DetailViewHolder(view: View, adapter: FlexibleAdapter<*>) : BaseViewHolder(view, adapter) {
        init {
            horde_view_arrow.visibility = View.GONE
            horde_upper_limit_num.visibility = View.GONE
        }
        override fun bind(teamEntity: TeamEntity, isChief: Boolean, position: Int, swipable: Boolean) {
            super.bind(teamEntity, isChief, position, swipable)
            mDelete.setOnClickListener {
                mCLick?.onDelete(position)
            }
            swipe_content.setOnClickListener {
                mCLick?.onClick(position)
            }
            swipe_content.setOnLongClickListener {
                if (mAdapter.getItem(position) is IHeader || !swipable) {
                    false
                } else {
                    mCLick?.onLongClick(position)
                    true
                }
            }
        }
    }

    class SearchViewHolder(view: View, adapter: FlexibleAdapter<*>) : BaseViewHolder(view, adapter) {
        init {
            horde_view_arrow.visibility = View.GONE
            horde_upper_limit_num.visibility = View.GONE
        }

        override fun bind(teamEntity: TeamEntity, isChief: Boolean, position: Int, swipable: Boolean) {
            super.bind(teamEntity, isChief, position, swipable)
            mDelete.setOnClickListener {
                mCLick?.onDelete(position)
            }
            swipe_content.setOnClickListener {
                mCLick?.onClick(position)
            }
            swipe_content.setOnLongClickListener {
                if (mAdapter.getItem(position) is IHeader || !swipable) {
                    false
                } else {
                    mCLick?.onLongClick(position)
                    true
                }
            }
        }
    }

    class ScoreViewHolder(view: View, adapter: FlexibleAdapter<*>) : BaseViewHolder(view, adapter) {
        init {
            horde_view_arrow.visibility = View.VISIBLE
            horde_upper_limit_num.visibility = View.VISIBLE
        }
    }
}