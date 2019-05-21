
package com.netease.nim.uikit.common.util.log;

public class LogUtil {
    private static final String TAG = "zzh";
    public static boolean s_debuggable = false;
    public static final void init(String logFile, int level, boolean debuggable) {
        LogUtil.s_debuggable = debuggable;
        LogImpl.init(logFile, level);
    }

    public static final void v(String tag, String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.v(tag, buildMessage(msg));
    }

    public static final void v(String tag, String msg, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.v(tag, buildMessage(msg), thr);
    }

    public static final void d(String tag, String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.d(tag, buildMessage(msg));
    }

    public static final void d(String tag, String msg, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.d(tag, buildMessage(msg), thr);
    }

    public static final void i(String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i(TAG, buildMessage(msg));
    }

    public static final void i(String tag, String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i(tag, buildMessage(msg));
    }

    public static final void i(String tag, String msg, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i(tag, buildMessage(msg), thr);
    }

    public static final void w(String tag, String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.w(tag, buildMessage(msg));
    }

    public static final void w(String tag, String msg, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.w(tag, buildMessage(msg), thr);
    }

    public static final void w(String tag, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.w(tag, buildMessage(""), thr);
    }

    public static final void e(String tag, String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.e(tag, buildMessage(msg));
    }

    public static final void e(String tag, String msg, Throwable thr) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.e(tag, buildMessage(msg), thr);
    }

    public static final void ui(String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i("ui", buildMessage(msg));
    }

    public static final void res(String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i("RES", buildMessage(msg));
    }

    public static final void audio(String msg) {
        if (!LogUtil.s_debuggable) {
            return;
        }
        LogImpl.i("AudioRecorder", buildMessage(msg));
    }

    public static String getLogFileName(String cat) {
        return LogImpl.getLogFileName(cat);
    }

    private static String buildMessage(String msg) {
        return msg;
    }
}