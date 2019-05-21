package com.htgames.nutspoker.ui.activity.Club.credit

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.ClubAction
import com.htgames.nutspoker.ui.activity.Club.credit.observer.CreditUpdateItem
import com.htgames.nutspoker.ui.activity.Club.credit.observer.CreditUpdateObserver
import com.htgames.nutspoker.ui.adapter.BaseAdapter
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.htgames.nutspoker.ui.items.HeaderItem
import com.netease.nim.uikit.AnimUtil
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.chesscircle.CacheConstant
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.chesscircle.DealerConstant
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClick
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nim.uikit.uinfo.UserInfoHelper
import com.netease.nim.uikit.uinfo.UserInfoObservable
import com.netease.nimlib.sdk.team.constant.TeamMemberType
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_credit_list.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by 周智慧 on 17/9/19.
 */
class CreditListAC : BaseTeamActivity(), View.OnClickListener, IClick, FlexibleAdapter.OnUpdateListener  {
    override fun onDelete(position: Int) {
    }

    override fun onClick(position: Int) {
        if (mAdapter.getItem(position) !is CreditListItem) {
            return
        }
        val teamMember = (mAdapter.getItem(position) as CreditListItem).mEntity ?: return
        CreditGrantAC.Companion.start(this, teamMember, mTeam)
    }

    override fun onLongClick(position: Int) {
    }

    override fun onUpdateEmptyView(size: Int) {
        if (size > 0) {
            mResultDataView.successShow()
        } else {
            mResultDataView.nullDataShow(R.string.no_data)
        }
    }

    var zeroCreditHeader: HeaderItem? = null
    var mTeam: Team? = null
    var datas: MutableList<AbstractFlexibleItem<*>> = ArrayList()
    lateinit var mAdapter: BaseAdapter
    var mClubAction: ClubAction? = null
    private var userInfoObserver: UserInfoObservable.UserInfoObserver? = null
    private val memberList = java.util.ArrayList<TeamMember>()
    private val memberAccounts = java.util.ArrayList<String>()
    private val mScoreMap = HashMap<String, Int>()
    private val mScoreList = ArrayList<CreditListEntity>()
    override fun onClick(v: View?) {
        val viewId = v?.id
        when (viewId) {
            R.id.tv_head_right -> {
                CreditLogAC.Companion.start(this, mTeam, CreditLogAC.LOG_TYPE_ALL, null)
            }
        }
    }

    companion object {
        fun start(activity: Activity, team: Team?) {
            val intent = Intent(activity, CreditListAC::class.java)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_list)
        setHeadTitle(R.string.credit)
        setHeadRightButton(R.string.operate_log, this)
        initAdapter()
//        registerUserInfoChangedObserver(true)
//        registerObservers(true)
        mClubAction = ClubAction(this, null)
        requestData()
        setSearchRelated()
        registerCreditUpdateObserver(true)
        fetchMemberData()
    }

    fun initAdapter() {
        mAdapter = BaseAdapter(datas, this, true)
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                //                .setAnimationInitialDelay(300)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(250L)
                .setAnimationOnReverseScrolling(false).isOnlyEntryAnimation = false
        lv_members.adapter = mAdapter
        lv_members.setHasFixedSize(true) //Size of RV will not change
        lv_members.setItemViewCacheSize(0) //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        val fastScroller = findViewById(R.id.fast_scroller) as FastScroller
//        fastScroller.addOnScrollStateChangeListener(this)
        fastScroller.setBubbleAndHandleColor(Color.RED)
        mAdapter.setFastScroller(fastScroller)
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                // Show Headers at startUp, 1st call, correctly executed, no warning log message!
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true)
    }

//    private fun registerUserInfoChangedObserver(register: Boolean) {
//        if (register) {
//            if (userInfoObserver == null) {
//                userInfoObserver = UserInfoObservable.UserInfoObserver {
//                    notifyData()//registerUserInfoChangedObserver
//                }
//            }
//            UserInfoHelper.registerObserver(userInfoObserver)
//        } else {
//            UserInfoHelper.unregisterObserver(userInfoObserver)
//        }
//    }

//    private fun registerObservers(register: Boolean) {
//        if (register) {
//            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver)
//        } else {
//            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver)
//        }
//    }
//
//    internal var teamMemberObserver: TeamDataCache.TeamMemberDataChangedObserver = object : TeamDataCache.TeamMemberDataChangedObserver {
//        override fun onUpdateTeamMember(members: List<TeamMember>) {
//            for (teamMember in members) {
//                if (mTeam?.id == teamMember.tid) {
//                    //新增会员
//                    if (!memberAccounts.contains(teamMember.account)) {
//                        memberAccounts.add(teamMember.account)
//                        memberList.add(teamMember)
//                    } else {
//                        //信息更新
//                        var i = 0
//                        for (currentMember in memberList) {
//                            if (currentMember.getAccount() == teamMember.account) {
//                                memberList.set(i, teamMember)
//                                break
//                            }
//                            i++
//                        }
//                    }
//                }
//            }
//            notifyData()//teamMemberObserver
//        }
//
//        override fun onRemoveTeamMember(member: TeamMember) {
//            removeMember(member.account)
//        }
//    }

//    private fun removeMember(uid: String) {
//        if (TextUtils.isEmpty(uid)) {
//            return
//        }
//        for (item in memberList) {
//            if (item.account != null && item.account == uid) {
//                memberList.remove(item)
//                memberAccounts.remove(item.account)
//                break
//            }
//        }
//        notifyData()//removeMember
//    }

    private fun requestData() {
        val members = TeamDataCache.getInstance().getTeamMemberList(mTeam?.id)
        if (!members.isEmpty()) {
            memberList.clear()
            memberAccounts.clear()
            for (member in members) {
                memberAccounts.add(member.account)
                memberList.add(member)
            }
//            notifyData()//requestData
        }
    }

    private fun fetchMemberData() {
        mClubAction?.creditScoreList(mTeam?.id, object : GameRequestCallback {
            override fun onSuccess(response: JSONObject?) {
                if (response == null || !response?.has("data")) {
                    return
                }
                val datas = response?.optJSONArray("data")
                (0..(datas.length() - 1))
                        .map { datas.getJSONObject(it) }
                        .forEach {
                            val entity = CreditListEntity(it.optString("uid"), it.optInt("score"))
                            mScoreMap.put(entity.uid, entity.score)
                        }
                memberList.indices
                        .map { memberList[it] }
                        .filter { it -> it.type != TeamMemberType.Owner && it.type != TeamMemberType.Manager && !mScoreMap.containsKey(it.account) }
                        .forEach { it -> mScoreMap.put(it.account, 0) }
                notifyData()//onSuccess
            }
            override fun onFailed(code: Int, response: JSONObject?) {
            }
        })
    }

    private fun notifyData() {
        var numWithoutScore: Int = 0
        datas.clear()
        mScoreList.clear()
        zeroCreditHeader = null
        mScoreMap.forEach {
            mScoreList.add(CreditListEntity(it.key, it.value))
            if (it.value <= 0) {
                numWithoutScore++
            }
        }
        Collections.sort(mScoreList, CreditComparator.comparator)
        mScoreList.forEach {
            var item = CreditListItem(it)
            if (zeroCreditHeader == null && it.score <= 0) {//信用分为0的加上一个header
                zeroCreditHeader = HeaderItem("${Math.random()}", "未设置信用分(${numWithoutScore})", "", "", null, 0)
                item.mHeader = zeroCreditHeader
            }
            datas.add(item)
        }
        mAdapter.mMaxNum = memberList.size
        mAdapter.updateDataSet(datas)
        if (mAdapter.mAnimatorNotifierObserver != null) {
            mAdapter.mAnimatorNotifierObserver.notified = false
        }
    }

    private var queryText: String? = null
    private fun setSearchRelated() {
        search_mtt_mgr_tv_cancel.setOnClickListener {
            queryText = ""
            edit_search_record.setText("")
            filterSearch()
            showKeyboard(false)
            showCancelSearchTV(false)
        }
        edit_search_record.setOnClickListener {
            if (search_mtt_mgr_tv_cancel.visibility != View.VISIBLE) {
                showCancelSearchTV(true)
            }
        }
        edit_search_record.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                queryText = s.toString()
                filterSearch()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun showCancelSearchTV(show: Boolean) {
        if (show) {
            AnimUtil.scaleLargeIn(search_mtt_mgr_tv_cancel, 300)
        } else {
            AnimUtil.scaleSmall(search_mtt_mgr_tv_cancel, 300)
        }
    }

    private fun filterSearch() {
        if (mAdapter.hasNewSearchText(queryText)) {
            mAdapter.searchText = queryText
            mAdapter.filterItems(300)
        }
    }

    var creditUpdateCallback: CreditUpdateObserver.CreditUpdateCallback = object : CreditUpdateObserver.CreditUpdateCallback {
        override fun onUpdate(item: CreditUpdateItem) {
            for (i in 0..(mAdapter.itemCount - 1)) {
                var adapterItem: AbstractFlexibleItem<*>? = mAdapter.getItem(i) as? CreditListItem ?: continue
                if (item.uid == (adapterItem as CreditListItem).mEntity?.uid) {
                    val dumpEntity = adapterItem.mEntity
                    val finalNum = if (item.updateType == CreditUpdateItem.UPDATE_TYPE_ADD) ((dumpEntity?.score ?: 0) + item.updateCreditNum.toInt()) else 0
                    if (item.updateType == CreditUpdateItem.UPDATE_TYPE_ADD && (dumpEntity?.score ?: 0) <= 0) {//从0到有
                        mScoreMap.put(dumpEntity?.uid ?: "1", finalNum)
                        notifyData()//从0到有
                    } else if (item.updateType == CreditUpdateItem.UPDATE_TYPE_ADD && (dumpEntity?.score ?: 0) > 0) {//从有到有
                        adapterItem.mEntity = CreditListEntity(dumpEntity?.uid ?: "${Math.random()}", finalNum)//if (item.updateType == CreditUpdateItem.UPDATE_TYPE_ADD) ((adapterItem.mEntity?.score ?: 0) + item.updateCreditNum.toInt()) else 0//
                        if (adapterItem.mHeader is HeaderItem) {
                            adapterItem.mHeader = HeaderItem("${Math.random()}", (adapterItem.mHeader as HeaderItem).mLeft, "", "", null, 0)
                        }
                        mAdapter.notifyItemChanged(i)
                    } else if (item.updateType == CreditUpdateItem.UPDATE_TYPE_CLEAR) {//清除
                        mScoreMap.put(dumpEntity?.uid ?: "1", finalNum)
                        notifyData()//清除
                    }
                    break
                }
            }
        }
    }
    fun registerCreditUpdateObserver(register: Boolean) {
        if (register) {
            CreditUpdateObserver.getInstance().registerCallback(creditUpdateCallback)
        } else {
            CreditUpdateObserver.getInstance().unregisterCallback(creditUpdateCallback)
        }
    }

    override fun onDestroy() {
//        registerUserInfoChangedObserver(false)
//        registerObservers(false)
        mClubAction?.onDestroy()
        mClubAction = null
        registerCreditUpdateObserver(false)
        super.onDestroy()
    }
}