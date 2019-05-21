package com.htgames.nutspoker.ui.activity.Club.credit

/**
 * Created by 周智慧 on 2017/9/25.
 */
object CreditComparator {
    var comparator: Comparator<CreditListEntity> = Comparator {o1, o2 ->
        o2.score - o1.score
    }
}