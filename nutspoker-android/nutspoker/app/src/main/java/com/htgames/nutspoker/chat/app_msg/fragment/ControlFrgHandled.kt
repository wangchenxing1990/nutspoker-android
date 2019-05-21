package com.htgames.nutspoker.chat.app_msg.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.htgames.nutspoker.R
import com.htgames.nutspoker.chat.app_msg.activity.AppMsgControlAC
import com.htgames.nutspoker.chat.app_msg.adapter.ControlCenterAdap
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify
import com.htgames.nutspoker.chat.app_msg.model.AppMessage
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus
import com.htgames.nutspoker.db.AppMsgDBHelper
import com.htgames.nutspoker.game.helper.BuyChipsResultHelper
import com.htgames.nutspoker.ui.base.BaseFragment
import com.netease.nim.uikit.chesscircle.DealerConstant
import com.netease.nim.uikit.common.ui.dialog.CustomAlertDialog
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.interfaces.IClickPayload
import com.netease.nim.uikit.interfaces.IOperationClick
import com.netease.nim.uikit.session.constant.Extras
import kotlinx.android.synthetic.main.fragment_notice.*

class ControlFrgHandled : BaseFragment(), IClickPayload, IOperationClick {
    override fun onAgree(position: Int, payload: Any?) {
    }
    override fun onReject(position: Int, payload: Any?) {
    }
    override fun onOtherOperation(position: Int, payload: Any?) {
    }
    override fun onDelete(position: Int, payload: Any?) {
    }
    override fun onClick(position: Int, payload: Any?) {
        if (payload !is AppMessage || activity !is AppMsgControlAC) {
            return
        }
        (activity as AppMsgControlAC).clickMessage(payload)
    }
    override fun onLongClick(position: Int, payload: Any?) {
        if (payload !is AppMessage) {
            return
        }
        showLongClickMenu(position, payload)
    }
    private fun showLongClickMenu(position: Int, message: AppMessage) {
        val alertDialog = CustomAlertDialog(activity)
        alertDialog.setTitle(R.string.delete_tip)
        var requestNickname = getString(R.string.delete_system_message)
        if (message.attachObject is BuyChipsNotify) {
            requestNickname = (message.attachObject as BuyChipsNotify).userNickname
        } else if (message.attachObject is MatchBuyChipsNotify) {
            requestNickname = (message.attachObject as MatchBuyChipsNotify).userNickname
        }
        var title = "删除“$requestNickname”的请求信息？"
        title = getString(R.string.delete_system_message)
        alertDialog.addItem(title) { deleteAppMessage(position, message) }
        alertDialog.show()
    }
    private fun deleteAppMessage(position: Int, message: AppMessage) {
        AppMsgDBHelper.deleteAppMessage(context, message)
        for (i in position + 1 until mAdapter.mDatas.size) {
            val nextMsg = mAdapter.mDatas[i]
            if (nextMsg.sortKey == null || nextMsg.sortKey != message.sortKey) {
                break
            }
            nextMsg.gidGroupIndex--
        }
        mDatas.remove(message)
        mAdapter.mDatas.remove(message)
        mAdapter.notifyItemRemoved(position)
        mAdapter.notifyItemRangeChanged(position, mAdapter.itemCount)
        judgeEmptyData()
    }

    companion object {
        fun newInstance(): ControlFrgHandled {
            val mInstance = ControlFrgHandled()
            val bundle = Bundle()
            bundle.putBoolean(Extras.EXTRA_SHOW_DIVIDER, false)
            mInstance.arguments = bundle
            return mInstance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    var mDatas: ArrayList<AppMessage> = ArrayList<AppMessage>()
    lateinit var mAdapter: ControlCenterAdap
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_notice, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        Looper.myQueue().addIdleHandler {//如果在teammessageac界面，然后顶部出现了自定义的"控制中心"通知栏，那么点击通知栏跳到这个页面，addIdleHandler接口不执行，不懂
//            mDatas = AppMsgDBHelper.queryAppMessageWithStatus(activity, AppMsgDBHelper.TYPE_CONTROL_CENTER, AppMessageStatus.init, false)
//            mAdapter = ControlCenterAdap(mDatas, this)
//            mRecyclerView?.adapter = mAdapter
//            judgeEmptyData()
//            false
//        }
        mDatas = AppMsgDBHelper.queryAppMessageWithStatus(activity, AppMsgDBHelper.TYPE_CONTROL_CENTER, AppMessageStatus.init, false)
        mAdapter = ControlCenterAdap(mDatas, this)
        mRecyclerView?.adapter = mAdapter
        judgeEmptyData()
    }

    fun onIncomingMessage(appMessage: AppMessage) {
        if (!DealerConstant.isNumeric(appMessage.sortKey)) {//只针对处理过的消息
            return
        }
        val gid = appMessage.sortKey
        val gidNum = appMessage.sortKey.toInt()
        val gidNumCurrentMax = if (mDatas.size <= 0) -100 else mDatas.get(0).sortKey.toInt()//第零个的gid是最大的
        var hasSameGid = false
        var currentGidIndex = 0//=0表示当前的牌局是第一行，最大的
        var hasSameMessage = false
        var preListNum = 0
        var appMessageList = java.util.ArrayList<AppMessage>()
        if (gidNum <= gidNumCurrentMax) {
            for (i in mDatas.indices) {
                val item = mDatas.get(i)
                if (appMessage.sortKey.equals(item.sortKey)) {
                    hasSameGid = true
                    appMessageList.add(item)
                    if (!StringUtil.isSpace(item.key) && item.key == appMessage.key && item.type == appMessage.type && appMessage.checkinPlayerId == item.checkinPlayerId) {
                        hasSameMessage = true
                        val viewHolder = if (mRecyclerView != null) mRecyclerView.findViewHolderForAdapterPosition(i) else null
                        if (viewHolder is ControlCenterAdap.AppControlVH) {
                            if (viewHolder.mCountDownTimers != null) {
                                viewHolder.mCountDownTimers!!.cancel()
                                viewHolder.mCountDownTimers = null
                            }
                        }
                        appMessage.unread = false
                        if (appMessage.status == AppMessageStatus.init) {//已处理的消息变成了未处理，那么移除掉
                            for (j in i + 1 until mDatas.size) {
                                val nextMsg = mDatas[j]
                                if (appMessage.sortKey == nextMsg.sortKey) {
                                    nextMsg.gidGroupIndex--
                                } else {
                                    break
                                }
                            }
                            mDatas.remove(item)
//                            for (j in i + 1 until mAdapter.mDatas.size) {mDatas和mAdapter.mDatas是不同的引用，但是列表里面的item是相同的引用，浅拷贝，gidGroupIndex不需要再减了
//                                val nextMsg = mAdapter.mDatas[j]
//                                if (appMessage.sortKey == nextMsg.sortKey) {
//                                    nextMsg.gidGroupIndex--
//                                } else {
//                                    break
//                                }
//                            }
                            mAdapter.mDatas.remove(item)
                            mAdapter.notifyItemRemoved(i)
                            mAdapter.notifyItemChanged(i)
                        } else {
                            appMessage.gidGroupIndex = item.gidGroupIndex
                            mDatas[i] = appMessage
                            mAdapter.mDatas[i] = appMessage
                            mAdapter.notifyItemChanged(i)
                        }
                        break
                    } else {
                        appMessageList.add(item)
                    }
                } else {
                    if (hasSameGid) {//有共同的gid，但是没有找到相同的SameMessage，那么这个gid组插入一个新的item
                        break
                    } else {
                        preListNum++
                    }
                }
            }
        }
        if (false/*!hasSameGid*/) {
            appMessage.gidGroupIndex = 0
            mDatas.add(0, appMessage)
            mAdapter.mDatas.add(0, appMessage)
            mAdapter.notifyItemRangeInserted(0, 1)
            mAdapter.notifyItemRangeChanged(1, mAdapter.itemCount)
            mRecyclerView.smoothScrollToPosition(0)
        } else if (!hasSameMessage) {
//            if (appMessage.status == AppMessageStatus.init) {
//                appMessage.gidGroupIndex = 0
//                appMessageList.forEach { it.gidGroupIndex++ }
//                appMessage.unread = false
//                mDatas.add(preListNum, appMessage)
//                mAdapter.mDatas.add(preListNum, appMessage)
//                mAdapter.notifyItemRangeInserted(preListNum, 1)
//                mAdapter.notifyItemRangeChanged(preListNum + 1, mAdapter.itemCount)
//                if (preListNum <= 0) {//当前的gid是最大的，而且顶部新加入了数据，那么滑到顶部
//                    mRecyclerView?.smoothScrollToPosition(0)
//                }
//            }
        }
        BuyChipsResultHelper.showBuyChipsResultDialog(activity, appMessage)
        judgeEmptyData()//onIncomingMessage
    }

    fun clearNotice() {
        mDatas.clear()
        mAdapter.mDatas.clear()
        mAdapter.notifyDataSetChanged()
        judgeEmptyData()
    }

    fun judgeEmptyData() {
        if (mDatas.size <= 0) {
            mResultDataView?.nullDataShow(R.string.app_smg_control_null_data_text, R.mipmap.img_message_null)
        } else {
            mResultDataView?.successShow()
        }
    }
}