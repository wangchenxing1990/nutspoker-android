package com.htgames.nutspoker.ui.activity.Club.fund

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.AbsListView
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.ClubAction
import com.htgames.nutspoker.ui.adapter.BaseAdapter
import com.htgames.nutspoker.ui.adapter.clubfund.FundLogItem
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.htgames.nutspoker.ui.items.FooterLoadingItem
import com.htgames.nutspoker.ui.items.HeaderItem
import com.netease.nim.uikit.bean.FundLogEntity
import com.netease.nim.uikit.common.DateTools
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.team.model.Team
import eu.davidea.fastscroller.FastScroller
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_fund_log.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat

/**
 * Created by 周智慧 on 2017/8/28.
 */
class FundParticularsAC : BaseTeamActivity() {
    var mFooterLoadingItem: FooterLoadingItem? = null
    var isLoadMore: Boolean = false
    var loadFinish: Boolean = false
    var mTeam: Team? = null
    var datas: MutableList<AbstractFlexibleItem<*>> = ArrayList()
    var mClubAction: ClubAction? = null
    lateinit var mAdapter: BaseAdapter
    companion object {
        fun start(activity: Activity, team: Team?) {
            val intent = Intent(activity, FundParticularsAC::class.java)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fund_log)
        setHeadTitle(R.string.fund_particulars)
        initAdapter()
        mClubAction = ClubAction(this, null)
        mSwipeRefreshLayout.isEnabled = false
        fetchLogList("0")//初始化
    }

    private fun initAdapter() {
        mFooterLoadingItem = FooterLoadingItem()
        mAdapter = BaseAdapter(datas, this, true)
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationDuration(250L)
                .setAnimationOnReverseScrolling(false)
                .isOnlyEntryAnimation = false
        lv_members.adapter = mAdapter
        lv_members.setHasFixedSize(true) //Size of RV will not change
        lv_members.setItemViewCacheSize(0) //Setting ViewCache to 0 (default=2) will animate items better while scrolling down+up with LinearLayout
        val fastScroller = findViewById(R.id.fast_scroller) as FastScroller
        fastScroller.setBubbleAndHandleColor(android.graphics.Color.RED)
        mAdapter.fastScroller = fastScroller
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .setSwipeEnabled(false)
                .setUnlinkAllItemsOnRemoveHeaders(true)
                .setDisplayHeadersAtStartUp(true)
                .setStickyHeaders(true)
        lv_members.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (loadFinish || isLoadMore || mSwipeRefreshLayout.isRefreshing || mFooterLoadingItem == null) {
                    return
                }
                val linearLayoutManager = lv_members.layoutManager as LinearLayoutManager
                val firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition()
                val totalItemCount = linearLayoutManager.itemCount
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && totalItemCount >= 1 && lastVisibleItemPos + 5 >= totalItemCount && lastVisibleItemPos - firstVisibleItemPos != totalItemCount) {
                    if (datas.size > 1) {
                        val lastId = (datas[datas.size - 2] as? FundLogItem)?.mEntity?.id ?: "0"
                        if (mFooterLoadingItem?.mState != FooterLoadingItem.STATE_LOADING) {
                            mFooterLoadingItem?.mState = FooterLoadingItem.STATE_LOADING
                            mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
                        }
                        fetchLogList(lastId)//分页加载更多
                    }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    fun fetchLogList(id: String) {
        isLoadMore = true
        mClubAction?.fundLog(mTeam?.id, id, object : GameRequestCallback {
            override fun onSuccess(data: JSONObject?) {
                if ("0" == id) {//第一页
                    datas.clear()
                    mAdapter.clear()
                    datas.add(mFooterLoadingItem!!)
                    mAdapter.addItem(mFooterLoadingItem!!)
                }
                var tempDatas: MutableList<AbstractFlexibleItem<*>> = ArrayList()
                val headerPos = datas.size
                if (data != null && data.getJSONArray("fund_log") != null && data.getJSONArray("fund_log").length() > 0) {
                    var fund_log: JSONArray = data.getJSONArray("fund_log")
                    (0..fund_log.length() - 1).forEach { i ->
                        var `object`: JSONObject = fund_log.getJSONObject(i)
                        var entity: FundLogEntity? = FundLogEntity.parseFundLogEntity(`object`)
                        var item: FundLogItem = FundLogItem(entity)
                        val sdf = SimpleDateFormat("yyyy年MM月")
                        val titleTime = sdf.format(entity?.time?.times(1000))
                        if (!DateTools.isTheSameMonth((datas[if (headerPos - 1 + i - 1 <= 0) 0 else headerPos - 1 + i - 1] as? FundLogItem)?.mEntity?.time ?: 0, item.mEntity?.time ?: 0)) {
                            val headerItem = HeaderItem("${entity?.time?.times(1000)}", titleTime, "", "", null, 0)
                            item.mHeader = headerItem
                        }
                        val insertPos = datas.size - 1
                        val insertAdapterPos = mAdapter.itemCount - 1
                        datas.add(insertPos, item)
                        tempDatas.add(item)
                        mAdapter.addItem(insertAdapterPos, item)
                    }
                }
                if (tempDatas.size <= 0) {
                    loadFinish = true
                    mFooterLoadingItem?.mState = FooterLoadingItem.STATE_FINISH
                    mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
                } else {
                    loadFinish = false
                    mFooterLoadingItem?.mState = FooterLoadingItem.STATE_PRE
                    mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
                    mAdapter.notifyItemInserted(mAdapter.itemCount)
                    mAdapter.mMaxNum = datas.size - 1
                    if ("0" == id && mAdapter.mAnimatorNotifierObserver != null) {
                        mAdapter.mAnimatorNotifierObserver.notified = false
                    }
                }
                checkNullData()
            }
            override fun onFailed(code: Int, response: JSONObject?) {
                checkNullData()
                if (mAdapter.itemCount > 0) {
                    mFooterLoadingItem?.mState = FooterLoadingItem.STATE_PRE
                    mAdapter.notifyItemChanged(mAdapter.itemCount - 1)
                }
            }
        })
    }

    fun checkNullData() {
        isLoadMore = false
        if (datas.size > 1) {
            mResultDataView?.successShow()
        } else {
            mResultDataView?.nullDataShow(R.string.no_data_fund_log)
        }
    }

    override fun onDestroy() {
        mClubAction?.onDestroy()
        mClubAction = null
        super.onDestroy()
    }
}