package com.htgames.nutspoker.chat.app_msg.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.crl.zzh.customrefreshlayout.ControlToast;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.app_msg.BuyChipsAction;
import com.htgames.nutspoker.chat.app_msg.adapter.ControlPageAdapter;
import com.htgames.nutspoker.chat.app_msg.attach.*;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.fragment.ControlFrgHandled;
import com.htgames.nutspoker.chat.app_msg.fragment.ControlFrgInitial;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;
import com.htgames.nutspoker.game.model.GameMatchBuyType;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateItem;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.notifications.HTNotificationManager;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.GameMatchAction;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.ui.activity.System.WebViewActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.ui.fragment.main.DiscoveryFragment;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.session.constant.Extras;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 16/11/24.
 */

public class AppMsgControlAC extends BaseActivity implements View.OnClickListener {
    public final static String TAG = "AppMsgControlAC";
    public BuyChipsAction mBuyChipsAction;
    public GameMatchAction mGameMatchAction;
    public GameAction mGameAction;
    public HotUpdateAction mHotUpdateAction;
    public RecordAction mRecordAction;
    public boolean isAppMessageChanged = false;
    ControlFrgInitial mControlFrgInitial;
    ControlFrgHandled mControlFrgHandled;
    ViewPager mViewPager;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AppMsgControlAC.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_message_control);
        initHead();
        initFragment();
        initAction();
        HTNotificationManager.getInstance(getApplicationContext()).clearAppNotify();
        registerObservers(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ControlToast.Companion.getInstance().setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ControlToast.Companion.getInstance().setEnabled(true);
    }

    private void initFragment() {
        tv_control_msg_reddot = (TextView) findViewById(R.id.tv_control_msg_reddot);
        mControlFrgInitial = ControlFrgInitial.Companion.newInstance();
        mControlFrgHandled = ControlFrgHandled.Companion.newInstance();
        ArrayList fragmentList = new ArrayList<Fragment>();
        fragmentList.add(mControlFrgInitial);
        fragmentList.add(mControlFrgHandled);
        ControlPageAdapter adapter = new ControlPageAdapter(getSupportFragmentManager(), fragmentList, this);
        mViewPager = (ViewPager) findViewById(R.id.control_viewpager);
        mViewPager.setAdapter(adapter);
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
        final PagerSlidingTabStrip team_message_tabs = (PagerSlidingTabStrip) findViewById(R.id.control_tabs);
        team_message_tabs.setViewPager(mViewPager);
        team_message_tabs.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                View linearLayout = team_message_tabs.getChildAt(0);//.getChildAt(1);
                LogUtil.i("onPreDraw   linearLayout " + linearLayout);
                if (!(linearLayout instanceof LinearLayout && ((LinearLayout) linearLayout).getChildCount() >= 2)) {
                    return true;
                }
                View child = ((LinearLayout) linearLayout).getChildAt(0);
                float middleX = (child.getRight() - child.getLeft()) / 2;
                int offset = ScreenUtil.dp2px(AppMsgControlAC.this, 35);
                tv_control_msg_reddot.setX(middleX + child.getLeft() + offset);
                team_message_tabs.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    public void initHead() {
        ((TextView) findViewById(R.id.tv_head_title)).setText(R.string.app_message_control_center);
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));//右边"更多"
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText(R.string.clear);
        tv_head_right.setOnClickListener(this);
    }

    private void initAction() {
        mGameAction = new GameAction(this, null);
        mBuyChipsAction = new BuyChipsAction(this, null);
        mRecordAction = new RecordAction(this, null);
        mHotUpdateAction = new HotUpdateAction(this, null);
        mGameMatchAction = new GameMatchAction(this, null);
        mHotUpdateAction.onCreate();
    }

    public void registerObservers(boolean register) {
        HotUpdateManager.getInstance().clearCallback();
        if (register) {
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
            HotUpdateManager.getInstance().registerCallback(hotUpdateCallback);
        } else {
            unregisterReceiver(mAppMessageReceiver);
            HotUpdateManager.getInstance().unregisterCallback(hotUpdateCallback);
        }
    }

    HotUpdateManager.HotUpdateCallback hotUpdateCallback = new HotUpdateManager.HotUpdateCallback() {
        @Override
        public void onUpdated(HotUpdateItem item) {
            int type = item.updateType;
            if (type == HotUpdateItem.UPDATE_TYPE_ING) {
                if (mHotUpdateAction != null) {
                    mHotUpdateAction.lastDownTime = DemoCache.getCurrentServerSecondTime();
                }
                int downloadFileCount = item.downloadFileCount;
                int finishFileCount = item.finishFileCount;
                int successFileCount = item.successFileCount;
                String newVersion = item.newVersion;
                UpdateFileEntity updateFileEntity = item.updateFileEntity;
                if (downloadFileCount == finishFileCount) {
                    if (mHotUpdateAction != null) {
                        mHotUpdateAction.lastDownTime = Long.MAX_VALUE;
                    }
                    if (finishFileCount == successFileCount) {
                        showUpdateResultDialog(true);
                    } else {
                        showUpdateResultDialog(false);
                    }
                } else {
                    //还在下载中
                }
            } else if (type == HotUpdateItem.UPDATE_TYPE_STUCK) {
                showUpdateResultDialog(false);
            }
        }
    };

    EasyAlertDialog updateResultDialog = null;
    public void showUpdateResultDialog(boolean isSuccess) {
        if (mHotUpdateAction != null) {
            mHotUpdateAction.destroyTimer();
        }
        if(isSuccess){
            updateResultDialog = EasyAlertDialogHelper.createOneButtonDiolag(this , "",
                    getString(R.string.game_update_success), getString(R.string.ok), false, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
        } else {
            updateResultDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                    getString(R.string.game_update_failure), getString(R.string.reget), getString(R.string.cancel), false,
                    new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                        }
                        @Override
                        public void doOkAction() {
                            if (mHotUpdateAction != null) {
                                mHotUpdateAction.downUpdateFile();
                            }
                        }
                    });
        }
        if (!this.isFinishing() && !this.isDestroyedCompatible()) {
            updateResultDialog.show();
        }
    }

    AppMessageReceiver mAppMessageReceiver = new AppMessageReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            if (intent != null) {
                AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                if (appMessage != null) {
                    if (appMessage.type == AppMessageType.GameOver || appMessage.type == AppMessageType.AppNotice || appMessage.type == AppMessageType.MatchBuyChipsResult) {
                        //"系统消息"和"控制中心"由原来的一个activity分成两个activity独立管理
                    } else if (appMessage.type == AppMessageType.MatchBuyChips || appMessage.type == AppMessageType.GameBuyChips) {
                        if (mControlFrgHandled != null) {
                            mControlFrgHandled.onIncomingMessage(appMessage);
                        }
                        if (mControlFrgInitial != null) {
                            mControlFrgInitial.onIncomingMessage(appMessage);
                        }
                    }
                }
            }
        }
    };

    /**
     * 消息消息
     *
     * @param message
     */
    public void clickMessage(AppMessage message) {
        if (message.type == AppMessageType.GameBuyChips && message.attachObject instanceof BuyChipsNotify) {
            final BuyChipsNotify buyChipsNotify = (BuyChipsNotify) message.attachObject;
            long passTime = DemoCache.getCurrentServerSecondTime() - buyChipsNotify.gameCreateTime;//过去的时间
            int remainingTime = buyChipsNotify.gameDurations / 60 - (int) (passTime / 60);//剩余的时间
            if (buyChipsNotify.gameMode == GameConstants.GAME_MODE_NORMAL && remainingTime <= 0) {
                //已经结束(普通模式并且超时)
                return;
            }
            doJoinGame(buyChipsNotify.gameCode);
        } else if (message.type == AppMessageType.AppNotice && message.attachObject instanceof AppNotify) {
            AppNotify appNotify = (AppNotify) message.attachObject;
            WebViewActivity.start(this, WebViewActivity.TYPE_APP_NOTICE, appNotify);
        } else if (message.type == AppMessageType.GameOver && message.attachObject instanceof GameOverNotify) {
            //游戏状态
            GameOverNotify gameOverNotify = (GameOverNotify) message.attachObject;
            String gameCode = gameOverNotify.gameCode;
            if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_START) {
                //游戏开始
                if (!TextUtils.isEmpty(gameCode)) {
                    doJoinGame(gameOverNotify.gameCode);
                }
            } else if (gameOverNotify.gameStatus == GameStatus.GAME_STATUS_FINISH) {
                //游戏结束
                if (gameOverNotify.gameMode == GameConstants.GAME_MODE_NORMAL) {
                    getRecordDetail(gameOverNotify.gameId);
                } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_SNG) {
                    int balance = gameOverNotify.userBalance;
                    int endType = gameOverNotify.gameEndType;//结束类型
                    if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                        //正常结束
                        getRecordDetail(gameOverNotify.gameId);
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                        //房主提前结束
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_TIMEOUT) {
                        //超时结束
                    }
                } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MTT) {
                    int endType = gameOverNotify.gameEndType;//结束类型
                    if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                        //正常结束
                        getRecordDetail(gameOverNotify.gameId);
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                        //房主提前结束
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                        //人数不齐
                    }
                } else if (gameOverNotify.gameMode == GameConstants.GAME_MODE_MT_SNG) {
                    int endType = gameOverNotify.gameEndType;//结束类型
                    if (endType == AppMessageConstants.TYPE_GAMEOVER_END_NORMAL) {
                        //正常结束
                        getRecordDetail(gameOverNotify.gameId);
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_BY_CREATOR) {
                        //房主提前结束
                    } else if (endType == AppMessageConstants.TYPE_GAMEOVER_END_PLAYER_NOT_ENOUGH) {
                        //人数不齐
                    }
                }
            } else if (gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_REST_FINISH
                    || gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_WILL_START
                    || gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_PAUSE
                    || gameOverNotify.gameStatus == GameMatchStatus.GAME_STATUS_RESUME) {
                //即将开始
                if (!TextUtils.isEmpty(gameCode)) {
                    doJoinGame(gameOverNotify.gameCode);
                }
            }
        } else if (message.type == AppMessageType.MatchBuyChipsResult && message.attachObject instanceof MatchBuyChipsResultNotify) {
            //房主同意、拒绝
            MatchBuyChipsResultNotify resultNotify = (MatchBuyChipsResultNotify) message.attachObject;
            String gameCode = resultNotify.gameCode;
            if (message.status == AppMessageStatus.passed) {
                if (!TextUtils.isEmpty(gameCode)) {
                    doJoinGame(gameCode);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
//        AppMsgDBHelper.clearDealedBuyChips(this);//清除已经处理的消息
        ReminderManager.getInstance().updateAppMsgUnreadNum(1);//交给"发现"fragment注册的观察者处理;//表示"控制中心"的消息被处理（"拒绝"或者"同意"），然后交给"发现"fragment注册的观察者处理
        registerObservers(false);
        if (mBuyChipsAction != null) {
            mBuyChipsAction.onDestroy();
            mBuyChipsAction = null;
        }
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        if (mHotUpdateAction != null) {
            mHotUpdateAction.onDestroy();
            mHotUpdateAction = null;
        }
        if (mRecordAction != null) {
            mRecordAction.onDestroy();
            mRecordAction = null;
        }
        if (mGameMatchAction != null) {
            mGameMatchAction.onDestroy();
            mGameMatchAction = null;
        }
        DiscoveryFragment.updateAppControlNum = true;
        super.onDestroy();
    }

    public void getRecordDetail(String gid) {
        mRecordAction.getRecordDetail(gid, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if (code == 0) {
                    try {
                        JSONObject dataOBJ = new JSONObject(result).optJSONObject("data");
                        if (dataOBJ != null && dataOBJ.has("games")) {
                            JSONObject gameOBJ = dataOBJ.optJSONObject("games");
                            if (gameOBJ != null && !TextUtils.isEmpty(dataOBJ.toString())) {
                                GameBillEntity gameBillEntity = RecordJsonTools.getGameBillEntity(gameOBJ);
                                if (gameBillEntity != null) {
                                    ArrayList<GameBillEntity> list = new ArrayList<GameBillEntity>();
                                    list.add(gameBillEntity);
                                    GameRecordDBHelper.saveGameRecordList(getApplicationContext(), list);//保存到数据库
                                    RecordDetailsActivity.start(AppMsgControlAC.this, gameBillEntity.gameInfo.gid);
                                    return;
                                }
                            }
                            Toast.makeText(ChessApp.sAppContext, R.string.record_details_noexist, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChessApp.sAppContext, R.string.get_failuer, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ChessApp.sAppContext, R.string.get_failuer, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    public void doJoinGame(final String gameCode) {
        if (HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        if (HotUpdateHelper.isNeedToCheckVersion()) {
            mHotUpdateAction.doHotUpdate(true, new CheckHotUpdateCallback() {
                @Override
                public void notUpdate() {
                    executeJoinGame(gameCode);
                }
            });
        } else {
            executeJoinGame(gameCode);
        }
    }

    public void executeJoinGame(String gameCode) {
        //mGameAction.executeJoinGame(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_APP_MESSAGE,
        mGameAction.joinGame(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_APP_MESSAGE,
                gameCode, new GameRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                    }
                    @Override
                    public void onFailed(int code, JSONObject response) {
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_head_right) {
            showClearAllControlMsgDialog();
        }
    }

    private void showClearAllControlMsgDialog() {
        final int pageIndex = mViewPager.getCurrentItem();
        String message = getResources().getString(pageIndex == 0 ? R.string.control_clear_initial_str : R.string.control_clear_handled_str);
        EasyAlertDialog clearDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "", message, getString(R.string.ok), getString(R.string.cancel), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        AppMsgDBHelper.clearControlCenterMessages(AppMsgControlAC.this, AppMessageStatus.init, pageIndex == 0);
                        if (pageIndex == 0) {
                            if (mControlFrgInitial != null) {
                                mControlFrgInitial.clearNotice();
                            }
                        } else {
                            if (mControlFrgHandled != null) {
                                mControlFrgHandled.clearNotice();
                            }
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            clearDialog.show();
        }
    }

    //控制带入（如果是比赛模式的报名，走mGameMatchAction ， 如果是普通模式或者比赛模式的赠购买走mBuyChipsAction）
    public void doControlCheckIn(AppMessage appMessage, AppMessageStatus dealStatus, GameRequestCallback callback) {
        int action = GameMatchAction.ACTION_REJECT;
        if (dealStatus == AppMessageStatus.passed) {
            action = GameMatchAction.ACTION_AGREE;
        }
        if (appMessage.type == AppMessageType.GameBuyChips) {
            mBuyChipsAction.controlBuyIn(appMessage, action, callback);//"game/controlin";//控制买入
        } else if (appMessage.type == AppMessageType.MatchBuyChips && appMessage.attachObject instanceof MatchBuyChipsNotify) {
            int buyType = ((MatchBuyChipsNotify) appMessage.attachObject).buyType;
            if (buyType == GameMatchBuyType.TYPE_BUY_CHECKIN) {//报名
                mGameMatchAction.controlCheckIn(appMessage, action, true, callback);//"game/mttpasscheckin";//比赛等级许可
            } else if (buyType == GameMatchBuyType.TYPE_BUY_REBUY_REVIVAL) {//复活重购
                mGameMatchAction.controlReVivalIn(appMessage, action, true, callback);//"game/matchRevivalPass";//是否同意或拒绝"复活"code uid player_id action 0->拒绝 1->同意
            } else {
                mBuyChipsAction.controlBuyIn(appMessage, action, callback);//"game/controlin";//控制买入
            }
        }
    }

    public TextView tv_control_msg_reddot;
    //ゲンgィン更新"未处理"附近的小红点信息
    public void updateReddot(int reddotNum) {
        tv_control_msg_reddot.setVisibility(reddotNum <= 0 ? View.INVISIBLE : View.VISIBLE);
        tv_control_msg_reddot.setText(String.valueOf(reddotNum));
    }

}
