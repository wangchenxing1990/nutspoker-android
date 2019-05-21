package com.htgames.nutspoker.hotupdate;

import android.net.Uri;
import android.text.TextUtils;

import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.model.UpdateInfoEntity;
import com.netease.nim.uikit.common.util.log.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 *
 */
public class HotUpdateConstants {
    public static String TAG = HotUpdateAction.TAG;
//    public static final String FILE_HOST = "http://120.27.162.46:8000/";//热更新服务器：host
    //public static final String FILE_HOST = "http://tcupdate.htgames.cn/";//热更新服务器：host

//    public static final String URL_HOT_UPDATE = HOST + "update/patch/";//热更新
    public static final String URL_HOT_FILE_UPDATE = ApiConfig.getFileHost();//热更新资源前缀
    public static final String URL_HOT_FILE_UPDATE_RES = "/res/";//热更新资源前缀

    public static final String GAME_FOLDER_NAME = "/upd";//游戏热更新包
    public static final String GAME_TEMPORARY_FOLDER_NAME = "/upd_tem";//游戏热更新临时包
    //
//    public static final String URL_HOT_UPDATE_SUFFIX = ".json";
    //
    public static final String KEY_FLIST = "flist";
    public static final String KEY_VERSION = "version";
    //
    public static final String KEY_OLD_VERSION = "oldVer";
    public static final String KEY_NEW_VERSION = "newVer";
    public static final String KEY_DIFFENT = "diff";
    public static final String KEY_FILE_SIZE = "size";
    public static final String KEY_FILE_NAME = "name";
    public static final String KEY_FILE_CODE = "code";
    public static final String KEY_UPDATE_INFO = "info";
    //
    public static final String EXTRA_KEY_DATA = "data";

    //
    public static final String SHUFFLE_32 = "data_1.ever";//COCOS 32位执行文件
    public static final String SHUFFLE_64 = "data_2.ever";//COCOS 64位执行文件

    //热更新目录文件
    public static File getUpdateResFile(String fileName){
        if(TextUtils.isEmpty(fileName)){
            return null;
        }
        String filePath = getUpdateResPath() + File.separator + fileName;
        File file = new File(filePath);
        return file;
    }

    //热更新路径
    public static String getUpdateResPath(){
        return DemoCache.getContext().getFilesDir().getAbsolutePath() + GAME_FOLDER_NAME;
    }

    //热更新临时目录
    public static File getUpdateTemporaryResFile(String fileName){
        if(TextUtils.isEmpty(fileName)){
            return null;
        }
        String filePath = getUpdateTemporaryResPath() + File.separator + fileName;
        File file = new File(filePath);
        return file;
    }

    //热更新临时路径
    public static String getUpdateTemporaryResPath(){
        return DemoCache.getContext().getFilesDir().getAbsolutePath() + HotUpdateConstants.GAME_TEMPORARY_FOLDER_NAME;
    }

    /**
     * 获取热更新文件对应URL
     * @param fileName
     * @return
     */
    public static final String getHotUpdateResUrl(String version , String fileName) {
        String hotUpdateResUr = URL_HOT_FILE_UPDATE + "/" + version + URL_HOT_FILE_UPDATE_RES + fileName;
        LogUtil.i(TAG, hotUpdateResUr);
        return hotUpdateResUr;
    }

    /**
     * 解析热更新的文件
     * @return
     */
    public static UpdateInfoEntity parseUpdateInfoEntity(String data , boolean isArm64) {
        UpdateInfoEntity updateInfoEntity = null;
        try {
            JSONObject dataObj = new JSONObject(data);
            if (dataObj.has(KEY_FLIST) && !TextUtils.isEmpty(dataObj.optString(KEY_FLIST))) {
                String flist = dataObj.optString(KEY_FLIST);
                JSONObject infoObj = new JSONObject(flist);
                updateInfoEntity = new UpdateInfoEntity();
                String oldVersion = infoObj.optString(KEY_OLD_VERSION);
                String newVersion = infoObj.optString(KEY_NEW_VERSION);
                String updateInfo = dataObj.optString(KEY_UPDATE_INFO);
                updateInfoEntity.oldVersion = (oldVersion);
                updateInfoEntity.newVersion = (newVersion);
                updateInfoEntity.updateInfo = (updateInfo);
                if (!oldVersion.equals(newVersion) && infoObj.has(KEY_DIFFENT) && !infoObj.isNull(KEY_DIFFENT)) {
                    ArrayList<UpdateFileEntity> fileList = new ArrayList<>();
                    JSONArray jsonArray = infoObj.optJSONArray(KEY_DIFFENT);
                    if (jsonArray != null) {
                        int size = jsonArray.length();
                        for (int i = 0; i < size; i++) {
                            JSONObject fileJson = jsonArray.optJSONObject(i);
                            UpdateFileEntity updateFileEntity = new UpdateFileEntity();
                            String fileName = fileJson.optString(KEY_FILE_NAME);
//                            if ((isArm64 && fileName.equals(SHUFFLE_32))
//                                    || (!isArm64 && fileName.equals(SHUFFLE_64))) {
//                                //1.64位,过滤掉32位文件  2.32位过滤掉64位文件
//                                continue;
//                            }
                            if(fileName.equals(SHUFFLE_64)) {
                                continue;
                            }
                            if (fileName.contains(" ")) {
                                fileName = Uri.encode(fileName);//encode一下，否则文件名包含空格时下载失败（只对包含空格的文件名进行encode，否则总是下载不全）
                            }
                            updateFileEntity.fileName = (fileName);
                            updateFileEntity.fileSize = (fileJson.optLong(KEY_FILE_SIZE));
                            updateFileEntity.fileCode = (fileJson.optString(KEY_FILE_CODE));
                            fileList.add(updateFileEntity);
                        }
                        updateInfoEntity.diffFileList = (fileList);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateInfoEntity;
    }
}
