package com.netease.nim.uikit.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;

/**
 * Created by 周智慧 on 17/1/9.
 * home页底部的5个tab
 */

public class HomeTabLayout extends LinearLayout {
    private int gameTabWidth = 0;
    private int itemWidth = 0;
    public HomeTabLayout(Context context) {
        this(context, null);
    }
    public HomeTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public HomeTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeTabLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleRes) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.homeTab, defStyleRes, 0);
        float itemCount = ta.getInt(R.styleable.homeTab_itemCount, 5);
        gameTabWidth = ScreenUtil.dp2px(context, 48);//中间的hometab图片宽度弄宽点48dp
        itemWidth = (int) (ScreenUtil.screenMin * 1.0f / itemCount);
        if (itemWidth < gameTabWidth) {
            gameTabWidth = itemWidth;
        }
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        Resources res = context.getResources();
        setBackgroundColor(res.getColor(R.color.home_tab_bg_color));
        LayoutParams lp = new LayoutParams(itemWidth, LayoutParams.MATCH_PARENT);
        for (int i = 0; i < itemCount; i++) {
            if (i == 2) {//第二个hometab有点特殊
                FrameLayout frameLayout = new FrameLayout(context);
                FrameLayout.LayoutParams homeLP = new FrameLayout.LayoutParams(gameTabWidth, LayoutParams.MATCH_PARENT);
                homeLP.gravity = Gravity.CENTER;
                ImageView homeImage = new ImageView(context);
                homeImage.setId(R.id.item_data);
                homeImage.setEnabled(false);
                homeImage.setPadding(0, 0, 0, ScreenUtil.dp2px(context, 5));
                frameLayout.addView(homeImage, homeLP);
                addView(frameLayout, lp);
            } else {
                HomeTabItem itemView = new HomeTabItem(context);
                itemView.setEnabled(true);
                addView(itemView, lp);
            }
        }
    }

}
