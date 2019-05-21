package com.netease.nim.uikit.bean

import java.io.Serializable

/**
 * Created by 周智慧 on 2017/8/30.
 * {
    "code": 0,
    "message": "ok",
    "data": [
        {
        "goods_id": 4001,
        "name": "32800基金",
        "diamond": 3280,
        "chips": 32800,
        "desc": "32800基金送3200",
        "present": 3200,
        "price": 328
        }
        ]
    }
 */
data class FundGoodEntity(var goods_id: Int = 0) : Serializable {
    var name: String = ""
    var diamond = 0//购买商品消耗钻石
    var chips = 0//购买商品得到的金币
    var desc: String = ""
    var present = 0//购买商品赠送的金币
    var price = 0//不知道干啥的 好像只有服务端使用，好像是是人民币的钱
}