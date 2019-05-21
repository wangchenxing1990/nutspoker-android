package com.htgames.nutspoker.ui.activity.Club.credit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.CallSuper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.Toast
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.ClubAction
import com.htgames.nutspoker.ui.activity.Club.credit.observer.CreditUpdateItem
import com.htgames.nutspoker.ui.activity.Club.credit.observer.CreditUpdateObserver
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.htgames.nutspoker.ui.inputfilter.NameLengthFilter
import com.netease.nim.uikit.api.ApiCode
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.chesscircle.DealerConstant
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.team.model.Team
import kotlinx.android.synthetic.main.activity_grant_credit.*
import org.json.JSONObject

/**
 * Created by 周智慧 on 2017/9/20.
 */
class CreditGrantAC : BaseTeamActivity() {
    var mMember: CreditListEntity? = null
    var mTeam: Team? = null
    var mClubAction: ClubAction? = null
    companion object {
        fun start(activity: Activity, teamMember: CreditListEntity, team: Team?) {
            val intent = Intent(activity, CreditGrantAC::class.java)
            intent.putExtra(Extras.KEY_TEAM_MEMBER, teamMember)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        mMember = intent.getSerializableExtra(Extras.KEY_TEAM_MEMBER) as? CreditListEntity?
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grant_credit)
        mClubAction = ClubAction(this, null)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setHeadTitle(R.string.grant_credit)
        iv_member_info_head?.loadBuddyAvatar(mMember?.uid)
        tv_member_name?.text = NimUserInfoCache.getInstance().getUserDisplayName(mMember?.uid)
        tv_current_credit_num?.text = "当前分：${mMember?.score ?: 0}"
        btn_grant_credit?.setOnClickListener { grantCredit() }
        tv_clear_credit?.setOnClickListener { clearCredit() }
        changeGrantBtnEnabled()
        et_grant_credit_num.filters = arrayOf<InputFilter>(NameLengthFilter(9))
        et_grant_credit_num?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                changeGrantBtnEnabled()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
//        setHeadRightButton(R.string.particulars) {
//            CreditLogAC.Companion.start(this, mTeam, CreditLogAC.LOG_TYPE_SPECIAL, mMember)
//        }
        setHeadRightButtonGone()
    }

    fun changeGrantBtnEnabled() {
        var grantCreditDigit = if (DealerConstant.isNumeric(et_grant_credit_num?.text.toString().trim())) et_grant_credit_num?.text.toString().trim().toLong() else 0
        btn_grant_credit?.isEnabled = grantCreditDigit > 0
    }

    private fun grantCredit() {
        var str = et_grant_credit_num?.text?.toString()?.trim()
        if (!DealerConstant.isNumeric(str)) {
            Toast.makeText(this, "请输入合法内容", Toast.LENGTH_SHORT).show()
        }
        showGrantFundDialog(true)
    }

    var mGrantFundDialog: EasyAlertDialog? = null
    fun showGrantFundDialog(grant: Boolean) {
        val strGrant = if (grant) "确定增加${et_grant_credit_num.text.toString().trim().toLong()}信用分给“${NimUserInfoCache.getInstance().getUserDisplayName(mMember?.uid)}”？" else ""
        val strClear = "确定将“${NimUserInfoCache.getInstance().getUserDisplayName(mMember?.uid)}”信用分清零？"
//        if (mGrantFundDialog == null) {
//        }
        mGrantFundDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", if (grant) strGrant else strClear, false, object : EasyAlertDialogHelper.OnDialogActionListener {
            override fun doCancelAction() {
            }
            override fun doOkAction() {
                if ((mMember?.score ?: 0) <= 0 && !grant) {//已经是0再次清零的话  拦截下
                    Toast.makeText(ChessApp.sAppContext, "清除信用分成功", Toast.LENGTH_SHORT).show()
                    return
                }
                val creditUpdateNum = if (grant) et_grant_credit_num.text.toString().trim().toLong() else 0
                val updateType = if (grant) CreditUpdateItem.UPDATE_TYPE_ADD else CreditUpdateItem.UPDATE_TYPE_CLEAR
                val finalNum = if (grant) (creditUpdateNum + (mMember?.score ?: 0)) else 0
                mMember?.score = finalNum.toInt()
                mClubAction?.creditScoreSet(mTeam?.id, mMember?.uid, "$creditUpdateNum", object : GameRequestCallback {
                    override fun onSuccess(response: JSONObject?) {
                        if (creditUpdateNum <= 0) {
                            Toast.makeText(ChessApp.sAppContext, "清除信用分成功", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(ChessApp.sAppContext, "增加信用分成功", Toast.LENGTH_SHORT).show()
                        }
                        CreditUpdateObserver.getInstance().executeCallback(CreditUpdateItem(creditUpdateNum, updateType, mMember?.uid ?: ""))
                        tv_current_credit_num?.text = "当前分：${mMember?.score ?: 0}"
                        et_grant_credit_num?.setText("")
                    }

                    override fun onFailed(code: Int, response: JSONObject?) {
                        val failMsg = ApiCode.SwitchCode(code, response?.toString())
                        Toast.makeText(ChessApp.sAppContext, "$failMsg  code=$code", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        })
        mGrantFundDialog?.setMessage(if (grant) strGrant else strClear)
        if (!isFinishing && !isDestroyedCompatible) {
            mGrantFundDialog?.show()
        }
    }

    private fun clearCredit() {
        showGrantFundDialog(false)
    }

    protected var mHandler = Handler(Looper.getMainLooper(), HandlerCallback())
    val SHOW_KEYBOARD = 1
    val HIDE_KEYBOARD = 2
    inner class HandlerCallback : Handler.Callback {
        @CallSuper
        override fun handleMessage(message: Message): Boolean {
            when (message.what) {
                SHOW_KEYBOARD -> {
                    sl_grant_credit.fullScroll(ScrollView.FOCUS_DOWN)
                    return true
                }
                HIDE_KEYBOARD -> {
                    sl_grant_credit.fullScroll(ScrollView.FOCUS_UP)
                    return true
                }
            }
            return false
        }
    }
    override fun showKeyboard(isShow: Boolean) {
        super.showKeyboard(isShow)
        if (isShow) {
            mHandler.removeMessages(SHOW_KEYBOARD)
            mHandler.sendMessage(Message.obtain(mHandler, SHOW_KEYBOARD, null))
        } else {
            mHandler.removeMessages(HIDE_KEYBOARD)
            mHandler.sendMessage(Message.obtain(mHandler, HIDE_KEYBOARD, null))
        }
    }

    override fun onDestroy() {
        mClubAction?.onDestroy()
        mClubAction = null
        super.onDestroy()
    }
}