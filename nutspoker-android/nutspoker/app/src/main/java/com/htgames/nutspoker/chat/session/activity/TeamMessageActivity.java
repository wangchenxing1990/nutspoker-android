package com.htgames.nutspoker.chat.session.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.game.model.GameStatus;
import com.netease.nim.uikit.bean.GameBillEntity;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.PaipuEntity;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.notification.helper.CustomNotificationHelper;
import com.htgames.nutspoker.chat.session.SessionHelper;
import com.htgames.nutspoker.chat.session.extension.BillAttachment;
import com.htgames.nutspoker.chat.session.extension.GameAttachment;
import com.htgames.nutspoker.chat.session.extension.PaipuAttachment;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.activity.Club.GroupInfoActivity;
import com.htgames.nutspoker.data.common.CircleConstant;
import com.netease.nim.uikit.constants.GameConstants;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.chat.reminder.ReminderId;
import com.htgames.nutspoker.chat.reminder.ReminderItem;
import com.htgames.nutspoker.chat.reminder.ReminderManager;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.chesscircle.interfaces.OnClubConfigChangeListener;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
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
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 群聊界面
 * <p/>
 * Created by huangjun on 2015/3/5.
 */
public class TeamMessageActivity extends BaseMessageActivity implements OnClubConfigChangeListener {
    private static final String TAG = "TeamMessageActivity";
    private Team team;
    private RelativeLayout invalidTeamTipView;
    TextView tv_invalid_tip;
    TextView btn_invalid_action;
    TextView tv_details;
    ImageView iv_head_mute;
    //
    private TeamMessageFragment fragment;
    public String creator;
    private TextView tv_chatroom_game;
    public static final int REQUESTCODE_GAMECREATE = 1;
    //

    public static void StartActivity(Context context,int type,PaipuEntity paipu){
        Intent intent = new Intent(context, P2PMessageActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data", paipu);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, TeamMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void start(Context context, String tid, int newMessageNum, SessionCustomization customization) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_ACCOUNT, tid);
        intent.putExtra(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
        intent.putExtra(Extras.EXTRA_CUSTOMIZATION, customization);
        intent.setClass(context, TeamMessageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_team_message;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int type = intent.getIntExtra(Extras.EXTRA_TYPE, 0);
            if (type == CircleConstant.TYPE_PAIPU) {
                PaipuEntity paipuEntity = (PaipuEntity) intent.getSerializableExtra(Extras.EXTRA_DATA);
                PaipuAttachment attachment = new PaipuAttachment(paipuEntity.jsonDataStr);
                IMMessage message = MessageBuilder.createCustomMessage(sessionId, SessionTypeEnum.Team, getString(R.string.msg_custom_type_paipu_info_desc), attachment);
                messageFragment.sendMessage(message);
            }
        }
    }

    public void initInvalidTipView() {
        invalidTeamTipView = (RelativeLayout) findViewById(R.id.rl_invalid_tip);
        tv_invalid_tip = (TextView) findViewById(R.id.tv_invalid_tip);
        btn_invalid_action = (TextView) findViewById(R.id.btn_invalid_action);
        btn_invalid_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddVerifyActivity.start(TeamMessageActivity.this, sessionId, AddVerifyActivity.TYPE_VERIFY_CLUB);
            }
        });
    }

    public void updataInvalidTipView() {
        if  (invalidTeamTipView == null) {
            return;
        }
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

    protected void findViews() {
        btn_head_back = (TextView) findViewById(R.id.btn_head_back);
        iv_head_mute = (ImageView) findViewById(R.id.iv_head_mute);
        //
        tv_chatroom_game = (TextView) findViewById(R.id.tv_chatroom_game);
        tv_chatroom_game.setVisibility(View.VISIBLE);
        setHeadRightButton(R.string.details, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (team == null) {
                    return;
                }
                if (team.getType() == TeamTypeEnum.Advanced) {
                    ClubInfoActivity.start(TeamMessageActivity.this, team, ClubInfoActivity.FROM_TYPE_CLUB, null); // 启动固定群组资料页
                } else {
                    GroupInfoActivity.start(TeamMessageActivity.this, sessionId); // 启动普通群组资料页
                }
            }
        });
        tv_chatroom_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gamePlayingList != null && !gamePlayingList.isEmpty()) {
//                    TeamGameListActivity.start(TeamMessageActivity.this, team);
                } else {
//                    GameCreateActivity.startActivityForResult(TeamMessageActivity.this, REQUESTCODE_GAMECREATE, team.getId(), GameConstants.GAME_TYPE_CLUB);//这个teammessageactivity.java类被teammessageac.java类代替，注释掉部分代码
                }
            }
        });
        tv_chatroom_game.setText("");
    }

    public void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);//注册群信息更新监听
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberDataChangedObserver);//注册群信息更新监听
            //
            IntentFilter filter = new IntentFilter();//动态注册有新消息提醒通知
            filter.addAction(NewGameReceiver.ACTION_NEWGAME);
            registerReceiver(mNewGameReceiver, filter);
            //
            ReminderManager.getInstance().registerUnreadNumChangedCallback(unreadNumChangedCallback);//注册未读消息数量观察者
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberDataChangedObserver);
            //
            unregisterReceiver(mNewGameReceiver);
            //
            ReminderManager.getInstance().unregisterUnreadNumChangedCallback(unreadNumChangedCallback);//注册未读消息数量观察者
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        messageFragment.setOnClubConfigChangeListener(this);
        LogUtil.i(TAG, "sessionId : " + sessionId);//就是teamId
        findViews();
        initInvalidTipView();
        requestTeamInfo();
        registerObservers(true);
//        registerCustomNotificationObserver(true);
//        sendCustomNotification(sessionId , SessionTypeEnum.Team);
        updateNewChatUI(btn_head_back, ReminderManager.getInstance().getUnreadSessionCount());
    }

    ReminderManager.UnreadNumChangedCallback unreadNumChangedCallback = new ReminderManager.UnreadNumChangedCallback() {
        @Override
        public void onUnreadNumChanged(ReminderItem item) {
            if (item.getId() == ReminderId.SESSION) {
                updateNewChatUI(btn_head_back, item.getUnread());
            } else if (item.getId() == ReminderId.CONTACT) {
            }
        }
    };

    @Override
    protected void onDestroy() {
        registerObservers(false);
        super.onDestroy();
//        registerCustomNotificationObserver(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (team != null && team.isMyTeam()) {
            getGamePlayingList();
        }
        if (messageFragment != null) {
//            messageFragment.notifyMessageList();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void getGamePlayingList() {
        mGameAction.getGamePlayingList(sessionId, mGameRequestCallback);
    }

    GameRequestCallback mGameRequestCallback = new GameRequestCallback() {

        @Override
        public void onSuccess(JSONObject response) {
            gamePlayingList = GameJsonTools.getGamePlayingList(sessionId, response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updataGameCreateBtnUI(null);
                }
            });
        }

        @Override
        public void onFailed(int code, JSONObject response) {

        }
    };

    /**
     * 请求群基本信息
     */
    private void requestTeamInfo() {
        // 请求群基本信息
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
                        Toast.makeText(TeamMessageActivity.this, R.string.get_club_info_failure, Toast.LENGTH_SHORT);
                        finish();
                    }
                }
            });
        }
    }

    public void updataGameCreateBtnUI(TeamMember tm) {
        if (team != null) {
            if (!team.isMyTeam()) {
                tv_chatroom_game.setVisibility(View.GONE);
            } else {
                if (gamePlayingList == null || gamePlayingList.size() == 0) {
                    tv_chatroom_game.setBackgroundResource(R.drawable.btn_match_checkin);//btn_chatroom_game_create);
                    tv_chatroom_game.setText("");

                    if(ClubConstant.isClubCreateGameByCreatorLimit(team)){//限制开局
                        if(tm == null)
                            tm = NIMClient.getService(TeamService.class).queryTeamMemberBlock(team.getId(),DemoCache.getAccount());
                        if(tm.getType() == TeamMemberType.Owner || tm.getType() == TeamMemberType.Manager)
                            tv_chatroom_game.setVisibility(View.VISIBLE);
                        else
                            tv_chatroom_game.setVisibility(View.GONE);
                    } else
                        tv_chatroom_game.setVisibility(View.VISIBLE);
                } else {
                    tv_chatroom_game.setBackgroundResource(R.drawable.btn_match_checkin);//btn_chatroom_game_ing);
                    tv_chatroom_game.setText(String.valueOf(gamePlayingList.size()));
                    tv_chatroom_game.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param d
     */
    private void updateTeamInfo(final Team d) {
        if (d == null) {
            return;
        }
        team = d;
        fragment.setTeam(team);
        String teamName = team.getName();
//        setTitle(team == null ? sessionId : teamName + "(" + team.getMemberCount() + "人)");
        boolean noName = (team == null || TextUtils.isEmpty(team.getName()) || ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName()));
//        setHeadTitle((noName ? getString(R.string.group) : team.getName()) + "(" + team.getMemberCount() + "人)");
        setHeadTitle((noName ? getString(R.string.group) : team.getName()) + getString(R.string.team_num_head_title, team.getMemberCount()));
        //判断是否还在当前群组里面
        updataGameCreateBtnUI(null);
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
//        setHeadTitle((noName ? getString(R.string.group) : team.getName()) + "(" + team.getMemberCount() + "人)");
        setHeadTitle((noName ? getString(R.string.group) : team.getName()) + getString(R.string.team_num_head_title, team.getMemberCount()));
        if (creator.equals(DemoCache.getAccount())) {

        } else {

        }
    }

    //刷新Action工具
    public void updateActionPanelLayout(TeamMember tm) {
        if (fragment != null) {
            customization.actions = SessionHelper.getClubActions(sessionId, customization.actions,tm);
            fragment.updateActionPanelLayout(customization.actions);
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
            fragment.refreshMessageList();

            for(TeamMember tm : members){
                if(tm.getAccount().equals(DemoCache.getAccount())){
                    //如果是我的资料变动，需要更新游戏开局按钮
                    updataGameCreateBtnUI(tm);
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

    @Override
    protected MessageFragment fragment() {
        // 添加fragment
        Bundle arguments = getIntent().getExtras();
        arguments.putSerializable(Extras.EXTRA_TYPE, SessionTypeEnum.Team);
        arguments.putInt(Extras.EXTRA_NEW_MESSAGE_NUM, newMessageNum);
        fragment = new TeamMessageFragment();
        fragment.setArguments(arguments);
        fragment.setContainerId(R.id.message_fragment_container);
        return fragment;
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
//                            PokerActivity.startGameByPlay(TeamMessageActivity.this, gameEntity.getGid(), "" + gameMode, gameEntity.originalCode, gameEntity.room_id, gameEntity.getType(), gameEntity.getServerIp(), 0, "2");
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
            messageFragment.sendMessage(message);
        }
    }

    /**
     * 发送新的牌局消息
     *
     * @param jsonData
     */
    public void sendNewGameMessage(String jsonData) {
        GameAttachment attachment = new GameAttachment(jsonData);
        //创建一条APP自定义类型消息, 同时提供描述字段，可用于推送以及状态栏消息提醒的展示。
        IMMessage message = MessageBuilder.createCustomMessage(team.getId(), SessionTypeEnum.Team, getString(R.string.msg_custom_type_game_create_desc), attachment);
        messageFragment.sendMessage(message);
    }

    public void sendGameTipNotification(String tipContent) {
        CustomNotificationHelper.sendGameTipNotification(team.getId(), tipContent);
    }

    ArrayList<GameEntity> gamePlayingList = null;

    NewGameReceiver mNewGameReceiver = new NewGameReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context , "onReceive" , Toast.LENGTH_SHORT).show();
            if (intent != null) {
                String data = intent.getStringExtra(Extras.EXTRA_DATA);
                sendNewGameMessage(data);//发出自定义消息
            }
        }
    };

    /**
     * 更新牌局状态为结束状态
     *
     * @param message
     */
    public void updateGameStatusFinished(IMMessage message) {
        //先搜索，之后获取到对应的IMMessage，去求该状态，需要1.7.0版本
        if (message.getAttachment() instanceof GameAttachment) {
            GameEntity gameEntity = ((GameAttachment) message.getAttachment()).getValue();
            gameEntity.status = (GameStatus.GAME_STATUS_FINISH);
            message.setContent(GameAttachment.toJsonData(gameEntity));
        }
        NIMClient.getService(MsgService.class).updateIMMessageStatus(message);
    }

    @Override
    public void configChanged() {
        updateActionPanelLayout(null);
    }
}
