package com.netease.nim.uikit.common.framework;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;


import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.netease.nim.uikit.chesscircle.CacheConstant;

public class AppDirUtil {

	//头像缓存
	public final static String HEAD_PATH = "head";
	//歌手图片缓存
	public final static String SINGER_PATH = "singer";
	//Banner图片缓存
	public final static String BANNER_PATH = "banner";
	//礼物图片缓存
	public final static String GIFT_PATH = "gift";
	//用户小头像
	public final static String SmallHEAD_PATH = "smallhead";
	//更新路径
	public final static String APPUPGRADE_PATH = "apkdownload";
	//http cache dir
	public final static String CACHE_HTTP_PATH = "cache_http";
	//
	//dump路径
	public static final String DUMP_PATH = "dump";

	private final static int MB = 1024 * 1024;
	private final static int CACHE_SIZE = 20 * MB;
	public final static int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	private final static long OVER_TIME = 36000;

	Context m_context;

	public AppDirUtil(Context context) {
		m_context = context.getApplicationContext();
	}

	/**
	 * 指定App路径
	 * @return
	 */
	public File getTempCacheDirFile() {
		return makeDir(getCacheDirFile(), AppDirUtil.CACHE_HTTP_PATH);
	}

	public String getCacheDir() {
		String dir = makeDir(getCacheDirFile(), AppDirUtil.CACHE_HTTP_PATH).getAbsolutePath() + "/";
		dealSomeWork(dir);
		return dir;
	}

	public File getDumpDirFile() {
		return makeDir(getAppDirFile(), DUMP_PATH);
	}

	public String getUpgradeDir() {
		String dir = makeDir(getSuitableDirFile(true), APPUPGRADE_PATH).getAbsolutePath() + "/";
		return dir;
	}

	public String getGiftDir() {
		String dir = makeDir(getAppDirFile(), AppDirUtil.GIFT_PATH).getAbsolutePath() + "/";
		dealSomeWork(dir);
		return dir;
	}

	public String getHeadDir() {
		String dir = makeDir(getAppDirFile(), AppDirUtil.HEAD_PATH).getAbsolutePath() + "/";
		dealSomeWork(dir);
		return dir;
	}

	public String getSingerDir() {
		String dir = makeDir(getAppDirFile(), AppDirUtil.SINGER_PATH).getAbsolutePath() + "/";
		dealSomeWork(dir);
		return dir;
	}

	public String getBannerDir() {
		String dir = makeDir(getAppDirFile(), AppDirUtil.BANNER_PATH).getAbsolutePath() + "/";
		dealSomeWork(dir);
		return dir;
	}

	private void dealSomeWork(final String dir) {
		ThreadUtil.Execute(new Runnable() {
			@Override
			public void run() {
				AppDirUtil.this.removeCache(dir);
			}
		});
	}

	public String getAppDir() {
		return getAppDirFile().getAbsolutePath();
	}

	public File getAppDirFile() {
		return makeDir(getSuitableDirFile(true), CacheConstant.APP_FOLDER_NAME);
	}

	public File getAppDirFile(boolean needSd) {
		return makeDir(getSuitableDirFile(needSd), CacheConstant.APP_FOLDER_NAME);
	}

	public File getCacheDirFile(){
		return m_context.getCacheDir();
	}

	public File getSuitableDirFile(boolean needSd) {
		File suitableDir;
		if (needSd) {
			String state = Environment.getExternalStorageState();
			boolean mExternalStorageAvailable, mExternalStorageWriteable;
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;
			} else {
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}
			//如果SD卡可用
			if (mExternalStorageAvailable && mExternalStorageWriteable)
				suitableDir = Environment.getExternalStorageDirectory();
			else//不可用
				suitableDir = m_context.getFilesDir();
		} else { // 不存SD卡
			suitableDir = m_context.getFilesDir();
		}
		return suitableDir;
	}

	private File makeDir(File file, String name) {
		File dataDir = new File(file, name);
		if (!dataDir.isDirectory())
			dataDir.mkdirs();
		return dataDir;
	}

	/**
	 * 计算存储目录下的文件大小，
	 * 当文件总大小大于规定的cache_size或者sdcard剩余空间小于FREE_SD_SPACE_NEEDED_TO_CACHE的规定
	 * 那么删除40%最近没有被使用的文件
	 *
	 * @param dirPath
	 */
	public void removeCache(String dirPath) {
		// System.out.println("removeCache");

		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (null == files || files.length == 0) {
			return;
		}
		int dirSize = 0;
		for (int i = 0; i < files.length; i++) {// 未判断多级目录缓存文件
			dirSize += files[i].length();
		}
		if (dirSize > CACHE_SIZE || FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSD()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifySort());

			// clear some file
			if (removeFactor <= files.length) {
				for (int i = 0; i < removeFactor; i++) {
					files[i].delete();
				}
			}

		}
	}

	/**
	 * 删除过期文件
	 *
	 * @param dirPath
	 * @param filename
	 */
	public static void removeExpiredCache(String dirPath, String filename) {
		if (null == dirPath || null == filename) {
			return;
		}
		File file = new File(dirPath, filename);
		if (System.currentTimeMillis() - file.lastModified() > OVER_TIME) {
			file.delete();
		}
	}

	/**
	 * 计算sdcard上的剩余空间
	 *
	 * @return
	 */
	public static int freeSpaceOnSD() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	class FileLastModifySort implements Comparator<File> {

		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}
