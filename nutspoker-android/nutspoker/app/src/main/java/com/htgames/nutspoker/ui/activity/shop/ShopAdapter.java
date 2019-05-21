package com.htgames.nutspoker.ui.activity.shop;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.netease.nim.uikit.bean.DiamondGoodsEntity;
import com.netease.nim.uikit.bean.GameGoodsEntity;
import com.htgames.nutspoker.ui.recycler.MeRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 16/11/23.
 */

public class ShopAdapter<T> extends PagerAdapter {
    public static final String COIN_PAGE = "coin";
    public static final String DIAMOND_PAGE = "diamond";
    public MeRecyclerView mCoinRecyc;
    public MeRecyclerView mDiamondRecyc;
    protected ArrayList<T> mData = new ArrayList<>();
    ArrayList<GameGoodsEntity> goodsList = new ArrayList<GameGoodsEntity>();
    ArrayList<DiamondGoodsEntity> diamondGoodsList = new ArrayList<DiamondGoodsEntity>();
    protected LayoutInflater mInflater;
    protected Context mContext;
    public ShopAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(c);
    }

    public void setData(final List<T> items) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mCoinRecyc.setLayoutParams(lp);
        mDiamondRecyc.setLayoutParams(lp);
        mData.clear();
        if (items != null) {
            mData.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String type = (String) mData.get(position);
        if (COIN_PAGE.equals(type)) {
            container.addView(mCoinRecyc);
            return mCoinRecyc;
        } else if (DIAMOND_PAGE.equals(type)) {
            container.addView(mDiamondRecyc);
            return mDiamondRecyc;
        }
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
