package com.netease.nim.uikit.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.crl.zzh.customrefreshlayout.ControlToast;
import com.netease.nim.uikit.PendingIntentConstants;
import com.netease.nim.uikit.R;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.IControlComming;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nim.uikit.session.constant.Extras;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    /*开启这个flag后，你就可以正常使用Selector这样的DrawableContainers了。同时，你还开启了类似android:drawableLeft这样的compound drawable的使用权限，
     以及RadioButton的使用权限，以及ImageView’s src属性。
     文／eclipse_xu（简书作者） 原文链接：http://www.jianshu.com/p/e3614e7abc03 著作权归作者所有，转载请联系作者获得授权，并标注“简书作者”。*/
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public static Handler sHandler = new Handler(Looper.getMainLooper());
    /**
     * Screen informationBaseAppCompatActivity
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;

    /**
     * context
     */
    protected Context mContext = null;

    /**
     * network status
     */
//    protected NetChangeObserver mNetChangeObserver = null;

    /**
     * overridePendingTransition mode
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    @Override
    protected void onPause() {
        super.onPause();
        ControlToast.Companion.getInstance().setMIControlComming(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
        super.onCreate(savedInstanceState);
        mContext = this;
        //initWindows();//设置沉浸式聊天界面的输入框和输入键盘有问题(setFitsSystemWindows(true)导致)，后续只对LOLLIPOP=21做沉浸式
    }

    @Override
    protected void onResume() {
        super.onResume();
        ControlToast.Companion.getInstance().setMIControlComming(new IControlComming() {
            @Override
            public void showStatusbar(boolean show) {
                if (show) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                } else {
//                    View decorView = getWindow().getDecorView();
//                    int uiOptions = 0;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                    }
//                    decorView.setSystemUiVisibility(uiOptions);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            }
            @Override
            public void click() {
                if (ControlToast.Companion.getInstance().getMainActivityDestroyed()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Extras.EXTRA_PENDINGINTENT_ACIONT, PendingIntentConstants.ACTION_APP_MESSAGE_CONTROL);
                    Nav.from(CacheConstant.sAppContext).withExtras(bundle).withFlags(Intent.FLAG_ACTIVITY_NEW_TASK).toUri(UrlConstants.URL_WELCOME);
                } else {
                    Nav.from(BaseAppCompatActivity.this).toUri(UrlConstants.URL_APP_CONTROL);
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
            }
        }
    }

    /**
     * toggle overridePendingTransition
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();

    /**
     * get the overridePendingTransition mode
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();

    private void initWindows() {
        /*<!-- fitsSystemWindows只作用在sdk>=19的系统上就是高于4.4的系统，这个属性可以给任何view设置,只要设置了这个属性此view的所有padding属性失效.
        只有在设置了透明状态栏(StatusBar)或者导航栏(NavigationBar)此属性才会生效，
        如果上述设置了状态栏和导航栏为透明的话，相当于对该View自动添加一个值等于状态栏高度的paddingTop，和等于导航栏高度的paddingBottom -->*/
        /*  http://www.cnblogs.com/whoislcj/p/6250284.html  */
        Window window = getWindow();
        int color = getResources().getColor(R.color.head_bg);
        if (false/*Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP*/) {
//			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            //设置状态栏颜色
////            window.setStatusBarColor(color);
//            //设置导航栏颜色
////            window.setNavigationBarColor(color);
////            ViewGroup contentView = ((ViewGroup) findViewById(R.id.action_bar_root));
//            ViewGroup contentView = ((ViewGroup) findViewById(android.R.id.content));//this view is her: ---> 开发者布局的父布局
//            contentView.setPadding(0, getStatusBarHeight(this), 0, 0);
//            View childAt = contentView.getChildAt(0);
//            if (childAt != null) {
//                childAt.setFitsSystemWindows(true);
//            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置contentview为fitsSystemWindows
            ViewGroup contentView = ((ViewGroup) findViewById(R.id.action_bar_root));
            View childAt = contentView.getChildAt(0);
            if (childAt != null && !(this instanceof com.netease.nim.uikit.session.activity.BaseMessageActivity)) {
                childAt.setFitsSystemWindows(true);
            }
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.getStatusBarHeight(this)));
            view.setBackgroundColor(color);
            view.setId(R.id.android_status_bar);
            contentView.addView(view, 0);
        }
    }
}

