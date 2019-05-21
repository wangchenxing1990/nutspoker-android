package com.htgames.nutspoker.ui.activity.Club.credit.observer

/**
 * Created by 周智慧 on 17/9/21.
 */
class CreditUpdateObserver {
    val callbacks = ArrayList<CreditUpdateCallback>()
    interface CreditUpdateCallback {
        fun onUpdate(item: CreditUpdateItem)
    }

    companion object {
        fun getInstance(): CreditUpdateObserver {
            return Holder.instance
        }
    }

    private object Holder {
        val instance = CreditUpdateObserver()
    }

    fun registerCallback(callback: CreditUpdateCallback) {
        if (callbacks.contains(callback)) {
            return
        }
        callbacks.add(callback)
    }

    fun unregisterCallback(callback: CreditUpdateCallback) {
        if (!callbacks.contains(callback)) {
            return
        }
        callbacks.remove(callback)
    }

    fun executeCallback(item: CreditUpdateItem?) {
        if (item == null) {
            return
        }
        callbacks.forEach { it.onUpdate(item) }
    }
}