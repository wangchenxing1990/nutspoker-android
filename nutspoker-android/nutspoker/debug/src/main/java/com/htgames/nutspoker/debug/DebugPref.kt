package com.htgames.nutspoker.debug

import android.content.Context
import android.content.SharedPreferences
import com.netease.nim.uikit.chesscircle.CacheConstant

/**
 * Created by 周智慧 on 2017/6/27.
 */
class DebugPref private constructor(context: Context) {
    var sp: SharedPreferences
    init {
        sp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
    }
    var testUrl: String
        get() = sp.getString(KEY_URL, "http://192.168.31.123/assets/nutspoker-other/banner/index.html")
        set(value) {
            val edit = sp.edit()
            edit.putString(KEY_URL, value)
            edit.apply()
        }
    companion object {
        val TAG = DebugPref::class.java.simpleName
        private val KEY_URL = "key_url"
        fun getInstance(): DebugPref {
            return Holder.instance
        }
    }
    private object Holder {
        val instance = DebugPref(CacheConstant.sAppContext)
    }
}