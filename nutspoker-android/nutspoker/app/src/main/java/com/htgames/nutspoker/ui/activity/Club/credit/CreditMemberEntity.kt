package com.htgames.nutspoker.ui.activity.Club.credit

import java.io.Serializable

/**
 * Created by 周智慧 on 2017/9/22.
 */
data class CreditMemberEntity(var uid: String = "") : Serializable {
    var credit: Int = 0
}