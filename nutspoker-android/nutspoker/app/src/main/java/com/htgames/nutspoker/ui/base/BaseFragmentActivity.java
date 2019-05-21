package com.htgames.nutspoker.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;

/**
 * Created by 20150726 on 2015/9/22.
 */
public class BaseFragmentActivity extends FragmentActivity{
    protected ChessApp mChessApp;
    protected RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initRequestQueue();
    }

    public void setHeadTitle(int resId){
        (findViewById(R.id.rl_head)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.btn_head_back)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_head_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_head_title)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tv_head_title)).setText(resId);
    }

    public void setHeadGone(){
        (findViewById(R.id.rl_head)).setVisibility(View.GONE);
    }

    public void setHeadTitle(String title){
        (findViewById(R.id.rl_head)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.btn_head_back)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_head_right)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.tv_head_title)).setText(title);
    }

    /**
     * 设置头部右边按钮
     * @param resId
     * @param onClickListener
     */
    public void setHeadRightButton(int resId , View.OnClickListener onClickListener){
        TextView tv_head_right = ((TextView)findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(resId);
        tv_head_right.setOnClickListener(onClickListener);
    }

    public void setHeadRightButtonGone() {
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));
        tv_head_right.setVisibility(View.GONE);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
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
     * @param view 焦点所在View
     * @param event 触摸事件
     * @return
     */
    public  boolean isClickEt(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = { 0, 0 };
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
     * @param requestUrl
     */
    public void cancelAll(String requestUrl){
        if(!TextUtils.isEmpty(requestUrl)){
            mRequestQueue.cancelAll(requestUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        UmengAnalytics.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UmengAnalytics.onPause(this);
    }
}
