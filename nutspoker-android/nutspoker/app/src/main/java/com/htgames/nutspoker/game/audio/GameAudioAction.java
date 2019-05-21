package com.htgames.nutspoker.game.audio;

import android.app.Activity;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.cocos2d.RecordTime;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.ui.base.BaseAction;
import com.netease.nim.uikit.chesscircle.helper.MessageConfigHelper;
import com.netease.nim.uikit.chesscircle.view.AudioConstant;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.io.File;
import java.util.Map;

/**
 * 游戏录音模块
 */
public class GameAudioAction extends BaseAction implements IAudioRecordCallback {
    private final static String TAG = "GameAudioAction";
    private static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;
    public static AudioRecorder audioMessageHelper = null;// 语音
    public static final int MAX_AUDIO_RECORD_TIME_SECOND = 60;//游戏录音最大时间
    public static RecordTime time;
    int gameMode = GameConstants.GAME_MODE_NORMAL;
    private boolean started = false;
    private boolean cancelled = false;
    private boolean touched = false; // 是否按着
    String sessionId;
    String gameCode;
    //
//    protected View audioAnimLayout; // 录音动画布局
//    Button audioRecordBtn;
//    private Chronometer chronometer;
//    private TextView timerTip;
//    private LinearLayout timerTipContainer;
    View ll_match_advance_voice_ing; // 录音动画布局
    ProgressBar probar_voice_timer;
    View btn_match_menu_voice;
    Chronometer tv_voice_timer;
    ImageView iv_start_record;
    TextView tv_voice_timer_tip;
    GameAudioListener mGameAudioListener;

    private static Map<String, Object> remoteExtension;

    public GameAudioAction(Activity activity, View baseView, String sessionId, String gameCode, int gameMode) {
        super(activity, baseView);
        this.sessionId = sessionId;
        this.gameCode = gameCode;
        this.gameMode = gameMode;
        remoteExtension = AudioConstant.getMatchRoomAudioMessageExtension(DemoCache.getAccount(), gameCode);
        initView();
        initRecordTime();
        initAudioRecord();
        initAudioRecordButton();
    }

    public void initRecordTime() {
        time = new RecordTime(ChessApp.sAppContext, 500);
        time.setOnRecordTimeUpdateListener(new RecordTime.OnRecordTimeUpdateListener() {
            @Override
            public void onRecordTimeUpdate(long currentTime) {
                LogUtil.i(TAG, "currentTime : " + currentTime);
                probar_voice_timer.setProgress((int) (currentTime / 1000L));
            }
        });
    }

    private void initView() {
        ll_match_advance_voice_ing = mBaseView.findViewById(R.id.ll_match_advance_voice_ing);
        probar_voice_timer = (ProgressBar) mBaseView.findViewById(R.id.probar_voice_timer);
        tv_voice_timer = (Chronometer) mBaseView.findViewById(R.id.tv_voice_timer);
        tv_voice_timer_tip = (TextView) mBaseView.findViewById(R.id.tv_voice_timer_tip);
        btn_match_menu_voice = mBaseView.findViewById(R.id.start_record_container);
        iv_start_record = (ImageView) mBaseView.findViewById(R.id.iv_start_record);
    }

    private void initAudioRecordButton() {
        btn_match_menu_voice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touched = true;
                    iv_start_record.setImageResource(R.mipmap.room_manager_record_press);
                    initAudioRecord();
                    onStartAudioRecord();
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    iv_start_record.setImageResource(R.mipmap.room_manager_record_normal);
                    touched = false;
                    onEndAudioRecord(isCancelled(v, event));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    iv_start_record.setImageResource(R.mipmap.room_manager_record_press);
                    touched = false;
                    cancelAudioRecord(isCancelled(v, event));
                }
                return true;
            }
        });
    }

    // 上滑取消录音判断
    private static boolean isCancelled(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        if (event.getRawX() < location[0] || event.getRawX() > location[0] + view.getWidth() || event.getRawY() < location[1] - 40 || event.getRawY() > location[1] + view.getHeight()) {
            return true;
        }
        return false;
    }

    /**
     * 初始化AudioRecord
     */
    private void initAudioRecord() {
        LogUtil.i(TAG, "initAudioRecord");
        if (audioMessageHelper == null) {
            audioMessageHelper = new AudioRecorder(ChessApp.sAppContext, RecordType.AAC, MAX_AUDIO_RECORD_TIME_SECOND, this);
        }
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        started = audioMessageHelper.startRecord();
        cancelled = false;
        if (started == false) {
//            Toast.makeText(mActivity, com.netease.nim.uikit.R.string.recording_init_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!touched) {
            return;
        }
        updateTimerTip(false); // 初始化语音动画状态
        playAudioRecordAnim();
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
        mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        audioMessageHelper.completeRecord(cancel);
        stopAudioRecordAnim();
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private void cancelAudioRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }
        cancelled = cancel;
        updateTimerTip(cancel);
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     *
     * @param cancel
     */
    private void updateTimerTip(boolean cancel) {
        if (cancel) {
            tv_voice_timer_tip.setText(com.netease.nim.uikit.R.string.recording_cancel_tip);
            tv_voice_timer_tip.setTextColor(Color.RED);
//            timerTipContainer.setBackgroundResource(com.netease.nim.uikit.R.drawable.nim_cancel_record_red_bg);
        } else {
            tv_voice_timer_tip.setText(com.netease.nim.uikit.R.string.recording_cancel);
            tv_voice_timer_tip.setTextColor(Color.WHITE);
//            timerTipContainer.setBackgroundResource(0);
        }
    }

    /**
     * 开始语音录制动画
     */
    private void playAudioRecordAnim() {
        ll_match_advance_voice_ing.setVisibility(View.VISIBLE);
        time.setBase(SystemClock.elapsedRealtime());
        time.start();
        tv_voice_timer.setBase(SystemClock.elapsedRealtime());
        tv_voice_timer.start();
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudioRecordAnim() {
        ll_match_advance_voice_ing.setVisibility(View.GONE);
        tv_voice_timer.stop();
        tv_voice_timer.setBase(SystemClock.elapsedRealtime());
        time.stop();
        time.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止录音
        if (audioMessageHelper != null) {
            onEndAudioRecord(true);
        }
    }

    public boolean isRecording() {
        return audioMessageHelper != null && audioMessageHelper.isRecording();
    }

    @Override
    public void onRecordReady() {
        // 初始化完成回调，提供此接口用于在录音前关闭本地音视频播放（可选）
        LogUtil.i(TAG, "onRecordReady");
    }

    @Override
    public void onRecordStart(File file, RecordType recordType) {
        // 开始录音回调
        LogUtil.i(TAG, "onRecordStart :" + file.getAbsolutePath());
        //开始录音
        onStartAudioRecord();
        if (mGameAudioListener != null) {
            mGameAudioListener.onAudioRecordStart();
        }
    }

    @Override
    public void onRecordSuccess(File audioFile, long audioLength, RecordType recordType) {
        LogUtil.i(TAG, "onRecordSuccess");
        if (mGameAudioListener != null) {
            mGameAudioListener.onAudioRecordSuccess(audioFile, audioLength, recordType);
        }
    }

    @Override
    public void onRecordFail() {
        // 录音结束，出错
        LogUtil.i(TAG, "onRecordFail");
        if (mGameAudioListener != null) {
            mGameAudioListener.onAudioRecordFail();
        }
    }

    @Override
    public void onRecordCancel() {
        // 录音结束， 用户主动取消录音
        LogUtil.i(TAG, "onRecordCancel");
        if (mGameAudioListener != null) {
            mGameAudioListener.onAudioRecordCancel();
        }
    }

    @Override
    public void onRecordReachedMaxTime(final int maxTime) {
        // 到达指定的最长录音时间
        LogUtil.i(TAG, "到达指定的最长录音时间 ,maxTime : " + maxTime);
        stopAudioRecordAnim();
        EasyAlertDialogHelper.createOkCancelDiolag(mActivity, "", mActivity.getString(com.netease.nim.uikit.R.string.recording_max_time), false, new EasyAlertDialogHelper.OnDialogActionListener() {
            @Override
            public void doCancelAction() {
            }

            @Override
            public void doOkAction() {
                audioMessageHelper.handleEndRecord(true, maxTime);
            }
        }).show();
    }

    //发送录音消息
    public void sendAudioMessage(File audioFile, long audioLength, RecordType recordType) {
        // 录音结束，成功
        if (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG) {
            final IMMessage audioMessage = MessageBuilder.createAudioMessage(sessionId, SessionTypeEnum.Team, audioFile, audioLength);
            audioMessage.setRemoteExtension(remoteExtension);//设置拓展字段
            audioMessage.setConfig(MessageConfigHelper.getGameAudioMessageConfig(audioMessage.getConfig()));
            NIMClient.getService(MsgService.class).sendMessage(audioMessage, false).setCallback(new RequestCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }

                @Override
                public void onFailed(int i) {
                }

                @Override
                public void onException(Throwable throwable) {
                }
            });
        } else if (gameMode == GameConstants.GAME_MODE_MTT || gameMode == GameConstants.GAME_MODE_MT_SNG) {
            //聊天室
            final ChatRoomMessage audioMessage = ChatRoomMessageBuilder.createChatRoomAudioMessage(sessionId, audioFile, audioLength);
            audioMessage.setRemoteExtension(remoteExtension);//设置拓展字段
            audioMessage.setConfig(MessageConfigHelper.getGameAudioMessageConfig(audioMessage.getConfig()));
            NIMClient.getService(ChatRoomService.class).sendMessage(audioMessage, false)
                    .setCallback(null);
        }
    }

    public void setGameAudioListener(GameAudioListener listener) {
        this.mGameAudioListener = listener;
    }
}
