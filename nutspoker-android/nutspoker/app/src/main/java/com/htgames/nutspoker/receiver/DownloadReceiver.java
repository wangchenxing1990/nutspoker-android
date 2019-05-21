package com.htgames.nutspoker.receiver;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.hotupdate.service.DownloadAppService;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.util.log.LogUtil;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {
    private DownloadManager downloadManager;
    private SettingsPreferences mSettingsPreferences;
    public final static String ACTION_DOWNLOAD = DemoCache.getContext().getPackageName() + ".version";

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        mSettingsPreferences = SettingsPreferences.getInstance(context);
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        String id_str = String.valueOf(id);

        if (id_str.equals(mSettingsPreferences.getDownloadId())) {
            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Toast.makeText(context, context.getString(R.string.app_download_success), Toast.LENGTH_LONG).show();
                Uri uri = null;
                // 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor cursor = downloadManager.query(query);

                int columnCount = cursor.getColumnCount();
                String path = null; // TODO
                // 这里把所有的列都打印一下，有什么需求，就怎么处理,文件的本地路径就是path
                while (cursor.moveToNext()) {//if (cursor.moveToFirst())
                    for (int j = 0; j < columnCount; j++) {
                        int index_local_uri = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                        String columValue = cursor.getString(index_local_uri);
//                        int index_column_local_filename = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
//                        String column_local_filename = "";//cursor.getString(index_column_local_filename);
//                        int index_uri = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
//                        String uri_index_str = cursor.getString(index_uri);
                        String columnName = cursor.getColumnName(j);//=_id
//                        String columValue = cursor.getString(j);//java.lang.SecurityException: COLUMN_LOCAL_FILENAME is deprecated; use ContentResolver.openFileDescriptor() instead
//                        LogUtil.i("DownloadReceiver", "columnName: " + columnName + " : " + "  "
//                                + "\n index_local_uri: " + index_local_uri + columValue +
//                                "\n index_uri: " + index_uri + uri_index_str
//                                + "\n index_column_local_filename: " + index_column_local_filename + column_local_filename);
                        /*columnName: _id :
                                                                          index_local_uri: 16file:///storage/emulated/0/EverPoker/download/Poker_1.1.5.apk
                                                                          index_uri: 6https://api.everpoker.win/index/download
                                                                          index_column_local_filename: 1*/
                        if (!TextUtils.isEmpty(columValue)) {
                            if (columValue.startsWith("file://")) {
                                path = columValue;
                                uri = Uri.parse(columValue);
                                break;
                            } else if (columValue.startsWith("/data/data/")) {
                                path = columValue;
                                uri = Uri.fromFile(new File(columValue));
                                break;
                            }
                        }
                    }
                }
                cursor.close();
                // 如果sdcard不可用时下载下来的文件，那么这里将是一个内容提供者的路径，这里打印出来，有什么需求就怎么样处理
//				if (path.startsWith("content:")) {
//					cursor = context.getContentResolver().query(
//							Uri.parse(path), null, null, null, null);
//					columnCount = cursor.getColumnCount();
//					while (cursor.moveToNext()) {
//						for (int j = 0; j < columnCount; j++) {
//							String columnName = cursor.getColumnName(j);
//							String string = cursor.getString(j);
//							Log.d("CompleteReceiver", "columnName:"
//									+ columnName + ",string:" + string);
//							if (columnName.equals("_data")) {
//								path = string;
//							}
//						}
//					}
//					cursor.close();
//				}
                if (uri == null) {
                    return;
                }
                LogUtil.i("DownloadTools" , uri.toString());
                Intent intent1 = new Intent();
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.setAction(Intent.ACTION_VIEW);
//			Uri uri = Uri.parse(path);
//			Uri uri = Uri.fromFile(new File(path));
                intent1.setDataAndType(uri, "application/vnd.android.package-archive");
                context.startActivity(intent1);
            } else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                // Toast.makeText(context, "点击通知了....",
                // Toast.LENGTH_LONG).show();
            }
            Intent serviceIntent = new Intent(ChessApp.sAppContext, DownloadAppService.class);
            context.stopService(serviceIntent);
        }
    }
}
