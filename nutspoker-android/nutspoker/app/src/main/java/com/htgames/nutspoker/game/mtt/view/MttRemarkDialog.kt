package com.htgames.nutspoker.game.mtt.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.game.match.activity.EditMatchStateAC
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity
import com.htgames.nutspoker.game.mtt.adapter.RemarkPicAdap
import com.netease.nim.uikit.bean.GameEntity
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.util.BaseTools
import com.netease.nim.uikit.common.util.string.StringUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.interfaces.IClickPayload

class MttRemarkDialog(context: Context, var mGameInfo: GameEntity?, var mRemarkStr: String?, var mMatchPicList: String?) : Dialog(context, R.style.MyDialog), IClickPayload {
    var rootView: View
    var btn_mtt_remart_close: View
    var tv_edit_remark: TextView
    var tv_checkin_num: TextView
    var tv_remark_null: TextView
    var mtt_remark_pic_recyclerview: RecyclerView
    var mAdapter: RemarkPicAdap
    init {
        val padding = ScreenUtil.dp2px(context, 20f)//setPadding(padding, 0, padding, 0)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = BaseTools.getWindowHeigh(context) * 2 / 3
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val win = this.window
        win.setGravity(Gravity.CENTER)
        win.decorView.setPadding(0, 0, 0, 0)
        val lp = win.attributes
        lp.width = width   //宽度填满
        lp.height = height//WindowManager.LayoutParams.WRAP_CONTENT;  //高度自适应
        win.attributes = lp
        rootView = LayoutInflater.from(context).inflate(R.layout.dialog_mtt_remark, null)
        setContentView(rootView)
        (rootView.getLayoutParams() as FrameLayout.LayoutParams).setMargins(padding, 0, padding, 0)
        mtt_remark_pic_recyclerview = rootView.findViewById(R.id.mtt_remark_pic_recyclerview) as RecyclerView
        tv_checkin_num = rootView.findViewById(R.id.tv_checkin_num) as TextView
        tv_remark_null = rootView.findViewById(R.id.tv_remark_null) as TextView
        btn_mtt_remart_close = rootView.findViewById(R.id.btn_mtt_remart_close)
        btn_mtt_remart_close.setOnClickListener { this@MttRemarkDialog.dismiss() }
        tv_edit_remark = rootView.findViewById(R.id.tv_edit_remark) as TextView
        tv_edit_remark.setOnClickListener { EditMatchStateAC.startForResult(context as Activity, mRemarkStr ?: "", mMatchPicList ?: "", mGameInfo?.code ?: "") }
        mAdapter = RemarkPicAdap(null, context)
        mAdapter.mClick = this
        mAdapter.mRemarkStr = mRemarkStr
        mtt_remark_pic_recyclerview.adapter = mAdapter
    }

    fun updateDialogMatchState(matchStatusEntity: MatchStatusEntity?, str: String?, picList: String?) {
        mRemarkStr = str
        mMatchPicList = picList
        mAdapter.mRemarkStr = mRemarkStr
        //报名人数
        val checkinNum = matchStatusEntity?.checkInPlayer ?: 0
        tv_checkin_num.text = ChessApp.sAppContext.resources.getString(R.string.game_mtt_checkin_count, checkinNum)
        //
        tv_edit_remark.visibility = if (DemoCache.getAccount() == mGameInfo?.creatorInfo?.account) View.VISIBLE else View.GONE
        val stateStrEmpty = StringUtil.isSpace(mRemarkStr)
        tv_edit_remark.setText(if (stateStrEmpty) R.string.mtt_remark_add else R.string.mtt_remark_modify)
        //下面是公告内容
        if (!StringUtil.isSpaceOrZero(mMatchPicList)) {
            var urls = picList!!.split(",")
            mAdapter.updateData(urls)
            mAdapter.notifyDataSetChanged()
        }
        //内容为空，文字和图片同时为空才算为空
        tv_remark_null.visibility = if (stateStrEmpty && mAdapter.mDatas.size <= 0) View.VISIBLE else View.GONE
        tv_remark_null.setText(if (mGameInfo?.match_type == GameConstants.MATCH_TYPE_DIAMOND) R.string.match_diamond_reward_state else R.string.match_coin_reward_state)
    }

    override fun onDelete(position: Int, payload: Any?) {
    }
    override fun onClick(position: Int, payload: Any?) {
    }
    override fun onLongClick(position: Int, payload: Any?) {
    }
}