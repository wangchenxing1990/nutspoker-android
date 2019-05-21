package com.htgames.nutspoker.ui.activity.Club;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.netease.nim.uikit.common.util.log.LogUtil;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.htgames.nutspoker.R;
import com.htgames.nutspoker.chat.contact_selector.ContactSelectHelper;
import com.htgames.nutspoker.chat.contact_selector.activity.ContactSelectActivity;
import com.htgames.nutspoker.ui.adapter.team.GroupAdapter;
import com.htgames.nutspoker.ui.base.BaseTeamActivity;
import com.htgames.nutspoker.ui.helper.team.TeamCreateHelper;
import com.htgames.nutspoker.ui.activity.MainActivity;
import com.htgames.nutspoker.thirdPart.umeng.UmengAnalyticsEvent;
import com.htgames.nutspoker.view.ContactCountView;
import com.htgames.nutspoker.view.ResultDataView;
import com.htgames.nutspoker.view.ShareToSessionDialog;
import com.htgames.nutspoker.widget.Toast;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.chesscircle.ClubConstant;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.session.constant.Extras;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 群列表(通讯录)
 */
public class GroupListActivity extends BaseTeamActivity {
    private final static String TAG = "GroupListActivity";
    public static final int REQUEST_CODE_CREATE = 1;
    private ListView lv_mygroup;
    ContactCountView mContactCountView;
    GroupAdapter mGroupAdapter;
    ArrayList<Team> teamList;
    ResultDataView mResultDataView;

    public static void start(Activity activity , int from ,int shareType , Object shareContent){
        Intent intent = new Intent(activity , GroupListActivity.class);
        intent.putExtra(Extras.EXTRA_FROM, from);
        intent.putExtra(Extras.EXTRA_SHARE_TYPE, shareType);
        intent.putExtra(Extras.EXTRA_SHARE_DATA, (Serializable)shareContent);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);
        teamList = new ArrayList<Team>();
        initHead();
        initView();
        mResultDataView.showLoading();
        getTeamList(TeamTypeEnum.Normal);
//        getTeamList(TeamTypeEnum.Advanced);
        // load data
//        List<Team> teams = TeamDataCache.getInstance().queryTeamList();
//        if (teams.isEmpty()) {
//            Toast.makeText(TeamListActivity.this, R.string.no_team, Toast.LENGTH_SHORT).show();
//        }
        registerTeamUpdateObserver(true);
    }

    private void initHead() {
        if(from == FROM_LIST){
            setHeadTitle(R.string.me_column_group);
            setHeadRightButton(R.string.chat_new_circle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> memberAccounts = new ArrayList<>();
                    ContactSelectActivity.Option option = ContactSelectHelper.getCreateContactSelectOption(memberAccounts,
                            ClubConstant.getGroupMemberLimit());
//                    NimUIKit.startContactSelect(GroupListActivity.this, option, REQUEST_CODE_CREATE);// 创建群
                    option.maxSelectedTip = getString(R.string.reach_group_member_capacity);
                    ContactSelectHelper.startContactSelect(GroupListActivity.this, option, REQUEST_CODE_CREATE);// 创建群
                }
            });
        } else{
            setHeadTitle(R.string.select_recent_contact_group_head);
        }
    }

    private void registerTeamUpdateObserver(boolean register) {
        if (register) {
            TeamDataCache.getInstance().registerTeamDataChangedObserver(teamDataChangedObserver);
        } else {
            TeamDataCache.getInstance().unregisterTeamDataChangedObserver(teamDataChangedObserver);
        }
    }

    TeamDataCache.TeamDataChangedObserver teamDataChangedObserver = new TeamDataCache.TeamDataChangedObserver() {
        @Override
        public void onUpdateTeams(List<Team> teams) {
//            adapter.load(true);

        }

        @Override
        public void onRemoveTeam(Team team) {
//            adapter.load(true);
            if(teamList != null){
                for (Team currentTeam : teamList) {
                    if (team.getId().equals(currentTeam.getId())) {
                        teamList.remove(currentTeam);
                        mGroupAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }
    };

    private void initView() {
        mResultDataView = (ResultDataView) findViewById(R.id.mResultDataView);
        lv_mygroup = (ListView) findViewById(R.id.lv_mygroup);
        mContactCountView = new ContactCountView(getApplicationContext());
        mContactCountView.setCount(String.format(getString(R.string.group_num), 0));
        lv_mygroup.addFooterView(mContactCountView);
        lv_mygroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team team = (Team) mGroupAdapter.getItem(position);
                if(from == FROM_LIST){
                    MainActivity.startSession(GroupListActivity.this, team.getId(), MainActivity.TYPE_SESSION_TEAM);
                }else{
                    //选择发送
                    if (mShareToSessionDialog == null) {
                        mShareToSessionDialog = new ShareToSessionDialog(GroupListActivity.this, shareType , shareObject , from);
                    }
                    mShareToSessionDialog.show(team.getId() , SessionTypeEnum.Team);
                }
            }
        });
        mGroupAdapter = new GroupAdapter(getApplicationContext(), teamList);
        lv_mygroup.setAdapter(mGroupAdapter);
    }

    /**
     * 获取群列表
     *
     * @param type
     */
    public void getTeamList(TeamTypeEnum type) {
        queryTeamListByType(type, new RequestCallback<List<Team>>() {
            @Override
            public void onSuccess(List<Team> teams) {
                TeamDataCache.getInstance().addOrUpdateTeam(teams);
                teamList.clear();
                teamList.addAll((ArrayList) teams);
                updateTeamListView();
            }

            @Override
            public void onFailed(int i) {

            }

            @Override
            public void onException(Throwable throwable) {

            }
        });
    }

    public void updateTeamListView() {
        if(teamList != null && teamList.size() != 0) {
            lv_mygroup.setVisibility(View.VISIBLE);
            //
            mResultDataView.successShow();
            //
            mContactCountView.setCount(String.format(getString(R.string.group_num), teamList.size()));
            mGroupAdapter.notifyDataSetChanged();
        }else{
            mResultDataView.nullDataShow(R.string.group_null , R.mipmap.img_group_null);
            lv_mygroup.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> selects = data.getStringArrayListExtra(ContactSelectActivity.RESULT_DATA);
            createGroup("", selects);
        }
    }

    public void createGroup(String teamName, List<String> accounts) {
        DialogMaker.showProgressDialog(this, getString(com.netease.nim.uikit.R.string.empty), true);
        TeamCreateHelper.creteNormalTeam(teamName, accounts, new RequestCallback<Team>() {
            @Override
            public void onSuccess(Team team) {
                LogUtil.i(TAG, "onSuccess");
                DialogMaker.dismissProgressDialog();
                onCreateSuccess(team);
                UmengAnalyticsEvent.onEventGroupCount(getApplicationContext());
//                Toast.makeText(getApplicationContext(), R.string.create_team_success, Toast.LENGTH_SHORT).show();
//                CustomNotificationHelper.sendGroupCreateNotification(team.getId());
                //如果不是我的俱乐部（刚创建，没有发信息，都认定不是我的俱乐部）
//                MessageTipHelper.sendGameTipMessage(team, "加入", true);
                MainActivity.startSession(GroupListActivity.this, team.getId(), MainActivity.TYPE_SESSION_TEAM);
                finish();
            }

            @Override
            public void onFailed(int code) {
                LogUtil.i(TAG, "onFailed");
                DialogMaker.dismissProgressDialog();
                if (code == 801) {
                    int teamCapacity = ClubConstant.getGroupMemberLimit();
                    String tip = getString(com.netease.nim.uikit.R.string.over_team_member_capacity, teamCapacity);
                    Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.create_group_failed, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onException(Throwable throwable) {
                LogUtil.i(TAG, "onException");
                DialogMaker.dismissProgressDialog();
            }
        });
    }

    /**
     * 创建成功回调
     */
    private void onCreateSuccess(Team team) {
        if (team == null) {
            return;
        }
        LogUtil.i(TAG, "create and update team success");
        TeamDataCache.getInstance().addOrUpdateTeam(team);
        teamList.add(team);
        updateTeamListView();
        TeamDataCache.getInstance().fetchTeamMemberList(team.getId(), new SimpleCallback<List<TeamMember>>() {
            @Override
            public void onResult(boolean success, List<TeamMember> result) {
                if(success && mGroupAdapter!= null){
                    mGroupAdapter.notifyDataSetChanged();
                }
            }
        });
//        SessionHelper.startTeamSession(GroupListActivity.this, team.getId()); // 进入创建的群
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerTeamUpdateObserver(false);
    }
}
