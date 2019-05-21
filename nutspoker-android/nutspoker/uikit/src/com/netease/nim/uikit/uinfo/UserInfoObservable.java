package com.netease.nim.uikit.uinfo;

import android.content.Context;
import android.os.Handler;

import com.netease.nim.uikit.NimUIKit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户资料变动观察者。
 */
public class UserInfoObservable {

    private List<UserInfoObserver> observers = new ArrayList<>();
    private WeakReference<Handler> uiHandler;

    public UserInfoObservable(Context context) {
        uiHandler = new WeakReference<Handler>(NimUIKit.sHandler); //new Handler(context.getMainLooper());
    }

    synchronized public void registerObserver(UserInfoObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    synchronized public void unregisterObserver(UserInfoObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    synchronized public void notifyObservers(final List<String> accounts) {
        uiHandler.get().post(new Runnable() {
            @Override
            public void run() {
                for (UserInfoObserver observer : observers) {
                    observer.onUserInfoChanged(accounts);
                }
            }
        });
    }

    public interface UserInfoObserver {
        void onUserInfoChanged(List<String> accounts);
    }
}
