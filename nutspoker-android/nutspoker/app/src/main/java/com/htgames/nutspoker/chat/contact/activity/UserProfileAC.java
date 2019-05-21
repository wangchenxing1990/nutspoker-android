package com.htgames.nutspoker.chat.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.msg.model.SystemMessage;
import com.htgames.nutspoker.data.common.UserConstant;
import com.htgames.nutspoker.ui.action.FriendRelationshipAction;
import com.htgames.nutspoker.ui.action.UserAction;
import com.htgames.nutspoker.ui.activity.Club.ClubMemberACNew;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.ui.base.BaseActivity;
import com.htgames.nutspoker.view.UserInfoView;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.DealerConstant;
import com.netease.nim.uikit.common.DemoCache;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 周智慧 on 16/12/4.
 * 代码重构---->来自UserProfileActivity.java
 */

public class UserProfileAC extends BaseActivity implements View.OnClickListener {
    private final static String TAG = UserProfileAC.class.getSimpleName();
    public final static int FROM_FRIEND = 0;
    public final static int FROM_OTHER_LIST = 1;//来自列表（除了好友列表）
    public final static int FROM_CLUB_OWNER = 2;//来自自己拥有的俱乐部, 俱乐部会员管理界面
    public final static int FROM_MESSAGE_ADDFRIEND = 3;//来自消息列表：申请好友
    public final static int FROM_MESSAGE_CLUBAPPLY = 4;//来自消息列表：俱乐部申请

    public final static int FROM_CLUB_ADD_MEMBER = 5;//俱乐部群主或者管理员搜索用户点击用户来到用户详情页
    public final static int FROM_CLUB_CHAT = 6;//俱乐部群聊界面点击头像
    public String account;
    int from = FROM_OTHER_LIST;
    String clubId = "";
    private List<TeamMember> memberList  = new ArrayList<>();//如果clubId不為空，则请求这个俱乐部的所有成员（要判断是否是管理员）
    boolean mIsMgr = false;
    SystemMessage mSystemMessage;
    FriendRelationshipAction mFriendRelationshipAction;
    UserAction mUserAction;
    private Team team;
    @BindView(R.id.mUserInfoView) UserInfoView mUserInfoView;
    @BindView(R.id.btn_begin_chat) Button btn_begin_chat;
    @BindView(R.id.user_profile_invite_club_tv) Button user_profile_invite_club_tv;
    @BindView(R.id.user_profile_remove_club_tv) Button user_profile_remove_club_tv;
    @BindView(R.id.rl_column_alias) View rl_column_alias;

    public static void start(Activity activity, String account, int from , String teamId,boolean isMgr) {
        if (DemoCache.getAccount().equals(account) || TextUtils.isEmpty(account) || DealerConstant.isDealer(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileAC.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
        intent.putExtra(Extras.EXTRA_TYPE, isMgr);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public static void start(Activity activity, String account, int from ,SystemMessage mSystemMessage) {
        if (DemoCache.getAccount().equals(account) || TextUtils.isEmpty(account)) {
            return;
        }
        Intent intent = new Intent(activity, UserProfileAC.class);
        intent.putExtra(Extras.EXTRA_ACCOUNT, account);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_MESSAGE_DATA, mSystemMessage);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_new);
        mUnbinder = ButterKnife.bind(this);
        initHead();
        setHeadTitle(R.string.head_user_profile);
        account = getIntent().getStringExtra(Extras.EXTRA_ACCOUNT);
        from = getIntent().getIntExtra(Extras.EXTRA_FROM, FROM_OTHER_LIST);
        clubId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        mIsMgr = getIntent().getBooleanExtra(Extras.EXTRA_TYPE, false);
        if(from == FROM_MESSAGE_ADDFRIEND || from == FROM_MESSAGE_CLUBAPPLY){
            mSystemMessage = (SystemMessage)getIntent().getSerializableExtra(Extras.EXTRA_MESSAGE_DATA);
        }
        mFriendRelationshipAction = new FriendRelationshipAction(this, null);
        mUserAction = new UserAction(this , null);
        initView();
        registerObserver(true);
        requestTeamMemberData();
        //下面是设置备注需要的观察者
        FriendDataCache.getInstance().buildCache();
        FriendDataCache.getInstance().registerObservers(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void initHead() {
        findViewById(R.id.rl_head).setBackgroundColor(Color.TRANSPARENT);
        setHeadTitle(R.string.head_user_profile);
        ((RelativeLayout.LayoutParams) findViewById(R.id.ll_userinfo).getLayoutParams()).setMargins(0, getResources().getDimensionPixelOffset(R.dimen.action_bar_height), 0 ,0);
    }

    private void initView() {
        findViewById(R.id.me_wealth_container).setVisibility(View.GONE);
        btn_begin_chat.setOnClickListener(this);
        user_profile_invite_club_tv.setOnClickListener(this);
        user_profile_remove_club_tv.setOnClickListener(this);
        final NimUserInfo userInfo = NimUserInfoCache.getInstance().getUserInfo(account);
        if (userInfo != null) {
            mUserInfoView.updateUserInfo(userInfo);
        }
    }

    private void registerObserver(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver);
        }
    }

    TeamDataCache.TeamMemberDataChangedObserver teamMemberObserver = new TeamDataCache.TeamMemberDataChangedObserver() {
        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
            memberList.clear();
            memberList.addAll(members);
            updateUI();//onUpdateTeamMember
        }
        @Override
        public void onRemoveTeamMember(TeamMember member) {
            Iterator<TeamMember> iterator = memberList.iterator();
            while (iterator.hasNext()) {
                TeamMember oldMember = iterator.next();
                if (oldMember.getAccount().equals(member.getAccount())) {
                    iterator.remove();
                }
            }
            updateUI();//onRemoveTeamMember
        }
    };

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

    private void requestTeamMemberData() {
        if (TextUtils.isEmpty(clubId)) {
            return;
        }
        team = TeamDataCache.getInstance().getTeamById(clubId);
        TeamDataCache.getInstance().fetchTeamMemberList(clubId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> members) {
                if(success) {
                    if (!members.isEmpty()) {
                        memberList.clear();
                        for(TeamMember member : members){
                            memberList.add(member);
                        }
                    }
                    updateUI();//requestTeamMemberData
                }
            }
        });
    }

    private void updateUI() {
        if (TextUtils.isEmpty(clubId)) {
            if (btn_begin_chat != null && user_profile_invite_club_tv != null && user_profile_remove_club_tv != null) {
                btn_begin_chat.setVisibility(View.GONE);
                user_profile_invite_club_tv.setVisibility(View.GONE);
                user_profile_remove_club_tv.setVisibility(View.GONE);
            }
        } else {
            boolean isUserInTeam = false;//对方是否在俱乐部
            boolean isUserMgr = false;//对方是否是管理员
            boolean isMeMgr = false;//"我"是否是管理员
            boolean isMeCreator = false;//"我"是否是创建者
            for (TeamMember member : memberList) {
                if (account.equals(member.getAccount())) {
                    isUserInTeam = true;
                    isUserMgr = (member.getType() == TeamMemberType.Manager || member.getType() == TeamMemberType.Owner);//如果在俱乐部，判断是不是管理员
                }
                if (DemoCache.getAccount().equals(member.getAccount())) {
                    isMeMgr = (member.getType() == TeamMemberType.Manager);//我，判断是不是管理员
                    isMeCreator = team != null && DemoCache.getAccount().equals(team.getCreator());
                }
            }
            //1对方不在俱乐部
            if (!isUserInTeam && (isMeMgr || isMeCreator)) {
                if (btn_begin_chat != null && user_profile_invite_club_tv != null && user_profile_remove_club_tv != null) {
                    btn_begin_chat.setVisibility(View.GONE);
                    user_profile_invite_club_tv.setVisibility(View.VISIBLE);
                    user_profile_remove_club_tv.setVisibility(View.GONE);
                }
            }
            //2对方在俱乐部
            if (isUserInTeam) {
                if (btn_begin_chat != null && user_profile_invite_club_tv != null && user_profile_remove_club_tv != null) {
                    user_profile_invite_club_tv.setVisibility(View.GONE);
                    btn_begin_chat.setVisibility(View.GONE);
                    if (isUserMgr || isMeMgr || isMeCreator) {//只要有一个人是管理员就能私聊
                        btn_begin_chat.setVisibility(View.VISIBLE);
                    }
                    user_profile_remove_club_tv.setVisibility(isMeCreator ? View.VISIBLE : View.GONE);//如果我是创建者，则能移除这个用户
                }
            }
            //设置备注名  产品需求是这样的：只有管理员才能给别人设置备注名
            if (rl_column_alias != null) {
                rl_column_alias.setVisibility(!StringUtil.isSpace(clubId) && (isMeCreator || isMeMgr) ? View.VISIBLE : View.GONE);
                rl_column_alias.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        registerObserver(false);
        super.onDestroy();
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
                        ClubMemberACNew.doRemoveMember(account,clubId, UserProfileAC.this);
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            dialog.show();
        }
    }

    public void inviteMembers(final ArrayList<String> accounts) {
        NIMClient.getService(TeamService.class).addMembers(clubId, accounts).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                // 返回onSuccess，表示拉人不需要对方同意，且对方已经入群成功了
                com.htgames.nutspoker.widget.Toast.makeText(getApplicationContext(), R.string.club_invite_members_success, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
                updateUI();//inviteMembers  onSuccess
            }
            @Override
            public void onFailed(int code) {
                if (code == 801) {
                    com.htgames.nutspoker.widget.Toast.makeText(getApplicationContext(), R.string.club_invite_member_count_limit, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
                } else if (code == 810) {
                    //// 返回onFailed，并且返回码为810，表示发出邀请成功了，但是还需要对方同意
                    com.htgames.nutspoker.widget.Toast.makeText(getApplicationContext(), R.string.club_invite_members_success, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
                } else {
                    com.htgames.nutspoker.widget.Toast.makeText(getApplicationContext(), R.string.club_invite_members_failed, com.htgames.nutspoker.widget.Toast.LENGTH_SHORT).show();
                }
                LogUtil.e(TAG, "invite teamembers failed, code=" + code);
                updateUI();//inviteMembers  onFailed
            }
            @Override
            public void onException(Throwable exception) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.user_profile_invite_club_tv:
                ArrayList<String> accountList = new ArrayList<String>();
                accountList.add(account);
                inviteMembers(accountList);
                break;
            case R.id.btn_begin_chat:
                MainActivity.startSession(UserProfileAC.this, account, MainActivity.TYPE_SESSION_P2P);
                break;
            case R.id.user_profile_remove_club_tv:
                onRemoveClubMember();
                break;
            case R.id.rl_column_alias:
                EditUserItemActivity.start(this, account, UserConstant.KEY_ALIAS, NimUserInfoCache.getInstance().getUserDisplayName(account));
                break;
        }
    }
}
