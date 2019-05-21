package com.htgames.nutspoker.hotupdate.tool;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/4/14.
 */

public class HotUpdateManager {
    private static HotUpdateManager instance;
    private SparseArray<HotUpdateItem> items = new SparseArray<>();
    // observers
    public static synchronized HotUpdateManager getInstance() {
        if (instance == null) {
            instance = new HotUpdateManager();
        }
        return instance;
    }

    private HotUpdateManager() {
        populate();
    }

    private void populate() {
//        items.put(HotUpdateItem.UPDATE_TYPE_NAME, new HotUpdateItem(UpdateItem.UPDATE_TYPE_NAME));
//        items.put(HotUpdateItem.UPDATE_TYPE_IS_CONTROL, new HotUpdateItem(UpdateItem.UPDATE_TYPE_IS_CONTROL));
    }

    private List<HotUpdateCallback> callbacks = new ArrayList<>();
    public static interface HotUpdateCallback {
        public void onUpdated(HotUpdateItem item);
    }

    public void clearCallback() {
        callbacks.clear();
    }

    public List<HotUpdateCallback> getCallbacks() {
        return callbacks;
    }

    public void registerCallback(HotUpdateCallback cb) {
        if (callbacks.contains(cb)) {
            return;
        }
        callbacks.add(cb);
    }

    public void unregisterCallback(HotUpdateCallback cb) {
        if (!callbacks.contains(cb)) {
            return;
        }
        callbacks.remove(cb);
    }

    public void execludeCallback(HotUpdateItem updateItem) {
        if (updateItem == null) {
            return;
        }
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onUpdated(updateItem);
        }
    }
}
