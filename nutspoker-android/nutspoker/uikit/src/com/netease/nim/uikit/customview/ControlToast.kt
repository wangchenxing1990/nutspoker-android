package com.crl.zzh.customrefreshlayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.netease.nim.uikit.AnimUtil
import com.netease.nim.uikit.PendingIntentConstants
import com.netease.nim.uikit.R
import com.netease.nim.uikit.chesscircle.CacheConstant
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.interfaces.IControlComming
import com.netease.nim.uikit.nav.Nav
import com.netease.nim.uikit.nav.UrlConstants
import com.netease.nim.uikit.session.constant.Extras
import java.lang.reflect.Field
import java.lang.reflect.Method

class ControlToast {
    companion object {
        val TAG: String = ControlToast::class.java.simpleName
        val LENGTH_ALWAYS: Int = 0
        val LENGTH_SHORT: Int = 2
        val LENGTH_LONG: Int = 4
        fun getInstance(): ControlToast {
            return Holder.instance
        }
    }
    private var isShown: Boolean = false
    private var toast: Toast
    private var mDuration: Int = LENGTH_SHORT
    private var animations: Int = -1
    private var mTN: Any? = null
    private lateinit var show: Method
    private lateinit var hide: Method
    private lateinit var params: WindowManager.LayoutParams
    private var mView: View
    private var tv_control_content: TextView
    private var control_go_to_see: View

    var mIControlComming: IControlComming? = null
    var enabled = true
    var mainActivityDestroyed = false

    private var handler: Handler = Handler()
    private var hideRunnable: Runnable = Runnable {
        hide()
    }

    private constructor() {
    }

    private object Holder {
        var instance = ControlToast()
    }

    fun show(contentStr: String?) {
        if (!enabled) {
            return
        }
//        if (isShown) {
//            return
//        }
        initTN()
        handler.removeCallbacks(hideRunnable)
        tv_control_content.text = contentStr
        mDuration = 3
        show.invoke(mTN)
        isShown = true
        if (mDuration > LENGTH_ALWAYS) {
            handler.postDelayed(hideRunnable, mDuration * 1000L)
        }
        AnimUtil.shake(CacheConstant.sAppContext, control_go_to_see)
        mIControlComming?.showStatusbar(true)
    }

    fun hide() {
//        if (!isShown) {
//            return
//        }
        hide.invoke(mTN)
        isShown = false
        mIControlComming?.showStatusbar(true)
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
        params.width = ScreenUtil.screenMin
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
//                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY or
//                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                PixelFormat.TRANSLUCENT
        if (animations != -1) {
            params.windowAnimations = animations
        }
        //**调用tn.show之前一定要先设置mNextView*/
        var tnNextViewField: Field = mTN!!.javaClass.getDeclaredField("mNextView")
        tnNextViewField.isAccessible = true
        tnNextViewField.set(mTN, toast.view)
    }

    init {
        Log.i("ControlToast", "ControlToast ControlToast 私有构造器 ${CacheConstant.sAppContext}")
        toast = Toast(CacheConstant.sAppContext)
        mView = LayoutInflater.from(CacheConstant.sAppContext).inflate(R.layout.layout_control_top, null, false)
        mView.setOnClickListener {
            hide()
            if (!enabled) {
                return@setOnClickListener
            }
            if (mainActivityDestroyed) {
                val bundle = Bundle()
                bundle.putInt(Extras.EXTRA_PENDINGINTENT_ACIONT, PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL)
                Nav.from(CacheConstant.sAppContext).withExtras(bundle).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK).toUri(UrlConstants.URL_WELCOME)
            } else {
                mIControlComming?.click()
            }
        }
        tv_control_content = mView.findViewById(R.id.tv_control_content) as TextView
        control_go_to_see = mView.findViewById(R.id.control_go_to_see)
        toast.view = mView
        toast.setGravity(Gravity.LEFT or Gravity.TOP, 0, 0)
    }
}