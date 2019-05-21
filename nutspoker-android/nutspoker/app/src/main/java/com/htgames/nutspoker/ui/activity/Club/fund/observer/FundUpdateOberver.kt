package com.htgames.nutspoker.ui.activity.Club.fund.observer

import java.util.*

/**
 * Created by 周智慧 on 2017/8/30.
 * "基金"变化观察者
 */
class FundUpdateOberver {
    interface FundUpdateCallback {
        fun onUpdate(item: UpdateItem?)
    }
    private val callbacks = ArrayList<FundUpdateCallback>()
    //静态内部类实现单例模式
    companion object {
        fun getInstance(): FundUpdateOberver {
            return Holder.instance
        }
    }
    private object Holder {
        val instance = FundUpdateOberver()
    }

    fun registerCallback(cb: FundUpdateCallback) {
        if (callbacks.contains(cb)) {
            return
        }
        callbacks.add(cb)
    }

    fun unregisterCallback(cb: FundUpdateCallback) {
        if (!callbacks.contains(cb)) {
            return
        }
        callbacks.remove(cb)
    }

    fun executeCallback(item: UpdateItem?) {
        if (item == null) {
            return
        }
        for (i in callbacks.indices) {
            callbacks[i].onUpdate(item = item)
        }
    }
}