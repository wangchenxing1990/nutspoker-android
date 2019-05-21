package com.htgames.nutspoker.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.adapter.AlbumPagerAdapter;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/10/20.
 */
public class AlbumView extends RelativeLayout {
    AlbumPagerAdapter mAlbumPagerAdapter;
    ViewPager mAlbumViewPager;
    public View view;
    LinearLayout ll_indicator;
    public final static int ALBUM_TYPE_USER = 0;
    public final static int ALBUM_TYPE_CLUB = 1;

    public AlbumView(Context context) {
        super(context);
        init(context);
    }

    public AlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context){
        view = LayoutInflater.from(context).inflate(R.layout.view_ablum , null);
        mAlbumViewPager = (ViewPager) view.findViewById(R.id.mAlbumViewPager);
        ll_indicator = (LinearLayout) view.findViewById(R.id.ll_indicator);
        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setAlbumInfo(ArrayList<String> imgList , int albumType){
        mAlbumPagerAdapter = new AlbumPagerAdapter(getContext() , imgList , albumType);
        mAlbumViewPager.setAdapter(mAlbumPagerAdapter);
        setOvalLayout(ll_indicator);
    }

    int oldIndex = 0;
    int curIndex = 0;

    /**
     * 获取Banner的数量
     * @return
     */
    public int getAlbumSize(){
        return mAlbumPagerAdapter != null ? mAlbumPagerAdapter.getCount() : 0;
    }

    // 设置圆点
    private void setOvalLayout(final LinearLayout ovalLayout) {
        if (ovalLayout != null) {
            ovalLayout.removeAllViews();//先清空所有圆点
            int size = getAlbumSize();
            if (size <= 1) {
                //如果Banner数量少于等于1，不显示圆点
                return;
            }
            for (int i = 0; i < size; i++) {
                ovalLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.view_banner_indicator, null));
            }
            // 选中第一个
            ovalLayout.getChildAt(0).findViewById(R.id.v_banner_indicator).setBackgroundResource(R.mipmap.page_indicator_focused);
            mAlbumViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    curIndex = position;
                    // 取消圆点选中
                    ovalLayout.getChildAt(oldIndex).findViewById(R.id.v_banner_indicator).setBackgroundResource(R.mipmap.page_indicator_unfocused);
                    // 圆点选中
                    ovalLayout.getChildAt(curIndex).findViewById(R.id.v_banner_indicator).setBackgroundResource(R.mipmap.page_indicator_focused);
                    oldIndex = curIndex;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            mAlbumViewPager.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
