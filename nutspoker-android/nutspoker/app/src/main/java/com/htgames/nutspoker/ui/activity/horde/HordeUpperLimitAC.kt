package com.htgames.nutspoker.ui.activity.horde

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.HordeAction
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem
import com.htgames.nutspoker.ui.adapter.team.HordeViewAdap
import com.htgames.nutspoker.ui.adapter.team.HordeViewItem
import com.htgames.nutspoker.ui.base.BaseActivity
import com.netease.nim.uikit.chesscircle.entity.HordeEntity
import com.netease.nim.uikit.chesscircle.entity.TeamEntity
import com.netease.nim.uikit.interfaces.IClick
import com.netease.nim.uikit.session.constant.Extras
import kotlinx.android.synthetic.main.activity_horde_upper_limit.*
import org.json.JSONObject

/**
 * Created by 周智慧 on 2017/6/19.
 */
class HordeUpperLimitAC : BaseActivity(), View.OnClickListener, IClick {
    override fun onDelete(position: Int) {
    }

    override fun onClick(position: Int) {
        if (mDatas.size <= position || mDatas[position] == null || mDatas[position].mTeamEntity == null) {
            return
        }
        SetHordeUpperLimitAC.Companion.start(this@HordeUpperLimitAC, mDatas[position].mTeamEntity, mHordeEntity)
    }

    override fun onLongClick(position: Int) {
    }

    companion object {
        fun start(activity: Activity, hordeEntity: HordeEntity, teams: ArrayList<TeamEntity>) {
            var intent: Intent = Intent(activity, HordeUpperLimitAC::class.java)
            intent.putExtra(Extras.EXTRA_CUSTOMIZATION, hordeEntity)
            intent.putExtra(Extras.EXTRA_DATA, teams)
            activity.startActivity(intent)
        }
    }

    private lateinit var mHordeEntity: HordeEntity
    private lateinit var mAdapter: HordeViewAdap
    private lateinit var mHordeAction: HordeAction
    private var mDatas: ArrayList<HordeViewItem> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHordeEntity = intent.getSerializableExtra(Extras.EXTRA_CUSTOMIZATION) as HordeEntity
        setContentView(R.layout.activity_horde_upper_limit)
        mHordeAction = HordeAction(this, null)
        switch_horde_upper_limit.setOnClickListener(this@HordeUpperLimitAC)
        switch_horde_upper_limit.isChecked = mHordeEntity.is_score == 1
        recycler_view.visibility = if (switch_horde_upper_limit.isChecked) View.VISIBLE else View.GONE
        setHeadTitle("选择俱乐部成员")
        initAdapter()
        registerObservers(true)
    }

    private fun registerObservers(register: Boolean) {
        if (register) {
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback)
        } else {
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback)
        }
    }

    fun initAdapter() {
        var teams: ArrayList<TeamEntity> = intent.getSerializableExtra(Extras.EXTRA_DATA) as ArrayList<TeamEntity>
        teams.forEach {
            var viewItem = HordeViewItem(it, isChief = true)
            viewItem.itemType = HordeViewItem.Companion.ITEM_TYPE_SCORE
            mDatas.add(viewItem)
        }
        mAdapter = HordeViewAdap(mDatas, this@HordeUpperLimitAC, true)
        mAdapter.setAnimateChangesWithDiffUtil(true)
                .setAnimateToLimit(10000)
                .setNotifyMoveOfFilteredItems(false)
                .setNotifyChangeOfUnfilteredItems(true)
                .setAnimationDelay(100)
                .setAnimationOnScrolling(true)
                .setAnimationOnReverseScrolling(true)
                .isOnlyEntryAnimation = true
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = mAdapter
        recycler_view.setItemViewCacheSize(0)
        mAdapter.setLongPressDragEnabled(false)
                .setHandleDragEnabled(true)
                .isSwipeEnabled = false
    }

    override fun onClick(v: View) {
        val viewId: Int = v.id
        when (viewId) {
            R.id.switch_horde_upper_limit -> {
                updateIsSocre(if (switch_horde_upper_limit.isChecked) 1 else 0)
            }
        }
    }

    fun updateIsSocre(is_score: Int) = mHordeAction.updateIsScore(mHordeEntity.horde_id, is_score, object : GameRequestCallback {
        override fun onSuccess(response: JSONObject?) {
            recycler_view.visibility = if (switch_horde_upper_limit.isChecked) View.VISIBLE else View.GONE
            HordeUpdateManager.getInstance().execludeCallback(UpdateItem(UpdateItem.UPDATE_TYPE_IS_SCORE)
                    .setHordeId(if (mHordeEntity == null) "" else mHordeEntity.horde_id)
                    .setIsScore(is_score))
        }

        override fun onFailed(code: Int, response: JSONObject?) {
            switch_horde_upper_limit.isChecked = !switch_horde_upper_limit.isChecked
            recycler_view.visibility = if (switch_horde_upper_limit.isChecked) View.VISIBLE else View.GONE
        }

    })

    override fun onDestroy() {
        super.onDestroy()
        if (mHordeAction != null) {
            mHordeAction.onDestroy()
        }
        registerObservers(false)
    }

    internal var hordeUpdateCallback: HordeUpdateManager.HordeUpdateCallback = HordeUpdateManager.HordeUpdateCallback { item ->
        if (item == null) {
            return@HordeUpdateCallback
        }
        if (item.updateType == UpdateItem.UPDATE_TYPE_SET_SCORE) {
            val tid: String = item.tid
            for (i in mDatas.indices) {
                if (mDatas[i].mTeamEntity.id == tid) {
                    mDatas[i].mTeamEntity.score = item.score
                    mAdapter.notifyItemChanged(i)
                    break
                }
            }
        }
    }
}