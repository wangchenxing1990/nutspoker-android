package com.netease.nim.uikit.bean

import org.json.JSONObject
import java.io.Serializable

/**
 * Created by 周智慧 on 2017/8/30.
 *
{
    code: 0,
    message: "ok",
    data: {
        fund_log: [
                    {
                        id: "3",
                        tid: "95689880",
                        admin_uid: "10062",
                        admin_nickname: "an28",
                        receiver_uid: "0",
                        receiver_nickname: null,
                        type: "0",
                        fund: "36000",
                        time: "1504064342"
                    },
                    {
                        id: "2",
                        tid: "95689880",
                        admin_uid: "10062",
                        admin_nickname: "an28",
                        receiver_uid: "0",
                        receiver_nickname: null,
                        type: "0",
                        fund: "36000",
                        time: "1504064292"
                    }
                ]
            }
}
 */
data class FundLogEntity(var id: String = "0") : Serializable {
    companion object {
        fun parseFundLogEntity(data: JSONObject?): FundLogEntity? {
            if (data == null) {
                return FundLogEntity("0")
            }
            var entity: FundLogEntity = FundLogEntity("${data.opt("id")}")
            entity.tid = "${data.opt("tid")}"
            entity.admin_uid = "${data.opt("admin_uid")}"
            entity.admin_nickname = "${data.opt("admin_nickname")}"
            entity.receiver_uid = "${data.opt("receiver_uid")}"
//            entity.receiver_nickname = "${data.opt("receiver_nickname")}"//这个貌似服务端没有传
            entity.type = data.optInt("type")//0充值    1发放
            entity.fund = data.optLong("fund")
            entity.time = data.optLong("time")
            return entity
        }
    }

    var tid: String = ""
    var admin_uid: String = ""
    var admin_nickname: String = ""
    var receiver_uid: String = ""
//    var receiver_nickname: String = ""//这个貌似服务端没有传
    var type: Int = 0//0充值    1发放
    var fund: Long = 0
    var time: Long = 0
}