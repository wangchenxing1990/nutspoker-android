package com.htgames.nutspoker.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.netease.nim.uikit.chesscircle.CacheConstant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     * 获取目录名称
     * @param url
     * @return FileName
     */
    public static String getFileName(String url) {
        int lastIndexStart = url.lastIndexOf("/");
        if (lastIndexStart != -1) {
            return url.substring(lastIndexStart + 1, url.length());
        } else {
            return new Timestamp(System.currentTimeMillis()).toString();
        }
    }

    /**
     * 判断SD卡是否存在
     *
     * @return boolean
     */
    public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 保存目录目录到目录
     *
     * @param context
     * @return 目录保存的目录
     */
    public static String setMkdir(Context context) {
        String filePath = null;
        filePath = CacheConstant.getAppDownloadPath();
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
//            Log.e("file", "目录不存在   创建目录    ");
        } else {
//            Log.e("file", "目录存在");
        }
        return filePath;
    }

    /**
     * 获取路径
     *
     * @return
     * @throws IOException
     */
    public static String getPath(Context context, String url) {
        String path = null;
        try {
            path = FileUtil.setMkdir(context) + File.separator + url.substring(url.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 获取文件的大小
     *
     * @param fileSize 文件的大小
     * @return
     */
    public static String FormetFileSize(int fileSize) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 删除文件及文件下的所有文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            file.delete();
        }
    }

    public static File saveMyBitmap(String bitName, Bitmap mBitmap) {
        File f = new File(bitName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            // DebugMessage.put("在保存图片时出错："+e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 删除监听
     */
    public interface OnDeleteListener {
        public void end();
    }

    /**
     * 删除文件及文件下的所有文件，带监听
     *
     * @param file
     */
    public static void deleteFile(File file, OnDeleteListener listener) {
        OnDeleteListener mOnDeleteListener = listener;
        if (file.isFile()) {
            file.delete();
            mOnDeleteListener.end();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                mOnDeleteListener.end();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFile(childFiles[i]);
            }
            file.delete();
            mOnDeleteListener.end();
        }
    }

    /**
     * 保存的截图地址
     */
    public final static String PATH_SCREEN_DIR = "/Playhit/ScreenImage";
    public final static String SCREEN_FILE_NAME = "screen.png";
    public final static String PATH_SCREEN_FILE = PATH_SCREEN_DIR + "/" + SCREEN_FILE_NAME;

    /**
     * 获取截图目录
     *
     * @return
     */
    public static String getScreenDirPath() {
        return getSDCardPath() + PATH_SCREEN_DIR;
    }

    /**
     * 获取截图路径
     *
     * @return
     */
    public static String getScreenFilePath() {
        return getSDCardPath() + PATH_SCREEN_FILE;
    }

    /**
     * 获取SDCard的目录路径功能
     */
    private static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }
}