package com.htgames.nutspoker.ui.activity.Record

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.db.GameRecordDBHelper
import com.htgames.nutspoker.interfaces.RequestCallback
import com.htgames.nutspoker.tool.json.RecordJsonTools
import com.htgames.nutspoker.ui.action.RecordAction
import com.htgames.nutspoker.ui.adapter.record.RecordListAdap
import com.htgames.nutspoker.ui.adapter.record.RecordListItem
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.base.BaseAppCompatActivity
import com.netease.nim.uikit.bean.GameBillEntity
import com.netease.nim.uikit.common.framework.ThreadUtil
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClick
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import kotlinx.android.synthetic.main.activity_record_search.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by 周智慧 on 2017/8/8.
 */
class RecordSearchAC : BaseActivity(), IClick {
    private var finishing: Boolean = false
    var recordList: ArrayList<GameBillEntity> = ArrayList()
    var mDatas: ArrayList<RecordListItem> = ArrayList<RecordListItem>()
    var mRecordAction: RecordAction? = null
    var isLoadMore = false
    var name: String = ""
    var last_gid: String = ""
    lateinit var mAdapter: RecordListAdap
    companion object {
        val TAG = RecordSearchAC::class.java.simpleName
        var SEARCH_TYPE_MY_LIST = 0
        var SEARCH_TYPE_TEAM_LIST = 1
        var SEARCH_TYPE_HORDE_LIST = 2
        fun startByMyList(activity: Activity, view: View, type: Int, tid: String, horde_id: String) {
            val intent = Intent(activity, RecordSearchAC::class.java)
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            intent.putExtra("x", location[0])
            intent.putExtra("y", location[1])
            intent.putExtra("type", type)
            intent.putExtra("tid", tid)
            intent.putExtra("horde_id", horde_id)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_search)
        mRecordAction = RecordAction(this, null)
        edit_search_record.setText("")
        edit_search_record.setIconResource(R.mipmap.icon_search)
        edit_search_record.getViewTreeObserver().addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edit_search_record.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                }
                performEnterAnimation()
            }
        })
        edit_search_record.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                searchKey()
                return@OnEditorActionListener true
            }
            false
        })
        recyclerview_team_record.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if (isLoadMore) {
                    return
                }
                val linearLayoutManager = recyclerview_team_record.getLayoutManager() as LinearLayoutManager
                val firstVisibleItemPos = linearLayoutManager.findFirstVisibleItemPosition()
                val lastVisibleItemPos = linearLayoutManager.findLastVisibleItemPosition()
                val totalItemCount = linearLayoutManager.itemCount
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && totalItemCount >= 1 && lastVisibleItemPos + 5 >= totalItemCount && lastVisibleItemPos - firstVisibleItemPos != totalItemCount) {
                    if (recordList != null && recordList.size != 0) {
                        last_gid = recordList[recordList.size - 1].gameInfo.gid
                        val endTime = recordList[recordList.size - 1].gameInfo.endTime
                        val type = intent.getIntExtra("type", SEARCH_TYPE_MY_LIST)
                        if (type == SEARCH_TYPE_MY_LIST) {
                            searchMyList(last_gid, name)
                        } else if (type == SEARCH_TYPE_TEAM_LIST) {
                            searchTeamList(last_gid, name)
                        } else if (type == SEARCH_TYPE_HORDE_LIST) {
                            searchHordeList(last_gid, name)
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        initAdapter()
    }

    fun initAdapter() {
        mAdapter = RecordListAdap(mDatas, this, true)
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyChangeOfUnfilteredItems(false)
                .setAnimationDelay(70)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .setOnlyEntryAnimation(true)
        recyclerview_team_record.setAdapter(mAdapter)
        recyclerview_team_record.setHasFixedSize(true)
        recyclerview_team_record.setItemViewCacheSize(0)
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(false)
                .setSwipeEnabled(false)
    }

    fun searchKey() {
        name = edit_search_record.text.toString().trim()
        if (StringUtil.isSpace(name)) {
            Toast.makeText(applicationContext, R.string.not_allow_empty, Toast.LENGTH_SHORT).show()
            return
        } else {
            last_gid = ""
            showKeyboard(false)
            val type = intent.getIntExtra("type", SEARCH_TYPE_MY_LIST)
            if (type == SEARCH_TYPE_MY_LIST) {
                searchMyList("", name)
            } else if (type == SEARCH_TYPE_TEAM_LIST) {
                searchTeamList("", name)
            } else if (type == SEARCH_TYPE_HORDE_LIST) {
                searchHordeList("", name)
            }
        }
    }

    var pageRecordList = ArrayList<GameBillEntity>()//每一页追加的网络战绩
    var callback: RequestCallback = object : RequestCallback {
        override fun onResult(code: Int, result: String, var3: Throwable?) {
            LogUtil.i(TAG, result)
            if (code == 0) {
                pageRecordList.clear()
                try {
                    val json = JSONObject(result)
                    val dataJson = json.getJSONArray("data")
                    val fetchParams = StringBuilder()
                    for (i in 0..dataJson.length() - 1) {
                        val gid = dataJson.optString(i)
                        val gameBillEntity = GameRecordDBHelper.getGameRecordByGid(applicationContext, gid)
                        if (gameBillEntity == null) {
                            fetchParams.append(gid + ",")//请求参数/game/gameView?gids=6782,6781,6773
                        } else {
                            pageRecordList.add(gameBillEntity)
                        }
                    }
                    if (fetchParams.length > 1 && mRecordAction != null) {
                        mRecordAction?.getRecordListByGid(fetchParams.substring(0, fetchParams.length - 1)/*出去最后一个,*/, object : RequestCallback {
                            override fun onResult(code: Int, result: String, var3: Throwable?) {
                                isLoadMore = false
                                if (code == 0) {
                                    try {
                                        val jsonNewApi = JSONObject(result)
                                        val list = RecordJsonTools.getRecordGameBillListByGids(jsonNewApi)
                                        if (StringUtil.isSpace(last_gid)) {
                                            if (list != null) {
                                                pageRecordList.addAll(list)
                                                Collections.sort<GameBillEntity>(pageRecordList, RecordListAC.recordComp)
                                                recordList.clear()
                                                recordList.addAll(pageRecordList)
                                            }
                                            setRecordList()
                                        } else {
                                            pageRecordList.addAll(list)
                                            Collections.sort<GameBillEntity>(pageRecordList, RecordListAC.recordComp)
                                            setMoreRecordList(pageRecordList)
                                        }
                                        if (list != null && list.size != 0) {
                                            saveGameRecordToDB(list)
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                } else {
                                }
                            }
                            override fun onFailed() {
                                if (StringUtil.isSpace(last_gid)) {
                                    getRecordFailed()
                                } else {
                                    getRecordMoreFailed()
                                }
                            }
                        })
                    } else {
                        isLoadMore = false
                        Collections.sort<GameBillEntity>(pageRecordList, RecordListAC.recordComp)
                        if (StringUtil.isSpace(last_gid)) {
                            recordList.clear()
                            recordList.addAll(pageRecordList)
                            setRecordList()
                        } else {
                            setMoreRecordList(pageRecordList)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                if (StringUtil.isSpace(last_gid)) {
                    getRecordFailed()
                } else {
                    getRecordMoreFailed()
                }
            }
        }
        override fun onFailed() {
            isLoadMore = false
            if (StringUtil.isSpace(last_gid)) {
                getRecordFailed()
            } else {
                getRecordMoreFailed()
            }
        }
    }
    fun searchMyList(last_gid: String, name: String) {
        mRecordAction?.getRecordListGids(last_gid, "", "", name, callback)
    }

    fun searchTeamList(last_gid: String, name: String) {
        mRecordAction?.getRecordListGidsTeam(last_gid, intent.getStringExtra("tid"), "", name, callback)
    }

    fun searchHordeList(last_gid: String, name: String) {
        mRecordAction?.getRecordListGidsHorde(last_gid, intent.getStringExtra("tid"), intent.getStringExtra("horde_id"), name, callback)
    }

    fun setRecordList() {
        if (recordList == null || recordList.size == 0) {
            recyclerview_team_record.setVisibility(View.GONE)
            mResultDataView.nullDataShow(R.string.no_data, R.mipmap.img_record_null)
        } else {
            recyclerview_team_record.setVisibility(View.VISIBLE)
            setDatas()
            mResultDataView.successShow()
        }
    }

    fun setMoreRecordList(moreList: ArrayList<GameBillEntity>) {
        val newList = filterData(moreList)
        if (newList.isEmpty()) {
            return
        }
        recordList.addAll(newList)
        mAdapter.notifyItemChanged(mDatas.size - 1)
        var positionInsert = mDatas.size
        for (i in newList.indices) {
            val lastEntity = if (recordList.size - newList.size + i - 1 >= 0) recordList[recordList.size - newList.size + i - 1] else null
            val entity = newList.get(i)
            val item = RecordListItem(entity, lastEntity)
            item.itemType = if (intent.getIntExtra("type", SEARCH_TYPE_MY_LIST) == SEARCH_TYPE_MY_LIST) RecordListItem.ITEM_TYPE_NORMAL else RecordListItem.ITEM_TYPE_TEAM_HORDE
            mDatas.add(positionInsert, item)
            if (mAdapter != null) {
                mAdapter.addItem(positionInsert, item)
            }
            positionInsert++
        }
        if (mAdapter != null) {
            mAdapter.mMaxNum = mDatas.size - 1
            mAdapter.notifyItemInserted(mDatas.size)
        }
    }

    fun getRecordFailed() {
        recyclerview_team_record.setVisibility(View.GONE)
        mResultDataView.showError(applicationContext, R.string.no_data)
    }

    fun getRecordMoreFailed() {
    }

    private fun filterData(moreList: ArrayList<GameBillEntity>): ArrayList<GameBillEntity> {
        val newList = ArrayList<GameBillEntity>()
        val alreadyGid = HashSet<String>()
        for (i in recordList.indices) {
            alreadyGid.add(recordList[i].gameInfo.gid)
        }
        for (i in moreList.indices) {
            if (alreadyGid.contains(moreList[i].gameInfo.gid)) {
                continue
            } else {
                newList.add(moreList[i])
            }
        }
        return newList
    }

    private fun setDatas() {
        mDatas.clear()
        for (i in recordList.indices) {
            val entity = recordList[i]
            val item = RecordListItem(entity, if (i > 0) recordList[i - 1] else null)
            item.itemType = if (intent.getIntExtra("type", SEARCH_TYPE_MY_LIST) == SEARCH_TYPE_MY_LIST) RecordListItem.ITEM_TYPE_NORMAL else RecordListItem.ITEM_TYPE_TEAM_HORDE
            mDatas.add(item)
        }
        if (mAdapter != null) {
            mAdapter.mMaxNum = mDatas.size - 1
            mAdapter.updateDataSet(mDatas as List<AbstractFlexibleItem<*>>?)
        }
    }

    override fun onBackPressed() {
        if (!finishing) {
            finishing = true
            performExitAnimation()
        }
    }
    
    fun performEnterAnimation() {
        val originY = intent.getIntExtra("y", 0).toFloat()
        val location = IntArray(2)
        edit_search_record.getLocationOnScreen(location)
        val translateY = originY - location[1].toFloat()
        //放到前一个页面的位置
        val top = edit_search_record.getY()//resources.displayMetrics.density * 20
        edit_search_record.setY(edit_search_record.getY() + translateY)
        val translateVa = ValueAnimator.ofFloat(edit_search_record.getY(), top)
        translateVa.addUpdateListener { valueAnimator ->
            edit_search_record.setY(valueAnimator.animatedValue as Float)
        }

        val alphaVa = ValueAnimator.ofFloat(0F, 1f)
        alphaVa.addUpdateListener { valueAnimator ->
            root_record_search.alpha = valueAnimator.animatedValue as Float
        }
        alphaVa.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }
            override fun onAnimationEnd(animation: Animator?) {
                BaseAppCompatActivity.sHandler.postDelayed({
                    edit_search_record.setFocusable(true)
                    edit_search_record.setFocusableInTouchMode(true)
                    edit_search_record.requestFocus()
                    showKeyboard(true)
                }, 20L)
            }
            override fun onAnimationCancel(animation: Animator?) {
            }
            override fun onAnimationStart(animation: Animator?) {
            }
        })

        val scaleVa = ValueAnimator.ofFloat(1F, 1.4f)
        scaleVa.addUpdateListener { valueAnimator -> edit_search_record.setScaleY(valueAnimator.animatedValue as Float) }

        alphaVa.duration = 500
        translateVa.duration = 500
        scaleVa.duration = 500

        alphaVa.start()
        translateVa.start()
//        scaleVa.start()
    }

    fun performExitAnimation() {
        val originY = intent.getIntExtra("y", 0).toFloat()

        val location = IntArray(2)
        root_record_search.getLocationOnScreen(location)

        val translateY = originY - location[1].toFloat()

        val translateVa = ValueAnimator.ofFloat(root_record_search.getY(), root_record_search.getY() + translateY)
        translateVa.addUpdateListener { valueAnimator ->
            root_record_search.setY(valueAnimator.animatedValue as Float)
        }
        translateVa.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
            }
            override fun onAnimationEnd(animator: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
            override fun onAnimationCancel(animator: Animator) {
            }
            override fun onAnimationRepeat(animator: Animator) {
            }
        })

        val scaleVa = ValueAnimator.ofFloat(1.4f, 1f)
        scaleVa.addUpdateListener { valueAnimator -> edit_search_record.setScaleY(valueAnimator.animatedValue as Float) }

        val alphaVa = ValueAnimator.ofFloat(1F, 0f)
        alphaVa.addUpdateListener { valueAnimator ->
            root_record_search.setAlpha(valueAnimator.animatedValue as Float)
        }

        alphaVa.duration = 500
        translateVa.duration = 500
        scaleVa.duration = 500

        alphaVa.start()
        translateVa.start()
//        scaleVa.start()
    }

    override fun toggleOverridePendingTransition(): Boolean {
        return false
    }

    override fun getOverridePendingTransitionMode(): BaseAppCompatActivity.TransitionMode {
        return BaseAppCompatActivity.TransitionMode.FADE
    }

    override fun onDelete(position: Int) {

    }

    override fun onClick(position: Int) {
        if (mAdapter.getItem(position) !is RecordListItem) {
            return
        }
        val billEntity = (mAdapter.getItem(position) as RecordListItem).mBill
        RecordDetailsActivity.start(this@RecordSearchAC, billEntity!!.gameInfo.gid)
//        if (from == FROM_NORMAL) {
//            if (false/*mGameInfoListAdapter.isSelectShowMode()*/) {
//                //                if (billEntity != null && billEntity.gameInfo != null && billEntity.gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
//                //                    mGameInfoListAdapter.clickCheckBox(position);
//                //                }
//            } else {
//                RecordDetailsActivity.start(this@RecordListAC, billEntity!!.gameInfo.gid)
//            }
//        } else if (from == FROM_SHARE) {
//            PublishActivity.start(this@RecordListAC, CircleConstant.TYPE_PAIJU, billEntity, RecordDetailsActivity.FROM_CIRCLE)
//        } else if (from == FROM_SEND_BY_SESSION) {
//            val intent = Intent()
//            intent.putExtra(Extras.EXTRA_BILL, billEntity)
//            setResult(Activity.RESULT_OK, intent)
//            finish()
//        }
    }

    override fun onLongClick(position: Int) {}

    fun saveGameRecordToDB(list: ArrayList<GameBillEntity>) {
        ThreadUtil.Execute {
            GameRecordDBHelper.saveGameRecordList(applicationContext, list)//保存到数据库
        }
    }
}