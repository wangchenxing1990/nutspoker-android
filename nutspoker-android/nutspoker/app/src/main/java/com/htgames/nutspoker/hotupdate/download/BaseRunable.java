package com.htgames.nutspoker.hotupdate.download;

/**
 */
public class BaseRunable implements Runnable {
    private final static String TAG = "BaseRunnable";
    public boolean mIsFinished = false;

    public BaseRunable() {
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    @Override
    public void run() {

    }
}
