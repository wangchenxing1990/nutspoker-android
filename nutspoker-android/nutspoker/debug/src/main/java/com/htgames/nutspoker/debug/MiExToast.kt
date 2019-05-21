package com.htgames.nutspoker.debug

import android.content.Context
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.netease.nim.uikit.api.ApiConfig
import com.netease.nim.uikit.chesscircle.CacheConstant
import com.netease.nim.uikit.common.util.log.LogUtil
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Created by 周智慧 on 2017/6/2.
 */
class MiExToast(var mContext: Context) {
    companion object {
        val TAG: String = MiExToast::class.java.simpleName
        val LENGTH_ALWAYS: Int = 0
        val LENGTH_SHORT: Int = 2
        val LENGTH_LONG: Int = 4
    }
    private var isShown: Boolean = false
    private var toast: Toast
    private var mDuration: Int = LENGTH_SHORT
    private var animations: Int = -1
    private var mTN: Any? = null
    private lateinit var show: Method
    private lateinit var hide: Method
    private lateinit var params: WindowManager.LayoutParams
    private var mView: TextView

    private var handler: Handler = Handler()
    private var hideRunnable: Runnable = Runnable {
        hide()
    }

    private fun show() {
        if (isShown || !CacheConstant.debugBuildType) {
            return
        }
        mView.text = if (ApiConfig.isTestVersion) "Debug-测试服" else "Debug-正式服"
        mView.setBackgroundResource(R.color.list_item_bg_ripple)
        mView.setTextColor(CacheConstant.sAppContext.resources.getColor(R.color.login_solid_color))
        toast.view = mView
        toast.setGravity(Gravity.LEFT or Gravity.TOP, 0, 0)
        mDuration = LENGTH_ALWAYS
        initTN()
        show.invoke(mTN)
        isShown = true
        if (mDuration > LENGTH_ALWAYS) {
            handler.postDelayed(hideRunnable, mDuration * 1000L)
        }
    }

    private fun hide() {
        if (!isShown || !CacheConstant.debugBuildType) {
            return
        }
        hide.invoke(mTN)
        isShown = false
    }

    private fun initTN(): Unit {
        var tnFiled: Field = toast.javaClass.getDeclaredField("mTN")
        tnFiled.isAccessible = true
        mTN = tnFiled.get(toast)
        show = mTN!!::class.java.getMethod("show")
        hide = mTN!!::class.java.getMethod("hide")
        //设置动画
        var tnParamsField: Field = mTN!!.javaClass.getDeclaredField("mParams")
        tnParamsField.isAccessible = true
        params = tnParamsField.get(mTN) as WindowManager.LayoutParams
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        if (animations != -1) {
            params.windowAnimations = animations
        }
        //**调用tn.show之前一定要先设置mNextView*/
        var tnNextViewField: Field = mTN!!.javaClass.getDeclaredField("mNextView")
        tnNextViewField.isAccessible = true
        tnNextViewField.set(mTN, toast.view)
    }

    init {
        LogUtil.i("MiExToast", "MiExToast MiExToast 私有构造器")
        toast = Toast(mContext)
        mView = TextView(mContext)
    }

    private fun showNewDebugViewContent(content: String): Unit {
        mView.text = content
    }
}