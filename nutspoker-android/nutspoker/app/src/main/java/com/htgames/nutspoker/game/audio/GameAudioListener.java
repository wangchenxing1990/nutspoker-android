package com.htgames.nutspoker.game.audio;

import com.netease.nimlib.sdk.media.record.RecordType;

import java.io.File;

/**
 */
public interface GameAudioListener {
    public void onAudioRecordStart();

    public void onAudioRecordFail();

    public void onAudioRecordCancel();

    public void onAudioRecordIng(int time);

    public void onAudioRecordSuccess(File audioFile, long audioLength, RecordType recordType);
}
