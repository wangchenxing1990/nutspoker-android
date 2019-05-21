package com.htgames.nutspoker.ui.activity.Club.fund

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.htgames.nutspoker.R
import com.htgames.nutspoker.ui.activity.Club.fund.observer.FundUpdateOberver
import com.htgames.nutspoker.ui.activity.Club.fund.observer.UpdateItem
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.netease.nim.uikit.cache.TeamDataCache
import com.netease.nim.uikit.chesscircle.ClubConstant
import com.netease.nim.uikit.common.ui.dialog.DialogMaker
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.team.model.Team
import kotlinx.android.synthetic.main.activity_club_fund.*

/**
 * Created by 周智慧 on 2017/8/28.
 */
class ClubFundAC : BaseTeamActivity() {
    var mTotalFund = "0"
    var mTeam: Team? = null
    companion object {
        fun start(activity: Activity, team: Team, totalFund: String) {
            val intent = Intent(activity, ClubFundAC::class.java)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            intent.putExtra(ClubConstant.KEY_TOTAL_FUND, totalFund)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_club_fund)
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        mTotalFund = intent.getStringExtra(ClubConstant.KEY_TOTAL_FUND)
        setHeadTitle("俱乐部基金")
        tv_fund_num.text = mTotalFund
        rl_recharge_fund.setOnClickListener { FundRechargeAC.start(this, team = mTeam) }
        rl_grant_fund.setOnClickListener { FundGrantAC.start(this, team = mTeam, totalFund = mTotalFund) }
        rl_fund_particulars.setOnClickListener { FundParticularsAC.start(this, team = mTeam) }
        registerObservers(true)
    }

    fun registerObservers(register: Boolean) {
        if (register) {
            FundUpdateOberver.getInstance().registerCallback(fundUpdateCallback)
        } else {
            FundUpdateOberver.getInstance().unregisterCallback(fundUpdateCallback)
        }
    }

    var fundUpdateCallback: FundUpdateOberver.FundUpdateCallback = object : FundUpdateOberver.FundUpdateCallback {
        override fun onUpdate(item: UpdateItem?) {
            if (item == null) {
                return
            }
            mTotalFund = "${mTotalFund.toInt() + item.updateNum}"
            tv_fund_num.text = mTotalFund
            searchTeam(mTeam?.id, object : RequestCallback<Team> {
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

    override fun onDestroy() {
        registerObservers(false)
        super.onDestroy()
    }
}