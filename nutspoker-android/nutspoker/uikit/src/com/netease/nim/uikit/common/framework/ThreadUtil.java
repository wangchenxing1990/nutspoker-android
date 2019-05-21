package com.netease.nim.uikit.common.framework;

/**
 * Created by glp on 2016/8/26.
 */


import com.netease.nim.uikit.common.framework.PriorityThreadFactory;
import com.netease.nim.uikit.chesscircle.CacheConstant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池对象，统一调用这个，
 *
 * 警告：不要单独去new Thread start,会导致OOM
 */

public class ThreadUtil {

    static ExecutorService sInstance;
    public static ExecutorService GetInstance() {
        if(sInstance == null)
            sInstance = Executors.newFixedThreadPool(CacheConstant.sMaxThread ,new PriorityThreadFactory("ThreadUtil", android.os.Process.THREAD_PRIORITY_BACKGROUND));
        return sInstance;
    }
    /**
     *
     * @param runnable
     */
    public static void Execute(Runnable runnable){
        GetInstance().execute(runnable);
    }

    public static void Shutdown(){
        GetInstance().shutdown();
    }

}
