package com.htgames.nutspoker.debug.network_monitor.support;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.htgames.nutspoker.debug.network_monitor.data.ChuckContentProvider;

public class ClearTransactionsService extends IntentService {

    public ClearTransactionsService() {
        super("Chuck-ClearTransactionsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        getContentResolver().delete(ChuckContentProvider.TRANSACTION_URI, null, null);
        NotificationHelper.clearBuffer();
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.dismiss();
    }
}