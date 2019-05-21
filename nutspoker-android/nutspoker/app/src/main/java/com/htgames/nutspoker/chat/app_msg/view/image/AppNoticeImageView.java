package com.htgames.nutspoker.chat.app_msg.view.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 */
public class AppNoticeImageView extends ImageView {
    private int defaultImageResId;

    private DisplayImageOptions options;

    private final DisplayImageOptions createImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImageResId)
                .showImageOnFail(defaultImageResId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new AppNoticeRoundedBitmapDisplayer(5))
                .build();
    }

    public AppNoticeImageView(Context context) {
        super(context);
    }

    public AppNoticeImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppNoticeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, com.netease.nim.uikit.R.styleable.ImageViewEx, defStyle, 0);
        defaultImageResId = a.getResourceId(com.netease.nim.uikit.R.styleable.ImageViewEx_exiv_default_image_res, 0);
        a.recycle();

        this.options = createImageOptions();
    }

    /**
     * 加载图片
     */
    public void load(final String url) {
        ImageLoader.getInstance().displayImage(url, this, options);
    }


    public void getDrawable(final String url , final ImageLoadingListener imageLoadingListener) {
        ImageLoader.getInstance().loadImage(url, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                imageLoadingListener.onLoadingComplete(s, view, bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
}
