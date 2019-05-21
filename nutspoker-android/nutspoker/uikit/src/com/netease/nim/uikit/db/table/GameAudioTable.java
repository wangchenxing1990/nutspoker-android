package com.netease.nim.uikit.db.table;

import android.provider.BaseColumns;

/**
 * 语音文件缓存
 */
public class GameAudioTable implements BaseColumns {
    public static final String TABLE_GAME_AUDIO = "game_audio";
    public static final String COLUMN_AUDIO_PATH = "audio_path";//语音路径
    public static final String COLUMN_AUDIO_TIME = "audio_time";//语音时间
}
