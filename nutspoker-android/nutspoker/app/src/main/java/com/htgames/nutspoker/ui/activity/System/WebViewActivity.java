package com.htgames.nutspoker.ui.activity.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.AppNotify;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.netease.nim.uikit.api.SignStringRequest;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 网页
 */
public class WebViewActivity extends BaseActivity {
    private final String TAG = "WebViewActivity";

    public static final String HREF_CHECK_IN = "zydzq_go_checkin:";// "startgame:"

    public final static int TYPE_APP_NOTICE = 1;//APP公告
    public final static int TYPE_HELP = 2;//帮助
    public final static int TYPE_TERMS = 3;//服务条款
    public final static int TYPE_SNG_RULE = 4;//SNG比赛模式规则
    public final static int TYPE_MTT_RULE = 5;//MTT比赛模式规则
    public final static int TYPE_ABOUT_US = 6;//MTT比赛模式规则
    public final static int TYPE_DISCOVERY_BANNER = 7;//"发现"tab页面顶部的广告链接
    public final static int TYPE_POKERCLANS_PROTOCOL = 8;//注册的时候点击"扑克部落服务协议和隐私政策"的h5落地页
    private WebView mWebView;
    String url;
    ProgressBar mProgressBar;
    int type = 0;
    private boolean isService = true;//隐私政策和服务条款
    String languae = BaseTools.getLanguage().toLowerCase();
    Map<String, String> extraHeaders = new HashMap<>();
    AppNotify mAppNotify;

    public static void start(Activity activity, int type, AppNotify notify) {

        if(TextUtils.isEmpty(notify.url))
            return;

        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, type);
        intent.putExtra(Extras.EXTRA_URL, notify.url);
        intent.putExtra(Extras.EXTRA_DATA, notify);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, int type, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(Extras.EXTRA_TYPE, type);
        intent.putExtra(Extras.EXTRA_URL, url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        url = getIntent().getStringExtra(Extras.EXTRA_URL);
        type = getIntent().getIntExtra(Extras.EXTRA_TYPE, 0);
        if(type == TYPE_APP_NOTICE)
            mAppNotify = (AppNotify)getIntent().getSerializableExtra(Extras.EXTRA_DATA);

        extraHeaders.put(SignStringRequest.HEADER_KEY_APPVER, BaseTools.getAppVersionName(DemoCache.getContext()));
        if (type == TYPE_HELP) {
            url = getHost() + ApiConstants.URL_HELP;
            if (languae.contains("tw")) {
                url = getHost() + ApiConstants.URL_HELP_TW;
            }
        } else if (type == TYPE_TERMS) {
            url = getHost() + ApiConstants.URL_PRIVATE;
            if (languae.contains("tw")) {
                url = getHost() + ApiConstants.URL_PRIVATE_TW;
            }
        } else if(type == TYPE_SNG_RULE) {
            url = getHost() + ApiConstants.URL_SNG_RULE;
            if (languae.contains("tw")) {
                url = getHost() + ApiConstants.URL_SNG_RULE_TW;
            }
        } else if(type == TYPE_MTT_RULE) {
            url = getHost() + ApiConstants.URL_MTT_RULE;
            if (languae.contains("tw")) {
                url = getHost() + ApiConstants.URL_MTT_RULE_TW;
            }
        }
        initHeadView();
        initView();
        initWebView();
        loadUrl();
    }

    public void loadUrl() {
        if (!TextUtils.isEmpty(url)) {
            //Log.d(TAG, url);
            mWebView.loadUrl(url, extraHeaders);
        }
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mWebView = (WebView) findViewById(R.id.mWebView);
    }

    public void initHeadView() {
        if (type == TYPE_APP_NOTICE) {
            if(mAppNotify != null && !TextUtils.isEmpty(mAppNotify.title))
                setHeadTitle(mAppNotify.title);
            else
                setHeadTitle(R.string.app_notice);
        } else if (type == TYPE_HELP) {
            setHeadTitle(R.string.help);
        } else if (type == TYPE_TERMS) {
            setHeadTitle(R.string.terms_service);
            setHeadRightButton(R.string.terms_private, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isService) {
                        setHeadRightButtonText(R.string.terms_service);
                        setHeadTitle(R.string.terms_private);
                        mWebView.stopLoading();
                        if (languae.contains("tw")) {
                            url = getHost() + ApiConstants.URL_PRIVATE_TW;
                        } else{
                            url = getHost() + ApiConstants.URL_PRIVATE;
                        }
                        loadUrl();
                    } else {
                        setHeadRightButtonText(R.string.terms_private);
                        setHeadTitle(R.string.terms_service);
                        mWebView.stopLoading();
                        if (languae.contains("tw")) {
                            url = getHost() + ApiConstants.URL_SERVICE_TW;
                        } else{
                            url = getHost() + ApiConstants.URL_SERVICE;
                        }
                        loadUrl();
                    }
                    isService = !isService;
                }
            });
        } else if(type == TYPE_SNG_RULE || type == TYPE_MTT_RULE) {
            setHeadTitle(R.string.rule);
        } else if(type == TYPE_ABOUT_US) {
            setHeadTitle(R.string.settings_column_aboutus);
        } else if (type == TYPE_DISCOVERY_BANNER) {
            setHeadTitle(R.string.app_name);
        } else if (type == TYPE_POKERCLANS_PROTOCOL) {
            setHeadTitle("游戏许可及服务协议");
        }
    }

    public void initWebView() {
        mWebView.setVisibility(View.VISIBLE);
//        mWebView.clearCache(true);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(false);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setBuiltInZoomControls(true);
        webSettings.setBuiltInZoomControls(false);
//        webSettings.setUseWideViewPort(true);//集WebView是否应该使支持“视窗”HTML meta标记或应该使用视窗。
        webSettings.setLoadWithOverviewMode(true);  //是否使用WebView加载页面,也就是说,镜头拉出宽度适合在屏幕上的内容。
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
//        webSettings.setBlockNetworkImage(true);//图片后台加载
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //H5
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(WebViewActivity.TYPE_APP_NOTICE == type){
                    if(!TextUtils.isEmpty(url))//启动牌局
                    {
                        int pos = TextUtils.indexOf(url,HREF_CHECK_IN);
                        if(pos != -1){
                            final String code = url.substring(pos+HREF_CHECK_IN.length());

                            if(code.length() >= 6){
                                checkGameVersion(new CheckHotUpdateCallback() {
                                    @Override
                                    public void notUpdate() {
                                        doGameJoinByGameStatus(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE, code);
                                    }
                                });
                                return  true;
                            }
                        }
                    }
                }
                mWebView.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
//                mWebView.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
//                mWebView.setVisibility(View.VISIBLE);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.stopLoading();
        mWebView = null;

        if(mHotUpdateAction != null)
            mHotUpdateAction.onDestroy();
        if(mGameAction != null)
            mGameAction.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取对应的CODE的牌局状态
     */
    public void doGameJoinByGameStatus(final String joinWay ,final String code) {
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(WebViewActivity.this, getString(R.string.game_join_code_notnull), Toast.LENGTH_SHORT).show();
            return;
        }

        if(mGameAction == null)
            mGameAction = new GameAction(this,null);
        mGameAction.joinGame(joinWay, code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                //joinGameByCodeSuccess();
                //里面已经跳转
            }

            @Override
            public void onFailed(int resultCode, JSONObject response) {
                if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT)
                    Toast.makeText(WebViewActivity.this, getString(R.string.game_join_notexist), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkGameVersion(CheckHotUpdateCallback callback) {
        if(mHotUpdateAction == null) {
            mHotUpdateAction = new HotUpdateAction(this, null);
            mHotUpdateAction.onCreate();
        }
        mHotUpdateAction.doHotUpdate(false, callback);
    }

    GameAction mGameAction;
    HotUpdateAction mHotUpdateAction;
}
