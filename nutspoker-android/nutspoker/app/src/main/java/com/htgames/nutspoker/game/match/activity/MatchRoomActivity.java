package com.htgames.nutspoker.game.match.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.protobuf.InvalidProtocolBufferException;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.chat.app_msg.BuyChipsAction;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.contact_selector.ContactSelectHelper;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.config.GameConfigData;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.game.match.Match;
import com.htgames.nutspoker.game.match.adapter.MatchPagerAdapter;
import com.htgames.nutspoker.game.match.bean.MatchPlayerEntity;
import com.htgames.nutspoker.game.match.bean.MatchStatusEntity;
import com.htgames.nutspoker.game.match.bean.MatchTableEntity;
import com.htgames.nutspoker.game.match.constants.MatchConstants;
import com.htgames.nutspoker.game.match.fragment.MatchInfoFragment;
import com.htgames.nutspoker.game.match.fragment.MatchMgrFragment;
import com.htgames.nutspoker.game.match.fragment.MatchPlayerFragment;
import com.htgames.nutspoker.game.match.fragment.MatchTableFragment;
import com.htgames.nutspoker.game.match.helper.GameMatchHelper;
import com.htgames.nutspoker.game.match.helper.MatchManager;
import com.htgames.nutspoker.game.match.helper.MatchMgrItem;
import com.htgames.nutspoker.game.match.net.MatchRequestHelper;
import com.htgames.nutspoker.game.match.view.AdvancedFunctionView;
import com.htgames.nutspoker.game.match.view.MatchTipView;
import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;
import com.htgames.nutspoker.game.model.CheckInStatus;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.game.tools.GameMatchJsonTools;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.net.websocket.Draft17WithOrigin;
import com.htgames.nutspoker.net.websocket.TexasSocketConstants;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.GameMatchAction;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.System.ShopActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.ShareCodeView;
import com.htgames.nutspoker.view.ShareView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.api.ApiConfig;
import com.netease.nim.uikit.api.ApiConstants;
import com.netease.nim.uikit.api.NetWork;
import com.netease.nim.uikit.api.SignStringRequest;
import com.netease.nim.uikit.bean.BaseMttConfig;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.GameMtSngConfig;
import com.netease.nim.uikit.bean.GameMttConfig;
import com.netease.nim.uikit.bean.PineappleConfigMtt;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.CacheConstant;
import com.netease.nim.uikit.chesscircle.helper.MessageConfigHelper;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.contact.core.item.ContactIdFilter;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.login.LogoutHelper;
import com.netease.nim.uikit.nim.ChatRoomMemberCache;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.NotificationType;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.netease.nim.uikit.api.HostManager.getHost;

/**
 * 游戏大厅
 */
public class MatchRoomActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = MatchRoomActivity.class.getSimpleName();
    public static final int REQUEST_CODE_INVITE = 1;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    MatchPagerAdapter mMttPagerAdapter;
    PopupWindow rightTopPopupWindow = null;
    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList;
    List<Integer> iconList;
    String websocketUrl = "";
    MatchInfoFragment mMatchInfoFragment;
    //    MatchMemberFragment mMatchMemberFragment;
    MatchPlayerFragment mMatchPlayerFragment;
    MatchTableFragment mMatchTableFragment;
    //    ChatRoomMessageFragment mMatchChatFragment;
    //
    RelativeLayout rl_mtt_bottom;
    RelativeLayout rl_mtt_control;
    LinearLayout ll_mtt_control_btn;
    Button btn_game_invite;
    Button btn_game_checkin;
    TextView tv_match_status_final;
    TextView tv_game_status;
    TextView tv_match_bottom_status_top;
    TextView tv_match_bottom_status_center;
    TextView tv_match_bottom_status_center_prompt;
    TextView tv_new_notify;
    View websocket_loading_container;
    //
    private PagerSlidingTabStrip tabs;
    GameEntity gameInfo;
    private int checkInCount = 0;//已经登记的人数
    int checkInMayFee = 0;//报名所需费用：报名费 + 记录费
    int matchChips = 0;//起始记分牌
    int matchLevel = 0;//终止参赛等级
    int matchPlayer = 9;//单桌人数
    int totalPlayer = 0;//比赛总人数
    boolean isAreadyInMatch = false;//是否已经在比赛中
    //
    private String roomId;
    private String joinCode;  //加入时输入的 code，通道 id
    private ChatRoomInfo roomInfo;
    public String creatorId = "";
    public long lastClickTime = 0;
    public final static int COUNTDOWN_INTERVAL = 60 * 30;
    public final static int COUNTDOWN_WAIT_FOR_CONTROL_CHECKIN = 60 * 3;//点击"报名"，等待3分钟房主处理，3分钟后变成"重新报名"
    GameMatchAction mGameMatchAction;
    BuyChipsAction mBuyChipsAction;
    ShareView mShareView;
    Bitmap shareCodeBitmap;
    ShareCodeView mShareCodeView;
    //websocket
    private WebSocketClient mWebSocketClient;
    private Draft mSocketDraft;
    String hostIp;
    public GameAction mGameAction;
    public ClubAction mClubAction;
    public MatchRequestHelper mMatchRequestHelper;
    public int currentPlayerSequence = 0;
    public int currentMgrPlayerSequence = 0;//管理页面的
    long matchBeginTime = 0;
    int gameStatus = GameStatus.GAME_STATUS_WAIT;//比赛状态
    int myCheckInStatus = CheckInStatus.CHECKIN_STATUES_NOT;//我的报名状态
    public int buyTypeStatus = GameMatchBuyType.TYPE_BUY_CHECKIN;//买入情况
    int matchIngStatus = GameMatchStatus.GAME_STATUS_NORMAL;//比赛中状态
    public MatchStatusEntity matchStatus;
    Timer timer;
    long lastConnectSocketTime = 0;
    public long lastGetPlayerTime = 0;
    public long lastGetMgrPlayerTime = 0;//管理页面的
    long lastGetMyStatusTime = 0;
    long lastSendPingTime = 0;
    long lastRecvPingTime = 0;
    String channel;
    boolean isInitiativeDismiss = false;//是否是主动解散
    AdvancedFunctionView advancedFunctionView;
    MatchTipView mMatchTipView;

//    public static void start(Activity activity, ChatRoomInfo chatRoomInfo, String hostIp, String joinCode, GameEntity gameEntity) {
//        Intent intent = new Intent(activity, MatchRoomActivity.class);
//        intent.putExtra(Extras.EXTRA_ROOM_INFO, (Serializable) chatRoomInfo);
//        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, hostIp);
//        intent.putExtra(Extras.EXTRA_GAME_CODE, joinCode); //add by db 加入时输入的码。通道id
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameEntity == null ? joinCode : gameEntity.channel);
//        activity.startActivity(intent);
//    }

    public static void startWithWebsockUrl(Activity activity, ChatRoomInfo chatRoomInfo, String hostIp, String joinCode, GameEntity gameEntity, String ws_url, String roomId) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MatchRoomActivity.class);
        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, hostIp);
        intent.putExtra(Extras.EXTRA_URL, ws_url);
        intent.putExtra(Extras.EXTRA_ROOM_ID, roomId);
        intent.putExtra(Extras.EXTRA_GAME_CODE, joinCode); //add by db 加入时输入的码。通道id
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameEntity == null ? joinCode : gameEntity.channel);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtt);
        initAction();
        initData();
        initView();
        initFragment();
        initTabLayout();
        enterMatchRoom(roomId, hostIp, joinCode, null, null, websocketUrl);
    }

    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    //进入聊天室
    public void enterMatchRoom(final String roomId ,final String hostIp, final String joinCode, final GameRequestCallback mGameRequestCallback, final GameEntity gameEntity, final String websocketUrl) {
        if (NIMClient.getStatus() != StatusCode.LOGINED || TextUtils.isEmpty(roomId) || NIMClient.getStatus() != StatusCode.LOGINED) {
            Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
            DialogMaker.dismissProgressDialog();
            if (mGameRequestCallback != null) {
                mGameRequestCallback.onFailed(0, null);
            }
            return;
        }
        LogUtil.e(TAG, "云信进入聊天室, roomId=" + roomId);
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                if (result == null || result.getRoomInfo() == null) {
                    if (mGameRequestCallback != null) {
                        mGameRequestCallback.onFailed(-1, null);
                    }
                    enterRoomResult(false);//onSuccess result = null
                    return;
                }
                roomInfo = result.getRoomInfo();
                onLoginDone();
                enterRoomResult(true);//onSuccess
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onSuccess(null);
                }
            }
            @Override
            public void onFailed(int code) {
                LogUtil.e(TAG, "云信进入聊天室失败, code=" + code);
                if(code == 302){ //云信异常...
                    Toast.makeText(ChessApp.sAppContext, R.string.game_join_failure, Toast.LENGTH_SHORT).show();
                    UserPreferences.getInstance(ChessApp.sAppContext).setUserToken("");
                    NIMClient.getService(AuthService.class).logout();
                    GeTuiHelper.unBindAliasUid(ChessApp.sAppContext);//解绑个推别名
                    HandsCollectDBHelper.clearCollectHands(ChessApp.sAppContext);//清空手牌收藏
                    UserPreferences.getInstance(ChessApp.sAppContext).setCollectHandNum(0);//清空手牌收藏数量
                    UmengAnalytics.onProfileSignOff();
                    //清除登录状态&缓存&注销监听
                    LogoutHelper.logout();
                    DBUtil.closeDBUtil();//关闭数据库，因为数据库以个人UI作为账户
                    // 启动登录
                    Intent intent = new Intent(ChessApp.sAppContext, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Extras.EXTRA_REASON, LoginActivity.REASON_NO);
                    ChessApp.sAppContext.startActivity(intent);
                    return ;
                }
                int errorCode = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
                onLoginDone();
                if (mGameRequestCallback != null) {
                    mGameRequestCallback.onFailed(code, null);
                }
                enterRoomResult(false);//onFailed
//                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
//                    Toast.makeText(ChessApp.sAppContext, "你已被拉入黑名单，不能再进入", android.widget.Toast.LENGTH_SHORT).show();
//                } else if (code == ResponseCode.RES_ENONEXIST) {
//                    Toast.makeText(ChessApp.sAppContext, "聊天室不存在", android.widget.Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ChessApp.sAppContext, "enter chat room failed, code=" + code, android.widget.Toast.LENGTH_SHORT).show();
//                }
            }
            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                Toast.makeText(ChessApp.sAppContext, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                enterRoomResult(false);//onException
            }
        });
    }

    private void enterRoomResult(boolean success) {
        if (success) {
            updateRoomInfo(false);
            if (gameInfo != null && !StringUtil.isSpace(gameInfo.code) && !StringUtil.isSpace(gameInfo.gid)) {
                continueInit();//进入聊天室成功
            } else {
                getGame();//有时进入聊天室成功但是获取的云心扩展字段有问题(很少)，那么也调用game/getGame接口
            }
        } else {
            getGame();//进入聊天室失败
        }


    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    private void continueInit() {
        if (isFinishing() || mGameMatchAction == null) {
            return;
        }
        setGameInfo(false);
        registerObservers(true);// 注册监听
        setMatchRoomInfo();
        onCurrent();
        updateMatchStatusBottomUI(myCheckInStatus, false);
        mGameMatchAction.doJoinWaitMttGame(gameInfo != null ? gameInfo.code : "");
        startTimer();
        RequestTimeLimit.lastGetRecentGameTime = (0);
        MainActivity.sMatchRA = this;
        setMgrFragment();
        mMatchInfoFragment.setGameInfo(gameInfo);
        mMatchPlayerFragment.setGameInfo(gameInfo);
        mMatchTableFragment.setGameInfo(gameInfo);
        changePineappleBg();
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(updateRunnable);
            }
        }, 1000L, 1000L);
    }

    private void updateWaitForCheckin() {
        if (matchStatus == null || gameInfo == null) {
            return;
        }
        int remainTime = (int) (COUNTDOWN_WAIT_FOR_CONTROL_CHECKIN + matchStatus.checkin_time - getCurrentTime());
        boolean canCheckIn = GameMatchHelper.canCheckInMtt(matchStatus, ((BaseMttConfig) gameInfo.gameConfig).matchLevel, gameInfo);
        if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {
            if (remainTime <= COUNTDOWN_WAIT_FOR_CONTROL_CHECKIN && remainTime > 0) {//等待房主3分钟倒计时处理
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setText(getResources().getString(R.string.game_mtt_checkin_waiting, remainTime));
                tv_match_bottom_status_top.setVisibility(View.GONE);
            } else if (remainTime <= 0) {//变为"重新报名"
                tv_match_bottom_status_center.setVisibility(View.GONE);
                ll_mtt_control_btn.setVisibility(View.VISIBLE);
                btn_game_checkin.setEnabled(true);
                btn_game_checkin.setText(R.string.game_mtt_checkin_again);
                tv_match_bottom_status_top.setVisibility(View.VISIBLE);
                tv_match_bottom_status_top.setText(R.string.game_mtt_control_checkin_no_responce_tip);
            }
            if (!canCheckIn) {
                //不可以报名（人满）
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
            }
        }
    }

    Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateWaitForCheckin();//3分钟倒计时等待房主处理报名请求
            long manulStart = (matchStatus == null) ? 0 : (matchStatus.manul_begin_game_success);
            if (manulStart > 0) {
                matchBeginTime = manulStart;
            }
            int remainTime = (int) (matchBeginTime - getCurrentTime());//begin_time比start_time小了几秒，大概是10秒
            boolean is_auto = (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig)gameInfo.gameConfig).is_auto == 1);//是否是自动开赛
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                if (/*gameInfo.gameMode == GameConstants.GAME_MODE_MTT*/true) {
                    //MTT
                    if (remainTime > COUNTDOWN_INTERVAL) {
                        tv_game_status.setText(getString(R.string.game_mtt_status_waiting, DateTools.getOtherStrTime_ymd_hm("" + matchBeginTime)));
                        mMatchTipView.setVisibility(View.GONE);
                    } else if (remainTime > 0 && remainTime <= COUNTDOWN_INTERVAL) {// : 17/2/14 (is_auto || 已经点击开赛)
                        if (is_auto || manulStart > 0) {
                            if (remainTime <= 60) {
                                mMatchTipView.showWillBeginRemainTime(remainTime);
                            }
                            String remainTimeStr = GameConstants.getGameSngDurationShow(remainTime);
                            tv_game_status.setText(getString(R.string.game_mtt_status_remain, remainTimeStr));
                            if (remainTime <= 1) {
                                gameWillStart = true;
                            }
                        } else {
                            tv_game_status.setText(getString(R.string.game_mtt_status_waiting, DateTools.getOtherStrTime_ymd_hm("" + matchBeginTime)));
                            mMatchTipView.setVisibility(View.GONE);
                        }
                    } else {
                        mMatchTipView.setVisibility(View.GONE);
                        if (matchBeginTime == -1) {//表示没有设置开赛时间，直接不显示，或者文案显示为"请手动开赛"，需求有待调整
                            tv_game_status.setText("");//不能gone掉，否则viewpager会覆盖掉底部的状态栏
                        } else {
                            String remainTimeStr = GameConstants.getGameSngDurationShow(Math.abs(remainTime));
                            if (!is_auto) {//手动开赛
                                tv_game_status.setText("");
                            } else {
                                tv_game_status.setText(getString(R.string.game_mtt_status_ing, remainTimeStr));
                                gameWillStart = true;
                            }
                        }
                    }
                } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    //MT-SNG
                    int checkInPlayer = 0;
                    if (matchStatus != null) {
                        checkInPlayer = matchStatus.checkInPlayer;
                    }
                    tv_game_status.setText(getString(R.string.game_mt_sng_status_ing, checkInPlayer, totalPlayer));
                }
            } else if (gameStatus == GameStatus.GAME_STATUS_START) {
                gameWillStart = true;
                String remainTimeStr = GameConstants.getGameSngDurationShow(Math.abs(remainTime));
                tv_game_status.setText(getString(R.string.game_mtt_status_ing, remainTimeStr));
                //LogUtil.i(TAG, "matchStatus.getMatchInRest(): " + (matchStatus == null ? "null" : matchStatus.getMatchInRest()));
                if (matchStatus != null) {
                    LogUtil.i("matchStatus.matchInRest", "matchStatus.matchInRest: " + matchStatus.matchInRest + "      matchStatus.getMatchPauseTime()" + matchStatus.matchPauseTime);
                    if (matchStatus.matchInRest == MatchConstants.MATCH_IN_REST) {
                        //中场休息中
                        LogUtil.i("matchStatus.matchInRest", "中场休息中matchStatus.getMatchPauseTime()" + matchStatus.matchPauseTime);
                        if (matchStatus.matchPauseTime != 0) {
                            mRemaindPauseTime = (int) (matchStatus.matchPauseTime - getCurrentTime());
                            LogUtil.i("matchStatus.matchInRest", "mRemaindPauseTime： " + mRemaindPauseTime);
                            if (mRemaindPauseTime > 0) {
                                mMatchTipView.showPauseStatus(mRemaindPauseTime);
                            } else {
                                if(advancedFunctionView != null) {
                                    advancedFunctionView.setPause(false);
                                }
                                mMatchTipView.setVisibility(View.GONE);
                            }
                        }
                    } else if (matchStatus.matchInRest == MatchConstants.MATCH_IN_REST_BEFORE) {//倒计时前的等待界面
                        mMatchTipView.showPauseBefore();
                    } else {
                        //如果我不是比赛创建者，需要更新     //如果倒计时没有置为0，那么需要做一些操作
                        if(mMatchTipView.isShown() || (mRemaindPauseTime > 0)) {
                            if(advancedFunctionView != null && !creatorId.equals(DemoCache.getAccount())) {
                                advancedFunctionView.setPause(false);
                            }
                            mMatchTipView.setVisibility(View.GONE);
                            mRemaindPauseTime = 0;
                        }
                    }
                } else {
                    mMatchTipView.setVisibility(View.GONE);
                }
            }

            //如果在报名玩家列表界面
            if(mMatchMgrFragment != null && mMatchMgrFragment.isVisible() && gameStatus == GameStatus.GAME_STATUS_START) {
                getMgrPlayerList();//开赛后10秒获取一次管理页面的玩家列表 by zzh // : 17/2/23
            }
            //getPlayerList(false);
//            getGameMatchStatus();
            //10秒发一次心跳包
            sendWebSocketPing();
        }
    };

    public void initData() {
        websocketUrl = getIntent().getStringExtra(Extras.EXTRA_URL);
        hostIp = getIntent().getStringExtra(Extras.EXTRA_GAME_SERVERIP);
        roomId = getIntent().getStringExtra(Extras.EXTRA_ROOM_ID);
        joinCode = getIntent().getStringExtra(Extras.EXTRA_GAME_CODE);
        channel = getIntent().getStringExtra(Extras.EXTRA_GAME_CHANNEL);
    }

    public void updateRoomInfo(boolean update) {//是否是云信更新消息，enterroom的时候为FALSE；云信消息的时候是true
        String jsonStr = new JSONObject(roomInfo == null ? new HashMap() : roomInfo.getExtension()).toString();
        gameJson = jsonStr;
        LogUtil.i(GameAction.TAG, TAG + "  云信扩展字段  " + jsonStr);
        if (gameInfo != null && !StringUtil.isSpace(gameInfo.channel)) {
            channel = gameInfo.channel;
        }
        try {
            gameInfo = GameJsonTools.parseGameData(jsonStr);
            gameInfo.json_str = gameJson;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setGameInfo(update);
    }

    private void setGameInfo(boolean update) {
        creatorId = roomInfo == null ? (gameInfo != null && gameInfo.creatorInfo != null ? gameInfo.creatorInfo.account : "") : roomInfo.getCreator();
        //重置牌局code，使用通道 id
        if (joinCode.length() >= 6 /* && !isMeGameCreator()*/) {
            if (gameInfo != null) {
                gameInfo.joinCode = joinCode;
                gameInfo.channel = StringUtil.isSpace(channel) ? gameInfo.joinCode : channel;
            }
        } else {
            if (gameInfo != null) {
                gameInfo.joinCode = gameInfo.code;//创建比赛的时候把原始的code赋值给joinCode
                gameInfo.channel = StringUtil.isSpace(channel) ? gameInfo.joinCode : channel;
            }
        }
        if (gameInfo == null || StringUtil.isSpace(gameInfo.code)) {//云信扩展字段信息不全，有时候只有两个字段server和status
            return;
        }
        if (StringUtil.isSpace(creatorId) && gameInfo.creatorInfo != null) {
            creatorId = gameInfo.creatorInfo.account;
        }
        if (update && !StringUtil.isSpace(gameInfo.serverIp)) {
            hostIp = gameInfo.serverIp;//如果是云信更新的消息，那么以云信的为准
        }
        if (!StringUtil.isSpace(hostIp)) {//云信扩展字段的server不准
            gameInfo.serverIp = hostIp;
        }
//        if (!StringUtil.isSpace(gameInfo.serverIp)) {//云信扩展字段的server又准了，以云信扩字段为准
//            hostIp = gameInfo.serverIp;
//        }
        LogUtil.i(TAG, "gameStatus : " + gameStatus + ";gameInfo : " + gameInfo.status);
        if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT && gameInfo.gameConfig instanceof BaseMttConfig) {
            BaseMttConfig gameMttConfig = (BaseMttConfig) gameInfo.gameConfig;
            matchBeginTime = gameMttConfig.beginTime;
            checkInMayFee = gameMttConfig.matchCheckinFee + (gameInfo.match_type == GameConstants.MATCH_TYPE_NORMAL ? gameMttConfig.matchCheckinFee / 10 : 0);
            matchChips = gameMttConfig.matchChips;
            matchLevel = gameMttConfig.matchLevel;
            matchPlayer = gameMttConfig instanceof GameMttConfig ? ((GameMttConfig) gameMttConfig).matchPlayer : 2;
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG && gameInfo.gameConfig instanceof GameMtSngConfig) {
            GameMtSngConfig gameMtSngConfig = (GameMtSngConfig) gameInfo.gameConfig;
            matchChips = gameMtSngConfig.matchChips;
            matchPlayer = gameMtSngConfig.matchPlayer;
            totalPlayer = gameMtSngConfig.totalPlayer;
        }
        if (gameStatus != gameInfo.status) {
            LogUtil.i(TAG, "gameStatus change:" + gameStatus);
            gameStatus = gameInfo.status;
            if (matchStatus != null) {
                matchStatus.gameStatus = gameStatus;
            }
            if (mMatchInfoFragment != null) {
                mMatchInfoFragment.updateMatchIngUI(matchStatus);
            }
            if (mMatchPlayerFragment != null) {
                mMatchPlayerFragment.updatePlyaerCount(gameStatus, matchStatus);
            }
            if (mMatchMgrFragment != null) {
                mMatchMgrFragment.gameStatusChanged(matchStatus);
            }
            updateMatchStatusBottomUI(myCheckInStatus, true);
            lastGetPlayerTime = 0;//刷新玩家列表数据源，开赛后要显示排名，老的数据源排名全是1
        } else {
            updateMatchStatusBottomUI(myCheckInStatus, false);
        }
        if (creatorId.equals(DemoCache.getAccount())) {
            Drawable rightHeadButtonDrawable = getResources().getDrawable(R.mipmap.icon_manage);
            rightHeadButtonDrawable.setBounds(0, 0, rightHeadButtonDrawable.getIntrinsicWidth(), rightHeadButtonDrawable.getIntrinsicHeight());
            TextView rightHeadButton = (TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right);
            rightHeadButton.setCompoundDrawables(rightHeadButtonDrawable, null, null, null);
            rightHeadButton.setText("");
            rightHeadButton.setOnClickListener(this);
            rightHeadButton.setVisibility(View.VISIBLE);
        } else {
            setHeadRightButtonGone();
        }
    }

    private void getGame() {
        if (isFinishing() || isDestroyedCompatible()) {
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            android.widget.Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, android.widget.Toast.LENGTH_LONG).show();
            DialogMaker.dismissProgressDialog();
            return;
        }
        DialogMaker.showProgressDialog(this, "", false);
        mGameAction.getGameInfo(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_ENTER_ROOM_FAILED, joinCode, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                if (StringUtil.isSpace(response)) {
                    return;
                }
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == ApiCode.CODE_SUCCESS) {
                        JSONObject data = json.optJSONObject("data");
                        gameInfo = GameJsonTools.parseGameData(data.toString());
                        if (gameInfo != null) {
                            gameInfo.joinCode = StringUtil.isSpace(joinCode) ? gameInfo.code : joinCode;
                            gameInfo.channel = StringUtil.isSpace(channel) ? gameInfo.joinCode : channel;
                        }
                        gameJson = data.toString();
                        continueInit();// game/getGame调用成功
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DialogMaker.dismissProgressDialog();
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                continueInit();// game/getGame调用失败
            }
        });
    }

    public void setMatchRoomInfo() {
        if (mMatchInfoFragment != null && roomInfo != null && roomInfo.getExtension() != null) {
            LogUtil.i(TAG, "roomInfo :" + roomInfo.getExtension().toString());
            if (mShareCodeView == null) {
                mShareCodeView = (ShareCodeView) findViewById(R.id.mShareCodeView);
                mShareCodeView.setGameInfo(gameInfo.creatorInfo.account, gameInfo.name, gameInfo.code, gameInfo.gameMode, gameInfo.gameConfig);
            }
        }
    }

    EasyAlertDialog nicknameExistedDialog;
    private void showNicknameExistedDialog() {
        if (nicknameExistedDialog == null) {
            nicknameExistedDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, "",
                    "连接失败，请退出大厅重试", getString(R.string.ok), false,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible() && !nicknameExistedDialog.isShowing()) {
            nicknameExistedDialog.show();
        }
    }
    int mWebSocketUrlIndex = 0;
    int connectWSTryTimes = 0;//尝试长连接次数  5次就退出页面
    public void initWebSocket() throws URISyntaxException {
        if (gameInfo == null || StringUtil.isSpace(gameInfo.code) || StringUtil.isSpace(gameInfo.channel)) {
            return;
        }
        String[] urls = websocketUrl.split(",");
        if (connectWSTryTimes >= urls.length && connectWSTryTimes >= 3 && connectWSTryTimes <= 5) {
            websocket_loading_container.setVisibility(View.GONE);
            showNicknameExistedDialog();
            return;
        }
        String searchWebSocketUrl = "";
        if (connectWSTryTimes == 0) {
            mWebSocketUrlIndex = (int) (Math.random() * urls.length);
        }
        for (; mWebSocketUrlIndex < urls.length; mWebSocketUrlIndex++) {
            searchWebSocketUrl = urls[mWebSocketUrlIndex];
            if (!StringUtil.isSpace(searchWebSocketUrl) && !"0".equals(searchWebSocketUrl)) {//蛋疼，有时候长连地址出现0的话也过滤
                break;
            } else {
                continue;
            }
        }
        mWebSocketUrlIndex++;//下一次再次进入这个函数表示上个长连接地址close，那么index++使用下一个长连地址
        connectWSTryTimes++;
        if (mWebSocketUrlIndex >= urls.length) {
            mWebSocketUrlIndex = 0;
        }
        URI uri = TexasSocketConstants.getRoomWebSocketUri(DemoCache.getAccount(), gameInfo, searchWebSocketUrl);
        if (uri == null) {
            return;
        }
        mSocketDraft = new Draft17WithOrigin();
        mWebSocketClient = new WebSocketClient(uri, mSocketDraft) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                connectWSTryTimes = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        websocket_loading_container.setVisibility(View.GONE);
                    }
                });
                lastConnectSocketTime = getCurrentTime();
                LogUtil.i(TAG, "onOpen :");
                getGameMatchStatus();//长连接刚连上
                if (mMatchPlayerFragment != null && mMatchPlayerFragment.isFragmentCreated) {
                    lastGetPlayerTime = 0;
                    getPlayerList();
                }
                if (mMatchTableFragment != null && mMatchTableFragment.isFragmentCreated) {
                    mMatchTableFragment.lastGetTableTime = 0;
                    getTableList();
                }
                if (mMatchMgrFragment != null && mMatchMgrFragment.isFragmentCreated) {
                    lastGetMgrPlayerTime = 0;
                    getMgrPlayerList();
                }
            }

            @Override
            public void onMessage(final String message) {
                LogUtil.i(TAG, "onMessage :" + message);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        dealEvenMessage(message);
//                    }
//                });
            }

            @Override
            public void onMessage(final ByteBuffer bytes) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dealBytesMessage(bytes);
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                LogUtil.i(TAG, "onClose :" + code + " ; reason:" + reason + ";remote :" + remote);
                if (code == 1006) {
                    //断线重连
//                    try {
//                        initWebSocket();
//                    } catch (URISyntaxException e) {
//                        e.printStackTrace();
//                    }
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                LogUtil.i(TAG, "onError , e: " + (e == null ? "e == null" : e.toString()));
            }

            @Override
            public void onWebsocketPing(WebSocket conn, Framedata f) {
                super.onWebsocketPing(conn, f);
                LogUtil.i(TAG, "onWebsocketPing");
            }

            @Override
            public void onWebsocketPong(WebSocket conn, Framedata f) {
                super.onWebsocketPong(conn, f);
                LogUtil.i(TAG, "onWebsocketPong");
            }
        };
        mWebSocketClient.connect();
        mMatchRequestHelper = new MatchRequestHelper(mWebSocketClient, gameInfo.code);
    }

    /**
     * 发送心跳包
     */
    public void sendWebSocketPing() {
        if (CacheConstant.debugBuildType) {
            String previousStr = ApiConfig.isTestVersion ? "Debug-测试服" : "Debug-正式服";
            String newStr = previousStr + ("-" + packageLengthPerSecond + "b/s") + ("-" + packageLengthTotal);
            ChessApp.showNewDebugViewContent(newStr);
        }
        if (mGameOver) {
            return;
        }
        packageLengthPerSecond = 0;
        final long currentTime = getCurrentTime();
        if (currentTime - lastSendPingTime > 5) {
            if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isOpen()) {
                lastSendPingTime = currentTime;
                getNetTime();
            } else {
                LogUtil.i(TAG, "=======> WebSocketClient 重连1");
                lastSendPingTime = currentTime;
                lastRecvPingTime = currentTime;
                reConnectWebSocket();
            }
        } else if(currentTime - lastRecvPingTime > 8) {
            LogUtil.i(TAG, "=======> WebSocketClient 重连2");
            lastSendPingTime = currentTime;
            lastRecvPingTime = currentTime;
            reConnectWebSocket();//重连
        }
        if (currentTime - lastGetPlayerTime > 5) {
            if (mMatchRequestHelper != null) {
                mMatchRequestHelper.isRequestingPlayerList = false;
            }
        }
//        final long currentTime = getCurrentTime();
//        long remaindTime = currentTime - lastSendPingTime;
//        if (remaindTime <= 10) {
//            return;
//        }
        if (mMatchRequestHelper != null &&
                gameWillStart//gameStatus == GameStatus.GAME_STATUS_START  //以前是开赛后才动态刷status，现在改为gameWillStart=true就动态刷status
                && (mViewPager != null && mViewPager.getCurrentItem() <= 1)
                && currentTime - lastGetMyStatusTime >= 10) {
            getGameMatchStatus();//比赛开始后10秒请求一次(并且当前页面在"状态"或者"玩家")，开始前不定时请求
            lastGetMyStatusTime = currentTime;
        }
//        lastSendPingTime = currentTime;
//        if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isOpen()) {
//            LogUtil.d(TAG, "sendWebSocketPing");
//            FramedataImpl1 frame = new FramedataImpl1(Framedata.Opcode.PING);
//            frame.setFin(true);
//            mWebSocketClient.getConnection().sendFrame(frame);
//        } else {
//            LogUtil.d(TAG, "重连");
//            reConnectWebSocket();//重连
//        }
    }

    private int packageLengthPerSecond = 0;
    private int packageLengthTotal = 0;
    public void dealBytesMessage(ByteBuffer bytes) {
        if (isFinishing()) {
            return;
        }
        packageLengthPerSecond += (bytes == null ? 0 : bytes.array().length);
        packageLengthTotal += (bytes == null ? 0 : bytes.array().length);
        if (bytes == null || bytes.array() == null || bytes.array().length < 2) {
            LogUtil.i(TAG, "dealBytesMessage websocket返回的数据太短：len: " + (bytes == null ? "bytes == null" : bytes.array().length));
            return;
        }
        LogUtil.i(TAG, "dealBytesMessage len:" + bytes.array().length);
        short cmd = bytes.getShort();
        LogUtil.i(TAG, "dealBytesMessage cmd:" + cmd + " len:" + bytes.array().length);
        byte[] data = new byte[bytes.array().length - 2];
        System.arraycopy(bytes.array(), 2, data, 0, data.length);
        if (cmd < MatchRequestHelper.CMD_ID_USER) {
            return;
        }
        lastRecvPingTime = getCurrentTime();
        try {
            if (cmd == MatchRequestHelper.HEART_BEAT_NTF) {
                Match.HeartbeatNtf rep = Match.HeartbeatNtf.parseFrom(data);
                LogUtil.i(TAG, "代替老的ping心跳包也是请求服务器时间:" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                //校时
                long netTime = rep.getTime();
                if (netTime != 0) {
                    //服务器比本地时间快多少
                    DemoCache.setCheckTimeValue(netTime);
                }
            }
            if (cmd == MatchRequestHelper.MATCH_SELF_STATUS_REP) {
                Match.MatchSelfStatusRep rep = Match.MatchSelfStatusRep.parseFrom(data);
                String repStr = rep == null ? "null" : rep.toString();
                LogUtil.i(TAG, "游戏状态:------------------------------" + repStr);
                if (rep == null) {
                    return;
                }
//                String finalJsonStr = repStr.replaceAll("\\n", ",").substring(0, repStr.length() - 2) + "\"";
//                MatchStatusEntity matchStatus = GsonUtils.getGson().fromJson("{" + finalJsonStr + "}", new TypeToken<MatchStatusEntity>() {}.getType());
                MatchStatusEntity matchStatus = GameMatchJsonTools.getMatchStatusEntity(rep);
                updateMatchStatus(matchStatus);
                MatchManager.getInstance().execludeCallback(new MatchMgrItem(matchStatus));
            } else if (cmd == MatchRequestHelper.MATCH_USERLIST_REP) {
                mMatchRequestHelper.isRequestingPlayerList = false;
                Match.MatchUserListRep rep = Match.MatchUserListRep.parseFrom(data);
                LogUtil.i(TAG, "游戏玩家:------------------------------" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                int sequence = rep.getSequence();
                int msg_index = rep.getMsgIndex();
                int msg_cnt = rep.getMsgCnt();
                int isEnd = msg_cnt == 1 || msg_index >= msg_cnt ? 1 : 0;
                int rank_type = rep.getRankType();
                boolean clear = msg_index == 1;
                String wsChannel = rep.getChannel();
                ArrayList<MatchPlayerEntity> playerItems = GameMatchJsonTools.parseMatchPlayerList(rep, gameStatus, matchChips);
                //刷新备注
//                List<String> nimRefreshList = new ArrayList<>();
//                for (int i = 0; i < playerItems.size(); i++) {
//                    nimRefreshList.add(playerItems.get(i).uid);
//                }
//                NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
                LogUtil.i(TAG, "游戏玩家:------------------------------rank_type: " + rank_type + "    currentMgrPlayerSequence： " + currentMgrPlayerSequence + "  currentPlayerSequence: " + currentPlayerSequence + "  sequence: " + sequence + "  sequenceChanged：");
                if (StringUtil.isSpace(wsChannel)) {//总的"玩家"页面
                    if (currentPlayerSequence == sequence || mMatchPlayerFragment == null || playerItems == null) {//数据无变化
                        return;
                    }
                    if (mMatchPlayerFragment.rank_type == rank_type) {
                        mMatchPlayerFragment.updateList(playerItems, clear, isEnd);
                    }
                    currentPlayerSequence = sequence;
                } else {//管理页面
                    if (mMatchMgrFragment != null && mMatchMgrFragment.refresh_layout != null) {
                        mMatchMgrFragment.refresh_layout.setRefreshing(false);
                    }
                    if (currentMgrPlayerSequence == sequence || mMatchMgrFragment == null || playerItems == null) {
                        return;
                    }
                    if (mMatchMgrFragment.rank_type == rank_type) {
                        DecimalFormat headTotalForm = new DecimalFormat("#.##");
                        String headTotalStr = headTotalForm.format(rep.getKoHeadTotal() / 100f);
                        mMatchMgrFragment.updateList(playerItems, clear, isEnd, rep.getRebuyCnt(), rep.getCheckinCnt(), headTotalStr);
                    }
                    currentMgrPlayerSequence = sequence;
                }
            } else if (cmd == MatchRequestHelper.MATCH_USERINFO_REP) {
                Match.MatchUserInfoRep rep = Match.MatchUserInfoRep.parseFrom(data);
                LogUtil.i(TAG, "增量游戏玩家:------------------------------" + (rep == null ? "null" : rep.toString()));//这个增量更新的cmd不要了，暂时做成全量更新----
                if (rep == null || rep.getUserInfo() == null) {
                    return;
                }
                MatchPlayerEntity playerEntity = GameMatchJsonTools.parseMatchPlayer(rep.getUserInfo());
//                //刷新备注
//                List<String> nimRefreshList = new ArrayList<>();
//                nimRefreshList.add(playerEntity.uid);
//                NimUserInfoCache.getUserListByNeteaseEx(nimRefreshList, 0, null);
                if (mMatchMgrFragment != null && playerEntity != null) {
                    mMatchMgrFragment.updateMgrPlayer(playerEntity, 1, rep.getChannel());
                }
                if (mMatchPlayerFragment != null && playerEntity != null) {
                    MatchPlayerEntity playerEntityAll = MatchPlayerEntity.copyMatchPlayer(playerEntity);
                    if (!creatorId.equals(DemoCache.getAccount())) {//"玩家"页面只有房主看得到uuid和通过人
                        playerEntityAll.uuid = "";
                        playerEntityAll.opt_user = "";
                    }
                    mMatchPlayerFragment.updatePlayer(playerEntityAll, 1/*rep.getMsgType()*/);
                }
            } else if (cmd == MatchRequestHelper.MATCH_TABLELIST_REP) {
                Match.MatchTableListRep rep = Match.MatchTableListRep.parseFrom(data);
                LogUtil.i(TAG, "桌子列表:------------------------------" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                ArrayList<MatchTableEntity> tableList = GameMatchJsonTools.parseMatchTableList(rep);
                if (mMatchTableFragment != null && tableList != null && tableList.size() > 0) {
                    mMatchTableFragment.updateTableList(tableList);
                }
            } else if (cmd == MatchRequestHelper.MATCH_SEARCH_PLAYER_REP) {
                Match.SearchMatchUserRep rep = Match.SearchMatchUserRep.parseFrom(data);
                LogUtil.i(TAG, "搜索玩家message:------------------------------" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                ArrayList<MatchPlayerEntity> searchPlayerItems = GameMatchJsonTools.parseMatchPlayerList(rep, gameStatus, matchChips);
                if (mMatchPlayerFragment != null && searchPlayerItems != null && searchPlayerItems.size() > 0) {
                    mMatchPlayerFragment.updateSearchList(searchPlayerItems);
                }
            } else if (cmd == MatchRequestHelper.MATCH_CHECKIN_NTF) {
                Match.MatchCheckInNtf rep = Match.MatchCheckInNtf.parseFrom(data);
                LogUtil.i(TAG, "checkinntf报名状态更新:------------------------------" + (rep == null ? "null" : rep.toString()));
                //0=>申请报名 1=> 报名成功 2=>取消报名或者踢人  3=>拒绝报名   只处理1和2   如果是1  发送107请求，如果是2客户端自己根据uid减掉这个玩家
                if (rep == null) {
                    return;
                }
                int checkin_type = rep.getCheckinType();
                String uid = rep.getUid() + "";
                int checkin_player = rep.getCheckinPlayer();
                int all_reward = rep.getAllReward();
                String checkinChannel = rep.getChannel();
                if (checkin_type == 1) {//发送107请求
                    if (mMatchRequestHelper != null) {
                        mMatchRequestHelper.getPlayer(rep.getUid(), checkinChannel);
                    }
                } else if (checkin_type == 2) {//客户端自己根据uid减掉这个玩家
                    MatchPlayerEntity playerEntity = new MatchPlayerEntity();
                    playerEntity.uid = uid;
                    if (mMatchPlayerFragment != null) {
                        mMatchPlayerFragment.updatePlayer(playerEntity, 2/*rep.getMsgType()*/);
                    }
                    if (mMatchMgrFragment != null) {
                        mMatchMgrFragment.updateMgrPlayer(playerEntity, 2, checkinChannel);
                    }
                }
                if (matchStatus != null) {
                    matchStatus.checkInPlayer = checkin_player;
                    matchStatus.allReward = all_reward;
                }
                if (uid.equals(DemoCache.getAccount())) {//请求statusentity
                    getGameMatchStatus();//checkin_ntf自己报名
                    if (checkin_type == 1) {
                        selfCheckinSuccessAfterGameStarting = true;
                    }
                } else {
                    updateMatchStatus(matchStatus);
                }
            } else if (cmd == MatchRequestHelper.MATCH_START_NTF) {//------------------房主点击提前开赛,更改start_time字段
                Match.MatchStartNtf rep = Match.MatchStartNtf.parseFrom(data);
                LogUtil.i(TAG, "房主点击提前开赛,更改start_time字段:------------------------------" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                if (matchStatus != null) {
                    matchStatus.manul_begin_game_success = rep.getStartTime();
                }
                updateMatchStatusBottomUI(myCheckInStatus, false);
            } else if (cmd == MatchRequestHelper.MATCH_INR_ESTT_NTF) {//mtt比赛暂停
                Match.MatchInResttNtf rep = Match.MatchInResttNtf.parseFrom(data);
                LogUtil.i(TAG, "mtt比赛暂停:------------------------------" + (rep == null ? "null" : rep.toString()));
                if (rep == null) {
                    return;
                }
                if (matchStatus != null) {
                    matchStatus.matchInRest = rep.getMatchInRest();
                    matchStatus.matchPauseTime = rep.getMatchPauseTime();
                }
            }
        } catch (InvalidProtocolBufferException e) {
            LogUtil.i(TAG, "protoc数据解析异常:" + (e == null ? "e == null" : e.toString()));
            e.printStackTrace();
        }
    }

    public void updateIsControlResult(int isControl) {
        if (matchStatus != null) {
            matchStatus.isControl = isControl;
        }
    }

    private void updateMatchLevel() {//更新当前的盲注级别
        if (gameInfo == null || matchStatus == null) {
            return;
        }
        int blindMode = gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).sblinds_mode : 0;
        matchLevel = 1;
        if (matchStatus.currentLevel == 0) {
            matchLevel = 1;
        } else {
            if (blindMode == 1) {
                if (matchStatus.currentLevel <= GameConfigData.mtt_sblinds_multiple.length) {
                    matchLevel = matchStatus.currentLevel;//websocket从1开始传的
                } else {
                    matchLevel = GameConfigData.mtt_sblinds_multiple.length;
                }
            } else if (blindMode == 2) {
                if (matchStatus.currentLevel <= GameConfigData.mtt_sblinds_multiple_quick.length) {
                    matchLevel = matchStatus.currentLevel;//websocket从1开始传的
                } else {
                    matchLevel = GameConfigData.mtt_sblinds_multiple_quick.length;
                }
            }
        }
    }

    private boolean selfCheckinSuccessAfterGameStarting = false;//比赛开赛后自己报名成功
    public synchronized void updateMatchStatus(MatchStatusEntity matchStatus) {
        if (matchStatus == null) {
            return;
        }
        this.matchStatus = matchStatus;
        if (matchBeginTime <= matchStatus.beginTime) {
            matchBeginTime = matchStatus.beginTime;
        }
        if (matchStatus.gameStatus > this.gameStatus) {
            this.gameStatus = matchStatus.gameStatus;//长连接的状态比云信的状态延迟
        }
        if (mMatchInfoFragment != null) {
            mMatchInfoFragment.updateCheckinPlayer(matchStatus);
            mMatchInfoFragment.updateMatchIngUI(matchStatus);
        }
        if (mMatchPlayerFragment != null) {
            mMatchPlayerFragment.updatePlyaerCount(gameStatus, matchStatus);
        }
        if (mMatchMgrFragment != null) {
            mMatchMgrFragment.gameStatusChanged(matchStatus);
        }
        myCheckInStatus = matchStatus.myCheckInStatus;
        buyTypeStatus = matchStatus.buyTypeStatus;
        matchIngStatus = matchStatus.matchStatus;
        updateMatchLevel();
        boolean openGame = selfCheckinSuccessAfterGameStarting;
        updateMatchStatusBottomUI(myCheckInStatus, openGame);
        if (selfCheckinSuccessAfterGameStarting) {
            selfCheckinSuccessAfterGameStarting = false;//重置
        }
    }

    public void updateMatchStatusBottomUI(int myCheckInStatus, boolean isOpenGame) {
        this.myCheckInStatus = myCheckInStatus;
        if (matchStatus == null || gameInfo == null) {
            return;
        }
        if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT) {
            //MTT比赛模式
            boolean canCheckIn = GameMatchHelper.canCheckInMtt(matchStatus, ((BaseMttConfig) gameInfo.gameConfig).matchLevel, gameInfo);
            //房主状态，私人牌局
            if (gameInfo.type == GameConstants.GAME_TYPE_GAME && creatorId.equals(DemoCache.getAccount())) {
//                btn_game_invite.setVisibility(View.VISIBLE);// 16/12/19  邀请功能暂时去掉
                if (canCheckIn) {
                    btn_game_invite.setEnabled(true);
                } else {
                    btn_game_invite.setEnabled(false);
                }
            } else {
                btn_game_invite.setVisibility(View.GONE);
            }
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                //比赛未开始
                updateMttWaitStatusUI(canCheckIn);
            } else {
                //比赛已开始
                updateMttIngStatusUI(canCheckIn, isOpenGame);
            }
        } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG) {
            //MT-SNG比赛模式
            boolean canCheckIn = GameMatchHelper.canCheckInMtSng(matchStatus, totalPlayer);
            //房主状态，私人牌局
            if (gameInfo.type == GameConstants.GAME_TYPE_GAME && creatorId.equals(DemoCache.getAccount())) {
//                btn_game_invite.setVisibility(View.VISIBLE);//  16/12/19邀请功能暂时去掉
                if (canCheckIn) {
                    btn_game_invite.setEnabled(true);
//                    btn_game_invite.setVisibility(View.VISIBLE);//  16/12/19邀请功能暂时去掉
                } else {
                    btn_game_invite.setEnabled(false);
//                    btn_game_invite.setVisibility(View.GONE);
                }
            } else {
                btn_game_invite.setVisibility(View.GONE);
            }
            if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                updateMtSngWaitStatusUI(canCheckIn);
            } else {
                //比赛已开始
                updateMtSngIngStatusUI(canCheckIn, isOpenGame);
            }
        }
    }

    private boolean canRebuy() {//是否能够重构
        if (gameInfo != null && gameInfo.gameConfig instanceof GameMttConfig && matchStatus != null) {
            BaseMttConfig config = (BaseMttConfig) gameInfo.gameConfig;
            if (config.match_rebuy_cnt == matchStatus.rebuy_cnt) {
                return false;
            }
        }
        return true;
    }
    public void updateMttWaitStatusUI(boolean canCheckIn) {
        boolean is_auto = (gameInfo != null && gameInfo.gameConfig instanceof BaseMttConfig && ((BaseMttConfig) gameInfo.gameConfig).is_auto == 1);//是否是自动开赛
        long manulStart = (matchStatus == null) ? 0 : (matchStatus.manul_begin_game_success);
        //可以报名（已报，未报）
        if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_NOT) {
            //未报名，报名参赛
            ll_mtt_control_btn.setVisibility(View.VISIBLE);
            tv_match_bottom_status_center.setVisibility(View.GONE);
            tv_match_bottom_status_top.setVisibility(View.GONE);
            if (canCheckIn) {
                btn_game_checkin.setText(R.string.game_mtt_checkin);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
            } else {
                //不可以报名（人满）
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
            //已报名
            ll_mtt_control_btn.setVisibility(View.VISIBLE);
            tv_match_bottom_status_top.setVisibility(View.GONE);//tv_match_bottom_status_top.setVisibility(View.VISIBLE);  "比赛前1分钟无法取消报名"的提示不显示
            tv_match_bottom_status_center.setVisibility(View.GONE);
            //比赛未开始，取消报名
            btn_game_checkin.setText(R.string.game_mtt_checkin_cancel);
//            btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin_cancel);
            tv_match_bottom_status_top.setText(R.string.game_mtt_cehckin_cancel_10min);
            if ((manulStart - getCurrentTime()) <= 1 * 62 && manulStart > 0) {//多加2秒  嘿嘿
                //比赛开始前10分钟，报名按钮灰色
                if (is_auto || manulStart > 0) {//// : 17/2/16 "或"加上一个新字段表示已经点击开赛
                    btn_game_checkin.setEnabled(false);
                } else {
                    btn_game_checkin.setEnabled(true);
                }
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {
            // : 17/2/28  等待报名报名已经updateWaitForCheckin()函数里面处理;
//            ll_mtt_control_btn.setVisibility(View.GONE);
//            tv_match_bottom_status_center.setVisibility(View.VISIBLE);
//            tv_match_bottom_status_center.setText(R.string.game_mtt_checkin_dealing);
//            if (!canCheckIn) {
//                //不可以报名（人满）
//                ll_mtt_control_btn.setVisibility(View.GONE);
//                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
//                tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
//            }
        }
    }

    public void updateMttIngStatusUI(boolean canCheckIn, boolean isOpenGame) {
        //1.可以报名（已报，未报）   2.不可以报名（决赛，盲注到达终止级别，人数已满）
        if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_NOT) {
            if (canCheckIn) {
                //未报名，报名参赛
                ll_mtt_control_btn.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setVisibility(View.GONE);
                tv_match_bottom_status_top.setVisibility(View.GONE);
                tv_match_status_final.setVisibility(View.GONE);
                btn_game_checkin.setText(R.string.game_mtt_checkin);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
                tv_match_bottom_status_center_prompt.setVisibility(View.GONE);
            } else {
                //未报名，不能报名
                ll_mtt_control_btn.setVisibility(View.GONE);
                if (matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL_REST || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_REST_FINISH || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL) {
                    //
                    tv_match_bottom_status_center.setVisibility(View.GONE);
                    tv_match_status_final.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center_prompt.setVisibility(View.GONE);
                } else if (matchStatus.currentLevel >= ((BaseMttConfig) gameInfo.gameConfig).matchLevel) {
                    tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center.setText(R.string.match_game_checkin_cutoff);
                    tv_match_bottom_status_center_prompt.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center_prompt.setText(getString(R.string.match_game_current_level, matchLevel));
                } else {
                    tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
                    tv_match_bottom_status_center_prompt.setVisibility(View.GONE);
                }
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
            tv_match_bottom_status_center_prompt.setVisibility(View.GONE);
            if (isOpenGame) {
                openGame("", "", false);
                if (matchStatus.score <= 0) {
                    //由于自己的收到通过报名通知后马上进游戏的，此时不会去拉去Score，故Score=0,此时赋值，让它不是被淘汰的状态
                    matchStatus.score = (1);
                }
            }
            if (matchStatus.score <= 0) {
                //已经淘汰（是否是决赛桌）
                isAreadyInMatch = false;
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_top.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                if (matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL_REST || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_REST_FINISH || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL) {
                    tv_match_bottom_status_center.setVisibility(View.GONE);
                    tv_match_status_final.setVisibility(View.VISIBLE);
                } else {
                    tv_match_status_final.setVisibility(View.GONE);
                    tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center.setText(R.string.match_game_mystatus_weedout);
                    btn_game_checkin.setOnClickListener(this);
                    if (canCheckIn && matchStatus.currentLevel < ((BaseMttConfig) gameInfo.gameConfig).matchLevel && canRebuy()) {
                    //被淘汰后如果满足条件：1没进入决赛；2能报名；3没有达到终止报名级别；4重构次数没有用完    那么"报名"显示"复活"
                        tv_match_bottom_status_center.setVisibility(View.GONE);
                        ll_mtt_control_btn.setVisibility(View.VISIBLE);
                        btn_game_checkin.setText("复活");
                        btn_game_checkin.setEnabled(true);
                        btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
                        btn_game_checkin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int myCoin = UserPreferences.getInstance(getApplicationContext()).getCoins();
                                int myDiamond = UserPreferences.getInstance(getApplicationContext()).getDiamond();
                                boolean coinInsufficient = gameInfo.match_type == 0 && myCoin < checkInMayFee;
                                boolean diamondInsufficient = gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND && myDiamond < checkInMayFee;
                                if (coinInsufficient || diamondInsufficient) {
                                    showTopUpDialog();
                                    return;
                                }
                                showRevivalDialog();
                            }
                        });
                    }
                }
            } else {
                //比赛开始，回到游戏
                isAreadyInMatch = true;
                ll_mtt_control_btn.setVisibility(View.VISIBLE);
                btn_game_checkin.setVisibility(View.VISIBLE);
                tv_match_bottom_status_top.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setVisibility(View.GONE);
                tv_match_status_final.setVisibility(View.GONE);
                tv_match_bottom_status_top.setText(R.string.match_game_mystatus_ingame);
                btn_game_checkin.setEnabled(true);
                btn_game_checkin.setText(R.string.game_mtt_back_game);
                btn_game_checkin.setOnClickListener(this);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
            }
            //
            if (buyTypeStatus == GameMatchBuyType.TYPE_BUY_REBUY || buyTypeStatus == GameMatchBuyType.TYPE_BUY_REBUY_WEEDOUT || buyTypeStatus == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {
                isAreadyInMatch = false;
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setText(R.string.game_mtt_rebuy_dealing);
            } else if (buyTypeStatus == GameMatchBuyType.TYPE_BUY_ADDON || buyTypeStatus == GameMatchBuyType.TYPE_BUY_ADDON_WEEDOUT) {
                isAreadyInMatch = false;
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setText(R.string.game_mtt_addon_dealing);
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {
            //报名处理中   // : 17/2/28  等待报名报名已经updateWaitForCheckin()函数里面处理;
//            ll_mtt_control_btn.setVisibility(View.GONE);
//            tv_match_bottom_status_top.setVisibility(View.GONE);
//            tv_match_bottom_status_center_prompt.setVisibility(View.GONE);
//            tv_match_bottom_status_center.setVisibility(View.VISIBLE);
//            tv_match_bottom_status_center.setText(R.string.game_mtt_checkin_dealing);
        }
    }

    public void showRevivalDialog() {
        String moneyType = gameInfo != null && gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND ? "钻石" : "扑克币";
        String message = "确定花费" + checkInMayFee + moneyType + "复活比赛?";
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", message, getString(R.string.game_mtt_revival), getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        //// : 17/2/7 复活接口
                        mGameMatchAction.revivalInMatch(gameInfo.joinCode, DemoCache.getAccount(), new GameRequestCallback() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                ll_mtt_control_btn.setVisibility(View.GONE);
                                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                                tv_match_bottom_status_center.setText(R.string.game_mtt_rebuy_dealing);
                                btn_game_checkin.setOnClickListener(MatchRoomActivity.this);
                                getGameMatchStatus();//调用复活接口
                            }
                            @Override
                            public void onFailed(int code, JSONObject json) {
                                if (code == ApiCode.CODE_MATCH_CHECKIN_ONLY_IN_ORIGINAL_CHANNEL) {
                                    if (json == null) {
                                        return;
                                    }
                                    try {
                                        JSONObject data = json.getJSONObject("data");
                                        String previousCode = data.optString("code");
                                        String admin_id = data.optString("admin_id");
                                        String nickName = NimUserInfoCache.getInstance().getUserDisplayName(admin_id);
                                        String normalStr = "”处报名，只能在此管理员通道复活，请返回复活，邀请码：";
                                        if (previousCode != null && previousCode.length() > 6) {//说明之前是在俱乐部渠道报的名
                                            String teamId = gameInfo.tid;
                                            String clubName = "";
                                            if (!TextUtils.isEmpty(teamId) && TeamDataCache.getInstance().getTeamById(teamId) != null) {
                                                clubName = TeamDataCache.getInstance().getTeamById(teamId).getName();
                                                if (!StringUtil.isSpace(clubName)) {
                                                    clubName = "“" + clubName + "”";
                                                }
                                            }
                                            normalStr = "”处报名，只能在此管理员通道复活，请返回俱乐部" + clubName + "复活";
                                            previousCode = "";
                                        }
                                        EasyAlertDialog dialog2 = EasyAlertDialogHelper.createOneButtonDiolag(MatchRoomActivity.this, null, "您已在管理员“" + nickName + normalStr + previousCode, getString(R.string.ok) , false, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                            }
                                        });
                                        if (!isFinishing() && !isDestroyedCompatible()) {
                                            dialog2.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_SCORE_INSUFFICIENT) {
                                    showInsufficientDialog();
                                } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                                    showTopUpDialog();
                                }
                            }
                        });
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    public void updateMtSngWaitStatusUI(boolean canCheckIn) {
        if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_NOT) {
            //未报名，报名参赛
            ll_mtt_control_btn.setVisibility(View.VISIBLE);
            tv_match_bottom_status_center.setVisibility(View.GONE);
            tv_match_bottom_status_top.setVisibility(View.GONE);
            if (canCheckIn) {
                btn_game_checkin.setText(R.string.game_mtt_checkin);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
            } else {
                //不可以报名（人满）
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
            //已报名
            ll_mtt_control_btn.setVisibility(View.VISIBLE);
            tv_match_bottom_status_top.setVisibility(View.GONE);
            tv_match_bottom_status_center.setVisibility(View.GONE);
            //比赛未开始，取消报名
            btn_game_checkin.setText(R.string.game_mtt_checkin_cancel);
//            btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin_cancel);
            btn_game_checkin.setEnabled(true);
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {
            ll_mtt_control_btn.setVisibility(View.GONE);
            tv_match_bottom_status_top.setVisibility(View.GONE);
            tv_match_bottom_status_center.setVisibility(View.VISIBLE);
            tv_match_bottom_status_center.setText(R.string.game_mtt_checkin_dealing);
        }
    }

    public void updateMtSngIngStatusUI(boolean canCheckIn, boolean isOpenGame) {
        if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_NOT) {
            if (canCheckIn) {
                //未报名，报名参赛
                ll_mtt_control_btn.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setVisibility(View.GONE);
                tv_match_bottom_status_top.setVisibility(View.GONE);
                tv_match_status_final.setVisibility(View.GONE);
                btn_game_checkin.setText(R.string.game_mtt_checkin);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
            } else {
                //未报名，不能报名
                ll_mtt_control_btn.setVisibility(View.GONE);
                if (matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL_REST || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_REST_FINISH || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL) {
                    //决赛桌
                    tv_match_bottom_status_center.setVisibility(View.GONE);
                    tv_match_status_final.setVisibility(View.VISIBLE);
                    tv_match_status_final.setText(R.string.match_game_final);
                    tv_match_status_final.setOnClickListener(this);
                } else {
                    tv_match_bottom_status_center.setVisibility(View.GONE);
                    tv_match_status_final.setVisibility(View.VISIBLE);
//                    tv_match_bottom_status_center.setVisibility(View.VISIBLE);
//                    tv_match_bottom_status_center.setText(R.string.match_game_checkin_full);
                    tv_match_status_final.setText(R.string.match_game_ing);
                    tv_match_status_final.setOnClickListener(null);
                }
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
            if (isOpenGame) {
                openGame("", "", false);
                if (matchStatus.score <= 0) {
                    //由于自己的收到通过报名通知后马上进游戏的，此时不会去拉去Score，故Score=0,此时赋值，让它不是被淘汰的状态
                    matchStatus.score = (1);
                }
            }
            if (matchStatus.score <= 0) {
                //已经淘汰（是否是决赛桌）
                isAreadyInMatch = false;
                ll_mtt_control_btn.setVisibility(View.GONE);
                tv_match_bottom_status_top.setVisibility(View.GONE);
                tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                if (matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL_REST || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_REST_FINISH || matchStatus.matchStatus == GameMatchStatus.GAME_STATUS_FINAL) {
                    tv_match_bottom_status_center.setVisibility(View.GONE);
                    tv_match_status_final.setVisibility(View.VISIBLE);
                } else {
                    tv_match_status_final.setVisibility(View.GONE);
                    tv_match_bottom_status_center.setVisibility(View.VISIBLE);
                    tv_match_bottom_status_center.setText(R.string.match_game_mystatus_weedout);
                }
            } else {
                //比赛开始，回到游戏
                isAreadyInMatch = true;
                ll_mtt_control_btn.setVisibility(View.VISIBLE);
                btn_game_checkin.setVisibility(View.VISIBLE);
                tv_match_bottom_status_top.setVisibility(View.VISIBLE);
                tv_match_bottom_status_center.setVisibility(View.GONE);
                tv_match_status_final.setVisibility(View.GONE);
                tv_match_bottom_status_top.setText(R.string.match_game_mystatus_ingame);
                btn_game_checkin.setEnabled(true);
                btn_game_checkin.setText(R.string.game_mtt_back_game);
                btn_game_checkin.setBackgroundResource(R.drawable.btn_match_checkin);
            }
        } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {
            //报名处理中
            ll_mtt_control_btn.setVisibility(View.GONE);
            tv_match_bottom_status_top.setVisibility(View.GONE);
            tv_match_bottom_status_center.setVisibility(View.VISIBLE);
            tv_match_bottom_status_center.setText(R.string.game_mtt_checkin_dealing);
        }
    }

    private void initAction() {
        mGameMatchAction = new GameMatchAction(this, null);
        mBuyChipsAction = new BuyChipsAction(this, null);
        mGameAction = new GameAction(this, null);
        mClubAction = new ClubAction(this, null);
    }

    private void fetchRoomInfo() {
        final String roomId = roomInfo != null ? roomInfo.getRoomId() : "";
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo param) {
                LogUtil.i(TAG, "roomId : " + roomId + ";updateRoomInfo");
                roomInfo = param;
                updateRoomInfo(true);
                if (mMatchInfoFragment != null && gameInfo != null) {
                    mMatchInfoFragment.updateInfoUI(gameInfo.status, gameInfo.joinCode);
                }
//                getGameMatchStatus();
            }

            @Override
            public void onFailed(int code) {
                LogUtil.i(TAG, "fetch room info failed:" + code);
            }

            @Override
            public void onException(Throwable exception) {
                LogUtil.i(TAG, "fetch room info exception:" + exception);
            }
        });
    }

    public void openGame(String lookUid, String lookTableIndex, boolean isClickList) {
        int ko_mode = 0;
        if (gameInfo != null && gameInfo.gameConfig instanceof  BaseMttConfig) {
            ko_mode = ((BaseMttConfig) gameInfo.gameConfig).ko_mode;
        }
        if (isClickList && isAreadyInMatch) {
            //在比赛中，并且点击列表进入
            showBackGameDialog();
        } else {
            PokerActivity.startGameByWatch(MatchRoomActivity.this, gameInfo, roomId,  lookUid, lookTableIndex, ko_mode);
        }
        if (dismissGameDialog != null && dismissGameDialog.isShowing()) {
            dismissGameDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    private boolean mHasRegistered = false;
    @Override
    protected void onDestroy() {
        DialogMaker.dismissProgressDialog();
        if (mMatchMgrFragment != null) {
            mMatchMgrFragment.registerObservers(false);
        }
        MainActivity.sMatchRA = null;
        if (mHasRegistered) {
            registerObservers(false);
        }
//        stopCountDownTimer();
//        mCountDownTimer = null;
        if (mShareView != null) {
            mShareView.onDestroy();
            mShareView = null;
        }
        mShareCodeView = null;
        if (shareCodeBitmap != null) {
            shareCodeBitmap.recycle();
            shareCodeBitmap = null;
        }
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        logoutChatRoom();
        if (mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
        if (mGameMatchAction != null) {
            mGameMatchAction.onDestroy();
            mGameMatchAction = null;
        }
        if (mBuyChipsAction != null) {
            mBuyChipsAction.onDestroy();
            mBuyChipsAction = null;
        }
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        LogUtil.i(TAG, "onDestroy");
        super.onDestroy();
    }

    //报名
    public void checkInMatch() {
        if (gameInfo == null) {
            return;
        }
        int myCoin = UserPreferences.getInstance(getApplicationContext()).getCoins();
        int myDiamond = UserPreferences.getInstance(getApplicationContext()).getDiamond();
        boolean coinInsufficient = gameInfo.match_type == 0 && myCoin < checkInMayFee;
        boolean diamondInsufficient = gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND && myDiamond < checkInMayFee;
        if (coinInsufficient || diamondInsufficient) {
            showTopUpDialog();
            return;
        }
        mGameMatchAction.checkInMatch(gameInfo.joinCode, new GameRequestCallback() {//game/mttcheckin
            @Override
            public void onSuccess(JSONObject response) {
                if (creatorId.equals(DemoCache.getAccount())) {
                    //如果是房主，报名成功
                    myCheckInStatus = CheckInStatus.CHECKIN_STATUES_ED;
                    if (matchStatus != null) {
                        matchStatus.score = (1);
                    }
                    //比赛开始，带入游戏
                    updateMatchStatusBottomUI(myCheckInStatus, true);
                } else {
                    //如果是玩家，并且是控制带入，等待通过
//                        myCheckInStatus = CheckInStatus.CHECKIN_STATUES_DEALING;
                    updateMatchStatusBottomUI(myCheckInStatus, false);
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_NOTEXIST) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_exist, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_ALREADY) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_aready, Toast.LENGTH_SHORT).show();
                    if (gameStatus == GameStatus.GAME_STATUS_START && code == ApiCode.CODE_MATCH_CHECKIN_ALREADY) {
                        int ko_mode = 0;
                        if (gameInfo != null && gameInfo.gameConfig instanceof  GameMttConfig) {
                            ko_mode = ((BaseMttConfig) gameInfo.gameConfig).ko_mode;
                        }
                        PokerActivity.startGameByPlay(MatchRoomActivity.this, gameInfo, roomId, ko_mode);
                    }
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_BLINDLEVEL_REACH) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_blindlevel_reach, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_FINAL) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_final, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_DEALING) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_dealing, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CUTOFF) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_cutoff, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CANT) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_cant, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_PLAYER_FULL) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_player_full, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_ADMIN_FORBID) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_failure_admin_forbid, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_START_MOMENT) {
                    showThirtyLimitDialog("比赛即将开始\n此段时间内暂停报名");
                } else if (code == ApiCode.CODE_GAME_NONE_EXISTENT || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                    EasyAlertDialog channelOrGameNotExitDialog = EasyAlertDialogHelper.createOneButtonDiolag(MatchRoomActivity.this, "", "当前比赛通道已被删除，\n" +
                            "请输入其他邀请码加入比赛", getString(R.string.ok), false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    if (!isFinishing() && !isDestroyedCompatible()) {
                        channelOrGameNotExitDialog.show();
                    }
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_SCORE_INSUFFICIENT) {//报名 3096 上分控制，积分不足
                    showInsufficientDialog();
                } else if (code == ApiCode.CODE_BALANCE_INSUFFICIENT) {
                    showTopUpDialog();
                } else {
                    String message = ApiResultHelper.getShowMessage(response);
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.game_match_checkin_failure);
                    }
                    Toast.makeText(ChessApp.sAppContext, ChessApp.sAppContext.getString(R.string.game_match_checkin_failure), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    EasyAlertDialog vipDialog;
    public void showInsufficientDialog() {
        if (vipDialog == null) {
            vipDialog = EasyAlertDialogHelper.createOkCancelDiolag(MatchRoomActivity.this, "",
                    "该俱乐部带入分已用尽", getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }
                        @Override
                        public void doOkAction() {
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            vipDialog.show();
        }
    }

    //取消报名
    public void cancelCheckInMatch() {
        if (gameInfo == null) {
            return;
        }
        mGameMatchAction.cancelCheckInMatch(gameInfo.code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                myCheckInStatus = CheckInStatus.CHECKIN_STATUES_NOT;
                updateMatchStatusBottomUI(myCheckInStatus, false);
            }
            @Override
            public void onFailed(int code, JSONObject json) {
                if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CUTOFF) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_cancel_failure_cutoff, Toast.LENGTH_SHORT).show();
                } else if(code == ApiCode.CODE_GAME_IS_BEGIN) {
                    Toast.makeText(ChessApp.sAppContext, R.string.game_match_checkin_cancel_failure_areadystart, Toast.LENGTH_SHORT).show();
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND || code == ApiCode.CODE_GAME_NONE_EXISTENT) {
                    EasyAlertDialog gameOverDialog = EasyAlertDialogHelper.createOneButtonDiolag(MatchRoomActivity.this, null, "该渠道不存在或者已经被房主删除，请在其它渠道下取消报名", getString(R.string.ok)
                            , false, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    RequestTimeLimit.lastGetRecentGameTime = (0);
                                    RequestTimeLimit.lastGetGameListInClub = 0;
                                    RequestTimeLimit.lastGetGameListInHorde = 0;
                                    RequestTimeLimit.lastTeamRecord = 0;
                                    if (gameInfo != null) {
                                        if ((gameInfo.code != null && gameInfo.code.length() > 6) ||
                                                (gameInfo.joinCode != null && gameInfo.joinCode.length() > 6) ||
                                                (gameInfo.channel != null && gameInfo.channel.length() > 6)) {
                                            RequestTimeLimit.lastGetGamePlayingTime = 0;
                                        }
                                    }
                                    finish();
                                }
                            });
                    if (!isFinishing() && !isDestroyedCompatible()) {
                        gameOverDialog.show();
                    }
                } else if (code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_SCORE_RETURN) {
                    Toast.makeText(ChessApp.sAppContext, "返还积分失败，取消报名失败", Toast.LENGTH_SHORT).show();
                } else {
                    String message = ApiResultHelper.getShowMessage(json);
                    if (TextUtils.isEmpty(message)) {
                        message = ChessApp.sAppContext.getString(R.string.game_match_checkin_cancel_failure);
                    }
                    Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //控制带入（如果是比赛模式的报名，走mGameMatchAction ， 如果是普通模式或者比赛模式的赠购买走mBuyChipsAction）
    public void doControlCheckIn(AppMessage appMessage, AppMessageStatus dealStatus, GameRequestCallback callback) {
        int dealAction = GameMatchAction.ACTION_REJECT;
        if (dealStatus == AppMessageStatus.passed) {
            dealAction = GameMatchAction.ACTION_AGREE;
        }
        if (appMessage.type == AppMessageType.GameBuyChips) {
            mBuyChipsAction.controlBuyIn(appMessage, dealAction, callback);//"game/controlin";//控制买入
        } else if (appMessage.type == AppMessageType.MatchBuyChips && appMessage.attachObject instanceof MatchBuyChipsNotify) {
            int buyType = ((MatchBuyChipsNotify) appMessage.attachObject).buyType;
            if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {
                mGameMatchAction.controlCheckIn(appMessage, dealAction, true, callback);//"game/mttpasscheckin";//比赛等级许可
            } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {//复活重购
                mGameMatchAction.controlReVivalIn(appMessage, dealAction, true, callback);//"game/matchRevivalPass";//是否同意或拒绝"复活"code uid player_id action 0->拒绝 1->同意
            } else {
                mBuyChipsAction.controlBuyIn(appMessage, dealAction, callback);//"game/controlin";//控制买入
            }
        }
    }

    /**
     * 暂停比赛
     *
     * @param actionType
     */
    public void doPauseMatch(final int actionType) {
        mGameMatchAction.pauseMttGame(gameInfo.code, actionType, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                LogUtil.i(TAG, "doPauseMatch onSuccess response:" + (response == null ? "null" : response.toString()) + "        actionType: " + actionType);
                if (actionType == GameMatchAction.TYPE_START) {
                    if (matchStatus != null) {
                        matchStatus.matchInRest = (MatchConstants.MATCH_IN_REST_NOT);
                    }
                    matchStatus.matchPauseTime = (0);//置为0
                    if (advancedFunctionView != null) {
                        advancedFunctionView.setPause(false);
                    }
                    mMatchTipView.setVisibility(View.GONE);

                } else if (actionType == GameMatchAction.TYPE_PAUSE) {
                    if (matchStatus != null) {
                        matchStatus.matchInRest = (MatchConstants.MATCH_IN_REST);
                    }

                    if (advancedFunctionView != null) {
                        advancedFunctionView.setPause(false);
                    }
                    mMatchTipView.showPauseBefore();
                }
            }

            @Override
            public void onFailed(int code, JSONObject response) {
                LogUtil.i(TAG, "doPauseMatch onFailed code:" + code + "        actionType: " + actionType);
                if (code == ApiCode.CODE_MATCH_PAUSE_AREADY) {
                    android.widget.Toast.makeText(ChessApp.sAppContext,R.string.game_had_paused, android.widget.Toast.LENGTH_SHORT).show();
                    //已经暂停
                    if (matchStatus != null) {
                        matchStatus.matchInRest = (MatchConstants.MATCH_IN_REST);
                    }
                    if(advancedFunctionView != null)
                        advancedFunctionView.setPause(false);
                    mMatchTipView.showPauseBefore();
                } else if (code == ApiCode.CODE_MATCH_PAUSE_NOT) {
                    //比赛没有暂停，继续开始比赛
                    matchStatus.matchPauseTime = (0);
                    if (matchStatus != null) {
                        matchStatus.matchInRest = (MatchConstants.MATCH_IN_REST_NOT);
                    }
                    if (advancedFunctionView != null)
                        advancedFunctionView.setPause(false);
                    mMatchTipView.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onCurrent() {
        if (!isFastClick()) {

        }
    }

    /**
     * 频率控制，至少间隔一分钟
     *
     * @return
     */
    private boolean isFastClick() {
        long current = System.currentTimeMillis();
        long time = current - lastClickTime;
        if (0 < time && time < 60000) {
            return true;
        }
        lastClickTime = current;
        return false;
    }

    private void registerObservers(boolean register) {
        mHasRegistered = register;
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
        if (register) {
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
        } else {
            unregisterReceiver(mAppMessageReceiver);
        }
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            // 处理新收到的消息
            LogUtil.i(TAG, "messages size : " + messages.size());
            for (ChatRoomMessage message : messages) {
                if (message.getAttachment() instanceof ChatRoomNotificationAttachment) {
                    ChatRoomNotificationAttachment chatRoomNotificationAttachment = (ChatRoomNotificationAttachment) message.getAttachment();
                    if (chatRoomNotificationAttachment.getType() == NotificationType.ChatRoomInfoUpdated) {
                        LogUtil.i(TAG, "ChatRoomInfoUpdated");
//                        mMatchInfoFragment.updateMatchStatus();
                        fetchRoomInfo();
                        break;
                    }
                }
            }
        }
    };

    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
//                int code = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(roomId);
//                if (code != ResponseCode.RES_ECONNECTION) {
//                    Toast.makeText(ChessApp.sAppContext, "未登录,code=" + code, Toast.LENGTH_LONG).show();
//                }
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
//                Toast.makeText(ChessApp.sAppContext, R.string.net_broken, Toast.LENGTH_SHORT).show();
            }
            com.netease.nim.uikit.common.util.log.LogUtil.i(TAG, "chat room online status changed to " + chatRoomStatusChangeData.status.name());
        }
    };

    private boolean mGameOver = false;
    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            LogUtil.i(TAG, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason() + "   chatRoomKickOutEvent.getRoomId(): " + chatRoomKickOutEvent.getRoomId() + " roomId:" + roomId);
            sendCancelGameOrGameOverMessage(NewGameReceiver.ACTION_CANCELGAME_TYPE);
            if (chatRoomKickOutEvent.getRoomId().equals(roomId)) {
                mGameOver = true;
                LogUtil.i(TAG, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason());
                clearChatRoom();
                if (isInitiativeDismiss && DemoCache.getAccount().equals(creatorId)) {
                    LogUtil.i(TAG, "房主主动解散");
                    //房主主动解散
                } else {
                    //系统解散
                    showGameOverDialog();
                }
            }
        }
    };

    private String gameJson;
    private void sendCancelGameOrGameOverMessage(int gameActionType) {//向俱乐部里面发送创建游戏或者解散游戏的消息
        RequestTimeLimit.lastGetGameListInClub = 0;
        RequestTimeLimit.lastGetGameListInHorde = 0;
        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
        intent.putExtra(NewGameReceiver.ACTION_NEWGAME, gameActionType);
        intent.putExtra(Extras.EXTRA_DATA, gameJson);
        if (gameInfo != null) {
            if ((gameInfo.code != null && gameInfo.code.length() > 6) ||
                    (gameInfo.joinCode != null && gameInfo.joinCode.length() > 6) ||
                    (gameInfo.channel != null && gameInfo.channel.length() > 6)) {
                RequestTimeLimit.lastGetGamePlayingTime = 0;
            }
        }
        sendBroadcast(intent);
    }

    private boolean mgrFrgHasAdded = false;//管理员fragment是否已经加到viewpager中，这个fragment是异步的
    MatchMgrFragment mMatchMgrFragment;
    private void initHead() {
        setHeadTitle(gameInfo != null ? gameInfo.name : "");
        //如果进入大厅前输入房主的原始code，那么强制转换为自己管理员的code
        if (gameInfo != null) {
            if (isMeGameCreator()) {
                gameInfo.channel = gameInfo.code;
                channel = gameInfo.code;
                if (mMatchInfoFragment != null) {
                    mMatchInfoFragment.updateInfoUI(gameStatus, gameInfo.joinCode);// : 17/2/28   新版本需求，不改变显示的code
                }
                if (mMatchMgrFragment != null) {
                    mMatchMgrFragment.updateGameInfo(gameInfo);
                }
            } else if (isMeGameManager()) {
                ArrayList<UserEntity> list = ChessApp.gameManagers.get(gameInfo.gid);
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        UserEntity bean = list.get(i);
                        if (bean.account.equals(DemoCache.getAccount())) {
                            gameInfo.channel = bean.channel;
                            channel = bean.channel;
                            if (mMatchInfoFragment != null) {
                                mMatchInfoFragment.updateInfoUI(gameStatus, gameInfo.joinCode);// : 17/2/28   新版本需求，不改变显示的code
                            }
                            if (mMatchMgrFragment != null) {
                                mMatchMgrFragment.updateGameInfo(gameInfo);
                            }
                            break;//改变code的显示
                        }
                    }
                }
            } else if(!creatorId.equals(DemoCache.getAccount())) {
                setHeadRightButtonGone();
                if (mgrFrgHasAdded) {
                    fragmentList.remove(mMatchMgrFragment);
                    mMttPagerAdapter.notifyDataSetChanged();
                    tabs.notifyDataSetChanged();
                    mgrFrgHasAdded = false;
//                    gameInfo.code = gameInfo.originalCode;
                    if (mMatchInfoFragment != null) {
                        mMatchInfoFragment.updateInfoUI(gameStatus, gameInfo.joinCode);// : 17/2/28   新版本需求，不改变显示的code    //改变code的显示
                    }
                }
            }
        }
        if (creatorId.equals(DemoCache.getAccount()) || isMeGameManager()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕长亮
            if (!mgrFrgHasAdded) {
                mMatchMgrFragment = MatchMgrFragment.newInstance(gameInfo);
                fragmentList.add(mMatchMgrFragment);
                mMttPagerAdapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();
                mgrFrgHasAdded = true;
            }
        }
    }

    private void initFragment() {
        mMatchInfoFragment = MatchInfoFragment.newInstance(null);
        mMatchPlayerFragment = MatchPlayerFragment.newInstance(/*gameInfo*/null, roomId, /*gameInfo.gid*/"", /*matchChips*/0);
        mMatchTableFragment = MatchTableFragment.newInstance(matchPlayer);
        fragmentList.add(mMatchInfoFragment);
        fragmentList.add(mMatchPlayerFragment);
        fragmentList.add(mMatchTableFragment);
    }

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        clearChatRoom();
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
    }

//    private void onLoginDone() {
//        enterRequest = null;
//        DialogMaker.dismissProgressDialog();
//    }

    public GameEntity getGameInfo() {
        return gameInfo;
    }

    private void initTabLayout() {
        //设置TabLayout的模式
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        //
        titleList = new ArrayList<>();
        titleList.add(getString(R.string.game_match_tab_status));
        titleList.add(getString(R.string.game_match_tab_member));
        titleList.add(getString(R.string.game_match_tab_table));
        titleList.add(getString(R.string.match_manage));
        titleList.add(getString(R.string.game_match_tab_chat));
        //
        iconList = new ArrayList<>();
        iconList.add(R.mipmap.message_system);//icon_mtt_tab_info);
        iconList.add(R.mipmap.message_system);//icon_mtt_tab_player);
        iconList.add(R.mipmap.message_system);//icon_mtt_tab_table);
        iconList.add(R.mipmap.message_system);//icon_mtt_tab_reward);
        iconList.add(R.mipmap.message_system);//icon_mtt_tab_chat);
        //为TabLayout添加tab名称
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(3)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titleList.get(4)));
        //
        mMttPagerAdapter = new MatchPagerAdapter(getSupportFragmentManager(), fragmentList, titleList, iconList);
        mViewPager.setOffscreenPageLimit(mMttPagerAdapter.getCount());
        // page swtich animation
//        viewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        //viewpager加载adapter
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setSwipeBackEnable(position == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mMttPagerAdapter);
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        mTabLayout.setupWithViewPager(mViewPager);
        //tab_FindFragment_title.set
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);
        tabs.setAnimationView(rl_mtt_bottom, tv_game_status);
    }

    public void getNetTime() {
        LogUtil.i(TAG, "getNetTime");
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                mMatchRequestHelper.getTime();
            }
        });
    }

    private boolean gameWillStart;//以前是开赛后才动态刷status，现在改为gameWillStart=true就动态刷status
    public void getGameMatchStatus() {
        //1秒获取一次当前比赛状态是正确的，不要等30秒，这样暂停及时性很差
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                if (mMatchRequestHelper != null) {
                    mMatchRequestHelper.getMyStatus();
                }
            }
        });
    }

    //10秒刷新玩家列表
    public void getPlayerList() {
        final long currentTime = getCurrentTime();
        if (mMatchRequestHelper == null || currentTime - lastGetPlayerTime <= 10) {
            return;
        }
//        mMatchPlayerFragment.notifyDataSetChanged();
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
//                isNewPlayerRequst = true;
                lastGetPlayerTime = currentTime;
//                currentPlayerSequence = String.valueOf(getCurrentTime());
                if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                    mMatchRequestHelper.getPlayerList(MatchRequestHelper.TYPE_PLAYER_PLAYER, currentPlayerSequence, mMatchPlayerFragment == null ? 1 : mMatchPlayerFragment.rank_type);
                } else if (gameStatus == GameStatus.GAME_STATUS_START) {
                    mMatchRequestHelper.getPlayerList(MatchRequestHelper.TYPE_PLAYER_RANK, currentPlayerSequence, mMatchPlayerFragment == null ? 1 : mMatchPlayerFragment.rank_type);
                }
            }
        });
    }

    //10秒刷新管理页面的玩家列表，跟"玩家"页面逻辑不一样，停留在"玩家"页面其数据不会变化，但是停留在"管理"页面数据10秒动态变化一次
    public void getMgrPlayerList() {
        final long currentTime = getCurrentTime();
        if (mMatchRequestHelper == null || (currentTime - lastGetMgrPlayerTime) <= 10 || StringUtil.isSpace(gameInfo == null ? "" : gameInfo.channel)) {
            if (mMatchMgrFragment != null && mMatchMgrFragment.refresh_layout != null) {
                mMatchMgrFragment.refresh_layout.setRefreshing(false);
            }
            return;
        }
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                lastGetMgrPlayerTime = currentTime;
                mMatchRequestHelper.getMgrPlayerList(gameInfo.channel, currentMgrPlayerSequence, mMatchMgrFragment == null ? 1 : mMatchMgrFragment.rank_type);
            }
        });
    }
//    public void getPlayListMore() {//玩家列表页面点击"更多"按钮显示所有钱100名玩家
//        final long currentTime = getCurrentTime();
////        mMatchPlayerFragment.notifyDataSetChanged();
//        ThreadUtil.Execute(new Runnable() {
//            @Override
//            public void run() {
//                isNewPlayerRequst = true;
//                lastGetPlayerTime = currentTime;
//                currentPlayerSequence = String.valueOf(getCurrentTime());
//                getPlayListMore = true;
//                if (gameStatus == GameConstants.GAME_STATUES_WAIT) {
//                    mMatchRequestHelper.getPlayerList(MatchRequestHelper.TYPE_PLAYER_PLAYER, currentPlayerSequence, getPlayListMore);
//                } else if (gameStatus == GameConstants.GAME_STATUES_START) {
//                    mMatchRequestHelper.getPlayerList(MatchRequestHelper.TYPE_PLAYER_RANK, currentPlayerSequence, getPlayListMore);
//                }
//            }
//        });
//    }

    public void getTableList() {
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                if (mMatchRequestHelper != null) {
                    mMatchRequestHelper.getTableList();
                }
            }
        });
    }

    private void initView() {
        rl_tabs = findViewById(R.id.rl_tabs);
        changePineappleBg();
        websocket_loading_container = findViewById(R.id.websocket_loading_container);
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);
        btn_game_invite = (Button) findViewById(R.id.btn_game_invite);
        tv_new_notify = (TextView) findViewById(R.id.tv_new_notify);
        mMatchTipView = (MatchTipView) findViewById(R.id.mMatchTipView);
        mMatchTipView.setInfo(creatorId);
        mMatchTipView.setMatchContinueClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContinueDialog();
            }
        });
        mMatchTipView.setVisibility(View.GONE);
        //
        rl_mtt_bottom = (RelativeLayout) findViewById(R.id.rl_mtt_bottom);
//        rl_mtt_control = (RelativeLayout) findViewById(R.id.rl_mtt_control);//这个组件优化时删掉了
        ll_mtt_control_btn = (LinearLayout) findViewById(R.id.ll_mtt_control_btn);
        btn_game_checkin = (Button) findViewById(R.id.btn_game_checkin);
        btn_game_checkin.setOnClickListener(this);
        btn_game_invite.setOnClickListener(this);
        //
        tv_game_status = (TextView) findViewById(R.id.tv_game_status);
        tv_match_bottom_status_top = (TextView) findViewById(R.id.tv_match_bottom_status_top);
        tv_match_bottom_status_center = (TextView) findViewById(R.id.tv_match_bottom_status_center);
        tv_match_bottom_status_center_prompt = (TextView) findViewById(R.id.tv_match_bottom_status_center_prompt);
        tv_match_status_final = (TextView) findViewById(R.id.tv_match_status_final);
        tv_match_status_final.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG + " onPause ");
        getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//取消屏幕长亮
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG + " onStop ");
        if (mMatchRequestHelper != null) {
            mMatchRequestHelper.hasOnStop = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMatchRequestHelper != null) {
            mMatchRequestHelper.hasOnStop = false;
        }
        getGameMatchStatus();//onresume调用一次
        LogUtil.i(TAG + " onResume ");
        mRemaindPauseTime = 0;//需要置为0
        reConnectWebSocket();
        if (gameInfo == null || StringUtil.isSpace(gameInfo.code) || StringUtil.isSpace(gameInfo.gid)) {
            return;
        }
        setMgrFragment();
        if (creatorId.equals(DemoCache.getAccount()) || isMeGameManager()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕长亮
        }
    }

    private void setMgrFragment() {
        if (gameInfo == null) {
            return;
        }
        mGameAction.getMgrList(gameInfo.gid, creatorId, gameInfo.code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                initHead();
            }
            @Override
            public void onFailed(int code, JSONObject response) {
                initHead();
            }
        });//获取赛事管理员列表
        if (creatorId.equals(DemoCache.getAccount())) {
            if (!mgrFrgHasAdded) {
                mMatchMgrFragment = MatchMgrFragment.newInstance(gameInfo);
                fragmentList.add(mMatchMgrFragment);
                mMttPagerAdapter.notifyDataSetChanged();
                tabs.notifyDataSetChanged();
                mgrFrgHasAdded = true;
            }
        }
    }

    public void reConnectWebSocket() {
        if (mGameOver || !NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            return;
        }
        currentPlayerSequence = 0;
        currentMgrPlayerSequence = 0;
        if (mWebSocketClient != null && mWebSocketClient.getConnection() != null) {
            WebSocket webSocket = mWebSocketClient.getConnection();
            if (webSocket.isClosed()) {
                try {
                    initWebSocket();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (webSocket.isOpen()) {
            }
        } else {
            try {
                initWebSocket();
                LogUtil.i(TAG, "initWebSocket");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void searchPlayer(String key) {
        mMatchRequestHelper.searchPlayer(key);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mShareView != null) {
            mShareView.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == REQUEST_CODE_INVITE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selects = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            String inviteContent = new JSONObject(roomInfo == null ? new HashMap() : roomInfo.getExtension()).toString();
            LogUtil.i(TAG, inviteContent);
            GameAttachment attachment = new GameAttachment(inviteContent);
            //
            String tipContent = String.format(getString(R.string.game_inivite_tip), NimUserInfoCache.getInstance().getUserDisplayName(DemoCache.getAccount()));
            //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
            for (String account : selects) {
                final IMMessage tipMessage = MessageBuilder.createTipMessage(account, SessionTypeEnum.P2P);
                tipMessage.setContent(tipContent);
                tipMessage.setConfig(MessageConfigHelper.getTipMessageConfig(tipMessage.getConfig()));
                IMMessage message = MessageBuilder.createCustomMessage(account, SessionTypeEnum.P2P, getString(R.string.msg_custom_type_game_create_desc), attachment);
                NIMClient.getService(MsgService.class).sendMessage(tipMessage, false).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        NIMClient.getService(MsgService.class).deleteChattingHistory(tipMessage);//删除
                    }

                    @Override
                    public void onFailed(int i) {
                        NIMClient.getService(MsgService.class).deleteChattingHistory(tipMessage);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        NIMClient.getService(MsgService.class).deleteChattingHistory(tipMessage);
                    }
                });//邀请提示的
                NIMClient.getService(MsgService.class).sendMessage(message, false);//牌局内容
            }
            //邀请好友加入牌局
            mGameAction.doInviteFriedns(gameInfo.code, selects);
            Toast.makeText(ChessApp.sAppContext, R.string.game_inivite_send_success, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
        } else if (requestCode == EditMatchStateAC.Companion.getREQUEST_CODE() && resultCode == Activity.RESULT_OK) {
            if (mMatchInfoFragment != null) {
                String remark_content = data.getStringExtra(EditMatchStateAC.Companion.getKEY_PREVIOUS_STATE());
                String remark_pic_list = data.getStringExtra(EditMatchStateAC.Companion.getKEY_PREVIOUS_PIC_LIST());
                mMatchInfoFragment.updateDialogMatchState(remark_content, remark_pic_list);
            }
        }
    }

    public void postShare() {
        if (mShareView == null) {
            mShareView = new ShareView(this);
            mShareView.setOnCallOverback(new ShareView.OnCallOverback() {
                @Override
                public void showShadow(boolean show) {
                    if (show) {
                        findViewById(R.id.view_shadow).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.view_shadow).setVisibility(View.GONE);
                    }
                }
            });
        }
        if (shareCodeBitmap == null) {
            //保存验证码生成的效果图
//            shareCodeBitmap = mShareView.convertViewToBitmap(mShareCodeView);
//            mShareView.setShareBitmap(shareCodeBitmap);//  16/12/27 图片功能去掉
        }
        mShareView.gameEntity = gameInfo;
        mShareView.show();
    }

    public void showCancelCheckInDialog() {
        String message = getString(R.string.game_mtt_cehckin_cancel_dialog_message);
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                message, getString(R.string.yes), getString(R.string.no), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        cancelCheckInMatch();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    EasyAlertDialog topUpDialog;
    EasyAlertDialog dismissGameDialog;
    EasyAlertDialog checkInGameDialog;
    EasyAlertDialog backGameDialog;

    public void showTopUpDialog() {
        if (topUpDialog == null) {
            topUpDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(gameInfo.match_type == 0 ? R.string.game_create_topup_dialog_tip : R.string.game_create_topup_dialog_tip_diamond), getString(R.string.buy), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                            topUpDialog.dismiss();
                        }

                        @Override
                        public void doOkAction() {
                            ShopActivity.start(MatchRoomActivity.this, gameInfo.match_type == 0 ? ShopActivity.TYPE_SHOP_COIN : ShopActivity.TYPE_SHOP_DIAMOND);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            topUpDialog.show();
        }
    }

    public void showDismissDialog() {
        if (dismissGameDialog == null) {
            dismissGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.match_game_dismiss_confim_dialog_message), getString(R.string.ok), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            dismissMatchGame();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            dismissGameDialog.show();
        }
    }

    public void showBackGameDialog() {
        if (backGameDialog == null) {
            String message = getString(R.string.match_is_in_game);
            backGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    message, getString(R.string.match_back_game), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            openGame("", "", false);
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            backGameDialog.show();
        }
    }

    public void showGameOverDialog() {
//        String message = getString(R.string.match_game_dismiss_dialog_message);
//        if (gameStatus == GameConstants.GAME_STATUES_WAIT) {
//            message = getString(R.string.match_game_gameover_dialog_message);
//        }
        String message = getString(R.string.match_game_gameover_dialog_message);
        if (gameStatus == 0) {
            message = getString(R.string.match_game_dismiss_dialog_message);
            if (DemoCache.getAccount().equals(creatorId)) {
                if (gameInfo.gameMode == GameConstants.GAME_MODE_MTT) {
                    message = getString(R.string.match_game_dismiss_insufficient_dialog_message);
                } else if (gameInfo.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                }
            }
        }
        EasyAlertDialog gameOverDialog = EasyAlertDialogHelper.createOneButtonDiolag(this, null, message, getString(R.string.ok)
                , false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RequestTimeLimit.lastGetRecentGameTime = (0);
                        RequestTimeLimit.lastGetGameListInClub = 0;
                        RequestTimeLimit.lastGetGameListInHorde = 0;
                        RequestTimeLimit.lastTeamRecord = 0;
                        if (gameInfo != null) {
                            if ((gameInfo.code != null && gameInfo.code.length() > 6) ||
                                    (gameInfo.joinCode != null && gameInfo.joinCode.length() > 6) ||
                                    (gameInfo.channel != null && gameInfo.channel.length() > 6)) {
                                RequestTimeLimit.lastGetGamePlayingTime = 0;
                            }
                        }
                        finish();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            gameOverDialog.show();
        }
    }

    public void showCheckInDialog() {
        if (checkInGameDialog == null) {
            String message = getString(gameInfo != null && gameInfo.match_type == GameConstants.MATCH_TYPE_DIAMOND ? R.string.match_checkin_confirm_diamond : R.string.match_checkin_confirm, checkInMayFee);
            checkInGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    message, getString(R.string.game_mtt_checkin), getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            checkInMatch();
                        }
                    });
        }
        if (!isFinishing() && !isDestroyedCompatible()) {
            checkInGameDialog.show();
        }
    }

    public void dismissMatchGame() {
        isInitiativeDismiss = true;//解散比赛要用原始的code
        mGameMatchAction.dismissMatch(StringUtil.isSpace(gameInfo.code) ? gameInfo.joinCode : gameInfo.code, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    isInitiativeDismiss = true;
//                        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
//                        intent.putExtra(Extras.EXTRA_GAME_ID, gid);
//                        intent.putExtra(Extras.EXTRA_GAME_CANCEL, true);
//                        mActivity.sendBroadcast(intent);
                    RequestTimeLimit.lastGetRecentGameTime = (0);
                    RequestTimeLimit.lastGetGameListInClub = 0;
                    RequestTimeLimit.lastGetGameListInHorde = 0;
                    RequestTimeLimit.lastTeamRecord = 0;
                    if (gameInfo != null) {
                        if ((gameInfo.code != null && gameInfo.code.length() > 6) ||
                                (gameInfo.joinCode != null && gameInfo.joinCode.length() > 6) ||
                                (gameInfo.channel != null && gameInfo.channel.length() > 6)) {
                            RequestTimeLimit.lastGetGamePlayingTime = 0;
                        }
                    }
                    finish();
                } else {
                    isInitiativeDismiss = false;
                }
            }

            @Override
            public void onFailed() {
                isInitiativeDismiss = false;
            }
        });
    }

    /**
     * 更新控制带入
     *
     * @param requestCallback
     */
    public void updateControl(boolean isControl, com.htgames.nutspoker.interfaces.RequestCallback requestCallback) {
        mGameMatchAction.updateControl(gameInfo.code, isControl, requestCallback);
    }

    /**
     * 邀请好友
     */
    public void inviteFriend() {
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = getString(R.string.game_invite_member_title);
        ArrayList<String> disableAccounts = new ArrayList<>();
//        disableAccounts.addAll(memberAccountList);
        option.itemDisableFilter = new ContactIdFilter(disableAccounts);
        ContactSelectHelper.startContactSelect(this, option, GameConstants.REQUEST_CODE_INVITE);// 创建群
        UmengAnalyticsEvent.onEventGameInvite(getApplicationContext(), gameInfo.gameMode);//统计
    }

    /**
     * 更新购买申请提示
     *
     * @param unReadCount
     */
    public void updateBuyChipsNotify(int unReadCount) {
        if (isMeGameManager() || creatorId.equals(DemoCache.getAccount())) {
            if (unReadCount > 0) {
                tv_new_notify.setVisibility(View.VISIBLE);
                tv_new_notify.setText("" + unReadCount);
            } else {
                tv_new_notify.setVisibility(View.GONE);
            }
        } else {
            tv_new_notify.setVisibility(View.GONE);
        }
    }

    public long getCurrentTime() {
        long nowTime = DemoCache.getCurrentServerSecondTime();//DemoCache.getCurrentServerSecondTime() + timeError;
        return nowTime;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_game_checkin:
                if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_NOT || myCheckInStatus == CheckInStatus.CHECKIN_STATUES_DEALING) {//// : 17/2/28  以前是只有未报名0才能"报名"，现在3分钟倒计时结束后也能"重新报名"
                    showCheckInDialog();
                } else if (myCheckInStatus == CheckInStatus.CHECKIN_STATUES_ED) {
                    if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                        showCancelCheckInDialog();
                    } else if (gameStatus == GameStatus.GAME_STATUS_START) {
                        openGame("", "", false);
                    }
                }
                break;
            case R.id.tv_head_right:
                if (gameStatus == GameStatus.GAME_STATUS_WAIT) {
                    if (rightTopPopupWindow != null && rightTopPopupWindow.isShowing()) {
                        dissmissClubPopWindow();
                    } else if (waitTopPopupWindow != null && waitTopPopupWindow.isShowing()) {
                        waitTopPopupWindow.dismiss();
                    } else {
                        showWaitPopWindow();//为开赛前点击右上角的弹窗
                    }
                } else {
                    if (advancedFunctionView == null) {
                        advancedFunctionView = new AdvancedFunctionView(MatchRoomActivity.this);
                        advancedFunctionView.setInfo(roomId, gameInfo.code, gameInfo.gameMode);
                    }
                    if (waitTopPopupWindow != null && waitTopPopupWindow.isShowing()) {
                        waitTopPopupWindow.dismiss();
                    } else if (rightTopPopupWindow != null && rightTopPopupWindow.isShowing()) {
                        dissmissClubPopWindow();
                    } else {
                        showClubPopWindow();//比赛进行中点击右上角的弹窗
                    }
                }
                break;
            case R.id.btn_game_invite:
                inviteFriend();
                break;
            case R.id.tv_match_status_final:
                openGame("", "", false);
                break;
            ////////////////////////////////////////////////////////////////////////////比赛已经开始的popwindow////////////////////////////////////////////////////////////////////////////
            case R.id.ll_pop_club_create://添加管理员
                dissmissClubPopWindow();
                gotoAddMttManagerPage();
                break;
            case R.id.ll_pop_club_row3://暂停比赛
                dissmissClubPopWindow();
                showPauseDialog();
                break;
            case R.id.ll_pop_club_row4://发送语音公告
                dissmissClubPopWindow();
                advancedFunctionView.show();
                break;
            ////////////////////////////////////////////////////////////////////////////比赛已经开始的popwindow////////////////////////////////////////////////////////////////////////////
        }
    }

    private void gotoAddMttManagerPage() {
        if (gameInfo != null) {
            ChannelListAC.start(this, gameInfo);
        }
    }

    ////////////////////////////////////////////////////////////////////////////比赛未开始的popwindow////////////////////////////////////////////////////////////////////////////
    PopupWindow waitTopPopupWindow = null;
    View wait_ll_pop_club_row1;
    View wait_ll_pop_club_row3;
    View wait_ll_pop_club_row4;
    private void showWaitPopWindow() {
        View.OnClickListener waitClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == wait_ll_pop_club_row1) {//添加赛事管理
                    gotoAddMttManagerPage();
                }else if (v == wait_ll_pop_club_row3) {//提前开赛
                    gameStartAdvanceDialog();
                } else if (v == wait_ll_pop_club_row4) {//解散比赛
                    showDismissDialog();// "解散比赛", Toast.LENGTH_SHORT).show();
                }
                if (waitTopPopupWindow != null && waitTopPopupWindow.isShowing()) {
                    waitTopPopupWindow.dismiss();
                }
            }
        };
        if (waitTopPopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);
            waitTopPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            waitTopPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            waitTopPopupWindow.setOutsideTouchable(true);
            waitTopPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            waitTopPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            waitTopPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            wait_ll_pop_club_row1 = popView.findViewById(R.id.ll_pop_club_create);//添加赛事管理
            wait_ll_pop_club_row1.setOnClickListener(waitClickListener);
            wait_ll_pop_club_row1.setVisibility(View.VISIBLE);
            popView.findViewById(R.id.ll_pop_club_join).setVisibility(View.GONE);
            wait_ll_pop_club_row3 = popView.findViewById(R.id.ll_pop_club_row3);//我的管理
            wait_ll_pop_club_row3.setOnClickListener(waitClickListener);
            wait_ll_pop_club_row3.setVisibility(View.VISIBLE);
            wait_ll_pop_club_row4 = popView.findViewById(R.id.ll_pop_club_row4);//解散比赛
            wait_ll_pop_club_row4.setOnClickListener(waitClickListener);
            wait_ll_pop_club_row4.setVisibility(View.VISIBLE);
            Drawable addLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_add);
            addLeftDrawable.setBounds(0, 0, addLeftDrawable.getIntrinsicWidth(), addLeftDrawable.getIntrinsicHeight());
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            addText.setText("赛事管理员");
            addText.setCompoundDrawables(addLeftDrawable, null, null, null);

            Drawable voiceLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_start);
            voiceLeftDrawable.setBounds(0, 0, voiceLeftDrawable.getIntrinsicWidth(), voiceLeftDrawable.getIntrinsicHeight());
            TextView bottomText = (TextView) popView.findViewById(R.id.tv_pop_club_row3);
            ((FrameLayout.LayoutParams) bottomText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            bottomText.setText("开始比赛");
            bottomText.setCompoundDrawables(voiceLeftDrawable, null, null, null);

            Drawable endLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_end);
            endLeftDrawable.setBounds(0, 0, endLeftDrawable.getIntrinsicWidth(), endLeftDrawable.getIntrinsicHeight());
            TextView rowText4 = (TextView) popView.findViewById(R.id.tv_pop_club_row4);
            ((FrameLayout.LayoutParams) rowText4.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            rowText4.setText("解散比赛");
            rowText4.setCompoundDrawables(endLeftDrawable, null, null, null);
        }
        waitTopPopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    EasyAlertDialog pauseGameDialog;
    public void showPauseDialog() {
        if (pauseGameDialog == null) {
            pauseGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getResources().getString(R.string.match_game_pause_confim_dialog_message),
                    getResources().getString(R.string.ok),
                    getResources().getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            if (!isFinishing()) {
                                doPauseMatch(GameMatchAction.TYPE_PAUSE);
                            }
                        }
                    });
        }
        if (!isFinishing()) {
            pauseGameDialog.show();
        }
    }

    View ll_pop_club_row1;
    View ll_pop_club_row3;
    View ll_pop_club_row4;
    public void dissmissClubPopWindow(){
        if(rightTopPopupWindow != null && rightTopPopupWindow.isShowing()){
            rightTopPopupWindow.dismiss();
        }
    }

    private void showClubPopWindow() {
        if(rightTopPopupWindow == null){
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);
            rightTopPopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            rightTopPopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            rightTopPopupWindow.setOutsideTouchable(true);
            rightTopPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            rightTopPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            rightTopPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            ll_pop_club_row1 = popView.findViewById(R.id.ll_pop_club_create);//添加赛事管理
            ll_pop_club_row1.setOnClickListener(this);
            ll_pop_club_row1.setVisibility(isMeGameCreator() ? View.VISIBLE : View.GONE);
            popView.findViewById(R.id.ll_pop_club_join).setVisibility(View.GONE);//我的管理
            ll_pop_club_row3 = popView.findViewById(R.id.ll_pop_club_row3);//暂停比赛
            ll_pop_club_row3.setOnClickListener(this);
            ll_pop_club_row3.setVisibility(isMeGameCreator() ? View.VISIBLE : View.GONE);
            ll_pop_club_row4 = popView.findViewById(R.id.ll_pop_club_row4);//发送语音公告
            ll_pop_club_row4.setOnClickListener(this);
            ll_pop_club_row4.setVisibility(isMeGameCreator() ? View.VISIBLE : View.GONE);
            Drawable addLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_add);
            addLeftDrawable.setBounds(0, 0, addLeftDrawable.getIntrinsicWidth(), addLeftDrawable.getIntrinsicHeight());
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            addText.setText("赛事管理员");
            addText.setCompoundDrawables(addLeftDrawable, null, null, null);

            Drawable pauseLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_pause);
            pauseLeftDrawable.setBounds(0, 0, pauseLeftDrawable.getIntrinsicWidth(), pauseLeftDrawable.getIntrinsicHeight());
            TextView pauseText = (TextView) popView.findViewById(R.id.tv_pop_club_row3);
            ((FrameLayout.LayoutParams) pauseText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            pauseText.setText("暂停比赛");
            pauseText.setCompoundDrawables(pauseLeftDrawable, null, null, null);

            Drawable voiceLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_voice);
            voiceLeftDrawable.setBounds(0, 0, voiceLeftDrawable.getIntrinsicWidth(), voiceLeftDrawable.getIntrinsicHeight());
            TextView bottomText = (TextView) popView.findViewById(R.id.tv_pop_club_row4);
            ((FrameLayout.LayoutParams) bottomText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            bottomText.setText("发送公告");
            bottomText.setCompoundDrawables(voiceLeftDrawable, null, null, null);
        }
        rightTopPopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    EasyAlertDialog continueGameDialog;

    public void showContinueDialog(){
        showContinueDialog(false);
    }

    public void showContinueDialog(final boolean isDialog) {
        if (continueGameDialog == null) {
            continueGameDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.match_game_continue_confim_dialog_message),
                    getString(R.string.ok),
                    getString(R.string.cancel), true,
                    new EasyAlertDialogHelper.OnDialogActionListener() {

                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                            doPauseMatch(GameMatchAction.TYPE_START);

                            if(isDialog)
                                advancedFunctionView.dismiss();
                        }
                    });
        }
        if (!isFinishing()) {
            continueGameDialog.show();
        }
    }
    int mRemaindPauseTime = 0;

    private boolean isMeGameManager() {
        if (gameInfo == null) {
            return false;
        }
        //游戏  创建者不是 管理员
        String uid = DemoCache.getAccount();
        ArrayList<UserEntity> list = ChessApp.gameManagers.get(gameInfo.gid);
        if (list == null) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            UserEntity bean = list.get(i);
            if (bean.account.equals(uid)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMeGameCreator() {
        String uid = DemoCache.getAccount();
        if (gameInfo == null || TextUtils.isEmpty(uid)) {
            return false;
        }
        return !StringUtil.isSpace(creatorId) && uid.equals(creatorId);
    }

    private void gameStartAdvanceDialog() {
        EasyAlertDialog startGameAdvanceDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", "确定开始比赛？", getResources().getString(R.string.ok), getResources().getString(R.string.cancel), true, new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        if (!isFinishing()) {
                            startMttInAdvance();
                        }
                    }
                });
        if (!isFinishing()) {
            startGameAdvanceDialog.show();
        }
    }

    /**
     * mtt 比赛提前开赛   条件是报名人数>单桌人数 POST  params: uid  code    现在版本只有 游戏创建者能够提前开赛
     */
    private void startMttInAdvance() {
        if (gameInfo == null) {
            return;
        }
        int leastNum = gameInfo.gameConfig instanceof GameMttConfig ? ((GameMttConfig) gameInfo.gameConfig).matchPlayer + 1 : 6;//mtt大菠萝至少6人开局
        if (matchStatus == null || matchStatus.checkInPlayer < leastNum) {
            Toast.makeText(ChessApp.sAppContext, "人数至少" + (leastNum) + "人才能提前开赛", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(this, "", false);
        final HashMap<String, String> paramsMap = NetWork.getRequestCommonParams(ChessApp.sAppContext);
        paramsMap.put("uid", creatorId);
        paramsMap.put("code", gameInfo.code);
        String requestCreateUrl = getHost() + ApiConstants.URL_GAME_MTT_START_IN_ADVANCE + NetWork.getRequestParams(paramsMap);
        LogUtil.i(GameAction.TAG, requestCreateUrl);
        SignStringRequest signRequest = new SignStringRequest(Request.Method.POST, requestCreateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.i(GameAction.TAG, response);
                DialogMaker.dismissProgressDialog();
                try {
                    JSONObject json = new JSONObject(response);
                    int code = json.getInt("code");
                    if (code == 0) {
                        Toast.makeText(ChessApp.sAppContext, "开始比赛成功，请稍等", Toast.LENGTH_LONG).show();
                    } else {
                        if (code == 3048) {//报名人数不足
                            Toast.makeText(ChessApp.sAppContext, "报名人数不足, 提前开赛失败", Toast.LENGTH_SHORT).show();
                        } else if (code == 3049) {//Manual start mtt game fail
                            Toast.makeText(ChessApp.sAppContext, "Manual start mtt game fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DialogMaker.dismissProgressDialog();
                    Toast.makeText(ChessApp.sAppContext, "提前开赛失败", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(ChessApp.sAppContext, "提前开赛失败", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        signRequest.setTag(requestCreateUrl);
        ChessApp.sRequestQueue.add(signRequest);
    }

    AppMessageReceiver mAppMessageReceiver = new AppMessageReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent != null) {
                AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                if (appMessage != null) {
                    if (appMessage.type == AppMessageType.MatchBuyChips || appMessage.type == AppMessageType.MatchBuyChipsResult) {
                        if (mMatchMgrFragment != null) {
                            mMatchMgrFragment.onIncommingMessage(appMessage);
                        } else {
                            if (appMessage.type == AppMessageType.MatchBuyChipsResult) {//买入结果
                                //同意或者拒绝
                                MatchBuyChipsResultNotify matchBuyChipsNotify = (MatchBuyChipsResultNotify) appMessage.attachObject;
                                int buyType = matchBuyChipsNotify.buyType;
                                if (matchBuyChipsNotify.gameId.equals(gameInfo.gid)) {
                                    boolean openGame = true;
                                    int myCheckInStatus = 0;
                                    if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {//报名结果
                                        myCheckInStatus = (appMessage.status == AppMessageStatus.passed ? CheckInStatus.CHECKIN_STATUES_ED : CheckInStatus.CHECKIN_STATUES_NOT);
                                    } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {//复活重购结果
                                        myCheckInStatus = CheckInStatus.CHECKIN_STATUES_ED;//复活重购  就是已经报名了
                                        buyTypeStatus = 0;
                                        openGame = appMessage.status == AppMessageStatus.passed;//是否直接进入游戏，true进入false不进入
                                        updateMatchStatusBottomUI(myCheckInStatus, openGame);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    EasyAlertDialog thirtyLimitDialog;
    public void showThirtyLimitDialog(String messageP) {//正式开赛前30秒的限制：1不能报名、2不能移除玩家、3不能同意或拒绝玩家
        if (thirtyLimitDialog == null) {
            thirtyLimitDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", messageP,
                    getString(R.string.ok), getString(R.string.cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }

                        @Override
                        public void doOkAction() {
                        }
                    });
        }
        if (!isFinishing() && !thirtyLimitDialog.isShowing() && !isDestroyedCompatible()) {
            thirtyLimitDialog.setMessage(messageP);
            thirtyLimitDialog.show();
        }
    }

    View rl_tabs;
    public void changePineappleBg() {
        if (rl_tabs != null && gameInfo != null && gameInfo.gameConfig instanceof PineappleConfigMtt) {
            rl_tabs.setBackgroundColor(ChessApp.sAppContext.getResources().getColor(R.color.bg_pineapple_mtt));
        }
    }
}
