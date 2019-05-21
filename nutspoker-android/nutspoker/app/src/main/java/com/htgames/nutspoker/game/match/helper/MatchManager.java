package com.htgames.nutspoker.game.match.helper;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 周智慧 on 17/4/17.
 */

public class MatchManager {
    private static MatchManager instance;
    private SparseArray<MatchMgrItem> items = new SparseArray<>();
    // observers
    public static synchronized MatchManager getInstance() {
        if (instance == null) {
            instance = new MatchManager();
        }
        return instance;
    }

    private MatchManager() {
        populate();
    }

    private void populate() {
//        items.put(MatchMgrItem.UPDATE_TYPE_NAME, new MatchMgrItem(UpdateItem.UPDATE_TYPE_NAME));
//        items.put(MatchMgrItem.UPDATE_TYPE_IS_CONTROL, new MatchMgrItem(UpdateItem.UPDATE_TYPE_IS_CONTROL));
    }

    private List<MatchStatusCallback> callbacks = new ArrayList<>();
    public static interface MatchStatusCallback {
        public void onUpdated(MatchMgrItem item);
    }

    public void clearCallback() {
        callbacks.clear();
    }

    public List<MatchStatusCallback> getCallbacks() {
        return callbacks;
    }

    public void registerCallback(MatchStatusCallback cb) {
        if (callbacks.contains(cb)) {
            return;
        }
        callbacks.add(cb);
    }

    public void unregisterCallback(MatchStatusCallback cb) {
        if (!callbacks.contains(cb)) {
            return;
        }
        callbacks.remove(cb);
    }

    public void execludeCallback(MatchMgrItem updateItem) {
        if (updateItem == null) {
            return;
        }
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).onUpdated(updateItem);
        }
    }
}
