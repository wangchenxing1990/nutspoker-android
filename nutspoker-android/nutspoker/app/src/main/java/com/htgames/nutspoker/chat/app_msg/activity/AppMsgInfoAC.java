package com.htgames.nutspoker.chat.app_msg.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.ui.fragment.main.DiscoveryFragment;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.htgames.nutspoker.chat.app_msg.BuyChipsAction;
import com.htgames.nutspoker.chat.app_msg.attach.AppNotify;
import com.htgames.nutspoker.chat.app_msg.attach.BuyChipsNotify;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.attach.MatchBuyChipsResultNotify;
import com.htgames.nutspoker.chat.app_msg.contact.AppMessageConstants;
import com.htgames.nutspoker.chat.app_msg.fragment.NoticeFragment;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageStatus;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.db.AppMsgDBHelper;
import com.htgames.nutspoker.db.GameRecordDBHelper;
import com.htgames.nutspoker.game.model.GameMatchStatus;
import com.htgames.nutspoker.hotupdate.HotUpdateAction;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateItem;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.notifications.HTNotificationManager;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.netease.nim.uikit.common.util.BaseTools;
import com.htgames.nutspoker.tool.json.RecordJsonTools;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.action.GameMatchAction;
import com.htgames.nutspoker.ui.action.RecordAction;
import com.htgames.nutspoker.ui.activity.Record.RecordDetailsActivity;
import com.htgames.nutspoker.ui.activity.System.WebViewActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.netease.nim.uikit.common.framework.ThreadUtil;
import com.netease.nim.uikit.common.DateTools;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.session.constant.Extras;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 周智慧 on 16/11/28.
 */

public class AppMsgInfoAC extends BaseActivity implements View.OnClickListener {
    public static final String TAG = AppMsgInfoAC.class.getSimpleName();
    public final static int DELETE_ALL = 1;
    public final static int DELETE_APPLYBUY = 2;
    public final static int DELETE_GAMEOVER = 3;
    public BuyChipsAction mBuyChipsAction;
    public GameMatchAction mGameMatchAction;
    public GameAction mGameAction;
    public HotUpdateAction mHotUpdateAction;
    public RecordAction mRecordAction;
    EasyAlertDialog clearDialog;
    //
    PopupWindow deletePopupWindow = null;
    View ll_pop_delete_all;
    View ll_pop_delete_gameover;
    NoticeFragment mNoticeFragment;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AppMsgInfoAC.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_message_info);
        initHead();
        initFragment();
        initAction();
        HTNotificationManager.getInstance(getApplicationContext()).clearAppNotify();
        AppMsgDBHelper.resetAppMessageUnreadCountByType(AppMsgInfoAC.this, AppMsgDBHelper.TYPE_NOTICE);//只要一进来就把所有的"系统消息"标志位已读
        registerObservers(true);
    }

    private void initFragment() {
        mNoticeFragment = NoticeFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.app_msg_info_frame_content, mNoticeFragment, mNoticeFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    public void initHead() {
        ((TextView) findViewById(R.id.tv_head_title)).setText(R.string.app_message);
        Drawable tv_head_right_drawable = getResources().getDrawable(R.mipmap.btn_club_more);
        tv_head_right_drawable.setBounds(0, 0, tv_head_right_drawable.getIntrinsicWidth(), tv_head_right_drawable.getIntrinsicHeight());
        TextView tv_head_right = ((TextView) findViewById(R.id.tv_head_right));//右边"更多"
        tv_head_right.setVisibility(View.VISIBLE);
        tv_head_right.setText("");
        tv_head_right.setCompoundDrawables(tv_head_right_drawable, null, null, null);
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
                        //置为已读
                        AppMsgDBHelper.resetOneAppMessageUnreadCount(getApplicationContext(), appMessage);
                        mNoticeFragment.onImcomingMessage(appMessage);
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
        ReminderManager.getInstance().updateAppMsgUnreadNum(1);//重置为已读
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
        DiscoveryFragment.updateAppInfoNum = true;
        super.onDestroy();
    }

    public void getRecordDetail(final String gid) {
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
                                    RecordDetailsActivity.start(AppMsgInfoAC.this, gameBillEntity.gameInfo.gid);
                                    return;
                                }
                            }
                            Toast.makeText(getApplicationContext(), R.string.record_details_noexist, Toast.LENGTH_SHORT).show();
                        } else {
                            gotoRecordDetailsByDB(gid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        gotoRecordDetailsByDB(gid);
                    }
                }
            }
            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
                gotoRecordDetailsByDB(gid);
            }
        });
    }

    public void gotoRecordDetailsByDB(String gid) {//网络请求失败就从数据库中获取
        RecordDetailsActivity.start(AppMsgInfoAC.this, gid);
//        GameBillEntity gameBillEntity = GameRecordDBHelper.getGameRecordByGid(this, gid);
//        if (gameBillEntity != null) {
//        } else {
//            Toast.makeText(AppMsgInfoAC.this, R.string.get_failuer, Toast.LENGTH_SHORT).show();
//        }
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

    private void showDeletePopWindow() {
        if (deletePopupWindow == null) {
            View popView = LayoutInflater.from(this).inflate(R.layout.pop_club_add, null);
            deletePopupWindow = new PopupWindow(popView);
            //获取popwindow焦点
            deletePopupWindow.setFocusable(true);
            //设置popwindow如果点击外面区域，便关闭。
            deletePopupWindow.setOutsideTouchable(true);
            deletePopupWindow.setBackgroundDrawable(new ColorDrawable(0));
            deletePopupWindow.setWidth(BaseTools.dip2px(getApplicationContext(), 160));
            deletePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置popwindow出现和消失动画
//        clubPopupWindow.setAnimationStyle(R.style.PopMenuAnimation);
            ll_pop_delete_all = popView.findViewById(R.id.ll_pop_club_create);
            ll_pop_delete_all.setOnClickListener(this);
            ll_pop_delete_gameover = popView.findViewById(R.id.ll_pop_club_join);
            ll_pop_delete_gameover.setOnClickListener(this);
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.CENTER;
            addText.setText(R.string.app_message_delete_all);
            addText.setCompoundDrawables(null, null, null, null);

            TextView pauseText = (TextView) popView.findViewById(R.id.tv_pop_club_join);
            ((FrameLayout.LayoutParams) pauseText.getLayoutParams()).gravity = Gravity.CENTER;
            pauseText.setText(R.string.app_message_delete_gameover);
            pauseText.setCompoundDrawables(null, null, null, null);

        }
        deletePopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
                showDeletePopWindow();
                break;
            case R.id.ll_pop_club_create:
                showClearDialog(DELETE_ALL);
                break;
            case R.id.ll_pop_club_join:
                showClearDialog(DELETE_GAMEOVER);
                break;
        }
    }

    /**
     * 删除全部消息
     */
    public void deleteAllGameMessage(final int deleteType) {
        DialogMaker.showProgressDialog(AppMsgInfoAC.this, "");
        ThreadUtil.Execute(new Runnable() {
            @Override
            public void run() {
                if (deleteType == DELETE_ALL) {
                    AppMsgDBHelper.clearNoticeCenterMessages(getApplicationContext());
                } else if (deleteType == DELETE_APPLYBUY) {
                    AppMsgDBHelper.clearAppMessagesByType(getApplicationContext(), AppMessageType.GameBuyChips);
                } else if (deleteType == DELETE_GAMEOVER) {
                    AppMsgDBHelper.clearAppMessagesByType(getApplicationContext(), AppMessageType.GameOver);
                }
                if (mNoticeFragment != null) {
                    mNoticeFragment.clearNotice(deleteType);
                }
            }
        });
    }

    public void showClearDialog(final int deleteType) {
        deletePopupWindow.dismiss();
        String message = getString(R.string.app_message_clear_all_promot, getString(R.string.app_message_delete_all));
        if (deleteType == DELETE_APPLYBUY) {
            message = getString(R.string.app_message_clear_type_promot, getString(R.string.app_message_delete_applybuy));
        } else if (deleteType == DELETE_GAMEOVER) {
            message = getString(R.string.app_message_clear_type_promot, getString(R.string.app_message_delete_gameover));
        }
        clearDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, "",
                message, getString(R.string.ok), getString(R.string.cancel), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {
                    @Override
                    public void doCancelAction() {
                    }
                    @Override
                    public void doOkAction() {
                        deleteAllGameMessage(deleteType);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            clearDialog.show();
        }
    }

}
