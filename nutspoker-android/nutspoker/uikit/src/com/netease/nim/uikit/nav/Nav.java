package com.netease.nim.uikit.nav;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by 周智慧 on 17/1/2. nav导航
 */

public class Nav {
    public static final String TAG = Nav.class.getSimpleName();
    private Context mContext;
    private Bundle mExtras;
    private int mFlags = -1;
    private Nav(Context context) {
        mContext = context;
    }

    /**
     * @param context use current Activity if possible
     */
    public static Nav from(final Context context) {
        return new Nav(context);
    }

    public boolean toUri(String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        if (mFlags != -1) {
            intent.addFlags(mFlags);
        }
        if (mExtras != null) {
            intent.putExtras(mExtras);
        }
        if (mContext != null) {
            mContext.startActivity(intent);
        }
        return true;
    }

    public Nav withExtras(final Bundle extras) {
        mExtras = extras;
        return this;
    }

    public Nav withFlags(final int flags) {
        mFlags = flags;
        return this;
    }

    public Nav forResult(final int request_code) {
        return this;
    }

    public PendingIntent toPendingUri(Uri uri, int requestCode, int flags) {
        return null;
    }
    public boolean toDetail(String itemId, Bundle bundle) {
        return true;
    }
}
