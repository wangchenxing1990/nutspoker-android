package com.htgames.nutspoker.data

/**
 * Created by 周智慧 on 2017/7/31.
 */
data class GameName(var name: String) {
    var prefix: String = ""
    var gameCount: Int = 1

    init {
        var digitBuilder: StringBuilder = StringBuilder()
        var i = name.length - 1
        while (i >= 0) {
            if (Character.isDigit(name[i])) {
                digitBuilder.append(name[i])
            } else {
                break
            }
            i--
        }
        if (digitBuilder.isEmpty()) {//无数字
            prefix = name
            gameCount = 1
        } else if (i == -1) {//全是数字
            prefix = ""
            gameCount = digitBuilder.reverse().toString().toInt()
        } else {
            prefix = name.substring(0, i + 1)
            gameCount = digitBuilder.reverse().toString().toInt()
        }
    }

    override fun toString(): String {
        return "$name     $prefix   $gameCount"
    }
}