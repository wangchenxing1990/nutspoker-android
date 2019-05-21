package com.htgames.nutspoker.chat.app_msg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class AppMessagePagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mFragmentList;

    public AppMessagePagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        mFragmentList = list;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragmentList != null && position < mFragmentList.size()) {
            return mFragmentList.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }
}
