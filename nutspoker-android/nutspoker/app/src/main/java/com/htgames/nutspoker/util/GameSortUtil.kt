package com.htgames.nutspoker.util

import com.netease.nim.uikit.bean.*
import com.netease.nim.uikit.constants.GameConstants

/**
 * Created by 周智慧 on 2017/9/26.
 */
object GameSortUtil {
    var sort_type = 0//0从小到大  1从大到小
    //总共有三类排序方法  1大小盲(非大菠萝的普通局)  2报名费(sng和mtt)   3底注(大菠萝的普通局)
    //1大小盲(非大菠萝的普通局)---------------------------------
    var comparatorBlinds: Comparator<GameEntity> = Comparator { o1, o2 ->
        if (o1.gameMode == GameConstants.GAME_MODE_NORMAL && o1.gameConfig is GameNormalConfig
                && o2.gameMode == GameConstants.GAME_MODE_NORMAL && o2.gameConfig is GameNormalConfig) {
            if (sort_type == 0) {
                (o1.gameConfig as GameNormalConfig).blindType - (o2.gameConfig as GameNormalConfig).blindType
            } else {
                (o2.gameConfig as GameNormalConfig).blindType - (o1.gameConfig as GameNormalConfig).blindType
            }
        } else {
            0
        }
    }
    //2报名费(sng和mtt)---------------------------------
    var comparatorMatch: Comparator<GameEntity> = Comparator { o1, o2 ->
        if (o1.gameConfig is GameSngConfigEntity && o2.gameConfig is GameSngConfigEntity) {
            if (sort_type == 0) {
                (o1.gameConfig as GameSngConfigEntity).checkInFee - (o2.gameConfig as GameSngConfigEntity).checkInFee
            } else {
                (o2.gameConfig as GameSngConfigEntity).checkInFee - (o1.gameConfig as GameSngConfigEntity).checkInFee
            }
        } else if (o1.gameConfig is BaseMttConfig && o2.gameConfig is BaseMttConfig) {
            if (sort_type == 0) {
                (o1.gameConfig as BaseMttConfig).matchCheckinFee - (o2.gameConfig as BaseMttConfig).matchCheckinFee
            } else {
                (o2.gameConfig as BaseMttConfig).matchCheckinFee - (o1.gameConfig as BaseMttConfig).matchCheckinFee
            }
        } else {
            0
        }
    }
    //3底注(大菠萝的普通局)---------------------------------
    var comparatorPineappleNormal: Comparator<GameEntity> = Comparator { o1, o2 ->
        if (o1.gameConfig is PineappleConfig && o2.gameConfig is PineappleConfig) {
            if (sort_type == 0) {
                (o1.gameConfig as PineappleConfig).ante - (o2.gameConfig as PineappleConfig).ante
            } else {
                (o2.gameConfig as PineappleConfig).ante - (o1.gameConfig as PineappleConfig).ante
            }
        } else {
            0
        }
    }
}