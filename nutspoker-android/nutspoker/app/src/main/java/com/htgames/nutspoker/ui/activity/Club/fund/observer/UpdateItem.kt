package com.htgames.nutspoker.ui.activity.Club.fund.observer

import java.io.Serializable

/**
 * Created by 周智慧 on 2017/8/30.
 */
data class UpdateItem(var updateType: Int = UPDATE_TYPE_CHARGE) : Serializable {
    companion object {
        val UPDATE_TYPE_CHARGE = 0//基金充值
        val UPDATE_TYPE_GRANT = 1//基金发放
    }

    var updateNum = 0
    fun setFundUpdateNum(numP: Int): UpdateItem {
        updateNum = numP
        return this
    }
}