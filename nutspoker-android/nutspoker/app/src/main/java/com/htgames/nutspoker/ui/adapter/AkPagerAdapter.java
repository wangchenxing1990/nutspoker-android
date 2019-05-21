package com.htgames.nutspoker.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 16/4/5.
 */
public abstract class AkPagerAdapter<T> extends PagerAdapter {
    public ArrayList<T> mData = new ArrayList();
    protected LayoutInflater mInflater;
    protected Activity mActivity;

    public AkPagerAdapter(Activity c) {
        mActivity = c;
        mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public abstract Object instantiateItem(ViewGroup container, int position);

    @Override
    public abstract void destroyItem(ViewGroup container, int position, Object object);

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    public void addItem(final T item) {
        mData.add(item);
        notifyDataSetChanged();
    }
    public void addAll(final List<T> items) {
        if (items != null) {
            mData.addAll(items);
        }
        notifyDataSetChanged();
    }
    public void setData(final List<T> items) {
        mData.clear();
        if (items != null) {
            mData.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addItem(int idx, final T item) {
        mData.add(idx, item);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mData.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public ArrayList<T> getData() {
        return mData;
    }
}
