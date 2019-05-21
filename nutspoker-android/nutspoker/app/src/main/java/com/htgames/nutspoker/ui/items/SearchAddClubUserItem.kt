package com.htgames.nutspoker.ui.items
import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.main.activity.AddClubMember
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberHeader
import com.netease.nim.uikit.bean.SearchUserBean
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.interfaces.IClick
import com.netease.nimlib.sdk.team.model.Team
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.AbstractItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.flexibleadapter.utils.DrawableUtils
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import eu.davidea.viewholders.FlexibleViewHolder
import java.io.Serializable

/**
 * Created by 周智慧 on 2017/6/2.
 */
class SearchAddClubUserItem(var mMember: SearchUserBean?) : AbstractItem<SearchAddClubUserItem.SimpleViewHolder>(mMember!!.id), ISectionable<SearchAddClubUserItem.SimpleViewHolder, ClubMemberHeader>, IFilterable, Serializable {
    internal var header: ClubMemberHeader? = null
    var nickName: String? = null
    lateinit var mTeam: Team

    init {
        nickName = NimUserInfoCache.getInstance().getUserDisplayName(mMember!!.id)
        setDraggable(true)
        setSwipeable(true)
    }

    constructor(tm: SearchUserBean, header: ClubMemberHeader?) : this(tm) {
        this.header = header
    }

    override fun getHeader(): ClubMemberHeader? {
        return header
    }

    override fun setHeader(header: ClubMemberHeader?) {
        this.header = header
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_phonecontact_item
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): SimpleViewHolder {
        return SimpleViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>, holder: SimpleViewHolder, position: Int, payloads: List<*>) {
        if (mMember == null) {
            return
        }
        val context = holder.itemView.getContext()
        holder.listener = if (adapter is ClubMemberAdapNew) (adapter as ClubMemberAdapNew).listener else null
        // Background, when bound the first time
        if (payloads.size == 0) {
            val drawable = DrawableUtils.getSelectableBackgroundCompat(
                    Color.WHITE, Color.parseColor("#dddddd"), //Same color of divider
                    DrawableUtils.getColorControlHighlight(context))
            DrawableUtils.setBackgroundCompat(holder.itemView, drawable)
        }
        holder.contacts_item_head.loadBuddyAvatar(mMember!!.id)
        holder.tv_contact_name.text = (NimUserInfoCache.getInstance().getUserDisplayName(mMember!!.id))
        if (mMember!!.id == DemoCache.getAccount()) {
            holder.tv_club_myself.visibility = View.VISIBLE
            holder.tv_club_myself.setText(R.string.me)
            holder.btn_contact_action.visibility = View.GONE
        } else {
            holder.tv_club_myself.visibility = View.GONE
            holder.btn_contact_action.visibility = View.VISIBLE
            if (isClubMember(mMember!!.id)) {
                holder.btn_contact_action.setOnClickListener(null)
                holder.btn_contact_action.setBackgroundResource(android.R.color.transparent)
                holder.btn_contact_action.setText(R.string.friend_action_team_member_already)
                holder.btn_contact_action.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.login_grey_color))
            } else if (hasInvited(mMember!!.id, holder)) {
                holder.btn_contact_action.setOnClickListener(null)
                holder.btn_contact_action.setBackgroundResource(android.R.color.transparent)
                holder.btn_contact_action.setText(R.string.friend_action_invite_already)
                holder.btn_contact_action.setTextColor(ChessApp.sAppContext.getResources().getColor(R.color.login_grey_color))
            } else {
                holder.btn_contact_action.setBackgroundResource(R.drawable.bg_login_btn)
                holder.btn_contact_action.text = context.getString(R.string.friend_action_invite)
                holder.btn_contact_action.setTextColor(Color.WHITE)
                holder.btn_contact_action.setOnClickListener {
                    var list = ArrayList<String>()
                    list.add(mMember!!.id)
                    if (holder.listener is AddClubMember) {
                        (holder.listener as AddClubMember).inviteMembers(list, position)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            //点击
            override fun onClick(v: View) {
                if (holder.listener != null) {
                    holder.listener!!.onClick(position)
                }
            }
        })
        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            //长按
            override fun onLongClick(v: View): Boolean {
                if (holder.listener != null) {
                    holder.listener!!.onLongClick(position)
                }
                return true
            }
        })
        // In case of searchText matches with Title or with a field this will be highlighted
        if (adapter.hasSearchText()) {
            FlexibleUtils.colorAccent = ChessApp.sAppContext.getResources().getColor(R.color.login_solid_color)
            FlexibleUtils.highlightText(holder.tv_contact_name, holder.tv_contact_name.getText().toString(), adapter.getSearchText())
        }
    }

    override fun filter(constraint: String): Boolean {
        return getTitle() != null && getTitle().toLowerCase().trim({ it <= ' ' }).contains(constraint) || nickName != null && nickName!!.toLowerCase().trim { it <= ' ' }.contains(constraint)
    }

    public class SimpleViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        var mContext: Context
        var listener: IClick? = null
        var tv_club_myself: TextView
        var ll_contact: LinearLayout
        var contacts_item_head: HeadImageView
        var btn_contact_action: TextView
        var tv_contact_name: TextView
        var tv_contact_desc: TextView

        init {
            this.mContext = view.getContext()
            ll_contact = view.findViewById(R.id.ll_contact) as LinearLayout
            contacts_item_head = view.findViewById(R.id.contacts_item_head) as HeadImageView
            btn_contact_action = view.findViewById(R.id.btn_contact_action) as TextView
            tv_contact_name = view.findViewById(R.id.contacts_item_name) as TextView
            tv_contact_desc = view.findViewById(R.id.contacts_item_desc) as TextView
            tv_club_myself = view.findViewById(R.id.tv_club_myself) as TextView
        }

        protected override fun setDragHandleView(view: View) {
            if (mAdapter.isHandleDragEnabled()) {
                view.setVisibility(View.VISIBLE)
                super.setDragHandleView(view)
            } else {
                view.setVisibility(View.GONE)
            }
        }

        override fun onClick(view: View) {
            Toast.makeText(mContext, "Click on " + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show()
            super.onClick(view)
        }

        override fun onLongClick(view: View): Boolean {
            Toast.makeText(mContext, "LongClick on " + " position " + getAdapterPosition(), Toast.LENGTH_SHORT).show()
            return super.onLongClick(view)
        }

        override fun toggleActivation() {
            super.toggleActivation()
            // Here we use a custom Animation inside the ItemView
        }

        override fun getActivationElevation(): Float {
            return ScreenUtil.dp2px(itemView.getContext(), 4f).toFloat()
        }

        protected override fun shouldActivateViewWhileSwiping(): Boolean {
            return false//default=false
        }

        protected override fun shouldAddSelectionInActionMode(): Boolean {
            return false//default=false
        }

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            if (mAdapter.getRecyclerView().getLayoutManager() is GridLayoutManager || mAdapter.getRecyclerView().getLayoutManager() is StaggeredGridLayoutManager) {
                if (position % 2 != 0)
                    AnimatorHelper.slideInFromRightAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f)
                else
                    AnimatorHelper.slideInFromLeftAnimator(animators, itemView, mAdapter.getRecyclerView(), 0.5f)
            } else {
                //Linear layout
                if (isForward)
                    AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.getRecyclerView())
                else
                    AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.getRecyclerView())
            }
        }

        override fun onItemReleased(position: Int) {
            super.onItemReleased(position)
        }
    }

    override fun toString(): String {
        return "SearchAddClubUserItem[" + super.toString() + "]"
    }

    override fun equals(inObject: Any?): Boolean {
        if (inObject is SearchAddClubUserItem) {
            return this.getId() == inObject.getId()
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun isClubMember(uid: String): Boolean {
        val teamMembers = TeamDataCache.getInstance().getTeamMemberList(mTeam.id)
        teamMembers.forEach {
            if (uid == it.account) {
                return true
            }
        }
        return false
    }

    fun hasInvited(uid: String, holder: SimpleViewHolder): Boolean {
        if (holder.listener is AddClubMember) {
            return (holder.listener as AddClubMember).tempAlreadyInviteAccountList.contains(uid)
        }
        return false
    }
}
