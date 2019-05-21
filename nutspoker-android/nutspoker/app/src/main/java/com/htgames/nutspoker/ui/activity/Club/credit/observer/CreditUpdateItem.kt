package com.htgames.nutspoker.ui.activity.Club.credit.observer

import java.io.Serializable

/**
 * Created by 周智慧 on 17/9/21.
 */
data class CreditUpdateItem(var updateCreditNum: Long, var updateType: Int, var uid: String) : Serializable {
    companion object {
        val UPDATE_TYPE_ADD = 0//增加信用分
        val UPDATE_TYPE_CLEAR = 1//清除信用分
    }
}