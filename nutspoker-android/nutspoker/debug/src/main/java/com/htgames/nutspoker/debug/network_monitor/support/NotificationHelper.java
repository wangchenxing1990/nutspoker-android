/*
 * Copyright (C) 2017 Jeff Gilfelt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htgames.nutspoker.debug.network_monitor.support;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.LongSparseArray;

import com.htgames.nutspoker.debug.R;
import com.htgames.nutspoker.debug.network_monitor.ChuckActivity;
import com.htgames.nutspoker.debug.network_monitor.HttpTransaction;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NotificationHelper {

    private static final int NOTIFICATION_ID = 1138;
    private static final int BUFFER_SIZE = 10;

    private static final LongSparseArray<HttpTransaction> transactionBuffer = new LongSparseArray<>();
    private static int transactionCount;

    private final Context context;
    private final NotificationManager notificationManager;

    public static synchronized void clearBuffer() {
        transactionBuffer.clear();
        transactionCount = 0;
    }

    private static synchronized void addToBuffer(HttpTransaction transaction) {
        if (transaction.getStatus() == HttpTransaction.Status.Requested) {
            transactionCount++;
        }
        transactionBuffer.put(transaction._id, transaction);
        if (transactionBuffer.size() > BUFFER_SIZE) {
            transactionBuffer.removeAt(0);
        }
    }

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public synchronized void show(HttpTransaction transaction) {
        addToBuffer(transaction);
        if (!ChuckActivity.isInForeground()) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, ChuckActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0))
                    .setLocalOnly(true)
                    .setSmallIcon(R.mipmap.icon)
                    .setColor(ContextCompat.getColor(context, R.color.chuck_colorPrimary))
                    .setContentTitle(context.getString(R.string.chuck_notification_title));
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            int count = 0;
            for (int i = transactionBuffer.size() - 1; i >= 0; i--) {
                if (count < BUFFER_SIZE) {
                    if (count == 0) {
                        mBuilder.setContentText(transactionBuffer.valueAt(i).getNotificationText());
                    }
                    inboxStyle.addLine(transactionBuffer.valueAt(i).getNotificationText());
                }
                count++;
            }
            mBuilder.setAutoCancel(true);
            mBuilder.setStyle(inboxStyle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mBuilder.setSubText(String.valueOf(transactionCount));
            } else {
                mBuilder.setNumber(transactionCount);
            }
            mBuilder.addAction(getClearAction());
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }

    @NonNull
    private NotificationCompat.Action getClearAction() {
        CharSequence clearTitle = context.getString(R.string.chuck_clear);
        Intent deleteIntent = new Intent(context, ClearTransactionsService.class);
        PendingIntent intent = PendingIntent.getService(context, 11, deleteIntent, PendingIntent.FLAG_ONE_SHOT);
        return new NotificationCompat.Action(R.mipmap.icon, clearTitle, intent);
    }

    public void dismiss() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
