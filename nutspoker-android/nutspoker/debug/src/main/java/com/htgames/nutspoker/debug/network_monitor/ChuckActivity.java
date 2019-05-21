package com.htgames.nutspoker.debug.network_monitor;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.htgames.nutspoker.debug.R;
import com.htgames.nutspoker.debug.network_monitor.support.NotificationHelper;
import com.htgames.nutspoker.debug.network_monitor.ui.TransactionActivity;
import com.htgames.nutspoker.debug.network_monitor.ui.TransactionListFragment;
import com.netease.nim.uikit.base.BaseSwipeBackActivity;

/**
 * Created by 周智慧 on 17/5/2.
 */

public class ChuckActivity extends BaseSwipeBackActivity implements TransactionListFragment.OnListFragmentInteractionListener {
    private static boolean inForeground;

    private NotificationHelper notificationHelper;

    public static boolean isInForeground() {
        return inForeground;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationHelper = new NotificationHelper(this);
        setContentView(R.layout.chuck_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(getApplicationName());
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, TransactionListFragment.newInstance()).commit();//commitAllowingStateLoss();
        }
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    public void onListFragmentInteraction(HttpTransaction transaction) {
        TransactionActivity.start(this, transaction._id);
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        inForeground = true;
        notificationHelper.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        inForeground = false;
    }
}
