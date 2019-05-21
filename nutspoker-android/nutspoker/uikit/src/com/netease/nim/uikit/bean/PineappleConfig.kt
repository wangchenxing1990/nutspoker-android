package com.netease.nim.uikit.bean

import com.netease.nim.uikit.constants.GameConstants
import java.io.Serializable
/**
 * Created by 周智慧 on 2017/6/22.
 */
data class PineappleConfig(var play_type: Int = GameConstants.PINEAPPLE_MODE_NORMAL) : Serializable {
    var ante: Int = 0
    var chips: Int = 0
    var duration: Int = 0//单位秒
    var duration_index: Int = 0//时间的index   //创建游戏传的是档位，服务端返回json解析是数值秒，（蛋疼）
    var buy_in_control: Int = 0
    var ip_limit: Int = 0
    var gps_limit: Int = 0
    var match_player: Int = 2
    var ready_time: Int = 0//0慢速  1快速
    var limit_chips: Int = 100

    var horde_id: String = ""
    var horde_name: String = ""
    var club_channel: Int = 0
    var deal_order: Int = 0//发牌顺序


    //下面的变量不需要传给服务端，只是把游戏设置保存到本地
    var ante_index: Int = 0
    var chips_index: Int = 0
    var limit_chips_index: Int = 0
    var match_player_index: Int = 0
}