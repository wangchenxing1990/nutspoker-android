package com.htgames.nutspoker.game.match.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.app_msg.adapter.ControlPageAdapter
import com.htgames.nutspoker.game.match.fragment.AddChannelFrgBase
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.bean.GameEntity
import com.netease.nim.uikit.bean.SearchUserBean
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.ui.imageview.HeadImageView
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IOperationClick
import com.netease.nim.uikit.nav.UrlConstants
import kotlinx.android.synthetic.main.activity_add_channel.*
import java.io.Serializable
import java.util.*

class AddChannelAC : BaseActivity() {
    companion object {
        fun start(activity: Activity?) {
            val intent = Intent(activity, AddChannelAC::class.java)
            intent.putExtras(activity?.intent?.extras)
            activity?.startActivity(intent)
        }
        val KEY_CHANNEL_TYPE = "key_channel_type"
        val CHANNEL_TYPE_PERSONAL = 0//私人渠道
        val CHANNEL_TYPE_CLUB = 1//俱乐部渠道
    }
    var gameInfo: GameEntity? = null
    lateinit var mAddChannelFrg: AddChannelFrgBase
    lateinit var mAddClubChannelFrg: AddChannelFrgBase
    var changePageByScroll = true
    lateinit var mAdapter: ControlPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameInfo = intent.getSerializableExtra(UrlConstants.ADD_GAME_MANAGER_GAME_INFO) as GameEntity?
        setContentView(R.layout.activity_add_channel)
        setHeadTitle(R.string.add_mttgamemgr)
        initFragment()
        initSearchRelated()
    }

    private fun initFragment() {
        mAddChannelFrg = AddChannelFrgBase.newInstance(CHANNEL_TYPE_PERSONAL)
        mAddClubChannelFrg = AddChannelFrgBase.newInstance(CHANNEL_TYPE_CLUB)
        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(mAddChannelFrg)
        fragmentList.add(mAddClubChannelFrg)
        mAdapter = ControlPageAdapter(supportFragmentManager, fragmentList, this)
        viewpager_add_channel.setAdapter(mAdapter)
        viewpager_add_channel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                setSwipeBackEnable(position == 0)
                search_mtt_mgr_edit_search.setHint(if (position == 0) R.string.search_hint_user else R.string.search_hint_club_id)
                if (changePageByScroll) {
                    iv_select_channel_personal.isEnabled = position != 0
                    iv_select_channel_club.isEnabled = position != 1
                }
                changePageByScroll = true
                LogUtil.i("changePageByScroll", "changePageByScroll: $changePageByScroll    pos: $position")
            }
            override fun onPageScrollStateChanged(state: Int) {}
        })
        iv_select_channel_personal.setOnClickListener {
            changePageByScroll = false
            if (iv_select_channel_club.isInAnim) {
                return@setOnClickListener
            }
            iv_select_channel_personal.isEnabled = false
            iv_select_channel_club.isEnabled = true
            viewpager_add_channel.currentItem = 0
        }
        iv_select_channel_club.setOnClickListener {
            changePageByScroll = false
            if (iv_select_channel_personal.isInAnim) {
                return@setOnClickListener
            }
            iv_select_channel_personal.isEnabled = true
            iv_select_channel_club.isEnabled = false
            viewpager_add_channel.currentItem = 1
        }
    }

    fun initSearchRelated() {
        search_mtt_mgr_edit_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                var key = search_mtt_mgr_edit_search.getText().toString()
                key = key.trim({ it <= ' ' })
                if (StringUtil.isEmpty(key)) {
                    Toast.makeText(getApplicationContext(), R.string.not_allow_empty, Toast.LENGTH_SHORT).show()
                } else {
                    (mAdapter.getItem(viewpager_add_channel.currentItem) as? AddChannelFrgBase)?.search(key)
                }
                return@OnEditorActionListener true
            }
            false
        })
        search_mtt_mgr_edit_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s == null || StringUtil.isEmpty(s.toString())) {//输入框的内容为空时显示"历史记录"，隐藏"搜索结果"
                    if (mAddChannelFrg.showHistory == false) {
                        mAddChannelFrg.changeIsShowHistory(true)
                    }
                    if (mAddClubChannelFrg.showHistory == false) {
                        mAddClubChannelFrg.changeIsShowHistory(true)
                    }
                }
            }
        })
        search_mtt_mgr_edit_search.requestFocus()
        search_mtt_mgr_edit_search.setIconResource(R.mipmap.icon_search)
        search_mtt_mgr_tv_cancel.setOnClickListener {
            var key = search_mtt_mgr_edit_search.getText().toString()
            key = key.trim({ it <= ' ' })
            if (key.length > 0) {
                search_mtt_mgr_edit_search.setText("")
            } else {
                finish()
            }
        }
    }

    class HistoryMgrList : Serializable {
        var list: ArrayList<SearchUserBean>? = null
    }

    class AddChannelAdap : RecyclerView.Adapter<RecyclerView.ViewHolder> {
        var gameInfo: GameEntity? = null
        var channelType = AddChannelAC.CHANNEL_TYPE_PERSONAL
        var mDatas: ArrayList<SearchUserBean> = ArrayList()
        var mActivity: Activity? = null
        var mClickListener: IOperationClick? = null
        var showHistory = true//默认看是历史记录
        override fun getItemCount(): Int {
            return mDatas.size
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            return AddChannelVH(LayoutInflater.from(mActivity).inflate(R.layout.item_channel_to_add, parent, false))
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            if (holder is AddChannelVH && position < mDatas.size && position >= 0) {
                holder.bind(mDatas[position], this)
            }
        }
        constructor(datas: List<SearchUserBean>?, listeners: Any?) {
            if (datas != null) {
                mDatas.addAll(datas)
            }
            if (listeners is Fragment) {
                mActivity = listeners.activity
            }
            if (listeners is Activity) {
                mActivity = listeners
            }
            if (listeners is IOperationClick) {
                mClickListener = listeners
            }
        }
        fun updateData(datas: List<SearchUserBean>?) {
            mDatas.clear()
            if (datas != null) {
                mDatas.addAll(datas)
            }
        }
    }

    class AddChannelVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contacts_item_head = itemView.findViewById(R.id.contacts_item_head) as HeadImageView?
        var club_head_image_mask = itemView.findViewById(R.id.club_head_image_mask)
        var contacts_item_name = itemView.findViewById(R.id.contacts_item_name) as TextView
        var contacts_item_desc = itemView.findViewById(R.id.contacts_item_desc) as TextView
        var tv_action_agree = itemView.findViewById(R.id.tv_action_agree) as TextView
        var tv_action_reject = itemView.findViewById(R.id.tv_action_reject) as TextView
        var tv_club_myself = itemView.findViewById(R.id.tv_club_myself) as TextView
        fun bind(bean: SearchUserBean, adap: AddChannelAdap) {
            tv_club_myself.visibility = if (DemoCache.getAccount() == bean.id) View.VISIBLE else View.GONE
            if (adap.channelType == CHANNEL_TYPE_PERSONAL) {
                contacts_item_head?.loadBuddyAvatar(bean.id)
                club_head_image_mask.visibility = View.GONE
                contacts_item_name.text = NimUserInfoCache.getInstance().getUserDisplayName(bean.id)
                contacts_item_desc.setText("ID: " + bean.uuid)
            } else if (adap.channelType == CHANNEL_TYPE_CLUB) {
                val team = TeamDataCache.getInstance().getTeamById(bean.tid)
                if (team == null) {
                    TeamDataCache.getInstance().fetchTeamById(bean.tid) { success, result ->
                        if (success && result != null && contacts_item_head != null) {
                            contacts_item_head?.loadClubAvatarByUrl(bean.id, ClubConstant.getClubExtAvatar(result.extServer), HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
                            contacts_item_name.text = result.name
                        } else {
                        }
                    }
                } else {
                    contacts_item_head?.loadClubAvatarByUrl(bean.tid, "", HeadImageView.DEFAULT_AVATAR_THUMB_SIZE)
                    contacts_item_name.text = team.name
                }
                club_head_image_mask.visibility = View.VISIBLE
                contacts_item_desc.setText("ID: " + bean.vid)
            }
            //下面是两个操作按钮的逻辑
            var hasThisChannleAlready = false
            if (ChessApp.gameManagers.containsKey(adap.gameInfo?.gid)) {
                if (adap.channelType == AddChannelAC.CHANNEL_TYPE_PERSONAL) {
                    val list = ChessApp.gameManagers[adap.gameInfo?.gid]
                    for (i in 0 until (list?.size ?: 0)) {
                        val item = list?.get(i)!!
                        if (!StringUtil.isSpace(item.account) && item.account == bean.id) {
                            hasThisChannleAlready = true
                            break
                        }
                    }
                } else {
                    val list = ChessApp.gameChannels[adap.gameInfo?.gid]
                    for (i in 0 until (list?.size ?: 0)) {
                        val item = list?.get(i)!!
                        if (!StringUtil.isSpace(item.tid) && item.tid == bean.tid) {
                            hasThisChannleAlready = true
                            break
                        }
                    }
                }
            }
            tv_action_agree.setText(if (hasThisChannleAlready) R.string.added else R.string.add)
            tv_action_agree.isEnabled = !hasThisChannleAlready
            tv_action_agree.setOnClickListener { adap.mClickListener?.onAgree(adapterPosition, bean) }
            tv_action_reject.visibility = if (adap.showHistory) View.VISIBLE else View.GONE
            tv_action_reject.setOnClickListener { adap.mClickListener?.onReject(adapterPosition, bean) }
        }
    }
}