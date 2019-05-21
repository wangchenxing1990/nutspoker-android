package com.htgames.nutspoker.config;

import android.content.Context;
import android.graphics.Bitmap;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;
import java.io.IOException;

/**
 * UniversalImageLoader图片加载配置类
 */
public class UniversalImageLoaderUtil {
    private final static String TAG = "UniversalImageLoaderUtil";
    private static final int M = 1024 * 1024;

    public static ImageLoaderConfiguration getDefaultConfig(Context context) {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        int MAX_CACHE_MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);
        String cachePath = CacheConstant.getImageCachePath();
        File cacheDir = new File(cachePath);
        LogUtil.i(TAG, "ImageLoader memory cache size = " + MAX_CACHE_MEMORY_SIZE / M + "M");
        LogUtil.i(TAG, "ImageLoader disk cache directory = " + cacheDir.getAbsolutePath());
        ImageLoaderConfiguration config = null;
        try {
            config = new ImageLoaderConfiguration
                    .Builder(context)
                    .threadPoolSize(3) // 线程池内加载的数量
                    .threadPriority(Thread.NORM_PRIORITY - 2) // 降低线程的优先级，减小对UI主线程的影响
                    .denyCacheImageMultipleSizesInMemory()
//                    .memoryCache(new LruMemoryCache(MAX_CACHE_MEMORY_SIZE))
                    .memoryCache(new LRULimitedMemoryCache(MAX_CACHE_MEMORY_SIZE))
                    .discCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 0))
                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                    .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
//                    .writeDebugLogs()
                    .build();
            return config;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取系统消息图片DisplayImageOptions
     * @return
     */
    public static DisplayImageOptions getAppNoticeImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.app_message_imga_default)
                .showImageOnFail(R.mipmap.app_message_imga_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
                .build();
        return options;
    }
}
