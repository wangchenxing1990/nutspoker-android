package com.netease.nim.uikit.session.module.input;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.actions.BaseAction;

import java.util.List;

/**
 * 更多操作模块
 * Created by hzxuwen on 2015/6/17.
 */
public class ActionsPanel {
    private final static String TAG = "ActionsPanel";
    static List<BaseAction> actionList;
    static ActionsPagerAdapter adapter;

    // 初始化更多布局adapter
    public static void init(View view, List<BaseAction> actions) {
        actionList = actions;
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        ViewGroup indicator = (ViewGroup) view.findViewById(R.id.actions_page_indicator);

        adapter = new ActionsPagerAdapter(viewPager, actionList);
        viewPager.setAdapter(adapter);
        initPageListener(indicator, adapter.getCount(), viewPager);
    }

    public static void updateActionPanelLayout(View view, List<BaseAction> actions) {
        if (adapter != null && view != null) {
            actionList = actions;
            ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
            ViewGroup indicator = (ViewGroup) view.findViewById(R.id.actions_page_indicator);
            adapter = new ActionsPagerAdapter(viewPager, actionList);
            viewPager.setAdapter(adapter);
            initPageListener(indicator, adapter.getCount(), viewPager);
        }
        LogUtil.d(TAG, "updateActionPanelLayout");
    }

    //摧毁当前布局中的内容，以免照成异常
    public static void onDestory() {
        adapter = null;
        actionList = null;
    }

    // 初始化更多布局PageListener
    private static void initPageListener(final ViewGroup indicator, final int count, final ViewPager viewPager) {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setIndicator(indicator, count, position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setIndicator(indicator, count, 0);
    }

    /**
     * 设置页码
     */
    private static void setIndicator(ViewGroup indicator, int total, int current) {
        if (total <= 1) {
            indicator.removeAllViews();
        } else {
            indicator.removeAllViews();
            for (int i = 0; i < total; i++) {
                ImageView imgCur = new ImageView(indicator.getContext());
                imgCur.setId(i);
                // 判断当前页码来更新
                if (i == current) {
                    imgCur.setBackgroundResource(R.drawable.nim_moon_page_selected);
                } else {
                    imgCur.setBackgroundResource(R.drawable.nim_moon_page_unselected);
                }

                indicator.addView(imgCur);
            }
        }
    }
}
