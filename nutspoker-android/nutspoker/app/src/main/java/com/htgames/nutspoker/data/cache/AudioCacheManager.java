package com.htgames.nutspoker.data.cache;

import android.content.Context;

import com.htgames.nutspoker.db.GameAudioDBHelper;
import com.htgames.nutspoker.tool.FileUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.ArrayList;

/**
 * 语音缓存管理
 */
public class AudioCacheManager {
    private final static String TAG = "AudioCacheManager";
    Context context;
    ArrayList<String> cacheFilePathList;

    public AudioCacheManager(Context context) {
        this.context = context;
        cacheFilePathList = new ArrayList<String>();
    }

    public void addAudioFile(IMMessage msg) {
        if (msg.getAttachment() instanceof AudioAttachment) {
            AudioAttachment audioAttachment = (AudioAttachment) msg.getAttachment();
            String path = audioAttachment.getPath();
            GameAudioDBHelper.saveGameAudio(context, path, msg.getTime());
            LogUtil.i(TAG, "addAudioFile :" + path);
        }
    }

    /**
     * 清除游戏录音文件
     */
    public void clearAudioCache() {
        cacheFilePathList = GameAudioDBHelper.getOverdueAudioList(context);
        LogUtil.i(TAG, "getOldAudioFile size :" + (cacheFilePathList == null ? 0 : cacheFilePathList.size()));
        if (cacheFilePathList == null || cacheFilePathList.isEmpty()) {
            return;
        }
        try {
            LogUtil.i(TAG, "clearAudioCache");
            File file = null;
            for (String path : cacheFilePathList) {
                LogUtil.i(TAG, "delete :" + path);
                file = new File(path);
                FileUtil.deleteFile(file);
                GameAudioDBHelper.deleteGameAudio(context, path);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
