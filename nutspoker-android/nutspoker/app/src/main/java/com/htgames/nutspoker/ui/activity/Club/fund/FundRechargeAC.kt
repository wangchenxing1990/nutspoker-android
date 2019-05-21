package com.htgames.nutspoker.ui.activity.Club.fund

import android.widget.Toast
import com.htgames.nutspoker.ChessApp
import com.htgames.nutspoker.interfaces.GameRequestCallback
import com.htgames.nutspoker.interfaces.RequestCallback
import com.htgames.nutspoker.net.RequestTimeLimit
import com.htgames.nutspoker.ui.action.AmountAction
import com.htgames.nutspoker.ui.action.ClubAction
import com.htgames.nutspoker.ui.activity.Club.fund.observer.FundUpdateOberver
import com.htgames.nutspoker.ui.activity.Club.fund.observer.UpdateItem
import com.htgames.nutspoker.ui.base.BaseTeamActivity
import com.netease.nim.uikit.bean.FundGoodEntity
import com.netease.nim.uikit.common.preference.UserPreferences
import com.netease.nim.uikit.session.constant.Extras
import com.netease.nimlib.sdk.team.model.Team
import kotlinx.android.synthetic.main.activity_fund_recharge.*
import org.json.JSONObject

/**
 * Created by 周智慧 on 2017/8/28.
 */
class FundRechargeAC : BaseTeamActivity() {
    var mFundGoodEntity: FundGoodEntity? = null
    var mTeam: Team? = null
    var mClubAction: ClubAction? = null
    var mAmountAction: AmountAction? = null
    var mRechargeTiems = 1
    val MIN_TIMES = 1//最小充值次数
    val MAX_TIMES = 10//最大充值次数
    var PER_TIMES_CONSUME_COIN = 32800//每次充值获得金币
    var PER_TIMES_CONSUME_DIAMOND = 3280//每次消耗钻石
    var PER_TIMES_PRESENT_COIN = 3200//每次赠送金币
    val addDisabledBg by lazyDrawable(com.htgames.nutspoker.R.mipmap.fund_add_disable)
    val addEnabledBg by lazyDrawable(com.htgames.nutspoker.R.drawable.bg_add_fund)
    val reduceDisabledBg by lazyDrawable(com.htgames.nutspoker.R.mipmap.fund_reduce_disable)
    val reduceEnabledBg by lazyDrawable(com.htgames.nutspoker.R.drawable.bg_reduce_fund)
    fun android.content.Context.lazyDrawable(@android.support.annotation.DrawableRes drawableResId: Int) : Lazy<android.graphics.drawable.Drawable> = lazy(LazyThreadSafetyMode.NONE) {
        resources.getDrawable(drawableResId)
    }
    fun android.content.Context.lazyString(@android.support.annotation.StringRes stringResId: Int) : Lazy<String> = lazy(LazyThreadSafetyMode.NONE) {
        resources.getString(stringResId)
    }
    fun android.content.Context.lazyColor(@android.support.annotation.ColorRes colorResId: Int): Lazy<Int> = lazy(LazyThreadSafetyMode.NONE) {
        android.support.v4.content.res.ResourcesCompat.getColor(resources, colorResId, theme)
    }
    companion object {
        fun start(activity: android.app.Activity, team: com.netease.nimlib.sdk.team.model.Team?) {
            val intent = android.content.Intent(activity, FundRechargeAC::class.java)
            intent.putExtra(Extras.EXTRA_TEAM_DATA, team)
            activity.startActivity(intent)
        }
    }
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.htgames.nutspoker.R.layout.activity_fund_recharge)
        setHeadTitle(com.htgames.nutspoker.R.string.rechange_fund)
        mTeam = intent.getSerializableExtra(Extras.EXTRA_TEAM_DATA) as Team
        //充值钻石
        tv_diamond_remain_num.text = "${UserPreferences.getInstance(this).diamond}"
        rl_recharge_diamond.setOnClickListener { com.htgames.nutspoker.ui.activity.System.ShopActivity.start(this, com.htgames.nutspoker.ui.activity.System.ShopActivity.TYPE_SHOP_DIAMOND) }
        //设置充值数量
        initRechargeTimes()
        //充值按钮
        btn_charge_fund.setOnClickListener { showChargeDialog() }
        mClubAction = ClubAction(this, null)
        mClubAction?.getFundGoods(mTeam?.id, object : GameRequestCallback {
            override fun onSuccess(response: JSONObject?) {
                if (response == null || response.getJSONArray("data") == null || response.getJSONArray("data").length() <= 0) {
                    return
                }
                var data: JSONObject = response.getJSONArray("data").getJSONObject(0)
                mFundGoodEntity = FundGoodEntity(data.optInt("goods_id"))
                mFundGoodEntity!!.name = data.optString("name")
                mFundGoodEntity!!.diamond = data.optInt("diamond")//购买商品消耗钻石
                mFundGoodEntity!!.chips = data.optInt("chips")//购买商品得到的金币
                mFundGoodEntity!!.desc = data.optString("desc")
                mFundGoodEntity!!.present = data.optInt("present")//购买商品赠送的金币
                mFundGoodEntity!!.price = data.optInt("price")
                PER_TIMES_CONSUME_COIN = mFundGoodEntity!!.chips//每次消耗金币
                PER_TIMES_CONSUME_DIAMOND = mFundGoodEntity!!.diamond//每次消耗钻石
                PER_TIMES_PRESENT_COIN = mFundGoodEntity!!.present
            }
            override fun onFailed(code: Int, response: JSONObject?) {
            }
        })
    }

    fun initRechargeTimes() {
        iv_add_fund.setOnClickListener { changeRechargeTimes(true) }
        iv_reduce_fund.setOnClickListener { changeRechargeTimes(false) }
        changeEnabled()
        changeConsumeNum()
    }

    fun changeRechargeTimes(isAdd: Boolean) {
        if (isAdd) ++mRechargeTiems else --mRechargeTiems
        if (mRechargeTiems <= MIN_TIMES) {
            mRechargeTiems = MIN_TIMES
        } else if (mRechargeTiems >= MAX_TIMES) {
            mRechargeTiems = MAX_TIMES
        }
        tv_recharge_fund_times.text = "$mRechargeTiems"
        changeEnabled()
        changeConsumeNum()
    }

    fun changeEnabled() {
        iv_add_fund.isEnabled = mRechargeTiems < MAX_TIMES
        iv_add_fund.setBackgroundDrawable(if (iv_add_fund.isEnabled) addEnabledBg else addDisabledBg)
        iv_reduce_fund.isEnabled = mRechargeTiems > MIN_TIMES
        iv_reduce_fund.setBackgroundDrawable(if (iv_reduce_fund.isEnabled) reduceEnabledBg else reduceDisabledBg)
    }

    fun changeConsumeNum() {
        tv_fund_charge_num.text = "${PER_TIMES_CONSUME_COIN * mRechargeTiems}(送${PER_TIMES_PRESENT_COIN * mRechargeTiems})"
        tv_fund_charge_consume_diamond_num.text = "${PER_TIMES_CONSUME_DIAMOND * mRechargeTiems}"
    }

    var buyDialog: com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog? = null
    fun showChargeDialog() {
        if (com.netease.nim.uikit.common.preference.UserPreferences.getInstance(this).diamond < PER_TIMES_CONSUME_DIAMOND * mRechargeTiems) {
            showTopUpDialog()
            return
        }
        if (buyDialog == null) {
            buyDialog = com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.createOkCancelDiolag(this, "", "确定花费${tv_fund_charge_consume_diamond_num.text}钻石购买俱乐部基金？", false, object : com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.OnDialogActionListener {
                override fun doCancelAction() {
                }
                override fun doOkAction() {
                    mClubAction?.purchaseFundGoods(mTeam?.id, "${mFundGoodEntity?.goods_id}", "$mRechargeTiems", object : GameRequestCallback {
                        override fun onSuccess(data: JSONObject?) {
                            Toast.makeText(ChessApp.sAppContext, "充值成功", Toast.LENGTH_SHORT).show()
                            FundUpdateOberver.getInstance().executeCallback(UpdateItem(UpdateItem.UPDATE_TYPE_CHARGE)
                                    .setFundUpdateNum(mRechargeTiems * PER_TIMES_CONSUME_COIN))
                            tv_diamond_remain_num?.text = "${UserPreferences.getInstance(this@FundRechargeAC).diamond - mRechargeTiems * PER_TIMES_CONSUME_DIAMOND}"
                            changeUserAmount()
                        }
                        override fun onFailed(code: Int, response: JSONObject?) {
                            Toast.makeText(ChessApp.sAppContext, "充值失败：$code", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            })
        }
        if (!isFinishing && !isDestroyedCompatible) {
            buyDialog?.show()
        }
    }

    internal var topUpDialog: com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog? = null
    fun showTopUpDialog() {
        if (topUpDialog == null) {
            topUpDialog = com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    lazyString(com.htgames.nutspoker.R.string.game_create_topup_dialog_tip_diamond).value, true,
                    object : com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.OnDialogActionListener {

                        override fun doCancelAction() {
                        }
                        override fun doOkAction() {
                            com.htgames.nutspoker.ui.activity.System.ShopActivity.start(this@FundRechargeAC, com.htgames.nutspoker.ui.activity.System.ShopActivity.TYPE_SHOP_DIAMOND)
                        }
                    })
        }
        if (!isFinishing && !isDestroyedCompatible) {
            topUpDialog?.show()
        }
    }

    //充值成功后刷新用户余额
    fun changeUserAmount() {
        if (mAmountAction == null) {
            mAmountAction = AmountAction(this, null)
            mAmountAction?.setRequestCallback(object : RequestCallback {
                override fun onResult(code: Int, result: String?, var3: Throwable?) {
                    RequestTimeLimit.lastGetAmontTime = 0
                    if (code == 0) {
                        tv_diamond_remain_num?.text = "${UserPreferences.getInstance(this@FundRechargeAC).diamond}"
                        this@FundRechargeAC.finish()
                    }
                }
                override fun onFailed() {
                    RequestTimeLimit.lastGetAmontTime = 0
                }
            })
        }
        RequestTimeLimit.lastGetAmontTime = 0
        mAmountAction?.getAmount(false)
    }

    override fun onDestroy() {
        if (mClubAction != null) {
            mClubAction?.onDestroy()
            mClubAction = null
        }
        if (mAmountAction != null) {
            mAmountAction?.onDestroy()
            mAmountAction = null
        }
        super.onDestroy()
    }
}