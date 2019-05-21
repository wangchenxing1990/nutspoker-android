package com.netease.nim.uikit.common.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.netease.nim.uikit.R
import com.netease.nim.uikit.api.SignStringRequest
import com.netease.nim.uikit.chesscircle.CacheConstant
import com.netease.nim.uikit.common.DemoCache
import com.netease.nim.uikit.common.util.BaseTools
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import java.util.HashMap

/**
 * Created by 周智慧 on 2017/8/15.
 */
class ProtocolDialog(context: Context, resourceId: Int, style: Int) : Dialog(context, style) {
    private lateinit var tv_head_title: TextView
    private lateinit var easy_dialog_negative_btn: Button
    private var positiveListener: View.OnClickListener? = null
    private lateinit var easy_dialog_positive_btn: Button
    private var negativeListener: View.OnClickListener? = null
    private var resourceId: Int = 0
    private var webUrl: String = ""
    private var mTitle: String = ""
    private lateinit var mWebView: WebView
    private lateinit var mProgressBar: ProgressBar
    internal var extraHeaders: MutableMap<String, String> = HashMap()
    init {
        if (resourceId != -1) {
            this.resourceId = resourceId
        } else {
            this.resourceId = R.layout.layout_protocol
        }
        val Params = window.attributes
        window.decorView.setPadding(0, 0, 0, 0)
        Params.width = ScreenUtil.screenWidth//WindowManager.LayoutParams.MATCH_PARENT
        Params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = Params
    }
    @JvmOverloads constructor(context: Context, style: Int = R.style.dialog_default_style, cancelableP: Boolean = true, url: String) : this(context, -1, style) {
        setCancelable(cancelableP)
        this.webUrl = url
        LogUtil.i("ProtocolDialog", "constructor")
    }

    fun addPositiveButton(title: CharSequence, listener: View.OnClickListener) {
        positiveListener = listener
    }

    fun addNegativeButton(title: CharSequence, listener: View.OnClickListener) {
        negativeListener = listener
    }

    fun setTitle(title: String) {
        LogUtil.i("ProtocolDialog", "setTitle")
        mTitle = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resourceId)
        initHead()
        extraHeaders.put(SignStringRequest.HEADER_KEY_APPVER, BaseTools.getAppVersionName(DemoCache.getContext()))
        initWebView()
        mWebView.loadUrl(webUrl, extraHeaders)
        LogUtil.i("ProtocolDialog", "onCreate")
        tv_head_title = findViewById(R.id.tv_head_title) as TextView
        easy_dialog_positive_btn = findViewById(R.id.easy_dialog_positive_btn) as Button
        easy_dialog_positive_btn.setOnClickListener(positiveListener)
        easy_dialog_negative_btn = findViewById(R.id.easy_dialog_negative_btn) as Button
        easy_dialog_negative_btn.visibility = View.VISIBLE
        easy_dialog_negative_btn.setOnClickListener(negativeListener)
    }

    fun initHead() {
        mProgressBar = findViewById(R.id.progress) as ProgressBar
        findViewById(R.id.btn_head_back).visibility = View.GONE
        (findViewById(R.id.tv_head_title) as TextView).text = mTitle
    }

    fun initWebView() {
        mWebView = findViewById(R.id.mWebView) as WebView
        val webSettings = mWebView.settings
        webSettings.setSupportZoom(false)
        webSettings.savePassword = false
        webSettings.saveFormData = false
        webSettings.javaScriptEnabled = true
//        webSettings.setBuiltInZoomControls(true);
        webSettings.builtInZoomControls = false
//        webSettings.setUseWideViewPort(true);//集WebView是否应该使支持“视窗”HTML meta标记或应该使用视窗。
        webSettings.loadWithOverviewMode = true  //是否使用WebView加载页面,也就是说,镜头拉出宽度适合在屏幕上的内容。
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
//        webSettings.setBlockNetworkImage(true);//图片后台加载
        webSettings.defaultTextEncodingName = "UTF-8"
        webSettings.setAppCachePath(CacheConstant.sAppContext.getCacheDir().getAbsolutePath())
        webSettings.pluginState = WebSettings.PluginState.ON
        //H5
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        mWebView.setWebViewClient(object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                mWebView.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                mProgressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                mProgressBar.visibility = View.GONE
            }
        })
        mWebView.setWebChromeClient(object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                mProgressBar.progress = newProgress
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                return true
            }
        })
    }
}