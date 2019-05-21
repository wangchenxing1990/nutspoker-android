package com.htgames.nutspoker.chat.session.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.htgames.nutspoker.R;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 16/11/30.
 */

public class TeamMsgAdapter extends FragmentPagerAdapter {
    public TeamMsgAdapter(FragmentManager fm) {
        super(fm);
    }

    public ArrayList<Fragment> mFragmentList;
    private final String[] TITLES = new String[3];

    public TeamMsgAdapter(FragmentManager fm, ArrayList<Fragment> list, Activity activity) {
        super(fm);
        mFragmentList = list;
        Resources res = activity.getResources();
        TITLES[0] = res.getString(R.string.game);
        TITLES[1] = res.getString(R.string.game_match_tab_chat);
        TITLES[2] = res.getString(R.string.me_column_record);
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

    @Override
    public CharSequence getPageTitle(int position) {
        return position >= 0 && position < 3 ? TITLES[position] : super.getPageTitle(position);
    }
}
