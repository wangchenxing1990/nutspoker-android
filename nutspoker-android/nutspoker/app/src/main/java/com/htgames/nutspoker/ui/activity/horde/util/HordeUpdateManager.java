package com.htgames.nutspoker.ui.activity.horde.util;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/3/22. 更新部落信息不是长连接，但是频繁调用短连接=不好，暂时这样做
 */

public class HordeUpdateManager {
    private static HordeUpdateManager instance;
    // observers
    private SparseArray<UpdateItem> items = new SparseArray<>();
    public static synchronized HordeUpdateManager getInstance() {
        if (instance == null) {
            instance = new HordeUpdateManager();
        }
        return instance;
    }

    private HordeUpdateManager() {
        populate();
    }

    private void populate() {
        items.put(UpdateItem.UPDATE_TYPE_NAME, new UpdateItem(UpdateItem.UPDATE_TYPE_NAME));
        items.put(UpdateItem.UPDATE_TYPE_IS_CONTROL, new UpdateItem(UpdateItem.UPDATE_TYPE_IS_CONTROL));
    }

    private List<HordeUpdateCallback> callbacks = new ArrayList<>();
    public static interface HordeUpdateCallback {
        public void onUpdated(UpdateItem item);
    }

    public void registerCallback(HordeUpdateCallback cb) {
        if (callbacks.contains(cb)) {
            return;
        }
        callbacks.add(cb);
    }

    public void unregisterCallback(HordeUpdateCallback cb) {
        if (!callbacks.contains(cb)) {
            return;
        }
        callbacks.remove(cb);
    }

    public void execludeCallback(UpdateItem updateItem) {
        if (updateItem == null) {
            return;
        }
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onUpdated(updateItem);
        }
    }
}
