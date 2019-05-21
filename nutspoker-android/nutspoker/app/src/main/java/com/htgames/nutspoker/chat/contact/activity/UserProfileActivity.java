package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.main.activity.AddVerifyActivity;
import com.htgames.nutspoker.chat.msg.activity.MessageVerifyActivity;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.tool.JsonResolveUtil;
import com.htgames.nutspoker.ui.action.FriendRelationshipAction;
import com.htgames.nutspoker.ui.action.UserAction;
import com.htgames.nutspoker.ui.activity.Club.ClubInfoActivity;
import com.htgames.nutspoker.ui.activity.Club.ClubMemberACNew;
import com.htgames.nutspoker.ui.activity.Club.ClubRejectActivity;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.activity.Record.StatisticsActivity;
import com.htgames.nutspoker.ui.adapter.team.ClubGridAdapter;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.UserInfoView;
import com.htgames.nutspoker.view.widget.CustomGridView;
import com.htgames.nutspoker.view.widget.ObservableScrollView;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.chesscircle.entity.TeamEntity;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.constants.GameConstants;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.friend.model.Friend;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SystemMessageType;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.netease.nimlib.sdk.msg.model.SystemMessage;

/**
 * Created by 20150726 on 2015/9/29.
 */
public class UserProfileActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "UserProfileActivity";
    public final static int FROM_FRIEND = 0;
    public final static int FROM_OTHER_LIST = 1;//来自列表（除了好友列表）
    public final static int FROM_CLUB_OWNER = 2;//来自自己拥有的俱乐部
    public final static int FROM_MESSAGE_ADDFRIEND = 3;//来自消息列表：申请好友
    public final static int FROM_MESSAGE_CLUBAPPLY = 4;//来自消息列表：俱乐部申请
    public final static int FROM_ADD_CLUB_MEMBER = 5;//俱乐部群主或者管理员搜索用户点击用户来到用户详情页

    Button btn_add_friend;
    Button btn_begin_chat;
    Button btn_message_agree;
    RelativeLayout rl_column_alias;
    String account;
    boolean mIsMgr = false;

    UserInfoView mUserInfoView;
    RelativeLayout rl_column_data;
    TextView tv_club_count;
    ArrayList<TeamEntity> teamList;
    CustomGridView grid_club_joined;
    FriendRelationshipAction mFriendRelationshipAction;
    int from = FROM_OTHER_LIST;
    String clubId = "";
    ObservableScrollView mObservableScrollView;
    RelativeLayout rl_head;
    SystemMessage mSystemMessage;
    /** 消息是否处理*/
    boolean isMessageDeal = false;
    UserAction mUserAction;

    @BindView(R.id.ll_bottom) View ll_bottom;
    @BindView(R.id.ll_club_pane) View mViewClubPane;

    @OnClick(R.id.tv_reply_reject) void clickReplyReject(){
        //前往新的Activity
        ClubRejectActivity.StartActivityFor(this,mSystemMessage);
    }
    @OnClick(R.id.tv_reply_agree) void clickReplyAgree(){
        if(from == FROM_MESSAGE_CLUBAPPLY){
            if(!ClubConstant.isClubMemberIsFull(mSystemMessage.getTargetId())){
                //通过俱乐部申请
                agreeClubApply(true);
            } else{
                Toast.makeText(getApplicationContext(), R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void start(Activity activity, String account) {
        if (DemoCache.getAccount().equals(account) || DealerConstant.isDealer(account) || TextUtils.isEmpty(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String account , int from) {
        if (DemoCache.getAccount().equals(account) || DealerConstant.isDealer(account) || TextUtils.isEmpty(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String account, int from , String teamId,boolean isMgr) {
        if (DemoCache.getAccount().equals(account) || TextUtils.isEmpty(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
        intent.putExtra(Extras.EXTRA_TYPE,isMgr);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String account, int from ,SystemMessage mSystemMessage) {
        if (DemoCache.getAccount().equals(account) || TextUtils.isEmpty(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mUnbinder = ButterKnife.bind(this);

        findViewById(R.id.rl_head).setBackgroundResource(android.R.color.transparent);
//        setHeadTitle(R.string.head_user_profile);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_OTHER_LIST);
        clubId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        mIsMgr = getIntent().getBooleanExtra(Extras.EXTRA_TYPE,false);
        if(from == FROM_MESSAGE_ADDFRIEND || from == FROM_MESSAGE_CLUBAPPLY){
            mSystemMessage = (SystemMessage)getIntent().getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);
        }
        mFriendRelationshipAction = new FriendRelationshipAction(this, null);
        mUserAction = new UserAction(this , null);
        initView();
//        tv_club_count.setText(String.format(getString(R.string.club_another_count), "0"));
        registerObserver(true);
//        getTeamListByUid(account);//用户加入的所有俱乐部   用户详情页显示这个用户加入的俱乐部，新版本不需要再调用这个接口
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
//        updateToggleView();
        updateUserOperatorView();
    }

    /**
     * 刷新用户数据
     */
    private void getUserInfo() {
        if (NimUserInfoCache.getInstance().hasUser(account)) {
            updateUserInfoView();
            return;
        }
        if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
            showGetInfoFailureDialog();
            return;
        }
        DialogMaker.showProgressDialog(this, getString(R.string.get_ing), false).setCanceledOnTouchOutside(false);
        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, new RequestCallbackWrapper<NimUserInfo>() {
            @Override
            public void onResult(int code, NimUserInfo result, Throwable exception) {
                DialogMaker.dismissProgressDialog();
                updateUserInfoView();
            }

            @Override
            public void onFailed(int code) {
                super.onFailed(code);
                DialogMaker.dismissProgressDialog();
                showGetInfoFailureDialog();
            }

            @Override
            public void onException(Throwable throwable) {
                super.onException(throwable);
                DialogMaker.dismissProgressDialog();
                showGetInfoFailureDialog();
            }
        });
    }

    EasyAlertDialog infoFailureDialog = null;

    public void showGetInfoFailureDialog(){
        if(infoFailureDialog == null) {
            infoFailureDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.prompt), getString(R.string.get_userinfo_failure),
                    getString(R.string.retry), getString(R.string.cancel), false, new EasyAlertDialogHelper.OnDialogActionListener() {
                        @Override
                        public void doCancelAction() {
                            finish();
                        }

                        @Override
                        public void doOkAction() {
                            getUserInfo();
                        }
                    });
        }
        infoFailureDialog.show();
    }

    private void updateUserInfoView() {
        LogUtil.i(TAG, "帐号：" + account);
        final NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo == null) {
            return;
        }
        mUserInfoView.updateUserInfo(userInfo);
    }

    private void initView() {
        if(from == FROM_MESSAGE_CLUBAPPLY) {//俱乐部申请
            ll_bottom.setVisibility(View.GONE);
            mViewClubPane.setVisibility(View.VISIBLE);
        }
        else//其他
        {
            ll_bottom.setVisibility(View.VISIBLE);
            mViewClubPane.setVisibility(View.GONE);
        }

        rl_head = (RelativeLayout)findViewById(R.id.rl_head);
        mObservableScrollView = (ObservableScrollView) findViewById(R.id.mObservableScrollView);
        setObservableScrollViewListener(mObservableScrollView ,rl_head);
        //
        grid_club_joined = (CustomGridView) findViewById(R.id.grid_club_joined);
        mUserInfoView = (UserInfoView) findViewById(R.id.mUserInfoView);
        rl_column_alias = (RelativeLayout) findViewById(R.id.rl_column_alias);
        btn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        btn_begin_chat = (Button) findViewById(R.id.btn_begin_chat);
        btn_message_agree = (Button) findViewById(R.id.btn_message_agree);
        rl_column_data = (RelativeLayout) findViewById(R.id.rl_column_data);
        tv_club_count = (TextView) findViewById(R.id.tv_club_count);
        btn_add_friend.setOnClickListener(this);
        btn_begin_chat.setOnClickListener(this);
        btn_message_agree.setOnClickListener(this);
        rl_column_data.setOnClickListener(this);
        rl_column_alias.setOnClickListener(this);
        grid_club_joined.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getAdapter() == null || position >= parent.getAdapter().getCount()) {
                    return;
                }
                TeamEntity teamEntity = (TeamEntity) parent.getItemAtPosition(position);
                ClubInfoActivity.start(UserProfileActivity.this, teamEntity.id, ClubInfoActivity.FROM_TYPE_LIST);
            }
        });
    }

    public void doRemoveFriend() {
        if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.network_is_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        DialogMaker.showProgressDialog(UserProfileActivity.this, getString(R.string.remove_friend_ing), false);
        mFriendRelationshipAction.deleteFriend(account, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                DialogMaker.dismissProgressDialog();
                if (code == 0) {
                    RecentContactHelp.deleteRecentContact(account, SessionTypeEnum.P2P, true); //删除最近联系人数据
                    updateUserOperatorView();
                    finish();
                }
            }
            @Override
            public void onFailed() {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 删除好友
     */
    private void onRemoveFriend() {
        LogUtil.i(TAG, "onRemoveFriend");
        String message = String.format(getString(R.string.remove_friend_tip), NimUserInfoCache.getInstance().getUserDisplayName(account));
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, getString(R.string.remove_friend),
                message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        doRemoveFriend();
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    /**
     * 移除群成员
     */
    private void onRemoveClubMember() {
        EasyAlertDialog dialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null,
                getString(mIsMgr?R.string.club_remove_membersmgr_tip:R.string.club_remove_members_tip , NimUserInfoCache.getInstance().getUserDisplayName(account)), true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {

                    }

                    @Override
                    public void doOkAction() {
                        //移出俱乐部
                        ClubMemberACNew.doRemoveMember(account,clubId,UserProfileActivity.this);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    private void onChat() {
        MainActivity.startSession(UserProfileActivity.this, account, MainActivity.TYPE_SESSION_P2P);
//        SessionHelper.startP2PSession(this, account);
    }

    /**
     * 更新是否是好友的状态UI
     */
    private void updateUserOperatorView() {
        if (from == FROM_FRIEND || from == FROM_OTHER_LIST || from == FROM_MESSAGE_ADDFRIEND || from == FROM_MESSAGE_CLUBAPPLY) {
            if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
                setHeadRightButton(R.string.remove, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRemoveFriend();
                    }
                });
            } else {
                setHeadRightButtonGone();
            }
        } else if (from == FROM_CLUB_OWNER) {
            setHeadRightButton(R.string.club_remove, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(clubId)) {
                        onRemoveClubMember();
                    }
                }
            });
        }else{
            setHeadRightButtonGone();
        }
        if(from == FROM_MESSAGE_ADDFRIEND && !isMessageDeal){
            //消息未处理
            btn_begin_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
            btn_message_agree.setVisibility(View.VISIBLE);
            btn_message_agree.setText(R.string.agree_friend);
        } else if(from == FROM_MESSAGE_CLUBAPPLY && !isMessageDeal){
            //消息未处理
            btn_begin_chat.setVisibility(View.GONE);
            btn_add_friend.setVisibility(View.GONE);
            btn_message_agree.setVisibility(View.VISIBLE);
            btn_message_agree.setText(R.string.agree_club_apply);
        } else{
            //消息已经处理
            if (NIMClient.getService(FriendService.class).isMyFriend(account)) {
                btn_begin_chat.setVisibility(View.VISIBLE);
                btn_add_friend.setVisibility(View.GONE);
                btn_message_agree.setVisibility(View.GONE);
                updateAlias(true);
            } else {
                btn_add_friend.setVisibility(View.GONE);//btn_add_friend.setVisibility(View.VISIBLE);// TODO: 16/12/15 加好友的功能去掉
                btn_message_agree.setVisibility(View.GONE);
                btn_begin_chat.setVisibility(View.GONE);
                updateAlias(false);
            }
        }
    }

    private void updateAlias(boolean isFriend) {
        if (isFriend) {
            rl_column_alias.setVisibility(View.VISIBLE);
            Friend friend = FriendDataCache.getInstance().getFriendByAccount(account);
//            if (friend != null && !TextUtils.isEmpty(friend.getAlias())) {
//                nickText.setVisibility(View.VISIBLE);
//                nameText.setText(friend.getAlias());
//                nickText.setText("昵称：" + NimUserInfoCache.getInstance().getUserName(account));
//            } else {
//                nickText.setVisibility(View.GONE);
//                nameText.setText(NimUserInfoCache.getInstance().getUserName(account));
//            }
        } else {
            rl_column_alias.setVisibility(View.GONE);
//            nickText.setVisibility(View.GONE);
//            nameText.setText(NimUserInfoCache.getInstance().getUserName(account));
        }
    }

    private void registerObserver(boolean register) {
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache.FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onDeletedFriends(List<String> account) {
            updateUserOperatorView();
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
        }
    };


    public void getTeamListByUid(String uid) {
        mUserAction.getTeamListByUid(uid, new com.htgames.nutspoker.interfaces.RequestCallback() {
            @Override
            public void onResult(int code, String result, Throwable var3) {
                if (code == 0) {
                    filterClubList(JsonResolveUtil.getTeamList(result));
                    setTeamList();
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

    /**
     * 过滤高级俱乐部和游戏组
     *
     * @param teams
     */
    public void filterClubList(List<TeamEntity> teams) {
        teamList = new ArrayList<TeamEntity>();
        for (TeamEntity team : teams) {
            if(!GameConstants.isGmaeClub(team)){
                teamList.add(team);
            }
        }
    }

    ClubGridAdapter mClubGridAdapter;

    public void setTeamList() {
        if (teamList != null && teamList.size() != 0) {
//            grid_club_joined.setVisibility(View.VISIBLE);// TODO: 16/12/23 功能去掉
            mClubGridAdapter = new ClubGridAdapter(this, teamList);
            grid_club_joined.setAdapter(mClubGridAdapter);
//            tv_club_count.setText(String.format(getString(R.string.club_another_count), String.valueOf(teamList.size())));
        } else {
            grid_club_joined.setVisibility(View.GONE);
//            tv_club_count.setText(String.format(getString(R.string.club_another_count), "0"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObserver(false);
        if (mFriendRelationshipAction != null) {
            mFriendRelationshipAction.onDestroy();
            mFriendRelationshipAction = null;
        }
        if (mUserAction != null) {
            mUserAction.onDestroy();
            mUserAction = null;
        }
        infoFailureDialog = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_friend:
//                String nickname = NimUserInfoCache.getInstance().getUserDisplayName(account);
                AddVerifyActivity.start(UserProfileActivity.this, account, AddVerifyActivity.TYPE_VERIFY_FRIEND);
                break;
            case R.id.btn_begin_chat:
                onChat();
                break;
            case R.id.rl_column_data:
                startActivity(new Intent(this, StatisticsActivity.class));
                break;
            case R.id.rl_column_alias:
                EditUserItemActivity.start(this, account, UserConstant.KEY_ALIAS, "");
                break;
            case R.id.btn_message_agree:
                if(from == FROM_MESSAGE_ADDFRIEND){
                    //通过好友验证
                    agereAddFriend(true);
                } else if(from == FROM_MESSAGE_CLUBAPPLY){
                    if(!ClubConstant.isClubMemberIsFull(mSystemMessage.getTargetId())){
                        //通过俱乐部申请
                        agreeClubApply(true);
                    } else{
                        Toast.makeText(getApplicationContext(), R.string.club_invite_member_count_limit, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * 同意好友邀请
     * @param pass
     */
    public void agereAddFriend(final boolean pass){
        if(mSystemMessage != null) {
            mFriendRelationshipAction.addFriend(mSystemMessage.getFromAccount(), new com.htgames.nutspoker.interfaces.RequestCallback() {
                @Override
                public void onResult(int code, String result, Throwable var3) {
                    if (code == 0) {
                        isMessageDeal = true;
                        Intent intentSuccess = new Intent(MessageVerifyActivity.ACTION_MESSAGE_DEAL);
                        intentSuccess.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
                        sendBroadcast(intentSuccess);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.addfriend_agree_failure, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed() {

                }
            });
        }
    }

    /**
     * 同意俱乐部申请
     * @param pass
     */
    public void agreeClubApply(final boolean pass) {
        if (mSystemMessage != null && mSystemMessage.getType() == SystemMessageType.ApplyJoinTeam) {
            //验证入群申请,用户发出申请后，所有管理员都会收到一条系统消息，类型为 SystemMessageType#TeamApply。管理员可选择同意或拒绝：
            if (pass) {
                DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), false);
                // 同意申请
                NIMClient.getService(TeamService.class).passApply(mSystemMessage.getTargetId(), mSystemMessage.getFromAccount()).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DialogMaker.dismissProgressDialog();

                        mViewClubPane.setVisibility(View.GONE);

                        isMessageDeal = true;
                        updateUserOperatorView();
                        Intent intentSuccess = new Intent(MessageVerifyActivity.ACTION_MESSAGE_DEAL);
                        intentSuccess.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
                        sendBroadcast(intentSuccess);
                        finish();
                    }

                    @Override
                    public void onFailed(int i) {
                        DialogMaker.dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), R.string.taem_apply_agree_failure, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        DialogMaker.dismissProgressDialog();
                    }
                });
            }

        }
    }


}
