package com.htgames.nutspoker.game.match.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.htgames.nutspoker.ChessApp;
import com.htgames.nutspoker.R;
import com.netease.nim.uikit.api.ApiCode;
import com.htgames.nutspoker.api.ApiResultHelper;
import com.htgames.nutspoker.cocos2d.PokerActivity;
import com.htgames.nutspoker.db.HandsCollectDBHelper;
import com.htgames.nutspoker.game.match.adapter.MatchPagerAdapter;
import com.htgames.nutspoker.game.match.fragment.FreeAllPlayerFrg;
import com.htgames.nutspoker.game.match.fragment.FreeMyPlayerFrg;
import com.htgames.nutspoker.game.model.GameStatus;
import com.htgames.nutspoker.interfaces.GameRequestCallback;
import com.htgames.nutspoker.interfaces.VolleyCallback;
import com.htgames.nutspoker.net.RequestTimeLimit;
import com.htgames.nutspoker.push.GeTuiHelper;
import com.htgames.nutspoker.receiver.NewGameReceiver;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalytics;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEventConstants;
import com.htgames.nutspoker.tool.json.GameJsonTools;
import com.htgames.nutspoker.ui.action.ClubAction;
import com.htgames.nutspoker.ui.action.GameAction;
import com.htgames.nutspoker.ui.activity.Login.LoginActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.util.PrintUtil;
import com.htgames.nutspoker.view.FreeGameTableView;
import com.htgames.nutspoker.view.ShareView;
import com.netease.nim.uikit.bean.GameEntity;
import com.netease.nim.uikit.bean.UserEntity;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.preference.UserPreferences;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.db.DBUtil;
import com.netease.nim.uikit.login.LogoutHelper;
import com.netease.nim.uikit.nim.ChatRoomMemberCache;
import com.netease.nim.uikit.permission.PermissionUtils;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomNotificationAttachment;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.constant.NotificationType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 周智慧 on 17/3/2.   自由局（现金局和sng）的等待界面，和MatchRoomActivity对应
 */

public class FreeRoomAC extends BaseActivity implements View.OnClickListener {
    public String roomId;
    public String mJoinWay;
    String teamId;
    String gameCode;
    GameEntity gameInfo;
    public GameAction mGameAction;
    public ClubAction mClubAction;
    PopupWindow rightTopPopupWindow = null;
    String joinCode = "000000";
    String channel = "000000";
    String hostIp;
    boolean startByCreate = false;
    public boolean mIsMeCreator = false;//我是不是局头
    public  boolean mIsMeMgr = false;//我是不是管理员
    public static String TAG = GameAction.class.getSimpleName();
    private AbortableFuture<EnterChatRoomResultData> enterRequest;
    boolean isInitiativeDismiss = false;//是否是主动解散
    private boolean mgrFrgHasAdded;
    @OnClick(R.id.tv_free_all_player_click_area) void clickAllPlayerTab() {
        free_room_viewpager.setCurrentItem(0);
        tv_free_all_player.setSelected(free_room_viewpager.getCurrentItem() == 0);
        tv_free_my_player.setSelected(free_room_viewpager.getCurrentItem() == 1);
    }
    @OnClick(R.id.tv_free_my_player_click_area) void clickMyPlayerTab() {
        free_room_viewpager.setCurrentItem(1);
        tv_free_all_player.setSelected(free_room_viewpager.getCurrentItem() == 0);
        tv_free_my_player.setSelected(free_room_viewpager.getCurrentItem() == 1);
    }
    @BindView(R.id.tv_free_my_player_click_area) View tv_free_my_player_click_area;
    @BindView(R.id.mQuickGameTableView) FreeGameTableView mQuickGameTableView;
    @BindView(R.id.free_room_viewpager) ViewPager free_room_viewpager;
    @BindView(R.id.tv_free_all_player) TextView tv_free_all_player;
    @BindView(R.id.tv_free_my_player) TextView tv_free_my_player;
    @BindView(R.id.btn_free_game_start) Button btn_free_game_start;
    @BindView(R.id.free_game_divider) View free_game_divider;
    MatchPagerAdapter mMttPagerAdapter;
    List<Fragment> fragmentList = new ArrayList<>();
    FreeAllPlayerFrg mFreeAllPlayerFrg;
    FreeMyPlayerFrg mFreeMyPlayerFrg;
    public static void startByCreate(final Activity activity, String roomId, String teamId, String gameName, String gameCode, String hostIp) {//如果teamId非空表示是从俱乐部创建的自由局游戏
        Intent intent = new Intent(activity, FreeRoomAC.class);
        intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
        intent.putExtra(Extras.EXTRA_ROOM_ID, roomId);
        intent.putExtra(Extras.EXTRA_GAME_NAME, gameName);
        intent.putExtra(Extras.EXTRA_GAME_IS_CREATOR, true);
        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, hostIp);
        intent.putExtra(GameConstants.KEY_GAME_IS_ADMIN, 1);
        intent.putExtra(Extras.EXTRA_GAME_CODE, gameCode);
        intent.putExtra(Extras.EXTRA_GAME_JOIN_CODE, gameCode);//
        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameCode);
        intent.putExtra("startByCreate", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public static void startByJoin(Activity activity, GameEntity gameEntity, final String joinWay) {
        Intent intent = new Intent(activity, FreeRoomAC.class);
        intent.putExtra(UmengAnalyticsEventConstants.WAY_GAME_JOIN, joinWay);
        intent.putExtra(Extras.EXTRA_ROOM_ID, gameEntity.room_id);
        intent.putExtra(Extras.EXTRA_TEAM_ID, gameEntity.tid);
        intent.putExtra(Extras.EXTRA_GAME_CODE, gameEntity.code);
        intent.putExtra(Extras.EXTRA_GAME_JOIN_CODE, gameEntity.joinCode);//
        intent.putExtra(Extras.EXTRA_GAME_CHANNEL, gameEntity.channel);
        intent.putExtra(GameConstants.KEY_GAME_IS_ADMIN, gameEntity.is_admin);
        intent.putExtra(Extras.EXTRA_GAME_SERVERIP, gameEntity.serverIp);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerObservers(true);// 注册监听
        getData();
        mGameAction = new GameAction(this, null);
        mClubAction = new ClubAction(this, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free);
        mUnbinder = ButterKnife.bind(this);
        initTabLayout();
        checkMyIdentity();
        DialogMaker.showProgressDialog(this, "", false);
        if (StringUtil.isSpace(roomId) || "0".equals(roomId)) {
            getGameConfig();
        } else {
            enterRoom();
        }
        if (ActivityCompat.checkSelfPermission(FreeRoomAC.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(FreeRoomAC.this, PermissionUtils.CODE_READ_PHONE_STATE, null);
        }
    }

    private void getData() {
        mJoinWay = getIntent().getStringExtra(UmengAnalyticsEventConstants.WAY_GAME_JOIN);
        roomId = getIntent().getStringExtra(Extras.EXTRA_ROOM_ID);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        gameCode = getIntent().getStringExtra(Extras.EXTRA_GAME_CODE);
        mIsMeCreator = getIntent().getBooleanExtra(Extras.EXTRA_GAME_IS_CREATOR, false);
        startByCreate = getIntent().getBooleanExtra("startByCreate", false);
        mIsMeMgr = getIntent().getIntExtra(GameConstants.KEY_GAME_IS_ADMIN, 0) == 1;
        joinCode = getIntent().getStringExtra(Extras.EXTRA_GAME_JOIN_CODE);
        channel = getIntent().getStringExtra(Extras.EXTRA_GAME_CHANNEL);
        hostIp = getIntent().getStringExtra(Extras.EXTRA_GAME_SERVERIP);
    }

    private void onLoginDone() {
        enterRequest = null;
    }

    private String gameJson = "";
    private void enterRoom() {
        LogUtil.e(TAG, "云信进入聊天室, roomId=" + roomId);
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        data.setNick(NimUserInfoCache.getInstance().getUserDisplayName(UserPreferences.getInstance(this).getUserId()));
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoom(data);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                ChatRoomInfo roomInfo = result.getRoomInfo();
                String jsonStr = new JSONObject(roomInfo.getExtension()).toString();
                gameJson = jsonStr;
                LogUtil.i(TAG, "获取云信扩展字段" + jsonStr);
                try {
                    gameInfo = GameJsonTools.parseGameData(jsonStr);
                    if (gameInfo != null) {
                        gameInfo.joinCode = StringUtil.isSpace(joinCode) ? gameInfo.code : joinCode;
                        gameInfo.channel = StringUtil.isSpace(channel) ? gameInfo.joinCode : channel;
                    }
                    if (startByCreate) {
                        sendNewGameMessage(jsonStr, NewGameReceiver.ACTION_NEWGAME_TYPE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkMyIdentity();
                mQuickGameTableView.setGameInfo(gameInfo);
                DialogMaker.dismissProgressDialog();
                onLoginDone();
                btn_free_game_start.setVisibility(View.VISIBLE);
                LogUtil.i(TAG, result.toString());
            }
            @Override
            public void onFailed(int code) {
                LogUtil.e(TAG, "云信进入聊天室失败, code=" + code);
                getGameConfig();
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
                Toast.makeText(ChessApp.sAppContext, "云信进入聊天室失败errorCode=" + errorCode, Toast.LENGTH_SHORT).show();
                onLoginDone();
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
                getGameConfig();
                Toast.makeText(ChessApp.sAppContext, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNewGameMessage(String json, int gameActionType) {//向俱乐部里面发送创建游戏或者解散游戏的消息
        if (StringUtil.isSpace(teamId) || !mIsMeCreator || !startByCreate) {
            return;
        }
        RequestTimeLimit.lastGetGameListInClub = 0;
        RequestTimeLimit.lastGetGameListInHorde = 0;
        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
        intent.putExtra(Extras.EXTRA_DATA, json);
        intent.putExtra(NewGameReceiver.ACTION_NEWGAME, gameActionType);
        sendBroadcast(intent);
    }

    private void sendCancelGameOrGameOverMessage(int gameActionType) {//向俱乐部里面发送创建游戏或者解散游戏的消息
        RequestTimeLimit.lastGetGameListInClub = 0;
        RequestTimeLimit.lastGetGameListInHorde = 0;
        Intent intent = new Intent(NewGameReceiver.ACTION_NEWGAME);
        intent.putExtra(NewGameReceiver.ACTION_NEWGAME, gameActionType);
        intent.putExtra(Extras.EXTRA_DATA, gameJson);
        sendBroadcast(intent);
    }

    private void getGameConfig() {
        if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
            Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            DialogMaker.dismissProgressDialog();
            return;
        }
        if (mGameAction == null) {
            return;//如果mGameAction为null，说明执行了ondestroy，不需要重新实例化，直接return就行
        }
        mGameAction.getGameInfo(UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_ENTER_ROOM_FAILED, gameCode, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
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
                        sendNewGameMessage(gameJson, NewGameReceiver.ACTION_NEWGAME_TYPE);
                        checkMyIdentity();
                        mQuickGameTableView.setGameInfo(gameInfo);
                        btn_free_game_start.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                DialogMaker.dismissProgressDialog();
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    private void initHead() {
        String gameName = getIntent().getStringExtra(Extras.EXTRA_GAME_NAME);
        if (gameInfo != null && !StringUtil.isSpace(gameInfo.name)) {
            gameName = gameInfo.name;
        }
        if (findViewById(R.id.tv_head_title) == null || findViewById(R.id.tv_head_right) == null) {
            return;
        }
        setHeadTitle(gameName);
        if (mIsMeCreator) {
            Drawable rightHeadButtonDrawable = getResources().getDrawable(R.mipmap.icon_manage);
            rightHeadButtonDrawable.setBounds(0, 0, rightHeadButtonDrawable.getIntrinsicWidth(), rightHeadButtonDrawable.getIntrinsicHeight());
            TextView rightHeadButton = (TextView) findViewById(R.id.tv_head_right);
            rightHeadButton.setCompoundDrawables(rightHeadButtonDrawable, null, null, null);
            rightHeadButton.setText("");
            rightHeadButton.setOnClickListener(this);
            rightHeadButton.setVisibility(View.VISIBLE);
        } else {
            setHeadRightButtonGone();
        }
    }

    private void initTabLayout() {
        mFreeAllPlayerFrg = FreeAllPlayerFrg.newInstance();
        fragmentList.add(mFreeAllPlayerFrg);
        mMttPagerAdapter = new MatchPagerAdapter(getSupportFragmentManager(), fragmentList, new ArrayList<String>(), new ArrayList<Integer>());
        free_room_viewpager.setOffscreenPageLimit(mMttPagerAdapter.getCount());
        // page swtich animation
//        viewPager.setPageTransformer(true, new FadeInOutPageTransformer());
        free_room_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtil.i("onPageScrolled " + "   position:" + position + "   positionOffset:" + positionOffset + "  positionOffsetPixels:" + positionOffsetPixels);
            }
            @Override
            public void onPageSelected(int position) {
                tv_free_all_player.setSelected(free_room_viewpager.getCurrentItem() == 0);
                tv_free_my_player.setSelected(free_room_viewpager.getCurrentItem() == 1);
                setSwipeBackEnable(position == 0);
                LogUtil.i("onPageSelected " + "   position:" + position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        free_room_viewpager.setAdapter(mMttPagerAdapter);
        free_room_viewpager.setCurrentItem(0);
        tv_free_all_player.setSelected(free_room_viewpager.getCurrentItem() == 0);
        tv_free_my_player.setSelected(free_room_viewpager.getCurrentItem() == 1);
        //初始化其它组件
        btn_free_game_start.setOnClickListener(this);
    }

    private void checkMyIdentity() {
        if (gameInfo != null) {
            if (gameInfo.creatorInfo != null) {
                mIsMeCreator = gameInfo.creatorInfo.account.equals(DemoCache.getAccount());
            }
            gameInfo.is_admin = mIsMeCreator || mIsMeMgr ? 1 : 0;///////重要，云信扩展字段里面没有传这个字段，自己判断  join接口会传这个字段
            if (!StringUtil.isSpace(gameInfo.tid)) {
                teamId = gameInfo.tid;
            }
            if (!StringUtil.isSpace(hostIp)) {//云信扩展字段的server不准
                gameInfo.serverIp = hostIp;
            }
//        if (!StringUtil.isSpace(gameInfo.serverIp)) {//云信扩展字段的server又准了，以云信扩字段为准
//            hostIp = gameInfo.serverIp;
//        }
        }//上面的代码在前面不要变
        if (tv_free_my_player_click_area != null && free_game_divider != null) {
            tv_free_my_player_click_area.setVisibility(mIsMeCreator || mIsMeMgr ? View.VISIBLE : View.GONE);
            free_game_divider.setVisibility(mIsMeCreator || mIsMeMgr ? View.VISIBLE : View.GONE);
        }
        if ((mIsMeCreator || mIsMeMgr) && !mgrFrgHasAdded) {
            mgrFrgHasAdded = true;
            mFreeMyPlayerFrg = FreeMyPlayerFrg.newInstance();
            fragmentList.add(mFreeMyPlayerFrg);
            mMttPagerAdapter.notifyDataSetChanged();
        } else if (!mIsMeCreator && !mIsMeMgr && mgrFrgHasAdded) {
            fragmentList.remove(mFreeMyPlayerFrg);
            mMttPagerAdapter.notifyDataSetChanged();
            mgrFrgHasAdded = false;
        }
        initHead();
        if (gameInfo == null) {
            return;//下面的赋值都需要gameInfo不为null
        }
        //下面判断开始按钮的状态
        if (btn_free_game_start != null) {
            if (gameInfo.status == GameStatus.GAME_STATUS_WAIT) {
                if (mIsMeCreator) {
                    btn_free_game_start.setEnabled(true);
                    btn_free_game_start.setText("开始");
                } else {
                    btn_free_game_start.setEnabled(false);
                    btn_free_game_start.setText("等待房主开始");
                }
            } else if (gameInfo.status == GameStatus.GAME_STATUS_START) {
                btn_free_game_start.setEnabled(true);
                btn_free_game_start.setText("进入牌局");
            }
        }
        updateFragmentGameInfo();
        if (hasRequestMgrList) {
            return;
        }
        mGameAction.getMgrList(gameInfo.gid, gameInfo.creatorInfo.account, gameInfo.code, new GameRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                ArrayList<UserEntity> gameManagers = ChessApp.gameManagers.get(gameInfo.gid);
                for (int i = 0; i < gameManagers.size(); i++) {
                    UserEntity userEntity = gameManagers.get(i);
                    if (userEntity.account.equals(DemoCache.getAccount())) {
                        channel = userEntity.channel;
                        gameInfo.channel = channel;
                        if (UmengAnalyticsEventConstants.WAY_GAME_JOIN_BY_CODE.equals(mJoinWay)) {//如果是通过输码进入，那么channel设置为code
                            gameInfo.channel = gameCode;
                        }
                        updateFragmentGameInfo();
                    }
                }
            }
            @Override
            public void onFailed(int code, JSONObject response) {
            }
        });
        hasRequestMgrList = true;
    }
    boolean hasRequestMgrList = false;

    private void updateFragmentGameInfo() {
        if (mFreeAllPlayerFrg != null) {
            mFreeAllPlayerFrg.updateGameInfo(gameInfo);
        }
        if (mFreeMyPlayerFrg != null) {
            mFreeMyPlayerFrg.updateGameInfo(gameInfo);
        }
    }

    ShareView mShareView;
    Bitmap shareCodeBitmap;
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
//            mShareView.setShareBitmap(shareCodeBitmap);// TODO: 16/12/27 图片功能去掉
        }
        mShareView.gameEntity = gameInfo;
        mShareView.show();
    }

    private boolean isGameCreateFromClub() {//判断是私人局还是俱乐部局
        return StringUtil.isSpace(teamId) ? true : false;
    }

    public void updatePlayerCount(int position, int count) {
        if (position == 0) {
            tv_free_all_player.setText("全部玩家(" + count + ")");
        } else if (position == 1) {
            tv_free_my_player.setText("我的管理(" + count + ")");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_free_game_start:
                if (gameInfo == null || !NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
                    Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
                    return;
                }
                if (gameInfo.status == GameStatus.GAME_STATUS_WAIT && mIsMeCreator) {
                    clickStartGame();//手动开始比赛
                } else if (gameInfo.status == GameStatus.GAME_STATUS_START) {
                    if (ActivityCompat.checkSelfPermission(FreeRoomAC.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        PermissionUtils.requestPermission(FreeRoomAC.this, PermissionUtils.CODE_READ_PHONE_STATE, mPermissionGrant);
                    } else {
                        PokerActivity.startGameByPlay(FreeRoomAC.this, gameInfo, roomId, 0);
                    }
                }
                break;
            case R.id.tv_head_right:
                if (rightTopPopupWindow != null && rightTopPopupWindow.isShowing()) {
                    dissmissClubPopWindow();
                } else {
                    showClubPopWindow();//比赛进行中点击右上角的弹窗
                }
                break;
            case R.id.ll_pop_club_create://添加管理员
                dissmissClubPopWindow();
                if (gameInfo != null) {
                    ChannelListAC.start(this, gameInfo);
                }
                break;
            case R.id.ll_pop_club_join://解散比赛
                dissmissClubPopWindow();
                showGameCancelDialog();
                break;
        }
    }

    public void dissmissClubPopWindow(){
        if(rightTopPopupWindow != null && rightTopPopupWindow.isShowing()){
            rightTopPopupWindow.dismiss();
        }
    }

    View ll_pop_club_row1;
    View ll_pop_club_row2;
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
            ll_pop_club_row1.setVisibility(View.VISIBLE);
            popView.findViewById(R.id.ll_pop_club_join).setVisibility(View.GONE);//我的管理
            ll_pop_club_row2 = popView.findViewById(R.id.ll_pop_club_join);//暂停比赛
            ll_pop_club_row2.setOnClickListener(this);
            ll_pop_club_row2.setVisibility(gameInfo == null || gameInfo.status == GameStatus.GAME_STATUS_START ? View.GONE : View.VISIBLE);
            Drawable addLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_add);
            addLeftDrawable.setBounds(0, 0, addLeftDrawable.getIntrinsicWidth(), addLeftDrawable.getIntrinsicHeight());
            TextView addText = (TextView) popView.findViewById(R.id.tv_pop_club_create);
            ((FrameLayout.LayoutParams) addText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            addText.setText("管理员");
            addText.setCompoundDrawables(addLeftDrawable, null, null, null);

            Drawable pauseLeftDrawable = getResources().getDrawable(R.mipmap.room_manager_end);
            pauseLeftDrawable.setBounds(0, 0, pauseLeftDrawable.getIntrinsicWidth(), pauseLeftDrawable.getIntrinsicHeight());
            TextView pauseText = (TextView) popView.findViewById(R.id.tv_pop_club_join);
            ((FrameLayout.LayoutParams) pauseText.getLayoutParams()).gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            pauseText.setText("解散牌局");
            pauseText.setCompoundDrawables(pauseLeftDrawable, null, null, null);

        }
        ll_pop_club_row2.setVisibility(gameInfo == null || gameInfo.status == GameStatus.GAME_STATUS_START ? View.GONE : View.VISIBLE);
        rightTopPopupWindow.showAsDropDown(findViewById(R.id.tv_head_right), -ScreenUtil.dp2px(this, 9), -ScreenUtil.dp2px(this, 16));
    }

    private void clickStartGame() {
        mGameAction.startFreeGame(gameInfo.code, new VolleyCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 0) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String serverIp = data.optString(GameConstants.KEY_GAME_SERVER);
                        if (!StringUtil.isSpace(serverIp)) {
                            gameInfo.serverIp = serverIp;
                        }
                    } else if (code == ApiCode.CODE_GAME_NONE_EXISTENT || code == ApiCode.CODE_MATCH_CHECKIN_FAILURE_CHANNEL_NOT_FOUND) {
                        Toast.makeText(ChessApp.sAppContext, R.string.game_start_notexist, Toast.LENGTH_SHORT).show();
                    } else if (code == ApiCode.CODE_GAME_IS_BEGIN) {//3011 Game has begun
                        if (btn_free_game_start != null) {
                            btn_free_game_start.setEnabled(true);
                            btn_free_game_start.setText("进入牌局");
                        }
                        if (gameInfo != null) {
                            gameInfo.status = GameStatus.GAME_STATUS_START;
                            if (ActivityCompat.checkSelfPermission(FreeRoomAC.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                PermissionUtils.requestPermission(FreeRoomAC.this, PermissionUtils.CODE_READ_PHONE_STATE, mPermissionGrant);
                            } else {
                                PokerActivity.startGameByPlay(FreeRoomAC.this, gameInfo, roomId, 0);
                            }
                        }
                    } else {
                        Toast.makeText(ChessApp.sAppContext, "开局失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    EasyAlertDialog dialog;
    public void showGameCancelDialog() {
        String message = getString(R.string.quit_game_dialog_message);
        if (mIsMeCreator) {
            message = getString(R.string.dismiss_game_dialog_message);
        }
        dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                        dialog.dismiss();
                    }

                    @Override
                    public void doOkAction() {
                        if (mIsMeCreator) {
                            if (!NetworkUtil.isNetAvailable(ChessApp.sAppContext)) {
                                Toast.makeText(ChessApp.sAppContext, R.string.network_is_not_available, Toast.LENGTH_LONG).show();
                                return;
                            }
                            //解散youxi游戏
                            isInitiativeDismiss = true;
                            mGameAction.dismissFreeGame("", gameCode, new VolleyCallback() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject json = new JSONObject(response);
                                        int code = json.getInt("code");
                                        if (code == ApiCode.CODE_SUCCESS) {
                                            isInitiativeDismiss = true;
                                            //解散牌局成功！
                                            Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_success), Toast.LENGTH_SHORT).show();
                                            RequestTimeLimit.lastGetRecentGameTime = (0);
                                            RequestTimeLimit.lastGetGameListInClub = 0;
                                            RequestTimeLimit.lastGetGameListInHorde = 0;
                                            if (gameInfo != null) {
                                                if ((gameInfo.code != null && gameInfo.code.length() > 6) ||
                                                        (gameInfo.joinCode != null && gameInfo.joinCode.length() > 6) ||
                                                        (gameInfo.channel != null && gameInfo.channel.length() > 6)) {
                                                    RequestTimeLimit.lastGetGamePlayingTime = 0;
                                                }
                                            }
                                            finish();
                                        } else if (code == ApiCode.CODE_GAME_IS_BEGIN) {
                                            Toast.makeText(ChessApp.sAppContext, getString(R.string.game_dismiss_failure_be_gameisstart), Toast.LENGTH_SHORT).show();
                                            isInitiativeDismiss = false;
                                        } else {
                                            String message = ApiResultHelper.getShowMessage(json);
                                            if (TextUtils.isEmpty(message)) {
                                                message = ChessApp.sAppContext.getString(R.string.game_dismiss_failure);
                                            }
                                            Toast.makeText(ChessApp.sAppContext, message, Toast.LENGTH_SHORT).show();
                                            isInitiativeDismiss = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        isInitiativeDismiss = false;
                                    }
                                }
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    isInitiativeDismiss = false;
                                }
                            });
                        } else {
                            //退出这个牌局
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(incomingChatRoomMsg, register);
    }

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            LogUtil.i(TAG, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason() + "   chatRoomKickOutEvent.getRoomId(): " + chatRoomKickOutEvent.getRoomId() + " roomId:" + roomId);
            sendCancelGameOrGameOverMessage(NewGameReceiver.ACTION_CANCELGAME_TYPE);
            if (chatRoomKickOutEvent.getRoomId().equals(roomId)) {
                logoutChatRoom();
                if (isInitiativeDismiss && DemoCache.getAccount().equals(gameInfo.creatorInfo.account)) {
                    LogUtil.i(TAG, "房主主动解散");
                    //房主主动解散
                } else {
                    //系统解散
                    showGameOverDialog();
                }
            }
        }
    };

    public void showGameOverDialog() {
        String message = "该牌局已经结束";
        if (gameInfo != null && gameInfo.status == 0) {
            if (gameInfo.gameMode == GameConstants.GAME_MODE_NORMAL) {
                message = "牌局已经解散";
            } else if (gameInfo.gameMode == GameConstants.GAME_MODE_SNG) {
                message = "该牌局已解散，将会返还全部报名费";
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

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
        LogUtil.i(TAG, "logoutChatRoom");
        clearChatRoom();
    }

    public void clearChatRoom() {
        ChatRoomMemberCache.getInstance().clearRoomCache(roomId);
    }

    Observer<List<ChatRoomMessage>> incomingChatRoomMsg = new Observer<List<ChatRoomMessage>>() {
        @Override
        public void onEvent(List<ChatRoomMessage> messages) {
            // 处理新收到的消息
            PrintUtil.printChatRoomMessages(TAG, TAG, messages);
            for (ChatRoomMessage message : messages) {
                if (message.getAttachment() instanceof ChatRoomNotificationAttachment) {
                    ChatRoomNotificationAttachment chatRoomNotificationAttachment = (ChatRoomNotificationAttachment) message.getAttachment();
                    if (chatRoomNotificationAttachment.getType() == NotificationType.ChatRoomInfoUpdated) {
                        LogUtil.i(TAG, "ChatRoomInfoUpdated");
                        fetchRoomInfo();
                        break;
                    } else if (chatRoomNotificationAttachment.getType() == NotificationType.ChatRoomMemberIn) {
                        LogUtil.i(TAG, "ChatRoom ChatRoomMemberIn");
                        if (mFreeAllPlayerFrg != null) {
                            mFreeAllPlayerFrg.onChatMemberIn(message);
                        }
                    }
                }
            }
        }
    };

    private void fetchRoomInfo() {
        NIMClient.getService(ChatRoomService.class).fetchRoomInfo(roomId).setCallback(new RequestCallback<ChatRoomInfo>() {
            @Override
            public void onSuccess(ChatRoomInfo param) {
                LogUtil.i(TAG, "roomId : " + roomId + ";updateRoomInfo");
                updateRoomInfo(param);
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

    public void updateRoomInfo(ChatRoomInfo roomInfo) {
        String jsonStr = new JSONObject(roomInfo.getExtension()).toString();
        LogUtil.i(GameAction.TAG, "更新云信扩展字段" + " " + jsonStr);
        try {
            gameInfo = GameJsonTools.parseGameData(jsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (gameInfo == null) {
            return;
        }
        if (!StringUtil.isSpace(gameInfo.serverIp)) {
            hostIp = gameInfo.serverIp;//如果是云信更新的消息，那么以云信的为准
        }
        gameInfo.joinCode = StringUtil.isSpace(joinCode) ? gameInfo.code : joinCode;
        gameInfo.channel = StringUtil.isSpace(channel) ? gameInfo.joinCode : channel;
        checkMyIdentity();
        if (gameInfo.status == GameStatus.GAME_STATUS_START) {//这个更新云信消息是创建者点击"开始"发送的，房主直接就进游戏了
            if (ActivityCompat.checkSelfPermission(FreeRoomAC.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(FreeRoomAC.this, PermissionUtils.CODE_READ_PHONE_STATE, mPermissionGrant);
            } else {
                PokerActivity.startGameByPlay(FreeRoomAC.this, gameInfo, roomId, 0);
                if (!mIsMeMgr && !mIsMeCreator) {
                    finish();
                }
            }
        }
    }

    private void releaseChatRoomRelated() {//释放聊天室相关的资源
        registerObservers(false);
        logoutChatRoom();
    }

    @Override
    public void onBackPressed() {
        releaseChatRoomRelated();
        super.onBackPressed();
    }

    @Override
    public void onBack(View view) {
        super.onBack(view);
        releaseChatRoomRelated();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "FreeRoomAC onPause");
        if (isFinishing()) {
            cleardata();
        }
    }

    private void cleardata() {
        if (mGameAction != null) {
            mGameAction.onDestroy();
            mGameAction = null;
        }
        if (mClubAction != null) {
            mClubAction.onDestroy();
            mClubAction = null;
        }
        if (mIsMeMgr || mIsMeCreator || (gameInfo != null && gameInfo.status != GameStatus.GAME_STATUS_START)) {//不是管理员的话不要离开聊天室，
            releaseChatRoomRelated();
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "FreeRoomAC ondestroy");
        super.onDestroy();
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            if (requestCode == PermissionUtils.CODE_READ_PHONE_STATE) {
                PokerActivity.startGameByPlay(FreeRoomAC.this, gameInfo, roomId, 0);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
}
