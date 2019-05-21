package com.htgames.nutspoker.tool;

import android.app.Activity;

import java.util.Stack;

public class StackManager {
    /**
     * Stack 中对应的Activity列表 （也可以写做 Stack<Activity>）
     */
    private static Stack mActivityStack;
    private static StackManager mInstance;

    /**
     * @return ActivityManager
     * @描述 获取栈管理工具
     */
    public static StackManager getStackManager() {
        if (mInstance == null) {
            mInstance = new StackManager();
        }
        return mInstance;
    }

    /**
     * 推出栈顶Activity
     */
    public void popActivity(Activity activity) {
        //存在的话推出栈
        if (activity != null && isExcitTask(activity)) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 获得当前栈顶Activity
     */
    public Activity currentActivity() {
        // lastElement()获取最后个子元素，这里是栈顶的Activity
        if (mActivityStack == null || mActivityStack.size() == 0) {
            return null;
        }
        Activity activity = (Activity) mActivityStack.lastElement();
        return activity;
    }

    /**
     * 将当前Activity推入栈中
     */
    public void pushActivity(Activity activity) {
        if (mActivityStack == null) {
            mActivityStack = new Stack();
        }
        mActivityStack.add(activity);
    }

    /**
     * 判读堆栈里面是否有这个活动
     */
    public boolean isExcitTask(Activity activity) {
        if (mActivityStack == null) {
            return false;
        }
        return mActivityStack.contains(activity);
    }

    /**
     * 弹出指定的clsss所在栈顶部的中所有Activity
     *
     * @clsss : 指定的类
     */
    public void popTopActivitys(Class clsss) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(clsss)) {
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 弹出栈中所有Activity
     */
    public void popAllActivitys() {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            popActivity(activity);
        }
    }

    /**
     * 弹出栈中所有Activity,除了当前的那个界面
     */
    public void popActivitysOut(Activity outActivity) {
        if (mActivityStack == null) {
            return;
        }
        int size = mActivityStack.size();
        for (int i = (size - 1); i >= 0; i--) {
            Activity activity = (Activity) mActivityStack.get(i);
            if (activity != outActivity) {
                popActivity(activity);
            }
        }
    }

    /**
     * 获取指定的Class包含几个
     */
    public int getCurrentClassCount(Class clsss) {
        int count = 0;
        if (mActivityStack == null) {
            return count;
        }
        int size = mActivityStack.size();
        for (int i = 0; i < size; i++) {
            Activity activity = (Activity) mActivityStack.get(i);
            if (activity.getClass().equals(clsss)) {
                count = count + 1;
            }
        }
        return count;
    }
}