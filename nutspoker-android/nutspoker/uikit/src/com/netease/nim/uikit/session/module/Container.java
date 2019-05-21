package com.netease.nim.uikit.session.module;

import android.app.Activity;

import com.netease.nim.uikit.common.activity.TActionBarActivity;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;

/**
 * Created by zhoujianghua on 2015/7/6.
 */
public class Container {
    public final TActionBarActivity activity;
    public final String account;
    public final SessionTypeEnum sessionType;
    public final ModuleProxy proxy;

    public Container(Activity activity, String account, SessionTypeEnum sessionType, ModuleProxy proxy) {
        this.activity = (TActionBarActivity) activity;
        this.account = account;
        this.sessionType = sessionType;
        this.proxy = proxy;
    }
}