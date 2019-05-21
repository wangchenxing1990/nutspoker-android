package com.htgames.nutspoker.cocos2d;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by 20150726 on 2016/2/1.
 */
public class RecordTime {
    private static final String TAG = "RecordTime";
    Context context;
    private long mBase;
    OnRecordTimeUpdateListener mOnRecordTimeUpdateListener;
    private boolean mStarted;
    private boolean mRunning;
    private static final int TICK_WHAT = 2;
    private long currentRecordTime = 0;
    private int delayMillis = 1000;//多久提醒一次

    public interface OnRecordTimeUpdateListener {
        void onRecordTimeUpdate(long currentTime);
    }

    public void setOnRecordTimeUpdateListener(OnRecordTimeUpdateListener listener) {
        this.mOnRecordTimeUpdateListener = listener;
    }

    public RecordTime(Context context) {
        this.context = context;
        init();
    }

    public RecordTime(Context context, int millis) {
        this.context = context;
        this.delayMillis = millis;
        init();
    }

    private void init() {
        mBase = SystemClock.elapsedRealtime();
        updateRecordTime(mBase);
    }

    public void setBase(long base) {
        mBase = base;
        updateRecordTime(SystemClock.elapsedRealtime());
    }

    public long getBase() {
        return mBase;
    }

    public void updateRecordTime(long now) {
        currentRecordTime = now - mBase;
//        currentRecordTime /= 1000;
        if (mOnRecordTimeUpdateListener != null) {
            mOnRecordTimeUpdateListener.onRecordTimeUpdate(currentRecordTime);
        }
    }

    public long getCurrentRecordTime() {
        return currentRecordTime;
    }

    public void start() {
        mStarted = true;
        updateRunning();
    }

    public void stop() {
        mStarted = false;
        updateRunning();
    }

    public void setStarted(boolean started) {
        mStarted = started;
        updateRunning();
    }

    private void updateRunning() {
        boolean running = mStarted;
        if (running != mRunning) {
            if (running) {
                updateRecordTime(SystemClock.elapsedRealtime());
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 1000);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                updateRecordTime(SystemClock.elapsedRealtime());
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), delayMillis);
            }
        }
    };

    public void onDestroy() {
        mHandler = null;
    }
}
