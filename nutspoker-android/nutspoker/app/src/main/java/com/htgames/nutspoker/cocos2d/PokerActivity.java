package com.htgames.nutspoker.cocos2d;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.chat.app_msg.BuyChipsAction;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.app_msg.tool.AppMessageJsonTools;
import com.htgames.nutspoker.data.cache.AudioCacheManager;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.htgames.nutspoker.game.receiver.CheckInDealReceiver;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.tool.shop.ShopDataCache;
import com.htgames.nutspoker.ui.action.AmountAction;
import com.htgames.nutspoker.ui.action.GameMatchAction;
import com.htgames.nutspoker.ui.action.HandCollectAction;
import com.htgames.nutspoker.ui.action.ShopAction;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.wxapi.WXEntryActivity;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.HostManager;
import com.netease.nim.uikit.api.HttpApi;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.bean.CommonBeanT;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.NetCardCountEy;
import com.netease.nim.uikit.bean.PineappleConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.helper.MessageConfigHelper;
import com.netease.nim.uikit.chesscircle.view.AudioConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.gson.GsonUtils;
import com.netease.nim.uikit.common.media.audioplayer.BaseAudioControl;
import com.netease.nim.uikit.common.media.audioplayer.Playable;
import com.netease.nim.uikit.common.preference.SettingsPreferences;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.util.BaseTools;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nim.uikit.session.audio.MessageAudioControl;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.constant.VerifyType;
import com.netease.nimlib.sdk.friend.model.AddFriendData;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxLuaJavaBridge;
import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.netease.nim.uikit.api.HostManager.getHost;

public class PokerActivity extends Cocos2dxActivity {
    public final static String TAG = "PokerActivity";
    static String hostIPAdress = "0.0.0.0";
    public static int gameActionType;//玩游戏的动作
    //玩游戏
    public static int play_mode;
    public static String token;//Token
    public static String code;//牌局Code
    public static String channel;//牌局Code
    public static String gameMode;//牌局Mode
    public static String gid;//牌局id
    public static String uid;//用户ID
    public static String serverIp;//滚服IP
    public static String sessionId;//普通牌局:游戏群ID ; 俱乐部牌局和比赛牌局:聊天室ID
    private static String imagePath;//图片下载路径
    public static int ko_mode;
    //MT模式观战
    private static String lookTableIndex;//观战的桌子号
    private static String lookUid;//观战的人
    //牌谱播放
    public static String sheetUid;//
    public static String sheetPath;//
    public static String handRecordPath;//手牌记录路径

    ChessApp mChessApp;
    public static AudioRecorder audioMessageHelper = null;// 语音
    private static boolean started = false;
    private static boolean cancelled = false;
    private static int mStartCallBack = 0;//开始录音接口回调
    private static int mStopCallBack = 0;//停止录音接口回调
    private static int mCancelCallback = 0;//取消录音接口回调
    private static int mInterruptCallback = 0;//中断录音接口回调
    private static int mRecordProgressCallback;//录音进度发生变化,参数：当前录制时间
    private static int mBeginPlay = 0;//开始播放接口回调，参数： uid + ":" + 总时间
    private static int mEndPlay = 0;//结束播放接口，参数：uid
    private static int mInterruptPlay = 0;//中断播放接口，参数：uid
    private static int mNotSendCallback;//时间太短提示
    private static int mOnAudioMsg;//收到语音消息接口
    //
    private static int mCheckInMessageCallBack = 0;//报名
    //
    private static MessageAudioControl audioControl;
    private static final int CLICK_TO_PLAY_AUDIO_DELAY = 500;
    private static BaseAudioControl.AudioControlListener onPlayListener;
    private static ArrayList<IMMessage> audioMessageList;//音频播放队列
    private static IMMessage currentMessage = null;//当前的音频文件
    private static Map<String, Object> remoteExtension;
    public static final int MAX_AUDIO_RECORD_TIME_SECOND = 15;//游戏录音最大时间
    public static RecordTime time;
    private static int gameType = GameConstants.GAME_TYPE_GAME;
    private static GameEntity mGameEntity;
    private static PokerActivity instance;
    private static Map<String, IMMessage> historyAudioMessageMap;
    //
    private PowerManager.WakeLock wakeLock = null;
    private BroadcastReceiver batteryLevelRcvr = null;
    private IntentFilter batteryLevelFilter = null;
    private int mBatteryLevel = 0;
    private int mBatteryStatus = 0;
    private int mBatteryListener = 0;
    //
    public static int ACTION_PLAY_GAME = 1;//玩游戏
    public static int ACTION_PLAY_RECORD = 2;//播放牌谱
    public static HandCollectAction mHandCollectAction;
    public static AmountAction mAmountAction;
    public static ShopAction mShopAction;
    public static GameMatchAction mGameMatchAction;
    public BuyChipsAction mBuyChipsAction;
    public static int tableIndex = 0;//牌桌号
    public static String ownerId = "";//创建者ID
    public static int is_admin = 0;//自由局判断是否是管理员
    public static String gameName = "";
    AudioCacheManager mAudioCacheManager;

    //玩游戏
    public static void startGameByPlay(final Context context, final GameEntity gameEntity, final String sessionId, final int ko_mode) {
        if (context == null || gameEntity == null || StringUtil.isSpace(gameEntity.serverIp) || StringUtil.isSpace(gameEntity.code) || StringUtil.isSpace(gameEntity.channel)) {
            Toast.makeText(ChessApp.sAppContext, "信息不全", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtil.i(TAG, "gameEntity.gameMode: " + gameEntity.gameMode + "");
        Intent intent = new Intent(context, PokerActivity.class);
        intent.putExtra(Extras.EXTRA_GAME_ACTION_TYPE, ACTION_PLAY_GAME);
        intent.putExtra(Extras.EXTRA_GAME_MODE, gameEntity.gameMode + "");
        intent.putExtra(Extras.EXTRA_GAME_CODE, gameEntity.code);
        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameEntity.channel);
        intent.putExtra(Extras.EXTRA_GAME_GAMETYPE, gameEntity.type);
        intent.putExtra(Extras.EXTRA_GAME_GAMEID, gameEntity.gid);
        intent.putExtra(Extras.EXTRA_GAME_SESSIONID, sessionId);
        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, gameEntity.serverIp);
        intent.putExtra(Extras.EXTRA_GAME_KO_MODE, ko_mode);
        intent.putExtra(GameConstants.KEY_GAME_IS_ADMIN, gameEntity.is_admin);
        intent.putExtra(GameConstants.KEY_GAME_NAME, gameEntity.name);
        intent.putExtra(GameConstants.KEY_PLAY_MODE, gameEntity.play_mode);
        mGameEntity = gameEntity;
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    //玩游戏（游客观战）
    public static void startGameByWatch(final Context context, final GameEntity gameEntity, final String sessionId, final String lookUid, final String lookTableIndex, final int ko_mode) {
        if (gameEntity == null || StringUtil.isSpace(gameEntity.serverIp) || StringUtil.isSpace(gameEntity.code) || StringUtil.isSpace(gameEntity.channel)) {
            Toast.makeText(context, "信息不全", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(context, PokerActivity.class);
        intent.putExtra(Extras.EXTRA_GAME_ACTION_TYPE, ACTION_PLAY_GAME);
        intent.putExtra(Extras.EXTRA_GAME_MODE, gameEntity.gameMode + "");
        intent.putExtra(Extras.EXTRA_GAME_CODE, gameEntity.code);
        intent.putExtra(Extras.EXTRA_GAME_GAMETYPE, gameEntity.type);
        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, gameEntity.serverIp);
        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameEntity.channel);
        intent.putExtra(Extras.EXTRA_GAME_SESSIONID, sessionId);
        intent.putExtra(Extras.EXTRA_GAME_LOOK_TABLE_INDEX, lookTableIndex);
        intent.putExtra(Extras.EXTRA_GAME_LOOK_UID, lookUid);
        intent.putExtra(Extras.EXTRA_GAME_KO_MODE, ko_mode);
        intent.putExtra(GameConstants.KEY_GAME_NAME, gameEntity.name);
        intent.putExtra(GameConstants.KEY_PLAY_MODE, gameEntity.play_mode);
        mGameEntity = gameEntity;
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    //播放手牌记录
    public static void startGamePlayRecord(final Context context, final String sheetUid, final String sheetPath) {
        Intent intent = new Intent(context, PokerActivity.class);
        intent.putExtra(Extras.EXTRA_GAME_ACTION_TYPE, ACTION_PLAY_RECORD);
        intent.putExtra(Extras.EXTRA_GAME_SHEET_UID, sheetUid);
        intent.putExtra(Extras.EXTRA_GAME_SHEET_PATH, sheetPath);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.hideSystemUI();
        LogUtil.i(TAG, "onCreate");
        initFolder();
        mChessApp = (ChessApp) getApplication();
        historyAudioMessageMap = new HashMap<>();
        initData();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerMsgReceiveObserver(true);
                initAction();
                mAudioCacheManager = new AudioCacheManager(getApplicationContext());
                initRecordTime();
                initAudioControl();
                initAudioRecord();
                mChessApp.setGameIng(true);
                monitorBatteryState();//
            }
        });
    }

    private void initAction() {
        mHandCollectAction = new HandCollectAction(instance, null);
        mAmountAction = new AmountAction(instance, null);
        mShopAction = new ShopAction(instance, null);
        mGameMatchAction = new GameMatchAction(this, null);
        mBuyChipsAction = new BuyChipsAction(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (instance == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UmengAnalytics.onResume(getApplicationContext());
                LogUtil.i(TAG, "onResume");
                acquireWakeLock();//
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (instance == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UmengAnalytics.onPause(getApplicationContext());
                releaseWakeLock();
                LogUtil.i(TAG, "onPause");
                // 停止录音
                if (audioMessageHelper != null) {
                    onEndAudioRecord(true, false);
                }
            }
        });

        if (isFinishing()) {
            clearGame();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onDestroy");

    }

    private void clearGame() {
        LogUtil.i(TAG, "clear game");
        mChessApp.setGameIng(false);
        registerMsgReceiveObserver(false);
        if (!TextUtils.isEmpty(collectUrl)) {
            ChessApp.sRequestQueue.cancelAll(collectUrl);
        }
        if (!TextUtils.isEmpty(unCollectUrl)) {
            ChessApp.sRequestQueue.cancelAll(unCollectUrl);
        }
        if (batteryLevelRcvr != null) {
            unregisterReceiver(batteryLevelRcvr);
        }
        if (mHandCollectAction != null) {
            mHandCollectAction.onDestroy();
            mHandCollectAction = null;
        }
        if (mAmountAction != null) {
            mAmountAction.onDestroy();
            mAmountAction = null;
        }
        if (mShopAction != null) {
            mShopAction.onDestroy();
            mShopAction = null;
        }
        if (mGameMatchAction != null) {
            mGameMatchAction.onDestroy();
            mGameMatchAction = null;
        }
        if (mBuyChipsAction != null) {
            mBuyChipsAction.onDestroy();
            mBuyChipsAction = null;
        }
        if (mAudioCacheManager != null) {
            mAudioCacheManager = null;
        }
        if (mShareView != null) {
            mShareView.onDestroy();
            mShareView = null;
        }
        LocationManager locationManager = (LocationManager) instance.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
        instance = null;
    }

    //收到报名消息给游戏
    AppMessageReceiver mAppMessageReceiver = new AppMessageReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent != null) {
                AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                if (appMessage != null) {
                    if (appMessage.type == AppMessageType.MatchBuyChips
                            && appMessage.attachObject instanceof MatchBuyChipsNotify) {
                        MatchBuyChipsNotify matchBuyChipsNotify = (MatchBuyChipsNotify) appMessage.attachObject;
                        String messageGameCode = matchBuyChipsNotify.gameCode;
                        if (!TextUtils.isEmpty(messageGameCode)
                                && messageGameCode.equals(code) && matchBuyChipsNotify.buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                            //收到报名请求，并且是报名
                            String appMessageContent = AppMessageJsonTools.packageAppMessage(appMessage);
                            if (!TextUtils.isEmpty(appMessageContent)) {
                                LogUtil.i(TAG, appMessageContent);
                                LogUtil.i(TAG, "mAppMessageReceiver : " + mCheckInMessageCallBack);
                                doLuaFunction(mCheckInMessageCallBack, appMessageContent);
                            }
                        }
                    }
                }
            }
        }
    };

    //控制带入（如果是比赛模式的报名，走mGameMatchAction ， 如果是普通模式或者比赛模式的赠购买走mBuyChipsAction）
    public static void playerSignUpRep(final int dealAction, final String appMessageAttach) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AppMessage appMessage = AppMessageJsonTools.parseAppMessage(appMessageAttach);
                if (appMessage.type == AppMessageType.MatchBuyChips && appMessage.attachObject instanceof MatchBuyChipsNotify) {
                    int buyType = ((MatchBuyChipsNotify) appMessage.attachObject).buyType;
                    if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                        mGameMatchAction.controlCheckIn(appMessage, dealAction, false, new GameRequestCallback() {
                            @Override
                            public void onSuccess(org.json.JSONObject response) {
                                AppMessageStatus status = (dealAction == GameMatchAction.ACTION_AGREE ? AppMessageStatus.passed : AppMessageStatus.declined);
                                AppMsgDBHelper.setSystemMessageStatus(instance.getApplicationContext(), appMessage.type, appMessage.checkinPlayerId, appMessage.key, status);
                                LogUtil.i(TAG, "playerSignUpRep : onSuccess");
                                notifyCheckInDealResult(appMessage);
                            }

                            @Override
                            public void onFailed(int code, org.json.JSONObject response) {
                                LogUtil.i(TAG, "playerSignUpRep : onFailed");
                                if (!ApiResultHelper.isMatchBuychipsInvalid(code)
                                        && !TextUtils.isEmpty(appMessageAttach)) {
                                    //操作失败，并且消息还有效，继续弹
                                    LogUtil.i(TAG, appMessageAttach);
                                    LogUtil.i(TAG, "mAppMessageReceiver : " + mCheckInMessageCallBack);
                                    doLuaFunction(mCheckInMessageCallBack, appMessageAttach);
                                } else {
                                    notifyCheckInDealResult(appMessage);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //通知报名结果处理
    public static void notifyCheckInDealResult(AppMessage appMessage) {
        Intent intent = new Intent();
        intent.setAction(CheckInDealReceiver.ACTION_CHECKIN);
        intent.putExtra(Extras.EXTRA_APP_MESSAGE, appMessage);
        instance.sendBroadcast(intent);
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "sgspad_lock");
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    //保持屏幕常亮：lua调用
    static public void keepScreenOn(final boolean enable) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enable) {
                    instance.acquireWakeLock();
                } else {
                    instance.releaseWakeLock();
                }
            }
        });
    }

    //设置
    public static void setGameSoundEnable(final boolean enable) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SettingsPreferences.getInstance(DemoCache.getContext()).setGameSound(enable);
            }
        });
    }

    public static void setAutoMuckEnable(final int enable) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SettingsPreferences.getInstance(DemoCache.getContext()).setGameAutoMuck(enable);
            }
        });
    }

    public static int getAutoMuckEnable() {
        return SettingsPreferences.getInstance(DemoCache.getContext()).isGameAutoMuck();
    }

    public static void setCardCate(final int cate) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SettingsPreferences.getInstance(DemoCache.getContext()).setGameCardCate(cate);
            }
        });
    }

    public static int getCardCate() {
        return SettingsPreferences.getInstance((DemoCache.getContext())).getGameCardCate();
    }

    public static void registerBatteryListener(final int listener) {
        instance.mBatteryListener = listener;
        if (listener != 0 && instance.mBatteryLevel > 0) {
            final String strInfo = "{\"level\":" + instance.mBatteryLevel + ", \"status\":" + instance.mBatteryStatus + "}";
            instance.runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    Cocos2dxLuaJavaBridge.callLuaFunctionWithString(listener, strInfo);
                }
            });
        }
    }


    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                if (rawlevel >= 0 && scale > 0) {
                    mBatteryLevel = (rawlevel * 100) / scale;
                }
                //未识别 0  未充电1 充电中2 充满 3
                if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
                    mBatteryStatus = 0;
                } else if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    mBatteryStatus = 2;
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                    mBatteryStatus = 1;
                } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    mBatteryStatus = 1;
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    mBatteryStatus = 3;
                } else {
                    mBatteryStatus = 0;
                }
                final String strInfo = "{\"level\":" + mBatteryLevel + ", \"status\":" + mBatteryStatus + "}";
                if (mBatteryListener != 0) {
                    runOnGLThread(new Runnable() {
                        @Override
                        public void run() {
                            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mBatteryListener, strInfo);
                        }
                    });
                }
            }
        };

        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }

    public void initFolder() {
        try {
            imagePath = CacheConstant.getGameImageCachePath();
            handRecordPath = CacheConstant.getAppHandRecordPath();
            File iamgeFile = new File(imagePath);
            File recordFile = new File(handRecordPath);
            if (!iamgeFile.exists()) {
                iamgeFile.mkdirs();
            }
            if (!recordFile.exists()) {
                recordFile.mkdirs();
            }
            LogUtil.i(TAG, "imagePath:" + imagePath);
        } catch (Exception e) {
        }
    }

    public void initRecordTime() {
        time = new RecordTime(getApplicationContext(), 500);
        time.setOnRecordTimeUpdateListener(new RecordTime.OnRecordTimeUpdateListener() {
            @Override
            public void onRecordTimeUpdate(long currentTime) {
                doLuaFunction(mRecordProgressCallback, String.valueOf(currentTime));
                LogUtil.i(TAG, "currentTime : " + currentTime);
            }
        });
    }

    public void initData() {
        gameActionType = getIntent().getIntExtra(Extras.EXTRA_GAME_ACTION_TYPE, ACTION_PLAY_GAME);
        if (gameActionType == ACTION_PLAY_GAME) {
            token = UserPreferences.getInstance(getApplicationContext()).getUserToken();
            uid = UserPreferences.getInstance(getApplicationContext()).getUserId();
            gameMode = getIntent().getStringExtra(Extras.EXTRA_GAME_MODE);
            gid = getIntent().getStringExtra(Extras.EXTRA_GAME_GAMEID);
            code = getIntent().getStringExtra(Extras.EXTRA_GAME_CODE);
            channel = getIntent().getStringExtra(Extras.EXTRA_GAME_CHANNEL);
            sessionId = getIntent().getStringExtra(Extras.EXTRA_GAME_SESSIONID);
            gameType = getIntent().getIntExtra(Extras.EXTRA_GAME_GAMETYPE, GameConstants.GAME_TYPE_GAME);
            serverIp = getIntent().getStringExtra(Extras.EXTRA_GAME_SERVERIP);
            audioMessageList = new ArrayList<IMMessage>();
            remoteExtension = AudioConstant.getGameAudioMessageExtension(uid, code, tableIndex);
            LogUtil.i(TAG, gameType == GameConstants.GAME_TYPE_GAME ? "私人牌局" : "俱乐部牌局");
            lookTableIndex = getIntent().getStringExtra(Extras.EXTRA_GAME_LOOK_TABLE_INDEX);
            lookUid = getIntent().getStringExtra(Extras.EXTRA_GAME_LOOK_UID);
            ko_mode = getIntent().getIntExtra(Extras.EXTRA_GAME_KO_MODE, 0);
            is_admin = getIntent().getIntExtra(GameConstants.KEY_GAME_IS_ADMIN, 0);
            gameName = getIntent().getStringExtra(GameConstants.KEY_GAME_NAME);
            play_mode = getIntent().getIntExtra(GameConstants.KEY_PLAY_MODE, GameConstants.PLAY_MODE_TEXAS_HOLDEM);
        } else if (gameActionType == ACTION_PLAY_RECORD) {
            sheetPath = getIntent().getStringExtra(Extras.EXTRA_GAME_SHEET_PATH);
            sheetUid = getIntent().getStringExtra(Extras.EXTRA_GAME_SHEET_UID);
        }
    }

    //是否是好友：lua层调用
    public static int isMyFriend(String uid) {
        boolean isFriend = NIMClient.getService(FriendService.class).isMyFriend(uid);
        return isFriend ? 1 : 0;
    }

    //添加好友：lua层调用
    public static void addFriend(final String uid) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                VerifyType verifyType = VerifyType.VERIFY_REQUEST; // 发起好友验证请求
                String nickname = "";
                if (NimUserInfoCache.getInstance().hasUser(DemoCache.getAccount())) {
                    nickname = NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount());
                } else {
                    nickname = "";
                }
                String msg = String.format(instance.getString(R.string.verify_send_message), nickname);
                NIMClient.getService(FriendService.class).addFriend(new AddFriendData(uid, verifyType, msg))
                        .setCallback(new RequestCallback<Void>() {
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
            }
        });
    }

    //执行LUA代码
    private static void doLuaFunction(final int luaFunctionId, final String params) {
        if (luaFunctionId <= 0 || instance == null) {
            return;
        }
        instance.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxLuaJavaBridge.callLuaFunctionWithString(luaFunctionId, params);
            }
        });
    }

    //释放lua回调
    private static void releaseLuaFunction(final int luaFunctionId) {
        if (luaFunctionId <= 0 || instance == null) {
            return;
        }
        instance.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxLuaJavaBridge.releaseLuaFunction(luaFunctionId);
            }
        });
    }

    public void initAudioControl() {
        LogUtil.i(TAG, "initAudioControl");
        audioControl = MessageAudioControl.getInstance(getApplicationContext());
        audioControl.setEarPhoneModeEnable(false);// true:听筒模式/扬声器模式
        onPlayListener = new BaseAudioControl.AudioControlListener() {
            @Override
            public void onAudioControllerReady(Playable playable) {
                //AudioControl准备就绪，已经postDelayed playRunnable，不等同于AudioPlayer已经开始播放
                LogUtil.i(TAG, "onAudioControllerReady");
            }

            @Override
            public void onEndPlay(Playable playable) {
                //结束播放
                LogUtil.i(TAG, "onEndPlay");
//                    updateTime(playable.getDuration());
                if (currentMessage != null && audioMessageList.contains(currentMessage)) {
                    if (currentMessage.getRemoteExtension() != null && currentMessage.getRemoteExtension().containsKey(AudioConstant.KEY_AUDIO_UID)) {
                        doLuaFunction(mEndPlay, (String) currentMessage.getRemoteExtension().get(AudioConstant.KEY_AUDIO_UID));
                    }
                    historyAudioMessageMap.put(currentMessage.getFromAccount(), currentMessage);//保存每个用户最后一条录音
                    audioMessageList.remove(currentMessage);
//                    deleteChattingHistory(currentMessage);//删除
                    currentMessage = null;
                }
                startPlaySoundList();
            }

            @Override
            public void updatePlayingProgress(Playable playable, long curPosition) {
                //显示播放过程中的进度条，当前进度，如果传-1则自动获取进度
                LogUtil.i(TAG, "updatePlayingProgress ：" + curPosition);
                if (curPosition > playable.getDuration()) {
                    return;
                }
            }
        };
    }

    private void controlPlaying(IMMessage message) {
        final AudioAttachment msgAttachment = (AudioAttachment) message.getAttachment();
        long duration = msgAttachment.getDuration();
        if (!isMessagePlaying(audioControl, message)) {
            if (audioControl.getAudioControlListener() != null
                    && audioControl.getAudioControlListener().equals(onPlayListener)) {
                audioControl.changeAudioControlListener(null);
            }
//            updateTime(duration);
//            stop();
        } else {
            audioControl.changeAudioControlListener(onPlayListener);
//            play();
        }
    }

    public static void initLuaCallBack(final int startCallBack, final int stopCallBack, final int cancelCallback, final int interruptCallback, final int beginPlay,
                                       final int endPlay, final int interruptPlay, final int notSendCallback, final int progressCallback, final int onAudioMsg) {
        LogUtil.i(TAG, "initLuaCallBack");
        mStartCallBack = startCallBack;
        mStopCallBack = stopCallBack;
        mCancelCallback = cancelCallback;
        mInterruptCallback = interruptCallback;
        mBeginPlay = beginPlay;
        mEndPlay = endPlay;
        mInterruptPlay = interruptPlay;
        mNotSendCallback = notSendCallback;
        mRecordProgressCallback = progressCallback;
        mOnAudioMsg = onAudioMsg;
        Cocos2dxLuaJavaBridge.retainLuaFunction(beginPlay);
    }


    //初始化比赛接口回调
    public static void initMatchLuaCallBack(final int checkInMessageCallBack) {
        LogUtil.i(TAG, "initMatchLuaCallBack");
        mCheckInMessageCallBack = checkInMessageCallBack;
    }

    /**
     * 开启新消息通知变化
     */
    public void registerMsgReceiveObserver(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(statusObserver, register);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
//        NIMClient.getService(MsgServiceObserve.class).observeMsgStatus(chatroomStatusObserver, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeMsgStatus(chatroomStatusObserver, register);
        if (register) {
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
        } else {
            unregisterReceiver(mAppMessageReceiver);
        }
    }

    // 监听消息状态变化,多媒体文件收到之后，会进行自动下载或手动下载。需要在下载完成之后，才能获取到多媒体文件路径，并刷新界面。通过监听消息状态的变化，来查看是否下载完成
    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage msg) {
            // 1、根据sessionId判断是否是自己的消息 2、更改内存中消息的状态 3、刷新界面
            if (msg.getSessionId().equals(sessionId) && msg.getDirect() == MsgDirectionEnum.In && msg.getAttachStatus() == AttachStatusEnum.transferred) {
                //是当前牌局的语音消息，加入队列
                if (AudioConstant.isGameAudioMessage(msg, code)) {
                    LogUtil.i(TAG, "下载成功：" + msg.getUuid());
//                    audioMessageList.add(msg);
                    deleteChattingHistory(msg);//删除
//                    startPlaySoundList();
                    //
                    if (msg.getAttachment() instanceof AudioAttachment) {
                        AudioAttachment attachment = (AudioAttachment) msg.getAttachment();
                        String path = attachment.getPath();
                        String fromAccount = msg.getFromAccount();
                        String duration = String.valueOf(attachment.getDuration());
                        JSONObject data = new JSONObject();
                        data.put("path", path);
                        data.put("from", fromAccount);
                        data.put("duration", duration);
                        data.put("sendtime", msg.getTime());
                        LogUtil.i(TAG, data.toJSONString());
                        doLuaFunction(mOnAudioMsg, data.toJSONString());
                        mAudioCacheManager.addAudioFile(msg);//添加游戏录音到缓存
                    }
                }
            }
        }
    };

    // 监听消息状态变化,多媒体文件收到之后，会进行自动下载或手动下载。需要在下载完成之后，才能获取到多媒体文件路径，并刷新界面。通过监听消息状态的变化，来查看是否下载完成
    Observer<ChatRoomMessage> chatroomStatusObserver = new Observer<ChatRoomMessage>() {
        @Override
        public void onEvent(ChatRoomMessage msg) {
            // 1、根据sessionId判断是否是自己的消息 2、更改内存中消息的状态 3、刷新界面
            LogUtil.i(TAG, "chatroomStatusObserver" + msg.getUuid());
            if (msg.getSessionId().equals(sessionId) && msg.getDirect() == MsgDirectionEnum.In && msg.getAttachStatus() == AttachStatusEnum.transferred) {
                //是当前牌局的语音消息,或者创建者语音，加入队列
                if (AudioConstant.isMatchAudioMessage(msg, code, tableIndex) || AudioConstant.isMatchOwnerAudioMessage(msg, code, ownerId, tableIndex)) {
                    LogUtil.i(TAG, "下载成功：" + msg.getUuid());
                    if (msg.getAttachment() instanceof AudioAttachment) {
                        AudioAttachment attachment = (AudioAttachment) msg.getAttachment();
                        String path = attachment.getPath();
                        String fromAccount = msg.getFromAccount();
                        String duration = String.valueOf(attachment.getDuration());
                        JSONObject data = new JSONObject();
                        data.put("path", path);
                        data.put("from", fromAccount);
                        data.put("duration", duration);
                        data.put("isOnwer", AudioConstant.isMatchOwnerAudioMessage(msg, code, ownerId, tableIndex) ? 1 : 0);
                        data.put("sendtime", msg.getTime());
                        LogUtil.i(TAG, data.toJSONString());
                        doLuaFunction(mOnAudioMsg, data.toJSONString());
                    }
                }
            }
        }
    };

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (final IMMessage imMessage : messages) {
                if (imMessage.getSessionId().equals(sessionId)) {
                    //是当前牌局的语音消息，加入队列
                    if (AudioConstant.isGameAudioMessage(imMessage, code)) {
                        LogUtil.i(TAG, "收到新的音频消息 IMMessage：" + imMessage.getUuid());
                    }
                    LogUtil.i(TAG, "收到新的音频消息：" + imMessage.getSessionType().getValue());
                }
            }
        }
    };

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            // 处理新收到的消息
            LogUtil.i(TAG, "ChatRoomMessage messages size : " + messages.size() + ":sessionId :" + sessionId);
            for (ChatRoomMessage imMessage : messages) {
                if (imMessage.getSessionId().equals(sessionId)) {
                    LogUtil.i(TAG, "message :" + /*imMessage.getAttachment() +有时候null point exception*/ ";tableIndex:" + tableIndex);
                    //是当前牌局的语音消息，加入队列
                    if (AudioConstant.isGameAudioMessage(imMessage, code)) {
                        LogUtil.i(TAG, "收到新的音频消息：" + imMessage.getUuid());
                    }
                }
            }
        }
    };

    /**
     * 播放录音队列
     */
    public static void startPlaySoundList() {
        if (currentMessage != null && isMessagePlaying(audioControl, currentMessage)) {
            LogUtil.i(TAG, "startPlaySoundList：有音频在播放中");
            return;
        }
        if (audioMessageList != null && audioMessageList.size() != 0) {
            LogUtil.i(TAG, "播放录音队列，音频队列不为空");
            //音频队列不为空
            currentMessage = audioMessageList.get(0);
            final AudioAttachment attachment = (AudioAttachment) currentMessage.getAttachment();
            MsgStatusEnum status = currentMessage.getStatus();
            final AttachStatusEnum attachStatus = currentMessage.getAttachStatus();
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    if (currentMessage.getDirect() == MsgDirectionEnum.In && attachStatus == AttachStatusEnum.transferred) {
                    //1.判断消息方向，是否是接收到的消息   2.是否已经完成
                    startPalySound(currentMessage);
//                    }
                }
            }, 100L);
        }
    }

    /**
     * 初始化AudioRecord
     */
    public static void initAudioRecord() {
        LogUtil.i(TAG, "initAudioRecord");
//        if (audioMessageHelper == null) {
        LogUtil.i(TAG, "audioMessageHelper == null");
        audioMessageHelper = new AudioRecorder(instance.getApplicationContext(), RecordType.AAC, MAX_AUDIO_RECORD_TIME_SECOND, new IAudioRecordCallback() {
            // 录音状态回调
            @Override
            public void onRecordReady() {
                // 初始化完成回调，提供此接口用于在录音前关闭本地音视频播放（可选）
                LogUtil.i(TAG, "onRecordReady");
            }

            @Override
            public void onRecordStart(File file, RecordType recordType) {
                // 开始录音回调
                LogUtil.i(TAG, "onRecordStart :" + file.getAbsolutePath());
                doLuaFunction(mStartCallBack, "");//开始录音
            }

            @Override
            public void onRecordSuccess(final File audioFile, final long audioLength, RecordType recordType) {
                // 录音结束，成功
                instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        int gameModeInt = Integer.parseInt(gameMode);
                        //如果是私人牌局，并且是普通牌局或者普通SNG
//                        if (gameType == GameConstants.GAME_TYPE_GAME && (gameModeInt == GameConstants.GAME_MODE_NORMAL || gameModeInt == GameConstants.GAME_MODE_SNG)) {
//                            final IMMessage audioMessage = MessageBuilder.createAudioMessage(sessionId, SessionTypeEnum.Team, audioFile, audioLength);
//                            audioMessage.setRemoteExtension(remoteExtension);//设置拓展字段
//                            audioMessage.setConfig(MessageConfigHelper.getGameAudioMessageConfig(audioMessage.getConfig()));
//                            NIMClient.getService(MsgService.class).sendMessage(audioMessage, false).setCallback(new RequestCallback<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    deleteChattingHistory(audioMessage);
//                                }
//                                @Override
//                                public void onFailed(int i) {
//                                    deleteChattingHistory(audioMessage);
//                                }
//                                @Override
//                                public void onException(Throwable throwable) {
//                                    deleteChattingHistory(audioMessage);
//                                }
//                            });
//                        } else if (gameType == GameConstants.GAME_TYPE_CLUB || gameModeInt == GameConstants.GAME_MODE_MTT || gameModeInt == GameConstants.GAME_MODE_MT_SNG) {
//                            //聊天室
//                            final ChatRoomMessage audioMessage = ChatRoomMessageBuilder.createChatRoomAudioMessage(sessionId, audioFile, audioLength);
//                            audioMessage.setRemoteExtension(remoteExtension);//设置拓展字段
//                            audioMessage.setConfig(MessageConfigHelper.getGameAudioMessageConfig(audioMessage.getConfig()));
//                            NIMClient.getService(ChatRoomService.class).sendMessage(audioMessage, false).setCallback(null);
//                        }
                        final ChatRoomMessage audioMessage = ChatRoomMessageBuilder.createChatRoomAudioMessage(sessionId, audioFile, audioLength);
                        audioMessage.setRemoteExtension(remoteExtension);//设置拓展字段
                        audioMessage.setConfig(MessageConfigHelper.getGameAudioMessageConfig(audioMessage.getConfig()));
                        NIMClient.getService(ChatRoomService.class).sendMessage(audioMessage, false).setCallback(null);
                    }
                });
            }

            @Override
            public void onRecordFail() {
                // 录音结束，出错
                doLuaFunction(mInterruptCallback, "");
                LogUtil.i(TAG, "onRecordFail");
            }

            @Override
            public void onRecordCancel() {
                // 录音结束， 用户主动取消录音
                doLuaFunction(mCancelCallback, "");
                LogUtil.i(TAG, "onRecordCancel");
            }

            @Override
            public void onRecordReachedMaxTime(final int maxTime) {
                // 到达指定的最长录音时间
                LogUtil.i(TAG, "到达指定的最长录音时间 ,maxTime : " + maxTime + "  instance: " + instance);
                if (instance == null) {
                    return;
                }
                instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (audioMessageHelper != null) {
                            audioMessageHelper.handleEndRecord(true, maxTime);
                        }
                        onEndAudioRecord(false, false);
                    }
                });
//                    stopRecordSound();
//                    stopAudioRecordAnim();
//                    com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.createOkCancelDiolag(container.activity, "", container.activity.getString(com.netease.nim.uikit.R.string.recording_max_time), false, new com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper.OnDialogActionListener() {
//                        @Override
//                        public void doCancelAction() {
//                        }
//
//                        @Override
//                        public void doOkAction() {
//                            audioMessageHelper.handleEndRecord(true, maxTime);
//                        }
//                    }).show();
            }
        });
//        }
    }

    public static void deleteChattingHistory(IMMessage imMessage) {
        if (gameType == GameConstants.GAME_TYPE_CLUB) {
            //如果游戏类型是俱乐部/圈子牌局，删除本地（发送者：发送成功删除；接受者：播放成功删除）
            NIMClient.getService(MsgService.class).deleteChattingHistory(imMessage);
        }
    }

    //是否在录音
    public static boolean isRecording() {
        return audioMessageHelper != null && audioMessageHelper.isRecording();
    }

    public static boolean isMessagePlaying(MessageAudioControl audioControl, IMMessage message) {
        if (audioControl.getPlayingAudio() != null && audioControl.getPlayingAudio().isTheSame(message)) {
            return true;
        } else {
            return false;
        }
    }

    private static PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            if (requestCode == PermissionUtils.CODE_RECORD_AUDIO) {
                LogUtil.i(TAG, "startRecordSound");
//        initAudioRecord();
                instance.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onStartAudioRecord();
                    }
                });
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    //开始录音
    public static void startRecordSound() {
        PermissionUtils.requestPermission(instance, PermissionUtils.CODE_RECORD_AUDIO, mPermissionGrant);
    }

    //结束录音
    public static void stopRecordSound() {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "stopRecordSound recording:" + isRecording());
                if (!isRecording()) {
                    return;
                }
                //时间太短，停止录音
                if (time != null) {
                    if (time.getCurrentRecordTime() < 700) {
                        LogUtil.i(TAG, "录音时间太短 : " + time.getCurrentRecordTime());
                        onEndAudioRecord(true, true);
                    } else {
                        onEndAudioRecord(false, false);
                    }
                }
            }
        });
    }

    /**
     * 结束语音录制
     *
     * @param cancel      是否是取消
     * @param isTimeShort 是否是时间太短
     */
    private static void onEndAudioRecord(final boolean cancel, final boolean isTimeShort) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 取消录音
                if (audioMessageHelper != null) {
                    audioMessageHelper.completeRecord(cancel);
                }
                time.stop();
                time.setBase(SystemClock.elapsedRealtime());
                if (cancel) {
                    // 停止录音
                    LogUtil.i(TAG, "停止录音，不发送");
                    if (isTimeShort) {
                        doLuaFunction(mNotSendCallback, "");
                    } else {
                        doLuaFunction(mCancelCallback, "");
                    }
                } else {
                    LogUtil.i(TAG, "停止录音，发送");
                    doLuaFunction(mStopCallBack, "");
                }
            }
        });
    }

    //取消录音
    public static void cancelRecordSound() {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "cancelRecordSound");
                if (audioMessageHelper != null && isRecording()) {
                    onEndAudioRecord(true, false);
                }
                cancelAudioRecord(true);
            }
        });
    }

    //播放播放录音
    public static int startPalySound(IMMessage message) {
        LogUtil.i(TAG, "startPalySound");
        int recordDuration = 0;
        if (audioControl != null) {
            LogUtil.i(TAG, "audioControl != null");
            if (message.getDirect() == MsgDirectionEnum.In && message.getAttachStatus() != AttachStatusEnum.transferred) {
                //还未传输成功
                LogUtil.i(TAG, "0");
                return 0;
            }
            if (message.getAttachment() instanceof AudioAttachment) {
                final AudioAttachment attachment = (AudioAttachment) currentMessage.getAttachment();
                Map<String, Object> remoteExtension = message.getRemoteExtension();
                if (remoteExtension != null && remoteExtension.containsKey(AudioConstant.KEY_AUDIO_UID)) {
                    String audioText = remoteExtension.get(AudioConstant.KEY_AUDIO_UID) + ":" + attachment.getDuration();
                    LogUtil.i(TAG, audioText);
                    LogUtil.i(TAG, "mBeginPlay :" + mBeginPlay);
//                    doLuaFunction(mBeginPlay, "");//提醒COCOS播放的是谁的录音
                    doLuaFunction(mBeginPlay, audioText);//提醒COCOS播放的是谁的录音
                }
            }
            audioControl.startPlayAudioDelay(CLICK_TO_PLAY_AUDIO_DELAY, message, onPlayListener);
            LogUtil.i(TAG, "播放的音频消息：" + message.getUuid());
//            audioControl.setPlayNext(true, adapter, message);
        } else {
            LogUtil.i(TAG, "audioControl == null");
        }
        return recordDuration;
    }

    //中断播放录音
    public static void interruptPalySound() {
        LogUtil.i(TAG, "interruptPalySound");
        doLuaFunction(mBeginPlay, currentMessage.getFromAccount());
    }

    //结束播放录音
    public static void stopPalySound() {
        LogUtil.i(TAG, "stopPalySound");
    }

    /**
     * 开始语音录制
     */
    private synchronized static void onStartAudioRecord() {
        if (audioMessageHelper == null) {
            initAudioRecord();
        }
        started = audioMessageHelper.startRecord();//可能报错
        cancelled = false;
        LogUtil.i(TAG, "started :" + started);
        if (started == false) {
            onEndAudioRecord(true, false);
            return;
        }
        LogUtil.i(TAG, "isRecording :" + isRecording());
        if (!isRecording()) {
            return;
        }
        time.setBase(SystemClock.elapsedRealtime());
        time.start();
    }

    /**
     * 取消语音录制
     *
     * @param cancel
     */
    private static void cancelAudioRecord(boolean cancel) {
        // reject
        if (!started) {
            return;
        }
        // no change
        if (cancelled == cancel) {
            return;
        }
        cancelled = cancel;
    }

    //回收录音
    public void reclaim() {
        if (audioControl.getAudioControlListener() != null && audioControl.getAudioControlListener().equals(onPlayListener)) {
            audioControl.changeAudioControlListener(null);
        }
    }

    //获取好友信息(是好友并且有缓存)
    public static String getFriendInfo(String uid) {
        if (NIMClient.getService(FriendService.class).isMyFriend(uid) && NimUserInfoCache.getInstance().hasUser(uid)) {
            NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(uid);
            String avatar = userInfo.getAvatar();
            String nickname = NimUserInfoCache.getInstance().getAlias(uid);
            if (TextUtils.isEmpty(nickname)) {
                nickname = userInfo.getName();
            }
            JSONObject data = new JSONObject();
            data.put("avatar", avatar);//
            data.put("nickname", nickname);//
            LogUtil.i(TAG, "getFriendInfo :" + data.toJSONString());
            return data.toJSONString();
        }
        LogUtil.i(TAG, "getFriendInfo :" + "");
        return "";
    }

    //获取非好友信息
    public static void otherUserInfo(String uidsJson, final int callback) {
        try {
            org.json.JSONObject info = new org.json.JSONObject(uidsJson);
            LogUtil.i(TAG, uidsJson);

            List<String> uids = new ArrayList<>();
            Iterator keys = info.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                uids.add(info.getString(key));
            }

            final List<NimUserInfo> users = NIMClient.getService(UserService.class).getUserInfoList(uids);
            for (NimUserInfo user : users) {
                if (user.getName() != null) {
                    uids.remove(user.getAccount());
                }
            }

            if (uids.size() > 0) {
                NimUserInfoCache.getInstance().getUserInfoFromRemote(uids, new RequestCallback<List<NimUserInfo>>() {
                    @Override
                    public void onSuccess(List<NimUserInfo> nimUserInfos) {
                        for (NimUserInfo userInfo : nimUserInfos) {
                            users.add(userInfo);
                        }
                        userInfoesToLua(users, callback);
                    }

                    @Override
                    public void onFailed(int i) {
                        doLuaFunction(callback, "");
                        releaseLuaFunction(callback);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        doLuaFunction(callback, "");
                        releaseLuaFunction(callback);
                    }
                });
            } else {
                userInfoesToLua(users, callback);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private static void userInfoesToLua(List<NimUserInfo> userInfoes, int callback) {
        JSONObject data = new JSONObject();
        for (NimUserInfo userInfo : userInfoes) {
            JSONObject jsonInfo = new JSONObject();
            jsonInfo.put("nickname", userInfo.getName());
            jsonInfo.put("avatar", userInfo.getAvatar());
            data.put(userInfo.getAccount(), jsonInfo);
        }

        doLuaFunction(callback, data.toJSONString());
        releaseLuaFunction(callback);
    }

    /**
     * 获取用户余额
     */
    public static void getUserAmount(final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mAmountAction == null) {
                    return;
                }
                mAmountAction.setRequestCallback(new com.htgames.nutspoker.interfaces.RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            try {
                                org.json.JSONObject resultJson = new org.json.JSONObject(result);
                                String data = resultJson.optString("data");
                                LogUtil.i(TAG, "data :" + data);
                                if (!TextUtils.isEmpty(data)) {
                                    doLuaFunction(resultCallback, data);
                                } else {
                                    doLuaFunction(resultCallback, "");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                doLuaFunction(resultCallback, "");
                            }
                        } else {
                            doLuaFunction(resultCallback, "");
                        }
                    }

                    @Override
                    public void onFailed() {
                        doLuaFunction(resultCallback, "");
                    }
                });
                mAmountAction.getAmount(false);
            }
        });
    }

    public static void buyGoods(final int goodsId, final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShopAction.buyGoods(String.valueOf(goodsId), false, new com.htgames.nutspoker.interfaces.RequestCallback() {
                    @Override
                    public void onResult(int code, String result, Throwable var3) {
                        if (code == 0) {
                            try {
                                org.json.JSONObject resultJson = new org.json.JSONObject(result);
                                String data = resultJson.optString("data");
                                LogUtil.i(TAG, "data:" + data);
                                if (!TextUtils.isEmpty(data)) {
                                    doLuaFunction(resultCallback, data);
                                } else {
                                    doLuaFunction(resultCallback, "");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                doLuaFunction(resultCallback, "");
                            }
                        } else {
                            doLuaFunction(resultCallback, "");
                        }
                    }

                    @Override
                    public void onFailed() {
                        doLuaFunction(resultCallback, "");
                    }
                });
            }
        });
    }

    public static void getShopGoodsList(final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String jsonData = ShopDataCache.getInstance().getShopData();
                if (!TextUtils.isEmpty(jsonData)) {
                    doLuaFunction(resultCallback, jsonData);
                } else {
                    mShopAction.getShopGoodsList(new com.htgames.nutspoker.interfaces.RequestCallback() {
                        @Override
                        public void onResult(int code, String result, Throwable var3) {
                            if (code == 0) {
                                try {
                                    org.json.JSONObject resultJson = new org.json.JSONObject(result);
                                    String data = resultJson.optString("data");
                                    if (!TextUtils.isEmpty(data)) {
                                        ShopDataCache.getInstance().buildCache(result);
                                        doLuaFunction(resultCallback, data);
                                    } else {
                                        doLuaFunction(resultCallback, "");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    doLuaFunction(resultCallback, "");
                                }
                            } else {
                                doLuaFunction(resultCallback, "");
                            }
                        }

                        @Override
                        public void onFailed() {
                            doLuaFunction(resultCallback, "");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            this.hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mGLSurfaceView.setSystemUiVisibility(
                Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | Cocos2dxGLSurfaceView.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    /**
     * 获取牌局相关信息（游戏底层调用获取）
     *
     * @return
     */
    public static String getInfo() {
        String data = getDataJson();
        LogUtil.i(TAG, data);
        return data;
    }

    //用于LUA调用震动
    public static void startVibrate(final int milliseconds) {
        if (instance == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (instance == null) {
                    return;
                }
                Vibrator vib = (Vibrator) instance.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(milliseconds);
            }
        });
    }

    public static String getLanguage() {
        return BaseTools.getLanguage();
    }

//    public static void setInfo(int luaFunctionId){
//        String data = getDataJson();
//        Cocos2dxLuaJavaBridge.callLuaFunctionWithString(luaFunctionId, data);
//        Log.d(TAG, "luaFunctionId :" + luaFunctionId);
//        Log.d(TAG, data);
//    }

    public static void setTableIndex(int index) {
        tableIndex = index;
        remoteExtension = AudioConstant.getGameAudioMessageExtension(uid, code, tableIndex);
        LogUtil.i(TAG, "tableIndex :" + tableIndex);
    }

    //设置房主ID
    public static void setOwnerId(String owner) {
        //tableIndex为0，而我当前的桌子不为0，并且UID和OWNERID一样，代表房主
        ownerId = owner;
    }

    public static String getDataJson() {
        JSONObject data = new JSONObject();
        data.put("gameType", gameActionType);//游戏类型
        if (gameActionType == ACTION_PLAY_GAME) {
            data.put("token", token);
            data.put("code", code);
            data.put("channel", channel);
            data.put("gameMode", gameMode);
            data.put("uid", uid);
            data.put("path", imagePath);
            data.put("ip", serverIp);
            data.put("url", HostManager.getHost());
            data.put("sound", SettingsPreferences.getInstance(DemoCache.getContext()).isGameSound() ? 1 : 0);
            data.put("sheetDir", handRecordPath);//手牌记录路径地址
            data.put("ko_mode", ko_mode);
            data.put("name", gameName);
            data.put("play_mode", play_mode);
            int play_type = 0;
            if (mGameEntity != null) {
                if (mGameEntity.gameConfig instanceof PineappleConfig) {
                    play_type = ((PineappleConfig) mGameEntity.gameConfig).getPlay_type();
                } else if (mGameEntity.gameConfig instanceof PineappleConfigMtt) {
                    play_type = ((PineappleConfigMtt) mGameEntity.gameConfig).getPlay_type();
                }
            }
            data.put("play_type", play_type);
//            String gameEntityStr = GsonUtils.getGson().toJson(mGameEntity.json_str);
            data.put("json", mGameEntity == null ? "" : mGameEntity.json_str);
            //观战
            if (!TextUtils.isEmpty(lookTableIndex)) {
                data.put("lookTableIndex", lookTableIndex);//
            }
            if (!TextUtils.isEmpty(lookUid)) {
                data.put("lookUid", lookUid);//
            }
        } else if (gameActionType == ACTION_PLAY_RECORD) {
//            data.put("sheetUid" , sheetUid);//手牌纪录者的UID（如果别人分享的就是别人的UID）
            data.put("sheetPath", sheetPath);//播放的手牌整体文件path
        }
        return data.toJSONString();
    }

    /**
     * 退出游戏（Lua代码调用）
     */
    public static void exitGame() {
        if (instance == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //俱乐部牌局,离开聊天室
                if (gameActionType == ACTION_PLAY_GAME && is_admin != 1 && !StringUtil.isSpace(gameMode) && Integer.parseInt(gameMode) < GameConstants.GAME_MODE_MT_SNG) {//  1比赛不要退出聊天室在大厅里面退出；2自由局的管理员不要退出聊天室
                    NIMClient.getService(ChatRoomService.class).exitChatRoom(sessionId);//退出
                    LogUtil.i(TAG, "离开聊天室");
                }
                if (instance == null) {
                    return;
                }
                if (instance.mGLSurfaceView != null) {
                    instance.mGLSurfaceView.pauseDraw();//暂停COCOS的绘制
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                    handler = null;
                }
                token = null;
                code = null;
                uid = null;
                sessionId = null;
                remoteExtension = null;
                audioMessageHelper = null;
                if (time != null) {
                    time.stop();
                    time.onDestroy();
                    time = null;
                }
                started = false;
                cancelled = false;
//        recording = false;
                audioControl = null;
                onPlayListener = null;
                audioMessageList = null;
                currentMessage = null;
                mStartCallBack = 0;
                mStopCallBack = 0;
                mCancelCallback = 0;
                mInterruptCallback = 0;
                mBeginPlay = 0;
                mEndPlay = 0;
                mInterruptPlay = 0;
//                RequestTimeLimit.setLastGetAmontTime(0);
//                RequestTimeLimit.setLastGetStatisticalTime(0);
                RequestTimeLimit.resetGetTime();
                instance.finish();
            }
        });
    }

    private static Handler handler;

    public static final Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }


    static String collectUrl;
    static String unCollectUrl;

    //游戏内收藏手牌    Handhistory/collectHands　接口，游戏使用的时候传个type参数为１，hid变成gid+'_'+hand_cnt  新增错误码　9104->牌谱不存在
    public static void collectSheet(final int handsCnt, final int index, final int gid, final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
                    com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                    return;
                }
                String handId = gid + "_" + handsCnt + "_" + index;
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("hid", handId);
                paramsMap.put("type", "1");
                collectUrl = getHost() + ApiConstants.URL_CARDRECORD_COLLECT + NetWork.getRequestParams(paramsMap);
                LogUtil.i(HandCollectAction.TAG, collectUrl);
                SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, collectUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.i(HandCollectAction.TAG, "response:" + response);
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(response);
                            int code = json.getInt("code");
                            String msg = DemoCache.getContext().getString(code == 0 ? R.string.collect_success : R.string.collect_failure);
                            if (code == 0) {
                                com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_success, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            } else {
                                com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            }
                            JSONObject resultJson = new JSONObject();
                            resultJson.put("code", code);
                            resultJson.put("hands_cnt", handsCnt);
                            resultJson.put("index", index);
                            resultJson.put("msg", msg);
                            doLuaFunction(resultCallback, resultJson.toJSONString());
                        } catch (JSONException e) {
                            com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            String msg = DemoCache.getContext().getString(R.string.collect_failure);
                            JSONObject resultJson = new JSONObject();
                            resultJson.put("code", 1);
                            resultJson.put("msg", msg);
                            resultJson.put("hands_cnt", handsCnt);
                            resultJson.put("index", index);
                            LogUtil.i(HandCollectAction.TAG, "JSONException:" + e.toString() + " " + resultJson.toJSONString());
                            doLuaFunction(resultCallback, resultJson.toJSONString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                        String msg = DemoCache.getContext().getString(R.string.collect_failure);
                        JSONObject resultJson = new JSONObject();
                        resultJson.put("code", 1);
                        resultJson.put("msg", msg);
                        resultJson.put("hands_cnt", handsCnt);
                        resultJson.put("index", index);
                        if (!TextUtils.isEmpty(error.getMessage())) {
                            LogUtil.i(HandCollectAction.TAG, "onErrorResponse: " + error.getMessage() + "  " + resultJson.toJSONString());
                        }
                        doLuaFunction(resultCallback, resultJson.toJSONString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return paramsMap;
                    }
                };
                signRequest.setTag(collectUrl);
                ChessApp.sRequestQueue.add(signRequest);
            }
        });
    }

    //游戏内取消收藏手牌
    public static void unCollectSheet(final int handsCnt, final int index, final int gid, final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
                    com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.network_is_not_available, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                    return;
                }
                String handId = gid + "_" + handsCnt + "_" + index;
                final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
                paramsMap.put("hid", handId);
                paramsMap.put("type", "1");
                unCollectUrl = getHost() + ApiConstants.URL_HAND_HISTORY_UNCOLOECT + NetWork.getRequestParams(paramsMap);
                LogUtil.i(HandCollectAction.TAG, unCollectUrl);
                SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, unCollectUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.i(HandCollectAction.TAG, "response: " + response);
                        try {
                            org.json.JSONObject json = new org.json.JSONObject(response);
                            int code = json.getInt("code");
                            String msg = DemoCache.getContext().getString(code == 0 ? R.string.collect_cancel_success : R.string.collect_cancel_failure);
                            if (code == 0) {
                                com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_cancel_success, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            } else {
                                com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_cancel_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            }
                            JSONObject resultJson = new JSONObject();
                            resultJson.put("code", code);
                            resultJson.put("msg", msg);
                            resultJson.put("hands_cnt", handsCnt);
                            resultJson.put("index", index);
                            doLuaFunction(resultCallback, resultJson.toJSONString());
                        } catch (JSONException e) {
                            com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_cancel_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                            String msg = DemoCache.getContext().getString(R.string.collect_cancel_failure);
                            JSONObject resultJson = new JSONObject();
                            resultJson.put("code", 1);
                            resultJson.put("msg", msg);
                            resultJson.put("hands_cnt", handsCnt);
                            resultJson.put("index", index);
                            LogUtil.i(HandCollectAction.TAG, "JSONException:" + e.toString() + "  " + resultJson.toJSONString());
                            doLuaFunction(resultCallback, resultJson.toJSONString());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        com.htgames.nutspoker.widget.Toast.makeText(instance, R.string.collect_cancel_failure, com.htgames.nutspoker.widget.Toast.LENGTH_LONG).show();
                        String msg = DemoCache.getContext().getString(R.string.collect_cancel_failure);
                        JSONObject resultJson = new JSONObject();
                        resultJson.put("code", 1);
                        resultJson.put("msg", msg);
                        resultJson.put("hands_cnt", handsCnt);
                        resultJson.put("index", index);
                        if (!TextUtils.isEmpty(error.getMessage())) {
                            LogUtil.i(HandCollectAction.TAG, "onErrorResponse: " + error.getMessage() + "  " + resultJson.toJSONString());
                        }
                        doLuaFunction(resultCallback, resultJson.toJSONString());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return paramsMap;
                    }
                };
                signRequest.setTag(unCollectUrl);
                ChessApp.sRequestQueue.add(signRequest);
            }
        });
    }

    //游戏内调用，获取用户的收藏牌谱数目
    public static void lastCollectionInfo(final int resultCallback) {
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HttpApi.Builder builder = new HttpApi.Builder()
                        .methodGet(ApiConstants.METHOD_CARDCOLLECT_LIST)
                        .mapParams(NetWork.getRequestCommonParams())
                        .param("count", "1");
                HttpApi.GetNetObservable(builder, null)
                        .map(new Func1<String, CommonBeanT<NetCardCountEy>>() {
                            @Override
                            public CommonBeanT<NetCardCountEy> call(String s) {
                                CommonBeanT<NetCardCountEy> ret = null;
                                try {
                                    Type type = new TypeToken<CommonBeanT<NetCardCountEy>>() {
                                    }.getType();
                                    ret = GsonUtils.getGson().fromJson(s, type);
                                    return ret;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return ret;
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<CommonBeanT<NetCardCountEy>>() {
                            @Override
                            public void call(CommonBeanT<NetCardCountEy> cbt) {
                                if (cbt != null) {
                                    UserPreferences.getInstance(instance).setCollectHandNum(cbt.data.count);
                                    JSONObject resultJson = new JSONObject();
                                    resultJson.put("code", cbt.code);
                                    resultJson.put("collectedNum", cbt.data.count);
                                    resultJson.put("collectedLimit", 100);
                                    resultJson.put("msg", "获取收藏数目成功");
                                    LogUtil.i(HandCollectAction.TAG, "游戏内获取收藏数目：" + resultJson.toJSONString());
                                    doLuaFunction(resultCallback, resultJson.toJSONString());
                                }
                            }
                        });
            }
        });
    }

    private static int locationCallBack;
    private static Timer locationTimer;
    private static TimerTask locationTask;

    private static void getLocation(final int callback) {
        locationCallBack = callback;
        PermissionUtils.requestPermission(instance, PermissionUtils.CODE_ACCESS_FINE_LOCATION, null);
        PermissionUtils.requestPermission(instance, PermissionUtils.CODE_ACCESS_COARSE_LOCATION, null);
        if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopListenLocation("");
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final LocationManager locationManager = (LocationManager) instance.getSystemService(Context.LOCATION_SERVICE);

                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    stopListenLocation("");
                    return;
                }

                locationTimer = new Timer();
                locationTask = new TimerTask() {
                    @Override
                    public void run() {
                        stopListenLocation("");
                    }
                };

                locationTimer.schedule(locationTask, 5000);

                // Register the listener with the Location Manager to receive location updates
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                    if (ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(instance, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                }
            }
        });
    }

    private static void stopListenLocation(Location location) {
        JSONObject data = new JSONObject();
        data.put("latitude", location.getLatitude());
        data.put("longitude", location.getLongitude());
        stopListenLocation(data.toJSONString());
    }

    private static void stopListenLocation(String location) {
        doLuaFunction(locationCallBack, location);
        releaseLuaFunction(locationCallBack);
        if (locationTimer != null) {
            locationTimer.cancel();
            locationTimer = null;
            locationTask = null;
        }
        if (instance != null) {
            LocationManager locationManager = (LocationManager) instance.getSystemService(Context.LOCATION_SERVICE);
            locationManager.removeUpdates(locationListener);
        }
    }

    // Define a listener that responds to location updates
    private static LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            stopListenLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {
            final LocationManager locationManager = (LocationManager) instance.getSystemService(Context.LOCATION_SERVICE);

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                stopListenLocation("");
                return;
            }
        }
    };

    public static String appVersion() {
        String verionName = "";
        try {
            PackageManager pm = ChessApp.sAppContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ChessApp.sAppContext.getPackageName(), 0);
            if (pi.versionName != null) {
                verionName = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtil.i("version", "current version:" + verionName);
        return verionName;
    }

    public static String getUUID() {
        return UserPreferences.getInstance(getContext()).getZYId();
    }

    private static ShareView mShareView;
    /**
     * 游戏内分享lua调用
     */
    public static void postShare(final int type, final String url, final String shareImgUrl, final String title, final String message, final int resultCallback) {
        if (mShareView == null) {
            mShareView = new ShareView(instance);
            mShareView.gameEntity = mGameEntity;
        }
        if (instance == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mShareView.shareFromGame(type, url, shareImgUrl, title, message, new WXEntryActivity.ShareCallback() {
                    @Override
                    public void onShareSuccess() {
                        doLuaFunction(resultCallback, "");
                    }
                });
            }
        });
    }

    /**
     * 游戏内请求"复活"，lua调用
     */
    public static void revivalInMatch(final int resultCallback) {
        if (instance == null || mGameMatchAction == null || mGameEntity == null) {
            return;
        }
        instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGameMatchAction.revivalInMatch(mGameEntity.joinCode, DemoCache.getAccount(), new GameRequestCallback() {
                    @Override
                    public void onSuccess(org.json.JSONObject resultJson) {
                        doLuaFunction(resultCallback, (resultJson == null ? "" : resultJson.toString()));
                    }

                    @Override
                    public void onFailed(int code, org.json.JSONObject resultJson) {
                        doLuaFunction(resultCallback, (resultJson == null ? "" : resultJson.toString()));
                    }
                });
            }
        });
    }

    public static void getLocationFromMemory() {
        if (CacheConstant.mLocation == null) {
            return;
        }
        JSONObject data = new JSONObject();
        data.put("latitude", CacheConstant.mLocation.getLatitude());
        data.put("longitude", CacheConstant.mLocation.getLongitude());
        stopListenLocation(data.toJSONString());
    }

    //lua调用 获取渠道名称1个人渠道让陈建安自己获取，2俱乐部渠道我传给他俱乐部名称
    public static void getChannelName(final int resultCallback) {
        if (mGameEntity == null) {
            return;
        }
        if (mGameEntity.joinCode != null && mGameEntity.joinCode.length() > 6) {
            String teamId = mGameEntity.joinCode.substring(6, mGameEntity.joinCode.length());//gameInfo.getTid();
            if (StringUtil.isSpace(teamId)) {
                return;
            }
            final Team team = TeamDataCache.getInstance().getTeamById(teamId);
            if (team != null) {
                doLuaFunction(resultCallback, TeamDataCache.getInstance().getTeamById(teamId).getName());
            } else {
                TeamDataCache.getInstance().fetchTeamById(teamId, new SimpleCallback<Team>() {
                    @Override
                    public void onResult(boolean success, Team result) {
                        if (success && result != null) {
                            doLuaFunction(resultCallback, result.getName());
                        }
                    }
                });
            }
        }
    }
}

