package com.htgames.nutspoker.debug

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Process
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.htgames.nutspoker.debug.network_monitor.ChuckActivity
import com.netease.nim.uikit.api.ApiConfig
import com.netease.nim.uikit.api.ApiConstants
import com.netease.nim.uikit.api.HttpApi
import com.netease.nim.uikit.api.RetrofitService
import com.netease.nim.uikit.base.BaseAppCompatActivity
import com.netease.nim.uikit.base.BaseSwipeBackActivity
import com.netease.nim.uikit.bean.CommonBeanT
import com.netease.nim.uikit.bean.UserAmountBean
import com.netease.nim.uikit.cache.NimUserInfoCache
import com.netease.nim.uikit.chesscircle.CacheConstant
import com.netease.nim.uikit.common.preference.SettingsPreferences
import com.netease.nim.uikit.common.preference.UserPreferences
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper
import com.netease.nim.uikit.common.util.log.LogUtil
import com.netease.nim.uikit.common.util.sys.ScreenUtil
import com.netease.nim.uikit.constants.GameConstants
import com.netease.nim.uikit.nav.Nav
import com.netease.nim.uikit.nav.UrlConstants
import com.netease.nim.uikit.session.constant.Extras
import kotlinx.android.synthetic.main.activity_develop.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by 周智慧 on 16/12/25. https://api.github.com/users/aaaking
 */

class DevelopAC : BaseSwipeBackActivity(), View.OnClickListener {
    private var destroyed = false //只在包含声明的文件中可见
    protected lateinit var develop_btn_test: Button //在 "top-level" 中不可以使用
    internal lateinit var develop_btn_test2: Button //在同一模块中的任何地方可见
    public lateinit var develop_btn_normal: Button //这意味着你的声明在任何地方都可见
    internal lateinit var develop_uid: TextView
    internal lateinit var develop_uuid: TextView//站鱼id
    internal lateinit var develop_usernick: TextView
    internal lateinit var develop_phone: TextView
    internal lateinit var develop_countryCode: TextView
    internal lateinit var develop_token: TextView
    internal lateinit var mSettingsPreferences: SettingsPreferences
    lateinit var et_dev_web_url: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_develop)
        (findViewById(R.id.tv_head_title) as TextView).text = "开发者彩蛋..."
        val margings = ScreenUtil.dp2px(this, 15f)
        with(findViewById(R.id.tv_head_right) as TextView) {
            (layoutParams as RelativeLayout.LayoutParams).setMargins(margings, margings / 3, margings / 2, margings / 3)
            visibility = View.VISIBLE
            text = "登出"
            setBackgroundResource(R.drawable.bg_login_btn)
            setOnClickListener {
                val message = "退出登录"
                val backDialog = EasyAlertDialogHelper.createOkCancelDiolag(this@DevelopAC, "", message, true,
                        object : EasyAlertDialogHelper.OnDialogActionListener {
                            override fun doCancelAction() {}
                            override fun doOkAction() {
                                val reason = 0//LoginActivity.REASON_NO;
                                onLogout(reason)//代码拷贝自SettingsActivity.java  onLogout() 方法
                                // 启动登录
                                val bundle = Bundle()
                                bundle.putInt(Extras.EXTRA_REASON, reason)
                                Nav.from(this@DevelopAC).withExtras(bundle).withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK).toUri(UrlConstants.URL_LOGIN)
                                finish()
                            }
                        })
                if (!isFinishing && !isDestroyedCompatible) {
                    backDialog.show()
                }
            }
        }
        initViews()
        testRetrofit()
        initTestBtn()
    }

    override fun toggleOverridePendingTransition(): Boolean {
        return true
    }

    override fun getOverridePendingTransitionMode(): BaseAppCompatActivity.TransitionMode {
        return BaseAppCompatActivity.TransitionMode.RIGHT
    }

    private fun initViews() {
        val phoneWidthHeight = "(" + ScreenUtil.screenWidth + "+" + ScreenUtil.screenHeight + ")"
        develop_user_info_des.text = phoneWidthHeight + resources.displayMetrics.densityDpi + "用户信息" + resources.displayMetrics.density
        develop_uid = findViewById(R.id.develop_uid) as TextView
        develop_uid.text = "用户id(uid)：" + UserPreferences.getInstance(this).userId
        develop_uuid = findViewById(R.id.develop_uuid) as TextView
        develop_uuid.text = "用户站鱼id(uuid)：" + UserPreferences.getInstance(this).zyId
        develop_usernick = findViewById(R.id.develop_usernick) as TextView
        develop_usernick.text = "昵称(nick): " + NimUserInfoCache.getInstance().getUserDisplayName(UserPreferences.getInstance(this).userId)
        develop_phone = findViewById(R.id.develop_phone) as TextView
        develop_phone.text = "用户手机号：" + UserPreferences.getInstance(this).userPhone
        develop_countryCode = findViewById(R.id.develop_countryCode) as TextView
        develop_countryCode.text = "用户区域：" + UserPreferences.getInstance(this).userCountryCode
        develop_token = findViewById(R.id.develop_token) as TextView
        develop_token.text = "用户token：" + UserPreferences.getInstance(this).userToken
        develop_btn_test = findViewById(R.id.develop_btn_test) as Button
        develop_btn_test.setOnClickListener(this)
        develop_btn_test.isEnabled = !ApiConfig.isTestVersion
        develop_btn_test2 = findViewById(R.id.develop_btn_test2) as Button
        develop_btn_test2.setOnClickListener(this)
        develop_btn_normal = findViewById(R.id.develop_btn_normal) as Button
        develop_btn_normal.setOnClickListener(this)
        develop_btn_normal.isEnabled = ApiConfig.isTestVersion
        et_dev_web_url = findViewById(R.id.et_dev_web_url) as EditText
        et_dev_web_url.setText(DebugPref.getInstance().testUrl)
    }

    fun testWebJump(view: View) {
        val testUrl = et_dev_web_url.text.toString()
        if (TextUtils.isEmpty(testUrl)) {
            Toast.makeText(this, "请输入网址url", Toast.LENGTH_SHORT).show()
        } else {
            //WebViewActivity.start(this, 666, testUrl);
            val bundle = Bundle()
            bundle.putInt(Extras.EXTRA_TYPE, 666)
            bundle.putString(Extras.EXTRA_URL, testUrl)
            Nav.from(this@DevelopAC).withExtras(bundle).toUri(UrlConstants.URL_WEBVIEW)
            DebugPref.getInstance().testUrl = testUrl
        }
    }

    override fun onClick(v: View) {
        val viewId = v.id
        if (viewId == R.id.develop_btn_test || viewId == R.id.develop_btn_test2 || viewId == R.id.develop_btn_normal) {
            switchEnv()
        }
    }

    private fun switchEnv() {
        val title = if (ApiConfig.isTestVersion) "warning：您将切换到正式服!" else "warning：您将切换到测试服!"
        val message = "1.个推环境只能是初始化环境，无法更改个推环境，后续有待优化. \n" + "2.由于问题1未解决，测试“个推”时请不要切换环境"//切换环境将清除数据库，请确保已经处理未读消息，否则可能会导致一些未读消息丢失。2.
        val backDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, title, message, true,
                object : EasyAlertDialogHelper.OnDialogActionListener {
                    override fun doCancelAction() {}

                    override fun doOkAction() {
                        val reaseon = 0//LoginActivity.REASON_NO
                        onLogout(reaseon)
                        Toast.makeText(this@DevelopAC, "进程将被重启", Toast.LENGTH_SHORT).show()
                        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
                        val sdkPath = CacheConstant.getNimPath()//一定要和ChessApp的初始化运行方法getOptions()的目录一致
                        deleteFilesByDirectory(File(sdkPath))

                        /*清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * */
                        deleteFilesByDirectory(cacheDir)
                        /*清除本应用内部缓存(/mnt/sdcard/Android/data/com.my.app/cache) * * */
                        deleteFilesByDirectory(externalCacheDir)

                        /*清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * */
                        //                            deleteFilesByDirectory(new File("/data/data/"+ getPackageName() + "/shared_prefs"));

                        mSettingsPreferences = SettingsPreferences.getInstance(applicationContext)
                        mSettingsPreferences.sp_setting.getBoolean(SettingsPreferences.KEY_APP_IS_TEST_VERSION, true)
                        val edit = mSettingsPreferences.sp_setting.edit()
                        edit.putBoolean(SettingsPreferences.KEY_APP_IS_TEST_VERSION, !ApiConfig.isTestVersion)
                        edit.commit()

                        val mainHandler = Handler(this@DevelopAC.mainLooper)
                        val runnable = Runnable {
                            if (this@DevelopAC != null) {
                                Nav.from(this@DevelopAC).withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK).toUri(UrlConstants.URL_WELCOME)
                            }
                            Process.killProcess(Process.myPid())
                        }
                        mainHandler.postDelayed(runnable, 1000)
                    }
                })
        if (!isFinishing && !isDestroyedCompatible) {
            backDialog.show()
        }
    }

    // 注销
    private fun onLogout(reason: Int) {//代码拷贝自SettingsActivity.java  onLogout(int reason) 方法
        try {
            val params = arrayOfNulls<Any>(1)
            params[0] = reason
            val settingClass = Class.forName("com.htgames.nutspoker.ui.activity.System.SettingsActivity")//完整类名
            val setting = settingClass.newInstance()//获得实例
            val getAuthor = settingClass.getDeclaredMethod("onLogout", Int::class.javaPrimitiveType)//获得私有方法
            getAuthor.isAccessible = true//调用方法前，设置访问标志
            getAuthor.invoke(setting, *params)//使用方法
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun testRetrofit() {
        findViewById(R.id.do_http).setOnClickListener { doHttpActivity() }
        findViewById(R.id.launch_chuck_directly).setOnClickListener { launchChuckDirectly() }
    }

    private fun doHttpActivity() {//(https://github.com/jgilfelt/chuck)
        val api = HttpApi.GetRetrofitIns(null).create<RetrofitService.GetAmountService>(RetrofitService.GetAmountService::class.java!!)
        val cb = object : Callback<Void> {
            override fun onResponse(call: Call<Void>?, response: Response<Void>?) {
                LogUtil.i("" + response.toString())
            }

            override fun onFailure(call: Call<Void>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        api.getAmount(ApiConstants.OS_ANDROID, UserPreferences.getInstance(mContext).userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CommonBeanT<UserAmountBean>>() {
                    override fun onCompleted() {
                        LogUtil.i("doHttpActivity onCompleted(): ")
                    }

                    override fun onError(e: Throwable) {
                        LogUtil.i("doHttpActivity onError(): " + e.toString())
                    }

                    override fun onNext(s: CommonBeanT<UserAmountBean>) {
                        val textView = this@DevelopAC.findViewById(R.id.launch_chuck_directly) as TextView
                        //{"code":0,"message":"ok","data":{"diamond":"2000","coins":"1840","time":1487662096,"uuid":"5653559"}}
                        val result = "{\"code\":" + s.code + ",\"message\":" + s.message + ",\"data\":" + s.data.toString() + "}"
                        textView.text = result
                        LogUtil.i("doHttpActivity onNext(): " + result)
                    }
                })
    }

    private fun launchChuckDirectly() {
        startActivity(Intent(this, ChuckActivity::class.java))
    }

    val isDestroyedCompatible: Boolean
        get() {
            if (Build.VERSION.SDK_INT >= 17) {
                return isDestroyedCompatible17
            } else {
                return destroyed || super.isFinishing()
            }
        }

    private val isDestroyedCompatible17: Boolean
        @TargetApi(17)
        get() = super.isDestroyed()

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i(TAG, "DevelopAC onDestroy")
        destroyed = true
    }

    fun onBack(view: View) {
        //        onBackPressed();
        finish()
    }

    fun initTestBtn() {
        var btn_dev_test: Button = findViewById(R.id.btn_dev_test) as Button
        btn_dev_test.setOnClickListener(View.OnClickListener {
        })
    }

    companion object {

        /**
         * * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * *

         * @param directory
         */
        private fun deleteFilesByDirectory(directory: File?) {
            if (directory != null && directory.exists() && directory.isDirectory && directory.listFiles() != null && directory.listFiles().isNotEmpty()) {
                for (item in directory.listFiles()) {
                    item.delete()
                }
            }
        }

        val TAG = DevelopAC::class.java!!.simpleName
    }
}
