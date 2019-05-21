package com.htgames.nutspoker.ui.activity.Club.fund

import com.htgames.nutspoker.R
import com.htgames.nutspoker.ui.activity.Club.fund.observer.FundUpdateOberver
import com.htgames.nutspoker.ui.activity.Club.fund.observer.UpdateItem
import com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.common.ui.dialog.DialogMaker
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClick
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_fund_grant.*

/**
 * Created by 周智慧 on 2017/8/28.
 */
class FundGrantAC : BaseTeamActivity(), FastScroller.OnScrollStateChangeListener, IClick, FlexibleAdapter.OnUpdateListener {
    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            mResultDataView.successShow()
        } else {
            mResultDataView.nullDataShow(R.string.no_data)
        }
    }

    override fun onFastScrollerStateChange(scrolling: Boolean) {
    }
    var mTotalFund = "0"
    private var userInfoObserver: com.netease.nim.uikit.uinfo.UserInfoObservable.UserInfoObserver? = null
    private val memberList = java.util.ArrayList<TeamMember>()
    private val memberAccounts = java.util.ArrayList<String>()
    var teamId: String? = ""
    var mTeam: Team? = null
    lateinit var mAdapter: com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew
    internal var datas: MutableList<eu.davidea.flexibleadapter.items.AbstractFlexibleItem<*>> = java.util.ArrayList()
    //搜索相关
    private var queryText: String? = null
    companion object {
        fun start(activity: android.app.Activity, team: com.netease.nimlib.sdk.team.model.Team?, totalFund: String) {
            val intent = android.content.Intent(activity, FundGrantAC::class.java)
            intent.putExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_TEAM_DATA, team)
            intent.putExtra(ClubConstant.KEY_TOTAL_FUND, totalFund)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.htgames.nutspoker.R.layout.activity_fund_grant)
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        teamId = mTeam?.id
        setHeadTitle("发放基金")
        mTotalFund = intent.getStringExtra(ClubConstant.KEY_TOTAL_FUND)
        tv_fund_num.text = mTotalFund
        rl_recharge_fund.setOnClickListener { FundRechargeAC.Companion.start(this, team = mTeam) }
        initAdapter()
        registerUserInfoChangedObserver(true)
        registerObservers(true)
        requestData()
        setSearchRelated()
    }

    private fun registerUserInfoChangedObserver(register: Boolean) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = com.netease.nim.uikit.uinfo.UserInfoObservable.UserInfoObserver { notifyData() }
            }
            com.netease.nim.uikit.uinfo.UserInfoHelper.registerObserver(userInfoObserver)
            FundUpdateOberver.getInstance().registerCallback(fundUpdateCallback)
        } else {
            com.netease.nim.uikit.uinfo.UserInfoHelper.unregisterObserver(userInfoObserver)
            FundUpdateOberver.getInstance().unregisterCallback(fundUpdateCallback)
        }
    }

    private fun registerObservers(register: Boolean) {
        if (register) {
            com.netease.nim.uikit.cache.TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver)
        } else {
            com.netease.nim.uikit.cache.TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver)
        }
    }

    internal var teamMemberObserver: com.netease.nim.uikit.cache.TeamDataCache.TeamMemberDataChangedObserver = object : com.netease.nim.uikit.cache.TeamDataCache.TeamMemberDataChangedObserver {
        override fun onUpdateTeamMember(members: List<com.netease.nimlib.sdk.team.model.TeamMember>) {
            for (teamMember in members) {
                if (teamId == teamMember.tid) {
                    //新增会员
                    if (!memberAccounts.contains(teamMember.account)) {
                        memberAccounts.add(teamMember.account)
                        memberList.add(teamMember)
                    } else {
                        //信息更新
                        var i = 0
                        for (currentMember in memberList) {
                            if (currentMember.getAccount() == teamMember.account) {
                                memberList.set(i, teamMember)
                                break
                            }
                            i++
                        }
                    }
                }
            }
            notifyData()
        }
        override fun onRemoveTeamMember(member: com.netease.nimlib.sdk.team.model.TeamMember) {
            removeMember(member.account)
        }
    }

    var fundUpdateCallback: FundUpdateOberver.FundUpdateCallback = object : FundUpdateOberver.FundUpdateCallback {
        override fun onUpdate(item: UpdateItem?) {
            if (item == null) {
                return
            }
            mTotalFund = "${mTotalFund.toInt() + item.updateNum}"
            tv_fund_num?.text = mTotalFund
            searchTeam(teamId, object : RequestCallback<Team> {
                override fun onSuccess(team: Team) {
                    DialogMaker.dismissProgressDialog()
                    TeamDataCache.getInstance().addOrUpdateTeam(team)
                    mTeam = team
                    mTotalFund = ClubConstant.getClubFund(team)
                    tv_fund_num?.text = mTotalFund
                }
                override fun onFailed(code: Int) {
                    DialogMaker.dismissProgressDialog()
                    if (code == ResponseCode.RES_ETIMEOUT.toInt()) {
                    }
                }
                override fun onException(throwable: Throwable) {
                    DialogMaker.dismissProgressDialog()
                }
            })
        }
    }

    /**
     * 移除群成员成功后，删除列表中的群成员
     */
    private fun removeMember(uid: String) {
        if (android.text.TextUtils.isEmpty(uid)) {
            return
        }
        for (item in memberList) {
            if (item.account != null && item.account == uid) {
                memberList.remove(item)
                memberAccounts.remove(item.account)
                break
            }
        }
        notifyData()
    }

    private fun requestData() {
        val members = com.netease.nim.uikit.cache.TeamDataCache.getInstance().getTeamMemberList(teamId)
        if (!members.isEmpty()) {
            memberList.clear()
            memberAccounts.clear()
            for (member in members) {
                memberAccounts.add(member.account)
                memberList.add(member)
            }
            notifyData()
        }
    }

    private fun initAdapter() {
        mAdapter = com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberAdapNew(datas, this, true)
        mAdapter.isMeCreator = false//做个变量禁止左划
        mAdapter.showArrow = true
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(250L)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(false)
        lv_members.adapter = mAdapter
        lv_members.setHasFixedSize(true) //Size of RV will not change
        lv_members.setItemViewCacheSize(0) //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        val fastScroller = findViewById(com.htgames.nutspoker.R.id.fast_scroller) as eu.davidea.fastscroller.FastScroller
        fastScroller.addOnScrollStateChangeListener(this)
        fastScroller.setBubbleAndHandleColor(android.graphics.Color.RED)
        mAdapter.setFastScroller(fastScroller)
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true)
    }

    private fun notifyData() {
        datas.clear()
        val headerManager = com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberHeader("headerManager")
        val headerNormalMember = com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberHeader("headerNormalMember")
        val ownerList = java.util.ArrayList<ClubMemberItem>()
        val managerList = java.util.ArrayList<ClubMemberItem>()
        val normal = java.util.ArrayList<ClubMemberItem>()
        if (!memberList.isEmpty()) {
            for (tm in memberList) {
                val clubMemberItem = com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem(tm)
                if (tm.type == com.netease.nimlib.sdk.team.constant.TeamMemberType.Owner) {
                    ownerList.add(clubMemberItem)
                } else if (tm.type == com.netease.nimlib.sdk.team.constant.TeamMemberType.Manager) {
                    managerList.add(clubMemberItem)
                } else if (tm.type == com.netease.nimlib.sdk.team.constant.TeamMemberType.Normal) {
                    normal.add(clubMemberItem)
                }
            }
            val normalHeadStr = resources.getString(com.htgames.nutspoker.R.string.item_head_member_format, normal.size)
            headerManager.title = resources.getString(com.htgames.nutspoker.R.string.item_head_mgr_format, ownerList.size + managerList.size)
            headerNormalMember.title = normalHeadStr
            if (ownerList.size > 0) {
                ownerList[0].header = headerManager
            }
            if (normal.size > 0) {
                java.util.Collections.sort(normal, com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem.comparator)
                normal[0].header = headerNormalMember
            }
            datas.addAll(ownerList)
            datas.addAll(managerList)
            datas.addAll(normal)
            mAdapter.mMaxNum = memberList.size
            mAdapter.updateDataSet(datas)
            if (mAdapter.mAnimatorNotifierObserver != null) {
                mAdapter.mAnimatorNotifierObserver.notified = false
            }
        }
    }

    override fun onClick(position: Int) {
        if (mAdapter.getItem(position) !is com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem) {
            return
        }
        val member = (mAdapter.getItem(position) as com.htgames.nutspoker.ui.adapter.clubmember.ClubMemberItem).mTeamMember ?: return
        com.htgames.nutspoker.ui.activity.Club.fund.FundGrantMemberAC.Companion.start(this, member, mTotalFund, mTeam)
    }

    override fun onDelete(position: Int) {
    }

    override fun onLongClick(position: Int) {
    }

    private fun setSearchRelated() {
        search_mtt_mgr_tv_cancel.setOnClickListener {
            queryText = ""
            edit_search_record.setText("")
            filterSearch()
            showKeyboard(false)
            showCancelSearchTV(false)
        }
        edit_search_record.setOnClickListener {
            if (search_mtt_mgr_tv_cancel.visibility != android.view.View.VISIBLE) {
                showCancelSearchTV(true)
            }
        }
        edit_search_record.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                queryText = s.toString()
                filterSearch()
            }
            override fun afterTextChanged(s: android.text.Editable) {

            }
        })
    }

    private fun showCancelSearchTV(show: Boolean) {
        if (show) {
            com.netease.nim.uikit.AnimUtil.scaleLargeIn(search_mtt_mgr_tv_cancel, 300)
        } else {
            com.netease.nim.uikit.AnimUtil.scaleSmall(search_mtt_mgr_tv_cancel, 300)
        }
    }

    private fun filterSearch() {
        if (mAdapter.hasNewSearchText(queryText)) {
            mAdapter.searchText = queryText
            mAdapter.filterItems(300)
        }
    }

    override fun onBackPressed() {
        if (!StringUtil.isSpace(queryText)) {
            if (!StringUtil.isSpace(queryText)) {
                queryText = ""
                edit_search_record.setText("")
                filterSearch()
            }
            return
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        registerUserInfoChangedObserver(false)
        registerObservers(false)
        super.onDestroy()
    }
}