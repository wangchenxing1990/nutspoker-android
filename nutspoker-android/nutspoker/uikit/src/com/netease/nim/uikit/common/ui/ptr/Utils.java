package com.netease.nim.uikit.common.ui.ptr;


import com.netease.nim.uikit.common.util.log.LogUtil;

public class Utils {

	static final String LOG_TAG = "PullToRefresh";

	public static void warnDeprecation(String depreacted, String replacement) {
		LogUtil.w(LOG_TAG, "You're using the deprecated " + depreacted + " attr, please switch over to " + replacement);
	}

}
