package com.htgames.nutspoker.chat.session.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.app_msg.attach.GameOverNotify;
import com.htgames.nutspoker.chat.app_msg.model.AppMessage;
import com.htgames.nutspoker.chat.app_msg.model.AppMessageType;
import com.htgames.nutspoker.chat.app_msg.receiver.AppMessageReceiver;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.notification.helper.CustomNotificationHelper;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.chat.session.adapter.TeamMsgAdapter;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.netease.nim.uikit.common.preference.PaijuListPref;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.game.match.view.PagerSlidingTabStrip;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.hotupdate.HotUpdateHelper;
import com.htgames.nutspoker.hotupdate.interfaces.CheckHotUpdateCallback;
import com.htgames.nutspoker.hotupdate.model.UpdateFileEntity;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateItem;
import com.htgames.nutspoker.hotupdate.tool.HotUpdateManager;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.ICheckGameVersion;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.ui.action.HordeAction;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.activity.Club.GroupInfoActivity;
import com.htgames.nutspoker.ui.activity.horde.util.HordeUpdateManager;
import com.htgames.nutspoker.ui.activity.horde.util.UpdateItem;
import com.htgames.nutspoker.ui.fragment.TeamPaijuFragment;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.entity.HordeEntity;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.chesscircle.interfaces.OnClubConfigChangeListener;
import com.netease.nim.uikit.common.fragment.TFragment;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.interfaces.IGameChange;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.session.fragment.MessageFragment;
import com.netease.nim.uikit.session.fragment.TeamMessageFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 周智慧 on 16/11/30.
 * AppBarLayout子View的动作
 * 内部的子View通过在布局中加app:layout_scrollFlags设置执行的动作，那么app:layout_scrollFlags可以设置哪些动作呢？分别如下：
 *      （1） scroll:值设为scroll的View会跟随滚动事件一起发生移动。
 *      （2） enterAlways:值设为enterAlways的View,当ScrollView往下滚动时，该View会直接往下滚动。而不用考虑ScrollView是否在滚动。
 *      （3） exitUntilCollapsed：值设为exitUntilCollapsed的View，当这个View要往上逐渐“消逝”时，会一直往上滑动，直到剩下的的高度达到它的最小高度后，再响应ScrollView的内部滑动事件。
 *      （4） enterAlwaysCollapsed：是enterAlways的附加选项，一般跟enterAlways一起使用，它是指，View在往下“出现”的时候，首先是enterAlways效果，当View的高度达到最小高度时，
 *              View就暂时不去往下滚动，直到ScrollView滑动到顶部不再滑动时，View再继续往下滑动，直到滑到View的顶部结束。
         (5) snap  vi. 咬；厉声说；咯嗒一声关上   n. 猛咬；劈啪声；突然折断    adj. 突然的餐
    app:layout_collapseMode="pin"
    app:layout_collapseMode="parallax"
    app:layout_collapseMode="none"
 http://blog.csdn.net/huachao1001/article/details/51554608

 http://blog.csdn.net/u010687392/article/details/46906657
 When scrolling we see the following - appbar image starts hiding under the content and beyond the top edge of the screen. Parameter layout_collapseParallaxMultiplier determines what part of the image (in percent) will be hidden under the bottom content.
 So, for example, setting this parameter to value 1.0 means that top boundary of appbar's image is bound to the top edge of the screen and doesn't move when scrolling. And main content is moving up the top of the image.
 When the parameter is not set this corresponds to the value 0.5 and image will be overlapped above and below synchronously.
 */

public class TeamMessageAC extends BaseMessageActivity implements OnClubConfigChangeListener, View.OnClickListener, IGameChange, ICheckGameVersion {
    public static final String TAG = TeamMessageAC.class.getSimpleName();
    public static final int PAGE_TYPE_PAIJU = 0;//页面是"牌局"
    public static final int PAGE_TYPE_CHAT = 1;//页面是"聊天"
    public int pageType;
    private Team team;
    private RelativeLayout invalidTeamTipView;
    TextView tv_invalid_tip;
    TextView btn_invalid_action;
    public String creator;
    TextView tv_details;
    ImageView iv_head_mute;
    PagerSlidingTabStrip team_message_tabs;
    TextView team_message_chat_unread_num;
    HordeAction mHordeAction;
    public ArrayList<HordeEntity> costList = new ArrayList<HordeEntity>();//创建牌局共享到部落的钻石花费
    public static final int REQUESTCODE_GAMECREATE = 1;

    private ViewPager mViewPager;
    ArrayList<Fragment> fragmentList;
    private TeamMsgAdapter mAdapter;
    private TeamMessageFragment mChatFragment;//聊天界面
    private TeamPaijuFragment mPaijuFragment;//牌局界面
    private TeamRecordFragment mRecordFragment;
    public static void StartActivity(Context context, int type, PaipuEntity paipu){
        Intent intent = new Intent(context, P2PMessageActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", paipu);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, SessionCustomization customization, int pageType) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(TAG, pageType);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, TeamMessageAC.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, TeamMessageAC.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, int newMessageNum, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, TeamMessageAC.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public void registerObservers(boolean register) {
        HotUpdateManager.getInstance().clearCallback();
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);//注册群信息更新监听
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver);//注册群信息更新监听
            IntentFilter filter = new IntentFilter();//动态注册有新消息提醒通知
            filter.addAction(NewGameReceiver.ACTION_NEWGAME);
            registerReceiver(mNewGameReceiver, filter);
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);//注册未读消息数量观察者
            //app消息观察者
            IntentFilter intentFilter = new IntentFilter(AppMessageReceiver.ACTION_APP_MESSAGE);
            registerReceiver(mAppMessageReceiver, intentFilter);
            HotUpdateManager.getInstance().registerCallback(hotUpdateCallback);
            HordeUpdateManager.getInstance().registerCallback(hordeUpdateCallback);
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberDataChangedObserver);
            unregisterReceiver(mNewGameReceiver);
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);//注册未读消息数量观察者
            unregisterReceiver(mAppMessageReceiver);
            HotUpdateManager.getInstance().unregisterCallback(hotUpdateCallback);
            HordeUpdateManager.getInstance().unregisterCallback(hordeUpdateCallback);
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
            //App消息
            if (intent != null) {
                AppMessage appMessage = (AppMessage) intent.getSerializableExtra(Extras.EXTRA_APP_MESSAGE);
                if (appMessage.type == AppMessageType.GameOver && appMessage.attachObject instanceof GameOverNotify && ((GameOverNotify) appMessage.attachObject).teamId.equals(sessionId) && ((GameOverNotify) appMessage.attachObject).gameStatus == GameStatus.GAME_STATUS_FINISH) {//有游戏结束
                    if (mRecordFragment != null) {
                        mRecordFragment.getNetRecordList("");//有新战绩
                    }
                    if (mPaijuFragment != null) {
                        RequestTimeLimit.lastGetGameListInClub = 0;
                        mPaijuFragment.getGamePlayingList();//有游戏结束
                    }
                    if (mChatFragment != null && mChatFragment.getMessages() != null) {
                        for (int i = mChatFragment.getMessages().size() - 1; i >= 0; i-- ) {
                            IMMessage imMessage = mChatFragment.getMessages().get(i);
                            if (imMessage.getAttachment() != null && imMessage.getAttachment() instanceof GameAttachment) {
                                if (((GameOverNotify) appMessage.attachObject).gameId.equals(((GameAttachment) imMessage.getAttachment()).gid)) {
                                    updateGameStatusFinished(imMessage, i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 群资料变动通知和移除群的通知（包括自己退群和群被解散）
     */
    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            if (team == null) {
                return;
            }
            for (Team t : teams) {
                if (t.getId().equals(team.getId())) {
                    //被踢出群后，这个监听将不会再触发
                    updateTeamInfo(t);
                    break;
                }
            }
        }
        @Override
        public void onRemoveTeam(Team team) {
            if (team.getId().equals(sessionId)) {
                //移除群的观察者通知。自己退群，群被解散，自己被踢出群时，会收到该通知
                updateTeamInfo(team);
            }
        }
    };

    /**
     * 群成员资料变动通知和移除群成员通知
     */
    TeamDataCache.TeamMemberDataChangedObserver teamMemberDataChangedObserver = new TeamDataCache.TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
//            Toast.makeText(getApplicationContext(), "onUpdateTeamMember", Toast.LENGTH_SHORT).show();
//            fragment.refreshMessageList();
            mChatFragment.refreshMessageList();
            for(TeamMember tm : members){
                if(tm.getAccount().equals(DemoCache.getAccount())){
                    //如果是我的资料变动，需要更新游戏开局按钮
                    if (mPaijuFragment != null) {
                        mPaijuFragment.updataGameCreateBtnUI(tm);
                    }
                    updateActionPanelLayout(tm);
                    break;
                }
            }
        }
        @Override
        public void onRemoveTeamMember(TeamMember member) {
//            Toast.makeText(getApplicationContext(), "onRemoveTeamMember", Toast.LENGTH_SHORT).show();
        }
    };

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item.getId() == ReminderId.SESSION) {
            } else if (item.getId() == ReminderId.CONTACT) {
            }
        }
    };

    NewGameReceiver mNewGameReceiver = new NewGameReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String data = intent.getStringExtra(Extras.EXTRA_DATA);
                int gameActionType = intent.getIntExtra(NewGameReceiver.ACTION_NEWGAME, NewGameReceiver.ACTION_NEWGAME_TYPE);
                if (gameActionType == NewGameReceiver.ACTION_NEWGAME_TYPE) {
                    sendNewGameMessage(data);//发出自定义消息
                } else if (gameActionType == NewGameReceiver.ACTION_CANCELGAME_TYPE && mChatFragment != null && mChatFragment.getMessages() != null && mChatFragment.getMessages().size() > 0) {
//                    mPaijuFragment.getGamePlayingList();
                    GameAttachment attachment = new GameAttachment(data);
                    for (int i = mChatFragment.getMessages().size() - 1; i >= 0; i-- ) {
                        IMMessage imMessage = mChatFragment.getMessages().get(i);
                        if (imMessage.getAttachment() != null && imMessage.getAttachment() instanceof GameAttachment) {
                            if (attachment.gid.equals(((GameAttachment) imMessage.getAttachment()).gid)) {
                                updateGameStatusFinished(imMessage, i);
                                break;
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int type = intent.getIntExtra(Extras.EXTRA_TYPE, 0);
            if (type == CircleConstant.TYPE_PAIPU) {
                PaipuEntity paipuEntity = (PaipuEntity) intent.getSerializableExtra(Extras.EXTRA_DATA);
                PaipuAttachment attachment = new PaipuAttachment(paipuEntity.jsonDataStr);
                IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.Team, getString(R.string.msg_custom_type_paipu_info_desc), attachment);
//                messageFragment.sendMessage(message);
                mChatFragment.sendMessage(message);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestTimeLimit.lastGetGamePlayingTime = 0;
        PaijuListPref.firstLaunchTeamPaiju = true;
        LogUtil.i(TeamPaijuFragment.TAG, "TeamMessageAC onCreate");
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        pageType = getIntent().getIntExtra(TAG, PAGE_TYPE_PAIJU);
        findViews();
        setUnreadTeamMsg();//TeamMessageFragment初始化时会发送消息回执，一定要在发送消息回执之前设置未读消息数目，否则发送完回执未读消息清零了
        initFragment();
//        messageFragment.setOnClubConfigChangeListener(this);
        mChatFragment.setOnClubConfigChangeListener(this);
        LogUtil.i(TAG, "sessionId : " + sessionId);//就是teamId
        initInvalidTipView();
        requestTeamInfo();
        registerObservers(true);
//        registerCustomNotificationObserver(true);
//        sendCustomNotification(sessionId , SessionTypeEnum.Team);
    }

    private void getCostlist() {
        if (mHordeAction == null) {
            mHordeAction = new HordeAction(this, null);
        }
        mHordeAction.getCostList(sessionId, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response == null || response.optJSONArray("data") == null || response.optJSONArray("data").length() <= 0) {
                    costList.clear();
                    return;
                }
                costList.clear();
                JSONArray dataArray = response.optJSONArray("data");
                for (int i = 0; i < dataArray.length(); i++) {
                    HordeEntity hordeEntity = new HordeEntity();
                    JSONObject item = dataArray.optJSONObject(i);
                    hordeEntity.name = item.optString("name");
                    hordeEntity.horde_id = item.optString("horde_id");
                    hordeEntity.money = item.optInt("money");
                    hordeEntity.is_my = item.optInt("is_my");
                    hordeEntity.is_control = item.optInt("is_control");
                    costList.add(hordeEntity);
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
    }

    private boolean hasRegisterReceiveMsg = false;//当viewpager滑到"聊天页面"再接受消息，否则只要一接收消息mainactivity就会把这个群组的未读消息置为0
    private void initFragment() {
        mPaijuFragment = TeamPaijuFragment.newInstance(sessionId);
        mChatFragment = (TeamMessageFragment) fragment();
        mRecordFragment = TeamRecordFragment.newInstance(sessionId);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(mPaijuFragment);
        fragmentList.add(mChatFragment);
        fragmentList.add(mRecordFragment);
        mAdapter = new TeamMsgAdapter(getSupportFragmentManager(), fragmentList, this);
        mViewPager = (ViewPager) findViewById(R.id.team_message_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                LogUtil.i("TeamMessageAC onPageSelected position: " + position);
                setSwipeBackEnable(position == 0);
                if (position == 0) {
                    if (team != null && team.isMyTeam() && mPaijuFragment != null) {
                        mPaijuFragment.getGamePlayingList();//更新牌局列表信息  onPageSelected
                    }
                } else if (position == 1) {
                    mUnreadNum = 0;
                    team_message_chat_unread_num.setVisibility(View.INVISIBLE);
                    if (mChatFragment != null && !hasRegisterReceiveMsg) {
//                        mChatFragment.registerObservers(true);
                        hasRegisterReceiveMsg = true;
                    }
                    ChessApp.unreadChatNumPerTeam.put(sessionId, 0);//进入这个俱乐部页面后，群的未读消息清零. 这个做法不行，"聊天"页面只要一加载就会发送"回执"消息，意味着未读消息就是0了
                } else if (position == 2) {
                    if (mRecordFragment != null) {
                        mRecordFragment.requestNewData();
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (pageType == PAGE_TYPE_CHAT) {
            team_message_chat_unread_num.setVisibility(View.INVISIBLE);////关闭红点
            ChessApp.unreadChatNumPerTeam.put(sessionId, 0);//进入这个俱乐部页面后，群的未读消息清零. 这个做法不行，"聊天"页面只要一加载就会发送"回执"消息，意味着未读消息就是0了
        }
        team_message_tabs.setViewPager(mViewPager);
        team_message_tabs.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                View linearLayout = team_message_tabs.getChildAt(0);//.getChildAt(1);
                LogUtil.i("onPreDraw   linearLayout " + linearLayout);
                if (!(linearLayout instanceof LinearLayout && ((LinearLayout) linearLayout).getChildCount() >= 2)) {
                    return true;
                }
                View child = ((LinearLayout) linearLayout).getChildAt(1);
                float middleX = (child.getRight() - child.getLeft()) / 2;
                int offset = ScreenUtil.dp2px(TeamMessageAC.this, 20);
                team_message_chat_unread_num.setX(middleX + child.getLeft() + offset);
//                setUnreadTeamMsg();
                team_message_tabs.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
        mViewPager.setCurrentItem(pageType);
        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
    }

    private void setUnreadTeamMsg() {
        for (Map.Entry<String, Integer> entry : ChessApp.unreadChatNumPerTeam.entrySet()) {
            if (entry.getKey().equals(sessionId)) {
                int unreadAppMsgNum = entry.getValue();
                mUnreadNum += unreadAppMsgNum;
                if (mUnreadNum <= 0) {
                    team_message_chat_unread_num.setVisibility(View.INVISIBLE);
                } else {
                    if (mUnreadNum > 99) {
                        team_message_chat_unread_num.setText(R.string.new_message_count_max);
                    } else if (mUnreadNum >= 1) {
                        team_message_chat_unread_num.setText("" + mUnreadNum);
                    }
                    team_message_chat_unread_num.setVisibility(mUnreadNum >= 1 ? View.VISIBLE : View.INVISIBLE);
                }
                break;
            }
        }
    }

    private int mUnreadNum = 0;
    private void setUnreadTeamMsg(int unreadNum) {
        mUnreadNum += unreadNum;
        if (mUnreadNum <= 0) {
            team_message_chat_unread_num.setVisibility(View.INVISIBLE);
        } else {
            if (mUnreadNum > 99) {
                team_message_chat_unread_num.setText(R.string.new_message_count_max);
            } else if (mUnreadNum >= 1) {
                team_message_chat_unread_num.setText("" + mUnreadNum);
            }
            team_message_chat_unread_num.setVisibility(mUnreadNum >= 1 ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void initInvalidTipView() {
        invalidTeamTipView = (RelativeLayout) findViewById(R.id.rl_invalid_tip);
        tv_invalid_tip = (TextView) findViewById(R.id.tv_invalid_tip);
        btn_invalid_action = (TextView) findViewById(R.id.btn_invalid_action);
        btn_invalid_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVerifyActivity.start(TeamMessageAC.this, sessionId, AddVerifyActivity.TYPE_VERIFY_CLUB);
            }
        });
    }

    protected void findViews() {
        team_message_tabs = (PagerSlidingTabStrip) findViewById(R.id.team_message_tabs);
        team_message_chat_unread_num = (TextView) findViewById(R.id.team_message_chat_unread_num);
        btn_head_back = (TextView) findViewById(R.id.btn_head_back);
        iv_head_mute = (ImageView) findViewById(R.id.iv_head_mute);
        Drawable rightHeadButtonDrawable = getResources().getDrawable(R.mipmap.icon_chatroom_msg_member);
        rightHeadButtonDrawable.setBounds(0, 0, rightHeadButtonDrawable.getIntrinsicWidth(), rightHeadButtonDrawable.getIntrinsicHeight());
        TextView rightHeadButton = (TextView) findViewById(com.htgames.nutspoker.R.id.tv_head_right);
        rightHeadButton.setCompoundDrawables(rightHeadButtonDrawable, null, null, null);
        rightHeadButton.setText("");
        rightHeadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (team == null) {
                    return;
                }
                if (team.getType() == TeamTypeEnum.Advanced) {
                    ClubInfoActivity.start(TeamMessageAC.this, team, ClubInfoActivity.FROM_TYPE_CLUB, costList); // 启动固定群组资料页
                } else {
                    GroupInfoActivity.start(TeamMessageAC.this, sessionId); // 启动普通群组资料页
                }
            }
        });
    }

    public void updataInvalidTipView() {
        if (team.isMyTeam()) {
            invalidTeamTipView.setVisibility(View.GONE);
            return;
        }
        invalidTeamTipView.setVisibility(View.VISIBLE);
        if (team.getType() == TeamTypeEnum.Normal) {
            tv_invalid_tip.setText(R.string.session_tip_group_not);
            btn_invalid_action.setVisibility(View.GONE);
        } else {
            if (!team.isMyTeam()) {
                //被移除俱乐部
                btn_invalid_action.setVisibility(View.VISIBLE);
                tv_invalid_tip.setText(R.string.session_tip_club_not);
                btn_invalid_action.setText(R.string.invalid_action_apply_club);
            }
            //俱乐部已经解散
//            tv_invalid_tip.setText(R.string.session_tip_club_is_dismiss);
        }
    }

    /**
     * 请求群基本信息
     */
    private void requestTeamInfo() {
        Team t = TeamDataCache.getInstance().getTeamById(sessionId);
        if (t != null) {
            LogUtil.i(TAG, "本地存在，sessionId：" + sessionId);
            updateTeamInfo(t);
        } else {
            LogUtil.i(TAG, "本地不存在，获取网络 sessionId：" + sessionId);
            TeamDataCache.getInstance().fetchTeamById(sessionId, new SimpleCallback<Team>() {
                @Override
                public void onResult(boolean success, Team result) {
                    if (success && result != null) {
                        LogUtil.i(TAG, "获取网络onSuccess ：" + result.getExtServer());
                        updateTeamInfo(result);
                    } else {
                        com.netease.nim.uikit.common.util.log.LogUtil.e(TAG, "request team info failed, team is null");
                        Toast.makeText(TeamMessageAC.this, R.string.get_club_info_failure, Toast.LENGTH_SHORT);
                        finish();
                    }
                }
            });
        }
    }

    public void joinOrCreateGame(final GameEntity game) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, null);
            return;
        }
        String joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_GROUP;
        if (!TextUtils.isEmpty(sessionId) && TeamDataCache.getInstance().getTeamById(sessionId) != null) {
            Team team = TeamDataCache.getInstance().getTeamById(sessionId);
            if (team.getType() == TeamTypeEnum.Advanced) {
                joinWay = UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CLUB;
            }
        }
        mGameAction.joinGame(joinWay, game.code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
            }
            @Override
            public void onFailed(int resultCode, JSONObject response) {
                if (resultCode == ApiCode.CODE_GAME_NONE_EXISTENT || resultCode == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                    //牌局不存在
                    if (mPaijuFragment != null) {
                        mPaijuFragment.removePaijuNotExisted(game.code);
                    }
                }
            }
        });
    }

    public void checkGameVersionJoin(final GameEntity gameEntity) {
        if (HotUpdateHelper.isGameUpdateIng()) {
            return;
        }
        CheckHotUpdateCallback callback = new CheckHotUpdateCallback() {
            @Override
            public void notUpdate() {
                joinOrCreateGame(gameEntity);
            }
        };
        if (HotUpdateHelper.isNeedToCheckVersion()) {
            mHotUpdateAction.doHotUpdate(true , callback);
        } else{
            callback.notUpdate();
        }
    }

    @Override
    public void checkGameVersionCreate(CheckHotUpdateCallback callback) {
        mHotUpdateAction.doHotUpdate(true , callback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TeamPaijuFragment.TAG, "TeamMessageAC onResume");
        if (messageFragment != null) {
//            messageFragment.notifyMessageList();
        }
        getCostlist();//onResume
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
        LogUtil.i(PokerActivity.TAG, "TeamMessageAc onSaveInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TeamPaijuFragment.TAG, "TeamMessageAC onPause");
    }

    @Override
    protected void onDestroy() {
        registerObservers(false);
        if (mChatFragment != null && hasRegisterReceiveMsg) {
//            mChatFragment.registerObservers(false);
            hasRegisterReceiveMsg = false;
        }
        if (mHordeAction != null) {
            mHordeAction.onDestroy();
            mHordeAction = null;
        }
        LogUtil.i(TeamPaijuFragment.TAG, "TeamMessageAC onDestroy");
//        ChessApp.unreadChatNumPerTeam.put(team.getId(), mUnreadNum);
        super.onDestroy();
    }

    /**
     * 更新群信息
     * @param d
     */
    private void updateTeamInfo(final Team d) {
        if (d == null) {
            return;
        }
        team = d;
        mChatFragment.setTeam(d);
        String teamName = team.getName();
        //判断是否还在当前群组里面
        mPaijuFragment.team = d;
        mPaijuFragment.updataGameCreateBtnUI(null);
        if (!team.isMyTeam()) {
            findViewById(R.id.tv_head_right).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_head_right).setVisibility(View.VISIBLE);
        }
        if (team.mute()) {
            iv_head_mute.setVisibility(View.VISIBLE);
        } else {
            iv_head_mute.setVisibility(View.GONE);
        }
//        invalidTeamTipView.setVisibility(team.isMyTeam() ? View.GONE : View.VISIBLE);
        updataInvalidTipView();
        creator = team.getCreator();
        initHeadView();
    }

    private void initHeadView() {
        boolean noName = (team == null || TextUtils.isEmpty(team.getName()) || ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName()));
        setHeadTitle((noName ? getString(R.string.group) : team.getName()) + getString(R.string.team_num_head_title, team.getMemberCount()));
        if (creator.equals(DemoCache.getAccount())) {

        } else {

        }
    }

    //刷新Action工具
    public void updateActionPanelLayout(TeamMember tm) {
//        if (fragment != null) {
//            customization.actions = SessionHelper.getClubActions(sessionId, customization.actions,tm);
//            fragment.updateActionPanelLayout(customization.actions);
//        }
        if (mChatFragment != null) {
            customization.actions = SessionHelper.getClubActions(sessionId, customization.actions,tm);
            mChatFragment.updateActionPanelLayout(customization.actions);
        }
    }

    public void quitTeam(final String teamId) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        //退群后，群内所有成员(包括退出者)会收到一条消息类型为 notification 的 IMMessage，附件类型为 MemberChangeAttachment。
        NIMClient.getService(TeamService.class).quitTeam(teamId).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.dismiss_team_success, Toast.LENGTH_SHORT).show();
                RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
            }
            @Override
            public void onFailed(int i) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.dismiss_team_failed, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onException(Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    public void dismissTeam(final String teamId) {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        NIMClient.getService(TeamService.class).dismissTeam(teamId).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.quit_team_success, Toast.LENGTH_SHORT).show();
                RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                finish();
            }
            @Override
            public void onFailed(int i) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.quit_team_failed, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onException(Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_GAMECREATE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String jsonData = data.getStringExtra(Extras.EXTRA_DATA);
                sendNewGameMessage(jsonData);
                try {
                    GameEntity gameEntity = GameJsonTools.parseGameData(jsonData);
                    gameEntity.currentServerTime = (gameEntity.createTime);//设置服务器时间为创建时间
                    if (gameEntity != null) {
                        int gameMode = gameEntity.gameMode;
                        if (gameMode == GameConstants.GAME_MODE_NORMAL || gameMode == GameConstants.GAME_MODE_SNG) {
//                            PokerActivity.startGameByPlay(TeamMessageAC.this, gameEntity.getGid(), "" + gameMode, gameEntity.originalCode, gameEntity.room_id, gameEntity.getType(), gameEntity.getServerIp(), 0);
                        } else {

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUESTCODE_GAMEBILL && resultCode == Activity.RESULT_OK) {
            //分享牌局记录
            GameBillEntity gameBillEntity = (GameBillEntity) data.getSerializableExtra("bill");
            BillAttachment attachment = new BillAttachment(gameBillEntity.jsonStr);
            IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.Team, gameBillEntity.jsonStr, attachment);
//            messageFragment.sendMessage(message);
            mChatFragment.sendMessage(message);
        }
    }

    /**
     * 发送新的牌局消息
     * @param jsonData
     */
    public void sendNewGameMessage(String jsonData) {
        if (StringUtil.isSpace(jsonData)) {
            return;
        }
        GameAttachment attachment = new GameAttachment(jsonData);
        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
        IMMessage message = MessageBuilder.createCustomMessage(team.getId(), SessionTypeEnum.Team, getString(R.string.msg_custom_type_game_create_desc), attachment);
//        messageFragment.sendMessage(message);
        mChatFragment.sendMessage(message);
    }

    public void sendGameTipNotification(String tipContent) {
        CustomNotificationHelper.sendGameTipNotification(team.getId(), tipContent);
    }

    /**更新牌局状态为结束状态
     * @param message
     */
    public void updateGameStatusFinished(IMMessage message, int position) {
        //先搜索，之后获取到对应的IMMessage，去求该状态，需要1.7.0版本
        if (message.getAttachment() instanceof GameAttachment) {
            GameEntity gameEntity = ((GameAttachment) message.getAttachment()).getValue();
            gameEntity.status = (GameStatus.GAME_STATUS_FINISH);
            String json = GameAttachment.toJsonData(gameEntity);
            message.setContent(json);
            message.setAttachment(new GameAttachment(json));
        }
        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
        if (mChatFragment != null) {
            mChatFragment.notifydatasetChanged(message, position);
        }
    }

    HordeUpdateManager.HordeUpdateCallback hordeUpdateCallback = new HordeUpdateManager.HordeUpdateCallback() {
        @Override
        public void onUpdated(UpdateItem item) {
            if (item == null) {
                return;
            }
            if (item.updateType == UpdateItem.UPDATE_TYPE_CREATE_HORDE || item.updateType == UpdateItem.UPDATE_TYPE_CANCEL_HORDE) {
                getCostlist();//hordeUpdateCallback
            }
        }
    };













    @Override
    public void configChanged() {
        updateActionPanelLayout(null);
    }

    @Override
    protected MessageFragment fragment() {
        if (mChatFragment == null) {
            mChatFragment = TeamMessageFragment.newInstance();
        }
        // 添加fragment
        Bundle arguments = getIntent().getExtras();
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.Team);
        arguments.putInt(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
//        fragment = new TeamMessageFragment();
        mChatFragment.setArguments(arguments);
//        mChatFragment.setContainerId(R.id.message_fragment_container);
        return mChatFragment;
    }

    protected TFragment switchContent(TFragment fragment, boolean needAddToBackStack) {
        if (mChatFragment == null) {
            mChatFragment = TeamMessageFragment.newInstance();
            // 添加fragment
            Bundle arguments = getIntent().getExtras();
            arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.Team);
            arguments.putInt(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
//        fragment = new TeamMessageFragment();
            mChatFragment.setArguments(arguments);
//        mChatFragment.setContainerId(R.id.message_fragment_container);
        }
        return mChatFragment;
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_team_msg_new;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
        }
    }

    @Override
    public void onGameCreate(List<IMMessage> messages) {
        if (messages == null || messages.size() <= 0 || mPaijuFragment == null || team == null) {
            return;
        }
        boolean isMute = RecentContactHelp.isTeamMsgMute(team.getId());
        if (mViewPager.getCurrentItem() != 1 && !isMute) {
            int unreadNum = 0;
            for (int i = 0; i < messages.size(); i++) {
                IMMessage imMessage = messages.get(i);
                if (imMessage.getSessionId().equals(team.getId())) {
                    unreadNum++;
                    continue;
                }
            }
            setUnreadTeamMsg(unreadNum);
        }
        for (int i = (messages.size() - 1); i >= 0; i--) {
            IMMessage imMessage = messages.get(i);
            if (imMessage.getAttachment() != null && imMessage.getAttachment() instanceof GameAttachment && imMessage.getSessionId().equals(team.getId())) {
                RequestTimeLimit.lastGetGameListInClub = 0;
                mPaijuFragment.getGamePlayingList();//onGameCreate
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }
}
