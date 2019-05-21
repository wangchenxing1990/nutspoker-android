package com.htgames.nutspoker.game.match.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;

import java.util.List;

/**
 * Created by Administrator on 2016/5/22.
 */
public class MatchPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private List<Fragment> fragmentList;                         //fragment列表
    private List<String> titleList;
    private List<Integer> iconList;
    public boolean showIcon = false;

    public MatchPagerAdapter(FragmentManager fm, List<Fragment> fragmentList , List<String> titleList , List<Integer> iconList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
        this.iconList = iconList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position % getCount());
    }

    @Override
    public int getPageIconResId(int position) {
        return iconList.get(position % getCount());
    }

    @Override
    public boolean showIcon() {
        return showIcon;
    }
}