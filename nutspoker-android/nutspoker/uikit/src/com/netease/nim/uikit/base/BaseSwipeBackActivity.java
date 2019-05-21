package com.netease.nim.uikit.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
/*
* 正常activity的layout布局是：FrameLayout
*                               LinearLayout
*                                   FrameLayout
*                                       LinearLayout(action_bar_root)
*                                           FrameLayout(content)
* 右滑finish的activity的layout布局是：FrameLayout ---> FrameLayout(swipe)多了一层，其余一样
*                                       LinearLayout
*                                           FrameLayout
*                                               LinearLayout(action_bar_root)
*                                                   FrameLayout(content)
* */

public abstract class BaseSwipeBackActivity extends BaseAppCompatActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper = null;
    }

    @Override
    public Resources getResources() {
        //设置资源不跟随系统的大小变化
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();

        try{//这里会崩溃，就是设置失败
            res.updateConfiguration(config, res.getDisplayMetrics());
        } catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }
}
