package com.htgames.nutspoker.chat.app_msg.adapter

import android.app.Activity
import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.htgames.nutspoker.R
import java.util.ArrayList

class ControlPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    var mFragmentList: ArrayList<Fragment>? = null
    private val TITLES = arrayOfNulls<String>(2)

    constructor(fm: FragmentManager, list: ArrayList<Fragment>, activity: Activity) : this(fm) {
        mFragmentList = list
        var res: Resources = activity.resources
        TITLES.set(0, res.getString(R.string.message_status_untreated))
        TITLES.set(1, res.getString(R.string.message_status_expired))
    }

    override fun getItem(position: Int): Fragment? {
        if (mFragmentList != null && position < mFragmentList!!.size) {
            return mFragmentList!!.get(position)
        } else {
            return null
        }
    }

    override fun getCount(): Int {
        return mFragmentList?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position >= 0 && position < TITLES.size) {
            return TITLES[position].toString()
        } else {
            return super.getPageTitle(position)
        }
    }
}