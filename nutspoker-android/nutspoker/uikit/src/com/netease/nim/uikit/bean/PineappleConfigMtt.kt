package com.netease.nim.uikit.bean

import com.netease.nim.uikit.constants.GameConstants

/**
 * Created by 周智慧 on 2017/7/13.
 */
data class PineappleConfigMtt(var play_type: Int) : BaseMttConfig() {
    //    var start_sblinds: Int = 0//起始底注   mtt的是起始盲注
    var ante_table_type: Int = GameConstants.PINEAPPLE_MTT_ANTE_TABLE_NORMAL //大菠萝mtt底注表类型   0普通表   1快速表。和普通mtt的sblinds_mode同一个字段

    //下面的值不传给服务端，只是保存带本地
    var match_checkin_fee_index: Int = 0//报名费
    var match_chips_index: Int = 0//初始记分牌
    var match_duration_index: Int = 0//升底时间  mtt的是涨盲时间
    var start_sblinds_index: Int = 0//起始底注   mtt的是起始盲注
    var match_max_buy_cnt_index: Int = 0//mtt总买入上限
    var match_level_index: Int = 0//mtt终止报名级别
    var match_rebuy_cnt_index: Int = 0//mtt重构次数
    var ko_reward_rate_index = 0//  奖金分成
    var ko_head_rate_index: Int = 0 //人头分成(只有超级猎人赛才会有)
}