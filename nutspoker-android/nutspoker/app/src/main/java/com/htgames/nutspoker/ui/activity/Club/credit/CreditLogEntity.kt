package com.htgames.nutspoker.ui.activity.Club.credit

import org.json.JSONObject
import java.io.Serializable

/**
 * Created by 周智慧 on 17/9/20.
 * score_log: [
    {
        id: "63",
        tid: "22681811",
        admin_uid: "10043",
        admin_nickname: "an20",
        receiver_uid: "10056",
        receiver_nickname: null,
        type: "1",
        score: "11111",
        time: "1506322254"
    }
]
 */
//log_type  0总的信用记录    1单个人的信用分记录
data class CreditLogEntity (var id: String = "0", var log_type: Int = 0) : Serializable {
    companion object {
        val TYPE_ADD = 1
        val TYPE_CONSUME = 2
        fun parseCreditLogEntityAll(data: JSONObject?): CreditLogEntity? {
            if (data == null) {
                return CreditLogEntity("0")
            }
            var entity: CreditLogEntity = CreditLogEntity("${data.opt("id")}")
            entity.tid = "${data.opt("tid")}"
            entity.admin_uid = "${data.opt("admin_uid")}"
            entity.receiver_uid = "${data.opt("receiver_uid")}"
            entity.type = data.optInt("type")//0充值    1发放
            entity.score = data.optLong("score")
            entity.time = data.optLong("time")
            return entity
        }

        fun parseCreditLogEntitySpecial(data: JSONObject?): CreditLogEntity? {
            if (data == null) {
                return CreditLogEntity("0", 1)
            }
            var entity: CreditLogEntity = CreditLogEntity("${data.opt("id")}", 1)
            entity.tid = "${data.opt("tid")}"
            entity.admin_uid = "${data.opt("admin_uid")}"
            entity.receiver_uid = "${data.opt("receiver_uid")}"
            entity.type = data.optInt("type")//0充值    1发放
            entity.score = data.optLong("score")
            entity.time = data.optLong("time")
            entity.game_name = data.optString("game_name")
            return entity
        }
    }

    var tid: String = ""
    var admin_uid: String = ""
    var receiver_uid: String = ""
    var game_name: String = ""
    var type: Int = TYPE_ADD//1增加    2加入牌局消耗
    var score: Long = 0
    var time: Long = 0
}