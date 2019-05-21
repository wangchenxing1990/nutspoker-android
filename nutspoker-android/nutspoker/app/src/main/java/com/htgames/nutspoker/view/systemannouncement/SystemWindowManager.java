package com.htgames.nutspoker.view.systemannouncement;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;

import com.htgames.nutspoker.R;

/**
 */
public class SystemWindowManager {
    static SystemAnnouncementView mSystemAnnouncementView;
    private static LayoutParams smallWindowParams;
    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    public static SystemAnnouncementView createSystemAnnouncementView(Context context , String text){
        mWindowManager = getWindowManager(context);
        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        if(mSystemAnnouncementView == null || mSystemAnnouncementView.getContext() != context){
            mSystemAnnouncementView = new SystemAnnouncementView(context);
        }
        mSystemAnnouncementView.setAnnouncement(text);
        if (smallWindowParams == null) {
            smallWindowParams = new WindowManager.LayoutParams();
            smallWindowParams.type = LayoutParams.TYPE_SYSTEM_ALERT; // 设置window
//            smallWindowParams.type = LayoutParams.TYPE_SYSTEM_ALERT; // 设置window
//            smallWindowParams.type = LayoutParams.TYPE_APPLICATION; // 设置window
            // type
            smallWindowParams.format = PixelFormat.TRANSLUCENT;// 设置图片格式，效果为背景透明
//            smallWindowParams.format = PixelFormat.RGBA_8888;// 设置图片格式，效果为背景透明
            // IBinder ibinder = Binder.
//				 smallWindowParams.token = activity;
			/*
			 * * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
			 */
            smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
//				smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//						| LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//		                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            smallWindowParams.windowAnimations = 0;
//            smallWindowParams.gravity = Gravity.CENTER | Gravity.TOP;// 调整悬浮窗口至右侧中间
            smallWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;// 调整悬浮窗口至右侧中间
            // 设置悬浮窗口长宽数据
            smallWindowParams.width = LayoutParams.MATCH_PARENT;
            smallWindowParams.height = LayoutParams.WRAP_CONTENT;
            // 以屏幕左上角为原点，设置x、y初始值
//            smallWindowParams.x = screenWidth;
//            smallWindowParams.y = screenHeight / 2;
            smallWindowParams.x = 0;
            smallWindowParams.y = (int)context.getResources().getDimension(R.dimen.head_height);
        }
        if (mSystemAnnouncementView.getParent() == null) {
            // 如果未被添加进入父类
            if(mWindowManager != null && mSystemAnnouncementView != null){
                mWindowManager.addView(mSystemAnnouncementView, smallWindowParams);
            }
        }
        mSystemAnnouncementView.setParams(smallWindowParams);
        return mSystemAnnouncementView;
    }

    public static void showSystemAnnouncementView(){
        if(mSystemAnnouncementView != null){
            mSystemAnnouncementView.setVisibility(View.VISIBLE);
        }
    }

    public static void dismissSystemAnnouncementView(){
        if(mSystemAnnouncementView != null){
            mSystemAnnouncementView.setVisibility(View.GONE);
        }
    }

    public static void removeSystemAnnouncementView(Context context){
        if (mSystemAnnouncementView != null && mSystemAnnouncementView.getParent() != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(mSystemAnnouncementView);
        }
        mSystemAnnouncementView = null;
        smallWindowParams = null;
        mWindowManager = null;//必须销毁，否则在显示时按返回，然后重新创建显示会报错
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
