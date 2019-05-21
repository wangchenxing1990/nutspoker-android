package com.htgames.nutspoker.ui.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.view.widget.ObservableScrollView;
import com.netease.nim.uikit.base.BaseSwipeBackActivity;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.nav.Nav;
import com.netease.nim.uikit.nav.UrlConstants;
import com.netease.nim.uikit.shake.ShakeHelper;
import com.umeng.analytics.MobclickAgent;

import butterknife.Unbinder;

/**
 * Created by zjy on 2015/9/14.
 */
public class BaseActivity extends BaseSwipeBackActivity {
    public static final String Extra_Back =  "Extra_Back";
    protected ChessApp mChessApp;
    protected RequestQueue mRequestQueue;
    private boolean destroyed = false;
    private ShakeHelper shakeHelper;
    protected Unbinder mUnbinder = null;
    /***
     *  Activity名称
     */
    protected String mActivityName = "BaseActivity";
    /***
     * 是否包含Fragment
     */
    protected boolean mHasFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRequestQueue();
        //场景类型设置接口:普通统计场景类型
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (CacheConstant.debugBuildType) {
            shakeHelper = ShakeHelper.initShakeHelper(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if(mUnbinder != null) {
            mUnbinder.unbind();
        }
        mUnbinder = null;
        destroyed = true;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setHeadTitle(int resId) {
        ((TextView) findViewById(R.id.tv_head_title)).setText(resId);
    }

    public void setHeadTitle(String title) {
        ((TextView) findViewById(R.id.tv_head_title)).setText(title);
    }

    /**
     * 设置头部左边的按钮
     * @param resId
     */
    public void setHeadLeftButton(int resId) {
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.VISIBLE);
        btn_head_back.setText(resId);
    }

    public void setHeadLeftButtonGone() {
        TextView btn_head_back = ((TextView) findViewById(R.id.btn_head_back));
        btn_head_back.setVisibility(View.GONE);
    }

    /**
     * 设置头部右边按钮
     * @param resId
     * @param onClickListener
     */
    public void setHeadRightButton(int resId, View.OnClickListener onClickListener) {
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(resId);
        tv_head_right.setOnClickListener(onClickListener);
    }

    public void setHeadRightButtonText(int resId) {
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(resId);
    }

    /**
     * 设置头部右边第二个按钮
     * @param resId
     * @param onClickListener
     */
    public void setHeadRightSecondButton(int resId, View.OnClickListener onClickListener) {
        TextView tv_head_right_second = ((TextView) findViewById(R.id.tv_head_right_second));
        tv_head_right_second.setVisibility(View.VISIBLE);
        tv_head_right_second.setText(resId);
        tv_head_right_second.setOnClickListener(onClickListener);
    }

    public void setHeadRightButtonGone() {
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.GONE);
    }

    public void setHeadRightButtonVISIBLE() {
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
    }

    public void setHeadRightSecondButtonGone() {
        TextView tv_head_right_second = ((TextView) findViewById(R.id.tv_head_right_second));
        tv_head_right_second.setVisibility(View.GONE);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (getCurrentFocus() != null
//                    && getCurrentFocus().getWindowToken() != null) {
//                showKeyboard(false);
//            }
//            return super.onTouchEvent(event);
//        }
//        return super.onTouchEvent(event);
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //获取当前获得当前焦点所在View
            View view = getCurrentFocus();
            if (isClickEt(view, event)) {
                //如果不是edittext，则隐藏键盘
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    //隐藏键盘
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(event);
        }
        /**
         * 看源码可知superDispatchTouchEvent  是个抽象方法，用于自定义的Window
         * 此处目的是为了继续将事件由dispatchTouchEvent(MotionEvent event)传递到onTouchEvent(MotionEvent event)
         * 必不可少，否则所有组件都不能触发 onTouchEvent(MotionEvent event)
         */
        if (getWindow().superDispatchTouchEvent(event)) {
            return true;
        }
        return onTouchEvent(event);
    }

    /**
     * 获取当前点击位置是否为et
     *
     * @param view  焦点所在View
     * @param event 触摸事件
     * @return
     */
    public boolean isClickEt(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            view.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            //此处根据输入框左上位置和宽高获得右下位置
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void onBack(View view) {
//        onBackPressed();
        finish();
    }

    public void showKeyboard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            if (getCurrentFocus() == null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            } else {
                imm.showSoftInput(getCurrentFocus(), 0);
            }
        } else {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CacheConstant.debugBuildType) {
            ChessApp.showDebugView();
        }
        String back = getIntent().getStringExtra(Extra_Back);
        if(back != null && !back.isEmpty()){
            ((TextView)findViewById(R.id.btn_head_back)).setText(back);
        }

        if(!mHasFragment)//如果当前页面不包含Fragment
            MobclickAgent.onPageStart(mActivityName);
        UmengAnalytics.onResume(this);
        if (shakeHelper != null) {
            shakeHelper.onStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!mHasFragment) {
            MobclickAgent.onPageEnd(mActivityName);
        }
        UmengAnalytics.onPause(this);
        showKeyboard(false);
        if (shakeHelper != null) {
            shakeHelper.onPause();
        }
    }

    protected final Handler getHandler() {
        return sHandler;
    }

    protected ChessApp getChessApp() {
        if (mChessApp == null) {
            mChessApp = (ChessApp) getApplication();
        }
        return mChessApp;
    }

    protected void initRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = getChessApp().getRequestQueue();
        }
    }

    /**
     * 取消网络请求
     *
     * @param requestUrl
     */
    public void cancelAll(String requestUrl) {
        if (!TextUtils.isEmpty(requestUrl)) {
            mRequestQueue.cancelAll(requestUrl);
        }
    }

    public boolean isDestroyedCompatible() {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        } else {
            return destroyed || super.isFinishing();
        }
    }

    @TargetApi(17)
    private boolean isDestroyedCompatible17() {
        return super.isDestroyed();
    }

    /**
     * 编辑动作事件监听，屏蔽回车
     */
//    public TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {
//        @Override
//        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                showKeyboard(false);
//                return true;
//            }
//            return false;
//        }
//    };

    /**
     * 设置监听ScrollView,监听顶部背景变化（用于用户详情界面和俱乐部详情界面)
     * @param mObservableScrollView
     * @param head
     */
    public void setObservableScrollViewListener(ObservableScrollView mObservableScrollView ,final View head) {
        setHeadBackgroundColor(head , 0);
        mObservableScrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                int alipha = y;
                if(alipha <= 255 && alipha > 0){
                    setHeadBackgroundColor(head , alipha);
                } else if(alipha > 255){
                    setHeadBackgroundColor(head ,255);
                } else{
                    setHeadBackgroundColor(head , 0);
                }
            }
        });
    }

    public void setHeadBackgroundColor(View view ,int argb ) {
        view.getBackground().mutate().setAlpha(argb);
        view.invalidate();
    }

    protected void initWithNoFragment(String name){
        mHasFragment = true;
        mActivityName = name;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!CacheConstant.debugBuildType) {
            return super.dispatchKeyEvent(event);
        }
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.isLongPress() && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 开启彩蛋
                    eggProcess();
                    return true;
                }
                return super.dispatchKeyEvent(event); // 如果不是长按，则调用原有方法，执行按下back键应有的处理
            default:
                return super.dispatchKeyEvent(event); // 如果不是长按，则调用原有方法，执行按下back键应有的处理
        }
    }

    private void eggProcess() {
        if (CacheConstant.debugBuildType) {
            Toast.makeText(this, "开发者彩蛋开启中...", Toast.LENGTH_SHORT).show();
            Nav.from(this).toUri(UrlConstants.DEVELOP);
        }
    }
}
