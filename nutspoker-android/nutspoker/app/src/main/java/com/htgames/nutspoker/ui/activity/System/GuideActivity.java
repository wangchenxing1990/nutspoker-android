package com.htgames.nutspoker.ui.activity.System;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.htgames.nutspoker.ui.adapter.GuidePagerAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by 20150726 on 2015/9/23.
 */
public class GuideActivity extends BaseActivity {
    ViewPager mViewPager;
    GuidePagerAdapter mGuidePagerAdapter;
    ArrayList<View> views;
    // 定义底部小点图片
    private ImageView iv_indicator_one, iv_indicator_two, iv_indicator_three;
    private int[] guideImgResId = new int[]{R.mipmap.login_bg , R.mipmap.login_bg ,R.mipmap.login_bg};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initGuideLayout();
    }

    private void initGuideLayout() {
        mViewPager = (ViewPager)findViewById(R.id.mViewPager);
        views = new ArrayList<View>();
        int size = guideImgResId.length;
        for(int i = 0 ; i < size ; i ++){
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_guide_item , null);
            ((ImageView)view.findViewById(R.id.iv_guide_desc)).setImageResource(guideImgResId[i]);
            ((RelativeLayout)view.findViewById(R.id.rl_guide_item)).setBackgroundResource(guideImgResId[i]);
            if(i == 3){
                RelativeLayout rl_guide_enter = (RelativeLayout)view.findViewById(R.id.rl_guide_enter);
                rl_guide_enter.setVisibility(View.VISIBLE);
                rl_guide_enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingsPreferences.getInstance(getApplicationContext()).setIsFirst(false);
                    }
                });
            }
            views.add(view);
        }
        iv_indicator_one = (ImageView)findViewById(R.id.iv_indicator_one);
        iv_indicator_two = (ImageView)findViewById(R.id.iv_indicator_two);
        iv_indicator_three = (ImageView)findViewById(R.id.iv_indicator_three);
        mGuidePagerAdapter = new GuidePagerAdapter(views);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        iv_indicator_one.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_focused));
                        iv_indicator_two.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        iv_indicator_three.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        break;
                    case 1:
                        iv_indicator_one.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        iv_indicator_two.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_focused));
                        iv_indicator_three.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        break;
                    case 2:
                        iv_indicator_one.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        iv_indicator_two.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_unfocused));
                        iv_indicator_three.setImageDrawable(getResources().getDrawable(R.mipmap.guide_indicator_focused));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int position) {

            }
        });
        mViewPager.setAdapter(mGuidePagerAdapter);
    }

}
