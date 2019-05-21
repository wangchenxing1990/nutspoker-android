package com.netease.nim.uikit.common.preference

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by 周智慧 on 2017/6/5.  检查强制更新
 */
class CheckVersionPref private constructor(context: Context) {
    var sp: SharedPreferences
    init {
        sp = context.getSharedPreferences(/*DemoCache.getAccount() + "" + */mPreferenceTag, Context.MODE_PRIVATE)//这个不需要区分账户，因为所有账户的强更信息都是一样的
    }

    var versionStr: String
        get() = sp.getString(GET_VERSION_KEY, "")
        set(value) {
            val edit = sp.edit()
            edit.putString(GET_VERSION_KEY, value)
            edit.apply()
        }

    companion object {
        val CHECK_INTERVAL: Long = 10 * 60 //进入后台超过10分钟，再次出现前台时检测强制更新
        var mPreferences: CheckVersionPref? = null
        val mPreferenceTag: String = "CheckVersionPrefTag"
        private val GET_VERSION_KEY: String = "get_version_key"
        private val LAST_GO_BACKGROUND_TIME: String = "last_go_background_time"
        fun getInstance(context: Context): CheckVersionPref {
            if (mPreferences == null) {
                mPreferences = CheckVersionPref(context)
            }
            return mPreferences as CheckVersionPref
        }

        fun reset() {
            mPreferences = null
        }
    }

    var lastGoBackgroundTime: Long
        get() = sp.getLong(LAST_GO_BACKGROUND_TIME, Long.MAX_VALUE)
        set(value) {
            val edit = sp.edit()
            edit.putLong(LAST_GO_BACKGROUND_TIME, value)
            edit.apply()
        }

    private object Inner {
        var context: Context? = null
        val instance = CheckVersionPref(context!!)
    }
}