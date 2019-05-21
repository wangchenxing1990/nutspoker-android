package com.htgames.nutspoker.ui.activity.Club.fund

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.htgames.nutspoker.R
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.ui.action.ClubAction
import com.htgames.nutspoker.ui.activity.Club.fund.observer.FundUpdateOberver
import com.htgames.nutspoker.ui.activity.Club.fund.observer.UpdateItem
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.netease.nim.uikit.api.ApiCode
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.chesscircle.DealerConstant
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.team.model.Team
import com.netease.nimlib.sdk.team.model.TeamMember
import kotlinx.android.synthetic.main.activity_fund_grant_member.*
import org.json.JSONObject

/**
 * Created by 周智慧 on 2017/8/29.
 */
class FundGrantMemberAC : BaseTeamActivity() {
    var mClubAction: ClubAction? = null
    var mTotalFund = "0"
    var mMember: TeamMember? = null
    var mTeam: Team? = null
    companion object {
        fun start(activity: Activity, teamMember: TeamMember, totalFund: String, team: Team?) {
            val intent = Intent(activity, FundGrantMemberAC::class.java)
            intent.putExtra(Extras.KEY_TEAM_MEMBER, teamMember)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            intent.putExtra(ClubConstant.KEY_TOTAL_FUND, totalFund)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        mMember = intent.getSerializableExtra(Extras.KEY_TEAM_MEMBER) as? TeamMember?
        mTotalFund = intent.getStringExtra(ClubConstant.KEY_TOTAL_FUND)
        setContentView(R.layout.activity_fund_grant_member)
        setHeadTitle("发放基金")
        tv_total_fund.text = mTotalFund
        iv_member_info_head.loadBuddyAvatar(mMember?.account)
        tv_member_name.text = NimUserInfoCache.getInstance().getUserDisplayName(mMember?.account)
        changeGrantBtnEnabled()
        btn_grant_fund.setOnClickListener { showGrantFundDialog() }
        et_grant_fund_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                changeGrantBtnEnabled()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mClubAction = ClubAction(this,  null)
    }

    fun changeGrantBtnEnabled() {
        var totalFunDigit = if (DealerConstant.isNumeric(mTotalFund)) mTotalFund.toInt() else 0
        var grantFunDigit = if (DealerConstant.isNumeric(et_grant_fund_num.text.toString().trim())) et_grant_fund_num.text.toString().trim().toLong() else 0
        btn_grant_fund.isEnabled = grantFunDigit in 100..totalFunDigit
    }

    var mGrantFundDialog: EasyAlertDialog? = null
    fun showGrantFundDialog() {
        if (mGrantFundDialog == null) {
            mGrantFundDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", "确定发放${et_grant_fund_num.text.toString().trim()}基金给“${NimUserInfoCache.getInstance().getUserDisplayName(mMember?.account)}”？", false, object : EasyAlertDialogHelper.OnDialogActionListener {
                override fun doCancelAction() {
                }
                override fun doOkAction() {
                    val grantNum = et_grant_fund_num.text.toString().trim()
                    mClubAction?.fundGrant(mTeam?.id, grantNum, mMember?.account, object : GameRequestCallback {
                        override fun onSuccess(data: JSONObject?) {
                            Toast.makeText(this@FundGrantMemberAC, "发放成功", Toast.LENGTH_SHORT).show()
                            FundUpdateOberver.getInstance().executeCallback(UpdateItem(UpdateItem.UPDATE_TYPE_GRANT)
                                    .setFundUpdateNum(-grantNum.toInt()))
                            mTotalFund = "${mTotalFund.toInt() - grantNum.toInt()}"
                            tv_total_fund?.text = mTotalFund
                            this@FundGrantMemberAC.finish()
                        }
                        override fun onFailed(code: Int, response: JSONObject?) {
                            Toast.makeText(this@FundGrantMemberAC, "发放失败：$code", Toast.LENGTH_SHORT).show()
                            if (code == ApiCode.CODE_FUND_GRANT_INSUFFICIENT) {
                                showInsufficientFundDialog()
                            }
                        }
                    })
                }
            })
        }
        if (!isFinishing && !isDestroyedCompatible) {
            mGrantFundDialog?.show()
        }
    }

    var mInsufficientFundDialog: EasyAlertDialog? = null
    fun showInsufficientFundDialog() {
        if (mInsufficientFundDialog == null) {
            mInsufficientFundDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "", "基金余额不足", resources.getString(R.string.ok), true) { }
        }
        if (!isFinishing && !isDestroyedCompatible) {
            mInsufficientFundDialog?.show()
        }
    }

    override fun onDestroy() {
        mClubAction?.onDestroy()
        mClubAction = null
        super.onDestroy()
    }
}