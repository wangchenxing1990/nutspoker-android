package com.htgames.nutspoker.util.word;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by yfy on 2016/6/12.
 */
public class CHTool {
    private static final String TAG = CHTool.class.getName();

    /**
     * 是否在主进程
     *
     * @param context
     * @return
     */
    public static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;
                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 位图保存为文件
     *
     * @param bmp
     * @param dirPath
     * @param fileName
     * @return
     */
    public static boolean saveBitmapToFile(Bitmap bmp, String dirPath, String fileName) {
        boolean ret = false;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            stream = new FileOutputStream(dirPath + fileName);
            ret = bmp.compress(format, quality, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }


    /**
     * 缩放图片
     *
     * @param bmp
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap zoomImg(Bitmap bmp, int newWidth, int newHeight) {
        Log.i("CHTool", "zoomImg->newWidth=" + newWidth + ",newHeight=" + newHeight);
        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    /**
     * 获取view中的内容为位图
     *
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        //        Bitmap bitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.RGB_565);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    /**
     * 获得指定文件的byte数组
     *
     * @param filePath
     * @return
     */
    public static byte[] readFileBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 复制assets下的附件到指定位置
     *
     * @param context
     * @param from
     * @param target
     */
    public static void copyAssetsFile(Context context, String from, String target) {
        try {
            File file = new File(target);
            File dir = new File(target).getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream myInput = context.getAssets().open(from);
            OutputStream myOutput = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将byte[]保存为文件
     * <p/>
     * 用于上层调用需要处理异常的情况!!!!!!
     *
     * @param buff
     * @param target
     * @throws IOException
     */
    public static void saveBytes2File(byte[] buff, String target) throws IOException {
        Log.i(TAG, "saveBytes2File->" + target);
        File file = new File(target);
        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(buff);
        bos.flush();
        bos.close();
        fos.close();
    }

    /**
     * 获取唯一的设备号
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
//        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        final String tmDevice, tmSerial, tmPhone, androidId;
//        tmDevice = "" + tm.getDeviceId();
//        tmSerial = "" + tm.getSimSerialNumber();
//        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String uniqueId = deviceUuid.toString();
//        return uniqueId;
//        try {
//            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            WifiInfo info = wifi.getConnectionInfo();
//            Log.e(TAG, "getDeviceId->" + "wifi id=" + info.getMacAddress());
//            return info.getMacAddress();
//        } catch (Exception e) {
//            return UUID.randomUUID().toString();
//        }

        String addr = getMacAddrOld(context);
        if (addr.equals("02:00:00:00:00:00")) {
            addr = getMacAdd23(context);
        }
        return addr;
    }

    private static String getMacAdd23(Context context) {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return UUID.randomUUID().toString();
    }

    // Android 6.0之前的版本可以用的方法（模拟器可以使用）
    private static String getMacAddrOld(Context context) {
        String macString = "";
        WifiManager wifimsg = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifimsg != null) {
            if (wifimsg.getConnectionInfo() != null) {
                if (wifimsg.getConnectionInfo().getMacAddress() != null) {
                    macString = wifimsg.getConnectionInfo().getMacAddress();
                }
            }
        }
        return macString;
    }

    /**
     * 判断是否为wifi网络
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 获取本机当前版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取本机当前版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String toHexString(byte[] arr) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            str.append(String.format("%02X ", arr[i]));
        }
        return str.toString();
    }

    /**
     * 只允许字母、数字、下划线和汉字
     *
     * @param str
     * @return
     */
    public static boolean isInvalidString(String str) {
        try {
            String regEx = "[^a-zA-Z0-9_\u4E00-\u9FA5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            String res = m.replaceAll("").trim();
            return str.equals(res);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 屏蔽字验证
     * @param str  需要验证验证的文字
     * @return      验证后的结果
     */
    public static String tirmValue(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String value = str;
        value = tirmValueStart(value);
        value = tirmValueEnd(value);
        value = WordFilter.doFilter(value);
        Log.i("tirmValue", "value ->" + str + "<-- tirm ->" + value + "<");
        return value;
    }

    private static String tirmValueStart(String str) {
        String startString = str.substring(0, 1);
        if (startString.equals(" ")) {
            startString = str.substring(1);
            return tirmValueStart(startString);
        } else {
            return str;
        }
    }

    private static String tirmValueEnd(String str) {
        String endString = str.substring(str.length() - 1);
        if (endString.equals(" ")) {
            endString = str.substring(0, str.length() - 1);
            return tirmValueEnd(endString);
        } else {
            return str;
        }
    }

    /**
     * 提取末尾的数字
     *
     * @param content
     * @return
     */
    public static String getLastNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        String number = null;
        while (matcher.find()) {
            number = matcher.group(0);
        }
        return number;
    }

    public static String joinList(List<Integer> list, String sep) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                buffer.append(list.get(i));
            } else {
                buffer.append(list.get(i)).append(sep);
            }
        }
        return buffer.toString();
    }

    public static String getUrlQueryParam(String url, String key) {
        try {
            int start = url.indexOf("?");
            if (start >= 0) {
                String query = url.substring(start + 1);
                String[] pairs = query.split("&");
                Map<String, String> params = new HashMap<String, String>();
                for (String p : pairs) {
                    String[] kv = p.split("=");
                    if (kv.length == 2) {
                        params.put(kv[0], kv[1]);
                    }
                }
                return params.get(key);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }


    public static String timeslashData(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
//        long lcc = Long.valueOf(time);
//      int i = Integer.parseInt(time);
        String times = sdr.format(new Date(time * 1000L));
        return times;

    }


    public static String timeMinute(long time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm");
//        long lcc = Long.valueOf(time);
//        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(time * 1000L));
        return times;

    }

}
