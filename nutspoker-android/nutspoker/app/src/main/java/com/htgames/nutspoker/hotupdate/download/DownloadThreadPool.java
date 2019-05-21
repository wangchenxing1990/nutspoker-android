package com.htgames.nutspoker.hotupdate.download;

import com.netease.nim.uikit.common.framework.PriorityThreadFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 下载的线程池
 */
public class DownloadThreadPool {
    //	private static final int poolCount = Runtime.getRuntime().availableProcessors() * 3 + 2;
    private final static int POOL_SIZE = Thread.NORM_PRIORITY - 2;// 线程池的大小最好设置成为CUP核数的2N
    private final static int MAX_POOL_SIZE = Thread.NORM_PRIORITY - 2;// 设置线程池的最大线程数
    private final static int KEEP_ALIVE_TIME = 4;// 设置线程的存活时间
    //	private final Executor mExecutor;
    private final ExecutorService pool;
    BlockingQueue<Runnable> workQueue;

    public DownloadThreadPool() {
        // 创建线程池工厂
        ThreadFactory factory = new PriorityThreadFactory("download-thread-pool", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        // 创建工作队列
        workQueue = new LinkedBlockingDeque<Runnable>();
//		mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
        pool = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, factory);
    }

    public ExecutorService getExecutorService() {
        return pool;
    }

    /**
     * 获取线程迟所用的缓冲队列
     */
    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    // 在线程池中执行线程
    public <T> Future<T> submit(Callable<T> task) {
//		mExecutor.execute(command);
        return pool.submit(task);
    }

    // 在线程池中执行线程
    public void submit(Runnable command) {
//		mExecutor.execute(command);
        pool.submit(command);
    }

    // 在线程池中执行线程
    public void execute(Runnable command) {
//		mExecutor.execute(command);
        pool.execute(command);
    }

    public List<Runnable> shutdownNow() {
        return pool.shutdownNow();
    }

    public void shutdown() {
        pool.shutdown();
    }

    public boolean isTerminated() {
        return pool.isTerminated();
    }

    public void shutdownAndAwaitTermination() {
        shutdownAndAwaitTermination(pool);
    }

    /**
     * 关闭线程
     */
    public void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
