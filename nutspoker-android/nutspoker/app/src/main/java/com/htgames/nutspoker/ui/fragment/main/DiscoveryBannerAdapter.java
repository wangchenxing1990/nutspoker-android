package com.htgames.nutspoker.ui.fragment.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.Pools;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.ui.activity.System.WebViewActivity;
import com.htgames.nutspoker.ui.fragment.main.DiscoveryFragment.BannerItem;
import com.htgames.nutspoker.ui.adapter.AkPagerAdapter;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.ViewScaleType;
import com.nostra13.universalimageloader.core.imageaware.NonViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by 周智慧 on 17/3/17.
 */

public class DiscoveryBannerAdapter extends AkPagerAdapter implements View.OnClickListener {
    private Pools.SimplePool<View> mPool = new Pools.SimplePool<View>(3);
    public int homeTabIndex = 0;
    public String homeTabDisplayname = "全部";
    public DiscoveryBannerAdapter(Activity activity, Context c) {
        super(activity);
    }

    public void setHomeTabInfo(int index, String displayName) {
        homeTabIndex = index;
        homeTabDisplayname = displayName;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mData == null || mData.size() <= 0) {
            return null;
        }
        int realPos = position % mData.size();
        BannerItem bannerItem = (BannerItem) mData.get(realPos);
        LogUtil.i("循环轮播viewpager：" + position + "  root:" + "   realPos:" + realPos);
        final FrameLayout root = newJuImageView(realPos);
        int imageWidth = ScreenUtil.screenWidth;
        int imageHeight = mActivity.getResources().getDimensionPixelOffset(R.dimen.discovery_notice_height);
        ImageLoader.getInstance().displayImage(bannerItem.picUrl, new NonViewAware(new ImageSize(imageWidth, imageHeight), ViewScaleType.CROP), createImageOptions(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                root.setBackgroundDrawable(new BitmapDrawable(loadedImage));
            }
        });
        root.setForeground(mActivity.getResources().getDrawable(R.drawable.fg_horde));
        root.setOnClickListener(this);
        root.setTag(R.id.item_data, bannerItem);
        root.setTag(R.id.item_position, realPos);
        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mPool.release((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public void addAll(List items) {
        super.addAll(items);
        int count = items.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                FrameLayout root = new FrameLayout(mActivity);
                if (i < 3) {
                    mPool.release(root);
                }
            }
        }
        notifyDataSetChanged();
    }

    private FrameLayout newJuImageView(int i) {
        BannerItem bannerItem = (BannerItem) mData.get(i);
        FrameLayout root = (FrameLayout) mPool.acquire();
        if (root == null) {
            root = new FrameLayout(mActivity);
        }
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return root;
    }

    @Override
    public int getCount() {
        return (mData != null && mData.size() <= 1) ? mData.size() : Integer.MAX_VALUE;
    }


    @Override
    public void onClick(View v) {
        BannerItem bannerItem = (BannerItem) v.getTag(R.id.item_data);
        int position = (int) v.getTag(R.id.item_position);
        if (bannerItem != null) {
            LogUtil.i("zzh", "点击发现tab顶部的viewpager  position:" + position + " data: " + bannerItem.picUrl);
            WebViewActivity.start(mActivity, WebViewActivity.TYPE_DISCOVERY_BANNER, bannerItem.href);
        }
    }

    public static final DisplayImageOptions createImageOptions() {
        int defaultIcon = R.mipmap.system_notice_defalt;
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIcon)
                .showImageOnFail(defaultIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
}
