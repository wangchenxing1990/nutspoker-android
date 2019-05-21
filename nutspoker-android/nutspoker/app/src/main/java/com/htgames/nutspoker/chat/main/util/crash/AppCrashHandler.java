package com.htgames.nutspoker.chat.main.util.crash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class AppCrashHandler {

    private Context context;

    int versionCode;
    String versionName;
    File m_fileDir;

    private UncaughtExceptionHandler uncaughtExceptionHandler;

    private static AppCrashHandler instance;

    private static final String VERSION_NAME = "versionName";

    private static final String STACK_TRACE = "STACK_TRACE";
    /** 错误报告文件的扩展名 */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    /** 使用Properties来保存设备的信息和错误堆栈信息 */
    private Properties mDeviceCrashInfo = new Properties();

    private AppCrashHandler(final Context context) {
        this.context = context;

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        m_fileDir = CacheConstant.sAppDirUtil.getDumpDirFile();
        // get default
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

        // install
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable ex) {
                // save log
                //saveException(ex, true);

                // 收集设备信息
                collectCrashDeviceInfo();
                // 保存错误报告文件
                saveCrashInfoToFile(ex);

                // uncaught
                uncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        });
    }

    /**
     * 收集程序设备的信息
     *
     */
    public void collectCrashDeviceInfo() {
        mDeviceCrashInfo.put(VERSION_NAME,versionName);
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        try {
            mDeviceCrashInfo.put(STACK_TRACE, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String fileName = null;
        try {
            // long timestamp = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date curDate = new Date(System.currentTimeMillis());
            String timestamp = formatter.format(curDate);

            fileName = "chesscircle_crash-" + timestamp + CRASH_REPORTER_EXTENSION;
            File file = new File(m_fileDir, fileName);

            LogUtil.e("AppCrashHandler", "new Exception file occur : " + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream trace = new FileOutputStream(file);

            StringBuilder sb = new StringBuilder();
            sb.append('{');
            Iterator<Map.Entry<Object, Object>> i = mDeviceCrashInfo.entrySet().iterator();
            boolean hasMore = i.hasNext();
            while (hasMore) {
                Map.Entry<Object, Object> entry = i.next();

                Object key = entry.getKey();
                sb.append(key == mDeviceCrashInfo ? "(this Map)" : key.toString());
                sb.append('=');
                Object value = entry.getValue();
                sb.append(value == mDeviceCrashInfo ? "(this Map)" : value.toString());

                if (hasMore = i.hasNext()) {
                    sb.append(",\n");
                }
            }
            sb.append('}');
            String userInfo = "user_info:{" +
//                    "\n\tuid: " + UserPreferences.getInstance(context).getUserId() +//不要上传uid
                    "\n\tuuid: " + UserPreferences.getInstance(context).getZYId() +
                    "\n\t昵称：" + NimUserInfoCache.getInstance().getUserDisplayName(UserPreferences.getInstance(context).getUserId()) +
                    "\n\t手机号：" + UserPreferences.getInstance(context).getUserPhone() +
                    "\n\t用户区域：" + UserPreferences.getInstance(context).getUserCountryCode() +
                    "\n}\n";
            String temp = sb.toString() + userInfo;
            if (CacheConstant.debugBuildType) {
                trace.write(temp.getBytes());
            }
            trace.close();

            MobclickAgent.reportError(context, temp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static AppCrashHandler getInstance(Context mContext) {
        if (instance == null) {
            instance = new AppCrashHandler(mContext);
        }

        return instance;
    }

    public final void saveException(Throwable ex, boolean uncaught) {
        CrashSaver.save(context, ex, uncaught);
    }

    public void setUncaughtExceptionHandler(UncaughtExceptionHandler handler) {
        if (handler != null) {
            this.uncaughtExceptionHandler = handler;
        }
    }
}
