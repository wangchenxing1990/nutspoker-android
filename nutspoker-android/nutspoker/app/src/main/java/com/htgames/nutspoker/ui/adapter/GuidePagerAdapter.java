package com.htgames.nutspoker.ui.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author 功能描述：ViewPager适配器，用来绑定数据和view
 */
public class GuidePagerAdapter extends PagerAdapter {

    // 界面列表
    private ArrayList<View> views;

    public GuidePagerAdapter(ArrayList<View> views) {
        this.views = views;
    }

    /**
     * 获得当前界面数
     */
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    /**
     * 初始化position位置的界面
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
//		try {
//			if (views.get(position).getParent() == null) {
//				container.addView(views.get(position));
//			}
//			else {
//				((ViewGroup) views.get(position).getParent()).removeView(views.get(position));
//				container.addView(views.get(position));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        return views.get(position);
    }

    /**
     * 判断是否由对象生成界面
     */
    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return (view == arg1);
    }

    /**
     * 销毁position位置的界面
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//		container.removeView((View) object);
        container.removeView(views.get(position));
    }
}
