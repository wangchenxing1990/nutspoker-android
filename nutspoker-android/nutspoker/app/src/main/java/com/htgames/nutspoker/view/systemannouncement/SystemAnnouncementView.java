package com.htgames.nutspoker.view.systemannouncement;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.view.widget.MarqueeTextView;

import java.lang.reflect.Field;

/**
 * 系统公告（View）
 */
public class SystemAnnouncementView extends LinearLayout implements MarqueeTextView.OnMarqueeListener{
    private final static String TAG = "SystemAnnouncementView";
    WindowManager windowManager;
    int windowWidth = 0;
    private WindowManager.LayoutParams mParams;
    int statusBarHeight = 0;
    MarqueeTextView mMarqueeTextView;

    public SystemAnnouncementView(Context context) {
        super(context);
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowWidth = windowManager.getDefaultDisplay().getWidth();
        LayoutInflater.from(context).inflate(R.layout.view_system_announcement, this);
        mMarqueeTextView = (MarqueeTextView)findViewById(R.id.mMarqueeTextView);
        mMarqueeTextView.setOnMarqueeListener(this);
    }

    public void setAnnouncement(String text){
        mMarqueeTextView.setText(text);
        mMarqueeTextView.post(new Runnable() {
            @Override
            public void run() {
                mMarqueeTextView.startScroll(1);
            }
        });
    }

    /**
     * 更新悬浮窗在屏幕中的位置。
     * @param x
     * @param y
     */
    private void updateViewPosition(int x , int y) {
        LogUtil.i("updateViewPosition", "显示在 : x = " + x + ";y = " + y);
        mParams.x = x;
        mParams.y = y;
        windowManager.updateViewLayout(this , mParams);
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 用于获取状态栏的高度。
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            // 判断状态栏是否存在，不存在返回高度为0
            WindowManager.LayoutParams attrs = ((Activity) getContext()).getWindow().getAttributes();
            if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                LogUtil.i(TAG, "状态栏不存在！！");
                return 0;
            } else {
                LogUtil.i(TAG, "状态栏存在！！");
            }
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        SystemWindowManager.removeSystemAnnouncementView(getContext());
    }
}
