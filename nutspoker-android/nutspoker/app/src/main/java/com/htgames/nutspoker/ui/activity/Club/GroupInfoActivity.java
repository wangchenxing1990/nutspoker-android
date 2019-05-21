package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import com.netease.nim.uikit.common.util.log.LogUtil;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htgames.nutspoker.R;
import com.netease.nim.uikit.common.DemoCache;
import com.htgames.nutspoker.chat.contact.activity.UserProfileActivity;
import com.htgames.nutspoker.chat.contact_selector.ContactSelectHelper;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.ui.adapter.team.TeamMemberAdapter;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.netease.nim.uikit.bean.TeamMemberItem;
import com.htgames.nutspoker.view.TeamInfoGridView;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.netease.nim.uikit.common.util.NetworkUtil;
import com.htgames.nutspoker.widget.Toast;
import com.htgames.nutspoker.view.switchbutton.SwitchButton;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialog;
import com.netease.nim.uikit.common.ui.dialog.EasyAlertDialogHelper;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.chesscircle.helper.RecentContactHelp;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.contact.core.item.ContactIdFilter;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 圈子信息页面
 */
public class GroupInfoActivity extends BaseTeamActivity implements View.OnClickListener, TeamMemberAdapter.RemoveMemberCallback, TeamMemberAdapter.AddMemberCallback ,TeamMemberAdapter.TeamMemberHolderEventListener{
    private final static String TAG = "GroupInfoActivity";
    private static final int REQUEST_CODE_NAME = 101;
    public static final int RESULT_CODE_NAME_EDITED = 1;
    private static final int REQUEST_CODE_CONTACT_SELECT = 102;

    private TextView tv_group_info_name;
    private TextView tv_group_info_member;
    private Button btn_group_quit;
    private RelativeLayout rl_group_info_edit;
    //
    TeamInfoGridView gridView;
    //圈子创建者
    private String creator;
    //是否是管理员
    private boolean isSelfAdmin = false;

    private String teamId;
    ArrayList<TeamMemberItem> memberList;
    List<String> memberAccounts;
    TeamMemberAdapter mTeamMemberAdapter;

    RelativeLayout rl_clear_cache;//
    SwitchButton switch_newmessage_notify;
    SwitchButton switch_invite_by_host;
    SwitchButton switch_creat_game_by_host;
    Team team;
    /**
     * 是否不接受新消息提醒
     */
    boolean isMute = false;

    private int teamCapacity = ClubConstant.getGroupMemberLimit(); // 群人数上限，暂定

    RelativeLayout rl_newmessage_notify;

    private UserInfoObservable.UserInfoObserver userInfoObserver;

    public static void start(Context context, String id) {
        Intent intent = new Intent();
        intent.putExtra(Extras.EXTRA_TEAM_ID, id);
        intent.setClass(context, GroupInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        teamId = getIntent().getStringExtra(Extras.EXTRA_TEAM_ID);
        initView();
        initSwitchButton();
        initAdapter();
        loadTeamInfo();
        queryMemberList(teamId);
        registerObservers(true);
    }

    private void registerObservers(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataObserver);
            TeamDataCache.getInstance().registerTeamMemberDataChangedObserver(teamMemberObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataObserver);
            TeamDataCache.getInstance().unregisterTeamMemberDataChangedObserver(teamMemberObserver);
        }
        registerUserInfoChangedObserver(register);
    }

    TeamDataCache.TeamMemberDataChangedObserver teamMemberObserver = new TeamDataCache.TeamMemberDataChangedObserver() {

        @Override
        public void onUpdateTeamMember(List<TeamMember> members) {
//            for(TeamMember teamMember : members){
//                if(teamId.equals(teamMember.getTid()) && !memberAccounts.contains(teamMember.getAccount())){
//                    memberAccounts.add(teamMember.getAccount());
//                    memberList.add(teamMember);
//                }
//            }
//            mTeamMemberAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRemoveTeamMember(TeamMember member) {
//            mTeamMemberAdapter.notifyDataSetChanged();
//            removeMember(member.getAccount());
        }
    };

    TeamDataCache.TeamDataChangedObserver teamDataObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
            for (Team team : teams) {
                if (team.getId().equals(teamId)) {
                    updateTeamInfo(team);
                    break;
                }
            }
        }

        @Override
        public void onRemoveTeam(Team team) {
            if (team.getId().equals(teamId)) {
                GroupInfoActivity.this.team = team;
            }
        }
    };

    private void registerUserInfoChangedObserver(boolean register) {
        if (register) {
            if (userInfoObserver == null) {
                userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                    @Override
                    public void onUserInfoChanged(List<String> accounts) {
                        mTeamMemberAdapter.notifyDataSetChanged();
                    }
                };
            }
            UserInfoHelper.registerObserver(userInfoObserver);
        } else {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    private void loadTeamInfo() {
        creator = "";
        Team t = TeamDataCache.getInstance().getTeamById(teamId);
        if (t != null) {
            updateTeamInfo(t);
        } else {
            searchTeam(teamId, new RequestCallback<Team>() {
                @Override
                public void onSuccess(Team t) {
                    TeamDataCache.getInstance().addOrUpdateTeam(t);
                    updateTeamInfo(t);
                }

                @Override
                public void onFailed(int code) {
                    onGetTeamInfoFailed("" + code);
                }

                @Override
                public void onException(Throwable exception) {
                    onGetTeamInfoFailed(exception.getMessage().toString());
                }
            });
//        }
        }
    }

    private void updateTeamInfo(Team t) {
        if (t == null) {
            return;
        }
        team = t;
        creator = team.getCreator();
        if (creator.equals(NimUIKit.getAccount())) {
            isSelfAdmin = true;
        }
        // title
        String teamName = team.getName();
        setTitle(teamName);
        // team nameId
        boolean noName = (team == null || TextUtils.isEmpty(team.getName()) || ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName()));
        if (noName) {
            tv_group_info_name.setText(R.string.group_name_null);
        } else {
            tv_group_info_name.setText(teamName);
        }
        if (isSelfAdmin) {
            //是创建者
            btn_group_quit.setText(R.string.club_info_operate_dismiss);//解散
            switch_creat_game_by_host.setEnabled(true);
            switch_invite_by_host.setEnabled(true);
        } else {
            //是圈子成员
            btn_group_quit.setText(R.string.club_info_operate_quit);//退出
            switch_creat_game_by_host.setEnabled(false);
            switch_invite_by_host.setEnabled(false);
        }
        tv_group_info_name.setEnabled(isSelfAdmin);
        String memberCountShow = team.getMemberCount() + "/" + ClubConstant.getGroupMemberLimit();
        if(team.getMemberCount() >= ClubConstant.getGroupMemberLimit()){
            memberCountShow = memberCountShow + "  " + getString(R.string.member_limit);
        }
        tv_group_info_member.setText(memberCountShow);
        setHeadTitle(String.format(getString(R.string.group_info_head), team.getMemberCount()));
        // team notify
        rl_newmessage_notify.setVisibility(View.VISIBLE);
        setNotifyConfig();
    }

    /**
     * 设置消息通知按钮
     */
    private void setNotifyConfig() {
        isMute = team.mute();//如果返回true，不提醒，否则会提醒。
        if(switch_newmessage_notify.isChecked() != !isMute){
            LogUtil.i(TAG , "isMute :" + isMute);
            switch_newmessage_notify.setCheckedImmediately(!isMute);
        }
    }

    private void onGetTeamInfoFailed(String errorMsg) {
        Toast.makeText(this, getString(R.string.group_not_exist) + ", errorMsg=" + errorMsg, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initAdapter() {
        memberAccounts = new ArrayList<String>();
        memberList = new ArrayList<TeamMemberItem>();
        mTeamMemberAdapter = new TeamMemberAdapter(getApplicationContext(), memberList, TeamTypeEnum.Normal, this, this , this);
        gridView.setAdapter(mTeamMemberAdapter);
//        mTeamMemberAdapter.setEventListener(this);
    }

    /**
     * 群消息提醒设置
     */
    private void initSwitchButton() {
        switch_newmessage_notify = (SwitchButton) findViewById(R.id.switch_newmessage_notify);
        switch_invite_by_host = (SwitchButton) findViewById(R.id.switch_invite_by_host);
        switch_creat_game_by_host = (SwitchButton) findViewById(R.id.switch_creat_game_by_host);
        //NotifySwitchButton
        switch_newmessage_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!NetworkUtil.isNetAvailable(getApplicationContext())) {
                    //无网络情况
//                    switch_newmessage_notify.setCheckedImmediately(!isChecked);
                    return;
                }
                boolean mute = !isChecked;
                LogUtil.i(TAG , "isMute :" + isMute + ";mute : " + mute);
                //是否消息提醒，mute沉默，不开启提醒
                if(mute != isMute){
                    //未选中为静音
                    muteTeamConfig(teamId, !isChecked, new RequestCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            LogUtil.i(TAG, "muteTeam onSuccess");
                            isMute = !isMute;
                        }

                        @Override
                        public void onFailed(int code) {
                            LogUtil.i(TAG, "muteTeam failed code:" + code);
//                            switch_newmessage_notify.setCheckedImmediately(!switch_newmessage_notify.isChecked());
                        }

                        @Override
                        public void onException(Throwable throwable) {

                        }
                    });
                }
            }
        });
    }

    private void initView() {
        rl_clear_cache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        rl_newmessage_notify = (RelativeLayout) findViewById(R.id.rl_newmessage_notify);
        rl_group_info_edit = (RelativeLayout) findViewById(R.id.rl_group_info_edit);
        tv_group_info_name = (TextView) findViewById(R.id.tv_group_info_name);
        tv_group_info_member = (TextView) findViewById(R.id.tv_group_info_member);
        btn_group_quit = (Button) findViewById(R.id.btn_group_quit);
        rl_clear_cache.setOnClickListener(this);
        btn_group_quit.setOnClickListener(this);
        rl_group_info_edit.setOnClickListener(this);
        //
        gridView = (TeamInfoGridView) findViewById(R.id.team_members_grid_view);
        gridView.setSelector(R.color.transparent);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    mTeamMemberAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mTeamMemberAdapter.getMode() == TeamMemberAdapter.Mode.DELETE) {
                    mTeamMemberAdapter.setMode(TeamMemberAdapter.Mode.NORMAL);
                    mTeamMemberAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 查询群成员
     *
     * @param teamId
     */
    public void queryMemberList(String teamId) {
        // 该操作有可能只是从本地数据库读取缓存数据，也有可能会从服务器同步新的数据，
        // 因此耗时可能会比较长。
        gridView.setVisibility(View.GONE);
        memberAccounts.clear();

        TeamDataCache.getInstance().fetchTeamMemberList(teamId, new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> members) {
                if(success && members != null && !members.isEmpty()) {
                    gridView.setVisibility(View.VISIBLE);
                    List<String> accounts = new ArrayList<>();
                    for (TeamMember member : members) {
                        // 标记创建者（群主）
                        if (member.getType() == TeamMemberType.Owner) {
                            creator = member.getAccount();
                            if (creator.equals(NimUIKit.getAccount())) {
                                isSelfAdmin = true;
                            }
                        }
                        accounts.add(member.getAccount());
                    }
                    addMember(accounts, true);
                } else{
                    Toast.makeText(getApplicationContext(), R.string.get_group_member_failure, Toast.LENGTH_SHORT).show();
                }
            }
        });
//        queryMemberList(teamId, new RequestCallback<List<TeamMember>>() {
//            @Override
//            public void onSuccess(List<TeamMember> members) {
//                if (members == null || members.isEmpty()) {
//                    String errorMsg = "queryMemberList empty";
//                    Log.e(TAG, errorMsg);
//                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                gridView.setVisibility(View.VISIBLE);
//                List<String> accounts = new ArrayList<>();
//                for (TeamMember member : members) {
//                    // 标记创建者（群主）
//                    if (member.getType() == TeamMemberType.Owner) {
//                        creator = member.getAccount();
//                        if (creator.equals(NimUIKit.getAccount())) {
//                            isSelfAdmin = true;
//                        }
//                    }
//                    accounts.add(member.getAccount());
//                }
//                addMember(accounts, true);
//                setAdapter();
//            }
//
//            @Override
//            public void onFailed(int i) {
//
//            }
//
//            @Override
//            public void onException(Throwable throwable) {
//
//            }
//        });
    }

    private void addMember(List<String> accounts, boolean clear) {
        if (accounts == null || accounts.isEmpty()) {
            return;
        }
        if (clear) {
            this.memberAccounts.clear();
        }
        // add
        if (this.memberAccounts.isEmpty()) {
            this.memberAccounts.addAll(accounts);
        } else {
            for (String account : accounts) {
                if (!this.memberAccounts.contains(account)) {
                    this.memberAccounts.add(account);
                }
            }
        }
        // sort
        Collections.sort(this.memberAccounts, new Comparator<String>() {
            @Override
            public int compare(String l, String r) {
                if (creator == null) {
                    return 0;
                }
                if (creator.equals(l)) {
                    return -1;
                }
                if (creator.equals(r)) {
                    return 1;
                }
                return l.compareTo(r);
            }
        });
        updateDataSource();
    }

    private void updateDataSource() {
        memberList.clear();
        // member item
        String identity;
        for (String account : memberAccounts) {
            if (creator.equals(account)) {
                identity = TeamMemberItem.OWNER;
            } else {
                identity = null;
            }
            memberList.add(new TeamMemberItem(TeamMemberItem.TeamMemberItemTag.NORMAL, teamId, account, identity));
        }

        // add item
        memberList.add(new TeamMemberItem(TeamMemberItem.TeamMemberItemTag.ADD, null, null, null));

        // remove item
//        if (isSelfAdmin && target == TARGET_TEAM_INFO) {
        if (isSelfAdmin) {
            memberList.add(new TeamMemberItem(TeamMemberItem.TeamMemberItemTag.DELETE, null, null,
                    null));
        }

        // refresh
        if (mTeamMemberAdapter != null) {
            mTeamMemberAdapter.notifyDataSetChanged();
        }
    }

    private void removeMember(String account) {
        memberAccounts.remove(account);
        for (TeamMemberItem item : memberList) {
            if (account.equals(item.account)) {
                memberList.remove(item);
                break;
            }
        }
        // 为了解决2.3系统，移除用户后刷新界面不显示的问题
        if (Build.VERSION.SDK_INT < 11) {
            mTeamMemberAdapter.setMode(TeamMemberAdapter.Mode.NORMAL);
        }
        mTeamMemberAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NAME) {
            if (resultCode == RESULT_CODE_NAME_EDITED && data != null) {
                String teamName = data.getStringExtra(Extras.EXTRA_TEAM_NAME);
                if(!TextUtils.isEmpty(teamName)){
                    tv_group_info_name.setText(teamName);
                }else{
                    tv_group_info_name.setText(R.string.group_name_null);
                }
            }
        }
        if (requestCode == REQUEST_CODE_CONTACT_SELECT && resultCode == Activity.RESULT_OK) {
            final ArrayList<String> selected = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            if (selected != null && !selected.isEmpty()) {
                addMembersToTeam(selected);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_group_quit:
                //提示弹框
                showGroupConfirmDialog(creator.equals(DemoCache.getAccount()));
                break;
            case R.id.rl_group_info_edit:
                Intent intent = new Intent(this, GroupEditNameActivity.class);
                intent.putExtra(Extras.EXTRA_TEAM_ID, teamId);
                intent.putExtra(Extras.EXTRA_TEAM_NAME, TextUtils.isEmpty(team.getName())|| ClubConstant.GROUP_IOS_DEFAULT_NAME.equals(team.getName()) ? "" : team.getName());
                startActivityForResult(intent, REQUEST_CODE_NAME);
                break;
            case R.id.rl_clear_cache:
                doClearCache(teamId);
                break;
        }
    }

    EasyAlertDialog groupDialog;

    public void showGroupConfirmDialog(final boolean isCreator) {
//        String title = getString(R.string.quit_group_dialog_title);
        String message = getString(R.string.quit_group_dialog_message);
        if (isCreator) {
//            title = getString(R.string.dismiss_group_dialog_title);
            message = getString(R.string.dismiss_group_dialog_message);
        }
        groupDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        if (isCreator) {
                            //圈主是解散圈子
                            doDismissTeam();
                        } else {
                            //普通会员
                            doQuitTeam();
                        }
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            groupDialog.show();
        }
    }

    /**
     * 邀请群成员
     */
    @Override
    public void onAddMember() {
        if(team.getMemberCount() >= ClubConstant.getGroupMemberLimit()){
            Toast.makeText(getApplicationContext() , R.string.reach_group_member_capacity , Toast.LENGTH_SHORT).show();
            return;
        }
        ContactSelectActivity.Option option = new ContactSelectActivity.Option();
        option.title = getString(R.string.group_select_member);
        ArrayList<String> disableAccounts = new ArrayList<>();
        disableAccounts.addAll(memberAccounts);
        option.itemDisableFilter = new ContactIdFilter(disableAccounts);
        // 限制群成员数量在群容量范围内
        int capacity = teamCapacity - memberAccounts.size();
        option.maxSelectNum = capacity;
//        option.maxSelectedTip = getString(R.string.reach_team_member_capacity, teamCapacity);
        option.maxSelectedTip = getString(R.string.reach_group_member_capacity);
        ContactSelectHelper.startContactSelect(this, option, REQUEST_CODE_CONTACT_SELECT);
    }

    /**
     * 移除群成员
     */
    @Override
    public void onRemoveMember(final String account) {
        showRemoveMemberDialog(account);
    }

    public void showRemoveMemberDialog(final String account) {
        String message = getString(R.string.remove_group_member_dialog_message , NimUserInfoCache.getInstance().getUserDisplayName(account));
        EasyAlertDialog removeMemberDialog = EasyAlertDialogHelper.createOkCancelDiolag(this, null, message, true,
                new EasyAlertDialogHelper.OnDialogActionListener() {

                    @Override
                    public void doCancelAction() {
                    }

                    @Override
                    public void doOkAction() {
                        DialogMaker.showProgressDialog(GroupInfoActivity.this, getString(R.string.empty), true);
                        removeMember(teamId, account, new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                DialogMaker.dismissProgressDialog();
                                removeMember(account);
                                Toast.makeText(getApplicationContext(), R.string.remove_member_success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailed(int code) {
                                DialogMaker.dismissProgressDialog();
                                Toast.makeText(getApplicationContext(), R.string.remove_member_failed, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onException(Throwable exception) {
                                DialogMaker.dismissProgressDialog();
                            }
                        });
                    }
                });
        if (!isFinishing() && !isDestroyedCompatible()) {
            removeMemberDialog.show();
        }
    }

    @Override
    public void onHeadImageViewClick(String account) {
        UserProfileActivity.start(this , account);
    }

    /**
     * 添加群成员
     */
    private void addMembersToTeam(final ArrayList<String> selected) {
        // add members
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        addMembers(teamId, selected, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                DialogMaker.dismissProgressDialog();
                addMember(selected, false);
                Toast.makeText(getApplicationContext(), R.string.invite_member_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.invite_member_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }
//

    /**
     * 非群主退出群
     */
    private void doQuitTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        quitTeam(teamId, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.quit_group_success, Toast.LENGTH_SHORT).show();
//                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_QUIT));
//                RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
//                MainActivity.start(GroupInfoActivity.this);//打开主页
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                        MainActivity.start(GroupInfoActivity.this);//打开主页
                        finish();
                    }
                }, 200L);
            }

            @Override
            public void onFailed(int code) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.quit_group_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable exception) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 群主解散群(直接退出)
     */
    private void doDismissTeam() {
        DialogMaker.showProgressDialog(this, getString(R.string.empty), true);
        dismissTeam(teamId, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DialogMaker.dismissProgressDialog();
//                setResult(Activity.RESULT_OK, new Intent().putExtra(RESULT_EXTRA_REASON, RESULT_EXTRA_REASON_DISMISS));
                Toast.makeText(getApplicationContext(), R.string.dismiss_group_success, Toast.LENGTH_SHORT).show();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RecentContactHelp.deleteRecentContact(teamId, SessionTypeEnum.Team, true);
                        MainActivity.start(GroupInfoActivity.this);//打开主页
                        finish();
                    }
                }, 200L);
            }

            @Override
            public void onFailed(int i) {
                DialogMaker.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), R.string.dismiss_club_failed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mTeamMemberAdapter.switchMode()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mTeamMemberAdapter != null && mTeamMemberAdapter.switchMode()) {
            return;
        }
        super.onBackPressed();
    }

}
